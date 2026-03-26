@echo off
setlocal

set JAVA_HOME=C:\Program Files\Java\jdk-21.0.1
set JAVAC="%JAVA_HOME%\bin\javac.exe"
set JAVA="%JAVA_HOME%\bin\java.exe"

set SRC=src\main\java
set OUT=out
set LIB=lib\sqlite-jdbc-3.51.2.0.jar;lib\flatlaf-3.7.jar;lib\flatlaf-extras-3.7.jar

:: Verificar se os JARs existem
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


echo ============================================================
echo  Compilando Sistema de Gestao de Processos e Audiencias
echo  Java: %JAVA_HOME%
echo ============================================================
echo.

:: Limpar compilacao anterior
if exist "%OUT%" rmdir /s /q "%OUT%"
mkdir "%OUT%"

:: Coletar todos os .java recursivamente
for /r "%SRC%" %%f in (*.java) do (
    echo %%f >> _sources.txt
)

%JAVAC% -encoding UTF-8 -g -cp "%LIB%" -d "%OUT%" @_sources.txt
del _sources.txt

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [OK] Compilacao concluida com sucesso!

    :: Copiar recursos de src/main/resources para out/
    echo      Copiando recursos...
    if exist "src\main\resources" (
        xcopy "src\main\resources\*" "%OUT%\" /E /I /Y /Q
    )

    :: Recriar banco de dados do zero
    echo      Recriando banco de dados...
    if exist "audiencias.db" del "audiencias.db"
    %JAVA% -cp "%OUT%;%LIB%" br.edu.ufca.audiencias.tools.InitDb
    if %ERRORLEVEL% NEQ 0 (
        echo [ERRO] Falha ao inicializar banco de dados.
        exit /b 1
    )

    :: Popular banco com dados de teste (somente com flag --seed)
    if "%~1"=="-s" (
        echo      Inserindo dados de teste...
        %JAVA% -cp "%OUT%;%LIB%" br.edu.ufca.audiencias.tools.SeedDb
        if %ERRORLEVEL% NEQ 0 (
            echo [ERRO] Falha ao inserir dados de teste.
            exit /b 1
        )
    )

    echo.
) else (
    echo.
    echo [ERRO] Falha na compilacao.
)
