# 🚀 Fixes para Despliegue del Sistema de Permisos

## 🔧 Problemas Identificados y Corregidos

### **1. URL Duplicada en Frontend**
**Problema:** `environment.apiUrl` ya incluía `/api`, causando URLs como `/api/api/user/my-permissions`

**Solución:**
```typescript
// ANTES (incorrecto)
private readonly apiUrl = `${environment.apiUrl}/api/user`;

// DESPUÉS (correcto)
private readonly apiUrl = `${environment.apiUrl}/user`;
```

### **2. Configuración de Seguridad Faltante**
**Problema:** El endpoint `/api/user/**` no estaba configurado en SecurityConfig

**Solución:**
```java
// Agregado en SecurityConfig.java
.requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN", "COORDINATOR")
```

### **3. Nuevo Controlador de Usuario**
**Creado:** `UserPermissionsController.java` para endpoint `/api/user/my-permissions`

---

## 📋 Archivos Modificados

### **Backend:**
1. ✅ `UserPermissionsController.java` (NUEVO)
2. ✅ `JwtService.java` (agregado `extractUserId()`)  
3. ✅ `SecurityConfig.java` (configuración para `/api/user/**`)

### **Frontend:**
1. ✅ `authorization.service.ts` (URL corregida + fallback)

---

## 🚀 Instrucciones de Despliegue

### **Para Desarrollo Local:**
```bash
# Backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Frontend  
cd CampusBookings-front
ng serve --port 4200
```

### **Para Producción (Railway):**
Los cambios ya están compilados y listos. Solo necesitas:

1. **Push a Git:**
   ```bash
   git add .
   git commit -m "Fix: Corrección sistema de permisos dinámicos - URL y configuración de seguridad"
   git push origin master
   ```

2. **Railway se redesplegará automáticamente** con los nuevos cambios.

---

## 🔍 URLs Esperadas

### **Desarrollo:**
- ✅ `http://localhost:8080/api/user/my-permissions`

### **Producción:**
- ✅ `https://campusbookings-production.up.railway.app/api/user/my-permissions`

---

## 🧪 Cómo Probar

### **1. Verificar Backend (vía curl):**
```bash
# Obtener token
curl -X POST https://campusbookings-production.up.railway.app/api/auth/authenticate \
  -H "Content-Type: application/json" \
  -d '{"email":"tu-email@usco.edu.co","password":"tu-password"}'

# Probar endpoint de permisos
curl -X GET https://campusbookings-production.up.railway.app/api/user/my-permissions \
  -H "Authorization: Bearer TU_JWT_TOKEN"
```

### **2. Verificar Frontend:**
1. Abrir DevTools → Console
2. Iniciar sesión
3. Buscar log: `📋 Permisos del usuario cargados: {...}`
4. Verificar que NO hay errores 500

### **3. Probar UI Dinámica:**
- Dashboard sidebar se adapta según permisos
- Navigation bar muestra opciones correctas
- Botones aparecen/desaparecen según rol

---

## 🛡️ Roles y Permisos Esperados

### **USER:**
```json
{
  "userId": 123,
  "roleName": "USER", 
  "permissions": [
    {"resource": "SCENARIOS", "action": "READ"},
    {"resource": "RESERVATIONS", "action": "READ"},
    {"resource": "RESERVATIONS", "action": "CREATE"}
  ]
}
```

### **COORDINATOR:**
```json
{
  "userId": 123,
  "roleName": "COORDINATOR",
  "permissions": [
    {"resource": "SCENARIOS", "action": "MANAGE"},
    {"resource": "RESERVATIONS", "action": "MANAGE"},
    // ... más permisos
  ]
}
```

### **ADMIN:**
```json
{
  "userId": 123,
  "roleName": "ADMIN",
  "permissions": [
    // ... todos los permisos disponibles
  ]
}
```

---

## 🚨 Troubleshooting

### **Si sigue fallando:**

1. **Verificar logs del backend:**
   ```bash
   # En Railway Dashboard → Ver logs
   # Buscar errores relacionados con JWT o permisos
   ```

2. **Verificar token JWT:**
   ```javascript
   // En DevTools Console
   const token = localStorage.getItem('auth_token');
   console.log('Token claims:', JSON.parse(atob(token.split('.')[1])));
   // Debe incluir: userId, email, nombre, apellido, rol
   ```

3. **Verificar URL construida:**
   ```javascript
   // En DevTools Network tab
   // Buscar request a /my-permissions
   // URL debe ser: https://domain.com/api/user/my-permissions
   ```

4. **Fallback activado:**
   ```javascript
   // En Console, buscar:
   // "🔄 Usando permisos de fallback"
   // Esto indica que el backend no respondió correctamente
   ```

---

## ✅ Checklist de Verificación

### **Backend:**
- [ ] Compila sin errores
- [ ] Endpoint `/api/user/my-permissions` responde 200
- [ ] JWT contiene `userId` en claims
- [ ] SecurityConfig permite acceso a `/api/user/**`

### **Frontend:** 
- [ ] No hay errores en console
- [ ] URL se construye correctamente (sin `/api/api/`)
- [ ] Permisos se cargan después del login
- [ ] UI se adapta según rol

### **Integración:**
- [ ] Login exitoso
- [ ] No hay error 500 en Network tab
- [ ] Dashboard muestra opciones correctas según usuario

---

**🎯 Con estos cambios, el sistema de permisos debería funcionar correctamente en producción.**
