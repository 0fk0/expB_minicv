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
			int type = variable.getCType().getType();
			if (type == CType.T_pint  || type == CType.T_pint_array) {
				pcx.fatalError(op.toExplainString() + "型[" + variable.getCType().toString() + "]はポインタ指定できません");
			}
			this.setCType(CType.getCType(type));
			this.setConstant(variable.isConstant()); // +の左右両方が定数のときだけ定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		if (variable != null) {
			variable.codeGen(pcx);
		}
	}
}
