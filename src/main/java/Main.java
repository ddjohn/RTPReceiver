import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Main implements Runnable {
	private DatagramSocket socket;

	public Main(int port) throws SocketException {
		System.err.println("Starting UDP server at " + port);
		socket = new DatagramSocket(port);
		
		new Thread(this).start();
	}

	static long u(byte... bytes) {
		long value = 0;
		for(byte b : bytes) {
			value <<= 8;
			value += Byte.toUnsignedInt(b);
		}
		return value;
	}
	
	private static int PACKAGE_SIZE_RTP = 12;
	
	public void run() {
		
		byte[] buf;;

		while(true) {
			try {
				
				DatagramPacket packet = new DatagramPacket(buf = new byte[1328], 1328);
				socket.receive(packet);				
				printPacket(packet);
				
				printBuffer(buf, 0, 12);
				printRTPPacket(buf);

			}
			catch(IOException ie) {
				System.err.println(ie);
			}
			catch(OutOfSyncException ie) {
				System.err.println(ie);
				System.exit(0);;
			}
		}
		//socket.close();
	}
	
	private void printPacket(DatagramPacket packet) {
		InetAddress address = packet.getAddress();
		int port = packet.getPort();
		System.out.println(address.getHostName() + ":" + port);
	}

	private void printBuffer(byte buf[], int start, int length) {
		for(int i = start; i < start+length; i++) {
			
			System.out.print(" " + String.format("0x%02X", buf[i]));
			if((i+1) % 16 == 0) {
				System.out.println();
			}
		}
		System.out.println();
		System.out.println();
	}
	
	private void printRTPPacket(byte[] buf) throws OutOfSyncException {
		System.out.println("======= RTP =======");
		if(u(buf[0]) == 0x80) System.out.println("Found: RTP RFC1889v2");
		switch( (int)u(buf[1]) ) {
		case 0x00: System.out.println("Found: RTP PCM 64 kps");;
		case 0x03: System.out.println("Found: RTP GSM 13 kps");;
		case 0x07: System.out.println("Found: RTP LPC 2.4 kps");;
		case 0x1F: System.out.println("Found: RTP H.261");;
		case 0x21: System.out.println("Found: RTP MPEG2");;
		}		
		System.out.println("Found: RTP sequence " + u(buf[2], buf[3]));
		System.out.println("Found: RTP timestamp " + u(buf[4], buf[5], buf[6], buf[7]));
		System.out.println("Found: RTP synchronization " + u(buf[8], buf[9], buf[10], buf[11]));

		System.out.println();
	
		/*
		 * Packet
		 */
		printBuffer(buf, 12, 4);
		printTransportPacket(buf, 12);

		/*
		 * Packet
		 */
		printBuffer(buf, 200, 4);
		printTransportPacket(buf, 200);

		/*
		 * Packet
		 */
		printBuffer(buf, 388, 4);
		printTransportPacket(buf, 388);

		/*
		 * Packet
		 */
		printBuffer(buf, 388, 4);
		printTransportPacket(buf, 388);

		/*
		 * Packet
		 */
		printBuffer(buf, 388, 4);
		printTransportPacket(buf, 388);

		/*
		 * Packet
		 */
		printBuffer(buf, 388, 4);
		printTransportPacket(buf, 388);

		/*
		 * Packet
		 */
		printBuffer(buf, 388, 4);
		printTransportPacket(buf, 388);
	}

	private void printTransportPacket(byte[] buf, int start) throws OutOfSyncException {
		System.out.println("======= TS =======");
		if(u(buf[0 + start]) == 0x47) {
			System.out.println("Found: Sync");
		} else {
			System.out.println("ERROR");
			throw new OutOfSyncException();
		}
		System.out.println("Found: PID " + String.format("0x%02X", u(buf[2 + start])));
		System.out.println("Found: CC " + String.format("0x%02X", u(buf[4 + start])));
		System.out.println();
		
		printBuffer(buf, start + 4, 184);
		printPESPacket(buf, start + 4);
	}

	private void printPESPacket(byte[] buf, int start) throws OutOfSyncException {
		System.out.println("======= PES =======");
		System.out.println("Found: PES Packetstart " + u(buf[start], buf[start+1], buf[start+2]));
		System.out.println("Found: PES ID " + u(buf[start + 3]));
		System.out.println("Found: PES length " + u(buf[start + 4], buf[start + 5]));
		System.out.println("Found: PES 10 " + u(buf[start + 6], buf[start + 7]));
		System.out.println();
	}

	private void printMPEG(byte[] buf, int start) {
		System.out.println("======= MPEG =======");
		System.out.println();
	}


	public static void main(String[] args) throws SocketException {
		new Main(7910);
	}
}
