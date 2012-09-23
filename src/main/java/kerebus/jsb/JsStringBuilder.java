package kerebus.jsb;

/**
 * A string builder with a few convenience methods.
 */
public class JsStringBuilder {

	private StringBuilder sb = new StringBuilder();

	public JsStringBuilder append(Object o) {
		return append(o.toString());
	}

	public JsStringBuilder append(CharSequence str) {
		sb.append(str);
		return this;
	}

	public JsStringBuilder prepend(Object o) {
		return prepend(o.toString());
	}

	public JsStringBuilder prepend(CharSequence str) {
		sb.insert(0, str);
		return this;
	}

	/** Appends a semicolon (;) */
	public JsStringBuilder sc() {
		sb.append(";");
		return this;
	}

	/** Appends a new line */
	public JsStringBuilder nl() {
		sb.append("\n");
		return this;
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}
