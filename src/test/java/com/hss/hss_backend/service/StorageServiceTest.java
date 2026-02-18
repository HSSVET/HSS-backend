package com.hss.hss_backend.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private Storage storage;

    @Mock
    private Blob blob;

    @InjectMocks
    private StorageService storageService;

    private static final String BUCKET_NAME = "test-bucket";
    private static final String TEST_FILE_PATH = "gs://test-bucket/test-folder/test-file.jpg";
    private static final String TEST_BLOB_NAME = "test-folder/test-file.jpg";
    private static final String TEST_SIGNED_URL = "https://storage.googleapis.com/test-bucket/test-folder/test-file.jpg?X-Goog-Algorithm=GOOG4-RSA-SHA256&X-Goog-Credential=test&X-Goog-Date=20231201T000000Z&X-Goog-Expires=3600&X-Goog-SignedHeaders=host&X-Goog-Signature=test-signature";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(storageService, "bucketName", BUCKET_NAME);
    }

    @Test
    void generateSignedUrl_Success() throws MalformedURLException {
        // Given
        long expirationTime = 3600000L; // 1 saat
        BlobId expectedBlobId = BlobId.of(BUCKET_NAME, TEST_BLOB_NAME);

        when(storage.get(expectedBlobId)).thenReturn(blob);
        when(blob.signUrl(eq(expirationTime), eq(TimeUnit.MILLISECONDS), any(Storage.SignUrlOption.class)))
                .thenReturn(new URL(TEST_SIGNED_URL));

        // When
        String result = storageService.generateSignedUrl(TEST_FILE_PATH, expirationTime);

        // Then
        assertNotNull(result);
        assertEquals(TEST_SIGNED_URL, result);
        verify(storage).get(expectedBlobId);
        verify(blob).signUrl(eq(expirationTime), eq(TimeUnit.MILLISECONDS), any(Storage.SignUrlOption.class));
    }

    @Test
    void generateSignedUrl_WithDifferentExpirationTimes() throws MalformedURLException {
        // Given
        long shortExpiration = 60000L; // 1 dakika
        long longExpiration = 86400000L; // 24 saat

        BlobId expectedBlobId = BlobId.of(BUCKET_NAME, TEST_BLOB_NAME);

        when(storage.get(expectedBlobId)).thenReturn(blob);
        when(blob.signUrl(anyLong(), eq(TimeUnit.MILLISECONDS), any(Storage.SignUrlOption.class)))
                .thenReturn(new URL(TEST_SIGNED_URL));

        // When
        String result1 = storageService.generateSignedUrl(TEST_FILE_PATH, shortExpiration);
        String result2 = storageService.generateSignedUrl(TEST_FILE_PATH, longExpiration);

        // Then
        assertNotNull(result1);
        assertNotNull(result2);
        verify(blob, times(2)).signUrl(anyLong(), eq(TimeUnit.MILLISECONDS), any(Storage.SignUrlOption.class));
    }

    @Test
    void generateSignedUrl_FileNotFound() {
        // Given
        String nonExistentFile = "gs://test-bucket/non-existent-file.jpg";
        BlobId expectedBlobId = BlobId.of(BUCKET_NAME, "non-existent-file.jpg");

        when(storage.get(expectedBlobId)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            storageService.generateSignedUrl(nonExistentFile, 3600000L);
        });

        assertEquals("File not found in GCP: " + nonExistentFile + ", bucket: " + expectedBlobId.getBucket(),
                exception.getMessage());
        verify(storage).get(expectedBlobId);
    }

    @Test
    void generateSignedUrl_WithSimpleFilePath() throws MalformedURLException {
        // Given
        String simpleFilePath = "test-folder/simple-file.txt";
        long expirationTime = 3600000L;
        BlobId expectedBlobId = BlobId.of(BUCKET_NAME, simpleFilePath);

        when(storage.get(expectedBlobId)).thenReturn(blob);
        when(blob.signUrl(eq(expirationTime), eq(TimeUnit.MILLISECONDS), any(Storage.SignUrlOption.class)))
                .thenReturn(new URL(TEST_SIGNED_URL));

        // When
        String result = storageService.generateSignedUrl(simpleFilePath, expirationTime);

        // Then
        assertNotNull(result);
        assertEquals(TEST_SIGNED_URL, result);
        verify(storage).get(expectedBlobId);
    }

    @Test
    void generateSignedUrl_WithZeroExpirationTime() throws MalformedURLException {
        // Given
        long zeroExpirationTime = 0L;
        BlobId expectedBlobId = BlobId.of(BUCKET_NAME, TEST_BLOB_NAME);

        when(storage.get(expectedBlobId)).thenReturn(blob);
        when(blob.signUrl(eq(zeroExpirationTime), eq(TimeUnit.MILLISECONDS), any(Storage.SignUrlOption.class)))
                .thenReturn(new URL(TEST_SIGNED_URL));

        // When
        String result = storageService.generateSignedUrl(TEST_FILE_PATH, zeroExpirationTime);

        // Then
        assertNotNull(result);
        assertEquals(TEST_SIGNED_URL, result);
        verify(blob).signUrl(eq(zeroExpirationTime), eq(TimeUnit.MILLISECONDS), any(Storage.SignUrlOption.class));
    }

    @Test
    void generateSignedUrl_WithNegativeExpirationTime() throws MalformedURLException {
        // Given
        long negativeExpirationTime = -1000L;
        BlobId expectedBlobId = BlobId.of(BUCKET_NAME, TEST_BLOB_NAME);

        when(storage.get(expectedBlobId)).thenReturn(blob);
        when(blob.signUrl(eq(negativeExpirationTime), eq(TimeUnit.MILLISECONDS), any(Storage.SignUrlOption.class)))
                .thenReturn(new URL(TEST_SIGNED_URL));

        // When
        String result = storageService.generateSignedUrl(TEST_FILE_PATH, negativeExpirationTime);

        // Then
        assertNotNull(result);
        assertEquals(TEST_SIGNED_URL, result);
        verify(blob).signUrl(eq(negativeExpirationTime), eq(TimeUnit.MILLISECONDS), any(Storage.SignUrlOption.class));
    }

    @Test
    void generateSignedUrl_StorageException() {
        // Given
        BlobId expectedBlobId = BlobId.of(BUCKET_NAME, TEST_BLOB_NAME);

        when(storage.get(expectedBlobId)).thenThrow(new RuntimeException("Storage connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            storageService.generateSignedUrl(TEST_FILE_PATH, 3600000L);
        });

        assertEquals("Storage connection failed", exception.getMessage());
        verify(storage).get(expectedBlobId);
    }

    @Test
    void generateSignedUrl_BlobSignUrlException() throws MalformedURLException {
        // Given
        BlobId expectedBlobId = BlobId.of(BUCKET_NAME, TEST_BLOB_NAME);

        when(storage.get(expectedBlobId)).thenReturn(blob);
        when(blob.signUrl(anyLong(), any(TimeUnit.class), any(Storage.SignUrlOption.class)))
                .thenThrow(new RuntimeException("Failed to sign URL"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            storageService.generateSignedUrl(TEST_FILE_PATH, 3600000L);
        });

        assertEquals("Failed to sign URL", exception.getMessage());
        verify(blob).signUrl(anyLong(), eq(TimeUnit.MILLISECONDS), any(Storage.SignUrlOption.class));
    }

    @Test
    void generateSignedUrl_CrossBucket() throws MalformedURLException {
        // Given
        String crossBucketPath = "gs://other-bucket/folder/file.jpg";
        long expirationTime = 3600000L;
        BlobId expectedBlobId = BlobId.of("other-bucket", "folder/file.jpg");

        when(storage.get(expectedBlobId)).thenReturn(blob);
        when(blob.signUrl(eq(expirationTime), eq(TimeUnit.MILLISECONDS), any(Storage.SignUrlOption.class)))
                .thenReturn(new URL(TEST_SIGNED_URL));

        // When
        String result = storageService.generateSignedUrl(crossBucketPath, expirationTime);

        // Then
        assertNotNull(result);
        assertEquals(TEST_SIGNED_URL, result);
        verify(storage).get(expectedBlobId);
    }

    @Test
    void extractBlobName_WithGsProtocol() {
        // Given
        String gsPath = "gs://bucket-name/folder/subfolder/file.txt";

        // When
        String result = invokeExtractBlobName(gsPath);

        // Then
        assertEquals("folder/subfolder/file.txt", result);
    }

    @Test
    void extractBlobName_WithSimplePath() {
        // Given
        String simplePath = "folder/file.txt";

        // When
        String result = invokeExtractBlobName(simplePath);

        // Then
        assertEquals("folder/file.txt", result);
    }

    @Test
    void extractBlobName_WithInvalidGsPath() {
        // Given
        String invalidGsPath = "gs://bucket-name";

        // When
        String result = invokeExtractBlobName(invalidGsPath);

        // Then
        // With logic fallback: bucketName (default), "bucket-name" (path without
        // protocol)
        assertEquals("bucket-name", result);
    }

    // Helper method to test private extractBlobName method
    private String invokeExtractBlobName(String filePath) {
        try {
            java.lang.reflect.Method method = StorageService.class.getDeclaredMethod("extractBlobName", String.class);
            method.setAccessible(true);
            return (String) method.invoke(storageService, filePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke extractBlobName method", e);
        }
    }

    @Test
    void generateSignedUrl_UploadsPath_WithLocalDisabled_ShouldReturnGcsUrl() throws MalformedURLException {
        // Given
        ReflectionTestUtils.setField(storageService, "useLocalStorageFallback", false);
        String uploadsPath = "/uploads/test-file.jpg";
        long expirationTime = 3600000L;
        BlobId expectedBlobId = BlobId.of(BUCKET_NAME, "uploads/test-file.jpg"); // "uploads/" prefix is kept as part of
                                                                                 // blob name

        when(storage.get(expectedBlobId)).thenReturn(blob);
        when(blob.signUrl(eq(expirationTime), eq(TimeUnit.MILLISECONDS), any(Storage.SignUrlOption.class)))
                .thenReturn(new URL(TEST_SIGNED_URL));

        // When
        String result = storageService.generateSignedUrl(uploadsPath, expirationTime);

        // Then
        assertNotNull(result);
        assertEquals(TEST_SIGNED_URL, result);
        verify(storage).get(expectedBlobId);
    }

    @Test
    void generateSignedUrl_WithBucketFallback_ShouldReturnUrlFromDefaultBucket() throws MalformedURLException {
        // Given
        String wrongBucketPath = "gs://wrong-bucket/test-folder/test-file.jpg";
        long expirationTime = 3600000L;
        BlobId originalBlobId = BlobId.of("wrong-bucket", "test-folder/test-file.jpg");
        BlobId fallbackBlobId = BlobId.of(BUCKET_NAME, "test-folder/test-file.jpg");

        // First attempt returns null (file not found in wrong-bucket)
        when(storage.get(originalBlobId)).thenReturn(null);
        // Fallback attempt returns the blob
        when(storage.get(fallbackBlobId)).thenReturn(blob);

        when(blob.signUrl(eq(expirationTime), eq(TimeUnit.MILLISECONDS), any(Storage.SignUrlOption.class)))
                .thenReturn(new URL(TEST_SIGNED_URL));

        // When
        String result = storageService.generateSignedUrl(wrongBucketPath, expirationTime);

        // Then
        assertNotNull(result);
        assertEquals(TEST_SIGNED_URL, result);
        verify(storage).get(originalBlobId);
        verify(storage).get(fallbackBlobId);
    }
}