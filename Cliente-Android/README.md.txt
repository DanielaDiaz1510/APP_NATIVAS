# User Management Android App

Aplicación móvil desarrollada en Kotlin para Android que consume una API REST desarrollada con ASP.NET Core.

## Tecnologías

- Kotlin
- Android Studio
- Retrofit
- Gson
- XML Layouts
- REST API
- ASP.NET Core Backend
- MySQL

## Funcionalidades

- Inicio de sesión
- Registro de usuarios
- Validación de formularios
- Consumo de API REST
- Navegación entre Activities
- Comunicación mediante Retrofit
- Manejo de respuestas HTTP

## Arquitectura

```text
Android App
    |
    v
Activities
    |
    v
Models
    |
    v
Retrofit
    |
    | HTTP / JSON
    v
ASP.NET Core REST API
    |
    v
MySQL