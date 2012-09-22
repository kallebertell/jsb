package kerebus.jsb;

import org.junit.Test;

import static org.junit.Assert.*;
import static kerebus.jsb.Jsb.*;

public class JsbTest {

	@Test
	public void shouldDeclareVar() {
		String js = jsb().declareVar("x").end();
		assertEquals("var x;\n", js);
	}

	@Test
	public void shouldCreateScope() {
		String js = jsb()
				.scope()
					.declareVar("x")
				.endScope()
				.end();

		assertEquals(
				"(function() {\n" +
				"var x;\n" +
				"})()\n", js);
	}

	@Test
	public void shouldPassArgsToScope() {
		String js = jsb()
				.declareVar("x", "123")
				.scope("x, $")
					.appendLn("$('foo').bar(x);")
				.endScope()
				.end();

		assertEquals(
				"var x = 123;\n" +
				"(function(x, $) {\n" +
				"$('foo').bar(x);\n" +
				"})(x, $)\n", js);
	}

	@Test
	public void shouldCreateInnerScope() {
		String js = jsb()
				.scope()
					.scope()
						.scope()
							.appendLn("alert('huhuu');")
						.endScope()
					.endScope()
				.endScope()
				.end();

		assertEquals(
				"(function() {\n" +
					"(function() {\n" +
						"(function() {\n" +
							"alert('huhuu');\n" +
						"})()\n" +
					"})()\n" +
				"})()\n", js);
	}


	@Test
	public void shouldDoAssignment() {
		String js = jsb()
				.declareVar("x")
				.assign("13").to("x")
				.end();


		assertEquals("var x;\nx = 13;\n", js);
	}


	@Test
	public void shouldDoIfClause() {
		String js = jsb()
				.declareVar("x")
				.assign("13").to("x")
				.iff("x === 13",
						jsb("console.log(x);"))
				.endIf()
				.end();


		assertEquals("var x;\nx = 13;\nif (x === 13) {\nconsole.log(x);\n}\n", js);
	}

	@Test
	public void shouldDoElse() {
		String js = jsb()
				.declareVar("x", "13")
				.iff("x === 13",
						jsb("console.log(x);"))
				.elsee(
						jsb("console.log('foo!');"))
				.endIf()
				.end();


		assertEquals(
				"var x = 13;\n" +
				"if (x === 13) {\n" +
				"console.log(x);\n" +
				"}\n" +
				"else {\n" +
				"console.log('foo!');\n" +
				"}\n", js);
	}


	@Test
	public void shouldDoIfElse() {
		String js = jsb()
				.declareVar("x", "13")
				.iff("x === 13",
						jsb("console.log(x);"))
				.elseIf("x < 3",
						jsb("console.log('foo!');"))
				.endIf()
				.end();


		assertEquals(
				"var x = 13;\n" +
				"if (x === 13) {\n" +
				"console.log(x);\n" +
				"}\n" +
				"else if (x < 3) {\n" +
				"console.log('foo!');\n" +
				"}\n", js);
	}

	@Test
	public void shouldDoIfElseAndElse() {
		String js = jsb()
				.declareVar("x", "13")
				.iff("x === 13",
						"console.log(x);")
				.elseIf("x < 3",
						"console.log('foo!');")
				.elsee(
						"console.log('!');")
				.endIf()
				.end();


		assertEquals(
				"var x = 13;\n" +
				"if (x === 13) {\n" +
				"console.log(x);\n" +
				"}\n" +
				"else if (x < 3) {\n" +
				"console.log('foo!');\n" +
				"}\n" +
				"else {\n" +
				"console.log('!');\n" +
				"}\n", js);
	}

	@Test
	public void shouldUseIfShortcut() {
		String js = jsb()
				.ifThen("true", "alert('woo');")
				.end();

		assertEquals(
				"if (true) {\n" +
				"alert('woo');\n" +
				"}\n", js);
	}

	@Test
	public void shouldDeclareFunc() {
		String js = jsb()
				.declareFunc("foo(a, bar, c)", jsb()
						.assign("12").to("a")
						.append("alert('a is '+a+');"))
				.end();

		assertEquals(
				"function foo(a, bar, c) {\n" +
				"a = 12;\n" +
				"alert('a is '+a+');\n" +
				"}\n", js);
	}

}
