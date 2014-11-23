#!/bin/bash

# ---------------------------------------------------- #
# --
# ---------------------------------------------------- #

INSTALL_DEPENDENCIES=true
BASEDIR=$(pwd)
RESOURCES_FOLDER="res"
PRIMEFACES_BINARY="$RESOURCES_FOLDER""/primefaces/\
primefaces-4.0-with-tablefix.jar"

# ---------------------------------------------------- #
# -- Check dependencies
# ---------------------------------------------------- #

# Check if MAVEN is installed
if ! type "mvn" &> /dev/null; then
  echo "[ERROR] 'mvn' WAS NOT FOUND ON YOUR SYSTEM"
  echo "Instructions can be found at:"
  echo "http://maven.apache.org/download.cgi"
  exit 1
fi

# Check if JAVA is installed
if ! type "java" &> /dev/null; then
  echo "[ERROR] 'java' WAS NOT FOUND ON YOUR SYSTEM"
  echo "Instructions can be found at:"
  echo "http://www.oracle.com/technetwork/java/"\
"javase/downloads/jdk8-downloads-2133151.html"
  exit 1
fi

# ---------------------------------------------------- #
# -- Install primefaces with table fix
# ---------------------------------------------------- #

if $INSTALL_DEPENDENCIES ; then
  mvn install:install-file -Dfile="$PRIMEFACES_BINARY" \
         -DgroupId=org.primefaces \
         -DartifactId=primefaces \
         -Dversion=4.0-with-tablefix \
         -Dpackaging=jar | egrep -v "(^\[WARNING\])"

  # check if errors occured
  if [ ${PIPESTATUS[0]} -ne "0" ]; then exit 1; fi
fi

# ---------------------------------------------------- #
# -- Build from source
# ---------------------------------------------------- #

mvn tomcat:deploy
