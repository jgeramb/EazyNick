package com.justixdev.eazynick.nms;

import com.justixdev.eazynick.utilities.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Stream;

public class ReflectionHelper {

    public static final String NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static final Map<String, Class<?>> CLASS_CACHE = new HashMap<>();

    public static Object toArray(Object... objects) {
        Object array = Array.newInstance(objects.length > 0 ? objects[0].getClass() : Object.class, 1);

        for (int i = 0; i < objects.length; i++)
            Array.set(array, i, objects[i]);

        return array;
    }

    public static void setField(Object obj, String fieldName, Object value)
            throws IllegalArgumentException, IllegalAccessException {
        Field field = getField(obj.getClass(), fieldName);

        if(field != null) {
            field.set(obj, value);
            field.setAccessible(false);
        }
    }

    public static Class<?> getSubClass(Class<?> clazz, String className) {
        return Stream.of(clazz.getDeclaredClasses())
                .filter(subClazz -> subClazz.getSimpleName().equals(className))
                .findFirst()
                .orElse(null);
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);

            return field;
        } catch (NoSuchFieldException ex) {
            if(clazz.getSuperclass() != null)
                return getField(clazz.getSuperclass(), fieldName);
            else
                ex.printStackTrace();
        } catch (SecurityException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private static Optional<Field> getFirstFieldMatchingType(Stream<Field> fields, Class<?> type) {
        return fields
                .filter(field -> {
                    field.setAccessible(true);

                    return field.getType().equals(type);
                })
                .findFirst();
    }

    public static Optional<Field> getFirstFieldByType(Class<?> clazz, Class<?> type) {
        return getFirstFieldMatchingType(Stream.of(clazz.getDeclaredFields()), type);
    }

    public static Optional<Field> getLastFieldByType(Class<?> clazz, Class<?> type) {
        return getFirstFieldMatchingType(
                Stream.of(clazz.getDeclaredFields())
                        .sorted(Comparator.comparing(Field::getName).reversed()),
                type
        );
    }

    public static Object getFieldValue(Object obj, String fieldName) {
        if(obj instanceof Class)
            throw new IllegalArgumentException("expected object, class given - use getStaticFieldValue for static fields");

        return Optional.ofNullable(getField(obj.getClass(), fieldName)).map(field -> {
            try {
                return field.get(obj);
            } catch (IllegalAccessException ignore) {
                return null;
            }
        }).orElse(null);
    }

    public static Object getStaticFieldValue(Class<?> clazz, String fieldName) {
        return Optional.ofNullable(getField(clazz, fieldName)).map(field -> {
            try {
                return field.get(null);
            } catch (IllegalAccessException ignore) {
                return null;
            }
        }).orElse(null);
    }

    public static Object getFirstFieldByTypeValue(Object obj, Class<?> type) {
        return getFirstFieldByType(obj.getClass(), type).map(field -> {
            try {
                return field.get(obj);
            } catch (IllegalAccessException ignore) {
                return null;
            }
        }).orElse(null);
    }

    public static Object getLastFieldByTypeValue(Object obj, Class<?> type) {
        return getLastFieldByType(obj.getClass(), type).map(field -> {
            try {
                return field.get(obj);
            } catch (IllegalAccessException ignore) {
                return null;
            }
        }).orElse(null);
    }

    public static Class<?>[] types(Class<?>... types) {
        return types;
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... args) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(args);
            constructor.setAccessible(true);

            return constructor;
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();

            return null;
        }
    }

    public static Object newInstance(Class<?> clazz)
            throws NullPointerException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        return newInstance(clazz, new Class<?>[0]);
    }

    public static Object newInstance(Class<?> clazz, Class<?>[] types, Object... args)
            throws NullPointerException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        return Objects.requireNonNull(getConstructor(clazz, types)).newInstance(args);
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... types) {
        try {
            Method method = clazz.getDeclaredMethod(name, types);
            method.setAccessible(true);

            return method;
        } catch (NoSuchMethodException ex) {
            if(clazz.getSuperclass() != null)
                return getMethod(clazz.getSuperclass(), name, types);
            else
                ex.printStackTrace();
        }

        return null;
    }

    public static Object invoke(Object obj, String name)
            throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return invoke(obj, name, new Class<?>[0]);
    }

    public static Object invoke(Object obj, String name, Class<?>[] types, Object... args)
            throws NullPointerException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(obj instanceof Class)
            throw new IllegalArgumentException("expected object, class given - use invokeStatic for static methods");

        return Objects.requireNonNull(getMethod(obj.getClass(), name, types)).invoke(obj, args);
    }

    public static Object invokeNullable(Object obj, String name, Object... args) {
        return invokeNullable(obj, name, new Class<?>[0], args);
    }

    public static Object invokeNullable(Object obj, String name, Class<?>[] types, Object... args) {
        try {
            return invoke(obj, name, types, args);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    }

    public static Object invokeStatic(Class<?> clazz, String name) {
        return invokeStatic(clazz, name, new Class<?>[0]);
    }

    public static Object invokeStatic(Class<?> clazz, String name, Class<?>[] types, Object... args) {
        try {
            Method method = clazz.getDeclaredMethod(name, types);
            method.setAccessible(true);

            return method.invoke(null, args);
        } catch (Exception ignore) {
            return null;
        }
    }

    public static Class<?> findClass(String className) {
        if(CLASS_CACHE.containsKey(className))
            return CLASS_CACHE.get(className);

        try {
            Class<?> clazz = Class.forName(className);

            CLASS_CACHE.put(className, clazz);

            return clazz;
        } catch (ClassNotFoundException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Class '" + className + "' not found: " + ex.getMessage());

            return null;
        }
    }

    public static void debugRecursively(Object obj) {
        boolean isClass = obj instanceof Class;
        Class<?> current = isClass ? (Class<?>) obj : obj.getClass();
        int currentIndent = 0;

        do {
            if(currentIndent > 0)
                System.out.println(" ");

            System.out.println(
                    new StringUtils(" ").repeat(currentIndent)
                    + ((currentIndent == 0) ? "" : "... extends ")
                    + Modifier.toString(current.getModifiers())
                    + " " + current.getName()
            );

            for (Field field : current.getDeclaredFields()) {
                String value = "";

                if(!isClass) {
                    try {
                        value = ": " + field.get(obj);
                    } catch (Exception ignore) {
                    }
                }

                System.out.println(
                        new StringUtils(" ").repeat(currentIndent + 2)
                        + Modifier.toString(field.getModifiers())
                        + " " + field.getType().getName()
                        + " " + field.getName()
                        + value
                );
            }

            if(current.getDeclaredFields().length > 0)
                System.out.println(" ");

            for (Method method : current.getDeclaredMethods()) {
                StringBuilder stringBuilder = new StringBuilder();

                for (Parameter param : method.getParameters()) {
                    if (stringBuilder.length() > 0)
                        stringBuilder.append(", ");

                    stringBuilder.append(param.getType().getName())
                            .append(" ")
                            .append(param.getName());
                }

                System.out.println(
                        new StringUtils(" ").repeat(currentIndent + 2)
                        + Modifier.toString(method.getModifiers())
                        + " " + method.getReturnType().getName()
                        + " " + method.getName()
                        + " (" + stringBuilder + ")"
                );
            }

            currentIndent += 2;
        } while((current = current.getSuperclass()) != null);
    }

    // NMS/CraftBukkit methods

    public static Class<?> getNMSClass(String className) {
        return findClass(
                NMS_VERSION.startsWith("v1_17") || NMS_VERSION.startsWith("v1_18") || NMS_VERSION.startsWith("v1_19")
                        ? "net.minecraft." + className
                        : "net.minecraft.server." + NMS_VERSION + "." + className);
    }

    public static Class<?> getCraftClass(String className) {
        return findClass("org.bukkit.craftbukkit." + NMS_VERSION + "." + className);
    }

    public static void sendPacketNMS(Player player, Object packet) {
        try {
            boolean is1_17 = NMS_VERSION.startsWith("v1_17"),
                    is1_18 = NMS_VERSION.startsWith("v1_18"),
                    is1_19 = NMS_VERSION.startsWith("v1_19");
            Object handle = invoke(player, "getHandle");
            Object playerConnection = Objects.requireNonNull(getField(
                    handle.getClass(),
                    (is1_17 || is1_18 || is1_19) ? "b" : "playerConnection"
            )).get(handle);

            // Send packet to player connection
            invoke(
                    playerConnection,
                    is1_18 || is1_19
                            ? "a"
                            : "sendPacket",
                    types(getNMSClass(is1_17 || is1_18 || is1_19 ? "network.protocol.Packet" : "Packet")),
                    packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
