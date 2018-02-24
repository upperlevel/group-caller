package xyz.upperlevel.groupcaller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines that the method can be called by a {@link GroupCaller} with the same value (group).
 * <br>This annotation can only be used in static public methods without arguments
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface GroupCall {
    /**
     * The group that this method will subscribe to
     * @return groupname to subscribe
     */
    String value();

    /**
     * The priority of the subscription (0 by default)
     * @return priority
     */
    int priority() default 0;
}
