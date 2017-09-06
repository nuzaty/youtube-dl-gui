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

public class YoutubeDL {

    private static final String PROGRAM_PATH = "bin/youtube-dl.exe";

    public boolean download(String url, String formatCode, String outputPath, ProcessListener listener) {
        ProcessBuilder builder = new ProcessBuilder(PROGRAM_PATH, "-f", formatCode, "-o", outputPath, url);
        return ProcessUtils.run(builder, listener);
    }

    public boolean listVideoFormats(String url, ProcessListener listener) {
        ProcessBuilder builder = new ProcessBuilder(PROGRAM_PATH, "-F", url);
        return ProcessUtils.run(builder, listener);
    }

}
