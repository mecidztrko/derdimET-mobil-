import { z } from 'zod';

export const loginSchema = z.object({
  email: z.string().min(1, 'E-posta gerekli').email('Geçerli bir e-posta girin'),
  password: z.string().min(6, 'Şifre en az 6 karakter olmalı'),
});

export type LoginFormValues = z.infer<typeof loginSchema>;

export const registerSchema = z
  .object({
    name: z.string().min(1, 'Ad soyad gerekli').max(200, 'En fazla 200 karakter'),
    email: z.string().min(1, 'E-posta gerekli').email('Geçerli bir e-posta girin'),
    password: z.string().min(8, 'Şifre en az 8 karakter olmalı').max(128, 'En fazla 128 karakter'),
    confirmPassword: z.string().min(1, 'Şifre tekrarını girin'),
    role: z.enum(['MEAT_BUYER', 'ANIMAL_SELLER']),
    accountType: z.enum(['INDIVIDUAL', 'BUSINESS']),
    companyName: z.string().optional(),
    taxNumber: z.string().optional(),
    address: z.string().optional(),
  })
  .superRefine((data, ctx) => {
    if (data.password !== data.confirmPassword) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: 'Şifreler eşleşmiyor',
        path: ['confirmPassword'],
      });
    }
    if (data.accountType === 'BUSINESS') {
      if (!data.companyName?.trim()) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          message: 'Şirket adı gerekli',
          path: ['companyName'],
        });
      }
      if (!data.taxNumber?.trim()) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          message: 'Vergi numarası gerekli',
          path: ['taxNumber'],
        });
      }
      if (!data.address?.trim()) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          message: 'Adres gerekli',
          path: ['address'],
        });
      }
    }
  });

export type RegisterFormValues = z.infer<typeof registerSchema>;
