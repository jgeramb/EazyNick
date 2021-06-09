package net.dev.eazynick.utilities;

import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncTask {

	private AsyncRunnable asyncRunnable;
	private long delay, period;
	private AtomicBoolean running;
	
	public AsyncTask(AsyncRunnable asyncRunnable, long delay) {
		this(asyncRunnable, delay, -1);
	}
	
	public AsyncTask(AsyncRunnable asyncRunnable, long delay, long period) {
		this.asyncRunnable = asyncRunnable;
		this.delay = delay;
		this.period = period;
		this.running = new AtomicBoolean(period >= 0);
	}

	public AsyncTask run() {
		asyncRunnable.prepare(this);
		
		new Thread(() -> {
			//Wait 'delay' milliseconds
			try {
				Thread.sleep(delay);
			} catch (InterruptedException ex) {
			}
			
			do {
				asyncRunnable.run();
				
				//Check if task should be executed again
				if(period >= 0) {
					//Wait 'period' milliseconds
					try {
						Thread.sleep(period);
					} catch (InterruptedException ex) {
					}
				}
			} while(running.get());
			
			Thread.currentThread().interrupt();
		}).start();
		
		return this;
	}
	
	public AsyncTask cancel() {
		running.set(false);
		
		return this;
	}
	
	public static abstract class AsyncRunnable {
		
		private AsyncTask asyncTask;
		
		public void prepare(AsyncTask asyncTask) {
			this.asyncTask = asyncTask;
		}
		
		public abstract void run();
		
		public void cancel() {
			if(asyncTask != null)
				asyncTask.cancel();
			else
				throw new UnsupportedOperationException("Not running");
		}
		
	}
	
}
