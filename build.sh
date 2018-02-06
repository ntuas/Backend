#!/usr/bin/env bash
set -e
pushd . > /dev/null
cd $(dirname ${BASH_SOURCE[0]})
PROJECT_DIR=$(pwd)
source ./defaults.sh
popd > /dev/null

prepare () {
    (cd ${PROJECT_DIR} && ./mvnw clean install -Dapp.version=${TAG})
}

build () {
    local image_dir=$(cd ${PROJECT_DIR} && readlink -f "${DIST_DIR}")
    echo "Building image from '${image_dir}'..."
    docker build -t "${DOCKER_IMAGE}" --build-arg VERSION=${TAG} "${image_dir}"
}

prepare
build