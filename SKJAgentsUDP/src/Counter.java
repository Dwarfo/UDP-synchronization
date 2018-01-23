import java.io.IOException;
import java.util.Random;

public class Counter implements Runnable {

		private long currentTime;
		private Agent countingAgent;
		private long quant;
		Random rand = new Random();
		boolean stop = false;
		
		public void stop() {
			stop = true;
		}
		public Counter(long startTime, Agent countingAgent, long quant) {
			this.currentTime = startTime + System.currentTimeMillis();
			this.countingAgent = countingAgent;
			this.quant = quant;
		}
		
		public void setTime(long time) {
			this.currentTime = time;
		}
		public long getTime() {
			return System.currentTimeMillis() - this.currentTime;
		}
		
		public long getCurrentTime() {
			return this.currentTime;
		}
		public void setParameters(long time,long quant) {
			this.currentTime = time + System.currentTimeMillis();
			this.quant = quant;
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
						countingAgent.actualizeTime();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	


