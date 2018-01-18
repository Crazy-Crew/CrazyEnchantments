package me.badbones69.crazyenchantments.api;

import me.badbones69.crazyenchantments.Methods;

import java.util.Calendar;

public class Cooldown {

	private GKitz gkit;
	private Calendar cooldown;

	/**
	 *
	 * @param gkit The gkit this is tied to.
	 * @param cooldown When the cooldown ends.
	 */
	public Cooldown(GKitz gkit, Calendar cooldown) {
		this.gkit = gkit;
		this.cooldown = cooldown;
	}

	public GKitz getGKitz() {
		return this.gkit;
	}

	public Calendar getCooldown() {
		return this.cooldown;
	}

	public Boolean isCooldownOver() {
		return Calendar.getInstance().after(this.cooldown);
	}

	public String getCooldownLeft(String msg) {
		Calendar C = Calendar.getInstance();
		int total = ((int) (cooldown.getTimeInMillis() / 1000) - (int) (C.getTimeInMillis() / 1000));
		int D = 0;
		int H = 0;
		int M = 0;
		int S = 0;
		for(; total > 86400; total -= 86400, D++) ;
		for(; total > 3600; total -= 3600, H++) ;
		for(; total > 60; total -= 60, M++) ;
		S += total;
		return Methods.color(msg.replaceAll("%Day%", D + "").replaceAll("%day%", D + "").replaceAll("%Hour%", H + "").replaceAll("%hour%", H + "").replaceAll("%Minute%", M + "").replaceAll("%minute%", M + "").replaceAll("%Second%", S + "").replaceAll("%second%", S + ""));
	}

}