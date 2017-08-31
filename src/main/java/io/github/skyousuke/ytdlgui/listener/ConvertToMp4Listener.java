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

package io.github.skyousuke.ytdlgui.listener;

import com.esotericsoftware.minlog.Log;
import io.github.skyousuke.ytdlgui.controller.MainPageController;
import io.github.skyousuke.ytdlgui.utils.ConverterUtils;
import io.github.skyousuke.ytdlgui.utils.Duration;
import io.github.skyousuke.ytdlgui.utils.StatusMessage;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ConvertToMp4Listener extends AwaitingProcessListener {

    private Duration totalDuration;

    private MainPageController pageController;
    private Timer timer = new Timer();
    private int dotCount = 0;

    public ConvertToMp4Listener(MainPageController pageController, Duration totalDuration) {
        this.pageController = pageController;
        this.totalDuration = totalDuration;
    }

    @Override
    public void onStart() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String newStatus;
                if (dotCount < 3) {
                    newStatus = pageController.getStatusText() + '.';
                    dotCount++;
                } else {
                    dotCount = 0;
                    newStatus = pageController.getStatusText().substring(0, pageController.getStatusText().length() - 3);
                }
                pageController.updateStatusText(newStatus);
            }
        }, 250, 250);
    }

    @Override
    public void onOutput(String message) {
        Duration convertingDuration = ConverterUtils.getConvertingDuration(message);
        if (convertingDuration != null) {
            timer.cancel();
            float percentComplete = totalDuration.getPercent(convertingDuration);
            String text = String.format(Locale.US, "%s (%.1f%%)",
                    StatusMessage.CONVERTING_TO_MP4.toString(), percentComplete);
            pageController.updateStatusText(text);
        }
        Log.debug(message);
    }

}
