/*
 * Copyright 2017 Dominik Helm
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jsonant.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;

/**
 * Lexer to break the input into tokens
 * 
 * @author Dominik Helm
 */
public class JSONLexer {
	/** Reader to get the input data from */
	private final Reader input;

	/** Current position in the input (number of characters) */
	private long curPos;
	/** Current position in the input (number of lines) */
	private long curLine;
	/** Current position in the input (number of characters in current line) */
	private long curLinePos;

	/** Char currently inspected */
	private int currentChar;

	/** Sequence of chars to build the next token from */
	private StringBuilder currentTokenString;

	/** User supplied exception handler */
	private final Consumer<RuntimeException> exceptionHandler;

	/**
	 * Creates a new lexer instance for some input, only for use by JSONParser
	 * 
	 * @param input
	 *            Reader to get input data from
	 * @param exceptionHandler
	 *            User supplied exception handler
	 * @throws IOException
	 *             When reading the input fails
	 */
	JSONLexer(Reader input, Consumer<RuntimeException> exceptionHandler) throws IOException {
		this.input = input;
		this.exceptionHandler = exceptionHandler;
		currentChar = input.read();
		curPos = 0;
		curLine = 1;
		curLinePos = 0;
	}

	/**
	 * Helper to decide if a char is a digit in [0-9]
	 * 
	 * @param character
	 *            Character to inspect
	 * @return True, if character in [0-9], false otherwise
	 */
	private boolean isDigit(int character) {
		return '0' <= character && character <= '9';
	}

	/**
	 * Finds the next token in the input
	 * 
	 * @return The next token from the input
	 * @throws IOException
	 *             When reading the input fails
	 */
	public Token nextToken() throws IOException {
		while (Character.isWhitespace(currentChar)) {
			readChar(true);
		}
		currentTokenString = new StringBuilder();
		final long pos = curPos;
		final long line = curLine;
		final long linePos = curLinePos;
		switch (currentChar) {
		case '{':
			readChar(false);
			return new Token(TokenType.ObjStart, pos, line, linePos);
		case '}':
			readChar(false);
			return new Token(TokenType.ObjEnd, pos, line, linePos);
		case '[':
			readChar(false);
			return new Token(TokenType.ArrStart, pos, line, linePos);
		case ']':
			readChar(false);
			return new Token(TokenType.ArrEnd, pos, line, linePos);
		case ',':
			readChar(false);
			return new Token(TokenType.Comma, pos, line, linePos);
		case ':':
			readChar(false);
			return new Token(TokenType.Colon, pos, line, linePos);
		case 't':
			readChar(false);
			readChar('r');
			readChar('u');
			readChar('e');
			return new Token(TokenType.True, pos, line, linePos);
		case 'f':
			readChar(false);
			readChar('a');
			readChar('l');
			readChar('s');
			readChar('e');
			return new Token(TokenType.False, pos, line, linePos);
		case 'n':
			readChar(false);
			readChar('u');
			readChar('l');
			readChar('l');
			return new Token(TokenType.Null, pos, line, linePos);
		case '"':
			readChar(false);
			while (currentChar != '"' && currentChar != -1) {
				if (currentChar == '\\') {
					readChar(false);
					readSpecialChar();
				} else {
					readChar(true);
				}
			}
			readChar(false);
			return new Token(currentTokenString.toString(), pos, line, linePos);
		case '-':
			readChar(true);
		case '0':
			if (currentChar == '0') {
				readChar(true);
				return restOfNumber(pos, line, linePos);
			}
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			if (!isDigit(currentChar)) {
				currentTokenString.append('0');
				exceptionHandler.accept(new RuntimeException("Unexpected character '" + (char) currentChar + "' at line " + curLine + ", position " + curLinePos
						+ " [" + curPos + "], expected digit"));
			} else {
				readChar(true);
			}
			while (isDigit(currentChar)) {
				readChar(true);
			}
			return restOfNumber(pos, line, linePos);
		case -1:
			return new Token(TokenType.EOF, curPos, line, linePos);
		case '\ufeff': // Byte order mark
			readChar(false);
			return nextToken();
		default:
			exceptionHandler.accept(new RuntimeException(
					"Unexpected character '" + (char) currentChar + "' at line " + curLine + ", position " + curLinePos + " [" + curPos + "]"));
			readChar(false);
			return nextToken();
		}
	}

	/**
	 * Gets the next character from the input
	 * 
	 * @param append
	 *            True, if the previous char is to be appended to the currentTokenString
	 * @throws IOException
	 *             When reading the input fails
	 */
	private void readChar(boolean append) throws IOException {
		if (append) {
			currentTokenString.append((char) currentChar);
		}
		++curPos;
		++curLinePos;
		if (currentChar == '\n' || currentChar == '\r') {
			++curLine;
			curLinePos = 0;
		}
		int nextChar = input.read();
		if (currentChar == '\r') {
			if (nextChar == '\n') {
				if (append) {
					currentTokenString.append('\n');
				}
				++curPos;
				nextChar = input.read();
			}
		}
		currentChar = nextChar;
	}

	/**
	 * Gets the next character from the input, raising an exception if the currentChar doesn't match an expectation
	 * 
	 * @param expected
	 *            The character the currentChar must match (otherwise, a RuntimeException will be raised to the exceptionHandler)
	 * @throws IOException
	 *             When reading the input fails
	 */
	private void readChar(char expected) throws IOException {
		if (currentChar == expected) {
			readChar(false);
		} else {
			exceptionHandler.accept(new RuntimeException("Unexpected character '" + (char) currentChar + "' at line " + curLine + ", position " + curLinePos
					+ " [" + curPos + "]" + ", expected '" + expected + "'"));
		}
	}

	/**
	 * Inputs an escaped character inside a string
	 * 
	 * @throws IOException
	 *             When reading the input fails
	 */
	private void readSpecialChar() throws IOException {
		switch (currentChar) {
		case '"':
		case '\\':
		case '/':
			readChar(true);
			break;
		case 'b':
			readChar(false);
			currentTokenString.append('\b');
			break;
		case 'f':
			readChar(false);
			currentTokenString.append('\f');
			break;
		case 'n':
			readChar(false);
			currentTokenString.append('\n');
			break;
		case 'r':
			readChar(false);
			currentTokenString.append('\r');
			break;
		case 't':
			readChar(false);
			currentTokenString.append('\t');
			break;
		case 'u':
			readChar(false);
			final StringBuilder codePoint = new StringBuilder(String.valueOf((char) currentChar));
			readChar(false);
			codePoint.append((char) currentChar);
			readChar(false);
			codePoint.append((char) currentChar);
			readChar(false);
			codePoint.append((char) currentChar);
			readChar(false);
			currentTokenString.appendCodePoint(Integer.parseInt(codePoint.toString(), 16));
			break;
		default:
			currentTokenString.append('\\');
			exceptionHandler.accept(new RuntimeException(
					"Unexpected character '" + (char) currentChar + "' found at line " + curLine + ", position " + curLinePos + " [" + curPos + "]"));
		}
	}

	/**
	 * Handles floating point numbers, lexing their decimal and scientific suffixes
	 * 
	 * @param pos
	 *            Current position in the input
	 * @param line
	 *            Current line in the input
	 * @param linePos
	 *            Current position in the current line
	 * @return The token for the number with a numerical value
	 * @throws IOException
	 *             When reading the input fails
	 */
	private Token restOfNumber(long pos, long line, long linePos) throws IOException {
		boolean integer = true;
		if (currentChar == '.') {
			readChar(true);
			if (!isDigit(currentChar)) {
				exceptionHandler.accept(new RuntimeException("Unexpected character '" + (char) currentChar + "' at line " + curLine + ", position " + curLinePos
						+ " [" + curPos + "], expected digit"));
			} else {
				readChar(true);
			}
			while (isDigit(currentChar)) {
				readChar(true);
			}
			integer = false;
		}
		if (currentChar == 'e' || currentChar == 'E') {
			readChar(true);
			if (currentChar == '+' || currentChar == '-') {
				readChar(true);
			}
			if (!isDigit(currentChar)) {
				currentTokenString.append('0');
				exceptionHandler.accept(new RuntimeException("Unexpected character '" + (char) currentChar + "' at line " + curLine + ", position " + curLinePos
						+ " [" + curPos + "], expected digit"));
			} else {
				readChar(true);
			}
			while (isDigit(currentChar)) {
				readChar(true);
			}
			integer = false;
		}
		if (integer)
			return new Token(Long.valueOf(currentTokenString.toString()), pos, line, linePos);
		else
			return new Token(Double.valueOf(currentTokenString.toString()), pos, line, linePos);
	}
}
