#!/bin/bash
set -e
set -o noglob

function main() {
    local cluster_name="${1}"; shift

    ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@${cluster_name}.meissa-gmbh.de \
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

if [ $# -eq 1 ]
then
  main $1
else
  echo "Requires argument cluster_name in server fqdn {cluster_name}.meissa-gmbh.de"
  exit -1
fi
