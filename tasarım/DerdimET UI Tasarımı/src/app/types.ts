export type UserRole = "MEAT_BUYER" | "ANIMAL_SELLER" | "SLAUGHTERHOUSE";

export interface User {
  id: string;
  email: string;
  name: string;
  role: UserRole;
  companyName?: string;
  city?: string;
  address?: string;
  verified: boolean;
  avatar?: string;
}

export interface Listing {
  id: string;
  title: string;
  description: string;
  price: number;
  createdAt: Date;
  sellerId: string;
  sellerName: string;
  sellerCompany?: string;
  city?: string;
}

export interface AnimalListing extends Listing {
  category: "Küçükbaş" | "Büyükbaş";
  breed: string;
  age: number;
  quantity: number;
}

export interface MeatListing extends Listing {
  meatType: "Dana" | "Koyun" | "Kuzu" | "Keçi";
  weight: number;
}

export interface Offer {
  id: string;
  listingId: string;
  listingTitle: string;
  buyerId: string;
  buyerName: string;
  sellerId: string;
  sellerName: string;
  amount: number;
  status: "pending" | "accepted" | "rejected";
  createdAt: Date;
}

export interface Message {
  id: string;
  conversationId: string;
  senderId: string;
  senderName: string;
  content: string;
  createdAt: Date;
}

export interface Conversation {
  id: string;
  participants: User[];
  lastMessage?: Message;
  unreadCount: number;
}
