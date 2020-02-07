package me.badbones69.crazyenchantments.api.objects;

import me.badbones69.crazyenchantments.Methods;

import java.util.Calendar;

public class Cooldown {
    
    private GKitz gkit;
    private Calendar cooldownTime;
    
    /**
     *
     */
    public Cooldown() {
        this.gkit = null;
        this.cooldownTime = null;
    }
    
    /**
     *
     * @param gkit The gkit this is tied to.
     * @param cooldownTime When the cooldown ends.
     */
    public Cooldown(GKitz gkit, Calendar cooldownTime) {
        this.gkit = gkit;
        this.cooldownTime = cooldownTime;
    }
    
    public GKitz getGKitz() {
        return this.gkit;
    }
    
    public Calendar getCooldown() {
        return this.cooldownTime;
    }
    
    public boolean isCooldownOver() {
        return Calendar.getInstance().after(this.cooldownTime);
    }
    
    public String getCooldownLeft(String message) {
        int day = 0;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (cooldownTime != null) {
            int total = ((int) (cooldownTime.getTimeInMillis() / 1000) - (int) (Calendar.getInstance().getTimeInMillis() / 1000));
            for (; total > 86400; total -= 86400, day++) ;
            for (; total > 3600; total -= 3600, hour++) ;
            for (; total > 60; total -= 60, minute++) ;
            second += total;
        }
        return Methods.color(message.replace("%Day%", day + "").replace("%day%", day + "")
        .replace("%Hour%", hour + "").replace("%hour%", hour + "")
        .replace("%Minute%", minute + "").replace("%minute%", minute + "")
        .replace("%Second%", second + "").replace("%second%", second + ""));
    }
    
}