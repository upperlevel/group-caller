package xyz.upperlevel.groupcaller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Creates the caller class for all the {@link GroupCall} annotated methods with the same group name.
 * The method calls will be sorted by priority and will be put in a static function called {@code call} inside the caller class
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GroupCaller {
    /**
     * The group of the subscribers to include
     * @return groupname of subscriptions
     */
    String value();

    /**
     * The classname that will be used to create the group caller (by default {groupname}Caller)
     * @return the groupcaller's classname
     */
    String clazz() default "";
}
