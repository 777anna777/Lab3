@echo off
setlocal
set VERSION=9.8.1
set BASE=%USERPROFILE%\.gradle\bootstrap-distributions
set DIST=%BASE%\gradle-%VERSION%
if not exist "%DIST%\bin\gradle.bat" (
  if not exist "%BASE%" mkdir "%BASE%"
  powershell -NoProfile -Command "$ErrorActionPreference='Stop'; $p='%BASE%\gradle-%VERSION%-bin.zip'; Invoke-WebRequest 'https://services.gradle.org/distributions/gradle-%VERSION%-bin.zip' -OutFile $p; Expand-Archive $p -DestinationPath '%BASE%' -Force; Remove-Item $p"
  if errorlevel 1 exit /b 1
)
call "%DIST%\bin\gradle.bat" %*
