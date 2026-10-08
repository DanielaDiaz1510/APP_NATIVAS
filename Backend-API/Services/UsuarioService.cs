using UsuariosApi.DTOs;
using UsuariosApi.Entities;
using UsuariosApi.Interfaces;

namespace UsuariosApi.Services;

public class UsuarioService : IUsuarioService
{
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
    // 🌟 CREAR USUARIO (CON CÁLCULO DE EDAD ESTRICTO SEGÚN PUNTO 4.1)
    // ==========================================
    public async Task<UsuarioDto> CrearAsync(CrearUsuarioDto d)
    {
        var correo = d.Correo
            .Trim()
            .ToLowerInvariant();

        // Validar que el correo no esté registrado (Punto 4.4)
        if (await _repo.ObtenerPorCorreoAsync(correo) is not null)
        {
            throw new InvalidOperationException(
                "Ya existe un usuario con ese correo."
            );
        }

        // 🧠 LÓGICA DEL TALLER: Calcular Edad considerando Día, Mes y Año de forma estricta (Punto 4.1)
        DateTime nacimiento = d.FechaNacimiento;
        DateTime hoy = DateTime.Today;

        int edadCalculada = hoy.Year - nacimiento.Year;

        // Si la persona no ha llegado a su mes de cumpleaños, o si está en su mes pero no ha llegado al día, se le resta 1 año
        if (hoy.Month < nacimiento.Month || (hoy.Month == nacimiento.Month && hoy.Day < nacimiento.Day))
        {
            edadCalculada--;
        }

        // Convertir contraseña en HASH seguro con BCrypt (Punto 4.4)
        var passwordHash = BCrypt.Net.BCrypt.HashPassword(d.Password);

        // Mapeo hacia la entidad (Se guarda el entero como String o Int según tu entidad, convirtiendo a string de forma limpia)
        var usuario = new Usuario
        {
            Nombre = d.Nombre.Trim(),
            Apellido = d.Apellido.Trim(),
            Correo = correo,
            PasswordHash = passwordHash,
            FechaNacimiento = d.FechaNacimiento,
            Edad = edadCalculada.ToString(),       // 🌟 CUMPLIMIENTO: Guarda únicamente el número entero de años calculados
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
    // ACTUALIZAR (Adaptado a la estructura del taller)
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

        // 🧠 LÓGICA DEL TALLER: Recalcular Edad considerando Día, Mes y Año estricto en caso de actualización
        DateTime nacimiento = d.FechaNacimiento;
        DateTime hoy = DateTime.Today;

        int edadCalculada = hoy.Year - nacimiento.Year;

        if (hoy.Month < nacimiento.Month || (hoy.Month == nacimiento.Month && hoy.Day < nacimiento.Day))
        {
            edadCalculada--;
        }

        usuario.Nombre = d.Nombre.Trim();
        usuario.Apellido = d.Apellido.Trim();
        usuario.Correo = correo;
        usuario.FechaNacimiento = d.FechaNacimiento;
        usuario.Edad = edadCalculada.ToString(); // 🌟 Guarda el entero de años recalculado
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
            Edad = u.Edad,               // Transfiere limpiamente el número de años cumplidos
            Universidad = u.Universidad,
            Semestre = u.Semestre,       
            Activo = u.Activo,
            FechaCreacion = u.FechaCreacion
        };
    }
}
