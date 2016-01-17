package me.cungu.beanmater;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import me.cungu.beanmater.model.Attribute;
import me.cungu.beanmater.model.Bean;
import me.cungu.beanmater.model.Operation;
import me.cungu.beanmater.util.ObjectUtil;
import me.cungu.beanmater.util.ReflectUtil;
import me.cungu.beanmater.util.SpringUtil;
import me.cungu.beanmatertest.ClassA;

/**
 * 
 * @author fuhaining
 * 
 * @TODO add filter
 *
 */
public class SpringBeanMater implements ApplicationContextAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(SpringBeanMater.class);
	
	private ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public List<Bean> getBeans() {
		List<Bean> beans = new ArrayList<Bean>();
		
		String[] names = applicationContext.getBeanDefinitionNames();
		for (String name : names) {
			Object object = applicationContext.getBean(name);
			
			Bean bean = ObjectUtil.toBean(object);
			bean.setName(name);
			
			beans.add(bean);
		}
		
		return beans;
	}
	
	public Bean getBean(String beanName) {
		if (applicationContext.containsBean(beanName)) {
			Object object = applicationContext.getBean(beanName);
			Class<?> targetClazz = AopUtils.getTargetClass(object);
			
			Bean bean = ObjectUtil.toBean(object);
			bean.setName(beanName);
			
			for (Field field : ReflectUtil.getAllDeclaredFields(targetClazz)) {
				Attribute attribute = ObjectUtil.toAttribute(field);
				if (attribute != null) {
					Object value = ReflectionUtils.getField(field, object);
					boolean isSimpleValueType = ReflectUtil.isSimpleValueType(field.getType());
					if (isSimpleValueType) {
						attribute.setValue(value);
					} else {
						if (value != null) {
							attribute.setValue(((value instanceof Class) ? (Class<?>) value : value.getClass()).getName());
						}
					}
					attribute.setFinal(Modifier.isFinal(field.getModifiers()) || !isSimpleValueType);
					attribute.setValueRef(SpringUtil.getRefBeanName(applicationContext, beanName, field));
					
					bean.addAttribute(attribute);
				}
			}
			
			for (Method method : ReflectUtil.getAllDeclaredMethods(targetClazz)) {
				Operation operation = ObjectUtil.toOperation(method);
				if (operation != null) {
					bean.addOperation(operation);
				}
			}
			
			return bean;
		}
		
		return null;
	}
	
	public void setFieldValue(String beanName, final String fieldName, final String fieldDeclaringClass, Object value) {
		Object target = applicationContext.getBean(beanName);
		final Class<?> targetClazz = AopUtils.getTargetClass(target);
		final List<Field> fields = new ArrayList<Field>();
		ReflectionUtils.doWithFields(targetClazz, new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				String equalsClazz = StringUtils.isNotBlank(fieldDeclaringClass) ? fieldDeclaringClass : targetClazz.getName();
				if (field.getDeclaringClass().getName().equals(equalsClazz) 
						&& field.getName().equals(fieldName)) {
					fields.add(field);
				}
			}
		});
		if (fields == null || fields.size() == 0) {
			throw new NoSuchFieldError("no such field " + fieldName);
		}
		
		Field field = fields.get(0);
		ReflectionUtils.makeAccessible(field);
		ReflectionUtils.setField(field, target, value);
	}
	
//	public Object invokeMethod(String beanName, String methodName, String methodDeclaringClass, )
	
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("ac1.xml");
		SpringBeanMater bm = ctx.getBean(SpringBeanMater.class);
		
//		for (Bean bean : bm.getBeans()) {
//			Bean detail = bm.getBean(bean.getName());
//			try {
//			System.out.println(JsonUtil.toPrettyJson(detail));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			System.out.println("-----------------------");
//		}
		
		ClassA a = (ClassA) ctx.getBean("classA");
		System.out.println(a.getAaa());
		
		bm.setFieldValue("classA", "aaa", null, "abc");
		
		System.out.println(a.getAaa());
		
		
//		System.out.println(JsonUtil.toPrettyJson(bm.getBean("classA")));
	}
}