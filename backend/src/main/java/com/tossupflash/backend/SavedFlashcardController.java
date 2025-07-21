package com.tossupflash.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/saved-flashcards")
@CrossOrigin(origins = "http://localhost:5173")
public class SavedFlashcardController {
    
    @Autowired
    private SavedFlashcardRepository savedFlashcardRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping
    public ResponseEntity<SavedFlashcard> saveFlashcard(@RequestBody SaveFlashcardRequest request, 
                                                       HttpServletRequest httpRequest) {
        System.out.println("DEBUG: SaveFlashcard endpoint called");
        System.out.println("DEBUG: Request URI: " + httpRequest.getRequestURI());
        System.out.println("DEBUG: Authorization header: " + httpRequest.getHeader("Authorization"));
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        System.out.println("DEBUG: UserId from request attribute: " + userId);
        
        if (userId == null) {
            System.out.println("DEBUG: UserId is null, returning 401");
            return ResponseEntity.status(401).build();
        }
        
        User user = userRepository.findById(userId).orElse(null);
        System.out.println("DEBUG: User found: " + (user != null ? user.getUsername() : "null"));
        if (user == null) {
            System.out.println("DEBUG: User not found, returning 401");
            return ResponseEntity.status(401).build();
        }
        
        SavedFlashcard flashcard = new SavedFlashcard(
            request.getFront(), 
            request.getBack(), 
            request.getTopic(), 
            user
        );
        System.out.println("DEBUG: About to save flashcard: " + request.getFront());
        
        SavedFlashcard saved = savedFlashcardRepository.save(flashcard);
        System.out.println("DEBUG: Flashcard saved successfully with ID: " + saved.getId());
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping
    public ResponseEntity<List<SavedFlashcard>> getSavedFlashcards(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        List<SavedFlashcard> flashcards = savedFlashcardRepository.findByUserIdOrderBySavedAtDesc(userId);
        return ResponseEntity.ok(flashcards);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlashcard(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        // Check if flashcard belongs to user
        SavedFlashcard flashcard = savedFlashcardRepository.findById(id).orElse(null);
        if (flashcard == null || !flashcard.getUser().getId().equals(userId)) {
            return ResponseEntity.status(404).build();
        }
        
        savedFlashcardRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    public static class SaveFlashcardRequest {
        private String front;
        private String back;
        private String topic;
        
        // Getters and setters
        public String getFront() { return front; }
        public void setFront(String front) { this.front = front; }
        
        public String getBack() { return back; }
        public void setBack(String back) { this.back = back; }
        
        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
    }
} 