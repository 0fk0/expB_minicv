package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Variable extends CParseRule {
	// variable ::= ident [ array ]
	CParseRule ident, array;

	public Variable(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (Ident.isFirst(tk));
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (Ident.isFirst(tk)){
			ident = new Ident(pcx);
			ident.parse(pcx);
		}
		tk = ct.getCurrentToken(pcx);
		if (Array.isFirst(tk)) {
			array = new Array(pcx);
			array.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ident != null){
			ident.semanticCheck(pcx);
			setCType(ident.getCType());
			setConstant(ident.isConstant());
		}
		if (array != null) {
			array.semanticCheck(pcx);;
			setCType(array.getCType());
			setConstant(array.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; variable starts");
		if (ident != null) {
			ident.codeGen(pcx);
		}
		if (array != null){
			array.codeGen(pcx);
		}
		o.println(";;; variable completes");
	}
}
