import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CommandExecutioner {
	
	//All possible commands
	private final String addAgent = "addAgent";
	private final String removeAgent = "removeAgent";
	private final String setParameters = "setParameters";
	private final String showNetFor = "showNetFor";
	private final String showing = "showing";
	private final String addAdr = "addAdr";
	private final String foreignAgent = "foreignAgent";
	
	private long systemTime = System.currentTimeMillis();
	boolean reading = true;
	
	private ArrayList<Agent> Net;
	private Monitor monitor;
	private HashMap<String, Agent> addressBook;
	static List<Adress> adressNet = new ArrayList<Adress>();
	
	private BufferedReader commandReader;
	public Creator agentCreator = new Creator();
	
	
	public CommandExecutioner() throws Exception {
		commandReader = new BufferedReader(new InputStreamReader(System.in));
		Net = new ArrayList<Agent>();
		addressBook = new HashMap<String, Agent>();
		monitor = new Monitor(adressNet);
		//addressBook.put(String.valueOf(agent.getAdress().ip), agent);
		//System.out.println("All posible commands " + allPosibleCommands);
		System.out.println("Type command");
		
		this.readCommands();
		
	}
	public void readCommands() throws IOException, InterruptedException {
		while(reading) {
			String command = commandReader.readLine();
			
			executeCommand(command);
		}
		
	}
	
	public void executeCommand(String command) throws IOException, InterruptedException {
	
		String[] commandPart = command.split("\\s+");
		
		switch(commandPart[0]) {
		case addAgent: 
			Agent agent = agentCreator.addAgent(commandPart[1],5000,Long.valueOf(commandPart[2]),Long.valueOf(commandPart[3]));
			Net.add(agent);
			adressNet.add(agent.adr);
			addressBook.put(String.valueOf(agent.getAdress().ip) , agent);
			addAdresses();
			break;
		case removeAgent:
			Agent agentToRemove = addressBook.get(commandPart[1]);
			remove(agentToRemove);
			agentToRemove.stahp();
			break;
		case setParameters:
			long timeToSet = Long.valueOf(commandPart[2]);
			long quantToSet = Long.valueOf(commandPart[3]);
			Agent agentToSetTime = addressBook.get(commandPart[1]);
			agentToSetTime.counter.setParameters(timeToSet, quantToSet);
			
			break;
		case showNetFor:
			Agent agentToShowNet = addressBook.get(commandPart[1]);
			agentToShowNet.showAdresses();
			break;
		case showing:
			monitor.stopShowing();
			break;
		case addAdr:
			Adress foreignAdress = new Adress(Integer.valueOf(commandPart[1]),InetAddress.getByName(commandPart[2])); 
			adressNet.add(foreignAdress);
			addAdresses();
			break;
		case foreignAgent:
			Agent foreignagent = agentCreator.addAgent(commandPart[1],Integer.valueOf(commandPart[2]),Long.valueOf(commandPart[3]),Long.valueOf(commandPart[4]));
			Net.add(foreignagent);
			adressNet.add(foreignagent.adr);
			addressBook.put(String.valueOf(foreignagent.getAdress().port) , foreignagent);
			addAdresses();
			break;
		default:
			System.out.println("Unknown comand");
		}
		
	}
	
	private void remove(Agent agent) throws IOException {
		List<Adress> adresses = agent.getAdresses();
		for(Iterator<Adress> i = adresses.iterator();i.hasNext();) {
			Adress adr = i.next();
			agent.sendMessage("FRG", adr);
		}
	}
	
	private void addAdresses() {
		
		for(Iterator<Agent> i = Net.iterator();i.hasNext();) {
			Agent toAdd = i.next();
			toAdd.addAdress(adressNet);
		}
		monitor.addAdress(adressNet);
	}
}
