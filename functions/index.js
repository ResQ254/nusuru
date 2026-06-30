const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

admin.initializeApp();

const db = admin.firestore();

exports.onIncidentCreated = onDocumentCreated(
  "incidents/{incidentId}",
  async (event) => {

    const incident = event.data.data();
    const incidentId = event.params.incidentId;
    const incidentLocation = incident.location;

    console.log("🚨 Incident created:", incidentId);

    const providersSnapshot = await db.collection("service_providers")
      .where("is_active", "==", true)
      .get();

    const alerts = [];

    providersSnapshot.forEach((doc) => {

      const provider = doc.data();

      // ✅ CRITICAL FIX (you needed this)
      if (
        !provider.location ||
        provider.location.latitude === undefined ||
        provider.location.longitude === undefined
      ) {
        return;
      }

      const distance = calculateDistance(
        incidentLocation,
        provider.location
      );

      const radius = provider.radius_km || 5;

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
