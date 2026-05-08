import React, { useState } from 'react';
import { useNavigate } from 'react-router';
import {
  Camera, X, CheckCircle2, ChevronDown,
  DollarSign
} from 'lucide-react';
import { useApp } from '../../context/AppContext';
import { toast } from 'sonner';
import { motion, AnimatePresence } from 'motion/react';

type FormStep = 'type' | 'details' | 'pricing' | 'success';

// ──────────── ANIMAL SELLER FORM ──────────────────────────────────────────────

function AnimalListingForm() {
  const navigate = useNavigate();
  const [step, setStep] = useState<FormStep>('type');
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({
    category: '' as 'küçükbaş' | 'büyükbaş' | '',
    breed: '',
    age: '',
    count: '',
    weight: '',
    pricePerHead: '',
    description: '',
    imageAdded: false,
    hasVetReport: false,
    hasInsurance: false,
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  const update = (key: string, val: any) => {
    setForm(p => ({ ...p, [key]: val }));
    if (errors[key]) setErrors(p => ({ ...p, [key]: '' }));
  };

  const BREEDS_KUCUK = ['Merinos', 'Akkaraman', 'İvesi', 'Kıvırcık', 'Morkaraman'];
  const BREEDS_BUYUK = ['Simental', 'Holstein', 'Angus', 'Limuzin', 'Yerli Kara'];
  const AGES = ['0–6 ay', '6–12 ay', '12–18 ay', '18–24 ay', '24–36 ay', '36+ ay'];

  const validateDetails = () => {
    const e: Record<string, string> = {};
    if (!form.category) e.category = 'Kategori seçin';
    if (!form.breed) e.breed = 'Irk/tür girin';
    if (!form.age) e.age = 'Yaş seçin';
    if (!form.count || Number(form.count) <= 0) e.count = 'Geçerli adet girin';
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const validatePricing = () => {
    const e: Record<string, string> = {};
    if (!form.pricePerHead || Number(form.pricePerHead) <= 0) e.pricePerHead = 'Geçerli fiyat girin';
    if (!form.description.trim()) e.description = 'Açıklama girin';
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleNext = () => {
    if (step === 'type' && !form.category) { toast.error('Lütfen kategori seçin'); return; }
    if (step === 'details' && !validateDetails()) return;
    if (step === 'pricing') {
      if (!validatePricing()) return;
      handleSubmit();
      return;
    }
    const next: Record<FormStep, FormStep> = { type: 'details', details: 'pricing', pricing: 'success', success: 'success' };
    setStep(next[step]);
  };

  const handleSubmit = async () => {
    setLoading(true);
    await new Promise(r => setTimeout(r, 1200));
    setLoading(false);
    setStep('success');
  };

  if (step === 'success') {
    return (
      <div className="flex flex-col items-center justify-center flex-1 px-6 text-center">
        <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} transition={{ type: 'spring', delay: 0.1 }}>
          <div className="w-24 h-24 rounded-full bg-emerald-50 flex items-center justify-center mb-5">
            <CheckCircle2 size={48} className="text-emerald-500" />
          </div>
        </motion.div>
        <h2 className="text-xl font-bold text-gray-900 mb-2">İlan Yayınlandı!</h2>
        <p className="text-sm text-gray-500 mb-3">
          <strong>{form.breed}</strong> ilanınız aktif. Kesimhanelerden teklifler geldiğinde bildirim alacaksınız.
        </p>
        <div className="w-full p-4 bg-gray-50 rounded-2xl text-left mb-6 space-y-1">
          <div className="flex justify-between">
            <span className="text-xs text-gray-500">Kategori</span>
            <span className="text-xs font-semibold capitalize">{form.category}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-xs text-gray-500">Irk</span>
            <span className="text-xs font-semibold">{form.breed}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-xs text-gray-500">Adet</span>
            <span className="text-xs font-semibold">{form.count} baş</span>
          </div>
          <div className="flex justify-between">
            <span className="text-xs text-gray-500">Fiyat</span>
            <span className="text-xs font-semibold text-[#1B3A6B]">{Number(form.pricePerHead).toLocaleString()}₺/baş</span>
          </div>
        </div>
        <button onClick={() => navigate('/app/search')} className="w-full py-4 bg-[#1B3A6B] text-white rounded-2xl font-semibold text-sm mb-3 shadow-lg shadow-[#1B3A6B]/20">
          İlanlarımı Gör
        </button>
        <button onClick={() => { setStep('type'); setForm({ category: '', breed: '', age: '', count: '', weight: '', pricePerHead: '', description: '', imageAdded: false, hasVetReport: false, hasInsurance: false }); }}
          className="w-full py-3 bg-gray-100 text-gray-700 rounded-2xl font-semibold text-sm">
          Yeni İlan Ver
        </button>
      </div>
    );
  }

  const stepLabels = ['Kategori', 'Detaylar', 'Fiyat & Açıklama'];
  const stepIdx = step === 'type' ? 0 : step === 'details' ? 1 : 2;

  return (
    <div className="flex flex-col h-full">
      {/* Progress */}
      <div className="px-5 py-3 border-b border-gray-100 bg-white">
        <div className="flex items-center gap-1 mb-2">
          {stepLabels.map((label, i) => (
            <React.Fragment key={label}>
              <div className={`flex items-center gap-1.5 ${i <= stepIdx ? 'opacity-100' : 'opacity-40'}`}>
                <div className={`w-5 h-5 rounded-full flex items-center justify-center text-[10px] font-bold ${i < stepIdx ? 'bg-emerald-400 text-white' : i === stepIdx ? 'bg-[#1B3A6B] text-white' : 'bg-gray-200 text-gray-500'}`}>
                  {i < stepIdx ? '✓' : i + 1}
                </div>
                <span className={`text-[11px] font-medium ${i === stepIdx ? 'text-[#1B3A6B]' : 'text-gray-500'}`}>{label}</span>
              </div>
              {i < 2 && <div className={`flex-1 h-0.5 rounded-full ${i < stepIdx ? 'bg-emerald-300' : 'bg-gray-200'}`} />}
            </React.Fragment>
          ))}
        </div>
      </div>

      <div className="flex-1 overflow-y-auto px-5 py-5 space-y-5">
        {/* STEP 1: Category */}
        {step === 'type' && (
          <AnimatePresence mode="wait">
            <motion.div key="type" initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -20 }}>
              <p className="text-base font-bold text-gray-900 mb-4">Hayvan Kategorisi</p>
              <div className="grid grid-cols-2 gap-3">
                {[
                  { val: 'küçükbaş', icon: '🐑', label: 'Küçükbaş', sub: 'Koyun, Keçi, Kuzu' },
                  { val: 'büyükbaş', icon: '🐄', label: 'Büyükbaş', sub: 'Dana, İnek, Boğa' },
                ].map(opt => (
                  <button
                    key={opt.val}
                    onClick={() => update('category', opt.val)}
                    className={`p-5 rounded-2xl border-2 text-left transition-all active:scale-95 ${form.category === opt.val ? 'border-[#1B3A6B] bg-blue-50' : 'border-gray-200 bg-white'}`}
                  >
                    <div className="text-4xl mb-2">{opt.icon}</div>
                    <p className="font-bold text-gray-900 text-sm">{opt.label}</p>
                    <p className="text-xs text-gray-500 mt-0.5">{opt.sub}</p>
                    {form.category === opt.val && (
                      <div className="mt-2">
                        <CheckCircle2 size={16} className="text-[#1B3A6B]" />
                      </div>
                    )}
                  </button>
                ))}
              </div>
            </motion.div>
          </AnimatePresence>
        )}

        {/* STEP 2: Details */}
        {step === 'details' && (
          <AnimatePresence mode="wait">
            <motion.div key="details" initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -20 }} className="space-y-4">
              {/* Breed */}
              <div>
                <label className="text-sm font-semibold text-gray-700 mb-2 block">Irk / Tür *</label>
                <div className="relative">
                  <select
                    value={form.breed}
                    onChange={e => update('breed', e.target.value)}
                    className={`w-full px-4 py-3 bg-gray-50 border rounded-2xl text-sm appearance-none focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 ${errors.breed ? 'border-red-300' : 'border-gray-200'}`}
                  >
                    <option value="">Seçin</option>
                    {(form.category === 'küçükbaş' ? BREEDS_KUCUK : BREEDS_BUYUK).map(b => <option key={b} value={b}>{b}</option>)}
                  </select>
                  <ChevronDown size={16} className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 pointer-events-none" />
                </div>
                {errors.breed && <p className="text-xs text-red-500 mt-1 ml-1">{errors.breed}</p>}
              </div>

              {/* Age */}
              <div>
                <label className="text-sm font-semibold text-gray-700 mb-2 block">Yaş *</label>
                <div className="flex flex-wrap gap-2">
                  {AGES.map(age => (
                    <button
                      key={age}
                      onClick={() => update('age', age)}
                      className={`px-3 py-2 rounded-xl text-sm font-medium border transition-all ${form.age === age ? 'bg-[#1B3A6B] text-white border-[#1B3A6B]' : 'bg-gray-50 text-gray-700 border-gray-200'}`}
                    >
                      {age}
                    </button>
                  ))}
                </div>
                {errors.age && <p className="text-xs text-red-500 mt-1 ml-1">{errors.age}</p>}
              </div>

              {/* Count + Weight */}
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-sm font-semibold text-gray-700 mb-2 block">Adet *</label>
                  <div className="relative">
                    <input
                      type="number"
                      value={form.count}
                      onChange={e => update('count', e.target.value)}
                      placeholder="0"
                      min="1"
                      className={`w-full px-4 py-3 bg-gray-50 border rounded-2xl text-sm focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 ${errors.count ? 'border-red-300' : 'border-gray-200'}`}
                    />
                  </div>
                  {errors.count && <p className="text-xs text-red-500 mt-1 ml-1">{errors.count}</p>}
                </div>
                <div>
                  <label className="text-sm font-semibold text-gray-700 mb-2 block">Tahmini Ağırlık</label>
                  <div className="relative">
                    <input
                      type="text"
                      value={form.weight}
                      onChange={e => update('weight', e.target.value)}
                      placeholder="örn. 350–400 kg"
                      className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-2xl text-sm focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10"
                    />
                  </div>
                </div>
              </div>

              {/* Toggles */}
              <div className="space-y-3">
                {[
                  { key: 'hasVetReport', label: 'Veteriner Raporu Var', sub: 'Belgeli hayvanlar daha hızlı satılır' },
                  { key: 'hasInsurance', label: 'Sigortalı', sub: 'Hayvan sigortası mevcut' },
                ].map(toggle => (
                  <button
                    key={toggle.key}
                    onClick={() => update(toggle.key, !form[toggle.key as keyof typeof form])}
                    className="w-full flex items-center gap-3 p-3.5 bg-white rounded-2xl border border-gray-200 text-left"
                  >
                    <div className={`w-12 h-6 rounded-full transition-colors relative flex-shrink-0 ${(form as any)[toggle.key] ? 'bg-[#1B3A6B]' : 'bg-gray-200'}`}>
                      <div className={`absolute top-1 w-4 h-4 rounded-full bg-white shadow transition-transform ${(form as any)[toggle.key] ? 'translate-x-7' : 'translate-x-1'}`} />
                    </div>
                    <div>
                      <p className="text-sm font-medium text-gray-800">{toggle.label}</p>
                      <p className="text-xs text-gray-400">{toggle.sub}</p>
                    </div>
                  </button>
                ))}
              </div>
            </motion.div>
          </AnimatePresence>
        )}

        {/* STEP 3: Pricing & Description */}
        {step === 'pricing' && (
          <AnimatePresence mode="wait">
            <motion.div key="pricing" initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -20 }} className="space-y-4">
              {/* Price */}
              <div>
                <label className="text-sm font-semibold text-gray-700 mb-2 block">Fiyat (₺ / baş) *</label>
                <div className="relative">
                  <DollarSign size={16} className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" />
                  <input
                    type="number"
                    value={form.pricePerHead}
                    onChange={e => update('pricePerHead', e.target.value)}
                    placeholder="0"
                    className={`w-full pl-10 pr-4 py-3 bg-gray-50 border rounded-2xl text-sm focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 ${errors.pricePerHead ? 'border-red-300' : 'border-gray-200'}`}
                  />
                </div>
                {errors.pricePerHead && <p className="text-xs text-red-500 mt-1 ml-1">{errors.pricePerHead}</p>}
                {form.pricePerHead && form.count && (
                  <p className="text-xs text-emerald-600 mt-1.5 ml-1 font-medium">
                    Toplam tahmini değer: {(Number(form.pricePerHead) * Number(form.count)).toLocaleString()}₺
                  </p>
                )}
              </div>

              {/* Description */}
              <div>
                <label className="text-sm font-semibold text-gray-700 mb-2 block">Açıklama *</label>
                <textarea
                  value={form.description}
                  onChange={e => update('description', e.target.value)}
                  placeholder="Hayvanlarınız hakkında detaylı bilgi verin. Besleme, sağlık durumu, belgeler..."
                  rows={4}
                  className={`w-full px-4 py-3 bg-gray-50 border rounded-2xl text-sm resize-none focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 ${errors.description ? 'border-red-300' : 'border-gray-200'}`}
                />
                <div className="flex justify-between mt-1">
                  {errors.description && <p className="text-xs text-red-500 ml-1">{errors.description}</p>}
                  <span className="text-xs text-gray-400 ml-auto">{form.description.length}/500</span>
                </div>
              </div>

              {/* Photo */}
              <div>
                <label className="text-sm font-semibold text-gray-700 mb-2 block">Fotoğraf Ekle</label>
                {form.imageAdded ? (
                  <div className="relative w-full h-32 bg-gray-100 rounded-2xl overflow-hidden">
                    <img src="https://images.unsplash.com/photo-1775046163765-8852d1e0db19?w=400&fit=crop" alt="preview" className="w-full h-full object-cover" />
                    <button
                      onClick={() => update('imageAdded', false)}
                      className="absolute top-2 right-2 w-7 h-7 rounded-full bg-black/50 flex items-center justify-center"
                    >
                      <X size={14} className="text-white" />
                    </button>
                    <div className="absolute bottom-2 left-2 bg-emerald-400 text-white text-[10px] font-bold px-2 py-0.5 rounded-full">Yüklendi</div>
                  </div>
                ) : (
                  <button
                    onClick={() => update('imageAdded', true)}
                    className="w-full h-24 border-2 border-dashed border-gray-300 rounded-2xl flex flex-col items-center justify-center gap-2 text-gray-400 hover:border-[#1B3A6B] hover:text-[#1B3A6B] transition-colors"
                  >
                    <Camera size={24} />
                    <span className="text-xs font-medium">Fotoğraf Ekle</span>
                  </button>
                )}
              </div>

              {/* Summary Card */}
              <div className="p-4 bg-[#1B3A6B]/5 border border-[#1B3A6B]/10 rounded-2xl space-y-2">
                <p className="text-xs font-bold text-[#1B3A6B] mb-2">📋 İlan Özeti</p>
                {[
                  { label: 'Kategori', val: form.category },
                  { label: 'Irk', val: form.breed },
                  { label: 'Yaş', val: form.age },
                  { label: 'Adet', val: form.count ? `${form.count} baş` : '–' },
                  { label: 'Fiyat', val: form.pricePerHead ? `${Number(form.pricePerHead).toLocaleString()}₺/baş` : '–' },
                ].map(row => (
                  <div key={row.label} className="flex justify-between">
                    <span className="text-xs text-gray-500">{row.label}</span>
                    <span className="text-xs font-semibold text-gray-800 capitalize">{row.val || '–'}</span>
                  </div>
                ))}
              </div>
            </motion.div>
          </AnimatePresence>
        )}
      </div>

      {/* Bottom CTA */}
      <div className="px-5 py-4 border-t border-gray-100 bg-white flex gap-3">
        {step !== 'type' && (
          <button
            onClick={() => {
              const prev: Record<FormStep, FormStep> = { type: 'type', details: 'type', pricing: 'details', success: 'pricing' };
              setStep(prev[step]);
            }}
            className="px-6 py-4 bg-gray-100 text-gray-700 rounded-2xl font-semibold text-sm"
          >
            Geri
          </button>
        )}
        <button
          onClick={handleNext}
          disabled={loading}
          className="flex-1 py-4 bg-[#1B3A6B] text-white rounded-2xl font-semibold text-sm transition-all active:scale-95 disabled:opacity-70 flex items-center justify-center gap-2 shadow-lg shadow-[#1B3A6B]/20"
        >
          {loading ? (
            <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
          ) : step === 'pricing' ? 'İlanı Yayınla' : 'Devam Et'}
        </button>
      </div>
    </div>
  );
}

// ──────────── SLAUGHTERHOUSE FORM ─────────────────────────────────────────────

function MeatListingForm() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [form, setForm] = useState({
    title: '',
    meatType: '',
    quantity: '',
    pricePerKg: '',
    description: '',
    isOrganic: false,
    hasCertificate: false,
    city: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  const update = (key: string, val: any) => {
    setForm(p => ({ ...p, [key]: val }));
    if (errors[key]) setErrors(p => ({ ...p, [key]: '' }));
  };

  const MEAT_TYPES = ['Dana Kıyma', 'Kuzu But', 'Dana Antrikot', 'Koyun Kıyma', 'Dana Kaburga', 'Kuzu Kıyma', 'Dana Bonfile', 'Tavuk Göğüs'];
  const CITIES = ['Ankara', 'İstanbul', 'İzmir', 'Bursa', 'Konya', 'Gaziantep', 'Samsun', 'Trabzon'];

  const validate = () => {
    const e: Record<string, string> = {};
    if (!form.title.trim()) e.title = 'Başlık gerekli';
    if (!form.meatType) e.meatType = 'Et türü seçin';
    if (!form.quantity || Number(form.quantity) <= 0) e.quantity = 'Geçerli miktar girin';
    if (!form.pricePerKg || Number(form.pricePerKg) <= 0) e.pricePerKg = 'Geçerli fiyat girin';
    if (!form.description.trim()) e.description = 'Açıklama gerekli';
    if (!form.city) e.city = 'Şehir seçin';
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async () => {
    if (!validate()) return;
    setLoading(true);
    await new Promise(r => setTimeout(r, 1200));
    setLoading(false);
    setSubmitted(true);
  };

  if (submitted) {
    return (
      <div className="flex flex-col items-center justify-center flex-1 px-6 text-center">
        <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} transition={{ type: 'spring', delay: 0.1 }}>
          <div className="w-24 h-24 rounded-full bg-emerald-50 flex items-center justify-center mb-5">
            <CheckCircle2 size={48} className="text-emerald-500" />
          </div>
        </motion.div>
        <h2 className="text-xl font-bold text-gray-900 mb-2">Et İlanı Yayınlandı!</h2>
        <p className="text-sm text-gray-500 mb-6">
          <strong>{form.meatType}</strong> ilanınız aktif. Et alıcılarından teklif bekliyorsunuz.
        </p>
        <div className="w-full p-4 bg-gray-50 rounded-2xl text-left mb-6 space-y-1">
          {[
            { l: 'Et Türü', v: form.meatType },
            { l: 'Miktar', v: `${Number(form.quantity).toLocaleString()} kg` },
            { l: 'Fiyat', v: `${Number(form.pricePerKg).toLocaleString()}₺/kg` },
            { l: 'Toplam', v: `${(Number(form.pricePerKg) * Number(form.quantity)).toLocaleString()}₺` },
          ].map(row => (
            <div key={row.l} className="flex justify-between">
              <span className="text-xs text-gray-500">{row.l}</span>
              <span className="text-xs font-semibold text-gray-800">{row.v}</span>
            </div>
          ))}
        </div>
        <button onClick={() => navigate('/app/search')} className="w-full py-4 bg-[#1B3A6B] text-white rounded-2xl font-semibold text-sm mb-3 shadow-lg shadow-[#1B3A6B]/20">
          İlanlarımı Gör
        </button>
        <button onClick={() => { setSubmitted(false); setForm({ title: '', meatType: '', quantity: '', pricePerKg: '', description: '', isOrganic: false, hasCertificate: false, city: '' }); }}
          className="w-full py-3 bg-gray-100 text-gray-700 rounded-2xl font-semibold text-sm">
          Yeni İlan Ver
        </button>
      </div>
    );
  }

  return (
    <div className="flex-1 overflow-y-auto px-5 py-5 space-y-4">
      {/* Title */}
      <div>
        <label className="text-sm font-semibold text-gray-700 mb-1.5 block">İlan Başlığı *</label>
        <input
          type="text"
          value={form.title}
          onChange={e => update('title', e.target.value)}
          placeholder="örn. Taze Dana Kıyma – Büyük Parti"
          maxLength={80}
          className={`w-full px-4 py-3 bg-gray-50 border rounded-2xl text-sm focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 ${errors.title ? 'border-red-300' : 'border-gray-200'}`}
        />
        {errors.title && <p className="text-xs text-red-500 mt-1 ml-1">{errors.title}</p>}
        <p className="text-xs text-gray-400 mt-1 text-right">{form.title.length}/80</p>
      </div>

      {/* Meat Type */}
      <div>
        <label className="text-sm font-semibold text-gray-700 mb-2 block">Et Türü *</label>
        <div className="flex flex-wrap gap-2">
          {MEAT_TYPES.map(t => (
            <button
              key={t}
              onClick={() => update('meatType', t)}
              className={`px-3 py-2 rounded-xl text-sm font-medium border transition-all ${form.meatType === t ? 'bg-[#1B3A6B] text-white border-[#1B3A6B]' : 'bg-gray-50 text-gray-700 border-gray-200'}`}
            >
              {t}
            </button>
          ))}
        </div>
        {errors.meatType && <p className="text-xs text-red-500 mt-1.5 ml-1">{errors.meatType}</p>}
      </div>

      {/* Quantity + Price */}
      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="text-sm font-semibold text-gray-700 mb-1.5 block">Miktar (kg) *</label>
          <input
            type="number"
            value={form.quantity}
            onChange={e => update('quantity', e.target.value)}
            placeholder="0"
            className={`w-full px-4 py-3 bg-gray-50 border rounded-2xl text-sm focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 ${errors.quantity ? 'border-red-300' : 'border-gray-200'}`}
          />
          {errors.quantity && <p className="text-xs text-red-500 mt-1 ml-1">{errors.quantity}</p>}
        </div>
        <div>
          <label className="text-sm font-semibold text-gray-700 mb-1.5 block">Fiyat (₺/kg) *</label>
          <input
            type="number"
            value={form.pricePerKg}
            onChange={e => update('pricePerKg', e.target.value)}
            placeholder="0"
            className={`w-full px-4 py-3 bg-gray-50 border rounded-2xl text-sm focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 ${errors.pricePerKg ? 'border-red-300' : 'border-gray-200'}`}
          />
          {errors.pricePerKg && <p className="text-xs text-red-500 mt-1 ml-1">{errors.pricePerKg}</p>}
        </div>
      </div>
      {form.quantity && form.pricePerKg && (
        <div className="p-3 bg-emerald-50 border border-emerald-200 rounded-xl">
          <p className="text-xs text-emerald-700 font-semibold">
            Toplam Değer: {(Number(form.pricePerKg) * Number(form.quantity)).toLocaleString()}₺
          </p>
        </div>
      )}

      {/* City */}
      <div>
        <label className="text-sm font-semibold text-gray-700 mb-1.5 block">Şehir *</label>
        <div className="relative">
          <select
            value={form.city}
            onChange={e => update('city', e.target.value)}
            className={`w-full px-4 py-3 bg-gray-50 border rounded-2xl text-sm appearance-none focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 ${errors.city ? 'border-red-300' : 'border-gray-200'}`}
          >
            <option value="">Şehir seçin</option>
            {CITIES.map(c => <option key={c} value={c}>{c}</option>)}
          </select>
          <ChevronDown size={16} className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 pointer-events-none" />
        </div>
        {errors.city && <p className="text-xs text-red-500 mt-1 ml-1">{errors.city}</p>}
      </div>

      {/* Toggles */}
      <div className="space-y-2.5">
        {[
          { key: 'hasCertificate', label: 'Gıda Güvenliği Belgeli', sub: 'Sertifikalı ürünler öncelikli gösterilir' },
          { key: 'isOrganic', label: 'Organik Sertifikalı', sub: 'Organik etiketini kullanabilirsiniz' },
        ].map(toggle => (
          <button
            key={toggle.key}
            onClick={() => update(toggle.key, !(form as any)[toggle.key])}
            className="w-full flex items-center gap-3 p-3.5 bg-white rounded-2xl border border-gray-200 text-left"
          >
            <div className={`w-12 h-6 rounded-full transition-colors relative flex-shrink-0 ${(form as any)[toggle.key] ? 'bg-[#1B3A6B]' : 'bg-gray-200'}`}>
              <div className={`absolute top-1 w-4 h-4 rounded-full bg-white shadow transition-transform ${(form as any)[toggle.key] ? 'translate-x-7' : 'translate-x-1'}`} />
            </div>
            <div>
              <p className="text-sm font-medium text-gray-800">{toggle.label}</p>
              <p className="text-xs text-gray-400">{toggle.sub}</p>
            </div>
          </button>
        ))}
      </div>

      {/* Description */}
      <div>
        <label className="text-sm font-semibold text-gray-700 mb-1.5 block">Açıklama *</label>
        <textarea
          value={form.description}
          onChange={e => update('description', e.target.value)}
          placeholder="Et kalitesi, işleme yöntemi, ambalaj bilgisi, teslimat koşulları..."
          rows={3}
          className={`w-full px-4 py-3 bg-gray-50 border rounded-2xl text-sm resize-none focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 ${errors.description ? 'border-red-300' : 'border-gray-200'}`}
        />
        {errors.description && <p className="text-xs text-red-500 mt-1 ml-1">{errors.description}</p>}
      </div>

      {/* Submit */}
      <button
        onClick={handleSubmit}
        disabled={loading}
        className="w-full py-4 bg-[#1B3A6B] text-white rounded-2xl font-semibold text-sm transition-all active:scale-95 disabled:opacity-70 flex items-center justify-center gap-2 shadow-lg shadow-[#1B3A6B]/20 mb-6"
      >
        {loading ? (
          <><div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" /><span>Yayınlanıyor...</span></>
        ) : '🥩 Et İlanı Yayınla'}
      </button>
    </div>
  );
}

// ──────────── MAIN CREATE SCREEN ──────────────────────────────────────────────

export function CreateListingScreen() {
  const { user } = useApp();

  if (!user || user.role === 'MEAT_BUYER') {
    return (
      <div className="flex flex-col items-center justify-center h-full px-8 text-center">
        <div className="text-5xl mb-4">🚫</div>
        <h2 className="text-base font-bold text-gray-900 mb-2">Bu özellik size ait değil</h2>
        <p className="text-sm text-gray-500">İlan vermek için Hayvan Satıcı veya Kesimhane hesabı olmalısınız.</p>
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full bg-[#F5F7FA]">
      {/* Form Header */}
      <div className="bg-white border-b border-gray-100 px-5 py-4">
        <h2 className="text-base font-bold text-gray-900">
          {user.role === 'ANIMAL_SELLER' ? '🐄 Hayvan İlanı Ver' : '🥩 Et İlanı Ver'}
        </h2>
        <p className="text-xs text-gray-400 mt-0.5">
          {user.role === 'ANIMAL_SELLER' ? 'Hayvanlarınızı kesimhanelere satın' : 'Et ürünlerinizi alıcılara sunun'}
        </p>
      </div>

      {user.role === 'ANIMAL_SELLER' ? <AnimalListingForm /> : <MeatListingForm />}
    </div>
  );
}
