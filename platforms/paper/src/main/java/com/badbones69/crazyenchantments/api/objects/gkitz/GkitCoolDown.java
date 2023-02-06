package com.badbones69.crazyenchantments.api.objects.gkitz;

import com.badbones69.crazyenchantments.utilities.misc.ColorUtils;
import java.util.Calendar;

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
    
    public String getCoolDownLeft(String message) {
        int day = 0;
        int hour = 0;
        int minute = 0;
        int second = 0;

        if (coolDownTime != null) {
            int total = ((int) (coolDownTime.getTimeInMillis() / 1000) - (int) (Calendar.getInstance().getTimeInMillis() / 1000));
            for (; total > 86400; total -= 86400, day++) ;
            for (; total > 3600; total -= 3600, hour++) ;
            for (; total > 60; total -= 60, minute++) ;
            second += total;
        }

        return ColorUtils.color(message.replace("%Day%", day + "").replace("%day%", day + "")
        .replace("%Hour%", hour + "").replace("%hour%", hour + "")
        .replace("%Minute%", minute + "").replace("%minute%", minute + "")
        .replace("%Second%", second + "").replace("%second%", second + ""));
    }
}