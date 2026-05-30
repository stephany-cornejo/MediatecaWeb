@echo off
setlocal enabledelayedexpansion
set SCRIPT_DIR=%~dp0
set JAR=target\biblioteca-1.0-SNAPSHOT.jar
set TARGET_DIR=%USERPROFILE%\biblioteca
set TARGET_DB=%TARGET_DIR%\biblioteca.db
set TEMPLATE_DB=%SCRIPT_DIR%biblioteca.db

if not exist "%TARGET_DIR%" (
    echo Creando carpeta de base de datos: %TARGET_DIR%
    mkdir "%TARGET_DIR%"
)

if not exist "%TARGET_DB%" (
    if exist "%TEMPLATE_DB%" (
        echo Copiando base de datos de plantilla desde el repositorio...
        copy /Y "%TEMPLATE_DB%" "%TARGET_DB%" >nul
    ) else (
        echo No existe plantilla de base de datos en %SCRIPT_DIR%. Se creara una nueva al iniciar la app.
    )
) else (
    echo Base de datos ya existe en %TARGET_DB%. No se copiará.
)

set MVNW=%SCRIPT_DIR%mvnw.cmd
set WRAPPER_JAR=%SCRIPT_DIR%.mvn\wrapper\maven-wrapper.jar

if not exist "%JAR%" (
    echo Artefacto no encontrado. Compilando...
    if exist "%MVNW%" (
        if not exist "%WRAPPER_JAR%" (
            echo Descargando Maven Wrapper...
            if not exist "%SCRIPT_DIR%.mvn\wrapper" mkdir "%SCRIPT_DIR%.mvn\wrapper"
            powershell -NoProfile -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object System.Net.WebClient).DownloadFile('https://repo1.maven.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar', '%WRAPPER_JAR%')"
            if errorlevel 1 (
                echo Error: No se pudo descargar el Maven Wrapper.
                pause
                exit /b 1
            )
        )
        call "%MVNW%" clean package
        if errorlevel 1 (
            echo Maven Wrapper fallo. Intentando redescargar el jar...
            del /F /Q "%WRAPPER_JAR%" >nul 2>&1
            powershell -NoProfile -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object System.Net.WebClient).DownloadFile('https://repo1.maven.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar', '%WRAPPER_JAR%')"
            if errorlevel 1 (
                echo Error: No se pudo descargar el Maven Wrapper.
                pause
                exit /b 1
            )
            call "%MVNW%" clean package
        )
    ) else (
        if defined MAVEN_HOME (
            mvn clean package
        ) else (
            echo Error: Maven no esta instalado y no existe mvnw.
            echo Instala Maven o ejecuta este proyecto desde IntelliJ para generar el Jar.
            pause
            exit /b 1
        )
    )
    if errorlevel 1 (
        echo Error: Fallo la compilacion.
        pause
        exit /b 1
    )
)
if not exist "%JAR%" (
    echo Error: El jar ejecutable sigue sin existir.
    pause
    exit /b 1
)
echo Ejecutando biblioteca...
java -jar "%JAR%"
pause
