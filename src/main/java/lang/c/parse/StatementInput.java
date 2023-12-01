package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class StatementInput extends CParseRule {
	// statementInput ::= INPUT primary SEMI
	CParseRule primary;
	CToken input, semi;

	public StatementInput(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_INPUT);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		input = tk;

		tk = ct.getNextToken(pcx);
		if (Primary.isFirst(tk)) {
			primary = new Variable(pcx);
			primary.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "INPUTの後ろにはprimaryが必要です");
		}

		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_SEMI) {
			semi = tk;
		} else {
			pcx.fatalError(tk.toExplainString() + "入力文の最後には;が必要です");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (input != null && primary != null && semi != null){
			primary.semanticCheck(pcx);
		}

		if (primary.isConstant()) {
			pcx.fatalError("入力文の左辺が定数です");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementInput starts");
		if (input != null && primary != null && semi != null) {
			primary.codeGen(pcx);
			o.println("\tMOV\t#0xFFE0, (R6)+\t; statementInput: 入出力番地を右辺にセット");

			o.println("\tMOV\t-(R6), R0\t; statementInput: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入");
			o.println("\tMOV\t-(R6), R1\t; statementInput:");
			o.println("\tMOV\tR0, (R1)\t; statementInput:");
		}
		o.println(";;; statementInput completes");
	}
}
