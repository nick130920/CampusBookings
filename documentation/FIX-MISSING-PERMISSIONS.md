# Solución a Permisos Faltantes en Controladores

## Problema Identificado

Los controladores `FeedbackRestController` y `AlertaReservaController` estaban usando permisos que no existían en la base de datos:

- **FEEDBACK**: `CREATE`, `READ`, `UPDATE`, `DELETE`
- **ALERTS**: `CREATE`, `READ`, `UPDATE`, `DELETE`

Esto causaba que las verificaciones de permisos fallaran porque los permisos no estaban definidos en el sistema.

## Permisos Agregados

### Permisos de FEEDBACK
- `CREATE_FEEDBACK`: Permite crear feedback de escenarios
- `READ_FEEDBACK`: Permite leer feedback de escenarios
- `UPDATE_FEEDBACK`: Permite actualizar feedback existente
- `DELETE_FEEDBACK`: Permite eliminar feedback

### Permisos de ALERTS
- `CREATE_ALERTS`: Permite crear alertas de reservas
- `READ_ALERTS`: Permite leer alertas de reservas
- `UPDATE_ALERTS`: Permite actualizar alertas existentes
- `DELETE_ALERTS`: Permite eliminar alertas

## Asignación por Roles

### ADMIN
- ✅ **Todos los permisos** (FEEDBACK y ALERTS con todas las acciones)

### COORDINATOR
- ✅ **Todos los permisos de FEEDBACK** (CREATE, READ, UPDATE, DELETE)
- ✅ **Todos los permisos de ALERTS** (CREATE, READ, UPDATE, DELETE)

### USER
- ✅ **Todos los permisos de FEEDBACK** (CREATE, READ, UPDATE, DELETE)
- ❌ **Sin permisos de ALERTS** (solo para administración)

## Archivos Modificados

### 1. DataInitializer.java
**Ubicación:** `src/main/java/edu/usco/campusbookings/infrastructure/config/DataInitializer.java`

**Cambios:**
- Agregados 8 nuevos permisos en el método `createDefaultPermissions()`
- Actualizado `createDefaultRoles()` para asignar permisos a roles nuevos
- Actualizado `updateExistingRolesWithPermissions()` para roles existentes

### 2. Migración de Base de Datos
**Ubicación:** `src/main/resources/db/migration/V018__add_missing_feedback_alerts_permissions.sql`

**Contenido:**
- Script SQL para insertar los nuevos permisos
- Asignación automática de permisos a roles existentes
- Protección contra duplicados con `NOT EXISTS`

## Cómo Aplicar los Cambios

### Para Aplicaciones Nuevas
Los nuevos permisos se crearán automáticamente cuando se ejecute el `DataInitializer`.

### Para Aplicaciones Existentes
1. **Opción 1 - Automática:** Reiniciar la aplicación
   - El `DataInitializer` detectará automáticamente roles sin permisos y los asignará
   
2. **Opción 2 - Manual:** Ejecutar migración
   ```sql
   -- Se ejecutará automáticamente al iniciar la aplicación
   V018__add_missing_feedback_alerts_permissions.sql
   ```

## Verificación

### 1. Verificar Permisos en Base de Datos
```sql
-- Verificar que los permisos existen
SELECT * FROM permissions WHERE resource IN ('FEEDBACK', 'ALERTS');

-- Verificar asignación por roles
SELECT r.nombre as rol, p.name as permiso, p.resource, p.action
FROM roles r
JOIN rol_permissions rp ON r.id = rp.rol_id
JOIN permissions p ON rp.permission_id = p.id
WHERE p.resource IN ('FEEDBACK', 'ALERTS')
ORDER BY r.nombre, p.resource, p.action;
```

### 2. Verificar en Logs de Aplicación
Al usar las funcionalidades de feedback o alertas, ahora deberías ver logs como:

```
DEBUG - Usuario example@usco.edu.co - Rol USER tiene 8 permisos
DEBUG -   Permiso: CREATE_FEEDBACK (FEEDBACK:CREATE)
DEBUG -   Permiso: READ_FEEDBACK (FEEDBACK:READ)
...
```

### 3. Probar Funcionalidades
- **Crear Feedback**: `POST /api/v1/feedback`
- **Leer Alertas**: `GET /api/v1/alertas-reservas`
- **Actualizar Feedback**: `PUT /api/v1/feedback/{id}`

## Impacto

✅ **Positivo:**
- Los endpoints de feedback y alertas ahora funcionan correctamente
- Los usuarios pueden crear, leer, actualizar y eliminar feedback
- Los coordinadores pueden gestionar alertas de reservas
- No más errores de permisos faltantes

❌ **Sin Impacto Negativo:**
- Los cambios son retrocompatibles
- Los permisos existentes no se modifican
- Los usuarios existentes mantienen sus accesos actuales

## Notas Técnicas

1. **Orden de Creación**: Los permisos se crean antes que los roles para evitar problemas de dependencias
2. **Transaccionalidad**: Cada operación está en su propia transacción para evitar rollbacks masivos
3. **Idempotencia**: Los scripts pueden ejecutarse múltiples veces sin causar errores
4. **Logging**: Todos los cambios se registran en los logs para auditoría
