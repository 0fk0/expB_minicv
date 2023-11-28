package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class StatementBlock extends CParseRule {
    // StatementBlock ::= LCUR statement RCUR
	CParseRule statement;
	CToken lcur, rcur;

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
		if (Statement.isFirst(tk)){
			statement = new Condition(pcx);
			statement.parse(pcx);

			tk = ct.getCurrentToken(pcx);
			if (tk.getType() == CToken.TK_RCUR){
				rcur = tk;
				tk = ct.getNextToken(pcx);
			} else {
				pcx.fatalError(tk.toExplainString() + "文の後には}が必要です");
			}
		} else {
			pcx.fatalError(tk.toExplainString() + "{の後には文が必要です");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (statement != null){
			statement.semanticCheck(pcx);
			setCType(statement.getCType());
			setConstant(statement.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementBlock starts");
		if (statement != null){
			statement.codeGen(pcx);
		}
		o.println(";;; StatementBlock completes");
	}
}
