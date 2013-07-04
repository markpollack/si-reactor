package org.springframework.xd.samples;

import java.io.IOException;

import com.lmax.disruptor.dsl.ProducerType;

import reactor.Fn;
import reactor.R;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.fn.Consumer;
import reactor.fn.Event;
import reactor.fn.dispatch.Dispatcher;
import reactor.fn.dispatch.RingBufferDispatcher;
import reactor.fn.dispatch.SynchronousDispatcher;
import reactor.fn.selector.ObjectSelector;

import com.lmax.disruptor.BlockingWaitStrategy;

/**
 * a reactor itself is nothing more than a container for few things:
 * - a dedicated event space (with its own topics)
 * - an associated dispatcher (we provide 3 types)
 * - an associated balancing strategy (pub/sub, round robin or random)
 * - a dedicated converter/serializer
 * @author mpollack
 *
 */
public class ReactorHelloWorld {

	//Step 2 - play with other dispatchers
	//Reactor reactor = R.reactor().sync().get(); // uses SynchronousDispatcher
	//Reactor reactor = R.reactor().dispatcher(Environment.RING_BUFFER).get(); // uses ThreadPoolExecutorDispatcher
	//Reactor reactor = R.reactor().ringBuffer().get(); // users RingBufferDispatcher
	//Reactor reactor = R.reactor().eventLoop().get(); // users BlockingQueueDispatcher
	
	//Step 3 - more detailed configuration of a Dispatcher (pass in some ctor args)
	
	public static void main(String[] args) throws IOException {

		//Step 1
		//Reactor reactor = R.reactor().get();  //uses BlockingQueueDispatcher by default and 'NONE' for load balancing

		//Step 2 - configure to use ring buffer
		Dispatcher dispatcher = new RingBufferDispatcher("rbd", 1024, ProducerType.MULTI, new BlockingWaitStrategy());
		Reactor reactor = R.reactor().using(dispatcher).get();

		reactor.on(new ObjectSelector<String>("echoTopic"), new Consumer<Event<String>>() {
			@Override
			public void accept(Event<String> event) {
				System.out.println("Received event with data " + event.getData() );
			}
			
		});
		
		reactor.notify("echoTopic", new Event<String>("hello world"));
		
		System.out.println("Hit enter to exit");
		System.in.read();
		
		dispatcher.shutdown();
	}

}
