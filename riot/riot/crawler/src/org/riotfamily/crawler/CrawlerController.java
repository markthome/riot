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
package org.riotfamily.crawler;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class CrawlerController implements Controller {

	private Crawler crawler;
	
	public CrawlerController(Crawler crawler) {
		this.crawler = crawler;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html");
		
		PrintWriter out = response.getWriter();
		if (crawler.isRunning()) {
			out.print(crawler.getProgress()  + "% completed.");
		}
		else {
			if ("start".equals(request.getParameter("action"))) {
				new Thread(crawler).start();
				out.print("Crawler started.");
			}
			else {
				out.print("<a href=\"?action=start\">Start Crawling</a>");
			}
		}
		return null;
	}
}
