package io.github.skyousuke.ytdlgui.utils;

import com.esotericsoftware.minlog.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DurationTest {

    @BeforeAll
    static void setUp() {
        Log.DEBUG();
    }

    @Test
    void getPercent() {
        Assertions.assertEquals(0, new Duration(0, 0, 0).getPercent(new Duration(0, 0, 0)));
        Assertions.assertEquals(0, new Duration(1, 0, 0).getPercent(new Duration(0, 0, 0)));
        Assertions.assertEquals(0, new Duration(0, 1, 0).getPercent(new Duration(0, 0, 0)));
        Assertions.assertEquals(0, new Duration(0, 0, 1).getPercent(new Duration(0, 0, 0)));
        Assertions.assertEquals(0, new Duration(1, 1, 0).getPercent(new Duration(0, 0, 0)));
        Assertions.assertEquals(0, new Duration(0, 1, 1).getPercent(new Duration(0, 0, 0)));
        Assertions.assertEquals(0, new Duration(1, 0, 1).getPercent(new Duration(0, 0, 0)));
        Assertions.assertEquals(0, new Duration(99, 152, 45.5752f).getPercent(new Duration(0, 0, 0)));

        Assertions.assertEquals(50, new Duration(1, 0, 0).getPercent(new Duration(0, 30, 0)));
        Assertions.assertEquals(50, new Duration(2, 0, 0).getPercent(new Duration(1, 0, 0)));
        Assertions.assertEquals(50, new Duration(1, 59, 60).getPercent(new Duration(1, 0, 0)));
        Assertions.assertEquals(50, new Duration(8, 98, 99).getPercent(new Duration(4, 49, 49.5f)));
    }

}