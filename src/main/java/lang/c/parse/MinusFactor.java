package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

class MinusFactor extends CParseRule {
	// plusFactor ::= MINUS unsignedFactor
	CToken op;
	CParseRule undesignFactor;

	public MinusFactor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MINUS;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// -の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (UndesignedFactor.isFirst(tk)) {
			undesignFactor = new Term(pcx);
			undesignFactor.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "-の後ろはundesignfactorです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (undesignFactor != null) {
			undesignFactor.semanticCheck(pcx);
			setCType(undesignFactor.getCType()); // number の型をそのままコピー
			setConstant(undesignFactor.isConstant()); // number は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		if (undesignFactor != null) {
			undesignFactor.codeGen(pcx);
		}
	}
}