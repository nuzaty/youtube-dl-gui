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
import io.github.skyousuke.ytdlgui.video.AudioOnlyFormat;
import io.github.skyousuke.ytdlgui.video.VideoFormat;
import io.github.skyousuke.ytdlgui.video.VideoOnlyFormat;
import io.github.skyousuke.ytdlgui.video.VideoResolution;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoFormatUtils {

    private VideoFormatUtils() {
    }

    public static List<String> findBestFormatCodes(List<VideoFormat> videoFormats, VideoResolution preferResolution,
                                                   boolean prefer60Fps, boolean mp4Selected) {
        String bestAudioOnlyFormatCode = null;
        VideoFormat bestVideoOnly = null;
        VideoFormat bestVideo = null;

        VideoResolution currentPreferResolution = preferResolution;
        VideoResolution previousPreferResolution = null;

        while ((bestAudioOnlyFormatCode == null || bestVideoOnly == null) && bestVideo == null) {

            if (currentPreferResolution == previousPreferResolution) {
                return new ArrayList<>();
            }

            bestAudioOnlyFormatCode = VideoFormatUtils.findBestAudioOnlyFormatCode(videoFormats);
            bestVideoOnly = VideoFormatUtils.findBestVideoOnly(videoFormats, currentPreferResolution, prefer60Fps);
            bestVideo = VideoFormatUtils.findBestVideo(videoFormats, currentPreferResolution, prefer60Fps);

            previousPreferResolution = currentPreferResolution;
            currentPreferResolution = currentPreferResolution.getBetter();
        }

        Log.debug("best audio only format code: " + bestAudioOnlyFormatCode);
        Log.debug("best video only: " + bestVideoOnly);
        Log.debug("best video: " + bestVideo);

        List<String> formatCodes = new ArrayList<>();

        if (bestVideoOnly == null || bestAudioOnlyFormatCode == null) {
            formatCodes.add(bestVideo.getCode());
        } else if (bestVideo == null || isVideoOnlyBetter(mp4Selected, bestVideoOnly, bestVideo)) {
            formatCodes.add(bestVideoOnly.getCode());
            formatCodes.add(bestAudioOnlyFormatCode);
        } else {
            formatCodes.add(bestVideo.getCode());
        }

        Log.debug("download code: " + StringUtils.getListAsString(formatCodes));
        return formatCodes;
    }

    private static boolean isVideoOnlyBetter(boolean mp4Selected, VideoFormat videoOnly, VideoFormat video) {
        if (mp4Selected)
            return videoOnly.getVideo().getResolution().isBetter(video.getVideo().getResolution());
        else
            return videoOnly.getVideo().isBetter(video.getVideo());
    }

    public static String findBestAudioOnlyFormatCode(List<VideoFormat> videoFormats) {
        VideoFormat bestAudioOnly = null;
        for (VideoFormat videoFormat : videoFormats) {
            if (!videoFormat.isAudioOnly())
                continue;
            if (bestAudioOnly == null)
                bestAudioOnly = videoFormat;
            else if (videoFormat.getAudio().isBetter(bestAudioOnly.getAudio())) {
                bestAudioOnly = videoFormat;
            }
        }
        Log.debug("best audio only: " + bestAudioOnly);
        return bestAudioOnly != null ? bestAudioOnly.getCode() : null;
    }

    public static boolean isFormatInfo(String message) {
        return message.length() > 0 && Character.isDigit(message.charAt(0));
    }

    public static VideoFormat getVideoFormat(String formatInfo) {
        VideoFormat videoFormat = new VideoFormat();

        try {
            videoFormat.setCode(getFormatCode(formatInfo));

            if (isAudioOnly(formatInfo)) {
                videoFormat.setAudio(new AudioOnlyFormat(getAudioOnlyBitrate(formatInfo)));
            } else {
                int[] intResolution = getVideoResolution(formatInfo);
                VideoResolution resolution = VideoResolution.fromDimension(intResolution[0], intResolution[1]);

                if (isVideoOnly(formatInfo)) {
                    videoFormat.setVideo(new VideoOnlyFormat(resolution,
                            getVideoOnlyBitrate(formatInfo), getVideoOnlyFps(formatInfo)));
                } else if (isVideo(formatInfo)) {
                    videoFormat.setAudio(new AudioOnlyFormat(getAudioBitrate(formatInfo)));
                    videoFormat.setVideo(new VideoOnlyFormat(resolution, 0, 0));
                }
            }
        } catch (Exception e) {
            Log.debug("getVideoFormat() error!\nformat info:" + formatInfo, e);
            return null;
        }
        return videoFormat;
    }

    private static VideoFormat findBestVideoOnly(List<VideoFormat> videoFormats, VideoResolution maxResolution,
                                                 boolean prefer60Fps) {
        VideoFormat bestVideoOnly = null;
        for (VideoFormat videoFormat : videoFormats) {
            if (videoFormat.isVideoOnly() && isValidVideo(videoFormat.getVideo(), maxResolution, prefer60Fps)
                    && (bestVideoOnly == null || videoFormat.getVideo().isBetter(bestVideoOnly.getVideo()))) {
                bestVideoOnly = videoFormat;
            }
        }
        return bestVideoOnly;
    }

    private static VideoFormat findBestVideo(List<VideoFormat> videoFormats, VideoResolution maxResolution,
                                             boolean prefer60Fps) {
        VideoFormat bestVideo = null;
        for (VideoFormat videoFormat : videoFormats) {
            if (!videoFormat.isVideoOnly() && !videoFormat.isAudioOnly()
                    && isValidVideo(videoFormat.getVideo(), maxResolution, prefer60Fps)) {
                if (bestVideo == null) {
                    bestVideo = videoFormat;
                    continue;
                }
                final VideoResolution resolution = videoFormat.getVideo().getResolution();
                final VideoResolution bestResolution = bestVideo.getVideo().getResolution();
                if (resolution.isBetter(bestResolution) ||
                        (resolution == bestResolution) && videoFormat.getAudio().isBetter(bestVideo.getAudio())) {
                    bestVideo = videoFormat;
                }
            }
        }
        return bestVideo;
    }

    private static boolean isValidVideo(VideoOnlyFormat video, VideoResolution maxResolution, boolean prefer60Fps) {
        boolean correctFps = true;
        if (!prefer60Fps) {
            correctFps = (video.getFps() != 60);
        }
        return correctFps && !video.getResolution().isBetter(maxResolution);
    }

    private static boolean isVideo(String formatInfo) {
        String regex = "small|medium|hd720";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(formatInfo);
        return matcher.find();
    }

    private static boolean isVideoOnly(String formatInfo) {
        String regex = "video only";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(formatInfo);
        return matcher.find();
    }

    private static boolean isAudioOnly(String formatInfo) {
        String regex = "audio only";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(formatInfo);
        return matcher.find();
    }

    private static int getAudioOnlyBitrate(String formatInfo) {
        String regex = "DASH audio\\s+((\\d+)k)\\s+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(formatInfo);
        if (matcher.find())
            return Integer.parseInt(matcher.group(2)) * 1000;

        throw new IllegalArgumentException("audio only bitrate info not found!");
    }

    private static int getVideoOnlyBitrate(String formatInfo) {
        String regex = "(DASH video|\\d+p(\\d+)?)\\s+((\\d+)k)\\s+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(formatInfo);
        if (matcher.find())
            return Integer.parseInt(matcher.group(4)) * 1000;

        throw new IllegalArgumentException("video only bitrate info not found!");
    }

    private static int[] getVideoResolution(String formatInfo) {
        String regex = "(\\d+)x(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(formatInfo);
        if (matcher.find())
            return new int[]{Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2))};

        throw new IllegalArgumentException("video resolution info not found!");
    }

    private static int getVideoOnlyFps(String formatInfo) {
        String regex = "(\\d+)fps";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(formatInfo);
        if (matcher.find())
            return Integer.parseInt(matcher.group(1));

        throw new IllegalArgumentException("video only fps info not found!");
    }

    private static int getAudioBitrate(String formatInfo) {
        String regex = "@\\s?(\\d+)k";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(formatInfo);
        if (matcher.find())
            return Integer.parseInt(matcher.group(1)) * 1000;
        return 0;
    }

    private static String getFormatCode(String formatInfo) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < formatInfo.length(); i++) {
            char eachChar = formatInfo.charAt(i);
            if (Character.isDigit(eachChar))
                sb.append(eachChar);
            else
                break;
        }
        return sb.toString();
    }
}
