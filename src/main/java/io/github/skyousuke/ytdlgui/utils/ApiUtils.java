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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiUtils {

    private static final int BUFFER_SIZE = 4096;
    private static final int URL_TIMEOUT = 5000; //milliseconds

    private ApiUtils() {
    }

    public static String getYoutubeVideoTitle(String youtubeID) {
        final String requestUrl = "http://www.youtube.com/oembed?url=http://www.youtube.com/watch?v="
                + youtubeID + "&format=json";

        try {
            String jsonString = readJsonFromUrl(requestUrl);
            JsonObject jsonObject = Json.parse(jsonString).asObject();
            return jsonObject.get("title").asString();
        } catch (IOException e) {
            Log.debug("getYoutubeVideoTitle() error!", e);
            return null;
        }
    }

    public static boolean isValidYoutubeId(String youtubeID) {
        final String requestUrl = "http://www.youtube.com/oembed?url=http://www.youtube.com/watch?v="
                + youtubeID + "&format=json";
        try {
            readJsonFromUrl(requestUrl);
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    private static String readJsonFromUrl(String urlString) throws IOException {
        String fixedUrlString = urlString.replace(" ", "%20");

        URL url = new URL(fixedUrlString);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(URL_TIMEOUT);
        httpURLConnection.setReadTimeout(URL_TIMEOUT);

        BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        int read;
        char[] chars = new char[BUFFER_SIZE];
        while ((read = reader.read(chars)) != -1)
            stringBuilder.append(chars, 0, read);
        reader.close();
        return stringBuilder.toString();
    }
}
