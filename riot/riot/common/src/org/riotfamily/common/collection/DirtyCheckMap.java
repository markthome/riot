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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class DirtyCheckMap<K, V> implements Map<K, V> {

	private Map<K, V> map;

	private boolean dirty;
	
	public DirtyCheckMap(Map<K, V> map) {
		this.map = map;
	}

	protected void dirty() {
		this.dirty = true;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	// -----------------------------------------------------------------------
	// Methods that directly modify the map
	// -----------------------------------------------------------------------
	
	public void clear() {
		dirty();
		map.clear();
	}

	public V put(K key, V value) {
		dirty();
		return map.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		dirty();
		map.putAll(m);
	}

	public V remove(Object key) {
		dirty();
		return map.remove(key);
	}

	// -----------------------------------------------------------------------
	// Methods that return Collections backed by the map
	// -----------------------------------------------------------------------
	
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return new DirtyCheckMapSet<java.util.Map.Entry<K, V>>(map.entrySet());
	}
	
	public Set<K> keySet() {
		return new DirtyCheckMapSet<K>(map.keySet());
	}
	
	public Collection<V> values() {
		return new DirtyCheckMapSet<V>(map.values());
	}
	
	private class DirtyCheckMapSet<E> extends DirtyCheckSet<E> {

		public DirtyCheckMapSet(Collection<E> collection) {
			super(collection);
		}

		@Override
		protected void dirty() {
			DirtyCheckMap.this.dirty();
		}
	}
	
	// -----------------------------------------------------------------------
	// Methods that don't modify the structure
	// -----------------------------------------------------------------------
	
	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}
	
	public V get(Object key) {
		return map.get(key);
	}
	
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public boolean equals(Object o) {
		return map.equals(o);
	}

	public int hashCode() {
		return map.hashCode();
	}

}
