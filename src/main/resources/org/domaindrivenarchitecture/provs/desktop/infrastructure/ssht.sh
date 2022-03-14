#!/bin/bash
set -e
set -o noglob

function main() {
    local cluster_name="${1}"; shift

    ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@${cluster_name}.meissa-gmbh.de -L 8002:localhost:8002 -L 6443:192.168.5.1:6443
}

main $1
