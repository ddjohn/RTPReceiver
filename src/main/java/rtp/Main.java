package rtp;
import static utils.Utils.bits;
import static utils.Utils.u;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import packets.PATPacket;
import packets.PayloadOnlyPacket;
import packets.AdaptaionOnlyPacket;
import packets.AdaptationPayloadPacket;
import packets.CATPacket;
import packets.OtherPacket;
import packets.RTPHeader;
import packets.TransportStreamHeader;
import utils.Utils;

public class Main implements Runnable {
	private DatagramSocket socket;
	public static boolean DEBUG = false;

	public Main(int port) throws SocketException {
		System.err.println("Starting UDP server at " + port);
		socket = new DatagramSocket(port);

		new Thread(this).start();
	}

	public void run() {

		byte[] buf = new byte[1328];

		while(true) {
			try {
				DatagramPacket packet = new DatagramPacket(buf, 1328);
				socket.receive(packet);				
				Utils.printPacket(packet);

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
	}

	private void printRTPPHeader(byte[] buf) throws OutOfSyncException {
		System.out.println(new RTPHeader(buf, 0));

		// Known packet?
		if(Utils.u(buf[0]) != 0x80) {
			throw new OutOfSyncException("Did not find 0x80 in RTP");
		}

		// Know type
		if(Utils.u(buf[1]) != 0x21) {
			throw new OutOfSyncException("Did not find MPEG2 type in RTP");
		}

		/*
		 * Packet
		 */
		for(int i = 0; i < 7; i++) {
			printTransportPacket(buf, 12+i*188);
		}

		throw new OutOfSyncException("END OF PACKAGE");
	}

	private void printTransportPacket(byte[] buf, int start) throws OutOfSyncException {
		System.out.println(new TransportStreamHeader(buf, start));

		if(Utils.u(buf[0 + start]) != 0x47) {
			throw new OutOfSyncException("Did not find 0x47 in TS");		
		}

		long id = u(buf[2 + start]);
		switch((int)id) {
		case 0: printPAT(buf, start+4); break;
		case 1: printCAT(buf, start+4); break;

		default: {
			//int  length = (int) Utils.u(buf[4 + start]);

			//Utils.printBuffer(buf, start + 4, 184);

			int control = (int) bits(buf[3 + start], 0x30, 4);
			switch(control) {
			case 0x01: printPayloadOnlyPacket(buf, start + 4); break;
			case 0x02: printAdaptaionOnlyPacket(buf, start + 4); break;
			case 0x03: printAdaptationPayloadPacket(buf, start + 4); break;
			default: printOtherPacket(buf, start + 4); break;
			}	
		}
		}
	}

	private void printAdaptationPayloadPacket(byte[] buf, int start) {
		System.out.println(new AdaptationPayloadPacket(buf, start));		
	}

	private void printAdaptaionOnlyPacket(byte[] buf, int start) {
		System.out.println(new AdaptaionOnlyPacket(buf, start));		
	}

	private void printPayloadOnlyPacket(byte[] buf, int start) {
		System.out.println(new PayloadOnlyPacket(buf, start));		
	}

	private void printPAT(byte[] buf, int start) {
		System.out.println(new PATPacket(buf, start));
	}

	private void printCAT(byte[] buf, int start) {
		System.out.println(new CATPacket(buf, start));
	}

	private void printOtherPacket(byte[] buf, int start) throws OutOfSyncException {
		System.out.println(new OtherPacket(buf, start));
	}

	public static void main(String[] args) throws SocketException {
		new Main(5004);
	}
}
