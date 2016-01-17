package me.cungu.beanmater.model;

import java.io.Serializable;

public class FlagAttribute implements Serializable {

	private String beanName;
	
	private String flagName;
	
	private Attribute attribute;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getFlagName() {
		return flagName;
	}

	public void setFlagName(String flagName) {
		this.flagName = flagName;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
}