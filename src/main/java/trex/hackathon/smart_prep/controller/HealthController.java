package trex.hackathon.smart_prep.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

	private final JdbcTemplate jdbcTemplate;

	@GetMapping
	public ResponseEntity< Map<String, Object > > checkHealth() {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> components = new HashMap<>();
		boolean isHealthy = true;

		// Add application info
		response.put("status", "UP");
		response.put("timestamp", LocalDateTime.now().toString());

		// Check database connection
		Map<String, Object> dbStatus = checkDatabaseHealth().getBody();
		components.put("database", dbStatus);

		if (!"UP".equals(dbStatus.get("status"))) {
			isHealthy = false;
		}

		// Add JVM info
		Map<String, Object> jvmStatus = checkJvmHealth();
		components.put("jvm", jvmStatus);

		// Add disk space info
		Map<String, Object> diskStatus = checkDiskSpace();
		components.put("diskSpace", diskStatus);

		if (!"UP".equals(diskStatus.get("status"))) {
			isHealthy = false;
		}

		// Update overall status if any component is down
		if (!isHealthy) {
			response.put("status", "DOWN");
		}

		response.put("components", components);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/db")
	public ResponseEntity<Map<String, Object>> checkDatabaseHealth() {
		Map<String, Object> status = new HashMap<>();

		try {
			// Execute a simple query to check if database is accessible
			Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

			if (result != null && result == 1) {
				status.put("status", "UP");
				status.put("message", "Database connection successful");

				// Get additional database details
				status.put("databaseProductName", jdbcTemplate.queryForObject("SELECT current_database()", String.class));
				status.put("databaseVersion", jdbcTemplate.queryForObject("SELECT version()", String.class));

				// Get connection pool info if possible
				try {
					Integer activeConnections = jdbcTemplate.queryForObject(
							"SELECT count(*) FROM pg_stat_activity", Integer.class);
					status.put("activeConnections", activeConnections);
				} catch (Exception e) {
					// Ignore if this query fails
				}
			} else {
				status.put("status", "DOWN");
				status.put("message", "Database connection test failed");
			}
		} catch (Exception e) {
			status.put("status", "DOWN");
			status.put("message", "Database connection error");
			status.put("error", e.getMessage());
		}

		return ResponseEntity.ok(status);
	}

	private Map<String, Object> checkJvmHealth() {
		Map<String, Object> status = new HashMap<>();
		Runtime runtime = Runtime.getRuntime();

		status.put("status", "UP");
		status.put("availableProcessors", runtime.availableProcessors());
		status.put("freeMemory", bytesToMB(runtime.freeMemory()));
		status.put("totalMemory", bytesToMB(runtime.totalMemory()));
		status.put("maxMemory", bytesToMB(runtime.maxMemory()));

		return status;
	}

	private Map<String, Object> checkDiskSpace() {
		Map<String, Object> status = new HashMap<>();

		try {
			java.io.File file = new java.io.File(".");
			status.put("status", "UP");
			status.put("total", bytesToGB(file.getTotalSpace()));
			status.put("free", bytesToGB(file.getFreeSpace()));
			status.put("usable", bytesToGB(file.getUsableSpace()));

			// If free disk space is less than 1GB, consider it a warning
			if (file.getFreeSpace() < 1_073_741_824L) { // 1GB in bytes
				status.put("status", "WARNING");
				status.put("message", "Low disk space");
			}

			// If free disk space is less than 100MB, consider it down
			if (file.getFreeSpace() < 104_857_600L) { // 100MB in bytes
				status.put("status", "DOWN");
				status.put("message", "Critical disk space shortage");
			}
		} catch (Exception e) {
			status.put("status", "UNKNOWN");
			status.put("message", "Failed to check disk space");
			status.put("error", e.getMessage());
		}

		return status;
	}

	private String bytesToMB(long bytes) {
		return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
	}

	private String bytesToGB(long bytes) {
		return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
	}
}
