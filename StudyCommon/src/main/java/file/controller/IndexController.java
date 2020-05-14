package file.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @version:（1.0.0.0）
 * @Description: （对类进行功能描述）
 * @author: enoch
 * @date: 2018/12/5 17:48
 */
@Controller
public class IndexController {
    @RequestMapping(value = "/")
    public String index() {
        return "redirect:/swagger-ui.html";
    }
}
