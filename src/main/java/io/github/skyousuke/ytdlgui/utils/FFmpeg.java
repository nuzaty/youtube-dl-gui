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

import io.github.skyousuke.ytdlgui.listener.ProcessListener;

public class FFmpeg {

    private static final String PROGRAM_PATH = "bin/ffmpeg.exe";

    public boolean convertAudioToMp3(String inputPath, String outputPath, ProcessListener processListener) {
        ProcessBuilder builder = new ProcessBuilder(PROGRAM_PATH, "-i", inputPath,
                "-codec:a", "libmp3lame", "-q:a", "0", outputPath);

        return ProcessUtils.run(builder, processListener);
    }

    public boolean convertVideoToMkv(String completeVideoInputPath, String outputPath, ProcessListener processListener) {
        ProcessBuilder builder = new ProcessBuilder(PROGRAM_PATH,
                "-i", completeVideoInputPath, "-vcodec", "copy", "-acodec", "copy", outputPath);

        return ProcessUtils.run(builder, processListener);
    }

    public boolean convertVideoToMp4(String completeVideoInputPath, String outputPath, ProcessListener processListener) {
        ProcessBuilder builder = new ProcessBuilder(PROGRAM_PATH,
                "-i", completeVideoInputPath, "-f", "mp4", "-vcodec", "libx264", "-preset",
                "fast", "-profile:v", "main", "-acodec", "aac", outputPath, "-hide_banner");

        return ProcessUtils.run(builder, processListener);
    }

    public boolean mergeVideoAndAudio(String videoInputPath, String audioInputPath, String outputPath,
                                      ProcessListener processListener) {
        ProcessBuilder builder = new ProcessBuilder(PROGRAM_PATH,
                "-i", videoInputPath, "-i", audioInputPath,
                "-vcodec", "copy", "-acodec", "copy", outputPath);

        return ProcessUtils.run(builder, processListener);
    }
}
