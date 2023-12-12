package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Judge extends CParseRule {
	// judge ::= conditionAll { judgeAnd | judgeOr }
	CParseRule judge;

	public Judge(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return ConditionAll.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CParseRule condition = null, list = null;
		condition = new ConditionAll(pcx);
		condition.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		while (JudgeAnd.isFirst(tk) || JudgeOr.isFirst(tk)) {
			if (JudgeAnd.isFirst(tk)) list = new JudgeAnd(pcx, condition);
			if (JudgeOr.isFirst(tk)) list = new JudgeOr(pcx, condition);
			list.parse(pcx);
			condition = list;
			tk = ct.getCurrentToken(pcx);
		}
		judge = condition;
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (judge != null) {
			judge.semanticCheck(pcx);
			this.setCType(judge.getCType());
			this.setConstant(judge.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; expression starts");
		if (judge != null) {
			judge.codeGen(pcx);
		}
		o.println(";;; expression completes");
	}
}
