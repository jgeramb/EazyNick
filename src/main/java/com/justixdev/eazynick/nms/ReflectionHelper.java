package com.justixdev.eazynick.nms;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class ReflectionHelper {

	public void setField(Object obj, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException {
		Field field = getField(obj.getClass(), fieldName);
		field.set(obj, value);
		field.setAccessible(false);
	}
	
	public Optional<Field> getFirstFieldByType(Class<?> clazz, Class<?> type) {
		return Stream.of(clazz.getDeclaredFields()).filter(field -> {
			field.setAccessible(true);

			return field.getType().equals(type);
		}).findFirst();
	}
	
	public Optional<Class<?>> getSubClass(Class<?> clazz, String className) {
		return Stream.of(clazz.getDeclaredClasses())
				.filter(subClazz -> subClazz.getSimpleName().equals(className))
				.findFirst();
	}
	
	public Field getField(Class<?> clazz, String fieldName) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			
			return field;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException ex) {
			ex.printStackTrace();
			
			return null;
		}
	}

	public Class<?> getNMSClass(String className) {
		try {
			String version = getVersion();
			
			return Class.forName(
					(version.startsWith("v1_17") || version.startsWith("v1_18") || version.startsWith("v1_19"))
							? ("net.minecraft." + className)
							: ("net.minecraft.server." + version + "." + className)
			);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public Class<?> getCraftClass(String className) {
		try {
			return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + className);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}

	/* Debug Functions */
	
	public void debugObject(Object obj) {
		Class<?> clazz = obj.getClass();
		
		System.out.println();
		System.out.println("Class info of \"" + clazz.getSimpleName() + "\":");
		System.out.println("» Constructors:");
		
		for (Constructor<?> currentConstructor : clazz.getDeclaredConstructors())
			System.out.println("--> " + Arrays.toString(currentConstructor.getParameterTypes()));
		
		System.out.println("» Fields:");
		
		for (Field currentField : clazz.getDeclaredFields()) {
			try {
				currentField.setAccessible(true);
				
				System.out.println("--> " + currentField.getType().getSimpleName() + " " + currentField.getName() + ": " + currentField.get(obj));
			} catch (Exception ex) {
				System.out.println("--> " + currentField.getType().getSimpleName() + " " + currentField.getName() + ": ERROR (" + ex.getMessage() + ")");
			}
		}
	}
	
	public void debugClass(Class<?> clazz) {
		System.out.println();
		System.out.println("Class info of \"" + clazz.getSimpleName() + "\":");
		System.out.println("» Constructors:");
		
		for (Constructor<?> currentConstructor : clazz.getDeclaredConstructors())
			System.out.println("--> " + Arrays.toString(currentConstructor.getParameterTypes()));
		
		System.out.println("» Fields:");
		
		for (Field currentField : clazz.getDeclaredFields()) {
			try {
				currentField.setAccessible(true);
				
				System.out.println("--> " + currentField.getType().getSimpleName() + " " + currentField.getName());
			} catch (Exception ex) {
				System.out.println("--> " + currentField.getType().getSimpleName() + " " + currentField.getName() + ": ERROR (" + ex.getMessage() + ")");
			}
		}
	}
	
}
