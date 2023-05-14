package com.justixdev.eazynick.utilities;

import com.justixdev.eazynick.EazyNick;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.justixdev.eazynick.nms.ReflectionHelper.*;

public class ClassFinder {

    public static List<Class<?>> withAnnotation(String packageName, Class<? extends Annotation> annotaionClass) {
        return allInPackage(packageName)
                .stream()
                .filter(clazz -> clazz.isAnnotationPresent(annotaionClass))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static List<Class<?>> allInPackage(String packageName) {
        try {
            ClassLoader classLoader = EazyNick.getInstance().getPluginClassLoader();

            return
                    ((Set<Object>) invoke(
                            invokeStatic(
                                    findClass((NMS_VERSION.equals("v1_7_R4") ? "net.minecraft.util." : "") + "com.google.common.reflect.ClassPath"),
                                    "from",
                                    types(ClassLoader.class),
                                    classLoader
                            ),
                            "getTopLevelClassesRecursive",
                            types(String.class),
                            packageName
                    ))
                            .stream()
                            .map(classInfo -> {
                                try {
                                    return Class.forName((String) invoke(classInfo, "getName"), true, classLoader);
                                } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException ex) {
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return Collections.emptyList();
    }

}
