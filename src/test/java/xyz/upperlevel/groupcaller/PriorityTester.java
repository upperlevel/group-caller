package xyz.upperlevel.groupcaller;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

@GroupCaller("priority")
public class PriorityTester {
    public static int callCount = 0;

    @Test
    public void testRollcall() {
        PriorityCaller.call();
        assertEquals(callCount, 3);
    }

    @GroupCall(value = "priority", priority = -1)
    public static void thirdSubscriber() {
        assertEquals(callCount, 2);
        callCount++;
    }

    @GroupCall(value = "priority", priority = 100)
    public static void firstSubscriber() {
        assertEquals(callCount, 0);
        callCount++;
    }

    @GroupCall("priority")
    public static void secondSubscriber() {
        assertEquals(callCount, 1);
        callCount++;
    }
}
