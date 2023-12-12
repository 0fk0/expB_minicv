package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class JudgeOr extends CParseRule {
    // JudgeOr ::= OR conditionAll
	CParseRule conditionL, conditionR;
	CToken op;

	public JudgeOr(CParseContext pcx, CParseRule conditionAll) {
		this.conditionL = conditionAll;
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_OR);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_OR) {
            op = tk;
		}

		tk = ct.getNextToken(pcx);

		if (ConditionAll.isFirst(tk)) {
            conditionR = new ConditionAll(pcx);
            conditionR.parse(pcx);
        } else {
			pcx.fatalError(tk.toExplainString() + "論理演算子の後ろはconditionAllです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (conditionL != null && conditionR == null){
			conditionL.semanticCheck(pcx);
			conditionR.semanticCheck(pcx);

			if (conditionL.getCType().getType() != CType.T_bool){
				pcx.fatalError(op.toExplainString() + "左辺の型がboolではありません");
			} else if (conditionR.getCType().getType() != CType.T_bool){
				pcx.fatalError(op.toExplainString() + "右辺の型がboolではありません");
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; JudgeOr starts");
		if (conditionL != null && conditionR != null) {
			conditionL.codeGen(pcx);
			conditionR.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; JudgeOr: 二つの真理値を取り出して論理和を取る");
			o.println("\tMOV\t-(R6), R1\t; JudgeOr:");
			o.println("\tOR\tR0, R1\t; JudgeOr:");
			o.println("\tMOV\tR1, (R6)+\t; JudgeOr:");
		}
		o.println(";;; JudgeOr completes");
	}
}
