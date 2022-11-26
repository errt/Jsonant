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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jsonant.print.JSONPrinter;

/**
 * Represents a JSONArray for type safe access
 *
 * @author Dominik Helm
 */
public class JSONArray extends JSONValue {

	/**
	 * The encapsulated JSONArray
	 */
	private final List<Object> array;

	/**
	 * Constructs an empty JSONArray
	 *
	 * @param parent
	 *            The parent value of this JSONArray
	 */
	public JSONArray(final JSONValue parent) {
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
	public JSONArray(final JSONValue parent, boolean onDemand) {
		super(parent, onDemand);
		array = new ArrayList<>();
	}

	/**
	 * Constructs a JSONArray from a given list
	 *
	 * @param array
	 *            The list to encapsulate
	 * @param parent
	 *            The parent value of this JSONArray
	 */
	public JSONArray(final List<Object> array, final JSONValue parent) {
		super(parent);
		this.array = array;
	}

	/**
	 * Adds a boolean into this array
	 *
	 * @param value
	 *            The value to add
	 */
	public void add(final boolean value) {
		array.add(value);
	}

	/**
	 * Adds a double into this array
	 *
	 * @param value
	 *            The value to add
	 */
	public void add(final double value) {
		array.add(value);
	}

	/**
	 * Adds a boolean into this array
	 *
	 * @param index
	 *            The index the new element will be added at
	 * @param value
	 *            The value to add
	 */
	public void add(final int index, final boolean value) {
		array.add(index, value);
	}

	/**
	 * Adds a double into this array
	 *
	 * @param index
	 *            The index the new element will be added at
	 * @param value
	 *            The value to add
	 */
	public void add(final int index, final double value) {
		array.add(index, value);
	}

	/**
	 * Adds a JSONArray into this array
	 *
	 * @param index
	 *            The index the new element will be added at
	 * @param array
	 *            The array to add
	 */
	public void add(final int index, final JSONArray array) {
		if (array.getParent() != this) throw new IllegalArgumentException("Wrong hierarchy");
		this.array.add(index, array);
	}

	/**
	 * Adds a JSONObject into this array
	 *
	 * @param index
	 *            The index the new element will be added at
	 * @param object
	 *            The object to add
	 */
	public void add(final int index, final JSONObject object) {
		if (object.getParent() != this) throw new IllegalArgumentException("Wrong hierarchy");
		array.add(index, object);
	}

	/**
	 * Adds an int into this array
	 *
	 * @param index
	 *            The index the new element will be added at
	 * @param value
	 *            The value to add
	 */
	public void add(final int index, final long value) {
		array.add(index, value);
	}

	/**
	 * Adds a String into this array
	 *
	 * @param index
	 *            The index the new element will be added at
	 * @param value
	 *            The value to add
	 */
	public void add(final int index, final String value) {
		array.add(index, value);
	}

	/**
	 * Adds a JSONArray into this array
	 *
	 * @param array
	 *            The array to add
	 */
	public void add(final JSONArray array) {
		this.array.add(array);
	}

	/**
	 * Adds a JSONObject into this array
	 *
	 * @param object
	 *            The object to add
	 */
	public void add(final JSONObject object) {
		array.add(object);
	}

	/**
	 * Adds an int into this array
	 *
	 * @param value
	 *            The value to add
	 */
	public void add(final long value) {
		array.add(value);
	}

	/**
	 * Adds a String into this array
	 *
	 * @param value
	 *            The value to add
	 */
	public void add(final String value) {
		array.add(value);
	}

	/**
	 * Adds null into this array
	 *
	 * @param index
	 *            The index the new element will be added at
	 */
	public void addNull(final int index) {
		array.add(index, null);
	}

	/**
	 * Adds any value into this array
	 *
	 * @param index
	 *            The index the new element will be added at
	 * @param value
	 *            The value to add
	 */
	public void addUnsafe(final int index, final Object value) {
		if (value instanceof JSONValue) {
			array.add(index, ((JSONValue) value).clone(this));
		} else {
			array.add(index, value);
		}
	}

	/**
	 * Adds any value into this array
	 *
	 * @param value
	 *            The value to add
	 */
	public void addUnsafe(final Object value) {
		if (value instanceof JSONValue) {
			array.add(((JSONValue) value).clone(this));
		} else {
			array.add(value);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see jsonant.value.JSONValue#clear()
	 */
	@Override
	public void clear() {
		array.clear();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see jsonant.value.JSONValue#clone(jsonant.value.JSONValue)
	 */
	@Override
	public JSONArray clone(final JSONValue parent) {
		final List<Object> resultList = new ArrayList<>();
		final JSONArray result = new JSONArray(resultList, parent);
		for (final Object o : array) {
			if (o instanceof JSONValue) {
				resultList.add(((JSONValue) o).clone(result));
			} else {
				resultList.add(o);
			}
		}
		return result;
	}

	/**
	 * Returns, whether the given object is contained in this array
	 *
	 * @param object
	 *            The object that might be contained in the array
	 * @return True, if this array contains the given object, false otherwise
	 */
	public boolean contains(Object object) {
		if (object instanceof Integer) {
			object = ((Integer) object).longValue();
		} else if (object instanceof Float) {
			object = ((Float) object).doubleValue();
		}
		return array.contains(object);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object other) {
		if (other instanceof JSONArray) return array.equals(((JSONArray) other).array);
		return false;
	}

	/**
	 * Compares this to another JSONArray, ignoring element ordering
	 *
	 * @param other
	 *            The other JSONArray
	 * @return True, if they contain the same elements, even if in different order, false otherwise
	 */
	public boolean equalsIgnoreOrder(JSONArray other) {
		return array.containsAll(other.array) && other.array.containsAll(array);
	}

	/**
	 * Gets a JSONArray from the given index
	 *
	 * @param index
	 *            The index to fetch from
	 * @return The JSONArray at this position
	 */
	public JSONArray getArr(final int index) {
		final Object value = array.get(index);
		if (value instanceof JSONArray) return (JSONArray) value;
		if (value == null) {
			final JSONArray newArr = new JSONArray(this, true);
			add(newArr);
			return newArr;
		}
		return null;
	}

	/**
	 * Returns a collection of all JSONArrays in this JSONArray
	 *
	 * @return The collection of JSONArrays
	 */
	public Collection<JSONArray> getArrs() {
		final List<JSONArray> result = new ArrayList<>(size());
		for (final Object value : array) {
			if (value instanceof JSONArray) {
				result.add((JSONArray) value);
			}
		}
		return result;
	}

	/**
	 * Gets a boolean from the given index
	 *
	 * @param index
	 *            The index to fetch from
	 * @return The boolean at this position
	 */
	public Boolean getBool(final int index) {
		final Object value = array.get(index);
		if (value instanceof Boolean) return (Boolean) value;
		return null;
	}

	/**
	 * Returns a collection of all booleans in this JSONArray
	 *
	 * @return The collection of booleans
	 */
	public Collection<Boolean> getBools() {
		final List<Boolean> result = new ArrayList<>(size());
		for (final Object value : array) {
			if (value instanceof Boolean) {
				result.add((Boolean) value);
			}
		}
		return result;
	}

	/**
	 * Gets a double from the given index
	 *
	 * @param index
	 *            The index to fetch from
	 * @return The double at this position
	 */
	public Double getDouble(final int index) {
		final Object value = array.get(index);
		if (value instanceof Long) return ((Long) value).doubleValue();
		if (value instanceof Double) return (Double) value;
		return null;
	}

	/**
	 * Returns a collection of all doubles in this JSONArray
	 *
	 * @return The collection of doubles
	 */
	public Collection<Double> getDoubles() {
		final List<Double> result = new ArrayList<>(size());
		for (final Object value : array) {
			if (value instanceof Double) {
				result.add((Double) value);
			}
		}
		return result;
	}

	/**
	 * Gets an int from the given index
	 *
	 * @param index
	 *            The index to fetch from
	 * @return The int at this position
	 */
	public Integer getInt(final int index) {
		final Object result = array.get(index);
		if (result instanceof Long) return ((Long) result).intValue();
		return null;
	}

	/**
	 * Returns a collection of all ints in this JSONArray
	 *
	 * @return The collection of ints
	 */
	public Collection<Long> getInts() {
		final List<Long> result = new ArrayList<>(size());
		for (final Object value : array) {
			if (value instanceof Long) {
				result.add((Long) value);
			}
		}
		return result;
	}

	/**
	 * Gets a JSONObject from the given index
	 *
	 * @param index
	 *            The index to fetch from
	 * @return The JSONObject at this position
	 */
	public JSONObject getObj(final int index) {
		final Object value = array.get(index);
		if (value instanceof JSONObject) return (JSONObject) value;
		if (value == null) {
			final JSONObject newObj = new JSONObject(this, true);
			add(newObj);
			return newObj;
		}
		return null;
	}

	/**
	 * Returns a collection of all JSONObjects in this JSONArray
	 *
	 * @return The collection of JSONObjects
	 */
	public Collection<JSONObject> getObjs() {
		final List<JSONObject> result = new ArrayList<>(size());
		for (final Object value : array) {
			if (value instanceof JSONObject) {
				result.add((JSONObject) value);
			}
		}
		return result;
	}

	/**
	 * Gets a String from the given index
	 *
	 * @param index
	 *            The index to fetch from
	 * @return The String at this position
	 */
	public String getString(final int index) {
		final Object value = array.get(index);
		if (value instanceof String) return (String) value;
		return null;
	}

	/**
	 * Returns a collection of all strings in this JSONArray
	 *
	 * @return The collection of strings
	 */
	public Collection<String> getStrings() {
		final List<String> result = new ArrayList<>(size());
		for (final Object value : array) {
			if (value instanceof String) {
				result.add((String) value);
			}
		}
		return result;
	}

	/**
	 * Returns the object at the given index with unknown type
	 *
	 * @param index
	 *            The index to fetch from
	 * @return The object at this position
	 */
	public Object getUnsafe(int index) {
		return array.get(index);
	}

	/**
	 * Returns the index of the first occurrence of the given object in this array
	 *
	 * @param object
	 *            The object to search for
	 * @return The index of the first occurrence, or -1, if no such object exists in this array
	 */
	public int indexOf(Object object) {
		if (object instanceof Integer) {
			object = ((Integer) object).longValue();
		} else if (object instanceof Float) {
			object = ((Float) object).doubleValue();
		}
		return array.indexOf(object);
	}

	/**
	 * Returns the index of the first occurrence of the given object in this array after the specified start index
	 *
	 * @param object
	 *            The object to search for
	 * @param start
	 *            The index to start from
	 * @return The index of the first occurrence after the specified start index, or -1, if no such object exists in this array
	 */
	public int indexOf(Object object, int start) {
		if (object instanceof Integer) {
			object = ((Integer) object).longValue();
		} else if (object instanceof Float) {
			object = ((Float) object).doubleValue();
		}
		final int index = array.subList(start, array.size()).indexOf(object);
		if (index == -1) return -1;
		return index + start;
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
		array.remove(object);
	}

	/**
	 * Removes the element at the given index
	 *
	 * @param index
	 *            The index of the element to be removed
	 */
	public void removeAt(final int index) {
		array.remove(index);
	}

	/**
	 * Replaces the object currently at the given index with a new one
	 *
	 * @param index
	 *            The index the new object will be put at
	 * @param object
	 *            The new object
	 */
	public void set(final int index, final boolean object) {
		array.set(index, object);
	}

	/**
	 * Replaces the object currently at the given index with a new one
	 *
	 * @param index
	 *            The index the new object will be put at
	 * @param object
	 *            The new object
	 */
	public void set(final int index, final double object) {
		array.set(index, object);
	}

	/**
	 * Replaces the object currently at the given index with a new one
	 *
	 * @param index
	 *            The index the new object will be put at
	 * @param object
	 *            The new object
	 */
	public void set(final int index, final JSONArray object) {
		if (object.getParent() != this) throw new IllegalArgumentException("Wrong hierarchy");
		array.set(index, object);
	}

	/**
	 * Replaces the object currently at the given index with a new one
	 *
	 * @param index
	 *            The index the new object will be put at
	 * @param object
	 *            The new object
	 */
	public void set(final int index, final JSONObject object) {
		if (object.getParent() != this) throw new IllegalArgumentException("Wrong hierarchy");
		array.set(index, object);
	}

	/**
	 * Replaces the object currently at the given index with a new one
	 *
	 * @param index
	 *            The index the new object will be put at
	 * @param object
	 *            The new object
	 */
	public void set(final int index, final long object) {
		array.set(index, object);
	}

	/**
	 * Replaces the object currently at the given index with a new one
	 *
	 * @param index
	 *            The index the new object will be put at
	 * @param object
	 *            The new object
	 */
	public void set(final int index, final String object) {
		array.set(index, object);
	}

	/**
	 * Replaces the object currently at the given index with null
	 *
	 * @param index
	 *            The index the new object will be put at
	 */
	public void setNull(final int index) {
		array.set(index, null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see jsonant.value.JSONValue#shouldBePrinted()
	 */
	@Override
	public boolean shouldBePrinted() {
		if (!onDemand) return true;
		for (final Object value : array) {
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
		return array.size();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringWriter w = new StringWriter();
		try {
			JSONPrinter.print(w, this, 0);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return w.toString();
	}
}
