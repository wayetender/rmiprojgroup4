#!/bin/sh

rmiregistry -J-Djava.rmi.server.codebase="`cat rmicodebases`"
