package packets;

import static utils.Utils.printBuffer;

public class AdaptationPayloadPacket extends GenericPacket {

	public AdaptationPayloadPacket(int length, byte[] buf, int start) {
		printBuffer("Payload", buf,  start+length+1,  184-length-1);
	}

}
