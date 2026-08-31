@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script for Windows
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set "PROJECT_DIR=%~dp0"
if "%PROJECT_DIR:~-1%"=="\" set "PROJECT_DIR=%PROJECT_DIR:~0,-1%"

@REM 1. If global mvn is available, use it
where mvn >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    mvn %*
    exit /B %ERRORLEVEL%
)

@REM 2. Prepare wrapper jar
set "WRAPPER_JAR=%PROJECT_DIR%\.mvn\wrapper\maven-wrapper.jar"

if not exist "%PROJECT_DIR%\.mvn\wrapper" mkdir "%PROJECT_DIR%\.mvn\wrapper"

if not exist "%WRAPPER_JAR%" (
    powershell -NoProfile -ExecutionPolicy Bypass -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar', '%WRAPPER_JAR%')"
)

@REM 3. Execute wrapper
java -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%PROJECT_DIR%" org.apache.maven.wrapper.MavenWrapperMain %*
exit /B %ERRORLEVEL%
