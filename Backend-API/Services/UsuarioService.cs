using UsuariosApi.DTOs;
using UsuariosApi.Entities;
using UsuariosApi.Interfaces;

namespace UsuariosApi.Services;

public class UsuarioService : IUsuarioService
{
    // Edad máxima permitida para registrarse
    private const int EdadMaxima = 80;

    private readonly IUsuarioRepository _repo;

    public UsuarioService(IUsuarioRepository repo)
    {
        _repo = repo;
    }

    // ==========================================
    // OBTENER TODOS
    // ==========================================
    public async Task<List<UsuarioDto>> ObtenerTodosAsync()
    {
        var usuarios = await _repo.ObtenerTodosAsync();

        return usuarios
            .Select(Map)
            .ToList();
    }

    // ==========================================
    // OBTENER POR ID
    // ==========================================
    public async Task<UsuarioDto?> ObtenerPorIdAsync(int id)
    {
        var usuario = await _repo.ObtenerPorIdAsync(id);

        return usuario is null
            ? null
            : Map(usuario);
    }

    // ==========================================
    // CREAR USUARIO
    // ==========================================
    public async Task<UsuarioDto> CrearAsync(CrearUsuarioDto d)
    {
        var correo = d.Correo
            .Trim()
            .ToLowerInvariant();

      
        if (await _repo.ObtenerPorCorreoAsync(correo) is not null)
        {
            throw new InvalidOperationException(
                "Ya existe un usuario con ese correo."
            );
        }

        // Calcular edad  y validar el límite máximo
        int edadCalculada = CalcularEdad(d.FechaNacimiento);
        ValidarEdad(edadCalculada);

        // Convertir contraseña en HASH seguro con BCrypt (Punto 4.4)
        var passwordHash = BCrypt.Net.BCrypt.HashPassword(d.Password);

        
        var usuario = new Usuario
        {
            Nombre = d.Nombre.Trim(),
            Apellido = d.Apellido.Trim(),
            Correo = correo,
            PasswordHash = passwordHash,
            FechaNacimiento = d.FechaNacimiento,
            Edad = edadCalculada.ToString(),       
            Universidad = d.Universidad.Trim(),
            Semestre = d.Semestre
        };

        await _repo.CrearAsync(usuario);

        return Map(usuario);
    }

    // ==========================================
    // LOGIN
    // ==========================================
    public async Task<UsuarioDto?> LoginAsync(LoginDto dto)
    {
        var correo = dto.Correo
            .Trim()
            .ToLowerInvariant();

        // Buscar usuario por correo
        var usuario = await _repo.ObtenerPorCorreoAsync(correo);

        // Usuario no encontrado o inactivo
        if (usuario is null || !usuario.Activo)
        {
            return null;
        }

        // Validar que tenga contraseña registrada
        if (string.IsNullOrWhiteSpace(usuario.PasswordHash))
        {
            return null;
        }

        // Comparar la contraseña ingresada con el HASH almacenado en la BD (Punto 4.4)
        var passwordCorrecto = BCrypt.Net.BCrypt.Verify(dto.Password, usuario.PasswordHash);

        if (!passwordCorrecto)
        {
            return null;
        }

        // LOGIN CORRECTO
        return Map(usuario);
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================
    public async Task<bool> ActualizarAsync(int id, ActualizarUsuarioDto d)
    {
        var usuario = await _repo.ObtenerPorIdAsync(id);

        if (usuario is null)
        {
            return false;
        }

        var correo = d.Correo
            .Trim()
            .ToLowerInvariant();

        var otro = await _repo.ObtenerPorCorreoAsync(correo);

        if (otro is not null && otro.Id != id)
        {
            throw new InvalidOperationException(
                "El correo pertenece a otro usuario."
            );
        }

        // Recalcular edad y validar el límite máximo
        int edadCalculada = CalcularEdad(d.FechaNacimiento);
        ValidarEdad(edadCalculada);

        usuario.Nombre = d.Nombre.Trim();
        usuario.Apellido = d.Apellido.Trim();
        usuario.Correo = correo;
        usuario.FechaNacimiento = d.FechaNacimiento;
        usuario.Edad = edadCalculada.ToString(); 
        usuario.Universidad = d.Universidad.Trim();
        usuario.Semestre = d.Semestre;
        usuario.Activo = d.Activo;

        await _repo.ActualizarAsync(usuario);

        return true;
    }

    // ==========================================
    // ELIMINAR
    // ==========================================
    public async Task<bool> EliminarAsync(int id)
    {
        var usuario = await _repo.ObtenerPorIdAsync(id);

        if (usuario is null)
        {
            return false;
        }

        await _repo.EliminarAsync(usuario);

        return true;
    }

    // ==========================================
    // VALIDACIÓN DE EDAD
    // ==========================================
    private static int CalcularEdad(DateTime nacimiento)
    {
        DateTime hoy = DateTime.Today;

        int edad = hoy.Year - nacimiento.Year;

        // Si aún no llegó su cumpleaños este año, se resta 1
        if (hoy.Month < nacimiento.Month ||
            (hoy.Month == nacimiento.Month && hoy.Day < nacimiento.Day))
        {
            edad--;
        }

        return edad;
    }

    private static void ValidarEdad(int edad)
    {
        if (edad < 0)
        {
            throw new InvalidOperationException(
                "La fecha de nacimiento no puede ser futura."
            );
        }

        if (edad > EdadMaxima)
        {
            throw new InvalidOperationException(
                $"La edad no puede ser mayor a {EdadMaxima} años."
            );
        }
    }

    // ==========================================
    // MAPEO ENTITY -> DTO
    // ==========================================
    private static UsuarioDto Map(Usuario u)
    {
        return new UsuarioDto
        {
            Id = u.Id,
            Nombre = u.Nombre,
            Apellido = u.Apellido,
            Correo = u.Correo,
            Edad = u.Edad,               
            Universidad = u.Universidad,
            Semestre = u.Semestre,       
            Activo = u.Activo,
            FechaCreacion = u.FechaCreacion
        };
    }
}