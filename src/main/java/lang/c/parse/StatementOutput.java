package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class StatementOutput extends CParseRule {
	// statementOutput ::= OUTPUT expression SEMI
	CParseRule expression;
	CToken output, semi;

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
		if (Expression.isFirst(tk)) {
			expression = new Expression(pcx);
			expression.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "OUTPUTの後ろにはexpressionが必要です");
		}

		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_SEMI) {
			semi = tk;
		} else {
			pcx.fatalError(tk.toExplainString() + "出力文の最後には;が必要です");
		}

		tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (output != null && expression != null && semi != null){
			expression.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementOutput starts");
		if (output != null && expression != null && semi != null) {
			o.println("\tMOV\t#0xFFE0, (R6)+\t; statementOutput: 入出力番地を左辺にセット");
			expression.codeGen(pcx);

			o.println("\tMOV\t-(R6), R0\t; statementOutput: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入");
			o.println("\tMOV\t-(R6), R1\t; statementOutput:");
			o.println("\tMOV\tR0, (R1)\t; statementOutput:");
		}
		o.println(";;; statementOutput completes");
	}
}
