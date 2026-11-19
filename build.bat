@echo off
echo ========================================================
echo Building Tripzo Desktop Application (Java Swing)
echo ========================================================

if not exist bin mkdir bin

echo Compiling Java source files...
javac -cp "lib/mysql-connector-j-8.3.0.jar;lib/flatlaf-3.5.2.jar;lib/jfreechart-1.5.4.jar;lib/openpdf-1.3.30.jar" -d bin src/com/holiday/db/*.java src/com/holiday/model/*.java src/com/holiday/dao/*.java src/com/holiday/util/*.java src/com/holiday/gui/ui/*.java src/com/holiday/gui/*.java src/com/holiday/test/*.java src/com/holiday/*.java

if %ERRORLEVEL% EQU 0 (
    if exist src\com\holiday\assets (
        if not exist bin\com\holiday\assets mkdir bin\com\holiday\assets
        xcopy /S /Y /I src\com\holiday\assets bin\com\holiday\assets >nul 2>&1
    )
    echo.
    echo BUILD SUCCESSFUL! All classes compiled cleanly into /bin directory.
) else (
    echo.
    echo BUILD FAILED! Check compiler errors above.
)
if "%1" neq "--no-pause" pause

