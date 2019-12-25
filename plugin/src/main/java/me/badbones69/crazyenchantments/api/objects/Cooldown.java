package me.badbones69.crazyenchantments.api.objects;

import me.badbones69.crazyenchantments.Methods;

import java.util.Calendar;

public class Cooldown {
    
    private GKitz gkit;
    private Calendar cooldown;
    
    /**
     *
     */
    public Cooldown() {
        this.gkit = null;
        this.cooldown = null;
    }
    
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
    
    public boolean isCooldownOver() {
        return Calendar.getInstance().after(this.cooldown);
    }
    
    public String getCooldownLeft(String message) {
        int day = 0;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (cooldown != null) {
            Calendar C = Calendar.getInstance();
            int total = ((int) (cooldown.getTimeInMillis() / 1000) - (int) (C.getTimeInMillis() / 1000));
            for (; total > 86400; total -= 86400, day++) ;
            for (; total > 3600; total -= 3600, hour++) ;
            for (; total > 60; total -= 60, minute++) ;
            second += total;
        }
        return Methods.color(message.replaceAll("%Day%", day + "").replaceAll("%day%", day + "")
        .replaceAll("%Hour%", hour + "").replaceAll("%hour%", hour + "")
        .replaceAll("%Minute%", minute + "").replaceAll("%minute%", minute + "")
        .replaceAll("%Second%", second + "").replaceAll("%second%", second + ""));
    }
    
}