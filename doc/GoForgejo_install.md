# Go / forgejo Installation and Testing 

## go install/update
#### remove old version
sudo rm -rf ~/go
### download latest version and configure  
curl -OL https://go.dev/dl/$(curl 'https://go.dev/VERSION?m=text').linux-amd64.tar.gz

extract latest version to ~/go
tar -C ~ -xzf go*.linux-amd64.tar.gz

APPEND='export PATH=$PATH:$HOME/go/bin'

echo $APPEND >> $HOME/.profile

## VScode optional - TODO!?!
Go extension autoinstall  
install gpls, div, etc.

## Testing forgejo
full:
make test

require node:
make test-frontend

require go:
make test-backend

#nvm - required to build forgejo frontend
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
