package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class StatementWhile extends CParseRule {
	// statementWhile ::= WHILE ConditionBlcok Statement
	CParseRule conditionBlock, statement;
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
			if (Statement.isFirst(tk)){
				statement = new Statement(pcx);
				statement.parse(pcx);
			} else {
				pcx.recoverableError(tk.toExplainString() + "while(条件式)の後にはstatementが必要です");
			}
		} else {
			pcx.recoverableError(tk.toExplainString() + "whileの後には(条件式)が必要です");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (conditionBlock != null && statement != null){
			conditionBlock.semanticCheck(pcx);
			statement.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementWhile starts");
		if (conditionBlock != null && statement != null) {
			int seq = pcx.getSeqId();
			o.println("WHILE" + seq + ":\t; StatementWhile:");
			conditionBlock.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; StatementWhile:真理値を取り出す");
			o.println("\tBRZ\tENDWHILE"+ seq +"\t;");
			statement.codeGen(pcx);
			o.println("\tJMP\tWHILE"+ seq +"\t;");
			o.println("ENDWHILE" + seq + ":\t; StatementWhile:");
		}
		o.println(";;; statementWhile completes");
	}
}
