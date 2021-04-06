package net.dev.eazynick.nms;

import java.lang.reflect.Field;

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
	
	public Field getField(Class<?> clazz, String fieldName) {
		try {
			Field f = clazz.getDeclaredField(fieldName);
			f.setAccessible(true);
			
			return f;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException ex) {
			ex.printStackTrace();
			
			return null;
		}
	}

	public Class<?> getNMSClass(String className) {
		try {
			return Class.forName("net.minecraft.server." + getVersion() + "." + className);
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

	public void debugClass(Object msg) {
		Class<?> clazz = msg.getClass();
		
		System.out.println();
		System.out.println("Class info of \"" + clazz.getSimpleName() + "\":");
		
		for (Field f : clazz.getDeclaredFields()) {
			f.setAccessible(true);
			
			try {
				System.out.println("--> " + f.getType().getSimpleName() + " " + f.getName() + ": " + f.get(msg));
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				System.out.println("--> " + f.getType().getSimpleName() + " " + f.getName() + ": ERROR (" + ex.getMessage() + ")");
			}
		}
	}
	
}
