import { useState, useRef } from "react";
import { useParams, useNavigate } from "react-router";
import {
  ArrowLeft, Heart, Share2, MapPin, Calendar, Scale, Package, Tag,
  CheckCircle, Star, MessageCircle, BadgeCheck, Eye, ChevronRight,
  X, ChevronLeft, Beef,
  Shield, TrendingUp
} from "lucide-react";
import { cn } from "../../lib/utils";
import { getListingById, type AnyListing, type Review } from "../data/mockListings";
import { useAuth } from "../context/AuthContext";

// ---- Types ----
const typeColors: Record<string, string> = {
  Dana: "bg-blue-100 text-blue-700",
  Kuzu: "bg-amber-100 text-amber-700",
  Koyun: "bg-purple-100 text-purple-700",
  Keçi: "bg-emerald-100 text-emerald-700",
  Küçükbaş: "bg-orange-100 text-orange-700",
  Büyükbaş: "bg-red-100 text-red-700",
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

const typeStrip: Record<string, string> = {
  Dana: "bg-blue-400",
  Kuzu: "bg-amber-400",
  Koyun: "bg-purple-400",
  Keçi: "bg-emerald-400",
  Küçükbaş: "bg-orange-400",
  Büyükbaş: "bg-red-400",
};

// ---- Image Gallery ----
function ImageGallery({ images, title }: { images: string[]; title: string }) {
  const [current, setCurrent] = useState(0);
  const [showFullscreen, setShowFullscreen] = useState(false);
  const touchStartX = useRef<number | null>(null);

  const prev = () => setCurrent((c) => (c === 0 ? images.length - 1 : c - 1));
  const next = () => setCurrent((c) => (c === images.length - 1 ? 0 : c + 1));

  const handleTouchStart = (e: React.TouchEvent) => {
    touchStartX.current = e.touches[0].clientX;
  };
  const handleTouchEnd = (e: React.TouchEvent) => {
    if (touchStartX.current === null) return;
    const diff = touchStartX.current - e.changedTouches[0].clientX;
    if (Math.abs(diff) > 40) diff > 0 ? next() : prev();
    touchStartX.current = null;
  };

  return (
    <>
      <div
        className="relative w-full aspect-[4/3] bg-gray-100 overflow-hidden select-none"
        onTouchStart={handleTouchStart}
        onTouchEnd={handleTouchEnd}
      >
        {images.map((src, i) => (
          <img
            key={i}
            src={src}
            alt={`${title} - ${i + 1}`}
            className={cn(
              "absolute inset-0 w-full h-full object-cover transition-opacity duration-300",
              i === current ? "opacity-100" : "opacity-0"
            )}
          />
        ))}

        {/* Click to fullscreen */}
        <button
          onClick={() => setShowFullscreen(true)}
          className="absolute inset-0 z-10"
          aria-label="Büyüt"
        />

        {/* Nav arrows */}
        {images.length > 1 && (
          <>
            <button
              onClick={(e) => { e.stopPropagation(); prev(); }}
              className="absolute left-3 top-1/2 -translate-y-1/2 z-20 size-8 bg-black/40 backdrop-blur-sm rounded-full flex items-center justify-center text-white hover:bg-black/60 transition-colors"
            >
              <ChevronLeft className="size-4" />
            </button>
            <button
              onClick={(e) => { e.stopPropagation(); next(); }}
              className="absolute right-3 top-1/2 -translate-y-1/2 z-20 size-8 bg-black/40 backdrop-blur-sm rounded-full flex items-center justify-center text-white hover:bg-black/60 transition-colors"
            >
              <ChevronRight className="size-4" />
            </button>
          </>
        )}

        {/* Dot indicators */}
        {images.length > 1 && (
          <div className="absolute bottom-3 left-1/2 -translate-x-1/2 z-20 flex items-center gap-1.5">
            {images.map((_, i) => (
              <button
                key={i}
                onClick={(e) => { e.stopPropagation(); setCurrent(i); }}
                className={cn(
                  "rounded-full transition-all",
                  i === current ? "w-5 h-2 bg-white" : "size-2 bg-white/60"
                )}
              />
            ))}
          </div>
        )}

        {/* Counter badge */}
        <div className="absolute top-3 right-3 z-20 bg-black/50 backdrop-blur-sm text-white text-xs font-medium px-2.5 py-1 rounded-full">
          {current + 1}/{images.length}
        </div>
      </div>

      {/* Thumbnail strip */}
      {images.length > 1 && (
        <div className="flex gap-2 px-4 py-2 overflow-x-auto no-scrollbar">
          {images.map((src, i) => (
            <button
              key={i}
              onClick={() => setCurrent(i)}
              className={cn(
                "flex-shrink-0 size-14 rounded-xl overflow-hidden border-2 transition-all",
                i === current ? "border-primary" : "border-transparent opacity-60"
              )}
            >
              <img src={src} alt="" className="w-full h-full object-cover" />
            </button>
          ))}
        </div>
      )}

      {/* Fullscreen Modal */}
      {showFullscreen && (
        <div className="fixed inset-0 z-[100] bg-black flex flex-col">
          <div className="flex items-center justify-between p-4">
            <span className="text-white/70 text-sm">{current + 1} / {images.length}</span>
            <button onClick={() => setShowFullscreen(false)} className="p-2 text-white">
              <X className="size-5" />
            </button>
          </div>
          <div
            className="flex-1 flex items-center justify-center relative"
            onTouchStart={handleTouchStart}
            onTouchEnd={handleTouchEnd}
          >
            <img
              src={images[current]}
              alt={title}
              className="max-w-full max-h-full object-contain"
            />
            {images.length > 1 && (
              <>
                <button onClick={prev} className="absolute left-3 size-10 bg-white/10 rounded-full flex items-center justify-center text-white">
                  <ChevronLeft className="size-5" />
                </button>
                <button onClick={next} className="absolute right-3 size-10 bg-white/10 rounded-full flex items-center justify-center text-white">
                  <ChevronRight className="size-5" />
                </button>
              </>
            )}
          </div>
          {/* Thumbnails at bottom */}
          <div className="flex gap-2 p-4 overflow-x-auto justify-center">
            {images.map((src, i) => (
              <button key={i} onClick={() => setCurrent(i)}
                className={cn("flex-shrink-0 size-12 rounded-lg overflow-hidden border-2 transition-all",
                  i === current ? "border-white" : "border-transparent opacity-50")}>
                <img src={src} alt="" className="w-full h-full object-cover" />
              </button>
            ))}
          </div>
        </div>
      )}
    </>
  );
}

// ---- Star Rating Display ----
function StarDisplay({ rating, size = "sm" }: { rating: number; size?: "sm" | "lg" }) {
  const stars = [1, 2, 3, 4, 5];
  return (
    <div className="flex items-center gap-0.5">
      {stars.map((s) => (
        <Star
          key={s}
          className={cn(
            size === "sm" ? "size-3.5" : "size-5",
            s <= Math.floor(rating) ? "fill-amber-400 text-amber-400" :
            s === Math.ceil(rating) && rating % 1 > 0 ? "fill-amber-200 text-amber-400" :
            "fill-muted text-muted"
          )}
        />
      ))}
    </div>
  );
}

// ---- Rating Breakdown Bar ----
function RatingBar({ label, value, total }: { label: string; value: number; total: number }) {
  const pct = total > 0 ? (value / total) * 100 : 0;
  return (
    <div className="flex items-center gap-2">
      <span className="text-xs text-muted-foreground w-3">{label}</span>
      <div className="flex-1 h-1.5 bg-muted rounded-full overflow-hidden">
        <div className="h-full bg-amber-400 rounded-full transition-all" style={{ width: `${pct}%` }} />
      </div>
      <span className="text-xs text-muted-foreground w-3 text-right">{value}</span>
    </div>
  );
}

// ---- Offer Modal ----
function OfferModal({ listing, onClose }: { listing: AnyListing; onClose: () => void }) {
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
            <button onClick={onClose} className="w-full h-12 rounded-xl bg-primary text-white font-semibold">Kapat</button>
          </div>
        ) : (
          <>
            <div className="flex items-start justify-between mb-4">
              <div>
                <h3 className="font-bold text-foreground">{listing.title}</h3>
                <p className="text-sm text-muted-foreground">{listing.seller} · {listing.city}</p>
              </div>
              <button onClick={onClose} className="p-2 hover:bg-muted rounded-xl">
                <X className="size-4" />
              </button>
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
                  placeholder="Örn: 200 kg için bu fiyat teklif ediyorum..."
                  className="w-full min-h-[80px] rounded-xl border border-border bg-white px-4 py-3 text-sm text-foreground resize-none focus:outline-none focus:ring-2 focus:ring-ring"
                />
              </div>
              <button
                type="submit"
                disabled={loading}
                className="w-full h-12 rounded-xl bg-primary text-white font-semibold flex items-center justify-center gap-2 disabled:opacity-70"
              >
                {loading ? (
                  <span className="size-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                ) : "Teklif Gönder"}
              </button>
            </form>
          </>
        )}
      </div>
    </div>
  );
}

// ---- Review Card ----
function ReviewCard({ review }: { review: Review }) {
  return (
    <div className="py-4 border-b border-border last:border-0">
      <div className="flex items-start gap-3">
        <div className="size-9 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0">
          <span className="text-sm font-bold text-primary">{review.avatar}</span>
        </div>
        <div className="flex-1 min-w-0">
          <div className="flex items-center justify-between mb-1">
            <span className="text-sm font-semibold text-foreground">{review.reviewer}</span>
            <span className="text-xs text-muted-foreground">{review.date}</span>
          </div>
          <StarDisplay rating={review.rating} />
          <p className="text-sm text-muted-foreground mt-1.5 leading-relaxed">{review.comment}</p>
        </div>
      </div>
    </div>
  );
}

// ---- Main Page ----
export function ListingDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [isFavorite, setIsFavorite] = useState(false);
  const [showOffer, setShowOffer] = useState(false);
  const [shareToast, setShareToast] = useState(false);

  const listing = id ? getListingById(id) : undefined;

  if (!listing) {
    return (
      <div className="min-h-screen bg-background flex flex-col items-center justify-center px-6 text-center">
        <div className="size-20 bg-muted rounded-full flex items-center justify-center mx-auto mb-4">
          <Beef className="size-8 text-muted-foreground" />
        </div>
        <h2 className="font-bold text-foreground mb-2">İlan bulunamadı</h2>
        <p className="text-sm text-muted-foreground mb-6">Bu ilan kaldırılmış ya da mevcut değil.</p>
        <button
          onClick={() => navigate(-1)}
          className="px-6 py-2.5 bg-primary text-white rounded-xl font-medium"
        >
          Geri Dön
        </button>
      </div>
    );
  }

  const qty = "quantity" in listing ? listing.quantity : null;
  const breed = "breed" in listing ? listing.breed : null;
  const seller = listing.sellerProfile;

  const handleShare = () => {
    setShareToast(true);
    setTimeout(() => setShareToast(false), 2000);
  };

  // Rating distribution mock
  const ratingDist: Record<number, number> = { 5: 0, 4: 0, 3: 0, 2: 0, 1: 0 };
  listing.reviews.forEach((r) => { ratingDist[r.rating] = (ratingDist[r.rating] || 0) + 1; });

  return (
    <div className="min-h-screen bg-background pb-32">
      {/* Sticky Header */}
      <div className="sticky top-0 z-40 bg-white/95 backdrop-blur-sm border-b border-border/50 flex items-center justify-between px-4 h-14">
        <button
          onClick={() => navigate(-1)}
          className="p-2 -ml-2 hover:bg-muted rounded-xl transition-colors"
        >
          <ArrowLeft className="size-5 text-foreground" />
        </button>
        <span className="font-semibold text-foreground text-sm truncate mx-2 flex-1 text-center">İlan Detayı</span>
        <div className="flex items-center gap-1">
          <button
            onClick={handleShare}
            className="p-2 hover:bg-muted rounded-xl transition-colors"
          >
            <Share2 className="size-5 text-foreground" />
          </button>
          <button
            onClick={() => setIsFavorite((f) => !f)}
            className="p-2 hover:bg-muted rounded-xl transition-colors"
          >
            <Heart className={cn("size-5 transition-colors", isFavorite ? "fill-red-500 text-red-500" : "text-foreground")} />
          </button>
        </div>
      </div>

      {/* Image Gallery */}
      <ImageGallery images={listing.images} title={listing.title} />

      {/* Content */}
      <div className="px-4 pt-4 space-y-4">

        {/* Title & Badges */}
        <div>
          <div className="flex items-start justify-between gap-3 mb-2">
            <div className="flex-1 min-w-0">
              {listing.badge && (
                <span className={cn("inline-block mb-1.5 text-xs font-semibold px-2.5 py-0.5 rounded-full", badgeColors[listing.badge] || "bg-muted text-muted-foreground")}>
                  {listing.badge}
                </span>
              )}
              <h1 className="font-bold text-foreground leading-tight">{listing.title}</h1>
            </div>
            <div className={cn("flex-shrink-0 px-3 py-1.5 rounded-xl text-sm font-semibold", typeColors[listing.type] || "bg-muted text-muted-foreground")}>
              {breed ? `${listing.type}` : listing.type}
            </div>
          </div>

          {/* Stats row */}
          <div className="flex items-center gap-3 text-xs text-muted-foreground">
            <div className="flex items-center gap-1">
              <Eye className="size-3.5" />
              <span>{listing.viewCount} görüntülenme</span>
            </div>
            <div className="flex items-center gap-1">
              <Heart className="size-3.5" />
              <span>{listing.favoriteCount} favori</span>
            </div>
            <div className="flex items-center gap-1">
              <Calendar className="size-3.5" />
              <span>{listing.date}</span>
            </div>
          </div>
        </div>

        {/* Price Card */}
        <div className="bg-primary/5 border border-primary/20 rounded-2xl p-4 flex items-center justify-between">
          <div>
            <p className="text-xs text-muted-foreground mb-0.5">Satış Fiyatı</p>
            <p className="text-2xl font-bold text-primary">
              {listing.price.toLocaleString("tr-TR")} ₺
              <span className="text-sm font-normal text-muted-foreground ml-1">/ {listing.unit}</span>
            </p>
            {qty && (
              <p className="text-xs text-muted-foreground mt-0.5">
                Toplam: {(listing.price * qty).toLocaleString("tr-TR")} ₺ ({qty} {listing.unit})
              </p>
            )}
          </div>
          {listing.verified && (
            <div className="flex flex-col items-end gap-1">
              <div className="flex items-center gap-1 text-green-600 bg-green-50 px-2.5 py-1 rounded-full">
                <Shield className="size-3.5" />
                <span className="text-xs font-semibold">Onaylı</span>
              </div>
            </div>
          )}
        </div>

        {/* Details Grid */}
        <div className="grid grid-cols-2 gap-2.5">
          <div className="bg-white border border-border rounded-2xl p-3.5">
            <div className="flex items-center gap-1.5 mb-1">
              <Tag className="size-3.5 text-primary" />
              <p className="text-xs text-muted-foreground">Tür</p>
            </div>
            <p className="font-semibold text-foreground text-sm">
              {breed ? `${breed} ${listing.type}` : listing.type}
            </p>
          </div>

          <div className="bg-white border border-border rounded-2xl p-3.5">
            <div className="flex items-center gap-1.5 mb-1">
              {qty ? <Package className="size-3.5 text-primary" /> : <Scale className="size-3.5 text-primary" />}
              <p className="text-xs text-muted-foreground">{qty ? "Adet" : "Miktar"}</p>
            </div>
            <p className="font-semibold text-foreground text-sm">
              {qty ? `${qty} adet` : `${listing.weight} kg`}
            </p>
          </div>

          <div className="bg-white border border-border rounded-2xl p-3.5">
            <div className="flex items-center gap-1.5 mb-1">
              <Scale className="size-3.5 text-primary" />
              <p className="text-xs text-muted-foreground">Ortalama Ağırlık</p>
            </div>
            <p className="font-semibold text-foreground text-sm">{listing.weight} kg</p>
          </div>

          <div className="bg-white border border-border rounded-2xl p-3.5">
            <div className="flex items-center gap-1.5 mb-1">
              <MapPin className="size-3.5 text-primary" />
              <p className="text-xs text-muted-foreground">Konum</p>
            </div>
            <p className="font-semibold text-foreground text-sm">{listing.district}, {listing.city}</p>
          </div>
        </div>

        {/* Description */}
        <div className="bg-white border border-border rounded-2xl p-4">
          <h2 className="font-semibold text-foreground mb-2.5">İlan Açıklaması</h2>
          <p className="text-sm text-muted-foreground leading-relaxed">{listing.description}</p>
        </div>

        {/* Seller Profile Card */}
        <div className="bg-white border border-border rounded-2xl overflow-hidden">
          {/* Top strip */}
          <div className={cn("h-1.5", typeStrip[listing.type] || "bg-primary")} />

          <div className="p-4">
            <div className="flex items-start gap-3 mb-3">
              {/* Avatar */}
              <div className="size-14 rounded-2xl bg-primary flex items-center justify-center flex-shrink-0">
                <span className="text-xl font-bold text-white">{seller.avatar}</span>
              </div>

              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-1.5 flex-wrap">
                  <h3 className="font-bold text-foreground">{seller.name}</h3>
                  {seller.verified && (
                    <BadgeCheck className="size-4 text-primary flex-shrink-0" />
                  )}
                </div>
                <p className="text-xs text-muted-foreground">{seller.company}</p>
                <div className="flex items-center gap-1 mt-1">
                  <MapPin className="size-3 text-muted-foreground" />
                  <span className="text-xs text-muted-foreground">{seller.city}</span>
                  <span className="text-muted-foreground/40 text-xs">·</span>
                  <span className="text-xs text-muted-foreground">{seller.memberSince}'den beri üye</span>
                </div>
              </div>
            </div>

            {/* Rating summary */}
            <div className="flex items-center gap-4 py-3 border-y border-border/50 mb-3">
              <div className="text-center">
                <p className="text-2xl font-bold text-foreground">{seller.rating.toFixed(1)}</p>
                <StarDisplay rating={seller.rating} size="sm" />
                <p className="text-[11px] text-muted-foreground mt-0.5">{seller.reviewCount} değerlendirme</p>
              </div>
              <div className="flex-1 space-y-1">
                {[5, 4, 3, 2, 1].map((star) => (
                  <RatingBar
                    key={star}
                    label={star.toString()}
                    value={ratingDist[star] || Math.max(0, Math.round(seller.reviewCount * (star === 5 ? 0.6 : star === 4 ? 0.3 : star === 3 ? 0.07 : 0.02)))}
                    total={seller.reviewCount}
                  />
                ))}
              </div>
            </div>

            {/* Stats row */}
            <div className="grid grid-cols-3 gap-2 mb-3">
              <div className="text-center">
                <p className="font-bold text-foreground">{seller.totalListings}</p>
                <p className="text-[11px] text-muted-foreground">Toplam İlan</p>
              </div>
              <div className="text-center border-x border-border">
                <p className="font-bold text-foreground">{seller.reviewCount}</p>
                <p className="text-[11px] text-muted-foreground">Değerlendirme</p>
              </div>
              <div className="text-center">
                <p className="font-bold text-foreground">{seller.memberSince}</p>
                <p className="text-[11px] text-muted-foreground">Üyelik Yılı</p>
              </div>
            </div>

            {/* Action: See seller's other listings */}
            <button
              onClick={() => navigate("/search")}
              className="w-full flex items-center justify-between py-2.5 px-3 bg-muted/50 rounded-xl hover:bg-muted transition-colors"
            >
              <span className="text-sm font-medium text-foreground">Satıcının diğer ilanları</span>
              <ChevronRight className="size-4 text-muted-foreground" />
            </button>
          </div>
        </div>

        {/* Reviews Section */}
        {listing.reviews.length > 0 && (
          <div className="bg-white border border-border rounded-2xl p-4">
            <div className="flex items-center justify-between mb-1">
              <h2 className="font-semibold text-foreground">Değerlendirmeler</h2>
              <div className="flex items-center gap-1">
                <Star className="size-4 fill-amber-400 text-amber-400" />
                <span className="font-bold text-foreground text-sm">{seller.rating.toFixed(1)}</span>
                <span className="text-xs text-muted-foreground">({seller.reviewCount})</span>
              </div>
            </div>

            <div>
              {listing.reviews.map((review) => (
                <ReviewCard key={review.id} review={review} />
              ))}
            </div>

            {seller.reviewCount > listing.reviews.length && (
              <button className="w-full mt-3 py-2.5 text-sm font-medium text-primary border border-primary/30 rounded-xl hover:bg-primary/5 transition-colors">
                Tüm değerlendirmeleri gör ({seller.reviewCount})
              </button>
            )}
          </div>
        )}

        {/* Safety tips */}
        <div className="bg-amber-50 border border-amber-200 rounded-2xl p-4">
          <div className="flex items-start gap-2.5">
            <Shield className="size-5 text-amber-600 flex-shrink-0 mt-0.5" />
            <div>
              <p className="text-sm font-semibold text-amber-800 mb-1">Güvenli Alışveriş</p>
              <p className="text-xs text-amber-700 leading-relaxed">
                Ürünü teslim almadan ödeme yapmayın. Sağlık belgelerini talep edin. 
                Şüpheli durumlarda derdimET destek ekibiyle iletişime geçin.
              </p>
            </div>
          </div>
        </div>

      </div>

      {/* Fixed Bottom Action Bar */}
      <div className="fixed bottom-0 left-0 right-0 z-40 bg-white border-t border-border max-w-[430px] mx-auto">
        <div className="px-4 py-3 flex items-center gap-3">
          {/* Seller mini info */}
          <div className="flex items-center gap-2 flex-1 min-w-0">
            <div className="size-9 rounded-full bg-primary flex items-center justify-center flex-shrink-0">
              <span className="text-sm font-bold text-white">{seller.avatar}</span>
            </div>
            <div className="min-w-0">
              <p className="text-xs font-semibold text-foreground truncate">{seller.name}</p>
              <div className="flex items-center gap-1">
                <Star className="size-2.5 fill-amber-400 text-amber-400" />
                <span className="text-[11px] text-muted-foreground">{seller.rating} · {seller.reviewCount} değerlendirme</span>
              </div>
            </div>
          </div>

          {/* Buttons */}
          <div className="flex items-center gap-2 flex-shrink-0">
            <button
              onClick={() => navigate(`/chat/conv-${listing.id}`)}
              className="flex items-center gap-1.5 h-11 px-4 rounded-xl border-2 border-primary text-primary font-semibold text-sm hover:bg-primary/5 transition-colors"
            >
              <MessageCircle className="size-4" />
              Mesaj
            </button>
            <button
              onClick={() => setShowOffer(true)}
              className="flex items-center gap-1.5 h-11 px-5 rounded-xl bg-primary text-white font-semibold text-sm hover:bg-primary/90 transition-colors"
            >
              <TrendingUp className="size-4" />
              Teklif Ver
            </button>
          </div>
        </div>
      </div>

      {/* Offer Modal */}
      {showOffer && (
        <OfferModal listing={listing} onClose={() => setShowOffer(false)} />
      )}

      {/* Share Toast */}
      {shareToast && (
        <div className="fixed top-20 left-1/2 -translate-x-1/2 z-50 bg-foreground text-background text-sm font-medium px-4 py-2.5 rounded-2xl shadow-lg">
          İlan bağlantısı kopyalandı!
        </div>
      )}
    </div>
  );
}