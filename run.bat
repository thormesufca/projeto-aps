@echo off
setlocal

set JAVA_HOME=C:\Program Files\Java\jdk-21.0.1
set JAVA="%JAVA_HOME%\bin\java.exe"

set OUT=out
set LIB=lib\sqlite-jdbc-3.51.2.0.jar;lib\flatlaf-3.7.jar;lib\flatlaf-extras-3.7.jar
set MAIN_CLASS=br.edu.ufca.audiencias.Main

if not exist "%OUT%" (
    echo Projeto nao compilado. Execute compile.bat primeiro.
    pause
    exit /b 1
)

if not exist "lib\sqlite-jdbc-3.51.2.0.jar" (
    echo ERRO: JAR do SQLite nao encontrado em lib\sqlite-jdbc-3.51.2.0.jar
    echo Consulte lib\LEIA-ME.txt para instrucoes de download.
    pause
    exit /b 1
)
if not exist "lib\flatlaf-3.7.jar" (
    echo ERRO: FlatLaf JAR nao encontrado em lib\flatlaf-3.7.jar
    echo Consulte lib\LEIA-ME.txt para instrucoes de download.
    pause
    exit /b 1
)
if not exist "lib\flatlaf-extras-3.7.jar" (
    echo ERRO: FlatLaf Extras JAR nao encontrado em lib\flatlaf-extras-3.7.jar
    echo Consulte lib\LEIA-ME.txt para instrucoes de download.
    pause
    exit /b 1
)

echo Iniciando Sistema de Gestao de Processos e Audiencias...
%JAVA% -cp "%OUT%;%LIB%" %MAIN_CLASS%
