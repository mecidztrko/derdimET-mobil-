import React, { useState, useCallback } from 'react';
import { useNavigate } from 'react-router';
import {
  Search, SlidersHorizontal, X, MapPin, ChevronDown,
  Heart, Star, CheckCircle2, Package2, TrendingUp, TrendingDown
} from 'lucide-react';
import { useApp } from '../../context/AppContext';
import {
  MEAT_LISTINGS, ANIMAL_LISTINGS, ANIMAL_REQUESTS,
  MeatListing, AnimalListing, AnimalRequest
} from '../../data/mockData';
import { RoleBadge } from '../../components/shared/StatusBadge';
import { EmptyState, SkeletonCard } from '../../components/shared/EmptyState';
import { toast } from 'sonner';
import { motion, AnimatePresence } from 'motion/react';

// ──────────── Filter Sheet ─────────────────────────────────────────────────────

interface FilterChip {
  id: string;
  label: string;
  active: boolean;
}

function FilterSheet({
  open,
  onClose,
  role,
  filters,
  onApply,
}: {
  open: boolean;
  onClose: () => void;
  role: string;
  filters: any;
  onApply: (f: any) => void;
}) {
  const [local, setLocal] = useState(filters);

  const MEAT_TYPES = ['Dana Kıyma', 'Kuzu But', 'Dana Antrikot', 'Koyun Kıyma', 'Dana Kaburga', 'Tavuk'];
  const CITIES = ['Ankara', 'İstanbul', 'İzmir', 'Bursa', 'Konya', 'Sivas', 'Gaziantep', 'Urfa'];
  const SORTS = [
    { value: 'newest', label: '🕐 En Yeni' },
    { value: 'cheapest', label: '💸 En Ucuz' },
    { value: 'expensive', label: '💰 En Pahalı' },
  ];

  const toggle = (key: string, val: string) => {
    setLocal((prev: any) => {
      const arr = prev[key] || [];
      return { ...prev, [key]: arr.includes(val) ? arr.filter((v: string) => v !== val) : [...arr, val] };
    });
  };

  return (
    <AnimatePresence>
      {open && (
        <>
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="absolute inset-0 bg-black/40 z-40"
            onClick={onClose}
          />
          <motion.div
            initial={{ y: '100%' }}
            animate={{ y: 0 }}
            exit={{ y: '100%' }}
            transition={{ type: 'spring', damping: 30, stiffness: 300 }}
            className="absolute bottom-0 left-0 right-0 bg-white rounded-t-3xl z-50 max-h-[85%] flex flex-col"
          >
            {/* Handle */}
            <div className="flex justify-center pt-3 pb-2">
              <div className="w-10 h-1 rounded-full bg-gray-300" />
            </div>

            <div className="flex items-center justify-between px-5 pb-4 border-b border-gray-100">
              <h3 className="text-base font-bold text-gray-900">Filtrele</h3>
              <button onClick={onClose} className="w-8 h-8 rounded-xl bg-gray-100 flex items-center justify-center">
                <X size={16} className="text-gray-600" />
              </button>
            </div>

            <div className="flex-1 overflow-y-auto px-5 py-4 space-y-5">
              {/* Sort */}
              <div>
                <p className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-2.5">Sıralama</p>
                <div className="flex flex-wrap gap-2">
                  {SORTS.map(s => (
                    <button
                      key={s.value}
                      onClick={() => setLocal((p: any) => ({ ...p, sort: s.value }))}
                      className={`px-3.5 py-2 rounded-xl text-sm font-medium border transition-all ${local.sort === s.value ? 'bg-[#1B3A6B] text-white border-[#1B3A6B]' : 'bg-gray-50 text-gray-700 border-gray-200'}`}
                    >
                      {s.label}
                    </button>
                  ))}
                </div>
              </div>

              {/* Category (for animal) */}
              {(role === 'ANIMAL_SELLER' || role === 'SLAUGHTERHOUSE') && (
                <div>
                  <p className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-2.5">Kategori</p>
                  <div className="flex gap-2">
                    {['küçükbaş', 'büyükbaş'].map(cat => (
                      <button
                        key={cat}
                        onClick={() => toggle('categories', cat)}
                        className={`flex-1 py-2.5 rounded-xl text-sm font-medium border transition-all capitalize ${local.categories?.includes(cat) ? 'bg-[#1B3A6B] text-white border-[#1B3A6B]' : 'bg-gray-50 text-gray-700 border-gray-200'}`}
                      >
                        {cat === 'küçükbaş' ? '🐑 Küçükbaş' : '🐄 Büyükbaş'}
                      </button>
                    ))}
                  </div>
                </div>
              )}

              {/* Meat Types */}
              {role === 'MEAT_BUYER' && (
                <div>
                  <p className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-2.5">Et Türü</p>
                  <div className="flex flex-wrap gap-2">
                    {MEAT_TYPES.map(t => (
                      <button
                        key={t}
                        onClick={() => toggle('meatTypes', t)}
                        className={`px-3 py-1.5 rounded-xl text-sm font-medium border transition-all ${local.meatTypes?.includes(t) ? 'bg-[#1B3A6B] text-white border-[#1B3A6B]' : 'bg-gray-50 text-gray-700 border-gray-200'}`}
                      >
                        {t}
                      </button>
                    ))}
                  </div>
                </div>
              )}

              {/* Price Range */}
              <div>
                <p className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-2.5">
                  {role === 'MEAT_BUYER' ? 'Fiyat Aralığı (₺/kg)' : 'Fiyat Aralığı (₺/baş)'}
                </p>
                <div className="flex items-center gap-3">
                  <div className="flex-1">
                    <input
                      type="number"
                      placeholder="Min"
                      value={local.minPrice || ''}
                      onChange={e => setLocal((p: any) => ({ ...p, minPrice: e.target.value ? Number(e.target.value) : null }))}
                      className="w-full px-3 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:outline-none focus:border-[#1B3A6B]"
                    />
                  </div>
                  <span className="text-gray-400">–</span>
                  <div className="flex-1">
                    <input
                      type="number"
                      placeholder="Max"
                      value={local.maxPrice || ''}
                      onChange={e => setLocal((p: any) => ({ ...p, maxPrice: e.target.value ? Number(e.target.value) : null }))}
                      className="w-full px-3 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:outline-none focus:border-[#1B3A6B]"
                    />
                  </div>
                </div>
              </div>

              {/* Quantity */}
              <div>
                <p className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-2.5">
                  {role === 'MEAT_BUYER' ? 'Miktar (kg)' : 'Adet'}
                </p>
                <div className="flex items-center gap-3">
                  <input
                    type="number"
                    placeholder="Min"
                    value={local.minQty || ''}
                    onChange={e => setLocal((p: any) => ({ ...p, minQty: e.target.value ? Number(e.target.value) : null }))}
                    className="flex-1 px-3 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:outline-none focus:border-[#1B3A6B]"
                  />
                  <span className="text-gray-400">–</span>
                  <input
                    type="number"
                    placeholder="Max"
                    value={local.maxQty || ''}
                    onChange={e => setLocal((p: any) => ({ ...p, maxQty: e.target.value ? Number(e.target.value) : null }))}
                    className="flex-1 px-3 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:outline-none focus:border-[#1B3A6B]"
                  />
                </div>
              </div>

              {/* City */}
              <div>
                <p className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-2.5">Şehir</p>
                <div className="flex flex-wrap gap-2">
                  {CITIES.map(c => (
                    <button
                      key={c}
                      onClick={() => toggle('cities', c)}
                      className={`px-3 py-1.5 rounded-xl text-sm font-medium border transition-all ${local.cities?.includes(c) ? 'bg-[#1B3A6B] text-white border-[#1B3A6B]' : 'bg-gray-50 text-gray-700 border-gray-200'}`}
                    >
                      {c}
                    </button>
                  ))}
                </div>
              </div>
            </div>

            {/* Actions */}
            <div className="px-5 py-4 border-t border-gray-100 flex gap-3">
              <button
                onClick={() => {
                  const reset = { sort: 'newest', meatTypes: [], categories: [], cities: [], minPrice: null, maxPrice: null, minQty: null, maxQty: null };
                  setLocal(reset);
                  onApply(reset);
                  onClose();
                }}
                className="flex-1 py-3 bg-gray-100 text-gray-700 rounded-2xl font-semibold text-sm"
              >
                Sıfırla
              </button>
              <button
                onClick={() => { onApply(local); onClose(); }}
                className="flex-2 flex-[2] py-3 bg-[#1B3A6B] text-white rounded-2xl font-semibold text-sm shadow-lg shadow-[#1B3A6B]/20"
              >
                Uygula
              </button>
            </div>
          </motion.div>
        </>
      )}
    </AnimatePresence>
  );
}

// ──────────── Meat Listing Card ───────────────────────────────────────────────

function MeatCard({ listing, onOffer }: { listing: MeatListing & { isFavorited?: boolean }; onOffer: () => void }) {
  const [fav, setFav] = useState(listing.isFavorited || false);
  return (
    <div className="bg-white rounded-2xl border border-gray-100 overflow-hidden shadow-sm">
      {listing.imageUrl && (
        <div className="relative h-28 overflow-hidden">
          <img src={listing.imageUrl} alt={listing.title} className="w-full h-full object-cover" />
          <div className="absolute inset-0 bg-gradient-to-t from-black/40 to-transparent" />
          <div className="absolute top-2.5 right-2.5">
            <button
              onClick={() => { setFav(!fav); toast.success(fav ? 'Favoriden çıkarıldı' : 'Favorilere eklendi'); }}
              className="w-8 h-8 rounded-xl bg-white/80 backdrop-blur flex items-center justify-center"
            >
              <Heart size={16} className={fav ? 'text-red-500 fill-red-500' : 'text-gray-600'} />
            </button>
          </div>
          <div className="absolute bottom-2 left-3">
            <span className="text-xs font-semibold text-white bg-black/50 backdrop-blur px-2 py-0.5 rounded-full">
              {listing.meatType}
            </span>
          </div>
        </div>
      )}
      <div className="p-4">
        <div className="flex items-start justify-between gap-2">
          <div className="flex-1">
            <h4 className="text-sm font-bold text-gray-900 leading-tight">{listing.title}</h4>
            <div className="flex items-center gap-1.5 mt-1">
              <CheckCircle2 size={12} className="text-blue-500" />
              <span className="text-xs text-gray-500">{listing.slaughterhouseName}</span>
            </div>
          </div>
          {!listing.imageUrl && (
            <button
              onClick={() => { setFav(!fav); toast.success(fav ? 'Favoriden çıkarıldı' : 'Favorilere eklendi'); }}
              className="w-8 h-8 rounded-xl bg-gray-50 flex items-center justify-center flex-shrink-0"
            >
              <Heart size={16} className={fav ? 'text-red-500 fill-red-500' : 'text-gray-400'} />
            </button>
          )}
        </div>

        <div className="flex flex-wrap gap-2 mt-3">
          <span className="inline-flex items-center gap-1 px-2 py-1 bg-gray-50 rounded-lg text-xs text-gray-600">
            <Package2 size={11} />
            {listing.quantity} kg
          </span>
          <span className="inline-flex items-center gap-1 px-2 py-1 bg-gray-50 rounded-lg text-xs text-gray-600">
            <MapPin size={11} />
            {listing.city}
          </span>
          <span className="text-xs text-gray-400 flex items-center">{listing.date}</span>
        </div>

        <div className="flex items-center justify-between mt-3 pt-3 border-t border-gray-50">
          <div>
            <p className="text-lg font-bold text-[#1B3A6B]">{listing.pricePerKg}₺<span className="text-xs font-normal text-gray-400">/kg</span></p>
            <p className="text-xs text-gray-400">Toplam: {(listing.pricePerKg * listing.quantity).toLocaleString()}₺</p>
          </div>
          <button
            onClick={onOffer}
            className="px-4 py-2.5 bg-[#1B3A6B] text-white rounded-xl text-sm font-semibold active:scale-95 transition-transform shadow-sm shadow-[#1B3A6B]/20"
          >
            Teklif Ver
          </button>
        </div>
      </div>
    </div>
  );
}

// ──────────── Animal Listing Card (for SLAUGHTERHOUSE) ────────────────────────

function AnimalCard({ listing, onOffer }: { listing: AnimalListing; onOffer: () => void }) {
  const [fav, setFav] = useState(listing.isFavorited || false);
  return (
    <div className="bg-white rounded-2xl border border-gray-100 p-4 shadow-sm">
      <div className="flex items-start gap-3">
        <div className={`w-12 h-12 rounded-2xl flex items-center justify-center text-2xl flex-shrink-0 ${listing.category === 'büyükbaş' ? 'bg-amber-50' : 'bg-green-50'}`}>
          {listing.category === 'büyükbaş' ? '🐄' : '🐑'}
        </div>
        <div className="flex-1 min-w-0">
          <div className="flex items-start justify-between gap-2">
            <div>
              <h4 className="text-sm font-bold text-gray-900">{listing.breed}</h4>
              <div className="flex flex-wrap items-center gap-1.5 mt-0.5">
                <span className={`text-[11px] font-medium px-2 py-0.5 rounded-full ${listing.category === 'büyükbaş' ? 'bg-amber-50 text-amber-700' : 'bg-green-50 text-green-700'}`}>
                  {listing.category}
                </span>
                <span className="text-xs text-gray-400">{listing.age}</span>
              </div>
            </div>
            <button
              onClick={() => { setFav(!fav); toast.success(fav ? 'Favoriden çıkarıldı' : 'Favorilere eklendi'); }}
              className="w-8 h-8 rounded-xl bg-gray-50 flex items-center justify-center flex-shrink-0"
            >
              <Heart size={16} className={fav ? 'text-red-500 fill-red-500' : 'text-gray-400'} />
            </button>
          </div>
          <p className="text-xs text-gray-500 mt-1.5 flex items-center gap-1">
            <CheckCircle2 size={11} className="text-blue-400" />
            {listing.sellerName}
          </p>
        </div>
      </div>

      <div className="flex flex-wrap gap-2 mt-3">
        {[
          { label: `${listing.count} baş` },
          { label: listing.city },
          { label: listing.date },
        ].map((tag, i) => (
          <span key={i} className="text-xs text-gray-600 bg-gray-50 px-2.5 py-1 rounded-lg">{tag.label}</span>
        ))}
      </div>

      <div className="flex items-center justify-between mt-3 pt-3 border-t border-gray-50">
        <div>
          <p className="text-lg font-bold text-[#1B3A6B]">{listing.pricePerHead.toLocaleString()}₺<span className="text-xs font-normal text-gray-400">/baş</span></p>
          <p className="text-xs text-gray-400">Toplam: {listing.totalPrice.toLocaleString()}₺</p>
        </div>
        <button
          onClick={onOffer}
          className="px-4 py-2.5 bg-[#1B3A6B] text-white rounded-xl text-sm font-semibold active:scale-95 transition-transform shadow-sm shadow-[#1B3A6B]/20"
        >
          Teklif Ver
        </button>
      </div>
    </div>
  );
}

// ──────────── Animal Request Card (for ANIMAL_SELLER) ─────────────────────────

function AnimalRequestCard({ request, onOffer }: { request: AnimalRequest; onOffer: () => void }) {
  return (
    <div className="bg-white rounded-2xl border border-gray-100 p-4 shadow-sm">
      <div className="flex items-start gap-3">
        <div className={`w-12 h-12 rounded-2xl flex items-center justify-center text-2xl flex-shrink-0 ${request.category === 'büyükbaş' ? 'bg-amber-50' : 'bg-green-50'}`}>
          {request.category === 'büyükbaş' ? '🐄' : '🐑'}
        </div>
        <div className="flex-1">
          <h4 className="text-sm font-bold text-gray-900 leading-tight">{request.title}</h4>
          <p className="text-xs text-gray-500 flex items-center gap-1 mt-0.5">
            <CheckCircle2 size={11} className="text-blue-400" />
            {request.slaughterhouseName}
          </p>
        </div>
      </div>

      <div className="flex flex-wrap gap-2 mt-3">
        {[
          { label: `${request.requestedCount} baş`, icon: '📦' },
          { label: request.weightRange, icon: '⚖️' },
          { label: request.city, icon: '📍' },
        ].map((tag, i) => (
          <span key={i} className="text-xs text-gray-600 bg-gray-50 px-2.5 py-1 rounded-lg flex items-center gap-1">
            <span>{tag.icon}</span>{tag.label}
          </span>
        ))}
      </div>

      <div className="flex items-center justify-between mt-3 pt-3 border-t border-gray-50">
        <div>
          <p className="text-xs text-gray-500">Maks. Fiyat</p>
          <p className="text-lg font-bold text-[#1B3A6B]">{request.maxPricePerHead.toLocaleString()}₺<span className="text-xs font-normal text-gray-400">/baş</span></p>
        </div>
        <button
          onClick={onOffer}
          className="px-4 py-2.5 bg-[#1B3A6B] text-white rounded-xl text-sm font-semibold active:scale-95 transition-transform shadow-sm shadow-[#1B3A6B]/20"
        >
          Teklif Ver
        </button>
      </div>
    </div>
  );
}

// ──────────── Offer Modal ─────────────────────────────────────────────────────

function OfferModal({ open, onClose, title, onSubmit }: { open: boolean; onClose: () => void; title: string; onSubmit: (price: number, note: string) => void }) {
  const [price, setPrice] = useState('');
  const [note, setNote] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {
    if (!price) return;
    setLoading(true);
    await new Promise(r => setTimeout(r, 800));
    onSubmit(Number(price), note);
    setLoading(false);
    setPrice('');
    setNote('');
    onClose();
  };

  return (
    <AnimatePresence>
      {open && (
        <>
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="absolute inset-0 bg-black/40 z-40" onClick={onClose} />
          <motion.div
            initial={{ y: '100%' }} animate={{ y: 0 }} exit={{ y: '100%' }}
            transition={{ type: 'spring', damping: 30, stiffness: 300 }}
            className="absolute bottom-0 left-0 right-0 bg-white rounded-t-3xl z-50"
          >
            <div className="flex justify-center pt-3 pb-1">
              <div className="w-10 h-1 rounded-full bg-gray-300" />
            </div>
            <div className="px-5 py-4">
              <h3 className="text-base font-bold text-gray-900 mb-1">Teklif Ver</h3>
              <p className="text-xs text-gray-500 mb-5 truncate">{title}</p>

              <div className="space-y-3">
                <div>
                  <label className="text-xs font-semibold text-gray-600 mb-1.5 block">Teklif Fiyatı *</label>
                  <div className="relative">
                    <input
                      type="number"
                      value={price}
                      onChange={e => setPrice(e.target.value)}
                      placeholder="0"
                      className="w-full pl-4 pr-12 py-3 bg-gray-50 border border-gray-200 rounded-2xl text-sm focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10"
                    />
                    <span className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-500 font-medium text-sm">₺</span>
                  </div>
                </div>
                <div>
                  <label className="text-xs font-semibold text-gray-600 mb-1.5 block">Notunuz</label>
                  <textarea
                    value={note}
                    onChange={e => setNote(e.target.value)}
                    placeholder="Teklifiniz hakkında kısa bir not..."
                    rows={2}
                    className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-2xl text-sm focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 resize-none"
                  />
                </div>
              </div>

              <div className="flex gap-3 mt-5 mb-4">
                <button onClick={onClose} className="flex-1 py-3.5 bg-gray-100 text-gray-700 rounded-2xl font-semibold text-sm">İptal</button>
                <button
                  onClick={handleSubmit}
                  disabled={!price || loading}
                  className="flex-[2] py-3.5 bg-[#1B3A6B] text-white rounded-2xl font-semibold text-sm disabled:opacity-50 flex items-center justify-center gap-2 shadow-lg shadow-[#1B3A6B]/20"
                >
                  {loading ? <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" /> : 'Teklifi Gönder'}
                </button>
              </div>
            </div>
          </motion.div>
        </>
      )}
    </AnimatePresence>
  );
}

// ──────────── Main Search Screen ──────────────────────────────────────────────

export function SearchScreen() {
  const { user } = useApp();
  const navigate = useNavigate();
  const [query, setQuery] = useState('');
  const [filterOpen, setFilterOpen] = useState(false);
  const [offerModal, setOfferModal] = useState<{ open: boolean; title: string }>({ open: false, title: '' });
  const [loading] = useState(false);

  const defaultFilters = {
    sort: 'newest',
    meatTypes: [] as string[],
    categories: [] as string[],
    cities: [] as string[],
    minPrice: null as number | null,
    maxPrice: null as number | null,
    minQty: null as number | null,
    maxQty: null as number | null,
  };
  const [filters, setFilters] = useState(defaultFilters);

  const activeFilterCount = [
    filters.meatTypes.length > 0,
    filters.categories.length > 0,
    filters.cities.length > 0,
    filters.minPrice !== null,
    filters.maxPrice !== null,
    filters.sort !== 'newest',
  ].filter(Boolean).length;

  const openOffer = (title: string) => setOfferModal({ open: true, title });
  const closeOffer = () => setOfferModal({ open: false, title: '' });
  const handleOfferSubmit = (price: number, note: string) => {
    toast.success(`Teklifiniz gönderildi! (${price.toLocaleString()}₺)`);
    navigate('/app/offers');
  };

  const headerInfo = {
    MEAT_BUYER: { title: 'Et İlanları', subtitle: 'Kesimhanelerin aktif ilanları', emoji: '🥩' },
    ANIMAL_SELLER: { title: 'Hayvan Talepleri', subtitle: 'Kesimhanelerin hayvan ihtiyaçları', emoji: '🏭' },
    SLAUGHTERHOUSE: { title: 'Hayvan İlanları', subtitle: 'Satıcıların aktif hayvan ilanları', emoji: '🐄' },
  };
  const info = headerInfo[user?.role || 'MEAT_BUYER'];

  const sortListings = <T extends { pricePerKg?: number; pricePerHead?: number; maxPricePerHead?: number; date: string }>(arr: T[]) => {
    return [...arr].sort((a, b) => {
      if (filters.sort === 'cheapest') return ((a.pricePerKg || a.pricePerHead || a.maxPricePerHead || 0) - (b.pricePerKg || b.pricePerHead || b.maxPricePerHead || 0));
      if (filters.sort === 'expensive') return ((b.pricePerKg || b.pricePerHead || b.maxPricePerHead || 0) - (a.pricePerKg || a.pricePerHead || a.maxPricePerHead || 0));
      return b.date.localeCompare(a.date);
    });
  };

  const meatResults = sortListings(MEAT_LISTINGS.filter(l => {
    if (query && !l.title.toLowerCase().includes(query.toLowerCase()) && !l.meatType.toLowerCase().includes(query.toLowerCase())) return false;
    if (filters.meatTypes.length && !filters.meatTypes.includes(l.meatType)) return false;
    if (filters.cities.length && !filters.cities.includes(l.city)) return false;
    if (filters.minPrice && l.pricePerKg < filters.minPrice) return false;
    if (filters.maxPrice && l.pricePerKg > filters.maxPrice) return false;
    if (filters.minQty && l.quantity < filters.minQty) return false;
    if (filters.maxQty && l.quantity > filters.maxQty) return false;
    return true;
  }));

  const animalResults = sortListings(ANIMAL_LISTINGS.filter(l => {
    if (query && !l.breed.toLowerCase().includes(query.toLowerCase())) return false;
    if (filters.categories.length && !filters.categories.includes(l.category)) return false;
    if (filters.cities.length && !filters.cities.includes(l.city)) return false;
    if (filters.minPrice && l.pricePerHead < filters.minPrice) return false;
    if (filters.maxPrice && l.pricePerHead > filters.maxPrice) return false;
    return true;
  }));

  const requestResults = sortListings(ANIMAL_REQUESTS.filter(r => {
    if (query && !r.title.toLowerCase().includes(query.toLowerCase())) return false;
    if (filters.categories.length && !filters.categories.includes(r.category)) return false;
    if (filters.cities.length && !filters.cities.includes(r.city)) return false;
    return true;
  }));

  return (
    <div className="flex flex-col h-full relative bg-[#F5F7FA]">
      {/* Search Header */}
      <div className="bg-white px-4 pt-3 pb-4 border-b border-gray-100">
        <div className="flex items-center gap-3 mb-3">
          <div className="flex-1">
            <h2 className="text-base font-bold text-gray-900">{info.emoji} {info.title}</h2>
            <p className="text-xs text-gray-400">{info.subtitle}</p>
          </div>
          <span className="text-xs text-gray-500 bg-gray-100 px-2.5 py-1 rounded-full font-medium">
            {user?.role === 'MEAT_BUYER' ? meatResults.length : user?.role === 'ANIMAL_SELLER' ? requestResults.length : animalResults.length} ilan
          </span>
        </div>
        <div className="flex gap-2">
          <div className="relative flex-1">
            <Search size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              value={query}
              onChange={e => setQuery(e.target.value)}
              placeholder={user?.role === 'MEAT_BUYER' ? 'Et türü, kesimhane...' : 'Hayvan türü, ırk...'}
              className="w-full pl-10 pr-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm text-gray-800 placeholder-gray-400 focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 transition-all"
            />
            {query && (
              <button onClick={() => setQuery('')} className="absolute right-3 top-1/2 -translate-y-1/2">
                <X size={14} className="text-gray-400" />
              </button>
            )}
          </div>
          <button
            onClick={() => setFilterOpen(true)}
            className={`flex items-center gap-1.5 px-3.5 py-2.5 rounded-xl border text-sm font-semibold transition-all ${activeFilterCount > 0 ? 'bg-[#1B3A6B] text-white border-[#1B3A6B]' : 'bg-white text-gray-700 border-gray-200'}`}
          >
            <SlidersHorizontal size={16} />
            {activeFilterCount > 0 && <span className="text-xs bg-white text-[#1B3A6B] rounded-full w-4 h-4 flex items-center justify-center font-bold">{activeFilterCount}</span>}
          </button>
        </div>

        {/* Active filter chips */}
        {activeFilterCount > 0 && (
          <div className="flex items-center gap-2 mt-2 overflow-x-auto pb-1 scrollbar-hide">
            {filters.sort !== 'newest' && (
              <span className="flex-shrink-0 inline-flex items-center gap-1 bg-[#1B3A6B]/10 text-[#1B3A6B] text-xs font-medium px-2.5 py-1 rounded-full">
                {filters.sort === 'cheapest' ? 'En Ucuz' : 'En Pahalı'}
                <X size={11} className="cursor-pointer" onClick={() => setFilters(p => ({ ...p, sort: 'newest' }))} />
              </span>
            )}
            {filters.meatTypes.map(t => (
              <span key={t} className="flex-shrink-0 inline-flex items-center gap-1 bg-[#1B3A6B]/10 text-[#1B3A6B] text-xs font-medium px-2.5 py-1 rounded-full">
                {t}
                <X size={11} className="cursor-pointer" onClick={() => setFilters(p => ({ ...p, meatTypes: p.meatTypes.filter(x => x !== t) }))} />
              </span>
            ))}
            {filters.categories.map(c => (
              <span key={c} className="flex-shrink-0 inline-flex items-center gap-1 bg-[#1B3A6B]/10 text-[#1B3A6B] text-xs font-medium px-2.5 py-1 rounded-full capitalize">
                {c}
                <X size={11} className="cursor-pointer" onClick={() => setFilters(p => ({ ...p, categories: p.categories.filter(x => x !== c) }))} />
              </span>
            ))}
            {filters.cities.map(c => (
              <span key={c} className="flex-shrink-0 inline-flex items-center gap-1 bg-[#1B3A6B]/10 text-[#1B3A6B] text-xs font-medium px-2.5 py-1 rounded-full">
                {c}
                <X size={11} className="cursor-pointer" onClick={() => setFilters(p => ({ ...p, cities: p.cities.filter(x => x !== c) }))} />
              </span>
            ))}
          </div>
        )}
      </div>

      {/* Listings */}
      <div className="flex-1 overflow-y-auto px-4 py-4 space-y-3">
        {loading ? (
          Array.from({ length: 3 }).map((_, i) => <SkeletonCard key={i} />)
        ) : user?.role === 'MEAT_BUYER' ? (
          meatResults.length === 0 ? (
            <EmptyState type="search" />
          ) : (
            meatResults.map(l => (
              <MeatCard key={l.id} listing={l} onOffer={() => openOffer(l.title)} />
            ))
          )
        ) : user?.role === 'ANIMAL_SELLER' ? (
          requestResults.length === 0 ? (
            <EmptyState type="search" />
          ) : (
            requestResults.map(r => (
              <AnimalRequestCard key={r.id} request={r} onOffer={() => openOffer(r.title)} />
            ))
          )
        ) : (
          animalResults.length === 0 ? (
            <EmptyState type="search" />
          ) : (
            animalResults.map(l => (
              <AnimalCard key={l.id} listing={l} onOffer={() => openOffer(l.breed + ' · ' + l.category)} />
            ))
          )
        )}
        <div className="h-4" />
      </div>

      {/* Filter Sheet */}
      <FilterSheet
        open={filterOpen}
        onClose={() => setFilterOpen(false)}
        role={user?.role || 'MEAT_BUYER'}
        filters={filters}
        onApply={setFilters}
      />

      {/* Offer Modal */}
      <OfferModal
        open={offerModal.open}
        onClose={closeOffer}
        title={offerModal.title}
        onSubmit={handleOfferSubmit}
      />
    </div>
  );
}
