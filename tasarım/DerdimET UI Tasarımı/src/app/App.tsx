import { BrowserRouter, Routes, Route, Navigate } from "react-router";
import { AuthProvider, useAuth } from "./context/AuthContext";
import { LoginPage } from "./pages/LoginPage";
import { RegisterPage } from "./pages/RegisterPage";
import { SearchPage } from "./pages/SearchPage";
import { ProfilePage } from "./pages/ProfilePage";
import { OffersPage } from "./pages/OffersPage";
import { MessagesPage } from "./pages/MessagesPage";
import { CreateListingPage } from "./pages/CreateListingPage";
import { ChatPage } from "./pages/ChatPage";
import { ListingDetailPage } from "./pages/ListingDetailPage";

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { user } = useAuth();
  return user ? <>{children}</> : <Navigate to="/login" replace />;
}

function AppRoutes() {
  const { user } = useAuth();

  return (
    <Routes>
      <Route path="/login" element={user ? <Navigate to="/search" replace /> : <LoginPage />} />
      <Route path="/register" element={user ? <Navigate to="/search" replace /> : <RegisterPage />} />

      <Route path="/search" element={<ProtectedRoute><SearchPage /></ProtectedRoute>} />
      <Route path="/listing/:id" element={<ProtectedRoute><ListingDetailPage /></ProtectedRoute>} />
      <Route path="/profile" element={<ProtectedRoute><ProfilePage /></ProtectedRoute>} />
      <Route path="/offers" element={<ProtectedRoute><OffersPage /></ProtectedRoute>} />
      <Route path="/messages" element={<ProtectedRoute><MessagesPage /></ProtectedRoute>} />
      <Route path="/create" element={<ProtectedRoute><CreateListingPage /></ProtectedRoute>} />
      <Route path="/chat/:id" element={<ProtectedRoute><ChatPage /></ProtectedRoute>} />

      <Route path="/" element={<Navigate to="/search" replace />} />
      <Route path="*" element={<Navigate to="/search" replace />} />
    </Routes>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <div
          className="min-h-screen bg-background text-foreground"
          style={{ maxWidth: "430px", margin: "0 auto" }}
        >
          <AppRoutes />
        </div>
      </AuthProvider>
    </BrowserRouter>
  );
}