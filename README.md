#  Sistema de Biblioteca — Microservicios con Spring Boot 

Sistema de gestión de biblioteca desarrollado con arquitectura de microservicios usando **Java + Spring Boot + Maven**.

---

## Arquitectura del Proyecto

El sistema está compuesto por 4 servicios independientes:

| Servicio           | Descripción                          | Puerto |
|--------------------|--------------------------------------|--------|
| `usuarios-service` | Gestión de usuarios y autenticación  | 8081   |
| `catalogo-service` | Gestión del catálogo de libros       | 8082   |
| `prestamos-service`| Gestión de préstamos de libros       | 8083   |
| `frontend-app`     | Interfaz de usuario (Frontend)       | 8080   |

---

##  Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado lo siguiente:

- **Java JDK 17 o superior**
  - Verificar: `java -version`
  - Descarga: https://www.oracle.com/java/technologies/downloads/

- **Apache Maven 3.8 o superior**
  - Verificar: `mvn -version`
  - Descarga: https://maven.apache.org/download.cgi

- **Git**
  - Verificar: `git --version`
  - Descarga: https://git-scm.com/

- **PowerShell 5.1 o superior** (incluido en Windows 10/11)

---

##  Instalación y Clonación del Repositorio

### 1. Clonar el repositorio

```bash
git clone https://github.com/OscarMtz28/biblioteca.git
```

### 2. Entrar a la carpeta del proyecto

```bash
cd biblioteca/biblioteca
```

---

## Configuración del Entorno

### Variables de entorno y puertos

Cada microservicio corre en su propio puerto. Asegúrate de que los siguientes puertos estén **libres** antes de iniciar:

| Puerto | Servicio           |
|--------|--------------------|
| 8081   | usuarios-service   |
| 8082   | catalogo-service   |
| 8083   | prestamos-service  |
| 8080   | frontend-app       |

>  Si algún puerto está ocupado, el script `build_and_run.bat` lo liberará automáticamente antes de iniciar los servicios.

---

## Ejecución del Proyecto

Existen **dos formas** de levantar el sistema:

---

### Opción 1: Compilar y ejecutar (Recomendada para primera vez)

Este script **mata instancias previas**, **compila todo el proyecto** y **levanta todos los servicios**:

**En CMD (Símbolo del sistema):**
```cmd
build_and_run.bat
```


**¿Qué hace este script?**
1. Libera los puertos 8080, 8081, 8082 y 8083 si están ocupados
2. Ejecuta `mvn clean package -DskipTests` para compilar todos los servicios
3. Inicia cada servicio como proceso independiente en segundo plano

---

### Opción 2: Solo levantar servicios (sin recompilar)

Si el proyecto ya fue compilado anteriormente y solo deseas levantar los servicios:

**En CMD (Símbolo del sistema):**
```cmd
run_all.bat
```

**¿Qué hace este script?**
1. Inicia cada servicio usando `mvn spring-boot:run`
2. Cada servicio corre en segundo plano de forma independiente

---

### Opción 3: Levantar un servicio individualmente

Para iniciar un servicio individualmente:

```bash
cd usuarios-service
mvn spring-boot:run
```

Repite el proceso en terminales separadas para cada servicio.

---

##  Verificación de Servicios

Una vez ejecutado el proyecto, verifica que los servicios estén corriendo accediendo en tu navegador:

| Servicio           | URL                          | Documentación API (Swagger)                 |
|--------------------|------------------------------|---------------------------------------------|
| usuarios-service   | http://localhost:8081        | http://localhost:8081/swagger-ui/index.html |
| catalogo-service   | http://localhost:8082        | http://localhost:8082/swagger-ui/index.html |
| prestamos-service  | http://localhost:8083        | http://localhost:8083/swagger-ui/index.html |
| frontend-app       | http://localhost:8080        | N/A                                         |

Todos los servicios se consumen desde el frontend-app, por lo que no es necesario acceder a los servicios individualmente. 

---



##  Detener los Servicios

Para detener todos los servicios que corren en los puertos del proyecto de forma fácil y masiva:

**En CMD:**
```cmd
stop_all.bat
```

**En Terminal:**
```bash
ctrl + c
```

##  Estructura del Proyecto

```
biblioteca/
├── biblioteca/
│   ├── usuarios-service/        # Microservicio de usuarios
│   │   ├── src/
│   │   ├── target/
│   │   └── pom.xml
│   ├── catalogo-service/        # Microservicio de catálogo
│   │   ├── src/
│   │   ├── target/
│   │   └── pom.xml
│   ├── prestamos-service/       # Microservicio de préstamos
│   │   ├── src/
│   │   ├── target/
│   │   └── pom.xml
│   ├── frontend-app/            # Aplicación frontend
│   │   ├── src/
│   │   ├── target/
│   │   └── pom.xml
│   ├── src/                     # Fuentes del proyecto padre
│   ├── pom.xml                  # POM padre (Maven multi-módulo)
│   ├── build_and_run.ps1        # Script: compilar y ejecutar todo
│   └── run_all.ps1              # Script: solo ejecutar servicios
└── README.md
```

---

##  Equipo de Desarrollo

Proyecto desarrollado para la materia de **Integración de Sistemas** — UAM Cuajimalpa.
**Integrantes:**

-González Rodriguez José Alberto

-Martinez Barrales Oscar

-Mejia García Oswaldo

-Morales Lucas Luis Eduardo
