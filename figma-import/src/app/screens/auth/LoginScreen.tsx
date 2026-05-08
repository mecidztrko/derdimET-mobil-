import React, { useState } from 'react';
import { useNavigate } from 'react-router';
import { Eye, EyeOff, Mail, Lock, AlertCircle } from 'lucide-react';
import { useApp } from '../../context/AppContext';
import { toast } from 'sonner';
import { Role } from '../../data/mockData';

export function LoginScreen() {
  const navigate = useNavigate();
  const { login, switchRole } = useApp();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !password) {
      setError('E-posta ve şifre alanlarını doldurun.');
      return;
    }
    setError('');
    setLoading(true);
    const success = await login(email, password);
    setLoading(false);
    if (success) {
      toast.success('Giriş başarılı! Hoş geldiniz 👋');
      navigate('/app/search');
    } else {
      setError('E-posta veya şifre hatalı. Demo hesapları: alici@demo.com / satici@demo.com / kesimhane@demo.com (herhangi bir şifre)');
    }
  };

  const handleDemoLogin = (role: Role) => {
    switchRole(role);
    toast.success('Demo hesabına giriş yapıldı!');
    navigate('/app/search');
  };

  const demoAccounts = [
    { role: 'MEAT_BUYER' as Role, label: 'Et Alıcı', color: 'bg-purple-50 border-purple-200 text-purple-700', email: 'alici@demo.com' },
    { role: 'ANIMAL_SELLER' as Role, label: 'Hayvan Satıcı', color: 'bg-green-50 border-green-200 text-green-700', email: 'satici@demo.com' },
    { role: 'SLAUGHTERHOUSE' as Role, label: 'Kesimhane', color: 'bg-orange-50 border-orange-200 text-orange-700', email: 'kesimhane@demo.com' },
  ];

  return (
    <div className="flex flex-col h-full overflow-y-auto bg-white">
      {/* Header with gradient */}
      <div className="bg-gradient-to-b from-[#0F2C59] to-[#1B3A6B] px-6 pt-8 pb-10">
        <div className="flex items-center gap-3 mb-2">
          <div className="w-12 h-12 rounded-2xl bg-white/10 backdrop-blur flex items-center justify-center border border-white/20">
            <span className="text-white text-lg font-black tracking-tight">dE</span>
          </div>
          <div>
            <div className="flex items-baseline gap-0.5">
              <span className="text-2xl font-bold text-white">derdi</span>
              <span className="text-2xl font-bold text-[#FF7A3D]">mET</span>
            </div>
            <p className="text-white/50 text-xs mt-0.5">Hayvancılık & Et Ticareti</p>
          </div>
        </div>
        <h1 className="text-white text-xl font-semibold mt-6">Hoş Geldiniz</h1>
        <p className="text-white/60 text-sm mt-1">Hesabınıza giriş yapın</p>
      </div>

      {/* Form */}
      <div className="flex-1 px-5 pt-6 pb-8">
        <form onSubmit={handleLogin} className="space-y-4">
          {/* Email */}
          <div>
            <label className="text-sm font-medium text-gray-700 mb-1.5 block">E-posta</label>
            <div className="relative">
              <Mail size={18} className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" />
              <input
                type="email"
                value={email}
                onChange={e => { setEmail(e.target.value); setError(''); }}
                placeholder="ornek@sirket.com"
                className="w-full pl-11 pr-4 py-3.5 bg-gray-50 border border-gray-200 rounded-2xl text-gray-900 placeholder-gray-400 focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 transition-all text-sm"
              />
            </div>
          </div>

          {/* Password */}
          <div>
            <label className="text-sm font-medium text-gray-700 mb-1.5 block">Şifre</label>
            <div className="relative">
              <Lock size={18} className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" />
              <input
                type={showPassword ? 'text' : 'password'}
                value={password}
                onChange={e => { setPassword(e.target.value); setError(''); }}
                placeholder="••••••••"
                className="w-full pl-11 pr-12 py-3.5 bg-gray-50 border border-gray-200 rounded-2xl text-gray-900 placeholder-gray-400 focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 transition-all text-sm"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
          </div>

          {/* Remember me + Forgot */}
          <div className="flex items-center justify-between">
            <label className="flex items-center gap-2 cursor-pointer">
              <div
                onClick={() => setRememberMe(!rememberMe)}
                className={`w-5 h-5 rounded-md border-2 flex items-center justify-center transition-colors ${
                  rememberMe ? 'bg-[#1B3A6B] border-[#1B3A6B]' : 'border-gray-300'
                }`}
              >
                {rememberMe && (
                  <svg width="10" height="8" viewBox="0 0 10 8" fill="none">
                    <path d="M1 4L3.5 6.5L9 1" stroke="white" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                  </svg>
                )}
              </div>
              <span className="text-sm text-gray-600">Beni hatırla</span>
            </label>
            <button
              type="button"
              className="text-sm text-[#1B3A6B] font-medium hover:text-[#1D5BE6] transition-colors"
            >
              Şifremi unuttum
            </button>
          </div>

          {/* Error */}
          {error && (
            <div className="flex items-start gap-2.5 p-3.5 bg-red-50 border border-red-200 rounded-2xl">
              <AlertCircle size={16} className="text-red-500 flex-shrink-0 mt-0.5" />
              <p className="text-xs text-red-600 leading-relaxed">{error}</p>
            </div>
          )}

          {/* Login Button */}
          <button
            type="submit"
            disabled={loading}
            className="w-full py-4 bg-[#1B3A6B] text-white rounded-2xl font-semibold text-sm transition-all active:scale-95 disabled:opacity-70 flex items-center justify-center gap-2 shadow-lg shadow-[#1B3A6B]/30 mt-2"
          >
            {loading ? (
              <>
                <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                <span>Giriş yapılıyor...</span>
              </>
            ) : (
              'Giriş Yap'
            )}
          </button>
        </form>

        {/* Divider */}
        <div className="flex items-center gap-3 my-6">
          <div className="flex-1 h-px bg-gray-200" />
          <span className="text-xs text-gray-400 font-medium">veya demo giriş</span>
          <div className="flex-1 h-px bg-gray-200" />
        </div>

        {/* Demo Login Buttons */}
        <div className="space-y-2.5">
          <p className="text-xs text-gray-500 text-center mb-3">Rol seçerek demoyu keşfedin</p>
          {demoAccounts.map(acc => (
            <button
              key={acc.role}
              onClick={() => handleDemoLogin(acc.role)}
              className={`w-full py-3 px-4 border rounded-2xl flex items-center justify-between transition-all active:scale-95 ${acc.color}`}
            >
              <span className="text-sm font-semibold">{acc.label}</span>
              <span className="text-xs opacity-70">{acc.email}</span>
            </button>
          ))}
        </div>

        {/* Register Link */}
        <p className="text-center text-sm text-gray-500 mt-8">
          Hesabınız yok mu?{' '}
          <button
            onClick={() => navigate('/register')}
            className="text-[#1B3A6B] font-semibold hover:text-[#1D5BE6] transition-colors"
          >
            Kayıt Ol
          </button>
        </p>
      </div>
    </div>
  );
}
