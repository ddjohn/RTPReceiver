package packets;
import java.util.HashMap;

public class GenericPacket {
	protected HashMap<String, Object> header = new HashMap<String, Object>();
	protected HashMap<String, Object> map = new HashMap<String, Object>();
	
	public String toString() {
		return 
				super.getClass().getSimpleName() + ":" + 
				header.toString() + 
				map.toString();
	}
}
