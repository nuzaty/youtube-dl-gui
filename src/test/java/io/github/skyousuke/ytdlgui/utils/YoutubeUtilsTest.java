package io.github.skyousuke.ytdlgui.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class YoutubeUtilsTest {

    @Test
    void getYoutubeID() {
        String url1 = "http://www.youtube.com/v/0zM3nApSvMg?fs=1&hl=en_US&rel=0";
        String url2 = "http://www.youtube.com/embed/0zM3nApSvMg?rel=0";
        String url3 = "http://www.youtube.com/watch?v=0zM3nApSvMg&feature=feedrec_grec_index";
        String url4 = "http://www.youtube.com/watch?v=0zM3nApSvMg";
        String url5 = "http://youtu.be/0zM3nApSvMg";
        String url6 = "http://www.youtube.com/watch?v=0zM3nApSvMg#t=0m10s";
        String url7 = "http://www.youtube.com/user/IngridMichaelsonVEVO#p/a/u/1/KdwsulMb8EQ";
        String url8 = "http://youtu.be/dQw4w9WgXcQ";
        String url9 = "http://www.youtube.com/embed/dQw4w9WgXcQ";
        String url10 = "http://www.youtube.com/v/dQw4w9WgXcQ";
        String url11 = "http://www.youtube.com/e/dQw4w9WgXcQ";
        String url12 = "http://www.youtube.com/watch?v=dQw4w9WgXcQ";
        String url13 = "http://www.youtube.com/?v=dQw4w9WgXcQ";
        String url14 = "http://www.youtube.com/watch?feature=player_embedded&v=dQw4w9WgXcQ";
        String url15 = "http://www.youtube.com/?feature=player_embedded&v=dQw4w9WgXcQ";
        String url16 = "http://www.youtube.com/user/IngridMichaelsonVEVO#p/u/11/KdwsulMb8EQ";
        String url17 = "http://www.youtube-nocookie.com/v/6L3ZvIMwZFM?version=3&hl=en_US&rel=0";
        String url18 = "https://www.youtube.com/watch?v=NTuAV_fF-bs&list=PL0F7B2914E28488F2&index=3";
        String url19 = "https://youtu.be/NTuAV_fF-bs?list=PL0F7B2914E28488F2";
        String url20 = "https://youtu.be/kOQf3lAqDxA?t=32s";
        String url21 = "ZGfC2boki-I";

        Assertions.assertNotEquals("", YoutubeUtils.getYoutubeID(url1));
        Assertions.assertNotEquals("", YoutubeUtils.getYoutubeID(url2));
        Assertions.assertNotEquals("", YoutubeUtils.getYoutubeID(url3));
        Assertions.assertNotEquals("", YoutubeUtils.getYoutubeID(url4));
        Assertions.assertNotEquals("", YoutubeUtils.getYoutubeID(url5));
        Assertions.assertNotEquals("", YoutubeUtils.getYoutubeID(url6));
        Assertions.assertNotEquals("", YoutubeUtils.getYoutubeID(url7));
        Assertions.assertNotEquals("", YoutubeUtils.getYoutubeID(url8));
        Assertions.assertNotEquals("", YoutubeUtils.getYoutubeID(url9));
        Assertions.assertNotEquals("", YoutubeUtils.getYoutubeID(url10));
        Assertions.assertNotEquals("", YoutubeUtils.getYoutubeID(url11));
        Assertions.assertNotEquals("", YoutubeUtils.getYoutubeID(url12));
        Assertions.assertNotEquals("", YoutubeUtils.getYoutubeID(url13));
        Assertions.assertNotEquals("", YoutubeUtils.getYoutubeID(url14));
        Assertions.assertNotEquals("", YoutubeUtils.getYoutubeID(url15));
        Assertions.assertNotEquals("", YoutubeUtils.getYoutubeID(url16));
        Assertions.assertNotEquals("", YoutubeUtils.getYoutubeID(url17));

        Assertions.assertEquals("NTuAV_fF-bs", YoutubeUtils.getYoutubeID(url18));
        Assertions.assertEquals("NTuAV_fF-bs", YoutubeUtils.getYoutubeID(url19));
        Assertions.assertEquals("kOQf3lAqDxA", YoutubeUtils.getYoutubeID(url20));
        Assertions.assertEquals("ZGfC2boki-I", YoutubeUtils.getYoutubeID(url21));
    }

}