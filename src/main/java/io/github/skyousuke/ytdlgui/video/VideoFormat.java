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

package io.github.skyousuke.ytdlgui.video;

public class VideoFormat {

    private String code;
    private VideoOnlyFormat video;
    private AudioOnlyFormat audio;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public VideoOnlyFormat getVideo() {
        return video;
    }

    public void setVideo(VideoOnlyFormat video) {
        this.video = video;
    }

    public AudioOnlyFormat getAudio() {
        return audio;
    }

    public void setAudio(AudioOnlyFormat audio) {
        this.audio = audio;
    }

    public boolean isVideoOnly() {
        return video != null && audio == null;
    }

    public boolean isAudioOnly() {
        return video == null && audio != null;
    }

    @Override
    public String toString() {
        return "VideoFormat{" +
                "code=" + code +
                ", video=" + video +
                ", audio=" + audio +
                '}';
    }
}
