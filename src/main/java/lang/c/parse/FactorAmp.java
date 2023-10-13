package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class FactorAmp extends CParseRule {
	// factorAmp ::= AMP number
	CToken num, amp;

	public FactorAmp(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_AMP;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		// amp(&)の次の字句を読む
		amp = ct.getCurrentToken(pcx);
		// numberを読み込む
		num = ct.getNextToken(pcx);
		ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		this.setCType(CType.getCType(CType.T_int));
		this.setConstant(true);
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; number starts");
		if (num != null) {
			o.println("\tMOV\t#" + num.getText() + ", (R6)+\t; FactorAmp: 数を積む<" + num.toExplainString() + ">");
		}
		o.println(";;; number completes");
	}
}
