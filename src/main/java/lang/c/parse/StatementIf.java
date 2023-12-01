package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class StatementIf extends CParseRule {
	// statementIf ::= IF conditionBlock statement1 [ ELSE statement2 ]
	CParseRule conditionBlock, statement1, statement2;
	CToken IF, ELSE;

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
		if (Statement.isFirst(tk)){
			statement1 = new Statement(pcx);
			statement1.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "if(条件式)の後にはstatementが必要です");
		}

		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_ELSE){
			ELSE = tk;

			tk = ct.getNextToken(pcx);
			if (Statement.isFirst(tk)){
				statement2 = new Statement(pcx);
				statement2.parse(pcx);
			} else {
				pcx.fatalError(tk.toExplainString() + "elseの後にはstatementが必要です");
			}
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (conditionBlock != null && statement1 != null){
			conditionBlock.semanticCheck(pcx);
			statement1.semanticCheck(pcx);
		}

		if (ELSE != null && statement2 != null){
			statement2.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementIf starts");
		int seq = pcx.getSeqId();
		if (conditionBlock != null && statement1 != null){
			conditionBlock.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; StatementIF:真理値を取り出す");
			if (ELSE != null && statement2 != null){
				o.println("\tBRZ\tELSEIF"+ seq +"\t;");
			} else {
				o.println("\tBRZ\tENDIF"+ seq +"\t;");
			}
			statement1.codeGen(pcx);

			if (ELSE != null && statement2 != null){
				o.println("ELSEIF" + seq + ":\t; StatementIF:");
				statement2.codeGen(pcx);
			} else {
				o.println("ENDIF" + seq + ":\t; StatementIF:");
			}
		}

		if (ELSE != null){
			statement2.codeGen(pcx);
		}
		o.println(";;; statementIf completes");
	}
}
