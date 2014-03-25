#!/bin/sh

java -Djava.rmi.server.codebase="`cat rmicodebases`" \
     -Djava.rmi.server.hostname=`cat myhostname` \
     -Djava.security.policy=security.policy \
     -Djava.rmi.server.useCodebaseOnly=false \
     -cp group4.jar \
        edu.harvard.cs262.ComputeServer.Group4.WorkQueueServer

