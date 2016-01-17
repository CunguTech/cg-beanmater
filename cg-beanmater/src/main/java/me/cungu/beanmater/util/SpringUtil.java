package me.cungu.beanmater.util;

import java.lang.reflect.Field;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringUtil {
	
	/**
	 * 
	 * @param applicationContext
	 * @param beanName
	 * @param field
	 * @return
	 * 
	 * @seee org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition
	 * @seee org.springframework.context.annotation.ScannedGenericBeanDefinition
	 * @seee org.springframework.beans.factory.support.GenericBeanDefinition
	 * @seee org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
	 * @seee org.springframework.beans.factory.config.BeanDefinition
	 */
	public static String getRefBeanName(ApplicationContext applicationContext, String beanName, Field field) {
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)( (ConfigurableApplicationContext) applicationContext).getBeanFactory();
		
		String refBeanName = null;
		
		Resource resourceAnnotation = field.getAnnotation(Resource.class);
		if (resourceAnnotation != null) {
			refBeanName = resourceAnnotation.name();
			if (StringUtils.isBlank(refBeanName)) {
				String[] beanNamesForType = beanFactory.getBeanNamesForType(field.getType());
				refBeanName = ((beanNamesForType != null && beanNamesForType.length > 0) ? beanNamesForType[0] : null);
			}
		}
		if (StringUtils.isBlank(refBeanName)) {
			Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
			if (autowiredAnnotation != null) {
				Qualifier qualifierAnnotation = field.getAnnotation(Qualifier.class);
				if (qualifierAnnotation != null) {
					refBeanName = qualifierAnnotation.value();
				} else {
					String[] beanNamesForType = beanFactory.getBeanNamesForType(field.getType());
					refBeanName = ((beanNamesForType != null && beanNamesForType.length > 0) ? beanNamesForType[0] : null);
				}
			}
		}
		if (StringUtils.isBlank(refBeanName)) {
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
			PropertyValue propertyValue = beanDefinition.getPropertyValues().getPropertyValue(field.getName());
			if (propertyValue != null) {
				Object value = propertyValue.getValue();
				if (value instanceof RuntimeBeanReference) {
					RuntimeBeanReference ref = (RuntimeBeanReference) value;
					refBeanName = ref.getBeanName();
				}
			}
		}
		
		return refBeanName;
	}
	
	public static String getProxyType(Object object) {
		if (AopUtils.isJdkDynamicProxy(object)) {
			return "JDK_DYNAMIC_PROXY";
		} else if (AopUtils.isCglibProxy(object)) {
			return "CGLIB";
		}
		return null;
	}
}
