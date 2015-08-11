#!/bin/sh

PWD=`pwd`

javac facet/*.java
java -cp $PWD facet/Facet -i $1 -s $2  -p $3
