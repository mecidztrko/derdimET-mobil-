import React, { useState } from 'react';
import { useNavigate } from 'react-router';
import { Eye, EyeOff, Mail, Lock, User, Building2, MapPin, Phone, ArrowLeft, CheckCircle2 } from 'lucide-react';
import { useApp } from '../../context/AppContext';
import { Role } from '../../data/mockData';
import { toast } from 'sonner';

type Step = 'role' | 'info' | 'success';

const ROLE_OPTIONS = [
  {
    role: 'MEAT_BUYER' as Role,
    label: 'Et Alıcı',
    description: 'Kesimhanelerden et satın alıyorum, fiyat karşılaştırması yapıyorum.',
    icon: '🛒',
    color: 'border-purple-200 bg-purple-50',
    activeColor: 'border-purple-500 bg-purple-50 ring-2 ring-purple-200',
    badgeColor: 'bg-purple-500',
  },
  {
    role: 'ANIMAL_SELLER' as Role,
    label: 'Hayvan Satıcı',
    description: 'Çiftliğimdeki hayvanları kesimhanelere satıyorum.',
    icon: '🐄',
    color: 'border-green-200 bg-green-50',
    activeColor: 'border-green-500 bg-green-50 ring-2 ring-green-200',
    badgeColor: 'bg-green-500',
  },
  {
    role: 'SLAUGHTERHOUSE' as Role,
    label: 'Kesimhane',
    description: 'Hayvan alıp et ürünleri satıyorum, iki yönlü ticaret yapıyorum.',
    icon: '🏭',
    color: 'border-orange-200 bg-orange-50',
    activeColor: 'border-orange-500 bg-orange-50 ring-2 ring-orange-200',
    badgeColor: 'bg-orange-500',
  },
];

export function RegisterScreen() {
  const navigate = useNavigate();
  const { setUser } = useApp();

  const [step, setStep] = useState<Step>('role');
  const [selectedRole, setSelectedRole] = useState<Role | null>(null);
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const [form, setForm] = useState({
    name: '',
    email: '',
    phone: '',
    password: '',
    companyName: '',
    city: '',
    address: '',
  });
  const [errors, setErrors] = useState<Partial<typeof form>>({});

  const updateForm = (key: keyof typeof form, value: string) => {
    setForm(prev => ({ ...prev, [key]: value }));
    if (errors[key]) setErrors(prev => ({ ...prev, [key]: '' }));
  };

  const validateInfo = () => {
    const newErrors: Partial<typeof form> = {};
    if (!form.name.trim()) newErrors.name = 'Ad soyad gerekli';
    if (!form.email.trim()) newErrors.email = 'E-posta gerekli';
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) newErrors.email = 'Geçerli bir e-posta girin';
    if (!form.password.trim()) newErrors.password = 'Şifre gerekli';
    else if (form.password.length < 8) newErrors.password = 'Şifre en az 8 karakter olmalı';
    if (selectedRole !== 'MEAT_BUYER' && !form.companyName.trim()) newErrors.companyName = 'Firma adı gerekli';
    if (!form.city.trim()) newErrors.city = 'Şehir gerekli';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleRegister = async () => {
    if (!validateInfo()) return;
    setLoading(true);
    await new Promise(r => setTimeout(r, 1200));
    const newUser = {
      id: 'new-user',
      name: form.name,
      email: form.email,
      role: selectedRole!,
      companyName: form.companyName || undefined,
      city: form.city,
      verified: false,
      rating: 0,
      reviewCount: 0,
      phone: form.phone,
      address: form.address,
      joinDate: 'Mayıs 2026',
    };
    setUser(newUser);
    setLoading(false);
    setStep('success');
  };

  const CITIES = ['Adana', 'Ankara', 'Antalya', 'Bursa', 'Diyarbakır', 'Erzurum', 'Gaziantep', 'İstanbul', 'İzmir', 'Kayseri', 'Konya', 'Samsun', 'Sivas', 'Trabzon', 'Urfa'];

  if (step === 'success') {
    return (
      <div className="flex flex-col h-full bg-white items-center justify-center px-6 text-center">
        <div className="w-24 h-24 rounded-full bg-emerald-50 flex items-center justify-center mb-6">
          <CheckCircle2 size={48} className="text-emerald-500" />
        </div>
        <h2 className="text-xl font-bold text-gray-900 mb-2">Hesabınız Oluşturuldu!</h2>
        <p className="text-sm text-gray-500 leading-relaxed mb-8 max-w-xs">
          Hesabınız başarıyla oluşturuldu. Kimlik doğrulama belgelerinizi göndererek hesabınızı doğrulatabilirsiniz.
        </p>
        <div className="w-full p-4 bg-blue-50 border border-blue-200 rounded-2xl mb-6 text-left">
          <p className="text-xs font-semibold text-blue-800 mb-1">📋 Sonraki Adımlar</p>
          <ul className="text-xs text-blue-700 space-y-1 list-disc list-inside">
            <li>Vergi levhası / ticaret sicili yükleyin</li>
            <li>Veteriner belgelerinizi hazırlayın</li>
            <li>24 saat içinde doğrulama tamamlanır</li>
          </ul>
        </div>
        <button
          onClick={() => navigate('/app/search')}
          className="w-full py-4 bg-[#1B3A6B] text-white rounded-2xl font-semibold text-sm shadow-lg shadow-[#1B3A6B]/30 active:scale-95 transition-transform"
        >
          Uygulamaya Geç
        </button>
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full overflow-y-auto bg-white">
      {/* Header */}
      <div className="bg-gradient-to-b from-[#0F2C59] to-[#1B3A6B] px-5 pt-6 pb-8">
        <div className="flex items-center gap-3 mb-6">
          <button
            onClick={() => step === 'role' ? navigate('/login') : setStep('role')}
            className="w-9 h-9 rounded-xl bg-white/10 flex items-center justify-center"
          >
            <ArrowLeft size={18} className="text-white" />
          </button>
          <div className="flex-1">
            <div className="flex items-baseline gap-0.5">
              <span className="text-lg font-bold text-white">derdi</span>
              <span className="text-lg font-bold text-[#FF7A3D]">mET</span>
            </div>
          </div>
        </div>
        {/* Progress */}
        <div className="flex items-center gap-2 mb-4">
          {['role', 'info'].map((s, i) => (
            <React.Fragment key={s}>
              <div className={`w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold transition-all ${
                step === s ? 'bg-white text-[#1B3A6B]' :
                (step === 'info' && s === 'role') || step === 'success' ? 'bg-white/30 text-white' :
                'bg-white/10 text-white/50'
              }`}>
                {i + 1}
              </div>
              {i < 1 && <div className={`flex-1 h-0.5 ${step === 'info' || step === 'success' ? 'bg-white/50' : 'bg-white/20'}`} />}
            </React.Fragment>
          ))}
        </div>
        <h1 className="text-white text-xl font-semibold">
          {step === 'role' ? 'Rol Seçin' : 'Hesap Bilgileri'}
        </h1>
        <p className="text-white/60 text-sm mt-1">
          {step === 'role' ? 'Platformda ne yapmak istiyorsunuz?' : 'Hesabınızı oluşturmak için bilgilerinizi girin'}
        </p>
      </div>

      <div className="flex-1 px-5 pt-6 pb-8 space-y-4">
        {step === 'role' && (
          <>
            {ROLE_OPTIONS.map(opt => (
              <button
                key={opt.role}
                onClick={() => setSelectedRole(opt.role)}
                className={`w-full p-4 rounded-2xl border-2 text-left transition-all active:scale-95 ${
                  selectedRole === opt.role ? opt.activeColor : opt.color
                }`}
              >
                <div className="flex items-start gap-3">
                  <span className="text-3xl">{opt.icon}</span>
                  <div className="flex-1">
                    <div className="flex items-center gap-2">
                      <span className="font-bold text-gray-900">{opt.label}</span>
                      {selectedRole === opt.role && (
                        <CheckCircle2 size={16} className="text-emerald-500" />
                      )}
                    </div>
                    <p className="text-xs text-gray-600 mt-1 leading-relaxed">{opt.description}</p>
                  </div>
                </div>
              </button>
            ))}
            <button
              onClick={() => selectedRole && setStep('info')}
              disabled={!selectedRole}
              className="w-full py-4 bg-[#1B3A6B] text-white rounded-2xl font-semibold text-sm transition-all active:scale-95 disabled:opacity-40 shadow-lg shadow-[#1B3A6B]/20 mt-4"
            >
              Devam Et
            </button>
          </>
        )}

        {step === 'info' && (
          <div className="space-y-4">
            {/* Name */}
            <div>
              <label className="text-sm font-medium text-gray-700 mb-1.5 block">Ad Soyad *</label>
              <div className="relative">
                <User size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400" />
                <input
                  type="text"
                  value={form.name}
                  onChange={e => updateForm('name', e.target.value)}
                  placeholder="Adınız ve soyadınız"
                  className={`w-full pl-10 pr-4 py-3 bg-gray-50 border rounded-2xl text-sm text-gray-900 placeholder-gray-400 focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 transition-all ${errors.name ? 'border-red-300 bg-red-50' : 'border-gray-200'}`}
                />
              </div>
              {errors.name && <p className="text-xs text-red-500 mt-1 ml-1">{errors.name}</p>}
            </div>

            {/* Email */}
            <div>
              <label className="text-sm font-medium text-gray-700 mb-1.5 block">E-posta *</label>
              <div className="relative">
                <Mail size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400" />
                <input
                  type="email"
                  value={form.email}
                  onChange={e => updateForm('email', e.target.value)}
                  placeholder="email@sirket.com"
                  className={`w-full pl-10 pr-4 py-3 bg-gray-50 border rounded-2xl text-sm text-gray-900 placeholder-gray-400 focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 transition-all ${errors.email ? 'border-red-300 bg-red-50' : 'border-gray-200'}`}
                />
              </div>
              {errors.email && <p className="text-xs text-red-500 mt-1 ml-1">{errors.email}</p>}
            </div>

            {/* Phone */}
            <div>
              <label className="text-sm font-medium text-gray-700 mb-1.5 block">Telefon</label>
              <div className="relative">
                <Phone size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400" />
                <input
                  type="tel"
                  value={form.phone}
                  onChange={e => updateForm('phone', e.target.value)}
                  placeholder="+90 5XX XXX XX XX"
                  className="w-full pl-10 pr-4 py-3 bg-gray-50 border border-gray-200 rounded-2xl text-sm text-gray-900 placeholder-gray-400 focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 transition-all"
                />
              </div>
            </div>

            {/* Password */}
            <div>
              <label className="text-sm font-medium text-gray-700 mb-1.5 block">Şifre *</label>
              <div className="relative">
                <Lock size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  value={form.password}
                  onChange={e => updateForm('password', e.target.value)}
                  placeholder="En az 8 karakter"
                  className={`w-full pl-10 pr-10 py-3 bg-gray-50 border rounded-2xl text-sm text-gray-900 placeholder-gray-400 focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 transition-all ${errors.password ? 'border-red-300 bg-red-50' : 'border-gray-200'}`}
                />
                <button type="button" onClick={() => setShowPassword(!showPassword)} className="absolute right-3.5 top-1/2 -translate-y-1/2 text-gray-400">
                  {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>
              {errors.password && <p className="text-xs text-red-500 mt-1 ml-1">{errors.password}</p>}
              {form.password && !errors.password && (
                <div className="flex gap-1 mt-2">
                  {[0,1,2,3].map(i => (
                    <div key={i} className={`flex-1 h-1 rounded-full ${form.password.length > i * 2 ? (form.password.length >= 8 ? 'bg-emerald-400' : 'bg-amber-400') : 'bg-gray-200'}`} />
                  ))}
                </div>
              )}
            </div>

            {/* Company (for Seller/SH) */}
            {selectedRole !== 'MEAT_BUYER' && (
              <div>
                <label className="text-sm font-medium text-gray-700 mb-1.5 block">Firma / İşletme Adı *</label>
                <div className="relative">
                  <Building2 size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400" />
                  <input
                    type="text"
                    value={form.companyName}
                    onChange={e => updateForm('companyName', e.target.value)}
                    placeholder={selectedRole === 'SLAUGHTERHOUSE' ? 'Kesimhane A.Ş.' : 'Çiftlik Adı'}
                    className={`w-full pl-10 pr-4 py-3 bg-gray-50 border rounded-2xl text-sm text-gray-900 placeholder-gray-400 focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 transition-all ${errors.companyName ? 'border-red-300 bg-red-50' : 'border-gray-200'}`}
                  />
                </div>
                {errors.companyName && <p className="text-xs text-red-500 mt-1 ml-1">{errors.companyName}</p>}
              </div>
            )}

            {/* City */}
            <div>
              <label className="text-sm font-medium text-gray-700 mb-1.5 block">Şehir *</label>
              <div className="relative">
                <MapPin size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400 z-10" />
                <select
                  value={form.city}
                  onChange={e => updateForm('city', e.target.value)}
                  className={`w-full pl-10 pr-4 py-3 bg-gray-50 border rounded-2xl text-sm text-gray-900 focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 transition-all appearance-none ${errors.city ? 'border-red-300 bg-red-50' : 'border-gray-200'}`}
                >
                  <option value="">Şehir seçin</option>
                  {CITIES.map(c => <option key={c} value={c}>{c}</option>)}
                </select>
              </div>
              {errors.city && <p className="text-xs text-red-500 mt-1 ml-1">{errors.city}</p>}
            </div>

            {/* Address */}
            <div>
              <label className="text-sm font-medium text-gray-700 mb-1.5 block">Adres</label>
              <textarea
                value={form.address}
                onChange={e => updateForm('address', e.target.value)}
                placeholder="Mahalle, sokak, ilçe..."
                rows={2}
                className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-2xl text-sm text-gray-900 placeholder-gray-400 focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 transition-all resize-none"
              />
            </div>

            {/* Verification hint */}
            <div className="p-3.5 bg-blue-50 border border-blue-200 rounded-2xl">
              <p className="text-xs font-semibold text-blue-800 mb-1">📋 Hesap Doğrulama</p>
              <p className="text-xs text-blue-600 leading-relaxed">
                Kayıt sonrası kimlik doğrulama süreci başlar. Vergi kaydı ve yetkili belgelerinizi hazırlayın.
              </p>
            </div>

            <button
              onClick={handleRegister}
              disabled={loading}
              className="w-full py-4 bg-[#1B3A6B] text-white rounded-2xl font-semibold text-sm transition-all active:scale-95 disabled:opacity-70 flex items-center justify-center gap-2 shadow-lg shadow-[#1B3A6B]/30"
            >
              {loading ? (
                <>
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  <span>Hesap oluşturuluyor...</span>
                </>
              ) : 'Hesap Oluştur'}
            </button>

            <p className="text-center text-xs text-gray-400 pb-4">
              Kayıt olarak{' '}
              <button className="text-[#1B3A6B] font-medium">Kullanım Koşullarını</button>{' '}
              ve{' '}
              <button className="text-[#1B3A6B] font-medium">Gizlilik Politikasını</button>{' '}
              kabul etmiş olursunuz.
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
