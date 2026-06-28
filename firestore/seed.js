/**
 * ResQ254 — Firestore Seed Script
 * ─────────────────────────────────────────────────────────────────────────────
 * Run this once to populate your Firestore database with:
 *   • 3 sample users  (resident, guardian, admin)
 *   • 2 sample incidents
 *   • 3 sample alerts
 *   • 5 verified emergency contacts (real Nairobi numbers)
 *   • 5 service providers (hospitals, police, ambulance)
 *
 * HOW TO RUN:
 *   1. npm install firebase-admin
 *   2. Download your Firebase service account key from:
 *      Firebase Console → Project Settings → Service Accounts → Generate new key
 *   3. Save it as serviceAccountKey.json in this folder
 *   4. node seed.js
 * ─────────────────────────────────────────────────────────────────────────────
 */

const { initializeApp, cert } = require("firebase-admin/app");
const { getFirestore } = require("firebase-admin/firestore");
const { GeoPoint, Timestamp } = require("@google-cloud/firestore");
const serviceAccount = require("./serviceAccountKey.json");

initializeApp({
  credential: cert(serviceAccount),
});

const db = getFirestore();

// ─── USERS ────────────────────────────────────────────────────────────────────
const users = [
  {
    uid: "user_001",
    full_name: "Amina Ochieng",
    phone_number: "+254712345678",
    email: "amina.ochieng@gmail.com",
    role: "resident",
    location: new GeoPoint(-1.2921, 36.8219), // Nairobi CBD
    is_guardian: false,
    is_verified: true,
    created_at: Timestamp.fromDate(new Date("2026-01-10")),
  },
  {
    uid: "user_002",
    full_name: "Brian Kamau",
    phone_number: "+254723456789",
    email: "brian.kamau@gmail.com",
    role: "resident",
    location: new GeoPoint(-1.3031, 36.8082), // Westlands
    is_guardian: true,         // First aid certificate holder
    is_verified: true,
    created_at: Timestamp.fromDate(new Date("2026-01-15")),
  },
  {
    uid: "user_admin_001",
    full_name: "ResQ254 Admin",
    phone_number: "+254700000001",
    email: "admin@resq254.co.ke",
    role: "admin",
    location: new GeoPoint(-1.2921, 36.8219),
    is_guardian: false,
    is_verified: true,
    created_at: Timestamp.fromDate(new Date("2025-12-01")),
  },
];

// ─── INCIDENTS ────────────────────────────────────────────────────────────────
const incidents = [
  {
    incident_id: "incident_001",
    reported_by_uid: "user_001",
    incident_type: "medical",       // medical | accident | fire | crime | other
    location: new GeoPoint(-1.2921, 36.8219),
    address_description: "Outside KCB Bank, Moi Avenue, Nairobi CBD",
    status: "active",               // active | responding | resolved
    reported_at: Timestamp.fromDate(new Date("2026-06-27T09:15:00")),
    resolved_at: null,
    notes: "Person collapsed near ATM, not responsive",
  },
  {
    incident_id: "incident_002",
    reported_by_uid: "user_002",
    incident_type: "accident",
    location: new GeoPoint(-1.3031, 36.8082),
    address_description: "Westlands roundabout, near Sarit Centre",
    status: "resolved",
    reported_at: Timestamp.fromDate(new Date("2026-06-26T14:30:00")),
    resolved_at: Timestamp.fromDate(new Date("2026-06-26T15:10:00")),
    notes: "Matatu-motorcycle collision, handled by St John Ambulance",
  },
];

// ─── ALERTS ───────────────────────────────────────────────────────────────────
const alerts = [
  {
    alert_id: "alert_001",
    incident_id: "incident_001",
    recipient_uid: "user_002",       // Nearby guardian notified
    alert_type: "bystander",         // bystander | provider | guardian
    status: "sent",                  // sent | seen | responded | dismissed
    sent_at: Timestamp.fromDate(new Date("2026-06-27T09:15:05")),
    responded_at: null,
  },
  {
    alert_id: "alert_002",
    incident_id: "incident_001",
    recipient_uid: "provider_001",   // Nearest hospital notified
    alert_type: "provider",
    status: "responded",
    sent_at: Timestamp.fromDate(new Date("2026-06-27T09:15:06")),
    responded_at: Timestamp.fromDate(new Date("2026-06-27T09:17:30")),
  },
  {
    alert_id: "alert_003",
    incident_id: "incident_002",
    recipient_uid: "provider_003",
    alert_type: "provider",
    status: "responded",
    sent_at: Timestamp.fromDate(new Date("2026-06-26T14:30:08")),
    responded_at: Timestamp.fromDate(new Date("2026-06-26T14:35:00")),
  },
];

// ─── EMERGENCY CONTACTS ───────────────────────────────────────────────────────
// ResQ254's unfair advantage: verified, local, hard-to-replicate DB
const emergencyContacts = [
  {
    contact_id: "contact_001",
    name: "Kenya National Ambulance Service",
    category: "ambulance",
    phone_number: "0800 723 253",
    location: new GeoPoint(-1.2921, 36.8219),
    address: "Nairobi, Kenya",
    is_verified: true,
    rating: 4.2,
    last_updated: Timestamp.fromDate(new Date("2026-06-01")),
  },
  {
    contact_id: "contact_002",
    name: "Nairobi Police Headquarters",
    category: "police",
    phone_number: "999",
    location: new GeoPoint(-1.2864, 36.8172),
    address: "University Way, Nairobi CBD",
    is_verified: true,
    rating: 3.8,
    last_updated: Timestamp.fromDate(new Date("2026-06-01")),
  },
  {
    contact_id: "contact_003",
    name: "St John Ambulance Kenya",
    category: "ambulance",
    phone_number: "+254 722 200 131",
    location: new GeoPoint(-1.2977, 36.8161),
    address: "Upper Hill, Nairobi",
    is_verified: true,
    rating: 4.6,
    last_updated: Timestamp.fromDate(new Date("2026-06-01")),
  },
  {
    contact_id: "contact_004",
    name: "Kenya Red Cross",
    category: "relief",
    phone_number: "+254 20 350 5000",
    location: new GeoPoint(-1.3004, 36.7836),
    address: "Red Cross Road, off Waiyaki Way, Nairobi",
    is_verified: true,
    rating: 4.5,
    last_updated: Timestamp.fromDate(new Date("2026-06-01")),
  },
  {
    contact_id: "contact_005",
    name: "Nairobi Fire Brigade",
    category: "fire",
    phone_number: "020 222 2181",
    location: new GeoPoint(-1.2833, 36.8167),
    address: "Fire Station, Nairobi CBD",
    is_verified: true,
    rating: 4.0,
    last_updated: Timestamp.fromDate(new Date("2026-06-01")),
  },
];

// ─── SERVICE PROVIDERS ────────────────────────────────────────────────────────
// Used for nearest-provider aggregation (answering the reviewer's question)
const serviceProviders = [
  {
    provider_id: "provider_001",
    name: "Kenyatta National Hospital",
    type: "hospital",              // hospital | clinic | police | ambulance | fire
    phone_number: "+254 20 272 6300",
    location: new GeoPoint(-1.3007, 36.8073),
    address: "Hospital Road, Upper Hill, Nairobi",
    radius_km: 10.0,               // Coverage radius for dispatch
    is_active: true,
    last_updated: Timestamp.fromDate(new Date("2026-06-01")),
  },
  {
    provider_id: "provider_002",
    name: "Nairobi Hospital",
    type: "hospital",
    phone_number: "+254 20 284 5000",
    location: new GeoPoint(-1.2975, 36.8093),
    address: "Argwings Kodhek Road, Hurlingham, Nairobi",
    radius_km: 8.0,
    is_active: true,
    last_updated: Timestamp.fromDate(new Date("2026-06-01")),
  },
  {
    provider_id: "provider_003",
    name: "St John Ambulance — Westlands Unit",
    type: "ambulance",
    phone_number: "+254 722 200 131",
    location: new GeoPoint(-1.2634, 36.8033),
    address: "Westlands, Nairobi",
    radius_km: 5.0,
    is_active: true,
    last_updated: Timestamp.fromDate(new Date("2026-06-01")),
  },
  {
    provider_id: "provider_004",
    name: "Parklands Police Station",
    type: "police",
    phone_number: "+254 20 374 2222",
    location: new GeoPoint(-1.2607, 36.8219),
    address: "Ojijo Road, Parklands, Nairobi",
    radius_km: 4.0,
    is_active: true,
    last_updated: Timestamp.fromDate(new Date("2026-06-01")),
  },
  {
    provider_id: "provider_005",
    name: "Aga Khan University Hospital",
    type: "hospital",
    phone_number: "+254 20 366 2000",
    location: new GeoPoint(-1.2615, 36.8083),
    address: "3rd Parklands Avenue, Nairobi",
    radius_km: 7.0,
    is_active: true,
    last_updated: Timestamp.fromDate(new Date("2026-06-01")),
  },
];

// ─── SEED FUNCTION ─────────────────────────────────────────────────────────────
async function seed() {
  console.log("🚀 ResQ254 — Starting Firestore seed...\n");

  // Users
  console.log("📋 Seeding users...");
  for (const user of users) {
    await db.collection("users").doc(user.uid).set(user);
    console.log(`   ✓ ${user.full_name} (${user.role})`);
  }

  // Incidents
  console.log("\n🚨 Seeding incidents...");
  for (const incident of incidents) {
    await db.collection("incidents").doc(incident.incident_id).set(incident);
    console.log(`   ✓ ${incident.incident_type} — ${incident.address_description}`);
  }

  // Alerts
  console.log("\n🔔 Seeding alerts...");
  for (const alert of alerts) {
    await db.collection("alerts").doc(alert.alert_id).set(alert);
    console.log(`   ✓ Alert ${alert.alert_id} → ${alert.alert_type} (${alert.status})`);
  }

  // Emergency Contacts
  console.log("\n📞 Seeding emergency contacts...");
  for (const contact of emergencyContacts) {
    await db.collection("emergency_contacts").doc(contact.contact_id).set(contact);
    console.log(`   ✓ ${contact.name}`);
  }

  // Service Providers
  console.log("\n🏥 Seeding service providers...");
  for (const provider of serviceProviders) {
    await db.collection("service_providers").doc(provider.provider_id).set(provider);
    console.log(`   ✓ ${provider.name} (${provider.type}, radius: ${provider.radius_km}km)`);
  }

  console.log("\n✅ Seed complete! Your ResQ254 database is ready.\n");
  process.exit(0);
}

seed().catch((err) => {
  console.error("❌ Seed failed:", err);
  process.exit(1);
});
