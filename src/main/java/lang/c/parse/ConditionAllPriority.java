package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class ConditionAllPriority extends CParseRule {
	// conditionAllPriority	::=	conditionAll { judgeOAnd }
	CParseRule priority;

	public ConditionAllPriority(CParseContext pcx) {
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
		while (JudgeAnd.isFirst(tk)) {
			if (JudgeAnd.isFirst(tk)) list = new JudgeAnd(pcx, condition);
			list.parse(pcx);
			condition = list;
			tk = ct.getCurrentToken(pcx);
		}
		priority = condition;
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (priority != null) {
			priority.semanticCheck(pcx);
			this.setCType(priority.getCType());
			this.setConstant(priority.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; expression starts");
		if (priority != null) {
			priority.codeGen(pcx);
		}
		o.println(";;; expression completes");
	}
}
