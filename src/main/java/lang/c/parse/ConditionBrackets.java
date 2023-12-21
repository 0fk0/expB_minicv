package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class ConditionBrackets extends CParseRule {
	// conditionBrackets ::= LBRA judge RBRA
	CParseRule condition;
	CToken lbra, rbra;

	public ConditionBrackets(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_LBRA);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		lbra = tk;
		// [ の次の字句を読む
		tk = ct.getNextToken(pcx);
		if (Judge.isFirst(tk)) {
			condition = new Judge(pcx);
			condition.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "[ の後ろはconditioinAllです");
		}

		tk = ct.getCurrentToken(pcx); // expressionの最後でnumberが読まれgetNextTokenされる
		if (tk.getType() == CToken.TK_RBRA) {
			rbra = tk;
		} else {
			pcx.fatalError(tk.toExplainString() + "[conditionAll の後ろは ] です");
		}
		ct.getNextToken(pcx); // ]は構文規則ではないので自動でトークンを次に移してくれない
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (condition != null) {
			condition.semanticCheck(pcx);
			this.setCType(condition.getCType());
			this.setConstant(condition.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; conditionBrackets starts");
		if (lbra != null && rbra != null && condition != null) {
			condition.codeGen(pcx);
		}
		o.println(";;; conditionBrackets completes");
	}
}