#!/bin/sh

javac -cp .:../dist/record.jar *.java
java -cp .:../dist/record.jar CStruct


