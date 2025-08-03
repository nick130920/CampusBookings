/**
 * Configuración de tema USCO para Angular/TypeScript
 * Basado en: https://www.usco.edu.co/imagen-institucional/
 */

export interface USCOColorPalette {
  // Vino Tinto
  vinoTinto: string;
  vinoTintoLight: string;
  vinoTintoLighter: string;
  vinoTintoLightest: string;
  vinoTintoPale: string;
  vinoTintoDark: string;
  
  // Gris
  gris: string;
  grisLight: string;
  grisLighter: string;
  grisLightest: string;
  grisPale: string;
  grisDark: string;
  
  // Ocre
  ocre: string;
  ocreLight: string;
  ocreLighter: string;
  ocreLightest: string;
  ocrePale: string;
  ocreDark: string;
}

export interface USCOFacultadColors {
  juridicas: string;
  exactas: string;
  sociales: string;
  economia: string;
  educacion: string;
  ingenieria: string;
  salud: string;
}

export interface USCOColorAliases {
  primary: string;
  primaryLight: string;
  primaryDark: string;
  secondary: string;
  secondaryLight: string;
  secondaryDark: string;
  accent: string;
  accentLight: string;
  accentDark: string;
}

export interface USCOFunctionalColors {
  success: string;
  warning: string;
  danger: string;
  info: string;
  background: string;
  textPrimary: string;
  textSecondary: string;
  border: string;
}

export interface USCOTypography {
  fontFamily: string;
  fontFamilyCondensed: string;
  googleFonts: string;
  googleFontsCondensed: string;
}

export interface USCOThemeConfig {
  colors: USCOColorPalette;
  facultades: USCOFacultadColors;
  aliases: USCOColorAliases;
  funcionales: USCOFunctionalColors;
  typography: USCOTypography;
}

// Configuración principal del tema USCO
export const USCO_THEME: USCOThemeConfig = {
  colors: {
    // Vino Tinto - Color principal
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
    ocreDark: '#C7B363'
  },
  
  facultades: {
    juridicas: '#7C0B69',
    exactas: '#9DC107',
    sociales: '#CE932C',
    economia: '#003561',
    educacion: '#AD142E',
    ingenieria: '#7D9C10',
    salud: '#00A4B7'
  },
  
  aliases: {
    primary: '#8F141B',
    primaryLight: '#B15B60',
    primaryDark: '#5C0E12',
    secondary: '#4D626C',
    secondaryLight: '#839198',
    secondaryDark: '#1E262B',
    accent: '#DFD4A6',
    accentLight: '#E5DDB8',
    accentDark: '#C7B363'
  },
  
  funcionales: {
    success: '#9DC107',
    warning: '#DFD4A6',
    danger: '#8F141B',
    info: '#00A4B7',
    background: '#EDEFF0',
    textPrimary: '#1E262B',
    textSecondary: '#4D626C',
    border: '#A6B1B6'
  },
  
  typography: {
    fontFamily: "'Open Sans', Arial, Helvetica, sans-serif",
    fontFamilyCondensed: "'Open Sans Condensed', Arial, Helvetica, sans-serif",
    googleFonts: "https://fonts.googleapis.com/css?family=Open+Sans:400,400i,700,700i",
    googleFontsCondensed: "https://fonts.googleapis.com/css?family=Open+Sans+Condensed:300,300i,700"
  }
};

// Tokens de diseño para Angular Material
export const USCO_MATERIAL_THEME = {
  primary: {
    50: USCO_THEME.colors.vinoTintoPale,
    100: USCO_THEME.colors.vinoTintoLightest,
    200: USCO_THEME.colors.vinoTintoLighter,
    300: USCO_THEME.colors.vinoTintoLight,
    400: USCO_THEME.colors.vinoTinto,
    500: USCO_THEME.colors.vinoTinto,
    600: USCO_THEME.colors.vinoTinto,
    700: USCO_THEME.colors.vinoTintoDark,
    800: USCO_THEME.colors.vinoTintoDark,
    900: USCO_THEME.colors.vinoTintoDark,
    A100: USCO_THEME.colors.vinoTintoLighter,
    A200: USCO_THEME.colors.vinoTintoLight,
    A400: USCO_THEME.colors.vinoTinto,
    A700: USCO_THEME.colors.vinoTintoDark
  },
  
  accent: {
    50: USCO_THEME.colors.ocrePale,
    100: USCO_THEME.colors.ocreLightest,
    200: USCO_THEME.colors.ocreLighter,
    300: USCO_THEME.colors.ocreLight,
    400: USCO_THEME.colors.ocre,
    500: USCO_THEME.colors.ocre,
    600: USCO_THEME.colors.ocre,
    700: USCO_THEME.colors.ocreDark,
    800: USCO_THEME.colors.ocreDark,
    900: USCO_THEME.colors.ocreDark,
    A100: USCO_THEME.colors.ocreLighter,
    A200: USCO_THEME.colors.ocreLight,
    A400: USCO_THEME.colors.ocre,
    A700: USCO_THEME.colors.ocreDark
  }
};

// Utilidades para trabajar con colores
export class USCOColorUtils {
  
  /**
   * Convierte color hex a RGB
   */
  static hexToRgb(hex: string): { r: number; g: number; b: number } | null {
    const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result ? {
      r: parseInt(result[1], 16),
      g: parseInt(result[2], 16),
      b: parseInt(result[3], 16)
    } : null;
  }
  
  /**
   * Convierte RGB a hex
   */
  static rgbToHex(r: number, g: number, b: number): string {
    return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);
  }
  
  /**
   * Aplica transparencia a un color
   */
  static addAlpha(color: string, alpha: number): string {
    const rgb = this.hexToRgb(color);
    if (!rgb) return color;
    return `rgba(${rgb.r}, ${rgb.g}, ${rgb.b}, ${alpha})`;
  }
  
  /**
   * Obtiene color de facultad
   */
  static getFacultadColor(facultad: string): string {
    const facultadLower = facultad.toLowerCase();
    
    if (facultadLower.includes('juridica') || facultadLower.includes('politica')) {
      return USCO_THEME.facultades.juridicas;
    } else if (facultadLower.includes('exactas') || facultadLower.includes('naturales')) {
      return USCO_THEME.facultades.exactas;
    } else if (facultadLower.includes('sociales') || facultadLower.includes('humanas')) {
      return USCO_THEME.facultades.sociales;
    } else if (facultadLower.includes('economia') || facultadLower.includes('administracion')) {
      return USCO_THEME.facultades.economia;
    } else if (facultadLower.includes('educacion')) {
      return USCO_THEME.facultades.educacion;
    } else if (facultadLower.includes('ingenieria')) {
      return USCO_THEME.facultades.ingenieria;
    } else if (facultadLower.includes('salud')) {
      return USCO_THEME.facultades.salud;
    }
    
    return USCO_THEME.aliases.primary;
  }
  
  /**
   * Genera paleta de colores para gráficos
   */
  static getChartColors(): string[] {
    return [
      USCO_THEME.aliases.primary,
      USCO_THEME.aliases.accent,
      USCO_THEME.aliases.secondary,
      USCO_THEME.facultades.exactas,
      USCO_THEME.facultades.salud,
      USCO_THEME.facultades.ingenieria,
      USCO_THEME.facultades.economia,
      USCO_THEME.facultades.sociales
    ];
  }
  
  /**
   * Crea gradiente CSS
   */
  static createGradient(direction: string = 'to right', color1?: string, color2?: string): string {
    color1 = color1 || USCO_THEME.aliases.primary;
    color2 = color2 || USCO_THEME.aliases.primaryLight;
    return `linear-gradient(${direction}, ${color1}, ${color2})`;
  }
}

// Constantes para uso común
export const USCO_CONSTANTS = {
  INSTITUTION_NAME: 'Universidad Surcolombiana',
  INSTITUTION_SHORT: 'USCO',
  WEBSITE: 'https://www.usco.edu.co/',
  BRAND_GUIDE: 'https://www.usco.edu.co/imagen-institucional/',
  API_BASE_URL: '/api',
  BRANDING_ENDPOINT: '/api/branding'
};

// Configuración de breakpoints responsive
export const USCO_BREAKPOINTS = {
  xs: '(max-width: 575.98px)',
  sm: '(min-width: 576px) and (max-width: 767.98px)',
  md: '(min-width: 768px) and (max-width: 991.98px)',
  lg: '(min-width: 992px) and (max-width: 1199.98px)',
  xl: '(min-width: 1200px)'
};

// Configuración de animaciones
export const USCO_ANIMATIONS = {
  transition: {
    fast: '0.15s ease',
    normal: '0.3s ease',
    slow: '0.5s ease'
  },
  easing: {
    easeInOut: 'cubic-bezier(0.4, 0, 0.2, 1)',
    easeOut: 'cubic-bezier(0.0, 0, 0.2, 1)',
    easeIn: 'cubic-bezier(0.4, 0, 1, 1)'
  }
};