package packets;

import static utils.Utils.*;

public class CATPacket extends GenericPacket {
	
	public CATPacket(byte buf[], int start) {
		
		map.put("length", bits(u(buf[0], buf[1]), 0x8f, 0));
		map.put("id", ux(buf[2], buf[3]));
		
		//printBuffer("CAT", buf,  start,  184);
	}

}
