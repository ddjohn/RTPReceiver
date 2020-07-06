package packets;
import java.util.HashMap;

import rtp.Main;

public class GenericPacket {
	protected HashMap<String, Object> header = new HashMap<String, Object>();
	protected HashMap<String, Object> map = new HashMap<String, Object>();
	
	public String toString() {
		if(Main.DEBUG) {
			return 
					super.getClass().getSimpleName() + ":" + 
					header.toString() + 
					map.toString();
			
		} else {
			return 
					super.getClass().getSimpleName() + ":" + 
					header.toString();
			}
	}
}
