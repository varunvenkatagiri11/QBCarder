package com.tossupflash.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SavedFlashcardRepository extends JpaRepository<SavedFlashcard, Long> {
    List<SavedFlashcard> findByUserIdOrderBySavedAtDesc(Long userId);
    void deleteByIdAndUserId(Long id, Long userId);
} 