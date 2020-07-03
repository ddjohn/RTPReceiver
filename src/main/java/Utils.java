import java.net.DatagramPacket;
import java.net.InetAddress;

public class Utils {

	static public long u(byte... bytes) {
		long value = 0;
		for(byte b : bytes) {
			value <<= 8;
			value += Byte.toUnsignedInt(b);
		}
		return value;
	}

	static public String ux(byte... bytes) {
		return String.format("0x%x", u(bytes));
	}
	
	static public void printBuffer(byte buf[], int start, int length) {
		for(int i = start; i < start+length; i++) {
			
			System.out.print(" " + String.format("0x%02X", buf[i]));
			if((i+1) % 16 == 0) {
				System.out.println();
			}
		}
		System.out.println();
		System.out.println();
	}
	
	public static void printPacket(DatagramPacket packet) {
		InetAddress address = packet.getAddress();
		int port = packet.getPort();
		System.out.println(address.getHostName() + ":" + port);
	}
}
