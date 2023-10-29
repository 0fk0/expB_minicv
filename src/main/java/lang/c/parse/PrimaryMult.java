package lang.c.parse;

import lang.*;
import lang.c.*;

public class PrimaryMult extends CParseRule {
	// primaryMult ::= MULT variable
	CToken op;
	CParseRule variable;

	public PrimaryMult(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_MULT);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// *の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (Variable.isFirst(tk)) {
			variable = new Variable(pcx);
			variable.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "ポインタ型の*の後ろはvariableです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (variable != null) {
			variable.semanticCheck(pcx);
			int vartype = variable.getCType().getType();
			if (vartype == CType.T_int) {
				pcx.fatalError("*の後に型[" + variable.getCType().toString() + "]は許可されません");
			} else {
				setCType(variable.getCType());
				setConstant(variable.isConstant());
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		if (variable != null) {
			variable.codeGen(pcx);
		}
	}
}
