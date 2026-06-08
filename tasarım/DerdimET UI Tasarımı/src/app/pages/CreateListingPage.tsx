import { useState } from "react";
import { TopBar } from "../components/layout/TopBar";
import { BottomNav } from "../components/layout/BottomNav";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router";
import {
  CheckCircle2, X, Plus, Info, MapPin, Scale, Tag, FileText,
  Image as ImageIcon, ChevronRight, Star, BadgeCheck, Shield,
  Package, Beef, Eye, Heart, TrendingUp, Sparkles, Camera,
  ArrowLeft, CheckCheck
} from "lucide-react";
import { cn } from "../../lib/utils";

// ---- Constants ----
const cities = [
  "İstanbul", "Ankara", "İzmir", "Bursa", "Antalya", "Adana", "Konya",
  "Gaziantep", "Şanlıurfa", "Mersin", "Diyarbakır", "Kayseri", "Samsun",
  "Eskişehir", "Denizli", "Trabzon", "Erzurum", "Malatya", "Balıkesir",
  "Edirne", "Manisa", "Aydın",
];

const meatTypes = [
  { value: "Dana", emoji: "🐄", desc: "Dana/Sığır eti" },
  { value: "Kuzu", emoji: "🐑", desc: "Kuzu eti" },
  { value: "Koyun", emoji: "🐏", desc: "Koyun eti" },
  { value: "Keçi", emoji: "🐐", desc: "Keçi eti" },
];

const animalCategories = [
  { value: "Küçükbaş", emoji: "🐑", desc: "Koyun, kuzu, keçi" },
  { value: "Büyükbaş", emoji: "🐄", desc: "İnek, boğa, dana" },
];

const animalBreeds: Record<string, string[]> = {
  "Küçükbaş": ["Merinos", "Kıvırcık", "Akkaraman", "İvesi", "Saanen", "Ankara Keçisi", "Dağlıç", "Norduz"],
  "Büyükbaş": ["Simental", "Holstein", "Angus", "Hereford", "Montofon", "Charolais", "Limousin", "Yerli Kara"],
};

const badgeOptions = [
  { value: null, label: "Rozet Yok", color: "bg-muted/60 text-muted-foreground border-border" },
  { value: "Yeni", label: "🆕 Yeni İlan", color: "bg-blue-50 text-blue-700 border-blue-200" },
  { value: "Premium", label: "⭐ Premium", color: "bg-purple-50 text-purple-700 border-purple-200" },
  { value: "Çok Satan", label: "🔥 Çok Satan", color: "bg-orange-50 text-orange-700 border-orange-200" },
  { value: "Toplu Satış", label: "📦 Toplu Satış", color: "bg-teal-50 text-teal-700 border-teal-200" },
  { value: "Sertifikalı", label: "✅ Sertifikalı", color: "bg-green-50 text-green-700 border-green-200" },
  { value: "Damızlık", label: "🏆 Damızlık", color: "bg-rose-50 text-rose-700 border-rose-200" },
];

const typeColors: Record<string, string> = {
  Dana: "bg-blue-100 text-blue-700",
  Kuzu: "bg-amber-100 text-amber-700",
  Koyun: "bg-purple-100 text-purple-700",
  Keçi: "bg-emerald-100 text-emerald-700",
  Küçükbaş: "bg-orange-100 text-orange-700",
  Büyükbaş: "bg-red-100 text-red-700",
};

const typeStrip: Record<string, string> = {
  Dana: "bg-blue-400",
  Kuzu: "bg-amber-400",
  Koyun: "bg-purple-400",
  Keçi: "bg-emerald-400",
  Küçükbaş: "bg-orange-400",
  Büyükbaş: "bg-red-400",
};

const badgeColorMap: Record<string, string> = {
  "Çok Satan": "bg-orange-100 text-orange-700",
  "Premium": "bg-purple-100 text-purple-700",
  "Yeni": "bg-blue-100 text-blue-700",
  "Toplu Satış": "bg-teal-100 text-teal-700",
  "Sertifikalı": "bg-green-100 text-green-700",
  "Damızlık": "bg-rose-100 text-rose-700",
};

// ---- Placeholder photos (demo) ----
const demoPhotos = [
  "https://images.unsplash.com/photo-1777962822492-c0d637951f24?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
  "https://images.unsplash.com/photo-1740586222627-48338edac67d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
  "https://images.unsplash.com/photo-1719502044471-2187a7d36a81?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
  "https://images.unsplash.com/photo-1651945846830-1fe022473668?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
  "https://images.unsplash.com/photo-1762571808926-2555640f12a6?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
];

interface FormData {
  title: string;
  type: string;
  breed: string;
  category: string;
  quantity: string;
  weight: string;
  price: string;
  city: string;
  district: string;
  description: string;
  minOrder: string;
  badge: string | null;
}

// ---- Field Component ----
function Field({ label, hint, children }: { label: string; hint?: string; children: React.ReactNode }) {
  return (
    <div className="space-y-1.5">
      <label className="block text-sm font-semibold text-foreground">{label}</label>
      {children}
      {hint && <p className="text-xs text-muted-foreground">{hint}</p>}
    </div>
  );
}

// ---- Section Card ----
function SectionCard({ icon: Icon, title, children }: {
  icon: React.ElementType;
  title: string;
  children: React.ReactNode;
}) {
  return (
    <div className="bg-white rounded-2xl border border-border shadow-sm overflow-hidden">
      <div className="flex items-center gap-2.5 px-4 py-3 border-b border-border/50 bg-muted/20">
        <div className="size-7 rounded-lg bg-primary/10 flex items-center justify-center">
          <Icon className="size-3.5 text-primary" />
        </div>
        <span className="text-sm font-bold text-foreground">{title}</span>
      </div>
      <div className="p-4 space-y-4">{children}</div>
    </div>
  );
}

// ---- Preview Card ----
function PreviewCard({ formData, isSlaughterhouse, photos, user }: {
  formData: FormData;
  isSlaughterhouse: boolean;
  photos: string[];
  user: any;
}) {
  const typeLabel = isSlaughterhouse ? formData.type : formData.category;
  const displayTitle = formData.title || (isSlaughterhouse
    ? `${formData.type || "Et"} – Yeni İlan`
    : `${formData.breed ? formData.breed + " " : ""}${formData.category || "Hayvan"} – Satılık`);

  return (
    <div className="bg-white rounded-2xl border border-border shadow-sm overflow-hidden">
      {/* Color strip */}
      <div className={cn("h-1.5 w-full", typeStrip[typeLabel] || "bg-primary")} />

      {/* Photo preview */}
      {photos.length > 0 ? (
        <div className="relative w-full aspect-[16/9] bg-gray-100 overflow-hidden">
          <img src={photos[0]} alt="Önizleme" className="w-full h-full object-cover" />
          {photos.length > 1 && (
            <div className="absolute bottom-2 right-2 bg-black/50 text-white text-xs px-2 py-0.5 rounded-full">
              +{photos.length - 1} fotoğraf
            </div>
          )}
        </div>
      ) : (
        <div className="w-full aspect-[16/9] bg-muted/30 flex flex-col items-center justify-center gap-2">
          <Camera className="size-8 text-muted-foreground/40" />
          <span className="text-xs text-muted-foreground">Fotoğraf önizlemesi</span>
        </div>
      )}

      <div className="p-4">
        {/* Header */}
        <div className="flex items-start gap-3 mb-3">
          <div className={cn("size-10 rounded-xl flex items-center justify-center flex-shrink-0 text-sm font-bold",
            typeColors[typeLabel] || "bg-muted text-muted-foreground")}>
            {typeLabel.charAt(0) || "?"}
          </div>
          <div className="flex-1 min-w-0">
            <div className="flex items-start justify-between gap-2">
              <div>
                {formData.badge && (
                  <span className={cn("inline-block mb-1 text-[10px] font-semibold px-2 py-0.5 rounded-full",
                    badgeColorMap[formData.badge] || "bg-muted text-muted-foreground")}>
                    {formData.badge}
                  </span>
                )}
                <h3 className="font-semibold text-foreground text-sm leading-tight">{displayTitle}</h3>
              </div>
            </div>
            <div className="flex items-center gap-2 mt-1">
              <span className="text-xs text-muted-foreground">{user?.companyName || user?.name || "Şirket Adı"}</span>
              <span className="flex items-center gap-0.5 text-xs text-amber-600">
                <Star className="size-3 fill-amber-400 text-amber-400" />
                4.8
              </span>
            </div>
          </div>
        </div>

        {/* Details */}
        <div className="grid grid-cols-2 gap-2 mb-3">
          <div className="bg-muted/50 rounded-xl p-2.5">
            <p className="text-[11px] text-muted-foreground mb-0.5">
              {isSlaughterhouse ? "Tür" : "Irk / Kategori"}
            </p>
            <p className="text-sm font-semibold text-foreground">
              {isSlaughterhouse ? (formData.type || "—") : (formData.breed ? `${formData.breed} ${formData.category}` : formData.category || "—")}
            </p>
          </div>
          <div className="bg-muted/50 rounded-xl p-2.5">
            <p className="text-[11px] text-muted-foreground mb-0.5">
              {isSlaughterhouse ? "Miktar" : "Adet"}
            </p>
            <p className="text-sm font-semibold text-foreground">
              {formData.quantity ? `${formData.quantity} ${isSlaughterhouse ? "kg" : "adet"}` : "—"}
            </p>
          </div>
        </div>

        {/* Location + price */}
        <div className="flex items-center gap-3 mb-3 text-xs text-muted-foreground">
          <div className="flex items-center gap-1">
            <MapPin className="size-3.5" />
            {formData.district && formData.city
              ? `${formData.district}, ${formData.city}`
              : formData.city || "Konum belirtilmedi"}
          </div>
        </div>

        <div className="flex items-center justify-between pt-3 border-t border-border">
          <div>
            <p className="text-[11px] text-muted-foreground">Fiyat</p>
            <p className="font-bold text-primary">
              {formData.price ? `${Number(formData.price).toLocaleString("tr-TR")} ₺` : "—"}
              <span className="text-xs font-normal text-muted-foreground">
                /{isSlaughterhouse ? "kg" : "adet"}
              </span>
            </p>
          </div>
          <div className="flex items-center gap-1 text-xs text-muted-foreground">
            <Eye className="size-3.5" />
            <span>Önizleme</span>
          </div>
        </div>
      </div>
    </div>
  );
}

// ---- Step Indicator ----
function StepIndicator({ steps, active }: { steps: string[]; active: number }) {
  return (
    <div className="bg-white border-b border-border px-4 py-3 sticky top-14 z-30">
      <div className="max-w-md mx-auto flex items-center">
        {steps.map((step, i) => (
          <div key={i} className="flex items-center flex-1">
            <div className="flex flex-col items-center">
              <div className={cn(
                "size-8 rounded-full flex items-center justify-center text-sm font-bold transition-all",
                i + 1 === active
                  ? "bg-primary text-white ring-4 ring-primary/20"
                  : i + 1 < active
                  ? "bg-green-500 text-white"
                  : "bg-muted text-muted-foreground"
              )}>
                {i + 1 < active ? <CheckCircle2 className="size-4" /> : i + 1}
              </div>
              <span className={cn(
                "text-[10px] mt-1 font-medium whitespace-nowrap",
                i + 1 === active ? "text-primary" : "text-muted-foreground"
              )}>
                {step}
              </span>
            </div>
            {i < steps.length - 1 && (
              <div className={cn(
                "flex-1 h-0.5 mx-2 mb-4 transition-colors",
                i + 1 < active ? "bg-green-500" : "bg-border"
              )} />
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

// ---- Main Component ----
export function CreateListingPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [photos, setPhotos] = useState<string[]>([]);
  const [activeStep, setActiveStep] = useState(1);

  const isSlaughterhouse = user?.role === "SLAUGHTERHOUSE";

  const [formData, setFormData] = useState<FormData>({
    title: "",
    type: "",
    breed: "",
    category: "",
    quantity: "",
    weight: "",
    price: "",
    city: user?.city || "",
    district: "",
    description: "",
    minOrder: "",
    badge: null,
  });

  const update = (key: keyof FormData, value: string | null) => {
    if (key === "category") {
      setFormData((prev) => ({ ...prev, category: value as string, breed: "" }));
    } else {
      setFormData((prev) => ({ ...prev, [key]: value }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    await new Promise((resolve) => setTimeout(resolve, 1400));
    setLoading(false);
    setSuccess(true);
    setTimeout(() => navigate("/search"), 2800);
  };

  const addDemoPhoto = () => {
    if (photos.length < 5) {
      setPhotos((prev) => [...prev, demoPhotos[prev.length % demoPhotos.length]]);
    }
  };

  const steps = ["Ürün Bilgileri", "Miktar & Konum", "Detaylar & Önizleme"];

  // ---- Validation ----
  const step1Valid = isSlaughterhouse
    ? !!formData.type && !!formData.title
    : !!formData.category && !!formData.breed && !!formData.title;

  const step2Valid = !!formData.price && !!formData.quantity && !!formData.city && !!formData.weight;

  // ---- Success Screen ----
  if (success) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center p-6">
        <div className="text-center max-w-xs">
          <div className="relative size-28 mx-auto mb-6">
            <div className="size-28 bg-green-100 rounded-3xl flex items-center justify-center border-4 border-green-200">
              <CheckCheck className="size-14 text-green-600" />
            </div>
            <div className="absolute -top-2 -right-2 size-8 bg-amber-400 rounded-full flex items-center justify-center">
              <Sparkles className="size-4 text-white" />
            </div>
          </div>
          <h2 className="font-bold text-foreground text-xl mb-2">İlan Yayınlandı! 🎉</h2>
          <p className="text-muted-foreground text-sm mb-2">
            <span className="font-semibold text-foreground">&ldquo;{formData.title}&rdquo;</span> ilanınız başarıyla oluşturuldu.
          </p>
          <p className="text-muted-foreground text-xs mb-6">Alıcılar ilanınızı görmeye başlayabilir.</p>

          <div className="bg-muted/50 rounded-2xl p-4 mb-6 text-left space-y-2">
            <div className="flex justify-between text-sm">
              <span className="text-muted-foreground">Fiyat</span>
              <span className="font-bold text-primary">{formData.price} ₺/{isSlaughterhouse ? "kg" : "adet"}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-muted-foreground">Konum</span>
              <span className="font-medium">{formData.district ? `${formData.district}, ` : ""}{formData.city}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-muted-foreground">Fotoğraf</span>
              <span className="font-medium">{photos.length} adet</span>
            </div>
          </div>

          <div className="space-y-2">
            <div className="h-1.5 bg-muted rounded-full overflow-hidden">
              <div className="h-full bg-green-500 rounded-full animate-pulse w-full" />
            </div>
            <p className="text-xs text-muted-foreground">İlanlarınıza yönlendiriliyorsunuz...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background pb-28">
      <TopBar title="İlan Ver" showBack />
      <StepIndicator steps={steps} active={activeStep} />

      <div className="max-w-md mx-auto px-4 py-4">
        <form onSubmit={handleSubmit} className="space-y-4">

          {/* ==================== STEP 1 ==================== */}
          {activeStep === 1 && (
            <>
              {/* ET / HAYVAN TÜRÜ */}
              <SectionCard icon={Tag} title={isSlaughterhouse ? "Et Türü" : "Hayvan Kategorisi"}>
                {isSlaughterhouse ? (
                  <div className="grid grid-cols-2 gap-2.5">
                    {meatTypes.map(({ value, emoji, desc }) => (
                      <button
                        key={value}
                        type="button"
                        onClick={() => update("type", value)}
                        className={cn(
                          "p-3 rounded-xl border-2 text-left transition-all",
                          formData.type === value
                            ? "bg-primary/5 border-primary"
                            : "bg-white border-border hover:border-primary/40"
                        )}
                      >
                        <span className="text-xl block mb-1">{emoji}</span>
                        <span className={cn("text-sm font-semibold block", formData.type === value ? "text-primary" : "text-foreground")}>
                          {value}
                        </span>
                        <span className="text-[11px] text-muted-foreground">{desc}</span>
                      </button>
                    ))}
                  </div>
                ) : (
                  <>
                    <div className="grid grid-cols-2 gap-2.5">
                      {animalCategories.map(({ value, emoji, desc }) => (
                        <button
                          key={value}
                          type="button"
                          onClick={() => update("category", value)}
                          className={cn(
                            "p-3 rounded-xl border-2 text-left transition-all",
                            formData.category === value
                              ? "bg-primary/5 border-primary"
                              : "bg-white border-border hover:border-primary/40"
                          )}
                        >
                          <span className="text-xl block mb-1">{emoji}</span>
                          <span className={cn("text-sm font-semibold block", formData.category === value ? "text-primary" : "text-foreground")}>
                            {value}
                          </span>
                          <span className="text-[11px] text-muted-foreground">{desc}</span>
                        </button>
                      ))}
                    </div>

                    {/* IRKI */}
                    {formData.category && (
                      <div className="space-y-2">
                        <label className="block text-sm font-semibold text-foreground">Irk Seçin</label>
                        <div className="grid grid-cols-2 gap-2">
                          {(animalBreeds[formData.category] || []).map((breed) => (
                            <button
                              key={breed}
                              type="button"
                              onClick={() => update("breed", breed)}
                              className={cn(
                                "py-2.5 px-3 rounded-xl border-2 text-sm font-medium transition-all text-left",
                                formData.breed === breed
                                  ? "bg-primary text-white border-primary"
                                  : "bg-white text-foreground border-border hover:border-primary/40"
                              )}
                            >
                              {breed}
                            </button>
                          ))}
                        </div>
                      </div>
                    )}
                  </>
                )}
              </SectionCard>

              {/* BAŞLIK */}
              <SectionCard icon={FileText} title="İlan Başlığı">
                <Field
                  label=""
                  hint={isSlaughterhouse
                    ? "Örn: Dana Eti – Taze Karkas, 450 kg Stok"
                    : "Örn: Merinos Koyun – 25 Baş Sürü Satılık"}
                >
                  <input
                    value={formData.title}
                    onChange={(e) => update("title", e.target.value)}
                    placeholder={isSlaughterhouse
                      ? "Dana Eti – Taze Karkas"
                      : "Merinos Koyun – Sürü Satılık"}
                    maxLength={80}
                    className="w-full h-12 rounded-xl border-2 border-border bg-white px-4 text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:border-primary transition-colors"
                  />
                  <div className="flex justify-end mt-1">
                    <span className="text-xs text-muted-foreground">{formData.title.length}/80</span>
                  </div>
                </Field>
              </SectionCard>

              {/* ROZET */}
              <SectionCard icon={Sparkles} title="Rozet (Opsiyonel)">
                <p className="text-xs text-muted-foreground -mt-2">İlanınızı öne çıkarmak için rozet seçin</p>
                <div className="grid grid-cols-2 gap-2">
                  {badgeOptions.map(({ value, label, color }) => (
                    <button
                      key={label}
                      type="button"
                      onClick={() => update("badge", value)}
                      className={cn(
                        "py-2.5 px-3 rounded-xl border-2 text-xs font-semibold transition-all text-left",
                        formData.badge === value
                          ? "ring-2 ring-primary ring-offset-1 " + color
                          : color + " hover:opacity-80"
                      )}
                    >
                      {label}
                    </button>
                  ))}
                </div>
              </SectionCard>

              {/* NEXT */}
              <button
                type="button"
                onClick={() => setActiveStep(2)}
                disabled={!step1Valid}
                className={cn(
                  "w-full h-13 rounded-xl font-semibold flex items-center justify-center gap-2 transition-all py-3.5",
                  step1Valid
                    ? "bg-primary text-white hover:bg-primary/90 shadow-md shadow-primary/20"
                    : "bg-muted text-muted-foreground cursor-not-allowed"
                )}
              >
                Devam Et
                <ChevronRight className="size-4" />
              </button>
            </>
          )}

          {/* ==================== STEP 2 ==================== */}
          {activeStep === 2 && (
            <>
              {/* MİKTAR & AĞIRLIK & FİYAT */}
              <SectionCard icon={Scale} title="Miktar, Ağırlık & Fiyat">
                <div className="grid grid-cols-2 gap-3">
                  <Field label={isSlaughterhouse ? "Stok Miktarı (kg)" : "Adet"}>
                    <div className="relative">
                      <input
                        type="number"
                        value={formData.quantity}
                        onChange={(e) => update("quantity", e.target.value)}
                        placeholder="0"
                        min="1"
                        className="w-full h-11 rounded-xl border-2 border-border bg-white px-4 pr-10 text-sm text-foreground focus:outline-none focus:border-primary transition-colors"
                        required
                      />
                      <span className="absolute right-3 top-1/2 -translate-y-1/2 text-xs text-muted-foreground">
                        {isSlaughterhouse ? "kg" : "adet"}
                      </span>
                    </div>
                  </Field>
                  <Field label={isSlaughterhouse ? "Ort. Karkas (kg)" : "Ort. Ağırlık (kg)"}>
                    <div className="relative">
                      <input
                        type="number"
                        value={formData.weight}
                        onChange={(e) => update("weight", e.target.value)}
                        placeholder="0"
                        min="1"
                        className="w-full h-11 rounded-xl border-2 border-border bg-white px-4 pr-8 text-sm text-foreground focus:outline-none focus:border-primary transition-colors"
                        required
                      />
                      <span className="absolute right-3 top-1/2 -translate-y-1/2 text-xs text-muted-foreground">kg</span>
                    </div>
                  </Field>
                </div>

                <Field label={`Satış Fiyatı (₺/${isSlaughterhouse ? "kg" : "adet"})`}>
                  <div className="relative">
                    <input
                      type="number"
                      value={formData.price}
                      onChange={(e) => update("price", e.target.value)}
                      placeholder="0"
                      min="1"
                      className="w-full h-12 rounded-xl border-2 border-primary bg-white pl-4 pr-16 text-foreground font-semibold focus:outline-none focus:ring-2 focus:ring-primary/20 transition-colors"
                      required
                    />
                    <div className="absolute right-3 top-1/2 -translate-y-1/2 flex items-center gap-1">
                      <span className="text-sm font-semibold text-primary">₺</span>
                      <span className="text-xs text-muted-foreground">/{isSlaughterhouse ? "kg" : "adet"}</span>
                    </div>
                  </div>
                  {/* Total calculation */}
                  {formData.price && formData.quantity && (
                    <div className="bg-primary/5 rounded-xl px-3 py-2 flex items-center justify-between">
                      <span className="text-xs text-muted-foreground">Toplam değer</span>
                      <span className="text-sm font-bold text-primary">
                        {(Number(formData.price) * Number(formData.quantity)).toLocaleString("tr-TR")} ₺
                      </span>
                    </div>
                  )}
                </Field>

                <Field label="Minimum Sipariş (opsiyonel)">
                  <div className="relative">
                    <input
                      type="number"
                      value={formData.minOrder}
                      onChange={(e) => update("minOrder", e.target.value)}
                      placeholder={isSlaughterhouse ? "Örn: 50" : "Örn: 5"}
                      className="w-full h-11 rounded-xl border-2 border-border bg-white px-4 pr-10 text-sm text-foreground focus:outline-none focus:border-primary transition-colors"
                    />
                    <span className="absolute right-3 top-1/2 -translate-y-1/2 text-xs text-muted-foreground">
                      {isSlaughterhouse ? "kg" : "adet"}
                    </span>
                  </div>
                </Field>
              </SectionCard>

              {/* KONUM */}
              <SectionCard icon={MapPin} title="Konum">
                <Field label="Şehir" >
                  <select
                    className="w-full h-11 rounded-xl border-2 border-border bg-white px-4 text-sm text-foreground focus:outline-none focus:border-primary transition-colors appearance-none"
                    value={formData.city}
                    onChange={(e) => update("city", e.target.value)}
                    required
                  >
                    <option value="">Şehir seçin</option>
                    {cities.map((c) => <option key={c} value={c}>{c}</option>)}
                  </select>
                </Field>

                <Field label="İlçe" hint="İlçe belirtmek alıcıların daha kolay ulaşmasını sağlar">
                  <input
                    value={formData.district}
                    onChange={(e) => update("district", e.target.value)}
                    placeholder="Örn: Bornova, Selçuklu, Karatay..."
                    className="w-full h-11 rounded-xl border-2 border-border bg-white px-4 text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:border-primary transition-colors"
                  />
                </Field>

                {formData.city && formData.district && (
                  <div className="flex items-center gap-2 bg-green-50 rounded-xl px-3 py-2.5">
                    <MapPin className="size-4 text-green-600" />
                    <span className="text-sm font-medium text-green-700">
                      {formData.district}, {formData.city}
                    </span>
                  </div>
                )}
              </SectionCard>

              {/* NAV */}
              <div className="flex gap-3">
                <button
                  type="button"
                  onClick={() => setActiveStep(1)}
                  className="flex-1 h-12 rounded-xl border-2 border-border text-foreground font-semibold flex items-center justify-center gap-2 hover:bg-muted transition-colors"
                >
                  <ArrowLeft className="size-4" />
                  Geri
                </button>
                <button
                  type="button"
                  onClick={() => setActiveStep(3)}
                  disabled={!step2Valid}
                  className={cn(
                    "flex-1 h-12 rounded-xl font-semibold flex items-center justify-center gap-2 transition-all",
                    step2Valid
                      ? "bg-primary text-white hover:bg-primary/90 shadow-md shadow-primary/20"
                      : "bg-muted text-muted-foreground cursor-not-allowed"
                  )}
                >
                  Devam Et
                  <ChevronRight className="size-4" />
                </button>
              </div>
            </>
          )}

          {/* ==================== STEP 3 ==================== */}
          {activeStep === 3 && (
            <>
              {/* AÇIKLAMA */}
              <SectionCard icon={FileText} title="İlan Açıklaması">
                <div className="space-y-2">
                  <textarea
                    className="w-full min-h-[130px] rounded-xl border-2 border-border bg-white px-4 py-3 text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:border-primary resize-none transition-colors"
                    value={formData.description}
                    onChange={(e) => update("description", e.target.value)}
                    placeholder={isSlaughterhouse
                      ? "Veteriner sertifikası, soğuk zincir, teslimat koşulları, toplu alım indirimi gibi detayları yazın..."
                      : "Hayvanın yaşı, sağlık durumu, aşı bilgileri, canlı ağırlık, teslimat koşulları, parça satış gibi bilgileri ekleyin..."}
                    maxLength={600}
                  />
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                      <Info className="size-3 flex-shrink-0" />
                      Detaylı açıklama daha fazla alıcı çeker
                    </div>
                    <span className={cn("text-xs font-medium",
                      formData.description.length > 500 ? "text-amber-600" : "text-muted-foreground")}>
                      {formData.description.length}/600
                    </span>
                  </div>
                </div>
              </SectionCard>

              {/* FOTOĞRAFLAR */}
              <SectionCard icon={ImageIcon} title={`Fotoğraflar (${photos.length}/5)`}>
                <div className="grid grid-cols-3 gap-2">
                  {photos.map((src, i) => (
                    <div key={i} className="relative aspect-square bg-muted rounded-xl overflow-hidden group">
                      <img src={src} alt={`Fotoğraf ${i + 1}`} className="w-full h-full object-cover" />
                      {i === 0 && (
                        <div className="absolute bottom-0 left-0 right-0 bg-black/50 text-white text-[10px] text-center py-0.5">
                          Kapak
                        </div>
                      )}
                      <button
                        type="button"
                        onClick={() => setPhotos(photos.filter((_, pi) => pi !== i))}
                        className="absolute top-1.5 right-1.5 size-5 bg-black/60 rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
                      >
                        <X className="size-3 text-white" />
                      </button>
                    </div>
                  ))}
                  {photos.length < 5 && (
                    <button
                      type="button"
                      onClick={addDemoPhoto}
                      className="aspect-square rounded-xl border-2 border-dashed border-border hover:border-primary bg-muted/30 hover:bg-primary/5 transition-all flex flex-col items-center justify-center gap-1 text-muted-foreground hover:text-primary"
                    >
                      <Camera className="size-6" />
                      <span className="text-[10px] font-medium">Ekle</span>
                    </button>
                  )}
                </div>

                <div className="bg-blue-50 rounded-xl p-3 flex items-start gap-2">
                  <Info className="size-4 text-blue-600 flex-shrink-0 mt-0.5" />
                  <div className="text-xs text-blue-700">
                    <span className="font-semibold">İpucu:</span> Fotoğraf ekleyen ilanlar %60 daha fazla görüntüleniyor.
                    İlk fotoğraf kapak görseli olarak kullanılır.
                  </div>
                </div>
              </SectionCard>

              {/* PREVIEW */}
              <div>
                <div className="flex items-center gap-2 mb-3">
                  <Eye className="size-4 text-primary" />
                  <span className="text-sm font-bold text-foreground">İlan Önizlemesi</span>
                  <span className="text-xs text-muted-foreground">(alıcılar böyle görecek)</span>
                </div>
                <PreviewCard
                  formData={formData}
                  isSlaughterhouse={isSlaughterhouse}
                  photos={photos}
                  user={user}
                />
              </div>

              {/* FULL SUMMARY */}
              <div className="bg-primary/5 rounded-2xl border border-primary/20 p-4">
                <p className="text-sm font-bold text-primary mb-3 flex items-center gap-2">
                  <CheckCheck className="size-4" />
                  İlan Özeti
                </p>
                <div className="space-y-2.5">
                  {[
                    { label: "İlan Başlığı", value: formData.title },
                    {
                      label: isSlaughterhouse ? "Et Türü" : "Kategori / Irk",
                      value: isSlaughterhouse
                        ? formData.type
                        : `${formData.category}${formData.breed ? " – " + formData.breed : ""}`
                    },
                    {
                      label: isSlaughterhouse ? "Stok Miktarı" : "Adet",
                      value: `${formData.quantity} ${isSlaughterhouse ? "kg" : "adet"}`
                    },
                    { label: "Ort. Ağırlık", value: `${formData.weight} kg` },
                    {
                      label: "Fiyat",
                      value: `${Number(formData.price).toLocaleString("tr-TR")} ₺/${isSlaughterhouse ? "kg" : "adet"}`,
                      highlight: true
                    },
                    {
                      label: "Konum",
                      value: `${formData.district ? formData.district + ", " : ""}${formData.city}`
                    },
                    { label: "Rozet", value: formData.badge || "Yok" },
                    { label: "Fotoğraf", value: `${photos.length} adet` },
                  ].map(({ label, value, highlight }) => (
                    <div key={label} className="flex items-start justify-between gap-3">
                      <span className="text-sm text-muted-foreground">{label}</span>
                      <span className={cn("text-sm text-right font-medium", highlight ? "font-bold text-primary" : "text-foreground")}>
                        {value || "—"}
                      </span>
                    </div>
                  ))}
                </div>
              </div>

              {/* NAV */}
              <div className="flex gap-3">
                <button
                  type="button"
                  onClick={() => setActiveStep(2)}
                  className="flex-1 h-12 rounded-xl border-2 border-border text-foreground font-semibold flex items-center justify-center gap-2 hover:bg-muted transition-colors"
                >
                  <ArrowLeft className="size-4" />
                  Geri
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="flex-[2] h-12 rounded-xl bg-primary text-white font-bold flex items-center justify-center gap-2 hover:bg-primary/90 shadow-lg shadow-primary/25 transition-all disabled:opacity-70"
                >
                  {loading ? (
                    <>
                      <span className="size-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                      Yayınlanıyor...
                    </>
                  ) : (
                    <>
                      <Sparkles className="size-4" />
                      İlanı Yayınla
                    </>
                  )}
                </button>
              </div>
            </>
          )}
        </form>
      </div>

      <BottomNav />
    </div>
  );
}