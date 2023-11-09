package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class StatementAssign extends CParseRule {
	// statementAssign ::= primary ASSIGN expression SEMI
	CParseRule primary, expression;
	CToken assign, semi;

	public StatementAssign(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (Primary.isFirst(tk));
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (Primary.isFirst(tk)){
			primary = new Primary(pcx);
			primary.parse(pcx);
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_ASSIGN) {
			assign = tk;
		} else {
			pcx.fatalError(tk.toExplainString() + "変数の後には=が必要です");
		}
		tk = ct.getNextToken(pcx);
		if (Expression.isFirst(tk)) {
			expression = new Expression(pcx);
			expression.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "=の後ろはexpressionです");
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_SEMI) {
			semi = tk;
		} else {
			pcx.fatalError(tk.toExplainString() + "代入文の最後には;が必要です");
		}
		tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primary != null && expression != null){
			primary.semanticCheck(pcx);
			expression.semanticCheck(pcx);

			if (primary.getCType() != expression.getCType()){
				pcx.fatalError(assign.toExplainString() + "左辺の型[" + primary.getCType().toString() + "]と右辺の型[" + expression.getCType().toString() + "]が一致しません");
			}
			if (primary.isConstant()){
				pcx.fatalError(assign.toExplainString() + "左辺が定数で代入できません");
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	// 	PrintStream o = pcx.getIOContext().getOutStream();
	// 	o.println(";;; variable starts");
	// 	if (ident != null) {
	// 		ident.codeGen(pcx);

	// 		if (array != null){
	// 			array.codeGen(pcx);
	// 			o.println("\tMOV\t-(R6), R0\t; StatementAssign: 配列名とindexを取り出して配列先頭アドレスとindex分を足し、内容を参照して、積む");
	// 			o.println("\tMOV\t-(R6), R1\t; StatementAssign:");
	// 			o.println("\tADD\tR0, R1\t; StatementAssign:");
	// 			o.println("\tMOV\tR1, (R6)+\t; StatementAssign:");
	// 		}
	// 	}
	// 	o.println(";;; variable completes");
	}
}
