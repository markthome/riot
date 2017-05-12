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
package org.riotfamily.pages.riot.dao;

import java.util.Collection;
import java.util.List;

import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.RiotDaoAdapter;
import org.riotfamily.core.dao.Swapping;
import org.riotfamily.pages.model.Site;
import org.springframework.dao.DataAccessException;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class SiteRiotDao extends RiotDaoAdapter implements Swapping {

	@Override
	public Class<?> getEntityClass() {
		return Site.class;
	}

	@Override
	public Collection<?> list(Object parent, ListParams params) throws DataAccessException {
		return Site.findAll();
	}

	@Override
	public void save(Object entity, Object parent) throws DataAccessException {
		((Site) entity).save();
	}

	@Override
	public Object update(Object entity) throws DataAccessException {
		return ((Site) entity).merge();
	}

	@Override
	public void delete(Object entity, Object parent) throws DataAccessException {
		((Site) entity).delete();
	}

	@Override
	public String getObjectId(Object entity) {
		return ((Site) entity).getId().toString();
	}

	@Override
	public Object load(String id) throws DataAccessException {
		return Site.load(Long.valueOf(id));
	}
	
	public boolean canSwap(Object entity, Object parent, ListParams params,
			int swapWith) {
		
		Site site = (Site) entity;
		List<Site> sites = Site.findAll();
		int i = sites.indexOf(site) + swapWith;
		return i >= 0 && i < sites.size();
	}
	
	public void swapEntity(Object entity, Object parent, ListParams params, 
			int swapWith) {
		
		Site site = (Site) entity;
		List<Site> sites = Site.findAll();
		int i = sites.indexOf(site);
		Site other = sites.get(i + swapWith);
		
		long pos = site.getPosition();
		site.setPosition(other.getPosition());
		other.setPosition(pos);
	}
	
}
