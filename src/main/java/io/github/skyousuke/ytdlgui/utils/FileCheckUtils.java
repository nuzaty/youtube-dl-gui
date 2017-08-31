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

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;

public class FileCheckUtils {

    private FileCheckUtils() {}

    public static boolean canCreateNewFile(Path path) {
        boolean fileExists = path.toFile().exists();
        boolean createSuccess;
        try {
            createSuccess = path.toFile().createNewFile();
        } catch (IOException e) {
            return false;
        }
        if (createSuccess && !fileExists) {
            FileUtils.deleteQuietly(path.toFile());
        }
        return createSuccess;
    }
}
