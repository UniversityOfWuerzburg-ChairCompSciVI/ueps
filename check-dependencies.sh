#!/bin/bash

INSTALL_DEPENDENCIES=true
BASEDIR=$(pwd)
RESOURCES_FOLDER="res"

# modified primefaces version
PRIMEFACES_BINARY="$RESOURCES_FOLDER""/primefaces/\
primefaces-4.0.jar"

# ---------------------------------------------------- #
# -- Check dependencies
# ---------------------------------------------------- #

# Check if MAVEN is installed
HELP_PAGES="http://maven.apache.org/download.cgi"
COMMAND="mvn"

if ! type "$COMMAND" &> /dev/null; then
  echo "[ERROR] '$COMMAND' WAS NOT FOUND ON YOUR SYSTEM"
  echo "Instructions can be found at:"
  echo "$HELP_PAGES"
  exit 1
fi

# Check if JAVA is installed
HELP_PAGES="http://www.oracle.com/technetwork/java/\
javase/downloads/jdk8-downloads-2133151.html"
COMMAND="java"

if ! type "$COMMAND" &> /dev/null; then
  echo "[ERROR] '$COMMAND' WAS NOT FOUND ON YOUR SYSTEM"
  echo "Instructions can be found at:"
  echo "$HELP_PAGES"
  exit 1
fi

# ---------------------------------------------------- #
# -- Install primefaces with table fix
# ---------------------------------------------------- #

if $INSTALL_DEPENDENCIES ; then
  mvn install:install-file -Dfile="$PRIMEFACES_BINARY" \
         -DgroupId=org.primefaces \
         -DartifactId=primefaces \
         -Dversion=4.0 \
         -Dpackaging=jar | egrep -v "(^\[WARNING\])"

  # check if errors occured
  if [ ${PIPESTATUS[0]} -ne "0" ]; then exit 1; fi
fi
