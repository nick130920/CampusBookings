# 🚀 Guía de Optimización de Memoria - CampusBookings

## 📊 **Objetivo**: Reducir uso de RAM de 1GB a ~200-400MB

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

### 4. **🗑️ Dependencias Eliminadas**
- ❌ `spring-boot-starter-graphql` (~50MB)
- ❌ `spring-boot-starter-jdbc` (redundante)
- ❌ `spring-boot-devtools` (solo desarrollo)
- ❌ `springdoc-openapi` (comentado para prod)

### 5. **🧵 Thread Pool Reducido**
```properties
server.tomcat.threads.max=50           # Máximo 50 threads (vs 200)
server.tomcat.max-connections=200      # Máximo 200 conexiones
```

## 🚀 **Cómo Desplegar las Optimizaciones**

### **Opción A: Producción Normal** (~300-400MB)
```bash
# En Railway, usar profile prod (ya configurado)
SPRING_PROFILES_ACTIVE=prod
```

### **Opción B: Memoria Ultra Optimizada** (~200-300MB)
```bash
# Para aplicaciones de muy bajo tráfico
SPRING_PROFILES_ACTIVE=memory-optimized
```

## 📈 **Monitoreo de Memoria**

### **Ver uso actual:**
```bash
# En el contenedor
curl http://localhost:8081/actuator/health
curl http://localhost:8081/actuator/metrics/jvm.memory.used
```

### **Logs de memoria en Railway:**
```bash
railway logs
# Buscar líneas con "GC" o "memory"
```

## ⚠️ **Trade-offs de las Optimizaciones**

### **✅ Beneficios:**
- 🔻 **Memoria**: ~60-70% menos uso de RAM
- 💰 **Costos**: Menor costo en Railway
- ⚡ **Startup**: Más rápido con lazy loading

### **⚠️ Consideraciones:**
- 🐌 **Primera carga**: Lazy loading puede hacer más lenta la primera petición
- 🔒 **Conexiones**: Pool pequeño puede limitar concurrencia alta
- 🔍 **Debug**: Menos logging en producción

## 🔧 **Ajustes Adicionales por Tráfico**

### **Tráfico Bajo** (< 50 usuarios simultáneos):
```properties
# Usar memory-optimized profile
spring.datasource.hikari.maximum-pool-size=2
server.tomcat.threads.max=20
```

### **Tráfico Medio** (50-200 usuarios):
```properties
# Usar prod profile (configuración actual)
spring.datasource.hikari.maximum-pool-size=3
server.tomcat.threads.max=50
```

### **Tráfico Alto** (> 200 usuarios):
```properties
# Aumentar pools gradualmente
spring.datasource.hikari.maximum-pool-size=5
server.tomcat.threads.max=100
```

## 🎯 **Resultado Esperado**

| Configuración | Memoria Esperada | Uso Recomendado |
|---------------|------------------|-----------------|
| **Original** | ~1GB | ❌ Muy alto |
| **Prod Optimizado** | ~300-400MB | ✅ Recomendado |
| **Memory Optimized** | ~200-300MB | ⚡ Para bajo tráfico |

## 🚨 **En caso de problemas:**

1. **OutOfMemoryError**: Aumentar `-Xmx` a 768m
2. **Connections exhausted**: Aumentar `hikari.maximum-pool-size`
3. **Slow startup**: Deshabilitar lazy loading temporalmente

---

### 📝 **Monitoreo Continuo**
Revisar métricas semanalmente en Railway Dashboard:
- Memory usage
- CPU usage  
- Response times
- Error rates