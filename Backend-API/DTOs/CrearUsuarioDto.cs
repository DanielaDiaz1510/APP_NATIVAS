using System.ComponentModel.DataAnnotations;

namespace UsuariosApi.DTOs;

public class CrearUsuarioDto
{
    [Required(ErrorMessage = "El nombre es obligatorio")]
    [MaxLength(100)]
    public string Nombre { get; set; } = string.Empty;

    [Required(ErrorMessage = "El apellido es obligatorio")]
    [MaxLength(100)]
    public string Apellido { get; set; } = string.Empty;

    [Required(ErrorMessage = "El correo es obligatorio")]
    [EmailAddress(ErrorMessage = "El formato del correo no es válido")]
    [MaxLength(150)]
    public string Correo { get; set; } = string.Empty;

    [Required(ErrorMessage = "La contraseña es obligatoria")]
    [MinLength(6, ErrorMessage = "La contraseña debe tener al menos 6 caracteres")]
    public string Password { get; set; } = string.Empty;

    // Nuevos campos
    [Required(ErrorMessage = "La fecha de nacimiento es obligatoria")]
    public DateTime FechaNacimiento { get; set; }

    [Required(ErrorMessage = "La universidad es obligatoria")]
    [MaxLength(150)]
    public string Universidad { get; set; } = string.Empty;

    [Required(ErrorMessage = "El semestre es obligatorio")]
    [Range(1, 10, ErrorMessage = "El semestre debe estar entre 1 y 10")]
    public int Semestre { get; set; }
}
