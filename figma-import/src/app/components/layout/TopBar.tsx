import React from 'react';
import { ArrowLeft, Bell, Settings } from 'lucide-react';
import { useNavigate } from 'react-router';

interface TopBarProps {
  title?: string;
  showBack?: boolean;
  showBell?: boolean;
  showSettings?: boolean;
  onBack?: () => void;
  rightElement?: React.ReactNode;
  variant?: 'default' | 'transparent';
}

export function TopBar({
  title,
  showBack = false,
  showBell = false,
  showSettings = false,
  onBack,
  rightElement,
  variant = 'default',
}: TopBarProps) {
  const navigate = useNavigate();

  const handleBack = () => {
    if (onBack) onBack();
    else navigate(-1);
  };

  return (
    <div
      className={`flex items-center justify-between px-5 h-14 flex-shrink-0 ${
        variant === 'default'
          ? 'bg-white border-b border-gray-100'
          : 'bg-transparent'
      }`}
    >
      {/* Left */}
      <div className="flex items-center gap-3 min-w-[44px]">
        {showBack ? (
          <button
            onClick={handleBack}
            className="w-9 h-9 flex items-center justify-center rounded-xl hover:bg-gray-100 active:bg-gray-200 transition-colors"
          >
            <ArrowLeft size={20} className="text-gray-700" />
          </button>
        ) : (
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-xl bg-[#1B3A6B] flex items-center justify-center">
              <span className="text-white text-xs font-bold tracking-tight">dE</span>
            </div>
          </div>
        )}
      </div>

      {/* Center */}
      <div className="flex-1 text-center">
        {title ? (
          <span className="text-[15px] font-semibold text-gray-900">{title}</span>
        ) : (
          <div className="flex items-center justify-center gap-1">
            <span className="text-[17px] font-bold text-[#1B3A6B]">derdi</span>
            <span className="text-[17px] font-bold text-[#E05C2A]">mET</span>
          </div>
        )}
      </div>

      {/* Right */}
      <div className="flex items-center gap-1 min-w-[44px] justify-end">
        {rightElement}
        {showBell && (
          <button className="w-9 h-9 flex items-center justify-center rounded-xl hover:bg-gray-100 active:bg-gray-200 transition-colors relative">
            <Bell size={20} className="text-gray-700" />
            <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-[#E05C2A]" />
          </button>
        )}
        {showSettings && (
          <button
            onClick={() => navigate('/app/settings')}
            className="w-9 h-9 flex items-center justify-center rounded-xl hover:bg-gray-100 active:bg-gray-200 transition-colors"
          >
            <Settings size={20} className="text-gray-700" />
          </button>
        )}
      </div>
    </div>
  );
}
