#!/bin/bash

###
# #%L
# ************************************************************************
# ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
# PROJECT       :  UEPS - Uebungs-Programm fuer SQL
# FILENAME      :  build-deploy.sh
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
./check-dependencies.sh

# ---------------------------------------------------- #
# -- Build from source
# ---------------------------------------------------- #
# https://tomcat.apache.org/maven-plugin-2.2/tomcat7-maven-plugin/plugin-info.html
mvn clean tomcat7:redeploy
