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
 * Represents the types of tokens encountered in a JSON file
 * 
 * @author Dominik Helm
 */
public enum TokenType {
	/** Start of an object '{' */
	ObjStart,
	/** End of an object '}' */
	ObjEnd,
	/** Start of an array '[' */
	ArrStart,
	/** End of an array ']' */
	ArrEnd,
	/** Comma, separating values in arrays and objects ',' */
	Comma,
	/** Colon, separating key and value in objects ':' */
	Colon,
	/** Boolean constant 'true' */
	True,
	/** Boolean constant 'false' */
	False,
	/** Constant 'null' */
	Null,
	/** String of characters */
	String,
	/** Numeric value */
	Number,
	/** End of file */
	EOF;
}