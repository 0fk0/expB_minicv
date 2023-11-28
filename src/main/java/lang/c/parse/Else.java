package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Else extends CParseRule {
	// else ::= else ( statementIf ) statementBlock
	CParseRule statementIf, statementBlock;
	CToken ELSE;

	public Else(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_ELSE);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		ELSE = tk;

		tk = ct.getNextToken(pcx);
		if (StatementIf.isFirst(tk)){
			statementIf = new StatementIf(pcx);
			statementIf.parse(pcx);
		}

		tk = ct.getCurrentToken(pcx);
		if (StatementBlock.isFirst(tk)){
			statementBlock = new StatementBlock(pcx);
			statementBlock.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "elseの後には{文}が必要です");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (statementIf != null){
			statementIf.semanticCheck(pcx);
		}

		if (statementBlock != null){
			statementBlock.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementIf starts");
		if (statementIf != null){
			statementIf.codeGen(pcx);
		}
		if (statementBlock != null){
			statementBlock.codeGen(pcx);
		}
		o.println(";;; statementIf completes");
	}
}
