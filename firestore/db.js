/**
 * ResQ254 — Database Service
 */

import {
  getFirestore,
  collection,
  doc,
  addDoc,
  setDoc,            // ✅ FIX: added
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

export async function createUser(uid, data) {
  await setDoc(doc(db, "users", uid), {
    uid,
    full_name: data.full_name,
    phone_number: data.phone_number,
    email: data.email,

    role: "resident",
    location: null,
    is_guardian: false,
    is_verified: false,

    created_at: serverTimestamp(),
  });
}

export async function getUser(uid) {
  const snap = await getDoc(doc(db, "users", uid));
  return snap.exists() ? snap.data() : null;
}

export async function updateUserLocation(uid, lat, lng) {
  await updateDoc(doc(db, "users", uid), {
    location: new GeoPoint(lat, lng),
  });
}


// ═════════════════════════════════════════════════════════════════════════════
// INCIDENTS
// ═════════════════════════════════════════════════════════════════════════════

export async function reportIncident(
  reportedByUid,
  incidentType,
  lat,
  lng,
  addressDescription,
  notes = ""
) {

  const ref = await addDoc(collection(db, "incidents"), {
    reported_by_uid: reportedByUid,   // ✅ matches rules
    incident_type: incidentType,
    location: new GeoPoint(lat, lng),
    address_description: addressDescription,

    status: "active",                // ✅ enforced by rules
    reported_at: serverTimestamp(),
    resolved_at: null,
    notes,
  });

  return ref.id;
}

export async function resolveIncident(incidentId) {
  await updateDoc(doc(db, "incidents", incidentId), {
    status: "resolved",
    resolved_at: serverTimestamp(),
  });
}

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

export async function markAlertSeen(alertId) {
  await updateDoc(doc(db, "alerts", alertId), {
    status: "seen"
  });
}

export async function respondToAlert(alertId, incidentId = null) {

  // ✅ update alert
  await updateDoc(doc(db, "alerts", alertId), {
    status: "responded",
    responded_at: serverTimestamp(),
  });

  // ✅ OPTIONAL: update incident status
  if (incidentId) {
    await updateDoc(doc(db, "incidents", incidentId), {
      status: "responding"
    });
  }
}

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
// SERVICE PROVIDERS
// ═════════════════════════════════════════════════════════════════════════════

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


// ✅ SAFE VERSION (NO CRASHES)
export async function getNearbyProviders(
  incidentLat,
  incidentLng,
  type = null,
  maxResults = 3
) {

  const providers = await getServiceProviders(type);

  const validProviders = providers.filter(
    (provider) =>
      provider.location &&
      provider.location.latitude !== undefined &&
      provider.location.longitude !== undefined
  );

  const withDistance = validProviders.map((provider) => {
    const dist = haversine(
      incidentLat,
      incidentLng,
      provider.location.latitude,
      provider.location.longitude
    );

    return {
      ...provider,
      distance_km: Math.round(dist * 10) / 10
    };
  });

  return withDistance
    .sort((a, b) => a.distance_km - b.distance_km)
    .slice(0, maxResults);
}


// ═════════════════════════════════════════════════════════════════════════════
// UTILITY
// ═════════════════════════════════════════════════════════════════════════════

export function haversine(lat1, lon1, lat2, lon2) {
  const R = 6371;

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