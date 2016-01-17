package me.cungu.beanmater.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Bean implements Serializable {
	
	private String clazz;
	
	private String modifiers;
	
	private String superclass;
	
	private String[] interfaces;
	
	private String proxyType;
	
	private String name;
	
	private List<Attribute> attributeList = new ArrayList<Attribute>();
	
	private List<Operation> operationList = new ArrayList<Operation>();
	
//	private String signature;
	
	public void addAttribute(Attribute attribute) {
		attributeList.add(attribute);
	}
	
	public void addOperation(Operation operation) {
		operationList.add(operation);
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	
	public String getModifiers() {
		return modifiers;
	}

	public void setModifiers(String modifiers) {
		this.modifiers = modifiers;
	}
	
	public String getSuperclass() {
		return superclass;
	}

	public void setSuperclass(String superclass) {
		this.superclass = superclass;
	}

	public String[] getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(String[] interfaces) {
		this.interfaces = interfaces;
	}

	public String getProxyType() {
		return proxyType;
	}

	public void setProxyType(String proxyType) {
		this.proxyType = proxyType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Attribute> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<Attribute> attributeList) {
		this.attributeList = attributeList;
	}

	public List<Operation> getOperationList() {
		return operationList;
	}

	public void setOperationList(List<Operation> operationList) {
		this.operationList = operationList;
	}

//	public String getSignature() {
//		return signature;
//	}
//
//	public void setSignature(String signature) {
//		this.signature = signature;
//	}
}