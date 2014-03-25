#!/bin/sh

java -cp group4.jar \
     -Djava.rmi.server.codebase="`cat rmicodebases`" \
     -Djava.rmi.server.hostname=`cat myhostname` \
     -Djava.security.policy=security.policy \
     -Djava.rmi.server.useCodebaseOnly=false \
        edu.harvard.cs262.ComputeServer.Group4.WorkerServer `cat myhostname`

