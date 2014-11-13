package org.jbpm.spring.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/index")
public class WebController {

	@RequestMapping(method = RequestMethod.GET)
	public String printHello(ModelMap model) {
		return "index";
	}

}
