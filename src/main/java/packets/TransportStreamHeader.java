package packets;

import static utils.Utils.*;

public class TransportStreamHeader extends GenericPacket {

	public TransportStreamHeader(byte buf[], int start) {
		header.put("type", "MPEG-II");
		
		map.put("sync"      , ux(buf[0 + start]));
		header.put("pid"    , ux(buf[2 + start]));
		header.put("cc"     , String.format("0x%x", bits(buf[3 + start], 0x0f, 0)));
		header.put("control", String.format("0x%x", bits(buf[3 + start], 0x30, 4)));
		map.put("length"    , u(buf[4 + start]));
		
		map.put("discont"   , bits(buf[5 + start], 0x08, 3));
		map.put("random"    , bits(buf[5 + start], 0x04, 2));
		map.put("priority"  , bits(buf[5 + start], 0x02, 1));
		map.put("pcr"       , bits(buf[5 + start], 0x01, 0));;
		
		map.put("opcr"      , bits(buf[6 + start], 0x08, 3));
		map.put("splice"    , bits(buf[6 + start], 0x04, 2));
		map.put("transport" , bits(buf[6 + start], 0x02, 1));
		map.put("extention" , bits(buf[6 + start], 0x01, 0));

		int control = (int) ((0x30 & u(buf[3 + start])) >> 6);
		int length = (int)u(buf[4 + start]);

		//printBuffer("TS", buf, start+6+length-1, 182-length+1);
	}
}
