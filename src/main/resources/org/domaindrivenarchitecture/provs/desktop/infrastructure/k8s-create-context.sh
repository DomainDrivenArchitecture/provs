function main() {
    local cluster_name="${1}"; shift

    ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@${cluster_name}.meissa-gmbh.de \
    "cat /etc/kubernetes/admin.conf" | \
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

main $1
