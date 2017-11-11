package me.badbones69.crazyenchantments.multisupport;

import org.bukkit.Bukkit;

public enum Version {

	TOO_OLD(-1),
	v1_7_R1(171), v1_7_R2(172), v1_7_R3(173), v1_7_R4(174),
	v1_8_R1(181), v1_8_R2(182), v1_8_R3(183),
	v1_9_R1(191), v1_9_R2(192),
	v1_10_R1(1101),
	v1_11_R1(1111),
	v1_12_R1(1121),
	TOO_NEW(-2);

	private static Version latest;
	private Integer versionInteger;
	public static Version currentVersion;

	private Version(int versionInteger) {
		this.versionInteger = versionInteger;
	}

	/**
	 *
	 * @return Get the server's Minecraft version.
	 */
	public static Version getCurrentVersion() {
		if(currentVersion == null) {
			String ver = Bukkit.getServer().getClass().getPackage().getName();
			int v = Integer.parseInt(ver.substring(ver.lastIndexOf('.') + 1).replaceAll("_", "").replaceAll("R", "").replaceAll("v", ""));
			for(Version version : values()) {
				if(version.getVersionInteger() == v) {
					currentVersion = version;
					break;
				}
			}
			if(v > Version.getLatestVersion().getVersionInteger()) {
				currentVersion = Version.getLatestVersion();
			}
			if(currentVersion == null) {
				currentVersion = Version.TOO_NEW;
			}
		}
		return currentVersion;
	}

	/**
	 *
	 * @return The server's minecraft version as an integer.
	 */
	public Integer getVersionInteger() {
		return this.versionInteger;
	}

	/**
	 * Get the latest version allowed by the Version class.
	 * @return The latest version.
	 */
	public static Version getLatestVersion() {
		if(latest == null) {
			Version v = Version.TOO_OLD;
			for(Version version : values()) {
				if(version.comparedTo(v) == 1) {
					v = version;
				}
			}
			return v;
		}else {
			return latest;
		}
	}

	/**
	 * This checks if the current version is older, newer, or is the checked version.
	 * @param version The version you are checking.
	 * @return -1 if older, 0 if the same, and 1 if newer.
	 */
	public Integer comparedTo(Version version) {
		int resault = -1;
		int current = this.getVersionInteger();
		int check = version.getVersionInteger();
		if(current > check || check == -2) {// check is newer then current
			resault = 1;
		}else if(current == check) {// check is the same as current
			resault = 0;
		}else if(current < check || check == -1) {// check is older then current
			resault = -1;
		}
		return resault;
	}

	/**
	 * Checks to see if the current version is newer then the checked version.
	 * @param version The version you are checking.
	 * @return True if newer then the checked version and false if the same or older.
	 */
	public Boolean isNewer(Version version) {
		if(this.versionInteger > version.versionInteger || this.versionInteger == -2) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * Checks to see if the current version is the same as the checked version.
	 * @param version The version you are checking.
	 * @return True if both the current and checked version is the same and false if otherwise.
	 */
	public Boolean isSame(Version version) {
		if(this.versionInteger == version.versionInteger) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * Checks to see if the current version is older then the checked version.
	 * @param version The version you are checking.
	 * @return True if older then the checked version and false if the same or newer.
	 */
	public Boolean isOlder(Version version) {
		if(this.versionInteger < version.versionInteger || this.versionInteger == -1) {
			return true;
		}else {
			return false;
		}
	}

}