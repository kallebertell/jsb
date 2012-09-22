package kerebus.jsb;

/**
 * A string builder with a few convenience methods.
 */
public class JsStringBuilder {

	private StringBuilder sb = new StringBuilder();

	public JsStringBuilder append(CharSequence str) {
		sb.append(str);
		return this;
	}

	public JsStringBuilder prepend(CharSequence str) {
		sb.insert(0, str);
		return this;
	}

	public JsStringBuilder nl() {
		sb.append("\n");
		return this;
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}
