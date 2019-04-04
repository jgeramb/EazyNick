package net.dev.nickplugin.utils;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;

public class ReflectUtils {

	public static void setField(Class<?> clazz, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException {
		Field f = getField(clazz, fieldName);
		
		f.set(clazz, value);
		f.setAccessible(false);
	}
	
	public static void setField(Object obj, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException {
		Field f = getField(obj.getClass(), fieldName);
		
		f.set(obj, value);
		f.setAccessible(false);
	}
	
	public static Field getField(Class<?> clazz, String fieldName) {
		Field f = null;
		
		try {
			f = clazz.getDeclaredField(fieldName);
			
			f.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return f;
	}

	public static Class<?> getNMSClass(String className) {
		try {
			return Class.forName("net.minecraft.server." + getVersion() + "." + className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Class<?> getCraftClass(String className) {
		try {
			return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}
	
}
