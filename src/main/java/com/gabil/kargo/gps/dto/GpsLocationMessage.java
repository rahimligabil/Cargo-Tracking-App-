package com.gabil.kargo.gps.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GpsLocationMessage {
	private UUID driverId;
	private Double latitude;
	private Double longitude;
	private Instant timestamp;
}
