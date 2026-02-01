package com.gabil.kargo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gabil.kargo.delivery.Delivery;
import com.gabil.kargo.delivery.DeliveryRepository;
import com.gabil.kargo.delivery.DeliveryStatus;
import com.gabil.kargo.role.Role;
import com.gabil.kargo.role.RoleRepository;
import com.gabil.kargo.user.User;
import com.gabil.kargo.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final DeliveryRepository deliveryRepository;
	private final JdbcTemplate jdbcTemplate;

	@Value("${app.seed.roles:true}")
	private boolean seedRoles;
	
	@Value("${app.seed.deliveries:false}")
	private boolean seedDeliveries;
	
	@Value("${app.migrate.delivery-no:true}")
	private boolean migrateDeliveryNo;

	@Override
	@Transactional
	public void run(String... args) {
		jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS cargo");

		if (migrateDeliveryNo) {
			ensureDeliveryNoColumn();
		}

		ensureUserDeletedDefaults();

		if (seedRoles) {
			createRoleIfMissing("ROLE_DRIVER");
			createRoleIfMissing("ROLE_ADMIN");

			roleRepository.findAll()
					.forEach(role -> log.info("Available role {} -> {}", role.getRoleId(), role.getRoleName()));
		} else {
			log.debug("Role seeding skipped because app.seed.roles=false");
		}

		if (seedDeliveries) {
			seedFakeDeliveries();
		} else {
			log.debug("Delivery seeding skipped because app.seed.deliveries=false");
		}
	}

	private void createRoleIfMissing(String roleName) {
		roleRepository.findByRoleName(roleName)
				.orElseGet(() -> {
					log.info("Seeding missing role: {}", roleName);
					return roleRepository.save(Role.builder().roleName(roleName).build());
				});
	}

	private void ensureDeliveryNoColumn() {
		log.info("Ensuring cargo.delivery.delivery_no column exists");

		String dataType = jdbcTemplate.query("""
				SELECT data_type FROM information_schema.columns
				WHERE table_schema = 'cargo'
				  AND table_name = 'delivery'
				  AND column_name = 'delivery_no'
				""", rs -> rs.next() ? rs.getString(1) : null);

		if (dataType == null) {
			jdbcTemplate.execute("ALTER TABLE cargo.delivery ADD COLUMN delivery_no bigint");
			log.info("Added cargo.delivery.delivery_no column as bigint");
		} else if (!"bigint".equalsIgnoreCase(dataType)) {
			log.info("Recreating cargo.delivery.delivery_no as bigint");
			jdbcTemplate.execute("ALTER TABLE cargo.delivery DROP COLUMN IF EXISTS delivery_no");
			jdbcTemplate.execute("ALTER TABLE cargo.delivery ADD COLUMN delivery_no bigint");
		} else {
			log.debug("cargo.delivery.delivery_no already bigint, skipping recreate");
		}

		jdbcTemplate.execute("""
				DO $$
				BEGIN
					IF NOT EXISTS (
						SELECT 1 FROM pg_constraint
						WHERE conname = 'delivery_delivery_no_key'
					) THEN
						ALTER TABLE cargo.delivery ADD CONSTRAINT delivery_delivery_no_key UNIQUE (delivery_no);
					END IF;
				END$$;
				""");
	}

	private void ensureUserDeletedDefaults() {
		log.info("Ensuring cargo.user.deleted_status defaults to false and has no NULLs");

		jdbcTemplate.execute("""
				DO $$
				BEGIN
					IF EXISTS (
						SELECT 1 FROM information_schema.columns
						WHERE table_schema = 'cargo'
						  AND table_name = 'user'
						  AND column_name = 'deleted_status'
					) THEN
						UPDATE cargo."user"
						SET deleted_status = false
						WHERE deleted_status IS NULL;

						ALTER TABLE cargo."user"
							ALTER COLUMN deleted_status SET DEFAULT false,
							ALTER COLUMN deleted_status SET NOT NULL;
					END IF;
				END$$;
				""");
	}

	private void seedFakeDeliveries() {
		log.info("Seeding fake driver and deliveries (existing deliveries will be removed)");
		deliveryRepository.deleteAll();

		Role driverRole = roleRepository.findByRoleName("ROLE_DRIVER")
				.orElseThrow(() -> new IllegalStateException("Driver role missing"));

		User driver = userRepository.findByUserEmail("driver@example.com")
				.orElseGet(() -> userRepository.save(User.builder()
						.userName("Demo")
						.userSurname("Driver")
						.userEmail("driver@example.com")
						.phone("5550000000")
						.role(driverRole)
						.isActive(true)
						.isDeleted(false)
						.lastloginAt(java.time.Instant.now())
						.build()));

		driver.setRole(driverRole);
		driver.setActive(true);
		driver.setLastloginAt(java.time.Instant.now());
		driver.setDeleted(false);
		driver = userRepository.save(driver);

		Long currentMax = deliveryRepository.findMaxDeliveryNo();
		long counter = currentMax == null ? 0L : currentMax;

		Delivery d1 = Delivery.builder()
				.deliveryNo(++counter)
				.recipientName("Ali Veli")
				.recipientPhone("5551112233")
				.addressLine("İstanbul, Beşiktaş, Demo Sokak No:1")
				.status(DeliveryStatus.PENDING)
				.driver(driver)
				.build();

		Delivery d2 = Delivery.builder()
				.deliveryNo(++counter)
				.recipientName("Ayşe Yılmaz")
				.recipientPhone("5554445566")
				.addressLine("Ankara, Çankaya, Örnek Cadde No:2")
				.status(DeliveryStatus.COMPLETED)
				.driver(driver)
				.deliveredFeedback("Teslim edildi.")
				.build();

		Delivery d3 = Delivery.builder()
				.deliveryNo(++counter)
				.recipientName("Mehmet Demir")
				.recipientPhone("5557778899")
				.addressLine("İzmir, Karşıyaka, Test Mah. No:3")
				.status(DeliveryStatus.NOT_DELIVERED)
				.driver(driver)
				.undeliveredFeedback("Adres bulunamadı.")
				.build();

		Delivery d4 = Delivery.builder()
				.deliveryNo(++counter)
				.recipientName("Fatma Arslan")
				.recipientPhone("5552223344")
				.addressLine("Bursa, Nilüfer, Deneme Sokak No:4")
				.status(DeliveryStatus.PENDING)
				.driver(driver)
				.build();

		Delivery d5 = Delivery.builder()
				.deliveryNo(++counter)
				.recipientName("Can Kaya")
				.recipientPhone("5553334455")
				.addressLine("Antalya, Muratpaşa, Sahil Cad. No:5")
				.status(DeliveryStatus.COMPLETED)
				.driver(driver)
				.deliveredFeedback("Kapıya bırakıldı.")
				.build();

		Delivery d6 = Delivery.builder()
				.deliveryNo(++counter)
				.recipientName("Zeynep Aksoy")
				.recipientPhone("5554445566")
				.addressLine("Adana, Seyhan, Portakal Çiçeği Sokak No:6")
				.status(DeliveryStatus.NOT_DELIVERED)
				.driver(driver)
				.undeliveredFeedback("Alıcı yerinde yoktu.")
				.build();

		Delivery d7 = Delivery.builder()
				.deliveryNo(++counter)
				.recipientName("Emre Şahin")
				.recipientPhone("5555556677")
				.addressLine("Gaziantep, Şahinbey, Fıstık Mah. No:7")
				.status(DeliveryStatus.PENDING)
				.driver(driver)
				.build();

		Delivery d8 = Delivery.builder()
				.deliveryNo(++counter)
				.recipientName("Selin Koç")
				.recipientPhone("5556667788")
				.addressLine("Trabzon, Ortahisar, Meydan Cad. No:8")
				.status(DeliveryStatus.PENDING)
				.driver(driver)
				.build();

		deliveryRepository.saveAll(java.util.List.of(d1, d2, d3, d4, d5, d6, d7, d8));
		log.info("Seeded {} deliveries for driver {}", 8, driver.getUserEmail());
	}
}
