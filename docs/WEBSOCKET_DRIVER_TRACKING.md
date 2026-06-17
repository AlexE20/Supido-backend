# Driver Location & Order Tracking WebSocket

Real-time driver location tracking using STOMP over WebSocket (with SockJS fallback).

## Connection

- **Endpoint:** `ws://<host>/ws` (SockJS fallback also available at `http://<host>/ws`)
- **Protocol:** STOMP over WebSocket
- **Auth:** None currently enforced — `/ws/**` is `permitAll()` in `SecurityConfiguration`. Do not treat this channel as authenticated; do not send sensitive data beyond location/order status until a handshake auth check is added.
- **CORS:** Hardcoded to `http://localhost:3000` in `SecurityConfiguration` — update this for other environments/deployed frontends.

### Example connect (JS, `@stomp/stompjs` + `sockjs-client`)

```js
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const client = new Client({
  webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
  onConnect: () => {
    // subscribe / publish here
  },
});

client.activate();
```

## Driver → Server: send a location update

**Destination:** `/app/driver/location`

```json
{
  "deliveryPersonId": 5,
  "latitude": 13.6929,
  "longitude": -89.2182,
  "timestamp": "2026-06-15T14:30:45.123456"
}
```

- `deliveryPersonId` (number, required)
- `latitude` (number, required, -90 to 90)
- `longitude` (number, required, -180 to 180)
- `timestamp` (ISO-8601 string, optional — server fills it in if omitted)

```js
client.publish({
  destination: "/app/driver/location",
  body: JSON.stringify({
    deliveryPersonId: 5,
    latitude: 13.6929,
    longitude: -89.2182,
  }),
});
```

On receipt, the server updates the `DeliveryPerson`'s `latitude`/`longitude`/`lastLocationAt`, then looks up that driver's active orders (status `CONFIRMED`, `PREPARING`, or `ON_THE_WAY`) and broadcasts the update to each order's tracking topic.

## Server → Client: order tracking broadcast

**Topic:** `/topic/tracking/{orderId}`

Clients (e.g. the customer's order-tracking screen) subscribe per order to receive live driver location updates for that order.

```json
{
  "orderId": 42,
  "deliveryPersonId": 5,
  "latitude": 13.6929,
  "longitude": -89.2182,
  "orderStatus": "ON_THE_WAY",
  "timestamp": "2026-06-15T14:30:45.123456"
}
```

```js
client.subscribe(`/topic/tracking/${orderId}`, (message) => {
  const update = JSON.parse(message.body);
  // update map marker with update.latitude / update.longitude
});
```

Notes:
- Broadcasts only happen while the order is in an active status (`CONFIRMED`, `PREPARING`, `ON_THE_WAY`). No broadcast is sent for delivered/cancelled orders.
- There is no throttling — a broadcast is sent on every location update the driver sends, so clients should rate-limit how often they push updates if battery/bandwidth matters.

## Reference

| Item | Location |
|---|---|
| WebSocket/STOMP config | `src/main/java/com/backend/supido/websocket/config/WebSocketConfig.java` |
| Location update handler | `src/main/java/com/backend/supido/websocket/controller/LocationWebSocketController.java` |
| Request DTO | `src/main/java/com/backend/supido/websocket/dto/LocationUpdateMessage.java` |
| Broadcast DTO | `src/main/java/com/backend/supido/websocket/dto/LocationBroadcast.java` |
| Security rule for `/ws/**` | `src/main/java/com/backend/supido/auth/security/SecurityConfiguration.java` |
