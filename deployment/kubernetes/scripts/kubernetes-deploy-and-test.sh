#!/usr/bin/env bash


set -e

./kubernetes-deploy-all.sh

./kubernetes-run-end-to-end-tests.sh

