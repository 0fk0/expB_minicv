package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Judge extends CParseRule {
	// judge		::=	conditionAllPriority { judgeOr }
	CParseRule judge;

	public Judge(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return ConditionAllPriority.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CParseRule condition = null, list = null;
		condition = new ConditionAllPriority(pcx);
		condition.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		while (JudgeOr.isFirst(tk)) {
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
