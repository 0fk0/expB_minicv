package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Statement extends CParseRule {
	// statement ::= statementAssign | statementIf | statementWhile | statementInput | statementOutput | statementBlock
	CParseRule statement;

	public Statement(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (StatementAssign.isFirst(tk) || StatementIf.isFirst(tk) || StatementWhile.isFirst(tk) || StatementInput.isFirst(tk) || StatementOutput.isFirst(tk) || StatementBlock.isFirst(tk));
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (StatementAssign.isFirst(tk)){
			statement = new StatementAssign(pcx);
			statement.parse(pcx);
		} else if (StatementIf.isFirst(tk)){
			statement = new StatementIf(pcx);
			statement.parse(pcx);
		} else if (StatementWhile.isFirst(tk)){
			statement = new StatementWhile(pcx);
			statement.parse(pcx);
		} else if (StatementInput.isFirst(tk)){
			statement = new StatementInput(pcx);
			statement.parse(pcx);
		} else if (StatementOutput.isFirst(tk)){
			statement = new StatementOutput(pcx);
			statement.parse(pcx);
		} else if (StatementBlock.isFirst(tk)){
			statement = new StatementBlock(pcx);
			statement.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (statement != null) {
			statement.semanticCheck(pcx);
			setCType(statement.getCType());
			setConstant(statement.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statement starts");
		if (statement != null) {
			statement.codeGen(pcx);
		}
		o.println(";;; statement completes");
	}
}
