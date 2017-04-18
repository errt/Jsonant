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
package jsonant.value;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jsonant.print.JSONPrinter;

/**
 * Represents a JSONObject for type safe access
 *
 * @author Dominik Helm
 */
public class JSONObject extends JSONValue {
	/**
	 * The encapsulated JSONObject
	 */
	private final Map<String, Object> object;

	/**
	 * Constructs an empty JSONObject
	 *
	 * @param parent
	 *            The parent value of this JSONObject
	 */
	public JSONObject(final JSONValue parent) {
		this(parent, false);
	}

	/**
	 * Constructs an empty JSONArray
	 *
	 * @param parent
	 *            The parent value of this JSONArray
	 * @param onDemand
	 *            True, if this value is only to be created if it contains children
	 */
	public JSONObject(final JSONValue parent, boolean onDemand) {
		super(parent, onDemand);
		object = new LinkedHashMap<>();
	}

	/**
	 * Constructs a JSONObject from a JSONObject
	 *
	 * @param object
	 *            The JSONObject to encapsulate
	 * @param parent
	 *            The parent value of this JSONObject
	 */
	public JSONObject(final Map<String, Object> object, final JSONValue parent) {
		super(parent);
		this.object = object;
	}

	/**
	 * Adds all entries from another JSONObject into this one
	 *
	 * @param other
	 *            The JSONObject the entries are copied from
	 * @param override
	 *            True, if existing entries should be replaced, false if they should be kept as they are
	 */
	public void addAll(final JSONObject other, final boolean override) {
		for (final String key : other.keySet()) {
			if (override || !object.containsKey(key)) {
				final Object val = other.getUnsafe(key);
				object.put(key, val);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see jsonant.value.JSONValue#clear()
	 */
	@Override
	public void clear() {
		object.clear();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see jsonant.value.JSONValue#clone(jsonant.value.JSONValue)
	 */
	@Override
	public JSONObject clone(final JSONValue parent) {
		final LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
		final JSONObject result = new JSONObject(resultMap, parent);
		for (final Entry<String, Object> e : object.entrySet()) {
			final Object value = e.getValue();
			if (value instanceof JSONValue) {
				resultMap.put(e.getKey(), ((JSONValue) value).clone(result));
			} else {
				resultMap.put(e.getKey(), value);
			}
		}
		return result;
	}

	/**
	 * Returns, whether the given key is present in this object
	 *
	 * @param key
	 *            The key to check
	 * @return True if the given key is present, false otherwise
	 */
	public boolean containsKey(final String key) {
		return object.containsKey(key);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object other) {
		if (other instanceof JSONObject) return object.equals(((JSONObject) other).object);
		return false;
	}

	/**
	 * Gets a JSONArray from the given key
	 *
	 * @param key
	 *            The key to fetch from
	 * @return The JSONArray at this key
	 */
	public JSONArray getArr(final String key) {
		if (key == null) return null;
		final Object value = object.get(key);
		if (value instanceof JSONArray) return (JSONArray) value;
		if (value == null) {
			final JSONArray newArr = new JSONArray(this, true);
			put(key, newArr);
			return newArr;
		}
		return null;
	}

	/**
	 * Gets a JSONArray from the given key
	 *
	 * @param key
	 *            The key to fetch from
	 * @param def
	 *            The default value if no value is present
	 * @return The JSONArray at this key
	 */
	public JSONArray getArrOrDefault(final String key, final JSONArray def) {
		final Object value = object.getOrDefault(key, def);
		if (value instanceof JSONArray) return (JSONArray) value;
		return def;
	}

	/**
	 * Gets a boolean from the given key
	 *
	 * @param key
	 *            The key to fetch from
	 * @return The boolean at this key
	 */
	public Boolean getBool(final String key) {
		final Object value = object.get(key);
		if (value instanceof Boolean) return (Boolean) value;
		return null;
	}

	/**
	 * Gets a boolean from the given key
	 *
	 * @param key
	 *            The key to fetch from
	 * @param def
	 *            The default value if no value is present
	 * @return The boolean at this key
	 */
	public Boolean getBoolOrDefault(final String key, final Boolean def) {
		final Object value = object.getOrDefault(key, def);
		if (value instanceof Boolean) return (Boolean) value;
		return def;
	}

	/**
	 * Gets a double from the given key
	 *
	 * @param key
	 *            The key to fetch from
	 * @return The double at this key
	 */
	public Double getDouble(final String key) {
		final Object value = object.get(key);
		if (value instanceof Long) return ((Long) value).doubleValue();
		if (value instanceof Double) return (Double) value;
		return null;
	}

	/**
	 * Gets a double from the given key
	 *
	 * @param key
	 *            The key to fetch from
	 * @param def
	 *            The default value if no value is present
	 * @return The double at this key
	 */
	public Double getDoubleOrDefault(final String key, final Double def) {
		final Object value = object.getOrDefault(key, def);
		if (value instanceof Long) return ((Long) value).doubleValue();
		if (value instanceof Double) return (Double) value;
		return def;
	}

	/**
	 * Gets an int from the given key
	 *
	 * @param key
	 *            The key to fetch from
	 * @return The int at this key
	 */
	public Integer getInt(final String key) {
		final Object value = object.get(key);
		if (value instanceof Long) return ((Long) value).intValue();
		return null;
	}

	/**
	 * Gets an int from the given key
	 *
	 * @param key
	 *            The key to fetch from
	 * @param def
	 *            The default value if no value is present
	 * @return The int at this key
	 */
	public Integer getIntOrDefault(final String key, final Integer def) {
		final Object value = object.getOrDefault(key, def);
		if (value instanceof Long) return ((Long) value).intValue();
		return def;
	}

	/**
	 * Gets a JSONObject from the given key
	 *
	 * @param key
	 *            The key to fetch from
	 * @return The JSONObject at this key
	 */
	public JSONObject getObj(final String key) {
		final Object value = object.get(key);
		if (value instanceof JSONObject) return (JSONObject) value;
		if (value == null) {
			final JSONObject newObj = new JSONObject(this, true);
			put(key, newObj);
			return newObj;
		}
		return null;
	}

	/**
	 * Gets a JSONObject from the given key
	 *
	 * @param key
	 *            The key to fetch from
	 * @param def
	 *            The default value if no value is present
	 * @return The JSONObject at this key
	 */
	public JSONObject getObjOrDefault(final String key, final JSONObject def) {
		final Object value = object.getOrDefault(key, def);
		if (value instanceof JSONObject) return (JSONObject) value;
		return def;
	}

	/**
	 * Gets a String from the given key
	 *
	 * @param key
	 *            The key to fetch from
	 * @return The String at this key
	 */
	public String getString(final String key) {
		final Object value = object.get(key);
		if (value instanceof String) return (String) value;
		return null;
	}

	/**
	 * Gets a String from the given key
	 *
	 * @param key
	 *            The key to fetch from
	 * @param def
	 *            The default value if no value is present
	 * @return The String at this key
	 */
	public String getStringOrDefault(final String key, final String def) {
		final Object value = object.getOrDefault(key, def);
		if (value instanceof String) return (String) value;
		return def;
	}

	/**
	 * Returns the object from the given key with unknown type
	 *
	 * @param key
	 *            The key to fetch from
	 * @return The object at this key
	 */
	public Object getUnsafe(String key) {
		return object.get(key);
	}

	/**
	 * Returns the key of one occurrence of the given object in this object
	 *
	 * @param object
	 *            The object to search for
	 * @return The key of one occurrence, or null, if no such object exists in this object
	 */
	public String keyOf(Object object) {
		if (object instanceof Integer) {
			object = ((Integer) object).longValue();
		} else if (object instanceof Float) {
			object = ((Float) object).doubleValue();
		}
		for (final String key : this.object.keySet()) {
			if (this.object.get(key).equals(object)) return key;
		}
		return null;
	}

	/**
	 * Returns the set of keys in this JSONObject
	 *
	 * @return The set of keys in this JSONObject
	 */
	public Set<String> keySet() {
		return object.keySet();
	}

	/**
	 * Puts a boolean into this object
	 *
	 * @param key
	 *            The key to put this object under
	 * @param value
	 *            The value to put
	 */
	public void put(final String key, final boolean value) {
		object.put(key, value);
	}

	/**
	 * Puts a double into this object
	 *
	 * @param key
	 *            The key to put this object under
	 * @param value
	 *            The value to put
	 */
	public void put(final String key, final double value) {
		object.put(key, value);
	}

	/**
	 * Puts a JSONArray into this object
	 *
	 * @param key
	 *            The key to put this object under
	 * @param array
	 *            The array to put
	 */
	public void put(final String key, final JSONArray array) {
		object.put(key, array);
	}

	/**
	 * Puts a JSONObject into this object
	 *
	 * @param key
	 *            The key to put this object under
	 * @param object
	 *            The object to put
	 */
	public void put(final String key, final JSONObject object) {
		this.object.put(key, object);
	}

	/**
	 * Puts an int into this object
	 *
	 * @param key
	 *            The key to put this object under
	 * @param value
	 *            The value to put
	 */
	public void put(final String key, final long value) {
		object.put(key, value);
	}

	/**
	 * Puts a String into this object
	 *
	 * @param key
	 *            The key to put this object under
	 * @param value
	 *            The value to put
	 */
	public void put(final String key, final String value) {
		object.put(key, value);
	}

	/**
	 * Puts null into this object
	 *
	 * @param key
	 *            The key to put this object under
	 */
	public void putNull(final String key) {
		object.put(key, null);
	}

	/**
	 * Puts any value into this object
	 *
	 * @param key
	 *            The key to put this object under
	 * @param value
	 *            The value to put
	 */
	public void putUnsafe(final String key, final Object value) {
		object.put(key, value);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see jsonant.value.JSONValue#remove(java.lang.Object)
	 */
	@Override
	public void remove(Object object) {
		if (object instanceof Integer) {
			object = ((Integer) object).longValue();
		} else if (object instanceof Float) {
			object = ((Float) object).doubleValue();
		}
		this.object.values().remove(object);
	}

	/**
	 * Removes the element with the given key
	 *
	 * @param key
	 *            The key of the element to be removed
	 */
	public void removeKey(final String key) {
		object.remove(key);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see jsonant.value.JSONValue#shouldBePrinted()
	 */
	@Override
	public boolean shouldBePrinted() {
		if (!onDemand) return true;
		for (final Object value : object.values()) {
			if (value instanceof JSONValue) {
				if (((JSONValue) value).shouldBePrinted()) return true;
			} else
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see jsonant.value.JSONValue#size()
	 */
	@Override
	public int size() {
		return object.size();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringWriter writer = new StringWriter();
		try {
			JSONPrinter.print(writer, this);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return writer.toString();
	}
}
