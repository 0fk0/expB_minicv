package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

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
					condition.parse(pcx);
                    break;
                case CToken.TK_LE:
                    condition = new ConditionLE(pcx, expression);
					condition.parse(pcx);
                    break;
                case CToken.TK_GT:
                    condition = new ConditionGT(pcx, expression);
					condition.parse(pcx);
                    break;
                case CToken.TK_GE:
                    condition = new ConditionGE(pcx, expression);
					condition.parse(pcx);
                    break;
                case CToken.TK_EQ:
                    condition = new ConditionEQ(pcx, expression);
					condition.parse(pcx);
                    break;
                case CToken.TK_NE:
                    condition = new ConditionNE(pcx, expression);
					condition.parse(pcx);
                    break;
            }

        }
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (bool == null){
			if (expression != null){
				expression.semanticCheck(pcx);
			}
			if (condition != null){
				condition.semanticCheck(pcx);
			}
		} else {
			this.setCType(CType.getCType(CType.T_bool));
			this.setConstant(true);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; Condition starts");
		if (expression != null) {;
			expression.codeGen(pcx);

			if (condition != null) {
				condition.codeGen(pcx);
			}
		}
		o.println(";;; Condition completes");
	}
}
