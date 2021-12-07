package net.dev.eazynick.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.bukkit.Bukkit;

public class ReflectionHelper {

	public void setField(Class<?> clazz, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException {
		Field f = getField(clazz, fieldName);
		
		f.set(clazz, value);
		f.setAccessible(false);
	}
	
	public void setField(Object obj, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException {
		Field f = getField(obj.getClass(), fieldName);
		
		f.set(obj, value);
		f.setAccessible(false);
	}
	
	public Field getFirstFieldByType(Class<?> clazz, Class<?> type) {
		for(Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			
			if(field.getType() == type)
				return field;
		}
		
		return null;
	}
	
	public Class<?> getSubClass(Class<?> clazz, String className) {
		for(Class<?> subClazz : clazz.getDeclaredClasses()) {
			if(subClazz.getSimpleName().equals(className))
				return subClazz;
		}
		
		return null;
	}
	
	public Field getField(Class<?> clazz, String fieldName) {
		try {
			Field f = clazz.getDeclaredField(fieldName);
			f.setAccessible(true);
			
			return f;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException e) {
			e.printStackTrace();
			
			return null;
		}
	}

	public Class<?> getNMSClass(String className) {
		try {
			String version = getVersion();
			
			return Class.forName((version.startsWith("v1_17") || version.startsWith("v1_18")) ? ("net.minecraft." + className) : ("net.minecraft.server." + version + "." + className));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Class<?> getCraftClass(String className) {
		try {
			return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}
	
	public boolean isNewVersion() {
		return (Integer.parseInt(getVersion().substring(1).split("_")[1]) > 12);
	}
	
	/* Only for debugging purposes */
	
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
