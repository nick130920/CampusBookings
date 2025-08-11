# Actualización de Roles en Tiempo Real

## Descripción

Implementación de notificaciones WebSocket para actualizar automáticamente el rol de un usuario sin necesidad de cerrar sesión y volver a iniciar sesión.

## Problema Resuelto

Anteriormente, cuando un administrador cambiaba el rol de un usuario, este cambio solo se reflejaba después de que el usuario cerrara sesión y volviera a iniciar sesión, ya que la información del rol se almacenaba en el localStorage del navegador.

## Solución Implementada

### Backend

#### 1. Nuevo Tipo de Notificación
- **Archivo**: `ReservaNotificationDto.java`
- **Cambio**: Agregado `USER_ROLE_UPDATED` al enum `NotificationType`

#### 2. Servicio de Notificaciones
- **Archivo**: `NotificationService.java`
- **Método nuevo**: `notificarCambioRolUsuario()`
- **Funcionalidad**: Envía notificación WebSocket cuando se actualiza un rol

#### 3. Gestión de Usuarios
- **Archivo**: `UserManagementService.java`
- **Cambio**: El método `updateUserRole()` ahora envía notificación WebSocket automáticamente
- **Dependencia**: Inyección de `NotificationService`

### Frontend

#### 1. Servicio WebSocket
- **Archivo**: `websocket.service.ts`
- **Cambios**:
  - Agregado `USER_ROLE_UPDATED` al tipo `ReservaNotification`
  - Nuevo icono: `pi pi-user-edit`
  - Nueva severidad: `warn`

#### 2. Servicio de Notificaciones
- **Archivo**: `notification.service.ts`
- **Cambios**:
  - Detección automática de notificaciones `USER_ROLE_UPDATED`
  - Método `handleRoleUpdate()` para procesar cambios de rol
  - Toast específico con mensaje personalizado
  - Sonido distintivo para cambios de rol

#### 3. Servicio de Autenticación
- **Archivo**: `auth.service.ts`
- **Métodos nuevos**:
  - `updateUserRole()`: Actualiza el rol en tiempo real
  - `updateUserData()`: Actualiza datos completos del usuario
- **Funcionalidades**:
  - Actualización automática en localStorage
  - Actualización del BehaviorSubject `currentUser`
  - Emisión de evento personalizado `user-role-updated`

## Flujo de Funcionamiento

1. **Administrador cambia rol**: Un administrador actualiza el rol de un usuario desde la interfaz de gestión de usuarios
2. **Backend procesa**: `UserManagementService.updateUserRole()` actualiza la base de datos
3. **Notificación WebSocket**: Se envía automáticamente una notificación `USER_ROLE_UPDATED` al usuario afectado
4. **Frontend recibe**: El `NotificationService` detecta la notificación
5. **Actualización automática**: Se actualiza el rol en localStorage y en memoria
6. **Toast informativo**: Se muestra una notificación visual al usuario
7. **Evento global**: Se emite un evento para que otros componentes puedan reaccionar

## Características

### Notificación Visual
- **Título**: "👤 Rol Actualizado"
- **Mensaje**: "Tu rol ha sido cambiado de [ROL_ANTERIOR] a [ROL_NUEVO]. Los permisos se han actualizado automáticamente."
- **Duración**: 10 segundos (sticky)
- **Severidad**: Warning (amarillo)

### Sonido
- **Frecuencia**: 700Hz (tono medio-alto distintivo)
- **Duración**: 0.3 segundos

### Logs
- **Backend**: Log de confirmación en `UserManagementService` y `NotificationService`
- **Frontend**: Log detallado en consola con información del cambio

## Eventos Personalizados

Se emite un evento `user-role-updated` que otros componentes pueden escuchar:

```typescript
window.addEventListener('user-role-updated', (event: CustomEvent) => {
  const { oldRole, newRole, user } = event.detail;
  // Reaccionar al cambio de rol
});
```

## Compatibilidad

### Usuario Desconectado
Si el usuario no está conectado al WebSocket, el cambio se aplicará la próxima vez que inicie sesión, ya que el rol se obtiene del token JWT.

### Múltiples Sesiones
Si el usuario tiene múltiples ventanas/pestañas abiertas, todas recibirán la notificación y se actualizarán automáticamente.

### Fallback
En caso de error en la notificación WebSocket, la operación de cambio de rol se completa normalmente en la base de datos, garantizando la consistencia de datos.

## Casos de Uso

1. **Promoción de usuario**: De USER a ADMIN
2. **Degradación de permisos**: De ADMIN a USER  
3. **Asignación inicial**: De SIN_ROL a USER/ADMIN
4. **Cambios de roles personalizados**: Entre diferentes roles del sistema

## Beneficios

- ✅ **Experiencia de usuario mejorada**: No requiere cerrar sesión
- ✅ **Actualizaciones inmediatas**: Cambios reflejados al instante
- ✅ **Notificación clara**: Usuario informado del cambio
- ✅ **Consistencia**: Sincronización automática entre frontend y backend
- ✅ **Escalabilidad**: Funciona con múltiples usuarios simultáneamente

## Consideraciones de Seguridad

- El cambio de rol se valida completamente en el backend
- La notificación WebSocket es informativa, no ejecuta cambios de permisos
- Los permisos reales se basan en la base de datos y el token JWT
- La notificación solo se envía al usuario específico afectado

## Testing

Para probar la funcionalidad:

1. Conectar dos usuarios: uno como admin y otro como usuario regular
2. Desde la cuenta admin, cambiar el rol del usuario regular
3. Verificar que el usuario regular recibe la notificación inmediatamente
4. Confirmar que los permisos se actualizan sin recargar la página
