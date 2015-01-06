REM -- '@ECHO OFF' disables outputing comments
@ECHO OFF && CLS

CALL cmd /k "mvn license:update-file-header && CLS && ECHO License successfully added! && ECHO."
