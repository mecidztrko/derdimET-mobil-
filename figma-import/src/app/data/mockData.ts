export type Role = 'MEAT_BUYER' | 'ANIMAL_SELLER' | 'SLAUGHTERHOUSE';

export interface User {
  id: string;
  name: string;
  email: string;
  role: Role;
  companyName?: string;
  city: string;
  verified: boolean;
  rating: number;
  reviewCount: number;
  avatarUrl?: string;
  phone?: string;
  address?: string;
  joinDate: string;
}

export interface MeatListing {
  id: string;
  title: string;
  meatType: string;
  quantity: number;
  pricePerKg: number;
  slaughterhouseName: string;
  slaughterhouseId: string;
  city: string;
  date: string;
  description: string;
  imageUrl?: string;
  isFavorited?: boolean;
}

export interface AnimalListing {
  id: string;
  category: 'küçükbaş' | 'büyükbaş';
  breed: string;
  age: string;
  count: number;
  pricePerHead: number;
  totalPrice: number;
  sellerName: string;
  sellerId: string;
  city: string;
  date: string;
  description: string;
  imageUrl?: string;
  isFavorited?: boolean;
}

export interface AnimalRequest {
  id: string;
  title: string;
  category: 'küçükbaş' | 'büyükbaş';
  requestedCount: number;
  weightRange: string;
  maxPricePerHead: number;
  slaughterhouseName: string;
  slaughterhouseId: string;
  city: string;
  date: string;
  description: string;
  isFavorited?: boolean;
}

export type OfferStatus = 'pending' | 'accepted' | 'rejected';

export interface Offer {
  id: string;
  listingId: string;
  listingTitle: string;
  listingType: 'meat' | 'animal' | 'request';
  fromUserId: string;
  fromUserName: string;
  fromUserRole: Role;
  toUserId: string;
  toUserName: string;
  offeredPrice: number;
  quantity?: number;
  note: string;
  status: OfferStatus;
  date: string;
}

export interface Message {
  id: string;
  conversationId: string;
  senderId: string;
  text: string;
  timestamp: string;
}

export interface Conversation {
  id: string;
  participantId: string;
  participantName: string;
  participantRole: Role;
  participantCompany?: string;
  lastMessage: string;
  lastMessageTime: string;
  unreadCount: number;
  relatedListingTitle?: string;
  avatarUrl?: string;
}

export interface FavoriteUser {
  id: string;
  name: string;
  role: Role;
  companyName?: string;
  city: string;
  verified: boolean;
  rating: number;
  avatarUrl?: string;
}

// ── Demo Users ──────────────────────────────────────────────────────────────

export const DEMO_USERS: User[] = [
  {
    id: 'user-1',
    name: 'Mehmet Yıldız',
    email: 'alici@demo.com',
    role: 'MEAT_BUYER',
    companyName: 'Yıldız Market Zinciri',
    city: 'İstanbul',
    verified: true,
    rating: 4.8,
    reviewCount: 34,
    avatarUrl: 'https://images.unsplash.com/photo-1738440702720-2a57e6ce2b0b?w=200&h=200&fit=crop&crop=face',
    phone: '+90 532 111 2233',
    address: 'Kadıköy, İstanbul',
    joinDate: 'Mart 2023',
  },
  {
    id: 'user-2',
    name: 'Hasan Demir',
    email: 'satici@demo.com',
    role: 'ANIMAL_SELLER',
    companyName: 'Demir Çiftliği',
    city: 'Konya',
    verified: true,
    rating: 4.6,
    reviewCount: 58,
    avatarUrl: 'https://images.unsplash.com/photo-1759836675618-9271f56d1903?w=200&h=200&fit=crop&crop=face',
    phone: '+90 542 222 3344',
    address: 'Merkez, Konya',
    joinDate: 'Ocak 2022',
  },
  {
    id: 'user-3',
    name: 'Fatma Kaya',
    email: 'kesimhane@demo.com',
    role: 'SLAUGHTERHOUSE',
    companyName: 'Kaya Et Kombinası A.Ş.',
    city: 'Ankara',
    verified: true,
    rating: 4.9,
    reviewCount: 120,
    avatarUrl: 'https://images.unsplash.com/photo-1610387694365-19fafcc86d86?w=200&h=200&fit=crop&crop=face',
    phone: '+90 555 333 4455',
    address: 'Sincan OSB, Ankara',
    joinDate: 'Nisan 2021',
  },
];

// ── Meat Listings (Kesimhanelerin Et İlanları) ────────────────────────────────

export const MEAT_LISTINGS: MeatListing[] = [
  {
    id: 'ml-1',
    title: 'Taze Dana Kıyma – Büyük Parti',
    meatType: 'Dana Kıyma',
    quantity: 500,
    pricePerKg: 185,
    slaughterhouseName: 'Kaya Et Kombinası A.Ş.',
    slaughterhouseId: 'user-3',
    city: 'Ankara',
    date: '2026-05-07',
    description: 'USDA onaylı soğuk zincir korunarak hazırlanmış taze dana kıyma. Vakum paketli, sevkiyat mümkün.',
    imageUrl: 'https://images.unsplash.com/photo-1759997956694-55411e03aecf?w=400&fit=crop',
    isFavorited: false,
  },
  {
    id: 'ml-2',
    title: 'Kuzu But – Bayram Öncesi',
    meatType: 'Kuzu But',
    quantity: 300,
    pricePerKg: 320,
    slaughterhouseName: 'Anadolu Kesimhane Ltd.',
    slaughterhouseId: 'sh-2',
    city: 'Bursa',
    date: '2026-05-06',
    description: 'Taze kesilmiş, bütün kuzu but. Hijyenik koşullarda hazırlandı.',
    imageUrl: 'https://images.unsplash.com/photo-1606403078259-70355b33a31a?w=400&fit=crop',
    isFavorited: true,
  },
  {
    id: 'ml-3',
    title: 'Dana Antrikot – Premium Kesim',
    meatType: 'Dana Antrikot',
    quantity: 150,
    pricePerKg: 420,
    slaughterhouseName: 'Özgür Et Sanayi',
    slaughterhouseId: 'sh-3',
    city: 'İzmir',
    date: '2026-05-05',
    description: 'Özel dinlendirilmiş premium antrikot. Restoran ve otellere özel hazırlandı.',
    imageUrl: 'https://images.unsplash.com/photo-1759997956694-55411e03aecf?w=400&fit=crop',
    isFavorited: false,
  },
  {
    id: 'ml-4',
    title: 'Koyun Kıyma – Acele Satış',
    meatType: 'Koyun Kıyma',
    quantity: 800,
    pricePerKg: 145,
    slaughterhouseName: 'Kaya Et Kombinası A.Ş.',
    slaughterhouseId: 'user-3',
    city: 'Ankara',
    date: '2026-05-04',
    description: 'Günlük taze koyun kıyma. Büyük parti. Hızlı teslim.',
    isFavorited: false,
  },
  {
    id: 'ml-5',
    title: 'Dana Kaburga – Özel Kesim',
    meatType: 'Dana Kaburga',
    quantity: 200,
    pricePerKg: 290,
    slaughterhouseName: 'Türkoğlu Kesimhane',
    slaughterhouseId: 'sh-4',
    city: 'Gaziantep',
    date: '2026-05-03',
    description: 'Sincan OSB\'de işlenmiş taze kaburga.',
    isFavorited: false,
  },
];

// ── Animal Listings (Satıcıların Hayvan İlanları) ─────────────────────────────

export const ANIMAL_LISTINGS: AnimalListing[] = [
  {
    id: 'al-1',
    category: 'büyükbaş',
    breed: 'Simental',
    age: '18–24 ay',
    count: 12,
    pricePerHead: 28000,
    totalPrice: 336000,
    sellerName: 'Hasan Demir (Demir Çiftliği)',
    sellerId: 'user-2',
    city: 'Konya',
    date: '2026-05-07',
    description: 'Kendi çiftliğimizden. Tüm aşıları tam, veteriner belgeli. 18-24 aylık besili Simental.',
    imageUrl: 'https://images.unsplash.com/photo-1759836675618-9271f56d1903?w=400&fit=crop',
    isFavorited: false,
  },
  {
    id: 'al-2',
    category: 'küçükbaş',
    breed: 'Merinos',
    age: '8–12 ay',
    count: 50,
    pricePerHead: 3800,
    totalPrice: 190000,
    sellerName: 'Ali Çelik Çiftliği',
    sellerId: 'seller-2',
    city: 'Sivas',
    date: '2026-05-06',
    description: 'Ot besili Merinos koyun. Doğal koşullarda yetiştirildi. Kurban uygun.',
    imageUrl: 'https://images.unsplash.com/photo-1775046163765-8852d1e0db19?w=400&fit=crop',
    isFavorited: true,
  },
  {
    id: 'al-3',
    category: 'büyükbaş',
    breed: 'Holstein',
    age: '24–36 ay',
    count: 8,
    pricePerHead: 42000,
    totalPrice: 336000,
    sellerName: 'Karataş Hayvancılık',
    sellerId: 'seller-3',
    city: 'Bursa',
    date: '2026-05-05',
    description: 'Et ağırlıklı beslenmiş Holstein. Yüksek etli.',
    imageUrl: 'https://images.unsplash.com/photo-1759836675618-9271f56d1903?w=400&fit=crop',
    isFavorited: false,
  },
  {
    id: 'al-4',
    category: 'küçükbaş',
    breed: 'Akkaraman',
    age: '6–10 ay',
    count: 80,
    pricePerHead: 3200,
    totalPrice: 256000,
    sellerName: 'Yusuf Öztürk Çiftliği',
    sellerId: 'seller-4',
    city: 'Urfa',
    date: '2026-05-04',
    description: 'Yerli Akkaraman kuzular. Küçük parti alıma da açığız.',
    imageUrl: 'https://images.unsplash.com/photo-1775046163765-8852d1e0db19?w=400&fit=crop',
    isFavorited: false,
  },
];

// ── Animal Requests (Kesimhanelerin Hayvan Talepleri) ─────────────────────────

export const ANIMAL_REQUESTS: AnimalRequest[] = [
  {
    id: 'ar-1',
    title: 'Büyükbaş Dana Arıyorum – Haziran Partisi',
    category: 'büyükbaş',
    requestedCount: 20,
    weightRange: '400–550 kg',
    maxPricePerHead: 35000,
    slaughterhouseName: 'Kaya Et Kombinası A.Ş.',
    slaughterhouseId: 'user-3',
    city: 'Ankara',
    date: '2026-05-07',
    description: 'Haziran başı teslimat. Belgeli veteriner raporu şart.',
  },
  {
    id: 'ar-2',
    title: 'Kurban Sezonu – Koyun Talep',
    category: 'küçükbaş',
    requestedCount: 100,
    weightRange: '35–50 kg',
    maxPricePerHead: 4500,
    slaughterhouseName: 'Anadolu Kesimhane Ltd.',
    slaughterhouseId: 'sh-2',
    city: 'Bursa',
    date: '2026-05-06',
    description: 'Kurban mevsimi için toplu koyun alımı. Tüm aşılar tam olacak.',
  },
  {
    id: 'ar-3',
    title: 'Sürekli Dana Tedariki Arıyorum',
    category: 'büyükbaş',
    requestedCount: 15,
    weightRange: '350–500 kg',
    maxPricePerHead: 30000,
    slaughterhouseName: 'Özgür Et Sanayi',
    slaughterhouseId: 'sh-3',
    city: 'İzmir',
    date: '2026-05-05',
    description: 'Her ay düzenli 15 baş. Uzun vadeli tedarik ilişkisi.',
  },
  {
    id: 'ar-4',
    title: 'Kuzu Alımı – Sürekli Talep',
    category: 'küçükbaş',
    requestedCount: 40,
    weightRange: '25–40 kg',
    maxPricePerHead: 3600,
    slaughterhouseName: 'Kaya Et Kombinası A.Ş.',
    slaughterhouseId: 'user-3',
    city: 'Ankara',
    date: '2026-05-04',
    description: 'Küçük parti de kabul. Haftalık düzenli alım yapıyoruz.',
  },
];

// ── Offers ───────────────────────────────────────────────────────────────────

export const MOCK_OFFERS: Offer[] = [
  {
    id: 'off-1',
    listingId: 'ml-1',
    listingTitle: 'Taze Dana Kıyma – Büyük Parti',
    listingType: 'meat',
    fromUserId: 'user-1',
    fromUserName: 'Mehmet Yıldız',
    fromUserRole: 'MEAT_BUYER',
    toUserId: 'user-3',
    toUserName: 'Kaya Et Kombinası',
    offeredPrice: 175,
    quantity: 200,
    note: '200 kg için teklif veriyorum. Hızlı karar bekliyorum.',
    status: 'pending',
    date: '2026-05-07',
  },
  {
    id: 'off-2',
    listingId: 'ml-2',
    listingTitle: 'Kuzu But – Bayram Öncesi',
    listingType: 'meat',
    fromUserId: 'user-1',
    fromUserName: 'Mehmet Yıldız',
    fromUserRole: 'MEAT_BUYER',
    toUserId: 'sh-2',
    toUserName: 'Anadolu Kesimhane',
    offeredPrice: 300,
    quantity: 100,
    note: 'Mağazalarımız için 100 kg alabilirim.',
    status: 'accepted',
    date: '2026-05-05',
  },
  {
    id: 'off-3',
    listingId: 'ml-3',
    listingTitle: 'Dana Antrikot – Premium Kesim',
    listingType: 'meat',
    fromUserId: 'user-1',
    fromUserName: 'Mehmet Yıldız',
    fromUserRole: 'MEAT_BUYER',
    toUserId: 'sh-3',
    toUserName: 'Özgür Et Sanayi',
    offeredPrice: 390,
    quantity: 50,
    note: 'Restoranlarım için düzenli alım yapabilirim.',
    status: 'rejected',
    date: '2026-05-03',
  },
];

export const SELLER_OFFERS: Offer[] = [
  {
    id: 'soff-1',
    listingId: 'ar-1',
    listingTitle: 'Büyükbaş Dana Arıyorum – Haziran Partisi',
    listingType: 'request',
    fromUserId: 'user-2',
    fromUserName: 'Hasan Demir',
    fromUserRole: 'ANIMAL_SELLER',
    toUserId: 'user-3',
    toUserName: 'Kaya Et Kombinası',
    offeredPrice: 32000,
    quantity: 20,
    note: 'Çiftliğimden 20 baş sağlıklı Simental. Teslimat Haziran 1.',
    status: 'pending',
    date: '2026-05-07',
  },
  {
    id: 'soff-2',
    listingId: 'ar-2',
    listingTitle: 'Kurban Sezonu – Koyun Talep',
    listingType: 'request',
    fromUserId: 'user-2',
    fromUserName: 'Hasan Demir',
    fromUserRole: 'ANIMAL_SELLER',
    toUserId: 'sh-2',
    toUserName: 'Anadolu Kesimhane',
    offeredPrice: 4200,
    quantity: 30,
    note: '30 baş için teklif veriyorum. Kalan için de müsaitim.',
    status: 'accepted',
    date: '2026-05-04',
  },
];

export const SH_OFFERS: Offer[] = [
  {
    id: 'shoff-1',
    listingId: 'al-1',
    listingTitle: 'Simental Büyükbaş – 12 Baş',
    listingType: 'animal',
    fromUserId: 'user-3',
    fromUserName: 'Kaya Et Kombinası',
    fromUserRole: 'SLAUGHTERHOUSE',
    toUserId: 'user-2',
    toUserName: 'Hasan Demir',
    offeredPrice: 26000,
    quantity: 12,
    note: '12 baş için talebimiz var. Haziran başı teslim.',
    status: 'pending',
    date: '2026-05-06',
  },
  {
    id: 'shoff-2',
    listingId: 'al-2',
    listingTitle: 'Merinos Koyun – 50 Baş',
    listingType: 'animal',
    fromUserId: 'user-3',
    fromUserName: 'Kaya Et Kombinası',
    fromUserRole: 'SLAUGHTERHOUSE',
    toUserId: 'seller-2',
    toUserName: 'Ali Çelik Çiftliği',
    offeredPrice: 3600,
    quantity: 30,
    note: '30 başlık alım yapabiliriz.',
    status: 'accepted',
    date: '2026-05-05',
  },
];

// ── Conversations ────────────────────────────────────────────────────────────

export const BUYER_CONVERSATIONS: Conversation[] = [
  {
    id: 'conv-1',
    participantId: 'user-3',
    participantName: 'Fatma Kaya',
    participantRole: 'SLAUGHTERHOUSE',
    participantCompany: 'Kaya Et Kombinası A.Ş.',
    lastMessage: 'Teklifinizi değerlendireceğiz, yarın döneceğiz.',
    lastMessageTime: '14:32',
    unreadCount: 2,
    relatedListingTitle: 'Taze Dana Kıyma',
    avatarUrl: 'https://images.unsplash.com/photo-1610387694365-19fafcc86d86?w=100&h=100&fit=crop&crop=face',
  },
  {
    id: 'conv-2',
    participantId: 'sh-2',
    participantName: 'Burak Aydın',
    participantRole: 'SLAUGHTERHOUSE',
    participantCompany: 'Anadolu Kesimhane Ltd.',
    lastMessage: 'Evet, 100 kg için anlaştık. Fatura kesiyorum.',
    lastMessageTime: 'Dün',
    unreadCount: 0,
    relatedListingTitle: 'Kuzu But',
  },
];

export const SELLER_CONVERSATIONS: Conversation[] = [
  {
    id: 'sconv-1',
    participantId: 'user-3',
    participantName: 'Fatma Kaya',
    participantRole: 'SLAUGHTERHOUSE',
    participantCompany: 'Kaya Et Kombinası A.Ş.',
    lastMessage: 'Hayvanların belgelerini paylaşabilir misiniz?',
    lastMessageTime: '10:15',
    unreadCount: 1,
    relatedListingTitle: 'Büyükbaş Dana – Haziran Partisi',
    avatarUrl: 'https://images.unsplash.com/photo-1610387694365-19fafcc86d86?w=100&h=100&fit=crop&crop=face',
  },
  {
    id: 'sconv-2',
    participantId: 'sh-2',
    participantName: 'Burak Aydın',
    participantRole: 'SLAUGHTERHOUSE',
    participantCompany: 'Anadolu Kesimhane Ltd.',
    lastMessage: '30 başlık anlaşma tamam, yarın transfer.',
    lastMessageTime: 'Dün',
    unreadCount: 0,
    relatedListingTitle: 'Merinos Koyun',
  },
];

export const SH_CONVERSATIONS: Conversation[] = [
  {
    id: 'shconv-1',
    participantId: 'user-2',
    participantName: 'Hasan Demir',
    participantRole: 'ANIMAL_SELLER',
    participantCompany: 'Demir Çiftliği',
    lastMessage: 'Belgeler hazır, ne zaman müsaitsiniz?',
    lastMessageTime: '11:00',
    unreadCount: 3,
    relatedListingTitle: 'Simental 12 Baş',
    avatarUrl: 'https://images.unsplash.com/photo-1759836675618-9271f56d1903?w=100&h=100&fit=crop&crop=face',
  },
  {
    id: 'shconv-2',
    participantId: 'user-1',
    participantName: 'Mehmet Yıldız',
    participantRole: 'MEAT_BUYER',
    participantCompany: 'Yıldız Market Zinciri',
    lastMessage: 'Fatura için IBAN\'ı gönderir misiniz?',
    lastMessageTime: 'Dün',
    unreadCount: 0,
    relatedListingTitle: 'Dana Kıyma 200 kg',
    avatarUrl: 'https://images.unsplash.com/photo-1738440702720-2a57e6ce2b0b?w=100&h=100&fit=crop&crop=face',
  },
];

// ── Favorite Users ────────────────────────────────────────────────────────────

export const FAVORITE_SELLERS: FavoriteUser[] = [
  {
    id: 'user-2',
    name: 'Hasan Demir',
    role: 'ANIMAL_SELLER',
    companyName: 'Demir Çiftliği',
    city: 'Konya',
    verified: true,
    rating: 4.6,
    avatarUrl: 'https://images.unsplash.com/photo-1759836675618-9271f56d1903?w=100&h=100&fit=crop&crop=face',
  },
  {
    id: 'seller-3',
    name: 'Murat Karataş',
    role: 'ANIMAL_SELLER',
    companyName: 'Karataş Hayvancılık',
    city: 'Bursa',
    verified: true,
    rating: 4.7,
  },
];

export const FAVORITE_SLAUGHTERHOUSES: FavoriteUser[] = [
  {
    id: 'user-3',
    name: 'Fatma Kaya',
    role: 'SLAUGHTERHOUSE',
    companyName: 'Kaya Et Kombinası A.Ş.',
    city: 'Ankara',
    verified: true,
    rating: 4.9,
    avatarUrl: 'https://images.unsplash.com/photo-1610387694365-19fafcc86d86?w=100&h=100&fit=crop&crop=face',
  },
  {
    id: 'sh-2',
    name: 'Burak Aydın',
    role: 'SLAUGHTERHOUSE',
    companyName: 'Anadolu Kesimhane Ltd.',
    city: 'Bursa',
    verified: false,
    rating: 4.3,
  },
];

// ── Chat Messages ─────────────────────────────────────────────────────────────

export const CHAT_MESSAGES: Message[] = [
  { id: 'm1', conversationId: 'conv-1', senderId: 'user-3', text: 'Merhaba, teklifinizi aldık.', timestamp: '13:50' },
  { id: 'm2', conversationId: 'conv-1', senderId: 'user-1', text: 'Teşekkürler. 200 kg için 175 TL/kg önerdim.', timestamp: '13:52' },
  { id: 'm3', conversationId: 'conv-1', senderId: 'user-3', text: 'Fiyatı değerlendiriyoruz. Kalite belgeleriniz var mı?', timestamp: '14:05' },
  { id: 'm4', conversationId: 'conv-1', senderId: 'user-1', text: 'Evet, gıda güvenliği sertifikamız tam. İstediğinizde gönderirim.', timestamp: '14:10' },
  { id: 'm5', conversationId: 'conv-1', senderId: 'user-3', text: 'Harika. Teklifinizi değerlendireceğiz, yarın döneceğiz.', timestamp: '14:32' },
];

export const getMockMessages = (conversationId: string, currentUserId: string): Message[] => {
  const base = [
    { id: 'm1', conversationId, senderId: 'other', text: 'Merhaba, ilanınızı gördüm ilgileniyorum.', timestamp: '10:00' },
    { id: 'm2', conversationId, senderId: currentUserId, text: 'Merhaba! Buyurun, detay sormak ister misiniz?', timestamp: '10:03' },
    { id: 'm3', conversationId, senderId: 'other', text: 'Evet. Teslimat ne zaman yapılabilir?', timestamp: '10:05' },
    { id: 'm4', conversationId, senderId: currentUserId, text: 'Anlaşma olursa 3 iş günü içinde hazır olur.', timestamp: '10:08' },
    { id: 'm5', conversationId, senderId: 'other', text: 'Belgeler tam mı? Veteriner raporu gerekli.', timestamp: '10:12' },
    { id: 'm6', conversationId, senderId: currentUserId, text: 'Tüm belgelerimiz güncel ve tam. Sağlık raporları mevcut.', timestamp: '10:15' },
    { id: 'm7', conversationId, senderId: 'other', text: 'Anlaşalım o zaman. Fiyatı gözden geçirir misiniz?', timestamp: '14:30' },
  ];
  return base;
};
