package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class UndesignedFactor extends CParseRule {
	// unsignedFactor ::= factorAmp | number | LPAR expression RPAR
	CParseRule factorAmp, number, expression;
	CToken lpar, rpar;

	public UndesignedFactor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (FactorAmp.isFirst(tk) || Number.isFirst(tk) || tk.getType() == CToken.TK_LPAR);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (Number.isFirst(tk)){
			number = new Number(pcx);
			number.parse(pcx);
		} else if (FactorAmp.isFirst(tk)){
			factorAmp = new FactorAmp(pcx);
			factorAmp.parse(pcx);
		} else if (tk.getType() == CToken.TK_LPAR){
			lpar = tk;
			// ( の次の字句を読む
			tk = ct.getNextToken(pcx);
			if (Expression.isFirst(tk)) {
				expression = new Expression(pcx);
			} else {
				pcx.fatalError(tk.toExplainString() + "( の後ろはexpressionです");
			}

			tk = ct.getCurrentToken(pcx); // expressionの最後でnumberが読まれgetNextTokenされる
			if (tk.getType() == CToken.TK_RPAR) {
				rpar = tk;
			} else {
				pcx.fatalError(tk.toExplainString() + "(expressioin の後ろは ) です");
			}

			expression.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "factorに続く構文はfactorAmpかnumberか(expression)です");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (number != null) {
			number.semanticCheck(pcx);
			setCType(number.getCType()); // number の型をそのままコピー
			setConstant(number.isConstant()); // number は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factor starts");
		if (number != null) {
			number.codeGen(pcx);
		} else if (factorAmp != null) {
			factorAmp.codeGen(pcx);
		} else if (lpar != null && rpar != null && expression != null) {
			expression.codeGen(pcx);
		}
		o.println(";;; factor completes");
	}
}