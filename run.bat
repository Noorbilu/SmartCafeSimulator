@echo off
cd /d "%~dp0"

echo =====================================
echo       SMART CAFE SIMULATOR
echo =====================================

echo.
echo Compiling Java files...

if not exist bin mkdir bin

javac -d bin -cp "lib\*" src\Main.java src\util\*.java src\db\*.java src\model\*.java src\dao\*.java src\ui\*.java

if errorlevel 1 (
    echo.
    echo Compilation FAILED!
    pause
    exit /b 1
)

echo.
echo Compilation Successful!
echo.

echo Starting Smart Cafe Simulator...

java -cp "bin;lib\*" Main

echo.
echo Application closed.
pause