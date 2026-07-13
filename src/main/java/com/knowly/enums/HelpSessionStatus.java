package com.knowly.enums;

public enum HelpSessionStatus {
	PENDING("Pending"),
	ACTIVE("Active"),
	EXPIRED("Expired"),
	IGNORED("Ignored"),
	COMPLETED("Ended");
	
	private final String legacyName;
	
	HelpSessionStatus(String legacyName) {
		this.legacyName = legacyName;
	}
	
	public String getLegacyName() {
		return legacyName;
	}
	
	public static HelpSessionStatus fromString(String value) {
		if (value == null) return null;
		
		// Try direct match first (new uppercase values)
		try {
			return HelpSessionStatus.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException e) {
			// Try legacy values (old capitalized values)
			for (HelpSessionStatus status : values()) {
				if (status.legacyName.equalsIgnoreCase(value)) {
					return status;
				}
			}
			throw e;
		}
	}
}
