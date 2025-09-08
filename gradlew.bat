@echo off
REM Gradle wrapper script for Windows to download and run Gradle 8.9.

set "GRADLE_VERSION=8.9"
set "DIST_URL=https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip"

set "TMP_DIR=%TEMP%\gradle-wrapper-%GRADLE_VERSION%"
mkdir "%TMP_DIR%" >nul 2>&1

powershell -Command "& { Invoke-WebRequest -Uri '%DIST_URL%' -OutFile '%TMP_DIR%\gradle.zip' }"
powershell -Command "& { Add-Type -AssemblyName 'System.IO.Compression.FileSystem'; [System.IO.Compression.ZipFile]::ExtractToDirectory('%TMP_DIR%\gradle.zip','%TMP_DIR%') }"

set "GRADLE_HOME=%TMP_DIR%\gradle-%GRADLE_VERSION%"
set "PATH=%GRADLE_HOME%\bin;%PATH%"

gradle %*
