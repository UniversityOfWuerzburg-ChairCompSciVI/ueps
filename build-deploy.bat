REM -- '@ECHO OFF' disables outputing comments
@ECHO OFF && CLS

check-dependencies.bat && CLS && ^
CALL cmd /k "mvn clean tomcat7:redeploy && CLS && ECHO Ueps successfully deployed! && ECHO."
