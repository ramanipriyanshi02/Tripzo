@echo off
echo ========================================================
echo Running Automated Application and Festival Offers Tests...
echo ========================================================
java --enable-native-access=ALL-UNNAMED -cp "bin;lib/mysql-connector-j-8.3.0.jar;lib/flatlaf-3.5.2.jar;lib/jfreechart-1.5.4.jar;lib/openpdf-1.3.30.jar" com.holiday.test.TestApp
if %ERRORLEVEL% EQU 0 (
    java --enable-native-access=ALL-UNNAMED -cp "bin;lib/mysql-connector-j-8.3.0.jar;lib/flatlaf-3.5.2.jar;lib/jfreechart-1.5.4.jar;lib/openpdf-1.3.30.jar" com.holiday.test.TestFestivalOffers
)
if "%1" neq "--no-pause" pause

