import { useState } from "react";
import { useNavigate } from "react-router";
import { TopBar } from "../components/layout/TopBar";
import { BottomNav } from "../components/layout/BottomNav";
import { Search, MessageCircle, ChevronRight, X } from "lucide-react";
import { cn } from "../../lib/utils";

const mockConversations = [
  {
    id: "c1",
    name: "Kemal Çelik",
    company: "Çelik Gıda Marketleri",
    lastMessage: "Fiyat konusunda anlaşabilir miyiz? 200 kg için düşünüyorum",
    time: "10 dk",
    unread: 2,
    avatar: "KC",
    listingTitle: "Dana Eti – Karkas",
    online: true,
  },
  {
    id: "c2",
    name: "Selin Aydın",
    company: "Aydın Market",
    lastMessage: "Teşekkürler, siparişi onayladım. Ne zaman teslim alabiliriz?",
    time: "2 sa",
    unread: 0,
    avatar: "SA",
    listingTitle: "Kuzu Eti – Taze",
    online: false,
  },
  {
    id: "c3",
    name: "Fatma Kaya",
    company: "Kaya Gıda A.Ş.",
    lastMessage: "Anlaşamadık maalesef. Bir dahaki sefere belki.",
    time: "1 gün",
    unread: 0,
    avatar: "FK",
    listingTitle: "Koyun Eti – Karkas",
    online: false,
  },
  {
    id: "c4",
    name: "Ahmet Demir",
    company: "Demir Et Pazarı",
    lastMessage: "Dana eti için stok durumunuz nasıl?",
    time: "2 gün",
    unread: 1,
    avatar: "AD",
    listingTitle: "Dana Eti – Karkas",
    online: true,
  },
  {
    id: "c5",
    name: "Murat Yıldız",
    company: "Yıldız Restoran Grubu",
    lastMessage: "Haftalık sipariş için fiyat listesi alabilir miyim?",
    time: "3 gün",
    unread: 0,
    avatar: "MY",
    listingTitle: "Koyun Eti – Bütün Karkas",
    online: false,
  },
];

const avatarColors = [
  "bg-blue-100 text-blue-700",
  "bg-purple-100 text-purple-700",
  "bg-emerald-100 text-emerald-700",
  "bg-amber-100 text-amber-700",
  "bg-rose-100 text-rose-700",
];

export function MessagesPage() {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState("");

  const totalUnread = mockConversations.reduce((acc, c) => acc + c.unread, 0);

  const filtered = searchQuery
    ? mockConversations.filter(
        (c) =>
          c.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
          c.company.toLowerCase().includes(searchQuery.toLowerCase()) ||
          c.listingTitle.toLowerCase().includes(searchQuery.toLowerCase())
      )
    : mockConversations;

  return (
    <div className="min-h-screen bg-background pb-24">
      <TopBar
        title="Mesajlar"
        action={
          totalUnread > 0 ? (
            <span className="bg-primary text-white text-xs font-bold px-2.5 py-1 rounded-full">
              {totalUnread} yeni
            </span>
          ) : undefined
        }
      />

      {/* Search */}
      <div className="sticky top-14 bg-background/95 backdrop-blur-sm z-30 px-4 py-3 border-b border-border/50">
        <div className="relative max-w-md mx-auto">
          <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 size-4 text-muted-foreground" />
          <input
            type="text"
            placeholder="Konuşma veya kişi ara..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full h-11 bg-white border border-border rounded-xl pl-10 pr-9 text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring"
          />
          {searchQuery && (
            <button
              onClick={() => setSearchQuery("")}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground"
            >
              <X className="size-4" />
            </button>
          )}
        </div>
      </div>

      <div className="max-w-md mx-auto px-4 py-3">
        {/* Result count */}
        <p className="text-xs text-muted-foreground mb-3 px-1">
          {filtered.length} konuşma
          {totalUnread > 0 && (
            <span className="ml-2 font-semibold text-primary">{totalUnread} okunmamış</span>
          )}
        </p>

        {filtered.length === 0 ? (
          <div className="text-center py-16">
            <div className="size-20 bg-muted rounded-full flex items-center justify-center mx-auto mb-4">
              <MessageCircle className="size-8 text-muted-foreground" />
            </div>
            <h3 className="font-semibold text-foreground mb-1">Mesaj bulunamadı</h3>
            <p className="text-sm text-muted-foreground">Arama kriterinizi değiştirin</p>
          </div>
        ) : (
          <div className="space-y-2">
            {filtered.map((conv, idx) => (
              <button
                key={conv.id}
                onClick={() => navigate(`/chat/${conv.id}`)}
                className="w-full bg-white rounded-2xl border border-border shadow-sm p-4 flex items-center gap-3 hover:border-primary/30 hover:bg-primary/5 transition-all active:scale-[0.99] text-left"
              >
                {/* Avatar */}
                <div className="relative flex-shrink-0">
                  <div
                    className={cn(
                      "size-12 rounded-2xl flex items-center justify-center font-semibold text-sm",
                      avatarColors[idx % avatarColors.length]
                    )}
                  >
                    {conv.avatar}
                  </div>
                  {conv.online && (
                    <span className="absolute -bottom-0.5 -right-0.5 size-3 bg-green-500 border-2 border-white rounded-full" />
                  )}
                </div>

                {/* Content */}
                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between mb-0.5">
                    <p
                      className={cn(
                        "text-sm truncate pr-2",
                        conv.unread > 0
                          ? "font-bold text-foreground"
                          : "font-medium text-foreground"
                      )}
                    >
                      {conv.name}
                    </p>
                    <span className="text-xs text-muted-foreground flex-shrink-0">
                      {conv.time}
                    </span>
                  </div>
                  <p className="text-[11px] text-muted-foreground truncate mb-1">
                    {conv.company} · {conv.listingTitle}
                  </p>
                  <p
                    className={cn(
                      "text-xs truncate",
                      conv.unread > 0
                        ? "text-foreground font-medium"
                        : "text-muted-foreground"
                    )}
                  >
                    {conv.lastMessage}
                  </p>
                </div>

                {/* Badge / chevron */}
                <div className="flex-shrink-0 ml-1">
                  {conv.unread > 0 ? (
                    <span className="size-5 bg-primary text-white text-[10px] font-bold rounded-full flex items-center justify-center">
                      {conv.unread}
                    </span>
                  ) : (
                    <ChevronRight className="size-4 text-muted-foreground" />
                  )}
                </div>
              </button>
            ))}
          </div>
        )}
      </div>

      <BottomNav />
    </div>
  );
}
