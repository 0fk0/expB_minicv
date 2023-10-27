package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Factor extends CParseRule {
	// factor ::= plusFactor | minusFactor | unsignedFactor
	CParseRule plusFactor, minusFactor, unsiginedFactor;

	public Factor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (PlusFactor.isFirst(tk) || MinusFactor.isFirst(tk) || UnsignedFactor.isFirst(tk));
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (PlusFactor.isFirst(tk)){
			plusFactor = new PlusFactor(pcx);
			plusFactor.parse(pcx);
		} else if (MinusFactor.isFirst(tk)){
			minusFactor = new MinusFactor(pcx);
			minusFactor.parse(pcx);
		} else if (UnsignedFactor.isFirst(tk)){
			unsiginedFactor = new UnsignedFactor(pcx);
			unsiginedFactor.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "factorに続く構文はplusFactorかminusFactorかunsignedFactorです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (plusFactor != null) {
			plusFactor.semanticCheck(pcx);
			setCType(plusFactor.getCType()); // number の型をそのままコピー
			setConstant(plusFactor.isConstant()); // number は常に定数
		} else if (minusFactor != null) {
			minusFactor.semanticCheck(pcx);
			setCType(minusFactor.getCType()); // number の型をそのままコピー
			setConstant(minusFactor.isConstant()); // number は常に定数
		} else if (unsiginedFactor != null) {
			unsiginedFactor.semanticCheck(pcx);
			setCType(unsiginedFactor.getCType()); // number の型をそのままコピー
			setConstant(unsiginedFactor.isConstant()); // number は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factor starts");
		if (plusFactor != null) {
			plusFactor.semanticCheck(pcx);
			plusFactor.codeGen(pcx);
		} else if (minusFactor != null) {
			minusFactor.semanticCheck(pcx);
			minusFactor.codeGen(pcx);
		} else if (unsiginedFactor != null) {
			unsiginedFactor.semanticCheck(pcx);
			unsiginedFactor.codeGen(pcx);
		}
		o.println(";;; factor completes");
	}
}