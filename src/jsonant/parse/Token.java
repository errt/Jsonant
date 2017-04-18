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

/**
 * A token inside JSON data
 *
 * @author Dominik Helm
 */
public class Token {
	/** Type to distinguish different tokens */
	final TokenType type;

	/** Value, just for numerical and string tokens */
	private final Object value;

	/** Position of the token in the input (number of characters) */
	final long pos;
	/** Position of the token in the input (number of lines) */
	final long line;
	/** Position of the token in the input (number of characters in the line) */
	final long linePos;

	/**
	 * Creates a numerical token with a given value
	 *
	 * @param value
	 *            The numerical value (must be Long or Double)
	 * @param pos
	 *            Position of the token in the input (number of characters)
	 * @param line
	 *            Position of the token in the input (number of lines)
	 * @param linePos
	 *            Position of the token in the input (number of characters in the line)
	 */
	public Token(Object value, long pos, long line, long linePos) {
		this(TokenType.Number, value, pos, line, linePos);
	}

	/**
	 * Creates a string token with a given value
	 *
	 * @param value
	 *            The string represented by this token
	 * @param pos
	 *            Position of the token in the input (number of characters)
	 * @param line
	 *            Position of the token in the input (number of lines)
	 * @param linePos
	 *            Position of the token in the input (number of characters in the line)
	 */
	public Token(String value, long pos, long line, long linePos) {
		this(TokenType.String, value, pos, line, linePos);
	}

	/**
	 * Creates a token with a given type
	 *
	 * @param type
	 *            The type of the token
	 * @param pos
	 *            Position of the token in the input (number of characters)
	 * @param line
	 *            Position of the token in the input (number of lines)
	 * @param linePos
	 *            Position of the token in the input (number of characters in the line)
	 */
	public Token(TokenType type, long pos, long line, long linePos) {
		this(type, null, pos, line, linePos);
	}

	/**
	 * Creates a token
	 *
	 * @param type
	 *            The type of the token
	 * @param value
	 *            The value of the token (Long, Double, String or null)
	 * @param pos
	 *            Position of the token in the input (number of characters)
	 * @param line
	 *            Position of the token in the input (number of lines)
	 * @param linePos
	 *            Position of the token in the input (number of characters in the line)
	 */
	private Token(TokenType type, Object value, long pos, long line, long linePos) {
		this.type = type;
		this.value = value;
		this.pos = pos;
		this.line = line;
		this.linePos = linePos;
	}

	/**
	 * Returns the value of this token (Long, Double, String or null)
	 *
	 * @return The value of this token
	 */
	public Object getValue() {
		return value;
	}
}
