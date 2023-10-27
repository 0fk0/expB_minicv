package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

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
		if (UnsignedFactor.isFirst(tk)) {
			unsignedFactor = new UnsignedFactor(pcx);
			unsignedFactor.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "-の後ろはundesignfactorです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		final int s[] = { CType.T_err, CType.T_int, CType.T_pint };
		if (unsignedFactor != null) {
			unsignedFactor.semanticCheck(pcx);
			int uf = unsignedFactor.getCType().getType(); // unsignedFactorの型
			int nt = s[uf];
			if (nt == CType.T_pint) {
				pcx.fatalError("-の後に型[" + unsignedFactor.getCType().toString() + "]は許可されません");
			}
			this.setCType(CType.getCType(nt));
			this.setConstant(unsignedFactor.isConstant()); // +の左右両方が定数のときだけ定数
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