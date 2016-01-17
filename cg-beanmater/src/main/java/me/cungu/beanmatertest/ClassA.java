package me.cungu.beanmatertest;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import me.cungu.beanmater.Flag;

@Service
public class ClassA {

	@Resource
	private ClassB classB;
	
	@Flag("flag_aaa")
	private String aaa;

	public String m1(String p1, String p2) {
		System.out.println("ClassA m1 " + aaa);

		classB.m1(p1, p2);

		return "a";
	}

	public String getAaa() {
		return aaa;
	}

	public void setAaa(String aaa) {
		this.aaa = aaa;
	}
	
}