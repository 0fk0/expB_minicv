package lang.c;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import lang.*;

public class CTokenizer extends Tokenizer<CToken, CParseContext> {
	@SuppressWarnings("unused")
	private CTokenRule rule;
	private int lineNo, colNo;
	private char backCh;
	private boolean backChExist = false;

	// 各状態
	private final int FIRST_STATE = 0;
	private final int EOF_STATE = 1;
	private final int OTHER_STATE = 2;
	private final int DECIMAL_STATE = 3;
	private final int PLUS_STATE = 4;
	private final int MINUS_STATE = 5;
	private final int ASTER_STATE = 6;
	private final int SLASH_STATE = 7;
	private final int COMMENT_ASTER_CLOSE_STATE = 8;
	private final int COMMENT_SLASH_STATE = 9;
	private final int COMMENT_ASTER_STATE = 10;
	private final int HEX_OR_OCTAL_STATE = 11;
	private final int HEX_STATE = 12;
	private final int OCTAL_STATE = 13;
	private final int IDENT_STATE = 14;
	private final int AND_STATE = 15;
	private final int LPAR_STATE = 16;
	private final int RPAR_STATE = 17;
	private final int LBRA_STATE = 18;
	private final int RBRA_STATE = 19;
	private final int LCUR_STATE = 20;
	private final int RCUR_STATE = 21;
	private final int ASSIGN_STATE = 22;
	private final int SEMI_STATE = 23;
	private final int LESS_STATE = 24;
	private final int GREAT_STATE = 25;
	private final int NOT_STATE = 26;
	private final int BAR_STATE = 27;



	public CTokenizer(CTokenRule rule) {
		this.rule = rule;
		lineNo = 1;
		colNo = 1;
	}

	private InputStream in;
	private PrintStream err;

	private char readChar() {
		char ch;
		if (backChExist) {
			ch = backCh;
			backChExist = false;
		} else {
			try {
				ch = (char) in.read();
			} catch (IOException e) {
				e.printStackTrace(err);
				ch = (char) -1;
			}
		}
		++colNo;
		if (ch == '\n') {
			colNo = 1;
			++lineNo;
		}
		return ch;
	}

	private void backChar(char c) {
		backCh = c;
		backChExist = true;
		--colNo;
		if (c == '\n') {
			--lineNo;
		}
	}

	// 現在読み込まれているトークンを返す
	private CToken currentTk = null;

	public CToken getCurrentToken(CParseContext pctx) {
		return currentTk;
	}

	// 次のトークンを読んで返す
	public CToken getNextToken(CParseContext pctx) {
		in = pctx.getIOContext().getInStream();
		err = pctx.getIOContext().getErrStream();
		currentTk = readToken();
		return currentTk;
	}

	// 指定トークンまで読み飛ばす
	public CToken skipTo(CParseContext pctx, int type1, int type2) {
		while (!(this.currentTk.getType() == type1 || this.currentTk.getType() == type2)) {
			this.getNextToken(pctx);
		}
		
		return currentTk;
	}

	private CToken readToken() {
		CToken tk = null;
		char ch;
		int startCol = colNo;
		int lineNo_EOF = lineNo;
		StringBuffer text = new StringBuffer();

		int state = 0;
		int num_count = 0;
		char first_ch_8 = ' ';
		boolean accept = false;
		while (!accept) {
			switch (state) {
				case FIRST_STATE: // 初期状態
					ch = readChar();
					if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
					} else if (ch == (char) -1) { // EOF
						startCol = colNo - 1;
						state = EOF_STATE;
					} else if (ch == '0') { 	  // 16進数 or 8進数 or 10進数0単体
						startCol = colNo - 1;
						text.append(ch);
						state = HEX_OR_OCTAL_STATE;
					} else if ('1' <= ch && ch <= '9') { // 数（10進数）の開始
						startCol = colNo - 1;
						text.append(ch);
						state = DECIMAL_STATE;
					} else if (('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ch == '_') { // 識別子の開始
						startCol = colNo - 1;
						text.append(ch);
						state = IDENT_STATE;
					} else if (ch == '+') { // 和
						startCol = colNo - 1;
						text.append(ch);
						state = PLUS_STATE;
					} else if (ch == '-') { // 差
						startCol = colNo - 1;
						text.append(ch);
						state = MINUS_STATE;
					} else if (ch == '*') { // 積	
						startCol = colNo - 1;
						text.append(ch);
						state = ASTER_STATE;
					} else if (ch == '/'){ // コメント化状態
						state = SLASH_STATE;
					} else if (ch == '&'){ // コメント化状態
						startCol = colNo - 1;
						state = AND_STATE;
					} else if (ch == '('){ // ( 開始
						startCol = colNo - 1;
						state = LPAR_STATE;
					} else if (ch == ')'){ // ( 終了
						startCol = colNo - 1;
						state = RPAR_STATE;
					} else if (ch == '['){ // [ 開始
						startCol = colNo - 1;
						state = LBRA_STATE;
					} else if (ch == ']'){ // ] 終了
						startCol = colNo - 1;
						state = RBRA_STATE;
					} else if (ch == '{'){ // { 開始
						startCol = colNo - 1;
						state = LCUR_STATE;
					} else if (ch == '}'){ // } 終了
						startCol = colNo - 1;
						state = RCUR_STATE;
					} else if (ch == '='){ //  等号
						startCol = colNo - 1;
						state = ASSIGN_STATE;
					} else if (ch == '<'){ //  小なり
						startCol = colNo - 1;
						state = LESS_STATE;
					} else if (ch == '>'){ //  大なり
						startCol = colNo - 1;
						state = GREAT_STATE;
					} else if (ch == '!'){ //  エクスクラメーションマーク
						startCol = colNo - 1;
						state = NOT_STATE;
					} else if (ch == ';'){ //  セミコロン
						startCol = colNo - 1;
						state = SEMI_STATE;
					} else if (ch == '|'){ //  縦線
						startCol = colNo - 1;
						state = BAR_STATE;
					} else { // ヘンな文字を読んだ
						startCol = colNo - 1;
						text.append(ch);
						state = OTHER_STATE;
					}
					break;
				case EOF_STATE: // EOFを読んだ
					tk = new CToken(CToken.TK_EOF, lineNo, startCol, "end_of_file");
					accept = true;
					break;
				case OTHER_STATE: // ヘンな文字を読んだ
					tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
					accept = true;
					break;
				case DECIMAL_STATE: // 10進数状態
					ch = readChar();
					if (Character.isDigit(ch)) {
						text.append(ch);
					} else {
						// 数の終わり
						backChar(ch); // 数を表さない文字は戻す（読まなかったことにする

						String num_10_str = text.toString();
						int num_10_int = Integer.parseInt(num_10_str);
						if (-(1 << 15) <= num_10_int && num_10_int <= (1 << 15)) {
							tk = new CToken(CToken.TK_NUM, lineNo, startCol, num_10_str);
							accept = true;
						} else {
							state = OTHER_STATE; // オーバーフロー
						}
					}
					break;
				case HEX_OR_OCTAL_STATE: // 16進数状態　or 8進数状態　or 10進数0単体
					ch = readChar();
					if (ch == 'x'){
						text.append(ch);
						num_count = 0;
						state = HEX_STATE;
					} else if ('0' <= ch && ch <= '7') {
						backChar(ch);
						num_count = 0;
						state = OCTAL_STATE;
					} else {
						backChar(ch);
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
				case HEX_STATE: // 16進数状態
					ch = readChar();
					num_count++;
					if ('0' <= ch && ch <= '9' || 'A' <= ch && ch <= 'F' || 'a' <= ch && ch <= 'f') {
						text.append(ch);
					} else {
						// 数の終わり
						backChar(ch); 	// 数を表さない文字は戻す（読まなかったことにする）
						num_count--;
						// 0xのみの場合 | オーバーフロー
						if (num_count == 0 || num_count > 4){
							state = OTHER_STATE;
							continue;
						}

						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
				case OCTAL_STATE: // 8進数状態
					ch = readChar();
					num_count++;
					if (num_count == 1) first_ch_8 = ch;

					if ('0' <= ch && ch <= '7') {
						text.append(ch);
					} else {
						// 数の終わり
						backChar(ch); // 数を表さない文字は戻す（読まなかったことにする）
						num_count--;
						// オーバーフロー
						if (num_count >= 6 && !(num_count == 6 && first_ch_8 == '1')){
							state = OTHER_STATE;
							continue;
						}

						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
				case IDENT_STATE: // 識別子状態
					ch = readChar();
					if (('0' <= ch && ch <= '9') || ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ch == '_') {
						text.append(ch);
					} else {
						// 識別子の終わり
						backChar(ch); // 英数字か_を表さない文字は戻す（読まなかったことにする

						// 識別子 と true/false の判定
						if (text.toString().equals("true")) {
							tk = new CToken(CToken.TK_TRUE, lineNo, startCol, "true");
						} else if (text.toString().equals("false")) {
							tk = new CToken(CToken.TK_FALSE, lineNo, startCol, "false");
						} else if (text.toString().equals("if")) {
							tk = new CToken(CToken.TK_IF, lineNo, startCol, "if");
						} else if (text.toString().equals("else")) {
							tk = new CToken(CToken.TK_ELSE, lineNo, startCol, "else");
						} else if (text.toString().equals("while")) {
							tk = new CToken(CToken.TK_WHILE, lineNo, startCol, "while");
						} else if (text.toString().equals("input")) {
							tk = new CToken(CToken.TK_INPUT, lineNo, startCol, "input");
						} else if (text.toString().equals("output")) {
							tk = new CToken(CToken.TK_OUTPUT, lineNo, startCol, "output");
						} else {
							tk = new CToken(CToken.TK_IDENT, lineNo, startCol, text.toString());
						}
						accept = true;
					}
					break;
				case LPAR_STATE: 	// (を読んだ
					tk = new CToken(CToken.TK_LPAR, lineNo, startCol, "(");
					accept = true;
					break;
				case RPAR_STATE: 	// )を読んだ
					tk = new CToken(CToken.TK_RPAR, lineNo, startCol, ")");
					accept = true;
					break;
				case LBRA_STATE: 	// [を読んだ
					tk = new CToken(CToken.TK_LBRA, lineNo, startCol, "[");
					accept = true;
					break;
				case RBRA_STATE: 	// ]を読んだ
					tk = new CToken(CToken.TK_RBRA, lineNo, startCol, "]");
					accept = true;
					break;
				case LCUR_STATE: 	// {を読んだ
					tk = new CToken(CToken.TK_LCUR, lineNo, startCol, "{");
					accept = true;
					break;
				case RCUR_STATE: 	// }を読んだ
					tk = new CToken(CToken.TK_RCUR, lineNo, startCol, "}");
					accept = true;
					break;
				case ASSIGN_STATE: 	// =を読んだ
					ch = readChar();
					if (ch == '=') {
						tk = new CToken(CToken.TK_EQ, lineNo, startCol, "==");
					} else {
						backChar(ch);
						tk = new CToken(CToken.TK_ASSIGN, lineNo, startCol, "=");
					}
					accept = true;
					break;
				case LESS_STATE: 	// <を読んだ
					ch = readChar();
					if (ch == '=') {
						tk = new CToken(CToken.TK_LE, lineNo, startCol, "<=");
					} else {
						backChar(ch);
						tk = new CToken(CToken.TK_LT, lineNo, startCol, "<");
					}
					accept = true;
					break;
				case GREAT_STATE: 	// >を読んだ
					ch = readChar();
					if (ch == '=') {
						tk = new CToken(CToken.TK_GE, lineNo, startCol, ">=");
					} else {
						backChar(ch);
						tk = new CToken(CToken.TK_GT, lineNo, startCol, ">");
					}
					accept = true;
					break;
				case NOT_STATE: 	// !を読んだ
					ch = readChar();
					if (ch == '=') {
						tk = new CToken(CToken.TK_NE, lineNo, startCol, "!=");
						accept = true;
						break;
					} else {
						backChar(ch);
						tk = new CToken(CToken.TK_NT, lineNo, startCol, "!");
						accept = true;
						break;
					}
				case BAR_STATE: 	// |を読んだ
					ch = readChar();
					if (ch == '|') {
						tk = new CToken(CToken.TK_OR, lineNo, startCol, "||");
						accept = true;
						break;
					} else {
						backChar(ch);
						tk = new CToken(CToken.TK_ILL, lineNo, startCol, "|");
						accept = true;
						break;
					}
				case SEMI_STATE:   	// ;を読んだ
					tk = new CToken(CToken.TK_SEMI, lineNo, startCol, ";");
					accept = true;
					break;
				case PLUS_STATE: // +を読んだ
					tk = new CToken(CToken.TK_PLUS, lineNo, startCol, "+");
					accept = true;
					break;
				case MINUS_STATE: // -を読んだ
					tk = new CToken(CToken.TK_MINUS, lineNo, startCol, "-");
					accept = true;
					break;
				case ASTER_STATE: // -を読んだ
					tk = new CToken(CToken.TK_MULT, lineNo, startCol, "*");
					accept = true;
					break;
				case SLASH_STATE: // /を読んだ
					ch = readChar();
					if (ch == '/') { // //コメント
						state = COMMENT_SLASH_STATE;
					} else if (ch == '*'){ // /**/コメント
						state = COMMENT_ASTER_STATE;
					} else { // 割り算演算子
						backChar(ch);
						startCol = colNo - 1;
						text.append('/');
						tk = new CToken(CToken.TK_DIV, lineNo, startCol, "/");
						accept = true;
					}
					break;
				case COMMENT_ASTER_CLOSE_STATE: // /*のコメント化状態で*を読んだ
					ch = readChar();
					if (ch == '/'){
						state = 0;
					} else if (ch == '*'){
					} else if (ch == (char) -1) {
						startCol = colNo - 1;
						state = EOF_STATE;
					} else {
						state = COMMENT_ASTER_STATE;
					}
					break;
				case COMMENT_SLASH_STATE: // コメント化状態(//)
					ch = readChar();
					if (ch == '\n') {
						state = 0;
					} else if (ch == (char) -1) {
						state = EOF_STATE;
					}
					break;
				case COMMENT_ASTER_STATE: // コメント化状態(/**/)
					ch = readChar();
					if (ch == '*'){
						state = COMMENT_ASTER_CLOSE_STATE;
					} else if (ch == (char) -1) {
						state = EOF_STATE;
						lineNo = lineNo_EOF;
					}
					break;
				case AND_STATE: // &を読んだ
					ch = readChar();
					if (ch == '&') {
						tk = new CToken(CToken.TK_AND, lineNo, startCol, "&&");
						accept = true;
						break;
					} else {
						backChar(ch);
						tk = new CToken(CToken.TK_AMP, lineNo, startCol, "&");
						accept = true;
						break;
					}
			}
		}
		return tk;
	}
}
