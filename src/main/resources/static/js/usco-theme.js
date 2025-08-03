/**
 * Configuración de tema USCO para JavaScript
 * Basado en: https://www.usco.edu.co/imagen-institucional/
 */

window.USCOTheme = {
    // Colores principales
    colors: {
        // Vino Tinto
        vinoTinto: '#8F141B',
        vinoTintoLight: '#B15B60',
        vinoTintoLighter: '#E3C5C6',
        vinoTintoLightest: '#DDB8BA',
        vinoTintoPale: '#F4E7E8',
        vinoTintoDark: '#5C0E12',
        
        // Gris
        gris: '#4D626C',
        grisLight: '#839198',
        grisLighter: '#A6B1B6',
        grisLightest: '#DBE0E2',
        grisPale: '#EDEFF0',
        grisDark: '#1E262B',
        
        // Ocre
        ocre: '#DFD4A6',
        ocreLight: '#E5DDB8',
        ocreLighter: '#EFEAD3',
        ocreLightest: '#F5F2E4',
        ocrePale: '#F9F6ED',
        ocreDark: '#C7B363',
        
        // Facultades
        facultades: {
            juridicas: '#7C0B69',
            exactas: '#9DC107',
            sociales: '#CE932C',
            economia: '#003561',
            educacion: '#AD142E',
            ingenieria: '#7D9C10',
            salud: '#00A4B7'
        },
        
        // Aliases
        primary: '#8F141B',
        primaryLight: '#B15B60',
        primaryDark: '#5C0E12',
        secondary: '#4D626C',
        secondaryLight: '#839198',
        secondaryDark: '#1E262B',
        accent: '#DFD4A6',
        accentLight: '#E5DDB8',
        accentDark: '#C7B363',
        
        // Funcionales
        success: '#9DC107',
        warning: '#DFD4A6',
        danger: '#8F141B',
        info: '#00A4B7',
        background: '#EDEFF0',
        textPrimary: '#1E262B',
        textSecondary: '#4D626C',
        border: '#A6B1B6'
    },
    
    // Configuración de tipografía
    typography: {
        fontFamily: "'Open Sans', Arial, Helvetica, sans-serif",
        fontFamilyCondensed: "'Open Sans Condensed', Arial, Helvetica, sans-serif",
        googleFonts: "https://fonts.googleapis.com/css?family=Open+Sans:400,400i,700,700i",
        googleFontsCondensed: "https://fonts.googleapis.com/css?family=Open+Sans+Condensed:300,300i,700"
    },
    
    // Métodos utilitarios
    utils: {
        /**
         * Convierte color hex a RGB
         */
        hexToRgb: function(hex) {
            const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
            return result ? {
                r: parseInt(result[1], 16),
                g: parseInt(result[2], 16),
                b: parseInt(result[3], 16)
            } : null;
        },
        
        /**
         * Convierte RGB a hex
         */
        rgbToHex: function(r, g, b) {
            return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);
        },
        
        /**
         * Aplica transparencia a un color
         */
        addAlpha: function(color, alpha) {
            const rgb = this.hexToRgb(color);
            if (!rgb) return color;
            return `rgba(${rgb.r}, ${rgb.g}, ${rgb.b}, ${alpha})`;
        },
        
        /**
         * Obtiene color de facultad
         */
        getFacultadColor: function(facultad) {
            const facultadLower = facultad.toLowerCase();
            const colors = USCOTheme.colors.facultades;
            
            if (facultadLower.includes('juridica') || facultadLower.includes('politica')) {
                return colors.juridicas;
            } else if (facultadLower.includes('exactas') || facultadLower.includes('naturales')) {
                return colors.exactas;
            } else if (facultadLower.includes('sociales') || facultadLower.includes('humanas')) {
                return colors.sociales;
            } else if (facultadLower.includes('economia') || facultadLower.includes('administracion')) {
                return colors.economia;
            } else if (facultadLower.includes('educacion')) {
                return colors.educacion;
            } else if (facultadLower.includes('ingenieria')) {
                return colors.ingenieria;
            } else if (facultadLower.includes('salud')) {
                return colors.salud;
            }
            
            return USCOTheme.colors.primary;
        },
        
        /**
         * Aplica tema USCO dinámicamente
         */
        applyTheme: function() {
            const root = document.documentElement;
            const colors = USCOTheme.colors;
            
            // Aplicar variables CSS
            root.style.setProperty('--usco-primary', colors.primary);
            root.style.setProperty('--usco-primary-light', colors.primaryLight);
            root.style.setProperty('--usco-primary-dark', colors.primaryDark);
            root.style.setProperty('--usco-secondary', colors.secondary);
            root.style.setProperty('--usco-secondary-light', colors.secondaryLight);
            root.style.setProperty('--usco-secondary-dark', colors.secondaryDark);
            root.style.setProperty('--usco-accent', colors.accent);
            root.style.setProperty('--usco-accent-light', colors.accentLight);
            root.style.setProperty('--usco-accent-dark', colors.accentDark);
            
            // Aplicar fuentes
            if (!document.querySelector('link[href*="Open+Sans"]')) {
                const link = document.createElement('link');
                link.href = USCOTheme.typography.googleFonts;
                link.rel = 'stylesheet';
                document.head.appendChild(link);
            }
        },
        
        /**
         * Crea gradiente USCO
         */
        createGradient: function(direction = 'to right', color1 = null, color2 = null) {
            color1 = color1 || USCOTheme.colors.primary;
            color2 = color2 || USCOTheme.colors.primaryLight;
            return `linear-gradient(${direction}, ${color1}, ${color2})`;
        },
        
        /**
         * Genera paleta de colores para gráficos
         */
        getChartColors: function() {
            return [
                USCOTheme.colors.primary,
                USCOTheme.colors.accent,
                USCOTheme.colors.secondary,
                USCOTheme.colors.facultades.exactas,
                USCOTheme.colors.facultades.salud,
                USCOTheme.colors.facultades.ingenieria,
                USCOTheme.colors.facultades.economia,
                USCOTheme.colors.facultades.sociales
            ];
        }
    },
    
    // Configuración de componentes
    components: {
        button: {
            borderRadius: '4px',
            transition: 'all 0.3s ease',
            fontWeight: '600'
        },
        card: {
            borderRadius: '8px',
            boxShadow: '0 2px 4px rgba(143, 20, 27, 0.1)',
            transition: 'box-shadow 0.3s ease'
        },
        navbar: {
            backgroundColor: USCOTheme?.colors?.primary || '#8F141B',
            minHeight: '60px'
        }
    },
    
    // Información institucional
    institution: {
        name: 'Universidad Surcolombiana',
        shortName: 'USCO',
        website: 'https://www.usco.edu.co/',
        brandGuide: 'https://www.usco.edu.co/imagen-institucional/',
        favicon: 'favicon.ico'
    }
};

// Auto-aplicar tema cuando se carga el script
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
        USCOTheme.utils.applyTheme();
    });
} else {
    USCOTheme.utils.applyTheme();
}

// Exportar para uso en módulos
if (typeof module !== 'undefined' && module.exports) {
    module.exports = USCOTheme;
}