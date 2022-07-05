#!/bin/bash
set -e
set -o noglob

function usage() {
  echo "Requires argument cluster_name and domain_name in server fqdn {cluster_name}.{domain_name}"
}

function main() {
  local cluster_name="${1}"; shift
  local domain_name="${1:-meissa-gmbh.de}"; shift

  /usr/local/bin/k8s-create-context.sh ${cluster_name}
  kubectl config use-context ${cluster_name}
  ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@${cluster_name}.${domain_name} -L 8002:localhost:8002 -L 6443:192.168.5.1:6443
}

if [ $# -gt 0 ]
then
  main $1
else
  usage
  exit -1
fi
