import java.io.IOException;

public interface IListenable {
	void receiveMessage() throws IOException, InterruptedException;
}
