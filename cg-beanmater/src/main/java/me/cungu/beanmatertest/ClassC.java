package me.cungu.beanmatertest;

import java.util.Random;

import org.springframework.stereotype.Service;

public class ClassC {
	
	public static boolean exception = false; 
	
	public String m1(String p1, String p2) {
		System.out.println("ClassC m1");
		
		
		return "c";
	}
}
