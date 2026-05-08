import React from 'react';
import { RouterProvider } from 'react-router';
import { router } from './routes';
import { AppProvider } from './context/AppContext';
import { MobileFrame } from './components/layout/MobileFrame';
import { Toaster } from 'sonner';

export default function App() {
  return (
    <AppProvider>
      <MobileFrame>
        <RouterProvider router={router} />
      </MobileFrame>
      <Toaster position="top-center" richColors />
    </AppProvider>
  );
}
