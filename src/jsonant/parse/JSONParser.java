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

import jsonant.event.ArrayParseEvent;
import jsonant.event.ObjectParseEvent;
import jsonant.event.ParseListener;
import jsonant.value.JSONArray;
import jsonant.value.JSONObject;
import jsonant.value.JSONValue;

/**
 * Parser for the JSON grammar
 *
 * @author Dominik Helm
 */
public class JSONParser {
	/** Lexer to get tokens from */
	private JSONLexer lexer;

	/** Token currently inspected */
	private Token currentToken;

	/** User supplied event handler to intercept parsing */
	private final ParseListener eventHandler;
	/** User supplied exception handler */
	private final Consumer<RuntimeException> exceptionHandler;

	/**
	 * Creates a new parser
	 *
	 * @param eventHandler
	 *            Handler to intercept events during parsing
	 * @param exceptionHandler
	 *            Handler to deal with exceptions during the parsing
	 */
	public JSONParser(ParseListener eventHandler, Consumer<RuntimeException> exceptionHandler) {
		if (eventHandler != null) {
			this.eventHandler = eventHandler;
		} else {
			this.eventHandler = new ParseListener() {
				@Override
				public void handle(ArrayParseEvent event) {}

				@Override
				public void handle(ObjectParseEvent event) {}
			};
		}
		if (exceptionHandler != null) {
			this.exceptionHandler = exceptionHandler;
		} else {
			this.exceptionHandler = e -> {};
		}
	}

	/**
	 * Parses the given input into a JSONObject
	 *
	 * @param input
	 *            Reader to get the input from
	 * @return The JSONObject represented by the input
	 * @throws IOException
	 *             When reading the input fails
	 */
	public JSONObject parse(Reader input) throws IOException {
		lexer = new JSONLexer(input, exceptionHandler);
		readToken();
		final JSONObject result = parseObject(null);
		if (currentToken.type != TokenType.EOF) {
			exceptionHandler.accept(new RuntimeException("Unexpected token '" + currentToken.type + "' found at line " + currentToken.line + ", position "
					+ currentToken.linePos + " [" + currentToken.pos + "], expected end of file"));
		}
		return result;
	}

	/**
	 * Parses an array
	 *
	 * @param parent
	 *            The JSONValue this array is part of
	 * @return The parsed array
	 * @throws IOException
	 *             When reading the input fails
	 */
	private JSONArray parseArray(JSONValue parent) throws IOException {
		if (currentToken.type != TokenType.ArrStart) {
			exceptionHandler.accept(new RuntimeException("Unexpected token '" + currentToken.type + "' found at line " + currentToken.line + ", position "
					+ currentToken.linePos + " [" + currentToken.pos + "], expected '['"));
		} else {
			readToken();
		}
		final JSONArray result = new JSONArray(parent);
		while (currentToken.type != TokenType.ArrEnd) {
			final Object value = parseValue(result);
			if (value instanceof Token) {
				break;
			}

			final ArrayParseEvent event = new ArrayParseEvent(result, result.size(), value);
			eventHandler.handle(event);
			if (!event.isCancelled()) {
				result.addUnsafe(event.getIndex(), event.getValue());
			}

			if (currentToken.type == TokenType.ArrEnd) {
				break;
			}
			if (currentToken.type != TokenType.Comma) {
				exceptionHandler.accept(new RuntimeException("Unexpected token '" + currentToken.type + "' found at line " + currentToken.line + ", position "
						+ currentToken.linePos + " [" + currentToken.pos + "], expected ','"));
			} else {
				readToken();
			}
		}
		readToken();
		return result;
	}

	/**
	 * Parses an object
	 *
	 * @param parent
	 *            The JSONValue this object is part of
	 * @return The parsed object
	 * @throws IOException
	 *             When reading the input fails
	 */
	private JSONObject parseObject(JSONValue parent) throws IOException {
		if (currentToken.type != TokenType.ObjStart) {
			exceptionHandler.accept(new RuntimeException("Unexpected token '" + currentToken.type + "' found at line " + currentToken.line + ", position "
					+ currentToken.linePos + " [" + currentToken.pos + "], expected '{'"));
		} else {
			readToken();
		}
		final JSONObject result = new JSONObject(parent);
		while (currentToken.type == TokenType.String) {
			final String key = (String) currentToken.getValue();
			readToken();
			if (currentToken.type != TokenType.Colon) {
				exceptionHandler.accept(new RuntimeException("Unexpected token '" + currentToken.type + "' found at line " + currentToken.line + ", position "
						+ currentToken.linePos + " [" + currentToken.pos + "], expected ':'"));
			} else {
				readToken();
			}
			Object value = parseValue(result);
			if (value instanceof Token) {
				value = null;
			}

			final ObjectParseEvent event = new ObjectParseEvent(result, key, value);
			eventHandler.handle(event);
			if (!event.isCancelled()) {
				result.putUnsafe(event.getKey(), event.getValue());
			}

			if (currentToken.type == TokenType.ObjEnd) {
				break;
			}
			if (currentToken.type != TokenType.Comma) {
				exceptionHandler.accept(new RuntimeException("Unexpected token '" + currentToken.type + "' found at line " + currentToken.line + ", position "
						+ currentToken.linePos + " [" + currentToken.pos + "], expected ','"));
			} else {
				readToken();
			}
		}
		if (currentToken.type != TokenType.ObjEnd) {
			exceptionHandler.accept(new RuntimeException("Unexpected token '" + currentToken.type + "' found at line " + currentToken.line + ", position "
					+ currentToken.linePos + " [" + currentToken.pos + "], expected '}'"));
		} else {
			readToken();
		}

		return result;
	}

	/**
	 * Parses a value
	 *
	 * @param parent
	 *            The JSONValue this value is part of
	 * @return The parsed value
	 * @throws IOException
	 *             When reading the input fails
	 */
	private Object parseValue(JSONValue parent) throws IOException {
		switch (currentToken.type) {
		case ObjStart:
			return parseObject(parent);
		case ArrStart:
			return parseArray(parent);
		case String:
		case Number:
			final Object result = currentToken.getValue();
			readToken();
			return result;
		case True:
			readToken();
			return true;
		case False:
			readToken();
			return false;
		case Null:
			readToken();
			return null;
		default:
			exceptionHandler.accept(new RuntimeException("Unexpected token '" + currentToken.type + "' found at line " + currentToken.line + ", position "
					+ currentToken.linePos + " [" + currentToken.pos + "], expected value"));
			return currentToken;
		}
	}

	/**
	 * Advances to the next token
	 *
	 * @throws IOException
	 *             When reading the input fails
	 */
	private void readToken() throws IOException {
		currentToken = lexer.nextToken();
	}
}