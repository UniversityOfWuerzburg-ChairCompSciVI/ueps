REM  #
REM  #%L
REM  ************************************************************************
REM  ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
REM  PROJECT       :  UEPS - Uebungs-Programm fuer SQL
REM  FILENAME      :  add-licence-header.sh
REM  ************************************************************************
REM  %%
REM  Copyright (C) 2014 - 2015 Institute of Computer Science, University of Wuerzburg
REM  %%
REM  Licensed under the Apache License, Version 2.0 (the "License");
REM  you may not use this file except in compliance with the License.
REM  You may obtain a copy of the License at

REM       http://www.apache.org/licenses/LICENSE-2.0

REM  Unless required by applicable law or agreed to in writing, software
REM  distributed under the License is distributed on an "AS IS" BASIS,
REM  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM  See the License for the specific language governing permissions and
REM  limitations under the License.
REM  #L%
REM  #

REM -- '@ECHO OFF' disables outputing comments
@ECHO OFF && CLS

REM -- check if doxygen is installed
WHERE doxygen >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    ECHO. && ECHO [ERROR] COMMAND 'doxygen' WASN'T FOUND. EXITING...
    ECHO. && PAUSE && EXIT /b %ERRORLEVEL%
) else (
    ECHO [INFO] Doxygen installation found
    ECHO.
)

CALL cmd /k "mvn com.soebes.maven.plugins.dmg:doxygen-maven-plugin:report && CLS && ECHO Documentation successfully generated! && ECHO."
