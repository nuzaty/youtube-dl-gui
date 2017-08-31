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

public class FFprobe {

    private static final String TAG = FFprobe.class.getSimpleName();
    private static final String PROGRAM_PATH = "bin/ffprobe.exe";

    private ArrayList<Process> processes = new ArrayList<>();

    public boolean findDuration(String inputPath, ProcessListener processListener) {
        ProcessBuilder builder = new ProcessBuilder(PROGRAM_PATH, "-v", "error", "-show_entries",
                "format=duration", "-of", "default=noprint_wrappers=1:nokey=1", inputPath);
        builder.redirectErrorStream(true);

        Log.debug(TAG + ": findDuration() command: " + StringUtils.getListAsString(builder.command()));

        return run(builder, processListener);
    }

    public boolean findVideoCodec(String inputPath, ProcessListener processListener) {
        ProcessBuilder builder = new ProcessBuilder(PROGRAM_PATH, "-v", "error", "-select_streams",
                "v:0", "-show_entries", "stream=codec_name", "-of", "default=noprint_wrappers=1:nokey=1", inputPath);
        builder.redirectErrorStream(true);

        Log.debug(TAG + ": findDuration() command: " + StringUtils.getListAsString(builder.command()));

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
