package me.cungu.beanmatertest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClassB {

	@Autowired
	private ClassC classC;

	public String m1(String p1, String p2) {
		System.out.println("ClassB m1");
		
		classC.m1(p1, p2);
		
		return "b";
	}

}
