import { useState } from "react";
import { useNavigate } from "react-router";
import { Button } from "../components/ui/Button";
import { Input } from "../components/ui/Input";
import { useAuth } from "../context/AuthContext";
import type { UserRole } from "../types";
import { Beef, ArrowLeft, ShoppingCart, Tag, Building2, CheckCircle2, ChevronRight, Eye, EyeOff } from "lucide-react";

const roles: { value: UserRole; label: string; desc: string; icon: React.ReactNode; gradient: string }[] = [
  {
    value: "MEAT_BUYER",
    label: "Et Alıcı",
    desc: "Kesimhanelerden et satın alıyorum",
    icon: <ShoppingCart className="size-6" />,
    gradient: "from-blue-500 to-blue-600",
  },
  {
    value: "ANIMAL_SELLER",
    label: "Hayvan Satıcı",
    desc: "Kesimhaneye hayvan satıyorum",
    icon: <Tag className="size-6" />,
    gradient: "from-emerald-500 to-emerald-600",
  },
  {
    value: "SLAUGHTERHOUSE",
    label: "Kesimhane",
    desc: "Hayvan alıyor, et satıyorum",
    icon: <Building2 className="size-6" />,
    gradient: "from-purple-500 to-purple-600",
  },
];

const cities = [
  "İstanbul", "Ankara", "İzmir", "Bursa", "Antalya", "Adana", "Konya",
  "Gaziantep", "Şanlıurfa", "Mersin", "Diyarbakır", "Kayseri", "Samsun",
  "Eskişehir", "Denizli", "Trabzon", "Erzurum", "Malatya", "Balıkesir"
];

export function RegisterPage() {
  const navigate = useNavigate();
  const { register } = useAuth();
  const [step, setStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const [formData, setFormData] = useState({
    email: "",
    password: "",
    name: "",
    role: "" as UserRole | "",
    companyName: "",
    city: "",
    address: "",
  });

  const handleRoleSelect = (role: UserRole) => {
    setFormData({ ...formData, role });
    setStep(2);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.role) return;
    setLoading(true);
    try {
      await register({ ...formData, role: formData.role as UserRole });
      navigate("/search");
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const selectedRole = roles.find((r) => r.value === formData.role);

  return (
    <div className="min-h-screen bg-gradient-to-b from-primary via-blue-800 to-primary flex flex-col">
      {/* Header */}
      <div className="flex items-center justify-between px-5 pt-12 pb-6">
        <button
          onClick={() => (step === 2 ? setStep(1) : navigate("/login"))}
          className="size-10 rounded-xl bg-white/15 flex items-center justify-center"
        >
          <ArrowLeft className="size-5 text-white" />
        </button>
        <div className="text-center">
          <div className="flex items-center gap-2">
            <Beef className="size-5 text-white" />
            <span className="text-white font-semibold">derdimET</span>
          </div>
        </div>
        <div className="w-10" />
      </div>

      {/* Step indicator */}
      <div className="px-5 pb-4">
        <div className="flex items-center gap-2">
          <div className="flex-1 flex items-center gap-2">
            <div className="flex items-center justify-center size-6 rounded-full bg-white text-primary text-xs font-bold">1</div>
            <div className={`flex-1 h-0.5 ${step === 2 ? "bg-white" : "bg-white/30"}`} />
            <div className={`flex items-center justify-center size-6 rounded-full text-xs font-bold ${step === 2 ? "bg-white text-primary" : "bg-white/30 text-white"}`}>2</div>
          </div>
        </div>
        <div className="flex justify-between mt-1">
          <span className="text-[11px] text-blue-100">Rolünü Seç</span>
          <span className={`text-[11px] ${step === 2 ? "text-white" : "text-blue-200"}`}>Bilgilerini Gir</span>
        </div>
      </div>

      {/* Card */}
      <div className="flex-1 bg-background rounded-t-3xl px-5 pt-6 pb-8">
        {step === 1 ? (
          <>
            <h2 className="font-bold text-foreground mb-1">Rolünüzü seçin</h2>
            <p className="text-sm text-muted-foreground mb-5">Platformda nasıl yer almak istiyorsunuz?</p>
            <div className="space-y-3">
              {roles.map((role) => (
                <button
                  key={role.value}
                  onClick={() => handleRoleSelect(role.value)}
                  className="w-full flex items-center gap-4 p-4 rounded-2xl border-2 border-border hover:border-primary/50 hover:bg-primary/5 transition-all active:scale-98 text-left group"
                >
                  <div className={`size-12 rounded-2xl bg-gradient-to-br ${role.gradient} flex items-center justify-center text-white flex-shrink-0 shadow-sm`}>
                    {role.icon}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="font-semibold text-foreground">{role.label}</p>
                    <p className="text-sm text-muted-foreground">{role.desc}</p>
                  </div>
                  <ChevronRight className="size-5 text-muted-foreground group-hover:text-primary transition-colors flex-shrink-0" />
                </button>
              ))}
            </div>
          </>
        ) : (
          <>
            <div className="flex items-center gap-3 mb-5">
              {selectedRole && (
                <div className={`size-10 rounded-xl bg-gradient-to-br ${selectedRole.gradient} flex items-center justify-center text-white`}>
                  {selectedRole.icon}
                </div>
              )}
              <div>
                <h2 className="font-bold text-foreground">{selectedRole?.label} Hesabı</h2>
                <p className="text-sm text-muted-foreground">Bilgilerinizi doldurun</p>
              </div>
            </div>

            <form onSubmit={handleSubmit} className="space-y-3.5">
              <div>
                <label className="block text-sm text-foreground mb-1.5">İsim Soyisim</label>
                <Input
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="Ahmet Yılmaz"
                  required
                />
              </div>

              <div>
                <label className="block text-sm text-foreground mb-1.5">E-posta</label>
                <Input
                  type="email"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  placeholder="ornek@email.com"
                  required
                />
              </div>

              <div>
                <label className="block text-sm text-foreground mb-1.5">Şifre</label>
                <div className="relative">
                  <Input
                    type={showPassword ? "text" : "password"}
                    value={formData.password}
                    onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                    placeholder="En az 8 karakter"
                    className="pr-11"
                    required
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground"
                  >
                    {showPassword ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
                  </button>
                </div>
              </div>

              <div>
                <label className="block text-sm text-foreground mb-1.5">Firma / İşletme Adı</label>
                <Input
                  value={formData.companyName}
                  onChange={(e) => setFormData({ ...formData, companyName: e.target.value })}
                  placeholder="ABC Et ve Hayvan Ürünleri"
                />
              </div>

              <div>
                <label className="block text-sm text-foreground mb-1.5">Şehir</label>
                <select
                  className="flex h-11 w-full rounded-xl border border-border bg-white px-4 py-2 text-foreground focus:outline-none focus:ring-2 focus:ring-ring"
                  value={formData.city}
                  onChange={(e) => setFormData({ ...formData, city: e.target.value })}
                  required
                >
                  <option value="">Şehir seçin</option>
                  {cities.map((c) => (
                    <option key={c} value={c}>{c}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm text-foreground mb-1.5">Adres</label>
                <Input
                  value={formData.address}
                  onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                  placeholder="Tam adres"
                />
              </div>

              <Button type="submit" className="w-full h-12 rounded-xl mt-2" loading={loading}>
                <CheckCircle2 className="size-4" />
                Kayıt Ol
              </Button>
            </form>
          </>
        )}

        <p className="text-center text-sm text-muted-foreground mt-6">
          Zaten hesabınız var mı?{" "}
          <button onClick={() => navigate("/login")} className="text-primary font-semibold hover:underline">
            Giriş Yapın
          </button>
        </p>
      </div>
    </div>
  );
}
