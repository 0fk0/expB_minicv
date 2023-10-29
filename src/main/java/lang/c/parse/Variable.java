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
			if ((ident.getCType().getType() == CType.T_int || ident.getCType().getType() == CType.T_pint) && array != null){
				pcx.fatalError("型[" + ident.getCType().toString() + "]の識別子の後にarrayは続きません");
			} else if ((ident.getCType().getType() == CType.T_int_array || ident.getCType().getType() == CType.T_pint_array) && array == null){
				pcx.fatalError("配列型[" + ident.getCType().toString() + "]の識別子の後にarrayが必要です");
			} else if (ident.getCType().getType() == CType.T_pint && array != null){
				pcx.fatalError("参照型の配列は許可されていません");
			}

			if (array != null) {
				array.semanticCheck(pcx);
			}

			setCType(ident.getCType());
			setConstant(ident.isConstant());
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
