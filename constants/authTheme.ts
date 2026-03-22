import { Platform, type ViewStyle } from 'react-native';

/** Kimlik doğrulama / SaaS tarzı arayüz sabitleri */
export const AUTH = {
  accent: '#2563EB',
  accentMid: '#3B82F6',
  accentDark: '#1D4ED8',
  text: '#0F172A',
  textMuted: '#64748B',
  error: '#DC2626',
  white: '#FFFFFF',
  sky50: '#F0F9FF',
  sky100: '#E0F2FE',
  border: 'rgba(37, 99, 235, 0.14)',
  cardBg: 'rgba(255, 255, 255, 0.94)',
  radiusSm: 12,
  radiusMd: 16,
  radiusLg: 20,
} as const;

export const authShadows = {
  card: {
    shadowColor: '#0F172A',
    shadowOffset: { width: 0, height: 10 },
    shadowOpacity: 0.08,
    shadowRadius: 28,
    elevation: Platform.OS === 'android' ? 4 : 8,
  } satisfies ViewStyle,
  button: {
    shadowColor: '#2563EB',
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.28,
    shadowRadius: 12,
    elevation: 6,
  } satisfies ViewStyle,
  logo: {
    shadowColor: '#0F172A',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.12,
    shadowRadius: 16,
    elevation: 5,
  } satisfies ViewStyle,
  inputFocus: {
    shadowColor: '#2563EB',
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0.2,
    shadowRadius: 10,
    elevation: 3,
  } satisfies ViewStyle,
};
