package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class StatementOutput extends CParseRule {
	// statementInput ::= OUTPUT number
	CParseRule number;
	CToken output;

	public StatementOutput(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_OUTPUT);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		output = tk;

		tk = ct.getNextToken(pcx);
		if (Number.isFirst(tk)) {
			number = new Number(pcx);
			number.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "OUTPUTの後ろにはnumberが必要です");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (output != null && number != null){
			number.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementOutput starts");
		if (output != null && number != null) {
			o.println("\tMOV\t#0xFFE0, (R6)+\t; statementOutput: 入出力番地を左辺にセット");
			number.codeGen(pcx);

			o.println("\tMOV\t-(R6), R0\t; statementOutput: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入");
			o.println("\tMOV\t-(R6), R1\t; statementOutput:");
			o.println("\tMOV\tR0, (R1)\t; statementOutput:");
		}
		o.println(";;; statementOutput completes");
	}
}
