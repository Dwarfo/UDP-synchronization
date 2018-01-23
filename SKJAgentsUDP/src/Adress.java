import java.net.InetAddress;

public class Adress {
	public InetAddress ip;
	public Integer port;
	
	@Override
	public boolean equals(Object other){
		boolean result = false;
	    if (other == null || other.getClass() != getClass()) {
	        result = false;
	    } else {
	        Adress employee = (Adress) other;
	        if (this.ip.equals(employee.ip) && this.port.equals(employee.port)) {
	            result = true;
	        }
	    }
	    return result;
	}
	
	
	public Adress(int port, InetAddress ip) {
		this.port = port;
		this.ip = ip;
	}
	
	public String adrString() {
		return String.valueOf(port) + ": " + String.valueOf(ip);
	}
}
