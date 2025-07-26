@echo off
setlocal

:: Get current date and time components using WMIC LocalDateTime
:: This provides a clean YYYYMMDDHHMMSS string, which is easier to parse.
for /f "tokens=2 delims==" %%a in ('wmic os get LocalDateTime /value') do set "dt=%%a"

:: Extract components from the LocalDateTime string
set "currentYear=%dt:~0,4%"
set "currentMonth=%dt:~4,2%"
set "currentDay=%dt:~6,2%"
set "currentHour=%dt:~8,2%"
set "currentMinute=%dt:~10,2%"

:: Construct the releaseDate in YYYYMMDD-HH:mm format
set "releaseDate=%currentYear%%currentMonth%%currentDay%-%currentHour%%currentMinute%"

echo Generating GitHub release with tag: alpha-%releaseDate%

:: Execute the gh release create command
move app\build\outputs\apk\debug\app-debug.apk app\build\outputs\apk\debug\app-debug-alpha-%releaseDate%.apk
gh release create "alpha-%releaseDate%" --generate-notes --prerelease --title "alpha-%releaseDate%" app\build\outputs\apk\debug\app-debug-alpha-%releaseDate%.apk
move  app\build\outputs\apk\debug\app-debug-alpha-%releaseDate%.apk app\build\outputs\apk\debug\app-debug.apk

echo.
echo Command execution complete.
