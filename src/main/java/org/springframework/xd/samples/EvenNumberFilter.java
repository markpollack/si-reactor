package org.springframework.xd.samples;

import java.util.concurrent.CountDownLatch;

import org.springframework.integration.annotation.Filter;

public class EvenNumberFilter {

	
	CountDownLatch latch;
	
	@Filter
	public boolean evenOnly(String input) {
		latch.countDown();
		int number = Integer.parseInt(input);
		return ((number % 2) == 0);
	}

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

}
