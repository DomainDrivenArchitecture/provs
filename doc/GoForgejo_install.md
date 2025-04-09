# Go / forgejo Installation and Testing 

## go install/update
#### remove old version
sudo rm -rf ~/go
### download latest version and configure  
curl -OL https://go.dev/dl/go1.21.3.linux-amd64.tar.gz

# extract latest version to ~/go
tar -C ~ -xzf go*.linux-amd64.tar.gz

# append path
```
(meissa) jem@meissa-ide-2023:~$ cat .bashrc.d/go.sh
PATH=$PATH:$HOME/go/bin
export PATH
```

## VScode optional - TODO!?!
"Go for VS Code v0.39.1"

## Testing forgejo
full:
make test

require node:
make test-frontend

require go:  
make lint-backend  
make test-backend

### gofumpt:  
Installation:  
go install mvdan.cc/gofumpt@latest  

Usage:  
gofumpt --help  

eg:  
gofumpt -l -d .  

further autolinting:  
make lint-go-fix


## nvm - required to build forgejo frontend
sudo apt remove nodejs
sudo apt autoremove

adapt version to latest:
curl o https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.3/install.sh | bash
nvm install

optional:
nvm alias default "latest"

## forgejo build
TAGS="bindata" make build
-> include make frontend & make backend //see details Makefile
