package io.github.skyousuke.ytdlgui.utils;

import com.esotericsoftware.minlog.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ConverterUtilsTest {

    @BeforeAll
    static void setUp() {
        Log.DEBUG();
    }

    @Test
    void getTotalDurationTest() {
        String data1 = "ffmpeg version N-87043-gf0f4888 Copyright (c) 2000-2017 the FFmpeg developers";
        String data2 = "  libavdevice    57.  7.101 / 57.  7.101";
        String data3 = "  libavfilter     6.100.100 /  6.100.100";
        String data4 = "  libswscale      4.  7.103 /  4.  7.103";
        String data5 = "size=    3328kB time=00:01:51.91 bitrate= 243.6kbits/s speed=44.7x";
        String data6 = "size=    4096kB time=00:02:14.43 bitrate= 249.6kbits/s speed=44.8x";
        String data7 = "size=    4608kB time=00:02:36.84 bitrate= 240.7kbits/s speed=44.8x";
        String data8 = "video:0kB audio:6936kB subtitle:0kB other streams:0kB global headers:0kB muxing overhead: 0.003562%";
        String data9 = "  Duration: 00:03:53.64, start: 0.000000, bitrate: 127 kb/s";

        Assertions.assertEquals(null, ConverterUtils.getTotalDuration(data1));
        Assertions.assertEquals(null, ConverterUtils.getTotalDuration(data2));
        Assertions.assertEquals(null, ConverterUtils.getTotalDuration(data3));
        Assertions.assertEquals(null, ConverterUtils.getTotalDuration(data4));
        Assertions.assertEquals(null, ConverterUtils.getTotalDuration(data5));
        Assertions.assertEquals(null, ConverterUtils.getTotalDuration(data6));
        Assertions.assertEquals(null, ConverterUtils.getTotalDuration(data7));
        Assertions.assertEquals(null, ConverterUtils.getTotalDuration(data8));
        Assertions.assertEquals(new Duration(0, 3, 53.64f), ConverterUtils.getTotalDuration(data9));
    }

    @Test
    void getConvertingDurationTest() {
        String data1 = "ffmpeg version N-87043-gf0f4888 Copyright (c) 2000-2017 the FFmpeg developers";
        String data2 = "  libavdevice    57.  7.101 / 57.  7.101";
        String data3 = "  libavfilter     6.100.100 /  6.100.100";
        String data4 = "  libswscale      4.  7.103 /  4.  7.103";
        String data5 = "size=    3328kB time=00:01:51.91 bitrate= 243.6kbits/s speed=44.7x";
        String data6 = "size=    4096kB time=00:02:14.43 bitrate= 249.6kbits/s speed=44.8x";
        String data7 = "size=    4608kB time=00:02:36.84 bitrate= 240.7kbits/s speed=44.8x";
        String data8 = "video:0kB audio:6936kB subtitle:0kB other streams:0kB global headers:0kB muxing overhead: 0.003562%";
        String data9 = "  Duration: 00:03:53.64, start: 0.000000, bitrate: 127 kb/s";

        Assertions.assertEquals(null, ConverterUtils.getConvertingDuration(data1));
        Assertions.assertEquals(null, ConverterUtils.getConvertingDuration(data2));
        Assertions.assertEquals(null, ConverterUtils.getConvertingDuration(data3));
        Assertions.assertEquals(null, ConverterUtils.getConvertingDuration(data4));
        Assertions.assertEquals(new Duration(0, 1, 51.91f), ConverterUtils.getConvertingDuration(data5));
        Assertions.assertEquals(new Duration(0, 2, 14.43f), ConverterUtils.getConvertingDuration(data6));
        Assertions.assertEquals(new Duration(0, 2, 36.84f), ConverterUtils.getConvertingDuration(data7));
        Assertions.assertEquals(null, ConverterUtils.getConvertingDuration(data8));
        Assertions.assertEquals(null, ConverterUtils.getConvertingDuration(data9));
    }

}