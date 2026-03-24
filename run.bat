@echo off
:: ================================================
:: run.bat - One-click compile and run script
:: Store Management System
:: ================================================

set JAR=lib\mysql-connector-j-8.4.0.jar

echo Compiling...
javac --release 8 -cp "%JAR%" -d out src\*.java

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed. Fix the errors above and try again.
    pause
    exit /b 1
)

echo Compilation successful!
echo.
java -cp "out;%JAR%" Main

pause
