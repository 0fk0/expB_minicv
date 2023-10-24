package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class TermDiv extends CParseRule {
	// termDiv ::= DIV factor
	CToken op;
	CParseRule factor;
	CParseRule left, right;

	public TermDiv(CParseContext pcx, CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_DIV);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// 割り算の型計算規則
		final int s[][] = {
			// T_err T_int T_pint
			{ CType.T_err, CType.T_err, CType.T_err },  // T_err
			{ CType.T_err, CType.T_int, CType.T_err }, // T_int
			{ CType.T_err, CType.T_err, CType.T_err }, // T_pint
		};
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			int lt = left.getCType().getType(); // /の左辺の型
			int rt = right.getCType().getType(); // /の右辺の型
			int nt = s[lt][rt]; // 規則による型計算
			if (nt == CType.T_err) {
				pcx.fatalError(op.toExplainString() + "左辺の型[" + left.getCType().toString() + "]を右辺の型["
						+ right.getCType().toString() + "]で割れません");
			}
			this.setCType(CType.getCType(nt));
			this.setConstant(left.isConstant() && right.isConstant()); // +の左右両方が定数のときだけ定数
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (factor != null) {
			factor.semanticCheck(pcx);
			this.setCType(factor.getCType()); // factor の型をそのままコピー
			this.setConstant(factor.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		if (left != null && right != null) {
			left.codeGen(pcx); // 左部分木のコード生成を頼む
			right.codeGen(pcx); // 右部分木のコード生成を頼む
			o.println("\tMOV\t-(R6), R0\t; ExpressionAdd: ２数を取り出して、足し、積む<" + op.toExplainString() + ">");
			o.println("\tMOV\t-(R6), R1\t; ExpressionAdd:");
			o.println("\tADD\tR1, R0\t; ExpressionAdd:");
			o.println("\tMOV\tR0, (R6)+\t; ExpressionAdd:");
		}
	}
}
