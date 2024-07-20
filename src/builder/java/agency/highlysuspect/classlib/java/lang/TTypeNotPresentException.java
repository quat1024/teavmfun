package agency.highlysuspect.classlib.java.lang;

import org.teavm.classlib.java.lang.TRuntimeException;
import org.teavm.classlib.java.lang.TThrowable;

/**
 * @see java.lang.TypeNotPresentException
 *
 * just a class missed by TeaVM classlib. used in computeFrames stuff that probably doesn't work.
 */
public class TTypeNotPresentException extends TRuntimeException {
	private final String typeName;

	public TTypeNotPresentException(String typeName, TThrowable cause) {
		super("Type " + typeName + " not present", cause);
		this.typeName = typeName;
	}

	public String typeName() {
		return typeName;
	}
}
