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

public class VideoOnlyFormat {

    private VideoResolution resolution;
    private int bitrate;
    private int fps;

    public VideoOnlyFormat(VideoResolution resolution, int bitrate, int fps) {
        this.resolution = resolution;
        this.bitrate = bitrate;
        this.fps = fps;
    }

    public VideoResolution getResolution() {
        return resolution;
    }

    public void setResolution(VideoResolution resolution) {
        this.resolution = resolution;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public boolean isBetter(VideoOnlyFormat otherVideo) {
        if (resolution.isBetter(otherVideo.resolution)) {
            return true;
        }
        if (resolution == otherVideo.resolution) {
            if (fps > otherVideo.fps) {
                return true;
            }
            if (fps == otherVideo.fps && bitrate > otherVideo.bitrate) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "VideoOnlyFormat{" +
                "resolution=" + resolution +
                ", bitrate=" + bitrate +
                ", fps=" + fps +
                '}';
    }
}
