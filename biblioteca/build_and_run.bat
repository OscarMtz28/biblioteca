@echo off
echo Terminando instancias previas...
for %%P in (8080 8081 8082 8083) do (
    for /f "tokens=5" %%v in ('netstat -ano ^| find "LISTENING" ^| find ":%%P "') do (
        taskkill /F /PID %%v 2>nul
    )
)

echo Compilando proyecto...
call mvn clean package -DskipTests

set "SERVICES=usuarios-service catalogo-service prestamos-service frontend-app"

for %%S in (%SERVICES%) do (
    echo Iniciando %%S...
    for /f "delims=" %%J in ('dir /b "%%S\target\%%S*.jar" 2^>nul') do (
        start /B "" cmd /c "java -jar "%%S\target\%%J" > NUL 2>&1"
    )
)

echo El proyecto se esta ejecutando en segundo plano!
