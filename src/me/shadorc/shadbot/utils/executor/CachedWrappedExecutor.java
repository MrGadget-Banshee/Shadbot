package me.shadorc.shadbot.utils.executor;

import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.shadorc.shadbot.utils.Utils;
import me.shadorc.shadbot.utils.embed.log.LogUtils;

public class CachedWrappedExecutor extends ThreadPoolExecutor {

	/**
	 * A default cached thread pool with tasks wrapped to catch {@link Exception}
	 * 
	 * @param threadName - the thread name
	 */
	public CachedWrappedExecutor(String threadName) {
		super(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), Utils.createDaemonThreadFactory(threadName));
	}

	@Override
	public void execute(Runnable command) {
		super.execute(this.wrapRunnable(command));
	}

	@Override
	public Future<?> submit(Runnable task) {
		return super.submit(this.wrapRunnable(task));
	}

	private Runnable wrapRunnable(Runnable command) {
		return new Runnable() {
			@Override
			public void run() {
				try {
					command.run();
				} catch (Exception err) {
					LogUtils.error(err, String.format("{%s} An unknown exception occurred while running a task.",
							CachedWrappedExecutor.class.getSimpleName()));
					throw new RuntimeException(err);
				}
			}
		};
	}

}