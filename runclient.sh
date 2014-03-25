#!/bin/sh

if [ $# -lt 1 ]; then
    echo "usage: runclient.sh [computeHostname]"
    exit
fi

java -Djava.security.policy=security.policy \
     -Djava.rmi.server.useCodebaseOnly=false \
     -cp group4.jar \
        edu.harvard.cs262.ComputeServer.Group4.WorkClient ${1}

