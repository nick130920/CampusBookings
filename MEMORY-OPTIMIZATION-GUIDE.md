# 🚀 Guía de Optimización de Memoria - CampusBookings

## 📊 **Objetivo**: Reducir uso de RAM de 1GB a ~300-400MB

## ✅ **Optimizaciones Implementadas**

### 1. **🔧 JVM Optimizado (Dockerfile)**
```bash
# Configuraciones aplicadas:
-Xms128m          # Heap inicial: 128MB (vs 1GB por defecto)
-Xmx512m          # Heap máximo: 512MB (vs ilimitado)
-XX:+UseG1GC      # Garbage Collector eficiente
-XX:MaxRAMPercentage=75.0  # Usa máximo 75% de RAM disponible
```

### 2. **⚡ Spring Boot Lazy Loading**
```properties
spring.main.lazy-initialization=true    # Solo carga beans cuando se necesitan
spring.jpa.open-in-view=false          # Cierra sesiones JPA inmediatamente
```

### 3. **💾 Pool de Conexiones Optimizado**
```properties
spring.datasource.hikari.maximum-pool-size=3  # Máximo 3 conexiones (vs 10 por defecto)
spring.datasource.hikari.minimum-idle=1       # Mínimo 1 conexión activa
```

### 4. **🧵 Thread Pool Reducido**
```properties
server.tomcat.threads.max=50           # Máximo 50 threads (vs 200)
server.tomcat.max-connections=200      # Máximo 200 conexiones
```

### 5. **📝 Caché y Logging Optimizados**
```properties
# Caché reducido
spring.cache.caffeine.spec=maximumSize=50,expireAfterWrite=180s

# Logging mínimo
logging.file.max-size=1MB
logging.file.max-history=3
```

## 🚀 **Cómo Desplegar las Optimizaciones**

### **Para Producción (~300-400MB):**
```bash
# En Railway - Variable de entorno
SPRING_PROFILES_ACTIVE=prod
```

## 📈 **Monitoreo Implementado**

### **Nuevos Endpoints:**
- **`GET /api/system/memory`** - Información detallada de memoria
- **`GET /api/system/memory/summary`** - Resumen rápido del estado  
- **`GET /api/system/gc`** - Forzar garbage collection

### **Ejemplo de uso:**
```bash
curl https://tu-app.railway.app/api/system/memory/summary
# Respuesta: {"status":"HEALTHY","used":"250MB","max":"512MB","usage_percentage":"48.8%"}
```

## ⚡ **Impacto Esperado**

| Métrica | Antes | Después | Mejora |
|---------|-------|---------|---------|
| **Memoria RAM** | ~1GB | 300-400MB | **60-70% ↓** |
| **Startup Time** | ~45s | ~25s | **45% ↓** |
| **Costos Railway** | Alto | Medio | **40-50% ↓** |
| **Threads Pool** | 200 | 50 | **75% ↓** |
| **DB Connections** | 10 | 3 | **70% ↓** |

## 🔧 **Próximos Pasos**

1. **Desplegar a Railway** con `SPRING_PROFILES_ACTIVE=prod`
2. **Monitorear memoria** usando los nuevos endpoints
3. **Ajustar configuración** según el tráfico real

---

### 📝 **Monitoreo Continuo**
Revisar métricas semanalmente en Railway Dashboard:
- Memory usage
- CPU usage  
- Response times
- Error rates