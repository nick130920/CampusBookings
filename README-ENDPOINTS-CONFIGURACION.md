# Endpoints de Configuración del Sistema - CampusBookings

## 📝 Descripción
Implementación completa de endpoints para gestionar la configuración dinámica de límites de fecha en el sistema de reservas CampusBookings.

## 🚀 Endpoints Implementados

### 1. Obtener Configuración de Reservas
```http
GET /api/configuracion/reservas
```

**Descripción**: Obtiene la configuración actual de límites para reservas.

**Response (200)**:
```json
{
  "id": 1,
  "minDaysAdvance": 2,
  "maxDaysAdvance": 90,
  "descripcion": "Configuración por defecto del sistema de reservas",
  "tipoConfiguracion": "RESERVAS",
  "updatedAt": "2024-08-04T10:30:00",
  "updatedBy": "admin@usco.edu.co"
}
```

### 2. Actualizar Configuración de Reservas
```http
PUT /api/configuracion/reservas
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Descripción**: Actualiza los límites de días para realizar reservas. **Requiere rol ADMIN**.

**Request Body**:
```json
{
  "diasMinimosAnticipacion": 1,
  "diasMaximosAnticipacion": 120,
  "descripcion": "Configuración actualizada para temporada alta"
}
```

**Response (200)**:
```json
{
  "id": 1,
  "minDaysAdvance": 1,
  "maxDaysAdvance": 120,
  "descripcion": "Configuración actualizada para temporada alta",
  "tipoConfiguracion": "RESERVAS",
  "updatedAt": "2024-08-04T11:45:00",
  "updatedBy": "admin@usco.edu.co"
}
```

### 3. Inicializar Configuración por Defecto
```http
POST /api/configuracion/reservas/inicializar
Authorization: Bearer {jwt_token}
```

**Descripción**: Crea la configuración por defecto si no existe. **Requiere rol ADMIN**.

**Response (201)**:
```json
{
  "id": 1,
  "minDaysAdvance": 2,
  "maxDaysAdvance": 90,
  "descripcion": "Configuración por defecto del sistema de reservas",
  "tipoConfiguracion": "RESERVAS",
  "updatedAt": "2024-08-04T09:00:00",
  "updatedBy": "SYSTEM"
}
```

## 📊 Base de Datos

### Tabla: configuracion_sistema
```sql
CREATE TABLE configuracion_sistema (
    id BIGSERIAL PRIMARY KEY,
    dias_minimos_anticipacion INTEGER NOT NULL CHECK (dias_minimos_anticipacion >= 0 AND dias_minimos_anticipacion <= 7),
    dias_maximos_anticipacion INTEGER NOT NULL CHECK (dias_maximos_anticipacion >= 7 AND dias_maximos_anticipacion <= 365),
    tipo_configuracion VARCHAR(50) NOT NULL DEFAULT 'RESERVAS',
    descripcion TEXT,
    created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'SYSTEM',
    modified_by VARCHAR(255) DEFAULT 'SYSTEM',
    
    CONSTRAINT check_dias_validos CHECK (dias_minimos_anticipacion < dias_maximos_anticipacion),
    CONSTRAINT uk_configuracion_tipo UNIQUE (tipo_configuracion)
);
```

## 🏗️ Arquitectura Implementada

### Capas de la Arquitectura Hexagonal

#### 1. Domain Layer
- **ConfiguracionSistema.java**: Entidad de dominio con validaciones
- **TipoConfiguracion.java**: Enum para tipos de configuración

#### 2. Application Layer
- **ConfiguracionSistemaUseCase.java**: Casos de uso (puerto de entrada)
- **ConfiguracionSistemaService.java**: Implementación de casos de uso
- **ConfiguracionSistemaRepositoryPort.java**: Puerto de salida para persistencia

#### 3. Infrastructure Layer
- **ConfiguracionSistemaController.java**: Adaptador de entrada (REST API)
- **ConfiguracionSistemaRepositoryAdapter.java**: Adaptador de salida
- **ConfiguracionSistemaRepository.java**: Repositorio JPA

#### 4. DTOs
- **ActualizarConfiguracionRequest.java**: DTO para peticiones de actualización
- **ConfiguracionResponse.java**: DTO para respuestas

## 🔧 Características Técnicas

### Validaciones
- **Rango mínimo**: 0-7 días
- **Rango máximo**: 7-365 días
- **Validación cruzada**: mínimo < máximo
- **Validaciones JPA**: `@Valid`, `@Min`, `@Max`, `@NotNull`

### Seguridad
- **Lectura**: Acceso público (cualquier usuario autenticado)
- **Escritura**: Solo administradores (`@PreAuthorize("hasRole('ADMIN')")`)
- **CORS**: Configurado para permitir requests del frontend

### Logging
- **Info**: Operaciones exitosas
- **Warn**: Validaciones fallidas
- **Error**: Errores de sistema

### Manejo de Errores
- **400**: Datos inválidos
- **401**: No autenticado
- **403**: Sin permisos de administrador
- **404**: Configuración no encontrada
- **500**: Errores internos

## 🔄 Flujo de Datos

### 1. Obtener Configuración
```
Frontend → Controller → Service → Repository → Database
Frontend ← Controller ← Service ← Repository ← Database
```

### 2. Actualizar Configuración
```
Frontend → JWT Validation → Controller → Service → Repository → Database
Frontend ← Response ← Controller ← Service ← Repository ← Database
```

### 3. Aplicación Automática
```
Frontend Service → RxJS BehaviorSubject → Reactive Components
    ↓
Formulario de Reservas + Calendario → Límites Actualizados
```

## 📱 Integración Frontend

### Service Angular
```typescript
// Cargar configuración
this.systemConfigService.loadConfig().subscribe(config => {
  console.log('Días mínimos:', config.minDaysAdvance);
  console.log('Días máximos:', config.maxDaysAdvance);
});

// Actualizar configuración
this.systemConfigService.updateConfig({
  minDaysAdvance: 1,
  maxDaysAdvance: 120
}).subscribe(updatedConfig => {
  console.log('Configuración actualizada:', updatedConfig);
});
```

### Reactive Updates
```typescript
// Los componentes se suscriben automáticamente
this.systemConfigService.config$.subscribe(config => {
  this.minDate = this.systemConfigService.getMinAllowedDate();
  this.maxDate = this.systemConfigService.getMaxAllowedDate();
});
```

## 🧪 Testing

### URLs de Prueba
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Get Config**: `GET http://localhost:8080/api/configuracion/reservas`
- **Update Config**: `PUT http://localhost:8080/api/configuracion/reservas`

### Datos de Prueba
```json
{
  "diasMinimosAnticipacion": 1,
  "diasMaximosAnticipacion": 180,
  "descripcion": "Configuración de prueba"
}
```

## 📋 Próximos Pasos

1. **Despliegue**: Aplicar migración V4 en base de datos
2. **Testing**: Ejecutar tests de integración
3. **Documentación**: Actualizar Swagger con ejemplos
4. **Monitoreo**: Agregar métricas de cambios de configuración
5. **Extensiones**: Agregar más tipos de configuración (horarios, notificaciones)

## 🔍 Logs de Ejemplo

```
2024-08-04 10:30:15 INFO  ConfiguracionSistemaService - Obteniendo configuración de reservas
2024-08-04 10:30:15 INFO  ConfiguracionSistemaService - Configuración obtenida: días mín=2, días máx=90
2024-08-04 11:45:22 INFO  ConfiguracionSistemaController - PUT /api/configuracion/reservas - Actualizando configuración
2024-08-04 11:45:23 INFO  ConfiguracionSistemaService - Configuración actualizada exitosamente: días mín=1, días máx=120
```

---

✅ **Estado**: Implementación completa y lista para despliegue
🎯 **Compatibilidad**: Frontend Angular 18 + Backend Spring Boot 3
🏗️ **Arquitectura**: Hexagonal con separación clara de responsabilidades