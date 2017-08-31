/*
 *    Copyright 2017 Surasek Nusati <surasek@gmail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.skyousuke.ytdlgui.utils;

public class Duration {

    private int hours;
    private int minutes;
    private float seconds;

    public Duration(int hours, int minutes, float seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    /**
     * Calculate the percentage of input duration compare to this duration.
     *
     */
    public float getPercent(Duration duration) {
        return getPercent(duration.hours, duration.minutes, duration.seconds);
    }

    public float getPercent(int hours, int minutes, float seconds) {
        float thisTotalSecond = (this.hours * 60 * 60) + (this.minutes * 60) + this.seconds;
        float totalSecond = (hours * 60 * 60) + (minutes * 60) + seconds;
        if (thisTotalSecond == 0)
            return 0;
        return 100 * totalSecond / thisTotalSecond;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Duration duration = (Duration) o;

        if (hours != duration.hours) return false;
        if (minutes != duration.minutes) return false;
        return Float.compare(duration.seconds, seconds) == 0;
    }

    @Override
    public int hashCode() {
        int result = hours;
        result = 31 * result + minutes;
        result = 31 * result + (seconds != +0.0f ? Float.floatToIntBits(seconds) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Duration{" +
                "hours=" + hours +
                ", minutes=" + minutes +
                ", seconds=" + seconds +
                '}';
    }
}
