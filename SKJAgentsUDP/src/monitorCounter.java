import java.io.IOException;
import java.util.Random;

public class monitorCounter implements Runnable {
	
	private long currentTime;
	private Monitor countingMonitor;
	private long quant;
	Random rand = new Random();
	boolean stop = false;
	
	public void stop() {
		stop = true;
	}
	public monitorCounter(Monitor countingMonitor) {
		this.currentTime = System.currentTimeMillis();
		this.countingMonitor = countingMonitor;
		this.quant = 4000;
	}
	
	public long getTime() {
		return System.currentTimeMillis() - this.currentTime;
	}

	@Override
	public void run() {
		while(!stop) {
			try {
				Thread.sleep(quant);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				try {
					countingMonitor.getTime();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
}
