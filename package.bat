@echo off
setlocal

set JAVA_HOME=C:\Program Files\Java\jdk-21.0.1
set JAR_CMD="%JAVA_HOME%\bin\jar.exe"

set OUT=out
set LIB=lib\sqlite-jdbc-3.51.2.0.jar
set JAR_NAME=audiencias.jar
set MAIN_CLASS=br.edu.ufca.audiencias.Main
set TEMP_DIR=_jar_tmp

echo ============================================================
echo  Empacotando JAR executavel (fat/uber JAR)
echo  Saida: %JAR_NAME%
echo ============================================================
echo.

:: Verificar se o projeto foi compilado
if not exist "%OUT%" (
    echo [ERRO] Projeto nao compilado. Execute compile.bat primeiro.
    pause
    exit /b 1
)

:: Verificar SQLite JDBC
if not exist "%LIB%" (
    echo [ERRO] JAR do SQLite nao encontrado em "%LIB%"
    pause
    exit /b 1
)

:: Limpar diretorio temporario anterior
if exist "%TEMP_DIR%" rmdir /s /q "%TEMP_DIR%"
mkdir "%TEMP_DIR%"

echo [1/4] Extraindo dependencias do SQLite JDBC...
pushd "%TEMP_DIR%"
%JAR_CMD% xf "..\%LIB%"
popd

:: Remover o MANIFEST.MF do SQLite (vamos criar o nosso)
if exist "%TEMP_DIR%\META-INF\MANIFEST.MF" del /f /q "%TEMP_DIR%\META-INF\MANIFEST.MF"

echo [2/4] Copiando classes compiladas...
xcopy "%OUT%\*" "%TEMP_DIR%\" /E /I /Y >nul

echo [3/4] Gerando MANIFEST.MF...
:: O MANIFEST precisa terminar com linha em branco (requisito do formato JAR)
(
    echo Main-Class: %MAIN_CLASS%
    echo.
) > _manifest.txt

echo [4/4] Criando JAR executavel...
%JAR_CMD% cfm "%JAR_NAME%" "_manifest.txt" -C "%TEMP_DIR%" .

:: Limpeza
rmdir /s /q "%TEMP_DIR%"
del /f /q "_manifest.txt"

echo.
if %ERRORLEVEL% EQU 0 (
    echo [OK] JAR gerado com sucesso: %JAR_NAME%
    echo.
    echo      Para executar:
    echo      "%JAVA_HOME%\bin\java.exe" -jar %JAR_NAME%
    echo.
    echo      Ou simplesmente clique duas vezes em %JAR_NAME%
    echo      (desde que o Java esteja associado a arquivos .jar)
) else (
    echo [ERRO] Falha ao gerar o JAR. Verifique os passos acima.
)

pause
