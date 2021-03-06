#!/bin/sh

if [ $# -lt 1 ]; then
    echo "usage: runworker.sh [queueHostname]"
    exit
fi

BINDED_NAME="QueuedServer"

java -cp group4.jar \
     -Djava.rmi.server.codebase="`cat rmicodebases`" \
     -Djava.rmi.server.hostname=`cat myhostname` \
     -Djava.security.policy=security.policy \
     -Djava.rmi.server.useCodebaseOnly=false \
        edu.harvard.cs262.ComputeServer.Group4.WorkerServer ${1} ${BINDED_NAME}

