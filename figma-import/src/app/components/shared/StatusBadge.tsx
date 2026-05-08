import React from 'react';
import { OfferStatus } from '../../data/mockData';

interface StatusBadgeProps {
  status: OfferStatus | 'verified' | 'pending_verification';
  size?: 'sm' | 'md';
}

const STATUS_CONFIG = {
  pending: {
    label: 'Beklemede',
    className: 'bg-amber-50 text-amber-700 border border-amber-200',
    dot: 'bg-amber-400',
  },
  accepted: {
    label: 'Kabul Edildi',
    className: 'bg-emerald-50 text-emerald-700 border border-emerald-200',
    dot: 'bg-emerald-400',
  },
  rejected: {
    label: 'Reddedildi',
    className: 'bg-red-50 text-red-600 border border-red-200',
    dot: 'bg-red-400',
  },
  verified: {
    label: 'Doğrulandı',
    className: 'bg-blue-50 text-blue-700 border border-blue-200',
    dot: 'bg-blue-400',
  },
  pending_verification: {
    label: 'Doğrulama Bekliyor',
    className: 'bg-gray-50 text-gray-600 border border-gray-200',
    dot: 'bg-gray-400',
  },
};

export function StatusBadge({ status, size = 'sm' }: StatusBadgeProps) {
  const config = STATUS_CONFIG[status];
  return (
    <span
      className={`inline-flex items-center gap-1 rounded-full font-medium ${config.className} ${
        size === 'sm' ? 'px-2 py-0.5 text-[11px]' : 'px-3 py-1 text-xs'
      }`}
    >
      <span className={`w-1.5 h-1.5 rounded-full flex-shrink-0 ${config.dot}`} />
      {config.label}
    </span>
  );
}

interface VerifiedBadgeProps {
  verified: boolean;
  size?: 'sm' | 'md';
}

export function VerifiedBadge({ verified, size = 'sm' }: VerifiedBadgeProps) {
  if (!verified) return null;
  return (
    <span
      className={`inline-flex items-center gap-1 rounded-full bg-blue-50 text-blue-700 border border-blue-200 font-medium ${
        size === 'sm' ? 'px-2 py-0.5 text-[11px]' : 'px-3 py-1 text-xs'
      }`}
    >
      <svg width="10" height="10" viewBox="0 0 10 10" fill="none">
        <path d="M5 0L6.18 3.42L9.76 3.42L6.89 5.58L7.87 9L5 6.84L2.13 9L3.11 5.58L0.24 3.42L3.82 3.42L5 0Z" fill="#1D4ED8"/>
      </svg>
      Doğrulandı
    </span>
  );
}

interface RoleBadgeProps {
  role: 'MEAT_BUYER' | 'ANIMAL_SELLER' | 'SLAUGHTERHOUSE';
}

const ROLE_CONFIG = {
  MEAT_BUYER: { label: 'Et Alıcı', className: 'bg-purple-50 text-purple-700 border border-purple-200' },
  ANIMAL_SELLER: { label: 'Hayvan Satıcı', className: 'bg-green-50 text-green-700 border border-green-200' },
  SLAUGHTERHOUSE: { label: 'Kesimhane', className: 'bg-orange-50 text-orange-700 border border-orange-200' },
};

export function RoleBadge({ role }: RoleBadgeProps) {
  const config = ROLE_CONFIG[role];
  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-[11px] font-medium border ${config.className}`}>
      {config.label}
    </span>
  );
}
