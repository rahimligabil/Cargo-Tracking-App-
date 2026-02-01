package com.gabil.kargo.gps;

import java.time.Instant;
import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.gabil.kargo.gps.dto.GpsLocationMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GpsBroadcastService {

	private final SimpMessagingTemplate messagingTemplate;

	public void broadcastLocation(UUID driverId, Double lat, Double lon) {
		GpsLocationMessage message = GpsLocationMessage.builder()
				.driverId(driverId)
				.latitude(lat)
				.longitude(lon)
				.timestamp(Instant.now())
				.build();

		messagingTemplate.convertAndSend("/topic/gps/all", message);
		messagingTemplate.convertAndSend("/topic/gps/driver/" + driverId, message);
	}
}
