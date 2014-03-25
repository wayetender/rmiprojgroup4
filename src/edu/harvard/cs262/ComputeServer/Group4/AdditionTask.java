package edu.harvard.cs262.ComputeServer.Group4;

import java.io.Serializable;

import edu.harvard.cs262.ComputeServer.WorkTask;

/**
 * An AdditionTask is a simple task that adds two integers. To simulate a
 * computation-intensive task, it will sleep for 2 seconds.
 * 
 * @author Group 4: Lucas Waye, Christopher Mueller, George Wu, Kat Zhou
 * 
 */
public class AdditionTask implements WorkTask, Serializable {

	private static final long serialVersionUID = 1L;
	private final Integer a, b;

	/**
	 * Constructor
	 * 
	 * @param a
	 *            first number to add
	 * @param b
	 *            second number to add
	 */
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
