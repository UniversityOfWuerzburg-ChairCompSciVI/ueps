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
