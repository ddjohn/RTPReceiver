package packets;

import static utils.Utils.*;

public class OtherPacket extends GenericPacket {

	public OtherPacket(byte buf[], int start) {
		printBuffer("OTHER", buf,  start,  184);
		
		map.put("pktstart", ux(buf[start], buf[start+1], buf[start+2]));
		map.put("id", ux(buf[start + 3]));
		map.put("length", ux(buf[start + 6], buf[start + 7]));
	}
}
