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

public enum VideoResolution {

    YOUTUBE_144P_QCIF(176, 144, "144p (QCIF)"),
    YOUTUBE_144P(256, 144, "144p"),
    YOUTUBE_180P(320, 180, "180p"),
    YOUTUBE_240P(426, 240, "240p"),
    YOUTUBE_360P(640, 360, "360p"),
    YOUTUBE_480P(854, 480, "480p"),
    YOUTUBE_720P(1280, 720, "720p"),
    YOUTUBE_1080P(1920, 1080, "1080p"),
    YOUTUBE_1440P(2560, 1440, "1440p"),
    YOUTUBE_4K(3840, 2160, "4k");

    private static final VideoResolution[] values = values();

    final int width;
    final int height;

    final String name;

    VideoResolution(int width, int height, String name) {
        this.width = width;
        this.height = height;
        this.name = name;
    }

    public static VideoResolution fromDimension(int width, int height) {
        for (VideoResolution resolution : values) {
            if (width == resolution.width && height == resolution.height)
                return resolution;
        }
        throw new IllegalArgumentException("Wrong dimension: " + width + 'x' + height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public VideoResolution getWorse() {
        final int previousOrdinal = ordinal() - 1;
        if (previousOrdinal < 0) {
            return this;
        }
        return values[previousOrdinal];
    }

    public VideoResolution getBetter() {
        final int nextOrdinal = ordinal() + 1;
        if (nextOrdinal >= values.length) {
            return this;
        }
        return values[nextOrdinal];
    }

    public boolean isBetter(VideoResolution other) {
        return ordinal() > other.ordinal();
    }


    @Override
    public String toString() {
        return name;
    }
}
