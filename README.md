# Provs framework
Provs is a framework for automating tasks for provisioning reasons and other purposes.

It combines
* being able to use the power of shell commands
* a clear and detailed result summary of the built-in execution handling (incl. failure handling and reporting)
* the convenience and robustness of a modern programming language


### Write once, run everywhere

Tasks can be run

* locally
* remotely
* in a local docker container
* in a remote container

Additionally, it is possible to define a custom processor if needed.


## Usage

### Prerequisites

* A **Java Virtual machine** (JVM) is required.
* Install `jarwrapper` (e.g. `sudo apt install jarwrapper`)
* Download the latest `provs-desktop.jar` from: https://gitlab.com/domaindrivenarchitecture/provs/-/releases
* Make the jar-file executable by `chmod +x provs-desktop.jar`
* For server functionality (e.g. k3s) download the latest `provs-server.jar` from: https://gitlab.com/domaindrivenarchitecture/provs/-/releases

### Usage format

`provs-desktop.jar <type> <target> [<options>]`

**type** can be: 
* basic - install some basic packages)
* office - install office software (LibreOffice), E-Mail (Thunderbird), etc 
* ide - same as office with additionally ide-software (VSCode, IntelliJ, etc) 

**target** can be: 
* `local`
* `user123:mypassword@myhost.com` - general format is: <user[:password]@host> - 
  * if password is omitted, then ssh-keys will be used for authentication
  * if password is omitted but option `-p` is provided the password will be prompted interactively 

**options** 
* `-p` for interactive password question


#### Show usage options

`provs-desktop.jar -h`

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
