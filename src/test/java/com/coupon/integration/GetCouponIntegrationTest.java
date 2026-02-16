package com.coupon.integration;

import com.coupon.infrastructure.persistence.repository.CouponRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GetCouponIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CouponRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldGetCouponById() throws Exception {
        Map<String, Object> createRequest = buildRequest("ABC123", "Test", 10.0,
                LocalDateTime.now().plusDays(7), false);

        String response = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/coupon/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ABC123"));
    }

    @Test
    void shouldReturn404WhenCouponNotFound() throws Exception {
        mockMvc.perform(get("/coupon/nonexistent-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Coupon not found"));
    }

    @Test
    void shouldReturn404WhenCouponIsDeleted() throws Exception {
        Map<String, Object> createRequest = buildRequest("DEL123", "To delete", 10.0,
                LocalDateTime.now().plusDays(7), false);

        String response = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(delete("/coupon/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/coupon/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Coupon is not active"));
    }

    private Map<String, Object> buildRequest(String code, String description,
                                              Double discountValue, LocalDateTime expirationDate,
                                              Boolean published) {
        Map<String, Object> request = new HashMap<>();
        request.put("code", code);
        request.put("description", description);
        request.put("discountValue", discountValue);
        request.put("expirationDate", expirationDate);
        request.put("published", published);
        return request;
    }
}
