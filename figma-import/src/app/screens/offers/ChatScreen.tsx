import React, { useState, useRef, useEffect } from 'react';
import { useNavigate, useLocation, useParams } from 'react-router';
import { ArrowLeft, Send, Paperclip, MoreVertical, Phone, CheckCheck, Check } from 'lucide-react';
import { useApp } from '../../context/AppContext';
import { getMockMessages, Message } from '../../data/mockData';
import { motion, AnimatePresence } from 'motion/react';

interface ChatMessage extends Message {
  status?: 'sent' | 'delivered' | 'read';
}

const DATE_SEPARATORS: Record<string, string> = {
  'm3': 'Bugün',
  'm6': 'Dün',
};

export function ChatScreen() {
  const navigate = useNavigate();
  const location = useLocation();
  const { id } = useParams();
  const { user } = useApp();
  const bottomRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const conversation = location.state?.conversation;
  const participantName = conversation?.participantName || 'Kullanıcı';
  const participantCompany = conversation?.participantCompany || '';
  const avatarUrl = conversation?.avatarUrl;

  const [messages, setMessages] = useState<ChatMessage[]>(() =>
    getMockMessages(id || 'conv-1', user?.id || 'user-1').map(m => ({ ...m, status: m.senderId === (user?.id || 'user-1') ? 'read' : undefined }))
  );
  const [inputText, setInputText] = useState('');
  const [isTyping, setIsTyping] = useState(false);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  // Simulate typing indicator
  const simulateReply = () => {
    setIsTyping(true);
    setTimeout(() => {
      setIsTyping(false);
      const replies = [
        'Anladım, teşekkürler!',
        'Belgelerinizi iletebilir misiniz?',
        'Fiyatı değerlendiriyorum.',
        'Uygun gözüküyor, detayları konuşalım.',
        'Harika, anlaşalım o zaman!',
      ];
      const reply: ChatMessage = {
        id: `msg-${Date.now()}`,
        conversationId: id || 'conv-1',
        senderId: 'other',
        text: replies[Math.floor(Math.random() * replies.length)],
        timestamp: new Date().toLocaleTimeString('tr-TR', { hour: '2-digit', minute: '2-digit' }),
      };
      setMessages(prev => [...prev, reply]);
    }, 1500 + Math.random() * 1000);
  };

  const sendMessage = () => {
    if (!inputText.trim()) return;
    const msg: ChatMessage = {
      id: `msg-${Date.now()}`,
      conversationId: id || 'conv-1',
      senderId: user?.id || 'user-1',
      text: inputText.trim(),
      timestamp: new Date().toLocaleTimeString('tr-TR', { hour: '2-digit', minute: '2-digit' }),
      status: 'sent',
    };
    setMessages(prev => [...prev, msg]);
    setInputText('');
    simulateReply();
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  };

  // Group messages for date separators
  const getDateSeparator = (msgId: string) => DATE_SEPARATORS[msgId];

  return (
    <div className="flex flex-col h-full bg-[#F5F7FA]">
      {/* Chat Header */}
      <div className="bg-white border-b border-gray-100 px-4 py-3 flex items-center gap-3 flex-shrink-0">
        <button
          onClick={() => navigate(-1)}
          className="w-9 h-9 flex items-center justify-center rounded-xl hover:bg-gray-100 transition-colors"
        >
          <ArrowLeft size={20} className="text-gray-700" />
        </button>

        <div className="flex-1 flex items-center gap-2.5">
          {avatarUrl ? (
            <img src={avatarUrl} alt={participantName} className="w-10 h-10 rounded-full object-cover" />
          ) : (
            <div className="w-10 h-10 rounded-full bg-gradient-to-br from-[#1B3A6B] to-[#1D5BE6] flex items-center justify-center text-white font-bold text-sm">
              {participantName.split(' ').slice(0, 2).map((n: string) => n[0]).join('')}
            </div>
          )}
          <div>
            <p className="text-sm font-bold text-gray-900 leading-tight">{participantName}</p>
            <p className="text-xs text-gray-400">{participantCompany || 'çevrimiçi'}</p>
          </div>
        </div>

        <div className="flex items-center gap-1">
          <button className="w-9 h-9 flex items-center justify-center rounded-xl hover:bg-gray-100 transition-colors">
            <Phone size={18} className="text-gray-600" />
          </button>
          <button className="w-9 h-9 flex items-center justify-center rounded-xl hover:bg-gray-100 transition-colors">
            <MoreVertical size={18} className="text-gray-600" />
          </button>
        </div>
      </div>

      {/* Related listing chip */}
      {conversation?.relatedListingTitle && (
        <div className="bg-blue-50 border-b border-blue-100 px-4 py-2 flex items-center gap-2">
          <div className="w-1 h-8 rounded-full bg-[#1B3A6B] flex-shrink-0" />
          <div>
            <p className="text-[11px] text-blue-500 font-semibold">İlgili İlan</p>
            <p className="text-xs text-blue-800 font-medium">{conversation.relatedListingTitle}</p>
          </div>
        </div>
      )}

      {/* Messages */}
      <div className="flex-1 overflow-y-auto px-4 py-4 space-y-1">
        {messages.map((msg, idx) => {
          const isMe = msg.senderId === (user?.id || 'user-1');
          const dateSep = getDateSeparator(msg.id);
          const prevMsg = messages[idx - 1];
          const isSameGroup = prevMsg && prevMsg.senderId === msg.senderId;

          return (
            <React.Fragment key={msg.id}>
              {dateSep && (
                <div className="flex items-center gap-3 py-3">
                  <div className="flex-1 h-px bg-gray-200" />
                  <span className="text-xs text-gray-400 font-medium px-3 py-1 bg-gray-100 rounded-full">{dateSep}</span>
                  <div className="flex-1 h-px bg-gray-200" />
                </div>
              )}
              <motion.div
                initial={{ opacity: 0, y: 10, scale: 0.95 }}
                animate={{ opacity: 1, y: 0, scale: 1 }}
                transition={{ duration: 0.15 }}
                className={`flex ${isMe ? 'justify-end' : 'justify-start'} ${!isSameGroup ? 'mt-3' : 'mt-0.5'}`}
              >
                {!isMe && !isSameGroup && (
                  <div className="w-7 h-7 rounded-full bg-gradient-to-br from-[#1B3A6B] to-[#1D5BE6] flex items-center justify-center text-white text-[10px] font-bold flex-shrink-0 mr-2 mt-auto">
                    {participantName.split(' ').slice(0, 2).map((n: string) => n[0]).join('')}
                  </div>
                )}
                {!isMe && isSameGroup && <div className="w-7 mr-2" />}

                <div className={`max-w-[75%] ${isMe ? 'items-end' : 'items-start'} flex flex-col`}>
                  <div
                    className={`px-4 py-2.5 rounded-2xl text-sm leading-relaxed ${
                      isMe
                        ? 'bg-[#1B3A6B] text-white rounded-br-md'
                        : 'bg-white text-gray-800 border border-gray-100 rounded-bl-md shadow-sm'
                    }`}
                  >
                    {msg.text}
                  </div>
                  <div className={`flex items-center gap-1 mt-0.5 ${isMe ? 'justify-end' : 'justify-start'}`}>
                    <span className="text-[10px] text-gray-400">{msg.timestamp}</span>
                    {isMe && (
                      <span className="text-[10px] text-blue-400">
                        {msg.status === 'read' ? <CheckCheck size={12} className="text-blue-400" /> : <Check size={12} className="text-gray-400" />}
                      </span>
                    )}
                  </div>
                </div>
              </motion.div>
            </React.Fragment>
          );
        })}

        {/* Typing indicator */}
        <AnimatePresence>
          {isTyping && (
            <motion.div
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: 10 }}
              className="flex items-end gap-2 mt-2"
            >
              <div className="w-7 h-7 rounded-full bg-gradient-to-br from-[#1B3A6B] to-[#1D5BE6] flex items-center justify-center text-white text-[10px] font-bold flex-shrink-0">
                {participantName.split(' ').slice(0, 2).map((n: string) => n[0]).join('')}
              </div>
              <div className="bg-white border border-gray-100 shadow-sm rounded-2xl rounded-bl-md px-4 py-3 flex items-center gap-1">
                {[0, 1, 2].map(i => (
                  <div
                    key={i}
                    className="w-1.5 h-1.5 rounded-full bg-gray-400 animate-bounce"
                    style={{ animationDelay: `${i * 0.15}s` }}
                  />
                ))}
              </div>
            </motion.div>
          )}
        </AnimatePresence>

        <div ref={bottomRef} />
      </div>

      {/* Input */}
      <div className="bg-white border-t border-gray-100 px-4 py-3 flex items-center gap-2 flex-shrink-0">
        <button className="w-9 h-9 flex items-center justify-center rounded-xl text-gray-400 hover:bg-gray-100 transition-colors">
          <Paperclip size={18} />
        </button>
        <div className="flex-1 relative">
          <input
            ref={inputRef}
            type="text"
            value={inputText}
            onChange={e => setInputText(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="Mesaj yazın..."
            className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-2xl text-sm text-gray-900 placeholder-gray-400 focus:outline-none focus:border-[#1B3A6B] focus:ring-2 focus:ring-[#1B3A6B]/10 transition-all pr-3"
          />
        </div>
        <button
          onClick={sendMessage}
          disabled={!inputText.trim()}
          className={`w-10 h-10 rounded-2xl flex items-center justify-center transition-all active:scale-90 ${
            inputText.trim()
              ? 'bg-[#1B3A6B] text-white shadow-lg shadow-[#1B3A6B]/30'
              : 'bg-gray-100 text-gray-400'
          }`}
        >
          <Send size={18} className={inputText.trim() ? 'translate-x-0.5' : ''} />
        </button>
      </div>
    </div>
  );
}
