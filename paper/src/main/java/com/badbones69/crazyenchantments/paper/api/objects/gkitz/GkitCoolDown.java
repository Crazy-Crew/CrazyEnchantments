package com.badbones69.crazyenchantments.paper.api.objects.gkitz;

import com.ryderbelserion.cluster.bukkit.utils.LegacyUtils;
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

        if (this.coolDownTime != null) {
            int total = ((int) (this.coolDownTime.getTimeInMillis() / 1000) - (int) (Calendar.getInstance().getTimeInMillis() / 1000));
            second += total;
        }

        return LegacyUtils.color(message.replace("%Day%", String.valueOf(day)).replace("%day%", String.valueOf(day))
        .replace("%Hour%", String.valueOf(hour)).replace("%hour%", String.valueOf(hour))
        .replace("%Minute%", String.valueOf(minute)).replace("%minute%", String.valueOf(minute))
        .replace("%Second%", String.valueOf(second)).replace("%second%", String.valueOf(second)));
    }
}