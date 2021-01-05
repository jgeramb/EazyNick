package net.dev.eazynick.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.bukkit.Bukkit;

public class ReflectUtils {

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
	
	public Field getField(Class<?> clazz, String fieldName) {
		try {
			Field f = clazz.getDeclaredField(fieldName);
			Field modifiers = f.getClass().getDeclaredField("modifiers");
			f.setAccessible(true);
			modifiers.setAccessible(true);
			modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);
			
			return f;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			
			return null;
		}
	}

	public Class<?> getNMSClass(String className) {
		try {
			return Class.forName("net.minecraft.server." + getVersion() + "." + className);
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
	
}
