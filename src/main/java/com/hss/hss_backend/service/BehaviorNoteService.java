package com.hss.hss_backend.service;

import com.hss.hss_backend.dto.request.BehaviorNoteCreateRequest;
import com.hss.hss_backend.dto.request.BehaviorNoteUpdateRequest;
import com.hss.hss_backend.dto.response.BehaviorNoteResponse;

import java.util.List;

public interface BehaviorNoteService {
    
    BehaviorNoteResponse createBehaviorNote(BehaviorNoteCreateRequest request);
    
    BehaviorNoteResponse getBehaviorNoteById(Long behaviorNoteId);
    
    List<BehaviorNoteResponse> getBehaviorNotesByAnimalId(Long animalId);
    
    BehaviorNoteResponse updateBehaviorNote(Long behaviorNoteId, BehaviorNoteUpdateRequest request);
    
    void deleteBehaviorNote(Long behaviorNoteId);
    
    long countByAnimalId(Long animalId);
}
