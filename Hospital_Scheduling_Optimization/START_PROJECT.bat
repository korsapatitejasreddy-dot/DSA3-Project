@echo off
echo Starting Hospital Scheduling Backend...
start "Hospital Backend" cmd /k "cd /d %~dp0backend && run.bat"
timeout /t 2 >nul
start "" "%~dp0frontend\index.html"
echo.
echo Backend terminal opened separately.
echo Frontend opened in your default browser.
pause
