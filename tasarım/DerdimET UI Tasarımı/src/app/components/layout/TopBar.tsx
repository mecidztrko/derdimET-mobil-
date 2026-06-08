import { ArrowLeft, Bell, Beef } from "lucide-react";
import { useNavigate } from "react-router";
import { cn } from "../../../lib/utils";

interface TopBarProps {
  title?: string;
  showBack?: boolean;
  showLogo?: boolean;
  action?: React.ReactNode;
  subtitle?: string;
}

export function TopBar({ title, showBack, showLogo, action, subtitle }: TopBarProps) {
  const navigate = useNavigate();

  return (
    <header className="sticky top-0 bg-white border-b border-border z-40 shadow-sm">
      <div className="flex items-center justify-between h-14 px-4 max-w-md mx-auto">
        <div className="flex items-center gap-3 flex-1 min-w-0">
          {showBack && (
            <button
              onClick={() => navigate(-1)}
              className="p-2 -ml-2 hover:bg-muted rounded-xl transition-colors flex-shrink-0"
            >
              <ArrowLeft className="size-5 text-foreground" />
            </button>
          )}
          {showLogo ? (
            <div className="flex items-center gap-2.5">
              <div className="size-8 rounded-xl bg-primary flex items-center justify-center flex-shrink-0">
                <Beef className="size-4 text-white" />
              </div>
              <div>
                <span className="font-semibold text-primary tracking-tight">derdimET</span>
                {subtitle && (
                  <p className="text-[10px] text-muted-foreground leading-none mt-0.5">{subtitle}</p>
                )}
              </div>
            </div>
          ) : (
            <div className="min-w-0">
              <h1 className="font-semibold text-foreground truncate">{title}</h1>
              {subtitle && (
                <p className="text-xs text-muted-foreground leading-none mt-0.5">{subtitle}</p>
              )}
            </div>
          )}
        </div>
        <div className="flex items-center gap-1 ml-2 flex-shrink-0">
          {action}
        </div>
      </div>
    </header>
  );
}
