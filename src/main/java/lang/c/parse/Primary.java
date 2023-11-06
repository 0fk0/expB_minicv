package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Primary extends CParseRule {
	// primary ::= primaryMult | variable
	CParseRule primaryMult, variable;

	public Primary(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (PrimaryMult.isFirst(tk) || Variable.isFirst(tk));
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (PrimaryMult.isFirst(tk)){
			primaryMult = new PrimaryMult(pcx);
			primaryMult.parse(pcx);
		} else if(Variable.isFirst(tk)) {
			variable = new Variable(pcx);
			variable.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primaryMult != null){
			primaryMult.semanticCheck(pcx);
			setCType(primaryMult.getCType());
			setConstant(primaryMult.isConstant());
		} else if(variable != null) {
			variable.semanticCheck(pcx);
			setCType(variable.getCType());
			setConstant(variable.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; primary starts");
		if (primaryMult != null) {
			primaryMult.codeGen(pcx);
		} else if (variable != null){
			variable.codeGen(pcx);
		}
		o.println(";;; primary completes");
	}
}
