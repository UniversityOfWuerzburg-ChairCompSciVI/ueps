#!/bin/bash

# -------------------------------------------------------------------- #
if ! type "mvn" &> /dev/null; then
  echo "[ERROR] 'mvn' WAS NOT FOUND ON YOUR SYSTEM"
  echo "Instructions can be found at:"
  echo "http://maven.apache.org/download.cgi"
  exit 1
fi
# -------------------------------------------------------------------- #

# -------------------------------------------------------------------- #
if ! type "doxygen" &> /dev/null; then
  echo "[ERROR] 'doxygen' WAS NOT FOUND ON YOUR SYSTEM"
  echo "Instructions can be found at:"
  echo "http://www.stack.nl/~dimitri/doxygen/download.html"
  exit 1
fi
# -------------------------------------------------------------------- #

# generate doxygen documentation
# -------------------------------------------------------------------- #
mvn com.soebes.maven.plugins.dmg:doxygen-maven-plugin:report
# -------------------------------------------------------------------- #
