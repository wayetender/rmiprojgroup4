#!/bin/sh

if [ $# -lt 2 ]; then
    echo "usage: runworker.sh [hostname] [codebaseurl]"
    exit
fi

java -Djava.rmi.server.codebase=${2} \
     -Djava.rmi.server.hostname=${1} \
     -Djava.security.policy=security.policy \
     -Djava.rmi.server.useCodebaseOnly=false \
     -cp group4.jar \
        edu.harvard.cs262.ComputeServer.Group4.WorkQueueServer

