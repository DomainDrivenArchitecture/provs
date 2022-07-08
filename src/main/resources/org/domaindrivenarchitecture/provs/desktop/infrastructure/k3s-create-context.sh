#!/bin/bash
set -e
set -o noglob

function usage() {
  echo "Requires argument cluster_name and domain_name in server fqdn {cluster_name}.{domain_name}"
}


function main() {
    local cluster_name="${1}"; shift
    local domain_name="${1:-meissa-gmbh.de}"; shift

    ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@${cluster_name}.${domain_name} \
    "cat /etc/rancher/k3s/k3s.yaml" | \
    yq e ".clusters[0].name=\"${cluster_name}\" \
        | .clusters[0].cluster.server=\"https://kubernetes:6443\" \
        | .contexts[0].context.cluster=\"${cluster_name}\" \
        | .contexts[0].context.user=\"${cluster_name}\" \
        | .contexts[0].name=\"${cluster_name}\" \
        | del(.current-context) \
        | del(.preferences) \
        | .users[0].name=\"${cluster_name}\"" - \
    > ~/.kube/custom-contexts/${cluster_name}.yml
}

if [ $# -gt 0 ]
then
  main $1 $2
else
  usage
  exit -1
fi
