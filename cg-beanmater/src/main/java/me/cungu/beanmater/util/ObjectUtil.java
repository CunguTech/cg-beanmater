package me.cungu.beanmater.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.aop.support.AopUtils;
import org.springframework.util.ReflectionUtils;

import me.cungu.beanmater.model.Attribute;
import me.cungu.beanmater.model.Bean;
import me.cungu.beanmater.model.Operation;

public class ObjectUtil {
	
	public static Bean toBean(Object object) {
		Class<?> clazz = AopUtils.getTargetClass(object);
		
		Bean bean = new Bean();
		bean.setClazz(clazz.getName());
		bean.setModifiers(Modifier.toString(clazz.getModifiers()));
		bean.setSuperclass(clazz.getSuperclass().getName());
		bean.setInterfaces(ReflectUtil.toClassNames(clazz.getInterfaces()));
		bean.setProxyType(SpringUtil.getProxyType(object));
		
		return bean;
	}
	
	public static Attribute toAttribute(Field field) {
		ReflectionUtils.makeAccessible(field);
		
		Attribute attribute = new Attribute();
		attribute.setDeclaringClass(field.getDeclaringClass().getName());
		attribute.setName(field.getName());
		attribute.setModifiers(Modifier.toString(field.getModifiers()));
		attribute.setType(field.getType().getName());
		attribute.setSignature(field.toGenericString());
		
		return attribute;
	}
	
	public static Operation toOperation(Method method) {
		ReflectionUtils.makeAccessible(method);
		
		Operation operation = new Operation();
		operation.setDeclaringClass(method.getDeclaringClass().getName());
		operation.setName(method.getName());
		operation.setModifiers(Modifier.toString(method.getModifiers()));
		operation.setParameterTypes(ReflectUtil.toClassNames(method.getParameterTypes()));
		operation.setReturnType(method.getReturnType().getName());
		operation.setSignature(method.toGenericString());
		
		return operation;
	}
	
}