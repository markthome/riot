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
package org.riotfamily.forms.element.select;

import java.io.PrintWriter;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.TagWriter;


/**
 *
 */
public class OptionTagRenderer implements OptionRenderer {

	public void renderOption(OptionItem option, PrintWriter writer, boolean enabled) {
		new TagWriter(writer)
				.start("option")
				.attribute("id", option.getId())
				.attribute("class", option.getStyleClass())
				.attribute("value", option.getIndex())
				.attribute("selected", option.isSelected())
				.body(FormatUtils.stripTags(option.getLabel()))
				.end();
	}

}