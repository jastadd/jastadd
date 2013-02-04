#!/bin/bash

interrupted()
{
    exit $?
}

trap interrupted SIGINT

mkdir test/ast
echo "==================="
echo Running unit tests!
javac -cp jastadd2.jar RunTests.java
java RunTests &> results
grep "test/Test[0-9]*\.java passed" results > passed
echo "======================"
echo Comparing test results
diff -s test/shouldpass passed
