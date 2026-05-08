import React, { useState } from 'react';
import { useNavigate } from 'react-router';
import {
  Star, MapPin, Phone, Building2, MessageSquare,
  Heart, HeartOff, Settings, LogOut, ChevronRight,
  Shield, Bell, HelpCircle, FileText, CheckCircle2, Package
} from 'lucide-react';
import { useApp } from '../../context/AppContext';
import { VerifiedBadge, RoleBadge } from '../../components/shared/StatusBadge';
import {
  FAVORITE_SELLERS, FAVORITE_SLAUGHTERHOUSES, FavoriteUser,
  MEAT_LISTINGS, ANIMAL_LISTINGS
} from '../../data/mockData';
import { toast } from 'sonner';

function AvatarFallback({ name, size = 'md' }: { name: string; size?: 'sm' | 'md' | 'lg' }) {
  const initials = name.split(' ').slice(0, 2).map(n => n[0]).join('').toUpperCase();
  const sizes = { sm: 'w-10 h-10 text-sm', md: 'w-14 h-14 text-base', lg: 'w-20 h-20 text-xl' };
  return (
    <div className={`${sizes[size]} rounded-full bg-gradient-to-br from-[#1B3A6B] to-[#1D5BE6] flex items-center justify-center text-white font-bold flex-shrink-0`}>
      {initials}
    </div>
  );
}

function UserAvatar({ url, name, size = 'md' }: { url?: string; name: string; size?: 'sm' | 'md' | 'lg' }) {
  const sizes = { sm: 'w-10 h-10', md: 'w-14 h-14', lg: 'w-20 h-20' };
  if (!url) return <AvatarFallback name={name} size={size} />;
  return (
    <img
      src={url}
      alt={name}
      className={`${sizes[size]} rounded-full object-cover flex-shrink-0`}
      onError={e => { (e.target as HTMLImageElement).style.display = 'none'; }}
    />
  );
}

function FavoriteUserCard({ user, onChat, onRemove }: { user: FavoriteUser; onChat: () => void; onRemove: () => void }) {
  return (
    <div className="flex items-center gap-3 p-3 bg-white rounded-2xl border border-gray-100">
      <UserAvatar url={user.avatarUrl} name={user.name} size="sm" />
      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-1.5 flex-wrap">
          <span className="text-sm font-semibold text-gray-900 truncate">{user.name}</span>
          {user.verified && <CheckCircle2 size={13} className="text-blue-500 flex-shrink-0" />}
        </div>
        {user.companyName && (
          <p className="text-xs text-gray-500 truncate">{user.companyName}</p>
        )}
        <div className="flex items-center gap-2 mt-0.5">
          <div className="flex items-center gap-0.5">
            <Star size={11} className="text-amber-400 fill-amber-400" />
            <span className="text-xs text-gray-600">{user.rating}</span>
          </div>
          <span className="text-gray-300">·</span>
          <span className="text-xs text-gray-500">{user.city}</span>
        </div>
      </div>
      <div className="flex items-center gap-1.5">
        <button
          onClick={onChat}
          className="w-8 h-8 rounded-xl bg-[#1B3A6B]/5 flex items-center justify-center active:bg-[#1B3A6B]/10"
        >
          <MessageSquare size={15} className="text-[#1B3A6B]" />
        </button>
        <button
          onClick={onRemove}
          className="w-8 h-8 rounded-xl bg-red-50 flex items-center justify-center active:bg-red-100"
        >
          <HeartOff size={15} className="text-red-400" />
        </button>
      </div>
    </div>
  );
}

export function ProfileScreen() {
  const navigate = useNavigate();
  const { user, logout } = useApp();
  const [favSellers, setFavSellers] = useState(FAVORITE_SELLERS);
  const [favSlaughterhouses, setFavSlaughterhouses] = useState(FAVORITE_SLAUGHTERHOUSES);

  if (!user) return null;

  const handleLogout = () => {
    logout();
    navigate('/login');
    toast.success('Çıkış yapıldı.');
  };

  const SETTINGS_ITEMS = [
    { icon: <Bell size={18} />, label: 'Bildirimler', color: 'text-amber-600 bg-amber-50' },
    { icon: <Shield size={18} />, label: 'Güvenlik', color: 'text-blue-600 bg-blue-50' },
    { icon: <FileText size={18} />, label: 'Belgelerim', color: 'text-green-600 bg-green-50' },
    { icon: <HelpCircle size={18} />, label: 'Yardım & Destek', color: 'text-purple-600 bg-purple-50' },
  ];

  return (
    <div className="flex flex-col h-full overflow-y-auto bg-[#F5F7FA]">
      {/* Hero Card */}
      <div className="bg-gradient-to-b from-[#0F2C59] to-[#1B3A6B] px-5 pt-5 pb-8">
        <div className="flex items-start gap-4">
          <div className="relative">
            <UserAvatar url={user.avatarUrl} name={user.name} size="lg" />
            {user.verified && (
              <div className="absolute -bottom-1 -right-1 w-6 h-6 rounded-full bg-blue-500 border-2 border-white flex items-center justify-center">
                <CheckCircle2 size={13} className="text-white" strokeWidth={2.5} />
              </div>
            )}
          </div>
          <div className="flex-1 pt-1">
            <h2 className="text-white font-bold text-lg leading-tight">{user.name}</h2>
            {user.companyName && (
              <p className="text-white/70 text-sm mt-0.5">{user.companyName}</p>
            )}
            <div className="flex items-center flex-wrap gap-2 mt-2">
              <RoleBadge role={user.role} />
              <VerifiedBadge verified={user.verified} />
            </div>
          </div>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-3 gap-3 mt-6">
          {[
            { label: 'Puan', value: user.rating > 0 ? user.rating.toFixed(1) : '–' },
            { label: 'Değerlendirme', value: user.reviewCount > 0 ? user.reviewCount.toString() : '0' },
            { label: 'Üyelik', value: user.joinDate },
          ].map(stat => (
            <div key={stat.label} className="bg-white/10 rounded-2xl p-3 text-center">
              <p className="text-white font-bold text-base">{stat.value}</p>
              <p className="text-white/60 text-[11px] mt-0.5">{stat.label}</p>
            </div>
          ))}
        </div>
      </div>

      {/* Contact Info */}
      <div className="mx-4 -mt-4 bg-white rounded-2xl p-4 shadow-sm border border-gray-100 mb-3">
        <div className="space-y-2.5">
          {user.city && (
            <div className="flex items-center gap-2.5">
              <div className="w-7 h-7 rounded-xl bg-gray-50 flex items-center justify-center">
                <MapPin size={14} className="text-gray-500" />
              </div>
              <span className="text-sm text-gray-700">{user.city}</span>
            </div>
          )}
          {user.phone && (
            <div className="flex items-center gap-2.5">
              <div className="w-7 h-7 rounded-xl bg-gray-50 flex items-center justify-center">
                <Phone size={14} className="text-gray-500" />
              </div>
              <span className="text-sm text-gray-700">{user.phone}</span>
            </div>
          )}
          {user.companyName && (
            <div className="flex items-center gap-2.5">
              <div className="w-7 h-7 rounded-xl bg-gray-50 flex items-center justify-center">
                <Building2 size={14} className="text-gray-500" />
              </div>
              <span className="text-sm text-gray-700">{user.companyName}</span>
            </div>
          )}
        </div>
      </div>

      <div className="px-4 space-y-4 pb-8">
        {/* Role-specific sections */}
        {user.role === 'MEAT_BUYER' && (
          <>
            {/* Favorite Slaughterhouses */}
            <div>
              <div className="flex items-center justify-between mb-3">
                <h3 className="text-sm font-semibold text-gray-800">Favori Kesimhaneler</h3>
                <span className="text-xs text-gray-400">{favSlaughterhouses.length} kayıtlı</span>
              </div>
              {favSlaughterhouses.length === 0 ? (
                <div className="p-6 bg-white rounded-2xl text-center border border-gray-100">
                  <Heart size={24} className="text-gray-300 mx-auto mb-2" />
                  <p className="text-xs text-gray-400">Henüz favori kesimhane yok</p>
                </div>
              ) : (
                <div className="space-y-2">
                  {favSlaughterhouses.map(u => (
                    <FavoriteUserCard
                      key={u.id}
                      user={u}
                      onChat={() => { toast.success('Sohbet açılıyor...'); navigate('/app/offers'); }}
                      onRemove={() => { setFavSlaughterhouses(prev => prev.filter(x => x.id !== u.id)); toast.success('Favoriden çıkarıldı'); }}
                    />
                  ))}
                </div>
              )}
            </div>

            {/* Recent Purchases */}
            <div>
              <div className="flex items-center justify-between mb-3">
                <h3 className="text-sm font-semibold text-gray-800">Son Alışverişler</h3>
                <button className="text-xs text-[#1B3A6B] font-medium">Tümü</button>
              </div>
              <div className="space-y-2">
                {MEAT_LISTINGS.slice(0, 2).map(l => (
                  <div key={l.id} className="flex items-center gap-3 p-3 bg-white rounded-2xl border border-gray-100">
                    <div className="w-10 h-10 rounded-xl bg-emerald-50 flex items-center justify-center flex-shrink-0">
                      <Package size={18} className="text-emerald-500" />
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-gray-800 truncate">{l.title}</p>
                      <p className="text-xs text-gray-500">{l.slaughterhouseName}</p>
                    </div>
                    <div className="text-right">
                      <p className="text-sm font-semibold text-[#1B3A6B]">{l.pricePerKg}₺/kg</p>
                      <p className="text-xs text-gray-400">{l.date}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </>
        )}

        {user.role === 'ANIMAL_SELLER' && (
          <>
            {/* Favorite Slaughterhouses */}
            <div>
              <div className="flex items-center justify-between mb-3">
                <h3 className="text-sm font-semibold text-gray-800">Favori Kesimhaneler</h3>
                <span className="text-xs text-gray-400">{favSlaughterhouses.length} kayıtlı</span>
              </div>
              <div className="space-y-2">
                {favSlaughterhouses.map(u => (
                  <FavoriteUserCard
                    key={u.id}
                    user={u}
                    onChat={() => navigate('/app/offers')}
                    onRemove={() => { setFavSlaughterhouses(prev => prev.filter(x => x.id !== u.id)); toast.success('Favoriden çıkarıldı'); }}
                  />
                ))}
              </div>
            </div>

            {/* My Listings Summary */}
            <div>
              <div className="flex items-center justify-between mb-3">
                <h3 className="text-sm font-semibold text-gray-800">İlanlarım</h3>
                <button className="text-xs text-[#1B3A6B] font-medium" onClick={() => navigate('/app/create')}>İlan Ver</button>
              </div>
              <div className="space-y-2">
                {ANIMAL_LISTINGS.filter(l => l.sellerId === user.id).length === 0 ? (
                  <div className="p-6 bg-white rounded-2xl text-center border border-gray-100">
                    <Package size={24} className="text-gray-300 mx-auto mb-2" />
                    <p className="text-xs text-gray-400">Henüz aktif ilanınız yok</p>
                  </div>
                ) : (
                  ANIMAL_LISTINGS.filter(l => l.sellerId === user.id).map(l => (
                    <div key={l.id} className="flex items-center gap-3 p-3 bg-white rounded-2xl border border-gray-100">
                      <div className="w-10 h-10 rounded-xl bg-green-50 flex items-center justify-center text-xl flex-shrink-0">🐄</div>
                      <div className="flex-1">
                        <p className="text-sm font-medium text-gray-800">{l.breed} · {l.category}</p>
                        <p className="text-xs text-gray-500">{l.count} baş · {l.age}</p>
                      </div>
                      <div className="text-right">
                        <p className="text-sm font-semibold text-[#1B3A6B]">{l.pricePerHead.toLocaleString()}₺</p>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>

            {/* Recent Sales */}
            <div>
              <div className="flex items-center justify-between mb-3">
                <h3 className="text-sm font-semibold text-gray-800">Son Satışlar</h3>
                <button className="text-xs text-[#1B3A6B] font-medium">Tümü</button>
              </div>
              <div className="p-6 bg-white rounded-2xl text-center border border-gray-100">
                <Package size={24} className="text-gray-300 mx-auto mb-2" />
                <p className="text-xs text-gray-400">Henüz tamamlanan satış yok</p>
              </div>
            </div>
          </>
        )}

        {user.role === 'SLAUGHTERHOUSE' && (
          <>
            {/* Favorite Sellers */}
            <div>
              <div className="flex items-center justify-between mb-3">
                <h3 className="text-sm font-semibold text-gray-800">Favori Satıcılar</h3>
                <span className="text-xs text-gray-400">{favSellers.length} kayıtlı</span>
              </div>
              <div className="space-y-2">
                {favSellers.map(u => (
                  <FavoriteUserCard
                    key={u.id}
                    user={u}
                    onChat={() => navigate('/app/offers')}
                    onRemove={() => { setFavSellers(prev => prev.filter(x => x.id !== u.id)); toast.success('Favoriden çıkarıldı'); }}
                  />
                ))}
              </div>
            </div>

            {/* Favorite Buyers */}
            <div>
              <div className="flex items-center justify-between mb-3">
                <h3 className="text-sm font-semibold text-gray-800">Favori Alıcılar</h3>
                <span className="text-xs text-gray-400">{favSlaughterhouses.length} kayıtlı</span>
              </div>
              <div className="space-y-2">
                {favSlaughterhouses.map(u => (
                  <FavoriteUserCard
                    key={u.id}
                    user={u}
                    onChat={() => navigate('/app/offers')}
                    onRemove={() => { setFavSlaughterhouses(prev => prev.filter(x => x.id !== u.id)); toast.success('Favoriden çıkarıldı'); }}
                  />
                ))}
              </div>
            </div>

            {/* Recent Transactions */}
            <div className="grid grid-cols-2 gap-3">
              <div>
                <h3 className="text-sm font-semibold text-gray-800 mb-2">Son Alımlar</h3>
                <div className="p-4 bg-white rounded-2xl border border-gray-100">
                  <div className="text-2xl mb-1">🐄</div>
                  <p className="text-xs font-semibold text-gray-700">Simental x12</p>
                  <p className="text-xs text-gray-400">Hasan Demir'den</p>
                </div>
              </div>
              <div>
                <h3 className="text-sm font-semibold text-gray-800 mb-2">Son Satımlar</h3>
                <div className="p-4 bg-white rounded-2xl border border-gray-100">
                  <div className="text-2xl mb-1">🥩</div>
                  <p className="text-xs font-semibold text-gray-700">Dana Kıyma 200kg</p>
                  <p className="text-xs text-gray-400">Mehmet'e</p>
                </div>
              </div>
            </div>
          </>
        )}

        {/* Settings */}
        <div>
          <h3 className="text-sm font-semibold text-gray-800 mb-3">Ayarlar</h3>
          <div className="bg-white rounded-2xl border border-gray-100 overflow-hidden">
            {SETTINGS_ITEMS.map((item, idx) => (
              <button
                key={item.label}
                className={`w-full flex items-center gap-3 p-4 text-left active:bg-gray-50 transition-colors ${idx < SETTINGS_ITEMS.length - 1 ? 'border-b border-gray-50' : ''}`}
              >
                <div className={`w-8 h-8 rounded-xl ${item.color} flex items-center justify-center`}>
                  {item.icon}
                </div>
                <span className="flex-1 text-sm font-medium text-gray-700">{item.label}</span>
                <ChevronRight size={16} className="text-gray-400" />
              </button>
            ))}
          </div>
        </div>

        {/* Logout */}
        <button
          onClick={handleLogout}
          className="w-full flex items-center justify-center gap-2.5 p-4 bg-red-50 border border-red-100 rounded-2xl text-red-600 font-medium text-sm active:bg-red-100 transition-colors"
        >
          <LogOut size={18} />
          Çıkış Yap
        </button>

        <p className="text-center text-xs text-gray-300 pb-2">derdimET v1.0 · © 2026</p>
      </div>
    </div>
  );
}
