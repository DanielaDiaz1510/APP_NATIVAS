# 📱 App de Gestión de Usuarios – Android + API REST

Una app Android en **Kotlin** que se conecta a una **API REST propia en ASP.NET Core**. La API guarda los datos en **MySQL**. Con la app puedes registrarte, iniciar sesión y consultar, editar o eliminar usuarios.

*Taller práctico de la materia Aplicaciones Móviles Nativas – Fundación Universitaria Compensar.*

## 🛠️ Tecnologías

| Parte | Tecnologías |
|---|---|
| App móvil | Kotlin, Android Studio, Retrofit, Gson, XML Layouts |
| Backend | C#, ASP.NET Core Web API, Entity Framework Core, BCrypt, Swagger |
| Base de datos | MySQL |

## 🏗️ Cómo funciona

```
App Android (Kotlin)
      │  Retrofit · HTTP / JSON
      ▼
API REST ASP.NET Core
  Controller → Service → Repository → Entity Framework Core
      ▼
    MySQL
```

- **Seguridad:** las contraseñas no se guardan en texto plano. Se cifran con **BCrypt** al registrarse y se verifican al iniciar sesión.
- **Organización:** el backend usa DTOs, inyección de dependencias e interfaces para el servicio y el repositorio.

## 🔗 Endpoints

| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/api/Usuarios/login` | Iniciar sesión |
| GET | `/api/Usuarios` | Listar usuarios |
| GET | `/api/Usuarios/{id}` | Consultar un usuario |
| POST | `/api/Usuarios` | Crear usuario |
| PUT | `/api/Usuarios/{id}` | Actualizar usuario |
| DELETE | `/api/Usuarios/{id}` | Eliminar usuario |

## 🚀 Cómo ejecutarlo

1. **Prepara la base de datos.** En `Backend-API/appsettings.json`, cambia `TU_PASSWORD` por la contraseña de tu MySQL local.
2. **Arranca la API.** Entra a `Backend-API` y ejecuta `dotnet run`. La documentación queda en http://localhost:62128/swagger.
3. **Abre la app.** Abre `Cliente-Android` en Android Studio.
4. **Conecta la app con la API.** En `api/ApiClient.kt`, cambia `BASE_URL` por la IP de tu computador. El celular y el computador deben estar en la misma red Wi-Fi.
5. **Ejecuta la app** en un celular o emulador.

## 📁 Estructura

```
Backend-API/        API REST en ASP.NET Core (C#)
Cliente-Android/    App móvil en Kotlin
TALLER PRÁCTICO - APP.pdf   Enunciado y entregables del taller
```

---

👩‍💻 **Daniela Díaz Falla** · [LinkedIn](https://www.linkedin.com/in/daniela-diaz-517538385/) · [Portafolio](https://danieladiaz1510.github.io/Ejemplo_Portafolio/)
