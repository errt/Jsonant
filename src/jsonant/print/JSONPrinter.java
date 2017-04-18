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
package jsonant.print;

import java.io.IOException;
import java.io.Writer;

import jsonant.value.JSONArray;
import jsonant.value.JSONObject;
import jsonant.value.JSONValue;

/**
 * Pretty printer for JSONValues
 *
 * @author Dominik Helm
 */
public class JSONPrinter {

	/**
	 * Adds required indentation
	 *
	 * @param writer
	 *            Writer to output to
	 * @param indent
	 *            Number of indentation steps required
	 * @throws IOException
	 *             When writing the output fails
	 */
	private static void indent(Writer writer, int indent) throws IOException {
		for (int i = 0; i < indent; ++i) {
			writer.append('\t');
		}
	}

	/**
	 * Outputs a JSONArray
	 *
	 * @param writer
	 *            Writer to output to
	 * @param value
	 *            The JSONArray to output
	 * @param indent
	 *            Initial indentation for the output
	 * @throws IOException
	 *             When writing the output fails
	 */
	public static void print(Writer writer, JSONArray value, int indent) throws IOException {
		writer.append('[');
		boolean willIndent = false;
		if (value.size() > 10) {
			willIndent = true;
		}
		boolean first = true;
		for (int i = 0; i < value.size(); ++i) {
			final Object entry = value.getUnsafe(i);
			if (entry instanceof JSONValue) {
				if (!((JSONValue) entry).shouldBePrinted()) {
					continue;
				}
			}
			if (!first) {
				writer.append(',');
			}
			if (willIndent) {
				writer.append('\n');
				indent(writer, indent + (willIndent ? 1 : 0));
			} else if (!first) {
				writer.append(' ');
			}
			first = false;
			print(writer, entry, indent + (willIndent ? 1 : 0));
		}
		if (willIndent) {
			writer.append('\n');
			indent(writer, indent);
		}
		writer.append(']');
	}

	/**
	 * Outputs a JSONObject with no initial indentation
	 * 
	 * @param writer
	 *            Writer to output to
	 * @param value
	 *            The JSONObject to output
	 * @throws IOException
	 *             When writing the output fails
	 */
	public static void print(Writer writer, JSONObject value) throws IOException {
		print(writer, value, 0);
	}

	/**
	 * Outputs a JSONObject with an initial indentation
	 * 
	 * @param writer
	 *            Writer to output to
	 * @param value
	 *            The JSONObject to output
	 * @param indent
	 *            Initial indentation for the output
	 * @throws IOException
	 *             When writing the output fails
	 */
	public static void print(Writer writer, JSONObject value, int indent) throws IOException {
		writer.append('{');
		boolean willIndent = false;
		if (value.size() > 5) {
			willIndent = true;
		}
		boolean first = true;
		for (final String key : value.keySet()) {
			final Object entry = value.getUnsafe(key);
			if (entry instanceof JSONValue) {
				if (!((JSONValue) entry).shouldBePrinted()) {
					continue;
				}
			}
			if (first) {
				first = false;
			} else {
				writer.append(',');
			}
			if (willIndent) {
				writer.append('\n');
				indent(writer, indent + (willIndent ? 1 : 0));
			} else {
				writer.append(' ');
			}
			print(writer, key, indent);
			writer.append(" : ");
			print(writer, entry, indent + (willIndent ? 1 : 0));
		}
		if (willIndent) {
			writer.append('\n');
			indent(writer, indent);
		} else {
			writer.append(' ');
		}
		writer.append('}');
	}

	/**
	 * Outputs any valid JSON value in JSON format
	 * 
	 * @param writer
	 *            Writer to output to
	 * @param value
	 *            The value to output
	 * @param indent
	 *            Initial indentation for the output
	 * @throws IOException
	 *             When writing the output fails
	 */
	private static void print(Writer writer, Object value, int indent) throws IOException {
		if (value == null) {
			writer.append("null");
		} else if (value instanceof JSONObject) {
			print(writer, (JSONObject) value, indent);
		} else if (value instanceof JSONArray) {
			print(writer, (JSONArray) value, indent);
		} else if (value instanceof String) {
			print(writer, (String) value);
		} else {
			writer.append(value.toString());
		}
	}

	/**
	 * Outputs a String, escaping special characters as necessary
	 * 
	 * @param writer
	 *            Writer to output to
	 * @param value
	 *            The String to output
	 * @throws IOException
	 *             When writing the output fails
	 */
	private static void print(Writer writer, String string) throws IOException {
		writer.append('"');
		for (final char c : string.toCharArray()) {
			switch (c) {
			case '"':
				writer.append("\\\"");
				break;
			case '\\':
				writer.append("\\\\");
				break;
			case '\b':
				writer.append("\\b");
				break;
			case '\f':
				writer.append("\\f");
				break;
			case '\n':
				writer.append("\\n");
				break;
			case '\r':
				writer.append("\\r");
				break;
			case '\t':
				writer.append("\\t");
				break;
			default:
				writer.append(c);
				break;
			}
		}
		writer.append('"');
	}
}
