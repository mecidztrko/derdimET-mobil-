import { Search, FileText, Plus, User, MessageCircle } from "lucide-react";
import { useLocation, useNavigate } from "react-router";
import { cn } from "../../../lib/utils";
import { useAuth } from "../../context/AuthContext";

// Mock unread counts — in a real app these would come from context/store
const OFFERS_PENDING = 2;
const MESSAGES_UNREAD = 3;

export function BottomNav() {
  const location = useLocation();
  const navigate = useNavigate();
  const { user } = useAuth();

  const canCreateListing =
    user?.role === "ANIMAL_SELLER" || user?.role === "SLAUGHTERHOUSE";

  const isActive = (path: string) => location.pathname === path;

  /* ── Regular tab button ─────────────────────────────────────── */
  const Tab = ({
    path,
    icon: Icon,
    label,
    badge,
  }: {
    path: string;
    icon: React.ElementType;
    label: string;
    badge?: number;
  }) => {
    const active = isActive(path);
    return (
      <button
        onClick={() => navigate(path)}
        className={cn(
          "flex flex-col items-center justify-center gap-1 flex-1 py-2 px-1 transition-all",
          active ? "text-primary" : "text-muted-foreground hover:text-foreground"
        )}
      >
        <div className="relative">
          <Icon
            className={cn(
              "size-5 transition-all",
              active && "stroke-[2.5]"
            )}
          />
          {badge && badge > 0 ? (
            <span className="absolute -top-1.5 -right-1.5 min-w-[16px] h-4 bg-primary text-white text-[9px] font-bold rounded-full flex items-center justify-center px-1 leading-none">
              {badge > 9 ? "9+" : badge}
            </span>
          ) : null}
          {active && (
            <span className="absolute -bottom-1 left-1/2 -translate-x-1/2 w-1 h-1 bg-primary rounded-full" />
          )}
        </div>
        <span
          className={cn(
            "text-[10px] font-medium leading-none",
            active ? "text-primary" : "text-muted-foreground"
          )}
        >
          {label}
        </span>
      </button>
    );
  };

  /* ── Center create button ───────────────────────────────────── */
  const CreateTab = () => {
    const active = isActive("/create");
    return (
      <button
        onClick={() => navigate("/create")}
        className="flex flex-col items-center justify-center flex-1 py-1 px-1 transition-all"
      >
        <div
          className={cn(
            "size-12 rounded-2xl flex items-center justify-center shadow-lg transition-all",
            active
              ? "bg-primary scale-105 shadow-primary/30"
              : "bg-primary hover:scale-105 active:scale-95 shadow-primary/25"
          )}
        >
          <Plus className="size-6 text-white" strokeWidth={2.5} />
        </div>
        <span
          className={cn(
            "text-[10px] font-semibold leading-none mt-1",
            active ? "text-primary" : "text-muted-foreground"
          )}
        >
          İlan Ver
        </span>
      </button>
    );
  };

  return (
    <nav className="fixed bottom-0 left-0 right-0 bg-white border-t border-border z-50 shadow-[0_-4px_20px_rgba(0,0,0,0.06)]">
      <div
        className={cn(
          "flex items-end max-w-md mx-auto",
          canCreateListing ? "h-[64px]" : "h-[60px]"
        )}
      >
        <Tab path="/search"  icon={Search}        label="Ara" />
        <Tab path="/offers"  icon={FileText}       label="Teklifler" badge={OFFERS_PENDING} />
        {canCreateListing && <CreateTab />}
        <Tab path="/messages" icon={MessageCircle} label="Mesajlar"  badge={MESSAGES_UNREAD} />
        <Tab path="/profile" icon={User}           label="Profil" />
      </div>
    </nav>
  );
}
