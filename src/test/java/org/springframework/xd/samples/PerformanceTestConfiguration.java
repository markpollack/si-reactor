package org.springframework.xd.samples;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

import reactor.R;
import reactor.core.Reactor;
import reactor.fn.dispatch.Dispatcher;
import reactor.fn.dispatch.RingBufferDispatcher;
import reactor.fn.dispatch.ThreadPoolExecutorDispatcher;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

@Configuration
public class PerformanceTestConfiguration {

	
	@Bean 
	public TaskExecutor reactorRingBufferTaskExecutor() {
		Dispatcher dispatcher = new RingBufferDispatcher("rbd", 1024, ProducerType.MULTI, new BlockingWaitStrategy());
		Reactor reactor = R.reactor().using(dispatcher).get();
		return new ReactorTaskExecutor(reactor);
	}
	
	@Bean
	public TaskExecutor reactorFixedThreadPoolTaskExecutor() {		
		Dispatcher dispatcher = new ThreadPoolExecutorDispatcher(2, 1024);
		Reactor reactor = R.reactor().using(dispatcher).get();
		return new ReactorTaskExecutor(reactor);
	}
	

}
