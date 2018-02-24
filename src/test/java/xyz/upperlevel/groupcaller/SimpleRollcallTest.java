package xyz.upperlevel.groupcaller;

import org.junit.Assert;
import org.junit.Test;

@GroupCaller("rollcall")
public class SimpleRollcallTest {
    public static int callCount = 0;

    @Test
    public void testRollcall() {
        RollcallCaller.call();
        Assert.assertEquals(callCount, 2);
    }

    @GroupCall("rollcall")
    public static void rollcallSubscriber() {
        callCount++;
    }

    @GroupCall("rollcall")
    public static void anotherSubscriber() {
        callCount++;
    }
}
