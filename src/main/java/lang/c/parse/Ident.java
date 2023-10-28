package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Ident extends CParseRule {
	// number ::= NUM
	CToken ident;

	public Ident(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_IDENT);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		ident = tk;
		tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		this.setCType(CType.getCType(CType.T_int));
		this.setConstant(true);
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; number starts");
		if (ident != null) {
			o.println("\tMOV\t#" + ident.getText() + ", (R6)+\t; Ident: 数を積む<" + ident.toExplainString() + ">");
		}
		o.println(";;; number completes");
	}
}
