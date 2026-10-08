using Microsoft.EntityFrameworkCore;
using UsuariosApi.Data;
using UsuariosApi.Interfaces;
using UsuariosApi.Repositories;
using UsuariosApi.Services;

var builder = WebApplication.CreateBuilder(args);

// ========================================================
// 🌟 CONFIGURACIÓN DE RED PARA EL PARCIAL
// ========================================================
// Le enseñamos a C# que abra la antena y escuche a tu celular Samsung por el puerto 62128
builder.WebHost.ConfigureKestrel(options =>
{
    options.ListenAnyIP(62128); 
});

builder.Services.AddControllers();

// Conexión a tu base de datos de MySQL Workbench
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

// 🌟 LIBERADO: Swagger configurado para encenderse de forma directa en el taller
app.UseSwagger();
app.UseSwaggerUI();

// Para la práctica local con el celular físico evitamos redireccionar HTTP hacia HTTPS
// app.UseHttpsRedirection();

app.MapControllers();

app.Run();
