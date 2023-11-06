package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class FactorAmp extends CParseRule {
	// factorAmp ::= AMP number
	CParseRule number, primary;
	CToken amp;

	public FactorAmp(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_AMP);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		// amp(&)の次の字句を読む
		amp = ct.getCurrentToken(pcx);
		// numberを読み込む
		CToken tk = ct.getNextToken(pcx);
		if (tk.getType() == CToken.TK_MULT){
			pcx.fatalError(tk.toExplainString() + "&の後に*は許可されません");
	 	} else if (Number.isFirst(tk)) {
			number = new Number(pcx);
			number.parse(pcx);
		} else if (Primary.isFirst(tk)) {
			primary = new Primary(pcx);
			primary.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "&の後ろはnumberかprimaryです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primary != null) {
			primary.semanticCheck(pcx);
			if (primary.getCType().getType() == CType.T_pint){
				pcx.fatalError("&の後の参照型(ポインタのポインタ)は許可されません");
			}
		} else if (number != null){
			number.semanticCheck(pcx);
		}
		this.setCType(CType.getCType(CType.T_pint));
		this.setConstant(true);
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factorAmp starts");
		if (number != null) {
			number.codeGen(pcx);
		} else if (primary != null){
			primary.codeGen(pcx);
		}
		o.println(";;; factorAmp completes");
	}
}
