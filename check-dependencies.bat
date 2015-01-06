REM -- '@ECHO OFF' disables outputing comments
@ECHO OFF && CLS

REM -- check if java is installed
WHERE java >nul 2>&1 
if %ERRORLEVEL% NEQ 0 (
    ECHO. && ECHO [ERROR] COMMAND 'java' WASN'T FOUND. EXITING...
    ECHO. && PAUSE && EXIT /b %ERRORLEVEL% 
) else (
    ECHO [INFO] Java installation found
)

REM -- check if maven is installed
WHERE mvn >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    ECHO. && ECHO [ERROR] COMMAND 'mvn' WASN'T FOUND. EXITING...
	ECHO. && PAUSE && EXIT /b %ERRORLEVEL% 
) else (
	ECHO [INFO] Maven installation found
)

ECHO.

REM -- install custom primefaces binary
SET PRIMEFACES_BINARY="res/primefaces/primefaces-4.0.jar"
mvn install:install-file -Dfile="%PRIMEFACES_BINARY%" ^
-DgroupId=org.primefaces -DartifactId=primefaces ^
-Dversion=4.0 -Dpackaging=jar"
