#!/bin/sh

if [ $# -lt 1 ]; then
    echo "usage: runworker.sh [codebaseurl]"
    exit
fi

rmiregistry -J-Djava.rmi.server.codebase=${1}
