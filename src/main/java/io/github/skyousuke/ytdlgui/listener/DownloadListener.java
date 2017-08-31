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
import io.github.skyousuke.ytdlgui.utils.DownloaderUtils;

import java.util.Timer;
import java.util.TimerTask;

public class DownloadListener extends AwaitingProcessListener {

    private MainPageController pageController;
    private Timer timer = new Timer();
    private int dotCount = 0;

    private String frontProgressText;

    public DownloadListener(MainPageController pageController, String frontProgressText) {
        this.pageController = pageController;
        this.frontProgressText = frontProgressText;
    }

    public DownloadListener(MainPageController pageController) {
        this(pageController, "");
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
        String downloadProgressText = DownloaderUtils.getDownloadProgress(message);
        if (downloadProgressText != null) {
            timer.cancel();
            pageController.updateStatusText(frontProgressText + ' ' + downloadProgressText);
        }
        Log.debug(message);
    }

}
