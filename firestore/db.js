/**
 * ResQ254 — Database Service
 * ─────────────────────────────────────────────────────────────────────────────
 * All Firestore read/write operations in one place.
 * Your teammates import from this file — they never write Firestore calls
 * directly in their UI or logic code.
 *
 * Usage (from anywhere in the app):
 *   import { reportIncident, getNearbyProviders } from './db.js';
 * ─────────────────────────────────────────────────────────────────────────────
 */

import {
  getFirestore,
  collection,
  doc,
  addDoc,
  getDoc,
  getDocs,
  updateDoc,
  query,
  where,
  orderBy,
  limit,
  GeoPoint,
  serverTimestamp,
} from "firebase/firestore";

const db = getFirestore();

// ═════════════════════════════════════════════════════════════════════════════
// USERS
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Create a new user profile after sign-up.
 * @param {string} uid - Firebase Auth UID
 * @param {object} data - { full_name, phone_number, email }
 */
export async function createUser(uid, data) {
  await setDoc(doc(db, "users", uid), {
    uid,
    full_name: data.full_name,
    phone_number: data.phone_number,
    email: data.email,
    role: "resident",
    location: null,          // Updated when user grants location permission
    is_guardian: false,
    is_verified: false,
    created_at: serverTimestamp(),
  });
}

/**
 * Fetch a user's profile by their UID.
 * @param {string} uid
 * @returns {object|null}
 */
export async function getUser(uid) {
  const snap = await getDoc(doc(db, "users", uid));
  return snap.exists() ? snap.data() : null;
}

/**
 * Update a user's live GPS location.
 * Called whenever the app detects a location change.
 * @param {string} uid
 * @param {number} lat
 * @param {number} lng
 */
export async function updateUserLocation(uid, lat, lng) {
  await updateDoc(doc(db, "users", uid), {
    location: new GeoPoint(lat, lng),
  });
}

// ═════════════════════════════════════════════════════════════════════════════
// INCIDENTS
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Report a new emergency incident.
 * This is the core action — what happens when a user taps "Report".
 *
 * @param {string} reportedByUid - UID of the person reporting
 * @param {string} incidentType  - 'medical' | 'accident' | 'fire' | 'crime' | 'other'
 * @param {number} lat
 * @param {number} lng
 * @param {string} addressDescription - Human-readable location hint
 * @param {string} [notes]
 * @returns {string} - The new incident's document ID
 */
export async function reportIncident(
  reportedByUid,
  incidentType,
  lat,
  lng,
  addressDescription,
  notes = ""
) {
  const ref = await addDoc(collection(db, "incidents"), {
    reported_by_uid: reportedByUid,
    incident_type: incidentType,
    location: new GeoPoint(lat, lng),
    address_description: addressDescription,
    status: "active",
    reported_at: serverTimestamp(),
    resolved_at: null,
    notes,
  });
  return ref.id;
}

/**
 * Mark an incident as resolved.
 * @param {string} incidentId
 */
export async function resolveIncident(incidentId) {
  await updateDoc(doc(db, "incidents", incidentId), {
    status: "resolved",
    resolved_at: serverTimestamp(),
  });
}

/**
 * Get all active incidents (for admin dashboard or map view).
 * @returns {Array}
 */
export async function getActiveIncidents() {
  const q = query(
    collection(db, "incidents"),
    where("status", "==", "active"),
    orderBy("reported_at", "desc")
  );
  const snap = await getDocs(q);
  return snap.docs.map((d) => ({ id: d.id, ...d.data() }));
}

// ═════════════════════════════════════════════════════════════════════════════
// ALERTS
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Create an alert for a specific recipient.
 * Typically called by a Cloud Function after an incident is reported —
 * but exposed here for flexibility.
 *
 * @param {string} incidentId
 * @param {string} recipientUid
 * @param {'bystander'|'provider'|'guardian'} alertType
 * @returns {string} - Alert document ID
 */
export async function createAlert(incidentId, recipientUid, alertType) {
  const ref = await addDoc(collection(db, "alerts"), {
    incident_id: incidentId,
    recipient_uid: recipientUid,
    alert_type: alertType,
    status: "sent",
    sent_at: serverTimestamp(),
    responded_at: null,
  });
  return ref.id;
}

/**
 * Mark an alert as seen by the recipient.
 * @param {string} alertId
 */
export async function markAlertSeen(alertId) {
  await updateDoc(doc(db, "alerts", alertId), { status: "seen" });
}

/**
 * Mark an alert as responded to.
 * @param {string} alertId
 */
export async function respondToAlert(alertId) {
  await updateDoc(doc(db, "alerts", alertId), {
    status: "responded",
    responded_at: serverTimestamp(),
  });
}

/**
 * Get all alerts for a specific user (their notification inbox).
 * @param {string} uid
 * @returns {Array}
 */
export async function getUserAlerts(uid) {
  const q = query(
    collection(db, "alerts"),
    where("recipient_uid", "==", uid),
    orderBy("sent_at", "desc"),
    limit(20)
  );
  const snap = await getDocs(q);
  return snap.docs.map((d) => ({ id: d.id, ...d.data() }));
}

// ═════════════════════════════════════════════════════════════════════════════
// EMERGENCY CONTACTS
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Get all verified emergency contacts.
 * Used in the app's contacts directory screen.
 * @param {string} [category] - Optional filter: 'ambulance' | 'police' | 'fire' | 'relief'
 * @returns {Array}
 */
export async function getEmergencyContacts(category = null) {
  let q = query(
    collection(db, "emergency_contacts"),
    where("is_verified", "==", true),
    orderBy("rating", "desc")
  );

  if (category) {
    q = query(
      collection(db, "emergency_contacts"),
      where("is_verified", "==", true),
      where("category", "==", category),
      orderBy("rating", "desc")
    );
  }

  const snap = await getDocs(q);
  return snap.docs.map((d) => ({ id: d.id, ...d.data() }));
}

// ═════════════════════════════════════════════════════════════════════════════
// SERVICE PROVIDERS  —  Nearest-provider aggregation
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Get all active service providers of a given type.
 * After fetching, filter client-side by distance using haversine().
 *
 * NOTE: Firestore doesn't support native geo-radius queries.
 * For production, use the 'geofire-common' package or Firebase Extensions
 * (Firestore: Run Geo Queries). For MVP, this client-side approach works fine.
 *
 * @param {string} [type] - 'hospital' | 'ambulance' | 'police' | 'fire' | null (all)
 * @returns {Array}
 */
export async function getServiceProviders(type = null) {
  let q = query(
    collection(db, "service_providers"),
    where("is_active", "==", true)
  );

  if (type) {
    q = query(
      collection(db, "service_providers"),
      where("is_active", "==", true),
      where("type", "==", type)
    );
  }

  const snap = await getDocs(q);
  return snap.docs.map((d) => ({ id: d.id, ...d.data() }));
}

/**
 * Get the nearest service providers to an incident location.
 * Returns providers sorted by distance, closest first.
 *
 * @param {number} incidentLat
 * @param {number} incidentLng
 * @param {string} [type]      - Optional type filter
 * @param {number} [maxResults] - How many to return (default 3)
 * @returns {Array} - Providers with a `distance_km` field added
 */
export async function getNearbyProviders(
  incidentLat,
  incidentLng,
  type = null,
  maxResults = 3
) {
  const providers = await getServiceProviders(type);

  // Calculate distance for each provider
  const withDistance = providers.map((provider) => {
    const dist = haversine(
      incidentLat,
      incidentLng,
      provider.location.latitude,
      provider.location.longitude
    );
    return { ...provider, distance_km: Math.round(dist * 10) / 10 };
  });

  // Sort by distance, return nearest N
  return withDistance
    .sort((a, b) => a.distance_km - b.distance_km)
    .slice(0, maxResults);
}

// ═════════════════════════════════════════════════════════════════════════════
// UTILITY
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Haversine formula — calculates distance between two GPS coordinates.
 * Returns distance in kilometres.
 *
 * @param {number} lat1
 * @param {number} lon1
 * @param {number} lat2
 * @param {number} lon2
 * @returns {number}
 */
export function haversine(lat1, lon1, lat2, lon2) {
  const R = 6371; // Earth's radius in km
  const dLat = toRad(lat2 - lat1);
  const dLon = toRad(lon2 - lon1);
  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(toRad(lat1)) *
      Math.cos(toRad(lat2)) *
      Math.sin(dLon / 2) *
      Math.sin(dLon / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}

function toRad(deg) {
  return deg * (Math.PI / 180);
}
