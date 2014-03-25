package edu.harvard.cs262.ComputeServer.Group4;

import java.io.Serializable;

import edu.harvard.cs262.ComputeServer.WorkTask;

public class AdditionTask implements WorkTask, Serializable {

	private static final long serialVersionUID = 1L;
	private final Integer a, b;
	
	public AdditionTask(Integer a, Integer b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public Object doWork() {
		System.out.println("Adding " + a + " + " + b + " -- very slow");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		return a + b;
	}
	
	
	
}
