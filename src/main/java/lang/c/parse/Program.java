package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.*;
import lang.c.*;

public class Program extends CParseRule {
	// program ::= { statement } EOF
	CParseRule program; // コード生成テストでのエラー回避のために残している変数
	ArrayList<CParseRule> statementList = new ArrayList<CParseRule>();

	public Program(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return Statement.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CParseRule statement = null;
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		while (Statement.isFirst(tk)) {
			statement = new Statement(pcx);
			statement.parse(pcx);
			statementList.add(statement);
			tk = ct.getCurrentToken(pcx);
		}

		if (tk.getType() != CToken.TK_EOF) {
			pcx.fatalError(tk.toExplainString() + "プログラムの最後にゴミがあります");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (!statementList.isEmpty()) {
			for (CParseRule statement : statementList) {
				statement.semanticCheck(pcx);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; program starts");
		o.println("\t. = 0x100");
		o.println("\tJMP\t__START\t; ProgramNode: 最初の実行文へ");
		// (ここには将来、宣言に対するコード生成が必要)
		o.println("__START:");
		o.println("\tMOV\t#0x1000, R6\t; ProgramNode: 計算用スタック初期化");
		// o.println("\tMOV\t-(R6), R0\t; ProgramNode: 計算結果確認用"); // 代入分が完成したため不要

		if (!statementList.isEmpty()) {
			for (CParseRule statement : statementList) {
				statement.codeGen(pcx);
			}
		}
		o.println("\tHLT\t\t\t; ProgramNode:");
		o.println("\t.END\t\t\t; ProgramNode:");
		o.println(";;; program completes");
	}
}
