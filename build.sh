#!/usr/bin/env bash
set -e
cd $(dirname $0)
source ./defaults.sh

prepare () {
    (./mvnw clean install)
}

build () {
    local image_dir=$(readlink -f "${DIST_DIR}")
    echo "Building image from '${image_dir}'..."
    docker build --pull -t "${DOCKER_IMAGE}" --build-arg VERSION=${TAG} "${image_dir}"
}

prepare
build