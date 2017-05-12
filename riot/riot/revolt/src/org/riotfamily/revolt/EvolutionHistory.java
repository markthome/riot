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
package org.riotfamily.revolt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.riotfamily.revolt.definition.Table;
import org.riotfamily.revolt.support.DatabaseUtils;
import org.riotfamily.revolt.support.LogTable;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class EvolutionHistory implements BeanNameAware {

	private String[] depends = new String[0];
	
	private String moduleName;
	
	private String checkTableName;
	
	private List<ChangeSet> changeSets;

	private LogTable logTable;
	
	private ArrayList<String> appliedIds;
	
	public void setBeanName(String name) {
		this.moduleName = name;
	}
	
	/**
	 * Sets the name of a table that, if it exists, indicates that the module
	 * is already installed. The check is performed when the history contains  
	 * only one change-set in order to determine whether the module was 
	 * installed before. In case of a fresh install (check-table not found), 
	 * no changes are performed and the change-set are marked as applied.
	 * <p>
	 * Use the name of a table that exits <em>before</em> any change-set is 
	 * applied. It's okay when that table is renamed or dropped later.
	 * When no table name is set, all changes are performed that have not been
	 * marked as applied.
	 * </p>
	 */
	public void setCheckTableName(String checkTableName) {
		this.checkTableName = checkTableName;
	}
	
	public String getModuleName() {
		return this.moduleName;
	}

	public void setChangeSets(ChangeSet[] changeSets) {
		this.changeSets = new ArrayList<ChangeSet>();
		for (int i = 0; i < changeSets.length; i++) {
			ChangeSet changeSet = changeSets[i];
			changeSet.setHistory(this);
			changeSet.setSequenceNumber(i);
			this.changeSets.add(changeSet);
		}
	}

	/**
	 * Initializes the history from the given log-table.
	 */
	public void init(LogTable logTable, JdbcTemplate template) {
		this.logTable = logTable;
		appliedIds = new ArrayList<String>();
		appliedIds.addAll(logTable.getAppliedChangeSetIds(moduleName));
	}

	private boolean isModuleAlreadyInstalled(JdbcTemplate template) {
		if (!appliedIds.isEmpty()) {
			// Some changes have already been applied
			return true; 
		}
		return checkTableName == null || DatabaseUtils.tableExists(template, new Table(checkTableName));
	}
	
	/**
	 * Returns a script that needs to be executed in order update the schema.
	 */
	public Script getScript(Dialect dialect, JdbcTemplate template) {
		if (isModuleAlreadyInstalled(template)) {
			return getMigrationScript(dialect, template);
		}
		else {
			return getInitScript(template);
		}
	}
	
	private Script getInitScript(JdbcTemplate template) {
		Script script = new Script();
		for (ChangeSet changeSet : changeSets) {
			script.append(markAsApplied(changeSet, template));
		}
		return script;
	}
	
	private Script getMigrationScript(Dialect dialect, JdbcTemplate template) {
		Script script = new Script();
		for (ChangeSet changeSet : changeSets) {
			if (!isApplied(changeSet)) {
				script.append(changeSet.getScript(dialect, template));
				script.append(markAsApplied(changeSet, template));
			}
		}
		return script;
	}
		
	/**
	 * Returns whether the given changeSet has already been applied.
	 */
	private boolean isApplied(ChangeSet changeSet) {
		return appliedIds.contains(changeSet.getId());
	}
	
	/**
	 * Returns a script that can be used to add an entry to the log-table that
	 * marks the given ChangeSet as applied. 
	 */
	private Script markAsApplied(ChangeSet changeSet, JdbcTemplate template) {
		appliedIds.add(changeSet.getId());
		return logTable.getInsertScript(changeSet);
	}

	public void setDepends(String[] depends) {
		this.depends = depends;
	}

	public boolean dependsOn(EvolutionHistory history) {
		return Arrays.asList(depends).contains(history.getModuleName());
	}
}
