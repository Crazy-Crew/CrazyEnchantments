package com.badbones69.crazyenchantments.paper.api.objects.gkitz;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GkitCoolDown {
    
    private final GKitz gkit;
    private final Calendar coolDownTime;

    public GkitCoolDown() {
        this.gkit = null;
        this.coolDownTime = null;
    }
    
    /**
     * @param gkit The gkit this is tied to.
     * @param coolDownTime When the cool-down ends.
     */
    public GkitCoolDown(GKitz gkit, Calendar coolDownTime) {
        this.gkit = gkit;
        this.coolDownTime = coolDownTime;
    }
    
    public GKitz getGKitz() {
        return this.gkit;
    }
    
    public Calendar getCoolDown() {
        return this.coolDownTime;
    }
    
    public boolean isCoolDownOver() {
        return Calendar.getInstance().after(this.coolDownTime);
    }
    
    public Map<String, String> getCoolDownLeft() {
        long days = 0, hours = 0, minutes = 0, seconds = 0, total;

        int second = 1000, minute = second*60, hour = minute*60, day = hour*24;

        if (this.coolDownTime != null) {
            total = this.coolDownTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

            days = total / day;
            total %= day;
            hours = total / hour;
            total %= hour;
            minutes = total / minute;
            total %= minute;
            seconds = total / second;
        }

        final Map<String, String> placeholders = new HashMap<>();

        final String daily = String.valueOf(days);
        final String hourly = String.valueOf(hours);
        final String minutely = String.valueOf(minutes);
        final String secondly = String.valueOf(seconds);

        placeholders.put("%Day%", daily);
        placeholders.put("%day%", daily);

        placeholders.put("%Hour%", hourly);
        placeholders.put("%hour%", hourly);

        placeholders.put("%Minute%", minutely);
        placeholders.put("%minute%", minutely);

        placeholders.put("%Second%", secondly);
        placeholders.put("%second%", secondly);

        return placeholders;
    }
}