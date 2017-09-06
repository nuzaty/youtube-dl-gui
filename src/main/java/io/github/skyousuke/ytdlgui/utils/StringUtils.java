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

import java.util.List;

public class StringUtils {

    private StringUtils() {}

    public static String getListAsString(List list) {
        StringBuilder sb = new StringBuilder();
        final int size = list.size();
        for (int i = 0; i < size; ++i) {
            sb.append(list.get(i));
            if (i != size - 1)
                sb.append(' ');
        }
        return sb.toString();
    }
}
