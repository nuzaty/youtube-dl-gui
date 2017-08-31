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

public enum StatusMessage {

    READY("Ready"),

    GETTING_VIDEO_INFO("Getting video info"),

    DOWNLOADING_AUDIO("Downloading audio"),
    DOWNLOADING_VIDEO("Downloading video"),
    DOWNLOADING_COMPLETE_VIDEO("Downloading complete video"),

    CONVERTING_TO_MP3("Converting audio to mp3"),
    CONVERTING_TO_MP4("Converting video to mp4"),
    CONVERTING_TO_MKV("Converting video to mkv"),
    MERGING_TO_MKV("Merging video and audio to mkv"),
    FINISHED("Done!"),

    WRONG_URL("Please check the URL and try it again"),
    NO_FORMAT("Selected format not found! Please try it again later"),
    NAME_NOT_FOUND("Can't find video name! Please try it again later"),
    DOWNLOAD_ERROR("Download Error! Please try it again later");

    private final String message;

    StatusMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
