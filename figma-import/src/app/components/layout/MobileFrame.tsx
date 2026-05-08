import React from 'react';

interface MobileFrameProps {
  children: React.ReactNode;
}

export function MobileFrame({ children }: MobileFrameProps) {
  return (
    <div className="h-screen w-full bg-white md:bg-gradient-to-br md:from-[#0F2C59] md:via-[#1B3A6B] md:to-[#1D5BE6] md:flex md:items-center md:justify-center overflow-hidden">
      {/* Phone frame container */}
      <div className="h-full w-full flex flex-col overflow-hidden bg-white md:w-[390px] md:h-[844px] md:rounded-[44px] md:shadow-2xl md:border md:border-white/10">
        {/* iOS Status Bar – desktop only visual */}
        <div className="hidden md:flex items-center justify-between px-7 pt-4 pb-1 bg-white flex-shrink-0">
          <span style={{ fontSize: '13px', fontWeight: 600, color: '#111' }}>9:41</span>
          <div className="flex items-center gap-2">
            {/* Signal */}
            <svg width="17" height="12" viewBox="0 0 17 12" fill="none">
              <rect x="0" y="6" width="3" height="6" rx="0.8" fill="#111"/>
              <rect x="4.5" y="4" width="3" height="8" rx="0.8" fill="#111"/>
              <rect x="9" y="2" width="3" height="10" rx="0.8" fill="#111"/>
              <rect x="13.5" y="0" width="3" height="12" rx="0.8" fill="#D1D5DB"/>
            </svg>
            {/* WiFi */}
            <svg width="16" height="12" viewBox="0 0 16 12" fill="none">
              <path d="M8 3C10 3 11.8 3.8 13.1 5.2L14.3 3.8C12.6 2.1 10.4 1 8 1C5.6 1 3.4 2.1 1.7 3.8L2.9 5.2C4.2 3.8 6 3 8 3Z" fill="#111"/>
              <path d="M8 6C9.3 6 10.5 6.5 11.4 7.4L12.6 6C11.3 4.8 9.7 4 8 4C6.3 4 4.7 4.8 3.4 6L4.6 7.4C5.5 6.5 6.7 6 8 6Z" fill="#111"/>
              <circle cx="8" cy="10.5" r="1.5" fill="#111"/>
            </svg>
            {/* Battery */}
            <div className="flex items-center gap-0.5">
              <div className="w-6 h-3.5 rounded border border-gray-700 flex items-center px-0.5">
                <div className="w-5 h-2 rounded-sm bg-emerald-500" />
              </div>
              <div className="w-0.5 h-2 bg-gray-400 rounded-r" />
            </div>
          </div>
        </div>

        {/* App Content */}
        <div className="flex-1 min-h-0 overflow-hidden flex flex-col">
          {children}
        </div>

        {/* Home Indicator – desktop only */}
        <div className="hidden md:flex justify-center pb-2 pt-1 bg-white flex-shrink-0">
          <div className="w-32 h-1 rounded-full bg-gray-300" />
        </div>
      </div>
    </div>
  );
}
