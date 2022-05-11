# provs
[![pipeline status](https://gitlab.com/domaindrivenarchitecture/provs/badges/master/pipeline.svg)](https://gitlab.com/domaindrivenarchitecture/provs/-/commits/master)

[<img src="https://domaindrivenarchitecture.org/img/delta-chat.svg" width=20 alt="DeltaChat"> chat over e-mail](mailto:buero@meissa-gmbh.de?subject=community-chat) | [<img src="https://meissa-gmbh.de/img/community/Mastodon_Logotype.svg" width=20 alt="team@social.meissa-gmbh.de"> team@social.meissa-gmbh.de](https://social.meissa-gmbh.de/@team) | [Website & Blog](https://domaindrivenarchitecture.org)

## Purpose

provs provides cli-based tooling for provisioning desktop or server resp. perform system checks.
* provs-desktop minimal - provides a minimal setup (e.g. swappiness / randomutils) e.g. for setup on a VirtualBox  
* provs-desktop office - provides enhancements like zim / gopass / fakturama
* provs-desktop ide - provides development environments for java / kotlin / python / clojure / terraform
* provs-server k3s - provides a production ready & k3s setup with dualstack option
* provs-syspec - verifies a system according to the provided system spec config file

In general provs combines
* being able to use the power of shell commands
* a clear and detailed result summary of the built-in execution handling (incl. failure handling and reporting)
* the convenience and robustness of a modern programming language

## Status

under development - we are working hard on setting up our environments using provs.

## Try out
### Prerequisites

* A **Java Virtual machine** (JVM) is required.
* Install `jarwrapper` (e.g. `sudo apt install jarwrapper`)
* Download the latest `provs-desktop.jar` from: https://gitlab.com/domaindrivenarchitecture/provs/-/releases
* Make the jar-file executable by `chmod +x provs-desktop.jar`
* For server functionality (e.g. k3s) download the latest `provs-server.jar` from: https://gitlab.com/domaindrivenarchitecture/provs/-/releases

### provs-desktop

`provs-desktop.jar <type> <target> [<options>]`

**type** can be: 
* basic - install some basic packages)
* office - install office software (LibreOffice), E-Mail (Thunderbird), etc 
* ide - same as office with additionally ide-software (VSCode, IntelliJ, etc) 

**target** can be: 
* `local`
* `user123:mypassword@myhost.com` - general format is: <user[:password]@host> - 
  * if password is omitted, then ssh-keys will be used for authentication
  * if password is omitted but option `-p` is provided, then the password will be prompted interactively 

**options** 
* `-p` for interactive password question


#### Provision a desktop

```bash
provs-desktop.jar basic local
# or an office desktop remote:
provs-desktop.jar office myuser@myhost.com -p
```

In the last case you'll be prompted for the password of the remote user due to option `-p`.

### Provision k3s

```bash
provs-server.jar k3s local
# or remote: 
provs-server.jar k3s myuser@myhost.com    # using ssh-authentication - alternatively use option -p for password authentication
```

For the remote server please configure a config file (default file name: server-config.yaml)
```yaml
fqdn: "myhostname.com"
node:
  ipv4: "192.168.56.123"   # ip address
echo: true                 # for demo reasons only - deploy an echo app 
```

To add a grafana agent to your k3s installation add the following to the config:
  
```yaml
grafana:
  user: "myusername"   # username for the grafana data source 
  password:
    source: "PLAIN"           # PLAIN, GOPASS or PROMPT
    parameter: "mypassword"   # the password or api key for the grafana data source user 
  cluster: "mycluster"        # a cluster name of your choice  
```

To provision the grafana agent only to an existing k8s system, ensure that the config (as above) is available and execute:

```bash
provs-server.jar k3s myuser@myhost.com -o grafana
```



### Performing a system check

The default config-file for the system check is `syspec-config.yaml`, you can specify a different file with option `-c <config-file>`.

```bash
provs-syspec.jar local 
# or remote:
provs-syspec.jar myuser@myhost.com
```

## Get help

To get help you can make use of the `-h` option:

```bash
provs-desktop.jar -h
provs-server.jar -h
provs-syspec.jar -h
```

Or to get help for subcommands e.g.

```bash
provs-desktop.jar ide -h
provs-server.jar k3s -h
```
