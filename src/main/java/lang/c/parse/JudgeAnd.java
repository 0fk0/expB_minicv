package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class JudgeAnd extends CParseRule {
    // JudgeAnd ::= AND conditionAll
	CParseRule conditionL, conditionR;
	CToken op;

	public JudgeAnd(CParseContext pcx, CParseRule conditionAll) {
		this.conditionL = conditionAll;
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_AND);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// ANDの次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (ConditionAll.isFirst(tk)) {
            conditionR = new ConditionAll(pcx);
            conditionR.parse(pcx);
        } else {
			pcx.fatalError(tk.toExplainString() + "論理演算子の後ろはconditionAllです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (conditionL != null && conditionR != null){
			conditionL.semanticCheck(pcx);
			conditionR.semanticCheck(pcx);

			if (conditionL.getCType().getType() != CType.T_bool){
				pcx.fatalError(op.toExplainString() + "左辺の型がboolではありません");
			} else if (conditionR.getCType().getType() != CType.T_bool){
				pcx.fatalError(op.toExplainString() + "右辺の型がboolではありません");
			} else {
				this.setCType(CType.getCType(CType.T_bool));
				this.setConstant(conditionL.isConstant() && conditionR.isConstant());
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; JudgeAnd starts");
		if (conditionL != null && conditionR != null) {
			conditionL.codeGen(pcx);
			conditionR.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; JudgeAnd: 二つの真理値を取り出して論理積を取る");
			o.println("\tMOV\t-(R6), R1\t; JudgeAnd:");
			o.println("\tAND\tR0, R1\t; JudgeAnd:");
			o.println("\tMOV\tR1, (R6)+\t; JudgeAnd:");
		}
		o.println(";;; JudgeAnd completes");
	}
}
