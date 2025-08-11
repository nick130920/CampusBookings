# Solución al Problema de LazyInitializationException en Permisos

## Problema
Se presentaba el error `LazyInitializationException` al intentar verificar permisos en el `PermissionAspect`:

```
failed to lazily initialize a collection of role: edu.usco.campusbookings.domain.model.Permission.roles: could not initialize proxy - no Session
```

## Causa Raíz
El problema ocurría porque:
1. El `toString()` del objeto `Permission` intentaba acceder a la colección `roles` que estaba lazy loaded
2. La sesión de Hibernate se cerraba antes de acceder a estas propiedades
3. El logging en `PermissionAspect` causaba la inicialización de estos objetos lazy

## Soluciones Aplicadas

### 1. Mejora de la Consulta del Repositorio
**Archivo:** `SpringDataUsuarioRepository.java`

Cambio de `@EntityGraph` a `JOIN FETCH` para garantizar la carga eager:

```java
@Query("SELECT DISTINCT u FROM Usuario u " +
       "LEFT JOIN FETCH u.rol r " +
       "LEFT JOIN FETCH r.permissions " +
       "WHERE u.email = :email")
Optional<Usuario> findByEmailWithPermissions(@Param("email") String email);
```

### 2. Configuración de Entidades
**Archivos:** `Permission.java` y `Rol.java`

Agregamos `@ToString.Exclude` para evitar ciclos infinitos y problemas de lazy loading:

```java
// En Permission.java
@ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
@EqualsAndHashCode.Exclude
@ToString.Exclude
private Set<Rol> roles;

// En Rol.java  
@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
@JoinTable(...)
@EqualsAndHashCode.Exclude
@ToString.Exclude
private Set<Permission> permissions;
```

### 3. Mejora del Logging en PermissionAspect
**Archivo:** `PermissionAspect.java`

Cambios aplicados:
- Eliminamos el `toString()` problemático
- Agregamos logging más detallado pero seguro
- Mejorado el manejo de excepciones

```java
// Antes (problemático)
log.debug("Usuario {} - Rol {} tiene permisos, {} ", 
    usuario.getEmail(), rol.getNombre(), rol.getPermissions().toString());

// Después (seguro)
log.debug("Usuario {} - Rol {} tiene {} permisos", 
    usuario.getEmail(), rol.getNombre(), 
    rol.getPermissions() != null ? rol.getPermissions().size() : 0);

// Log detallado para debugging
if (log.isDebugEnabled()) {
    rol.getPermissions().forEach(permission -> 
        log.debug("  - Permiso: {} ({}:{})", 
            permission.getName(), permission.getResource(), permission.getAction())
    );
}
```

## Beneficios de la Solución

1. **Eliminación de LazyInitializationException**: Ya no se presentará el error al verificar permisos
2. **Mejor rendimiento**: Los permisos se cargan de una sola vez con JOIN FETCH
3. **Logging más informativo**: Ahora puedes ver exactamente qué permisos tiene cada usuario
4. **Código más robusto**: Mejor manejo de excepciones y casos edge

## Cómo Verificar que Funciona

1. Reinicia la aplicación
2. Realiza una operación que requiera verificación de permisos
3. Revisa los logs para ver la información detallada de permisos sin errores

Ejemplo de log esperado:
```
DEBUG - Usuario namc1309@gmail.com - Rol ADMIN tiene 15 permisos
DEBUG - Permiso: Gestionar Usuarios (USUARIOS:MANAGE)
DEBUG - Permiso: Ver Reservas (RESERVAS:READ)
...
```

## Notas Técnicas

- El `@Transactional(readOnly = true)` en el aspecto garantiza que hay una sesión activa
- El `DISTINCT` en la consulta evita duplicados por el JOIN FETCH
- Las anotaciones `@ToString.Exclude` previenen ciclos infinitos en el logging
