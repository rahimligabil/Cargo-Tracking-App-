package com.gabil.kargo.gps;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.gabil.kargo.gps.dto.GpsUpdateRequest;

import lombok.RequiredArgsConstructor;

/**
 * WebSocket/STOMP endpoint to receive location updates from mobile clients and broadcast them.
 */
@Controller
@RequiredArgsConstructor
public class GpsWebSocketController {

	private final GpsBroadcastService gpsBroadcastService;

	@MessageMapping("/gps/update") // client sends to /app/gps/update
	public void handleGpsUpdate(@Payload GpsUpdateRequest req) {
		if (req == null || req.getDriverId() == null) {
			return;
		}

		gpsBroadcastService.broadcastLocation(
				req.getDriverId(),
				req.getLatitude(),
				req.getLongitude()
		);
	}
}
