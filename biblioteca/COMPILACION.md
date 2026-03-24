# Guía de Compilación y Ejecución del Proyecto Biblioteca

Este proyecto está construido con **Java 17** y **Maven**, y está dividido en múltiples módulos (Microservicios y Frontend).

## Prerrequisitos
- **Java Development Kit (JDK) 17** o superior.
- **Apache Maven**.

## 1. Compilar todo el proyecto

Para compilar todo el proyecto (todos los microservicios y la aplicación frontend de una sola vez), abre una terminal en la carpeta principal del proyecto (`biblioteca`) donde se encuentra el archivo `pom.xml` padre, y ejecuta:

```bash
mvn clean install
```
o simplemente:
```bash
mvn clean package
```

Esto descargará todas las dependencias necesarias, compilará el código y generará los archivos ejecutables `.jar` dentro de la carpeta `target` de cada módulo.

## 2. Ejecutar los Microservicios

Tendrás que levantar cada módulo por separado. Puedes hacerlo de dos maneras:

### Opción A (Usando Maven)
Abre una terminal por cada servicio, entra en su carpeta respectiva y ejecuta el plugin de Spring Boot:

**Usuarios Service:**
```bash
cd usuarios-service
mvn spring-boot:run
```

**Catálogo Service:**
```bash
cd catalogo-service
mvn spring-boot:run
```

**Préstamos Service:**
```bash
cd prestamos-service
mvn spring-boot:run
```

**Frontend App:**
```bash
cd frontend-app
mvn spring-boot:run
```

### Opción B (Usando el archivo JAR)
Después de haber compilado el proyecto con `mvn clean package`, ve a la carpeta de cada servicio y ejecuta el `.jar` generado. Ejemplo para el servicio de usuarios:

```bash
cd usuarios-service/target
java -jar usuarios-service-1.0-SNAPSHOT.jar
```
*(El nombre exacto del `.jar` puede variar ligeramente dependiendo de cómo esté configurado cada pom.xml)*
