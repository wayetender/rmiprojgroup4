#!/bin/sh

if [ $# -lt 3 ]; then
    echo "usage: runworker.sh [hostname] [codebaseurl] [queuehost]"
    exit
fi

java -cp group4.jar \
     -Djava.rmi.server.codebase=${2} \
     -Djava.rmi.server.hostname=${1} \
     -Djava.security.policy=security.policy \
     -Djava.rmi.server.useCodebaseOnly=false \
        edu.harvard.cs262.ComputeServer.Group4.WorkerServer ${3}

