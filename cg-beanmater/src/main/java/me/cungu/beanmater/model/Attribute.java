package me.cungu.beanmater.model;

import java.io.Serializable;

public class Attribute implements Serializable {
	
	private String declaringClass;
	
	private String type;
	
	private String modifiers;
	
	private boolean isFinal;
	
	private String name;
	
	private Object value;
	
	private String valueRef;
	
	private String signature;
	
	public String getDeclaringClass() {
		return declaringClass;
	}

	public void setDeclaringClass(String declaringClass) {
		this.declaringClass = declaringClass;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getModifiers() {
		return modifiers;
	}

	public void setModifiers(String modifiers) {
		this.modifiers = modifiers;
	}
	
	public boolean isFinal() {
		return isFinal;
	}

	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public String getValueRef() {
		return valueRef;
	}

	public void setValueRef(String valueRef) {
		this.valueRef = valueRef;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}