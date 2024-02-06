@echo off
echo Turning off nginx...
taskkill /f /im nginx.exe

timeout /t 5 /nobreak >nul

echo Starting nginx...
cd "C:\nginx-1.25.3"
start nginx.exe