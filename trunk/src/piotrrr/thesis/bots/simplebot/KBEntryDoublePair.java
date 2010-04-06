package piotrrr.thesis.bots.simplebot;

public class KBEntryDoublePair implements Comparable<KBEntryDoublePair> {
	
	KBEntry kbe = null;
	
	double dbl = 0.0;
	
	public KBEntryDoublePair(KBEntry kbe, double dbl) {
		this.kbe = kbe;
		this.dbl = dbl;
	}

	@Override
	public int compareTo(KBEntryDoublePair o) {
		if (this.dbl > o.dbl) return 1;
		if (this.dbl < o.dbl) return -1;
		return 0;
	}

}
