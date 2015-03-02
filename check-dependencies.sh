#!/bin/bash

###
# #%L
# ************************************************************************
# ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
# PROJECT       :  UEPS - Uebungs-Programm fuer SQL
# FILENAME      :  check-dependencies.sh
# ************************************************************************
# %%
# Copyright (C) 2014 - 2015 Institute of Computer Science, University of Wuerzburg
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#      http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# #L%
###

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

if [ -f "$PRIMEFACES_BINARY" ]; then
  if $INSTALL_DEPENDENCIES ; then
    mvn install:install-file -Dfile="$PRIMEFACES_BINARY" \
           -DgroupId=org.primefaces \
           -DartifactId=primefaces \
           -Dversion=4.0 \
           -Dpackaging=jar | egrep -v "(^\[WARNING\])"

    # check if errors occured
    if [ ${PIPESTATUS[0]} -ne "0" ]; then exit 1; fi
  fi
fi
