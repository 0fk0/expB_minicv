package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.FatalErrorException;
import lang.RecoverableErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class StatementBlock extends CParseRule {
    // StatementBlock ::= LCUR statement RCUR
	CParseRule statement;
	CToken lcur, rcur;
	ArrayList<CParseRule> statementList = new ArrayList<CParseRule>();

	public StatementBlock(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_LCUR);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		lcur = tk;

		tk = ct.getNextToken(pcx);
		CParseRule statement = null;
		while (Statement.isFirst(tk)) {
			try {
				statement = new Statement(pcx);
				statement.parse(pcx);
				statementList.add(statement);
			} catch (RecoverableErrorException e){
				ct.skipTo(pcx, CToken.TK_SEMI, CToken.TK_RCUR);
				tk = ct.getNextToken(pcx);
			}
			tk = ct.getCurrentToken(pcx);
		}

		if (tk.getType() == CToken.TK_RCUR){
			rcur = tk;
			tk = ct.getNextToken(pcx);
		} else {
			pcx.warning(tk.toExplainString() + "{文}の最後の}を補完しました");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (!statementList.isEmpty()) {
			for (CParseRule statement : statementList) {
				statement.semanticCheck(pcx);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementBlock starts");
		if (!statementList.isEmpty()) {
			for (CParseRule statement : statementList) {
				statement.codeGen(pcx);
			}
		}
		o.println(";;; StatementBlock completes");
	}
}
