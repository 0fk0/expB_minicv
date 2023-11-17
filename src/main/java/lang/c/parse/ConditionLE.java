package lang.c.parse;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class ConditionLE extends CParseRule {
    // ConditionLE ::= LE expression
	CParseRule expression;
	CToken op;

	public ConditionLE(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_LE);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_LE) {
            op = tk;
            tk = ct.getNextToken(pcx);
		} else if (Expression.isFirst(tk)) {
            expression = new Expression(pcx);
            expression.parse(pcx);
        }
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// if (primary != null && expression != null){
		// 	primary.semanticCheck(pcx);
		// 	expression.semanticCheck(pcx);

		// 	if (primary.getCType() != expression.getCType()){
		// 		pcx.fatalError(assign.toExplainString() + "左辺の型[" + primary.getCType().toString() + "]と右辺の型[" + expression.getCType().toString() + "]が一致しません");
		// 	}
		// 	if (primary.isConstant()){
		// 		pcx.fatalError(assign.toExplainString() + "左辺が定数で代入できません");
		// 	}
		// }
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		// PrintStream o = pcx.getIOContext().getOutStream();
		// o.println(";;; statementAssign starts");
		// if (primary != null && expression != null) {
		// 	primary.codeGen(pcx);
		// 	expression.codeGen(pcx);

		// 	o.println("\tMOV\t-(R6), R0\t; statementAssign: 左辺の変数アドレスと右辺の値を取り出して、右辺の値を左辺の変数アドレスに代入");
		// 	o.println("\tMOV\t-(R6), R1\t; statementAssign:");
		// 	o.println("\tMOV\tR0, (R1)\t; statementAssign:");
		// }
		// o.println(";;; statementAssign completes");
	}
}