import { createContext, useContext, useState, type ReactNode } from "react";
import type { User, UserRole } from "../types";

interface AuthContextType {
  user: User | null;
  login: (email: string, password: string, demoRole?: UserRole) => Promise<void>;
  register: (data: RegisterData) => Promise<void>;
  logout: () => void;
  switchRole: (role: UserRole) => void;
}

interface RegisterData {
  email: string;
  password: string;
  name: string;
  role: User["role"];
  companyName?: string;
  city?: string;
  address?: string;
}

const demoUsers: Record<UserRole, User> = {
  MEAT_BUYER: {
    id: "buyer-1",
    email: "alici@demo.com",
    name: "Kemal Çelik",
    role: "MEAT_BUYER",
    companyName: "Çelik Gıda Marketleri",
    city: "İstanbul",
    verified: true,
    avatar: "",
  },
  ANIMAL_SELLER: {
    id: "seller-1",
    email: "satici@demo.com",
    name: "Mustafa Kara",
    role: "ANIMAL_SELLER",
    companyName: "Kara Hayvancılık",
    city: "Konya",
    verified: true,
    avatar: "",
  },
  SLAUGHTERHOUSE: {
    id: "kesim-1",
    email: "kesimhane@demo.com",
    name: "Ahmet Yılmaz",
    role: "SLAUGHTERHOUSE",
    companyName: "Yılmaz Et ve Hayvan Ürünleri",
    city: "İzmir",
    verified: true,
    avatar: "",
  },
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(() => {
    const stored = localStorage.getItem("derdimET_user");
    return stored ? JSON.parse(stored) : null;
  });

  const login = async (email: string, password: string, demoRole?: UserRole) => {
    await new Promise((resolve) => setTimeout(resolve, 700));

    const role: UserRole = demoRole || "SLAUGHTERHOUSE";
    const mockUser = { ...demoUsers[role], email: email || demoUsers[role].email };

    setUser(mockUser);
    localStorage.setItem("derdimET_user", JSON.stringify(mockUser));
  };

  const register = async (data: RegisterData) => {
    await new Promise((resolve) => setTimeout(resolve, 800));

    const mockUser: User = {
      id: Math.random().toString(36).substring(7),
      email: data.email,
      name: data.name,
      role: data.role,
      companyName: data.companyName,
      city: data.city,
      address: data.address,
      verified: false,
    };

    setUser(mockUser);
    localStorage.setItem("derdimET_user", JSON.stringify(mockUser));
  };

  const switchRole = (role: UserRole) => {
    const newUser = { ...demoUsers[role] };
    setUser(newUser);
    localStorage.setItem("derdimET_user", JSON.stringify(newUser));
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem("derdimET_user");
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout, switchRole }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return context;
}
