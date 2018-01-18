#!/usr/bin/env bash
set -e

export DIST_DIR="target/dist/"

export DOCKER_REGISTRY="dockerregistry.uas.nt.public"
export GROUP="nt.uas.backend"
export MODULE="backend"
export TAG=$(git describe --tags --dirty --abbrev=8 )
export DOCKER_IMAGE="${DOCKER_REGISTRY}/${GROUP}/${MODULE}:${TAG}"
