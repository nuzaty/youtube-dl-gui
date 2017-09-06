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

import com.esotericsoftware.minlog.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConverterUtils {

    private ConverterUtils() {}

    public static Duration getConvertingDuration(String message) {
        String regex = "size=\\s.+time=(\\d+):(\\d+):(\\d+(\\.\\d+)?).+\\n*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String hrs = matcher.group(1);
            String mins = matcher.group(2);
            String secs = matcher.group(3);
            return getDurationFromString(hrs, mins, secs);
        }
        return null;
    }

    private static Duration getDurationFromString(String hrs, String mins, String secs) {
        if (hrs != null && mins != null && secs != null) {
            try {
                int hours = Integer.parseInt(hrs);
                int minutes = Integer.parseInt(mins);
                float seconds = Float.parseFloat(secs);

                return new Duration(hours, minutes, seconds);

            } catch (NumberFormatException ignored){
                Log.debug("getDurationFromString() : the string does not have the appropriate format!: "
                        + hrs + " " + mins + " " + secs);
                return null;
            }
        }
        return null;
    }
}
