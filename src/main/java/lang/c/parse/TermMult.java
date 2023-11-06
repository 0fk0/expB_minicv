package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class TermMult extends CParseRule {
	// termMult ::= MULT factor
	CToken op;
	CParseRule left, right;

	public TermMult(CParseContext pcx, CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_MULT);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// *の次の字句を読む
		CToken tk = ct.getNextToken(pcx);
		if (Factor.isFirst(tk)) {
			right = new Factor(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "*の後ろはfactorです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 掛け算の型計算規則
		final int s[][] = {
			// T_err T_int T_pint T_int_array T_pint_array
			{ CType.T_err, CType.T_err, CType.T_err}, // T_err
			{ CType.T_err, CType.T_int, CType.T_err}, // T_int
			{ CType.T_err, CType.T_err, CType.T_err}  // T_pint
		};
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			int lt = left.getCType().getType(); // *の左辺の型
			int rt = right.getCType().getType(); // *の右辺の型
			int nt = s[lt][rt]; // 規則による型計算
			if (nt == CType.T_err) {
				pcx.fatalError(op.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型["
						+ right.getCType().toString() + "]は掛けられません");
			} else {
				this.setCType(CType.getCType(nt));
				this.setConstant(left.isConstant() && right.isConstant()); // *の左右両方が定数のときだけ定数
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (left != null && right != null) {
			left.codeGen(pcx); // 左部分木のコード生成を頼む
			right.codeGen(pcx); // 右部分木のコード生成を頼む
			o.println("\tJSR\tMUL\t; TermMult: R2に戻り値を入れるとする");
			o.println("\tSUB\t#2, R6; TermMult: 引数の分を戻す");
			o.println("\tMOV\tR2, (R6)+\t; TermMult:");
		}
	}
}
