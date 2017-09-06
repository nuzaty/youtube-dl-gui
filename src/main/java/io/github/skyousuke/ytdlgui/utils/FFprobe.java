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

public class FFprobe {

    private static final String PROGRAM_PATH = "bin/ffprobe.exe";

    public boolean findDuration(String inputPath, ProcessListener processListener) {
        ProcessBuilder builder = new ProcessBuilder(PROGRAM_PATH, "-v", "error", "-show_entries",
                "format=duration", "-of", "default=noprint_wrappers=1:nokey=1", inputPath);

        return ProcessUtils.run(builder, processListener);
    }

    public boolean findVideoCodec(String inputPath, ProcessListener processListener) {
        ProcessBuilder builder = new ProcessBuilder(PROGRAM_PATH, "-v", "error", "-select_streams",
                "v:0", "-show_entries", "stream=codec_name", "-of", "default=noprint_wrappers=1:nokey=1", inputPath);

        return ProcessUtils.run(builder, processListener);
    }
}
