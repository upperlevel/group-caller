package xyz.upperlevel.groupcaller;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({
        "xyz.upperlevel.groupcaller.GroupCall",
        "xyz.upperlevel.groupcaller.GroupCaller"
})
@SupportedSourceVersion(SourceVersion.RELEASE_9)
@AutoService(Processor.class)
public class GroupAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, List<GroupSubscriber>> methodsByGroup = new HashMap<>();
        for (Element e : roundEnv.getElementsAnnotatedWith(GroupCall.class)) {
            ExecutableType elem = (ExecutableType) e.asType();
            if (elem.getParameterTypes().size() != 0) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        "@GroupCall must be applied to a method without arguments", e);
                continue;
            }
            if (elem.getReceiverType() != null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        "@GroupCall must be applied to a static method", e);
                continue;
            }
            if (!e.getModifiers().contains(Modifier.PUBLIC)) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        "@GroupCall must be applied to a public method", e);
                continue;
            }

            GroupCall[] groups = e.getAnnotationsByType(GroupCall.class);
            for (GroupCall group : groups) {
                addToBag(methodsByGroup, group.value(), new GroupSubscriber(e, group.priority()));
            }

        }
        for (Element e : roundEnv.getElementsAnnotatedWith(GroupCaller.class)) {
            GroupCaller annotation = e.getAnnotation(GroupCaller.class);

            String packageName;
            {
                String packageClass = ((TypeElement) e).getQualifiedName().toString();
                int lastDot = packageClass.lastIndexOf('.');
                packageName = lastDot > 0 ? packageClass.substring(0, lastDot) : null;
            }
            String className = annotation.clazz();
            if (className.isEmpty()) {
                className = capitalize(annotation.value()) + "Caller";
            }
            List<GroupSubscriber> subscribers = methodsByGroup.getOrDefault(annotation.value(), Collections.emptyList());

            if (subscribers.isEmpty()) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "@GroupCaller " + className + " has no subscribers", e);
            }

            // Sort subscribers by reverse priority (who has higher priority comes first)
            List<Element> subscriberMethods = subscribers.stream()
                    .sorted(Comparator.comparingInt((GroupSubscriber s) -> s.prioprity).reversed())
                    .map(g -> g.method)
                    .collect(Collectors.toList());

            try {
                writeCallerClass(packageName, className, subscriberMethods);
            } catch (IOException e1) {
                throw new IllegalStateException(e1);
            }
        }
        return true;
    }

    private <K, V> void addToBag(Map<K, List<V>> bag, K key, V value) {
        List<V> values = bag.computeIfAbsent(key, k -> new ArrayList<>());
        values.add(value);
    }

    private void writeCallerClass(String packageName, String className, List<Element> called) throws IOException {
        String fullName = (packageName != null ? packageName + "." : "") + className;

        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(fullName);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            if (packageName != null) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }

            // Write class declaration
            out.print("public final class ");
            out.print(className);
            out.println(" {");

            // Write call method
            out.println("    public static void call() {");
            for (Element e : called) {
                String clazzPath = ((TypeElement)e.getEnclosingElement()).getQualifiedName().toString();
                String methodName = e.getSimpleName().toString();
                out.println("        " + clazzPath + "." + methodName + "();");
            }
            out.println("    }");

            // Write private constructor
            out.print("    private ");
            out.print(className);
            out.println(" (){}");

            out.println("}");
        }
    }

    private String capitalize(String in) {
        return Character.toUpperCase(in.charAt(0)) + in.substring(1);
    }

    private class GroupSubscriber {
        public final Element method;
        public final int prioprity;

        private GroupSubscriber(Element method, int prioprity) {
            this.method = method;
            this.prioprity = prioprity;
        }
    }
}
