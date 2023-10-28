package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Array extends CParseRule {
	// array ::= LBRA expression RBRA
	CParseRule expression;
	CToken lbra, rbra;

	public Array(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_LBRA);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_LBRA){
			lbra = tk;
			// ( の次の字句を読む
			tk = ct.getNextToken(pcx);
			if (Expression.isFirst(tk)) {
				expression = new Expression(pcx);
				expression.parse(pcx);
			} else {
				pcx.fatalError(tk.toExplainString() + "[ の後ろはexpressionです");
			}

			tk = ct.getCurrentToken(pcx); // expressionの最後でnumberが読まれgetNextTokenされる
			if (tk.getType() == CToken.TK_RBRA) {
				rbra = tk;
			} else {
				pcx.fatalError(tk.toExplainString() + "[expressioin の後ろは ] です");
			}
			ct.getNextToken(pcx); // )は構文規則ではないので自動でトークンを次に移してくれない
		} else {
			pcx.fatalError(tk.toExplainString() + "arrayに続く構文はは[expression]です");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (expression != null) {
			expression.semanticCheck(pcx);
			setCType(expression.getCType());
			setConstant(expression.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factor starts");
		if (lbra != null && rbra != null && expression != null) {
			expression.codeGen(pcx);
		}
		o.println(";;; factor completes");
	}
}