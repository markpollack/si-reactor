package org.springframework.xd.samples;

import org.junit.Test;

import reactor.Fn;
import reactor.R;
import reactor.core.Reactor;
import reactor.core.Environment;
import reactor.fn.Consumer;
import reactor.fn.Event;
import reactor.fn.dispatch.SynchronousDispatcher;
import reactor.fn.selector.ObjectSelector;


public class ReactorTests {

	@Test
	public void simpleTest() throws InterruptedException {
		Reactor reactor = R.reactor().using(new SynchronousDispatcher()).get();
		
		reactor.on(new ObjectSelector<String>("echo"), new Consumer<Event<String>>() {
			@Override
			public void accept(Event<String> event) {
				System.out.println("Received event with data " + event.getData() );
			}
			
		});
		
		reactor.notify("echo", new Event<String>("hello world"));
		Thread.sleep(1000);
	}
}
