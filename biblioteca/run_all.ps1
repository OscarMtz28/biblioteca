$services = @("usuarios-service", "catalogo-service", "prestamos-service", "frontend-app")

foreach ($service in $services) {
    Write-Host "Starting $service..."
    Start-Process cmd -ArgumentList "/c mvn spring-boot:run > ..\$service.log 2>&1" -WorkingDirectory ".\$service" -WindowStyle Hidden
}
Write-Host "All services started."
