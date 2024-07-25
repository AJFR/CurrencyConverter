package com.ajfr.currency.converter;

import com.ajfr.currency.converter.controller.redirect.RedirectController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("integrationTest")
public class RedirectControllerIntegrationTest {

    @Autowired
    private RedirectController redirectController;
    private MockMvc mockMvc;

    @BeforeEach
    public void beforeEach() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(redirectController).build();
    }

    @Test
    public void testShouldRedirect() throws Exception {
        mockMvc.perform(get("/").accept("application/json"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/swagger-ui/index.html"));
    }

}
