Write-Host "Killing old instances..."
Get-NetTCPConnection -LocalPort 8080,8081,8082,8083 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess | ForEach-Object { Stop-Process -Id $_ -Force -ErrorAction SilentlyContinue }

Write-Host "Building project..."
mvn clean package -DskipTests

$services = @("usuarios-service", "catalogo-service", "prestamos-service", "frontend-app")
foreach ($service in $services) {
    Write-Host "Starting $service..."
    $jarPath = Get-ChildItem -Path ".\$service\target" -Filter "$service*.jar" | Select-Object -First 1
    if ($jarPath) {
        Start-Process java -ArgumentList "-jar `"$($jarPath.FullName)`"" -RedirectStandardOutput ".\$service.log" -RedirectStandardError ".\$service-error.log" -WindowStyle Hidden
    } else {
        Write-Host "JAR not found for $service"
    }
}
Write-Host "Project is running!"
