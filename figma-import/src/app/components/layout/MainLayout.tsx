import React from 'react';
import { Outlet } from 'react-router';
import { TabBar } from './TabBar';
import { TopBar } from './TopBar';

interface MainLayoutProps {
  showTopBar?: boolean;
  topBarTitle?: string;
  showBell?: boolean;
}

export function MainLayout() {
  return (
    <div className="flex flex-col h-full overflow-hidden">
      <TopBar showBell />
      <div className="flex-1 overflow-hidden">
        <Outlet />
      </div>
      <TabBar />
    </div>
  );
}
