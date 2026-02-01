package com.gabil.kargo.gps;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gabil.kargo.gps.dto.GpsLocationMessage;
import com.gabil.kargo.gps.dto.GpsUpdateRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/gps")
@RequiredArgsConstructor
public class GpsController {

	private final GpsBroadcastService gpsBroadcastService;

	@PostMapping("/update")
	public ResponseEntity<GpsLocationMessage> updateLocation(@RequestBody GpsUpdateRequest req) {
		gpsBroadcastService.broadcastLocation(
				req.getDriverId(),
				req.getLatitude(),
				req.getLongitude()
		);

		GpsLocationMessage response = GpsLocationMessage.builder()
				.driverId(req.getDriverId())
				.latitude(req.getLatitude())
				.longitude(req.getLongitude())
				.timestamp(req.getTimestamp())
				.build();

		return ResponseEntity.ok(response);
	}
}
