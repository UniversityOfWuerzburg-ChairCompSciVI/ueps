#!/bin/bash
./check-dependencies.sh

# ---------------------------------------------------- #
# -- Build from source
# ---------------------------------------------------- #
# https://tomcat.apache.org/maven-plugin-2.2/tomcat7-maven-plugin/plugin-info.html
mvn clean tomcat7:redeploy
