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

/**
 * Interface for listeners that intervene in the parse process
 * 
 * @author Dominik Helm
 */
public interface ParseListener {
	/**
	 * Handle the insertion of a value into an array
	 * 
	 * @param event
	 *            ArrayParseEvent to modify the insertion process
	 */
	public void handle(ArrayParseEvent event);

	/**
	 * Handle the insertion of a value into an object
	 * 
	 * @param event
	 *            ObjectParseEvent to modify the insertion process
	 */
	public void handle(ObjectParseEvent event);
}
