/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.xd.samples;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.Matchers.is;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * 
 */
@ContextConfiguration	// default context name is <ClassName>-context.xml
@RunWith(SpringJUnit4ClassRunner.class)
public class PerformanceTests {
	
	@Autowired
	MessageChannel inputChannel;
	
	@Autowired
	QueueChannel testChannel;
	
	@Autowired
	EvenNumberFilter filter;

	AtomicLong count = new AtomicLong();
	AtomicLong start = new AtomicLong();
	AtomicLong end = new AtomicLong();
	CountDownLatch latch;
	
	final int msgs = 2000000;
	
	@Before
	public void setLatch() {
		latch = new CountDownLatch(msgs);
		//not sure why lifecycle isn't being called
		if (inputChannel instanceof RingBufferChannel) {
			((RingBufferChannel)inputChannel).onInit();
			((RingBufferChannel)inputChannel).start();
		}
	}
	
	@After
	public void stopChannel() {
		if (inputChannel instanceof RingBufferChannel) {
			((RingBufferChannel)inputChannel).stop();
		}
	}
	
	//@Test
	public void evenTest() {
		assertTrue(filter.evenOnly("2"));
		assertFalse(filter.evenOnly("3"));
	}
	
	@Test
	public void testPerf() throws InterruptedException {
		filter.setLatch(latch);
		
		start.set(System.currentTimeMillis());
		for (int i = 0; i < msgs; i++) {
			inputChannel.send(MessageBuilder.withPayload(Integer.toString(i)).build());			
		}
		
		assertTrue(latch.await(60, TimeUnit.SECONDS));
		end.set(System.currentTimeMillis());
		
		assertThat("latch was counted down", latch.getCount(), is(0L));

		double elapsed = (end.get() - start.get()) * 1.0;
		System.out.println("elapsed: " + (int) elapsed + "ms");
		System.out.println("throughput: " + (int) ((msgs) / (elapsed / 1000)) + "/sec");
	}
	


}
