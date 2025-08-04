# 🚀 Guía Completa de Despliegue a Producción

## 📋 Resumen del Proyecto

**CampusBookings** es una aplicación de gestión de reservas para la Universidad Surcolombiana (USCO) desplegada en:

- **Backend**: Railway (Spring Boot + PostgreSQL)
- **Frontend**: Vercel (Angular + Tailwind CSS)

## 🔗 URLs de Producción

### **Frontend (Vercel):**
- **URL**: `https://campus-bookings-front-3zp2nqik9-nick130920s-projects.vercel.app`
- **Repositorio**: `https://github.com/nick130920/CampusBookings-Front`

### **Backend (Railway):**
- **URL**: `https://campusbookings-production.up.railway.app`
- **API**: `https://campusbookings-production.up.railway.app/api`
- **Repositorio**: `https://github.com/nick130920/CampusBookings`

## 🏗️ Arquitectura del Sistema

### **Backend (Spring Boot)**
- **Framework**: Spring Boot 3.4.0
- **Java**: 17 (compatible con Railway)
- **Arquitectura**: Hexagonal (Clean Architecture)
- **Base de Datos**: PostgreSQL
- **Seguridad**: JWT + Spring Security
- **Documentación**: OpenAPI/Swagger

### **Frontend (Angular)**
- **Framework**: Angular 17
- **Styling**: Tailwind CSS
- **UI Components**: PrimeNG
- **Estado**: Services + RxJS
- **Autenticación**: JWT Interceptor

## 🚀 Configuración de Despliegue

### **1. Backend - Railway**

#### **Variables de Entorno Requeridas:**

```bash
# Base de datos (automáticas de Railway)
DATABASE_URL=postgresql://postgres:LDXviwbPMjYluAUQbHSJArEggXporhQT@postgres.railway.internal:5432/railway
PGDATABASE=railway
PGHOST=postgres.railway.internal
PGPASSWORD=LDXviwbPMjYluAUQbHSJArEggXporhQT
PGPORT=5432
PGUSER=postgres

# Configuración manual (agregar en Railway)
JWT_SECRET=7A2F4D6B8E9C1A3F5E7B9D0C2E4F6A8B1C3D5E7F9A0B2C4D6E8F0A1B3C5D7E9F2A4B6C8D0E2F4A6B8C0D2E4F6A8B0C2D4E6F8A0B1C3E5F7A9B1D3F5A7C9E1F3A5C7E9F1A3C5E7F9A1C3E5F
SPRING_PROFILES_ACTIVE=prod
SPRING_MAIL_USERNAME=namc1309@gmail.com
SPRING_MAIL_PASSWORD=pcorcxkrcjgjiivh
SECURITY_CORS_ALLOWED_ORIGINS=https://campus-bookings-front-3zp2nqik9-nick130920s-projects.vercel.app,http://localhost:4200
```

#### **Configuración de Base de Datos:**
- **RailwayDataSourceConfig**: Maneja automáticamente la conversión de `DATABASE_URL`
- **Pool de conexiones**: Optimizado para Railway (máx 5 conexiones)
- **Migraciones**: Automáticas con `spring.jpa.hibernate.ddl-auto=update`

#### **Configuración de Seguridad:**
- **CORS**: Configurado para permitir Vercel y desarrollo local
- **JWT**: Configurado con secret personalizable
- **Rate Limiting**: 30 requests/minuto en producción
- **Brute Force Protection**: 3 intentos máximos

### **2. Frontend - Vercel**

#### **Variables de Entorno Requeridas:**

```bash
# Configurar en Vercel Dashboard → Settings → Environment Variables
API_URL=https://campusbookings-production.up.railway.app/api
AUTH_ENDPOINT=/auth
ENABLE_DEBUG=false
```

#### **Configuración de Build:**
```json
{
  "scripts": {
    "build": "ng build --configuration production",
    "build:prod": "ng build --configuration production"
  }
}
```

#### **Configuración de Angular:**
- **Optimización**: Habilitada para producción
- **Source Maps**: Deshabilitados
- **Tree Shaking**: Habilitado
- **Compresión**: Habilitada

## 🔧 Configuraciones Específicas

### **Backend - application-prod.properties**

```properties
# Logging optimizado para producción
logging.level.root=WARN
logging.level.edu.usco.campusbookings=INFO

# JPA optimizado
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.jdbc.batch_size=20

# Hikari optimizado para Railway
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=1

# Seguridad reforzada
security.rate-limit.requests-per-minute=30
security.login.max-attempts=3

# Caché y compresión
spring.cache.type=caffeine
server.compression.enabled=true
```

### **Frontend - environment.prod.ts**

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://campusbookings-production.up.railway.app/api',
  authEndpoint: '/auth',
  defaultLanguage: 'es',
  version: '0.0.1',
  enableDebug: false
};
```

## 🚀 Proceso de Despliegue

### **1. Despliegue del Backend (Railway)**

#### **Configuración Inicial:**
1. Crear proyecto en Railway
2. Conectar repositorio GitHub: `https://github.com/nick130920/CampusBookings`
3. Crear servicio PostgreSQL
4. Configurar variables de entorno

#### **Despliegue Automático:**
- Railway detecta automáticamente nuevos commits en `master`
- Build automático con Maven
- Despliegue automático después del build exitoso

#### **Forzar Redeploy (si es necesario):**
1. Railway Dashboard → Servicio de aplicación
2. Pestaña "Deployments" → "Deploy Now"
3. Seleccionar "Deploy from GitHub"

### **2. Despliegue del Frontend (Vercel)**

#### **Configuración Inicial:**
1. Conectar repositorio GitHub: `https://github.com/nick130920/CampusBookings-Front`
2. Configurar variables de entorno
3. Configurar dominio personalizado (opcional)

#### **Despliegue Automático:**
- Vercel detecta automáticamente nuevos commits en `master`
- Build automático con Angular CLI
- Despliegue automático después del build exitoso

## 🔍 Verificación del Despliegue

### **Backend - Endpoints de Verificación:**

```bash
# Health Check
GET https://campusbookings-production.up.railway.app/actuator/health

# API Endpoints
GET https://campusbookings-production.up.railway.app/api/escenarios
POST https://campusbookings-production.up.railway.app/api/auth/authenticate

# Documentación API
GET https://campusbookings-production.up.railway.app/swagger-ui/
```

### **Frontend - Verificación:**

```bash
# Aplicación principal
https://campus-bookings-front.vercel.app/

# Verificar comunicación con backend
# Abrir DevTools → Network → Intentar login
```

## 🔧 Solución de Problemas

### **Problemas Comunes de Backend:**

#### **Error de Conexión a Base de Datos:**
```bash
# Verificar variables de entorno en Railway
DATABASE_URL=postgresql://postgres:password@host:port/db
PGHOST=host
PGPORT=port
PGDATABASE=db
PGUSER=postgres
PGPASSWORD=password
```

#### **Error de CORS:**
```bash
# Verificar SECURITY_CORS_ALLOWED_ORIGINS
SECURITY_CORS_ALLOWED_ORIGINS=https://campus-bookings-front-3zp2nqik9-nick130920s-projects.vercel.app,http://localhost:4200
```

#### **Error de JWT:**
```bash
# Verificar JWT_SECRET esté configurado
JWT_SECRET=tu_jwt_secret_aqui
```

### **Problemas Comunes de Frontend:**

#### **Error de Conexión al API:**
```bash
# Verificar API_URL en Vercel
API_URL=https://campusbookings-production.up.railway.app/api
```

#### **Error de Build:**
```bash
# Verificar configuración de Angular
ng build --configuration production
```

## 📊 Monitoreo y Logs

### **Railway (Backend):**
- **Logs**: Railway Dashboard → Servicio → Logs
- **Métricas**: Railway Dashboard → Servicio → Metrics
- **Variables**: Railway Dashboard → Servicio → Variables

### **Vercel (Frontend):**
- **Logs**: Vercel Dashboard → Proyecto → Deployments → Logs
- **Analytics**: Vercel Dashboard → Proyecto → Analytics
- **Variables**: Vercel Dashboard → Proyecto → Settings → Environment Variables

## 🔄 Actualizaciones y Mantenimiento

### **Proceso de Actualización:**

1. **Desarrollo Local:**
   ```bash
   # Backend
   git add .
   git commit -m "Descripción de cambios"
   git push origin master
   
   # Frontend
   cd ../CampusBookings-front
   git add .
   git commit -m "Descripción de cambios"
   git push origin master
   ```

2. **Despliegue Automático:**
   - Railway detecta cambios en backend
   - Vercel detecta cambios en frontend
   - Despliegue automático en ambos servicios

3. **Verificación:**
   - Probar endpoints críticos
   - Verificar funcionalidad principal
   - Revisar logs por errores

### **Rollback (si es necesario):**

#### **Railway:**
1. Railway Dashboard → Deployments
2. Seleccionar versión anterior
3. "Redeploy" versión anterior

#### **Vercel:**
1. Vercel Dashboard → Deployments
2. Seleccionar versión anterior
3. "Promote to Production"

## 📝 Notas Importantes

### **Seguridad:**
- ✅ JWT_SECRET configurado en Railway
- ✅ CORS configurado para dominios específicos
- ✅ Rate limiting habilitado
- ✅ HTTPS forzado en ambos servicios

### **Performance:**
- ✅ Pool de conexiones optimizado para Railway
- ✅ Caché habilitado en producción
- ✅ Compresión habilitada
- ✅ Build optimizado para Angular

### **Escalabilidad:**
- ✅ Railway auto-scaling habilitado
- ✅ Vercel edge functions disponibles
- ✅ CDN automático en Vercel

## 🔗 Enlaces Útiles

### **Documentación:**
- [Railway Documentation](https://docs.railway.app/)
- [Vercel Documentation](https://vercel.com/docs)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Angular Documentation](https://angular.io/docs)

### **Repositorios:**
- [Backend Repository](https://github.com/nick130920/CampusBookings)
- [Frontend Repository](https://github.com/nick130920/CampusBookings-Front)

### **Dashboards:**
- [Railway Dashboard](https://railway.app/dashboard)
- [Vercel Dashboard](https://vercel.com/dashboard)

---

**Última actualización**: Agosto 4, 2025  
**Versión**: 1.0.0  
**Estado**: Producción Activa ✅ 