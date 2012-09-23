package kerebus.jsb;

/**
 * Javascript Builder.
 * A small dsl for building javascript at runtime in Java.
 * It's recommended you use static imports.
 * <p/>
 * import static kerebus.jsb.Jsb.*;
 */
public class Jsb {

	private JsStringBuilder sb;

	private Jsb outerScope;
	private String scopeArgs;

	private Jsb() {
		this(null, null);
	}

	private Jsb(Jsb outerScope, String scopeArgs) {
		this.outerScope = outerScope;
		this.sb = new JsStringBuilder();
		this.scopeArgs = scopeArgs;
	}

	public static Jsb jsb() {
		return new Jsb();
	}

	public static Jsb jsb(String js) {
		return new Jsb().append(js);
	}

	/** E.g. var x; */
	public Jsb declareVar(String varName) {
		sb.append("var ").append(varName).sc().nl();
		return this;
	}

	/** E.g. var x = 12; */
	public Jsb declareVar(String varName, String value) {
		sb.append("var ").append(varName).append(" = ").append(value).sc().nl();
		return this;
	}

	/** E.g. x = 12; */
	public Assignment assign(String value) {
		return new Assignment(this, value);
	}

	/** Returns resulting javascript string */
	public String end() {
		if (outerScope != null) {
			JsStringBuilder scopeDeclaration = new JsStringBuilder();
			scopeDeclaration.append("(function(").append(getScopeArgs()).append(") {").nl();
			return sb.prepend(scopeDeclaration)
					.append("})(").append(getScopeArgs()).append(")").nl()
					.toString();
		}
		return sb.toString();
	}

	private String getScopeArgs() {
		if (scopeArgs == null) {
			return "";
		}

		return scopeArgs;
	}

	/** Ends an inner scope. See scope */
	public Jsb endScope() {
		if (outerScope == null) {
			throw new RuntimeException("Can't end top level scope.");
		}

		return outerScope.append(this.end());
	}

	/** Appends to the currently built javascript string */
	public Jsb append(String str) {
		sb.append(str);
		return this;
	}

	/** Appends to the currently built javascript string and adds a new line */
	public Jsb appendLn(String str) {
		sb.append(str).nl();
		return this;
	}

	/** E.g. (function() { ... })(); */
	public Jsb scope() {
		return new Jsb(this, null);
	}

	/** E.g. (function(args) { ... })(args); */
	public Jsb scope(String args) {
		return new Jsb(this, args);
	}

	/** E.g. if (condition) { ... } */
	public IfScope iff(String condition, Jsb blockScope) {
		return new IfScope(this, blockScope, condition);
	}

	/** E.g. if (condition) { ... } */
	public IfScope iff(String condition, String str) {
		return iff(condition, jsb(str));
	}

	/** E.g. if (condition) { ... } */
	public Jsb ifThen(String condition, String str) {
		return iff(condition, str).endIf();
	}

	/** E.g. function (a, b, c) { ... } */
	public Jsb declareFunc(String funcDeclaration, Jsb funcScope) {
		return new FuncScope(this, funcScope, funcDeclaration).endFunc();
	}


	private static class FuncScope {
		String funcDeclaration;
		Jsb funcScope;
		Jsb context;

		public FuncScope(Jsb context, Jsb funcScope, String funcDeclaration) {
			this.context = context;
			this.funcDeclaration = funcDeclaration;
			this.funcScope = funcScope;
		}

		public Jsb endFunc() {
			context.append("function ").append(funcDeclaration).appendLn(" {");
			context.appendLn(funcScope.end());
			context.appendLn("}");
			return context;
		}
	}


	public static class IfScope {
		Jsb context;
		String condition;
		Jsb blockScope;
		boolean isElseIf = false;

		public IfScope(Jsb context, Jsb blockScope, String condition, boolean isElseIf) {
			this.condition = condition;
			this.blockScope = blockScope;
			this.context = context;
			this.isElseIf = isElseIf;
		}

		public IfScope(Jsb context, Jsb blockScope, String condition) {
			this(context, blockScope, condition, false);
		}

		public Jsb endIf() {
			context.append(getIfStr()).append(" (").append(condition).appendLn(") {");
			context.appendLn(blockScope.end());
			context.appendLn("}");
			return context;
		}

		private String getIfStr() {
			return isElseIf ? "else if" : "if";
		}

		public IfScope elseIf(String condition, String str) {
			return elseIf(condition, jsb(str));
		}

		public IfScope elseIf(String condition, Jsb blockScope) {
			return new IfScope(endIf(), blockScope, condition, true);
		}

		public ElseScope elsee(String str) {
			return elsee(jsb(str));
		}

		public ElseScope elsee(Jsb blockScope) {
			return new ElseScope(endIf(), blockScope);
		}
	}


	public static class ElseScope {
		private Jsb context;
		private Jsb blockScope;

		public ElseScope(Jsb context, Jsb blockScope) {
			this.context = context;
			this.blockScope = blockScope;
		}

		public Jsb endIf() {
			context.appendLn("else {");
			context.appendLn(blockScope.end());
			context.appendLn("}");
			return context;
		}
	}


	public static class Assignment {
		Jsb context;
		String value;

		public Assignment(Jsb context, String value) {
			this.context = context;
			this.value = value;
		}

		public Jsb to(String varName) {
			return context.append(varName).append(" = ").append(value).appendLn(";");
		}
	}


}
