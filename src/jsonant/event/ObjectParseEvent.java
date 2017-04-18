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

import jsonant.value.JSONObject;

/**
 * An event raised any time a value inside an object is parsed
 *
 * @author Dominik Helm
 */
public class ObjectParseEvent {
	/** Object the value will be part of */
	private final JSONObject object;

	/** Key of the value in the object */
	private String key;
	/** Value added to the array */
	private Object value;

	/** Whether this insertion was cancelled */
	private boolean cancelled = false;

	/**
	 * Creates a new ObjectParseEvent
	 *
	 * @param object
	 *            The object the value will be added to
	 * @param key
	 *            The key the value will have in the object
	 * @param value
	 *            The value to be added to the object
	 */
	public ObjectParseEvent(JSONObject object, String key, Object value) {
		this.object = object;
		this.key = key;
		this.value = value;
	}

	/**
	 * Cancels the insertion into the object, discarding the value
	 */
	public void cancel() {
		cancelled = true;
	}

	/**
	 * Returns the key of the value in the object
	 * 
	 * @return The key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Returns the object the value will be added to
	 * 
	 * @return The object
	 */
	public JSONObject getObject() {
		return object;
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
	 * Changes the key the value will have in the object
	 * 
	 * @param key
	 *            The new key for the value
	 */
	public void setKey(String key) {
		this.key = key;
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
