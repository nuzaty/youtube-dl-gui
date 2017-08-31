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

package io.github.skyousuke.ytdlgui.controller;

import com.esotericsoftware.minlog.Log;
import io.github.skyousuke.ytdlgui.listener.*;
import io.github.skyousuke.ytdlgui.utils.*;
import io.github.skyousuke.ytdlgui.video.VideoFormat;
import io.github.skyousuke.ytdlgui.video.VideoResolution;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

public class MainPageController {

    @FXML
    private TextField linkTextField;
    @FXML
    private Button downloadButton;
    @FXML
    private Label statusLabel;
    @FXML
    private GridPane mainPane;
    @FXML
    private ChoiceBox<VideoResolution> preferResolutionChoiceBox;
    @FXML
    private CheckBox prefer60FpsCheckbox;
    @FXML
    private CheckBox audioOnlyCheckbox;
    @FXML
    private Hyperlink outputFileLink;

    private final File desktopDirectory = new File(System.getProperty("user.home"), "Desktop");

    private String downloadDirectory;
    private String videoTitle;
    private String finalFilePath;

    private YoutubeDL youtubeDL = new YoutubeDL();
    private FFmpeg ffmpeg = new FFmpeg();
    private FFprobe ffprobe = new FFprobe();

    public void initialize() {

        mainPane.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        newWindow.setOnCloseRequest(event -> cleanUp());
                    }
                });
            }
        });

        preferResolutionChoiceBox.setItems(FXCollections.observableArrayList(VideoResolution.values()));
        preferResolutionChoiceBox.setValue(VideoResolution.YOUTUBE_4K);

        final BooleanProperty receivedFocus = new SimpleBooleanProperty(true);

        linkTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && receivedFocus.get()) {
                mainPane.requestFocus();
                receivedFocus.set(false);
            }
        });

        linkTextField.textProperty().addListener((observable, oldValue, newValue) ->
                downloadButton.disableProperty().set(newValue.contentEquals("")));

        audioOnlyCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            prefer60FpsCheckbox.disableProperty().set(newValue);
            preferResolutionChoiceBox.disableProperty().set(newValue);
        });

        downloadButton.setOnAction(event -> onDownloadButtonClick());

        outputFileLink.managedProperty().bind(outputFileLink.visibleProperty());

        outputFileLink.visibleProperty().set(false);
    }

    private void cleanUp() {
        youtubeDL.dispose();
        ffmpeg.dispose();
        ffprobe.dispose();
    }

    private void onDownloadButtonClick() {
        new Thread(() -> {
            onStartDownload();

            List<VideoFormat> videoFormats = getVideoFormats();

            if (videoFormats.isEmpty()) {
                updateStatusText(StatusMessage.WRONG_URL);
                onFinishDownload();
                return;
            }

            if (audioOnlyCheckbox.isSelected()) {
                downloadAudioOnlyTask(videoFormats);
            } else {
                downloadVideoTask(videoFormats);
            }
        }).start();
    }

    private void downloadAudioOnlyTask(final List<VideoFormat> videoFormats) {
        new Thread(() -> {
            final String bestAudioOnlyFormatCode = VideoFormatUtils.findBestAudioOnlyFormatCode(videoFormats);
            if (bestAudioOnlyFormatCode == null) {
                updateStatusText(StatusMessage.NO_FORMAT);
                onFinishDownload();
                return;
            }

            if (!findVideoTitleName()) {
                updateStatusText(StatusMessage.NAME_NOT_FOUND);
                onFinishDownload();
                return;
            }

            Platform.runLater(() -> {
                if (showSaveDialog()) {
                    new Thread(() -> {
                        if (downloadAudio(bestAudioOnlyFormatCode)) {
                            updateStatusText(StatusMessage.FINISHED);
                        } else {
                            updateStatusText(StatusMessage.DOWNLOAD_ERROR);
                        }
                        onFinishDownload();
                    }).start();
                } else {
                    updateStatusText(StatusMessage.READY);
                    onFinishDownload();
                }
            });
        }).start();
    }

    private void downloadVideoTask(final List<VideoFormat> videoFormats) {
        new Thread(() -> {

            VideoResolution preferResolution = preferResolutionChoiceBox.getValue();
            boolean prefer60Fps = prefer60FpsCheckbox.isSelected();

            List<String> formatCodes = VideoFormatUtils.findBestFormatCodes(videoFormats, preferResolution, prefer60Fps, false);
            List<String> formatCodesPreferMp4 = VideoFormatUtils.findBestFormatCodes(videoFormats, preferResolution, prefer60Fps, true);

            if (formatCodes.isEmpty()) {
                updateStatusText(StatusMessage.NO_FORMAT);
                onFinishDownload();
                return;
            }

            if (!findVideoTitleName()) {
                updateStatusText(StatusMessage.NAME_NOT_FOUND);
                onFinishDownload();
                return;
            }

            Platform.runLater(() -> {
                if (showSaveDialog()) {
                    new Thread(() -> {
                        boolean mp4Selected = FilenameUtils.getExtension(finalFilePath).equalsIgnoreCase("mp4");
                        List<String> selectedFormatCodes = mp4Selected ? formatCodesPreferMp4 : formatCodes;

                        boolean success;
                        if (selectedFormatCodes.size() == 2) {
                            success = downloadVideo(selectedFormatCodes.get(0), selectedFormatCodes.get(1));
                        } else {
                            success = downloadCompleteVideo(selectedFormatCodes.get(0));
                        }
                        if (success)
                            updateStatusText(StatusMessage.FINISHED);
                        else
                            updateStatusText(StatusMessage.DOWNLOAD_ERROR);
                        onFinishDownload();
                    }).start();
                } else {
                    updateStatusText(StatusMessage.READY);
                    onFinishDownload();
                }
            });
        }).start();
    }

    public String getStatusText() {
        return statusLabel.getText();
    }

    public void updateStatusText(StatusMessage statusMessage) {
        updateStatusText(statusMessage.toString());
    }

    public void updateStatusText(final String text) {
        Platform.runLater(() -> statusLabel.setText(text));
    }

    private boolean findVideoTitleName() {
        final String youtubeID = YoutubeUtils.getYoutubeID(linkTextField.getText());

        videoTitle = ApiUtils.getYoutubeVideoTitle(youtubeID);
        if (videoTitle == null) {
            Log.debug("findVideoTitleName(): can't find video title!");
            return false;
        }
        return true;
    }

    private boolean downloadAudio(String formatCode) {
        final String youtubeID = YoutubeUtils.getYoutubeID(linkTextField.getText());

        updateStatusText(StatusMessage.DOWNLOADING_AUDIO);

        // setup temp file path
        String tempFilePath;
        int attemptCount = 0;
        do {
            String tempName = videoTitle.replace(' ', '_').replaceAll("[^A-Za-z0-9_]", "");
            tempFilePath = downloadDirectory + File.separator + tempName + ".temp" + attemptCount;
            ++attemptCount;
        } while (Paths.get(tempFilePath).toFile().exists());

        // download audio file
        DownloadListener downloadListener = new DownloadListener(this);

        youtubeDL.download(youtubeID, formatCode, tempFilePath, downloadListener);
        downloadListener.await();

        if (!Paths.get(tempFilePath).toFile().exists()) {
            Log.debug("downloadAudio(): problem with downloading temp audio file!");
            return false;
        }
        Log.debug("downloadAudio(): temp file path: " + tempFilePath);

        updateStatusText(StatusMessage.CONVERTING_TO_MP3);

        // delete the existing mp3 file if found. if failed to delete, change to new path.
        attemptCount = 0;
        String fixedFinalPath = finalFilePath;
        while (!FileCheckUtils.canCreateNewFile(Paths.get(fixedFinalPath))) {
            Log.debug("downloadAudio(): the existing mp3 file found! try to delete it");
            try {
                Files.delete(Paths.get(fixedFinalPath).toAbsolutePath());
            } catch (IOException e) {
                Log.debug("downloadAudio(): can't delete the existing mp3 file!", e);
                Log.debug("downloadAudio(): change file final path");
                fixedFinalPath = String.format(Locale.US, "%s(%d).%s",
                        FilenameUtils.removeExtension(finalFilePath), attemptCount,
                        FilenameUtils.getExtension(finalFilePath));
                attemptCount++;
            }
        }
        finalFilePath = fixedFinalPath;
        Log.debug("downloadAudio(): final file path: " + finalFilePath);

        //find audio duration
        DurationFinderListener durationFinderListener = new DurationFinderListener();
        if (ffprobe.findDuration(tempFilePath, durationFinderListener)) {
            durationFinderListener.await();
        } else {
            Log.debug("downloadAudio(): couldn't start duration finder process!");
            return false;
        }
        final Duration audioDuration = durationFinderListener.getDuration();

        ConvertToMp3Listener convertListener = new ConvertToMp3Listener(this, audioDuration);
        if (ffmpeg.convertAudioToMp3(tempFilePath, finalFilePath, convertListener)) {
            convertListener.await();
        } else {
            Log.debug("downloadAudio(): couldn't start mp3 converting process!");
            return false;
        }

        // delete temp audio file
        boolean tempDeletionSuccess = FileUtils.deleteQuietly(Paths.get(tempFilePath).toFile());
        Log.debug("downloadAudio(): temp file deletion success: " + tempDeletionSuccess);

        // set output link
        outputFileLink.setOnAction(event -> {
            String path;
            String command;
            if (Paths.get(finalFilePath).toFile().exists()) {
                command = "/select,";
                path = finalFilePath;
            } else {
                command = "/root,";
                path = downloadDirectory;
            }
            try {
                new ProcessBuilder("explorer.exe", command, path).start();
            } catch (IOException e) {
                Log.debug("output file link error: ", e);
            }
            outputFileLink.setVisited(false);
        });
        outputFileLink.visibleProperty().set(true);

        return true;
    }

    private boolean downloadCompleteVideo(String completeVideoFormatCode) {
        final String youtubeID = YoutubeUtils.getYoutubeID(linkTextField.getText());

        updateStatusText(StatusMessage.DOWNLOADING_COMPLETE_VIDEO);

        /* temp audio part */

        // setup temp file path
        String tempFilePath;
        int attemptCount = 0;
        do {
            String tempName = videoTitle.replace(' ', '_').replaceAll("[^A-Za-z0-9_]", "");
            tempFilePath = downloadDirectory + File.separator + tempName + ".temp" + attemptCount;
            ++attemptCount;
        } while (Paths.get(tempFilePath).toFile().exists());

        // download temp file
        DownloadListener downloadListener = new DownloadListener(this);
        youtubeDL.download(youtubeID, completeVideoFormatCode, tempFilePath, downloadListener);
        downloadListener.await();

        if (!Paths.get(tempFilePath).toFile().exists()) {
            Log.debug("downloadCompleteVideo(): problem with downloading temp file!");
            return false;
        }
        Log.debug("downloadCompleteVideo(): temp file path: " + tempFilePath);

        /* converting part */

        // delete the existing file if found. if it failed to delete, change final path.
        attemptCount = 0;
        String fixedFinalPath = finalFilePath;
        while (!FileCheckUtils.canCreateNewFile(Paths.get(fixedFinalPath))) {
            Log.debug("downloadCompleteVideo(): the existing file found! try to delete it");
            try {
                Files.delete(Paths.get(fixedFinalPath));
            } catch (Exception e) {
                Log.debug("downloadCompleteVideo(): can't delete the existing file!", e);
                Log.debug("downloadCompleteVideo(): change file final path");
                fixedFinalPath = String.format(Locale.US, "%s(%d).%s",
                        FilenameUtils.removeExtension(finalFilePath), attemptCount,
                        FilenameUtils.getExtension(finalFilePath));
                attemptCount++;
            }
        }
        finalFilePath = fixedFinalPath;
        Log.debug("downloadCompleteVideo(): final file path: " + finalFilePath);

        // find video duration
        DurationFinderListener durationFinderListener = new DurationFinderListener();
        if (ffprobe.findDuration(tempFilePath, durationFinderListener)) {
            durationFinderListener.await();
        } else {
            Log.debug("downloadCompleteVideo(): couldn't start duration finder process!");
            return false;
        }
        final Duration videoDuration = durationFinderListener.getDuration();

        if (!FilenameUtils.getExtension(finalFilePath).equalsIgnoreCase("mp4")) {

            updateStatusText(StatusMessage.CONVERTING_TO_MKV);

            ConvertToMkvListener convertListener = new ConvertToMkvListener(this, videoDuration);
            if (ffmpeg.convertVideoToMkv(tempFilePath, finalFilePath, convertListener)) {
                convertListener.await();
            } else {
                Log.debug("downloadCompleteVideo(): couldn't start mkv converting process!");
                return false;
            }

            boolean tempDeletionSuccess = FileUtils.deleteQuietly(Paths.get(tempFilePath).toFile());
            Log.debug("downloadCompleteVideo(): temp file deletion success: " + tempDeletionSuccess);

        } else {

            // find video codec
            updateStatusText(StatusMessage.CONVERTING_TO_MP4);
            CodecFinderListener codecFinderListener = new CodecFinderListener();
            if (ffprobe.findVideoCodec(tempFilePath, codecFinderListener)) {
                codecFinderListener.await();
            } else {
                Log.debug("downloadCompleteVideo(): couldn't find video codec!");
                return false;
            }
            final String codec = codecFinderListener.getCodec();

            // if video codec is mp4 format, just rename the temp file.
            if (codec.equalsIgnoreCase("mpeg4") || codec.equalsIgnoreCase("h264")) {
                Path source = Paths.get(tempFilePath);
                try {
                    Files.move(source, source.resolveSibling(FilenameUtils.getName(finalFilePath)));
                } catch (IOException e) {
                    Log.debug("downloadCompleteVideo(): couldn't rename to final path!");
                    return false;
                }
            } else {
                updateStatusText(StatusMessage.CONVERTING_TO_MP4);

                ConvertToMp4Listener convertListener = new ConvertToMp4Listener(this, videoDuration);
                if (ffmpeg.convertVideoToMp4(tempFilePath, finalFilePath, convertListener)) {
                    convertListener.await();
                } else {
                    Log.debug("downloadCompleteVideo(): couldn't start mp4 converting process!");
                    return false;
                }

                boolean tempDeletionSuccess = FileUtils.deleteQuietly(Paths.get(tempFilePath).toFile());
                Log.debug("downloadCompleteVideo(): temp file deletion success: " + tempDeletionSuccess);
            }
        }

        // set output link
        outputFileLink.setOnAction(event -> {
            String path;
            String command;
            if (Paths.get(finalFilePath).toFile().exists()) {
                command = "/select,";
                path = finalFilePath;
            } else {
                command = "/root,";
                path = downloadDirectory;
            }
            try {
                new ProcessBuilder("explorer.exe", command, path).start();
            } catch (IOException e) {
                Log.debug("output file link error: ", e);
            }
            outputFileLink.setVisited(false);
        });
        outputFileLink.visibleProperty().set(true);

        return true;
    }

    private boolean downloadVideo(String videoFormatCode, String audioFormatCode) {
        final String youtubeID = YoutubeUtils.getYoutubeID(linkTextField.getText());

       /* temp video part */

        updateStatusText(StatusMessage.DOWNLOADING_VIDEO);

        // setup temp video file path
        String tempVideoFilePath;
        int attemptCount = 0;
        do {
            String tempName = videoTitle.replace(' ', '_').replaceAll("[^A-Za-z0-9_]", "");
            tempVideoFilePath = downloadDirectory + File.separator + tempName + ".temp" + attemptCount;
            ++attemptCount;
        } while (Paths.get(tempVideoFilePath).toFile().exists());

        // download temp video file
        DownloadListener videoDownloadListener = new DownloadListener(this, "(Video)");
        youtubeDL.download(youtubeID, videoFormatCode, tempVideoFilePath, videoDownloadListener);
        videoDownloadListener.await();

        if (!Paths.get(tempVideoFilePath).toFile().exists()) {
            Log.debug("downloadVideo(): problem with downloading temp audio file!");
            return false;
        }
        Log.debug("downloadVideo(): temp video file path: " + tempVideoFilePath);

         /* temp audio part */

        updateStatusText(StatusMessage.DOWNLOADING_AUDIO);

        // setup temp audio file path
        String tempAudioFilePath;
        attemptCount = 0;
        do {
            String tempName = videoTitle.replace(' ', '_').replaceAll("[^A-Za-z0-9_]", "");
            tempAudioFilePath = downloadDirectory + File.separator + tempName + ".temp" + attemptCount;
            ++attemptCount;
        } while (Paths.get(tempAudioFilePath).toFile().exists());


        // download temp audio file
        DownloadListener audioDownloadListener = new DownloadListener(this, "(Audio)");
        youtubeDL.download(youtubeID, audioFormatCode, tempAudioFilePath, audioDownloadListener);
        audioDownloadListener.await();

        if (!Paths.get(tempAudioFilePath).toFile().exists()) {
            Log.debug("downloadVideo(): problem with downloading temp audio file!");
            return false;
        }
        Log.debug("downloadVideo(): temp audio file path: " + tempAudioFilePath);

        /* merging part */
        updateStatusText(StatusMessage.MERGING_TO_MKV);

        String tempFinalPath = FilenameUtils.removeExtension(finalFilePath) + ".mkv";

        // delete the existing mkv file if found. if failed to delete, change to new path.
        attemptCount = 0;
        String fixedFinalPath = tempFinalPath;
        while (!FileCheckUtils.canCreateNewFile(Paths.get(fixedFinalPath))) {
            Log.debug("downloadVideo(): the existing mkv file found! try to delete it");
            try {
                Files.delete(Paths.get(fixedFinalPath));
            } catch (Exception e) {
                Log.debug("downloadVideo(): can't delete the existing mkv file!", e);
                Log.debug("downloadVideo(): change file final path");
                fixedFinalPath = String.format(Locale.US, "%s(%d).%s",
                        FilenameUtils.removeExtension(tempFinalPath), attemptCount,
                        FilenameUtils.getExtension(tempFinalPath));
                attemptCount++;
            }
        }
        tempFinalPath = fixedFinalPath;
        Log.debug("downloadVideo(): temp final file path: " + tempFinalPath);

        // find video duration
        DurationFinderListener durationFinderListener = new DurationFinderListener();
        if (ffprobe.findDuration(tempVideoFilePath, durationFinderListener)) {
            durationFinderListener.await();
        } else {
            Log.debug("downloadAudio(): couldn't start duration finder process!");
            return false;
        }
        final Duration videoDuration = durationFinderListener.getDuration();

        MergeToMkvListener mergingListener = new MergeToMkvListener(this, videoDuration);
        if (ffmpeg.mergeVideoAndAudio(tempVideoFilePath, tempAudioFilePath, tempFinalPath, mergingListener)) {
            mergingListener.await();
        } else {
            Log.debug("downloadVideo(): couldn't start mkv merging process!");
            return false;
        }

        // delete temp audio file
        boolean tempVideoDeletionSuccess = FileUtils.deleteQuietly(Paths.get(tempVideoFilePath).toFile());
        boolean tempAudioDeletionSuccess = FileUtils.deleteQuietly(Paths.get(tempAudioFilePath).toFile());
        Log.debug("downloadVideo(): temp video file deletion success: " + tempVideoDeletionSuccess);
        Log.debug("downloadVideo(): temp audio file deletion success: " + tempAudioDeletionSuccess);

        /* converting part */

        if (FilenameUtils.getExtension(finalFilePath).equalsIgnoreCase("mp4")) {
            updateStatusText(StatusMessage.CONVERTING_TO_MP4);

            ConvertToMp4Listener convertListener = new ConvertToMp4Listener(this, videoDuration);
            if (ffmpeg.convertVideoToMp4(tempFinalPath, finalFilePath, convertListener)) {
                convertListener.await();
            } else {
                Log.debug("downloadCompleteVideo(): couldn't start mp4 converting process!");
                return false;
            }

            boolean tempDeletionSuccess = FileUtils.deleteQuietly(Paths.get(tempFinalPath).toFile());
            Log.debug("downloadCompleteVideo(): temp final file deletion success: " + tempDeletionSuccess);
        }


        // set output link
        outputFileLink.setOnAction(event -> {
            String path;
            String command;
            if (Paths.get(finalFilePath).toFile().exists()) {
                command = "/select,";
                path = finalFilePath;
            } else {
                command = "/root,";
                path = downloadDirectory;
            }
            try {
                new ProcessBuilder("explorer.exe", command, path).start();
            } catch (IOException e) {
                Log.debug("output file link error: ", e);
            }
            outputFileLink.setVisited(false);
        });
        outputFileLink.visibleProperty().set(true);

        return true;
    }

    private List<VideoFormat> getVideoFormats() {
        updateStatusText(StatusMessage.GETTING_VIDEO_INFO);

        VideoFormatFinderListener finderListener = new VideoFormatFinderListener(this);
        youtubeDL.listVideoFormats(YoutubeUtils.getYoutubeID(linkTextField.getText()), finderListener);
        finderListener.await();

        return finderListener.getVideoFormats();
    }

    private void onStartDownload() {
        outputFileLink.visibleProperty().set(false);

        prefer60FpsCheckbox.disableProperty().set(true);
        preferResolutionChoiceBox.disableProperty().set(true);
        audioOnlyCheckbox.disableProperty().set(true);
        downloadButton.disableProperty().set(true);
        linkTextField.disableProperty().set(true);
    }

    private void onFinishDownload() {
        if (!audioOnlyCheckbox.isSelected()) {
            prefer60FpsCheckbox.disableProperty().set(false);
            preferResolutionChoiceBox.disableProperty().set(false);
        }
        audioOnlyCheckbox.disableProperty().set(false);
        downloadButton.disableProperty().set(false);
        linkTextField.disableProperty().set(false);
    }

    private boolean showSaveDialog() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save as");
        chooser.setInitialDirectory(desktopDirectory);

        // fix forbidden character in filename
        String initialFileName = videoTitle.replaceAll("[<>:\"\\\\/|?*]", "");

        if (audioOnlyCheckbox.isSelected()) {
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("MPEG Layer-3 Audio File", "*.mp3")
            );
            chooser.setInitialFileName(initialFileName + ".mp3");
        } else {
            FileChooser.ExtensionFilter mkvExtension = new FileChooser.ExtensionFilter("Matroska Video File", "*.mkv");
            FileChooser.ExtensionFilter mp4Extension = new FileChooser.ExtensionFilter("MPEG-4 Video File", "*.mp4");
            chooser.getExtensionFilters().addAll(
                    mkvExtension,
                    mp4Extension
            );
            chooser.setSelectedExtensionFilter(mkvExtension);
            chooser.setInitialFileName(initialFileName + ".mkv");
        }

        File selectedFile = chooser.showSaveDialog(mainPane.getScene().getWindow());

        if (selectedFile != null) {
            finalFilePath = selectedFile.getAbsolutePath();
            downloadDirectory = selectedFile.getParent();
            Log.debug("Download directory: " + downloadDirectory);
            Log.debug("Final file path: " + finalFilePath);
            return true;
        }
        return false;
    }


}
