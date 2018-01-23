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

public class Agent implements Sendable,IListenable{
	
	DatagramSocket agentSocket;
	Listener listenThread;
	Sender sendThread;
	Counter counter;
	Adress adr;
	Random rand = new Random();
	int numberOfAnswers = 1;
	long toCount;
	int actualization = 0;
	boolean stop = false;
	Thread listener,countThread;

	private List<Adress> adresses = new ArrayList<Adress>();
	
	public Agent(String adress,int port,long startTime,long quant) throws SocketException, UnknownHostException {
		
		counter = new Counter(startTime, this, quant);
		agentSocket = new DatagramSocket(port, InetAddress.getByName(adress));
		adr = new Adress(port,InetAddress.getByName(adress));
		
		countThread = new Thread(this.counter);
		countThread.start();
		
		listenThread = new Listener(this);
		listener = new Thread(listenThread);
		listener.start();
		toCount = this.counter.getCurrentTime();
	
	}
	
	public Agent(String adress, int port,long startTime,long quant, Agent agent) throws SocketException, UnknownHostException {
		
		this(adress,port,startTime,quant);
		this.adresses.add(agent.getAdress());
	}
	
	public void receiveMessage() throws IOException, InterruptedException {
		
		byte[] receiveData = new byte[32];

		
		while(!stop) {
			
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			
			agentSocket.receive(receivePacket);
			
        	String mesage = new String(receivePacket.getData(),receivePacket.getOffset(),receivePacket.getLength());
        	
        	String[] line = mesage.split("\\s+");
        
        	//System.out.println(mesage + " " + this.getAdress().adrString());
        	
        	
        	InetAddress IPAddress = receivePacket.getAddress();
        	int port = receivePacket.getPort();
        	
        	
        	Adress newAdr = new Adress(port, IPAddress);
        	/*if(!adresses.contains(newAdr))
        		adresses.add(newAdr);
        	Thread.sleep(100);*/
        	workMessage(line,newAdr);
        	//System.out.println(mesage + " " + newAdr.adrString());
		}
	}
	
	private void clkCommand(Adress adr) throws InterruptedException  {
		String message = "ANS " + String.valueOf(this.counter.getCurrentTime());
		
		
		sendThread = new Sender(message,adr,this);
		Thread toSend = new Thread(sendThread);
		toSend.start();
		toSend.join();
		
	}
	
	private void ansCommand(String time) {
		
		toCount += Long.valueOf(time);
		numberOfAnswers++;
		if(numberOfAnswers >= adresses.size()+1) {
			//System.out.println(this.adr.adrString() + " Got " + numberOfAnswers + " answers");
			this.counter.setTime(toCount/(numberOfAnswers));
			toCount = this.counter.getCurrentTime();
			numberOfAnswers = 1;
		}
		
	}
	
	private void frgCommand(Adress adr) {
		adresses.remove(adr);
	}
	
	private void timeCommand(Adress adr) throws InterruptedException {
		String message = "TMS " + String.valueOf(this.counter.getTime());
		
		sendThread = new Sender(message,adr,this);
		Thread toSend = new Thread(sendThread);
		toSend.start();
		toSend.join();
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

	private void workMessage(String[] line, Adress senderAdr) throws IOException, InterruptedException {
		
		if(line[0].equals("ANS")){
			ansCommand(line[1]);
    	}
    	
    	if(line[0].equals("CLK")){
    		//System.out.println("Got clk");
    		clkCommand(senderAdr);
    	}	
    	if(line[0].equals("FRG")){
    		//System.out.println("Got clk");
    		frgCommand(senderAdr);
    	}	
    	if(line[0].equals("TIME")){
    		//System.out.println("Got clk");
    		timeCommand(senderAdr);
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
	
	public void stahp() {
		counter.stop();
		this.stop = true;
		agentSocket.close();
	}

}
