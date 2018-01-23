import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Creator {

	public Agent addAgent(String adr,int port, long startTime, long quant) throws IOException, InterruptedException {
		Agent newAgent = null;
		
		newAgent = new Agent(adr,port,startTime,quant);
		
		return newAgent;
	}
	

}
