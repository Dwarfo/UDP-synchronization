import java.io.BufferedReader;
	import java.io.IOException;
	import java.io.InputStreamReader;
	import java.net.DatagramPacket;
	import java.net.DatagramSocket;
	import java.net.InetAddress;
	import java.net.SocketException;
	import java.net.UnknownHostException;
	import java.util.ArrayList;
	import java.util.Iterator;
	import java.util.List;
	import java.util.Random;
	
public class Monitor implements Sendable,IListenable{
		
		DatagramSocket agentSocket;
		Listener listenThread;
		Sender sendThread;
		monitorCounter counter;
		Adress adr;
		Random rand = new Random();
		int numberOfAnswers = 1;
		long toCount;
		int actualization = 0;
		boolean showing = true;
		Thread listener,countThread;

		private List<Adress> adresses = new ArrayList<Adress>();
		
		public Monitor(List<Adress> Net) throws SocketException, UnknownHostException {
			
			counter = new monitorCounter(this);
			agentSocket = new DatagramSocket(2000, InetAddress.getByName("localhost"));
			adr = new Adress(2000,InetAddress.getByName("localhost"));
			
			countThread = new Thread(this.counter);
			countThread.start();
			
			listenThread = new Listener(this);
			listener = new Thread(listenThread);
			listener.start();
			
		}
		
		public void receiveMessage() throws IOException, InterruptedException {
			
			byte[] receiveData = new byte[32];

			
			while(true) {
				
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				
				agentSocket.receive(receivePacket);
				
	        	String mesage = new String(receivePacket.getData(),receivePacket.getOffset(),receivePacket.getLength());
	        	
	        	String[] line = mesage.split("\\s+");
	        
	        	//System.out.println(mesage + " " + this.getAdress().adrString());
	        	
	        	
	        	InetAddress IPAddress = receivePacket.getAddress();
	        	int port = receivePacket.getPort();
	        	
	        	
	        	Adress newAdr = new Adress(port, IPAddress);
	        	if(!adresses.contains(newAdr))
	        		adresses.add(newAdr);
	        	Thread.sleep(100);
	        	workMessage(line,newAdr);
	        	//System.out.println(mesage + " " + newAdr.adrString());
			}
		}
		
		public void sendMessage(String message,Adress adr) throws IOException  {
			
			//System.out.println("Message " + message +  " sended from " + this.getAdress().adrString() );
			byte[] sendData = new byte[32];
			
			String answer = message;
	        sendData = answer.getBytes();
	        DatagramPacket sendPacket =  new DatagramPacket(sendData, sendData.length, adr.ip, adr.port);
	        agentSocket.send(sendPacket);
	        
	        //System.out.println(counter.getTime() + ": " + answer + " sended from " + this.adr.adrString() + " to " + adr.adrString());
		}
		
		public void actualizeTime() throws IOException, InterruptedException {
			
			//System.out.println("actualizeTime");
			for(Iterator<Adress> i = adresses.iterator(); i.hasNext();) {
				Adress toSend = i.next();
				
				
				sendThread = new Sender("CLK",toSend,this);
				Thread sender = new Thread(sendThread);
				sender.start();
				sender.join();

			}
		}

		private void tmsCommand(String line,Adress adr) {
			if(showing)
				System.out.println(adr.port + ":" + String.valueOf(adr.ip) + " "+ line + " ms");
		}
		
		private void workMessage(String[] line, Adress senderAdr) throws IOException, InterruptedException {
			
			if(line[0].equals("TMS")){
				tmsCommand(line[1],senderAdr);
	    	}
	    		
		}
		
		public void addAdress(List<Adress> net) {
			
			ArrayList<Adress> newAdrs = new ArrayList<Adress>();
			for(Iterator<Adress> i = net.iterator();i.hasNext();) {
				Adress toAdd = i.next();
				if(!adresses.contains(toAdd))
					adresses.add(toAdd);
				
			}
			adresses.remove(this.getAdress());
		}
		
		public void getTime() throws InterruptedException{
			for(Iterator<Adress> i = adresses.iterator(); i.hasNext();) {
				Adress toSend = i.next();
				sendThread = new Sender("TIME",toSend,this);
				Thread sender = new Thread(sendThread);
				sender.start();
				sender.join();
				
			}
		}
		
		public Adress getAdress() {
			return this.adr;
		}
		
		public void showTime() {
			System.out.println(this.getAdress().adrString() +": "+ this.counter.getTime()+"ms");
		}
		public void showAdresses() {
			for(Iterator<Adress> i = adresses.iterator(); i.hasNext();) {
				Adress toSend = i.next();
				
				System.out.println("Adress " + toSend.ip + ":" + toSend.port);
			}
		}
		
		public List<Adress> getAdresses() {
			return adresses;
			
		}
		
		public void stopShowing() {
			
			this.showing = !this.showing;
			
		}

	

}
