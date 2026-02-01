package com.gabil.kargo.user;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.gabil.kargo.role.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Entity
@Table(name = "user",schema = "cargo",
uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"firebaseUid", "role"})

public class User {


	@Column(name = "name", length = 100,nullable = false)
	private String userName;
	
	
	@Column(name = "surname", length = 100,nullable = false)
	private String userSurname;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", updatable = false, nullable = false)
	@EqualsAndHashCode.Include
	private UUID userId;
	
	
	@Email
	@Column(nullable = false,unique = true, length = 256,name = "email")
	private String userEmail;
	
	
	@Column(length = 256, name = "phone")
	private String phone;
	
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;
	
	
	@Column(name = "activity_status",nullable = false)
	private boolean isActive = false;
	
	
	@Column(name = "firebase_uid", unique = true, length = 64)
	private String firebaseUid;
	
	
	@Column(name = "created_at", updatable = false, nullable = true)
	@CreationTimestamp
	private Instant createdAt;
	
	
	@Column(name = "last_login_at",nullable = false)
	@UpdateTimestamp
	private Instant lastloginAt;
	
	@Builder.Default
	@Column(name = "deleted_status", nullable = false)
	private boolean isDeleted = false;

	
}



	
