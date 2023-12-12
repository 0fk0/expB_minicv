package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class ConditionNT extends CParseRule {
    // ConditionNT ::= NT conditionAll
	CParseRule condition;
	CToken op;

	public ConditionNT(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_NT);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_NT) {
            op = tk;
		}

		tk = ct.getNextToken(pcx);

		if (ConditionAll.isFirst(tk)) {
            condition = new ConditionAll(pcx);
            condition.parse(pcx);
        } else {
			pcx.fatalError(tk.toExplainString() + "NTの後ろはconditionAllです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (condition != null){
			condition.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; !(condition) starts");
		if (condition != null) {
			condition.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; ConditionNT: 真理値を取り出して反転");
			o.println("\tXOR\t#0x0001, R0\t; ConditionNT: 反転");
			o.println("\tMOV\tR0, (R6)+\t; ConditionNT:");
		}
		o.println(";;; !(condition) completes");
	}
}
