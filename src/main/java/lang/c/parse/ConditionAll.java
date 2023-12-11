package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class ConditionAll extends CParseRule {
    // ConditionAll ::= condition | conditionNT
	CParseRule condition;

	public ConditionAll(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (Condition.isFirst(tk) || ConditionNT.isFirst(tk));
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (Condition.isFirst(tk)) {
			condition = new Condition(pcx);
			condition.parse(pcx);
		} else if (ConditionNT.isFirst(tk)) {
            condition = new ConditionNT(pcx);
			condition.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (condition != null){
			condition.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; conditionAll starts");
		if (condition != null) {
			condition.codeGen(pcx);
		}
		o.println(";;; conditionAll completes");
	}
}
