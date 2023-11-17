package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class ConditionLT extends CParseRule {
    // ConditionLT ::= LT expression
	CParseRule expressionL, expressionR;
	CToken op;

	public ConditionLT(CParseContext pcx, CParseRule expression) {
		expressionL = expression;
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_LT);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_LT) {
            op = tk;
		}

		tk = ct.getNextToken(pcx);

		if (Expression.isFirst(tk)) {
            expressionR = new Expression(pcx);
            expressionR.parse(pcx);
        } else {
			pcx.fatalError(tk.toExplainString() + "LTの後ろはexpressionです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (expressionL != null && expressionR != null){
			expressionL.semanticCheck(pcx);
			expressionR.semanticCheck(pcx);

			if (expressionL.getCType().getType() == expressionR.getCType().getType()){
				this.setCType(CType.getCType(CType.T_bool));
				this.setConstant(true);
			} else {
				pcx.fatalError(op.toExplainString() + "左辺の型[" + expressionL.getCType().toString() + "]と右辺の型[" + expressionR.getCType().toString() + "]が一致しません");
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition < (compare) starts");
		if (expressionL != null && expressionR != null) {
			expressionL.codeGen(pcx);
			expressionR.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6), R0\t; ConditionLT: ２数を取り出して、比べる");
			o.println("\tMOV\t-(R6), R1\t; ConditionLT:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionLT: set true");
			o.println("\tCMP\tR0, R1\t; ConditionLT: R1<R0 = R1-R0<0");
			o.println("\tBRN\tLT" + seq + "\t; ConditionLT");
			o.println("\tCLR\tR2\t\t; ConditionLT: set false");
			o.println("LT" + seq + ":\tMOV\tR2, (R6)+\t; ConditionLT:");
		}
		o.println(";;;condition < (compare) completes");
	}
}
