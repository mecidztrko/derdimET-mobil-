export interface Review {
  id: string;
  reviewer: string;
  rating: number;
  comment: string;
  date: string;
  avatar: string;
}

export interface SellerProfile {
  id: string;
  name: string;
  company: string;
  city: string;
  verified: boolean;
  rating: number;
  reviewCount: number;
  memberSince: string;
  totalListings: number;
  avatar: string;
  phone?: string;
}

export interface MeatListingFull {
  id: string;
  title: string;
  type: "Dana" | "Kuzu" | "Koyun" | "Keçi";
  weight: number;
  price: number;
  unit: string;
  seller: string;
  city: string;
  district: string;
  date: string;
  verified: boolean;
  rating: number;
  badge: string | null;
  images: string[];
  description: string;
  sellerId: string;
  sellerProfile: SellerProfile;
  reviews: Review[];
  viewCount: number;
  favoriteCount: number;
}

export interface AnimalListingFull {
  id: string;
  title: string;
  type: "Küçükbaş" | "Büyükbaş";
  breed: string;
  quantity: number;
  weight: number;
  price: number;
  unit: string;
  seller: string;
  city: string;
  district: string;
  date: string;
  verified: boolean;
  rating: number;
  badge: string | null;
  images: string[];
  description: string;
  sellerId: string;
  sellerProfile: SellerProfile;
  reviews: Review[];
  viewCount: number;
  favoriteCount: number;
}

export type AnyListing = MeatListingFull | AnimalListingFull;

// ---- Seller Profiles ----
const sellers: Record<string, SellerProfile> = {
  "s1": {
    id: "s1",
    name: "Yılmaz Kesimhane",
    company: "Yılmaz Et ve Gıda A.Ş.",
    city: "İzmir",
    verified: true,
    rating: 4.8,
    reviewCount: 134,
    memberSince: "2019",
    totalListings: 48,
    avatar: "Y",
    phone: "+90 530 555 01 01",
  },
  "s2": {
    id: "s2",
    name: "Aydın Et ve Gıda",
    company: "Aydın Et Gıda Ltd. Şti.",
    city: "Aydın",
    verified: true,
    rating: 4.6,
    reviewCount: 87,
    memberSince: "2020",
    totalListings: 31,
    avatar: "A",
  },
  "s3": {
    id: "s3",
    name: "Manisa Kesimhane",
    company: "Manisa Et İşleme Tesisi",
    city: "Manisa",
    verified: false,
    rating: 4.2,
    reviewCount: 45,
    memberSince: "2021",
    totalListings: 19,
    avatar: "M",
  },
  "s4": {
    id: "s4",
    name: "Ege Et İşleme",
    company: "Ege Et ve Süt Ürünleri",
    city: "İzmir",
    verified: true,
    rating: 4.5,
    reviewCount: 62,
    memberSince: "2020",
    totalListings: 27,
    avatar: "E",
  },
  "s5": {
    id: "s5",
    name: "Premium Et Merkezi",
    company: "Premium Gıda ve Tarım A.Ş.",
    city: "İstanbul",
    verified: true,
    rating: 4.9,
    reviewCount: 218,
    memberSince: "2018",
    totalListings: 92,
    avatar: "P",
    phone: "+90 212 555 88 99",
  },
  "s6": {
    id: "s6",
    name: "Akdeniz Kesimhane",
    company: "Akdeniz Et Gıda Sanayi",
    city: "Antalya",
    verified: true,
    rating: 4.7,
    reviewCount: 99,
    memberSince: "2019",
    totalListings: 37,
    avatar: "A",
  },
  "s7": {
    id: "s7",
    name: "Konya Et Dağıtım",
    company: "Konya Et Dağıtım ve Tic. Ltd.",
    city: "Konya",
    verified: true,
    rating: 4.4,
    reviewCount: 71,
    memberSince: "2020",
    totalListings: 44,
    avatar: "K",
  },
  "s8": {
    id: "s8",
    name: "Kara Hayvancılık",
    company: "Kara Hayvancılık ve Tarım",
    city: "Konya",
    verified: true,
    rating: 4.7,
    reviewCount: 108,
    memberSince: "2017",
    totalListings: 56,
    avatar: "K",
    phone: "+90 332 555 44 55",
  },
  "s9": {
    id: "s9",
    name: "Ege Çiftliği",
    company: "Ege Tarım ve Hayvancılık A.Ş.",
    city: "İzmir",
    verified: true,
    rating: 4.9,
    reviewCount: 195,
    memberSince: "2016",
    totalListings: 78,
    avatar: "E",
    phone: "+90 232 555 22 33",
  },
  "s10": {
    id: "s10",
    name: "Trakya Çiftliği",
    company: "Trakya Hayvancılık Ltd. Şti.",
    city: "Edirne",
    verified: false,
    rating: 4.1,
    reviewCount: 38,
    memberSince: "2022",
    totalListings: 14,
    avatar: "T",
  },
  "s11": {
    id: "s11",
    name: "Marmara Besicilik",
    company: "Marmara Et ve Besicilik A.Ş.",
    city: "Bursa",
    verified: true,
    rating: 4.8,
    reviewCount: 162,
    memberSince: "2018",
    totalListings: 63,
    avatar: "M",
    phone: "+90 224 555 77 88",
  },
  "s12": {
    id: "s12",
    name: "Karadeniz Çiftliği",
    company: "Karadeniz Tarım ve Hayvancılık",
    city: "Trabzon",
    verified: true,
    rating: 4.5,
    reviewCount: 74,
    memberSince: "2019",
    totalListings: 29,
    avatar: "K",
  },
  "s13": {
    id: "s13",
    name: "Orta Anadolu Et",
    company: "Orta Anadolu Et Ürünleri Tic.",
    city: "Ankara",
    verified: true,
    rating: 4.6,
    reviewCount: 121,
    memberSince: "2019",
    totalListings: 41,
    avatar: "O",
    phone: "+90 312 555 66 77",
  },
};

// ---- Reviews ----
const reviewPool: Review[] = [
  { id: "r1", reviewer: "Mehmet A.", rating: 5, comment: "Çok kaliteli ürün, tarife uygun tartı ve hızlı teslimat. Kesinlikle tekrar alacağım.", date: "3 gün önce", avatar: "M" },
  { id: "r2", reviewer: "Ayşe K.", rating: 5, comment: "Sertifikalı ve güvenilir bir satıcı. Fiyat/kalite dengesi mükemmel.", date: "1 hafta önce", avatar: "A" },
  { id: "r3", reviewer: "Hüseyin T.", rating: 4, comment: "Ürün beklentimi karşıladı, iletişim iyiydi ama teslimat biraz gecikti.", date: "2 hafta önce", avatar: "H" },
  { id: "r4", reviewer: "Fatma Ş.", rating: 5, comment: "Canlı hayvan sağlıklı ve istenen ağırlıkta teslim edildi. Teşekkürler!", date: "3 hafta önce", avatar: "F" },
  { id: "r5", reviewer: "Ali R.", rating: 4, comment: "Fotoğrafla birebir ürün geldi, güvenle alışveriş yapılabilir.", date: "1 ay önce", avatar: "A" },
  { id: "r6", reviewer: "Zeynep M.", rating: 3, comment: "Ürün iyi ancak iletişim bazen zor olabiliyor. Fiyat makul.", date: "1 ay önce", avatar: "Z" },
];

// ---- Meat Listings ----
export const meatListings: MeatListingFull[] = [
  {
    id: "m1",
    title: "Dana Eti – Karkas",
    type: "Dana",
    weight: 450,
    price: 185,
    unit: "kg",
    seller: "Yılmaz Kesimhane",
    city: "İzmir",
    district: "Bornova",
    date: "2 saat önce",
    verified: true,
    rating: 4.8,
    badge: "Çok Satan",
    images: [
      "https://images.unsplash.com/photo-1777962822492-c0d637951f24?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxmcmVzaCUyMGJlZWYlMjBtZWF0JTIwYnV0Y2hlcnxlbnwxfHx8fDE3NzgyMjg0MTB8MA&ixlib=rb-4.1.0&q=80&w=1080",
      "https://images.unsplash.com/photo-1740586222627-48338edac67d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxtZWF0JTIwbWFya2V0JTIwYnV0Y2hlciUyMHNob3B8ZW58MXx8fHwxNzc4MjI4NDExfDA&ixlib=rb-4.1.0&q=80&w=1080",
      "https://images.unsplash.com/photo-1763140446057-9becaa30b868?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxtZWF0JTIwY2FyY2FzcyUyMHdob2xlc2FsZSUyMGJ1dGNoZXJ5fGVufDF8fHx8MTc3ODIyODQxNXww&ixlib=rb-4.1.0&q=80&w=1080",
    ],
    description: "Taze kesilmiş, 450 kg'lık bütün dana karkası. Hayvanlar veteriner kontrolünden geçmiş, sağlık sertifikalıdır. İzmir merkez veya çevre ilçelere soğuk zincir ile teslimat yapılmaktadır. Toplu alımda fiyat indirimi uygulanmaktadır. Minimum alım 50 kg'dır.",
    sellerId: "s1",
    sellerProfile: sellers["s1"],
    reviews: [reviewPool[0], reviewPool[1], reviewPool[4]],
    viewCount: 312,
    favoriteCount: 24,
  },
  {
    id: "m2",
    title: "Kuzu Eti – Taze Karkas",
    type: "Kuzu",
    weight: 120,
    price: 230,
    unit: "kg",
    seller: "Aydın Et ve Gıda",
    city: "Aydın",
    district: "Merkez",
    date: "4 saat önce",
    verified: true,
    rating: 4.6,
    badge: null,
    images: [
      "https://images.unsplash.com/photo-1719502044471-2187a7d36a81?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxsYW1iJTIwc2hlZXAlMjBsaXZlc3RvY2slMjBmYXJtfGVufDF8fHx8MTc3ODIyODQxMHww&ixlib=rb-4.1.0&q=80&w=1080",
      "https://images.unsplash.com/photo-1777962822492-c0d637951f24?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxmcmVzaCUyMGJlZWYlMjBtZWF0JTIwYnV0Y2hlcnxlbnwxfHx8fDE3NzgyMjg0MTB8MA&ixlib=rb-4.1.0&q=80&w=1080",
    ],
    description: "Doğal beslenmiş, taze kuzu karkası. 120 kg stok mevcut. Kurban mevsimi öncesi uygun fiyatla sunulmaktadır. Aydın ve çevre ilçelere teslimat yapılır. Veteriner raporu mevcuttur.",
    sellerId: "s2",
    sellerProfile: sellers["s2"],
    reviews: [reviewPool[1], reviewPool[4]],
    viewCount: 187,
    favoriteCount: 15,
  },
  {
    id: "m3",
    title: "Koyun Eti – Yarım Karkas",
    type: "Koyun",
    weight: 350,
    price: 165,
    unit: "kg",
    seller: "Manisa Kesimhane",
    city: "Manisa",
    district: "Salihli",
    date: "6 saat önce",
    verified: false,
    rating: 4.2,
    badge: null,
    images: [
      "https://images.unsplash.com/photo-1763140446057-9becaa30b868?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxtZWF0JTIwY2FyY2FzcyUyMHdob2xlc2FsZSUyMGJ1dGNoZXJ5fGVufDF8fHx8MTc3ODIyODQxNXww&ixlib=rb-4.1.0&q=80&w=1080",
      "https://images.unsplash.com/photo-1740586222627-48338edac67d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxtZWF0JTIwbWFya2V0JTIwYnV0Y2hlciUyMHNob3B8ZW58MXx8fHwxNzc4MjI4NDExfDA&ixlib=rb-4.1.0&q=80&w=1080",
    ],
    description: "Yarım karkas koyun eti, 350 kg stok mevcuttur. Toptan alımlarda fiyatta anlaşılabilir. Ege bölgesine teslimat imkânı. Soğuk depoda bekletilmekte olup taze ürün garantisi verilmektedir.",
    sellerId: "s3",
    sellerProfile: sellers["s3"],
    reviews: [reviewPool[2], reviewPool[5]],
    viewCount: 98,
    favoriteCount: 7,
  },
  {
    id: "m4",
    title: "Keçi Eti – Taze",
    type: "Keçi",
    weight: 200,
    price: 150,
    unit: "kg",
    seller: "Ege Et İşleme",
    city: "İzmir",
    district: "Torbalı",
    date: "1 gün önce",
    verified: true,
    rating: 4.5,
    badge: "Yeni",
    images: [
      "https://images.unsplash.com/photo-1762571808926-2555640f12a6?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxnb2F0JTIwYW5pbWFsJTIwcGFzdHVyZSUyMGdyZWVufGVufDF8fHx8MTc3ODIyODQxNXww&ixlib=rb-4.1.0&q=80&w=1080",
      "https://images.unsplash.com/photo-1777962822492-c0d637951f24?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxmcmVzaCUyMGJlZWYlMjBtZWF0JTIwYnV0Y2hlcnxlbnwxfHx8fDE3NzgyMjg0MTB8MA&ixlib=rb-4.1.0&q=80&w=1080",
    ],
    description: "Doğal yemleme ile büyütülmüş keçi eti, 200 kg stok mevcuttur. Düşük yağ içeriği ve lezzetli yapısıyla bilinmektedir. İzmir merkez ve ilçelere soğuk zincir teslimat yapılır.",
    sellerId: "s4",
    sellerProfile: sellers["s4"],
    reviews: [reviewPool[0], reviewPool[3]],
    viewCount: 142,
    favoriteCount: 11,
  },
  {
    id: "m5",
    title: "Dana Bonfile – Premium",
    type: "Dana",
    weight: 80,
    price: 320,
    unit: "kg",
    seller: "Premium Et Merkezi",
    city: "İstanbul",
    district: "Bağcılar",
    date: "3 saat önce",
    verified: true,
    rating: 4.9,
    badge: "Premium",
    images: [
      "https://images.unsplash.com/photo-1740586222627-48338edac67d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxtZWF0JTIwbWFya2V0JTIwYnV0Y2hlciUyMHNob3B8ZW58MXx8fHwxNzc4MjI4NDExfDA&ixlib=rb-4.1.0&q=80&w=1080",
      "https://images.unsplash.com/photo-1777962822492-c0d637951f24?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxmcmVzaCUyMGJlZWYlMjBtZWF0JTIwYnV0Y2hlcnxlbnwxfHx8fDE3NzgyMjg0MTB8MA&ixlib=rb-4.1.0&q=80&w=1080",
      "https://images.unsplash.com/photo-1763140446057-9becaa30b868?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxtZWF0JTIwY2FyY2FzcyUyMHdob2xlc2FsZSUyMGJ1dGNoZXJ5fGVufDF8fHx8MTc3ODIyODQxNXww&ixlib=rb-4.1.0&q=80&w=1080",
    ],
    description: "A+ kalite sınıfında premium dana bonfile. Steakhouse ve üst segment restoranlar için idealdir. Vakumlu ambalajda, soğuk zincir ile teslimat. Tüm Türkiye'ye kargo. Adet bazında da sipariş alınmaktadır.",
    sellerId: "s5",
    sellerProfile: sellers["s5"],
    reviews: [reviewPool[0], reviewPool[1], reviewPool[3]],
    viewCount: 578,
    favoriteCount: 63,
  },
  {
    id: "m6",
    title: "Kuzu Pirzola – Taze",
    type: "Kuzu",
    weight: 60,
    price: 280,
    unit: "kg",
    seller: "Akdeniz Kesimhane",
    city: "Antalya",
    district: "Kepez",
    date: "5 saat önce",
    verified: true,
    rating: 4.7,
    badge: null,
    images: [
      "https://images.unsplash.com/photo-1719502044471-2187a7d36a81?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxsYW1iJTIwc2hlZXAlMjBsaXZlc3RvY2slMjBmYXJtfGVufDF8fHx8MTc3ODIyODQxMHww&ixlib=rb-4.1.0&q=80&w=1080",
      "https://images.unsplash.com/photo-1777962822492-c0d637951f24?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxmcmVzaCUyMGJlZWYlMjBtZWF0JTIwYnV0Y2hlcnxlbnwxfHx8fDE3NzgyMjg0MTB8MA&ixlib=rb-4.1.0&q=80&w=1080",
    ],
    description: "Taze kuzu pirzola, 60 kg stok mevcuttur. Otel ve restoran müşterilerimize özel hazırlanmış paketleme seçeneği mevcuttur. Antalya ve çevre illere teslimat yapılmaktadır.",
    sellerId: "s6",
    sellerProfile: sellers["s6"],
    reviews: [reviewPool[1], reviewPool[4]],
    viewCount: 221,
    favoriteCount: 18,
  },
  {
    id: "m7",
    title: "Koyun Eti – Bütün Karkas",
    type: "Koyun",
    weight: 480,
    price: 158,
    unit: "kg",
    seller: "Konya Et Dağıtım",
    city: "Konya",
    district: "Karatay",
    date: "2 gün önce",
    verified: true,
    rating: 4.4,
    badge: null,
    images: [
      "https://images.unsplash.com/photo-1763140446057-9becaa30b868?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxtZWF0JTIwY2FyY2FzcyUyMHdob2xlc2FsZSUyMGJ1dGNoZXJ5fGVufDF8fHx8MTc3ODIyODQxNXww&ixlib=rb-4.1.0&q=80&w=1080",
      "https://images.unsplash.com/photo-1740586222627-48338edac67d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxtZWF0JTIwbWFya2V0JTIwYnV0Y2hlciUyMHNob3B8ZW58MXx8fHwxNzc4MjI4NDExfDA&ixlib=rb-4.1.0&q=80&w=1080",
    ],
    description: "480 kg bütün koyun karkası, toptan fiyatla satışa sunulmuştur. Konya ve İç Anadolu bölgesine nakliye imkânı mevcuttur. Sağlık belgeli hayvanlardan elde edilmiştir.",
    sellerId: "s7",
    sellerProfile: sellers["s7"],
    reviews: [reviewPool[2], reviewPool[5]],
    viewCount: 156,
    favoriteCount: 12,
  },
];

// ---- Animal Listings ----
export const animalListings: AnimalListingFull[] = [
  {
    id: "a1",
    title: "Merinos Koyun – Sürü",
    type: "Küçükbaş",
    breed: "Merinos",
    quantity: 25,
    weight: 65,
    price: 4800,
    unit: "adet",
    seller: "Kara Hayvancılık",
    city: "Konya",
    district: "Selçuklu",
    date: "1 saat önce",
    verified: true,
    rating: 4.7,
    badge: "Toplu Satış",
    images: [
      "https://images.unsplash.com/photo-1719502044471-2187a7d36a81?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxsYW1iJTIwc2hlZXAlMjBsaXZlc3RvY2slMjBmYXJtfGVufDF8fHx8MTc3ODIyODQxMHww&ixlib=rb-4.1.0&q=80&w=1080",
      "https://images.unsplash.com/photo-1762571808926-2555640f12a6?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxnb2F0JTIwYW5pbWFsJTIwcGFzdHVyZSUyMGdyZWVufGVufDF8fHx8MTc3ODIyODQxNXww&ixlib=rb-4.1.0&q=80&w=1080",
      "https://images.unsplash.com/photo-1651945846830-1fe022473668?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxjYXR0bGUlMjBjb3clMjBmYXJtJTIwVHVya2V5fGVufDF8fHx8MTc3ODIyODQxMXww&ixlib=rb-4.1.0&q=80&w=1080",
    ],
    description: "25 baş Merinos koyun, ortalama 65 kg canlı ağırlık. Tüm hayvanlar aşılı ve küpeli. Sağlık sertifikaları mevcuttur. Nakliye aracı temin edilebilir. Parça satış yapılmamaktadır.",
    sellerId: "s8",
    sellerProfile: sellers["s8"],
    reviews: [reviewPool[0], reviewPool[3], reviewPool[4]],
    viewCount: 423,
    favoriteCount: 38,
  },
  {
    id: "a2",
    title: "Simental Dana – Genç",
    type: "Büyükbaş",
    breed: "Simental",
    quantity: 8,
    weight: 380,
    price: 42000,
    unit: "adet",
    seller: "Ege Çiftliği",
    city: "İzmir",
    district: "Ödemiş",
    date: "3 saat önce",
    verified: true,
    rating: 4.9,
    badge: "Sertifikalı",
    images: [
      "https://images.unsplash.com/photo-1651945846830-1fe022473668?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxjYXR0bGUlMjBjb3clMjBmYXJtJTIwVHVya2V5fGVufDF8fHx8MTc3ODIyODQxMXww&ixlib=rb-4.1.0&q=80&w=1080",
      "https://images.unsplash.com/photo-1719502044471-2187a7d36a81?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxsYW1iJTIwc2hlZXAlMjBsaXZlc3RvY2slMjBmYXJtfGVufDF8fHx8MTc3ODIyODQxMHww&ixlib=rb-4.1.0&q=80&w=1080",
    ],
    description: "8 baş genç Simental dana, 18-24 aylık. Ortalama 380 kg canlı ağırlık. Veteriner sertifikalı, brucella ve tüberküloz testi negatif. Besi için idealdir. Parça satış imkânı mevcuttur.",
    sellerId: "s9",
    sellerProfile: sellers["s9"],
    reviews: [reviewPool[0], reviewPool[1], reviewPool[3]],
    viewCount: 687,
    favoriteCount: 71,
  },
  {
    id: "a3",
    title: "Kıvırcık Kuzu – Mevsimlik",
    type: "Küçükbaş",
    breed: "Kıvırcık",
    quantity: 40,
    weight: 35,
    price: 2800,
    unit: "adet",
    seller: "Trakya Çiftliği",
    city: "Edirne",
    district: "Merkez",
    date: "5 saat önce",
    verified: false,
    rating: 4.1,
    badge: null,
    images: [
      "https://images.unsplash.com/photo-1762571808926-2555640f12a6?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxnb2F0JTIwYW5pbWFsJTIwcGFzdHVyZSUyMGdyZWVufGVufDF8fHx8MTc3ODIyODQxNXww&ixlib=rb-4.1.0&q=80&w=1080",
      "https://images.unsplash.com/photo-1719502044471-2187a7d36a81?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxsYW1iJTIwc2hlZXAlMjBsaXZlc3RvY2slMjBmYXJtfGVufDF8fHx8MTc3ODIyODQxMHww&ixlib=rb-4.1.0&q=80&w=1080",
    ],
    description: "40 baş Kıvırcık kuzu, mevsimlik uygun fiyatla sunulmaktadır. Ortalama 35 kg, yaş 4-6 ay. Trakya bölgesinden nakliye desteği verilebilir.",
    sellerId: "s10",
    sellerProfile: sellers["s10"],
    reviews: [reviewPool[2], reviewPool[5]],
    viewCount: 134,
    favoriteCount: 9,
  },
  {
    id: "a4",
    title: "Holstein İnek – Süt Sığırı",
    type: "Büyükbaş",
    breed: "Holstein",
    quantity: 5,
    weight: 550,
    price: 68000,
    unit: "adet",
    seller: "Marmara Besicilik",
    city: "Bursa",
    district: "İnegöl",
    date: "8 saat önce",
    verified: true,
    rating: 4.8,
    badge: "Yüksek Verimli",
    images: [
      "https://images.unsplash.com/photo-1651945846830-1fe022473668?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxjYXR0bGUlMjBjb3clMjBmYXJtJTIwVHVya2V5fGVufDF8fHx8MTc3ODIyODQxMXww&ixlib=rb-4.1.0&q=80&w=1080",
      "https://images.unsplash.com/photo-1762571808926-2555640f12a6?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxnb2F0JTIwYW5pbWFsJTIwcGFzdHVyZSUyMGdyZWVufGVufDF8fHx8MTc3ODIyODQxNXww&ixlib=rb-4.1.0&q=80&w=1080",
      "https://images.unsplash.com/photo-1719502044471-2187a7d36a81?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxsYW1iJTIwc2hlZXAlMjBsaXZlc3RvY2slMjBmYXJtfGVufDF8fHx8MTc3ODIyODQxMHww&ixlib=rb-4.1.0&q=80&w=1080",
    ],
    description: "5 baş Holstein inek, günlük 28-32 litre süt verimi. Gebelik durumları ve sağlık raporları mevcuttur. Tüm hayvanlar küpeli ve aşılıdır. Bursa ve çevre illere teslimat yapılabilir.",
    sellerId: "s11",
    sellerProfile: sellers["s11"],
    reviews: [reviewPool[0], reviewPool[1], reviewPool[4]],
    viewCount: 512,
    favoriteCount: 54,
  },
  {
    id: "a5",
    title: "Saanen Keçi – Süt Keçisi",
    type: "Küçükbaş",
    breed: "Saanen",
    quantity: 15,
    weight: 55,
    price: 5200,
    unit: "adet",
    seller: "Karadeniz Çiftliği",
    city: "Trabzon",
    district: "Vakfıkebir",
    date: "1 gün önce",
    verified: true,
    rating: 4.5,
    badge: null,
    images: [
      "https://images.unsplash.com/photo-1762571808926-2555640f12a6?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxnb2F0JTIwYW5pbWFsJTIwcGFzdHVyZSUyMGdyZWVufGVufDF8fHx8MTc3ODIyODQxNXww&ixlib=rb-4.1.0&q=80&w=1080",
      "https://images.unsplash.com/photo-1651945846830-1fe022473668?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxjYXR0bGUlMjBjb3clMjBmYXJtJTIwVHVya2V5fGVufDF8fHx8MTc3ODIyODQxMXww&ixlib=rb-4.1.0&q=80&w=1080",
    ],
    description: "15 baş Saanen keçi, süt verimi yüksek ve sağlıklı hayvanlar. Günlük 3-4 litre süt verimi. Aşı kartları ve sağlık belgeli. Trabzon il sınırları içinde teslimat yapılır.",
    sellerId: "s12",
    sellerProfile: sellers["s12"],
    reviews: [reviewPool[1], reviewPool[3]],
    viewCount: 267,
    favoriteCount: 22,
  },
  {
    id: "a6",
    title: "Angus Boğa – Besi",
    type: "Büyükbaş",
    breed: "Angus",
    quantity: 3,
    weight: 620,
    price: 75000,
    unit: "adet",
    seller: "Orta Anadolu Et",
    city: "Ankara",
    district: "Polatlı",
    date: "2 gün önce",
    verified: true,
    rating: 4.6,
    badge: "Damızlık",
    images: [
      "https://images.unsplash.com/photo-1651945846830-1fe022473668?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxjYXR0bGUlMjBjb3clMjBmYXJtJTIwVHVya2V5fGVufDF8fHx8MTc3ODIyODQxMXww&ixlib=rb-4.1.0&q=80&w=1080",
      "https://images.unsplash.com/photo-1719502044471-2187a7d36a81?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxsYW1iJTIwc2hlZXAlMjBsaXZlc3RvY2slMjBmYXJtfGVufDF8fHx8MTc3ODIyODQxMHww&ixlib=rb-4.1.0&q=80&w=1080",
    ],
    description: "3 baş saf kan Angus boğa, ortalama 620 kg canlı ağırlık. Damızlık veya besi amaçlı kullanıma uygundur. Soy kütükleri mevcuttur. Tüm Türkiye'ye özel araçla teslimat imkânı.",
    sellerId: "s13",
    sellerProfile: sellers["s13"],
    reviews: [reviewPool[0], reviewPool[2], reviewPool[4]],
    viewCount: 389,
    favoriteCount: 41,
  },
];

export function getListingById(id: string): AnyListing | undefined {
  return [...meatListings, ...animalListings].find((l) => l.id === id);
}
