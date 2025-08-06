# Sistema de Auditoría Automática - CampusBookings

## 📋 Descripción General

El sistema de auditoría automática de CampusBookings captura automáticamente información sobre quién y cuándo se realizan operaciones en la base de datos, eliminando la necesidad de establecer manualmente los campos de auditoría.

## 🔧 Componentes Implementados

### 1. AuditorAwareImpl
**Ubicación:** `src/main/java/edu/usco/campusbookings/infrastructure/config/AuditorAwareImpl.java`

Captura automáticamente el usuario actual del contexto de Spring Security:
- ✅ Usuarios autenticados via JWT → Captura el email del usuario
- ✅ Usuarios anónimos → Usa "SYSTEM" como fallback
- ✅ Operaciones sin contexto → Usa "SYSTEM" como fallback
- ✅ Manejo robusto de errores con logging

### 2. Clase Auditable Mejorada
**Ubicación:** `src/main/java/edu/usco/campusbookings/domain/model/Auditable.java`

Campos de auditoría automáticos:
```java
@CreatedDate
private LocalDateTime createdDate;

@LastModifiedDate  
private LocalDateTime modifiedDate;

@CreatedBy
private String createdBy;

@LastModifiedBy
private String modifiedBy;
```

### 3. Configuración Habilitada
**Ubicación:** `src/main/java/edu/usco/campusbookings/CampusBookingsApplication.java`

```java
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
```

## 🚀 Funcionalidad

### Automático al Crear Entidades
```java
// Al crear un nuevo escenario (con usuario autenticado)
Escenario escenario = new Escenario();
escenario.setNombre("Aula 101");
escenarioRepository.save(escenario);

// Automáticamente se establece:
// - createdDate: 2024-01-15T10:30:00
// - createdBy: "admin@usco.edu.co"
// - modifiedDate: 2024-01-15T10:30:00  
// - modifiedBy: "admin@usco.edu.co"
```

### Automático al Actualizar Entidades
```java
// Al actualizar un escenario existente
escenario.setCapacidad(40);
escenarioRepository.save(escenario);

// Automáticamente se actualiza:
// - modifiedDate: 2024-01-15T15:45:00
// - modifiedBy: "user@usco.edu.co"
// (createdDate y createdBy permanecen sin cambios)
```

## 🗃️ Migración de Datos Existentes

**Archivo:** `src/main/resources/db/migration/V6__update_audit_fields_for_existing_records.sql`

- Actualiza registros existentes con "SYSTEM" a NULL
- Los valores NULL serán manejados automáticamente en futuras actualizaciones
- Preserva la integridad de datos históricos

## 🧪 Pruebas

**Ubicación:** `src/test/java/edu/usco/campusbookings/infrastructure/config/AuditorAwareImplTest.java`

Cobertura de pruebas:
- ✅ Sin autenticación → "SYSTEM"
- ✅ Usuario anónimo → "SYSTEM"  
- ✅ Usuario con UserDetails → Email del usuario
- ✅ Principal como String → Valor del principal
- ✅ Tipo de principal desconocido → "SYSTEM"

## 📊 Beneficios

1. **🔄 Automático**: No necesidad de código manual para auditoría
2. **🔒 Seguro**: Captura el usuario real del contexto de seguridad
3. **🛡️ Robusto**: Manejo de errores con fallback a "SYSTEM"
4. **📈 Escalable**: Funciona para todas las entidades que extienden `Auditable`
5. **🔍 Trazable**: Logging detallado para debugging
6. **⚡ Performance**: Mínimo overhead en operaciones de base de datos

## 🎯 Entidades Auditadas

Todas las entidades que extienden `Auditable`:
- ✅ Usuario
- ✅ Escenario  
- ✅ Reserva
- ✅ ConfiguracionSistema
- ✅ PasswordResetToken

## 📝 Logs de Auditoría

El sistema genera logs de debug para monitoreo:
```
DEBUG - Auditor capturado desde UserDetails: admin@usco.edu.co
DEBUG - No hay autenticación válida, usando SYSTEM como auditor
WARN  - Tipo de principal desconocido: com.example.CustomPrincipal, usando SYSTEM como auditor
```

## 🔧 Mantenimiento

- **Sin mantenimiento requerido**: El sistema es completamente automático
- **Monitoreo**: Revisar logs de aplicación para casos edge
- **Actualizaciones**: El sistema se adapta automáticamente a cambios en Spring Security

---

**Versión:** 1.0  
**Estado:** ✅ Productivo