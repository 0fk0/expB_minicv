package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class UnsignedFactor extends CParseRule {
	// unsignedFactor ::= factorAmp | number | LPAR expression RPAR | addressToValue
	CParseRule factorAmp, number, expression, addressToValue;
	CToken lpar, rpar;

	public UnsignedFactor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (FactorAmp.isFirst(tk) || Number.isFirst(tk) || tk.getType() == CToken.TK_LPAR || AddressToValue.isFirst(tk));
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
				expression.parse(pcx);
			} else {
				pcx.fatalError(tk.toExplainString() + "( の後ろはexpressionです");
			}

			tk = ct.getCurrentToken(pcx); // expressionの最後でnumberが読まれgetNextTokenされる
			if (tk.getType() == CToken.TK_RPAR) {
				rpar = tk;
			} else {
				pcx.fatalError(tk.toExplainString() + "(expressioin の後ろは ) です");
			}
			ct.getNextToken(pcx); // )は構文規則ではないので自動でトークンを次に移してくれない
		} else if (AddressToValue.isFirst(tk)){
			addressToValue = new AddressToValue(pcx);
			addressToValue.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "factorに続く構文はfactorAmpかnumberか(expression)かaddressToValueです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (factorAmp != null) {
			factorAmp.semanticCheck(pcx);
			setCType(factorAmp.getCType());
			setConstant(factorAmp.isConstant());
	 	} else if (number != null) {
			number.semanticCheck(pcx);
			setCType(number.getCType());
			setConstant(number.isConstant());
		} else if (expression != null) {
			expression.semanticCheck(pcx);
			setCType(expression.getCType());
			setConstant(expression.isConstant());
		} else if (addressToValue != null) {
			addressToValue.semanticCheck(pcx);
			setCType(addressToValue.getCType());
			setConstant(addressToValue.isConstant());
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
		} else if (addressToValue != null) {
			addressToValue.codeGen(pcx);
		}
		o.println(";;; factor completes");
	}
}