package com.tossupflash.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class FlashcardController {
    
    private final FlashcardService flashcardService;
    
    @Autowired
    public FlashcardController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }
    
    @GetMapping("/flashcards")
    public List<Flashcard> getFlashcards(@RequestParam String topic) {
        return flashcardService.generateFlashcards(topic);
    }
}
