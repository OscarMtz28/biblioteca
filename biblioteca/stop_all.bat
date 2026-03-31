@echo off
echo Deteniendo servicios de la biblioteca...
for %%P in (8080 8081 8082 8083) do (
    for /f "tokens=5" %%v in ('netstat -ano ^| find "LISTENING" ^| find ":%%P "') do (
        echo Matando proceso %%v en el puerto %%P...
        taskkill /F /PID %%v 2>nul
    )
)
echo Todos los servicios han sido detenidos.
