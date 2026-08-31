@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script for Windows
@REM ----------------------------------------------------------------------------
@echo off
set ERROR_CODE=0

@REM Check for mvn in PATH
where mvn >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    mvn %*
    goto end
)

@REM Fallback using wrapper jar
set "WRAPPER_JAR=%~dp0.mvn\wrapper\maven-wrapper.jar"
if not exist "%WRAPPER_JAR%" (
    if not exist "%~dp0.mvn\wrapper" mkdir "%~dp0.mvn\wrapper"
    powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar', '%WRAPPER_JAR%')"
)

java -jar "%WRAPPER_JAR%" %*
if %ERRORLEVEL% NEQ 0 goto error
goto end

:error
set ERROR_CODE=1

:end
exit /B %ERROR_CODE%
