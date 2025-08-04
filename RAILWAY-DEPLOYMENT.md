# 🚀 Guía de Despliegue en Railway

## 📋 Pasos para desplegar CampusBookings en Railway

### 1. **Crear Base de Datos PostgreSQL en Railway**

1. Ve a tu proyecto en Railway Dashboard
2. Haz clic en **"New Service"** → **"Database"** → **"PostgreSQL"**
3. Railway creará automáticamente una base de datos PostgreSQL
4. Anota las credenciales que te proporciona Railway

### 2. **Variables de Entorno en Railway**

Railway automáticamente proporciona estas variables de entorno a tu aplicación:

```bash
# Variables de PostgreSQL (automáticas)
DATABASE_URL=postgresql://username:password@host:port/database
PGHOST=host
PGPORT=port
PGDATABASE=database
PGUSER=username
PGPASSWORD=password

# Variables adicionales que necesitas configurar
JWT_SECRET=tu_jwt_secret_aqui
SPRING_PROFILES_ACTIVE=prod
```

### 3. **Configurar Variables de Entorno**

En tu servicio de aplicación en Railway:

1. Ve a la pestaña **"Variables"**
2. Agrega estas variables adicionales:

```bash
# JWT Configuration
JWT_SECRET=7A2F4D6B8E9C1A3F5E7B9D0C2E4F6A8B1C3D5E7F9A0B2C4D6E8F0A1B3C5D7E9F2A4B6C8D0E2F4A6B8C0D2E4F6A8B0C2D4E6F8A0B1C3E5F7A9B1D3F5A7C9E1F3A5C7E9F1A3C5E7F9A1C3E5F

# Spring Profile
SPRING_PROFILES_ACTIVE=prod

# Email Configuration (configura con tus credenciales reales)
SPRING_MAIL_USERNAME=tu_email@gmail.com
SPRING_MAIL_PASSWORD=tu_app_password

# CORS Configuration
SECURITY_CORS_ALLOWED_ORIGINS=https://tu-dominio-frontend.com
```

### 4. **Configuración de Puertos**

Railway automáticamente asigna un puerto. Tu aplicación está configurada para usar el puerto 8081, pero Railway puede usar cualquier puerto. La configuración actual maneja esto automáticamente.

### 5. **Migraciones de Base de Datos**

Las migraciones se ejecutarán automáticamente cuando la aplicación se inicie gracias a:
```properties
spring.jpa.hibernate.ddl-auto=update
```

### 6. **Verificar el Despliegue**

1. Una vez desplegado, Railway te dará una URL como: `https://tu-app.railway.app`
2. Puedes verificar que la aplicación esté funcionando visitando:
   - `https://tu-app.railway.app/actuator/health` (si tienes Spring Boot Actuator)
   - `https://tu-app.railway.app/api/escenarios` (endpoint de prueba)

## 🔧 Solución de Problemas Comunes

### Error de Conexión a Base de Datos
- Verifica que las variables `DATABASE_URL`, `PGUSER`, `PGPASSWORD` estén configuradas
- Asegúrate de que la base de datos PostgreSQL esté creada en Railway

### Error de Puerto
- Railway asigna puertos automáticamente, no necesitas configurar puertos manualmente

### Error de JWT
- Asegúrate de que `JWT_SECRET` esté configurado en las variables de entorno

## 📝 Notas Importantes

- **No uses docker-compose.yml**: Railway maneja los servicios de manera independiente
- **Base de datos separada**: Railway crea una base de datos PostgreSQL independiente
- **Variables de entorno**: Railway proporciona automáticamente las credenciales de la base de datos
- **Escalabilidad**: Railway puede escalar automáticamente tu aplicación según la demanda

## 🔗 Enlaces Útiles

- [Railway Documentation](https://docs.railway.app/)
- [Railway PostgreSQL](https://docs.railway.app/databases/postgresql)
- [Spring Boot on Railway](https://docs.railway.app/deploy/deployments/railway-up) 