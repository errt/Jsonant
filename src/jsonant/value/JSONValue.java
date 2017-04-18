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

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import jsonant.event.JSONListener;

/**
 * Base class for JSONArray and JSONObject
 *
 * @author Dominik Helm
 */
public abstract class JSONValue {
	/**
	 * Contains all classes that want to be notified on local changes to any JSONValue
	 */
	protected static final Map<JSONValue, Set<JSONListener>> localListeners = new IdentityHashMap<>();

	/**
	 * Contains all classes that want to be notified on changes to any JSONValue
	 */
	protected static final Map<JSONValue, Set<JSONListener>> jsonListeners = new IdentityHashMap<>();

	/**
	 * Whether this value is only to be created if it contains children
	 */
	protected boolean onDemand;

	/**
	 * The parent value of this value
	 */
	protected final JSONValue parent;

	/**
	 * Constructs a JSONValue with a given parent
	 *
	 * @param parent
	 *            The parent of this JSONValue
	 */
	protected JSONValue(final JSONValue parent) {
		this.parent = parent;
		onDemand = false;
	}

	/**
	 * Constructs a JSONValue with a given parent
	 *
	 * @param parent
	 *            The parent of this JSONValue
	 * @param onDemand
	 *            True, if this value is only to be created if it contains children
	 */
	protected JSONValue(final JSONValue parent, boolean onDemand) {
		this.parent = parent;
		this.onDemand = onDemand;
	}

	/**
	 * Registers a listener to be notified if this value changes
	 *
	 * @param listener
	 *            The listener to register
	 */
	public void addListener(final JSONListener listener) {
		if (jsonListeners.containsKey(this)) {
			jsonListeners.get(this).add(listener);
		} else {
			final HashSet<JSONListener> listeners = new HashSet<>();
			listeners.add(listener);
			jsonListeners.put(this, listeners);
		}
	}

	/**
	 * Registers a listener to be notified on local changes
	 *
	 * @param listener
	 *            The listener to register
	 */
	public void addLocalListener(final JSONListener listener) {
		if (localListeners.containsKey(this)) {
			localListeners.get(this).add(listener);
		} else {
			final HashSet<JSONListener> listeners = new HashSet<>();
			listeners.add(listener);
			localListeners.put(this, listeners);
		}
	}

	/**
	 * Removes all entries from this JSONValue
	 */
	public abstract void clear();

	/**
	 * Creates a deep copy of this JSONValue
	 *
	 * @param parent
	 *            The JSONValue (may be null) this JSONValue is a child of
	 * @return A deep copy of this JSONValue
	 */
	public abstract JSONValue clone(final JSONValue parent);

	/**
	 * Returns the parent of this value
	 *
	 * @return The parent of this value
	 */
	public JSONValue getParent() {
		return parent;
	}

	/**
	 * Notifies all listeners registered for a particular value
	 *
	 * @param initiator
	 *            The JSONListener that initiated this change
	 */
	public void notifyListeners(final JSONListener initiator) {
		final Set<JSONListener> listeners = new HashSet<>();
		JSONValue current = this;
		while (current != null) {
			if (jsonListeners.containsKey(current)) {
				listeners.addAll(jsonListeners.get(current));
			}
			current = current.parent;
		}
		if (localListeners.containsKey(this)) {
			listeners.addAll(localListeners.get(this));
		}
		for (final JSONListener listener : listeners) {
			if (listener != initiator) {
				listener.notifyChanged(this);
			}
		}
	}

	/**
	 * Removes the given object from this value
	 *
	 * @param object
	 *            The object to be removed
	 */
	public abstract void remove(Object object);

	/**
	 * Removes a listener that was previously added
	 * Use with care, if you might have registered a listener twice, both are removed!
	 *
	 * @param listener
	 *            The listener to be removed
	 */
	public void removeListener(final JSONListener listener) {
		Set<JSONListener> listeners = jsonListeners.get(this);
		if (listeners != null) {
			listeners.remove(listener);
		}
		listeners = localListeners.get(this);
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	/**
	 * Changes, whether this value is only to be created if it contains children
	 *
	 * @param onDemand
	 *            True, if this value is only to be created if it contains children
	 */
	public void setOnDemand(boolean onDemand) {
		this.onDemand = onDemand;
	}

	/**
	 * Whether this value should be printed on serialization
	 *
	 * @return True, if the object is to be serialized, false otherwise
	 */
	public abstract boolean shouldBePrinted();

	/**
	 * Returns the number of children of this value
	 *
	 * @return The number of children of this value
	 */
	public abstract int size();
}
