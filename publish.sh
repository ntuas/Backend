#!/usr/bin/env bash
set -e
pushd . > /dev/null
cd $(dirname ${BASH_SOURCE[0]})
PROJECT_DIR=$(pwd)
source ./defaults.sh
popd > /dev/null

function publish() {
    docker push ${DOCKER_IMAGE}
    (cd ${PROJECT_DIR} && ./mvnw deploy -DskipTests -Dnexus.host=${NEXUS_HOST} -Dapp.version=${TAG})
}

publish
