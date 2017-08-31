package io.github.skyousuke.ytdlgui.utils;

import com.esotericsoftware.minlog.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class DownloaderUtilsTest {

    @BeforeAll
    static void setUp() {
        Log.DEBUG();
    }

    @Test
    void getDownloadProgressTest() {
        String data1 = "[download]   0.0% of 3.55MiB at 249.82KiB/s ETA 00:14";
        String data2 = "[download]   0.1% of 3.55MiB at 749.47KiB/s ETA 00:04";
        String data3 = "[download]   0.2% of 3.55MiB at  1.71MiB/s ETA 00:02";
        String data4 = "[download]   0.4% of 3.55MiB at  2.93MiB/s ETA 00:01";
        String data5 = "[download]   0.9% of 3.55MiB at  2.38MiB/s ETA 00:01";
        String data6 = "[download]   1.7% of 3.55MiB at  2.29MiB/s ETA 00:01";
        String data7 = "[download]   3.5% of 3.55MiB at  2.39MiB/s ETA 00:01";
        String data8 = "[download]   7.0% of 3.55MiB at  2.57MiB/s ETA 00:01";
        String data9 = "[download]  14.1% of 3.55MiB at  2.34MiB/s ETA 00:01";
        String data10 = "[download]  28.2% of 3.55MiB at  2.55MiB/s ETA 00:01";
        String data11 = "[download]  56.3% of 3.55MiB at  2.69MiB/s ETA 00:00";
        String data12 = "[download] 100.0% of 3.55MiB at  2.79MiB/s ETA 00:00";
        String data13 = "[download] 100% of 3.55MiB in 00:01";

        Assertions.assertEquals("0.0% of 3.55MiB at 249.82KiB/s ETA 00:14", DownloaderUtils.getDownloadProgress(data1));
        Assertions.assertEquals("0.1% of 3.55MiB at 749.47KiB/s ETA 00:04", DownloaderUtils.getDownloadProgress(data2));
        Assertions.assertEquals("0.2% of 3.55MiB at  1.71MiB/s ETA 00:02", DownloaderUtils.getDownloadProgress(data3));
        Assertions.assertEquals("0.4% of 3.55MiB at  2.93MiB/s ETA 00:01", DownloaderUtils.getDownloadProgress(data4));
        Assertions.assertEquals("0.9% of 3.55MiB at  2.38MiB/s ETA 00:01", DownloaderUtils.getDownloadProgress(data5));
        Assertions.assertEquals("1.7% of 3.55MiB at  2.29MiB/s ETA 00:01", DownloaderUtils.getDownloadProgress(data6));
        Assertions.assertEquals("3.5% of 3.55MiB at  2.39MiB/s ETA 00:01", DownloaderUtils.getDownloadProgress(data7));
        Assertions.assertEquals("7.0% of 3.55MiB at  2.57MiB/s ETA 00:01", DownloaderUtils.getDownloadProgress(data8));
        Assertions.assertEquals("14.1% of 3.55MiB at  2.34MiB/s ETA 00:01", DownloaderUtils.getDownloadProgress(data9));
        Assertions.assertEquals("28.2% of 3.55MiB at  2.55MiB/s ETA 00:01", DownloaderUtils.getDownloadProgress(data10));
        Assertions.assertEquals("56.3% of 3.55MiB at  2.69MiB/s ETA 00:00", DownloaderUtils.getDownloadProgress(data11));
        Assertions.assertEquals("100.0% of 3.55MiB at  2.79MiB/s ETA 00:00", DownloaderUtils.getDownloadProgress(data12));
        Assertions.assertEquals(null, DownloaderUtils.getDownloadProgress(data13));
    }
}