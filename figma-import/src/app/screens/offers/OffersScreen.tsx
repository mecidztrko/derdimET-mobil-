import React, { useState } from 'react';
import { useNavigate } from 'react-router';
import { MessageSquare, FileText, ChevronRight, CheckCircle2, Clock, XCircle } from 'lucide-react';
import { useApp } from '../../context/AppContext';
import {
  MOCK_OFFERS, SELLER_OFFERS, SH_OFFERS,
  BUYER_CONVERSATIONS, SELLER_CONVERSATIONS, SH_CONVERSATIONS,
  Offer, Conversation
} from '../../data/mockData';
import { StatusBadge } from '../../components/shared/StatusBadge';
import { EmptyState } from '../../components/shared/EmptyState';
import { toast } from 'sonner';

type Tab = 'offers' | 'messages';

function OfferCard({ offer, onAccept, onReject }: {
  offer: Offer;
  onAccept?: () => void;
  onReject?: () => void;
}) {
  const [status, setStatus] = useState(offer.status);
  const typeEmoji = offer.listingType === 'meat' ? '🥩' : offer.listingType === 'animal' ? '🐄' : '🏭';

  const handleAccept = () => {
    setStatus('accepted');
    toast.success('Teklif kabul edildi!');
    onAccept?.();
  };
  const handleReject = () => {
    setStatus('rejected');
    toast.error('Teklif reddedildi.');
    onReject?.();
  };

  return (
    <div className="bg-white rounded-2xl border border-gray-100 p-4 shadow-sm">
      <div className="flex items-start gap-3">
        <div className="w-11 h-11 rounded-2xl bg-gray-50 flex items-center justify-center text-xl flex-shrink-0">
          {typeEmoji}
        </div>
        <div className="flex-1 min-w-0">
          <div className="flex items-start justify-between gap-2">
            <p className="text-sm font-semibold text-gray-900 leading-tight truncate flex-1">{offer.listingTitle}</p>
            <StatusBadge status={status} />
          </div>
          <p className="text-xs text-gray-500 mt-0.5">
            {offer.fromUserName} → {offer.toUserName}
          </p>
        </div>
      </div>

      <div className="mt-3 p-3 bg-gray-50 rounded-xl">
        <div className="flex items-center justify-between">
          <span className="text-xs text-gray-500">Teklif Fiyatı</span>
          <span className="text-base font-bold text-[#1B3A6B]">{offer.offeredPrice.toLocaleString()}₺</span>
        </div>
        {offer.quantity && (
          <div className="flex items-center justify-between mt-1">
            <span className="text-xs text-gray-500">Miktar</span>
            <span className="text-xs font-medium text-gray-700">{offer.quantity} {offer.listingType === 'meat' ? 'kg' : 'baş'}</span>
          </div>
        )}
      </div>

      {offer.note && (
        <p className="text-xs text-gray-500 mt-2 px-1 italic">"{offer.note}"</p>
      )}

      <div className="flex items-center justify-between mt-3">
        <span className="text-xs text-gray-400 flex items-center gap-1">
          <Clock size={11} /> {offer.date}
        </span>
        {status === 'pending' && (onAccept || onReject) && (
          <div className="flex gap-2">
            <button
              onClick={handleReject}
              className="flex items-center gap-1 px-3 py-1.5 bg-red-50 text-red-600 rounded-xl text-xs font-semibold border border-red-100 active:scale-95 transition-transform"
            >
              <XCircle size={13} /> Reddet
            </button>
            <button
              onClick={handleAccept}
              className="flex items-center gap-1 px-3 py-1.5 bg-emerald-50 text-emerald-700 rounded-xl text-xs font-semibold border border-emerald-100 active:scale-95 transition-transform"
            >
              <CheckCircle2 size={13} /> Kabul Et
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

function ConversationCard({ conv, onPress }: { conv: Conversation; onPress: () => void }) {
  const roleEmoji = conv.participantRole === 'SLAUGHTERHOUSE' ? '🏭' : conv.participantRole === 'ANIMAL_SELLER' ? '🐄' : '🛒';

  return (
    <button
      onClick={onPress}
      className="w-full flex items-center gap-3 p-4 bg-white rounded-2xl border border-gray-100 text-left active:bg-gray-50 transition-colors shadow-sm"
    >
      <div className="relative flex-shrink-0">
        {conv.avatarUrl ? (
          <img src={conv.avatarUrl} alt={conv.participantName} className="w-12 h-12 rounded-full object-cover" />
        ) : (
          <div className="w-12 h-12 rounded-full bg-gradient-to-br from-[#1B3A6B] to-[#1D5BE6] flex items-center justify-center text-white font-bold text-sm">
            {conv.participantName.split(' ').slice(0, 2).map(n => n[0]).join('')}
          </div>
        )}
        {conv.unreadCount > 0 && (
          <span className="absolute -top-1 -right-1 w-5 h-5 rounded-full bg-[#E05C2A] text-white text-[10px] font-bold flex items-center justify-center">
            {conv.unreadCount}
          </span>
        )}
      </div>

      <div className="flex-1 min-w-0">
        <div className="flex items-center justify-between gap-2">
          <p className={`text-sm truncate ${conv.unreadCount > 0 ? 'font-bold text-gray-900' : 'font-semibold text-gray-800'}`}>
            {conv.participantName}
          </p>
          <span className="text-[11px] text-gray-400 flex-shrink-0">{conv.lastMessageTime}</span>
        </div>
        {conv.participantCompany && (
          <p className="text-xs text-gray-400 truncate">{conv.participantCompany}</p>
        )}
        <p className={`text-xs truncate mt-0.5 ${conv.unreadCount > 0 ? 'text-gray-700 font-medium' : 'text-gray-500'}`}>
          {conv.lastMessage}
        </p>
      </div>
    </button>
  );
}

export function OffersScreen() {
  const navigate = useNavigate();
  const { user } = useApp();
  const [activeTab, setActiveTab] = useState<Tab>('offers');

  const offers = user?.role === 'MEAT_BUYER' ? MOCK_OFFERS : user?.role === 'ANIMAL_SELLER' ? SELLER_OFFERS : SH_OFFERS;
  const conversations = user?.role === 'MEAT_BUYER' ? BUYER_CONVERSATIONS : user?.role === 'ANIMAL_SELLER' ? SELLER_CONVERSATIONS : SH_CONVERSATIONS;

  const pendingCount = offers.filter(o => o.status === 'pending').length;
  const unreadCount = conversations.reduce((sum, c) => sum + c.unreadCount, 0);

  const isSentOffers = user?.role === 'MEAT_BUYER' || user?.role === 'ANIMAL_SELLER';

  return (
    <div className="flex flex-col h-full bg-[#F5F7FA]">
      {/* Tab Header */}
      <div className="bg-white border-b border-gray-100 px-4 pt-2 pb-3">
        <div className="flex gap-1 bg-gray-100 rounded-2xl p-1">
          <button
            onClick={() => setActiveTab('offers')}
            className={`flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-sm font-semibold transition-all ${activeTab === 'offers' ? 'bg-white text-[#1B3A6B] shadow-sm' : 'text-gray-500'}`}
          >
            <FileText size={16} />
            Teklifler
            {pendingCount > 0 && (
              <span className={`text-xs rounded-full w-5 h-5 flex items-center justify-center font-bold ${activeTab === 'offers' ? 'bg-[#1B3A6B] text-white' : 'bg-gray-300 text-gray-600'}`}>
                {pendingCount}
              </span>
            )}
          </button>
          <button
            onClick={() => setActiveTab('messages')}
            className={`flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-sm font-semibold transition-all ${activeTab === 'messages' ? 'bg-white text-[#1B3A6B] shadow-sm' : 'text-gray-500'}`}
          >
            <MessageSquare size={16} />
            Mesajlar
            {unreadCount > 0 && (
              <span className={`text-xs rounded-full w-5 h-5 flex items-center justify-center font-bold ${activeTab === 'messages' ? 'bg-[#E05C2A] text-white' : 'bg-gray-300 text-gray-600'}`}>
                {unreadCount}
              </span>
            )}
          </button>
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-y-auto">
        {activeTab === 'offers' && (
          <div className="px-4 py-4 space-y-3">
            {/* Status Summary */}
            <div className="grid grid-cols-3 gap-2">
              {[
                { label: 'Beklemede', count: offers.filter(o => o.status === 'pending').length, color: 'bg-amber-50 text-amber-700', dot: 'bg-amber-400' },
                { label: 'Kabul', count: offers.filter(o => o.status === 'accepted').length, color: 'bg-emerald-50 text-emerald-700', dot: 'bg-emerald-400' },
                { label: 'Red', count: offers.filter(o => o.status === 'rejected').length, color: 'bg-red-50 text-red-600', dot: 'bg-red-400' },
              ].map(stat => (
                <div key={stat.label} className={`p-3 rounded-2xl ${stat.color} text-center`}>
                  <p className="text-xl font-bold">{stat.count}</p>
                  <p className="text-[11px] font-medium opacity-80">{stat.label}</p>
                </div>
              ))}
            </div>

            {offers.length === 0 ? (
              <EmptyState type="offers" />
            ) : (
              offers.map(offer => (
                <OfferCard
                  key={offer.id}
                  offer={offer}
                  onAccept={!isSentOffers ? () => {} : undefined}
                  onReject={!isSentOffers ? () => {} : undefined}
                />
              ))
            )}
            <div className="h-4" />
          </div>
        )}

        {activeTab === 'messages' && (
          <div className="px-4 py-4 space-y-2.5">
            {conversations.length === 0 ? (
              <EmptyState type="messages" />
            ) : (
              conversations.map(conv => (
                <ConversationCard
                  key={conv.id}
                  conv={conv}
                  onPress={() => navigate(`/app/chat/${conv.id}`, { state: { conversation: conv } })}
                />
              ))
            )}
            <div className="h-4" />
          </div>
        )}
      </div>
    </div>
  );
}
