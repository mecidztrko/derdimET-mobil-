import React from 'react';
import { PackageSearch, MessageSquare, FileText, AlertCircle } from 'lucide-react';

interface EmptyStateProps {
  type?: 'search' | 'messages' | 'offers' | 'generic' | 'error';
  title?: string;
  description?: string;
  action?: React.ReactNode;
}

const TYPE_CONFIG = {
  search: {
    icon: PackageSearch,
    color: 'text-blue-300',
    bg: 'bg-blue-50',
    defaultTitle: 'Sonuç bulunamadı',
    defaultDesc: 'Filtrelerinizi değiştirmeyi deneyin veya farklı anahtar kelimeler kullanın.',
  },
  messages: {
    icon: MessageSquare,
    color: 'text-purple-300',
    bg: 'bg-purple-50',
    defaultTitle: 'Henüz mesajınız yok',
    defaultDesc: 'İlanlar üzerinden teklif verdiğinizde sohbet başlayacak.',
  },
  offers: {
    icon: FileText,
    color: 'text-amber-300',
    bg: 'bg-amber-50',
    defaultTitle: 'Henüz teklifiniz yok',
    defaultDesc: 'Arama ekranından ilanları inceleyip teklif verebilirsiniz.',
  },
  error: {
    icon: AlertCircle,
    color: 'text-red-300',
    bg: 'bg-red-50',
    defaultTitle: 'Bir hata oluştu',
    defaultDesc: 'Lütfen sayfayı yenileyip tekrar deneyin.',
  },
  generic: {
    icon: PackageSearch,
    color: 'text-gray-300',
    bg: 'bg-gray-50',
    defaultTitle: 'İçerik bulunamadı',
    defaultDesc: 'Gösterilecek içerik bulunmuyor.',
  },
};

export function EmptyState({ type = 'generic', title, description, action }: EmptyStateProps) {
  const config = TYPE_CONFIG[type];
  const Icon = config.icon;

  return (
    <div className="flex flex-col items-center justify-center px-8 py-16 text-center">
      <div className={`w-20 h-20 rounded-3xl ${config.bg} flex items-center justify-center mb-5`}>
        <Icon size={36} className={config.color} />
      </div>
      <h3 className="text-base font-semibold text-gray-800 mb-2">
        {title || config.defaultTitle}
      </h3>
      <p className="text-sm text-gray-500 leading-relaxed max-w-xs">
        {description || config.defaultDesc}
      </p>
      {action && <div className="mt-6">{action}</div>}
    </div>
  );
}

export function LoadingState({ message = 'Yükleniyor...' }: { message?: string }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 gap-4">
      <div className="w-10 h-10 border-3 border-[#1B3A6B]/20 border-t-[#1B3A6B] rounded-full animate-spin" />
      <p className="text-sm text-gray-400 font-medium">{message}</p>
    </div>
  );
}

export function SkeletonCard() {
  return (
    <div className="bg-white rounded-2xl p-4 animate-pulse">
      <div className="flex gap-3 mb-3">
        <div className="w-12 h-12 rounded-xl bg-gray-100" />
        <div className="flex-1 space-y-2">
          <div className="h-4 bg-gray-100 rounded w-3/4" />
          <div className="h-3 bg-gray-100 rounded w-1/2" />
        </div>
      </div>
      <div className="space-y-2">
        <div className="h-3 bg-gray-100 rounded" />
        <div className="h-3 bg-gray-100 rounded w-5/6" />
      </div>
      <div className="flex gap-2 mt-4">
        <div className="h-8 bg-gray-100 rounded-xl flex-1" />
        <div className="h-8 w-16 bg-gray-100 rounded-xl" />
      </div>
    </div>
  );
}
