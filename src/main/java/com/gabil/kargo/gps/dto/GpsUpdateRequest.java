package com.gabil.kargo.gps.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class GpsUpdateRequest {
	private UUID driverId;
	private Double latitude;
	private Double longitude;
	private Instant timestamp;
}
