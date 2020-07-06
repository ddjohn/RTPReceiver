package packets;

import static utils.Utils.*;

public class PayloadOnlyPacket extends GenericPacket {

	public PayloadOnlyPacket(byte[] buf, int start) {
		printBuffer("Payload", buf,  start,  184);
	}
}
