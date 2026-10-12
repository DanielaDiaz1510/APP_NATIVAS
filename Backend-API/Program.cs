using Microsoft.EntityFrameworkCore;
using UsuariosApi.Data;
using UsuariosApi.Interfaces;
using UsuariosApi.Repositories;
using UsuariosApi.Services;

var builder = WebApplication.CreateBuilder(args);

// ========================================================
//  CONFIGURACIÓN DE RED PARA EL PARCIAL
// ========================================================
// Conectar el celular al puerto
builder.WebHost.ConfigureKestrel(options =>
{
    options.ListenAnyIP(62128); 
});

builder.Services.AddControllers();

// Conexión a la base de datos

var connectionString = builder.Configuration.GetConnectionString("MySql");

builder.Services.AddDbContext<ApplicationDbContext>(options =>
    options.UseMySql(
        connectionString,
        ServerVersion.AutoDetect(connectionString)
    ));

// Inyección de dependencias para los repositorios y servicios
builder.Services.AddScoped<IUsuarioRepository, UsuarioRepository>();
builder.Services.AddScoped<IUsuarioService, UsuarioService>();

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

app.UseSwagger();
app.UseSwaggerUI();


app.MapControllers();

app.Run();
