'use client';

import { useState, useEffect } from 'react';

interface Event {
  id: number;
  name: string;
}

interface Guest {
  name: string;
  phone: string;
  countryCode: string;
  eventIds: number[];
  lodgingName: string;
}

interface CreateInvitationRequest {
  name: string;
  phone: string;
  eventIds: number[];
  lodgingName: string;
}

const accommodationOptions = [
  { id: 'alcor', name: 'Alcor Hotel' },
  { id: 'sonnet', name: 'Hotel Sonnet' }
] as const;

interface PhoneError {
  hasError: boolean;
  message: string;
}

interface AuthDialogProps {
  isOpen: boolean;
  onSubmit: (code: string) => void;
  onCancel: () => void;
}

function AuthDialog({ isOpen, onSubmit, onCancel }: AuthDialogProps) {
  const [authCode, setAuthCode] = useState('');

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-gray-800 p-6 rounded-lg shadow-xl max-w-md w-full mx-4">
        <h2 className="text-xl font-bold text-white mb-4">Authentication Required</h2>
        <p className="text-gray-300 mb-4">Please enter the authentication code to submit invitations.</p>
        <input
          type="text"
          value={authCode}
          onChange={(e) => setAuthCode(e.target.value)}
          className="w-full px-4 py-3 mb-4 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-500 focus:ring-2 focus:ring-pink-500 focus:border-pink-500 transition-all"
          placeholder="Enter auth code"
        />
        <div className="flex space-x-3">
          <button
            onClick={() => onSubmit(authCode)}
            className="flex-1 bg-pink-600 text-white font-bold py-2 px-4 rounded-lg hover:bg-pink-700 focus:outline-none focus:ring-2 focus:ring-pink-500 transition-all"
          >
            Submit
          </button>
          <button
            onClick={onCancel}
            className="flex-1 bg-gray-700 text-pink-400 font-bold py-2 px-4 rounded-lg hover:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-pink-500 transition-all"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
}

// Common country codes with flag emojis
const countryCodes = [
  { code: '+1', flag: '🇺🇸', name: 'USA/Canada' },
  { code: '+44', flag: '🇬🇧', name: 'UK' },
  { code: '+91', flag: '🇮🇳', name: 'India' },
  { code: '+61', flag: '🇦🇺', name: 'Australia' },
  { code: '+65', flag: '🇸🇬', name: 'Singapore' },
  { code: '+971', flag: '🇦🇪', name: 'UAE' },
];

export default function WeddingInvite() {
  const [availableEvents, setAvailableEvents] = useState<Event[]>([]);
  const [guests, setGuests] = useState<Guest[]>([{ 
    name: '', 
    phone: '', 
    countryCode: '+91', // Default to India
    eventIds: [], 
    lodgingName: '' 
  }]);
  const [phoneErrors, setPhoneErrors] = useState<PhoneError[]>([{ hasError: false, message: '' }]);
  const [showSuccess, setShowSuccess] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showAuthDialog, setShowAuthDialog] = useState(false);
  const [pendingSubmission, setPendingSubmission] = useState<CreateInvitationRequest[]>([]);

  const validatePhoneNumber = (phone: string, countryCode: string): PhoneError => {
    if (!phone) return { hasError: true, message: 'Phone number is required' };
    
    // Remove any non-digit characters except + from the phone number
    const cleanPhone = phone.replace(/[^\d+]/g, '');
    
    // Different validation rules based on country code
    switch (countryCode) {
      case '+91': // India
        if (!/^\d{10}$/.test(cleanPhone)) {
          return { hasError: true, message: 'Indian phone numbers must be 10 digits' };
        }
        break;
      case '+1': // USA/Canada
        if (!/^\d{10}$/.test(cleanPhone)) {
          return { hasError: true, message: 'US/Canada phone numbers must be 10 digits' };
        }
        break;
      case '+44': // UK
        if (!/^\d{10,11}$/.test(cleanPhone)) {
          return { hasError: true, message: 'UK phone numbers must be 10-11 digits' };
        }
        break;
      default:
        if (cleanPhone.length < 8 || cleanPhone.length > 15) {
          return { hasError: true, message: 'Phone number must be 8-15 digits' };
        }
    }
    
    return { hasError: false, message: '' };
  };

  useEffect(() => {
    fetchEvents();
  }, []);

  const fetchEvents = async () => {
    try {
      const response = await fetch('https://wedding-api.srivatsa.dev/api/events');
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      const events = await response.json();
      setAvailableEvents(events);
    } catch (error) {
      console.error("Failed to load events:", error);
    }
  };

  const addGuest = () => {
    setGuests([...guests, { name: '', phone: '', countryCode: '+91', eventIds: [], lodgingName: '' }]);
    setPhoneErrors([...phoneErrors, { hasError: false, message: '' }]);
  };

  const removeGuest = (index: number) => {
    setGuests(guests.filter((_, i) => i !== index));
  };

  const updateGuest = (index: number, field: keyof Guest, value: string | number[]) => {
    const newGuests = [...guests];
    newGuests[index] = { ...newGuests[index], [field]: value };
    setGuests(newGuests);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validate all phone numbers first
    const phoneValidations = guests.map((guest, index) => {
      const error = validatePhoneNumber(guest.phone, guest.countryCode);
      const newErrors = [...phoneErrors];
      newErrors[index] = error;
      setPhoneErrors(newErrors);
      return !error.hasError;
    });

    const isFormValid = guests.every((guest, index) => 
      guest.name && 
      guest.phone && 
      phoneValidations[index] && 
      guest.eventIds.length > 0
    );

    if (!isFormValid) {
      alert('Please fill out all details and select at least one event for each guest.');
      return;
    }

    const requests: CreateInvitationRequest[] = guests.map(guest => ({
      name: guest.name,
      phone: `${guest.countryCode}${guest.phone}`,
      eventIds: guest.eventIds,
      lodgingName: guest.lodgingName
    }));

    setPendingSubmission(requests);
    setShowAuthDialog(true);
  };

  const handleAuthSubmit = async (authCode: string) => {
    setShowAuthDialog(false);
    setIsSubmitting(true);

    try {
      const response = await fetch(`https://wedding-api.srivatsa.dev/api/invitations/bulk?authCode=${encodeURIComponent(authCode)}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(pendingSubmission),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      console.log('Form Submitted:', result);
      
      setShowSuccess(true);
      setGuests([{ name: '', phone: '', countryCode: '+91', eventIds: [], lodgingName: '' }]);
      setPhoneErrors([{ hasError: false, message: '' }]);
      setPendingSubmission([]);
      
      setTimeout(() => setShowSuccess(false), 3000);
    } catch (error) {
      console.error('Error submitting form:', error);
      alert('Failed to submit form. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="bg-gray-900 text-gray-200 flex items-center justify-center min-h-screen p-4">
      <div className="w-full max-w-lg mx-auto bg-gray-800 p-8 md:p-12 rounded-2xl shadow-2xl shadow-pink-900/20 transition-all duration-300">
        <div className="text-center mb-10">
          <h1 className="text-4xl md:text-5xl font-bold text-white">Guest Invitation</h1>
          <p className="text-gray-400 mt-3">Add guests and select their individual events.</p>
        </div>

        {showSuccess && (
          <div className="mb-6 text-center p-4 bg-green-500/10 border border-green-500/30 rounded-lg">
            <p className="text-green-300 font-medium">Guest(s) successfully added to the list!</p>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-8">
          <div className="space-y-6">
            {guests.map((guest, index) => (
              <div key={index} className="guest-entry space-y-4 p-4 border border-gray-700 rounded-lg relative">
                {index > 0 && (
                  <button
                    type="button"
                    onClick={() => removeGuest(index)}
                    className="absolute -top-2 -right-2 bg-red-500 text-white h-6 w-6 rounded-full flex items-center justify-center text-xs font-bold hover:bg-red-600"
                  >
                    X
                  </button>
                )}
                
                <input
                  type="text"
                  required
                  value={guest.name}
                  onChange={(e) => updateGuest(index, 'name', e.target.value)}
                  className="w-full px-4 py-3 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-500 focus:ring-2 focus:ring-pink-500 focus:border-pink-500 transition-all"
                  placeholder="Full Name"
                />
                
                <div className="flex space-x-2">
                  <select
                    value={guest.countryCode}
                    onChange={(e) => {
                      updateGuest(index, 'countryCode', e.target.value);
                      const error = validatePhoneNumber(guest.phone, e.target.value);
                      const newErrors = [...phoneErrors];
                      newErrors[index] = error;
                      setPhoneErrors(newErrors);
                    }}
                    className="w-32 px-2 py-3 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-pink-500 focus:border-pink-500 transition-all"
                  >
                    {countryCodes.map(country => (
                      <option key={country.code} value={country.code}>
                        {country.flag} {country.code}
                      </option>
                    ))}
                  </select>
                  <div className="flex-1">
                    <input
                      type="tel"
                      required
                      value={guest.phone}
                      onChange={(e) => {
                        const newValue = e.target.value;
                        updateGuest(index, 'phone', newValue);
                        const error = validatePhoneNumber(newValue, guest.countryCode);
                        const newErrors = [...phoneErrors];
                        newErrors[index] = error;
                        setPhoneErrors(newErrors);
                      }}
                      className={`w-full px-4 py-3 bg-gray-700 border rounded-lg text-white placeholder-gray-500 focus:ring-2 focus:ring-pink-500 transition-all ${
                        phoneErrors[index]?.hasError 
                          ? 'border-red-500 focus:border-red-500' 
                          : 'border-gray-600 focus:border-pink-500'
                      }`}
                      placeholder="Phone Number"
                    />
                    {phoneErrors[index]?.hasError && (
                      <p className="mt-1 text-sm text-red-500">
                        {phoneErrors[index].message}
                      </p>
                    )}
                  </div>
                </div>
                
                <select
                  value={guest.lodgingName}
                  onChange={(e) => updateGuest(index, 'lodgingName', e.target.value)}
                  className="w-full px-4 py-3 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-pink-500 focus:border-pink-500 transition-all"
                >
                  <option value="">Select Accommodation</option>
                  {accommodationOptions.map(option => (
                    <option key={option.id} value={option.name}>
                      {option.name}
                    </option>
                  ))}
                </select>

                <div>
                  <label className="block text-sm font-medium text-gray-400 mb-2">
                    Events for this Guest
                  </label>
                  <div className="space-y-3">
                    {availableEvents.length > 0 ? (
                      availableEvents.map(event => (
                        <div key={event.id} className="flex items-center bg-gray-700/50 p-2 rounded-lg border border-gray-600/50 hover:bg-gray-700 transition-all">
                          <input
                            type="checkbox"
                            id={`${event.id}_guest${index}`}
                            checked={guest.eventIds.includes(event.id)}
                            onChange={(e) => {
                              const newEventIds = e.target.checked
                                ? [...guest.eventIds, event.id]
                                : guest.eventIds.filter((id: number) => id !== event.id);
                              updateGuest(index, 'eventIds', newEventIds);
                            }}
                            className="h-4 w-4 text-pink-500 bg-gray-600 border-gray-500 rounded focus:ring-pink-600"
                          />
                          <label
                            htmlFor={`${event.id}_guest${index}`}
                            className="ml-3 block text-sm font-medium text-gray-300"
                          >
                            {event.name}
                          </label>
                        </div>
                      ))
                    ) : (
                      <div className="flex items-center space-x-2 text-gray-500">
                        <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                        <span>Loading events...</span>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>

          <button
            type="button"
            onClick={addGuest}
            className="w-full bg-gray-700 text-pink-400 font-semibold py-2 px-4 rounded-lg hover:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-pink-500 transition-all"
          >
            + Add Another Guest
          </button>

          <button
            type="submit"
            disabled={isSubmitting}
            className="w-full bg-pink-600 text-white font-bold py-4 px-4 rounded-lg hover:bg-pink-700 focus:outline-none focus:ring-4 focus:ring-pink-500 focus:ring-opacity-50 transition-all transform hover:scale-105 disabled:opacity-50 disabled:cursor-wait"
          >
            {isSubmitting ? 'Adding...' : 'Add Guest(s) to List'}
          </button>
        </form>
      </div>
      <AuthDialog 
        isOpen={showAuthDialog}
        onSubmit={handleAuthSubmit}
        onCancel={() => {
          setShowAuthDialog(false);
          setPendingSubmission([]);
        }}
      />
    </div>
  );
}
