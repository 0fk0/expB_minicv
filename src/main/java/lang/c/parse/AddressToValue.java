package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class AddressToValue extends CParseRule {
	// adressToValue ::= primary
	CParseRule primary;

	public AddressToValue(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return (Primary.isFirst(tk));
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; addressToValue starts");
		if (primary != null) {
			primary.codeGen(pcx);
		}
		o.println(";;; adressTovalue completes");
	}
}
