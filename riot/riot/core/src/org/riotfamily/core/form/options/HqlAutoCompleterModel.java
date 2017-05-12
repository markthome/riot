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
package org.riotfamily.core.form.options;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.forms.element.suggest.AutocompleteTextField;
import org.riotfamily.forms.element.suggest.AutocompleterModel;

public class HqlAutoCompleterModel implements AutocompleterModel {

	private SessionFactory sessionFactory;
	
	private String hql;
	
	public HqlAutoCompleterModel(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setHql(String hql) {
		this.hql = hql;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<String> getSuggestions(String search,
			AutocompleteTextField element, HttpServletRequest request) {
		
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setParameter("search", "%" + search  + "%");
		if (hql.indexOf(":parent") != -1) {
			query.setParameter("parent", ScreenContext.Binding.get(request).getParent());	
		}
		return query.list();
	}

}
