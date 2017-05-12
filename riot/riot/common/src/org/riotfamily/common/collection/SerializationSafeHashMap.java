/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.collection;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * HashMap that can be serialized even if it contains non-serializable keys or
 * values. Only entries where both key and value are either serializable or null
 * will be retained.
 */
public class SerializationSafeHashMap<K, V> extends HashMap<K, V> {

	public SerializationSafeHashMap() {
		super();
	}

	public SerializationSafeHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public SerializationSafeHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	public SerializationSafeHashMap(Map<? extends K, ? extends V> m) {
		super(m);
	}

	private Object writeReplace() throws ObjectStreamException {
		HashMap<K, V> replacement = new SerializationSafeHashMap<K, V>(this);
		Iterator<Map.Entry<K, V>> it = replacement.entrySet().iterator();
		while (it.hasNext()) {
			if (!isSerializable(it.next())) {
				it.remove();
			};
		}
		return replacement;
	}

	private boolean isSerializable(Entry<K, V> entry) {
		return (entry.getKey() == null || entry.getKey() instanceof Serializable)
				&& (entry.getValue() == null || entry.getValue() instanceof Serializable);
	}
	
}
