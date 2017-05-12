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
package org.riotfamily.forms.element;

import java.io.PrintWriter;

import org.riotfamily.forms.AbstractElement;
import org.riotfamily.forms.ui.Dimension;

public class StaticText extends AbstractElement {

	private String text;
	
	public StaticText(String text) {
		this.text = text;
	}

	@Override
	protected void renderInternal(PrintWriter writer) {
		writer.print(text);
	}
	
	public Dimension getDimension() {
		return getFormContext().getSizing().getTextSize(text);
	}

}
