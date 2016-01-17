package me.cungu.beanmater.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

public class ReflectUtil {
	
	public static String[] toClassNames(Class<?>[] classes) {
		if (classes == null) {
			return null;
		}
		
		String[] classNames = new String[classes.length];
		for (int i = 0; i < classes.length; i++) {
			classNames[i] = classes[i].getName();
		}
		
		return classNames;
	}
	
	public static Method[] getAllDeclaredMethods(Class<?> leafClass) {
		return ReflectionUtils.getAllDeclaredMethods(leafClass);
	}
	
	public static Field[] getAllDeclaredFields(Class<?> leafClass) {
		final List<Field> fields = new ArrayList<Field>(32);
		ReflectionUtils.doWithFields(leafClass, new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				fields.add(field);
			}
		});
		
		return fields.toArray(new Field[fields.size()]);
	}
	
	public static boolean isSimpleValueType(Class<?> clazz) {
		return ClassUtils.isPrimitiveOrWrapper(clazz) || clazz.isEnum() ||
				CharSequence.class.isAssignableFrom(clazz) ||
				Number.class.isAssignableFrom(clazz) ||
				Date.class.isAssignableFrom(clazz) ||
				clazz.equals(URI.class) || clazz.equals(URL.class) || clazz.equals(Locale.class) || 
				(clazz.isArray() && isSimpleValueType(clazz.getComponentType())) || 
				Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz);
	}
	
}
