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
package org.riotfamily.forms.options;

import java.util.Arrays;
import java.util.Collection;

import org.riotfamily.forms.Element;

public class EnumOptionsModel implements OptionsModel {

	private Class<? extends Enum<?>> enumClass;
	
	public void setEnumClass(Class<? extends Enum<?>> enumClass) {
		this.enumClass = enumClass;
	}
	
	public Collection<?> getOptionValues(Element element) {
		return Arrays.asList(enumClass.getEnumConstants());
	}

}
