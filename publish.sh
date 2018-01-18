#!/usr/bin/env bash
set -e
cd $(dirname $0)
source ./defaults.sh

function publish() {
    docker push ${DOCKER_IMAGE}
}

publish
