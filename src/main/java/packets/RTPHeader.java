package packets;
import static utils.Utils.*;

public class RTPHeader extends GenericPacket {
	
	public RTPHeader(byte buf[], int offset) {
		header.put("type", "RFC1889");
		header.put("type", "MPEG-II");
		
		map.put("sequence"       , ux(buf[2], buf[3]));
		map.put("timestamp"      , ux(buf[4], buf[5], buf[6], buf[7]));
		map.put("synchronization", ux(buf[8], buf[9], buf[10], buf[11]));
	}
}
