# Colores Institucionales USCO - Campus Bookings

## 🎨 Paleta de Colores Oficiales

Este proyecto utiliza los colores oficiales de la **Universidad Surcolombiana (USCO)** según la [Guía de Imagen Institucional](https://www.usco.edu.co/imagen-institucional/).

### 🍷 Colores Principales

#### Vino Tinto (Color Principal)
- **Base**: `#8F141B` - Color principal de la identidad USCO
- **Claro**: `#B15B60` - Para elementos secundarios
- **Más Claro**: `#E3C5C6` - Para fondos suaves
- **Muy Claro**: `#DDB8BA` - Para bordes y divisores
- **Pálido**: `#F4E7E8` - Para fondos muy sutiles
- **Oscuro**: `#5C0E12` - Para texto y elementos de contraste

#### Gris (Color Secundario)
- **Base**: `#4D626C` - Color secundario principal
- **Claro**: `#839198` - Para textos secundarios
- **Más Claro**: `#A6B1B6` - Para bordes
- **Muy Claro**: `#DBE0E2` - Para fondos de sección
- **Pálido**: `#EDEFF0` - Para fondos generales
- **Oscuro**: `#1E262B` - Para texto principal

#### Ocre (Color de Acento)
- **Base**: `#DFD4A6` - Color de acento principal
- **Claro**: `#E5DDB8` - Para resaltados suaves
- **Más Claro**: `#EFEAD3` - Para fondos de advertencia
- **Muy Claro**: `#F5F2E4` - Para fondos sutiles
- **Pálido**: `#F9F6ED` - Para fondos muy sutiles
- **Oscuro**: `#C7B363` - Para bordes y contraste

### 🏛️ Colores por Facultades

- **Ciencias Jurídicas y Políticas**: `#7C0B69`
- **Ciencias Exactas y Naturales**: `#9DC107`
- **Ciencias Sociales y Humanas**: `#CE932C`
- **Economía y Administración**: `#003561`
- **Educación**: `#AD142E`
- **Ingeniería**: `#7D9C10`
- **Salud**: `#00A4B7`

## 🛠️ Uso en el Proyecto

### CSS Variables
Los colores están disponibles como variables CSS en `/static/css/usco-colors.css`:

```css
:root {
  --usco-vino-tinto: #8F141B;
  --usco-gris: #4D626C;
  --usco-ocre: #DFD4A6;
  /* ... más variables ... */
}

/* Uso */
.boton-principal {
  background-color: var(--usco-vino-tinto);
  color: white;
}
```

### Clases CSS Utilitarias
```css
/* Fondos */
.bg-primary { background-color: var(--usco-vino-tinto); }
.bg-secondary { background-color: var(--usco-gris); }
.bg-accent { background-color: var(--usco-ocre); }

/* Texto */
.text-primary { color: var(--usco-vino-tinto); }
.text-secondary { color: var(--usco-gris); }

/* Botones */
.btn-usco-primary { /* Estilo de botón principal USCO */ }
.btn-usco-secondary { /* Estilo de botón secundario USCO */ }
```

### JavaScript/TypeScript
Los colores están disponibles en JavaScript mediante `/static/js/usco-theme.js`:

```javascript
// Acceso a colores
const colorPrimario = USCOTheme.colors.primary;
const colorFacultad = USCOTheme.utils.getFacultadColor('ingenieria');

// Aplicar tema automáticamente
USCOTheme.utils.applyTheme();

// Crear gradientes
const gradiente = USCOTheme.utils.createGradient('to right');
```

### Java (Backend)
Los colores están disponibles como constantes en Java:

```java
import edu.usco.campusbookings.domain.model.constants.USCOColors;

// Uso de constantes
String colorPrimario = USCOColors.VINO_TINTO;
String colorFacultad = USCOColors.getColorFacultad("Ingeniería");
boolean esColorUSCO = USCOColors.isUSCOColor("#8F141B");
```

### Propiedades de Configuración
Los colores son configurables mediante `application.properties`:

```properties
# Colores principales
usco.branding.colors.primary=#8F141B
usco.branding.colors.secondary=#4D626C
usco.branding.colors.accent=#DFD4A6

# Colores por facultades
usco.branding.colors.facultad.ingenieria=#7D9C10
# etc...
```

## 🌐 API Endpoints

### Obtener Paleta Completa
```
GET /api/branding/colors
```

### Obtener Color de Facultad
```
GET /api/branding/colors/facultad/{nombre}
```

### Validar Color USCO
```
POST /api/branding/colors/validate
Content-Type: application/json

{
  "color": "#8F141B"
}
```

### Información de Branding
```
GET /api/branding/info
```

## 📱 Responsive y Accesibilidad

### Contrastes Verificados
Todos los colores cumplen con los estándares WCAG 2.1 AA para accesibilidad:

- **Texto sobre fondo claro**: Usar colores oscuros (`vino-tinto-dark`, `gris-dark`)
- **Texto sobre fondo oscuro**: Usar colores claros o blancos
- **Elementos interactivos**: Mantener ratio de contraste mínimo 4.5:1

### Media Queries
Los colores se adaptan automáticamente en diferentes dispositivos:

```css
@media (max-width: 768px) {
  .navbar-usco {
    background-color: var(--usco-vino-tinto-dark);
  }
}
```

## 🎯 Mejores Prácticas

### ✅ Recomendado
- Usar variables CSS en lugar de valores hexadecimales directos
- Preferir clases utilitarias para consistencia
- Usar colores de facultad para elementos específicos
- Aplicar colores funcionales para estados (success, warning, etc.)

### ❌ Evitar
- Hardcodear valores hexadecimales en CSS
- Usar colores que no estén en la paleta oficial
- Modificar los valores base sin actualizar la configuración
- Ignorar las pautas de contraste

## 🔄 Actualización de Colores

Si necesitas actualizar la paleta de colores:

1. **Modifica `application.properties`** para cambios de configuración
2. **Actualiza `USCOColors.java`** para nuevas constantes
3. **Regenera CSS** si cambias variables base
4. **Verifica contrastes** después de cualquier cambio

## 📚 Referencias

- [Guía de Imagen Institucional USCO](https://www.usco.edu.co/imagen-institucional/)
- [WCAG 2.1 Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)
- [CSS Custom Properties](https://developer.mozilla.org/en-US/docs/Web/CSS/--*)

---

**Última actualización**: Basado en la guía oficial USCO 2024
**Contacto**: Proyecto Campus Bookings - USCO