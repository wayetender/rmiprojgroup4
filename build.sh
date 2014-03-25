#!/bin/sh
set -e

cd src
javac edu/harvard/cs262/ComputeServer/Group4/*.java
javac edu/harvard/cs262/ComputeServer/*.java
jar cvf ../group4.jar edu/*
