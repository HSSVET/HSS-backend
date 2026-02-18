package com.hss.hss_backend.controller.example;

import com.hss.hss_backend.dto.common.ApiResponse;
import com.hss.hss_backend.dto.common.PagedResponse;
import com.hss.hss_backend.util.CacheUtils;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Example Controller demonstrating React Query compatible patterns
 * 
 * USAGE PATTERNS:
 * 
 * 1. Simple GET with caching:
 *    return ResponseEntity.ok()
 *        .cacheControl(CacheUtils.mediumCache())
 *        .body(ApiResponse.success(data));
 * 
 * 2. Paginated GET with headers:
 *    PagedResponse<T> paged = PagedResponse.of(page);
 *    return ResponseEntity.ok()
 *        .cacheControl(CacheUtils.shortCache())
 *        .header("X-Total-Count", String.valueOf(page.getTotalElements()))
 *        .body(ApiResponse.success(paged));
 * 
 * 3. Mutation (POST/PUT/DELETE):
 *    return ResponseEntity.ok()
 *        .cacheControl(CacheUtils.noCache())
 *        .body(ApiResponse.success(created, "Created successfully"));
 * 
 * 4. With ETag:
 *    return ResponseEntity.ok()
 *        .cacheControl(CacheUtils.mediumCache())
 *        .eTag(CacheUtils.generateETag(data))
 *        .body(ApiResponse.success(data));
 */
@RestController
@RequestMapping("/api/example")
public class ExampleReactQueryController {
    
    // ============================================================================
    // PATTERN 1: Simple GET with caching
    // ============================================================================
    
    /**
     * GET /api/example/items
     * Frontend: const { data } = useQuery({ queryKey: ['items'], ... })
     */
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<ExampleDTO>>> getItems() {
        List<ExampleDTO> items = fetchItems(); // Your service call
        
        return ResponseEntity.ok()
                .cacheControl(CacheUtils.mediumCache())  // 5 minutes
                .eTag(CacheUtils.generateETag(items))
                .body(ApiResponse.success(items));
    }
    
    // ============================================================================
    // PATTERN 2: Paginated GET
    // ============================================================================
    
    /**
     * GET /api/example/paged?page=0&size=20
     * Frontend: const { data } = useQuery({ 
     *   queryKey: ['items', page], 
     *   queryFn: () => fetch(`/api/example/paged?page=${page}`)
     * })
     */
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<ExampleDTO>>> getPagedItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ExampleDTO> pageData = fetchPage(pageable); // Your service call
        
        PagedResponse<ExampleDTO> pagedResponse = PagedResponse.of(pageData);
        
        return ResponseEntity.ok()
                .cacheControl(CacheUtils.shortCache())  // 30 seconds
                .header("X-Total-Count", String.valueOf(pageData.getTotalElements()))
                .header("X-Page-Number", String.valueOf(page))
                .header("X-Page-Size", String.valueOf(size))
                .body(ApiResponse.success(pagedResponse));
    }
    
    // ============================================================================
    // PATTERN 3: Infinite Scroll
    // ============================================================================
    
    /**
     * Optimized for React Query useInfiniteQuery
     * Frontend: const { data, fetchNextPage } = useInfiniteQuery({
     *   queryKey: ['items'],
     *   queryFn: ({ pageParam = 0 }) => fetch(`/api/example/infinite?page=${pageParam}`)
     * })
     */
    @GetMapping("/infinite")
    public ResponseEntity<ApiResponse<PagedResponse<ExampleDTO>>> getInfiniteItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<ExampleDTO> pageData = fetchPage(PageRequest.of(page, size));
        PagedResponse<ExampleDTO> response = PagedResponse.of(pageData);
        
        return ResponseEntity.ok()
                .cacheControl(CacheUtils.shortCache())
                .header("X-Total-Count", String.valueOf(pageData.getTotalElements()))
                .header("X-Has-Next", String.valueOf(pageData.hasNext()))
                .body(ApiResponse.success(response));
    }
    
    // ========================================================================== ==
    // PATTERN 4: Single Item GET
    // ============================================================================
    
    /**
     * GET /api/example/{id}
     * Frontend: const { data } = useQuery({ queryKey: ['item', id], ... })
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExampleDTO>> getItem(@PathVariable Long id) {
        ExampleDTO item = fetchById(id); // Your service call
        
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok()
                .cacheControl(CacheUtils.mediumCache())
                .eTag(CacheUtils.generateETag(item))
                .body(ApiResponse.success(item));
    }
    
    // ============================================================================
    // PATTERN 5: POST (Create)
    // ============================================================================
    
    /**
     * POST /api/example
     * Frontend: const mutation = useMutation({ 
     *   mutationFn: (data) => post('/api/example', data)
     * })
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ExampleDTO>> createItem(
            @RequestBody CreateExampleRequest request
    ) {
        ExampleDTO created = create(request); // Your service call
        
        return ResponseEntity.ok()
                .cacheControl(CacheUtils.noCache())  // Never cache mutations
                .body(ApiResponse.success(created, "Created successfully"));
    }
    
    // ============================================================================
    // PATTERN 6: PUT (Update)
    // ============================================================================
    
    /**
     * PUT /api/example/{id}
     * Frontend: useMutation with optimistic updates
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExampleDTO>> updateItem(
            @PathVariable Long id,
            @RequestBody UpdateExampleRequest request
    ) {
        ExampleDTO updated = update(id, request); // Your service call
        
        return ResponseEntity.ok()
                .cacheControl(CacheUtils.noCache())
                .body(ApiResponse.success(updated, "Updated successfully"));
    }
    
    // ============================================================================
    // PATTERN 7: DELETE
    // ============================================================================
    
    /**
     * DELETE /api/example/{id}
     * Frontend: useMutation for deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long id) {
        delete(id); // Your service call
        
        return ResponseEntity.ok()
                .cacheControl(CacheUtils.noCache())
                .body(ApiResponse.success(null, "Deleted successfully"));
    }
    
    // ============================================================================
    // Mock Methods (Replace with actual service calls)
    // ============================================================================
    
    private List<ExampleDTO> fetchItems() {
        return List.of(new ExampleDTO(1L, "Item 1"), new ExampleDTO(2L, "Item 2"));
    }
    
    private Page<ExampleDTO> fetchPage(Pageable pageable) {
        // Replace with actual repository call
        return Page.empty();
    }
    
    private ExampleDTO fetchById(Long id) {
        return new ExampleDTO(id, "Item " + id);
    }
    
    private ExampleDTO create(CreateExampleRequest request) {
        return new ExampleDTO(99L, request.getName());
    }
    
    private ExampleDTO update(Long id, UpdateExampleRequest request) {
        return new ExampleDTO(id, request.getName());
    }
    
    private void delete(Long id) {
        // Delete logic
    }
    
    // ============================================================================
    // DTOs
    // ============================================================================
    
    @Data
    private static class ExampleDTO {
        private Long id;
        private String name;
        
        public ExampleDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
    
    @Data
    private static class CreateExampleRequest {
        private String name;
    }
    
    @Data
    private static class UpdateExampleRequest {
        private String name;
    }
}
