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
package org.riotfamily.common.hibernate;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.CallbackException;
import org.hibernate.EntityMode;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.riotfamily.common.util.Generics;


/**
 * Hibernate {@link Interceptor} that allows the chaining of multiple 
 * implementations.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class ChainedInterceptor implements SessionFactoryAwareInterceptor {

	private Set<Interceptor> interceptors = Collections.emptySet();

	public void setInterceptors(Set<Interceptor> interceptors) {
		this.interceptors = Generics.emptyIfNull(interceptors);
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		for (Interceptor interceptor : interceptors) {
			if (interceptor instanceof SessionFactoryAwareInterceptor) {
				((SessionFactoryAwareInterceptor) interceptor).setSessionFactory(sessionFactory);
			}
		}
	}
	
	public void onDelete(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		
		for (Interceptor interceptor : interceptors) {
			interceptor.onDelete(entity, id, state, propertyNames, types);
		}
	}

	public boolean onLoad(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		
		boolean result = false;
		for (Interceptor interceptor : interceptors) {
			result |= interceptor.onLoad(entity, id, state, propertyNames, types);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public void postFlush(Iterator entities) {
		for (Interceptor interceptor : interceptors) {
			interceptor.postFlush(entities);
		}
	}

	@SuppressWarnings("unchecked")
	public void preFlush(Iterator entities) {
		for (Interceptor interceptor : interceptors) {
			interceptor.preFlush(entities);
		}
	}

	public Boolean isTransient(Object entity) {
		Boolean result = null;
		for (Interceptor interceptor : interceptors) {
			result = interceptor.isTransient(entity);
			if (result != null) {
				break;
			}
		}
		return result;
	}

	public void afterTransactionBegin(Transaction tx) {
		for (Interceptor interceptor : interceptors) {
			interceptor.afterTransactionBegin(tx);
		}
	}

	public void afterTransactionCompletion(Transaction tx) {
		for (Interceptor interceptor : interceptors) {
			interceptor.afterTransactionCompletion(tx);
		}
	}

	public void beforeTransactionCompletion(Transaction tx) {
		for (Interceptor interceptor : interceptors) {
			interceptor.beforeTransactionCompletion(tx);
		}
	}

	public String onPrepareStatement(String sql) {
		for (Interceptor interceptor : interceptors) {
			sql = interceptor.onPrepareStatement(sql);
		}
		return sql;
	}

	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) throws CallbackException {
		
		boolean result = false;
		for (Interceptor interceptor : interceptors) {
			result |= interceptor.onSave(entity, id, state, propertyNames, types);
		}
		return result;
	}

	public boolean onFlushDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		
		boolean result = false;
		for (Interceptor interceptor : interceptors) {
			result |= interceptor.onFlushDirty(entity, id, currentState,
					previousState, propertyNames, types);
		}
		return result;
	}

	public int[] findDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		
		for (Interceptor interceptor : interceptors) {
			int[] result = interceptor.findDirty(entity, id, currentState, previousState, propertyNames, types);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	public Object getEntity(String entityName, Serializable id)
			throws CallbackException {

		for (Interceptor interceptor : interceptors) {
			Object result = interceptor.getEntity(entityName, id);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	public String getEntityName(Object object) throws CallbackException {
		for (Interceptor interceptor : interceptors) {
			String result = interceptor.getEntityName(object);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	public Object instantiate(String entityName, EntityMode entityMode,
			Serializable id) throws CallbackException {

		for (Interceptor interceptor : interceptors) {
			Object result = interceptor.instantiate(entityName, entityMode, id);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	public void onCollectionRecreate(Object collection, Serializable key)
			throws CallbackException {
		
		for (Interceptor interceptor : interceptors) {
			interceptor.onCollectionRecreate(collection, key);
		}
	}

	public void onCollectionRemove(Object collection, Serializable key)
			throws CallbackException {
		
		for (Interceptor interceptor : interceptors) {
			interceptor.onCollectionRemove(collection, key);
		}
	}

	public void onCollectionUpdate(Object collection, Serializable key)
			throws CallbackException {
		
		for (Interceptor interceptor : interceptors) {
			interceptor.onCollectionUpdate(collection, key);
		}
	}

}
