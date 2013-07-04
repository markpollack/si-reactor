package org.springframework.xd.samples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import reactor.core.Reactor;
import reactor.fn.Consumer;
import reactor.fn.Event;
import reactor.fn.selector.Selector;
import reactor.fn.tuples.Tuple2;

import java.util.concurrent.Executor;

import static reactor.fn.Functions.$;

/**
 * A {@link TaskExecutor} implementation that uses a {@link Reactor} to dispatch and execute tasks.
 *
 * @author Jon Brisbin
 */
public class ReactorTaskExecutor implements TaskExecutor, Executor {

	private final Tuple2<Selector, Object> exec = $();
	private final Reactor reactor;

	@Autowired
	public ReactorTaskExecutor(Reactor reactor) {
		this.reactor = reactor;

		this.reactor.on(exec.getT1(), new Consumer<Event<Runnable>>() {
			@Override
			public void accept(Event<Runnable> ev) {
				ev.getData().run();
			}
		});
	}

	@Override
	public void execute(Runnable task) {
		reactor.notify(exec.getT2(), Event.wrap(task));
	}

}