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
package jsonant.event;

import jsonant.value.JSONArray;

/**
 * An event raised any time a value inside an array is parsed
 *
 * @author Dominik Helm
 */
public class ArrayParseEvent {
	/** Array the value will located in */
	private final JSONArray array;

	/** Index the value will be located at */
	private int index;
	/** Value added to the array */
	private Object value;

	/** Whether this insertion was cancelled */
	private boolean cancelled = false;

	/**
	 * Creates a new ArrayParseEvent
	 *
	 * @param array
	 *            The array currently parsed
	 * @param index
	 *            The index the new value will be added at
	 * @param value
	 *            The value to be added to the array
	 */
	public ArrayParseEvent(JSONArray array, int index, Object value) {
		this.array = array;
		this.index = index;
		this.value = value;
	}

	/**
	 * Cancels the insertion into the array, discarding the value
	 */
	public void cancel() {
		cancelled = true;
	}

	/**
	 * Returns the array that the value will be added to
	 *
	 * @return The array
	 */
	public JSONArray getArray() {
		return array;
	}

	/**
	 * Returns the index the value will be added at
	 *
	 * @return The index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns the value to be added
	 *
	 * @return The value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Returns, whether this insertion was cancelled
	 *
	 * @return True if the insertion was cancelled, false if it should be performed
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Changes the index to add the value at
	 *
	 * @param index
	 *            The new index the value will be added at
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Changes the value to be added
	 *
	 * @param value
	 *            The new value to be added
	 */
	public void setValue(Object value) {
		this.value = value;
	}
}
