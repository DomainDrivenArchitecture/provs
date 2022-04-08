#!/bin/bash
set -e
set -o noglob

function main() {
  local cluster_name="${1}"; shift

  /usr/local/bin/k3s-create-context.sh ${cluster_name}
  kubectl config use-context ${cluster_name}
  ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@${cluster_name}.meissa-gmbh.de -L 8002:localhost:8002 -L 6443:192.168.5.1:6443
}

if [ $# -eq 1 ]
then
  main $1
else
  echo "Requires argument cluster_name in server fqdn {cluster_name}.meissa-gmbh.de"
  exit -1
fi
