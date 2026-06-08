import { useState } from "react";
import { useNavigate } from "react-router";
import { Button } from "../components/ui/Button";
import { Input } from "../components/ui/Input";
import { useAuth } from "../context/AuthContext";
import type { UserRole } from "../types";
import { Beef, ShoppingCart, Tag, Building2, Eye, EyeOff, ChevronRight } from "lucide-react";

const demoRoles: { role: UserRole; label: string; desc: string; icon: React.ReactNode; color: string }[] = [
  {
    role: "MEAT_BUYER",
    label: "Et Alıcı",
    desc: "Et satın al",
    icon: <ShoppingCart className="size-4" />,
    color: "bg-blue-50 text-blue-700 border-blue-200",
  },
  {
    role: "ANIMAL_SELLER",
    label: "Hayvan Satıcı",
    desc: "Hayvan sat",
    icon: <Tag className="size-4" />,
    color: "bg-emerald-50 text-emerald-700 border-emerald-200",
  },
  {
    role: "SLAUGHTERHOUSE",
    label: "Kesimhane",
    desc: "Hayvan al & et sat",
    icon: <Building2 className="size-4" />,
    color: "bg-purple-50 text-purple-700 border-purple-200",
  },
];

export function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [demoLoading, setDemoLoading] = useState<UserRole | null>(null);
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await login(email, password);
      navigate("/search");
    } catch {
      setError("Giriş başarısız. Lütfen bilgilerinizi kontrol edin.");
    } finally {
      setLoading(false);
    }
  };

  const handleDemoLogin = async (role: UserRole) => {
    setDemoLoading(role);
    try {
      await login("", "", role);
      navigate("/search");
    } finally {
      setDemoLoading(null);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-primary via-blue-800 to-primary flex flex-col">
      {/* Hero Section */}
      <div className="flex flex-col items-center justify-center pt-14 pb-8 px-6">
        <div className="size-20 rounded-3xl bg-white/15 backdrop-blur-sm border border-white/20 flex items-center justify-center mb-5 shadow-xl">
          <Beef className="size-10 text-white" />
        </div>
        <h1 className="text-3xl font-bold text-white tracking-tight mb-1">derdimET</h1>
        <p className="text-blue-200 text-sm">Hayvancılık ve Et Ticareti Pazaryeri</p>
      </div>

      {/* Card */}
      <div className="flex-1 bg-background rounded-t-3xl px-5 pt-7 pb-8">
        {/* Demo quick login */}
        <div className="mb-6">
          <p className="text-xs font-medium text-muted-foreground mb-3 uppercase tracking-wider">Demo Modunda Hızlı Giriş</p>
          <div className="grid grid-cols-3 gap-2">
            {demoRoles.map((r) => (
              <button
                key={r.role}
                onClick={() => handleDemoLogin(r.role)}
                disabled={!!demoLoading}
                className={`flex flex-col items-center gap-1.5 p-3 rounded-xl border-2 transition-all ${r.color} ${demoLoading === r.role ? "opacity-70" : "hover:scale-105 active:scale-95"}`}
              >
                {demoLoading === r.role ? (
                  <div className="size-4 border-2 border-current border-t-transparent rounded-full animate-spin" />
                ) : (
                  r.icon
                )}
                <span className="text-[11px] font-semibold leading-tight text-center">{r.label}</span>
              </button>
            ))}
          </div>
        </div>

        <div className="flex items-center gap-3 mb-6">
          <div className="flex-1 h-px bg-border" />
          <span className="text-xs text-muted-foreground">veya e-posta ile</span>
          <div className="flex-1 h-px bg-border" />
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm text-foreground mb-1.5">E-posta</label>
            <Input
              type="email"
              placeholder="ornek@email.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          <div>
            <label className="block text-sm text-foreground mb-1.5">Şifre</label>
            <div className="relative">
              <Input
                type={showPassword ? "text" : "password"}
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="pr-11"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
              >
                {showPassword ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
              </button>
            </div>
          </div>

          <div className="flex items-center justify-between">
            <label className="flex items-center gap-2 cursor-pointer">
              <input type="checkbox" className="rounded accent-primary" />
              <span className="text-sm text-muted-foreground">Beni hatırla</span>
            </label>
            <button type="button" className="text-sm text-primary font-medium hover:underline">
              Şifremi unuttum
            </button>
          </div>

          {error && (
            <div className="bg-destructive/10 text-destructive text-sm p-3 rounded-xl border border-destructive/20">
              {error}
            </div>
          )}

          <Button type="submit" className="w-full h-12 rounded-xl" loading={loading}>
            <span>Giriş Yap</span>
            <ChevronRight className="size-4" />
          </Button>
        </form>

        <p className="text-center text-sm text-muted-foreground mt-6">
          Hesabınız yok mu?{" "}
          <button
            onClick={() => navigate("/register")}
            className="text-primary font-semibold hover:underline"
          >
            Kayıt Olun
          </button>
        </p>
      </div>
    </div>
  );
}
