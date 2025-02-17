package pl.cyfronet.s4e.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import pl.cyfronet.s4e.BasicTest;
import pl.cyfronet.s4e.Constants;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.cyfronet.s4e.Constants.API_PREFIX_V1;

@AutoConfigureMockMvc
@BasicTest
public class ConfigControllerTest {
    @Value("${geoserver.outsideBaseUrl}")
    private String geoserverOutsideBaseUrl;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnConfiguration() throws Exception {
        mockMvc.perform(get(API_PREFIX_V1+"/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.backendDateFormat").value(Constants.JACKSON_DATE_FORMAT))
                .andExpect(jsonPath("$.geoserverUrl").value(geoserverOutsideBaseUrl));
    }
}
