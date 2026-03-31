@echo off
set "SERVICES=usuarios-service catalogo-service prestamos-service frontend-app"

for %%S in (%SERVICES%) do (
    echo Iniciando %%S...
    start /MIN "" cmd /c "cd %%S && mvn spring-boot:run > NUL 2>&1"
)

echo Todos los servicios se estan iniciando en segundo plano.
