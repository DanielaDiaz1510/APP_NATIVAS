# 📱 User Management Mobile App

Aplicación móvil desarrollada con Kotlin para Android que consume una
API REST desarrollada con ASP.NET Core y utiliza MySQL como sistema
de persistencia.

## 🚀 Tecnologías

### Mobile
- Kotlin
- Android Studio
- Retrofit
- Gson
- XML Layouts

### Backend
- C#
- ASP.NET Core Web API
- Entity Framework Core
- BCrypt
- Repository Pattern

### Database
- MySQL

## 🏗️ Arquitectura

Android App
    ↓
Retrofit
    ↓
ASP.NET Core REST API
    ↓
Controller
    ↓
Service
    ↓
Repository
    ↓
Entity Framework Core
    ↓
MySQL

## 🔐 Autenticación

Las contraseñas no se almacenan en texto plano.

Durante el registro:

Password → BCrypt Hash → MySQL

Durante el login:

Password → BCrypt Verify → PasswordHash

## 📌 Funcionalidades

- Registro de usuarios
- Inicio de sesión
- Validación de credenciales
- Hash seguro de contraseñas
- Consulta de usuarios
- Actualización de usuarios
- Eliminación de usuarios
- Manejo de roles
- Consumo de API REST desde Android

## 🔗 Endpoints

| Método | Endpoint | Descripción |
|---|---|---|
| GET | /api/Usuarios | Consultar usuarios |
| GET | /api/Usuarios/{id} | Consultar usuario |
| POST | /api/Usuarios | Crear usuario |
| POST | /api/Usuarios/login | Iniciar sesión |
| PUT | /api/Usuarios/{id} | Actualizar usuario |
| DELETE | /api/Usuarios/{id} | Eliminar usuario |