package com.hss.hss_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hss.hss_backend.service.StorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class SignedUrlIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private StorageService storageService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private static final String TEST_FILE_PATH = "gs://test-bucket/test-folder/test-file.jpg";
    private static final String TEST_SIGNED_URL = "https://storage.googleapis.com/test-bucket/test-folder/test-file.jpg?X-Goog-Algorithm=GOOG4-RSA-SHA256&X-Goog-Credential=test&X-Goog-Date=20231201T000000Z&X-Goog-Expires=3600&X-Goog-SignedHeaders=host&X-Goog-Signature=test-signature";

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void signedUrlGeneration_EndToEndTest() throws Exception {
        // Given
        when(storageService.generateSignedUrl(TEST_FILE_PATH, 3600000L))
                .thenReturn(TEST_SIGNED_URL);

        // When & Then
        mockMvc.perform(get("/api/files/signed-url")
                .param("filePath", TEST_FILE_PATH)
                .param("expirationTime", "3600000")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.signedUrl").value(TEST_SIGNED_URL))
                .andExpect(jsonPath("$.expirationTime").value("3600000"));
    }

    @Test
    @WithMockUser(roles = "VETERINARIAN")
    void signedUrlGeneration_WithDifferentRoles() throws Exception {
        // Given
        when(storageService.generateSignedUrl(TEST_FILE_PATH, 1800000L))
                .thenReturn(TEST_SIGNED_URL);

        // When & Then
        mockMvc.perform(get("/api/files/signed-url")
                .param("filePath", TEST_FILE_PATH)
                .param("expirationTime", "1800000")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.signedUrl").value(TEST_SIGNED_URL))
                .andExpect(jsonPath("$.expirationTime").value("1800000"));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void signedUrlGeneration_WithStaffRole() throws Exception {
        // Given
        when(storageService.generateSignedUrl(TEST_FILE_PATH, 3600000L))
                .thenReturn(TEST_SIGNED_URL);

        // When & Then
        mockMvc.perform(get("/api/files/signed-url")
                .param("filePath", TEST_FILE_PATH)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.signedUrl").value(TEST_SIGNED_URL))
                .andExpect(jsonPath("$.expirationTime").value("3600000"));
    }

    @Test
    void signedUrlGeneration_UnauthorizedAccess() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/files/signed-url")
                .param("filePath", TEST_FILE_PATH)
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void signedUrlGeneration_InsufficientPermissions() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/files/signed-url")
                .param("filePath", TEST_FILE_PATH)
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void signedUrlGeneration_FileNotFound() throws Exception {
        // Given
        String nonExistentFile = "gs://test-bucket/non-existent-file.jpg";
        when(storageService.generateSignedUrl(nonExistentFile, 3600000L))
                .thenThrow(new RuntimeException("File not found: " + nonExistentFile));

        // When & Then
        mockMvc.perform(get("/api/files/signed-url")
                .param("filePath", nonExistentFile)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void signedUrlGeneration_InvalidExpirationTime() throws Exception {
        // Given
        when(storageService.generateSignedUrl(TEST_FILE_PATH, -1000L))
                .thenReturn(TEST_SIGNED_URL);

        // When & Then
        mockMvc.perform(get("/api/files/signed-url")
                .param("filePath", TEST_FILE_PATH)
                .param("expirationTime", "-1000")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.signedUrl").value(TEST_SIGNED_URL))
                .andExpect(jsonPath("$.expirationTime").value("-1000"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void signedUrlGeneration_MissingFilePath() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/files/signed-url")
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void signedUrlGeneration_InvalidExpirationTimeFormat() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/files/signed-url")
                .param("filePath", TEST_FILE_PATH)
                .param("expirationTime", "invalid")
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
