package kerebus.jsb;

/**
 * Javascript Builder.
 * A small dsl for building javascript at runtime in Java.
 * It's recommended you use static imports.
 *
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

    public Jsb declareVar(String varName) {
        sb.append("var ").append(varName).append(";").nl();
        return this;
    }

	public Jsb declareVar(String varName, String value) {
		sb.append("var ").append(varName).append(" = ").append(value).append(";").nl();
		return this;
	}

	public Assignment assign(String value) {
		return new Assignment(this, value);
	}

    public String end() {
        if (outerScope != null) {
			JsStringBuilder scopeDeclartion = new JsStringBuilder();
			scopeDeclartion.append("(function(").append(getScopeArgs()).append(") {").nl();
            return sb.prepend(scopeDeclartion.toString())
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

    public Jsb endScope() {
        if (outerScope == null) {
            throw new RuntimeException("Can't end top level scope.");
        }

        return outerScope.append(this.end());
    }

    public Jsb append(String str) {
        sb.append(str);
        return this;
    }

	public Jsb appendLn(String str) {
		sb.append(str).nl();
		return this;
	}

    public Jsb scope() {
        return new Jsb(this, null);
    }

	public Jsb scope(String args) {
		return new Jsb(this, args);
	}

	public IfScope iff(String condition, Jsb blockScope) {
		return new IfScope(this, blockScope, condition);
	}

	public IfScope iff(String condition, String str) {
		return iff(condition, jsb(str));
	}

	public Jsb ifThen(String condition, String str) {
		return iff(condition, str).endIf();
	}

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
			context.appendLn(getIfStr() +" ("+condition+") {");
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
