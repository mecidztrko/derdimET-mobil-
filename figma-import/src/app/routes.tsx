import React from 'react';
import { createBrowserRouter, Navigate } from 'react-router';
import { LoginScreen } from './screens/auth/LoginScreen';
import { RegisterScreen } from './screens/auth/RegisterScreen';
import { ProfileScreen } from './screens/profile/ProfileScreen';
import { SearchScreen } from './screens/search/SearchScreen';
import { OffersScreen } from './screens/offers/OffersScreen';
import { ChatScreen } from './screens/offers/ChatScreen';
import { CreateListingScreen } from './screens/create/CreateListingScreen';
import { MainLayout } from './components/layout/MainLayout';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <Navigate to="/login" replace />,
  },
  {
    path: '/login',
    element: <LoginScreen />,
  },
  {
    path: '/register',
    element: <RegisterScreen />,
  },
  {
    path: '/app',
    element: <MainLayout />,
    children: [
      { index: true, element: <Navigate to="/app/search" replace /> },
      { path: 'profile', element: <ProfileScreen /> },
      { path: 'search', element: <SearchScreen /> },
      { path: 'offers', element: <OffersScreen /> },
      { path: 'create', element: <CreateListingScreen /> },
    ],
  },
  {
    path: '/app/chat/:id',
    element: <ChatScreen />,
  },
  {
    path: '*',
    element: <Navigate to="/login" replace />,
  },
]);
