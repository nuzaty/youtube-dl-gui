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
import java.util.Arrays;
import java.util.List;

public class YoutubeDL {

    private static final String TAG = YoutubeDL.class.getSimpleName();
    private static final String PROGRAM_PATH = "bin/youtube-dl.exe";

    private ArrayList<Process> processes = new ArrayList<>();

    /**
     * download the youtube video in the selected format.
     *
     * @param outputPath the output file path
     * @param formatCode the audio only format code of the video
     * @param url        the link or youtube ID of the video
     * @param listener   the process listener
     */
    public void download(String url, String formatCode, String outputPath, ProcessListener listener) {
        String option = "-f " + formatCode + " -o \"" + outputPath + "\"";
        run(option, url, listener);
    }

    public void listVideoFormats(String url, ProcessListener listener) {
        run("-F", url, listener);
    }

    private void run(String option, String url, final ProcessListener processListener) {
        List<String> commandList = new ArrayList<>();
        commandList.add(PROGRAM_PATH);
        commandList.addAll(Arrays.asList(option.trim().split("\\s+")));
        commandList.add(url);

        ProcessBuilder builder = new ProcessBuilder(commandList);
        builder.redirectErrorStream(true);

        Process tempProcess;
        try {
            tempProcess = builder.start();
        } catch (IOException e) {
            Log.debug(TAG + ": can't start process!", e);
            processListener.onExit(-1);
            return;
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
    }

    public void dispose() {
        Log.debug(TAG + ": dispose called!");
        for (Process process : processes) {
            process.destroy();
        }
    }
}
