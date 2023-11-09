package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Statement extends CParseRule {
	// statement ::= statementAssign
	CParseRule statement;

	public Statement(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (StatementAssign.isFirst(tk));
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (StatementAssign.isFirst(tk)){
			statement = new StatementAssign(pcx);
			statement.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (statement != null) {
			statement.semanticCheck(pcx);
			setCType(statement.getCType());
			setConstant(statement.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		// PrintStream o = pcx.getIOContext().getOutStream();
		// o.println(";;; statement starts");
		// if (statement != null) {
		// 	statement.codeGen(pcx);
		// 	o.println("\tMOV\t-(R6), R0\t; statement: アドレスを取り出して、内容を参照して、積む");
		// 	o.println("\tMOV\t(R0), (R6)+\t; statement:");
		// }
		// o.println(";;; statement completes");
	}
}
