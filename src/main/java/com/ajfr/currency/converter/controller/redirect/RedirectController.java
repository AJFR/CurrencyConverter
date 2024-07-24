package com.ajfr.currency.converter.controller.redirect;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class RedirectController {

    @GetMapping("/")
    public void getSwagger(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui/index.html");
    }

}
