package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class StatementIf extends CParseRule {
	// statementIf ::= IF conditionBlock statementBlock ELSE
	CParseRule conditionBlock, statementBlock, ELSE;
	CToken IF;

	public StatementIf(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_IF);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		IF = tk;

		tk = ct.getNextToken(pcx);
		if (ConditionBlock.isFirst(tk)){
			conditionBlock = new ConditionBlock(pcx);
			conditionBlock.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "ifの後には(条件式)が必要です");
		}

		tk = ct.getCurrentToken(pcx);
		if (StatementBlock.isFirst(tk)){
			statementBlock = new StatementBlock(pcx);
			statementBlock.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "if(条件式)の後には{文}が必要です");
		}

		tk = ct.getCurrentToken(pcx);
		if (Else.isFirst(tk)){
			ELSE = new Else(pcx);
			ELSE.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (conditionBlock != null && statementBlock != null){
			conditionBlock.semanticCheck(pcx);
			statementBlock.semanticCheck(pcx);
		}

		if (ELSE != null){
			ELSE.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementIf starts");
		if (conditionBlock != null && statementBlock != null){
			conditionBlock.codeGen(pcx);
			statementBlock.codeGen(pcx);
		}

		if (ELSE != null){
			ELSE.codeGen(pcx);
		}
		o.println(";;; statementIf completes");
	}
}
