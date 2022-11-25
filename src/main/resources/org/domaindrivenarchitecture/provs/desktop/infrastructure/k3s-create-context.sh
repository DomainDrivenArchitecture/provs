#!/bin/bash
set -e
set -o noglob

function usage() {
  echo "Requires argument cluster_name and domain_name in server fqdn {cluster_name}.{domain_name}"
}

function sourceNewContext() {
  DEFAULT_KUBE_CONTEXTS="$HOME/.kube/config"
	if test -f "${DEFAULT_KUBE_CONTEXTS}"
	then
	  export KUBECONFIG="$DEFAULT_KUBE_CONTEXTS"
	fi

	# Additional contexts should be in ~/.kube/custom-contexts/
	CUSTOM_KUBE_CONTEXTS="$HOME/.kube/custom-contexts"
	mkdir -p "${CUSTOM_KUBE_CONTEXTS}"

	OIFS="$IFS"
	IFS=$'\n'
	for contextFile in `find "${CUSTOM_KUBE_CONTEXTS}" -type f -name "*.yml"`
	do
	    export KUBECONFIG="$contextFile:$KUBECONFIG"
	done
	IFS="$OIFS"
}

function main() {
    local cluster_name="${1}";
    local domain_name="${2:-meissa-gmbh.de}";

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
    sourceNewContext
}

if [ $# -gt 0 ]
then
  main $1 $2
else
  usage
  exit -1
fi
