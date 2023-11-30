package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class StatementWhile extends CParseRule {
	// statementWhile ::= WHILE ConditionBlcok StatementBlock
	CParseRule conditionBlock, statementBlock;
	CToken WHILE;

	public StatementWhile(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_WHILE);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		WHILE = tk;

		tk = ct.getNextToken(pcx);
		if (ConditionBlock.isFirst(tk)){
			conditionBlock = new ConditionBlock(pcx);
			conditionBlock.parse(pcx);

			tk = ct.getCurrentToken(pcx);
			if (StatementBlock.isFirst(tk)){
				statementBlock = new StatementBlock(pcx);
				statementBlock.parse(pcx);
			} else {
				pcx.fatalError(tk.toExplainString() + "while(条件式)の後には{文}が必要です");
			}
		} else {
			pcx.fatalError(tk.toExplainString() + "whileの後には(条件式)が必要です");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (conditionBlock != null && statementBlock != null){
			conditionBlock.semanticCheck(pcx);
			statementBlock.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementWhile starts");
		int seq = pcx.getSeqId();
		if (conditionBlock != null && statementBlock != null) {
			o.println("WHILE" + seq + ":\t; StatementWhile:");
			conditionBlock.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; StatementWhile:真理値を取り出す");
			o.println("\tBRZ\tENDWHILE"+ seq +"\t;");
			statementBlock.codeGen(pcx);
			o.println("\tJMP\tWHILE"+ seq +"\t;");
			o.println("ENDWHILE" + seq + ":\t; StatementWhile:");
		}
		o.println(";;; statementWhile completes");
	}
}
