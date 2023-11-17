package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Condition extends CParseRule {
    // Condition ::= TRUE | FALSE | expression ( conditionLT | conditionLE | conditionGT | conditionGE | conditionEQ | conditionNE )
	CParseRule expression, condition;
	CToken bool;

	public Condition(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_TRUE || tk.getType() == CToken.TK_FALSE || Expression.isFirst(tk));
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_TRUE || tk.getType() == CToken.TK_FALSE) {
            bool = tk;
            tk = ct.getNextToken(pcx);
		} else if (Expression.isFirst(tk)) {
            expression = new Expression(pcx);
            expression.parse(pcx);

            switch (tk.getType()) {
                case CToken.TK_LT:
                    condition = new ConditionLT(pcx, expression);
                    break;
                case CToken.TK_LE:
                    condition = new ConditionLE(pcx, expression);
                    break;
                case CToken.TK_GT:
                    condition = new ConditionGT(pcx, expression);
                    break;
                case CToken.TK_GE:
                    condition = new ConditionGE(pcx, expression);
                    break;
                case CToken.TK_EQ:
                    condition = new ConditionEQ(pcx, expression);
                    break;
                case CToken.TK_NE:
                    condition = new ConditionNE(pcx, expression);
                    break;
            }
            condition.parse(pcx);

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
