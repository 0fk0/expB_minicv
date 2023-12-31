package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Ident extends CParseRule {
	// ident ::= IDENT
	CToken ident;

	public Ident(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (tk.getType() == CToken.TK_IDENT);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		ident = tk;
		tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		String ident_type = "";
		String ident_str = ident.getText();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ident_str.length(); i++){
			char ident_char = ident_str.charAt(i);
			if (ident_char ==  '_'){
				ident_type = sb.toString();
				break;
			} else {
				sb.append(ident_char);
			}
		}

		CType settingType = null;
		boolean isConstant = true;
		switch (ident_type){
			case "i":
				settingType = CType.getCType(CType.T_int);
				isConstant = false;
				break;

			case "ip":
				settingType = CType.getCType(CType.T_pint);
				isConstant = false;
				break;

			case "ia":
				settingType = CType.getCType(CType.T_int_array);
				isConstant = false;
				break;

			case "ipa":
				settingType = CType.getCType(CType.T_pint_array);
				isConstant = false;
				break;

			case "c":
				settingType = CType.getCType(CType.T_int);
				isConstant = true;
				break;
			default:
				settingType = CType.getCType(CType.T_err);
				pcx.fatalError("予期されない識別子です");
				break;
		}

		this.setCType(settingType);
		this.setConstant(isConstant);
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; number starts");
		if (ident != null) {
			o.println("\tMOV\t#" + ident.getText() + ", (R6)+\t; Ident: 変数アドレスを積む<" + ident.toExplainString() + ">");
		}
		o.println(";;; number completes");
	}
}
