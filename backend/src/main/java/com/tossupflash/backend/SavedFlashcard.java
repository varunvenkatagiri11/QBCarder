package com.tossupflash.backend;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "saved_flashcards")
public class SavedFlashcard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 1000)
    private String front;
    
    @Column(nullable = false, length = 5000)
    private String back;
    
    @Column(nullable = false)
    private String topic;
    
    @Column(name = "saved_at", nullable = false)
    private LocalDateTime savedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    public SavedFlashcard() {}
    
    public SavedFlashcard(String front, String back, String topic, User user) {
        this.front = front;
        this.back = back;
        this.topic = topic;
        this.user = user;
        this.savedAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFront() {
        return front;
    }
    
    public void setFront(String front) {
        this.front = front;
    }
    
    public String getBack() {
        return back;
    }
    
    public void setBack(String back) {
        this.back = back;
    }
    
    public String getTopic() {
        return topic;
    }
    
    public void setTopic(String topic) {
        this.topic = topic;
    }
    
    public LocalDateTime getSavedAt() {
        return savedAt;
    }
    
    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
} 