package io.github.skyousuke.ytdlgui.utils;

import com.esotericsoftware.minlog.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ApiUtilsTest {

    @BeforeAll
    static void setUp() {
        Log.DEBUG();
    }

    @Test
    void getYoutubeVideoTitleTest() {
        Assertions.assertEquals(ApiUtils.getYoutubeVideoTitle(null), null);
        Assertions.assertEquals(ApiUtils.getYoutubeVideoTitle("wrongID"), null);
        Assertions.assertEquals(ApiUtils.getYoutubeVideoTitle("YEn0efm5SRQ"), "20 random facts about kanninich (20 เรื่องจริงของ kanninich )");
        Assertions.assertEquals(ApiUtils.getYoutubeVideoTitle("a2GujJZfXpg"), "スパークル [original ver.] -Your name. Music Video edition- 予告編 from new album「人間開花」初回盤DVD");
        Assertions.assertEquals(ApiUtils.getYoutubeVideoTitle("r5ZQ7KnK_lQ"), "RADWIMPS／夢灯籠（映画『君の名は。』主題歌）cover by 宇野悠人");
        Assertions.assertEquals(ApiUtils.getYoutubeVideoTitle("88_vQrClgMw"), "จังหวะการเล่นมันส์ๆ ของ \"ชนาธิป\" ปะทะ เวกัลตะ เซนได นัดที่ 6 ใน เจลีก | 26-08-17");
    }

    @Test
    void isValidYoutubeIdTest() {
        Assertions.assertTrue(ApiUtils.isValidYoutubeId("88_vQrClgMw"));
        Assertions.assertTrue(ApiUtils.isValidYoutubeId("r5ZQ7KnK_lQ"));
        Assertions.assertTrue(ApiUtils.isValidYoutubeId("a2GujJZfXpg"));
        Assertions.assertTrue(ApiUtils.isValidYoutubeId("YEn0efm5SRQ"));

        Assertions.assertFalse(ApiUtils.isValidYoutubeId("wrongID"));
        Assertions.assertFalse(ApiUtils.isValidYoutubeId(null));
    }
}