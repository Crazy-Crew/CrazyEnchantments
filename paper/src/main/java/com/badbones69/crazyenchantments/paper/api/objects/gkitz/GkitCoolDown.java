package com.badbones69.crazyenchantments.paper.api.objects.gkitz;

import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import java.util.Calendar;

public class GkitCoolDown {

    private final GKitz gkit;
    private final Calendar coolDownTime;

    public GkitCoolDown() {
        this.gkit = null;
        this.coolDownTime = null;
    }

    /**
     * @param gkit         The gkit this is tied to.
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
        long days = 0, hours = 0, minutes = 0, seconds = 0, total;

        int second = 1000, minute = second * 60, hour = minute * 60, day = hour * 24;

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

        return ColorUtils.color(message.replace("%Day%", String.valueOf(days)).replace("%day%", String.valueOf(days))
                .replace("%Hour%", String.valueOf(hours)).replace("%hour%", String.valueOf(hours))
                .replace("%Minute%", String.valueOf(minutes)).replace("%minute%", String.valueOf(minutes))
                .replace("%Second%", String.valueOf(seconds)).replace("%second%", String.valueOf(seconds)));
    }
}