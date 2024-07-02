package com.example.demo.service;

public class MyCustomException extends RuntimeException {
	public MyCustomException(Throwable msg) {
		super(msg);
		System.out.println("Exception Occured"+msg.getCause());
	}
}
