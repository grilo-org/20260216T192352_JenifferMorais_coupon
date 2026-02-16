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
class CreateCouponIntegrationTest {

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
    void shouldCreateCouponWithValidData() throws Exception {
        Map<String, Object> request = buildRequest("ABC123", "Desconto 10%", 10.5,
                LocalDateTime.now().plusDays(7), false);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ABC123"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.published").value(false))
                .andExpect(jsonPath("$.redeemed").value(false));
    }

    @Test
    void shouldSanitizeCodeByRemovingSpecialCharacters() throws Exception {
        Map<String, Object> request = buildRequest("AB@C#1$23", "Test", 10.0,
                LocalDateTime.now().plusDays(7), false);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ABC123"));
    }

    @Test
    void shouldTruncateCodeToSixCharacters() throws Exception {
        Map<String, Object> request = buildRequest("ABCDEFGHIJK", "Test", 10.0,
                LocalDateTime.now().plusDays(7), false);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ABCDEF"));
    }

    @Test
    void shouldRejectCodeWithLessThanSixCharacters() throws Exception {
        Map<String, Object> request = buildRequest("ABC", "Test", 10.0,
                LocalDateTime.now().plusDays(7), false);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectDiscountValueBelowMinimum() throws Exception {
        Map<String, Object> request = buildRequest("ABC123", "Test", 0.4,
                LocalDateTime.now().plusDays(7), false);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.discountValue", containsString("0.5")));
    }

    @Test
    void shouldRejectExpirationDateInThePast() throws Exception {
        Map<String, Object> request = buildRequest("ABC123", "Test", 10.0,
                LocalDateTime.now().minusDays(1), false);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectNullCode() throws Exception {
        Map<String, Object> request = buildRequest(null, "Test", 10.0,
                LocalDateTime.now().plusDays(7), false);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectDuplicateCouponCode() throws Exception {
        Map<String, Object> request = buildRequest("ABC123", "First coupon", 10.0,
                LocalDateTime.now().plusDays(7), false);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    void shouldRejectInvalidExpirationDateFormat() throws Exception {
        String json = """
                {
                  "code": "ABC123",
                  "description": "Test",
                  "discountValue": 10.0,
                  "expirationDate": "2026-d11-04T17:14:45.18Z",
                  "published": false
                }
                """;

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("expirationDate")));
    }

    @Test
    void shouldRejectInvalidDiscountValueFormat() throws Exception {
        String json = """
                {
                  "code": "ABC123",
                  "description": "Test",
                  "discountValue": "abc",
                  "expirationDate": "2026-11-04T17:14:45.18",
                  "published": false
                }
                """;

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("discountValue")));
    }

    @Test
    void shouldRejectEmptyBody() throws Exception {
        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectMalformedJson() throws Exception {
        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectUnsupportedMediaType() throws Exception {
        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("some text"))
                .andExpect(status().isUnsupportedMediaType());
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
