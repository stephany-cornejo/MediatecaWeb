@echo off
setlocal
set MAVEN_PROJECTBASEDIR=%~dp0
rem Remove the trailing backslash, otherwise quoted java property values break on Windows
if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%
set MAVEN_WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar
if not exist "%MAVEN_WRAPPER_JAR%" (
    echo Error: no se encontro %MAVEN_WRAPPER_JAR%
    exit /b 1
)
java -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" -cp "%MAVEN_WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
if errorlevel 1 (
    echo Error: Fallo al ejecutar el wrapper de Maven.
    exit /b 1
)
