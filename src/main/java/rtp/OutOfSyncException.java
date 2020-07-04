package rtp;

public class OutOfSyncException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public OutOfSyncException(String e) {
		super(e);
	}
}
