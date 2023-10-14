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
		// System.out.print("'"+ch+"'("+(int)ch+")");
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
				case 0: // 初期状態
					ch = readChar();
					if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
					} else if (ch == (char) -1) { // EOF
						startCol = colNo - 1;
						state = 1;
					} else if (ch == '0') { 	  // 16進数 or 8進数
						startCol = colNo - 1;
						text.append(ch);
						state = 10;
					} else if ('1' <= ch && ch <= '9') { // 数（10進数）の開始
						startCol = colNo - 1;
						text.append(ch);
						state = 3;
					} else if (ch == '+') { // 和
						startCol = colNo - 1;
						text.append(ch);
						state = 4;
					} else if (ch == '-') { // 差
						startCol = colNo - 1;
						text.append(ch);
						state = 5;
					} else if (ch == '/'){ // コメント化状態
						state = 6;
					} else { // ヘンな文字を読んだ
						startCol = colNo - 1;
						text.append(ch);
						state = 2;
					}
					break;
				case 1: // EOFを読んだ
					tk = new CToken(CToken.TK_EOF, lineNo, startCol, "end_of_file");
					accept = true;
					break;
				case 2: // ヘンな文字を読んだ
					tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
					accept = true;
					break;
				case 3: // 数（10進数）の開始
					ch = readChar();
					if (Character.isDigit(ch)) {
						text.append(ch);
					} else {
						// 数の終わり
						backChar(ch); // 数を表さない文字は戻す（読まなかったことにする）
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
				case 4: // +を読んだ
					tk = new CToken(CToken.TK_PLUS, lineNo, startCol, "+");
					accept = true;
					break;
				case 5: // -を読んだ
					tk = new CToken(CToken.TK_MINUS, lineNo, startCol, "-");
					accept = true;
					break;
				case 6: // /を読んだ
					ch = readChar();
					if (ch == '/') {
						state = 8;
					} else if (ch == '*'){
						state = 9;
					} else {
						text.append(ch);
						state = 2;
					}
					break;
				case 7: // *を読んだ(コメント化状態)
					ch = readChar();
					if (ch == '/'){
						state = 0;
					} else if (ch == '*'){
					} else if (ch == (char) -1) {
						startCol = colNo - 1;
						state = 1;
					} else {
						state = 9;
					}
					break;
				case 8: // コメント化状態(//)
					ch = readChar();
					if (ch == '\n') {
						state = 0;
					} else if (ch == (char) -1) {
						state = 1;
					}
					break;
				case 9: // コメント化状態(/**/)
					ch = readChar();
					if (ch == '*'){
						state = 7;
					} else if (ch == (char) -1) {
						state = 1;
						lineNo = lineNo_EOF;
					}
					break;
				case 10: // 16進数状態　or 8進数状態
					ch = readChar();
					if (ch == 'x'){
						text.append(ch);
						num_count = 0;
						state = 11;
					} else if ('0' <= ch && ch <= '7') {
						backChar(ch);
						num_count = 0;
						state = 12;
					} else {
						// 数の終わり
						tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
				case 11: // 16進数状態
					ch = readChar();
					num_count++;
					if ('0' <= ch && ch <= '9' || 'A' <= ch && ch <= 'F') {
						text.append(ch);
						// オーバーフロー
						if (num_count > 4){
							state = 2;
						}
					} else {
						// 数の終わり
						backChar(ch); // 数を表さない文字は戻す（読まなかったことにする）
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
				case 12: // 8進数状態
					ch = readChar();
					num_count++;
					if (num_count == 1) first_ch_8 = ch;

					if ('0' <= ch && ch <= '7') {
						text.append(ch);
						// オーバーフロー
						if (num_count >= 6 && !(num_count == 6 && first_ch_8 == '1')){
							state = 2;
						}
					} else {
						// 数の終わり
						backChar(ch); // 数を表さない文字は戻す（読まなかったことにする）
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
			}
		}
		return tk;
	}
}
