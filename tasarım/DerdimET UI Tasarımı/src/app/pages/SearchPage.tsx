import { useState, useMemo } from "react";
import { useNavigate } from "react-router";
import { TopBar } from "../components/layout/TopBar";
import { BottomNav } from "../components/layout/BottomNav";
import { Button } from "../components/ui/Button";
import { useAuth } from "../context/AuthContext";
import {
  Filter, Heart, MapPin, Calendar, Search, X, CheckCircle,
  SlidersHorizontal, Scale, Package, Tag, Star, Eye,
  TrendingUp, ArrowUpDown, CheckCircle2, ChevronRight,
  Flame, ArrowUpNarrowWide, ArrowDownNarrowWide, Weight,
  BadgeCheck, Sparkles, RotateCcw, ChevronDown, ChevronUp,
} from "lucide-react";
import { cn } from "../../lib/utils";
import { meatListings, animalListings } from "../data/mockListings";
import type { MeatListingFull, AnimalListingFull } from "../data/mockListings";

// ─── Types ──────────────────────────────────────────────────────────────────
type Listing = MeatListingFull | AnimalListingFull;

type SortKey =
  | "newest"
  | "lowest"
  | "highest"
  | "rating"
  | "mostViewed"
  | "mostFavorited"
  | "heaviest"
  | "lightest";

interface Filters {
  type: string;
  breed: string;
  city: string;
  priceMin: string;
  priceMax: string;
  weightMin: string;
  weightMax: string;
  verifiedOnly: boolean;
  badges: string[];
}

const DEFAULT_FILTERS: Filters = {
  type: "",
  breed: "",
  city: "",
  priceMin: "",
  priceMax: "",
  weightMin: "",
  weightMax: "",
  verifiedOnly: false,
  badges: [],
};

// ─── Constants ───────────────────────────────────────────────────────────────
const typeColors: Record<string, string> = {
  Dana: "bg-blue-100 text-blue-700",
  Kuzu: "bg-amber-100 text-amber-700",
  Koyun: "bg-purple-100 text-purple-700",
  Keçi: "bg-emerald-100 text-emerald-700",
  Küçükbaş: "bg-orange-100 text-orange-700",
  Büyükbaş: "bg-red-100 text-red-700",
};

const typeStrip: Record<string, string> = {
  Dana: "bg-blue-400", Kuzu: "bg-amber-400",
  Koyun: "bg-purple-400", Keçi: "bg-emerald-400",
  Küçükbaş: "bg-orange-400", Büyükbaş: "bg-red-400",
};

const badgeColors: Record<string, string> = {
  "Çok Satan": "bg-orange-100 text-orange-700",
  "Premium": "bg-purple-100 text-purple-700",
  "Yeni": "bg-blue-100 text-blue-700",
  "Toplu Satış": "bg-teal-100 text-teal-700",
  "Sertifikalı": "bg-green-100 text-green-700",
  "Yüksek Verimli": "bg-indigo-100 text-indigo-700",
  "Damızlık": "bg-rose-100 text-rose-700",
};

const sortOptions: { key: SortKey; label: string; icon: React.ElementType; desc: string }[] = [
  { key: "newest",       label: "En Yeni",           icon: Sparkles,             desc: "Son eklenen ilanlar" },
  { key: "lowest",       label: "En Düşük Fiyat",    icon: ArrowUpNarrowWide,    desc: "Fiyata göre artan" },
  { key: "highest",      label: "En Yüksek Fiyat",   icon: ArrowDownNarrowWide,  desc: "Fiyata göre azalan" },
  { key: "rating",       label: "En Yüksek Puan",    icon: Star,                 desc: "Satıcı puanı yüksek" },
  { key: "mostViewed",   label: "En Çok Görüntülenen",icon: Eye,                 desc: "Popüler ilanlar" },
  { key: "mostFavorited",label: "En Çok Favorilenen",icon: Heart,                desc: "Beğeni sayısı yüksek" },
  { key: "heaviest",     label: "En Yüksek Ağırlık", icon: Weight,               desc: "Ağırlığa göre azalan" },
  { key: "lightest",     label: "En Düşük Ağırlık",  icon: Scale,                desc: "Ağırlığa göre artan" },
];

const pricePresets = [
  { label: "0–100 ₺", min: "0", max: "100" },
  { label: "100–200 ₺", min: "100", max: "200" },
  { label: "200–300 ₺", min: "200", max: "300" },
  { label: "300+ ₺", min: "300", max: "" },
];

const animalPricePresets = [
  { label: "0–5K ₺", min: "0", max: "5000" },
  { label: "5K–20K ₺", min: "5000", max: "20000" },
  { label: "20K–50K ₺", min: "20000", max: "50000" },
  { label: "50K+ ₺", min: "50000", max: "" },
];

const animalBreedMap: Record<string, string[]> = {
  "Küçükbaş": ["Merinos", "Kıvırcık", "Akkaraman", "İvesi", "Saanen", "Ankara Keçisi"],
  "Büyükbaş": ["Simental", "Holstein", "Angus", "Hereford", "Montofon", "Charolais"],
};

// ─── Offer Modal ─────────────────────────────────────────────────────────────
function OfferModal({ listing, onClose }: { listing: Listing; onClose: () => void }) {
  const [amount, setAmount] = useState(listing.price.toString());
  const [note, setNote] = useState("");
  const [submitted, setSubmitted] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    await new Promise((r) => setTimeout(r, 900));
    setLoading(false);
    setSubmitted(true);
  };

  return (
    <div className="fixed inset-0 z-50 flex flex-col justify-end">
      <div className="absolute inset-0 bg-black/40 backdrop-blur-sm" onClick={onClose} />
      <div className="relative bg-background rounded-t-3xl px-5 pb-8 pt-4 z-10 max-w-md mx-auto w-full">
        <div className="w-10 h-1 bg-muted rounded-full mx-auto mb-4" />
        {submitted ? (
          <div className="text-center py-6">
            <div className="size-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <CheckCircle className="size-8 text-green-600" />
            </div>
            <h3 className="font-bold text-foreground mb-1">Teklifiniz Gönderildi!</h3>
            <p className="text-sm text-muted-foreground mb-5">Satıcı en kısa sürede dönüş yapacak</p>
            <Button onClick={onClose} className="w-full rounded-xl">Kapat</Button>
          </div>
        ) : (
          <>
            <div className="flex items-start justify-between mb-4">
              <div>
                <h3 className="font-bold text-foreground">{listing.title}</h3>
                <p className="text-sm text-muted-foreground">{listing.seller} · {listing.city}</p>
              </div>
              <button onClick={onClose} className="p-2 hover:bg-muted rounded-xl"><X className="size-4" /></button>
            </div>
            <div className="bg-muted/50 rounded-2xl p-3 mb-4">
              <div className="flex items-center justify-between">
                <span className="text-sm text-muted-foreground">İstenen Fiyat</span>
                <span className="font-bold text-primary">{listing.price.toLocaleString("tr-TR")} ₺/{listing.unit}</span>
              </div>
            </div>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-foreground mb-1.5">
                  Teklif Fiyatınız (₺/{listing.unit})
                </label>
                <div className="relative">
                  <input
                    type="number"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    className="h-12 w-full rounded-xl border-2 border-primary bg-white px-4 text-foreground font-semibold text-lg focus:outline-none"
                    required
                  />
                  <span className="absolute right-4 top-1/2 -translate-y-1/2 text-sm text-muted-foreground font-medium">₺</span>
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-foreground mb-1.5">Notunuz (opsiyonel)</label>
                <textarea
                  value={note}
                  onChange={(e) => setNote(e.target.value)}
                  placeholder="Örn: 500 kg için bu fiyat teklif ediyorum..."
                  className="w-full min-h-[80px] rounded-xl border border-border bg-white px-4 py-3 text-sm text-foreground resize-none focus:outline-none focus:ring-2 focus:ring-ring"
                />
              </div>
              <Button type="submit" className="w-full h-12 rounded-xl" loading={loading}>Teklif Gönder</Button>
            </form>
          </>
        )}
      </div>
    </div>
  );
}

// ─── Sort Sheet ───────────────────────────────────────────────────────────────
function SortSheet({ current, onChange, onClose }: {
  current: SortKey;
  onChange: (k: SortKey) => void;
  onClose: () => void;
}) {
  return (
    <div className="fixed inset-0 z-50 flex flex-col justify-end">
      <div className="absolute inset-0 bg-black/40 backdrop-blur-sm" onClick={onClose} />
      <div className="relative bg-background rounded-t-3xl px-4 pb-8 pt-4 z-10 max-w-md mx-auto w-full">
        <div className="w-10 h-1 bg-muted rounded-full mx-auto mb-4" />
        <div className="flex items-center justify-between mb-4 px-1">
          <h3 className="font-bold text-foreground">Sıralama</h3>
          <button onClick={onClose} className="p-1.5 hover:bg-muted rounded-lg"><X className="size-4" /></button>
        </div>
        <div className="space-y-1">
          {sortOptions.map(({ key, label, icon: Icon, desc }) => (
            <button
              key={key}
              onClick={() => { onChange(key); onClose(); }}
              className={cn(
                "w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all text-left",
                current === key
                  ? "bg-primary/8 border-2 border-primary"
                  : "hover:bg-muted border-2 border-transparent"
              )}
            >
              <div className={cn(
                "size-9 rounded-xl flex items-center justify-center flex-shrink-0",
                current === key ? "bg-primary text-white" : "bg-muted text-muted-foreground"
              )}>
                <Icon className="size-4" />
              </div>
              <div className="flex-1">
                <p className={cn("text-sm font-semibold", current === key ? "text-primary" : "text-foreground")}>{label}</p>
                <p className="text-xs text-muted-foreground">{desc}</p>
              </div>
              {current === key && <CheckCircle2 className="size-5 text-primary flex-shrink-0" />}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}

// ─── Filter Section Wrapper ───────────────────────────────────────────────────
function FilterSection({ title, icon: Icon, children, defaultOpen = true }: {
  title: string;
  icon: React.ElementType;
  children: React.ReactNode;
  defaultOpen?: boolean;
}) {
  const [open, setOpen] = useState(defaultOpen);
  return (
    <div className="border border-border rounded-2xl overflow-hidden">
      <button
        onClick={() => setOpen(!open)}
        className="w-full flex items-center justify-between px-4 py-3 bg-muted/30 hover:bg-muted/50 transition-colors"
      >
        <div className="flex items-center gap-2">
          <Icon className="size-4 text-primary" />
          <span className="text-sm font-bold text-foreground">{title}</span>
        </div>
        {open ? <ChevronUp className="size-4 text-muted-foreground" /> : <ChevronDown className="size-4 text-muted-foreground" />}
      </button>
      {open && <div className="px-4 py-4 bg-white space-y-3">{children}</div>}
    </div>
  );
}

// ─── Filter Sheet ─────────────────────────────────────────────────────────────
function FilterSheet({
  filters, types, breeds, cities, badges, isAnimal, onApply, onClose,
}: {
  filters: Filters;
  types: string[];
  breeds: string[];
  cities: string[];
  badges: string[];
  isAnimal: boolean;
  onApply: (f: Filters) => void;
  onClose: () => void;
}) {
  const [local, setLocal] = useState<Filters>({ ...filters });

  const set = <K extends keyof Filters>(key: K, val: Filters[K]) =>
    setLocal((prev) => ({ ...prev, [key]: val }));

  const toggleBadge = (badge: string) => {
    setLocal((prev) => ({
      ...prev,
      badges: prev.badges.includes(badge)
        ? prev.badges.filter((b) => b !== badge)
        : [...prev.badges, badge],
    }));
  };

  const reset = () => setLocal({ ...DEFAULT_FILTERS });

  const availableBreeds = local.type ? (animalBreedMap[local.type] || breeds) : breeds;
  const pricePresetsToUse = isAnimal ? animalPricePresets : pricePresets;

  const activeCount = [
    local.type, local.breed, local.city, local.priceMin, local.priceMax,
    local.weightMin, local.weightMax,
    local.verifiedOnly ? "v" : "",
    ...local.badges,
  ].filter(Boolean).length;

  return (
    <div className="fixed inset-0 z-50 flex flex-col justify-end">
      <div className="absolute inset-0 bg-black/40 backdrop-blur-sm" onClick={onClose} />
      <div className="relative bg-background rounded-t-3xl z-10 max-w-md mx-auto w-full flex flex-col max-h-[92vh]">
        {/* Header */}
        <div className="px-5 pt-4 pb-3 border-b border-border">
          <div className="w-10 h-1 bg-muted rounded-full mx-auto mb-3" />
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <h3 className="font-bold text-foreground">Filtreler</h3>
              {activeCount > 0 && (
                <span className="size-5 bg-primary text-white text-[10px] font-bold rounded-full flex items-center justify-center">
                  {activeCount}
                </span>
              )}
            </div>
            <div className="flex items-center gap-2">
              <button
                onClick={reset}
                className="flex items-center gap-1.5 text-sm text-muted-foreground hover:text-foreground transition-colors"
              >
                <RotateCcw className="size-3.5" />
                Sıfırla
              </button>
              <button onClick={onClose} className="p-1.5 hover:bg-muted rounded-lg ml-1">
                <X className="size-4" />
              </button>
            </div>
          </div>
        </div>

        {/* Scrollable content */}
        <div className="overflow-y-auto flex-1 px-4 py-4 space-y-3">

          {/* Tür */}
          <FilterSection title={isAnimal ? "Hayvan Kategorisi" : "Et Türü"} icon={Tag}>
            <div className="flex flex-wrap gap-2">
              {["", ...types].map((t) => (
                <button
                  key={t || "all"}
                  onClick={() => { set("type", t); if (!t) set("breed", ""); }}
                  className={cn(
                    "px-3.5 py-2 rounded-xl border-2 text-sm font-medium transition-all",
                    local.type === t
                      ? "bg-primary text-white border-primary"
                      : "bg-white text-muted-foreground border-border hover:border-primary/50"
                  )}
                >
                  {t || "Tümü"}
                </button>
              ))}
            </div>
          </FilterSection>

          {/* Irk (animals only) */}
          {isAnimal && (
            <FilterSection title="Irk" icon={Tag} defaultOpen={!!local.type}>
              {!local.type ? (
                <p className="text-xs text-muted-foreground py-1">Önce hayvan kategorisi seçin</p>
              ) : (
                <div className="flex flex-wrap gap-2">
                  {["", ...availableBreeds].map((b) => (
                    <button
                      key={b || "all"}
                      onClick={() => set("breed", b)}
                      className={cn(
                        "px-3 py-1.5 rounded-xl border-2 text-sm font-medium transition-all",
                        local.breed === b
                          ? "bg-primary text-white border-primary"
                          : "bg-white text-muted-foreground border-border hover:border-primary/50"
                      )}
                    >
                      {b || "Tüm Irklar"}
                    </button>
                  ))}
                </div>
              )}
            </FilterSection>
          )}

          {/* Şehir */}
          <FilterSection title="Konum" icon={MapPin}>
            <select
              className="w-full h-11 rounded-xl border-2 border-border bg-white px-4 text-sm text-foreground focus:outline-none focus:border-primary transition-colors appearance-none"
              value={local.city}
              onChange={(e) => set("city", e.target.value)}
            >
              <option value="">Tüm Şehirler</option>
              {cities.map((c) => <option key={c} value={c}>{c}</option>)}
            </select>
          </FilterSection>

          {/* Fiyat */}
          <FilterSection title="Fiyat Aralığı" icon={Tag}>
            {/* Quick presets */}
            <div className="flex flex-wrap gap-2 mb-3">
              {pricePresetsToUse.map((p) => {
                const active = local.priceMin === p.min && local.priceMax === p.max;
                return (
                  <button
                    key={p.label}
                    onClick={() => {
                      if (active) { set("priceMin", ""); set("priceMax", ""); }
                      else { set("priceMin", p.min); set("priceMax", p.max); }
                    }}
                    className={cn(
                      "px-3 py-1.5 rounded-xl border-2 text-xs font-semibold transition-all",
                      active
                        ? "bg-primary text-white border-primary"
                        : "bg-white text-muted-foreground border-border hover:border-primary/50"
                    )}
                  >
                    {p.label}
                  </button>
                );
              })}
            </div>
            {/* Manual inputs */}
            <div className="flex items-center gap-2">
              <div className="relative flex-1">
                <input
                  type="number"
                  placeholder="Min ₺"
                  value={local.priceMin}
                  onChange={(e) => set("priceMin", e.target.value)}
                  className="w-full h-10 rounded-xl border-2 border-border bg-white px-3 text-sm text-foreground focus:outline-none focus:border-primary transition-colors"
                />
              </div>
              <span className="text-muted-foreground text-sm font-medium">–</span>
              <div className="relative flex-1">
                <input
                  type="number"
                  placeholder="Max ₺"
                  value={local.priceMax}
                  onChange={(e) => set("priceMax", e.target.value)}
                  className="w-full h-10 rounded-xl border-2 border-border bg-white px-3 text-sm text-foreground focus:outline-none focus:border-primary transition-colors"
                />
              </div>
            </div>
          </FilterSection>

          {/* Ağırlık */}
          <FilterSection title="Ağırlık Aralığı (kg)" icon={Scale} defaultOpen={false}>
            {/* Quick presets for weight */}
            <div className="flex flex-wrap gap-2 mb-3">
              {(isAnimal
                ? [{ label: "0–100 kg", min: "0", max: "100" }, { label: "100–300 kg", min: "100", max: "300" }, { label: "300–600 kg", min: "300", max: "600" }, { label: "600+ kg", min: "600", max: "" }]
                : [{ label: "0–100 kg", min: "0", max: "100" }, { label: "100–300 kg", min: "100", max: "300" }, { label: "300–500 kg", min: "300", max: "500" }, { label: "500+ kg", min: "500", max: "" }]
              ).map((p) => {
                const active = local.weightMin === p.min && local.weightMax === p.max;
                return (
                  <button
                    key={p.label}
                    onClick={() => {
                      if (active) { set("weightMin", ""); set("weightMax", ""); }
                      else { set("weightMin", p.min); set("weightMax", p.max); }
                    }}
                    className={cn(
                      "px-3 py-1.5 rounded-xl border-2 text-xs font-semibold transition-all",
                      active ? "bg-primary text-white border-primary" : "bg-white text-muted-foreground border-border hover:border-primary/50"
                    )}
                  >
                    {p.label}
                  </button>
                );
              })}
            </div>
            <div className="flex items-center gap-2">
              <input
                type="number"
                placeholder="Min kg"
                value={local.weightMin}
                onChange={(e) => set("weightMin", e.target.value)}
                className="flex-1 h-10 rounded-xl border-2 border-border bg-white px-3 text-sm text-foreground focus:outline-none focus:border-primary transition-colors"
              />
              <span className="text-muted-foreground text-sm">–</span>
              <input
                type="number"
                placeholder="Max kg"
                value={local.weightMax}
                onChange={(e) => set("weightMax", e.target.value)}
                className="flex-1 h-10 rounded-xl border-2 border-border bg-white px-3 text-sm text-foreground focus:outline-none focus:border-primary transition-colors"
              />
            </div>
          </FilterSection>

          {/* Rozet */}
          {badges.length > 0 && (
            <FilterSection title="Rozet" icon={Sparkles} defaultOpen={false}>
              <div className="flex flex-wrap gap-2">
                {badges.map((badge) => {
                  const active = local.badges.includes(badge);
                  return (
                    <button
                      key={badge}
                      onClick={() => toggleBadge(badge)}
                      className={cn(
                        "px-3 py-1.5 rounded-xl border-2 text-xs font-semibold transition-all",
                        active
                          ? cn("border-primary ring-1 ring-primary", badgeColors[badge] || "bg-muted")
                          : cn("border-border", badgeColors[badge] || "bg-muted", "hover:border-primary/50")
                      )}
                    >
                      {badge}
                    </button>
                  );
                })}
              </div>
            </FilterSection>
          )}

          {/* Satıcı */}
          <FilterSection title="Satıcı Özellikleri" icon={BadgeCheck} defaultOpen={false}>
            <button
              onClick={() => set("verifiedOnly", !local.verifiedOnly)}
              className={cn(
                "w-full flex items-center justify-between px-4 py-3 rounded-xl border-2 transition-all",
                local.verifiedOnly ? "bg-green-50 border-green-400" : "bg-white border-border hover:border-primary/40"
              )}
            >
              <div className="flex items-center gap-2.5">
                <CheckCircle2 className={cn("size-5", local.verifiedOnly ? "text-green-600" : "text-muted-foreground")} />
                <div className="text-left">
                  <p className={cn("text-sm font-semibold", local.verifiedOnly ? "text-green-700" : "text-foreground")}>
                    Yalnızca Doğrulanmış Satıcılar
                  </p>
                  <p className="text-xs text-muted-foreground">Kimlik ve belge doğrulaması yapılmış</p>
                </div>
              </div>
              <div className={cn(
                "size-6 rounded-full border-2 flex items-center justify-center transition-all",
                local.verifiedOnly ? "bg-green-500 border-green-500" : "border-border"
              )}>
                {local.verifiedOnly && <CheckCircle2 className="size-3.5 text-white" />}
              </div>
            </button>
          </FilterSection>
        </div>

        {/* Footer */}
        <div className="px-4 py-4 border-t border-border bg-white">
          <button
            onClick={() => { onApply(local); onClose(); }}
            className="w-full h-12 bg-primary text-white font-bold rounded-xl flex items-center justify-center gap-2 hover:bg-primary/90 shadow-md shadow-primary/20 transition-all"
          >
            <Filter className="size-4" />
            Filtreleri Uygula
            {activeCount > 0 && (
              <span className="bg-white/20 px-2 py-0.5 rounded-full text-xs font-bold">
                {activeCount} aktif
              </span>
            )}
          </button>
        </div>
      </div>
    </div>
  );
}

// ─── Active Filter Pills ──────────────────────────────────────────────────────
function ActiveFilterPills({ filters, sortBy, onRemove, onClearAll }: {
  filters: Filters;
  sortBy: SortKey;
  onRemove: (key: keyof Filters | "badge", value?: string) => void;
  onClearAll: () => void;
}) {
  const pills: { label: string; key: keyof Filters | "badge"; value?: string }[] = [];
  if (filters.type) pills.push({ label: filters.type, key: "type" });
  if (filters.breed) pills.push({ label: filters.breed, key: "breed" });
  if (filters.city) pills.push({ label: filters.city, key: "city" });
  if (filters.priceMin || filters.priceMax)
    pills.push({ label: `${filters.priceMin || "0"} – ${filters.priceMax || "∞"} ₺`, key: "priceMin" });
  if (filters.weightMin || filters.weightMax)
    pills.push({ label: `${filters.weightMin || "0"} – ${filters.weightMax || "∞"} kg`, key: "weightMin" });
  if (filters.verifiedOnly) pills.push({ label: "Doğrulanmış", key: "verifiedOnly" });
  filters.badges.forEach((b) => pills.push({ label: b, key: "badge", value: b }));

  if (pills.length === 0) return null;

  return (
    <div className="flex items-center gap-2 overflow-x-auto pb-1 no-scrollbar">
      {pills.map((p, i) => (
        <div
          key={i}
          className="flex-shrink-0 flex items-center gap-1.5 bg-primary/10 text-primary border border-primary/20 px-2.5 py-1 rounded-full text-xs font-semibold"
        >
          {p.label}
          <button
            onClick={() => onRemove(p.key, p.value)}
            className="hover:bg-primary/20 rounded-full p-0.5 transition-colors"
          >
            <X className="size-3" />
          </button>
        </div>
      ))}
      <button
        onClick={onClearAll}
        className="flex-shrink-0 flex items-center gap-1 text-xs text-muted-foreground hover:text-foreground transition-colors px-1 ml-1 whitespace-nowrap"
      >
        <RotateCcw className="size-3" />
        Temizle
      </button>
    </div>
  );
}

// ─── Listing Card ─────────────────────────────────────────────────────────────
function ListingCard({ listing, isFav, onFav, onOffer, onClick }: {
  listing: Listing;
  isFav: boolean;
  onFav: () => void;
  onOffer: () => void;
  onClick: () => void;
}) {
  const qty = "quantity" in listing ? listing.quantity : null;
  const breed = "breed" in listing ? listing.breed : null;

  return (
    <div
      className="bg-white rounded-2xl border border-border shadow-sm overflow-hidden cursor-pointer active:scale-[0.99] transition-transform"
      onClick={onClick}
    >
      <div className={cn("h-1.5 w-full", typeStrip[listing.type] || "bg-primary")} />

      {/* Photo */}
      {listing.images?.length > 0 && (
        <div className="relative w-full h-36 bg-muted overflow-hidden">
          <img src={listing.images[0]} alt={listing.title} className="w-full h-full object-cover" />
          {listing.images.length > 1 && (
            <div className="absolute bottom-2 right-2 bg-black/50 text-white text-[10px] px-2 py-0.5 rounded-full">
              +{listing.images.length - 1} fotoğraf
            </div>
          )}
          {listing.badge && (
            <div className={cn(
              "absolute top-2 left-2 text-[10px] font-bold px-2 py-0.5 rounded-full",
              badgeColors[listing.badge] || "bg-white text-foreground"
            )}>
              {listing.badge}
            </div>
          )}
          <button
            onClick={(e) => { e.stopPropagation(); onFav(); }}
            className="absolute top-2 right-2 size-7 bg-white/90 rounded-full flex items-center justify-center shadow-sm hover:bg-white transition-colors"
          >
            <Heart className={cn("size-3.5 transition-colors", isFav ? "fill-red-500 text-red-500" : "text-muted-foreground")} />
          </button>
        </div>
      )}

      <div className="p-4">
        {/* Header */}
        <div className="flex items-start gap-3 mb-3">
          <div className={cn("size-10 rounded-xl flex items-center justify-center flex-shrink-0 text-sm font-bold", typeColors[listing.type] || "bg-muted")}>
            {listing.type.charAt(0)}
          </div>
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-1.5 flex-wrap mb-0.5">
              <h3 className="font-semibold text-foreground text-sm leading-tight">{listing.title}</h3>
              {listing.verified && <CheckCircle className="size-3.5 text-green-600 flex-shrink-0" />}
            </div>
            <div className="flex items-center gap-2">
              <span className="text-xs text-muted-foreground">{listing.seller}</span>
              <span className="flex items-center gap-0.5 text-xs text-amber-600">
                <Star className="size-3 fill-amber-400 text-amber-400" />
                {listing.rating}
              </span>
            </div>
          </div>
          {!listing.images?.length && (
            <button onClick={(e) => { e.stopPropagation(); onFav(); }} className="p-1.5 hover:bg-muted rounded-xl transition-colors">
              <Heart className={cn("size-4", isFav ? "fill-red-500 text-red-500" : "text-muted-foreground")} />
            </button>
          )}
        </div>

        {/* Details */}
        <div className="grid grid-cols-3 gap-2 mb-3">
          <div className="bg-muted/50 rounded-xl p-2">
            <p className="text-[10px] text-muted-foreground mb-0.5">Tür</p>
            <p className="text-xs font-semibold text-foreground truncate">
              {breed ? `${breed}` : listing.type}
            </p>
          </div>
          <div className="bg-muted/50 rounded-xl p-2">
            <p className="text-[10px] text-muted-foreground mb-0.5">{qty ? "Adet" : "Stok"}</p>
            <p className="text-xs font-semibold text-foreground">
              {qty ? `${qty} adet` : `${listing.weight} kg`}
            </p>
          </div>
          <div className="bg-muted/50 rounded-xl p-2">
            <p className="text-[10px] text-muted-foreground mb-0.5">Ağırlık</p>
            <p className="text-xs font-semibold text-foreground">{listing.weight} kg</p>
          </div>
        </div>

        {/* Location & stats */}
        <div className="flex items-center gap-3 mb-3 text-xs text-muted-foreground">
          <div className="flex items-center gap-1">
            <MapPin className="size-3.5" />
            {"district" in listing && listing.district
              ? `${listing.district}, ${listing.city}`
              : listing.city}
          </div>
          <div className="flex items-center gap-1 ml-auto">
            <Eye className="size-3.5" />
            {listing.viewCount}
          </div>
          <div className="flex items-center gap-1">
            <Heart className="size-3.5" />
            {listing.favoriteCount}
          </div>
        </div>

        {/* Price & CTA */}
        <div className="flex items-center justify-between pt-3 border-t border-border">
          <div>
            <p className="text-[11px] text-muted-foreground">Fiyat</p>
            <p className="font-bold text-primary">
              {listing.price.toLocaleString("tr-TR")} ₺
              <span className="text-xs font-normal text-muted-foreground">/{listing.unit}</span>
            </p>
          </div>
          <Button
            size="sm"
            onClick={(e) => { e.stopPropagation(); onOffer(); }}
            className="rounded-xl px-5"
          >
            Teklif Ver
          </Button>
        </div>
      </div>
    </div>
  );
}

// ─── Main Page ────────────────────────────────────────────────────────────────
export function SearchPage() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [searchQuery, setSearchQuery] = useState("");
  const [filters, setFilters] = useState<Filters>({ ...DEFAULT_FILTERS });
  const [favorites, setFavorites] = useState<string[]>([]);
  const [showFilterSheet, setShowFilterSheet] = useState(false);
  const [showSortSheet, setShowSortSheet] = useState(false);
  const [offerListing, setOfferListing] = useState<Listing | null>(null);
  const [sortBy, setSortBy] = useState<SortKey>("newest");

  const isSlaughterhouse = user?.role === "SLAUGHTERHOUSE";
  const baseListings: Listing[] = isSlaughterhouse ? animalListings : meatListings;
  const allTypes = [...new Set(baseListings.map((l) => l.type))];
  const allBreeds = [...new Set(
    (baseListings as AnimalListingFull[]).filter((l) => "breed" in l).map((l) => (l as AnimalListingFull).breed)
  )];
  const allCities = [...new Set(baseListings.map((l) => l.city))].sort();
  const allBadges = [...new Set(baseListings.map((l) => l.badge).filter(Boolean) as string[])];

  const filteredListings = useMemo(() => {
    let list = [...baseListings];

    if (searchQuery) {
      const q = searchQuery.toLowerCase();
      list = list.filter((l) =>
        l.title.toLowerCase().includes(q) ||
        l.seller.toLowerCase().includes(q) ||
        l.type.toLowerCase().includes(q) ||
        l.city.toLowerCase().includes(q) ||
        ("breed" in l && (l as AnimalListingFull).breed.toLowerCase().includes(q))
      );
    }

    if (filters.type) list = list.filter((l) => l.type === filters.type);
    if (filters.breed) list = list.filter((l) => "breed" in l && (l as AnimalListingFull).breed === filters.breed);
    if (filters.city) list = list.filter((l) => l.city === filters.city);
    if (filters.priceMin) list = list.filter((l) => l.price >= Number(filters.priceMin));
    if (filters.priceMax) list = list.filter((l) => l.price <= Number(filters.priceMax));
    if (filters.weightMin) list = list.filter((l) => l.weight >= Number(filters.weightMin));
    if (filters.weightMax) list = list.filter((l) => l.weight <= Number(filters.weightMax));
    if (filters.verifiedOnly) list = list.filter((l) => l.verified);
    if (filters.badges.length > 0) list = list.filter((l) => l.badge && filters.badges.includes(l.badge));

    switch (sortBy) {
      case "lowest":       list.sort((a, b) => a.price - b.price); break;
      case "highest":      list.sort((a, b) => b.price - a.price); break;
      case "rating":       list.sort((a, b) => b.rating - a.rating); break;
      case "mostViewed":   list.sort((a, b) => b.viewCount - a.viewCount); break;
      case "mostFavorited":list.sort((a, b) => b.favoriteCount - a.favoriteCount); break;
      case "heaviest":     list.sort((a, b) => b.weight - a.weight); break;
      case "lightest":     list.sort((a, b) => a.weight - b.weight); break;
    }

    return list;
  }, [baseListings, searchQuery, filters, sortBy]);

  const activeFilterCount = [
    filters.type, filters.breed, filters.city, filters.priceMin, filters.priceMax,
    filters.weightMin, filters.weightMax, filters.verifiedOnly ? "v" : "", ...filters.badges,
  ].filter(Boolean).length;

  const currentSortLabel = sortOptions.find((s) => s.key === sortBy)?.label || "Sırala";

  const toggleFavorite = (id: string) =>
    setFavorites((prev) => prev.includes(id) ? prev.filter((f) => f !== id) : [...prev, id]);

  const removeFilter = (key: keyof Filters | "badge", value?: string) => {
    if (key === "badge" && value) {
      setFilters((prev) => ({ ...prev, badges: prev.badges.filter((b) => b !== value) }));
    } else if (key === "priceMin") {
      setFilters((prev) => ({ ...prev, priceMin: "", priceMax: "" }));
    } else if (key === "weightMin") {
      setFilters((prev) => ({ ...prev, weightMin: "", weightMax: "" }));
    } else if (key === "verifiedOnly") {
      setFilters((prev) => ({ ...prev, verifiedOnly: false }));
    } else {
      setFilters((prev) => ({ ...prev, [key]: "" }));
    }
  };

  const clearAllFilters = () => setFilters({ ...DEFAULT_FILTERS });

  return (
    <div className="min-h-screen bg-background pb-24">
      <TopBar
        showLogo
        subtitle={isSlaughterhouse ? "Hayvan İlanları" : "Et İlanları"}
        action={
          <button
            onClick={() => setShowFilterSheet(true)}
            className="relative p-2 hover:bg-muted rounded-xl transition-colors"
          >
            <SlidersHorizontal className="size-5 text-foreground" />
            {activeFilterCount > 0 && (
              <span className="absolute -top-0.5 -right-0.5 size-4 bg-primary text-white text-[10px] font-bold rounded-full flex items-center justify-center">
                {activeFilterCount}
              </span>
            )}
          </button>
        }
      />

      {/* Sticky search + controls */}
      <div className="sticky top-14 bg-background/95 backdrop-blur-sm z-30 border-b border-border/50 space-y-2 px-4 pt-3 pb-2">
        <div className="relative max-w-md mx-auto">
          <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 size-4 text-muted-foreground" />
          <input
            type="text"
            placeholder={isSlaughterhouse ? "Irk, şehir veya satıcı ara..." : "Et türü, şehir veya satıcı ara..."}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full h-11 bg-white border border-border rounded-xl pl-10 pr-9 text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring"
          />
          {searchQuery && (
            <button onClick={() => setSearchQuery("")} className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground">
              <X className="size-4" />
            </button>
          )}
        </div>

        {/* Type chips + Sort button */}
        <div className="max-w-md mx-auto flex items-center gap-2 overflow-x-auto no-scrollbar pb-0.5">
          {/* Tümü chip */}
          <button
            onClick={() => setFilters((prev) => ({ ...prev, type: "", breed: "" }))}
            className={cn(
              "flex-shrink-0 px-3 py-1.5 rounded-xl text-sm font-medium transition-all border",
              filters.type === ""
                ? "bg-primary text-white border-primary"
                : "bg-white text-muted-foreground border-border hover:border-primary/50"
            )}
          >
            Tümü
          </button>
          {allTypes.map((type) => (
            <button
              key={type}
              onClick={() => setFilters((prev) => ({ ...prev, type: prev.type === type ? "" : type, breed: "" }))}
              className={cn(
                "flex-shrink-0 px-3 py-1.5 rounded-xl text-sm font-medium transition-all border",
                filters.type === type
                  ? "bg-primary text-white border-primary"
                  : "bg-white text-muted-foreground border-border hover:border-primary/50"
              )}
            >
              {type}
            </button>
          ))}

          {/* Sort button */}
          <button
            onClick={() => setShowSortSheet(true)}
            className={cn(
              "flex-shrink-0 ml-auto flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold border transition-all whitespace-nowrap",
              sortBy !== "newest"
                ? "bg-primary/10 text-primary border-primary/30"
                : "bg-white text-muted-foreground border-border hover:border-primary/50"
            )}
          >
            <ArrowUpDown className="size-3.5" />
            {currentSortLabel}
          </button>
        </div>

        {/* Active filter pills */}
        {activeFilterCount > 0 && (
          <div className="max-w-md mx-auto">
            <ActiveFilterPills
              filters={filters}
              sortBy={sortBy}
              onRemove={removeFilter}
              onClearAll={clearAllFilters}
            />
          </div>
        )}
      </div>

      {/* Result info */}
      <div className="max-w-md mx-auto px-4 py-2.5 flex items-center justify-between">
        <p className="text-xs text-muted-foreground">
          <span className="font-semibold text-foreground">{filteredListings.length}</span> ilan bulundu
          {filters.city && ` · ${filters.city}`}
        </p>
        {activeFilterCount > 0 && (
          <button
            onClick={clearAllFilters}
            className="text-xs text-primary font-semibold flex items-center gap-1 hover:underline"
          >
            <RotateCcw className="size-3" />
            Filtreleri Temizle
          </button>
        )}
      </div>

      {/* Listings */}
      <div className="max-w-md mx-auto px-4 space-y-3 pb-4">
        {filteredListings.length === 0 ? (
          <div className="text-center py-16">
            <div className="size-20 bg-muted rounded-full flex items-center justify-center mx-auto mb-4">
              <Search className="size-8 text-muted-foreground" />
            </div>
            <h3 className="font-semibold text-foreground mb-1">İlan bulunamadı</h3>
            <p className="text-sm text-muted-foreground mb-4">Arama kriterlerinizi değiştirmeyi deneyin</p>
            <button
              onClick={() => { setSearchQuery(""); clearAllFilters(); }}
              className="inline-flex items-center gap-2 px-4 py-2 rounded-xl border-2 border-border text-sm font-medium text-foreground hover:bg-muted transition-colors"
            >
              <RotateCcw className="size-4" />
              Filtreleri Temizle
            </button>
          </div>
        ) : (
          filteredListings.map((listing) => (
            <ListingCard
              key={listing.id}
              listing={listing}
              isFav={favorites.includes(listing.id)}
              onFav={() => toggleFavorite(listing.id)}
              onOffer={() => setOfferListing(listing)}
              onClick={() => navigate(`/listing/${listing.id}`)}
            />
          ))
        )}
      </div>

      <BottomNav />

      {offerListing && <OfferModal listing={offerListing} onClose={() => setOfferListing(null)} />}

      {showSortSheet && (
        <SortSheet current={sortBy} onChange={setSortBy} onClose={() => setShowSortSheet(false)} />
      )}

      {showFilterSheet && (
        <FilterSheet
          filters={filters}
          types={allTypes}
          breeds={allBreeds}
          cities={allCities}
          badges={allBadges}
          isAnimal={isSlaughterhouse}
          onApply={(f) => setFilters(f)}
          onClose={() => setShowFilterSheet(false)}
        />
      )}
    </div>
  );
}
