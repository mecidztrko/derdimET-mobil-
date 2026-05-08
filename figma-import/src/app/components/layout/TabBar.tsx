import React from 'react';
import { useNavigate, useLocation } from 'react-router';
import {
  User, Search, FileText, Plus
} from 'lucide-react';
import { useApp } from '../../context/AppContext';

interface TabItem {
  label: string;
  icon: React.ReactNode;
  path: string;
}

export function TabBar() {
  const { user } = useApp();
  const navigate = useNavigate();
  const location = useLocation();

  const buyerTabs: TabItem[] = [
    { label: 'Profil', icon: <User size={22} />, path: '/app/profile' },
    { label: 'Arama', icon: <Search size={22} />, path: '/app/search' },
    { label: 'Tekliflerim', icon: <FileText size={22} />, path: '/app/offers' },
  ];

  const sellerTabs: TabItem[] = [
    { label: 'Profil', icon: <User size={22} />, path: '/app/profile' },
    { label: 'Arama', icon: <Search size={22} />, path: '/app/search' },
    { label: 'Tekliflerim', icon: <FileText size={22} />, path: '/app/offers' },
    { label: 'İlan Ver', icon: <Plus size={22} />, path: '/app/create' },
  ];

  const tabs =
    user?.role === 'MEAT_BUYER'
      ? buyerTabs
      : sellerTabs;

  const isActive = (path: string) => location.pathname.startsWith(path);

  return (
    <div className="flex items-center bg-white border-t border-gray-100 flex-shrink-0 pb-safe">
      {tabs.map((tab) => {
        const active = isActive(tab.path);
        const isCreate = tab.path === '/app/create';

        if (isCreate) {
          return (
            <button
              key={tab.path}
              onClick={() => navigate(tab.path)}
              className="flex-1 flex flex-col items-center justify-center py-2 gap-0.5"
            >
              <div
                className={`w-11 h-11 rounded-2xl flex items-center justify-center transition-all ${
                  active
                    ? 'bg-[#1B3A6B] shadow-lg shadow-[#1B3A6B]/30'
                    : 'bg-[#1B3A6B]/10'
                }`}
              >
                <span className={active ? 'text-white' : 'text-[#1B3A6B]'}>
                  {tab.icon}
                </span>
              </div>
              <span
                className={`text-[10px] font-medium mt-0.5 ${
                  active ? 'text-[#1B3A6B]' : 'text-gray-400'
                }`}
              >
                {tab.label}
              </span>
            </button>
          );
        }

        return (
          <button
            key={tab.path}
            onClick={() => navigate(tab.path)}
            className="flex-1 flex flex-col items-center justify-center py-3 gap-0.5"
          >
            <div
              className={`w-6 h-6 flex items-center justify-center transition-all ${
                active ? 'text-[#1B3A6B]' : 'text-gray-400'
              }`}
            >
              {tab.icon}
            </div>
            <span
              className={`text-[10px] font-medium ${
                active ? 'text-[#1B3A6B] font-semibold' : 'text-gray-400'
              }`}
            >
              {tab.label}
            </span>
            {active && (
              <div className="w-1 h-1 rounded-full bg-[#1B3A6B] mt-0.5" />
            )}
          </button>
        );
      })}
    </div>
  );
}
