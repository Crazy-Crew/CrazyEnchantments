package me.badbones69.crazyenchantments.api;

import org.bukkit.Bukkit;

public enum Version {
	
	TOO_OLD(-1),
	v1_7_R4(174),
	v1_8_R1(181), v1_8_R2(182), v1_8_R3(183),
	v1_9_R1(191), v1_9_R2(192),
	v1_10_R1(1101),
	v1_11_R1(1111),
	TOO_NEW(-2);
	
	private Integer versionInteger;
	
	private Version(Integer versionInteger){
		this.versionInteger = versionInteger;
	}
	
	/**
	 * 
	 * @return Get the server's Minecraft version.
	 */
	public static Version getVersion(){
		String ver = Bukkit.getServer().getClass().getPackage().getName();
		ver = ver.substring(ver.lastIndexOf('.')+1);
		ver = ver.replaceAll("_", "").replaceAll("R", "").replaceAll("v", "");
		int version = Integer.parseInt(ver);
		if(version == 1111) return Version.v1_11_R1;
		if(version == 1101) return Version.v1_10_R1;
		if(version == 192) return Version.v1_9_R2;
		if(version == 191) return Version.v1_9_R1;
		if(version == 183) return Version.v1_8_R3;
		if(version == 182) return Version.v1_8_R2;
		if(version == 181) return Version.v1_8_R1;
		if(version == 174) return Version.v1_7_R4;
		if(version > 1111) return Version.TOO_NEW;
		return Version.TOO_OLD;
	}
	
	/**
	 * 
	 * @return The server's minecraft version as an integer.
	 */
	public Integer getVersionInteger(){
		return this.versionInteger;
	}
	
	/**
	 * This checks if the current version is older, newer, or is the checked version.
	 * @param version The version you are checking.
	 * @return -1 if older, 0 if the same, and 1 if newer.
	 */
	public Integer comparedTo(Version version){
		int resault = -1;
		int current = this.getVersionInteger();
		int check = version.getVersionInteger();
		if(current > check){
			resault = 1;
		}else if(current == check){
			resault = 0;
		}else if(current < check){
			resault = -1;
		}
		return resault;
	}
	
}
