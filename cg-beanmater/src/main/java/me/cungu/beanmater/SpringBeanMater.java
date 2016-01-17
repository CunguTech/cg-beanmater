package me.cungu.beanmater;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.MethodCallback;

import me.cungu.beanmater.model.Attribute;
import me.cungu.beanmater.model.Bean;
import me.cungu.beanmater.model.FlagAttribute;
import me.cungu.beanmater.model.Operation;
import me.cungu.beanmater.util.JsonUtil;
import me.cungu.beanmater.util.ObjectUtil;
import me.cungu.beanmater.util.ReflectUtil;
import me.cungu.beanmater.util.SpringUtil;

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
		
		LOG.debug("get beans {}, {}", beans.size(), beans);
		
		return beans;
	}
	
	public Bean getBean(String beanName) {
		Assert.notNull(beanName);
		
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
			
			LOG.debug("get bean {}, {}", beanName, bean);
			
			return bean;
		}
		
		return null;
	}
	
	public void setFieldValue(String beanName, final String fieldSignature, Object value) {
		Assert.notNull(beanName);
		Assert.notNull(fieldSignature);
		
		Object object = applicationContext.getBean(beanName);
		final Class<?> targetClazz = AopUtils.getTargetClass(object);
		final List<Field> fields = new ArrayList<Field>();
		ReflectionUtils.doWithFields(targetClazz, new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				if (field.toGenericString().equals(fieldSignature)) {
					fields.add(field); // break;
				}
			}
		});
		if (fields == null || fields.size() == 0) {
			throw new NoSuchFieldError("no such field signature -> " + fieldSignature);
		}
		
		Field field = fields.get(0);
		ReflectionUtils.makeAccessible(field);
		ReflectionUtils.setField(field, object, value);
		
		LOG.info("set field {}, {} = {}", beanName, fieldSignature, value);
	}
	
	public Object invokeMethod(String beanName, final String methodSignature, Object[] values) {
		Assert.notNull(beanName, "beanName must not be null");
		Assert.notNull(methodSignature, "methodSignature must not be null");
		
		Object object = applicationContext.getBean(beanName);
		final List<Method> methods = new ArrayList<Method>();
		final Class<?> targetClazz = AopUtils.getTargetClass(object);
		ReflectionUtils.doWithMethods(targetClazz, new MethodCallback() {
			@Override
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				if (method.toGenericString().equals(methodSignature)) {
					methods.add(method); // break;
				}
			}
		});
		
		if (methods == null || methods.size() == 0) {
			throw new NoSuchMethodError("no such method signature -> " + methodSignature);
		}
		
		Method method = methods.get(0);
		ReflectionUtils.makeAccessible(method);
		Object returnValue = ReflectionUtils.invokeMethod(method, object, values);
		
		LOG.info("invoke method {}, {} = {}", beanName, methodSignature, values);
		
		return returnValue;
	}
	
	public List<FlagAttribute> getFlagAttributes() {
		final List<FlagAttribute> flagAttributes = new ArrayList<FlagAttribute>();
		
		String[] names = applicationContext.getBeanDefinitionNames();
		for (String name : names) {
			final String beanName = name;
			Object object = applicationContext.getBean(beanName);
			final Class<?> targetClazz = AopUtils.getTargetClass(object);
			ReflectionUtils.doWithFields(targetClazz, new FieldCallback() {
				@Override
				public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
					Flag flagAnnotation = field.getAnnotation(Flag.class);
					if (flagAnnotation != null) {
						String flagName = flagAnnotation.value();
						
						FlagAttribute flagAttribute = new FlagAttribute();
						flagAttribute.setBeanName(beanName);
						flagAttribute.setFlagName(flagName);
						flagAttribute.setAttribute(ObjectUtil.toAttribute(field));
						flagAttributes.add(flagAttribute);
					}
				}
			});
		}
		
		LOG.debug("get flagAttributes {}, {}", flagAttributes.size(), flagAttributes);
		
		return flagAttributes;
	}
	
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
		
//		ClassA a = (ClassA) ctx.getBean("classA");
//		System.out.println(a.getAaa());
//		bm.setFieldValue("classA", "private java.lang.String me.cungu.beanmatertest.ClassA.aaa", "abc");
//		System.out.println(a.getAaa());
//		
//		bm.invokeMethod("classA", "public java.lang.String me.cungu.beanmatertest.ClassA.m1(java.lang.String,java.lang.String)", new Object[] {
//			"a",
//			"b"
//		});
		
		for (FlagAttribute flagAttribute : bm.getFlagAttributes()) {
			System.out.println(JsonUtil.toPrettyJson(flagAttribute));
		}
		
		
//		System.out.println(JsonUtil.toPrettyJson(bm.getBean("classA")));
	}
}