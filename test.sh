#!/bin/sh

mkdir test/ast
echo "==================="
echo Running unit tests!
java RunTests | grep "test/Test[0-9]*\.java passed" > passed
echo "======================"
echo Comparing test results
diff -s test/shouldpass passed
