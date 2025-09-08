package com.example.debugger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {
  // Forward anything that's not /api or /actuator or static file extensions
  @GetMapping({
      "/{path:^(?!api|actuator|ws|static|assets|webjars).*}",
      "/{path:^(?!api|actuator|ws|static|assets|webjars).*}/{subpath:^(?!\\.).*}"
  })
  public String forward() {
    return "forward:/index.html";
  }
}
