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

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.esotericsoftware.minlog.Log;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeUtils {

    private YoutubeUtils() {}

    public static String getYoutubeVideoTitle(String youtubeID) {
        final String requestUrl = "http://www.youtube.com/oembed?url=http://www.youtube.com/watch?v="
                + youtubeID + "&format=json";

        try {
            String jsonString = ApiUtils.get(requestUrl);
            JsonObject jsonObject = Json.parse(jsonString).asObject();
            return jsonObject.get("title").asString();
        } catch (IOException e) {
            Log.debug("getYoutubeVideoTitle() error!", e);
            return null;
        }
    }

    public static String getYoutubeID(String url) {
        String regex = "^.*(youtu.be\\/|v\\/|e\\/|u\\/\\w+\\/|embed\\/|v=)([^#\\&\\?]*).*";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find())
            return matcher.group(2);

        if (isValidYoutubeId(url))
            return url;

        return "";
    }

    private static boolean isValidYoutubeId(String youtubeID) {
        final String requestUrl = "http://www.youtube.com/oembed?url=http://www.youtube.com/watch?v="
                + youtubeID + "&format=json";
        try {
            ApiUtils.get(requestUrl);
            return true;
        } catch (IOException e) {
            Log.debug("isValidYoutubeId() error!", e);
            return false;
        }
    }

}
