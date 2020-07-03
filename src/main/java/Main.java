import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/*
 * RFC3550 - RTP
 * RFC3551 - RTP Profile for Audio and Video Conferences with Minimal Control
 * RFC2520 - RTP Payload Format for MPEG1/MPEG2 Video
 * MPA denotes MPEG-1 or MPEG-2 audio encapsulated as elementary
   streams.  The encoding is defined in ISO standards ISO/IEC 11172-3
   and 13818-3.  The encapsulation is specified in RFC 2250 [14].
 */
public class Main implements Runnable {
	private DatagramSocket socket;

	public Main(int port) throws SocketException {
		System.err.println("Starting UDP server at " + port);
		socket = new DatagramSocket(port);

		new Thread(this).start();
	}

	public void run() {

		byte[] buf;;

		while(true) {
			try {

				DatagramPacket packet = new DatagramPacket(buf = new byte[1328], 1328);
				socket.receive(packet);				
				Utils.printPacket(packet);

				//Utils.printBuffer(buf, 0, 12);
				printRTPPHeader(buf);
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

	private void printRTPPHeader(byte[] buf) throws OutOfSyncException {
		// Known packet?
		if(Utils.u(buf[0]) != 0x80) {
			throw new OutOfSyncException("Did not find 0x80 in RTP");
		}

		// Know type
		if(Utils.u(buf[1]) != 0x21) {
			throw new OutOfSyncException("Did not find MPEG2 type in RTP");
		}

		String        sequence =  Utils.ux(buf[2], buf[3]);
		String       timestamp =  Utils.ux(buf[4], buf[5], buf[6], buf[7]);
		String synchronization = Utils.ux(buf[8], buf[9], buf[10], buf[11]);

		System.out.println("+ RTP {" + 
				"type:" +            "MPEG2" + "," +
				"sequence:" +        sequence + "," +
				"timestamp:" +       timestamp + "," +
				"synchronization:" + synchronization +
				"}");

		/*
		 * Packet
		 */
		for(int i = 0; i < 7; i++) {
			//Utils.printBuffer(buf, 12+i*188, 4);
			printTransportPacket(buf, 12+i*188);
		}
		
		throw new OutOfSyncException("END OF PACKAGE");
	}

	private void printTransportPacket(byte[] buf, int start) throws OutOfSyncException {
		if(Utils.u(buf[0 + start]) != 0x47) {
			throw new OutOfSyncException("Did not find 0x47 in TS");		
		}

		String pid = Utils.ux(buf[2 + start]);
		long  cc = 0xf & Utils.u(buf[3 + start]);

		System.out.println("  + TS {" +
				"pid:" + pid + "," + 
				"cc:" +  cc + 
				"}");

		long id = Utils.u(buf[2 + start]);
		
		switch((int)id) {
		case 0: System.out.println("MPEG2 PAT"); break;
		case 1: System.out.println("MPEG2 CAT"); break;
		
		default: {
			int  length = (int) Utils.u(buf[4 + start]);
			System.out.println("length=" + length);
			//Utils.printBuffer(buf, start + 4, 184);
			//printPESPacket(buf, start + 4);
		}
		
		
	
		}
	}

	private void printPESPacket(byte[] buf, int start) throws OutOfSyncException {		
		String packetstart = Utils.ux(buf[start], buf[start+1], buf[start+2]);
		String          id = Utils.ux(buf[start + 3]);
		String      length = Utils.ux(buf[start + 4], buf[start + 5]);
		String         ten = Utils.ux(buf[start + 6], buf[start + 7]);

		System.out.println("    + PES{" +
				"packetstart:" + packetstart + "," + 
				"id:" + id + "," + 
				"length:" + length + "," + 
				"ten:" + ten + 
				"}");
		System.out.println();
	}

	public static void main(String[] args) throws SocketException {
		new Main(5004);
	}
}


/*
 * import java.io.IOException;
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

	public void run() {

		byte[] buf;;

		while(true) {
			try {

				DatagramPacket packet = new DatagramPacket(buf = new byte[1328], 1328);
				socket.receive(packet);				
				printPacket(packet);

				printBuffer(buf, 0, 12);
				printRTPPHeader(buf);

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

	private void printRTPPHeader(byte[] buf) throws OutOfSyncException {
		// Known packet?
		if(Utils.u(buf[0]) != 0x80) {
			throw new OutOfSyncException("Did not find 0x80 in RTP");
		}

		// Know type
		if(Utils.u(buf[1]) != 0x21) {
			throw new OutOfSyncException("Did not find MPEG2 type in RTP");
		}

		String type = "unknown";

		switch( (int)Utils.u(buf[1]) ) {
		case 0x00: type ="PCM 64 kps";;
		case 0x03: type ="GSM 13 kps";;
		case 0x07: type ="LPC 2.4 kps";;
		case 0x1F: type ="H.261";;
		case 0x21: type ="MPEG2";;
		}		

		String        sequence =  Utils.ux(buf[2], buf[3]);
		String       timestamp =  Utils.ux(buf[4], buf[5], buf[6], buf[7]);
		String synchronization = Utils.ux(buf[8], buf[9], buf[10], buf[11]);

		System.out.println("+ RTP {" + 
			               "type:" +            type + "," +
			           "sequence:" +        sequence + "," +
				      "timestamp:" +       timestamp + "," +
				"synchronization:" + synchronization +
		"}");

		printBuffer(buf, 12, 4);
		printTransportPacket(buf, 12);

		printBuffer(buf, 200, 4);
		printTransportPacket(buf, 200);

		printBuffer(buf, 388, 4);
		printTransportPacket(buf, 388);

		printBuffer(buf, 388, 4);
		printTransportPacket(buf, 388);

		printBuffer(buf, 388, 4);
		printTransportPacket(buf, 388);

		printBuffer(buf, 388, 4);
		printTransportPacket(buf, 388);

		printBuffer(buf, 388, 4);
		printTransportPacket(buf, 388);
	}

	private void printTransportPacket(byte[] buf, int start) throws OutOfSyncException {
		if(Utils.u(buf[0 + start]) != 0x47) {
			throw new OutOfSyncException("Did not find 0x47 in TS");		
		}

		String pid = Utils.ux(buf[2 + start]);
		String  cc = Utils.ux(buf[4 + start]);

		System.out.println("  + TS {" +
				"pid:" + pid + "," + 
				 "cc:" +  cc + 
		"}");

		printBuffer(buf, start + 4, 184);
		printPESPacket(buf, start + 4);
	}

	private void printPESPacket(byte[] buf, int start) throws OutOfSyncException {		
		String packetstart = Utils.ux(buf[start], buf[start+1], buf[start+2]);
		String          id = Utils.ux(buf[start + 3]);
		String      length = Utils.ux(buf[start + 4], buf[start + 5]);
		String         ten = Utils.ux(buf[start + 6], buf[start + 7]);

		System.out.println("    + PES{" +
				"packetstart:" + packetstart + "," + 
				         "id:" + id + "," + 
				     "length:" + length + "," + 
				        "ten:" + ten + 
		"}");
		System.out.println();
	}

	private void printMPEG(byte[] buf, int start) {
		System.out.println("======= MPEG =======");
		System.out.println();
	}


	public static void main(String[] args) throws SocketException {
		new Main(5004);
	}
}

 */