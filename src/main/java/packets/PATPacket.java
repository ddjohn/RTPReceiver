package packets;

import static utils.Utils.*;

public class PATPacket extends GenericPacket {
	
	public PATPacket(byte buf[], int start) {
		
		map.put("length", bits(u(buf[0], buf[1]), 0x8f, 0));
		map.put("id", ux(buf[2], buf[3]));
		
		//printBuffer("PAT", buf,  start,  184);
	}

}
