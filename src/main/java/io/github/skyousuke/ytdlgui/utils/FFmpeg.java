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
import io.github.skyousuke.ytdlgui.listener.ProcessListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FFmpeg {

    private static final String TAG = FFmpeg.class.getSimpleName();
    private static final String PROGRAM_PATH = "bin/ffmpeg.exe";

    private ArrayList<Process> processes = new ArrayList<>();

    public boolean convertAudioToMp3(String inputPath, String outputPath, ProcessListener processListener) {
        ProcessBuilder builder = new ProcessBuilder(PROGRAM_PATH, "-i", inputPath,
                "-codec:a", "libmp3lame", "-q:a", "0", outputPath);
        builder.redirectErrorStream(true);

        Log.debug(TAG + ": convertAudioToMp3() command: " + StringUtils.getListAsString(builder.command()));

        return run(builder, processListener);
    }

    public boolean convertVideoToMkv(String completeVideoInputPath, String outputPath, ProcessListener processListener) {
        ProcessBuilder builder = new ProcessBuilder(PROGRAM_PATH,
                "-i", completeVideoInputPath, "-vcodec", "copy", "-acodec", "copy", outputPath);
        builder.redirectErrorStream(true);

        Log.debug(TAG + ": convertVideoToMkv() command: " + StringUtils.getListAsString(builder.command()));

        return run(builder, processListener);
    }

    public boolean convertVideoToMp4(String completeVideoInputPath, String outputPath, ProcessListener processListener) {
        ProcessBuilder builder = new ProcessBuilder(PROGRAM_PATH,
                "-i", completeVideoInputPath, "-f", "mp4", "-vcodec", "libx264", "-preset",
                "fast", "-profile:v", "main", "-acodec", "aac", outputPath, "-hide_banner");
        builder.redirectErrorStream(true);

        Log.debug(TAG + ": convertVideoToMp4() command: " + StringUtils.getListAsString(builder.command()));

        return run(builder, processListener);
    }

    public boolean mergeVideoAndAudio(String videoInputPath, String audioInputPath, String outputPath,
                                      ProcessListener processListener) {
        ProcessBuilder builder = new ProcessBuilder(PROGRAM_PATH,
                "-i", videoInputPath, "-i", audioInputPath,
                "-vcodec", "copy", "-acodec", "copy", outputPath);
        builder.redirectErrorStream(true);

        Log.debug(TAG + ": mergeVideoAndAudio() command: " + StringUtils.getListAsString(builder.command()));

        return run(builder, processListener);
    }

    private boolean run(ProcessBuilder builder, final ProcessListener processListener) {
        Process tempProcess;
        try {
            tempProcess = builder.start();
        } catch (IOException e) {
            Log.debug(TAG + ": can't start process!", e);
            processListener.onExit(-1);
            return false;
        }

        processListener.onStart();
        final Process process = tempProcess;
        final InputStream processInputStream = process.getInputStream();
        processes.add(process);

        new Thread(() -> {
            try {
                String line;
                BufferedReader in = new BufferedReader(new InputStreamReader(processInputStream));
                while ((line = in.readLine()) != null) {
                    processListener.onOutput(line);
                }
                in.close();
            } catch (IOException e) {
                Log.debug(TAG + ": error closing processInputStream BufferedReader", e);
            }
        }).start();

        new Thread(() -> {
            try {
                int returnValue = process.waitFor();
                processListener.onExit(returnValue);
            } catch (InterruptedException e) {
                Log.debug(TAG + ": process is interrupted!", e);
                Thread.currentThread().interrupt();
                processListener.onExit(-1);
            }
        }).start();

        return true;
    }

    public void dispose() {
        Log.debug(TAG + ": dispose called!");
        for (Process process : processes) {
            process.destroy();
        }
    }
}
