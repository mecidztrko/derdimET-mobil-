import { useState } from "react";
import { useNavigate } from "react-router";
import { TopBar } from "../components/layout/TopBar";
import { BottomNav } from "../components/layout/BottomNav";
import { Button } from "../components/ui/Button";
import { useAuth } from "../context/AuthContext";
import {
  MessageCircle, FileText, CheckCircle, XCircle, Clock,
  TrendingUp, TrendingDown, Package, ChevronRight,
} from "lucide-react";
import { cn } from "../../lib/utils";

const mockOffers = [
  {
    id: "o1",
    listingTitle: "Dana Eti – Karkas",
    listingType: "et",
    buyerName: "Kemal Çelik",
    buyerCompany: "Çelik Gıda",
    buyerAvatar: "KC",
    amount: 190,
    originalPrice: 185,
    unit: "kg",
    status: "pending" as const,
    date: "1 saat önce",
    quantity: "200 kg",
    city: "İzmir",
  },
  {
    id: "o2",
    listingTitle: "Kuzu Eti – Taze",
    listingType: "et",
    buyerName: "Selin Aydın",
    buyerCompany: "Aydın Market",
    buyerAvatar: "SA",
    amount: 225,
    originalPrice: 230,
    unit: "kg",
    status: "accepted" as const,
    date: "3 saat önce",
    quantity: "80 kg",
    city: "Aydın",
  },
  {
    id: "o3",
    listingTitle: "Koyun Eti – Karkas",
    listingType: "et",
    buyerName: "Fatma Kaya",
    buyerCompany: "Kaya Gıda A.Ş.",
    buyerAvatar: "FK",
    amount: 155,
    originalPrice: 165,
    unit: "kg",
    status: "rejected" as const,
    date: "1 gün önce",
    quantity: "350 kg",
    city: "Manisa",
  },
  {
    id: "o4",
    listingTitle: "Merinos Koyun – Sürü",
    listingType: "hayvan",
    buyerName: "Yılmaz Kesimhane",
    buyerCompany: "Yılmaz Et",
    buyerAvatar: "YK",
    amount: 4600,
    originalPrice: 4800,
    unit: "adet",
    status: "pending" as const,
    date: "2 saat önce",
    quantity: "15 adet",
    city: "İzmir",
  },
  {
    id: "o5",
    listingTitle: "Dana Bonfile – Premium",
    listingType: "et",
    buyerName: "Murat Yıldız",
    buyerCompany: "Yıldız Restoran Grubu",
    buyerAvatar: "MY",
    amount: 310,
    originalPrice: 320,
    unit: "kg",
    status: "pending" as const,
    date: "4 saat önce",
    quantity: "25 kg",
    city: "İstanbul",
  },
];

const statusConfig = {
  pending: {
    label: "Bekliyor",
    icon: Clock,
    bg: "bg-amber-50",
    text: "text-amber-700",
    border: "border-amber-200",
    strip: "bg-amber-400",
  },
  accepted: {
    label: "Kabul Edildi",
    icon: CheckCircle,
    bg: "bg-green-50",
    text: "text-green-700",
    border: "border-green-200",
    strip: "bg-green-400",
  },
  rejected: {
    label: "Reddedildi",
    icon: XCircle,
    bg: "bg-red-50",
    text: "text-red-700",
    border: "border-red-200",
    strip: "bg-red-400",
  },
};

const avatarColors = [
  "bg-blue-100 text-blue-700",
  "bg-purple-100 text-purple-700",
  "bg-emerald-100 text-emerald-700",
  "bg-amber-100 text-amber-700",
  "bg-rose-100 text-rose-700",
];

type StatusFilter = "" | "pending" | "accepted" | "rejected";

const filterTabs: { key: StatusFilter; label: string; color: string }[] = [
  { key: "", label: "Tümü", color: "" },
  { key: "pending", label: "Bekleyen", color: "text-amber-600" },
  { key: "accepted", label: "Kabul", color: "text-green-600" },
  { key: "rejected", label: "Reddedilen", color: "text-red-600" },
];

export function OffersPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [filterStatus, setFilterStatus] = useState<StatusFilter>("");
  const [offerStatuses, setOfferStatuses] = useState<Record<string, "pending" | "accepted" | "rejected">>(
    Object.fromEntries(mockOffers.map((o) => [o.id, o.status]))
  );

  const pendingCount = Object.values(offerStatuses).filter((s) => s === "pending").length;
  const acceptedCount = Object.values(offerStatuses).filter((s) => s === "accepted").length;
  const rejectedCount = Object.values(offerStatuses).filter((s) => s === "rejected").length;

  const filteredOffers = mockOffers.filter((o) =>
    filterStatus ? offerStatuses[o.id] === filterStatus : true
  );

  const acceptOffer = (id: string) =>
    setOfferStatuses((prev) => ({ ...prev, [id]: "accepted" }));
  const rejectOffer = (id: string) =>
    setOfferStatuses((prev) => ({ ...prev, [id]: "rejected" }));

  return (
    <div className="min-h-screen bg-background pb-24">
      <TopBar
        title="Teklifler"
        action={
          pendingCount > 0 ? (
            <span className="bg-amber-400 text-white text-xs font-bold px-2.5 py-1 rounded-full">
              {pendingCount} bekliyor
            </span>
          ) : undefined
        }
      />

      {/* Stats bar */}
      <div className="bg-white border-b border-border">
        <div className="max-w-md mx-auto px-4 py-3">
          <div className="grid grid-cols-3 gap-2">
            {[
              { label: "Bekleyen", count: pendingCount, color: "text-amber-600", bg: "bg-amber-50", border: "border-amber-100" },
              { label: "Kabul", count: acceptedCount, color: "text-green-600", bg: "bg-green-50", border: "border-green-100" },
              { label: "Reddedilen", count: rejectedCount, color: "text-red-600", bg: "bg-red-50", border: "border-red-100" },
            ].map((stat) => (
              <div key={stat.label} className={cn("rounded-xl p-2.5 text-center border", stat.bg, stat.border)}>
                <p className={cn("font-bold text-lg leading-none mb-1", stat.color)}>{stat.count}</p>
                <p className="text-[11px] text-muted-foreground">{stat.label}</p>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Filter chips */}
      <div className="sticky top-14 bg-background/95 backdrop-blur-sm z-30 border-b border-border/50">
        <div className="max-w-md mx-auto px-4 py-2.5">
          <div className="flex gap-2 overflow-x-auto no-scrollbar pb-0.5">
            {filterTabs.map(({ key, label }) => {
              const count =
                key === "" ? mockOffers.length
                : key === "pending" ? pendingCount
                : key === "accepted" ? acceptedCount
                : rejectedCount;
              return (
                <button
                  key={key || "all"}
                  onClick={() => setFilterStatus(key)}
                  className={cn(
                    "flex-shrink-0 flex items-center gap-1.5 px-3.5 py-2 rounded-xl text-sm font-medium border-2 transition-all",
                    filterStatus === key
                      ? "bg-primary text-white border-primary"
                      : "bg-white text-muted-foreground border-border hover:border-primary/40"
                  )}
                >
                  {label}
                  <span className={cn(
                    "text-[10px] font-bold px-1.5 py-0.5 rounded-full",
                    filterStatus === key ? "bg-white/20 text-white" : "bg-muted text-muted-foreground"
                  )}>
                    {count}
                  </span>
                </button>
              );
            })}
          </div>
        </div>
      </div>

      {/* Offers list */}
      <div className="max-w-md mx-auto px-4 py-3 space-y-3">
        {filteredOffers.length === 0 ? (
          <div className="text-center py-16">
            <div className="size-20 bg-muted rounded-full flex items-center justify-center mx-auto mb-4">
              <FileText className="size-8 text-muted-foreground" />
            </div>
            <h3 className="font-semibold text-foreground mb-1">Teklif bulunamadı</h3>
            <p className="text-sm text-muted-foreground">Bu kategoride henüz teklif yok</p>
          </div>
        ) : (
          filteredOffers.map((offer, idx) => {
            const status = offerStatuses[offer.id];
            const cfg = statusConfig[status];
            const StatusIcon = cfg.icon;
            const isHigher = offer.amount > offer.originalPrice;
            const diff = Math.abs(offer.amount - offer.originalPrice);
            const diffPct = ((diff / offer.originalPrice) * 100).toFixed(1);

            return (
              <div
                key={offer.id}
                className="bg-white rounded-2xl border border-border shadow-sm overflow-hidden"
              >
                {/* Status strip */}
                <div className={cn("h-1.5 w-full", cfg.strip)} />

                <div className="p-4">
                  {/* Header row */}
                  <div className="flex items-start gap-3 mb-3">
                    <div className={cn(
                      "size-10 rounded-xl flex items-center justify-center text-sm font-bold flex-shrink-0",
                      avatarColors[idx % avatarColors.length]
                    )}>
                      {offer.buyerAvatar}
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="font-semibold text-foreground text-sm truncate">{offer.listingTitle}</p>
                      <p className="text-xs text-muted-foreground">{offer.buyerName} · {offer.buyerCompany}</p>
                    </div>
                    <span className={cn(
                      "flex items-center gap-1 px-2.5 py-1 rounded-xl text-xs font-semibold border flex-shrink-0",
                      cfg.bg, cfg.text, cfg.border
                    )}>
                      <StatusIcon className="size-3.5" />
                      {cfg.label}
                    </span>
                  </div>

                  {/* Price grid */}
                  <div className="grid grid-cols-3 gap-2 mb-3">
                    <div className="bg-muted/50 rounded-xl p-2.5">
                      <p className="text-[10px] text-muted-foreground mb-1">Teklif</p>
                      <div className="flex items-center gap-1">
                        <p className="font-bold text-primary text-sm">
                          {offer.amount.toLocaleString("tr-TR")} ₺
                        </p>
                        {isHigher
                          ? <TrendingUp className="size-3 text-green-500" />
                          : <TrendingDown className="size-3 text-red-500" />
                        }
                      </div>
                    </div>
                    <div className="bg-muted/50 rounded-xl p-2.5">
                      <p className="text-[10px] text-muted-foreground mb-1">İstenen</p>
                      <p className="font-semibold text-foreground text-sm">
                        {offer.originalPrice.toLocaleString("tr-TR")} ₺
                      </p>
                    </div>
                    <div className="bg-muted/50 rounded-xl p-2.5">
                      <p className="text-[10px] text-muted-foreground mb-1">Miktar</p>
                      <p className="font-semibold text-foreground text-sm">{offer.quantity}</p>
                    </div>
                  </div>

                  {/* Diff badge */}
                  <div className={cn(
                    "flex items-center gap-1.5 px-3 py-1.5 rounded-xl mb-3 w-fit text-xs font-semibold",
                    isHigher ? "bg-green-50 text-green-700" : "bg-red-50 text-red-700"
                  )}>
                    {isHigher ? <TrendingUp className="size-3.5" /> : <TrendingDown className="size-3.5" />}
                    {isHigher ? "+" : "-"}{diff.toLocaleString("tr-TR")} ₺/{offer.unit} ({diffPct}%)
                  </div>

                  {/* Footer */}
                  <div className="flex items-center justify-between pt-3 border-t border-border">
                    <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                      <Package className="size-3.5" />
                      {offer.date} · {offer.city}
                    </div>
                    <div className="flex gap-2">
                      {status === "pending" && (
                        <>
                          <button
                            onClick={() => rejectOffer(offer.id)}
                            className="px-3 py-1.5 rounded-xl text-xs font-semibold text-red-600 border border-red-200 bg-red-50 hover:bg-red-100 transition-colors"
                          >
                            Reddet
                          </button>
                          <button
                            onClick={() => acceptOffer(offer.id)}
                            className="px-3 py-1.5 rounded-xl text-xs font-semibold text-white bg-green-500 hover:bg-green-600 transition-colors shadow-sm"
                          >
                            Kabul Et
                          </button>
                        </>
                      )}
                      {status !== "pending" && (
                        <button
                          onClick={() => navigate(`/chat/c1`)}
                          className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold text-primary border border-primary/30 bg-primary/5 hover:bg-primary/10 transition-colors"
                        >
                          <MessageCircle className="size-3.5" />
                          Mesaj
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            );
          })
        )}
      </div>

      <BottomNav />
    </div>
  );
}
