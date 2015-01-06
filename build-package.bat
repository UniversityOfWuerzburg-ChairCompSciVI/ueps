REM -- '@ECHO OFF' disables outputing comments
@ECHO OFF && CLS

check-dependencies.bat && CLS && ^
CALL cmd /k "mvn clean package && CLS && ECHO File 'ueps.war' created! && ECHO."
