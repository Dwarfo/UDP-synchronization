import java.io.IOException;

public interface Sendable {

	void sendMessage(String message, Adress agentReceiver) throws IOException;
}
