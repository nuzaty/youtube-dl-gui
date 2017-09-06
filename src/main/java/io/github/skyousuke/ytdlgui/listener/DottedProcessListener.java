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

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.Timer;
import java.util.TimerTask;

public abstract class DottedProcessListener extends AwaitingProcessListener {

    private Timer timer;
    private int dotCount = 0;

    private Label progressLabel;

    public DottedProcessListener(Label progressLabel) {
        this.progressLabel = progressLabel;
    }

    protected void startDot() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String newStatus;
                if (dotCount < 3) {
                    newStatus = progressLabel.getText() + '.';
                    dotCount++;
                } else {
                    dotCount = 0;
                    newStatus = progressLabel.getText().substring(0, progressLabel.getText().length() - 3);
                }
                Platform.runLater(() -> progressLabel.setText(newStatus));
            }
        }, 250, 250);
    }

    protected void cancelDot() {
        if (timer != null)  {
            timer.cancel();
            timer = null;
        }
    }

}
