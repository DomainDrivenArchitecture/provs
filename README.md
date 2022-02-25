# provs
[![pipeline status](https://gitlab.com/domaindrivenarchitecture/provs/badges/master/pipeline.svg)](https://gitlab.com/domaindrivenarchitecture/provs/-/commits/master)

[<img src="https://domaindrivenarchitecture.org/img/delta-chat.svg" width=20 alt="DeltaChat"> chat over e-mail](mailto:buero@meissa-gmbh.de?subject=community-chat) | [<img src="https://meissa-gmbh.de/img/community/Mastodon_Logotype.svg" width=20 alt="team@social.meissa-gmbh.de"> team@social.meissa-gmbh.de](https://social.meissa-gmbh.de/@team) | [Website & Blog](https://domaindrivenarchitecture.org)

## Purpose

provs provide cli-tools for provisioning desktop // server
* provs-desktop minimal - provides as minimal virtual-box able setup (e.g. swappiness / randomutils)  
* provs-desktop offic - provides enhancements like zim / gopass / fakturama
* provs-desktop ide - provides development environments for java / kotlin / python / clojure / terraform
* provs-server k3s - provides a production ready & dualstack able k3s setup

In general provs combines
* being able to use the power of shell commands
* a clear and detailed result summary of the built-in execution handling (incl. failure handling and reporting)
* the convenience and robustness of a modern programming language

## Status

under development - we are working hard on seting up our environments using provs.

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

### Examples
#### Provision a basic desktop workplace locally

`provs-desktop.jar basic local`

#### Provision an office desktop workplace remotely

`provs-desktop.jar office myuser@myhost.com -p`

You'll be prompted for the password of the remote user due to option `-p`.

### Provision k3s

```bash
provs-server.jar k3s myuser@myhost.com 
```
