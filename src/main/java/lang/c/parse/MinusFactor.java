package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

class MinusFactor extends CParseRule {
	// plusFactor ::= MINUS unsignedFactor
	CToken op;
	CParseRule unsignedFactor;

	public MinusFactor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_MINUS);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// -の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (FactorAmp.isFirst(tk)) {
			pcx.fatalError(tk.toExplainString() + "-の後ろに参照型は来ません");
		} else if (UnsignedFactor.isFirst(tk)) {
			unsignedFactor = new UnsignedFactor(pcx);
			unsignedFactor.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "-の後ろはundesignfactorです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (unsignedFactor != null) {
			unsignedFactor.semanticCheck(pcx);
			setCType(unsignedFactor.getCType()); // number の型をそのままコピー
			setConstant(unsignedFactor.isConstant()); // number は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (unsignedFactor != null) {
			unsignedFactor.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; MinusFactor: 数を取り出して負の補数表現にして積む<" + op.toExplainString() + ">");
			o.println("\tXOR\t#0xFFFF, R0\t; MinusFactor: XORでビット反転");
			o.println("\tADD\t#1, R0\t; MinusFactor: +1");
			o.println("\tMOV\tR0, (R6)+\t; MinusFactor:");
		}
	}
}