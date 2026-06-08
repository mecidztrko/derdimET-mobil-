import { useState } from "react";
import { TopBar } from "../components/layout/TopBar";
import { BottomNav } from "../components/layout/BottomNav";
import { Button } from "../components/ui/Button";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router";
import type { UserRole } from "../types";
import {
  User, Building2, MapPin, Settings, LogOut, ChevronRight,
  Heart, ShoppingBag, FileText, Bell, Shield, HelpCircle,
  CheckCircle, Star, TrendingUp, MessageCircle, RefreshCw
} from "lucide-react";
import { cn } from "../../lib/utils";

const roleConfig: Record<UserRole, { label: string; color: string; bg: string; icon: React.ReactNode }> = {
  MEAT_BUYER: {
    label: "Et Alıcı",
    color: "text-blue-700",
    bg: "bg-blue-100",
    icon: <ShoppingBag className="size-4" />,
  },
  ANIMAL_SELLER: {
    label: "Hayvan Satıcı",
    color: "text-emerald-700",
    bg: "bg-emerald-100",
    icon: <FileText className="size-4" />,
  },
  SLAUGHTERHOUSE: {
    label: "Kesimhane",
    color: "text-purple-700",
    bg: "bg-purple-100",
    icon: <Building2 className="size-4" />,
  },
};

const demoRoles: { role: UserRole; label: string; desc: string }[] = [
  { role: "MEAT_BUYER", label: "Et Alıcı", desc: "Et satın al" },
  { role: "ANIMAL_SELLER", label: "Hayvan Satıcı", desc: "Hayvan sat" },
  { role: "SLAUGHTERHOUSE", label: "Kesimhane", desc: "Hayvan al / et sat" },
];

const stats = [
  { label: "İlan", value: "12", icon: <FileText className="size-4" /> },
  { label: "Teklif", value: "48", icon: <TrendingUp className="size-4" /> },
  { label: "Mesaj", value: "24", icon: <MessageCircle className="size-4" /> },
  { label: "Puan", value: "4.8", icon: <Star className="size-4" /> },
];

export function ProfilePage() {
  const { user, logout, switchRole } = useAuth();
  const navigate = useNavigate();
  const [showRoleSwitcher, setShowRoleSwitcher] = useState(false);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const role = user?.role;
  const cfg = role ? roleConfig[role] : null;

  const menuSections = [
    {
      title: "Hesabım",
      items: [
        { icon: <Heart className="size-5 text-red-400" />, label: "Favorilerim", badge: "3" },
        {
          icon: <ShoppingBag className="size-5 text-blue-500" />,
          label: role === "MEAT_BUYER" ? "Alışverişlerim" : "Satışlarım",
          badge: null,
        },
        ...(role !== "MEAT_BUYER"
          ? [{ icon: <FileText className="size-5 text-purple-500" />, label: "İlanlarım", badge: "12" }]
          : []),
      ],
    },
    {
      title: "Ayarlar",
      items: [
        { icon: <Bell className="size-5 text-amber-500" />, label: "Bildirimler" },
        { icon: <Shield className="size-5 text-green-500" />, label: "Gizlilik ve Güvenlik" },
        { icon: <Settings className="size-5 text-muted-foreground" />, label: "Genel Ayarlar" },
        { icon: <HelpCircle className="size-5 text-muted-foreground" />, label: "Yardım" },
      ],
    },
  ];

  return (
    <div className="min-h-screen bg-background pb-24">
      <TopBar title="Profil" />

      <div className="max-w-md mx-auto px-4 py-4 space-y-4">
        {/* Profile Card */}
        <div className="bg-white rounded-2xl border border-border shadow-sm overflow-hidden">
          {/* Header gradient */}
          <div className="h-20 bg-gradient-to-r from-primary to-blue-500" />

          <div className="px-4 pb-4">
            <div className="flex items-end justify-between -mt-8 mb-3">
              <div className="size-16 rounded-2xl bg-white border-4 border-white shadow-md flex items-center justify-center">
                <div className="size-full rounded-xl bg-primary/10 flex items-center justify-center">
                  <User className="size-8 text-primary" />
                </div>
              </div>
              <Button variant="secondary" size="sm" className="rounded-xl mb-1">
                <Settings className="size-3.5" />
                Düzenle
              </Button>
            </div>

            <div className="mb-3">
              <div className="flex items-center gap-2 mb-1">
                <h2 className="font-bold text-foreground">{user?.name || "Kullanıcı"}</h2>
                {user?.verified && (
                  <CheckCircle className="size-4.5 text-success" />
                )}
              </div>

              {cfg && (
                <span className={cn("inline-flex items-center gap-1.5 text-xs font-semibold px-2.5 py-1 rounded-full", cfg.bg, cfg.color)}>
                  {cfg.icon}
                  {cfg.label}
                </span>
              )}

              <div className="flex flex-col gap-1 mt-2.5">
                {user?.companyName && (
                  <div className="flex items-center gap-1.5 text-sm text-muted-foreground">
                    <Building2 className="size-3.5 flex-shrink-0" />
                    <span className="truncate">{user.companyName}</span>
                  </div>
                )}
                {user?.city && (
                  <div className="flex items-center gap-1.5 text-sm text-muted-foreground">
                    <MapPin className="size-3.5 flex-shrink-0" />
                    <span>{user.city}</span>
                  </div>
                )}
              </div>
            </div>

            {/* Stats */}
            <div className="grid grid-cols-4 gap-2">
              {stats.map((stat) => (
                <div key={stat.label} className="bg-muted/50 rounded-xl p-2.5 text-center">
                  <div className="flex items-center justify-center text-primary mb-1">
                    {stat.icon}
                  </div>
                  <p className="font-bold text-foreground text-sm">{stat.value}</p>
                  <p className="text-[10px] text-muted-foreground">{stat.label}</p>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Demo Role Switcher */}
        <div className="bg-white rounded-2xl border border-border shadow-sm overflow-hidden">
          <button
            onClick={() => setShowRoleSwitcher(!showRoleSwitcher)}
            className="w-full flex items-center justify-between p-4 hover:bg-muted/30 transition-colors"
          >
            <div className="flex items-center gap-3">
              <div className="size-9 rounded-xl bg-blue-100 flex items-center justify-center">
                <RefreshCw className="size-4 text-blue-600" />
              </div>
              <div className="text-left">
                <p className="font-medium text-foreground text-sm">Demo: Rol Değiştir</p>
                <p className="text-xs text-muted-foreground">Farklı kullanıcı deneyimlerini test et</p>
              </div>
            </div>
            <ChevronRight className={cn("size-5 text-muted-foreground transition-transform", showRoleSwitcher && "rotate-90")} />
          </button>

          {showRoleSwitcher && (
            <div className="border-t border-border">
              {demoRoles.map((r, i) => (
                <button
                  key={r.role}
                  onClick={() => { switchRole(r.role); setShowRoleSwitcher(false); }}
                  className={cn(
                    "w-full flex items-center justify-between px-4 py-3 hover:bg-muted/30 transition-colors",
                    i > 0 && "border-t border-border/50"
                  )}
                >
                  <div className="flex items-center gap-3">
                    <span className={cn("w-2 h-2 rounded-full", {
                      "bg-blue-500": r.role === "MEAT_BUYER",
                      "bg-emerald-500": r.role === "ANIMAL_SELLER",
                      "bg-purple-500": r.role === "SLAUGHTERHOUSE",
                    })} />
                    <div className="text-left">
                      <p className="text-sm font-medium text-foreground">{r.label}</p>
                      <p className="text-xs text-muted-foreground">{r.desc}</p>
                    </div>
                  </div>
                  {user?.role === r.role && (
                    <CheckCircle className="size-4 text-success" />
                  )}
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Menu Sections */}
        {menuSections.map((section) => (
          <div key={section.title} className="space-y-1">
            <p className="text-xs font-semibold text-muted-foreground uppercase tracking-wider px-1 mb-2">{section.title}</p>
            <div className="bg-white rounded-2xl border border-border shadow-sm overflow-hidden divide-y divide-border/50">
              {section.items.map((item) => (
                <button
                  key={item.label}
                  className="w-full flex items-center justify-between p-4 hover:bg-muted/30 transition-colors text-left"
                >
                  <div className="flex items-center gap-3">
                    <div className="size-9 rounded-xl bg-muted/50 flex items-center justify-center">
                      {item.icon}
                    </div>
                    <span className="font-medium text-foreground text-sm">{item.label}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    {"badge" in item && item.badge && (
                      <span className="size-5 bg-primary/10 text-primary text-[11px] font-bold rounded-full flex items-center justify-center">
                        {item.badge}
                      </span>
                    )}
                    <ChevronRight className="size-4 text-muted-foreground" />
                  </div>
                </button>
              ))}
            </div>
          </div>
        ))}

        {/* Logout */}
        <div className="bg-white rounded-2xl border border-border shadow-sm overflow-hidden">
          <button
            onClick={handleLogout}
            className="w-full flex items-center gap-3 p-4 hover:bg-red-50 transition-colors text-destructive"
          >
            <div className="size-9 rounded-xl bg-red-100 flex items-center justify-center">
              <LogOut className="size-4 text-destructive" />
            </div>
            <span className="font-medium text-sm">Çıkış Yap</span>
          </button>
        </div>
      </div>

      <BottomNav />
    </div>
  );
}
