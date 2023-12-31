package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class ConditionBlock extends CParseRule {
    // ConditionBlock ::= LPAR judge RPAR
	CParseRule judge;
	CToken lpar, rpar;

	public ConditionBlock(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_LPAR);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		lpar = tk;

		tk = ct.getNextToken(pcx);
		if (Judge.isFirst(tk)){
			judge = new Judge(pcx);
			judge.parse(pcx);

			tk = ct.getCurrentToken(pcx);
			if (tk.getType() == CToken.TK_RPAR){
				rpar = tk;
				tk = ct.getNextToken(pcx);
			} else {
				pcx.fatalError(tk.toExplainString() + "条件式/真理値の後には)が必要です");
			}
		} else {
			pcx.fatalError(tk.toExplainString() + "(の後には条件式/真理値が必要です");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (judge != null){
			judge.semanticCheck(pcx);
			setCType(judge.getCType());
			setConstant(judge.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; ConditionBlock starts");
		if (judge != null){
			judge.codeGen(pcx);
		}
		o.println(";;; ConditionBlock completes");
	}
}
