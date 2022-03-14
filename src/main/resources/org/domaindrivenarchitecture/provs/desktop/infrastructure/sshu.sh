#!/bin/bash
set -e
set -o noglob

function main() {
    local cluster_name="${1}"; shift

    ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@${cluster_name}.meissa-gmbh.de
}

main $1
