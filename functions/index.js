const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

admin.initializeApp();

const db = admin.firestore();

/** Reads is-active regardless of hyphen/underscore naming. */
function isActive(data) {
  return data["is-active"] === true || data.is_active === true;
}

/** Reads radius-km regardless of hyphen/underscore naming. */
function getRadiusKm(data) {
  const r = data["radius-km"] ?? data.radius_km;
  return typeof r === "number" ? r : 5;
}

function hasValidLocation(loc) {
  return loc && loc.latitude !== undefined && loc.longitude !== undefined;
}

exports.onIncidentCreated = onDocumentCreated(
  "incidents/{incidentId}",
  async (event) => {
    const incident = event.data.data();
    const incidentId = event.params.incidentId;
    const incidentLocation = incident.location;

    console.log("🚨 Incident created:", incidentId);

    if (!hasValidLocation(incidentLocation)) {
      console.error(`Incident ${incidentId} has no valid location, skipping fan-out.`);
      return;
    }

    const providersSnapshot = await db.collection("service-providers").get();

    const alerts = [];

    providersSnapshot.forEach((doc) => {
      const provider = doc.data();

      if (!isActive(provider)) return;

      const providerLocation = provider.location;
      if (!hasValidLocation(providerLocation)) return;

      const distance = calculateDistance(incidentLocation, providerLocation);
      const radius = getRadiusKm(provider);

      if (distance <= radius) {
        alerts.push({
          incident_id: incidentId,
          recipient_uid: doc.id,
          alert_type: "provider",
          status: "sent",
          sent_at: admin.firestore.FieldValue.serverTimestamp()
        });
      }
    });

    if (alerts.length === 0) {
      console.log("No providers in range, no alerts created.");
      return;
    }

    const batch = db.batch();

    alerts.forEach((alert) => {
      const ref = db.collection("alerts").doc();
      batch.set(ref, alert);
    });

    await batch.commit();

    console.log(`✅ Alerts created: ${alerts.length}`);
  }
);

/* ========= UTIL ========= */

function calculateDistance(a, b) {
  const R = 6371;

  const dLat = toRad(b.latitude - a.latitude);
  const dLng = toRad(b.longitude - a.longitude);

  const lat1 = toRad(a.latitude);
  const lat2 = toRad(b.latitude);

  const aVal =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.sin(dLng / 2) * Math.sin(dLng / 2) *
    Math.cos(lat1) * Math.cos(lat2);

  const c = 2 * Math.atan2(Math.sqrt(aVal), Math.sqrt(1 - aVal));

  return R * c;
}

function toRad(x) {
  return x * Math.PI / 180;
}