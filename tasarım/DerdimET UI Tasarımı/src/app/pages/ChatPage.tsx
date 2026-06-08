import { useState, useRef, useEffect } from "react";
import { useParams, useNavigate } from "react-router";
import { useAuth } from "../context/AuthContext";
import { TopBar } from "../components/layout/TopBar";
import { Send, Paperclip, Phone, MoreVertical, CheckCheck, Check, Package, Star, Info } from "lucide-react";
import { cn } from "../../lib/utils";

const conversationData: Record<string, { name: string; company: string; avatar: string; online: boolean; listingTitle: string; offerStatus: string; offerAmount: number }> = {
  c1: { name: "Kemal Çelik", company: "Çelik Gıda Marketleri", avatar: "KC", online: true, listingTitle: "Dana Eti – Karkas", offerStatus: "pending", offerAmount: 190 },
  c2: { name: "Selin Aydın", company: "Aydın Market", avatar: "SA", online: false, listingTitle: "Kuzu Eti – Taze", offerStatus: "accepted", offerAmount: 225 },
  c3: { name: "Fatma Kaya", company: "Kaya Gıda A.Ş.", avatar: "FK", online: false, listingTitle: "Koyun Eti – Karkas", offerStatus: "rejected", offerAmount: 155 },
  c4: { name: "Ahmet Demir", company: "Demir Et Pazarı", avatar: "AD", online: true, listingTitle: "Dana Eti – Karkas", offerStatus: "pending", offerAmount: 180 },
};

interface Message {
  id: string;
  senderId: string;
  content: string;
  time: string;
  status: "sent" | "delivered" | "read";
  isSystem?: boolean;
}

const initialMessages: Record<string, Message[]> = {
  c1: [
    { id: "1", senderId: "them", content: "Merhaba, dana eti için teklif verdim. Uygun fiyata anlaşabilir miyiz?", time: "09:30", status: "read" },
    { id: "2", senderId: "me", content: "Merhaba Kemal Bey, teklifinizi inceledim. 185 ₺/kg olan ürün için 190 ₺ teklif etmişsiniz.", time: "09:35", status: "read" },
    { id: "3", senderId: "them", content: "Evet, 200 kg almak istiyorum. Eğer toplu alımda indirim yaparsanız daha iyi olur.", time: "09:38", status: "read" },
    { id: "4", senderId: "me", content: "200 kg için 188 ₺/kg yapalım, bu en iyi fiyatım.", time: "09:42", status: "read" },
    { id: "5", senderId: "them", content: "Fiyat konusunda anlaşabilir miyiz? 200 kg için düşünüyorum", time: "10:15", status: "delivered" },
  ],
  c2: [
    { id: "1", senderId: "them", content: "Kuzu eti teklifim için teşekkürler!", time: "Dün", status: "read" },
    { id: "2", senderId: "me", content: "Rica ederiz, teklifiniz kabul edildi.", time: "Dün", status: "read" },
    { id: "3", senderId: "them", content: "Teşekkürler, siparişi onayladım. Ne zaman teslim alabiliriz?", time: "2 sa", status: "read" },
  ],
  c3: [
    { id: "1", senderId: "them", content: "Merhaba, koyun eti için teklifim uygun mu?", time: "1 gün", status: "read" },
    { id: "2", senderId: "me", content: "Maalesef teklifiniz fiyat aralığımızın altında kalıyor.", time: "1 gün", status: "read" },
    { id: "3", senderId: "them", content: "Anlaşamadık maalesef. Bir dahaki sefere belki.", time: "1 gün", status: "read" },
  ],
  c4: [
    { id: "1", senderId: "them", content: "Dana eti için stok durumunuz nasıl?", time: "2 gün", status: "read" },
  ],
};

const avatarColors = ["bg-blue-100 text-blue-700", "bg-purple-100 text-purple-700", "bg-emerald-100 text-emerald-700", "bg-amber-100 text-amber-700"];

const offerStatusBadge: Record<string, { label: string; className: string }> = {
  pending: { label: "Teklif Bekleniyor", className: "bg-amber-100 text-amber-700" },
  accepted: { label: "Teklif Kabul Edildi", className: "bg-green-100 text-green-700" },
  rejected: { label: "Teklif Reddedildi", className: "bg-red-100 text-red-700" },
};

export function ChatPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const convId = id || "c1";
  const [messages, setMessages] = useState<Message[]>(initialMessages[convId] || []);
  const [inputText, setInputText] = useState("");
  const [showInfo, setShowInfo] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLTextAreaElement>(null);

  const conv = conversationData[convId];
  if (!conv) return <div className="min-h-screen flex items-center justify-center"><p>Konuşma bulunamadı</p></div>;

  const colorIdx = Object.keys(conversationData).indexOf(convId) % avatarColors.length;

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleSend = () => {
    if (!inputText.trim()) return;
    const newMsg: Message = {
      id: Date.now().toString(),
      senderId: "me",
      content: inputText.trim(),
      time: new Date().toLocaleTimeString("tr-TR", { hour: "2-digit", minute: "2-digit" }),
      status: "sent",
    };
    setMessages((prev) => [...prev, newMsg]);
    setInputText("");
    inputRef.current?.focus();

    // Simulate reply after 2s
    setTimeout(() => {
      const replies = [
        "Anladım, teşekkür ederim.",
        "Peki, bunu düşüneceğim.",
        "Uygun, devam edelim.",
        "Tamam, size geri döneceğim.",
      ];
      const reply: Message = {
        id: (Date.now() + 1).toString(),
        senderId: "them",
        content: replies[Math.floor(Math.random() * replies.length)],
        time: new Date().toLocaleTimeString("tr-TR", { hour: "2-digit", minute: "2-digit" }),
        status: "delivered",
      };
      setMessages((prev) => [...prev, reply]);
    }, 1500 + Math.random() * 1000);
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  const offerBadge = offerStatusBadge[conv.offerStatus];

  return (
    <div className="min-h-screen bg-background flex flex-col max-w-md mx-auto">
      {/* Custom Header */}
      <header className="sticky top-0 bg-white border-b border-border z-40 shadow-sm">
        <div className="flex items-center gap-3 h-14 px-4">
          <button
            onClick={() => navigate(-1)}
            className="p-2 -ml-2 hover:bg-muted rounded-xl transition-colors"
          >
            <svg className="size-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
          </button>

          <div className="relative flex-shrink-0">
            <div className={cn("size-9 rounded-xl flex items-center justify-center font-semibold text-sm", avatarColors[colorIdx])}>
              {conv.avatar}
            </div>
            {conv.online && (
              <span className="absolute -bottom-0.5 -right-0.5 size-2.5 bg-green-500 border-2 border-white rounded-full" />
            )}
          </div>

          <div className="flex-1 min-w-0">
            <p className="font-semibold text-foreground text-sm leading-tight truncate">{conv.name}</p>
            <p className="text-xs text-muted-foreground truncate">{conv.online ? "Çevrimiçi" : conv.company}</p>
          </div>

          <div className="flex items-center gap-1">
            <button className="p-2 hover:bg-muted rounded-xl transition-colors">
              <Phone className="size-4.5 text-muted-foreground" />
            </button>
            <button
              onClick={() => setShowInfo(!showInfo)}
              className="p-2 hover:bg-muted rounded-xl transition-colors"
            >
              <MoreVertical className="size-4.5 text-muted-foreground" />
            </button>
          </div>
        </div>

        {/* Listing info bar */}
        <div className="px-4 pb-2.5">
          <div className="flex items-center justify-between bg-muted/70 rounded-xl px-3 py-2">
            <div className="flex items-center gap-2 min-w-0">
              <Package className="size-3.5 text-muted-foreground flex-shrink-0" />
              <p className="text-xs text-muted-foreground truncate">{conv.listingTitle}</p>
            </div>
            <span className={cn("text-[10px] font-semibold px-2 py-0.5 rounded-full flex-shrink-0 ml-2", offerBadge.className)}>
              {offerBadge.label}
            </span>
          </div>
        </div>
      </header>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto px-4 py-4 space-y-3">
        {/* Offer summary card */}
        <div className="flex justify-center mb-2">
          <div className="bg-white border border-border rounded-2xl p-3 shadow-sm max-w-[80%] w-full">
            <div className="flex items-center gap-2 mb-2">
              <Star className="size-3.5 text-amber-400 fill-amber-400" />
              <p className="text-xs font-semibold text-foreground">Teklif Detayı</p>
            </div>
            <p className="text-xs text-muted-foreground mb-1">{conv.listingTitle}</p>
            <p className="font-bold text-primary">{conv.offerAmount.toLocaleString("tr-TR")} ₺/kg</p>
          </div>
        </div>

        {messages.map((msg) => {
          const isMe = msg.senderId === "me";
          return (
            <div key={msg.id} className={cn("flex", isMe ? "justify-end" : "justify-start")}>
              {!isMe && (
                <div className={cn("size-7 rounded-xl flex items-center justify-center text-[10px] font-bold mr-2 flex-shrink-0 self-end", avatarColors[colorIdx])}>
                  {conv.avatar}
                </div>
              )}
              <div className={cn("max-w-[75%]")}>
                <div className={cn(
                  "rounded-2xl px-4 py-2.5 shadow-sm",
                  isMe
                    ? "bg-primary text-white rounded-br-md"
                    : "bg-white text-foreground border border-border rounded-bl-md"
                )}>
                  <p className="text-sm leading-relaxed">{msg.content}</p>
                </div>
                <div className={cn("flex items-center gap-1 mt-1", isMe ? "justify-end" : "justify-start")}>
                  <span className={cn("text-[10px]", isMe ? "text-muted-foreground" : "text-muted-foreground")}>
                    {msg.time}
                  </span>
                  {isMe && (
                    msg.status === "read"
                      ? <CheckCheck className="size-3 text-blue-400" />
                      : msg.status === "delivered"
                      ? <CheckCheck className="size-3 text-muted-foreground" />
                      : <Check className="size-3 text-muted-foreground" />
                  )}
                </div>
              </div>
            </div>
          );
        })}
        <div ref={messagesEndRef} />
      </div>

      {/* Input Area */}
      <div className="sticky bottom-0 bg-white border-t border-border px-4 py-3 pb-safe">
        <div className="flex items-end gap-2">
          <button className="p-2.5 hover:bg-muted rounded-xl transition-colors flex-shrink-0 mb-0.5">
            <Paperclip className="size-4.5 text-muted-foreground" />
          </button>

          <div className="flex-1 bg-muted/50 rounded-2xl border border-border overflow-hidden">
            <textarea
              ref={inputRef}
              value={inputText}
              onChange={(e) => setInputText(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="Mesajınızı yazın..."
              rows={1}
              className="w-full bg-transparent px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground resize-none focus:outline-none max-h-32"
              style={{ minHeight: "40px" }}
            />
          </div>

          <button
            onClick={handleSend}
            disabled={!inputText.trim()}
            className={cn(
              "size-10 rounded-xl flex items-center justify-center transition-all flex-shrink-0 mb-0.5",
              inputText.trim()
                ? "bg-primary text-white shadow-sm hover:bg-primary/90 active:scale-95"
                : "bg-muted text-muted-foreground cursor-not-allowed"
            )}
          >
            <Send className="size-4" />
          </button>
        </div>
      </div>
    </div>
  );
}