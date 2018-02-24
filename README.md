# GroupCaller
GroupCaller is a lightweight annotation processor that solves a simple tasks:
calling methods that aren't in a specific point in your application.
<br>Sometimes you need some kind of initialization where a lot of methods need to be called,
with the GroupCaller annotations you are able to define a list of methods to call without defining a method list.

## How to use
You can add methods to the call list by using the `@GroupCall` annotation and putting the group name in it
```java
class SomeSparseClass {
    @GroupCall("init")// Subscribe this method to the init list
    public static void initializeStrangeThing() {
        foobar();
    }
}

class AnotherClass {
    @GroupCall("init")// Subscribe this method too
    public static void initializeSomethingElse() {
        barfoo();
    }
}
```
When you want to call the methods just use `@GroupCaller` with the same group name to generate the caller class.
Then use it by calling the static method `CallerClass.call();` (the default name is the groupname + Caller)

```java
@GroupCaller("init")// Create the class InitCaller in the same package
class Main {
    protected void initProgram() {
        InitCaller.call();
    }
}
```
Note: You can set the subscriber priority with `@GroupCall(value = "groupname", prioriy = 50)`
Note: You can change the caller class name with `@GroupCaller(value = "groupname", clazz = "OtherClass")`

## How to include (Gradle)
```groovy
plugins {
    id "maven"
    // Then select one of the following
    id "net.ltgt.apt-idea" version "0.14"    // For intellij
    id "net.ltgt.apt-eclipse" version "0.14" // For eclipse
    id "net.ltgt.apt" version "0.14"         // With no IDE
}

dependencies {
    compileOnly group: 'xyz.upperlevel.groupcaller', name: 'groupcaller', version: '1.0'
    annotationProcessor group: 'xyz.upperlevel.groupcaller', name: 'groupcaller', version: '1.0'
}
```