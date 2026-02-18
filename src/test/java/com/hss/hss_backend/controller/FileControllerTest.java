package com.hss.hss_backend.controller;

import com.hss.hss_backend.service.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
class FileControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private StorageService storageService;

        @Autowired
        private ObjectMapper objectMapper;

        private static final String TEST_FILE_PATH = "gs://test-bucket/test-folder/test-file.jpg";
        private static final String TEST_SIGNED_URL = "https://storage.googleapis.com/test-bucket/test-folder/test-file.jpg?X-Goog-Algorithm=GOOG4-RSA-SHA256&X-Goog-Credential=test&X-Goog-Date=20231201T000000Z&X-Goog-Expires=3600&X-Goog-SignedHeaders=host&X-Goog-Signature=test-signature";

        @BeforeEach
        void setUp() {
                // Mock setup burada yapılacak
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void generateSignedUrl_Success() throws Exception {
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
        void generateSignedUrl_WithCustomExpirationTime() throws Exception {
                // Given
                long customExpirationTime = 7200000L; // 2 saat
                when(storageService.generateSignedUrl(TEST_FILE_PATH, customExpirationTime))
                                .thenReturn(TEST_SIGNED_URL);

                // When & Then
                mockMvc.perform(get("/api/files/signed-url")
                                .param("filePath", TEST_FILE_PATH)
                                .param("expirationTime", String.valueOf(customExpirationTime))
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.signedUrl").value(TEST_SIGNED_URL))
                                .andExpect(jsonPath("$.expirationTime").value(String.valueOf(customExpirationTime)));
        }

        @Test
        @WithMockUser(roles = "STAFF")
        void generateSignedUrl_WithDefaultExpirationTime() throws Exception {
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
        @WithMockUser(roles = "ADMIN")
        void generateSignedUrl_FileNotFound() throws Exception {
                // Given
                String nonExistentFile = "gs://test-bucket/non-existent-file.jpg";
                doThrow(new RuntimeException("File not found: " + nonExistentFile))
                                .when(storageService).generateSignedUrl(nonExistentFile, 3600000L);

                // When & Then
                mockMvc.perform(get("/api/files/signed-url")
                                .param("filePath", nonExistentFile)
                                .with(csrf()))
                                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void generateSignedUrl_InvalidFilePath() throws Exception {
                // Given
                String invalidFilePath = "invalid-path";
                doThrow(new RuntimeException("File not found: " + invalidFilePath))
                                .when(storageService).generateSignedUrl(invalidFilePath, 3600000L);

                // When & Then
                mockMvc.perform(get("/api/files/signed-url")
                                .param("filePath", invalidFilePath)
                                .with(csrf()))
                                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void generateSignedUrl_MissingFilePathParameter() throws Exception {
                // When & Then
                mockMvc.perform(get("/api/files/signed-url")
                                .with(csrf()))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void generateSignedUrl_UnauthorizedAccess() throws Exception {
                // When & Then
                mockMvc.perform(get("/api/files/signed-url")
                                .param("filePath", TEST_FILE_PATH)
                                .with(csrf()))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = "USER")
        void generateSignedUrl_WithUserRole() throws Exception {
                // Given
                when(storageService.generateSignedUrl(TEST_FILE_PATH, 3600000L))
                                .thenReturn(TEST_SIGNED_URL);

                // When & Then
                mockMvc.perform(get("/api/files/signed-url")
                                .param("filePath", TEST_FILE_PATH)
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.signedUrl").value(TEST_SIGNED_URL));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void generateSignedUrl_NegativeExpirationTime() throws Exception {
                // Given
                long negativeExpirationTime = -1000L;
                when(storageService.generateSignedUrl(TEST_FILE_PATH, negativeExpirationTime))
                                .thenReturn(TEST_SIGNED_URL);

                // When & Then
                mockMvc.perform(get("/api/files/signed-url")
                                .param("filePath", TEST_FILE_PATH)
                                .param("expirationTime", String.valueOf(negativeExpirationTime))
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.signedUrl").value(TEST_SIGNED_URL))
                                .andExpect(jsonPath("$.expirationTime").value(String.valueOf(negativeExpirationTime)));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void generateSignedUrl_ZeroExpirationTime() throws Exception {
                // Given
                long zeroExpirationTime = 0L;
                when(storageService.generateSignedUrl(TEST_FILE_PATH, zeroExpirationTime))
                                .thenReturn(TEST_SIGNED_URL);

                // When & Then
                mockMvc.perform(get("/api/files/signed-url")
                                .param("filePath", TEST_FILE_PATH)
                                .param("expirationTime", String.valueOf(zeroExpirationTime))
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.signedUrl").value(TEST_SIGNED_URL))
                                .andExpect(jsonPath("$.expirationTime").value("0"));
        }
}
