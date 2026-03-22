const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export function validateEmail(value: string): string | null {
  const v = value.trim();
  if (!v) return 'E-posta gerekli';
  if (v.length > 255) return 'E-posta çok uzun';
  if (!EMAIL_RE.test(v)) return 'Geçerli bir e-posta girin';
  return null;
}

export function validateLoginPassword(value: string): string | null {
  if (!value) return 'Şifre gerekli';
  return null;
}

export function validateRegisterPassword(value: string): string | null {
  if (!value) return 'Şifre gerekli';
  if (value.length < 8) return 'Şifre en az 8 karakter olmalı';
  if (value.length > 128) return 'Şifre en fazla 128 karakter olabilir';
  return null;
}

export function validateName(value: string): string | null {
  const v = value.trim();
  if (!v) return 'Ad soyad gerekli';
  if (v.length > 200) return 'Ad soyad en fazla 200 karakter olabilir';
  return null;
}

export function validatePhone(value: string): string | null {
  const v = value.trim();
  if (!v) return null;
  if (v.length > 50) return 'Telefon en fazla 50 karakter olabilir';
  return null;
}

export function validateCompanyName(value: string, accountType: 'INDIVIDUAL' | 'BUSINESS'): string | null {
  if (accountType !== 'BUSINESS') return null;
  const v = value.trim();
  if (!v) return 'Şirket adı gerekli';
  if (v.length > 300) return 'Şirket adı en fazla 300 karakter olabilir';
  return null;
}

export function validateTaxNumber(value: string, accountType: 'INDIVIDUAL' | 'BUSINESS'): string | null {
  if (accountType !== 'BUSINESS') return null;
  const v = value.trim();
  if (!v) return 'Vergi numarası gerekli';
  if (v.length > 50) return 'Vergi numarası en fazla 50 karakter olabilir';
  return null;
}

export function validateAddressLine(value: string): string | null {
  const v = value.trim();
  if (!v) return null;
  if (v.length > 500) return 'Adres en fazla 500 karakter olabilir';
  return null;
}

export function validateCity(value: string): string | null {
  const v = value.trim();
  if (!v) return null;
  if (v.length > 120) return 'Şehir en fazla 120 karakter olabilir';
  return null;
}

export function validatePasswordMatch(password: string, confirm: string): string | null {
  if (password !== confirm) return 'Şifreler eşleşmiyor';
  return null;
}
