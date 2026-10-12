@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac -d out src\*.java
if errorlevel 1 (
  echo.
  echo Compilation failed.
  pause
  exit /b 1
)
java -cp out HospitalServer
pause
