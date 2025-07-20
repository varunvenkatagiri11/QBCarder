package com.tossupflash.backend;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class FlashcardService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public FlashcardService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Complete flashcard generation pipeline: fetches tossups, extracts phrases, gets Wikipedia summaries,
     * and returns List<Flashcard> with front = phrase, back = context + summary
     * 
     * @param topic The topic to generate flashcards for (e.g., "Cold War")
     * @return List of flashcards with educational content
     */
    public List<Flashcard> generateFlashcards(String topic) {
        System.out.println("=== STARTING FLASHCARD GENERATION FOR TOPIC: " + topic + " ===");
        
        try {
            // STEP 1: Fetch tossup questions from QBReader API
            System.out.println("STEP 1: Fetching tossup questions from QBReader API...");
            List<JsonNode> tossups = fetchTossups(topic);
            
            if (tossups.isEmpty()) {
                System.out.println("❌ No tossup questions found for topic: " + topic);
                return new ArrayList<>();
            }
            
            // STEP 2: Extract all question strings for analysis
            System.out.println("STEP 2: Extracting question strings from " + tossups.size() + " tossups...");
            List<String> questionStrings = new ArrayList<>();
            
            for (JsonNode tossup : tossups) {
                String question = tossup.get("question").asText();
                questionStrings.add(question);
            }
            
            // STEP 3: Extract top multi-word phrases using frequency analysis
            System.out.println("STEP 3: Analyzing phrases using TF-IDF and frequency analysis...");
            List<String> topPhrases = extractTopPhrasesFromTossups(questionStrings);
            
            if (topPhrases.isEmpty()) {
                System.out.println("❌ No meaningful phrases extracted from tossup questions");
                return new ArrayList<>();
            }
            
            System.out.println("✓ Top phrases extracted (" + topPhrases.size() + "): " + topPhrases);
            
            // STEP 4: Get Wikipedia summaries for all phrases (batch processing)
            System.out.println("STEP 4: Fetching Wikipedia summaries for key phrases...");
            Map<String, String> wikipediaSummaries = getWikipediaSummariesBatch(topPhrases);
            
            // STEP 5: Create flashcards combining context + Wikipedia summaries
            System.out.println("STEP 5: Creating flashcards with context + Wikipedia summaries...");
            List<Flashcard> flashcards = new ArrayList<>();
            
            for (String phrase : topPhrases) {
                String wikipediaSummary = wikipediaSummaries.get(phrase);
                
                if (wikipediaSummary != null && !wikipediaSummary.trim().isEmpty()) {
                    // Find the best context sentence for this phrase from all questions
                    String contextSentence = findBestContextForPhrase(phrase, questionStrings);
                    
                    // Combine context + Wikipedia summary for the back of the flashcard
                    String back = "Context: " + contextSentence + "\n\nWikipedia: " + wikipediaSummary;
                    
                    // Create flashcard with phrase as front, context + summary as back
                    Flashcard flashcard = new Flashcard(phrase, back);
                    flashcards.add(flashcard);
                    
                    System.out.println("✓ Created flashcard #" + flashcards.size() + ": " + phrase);
                } else {
                    System.out.println("⚠️  Skipping phrase (no Wikipedia summary): " + phrase);
                }
                
                // Limit to prevent too many cards
                if (flashcards.size() >= 10) {
                    System.out.println("📝 Reached maximum of 10 flashcards");
                    break;
                }
            }
            
            System.out.println("=== FLASHCARD GENERATION COMPLETE ===");
            System.out.println("📊 Final Results:");
            System.out.println("   • Topic: " + topic);
            System.out.println("   • Tossups processed: " + tossups.size());
            System.out.println("   • Phrases analyzed: " + topPhrases.size());
            System.out.println("   • Wikipedia lookups: " + wikipediaSummaries.size());
            System.out.println("   • Flashcards created: " + flashcards.size());
            
            return flashcards;
            
        } catch (Exception e) {
            System.out.println("❌ ERROR in flashcard generation: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    private List<JsonNode> fetchTossups(String topic) throws Exception {
        // URL encode the topic (e.g., "Berlin Wall" becomes "Berlin%20Wall")
        String encodedTopic = URLEncoder.encode(topic, StandardCharsets.UTF_8);
        
        // Use the proper QB Reader search API with documented parameters
        String url = "https://www.qbreader.org/api/query?" +
                "queryString=" + encodedTopic +
                "&questionType=tossup" +
                "&searchType=answer" +  // Search ONLY answers - topic should be the answer!
                "&randomize=true" +     // Randomize results for variety
                "&maxReturnLength=8";   // Get more results for better variety
        
        System.out.println("Searching QB Reader API for topic: " + topic);
        System.out.println("API URL: " + url);
        
        String response = restTemplate.getForObject(url, String.class);
        
        System.out.println("QB Reader API Response received");
        
        JsonNode jsonNode = objectMapper.readTree(response);
        JsonNode tossupsObject = jsonNode.get("tossups");
        JsonNode tossupsArray = tossupsObject != null ? tossupsObject.get("questionArray") : null;
        
        List<JsonNode> tossups = new ArrayList<>();
        if (tossupsArray != null && tossupsArray.isArray()) {
            // Extract up to 8 tossup questions
            int count = 0;
            for (JsonNode tossup : tossupsArray) {
                if (count >= 8) break;
                tossups.add(tossup);
                
                // Log the question string for debugging
                String questionText = tossup.get("question").asText();
                System.out.println("Tossup " + (count + 1) + ": " + 
                    questionText.substring(0, Math.min(100, questionText.length())) + "...");
                count++;
            }
        }
        
        // If no results found with exact search, try a broader search
        if (tossups.isEmpty()) {
            System.out.println("No exact matches found, trying broader search...");
            
            // Try searching with individual words for complex topics
            String[] words = topic.split("\\s+");
            for (String word : words) {
                if (word.length() > 3) { // Skip short words
                    String broadUrl = "https://www.qbreader.org/api/query?" +
                            "queryString=" + URLEncoder.encode(word, StandardCharsets.UTF_8) +
                            "&questionType=tossup" +
                            "&searchType=all" +
                            "&randomize=true" +
                            "&maxReturnLength=3";
                    
                    try {
                        String broadResponse = restTemplate.getForObject(broadUrl, String.class);
                        JsonNode broadJson = objectMapper.readTree(broadResponse);
                        JsonNode broadTossups = broadJson.get("tossups");
                        JsonNode broadArray = broadTossups != null ? broadTossups.get("questionArray") : null;
                        
                        if (broadArray != null && broadArray.isArray()) {
                            for (JsonNode tossup : broadArray) {
                                if (tossups.size() >= 5) break;
                                tossups.add(tossup);
                            }
                        }
                        if (tossups.size() >= 5) break;
                    } catch (Exception e) {
                        System.out.println("Error in broad search for word: " + word);
                    }
                }
            }
        }
        
        System.out.println("Extracted " + tossups.size() + " tossup questions");
        return tossups;
    }
    

    
    /**
     * Extracts top 5-10 multi-word phrases from a list of tossup questions using frequency analysis
     * @param tossupQuestions List of tossup question strings
     * @return List of top ranked multi-word phrases
     */
    public List<String> extractTopPhrasesFromTossups(List<String> tossupQuestions) {
        Map<String, Integer> phraseFrequency = new HashMap<>();
        Set<String> stopWords = createStopWordSet();
        
        // Extract all multi-word phrases from all questions
        for (String question : tossupQuestions) {
            List<String> phrases = extractMultiWordPhrases(question);
            for (String phrase : phrases) {
                // Filter out phrases with stop words
                if (!containsStopWords(phrase, stopWords)) {
                    phraseFrequency.put(phrase, phraseFrequency.getOrDefault(phrase, 0) + 1);
                }
            }
        }
        
        // Sort phrases by frequency and importance score
        return phraseFrequency.entrySet().stream()
                .filter(entry -> entry.getValue() >= 1) // At least appears once
                .filter(entry -> entry.getKey().length() > 3) // Minimum length
                .sorted((a, b) -> {
                    // Custom scoring: frequency + length bonus + capitalization bonus
                    int scoreA = calculatePhraseScore(a.getKey(), a.getValue());
                    int scoreB = calculatePhraseScore(b.getKey(), b.getValue());
                    return Integer.compare(scoreB, scoreA);
                })
                .limit(10) // Top 10 phrases
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    private List<String> extractMultiWordPhrases(String text) {
        // Remove HTML tags and clean text, remove quiz bowl formatting
        String cleanText = text.replaceAll("<[^>]*>", "")
                              .replaceAll("[\\[\\]()]", "")
                              .replaceAll("\\*\\)", "") // Remove (*) markers
                              .replaceAll("For ten points", "") // Remove quiz bowl phrases
                              .replaceAll("ten points", "")
                              .replaceAll("\\bpoints\\b", "");
        
        String[] words = cleanText.split("\\s+");
        List<String> phrases = new ArrayList<>();
        
        // Extract 2-word and 3-word phrases, focusing on proper nouns and scientific terms
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = cleanWord(words[i]);
            String word2 = cleanWord(words[i + 1]);
            
            if (isValidEducationalWord(word1) && isValidEducationalWord(word2)) {
                String twoWordPhrase = word1 + " " + word2;
                phrases.add(twoWordPhrase);
                
                // Try 3-word phrases
                if (i < words.length - 2) {
                    String word3 = cleanWord(words[i + 2]);
                    if (isValidEducationalWord(word3)) {
                        String threeWordPhrase = word1 + " " + word2 + " " + word3;
                        phrases.add(threeWordPhrase);
                    }
                }
            }
        }
        
        return phrases;
    }
    
    private boolean isValidEducationalWord(String word) {
        if (word.length() < 3) return false;
        if (word.matches("\\d+")) return false;
        
        // Filter out quiz bowl specific terms
        String lowerWord = word.toLowerCase();
        Set<String> quizBowlTerms = Set.of("points", "ten", "answer", "question", "tossup", 
                                          "accept", "prompt", "reject", "what", "this", "these", 
                                          "that", "which", "name", "identify");
        
        if (quizBowlTerms.contains(lowerWord)) return false;
        
        // Prefer words that start with capital letters (proper nouns) or scientific terms
        return Character.isUpperCase(word.charAt(0)) || 
               lowerWord.matches(".*(ology|ism|tion|sion|ence|ance|ic|al|ous|ine|ide|ate)$");
    }
    
    private String cleanWord(String word) {
        return word.replaceAll("[^a-zA-Z]", "");
    }
    

    
    private Set<String> createStopWordSet() {
        return new HashSet<>(Arrays.asList(
            "the", "and", "for", "are", "but", "not", "you", "all", "can", "had", "her", "was", "one", "our", "out", "day", "get", "has", "him", "his", "how", "its", "may", "new", "now", "old", "see", "two", "way", "who", "boy", "did", "man", "men", "put", "say", "she", "too", "use"
        ));
    }
    
    private boolean containsStopWords(String phrase, Set<String> stopWords) {
        String[] words = phrase.toLowerCase().split("\\s+");
        for (String word : words) {
            if (stopWords.contains(word)) {
                return true;
            }
        }
        return false;
    }
    
    private int calculatePhraseScore(String phrase, int frequency) {
        int score = frequency * 10; // Base frequency score
        
        // Bonus for capitalized words (likely proper nouns)
        String[] words = phrase.split("\\s+");
        for (String word : words) {
            if (Character.isUpperCase(word.charAt(0))) {
                score += 5;
            }
        }
        
        // Bonus for longer phrases
        if (words.length == 3) {
            score += 3;
        }
        
        // Bonus for certain patterns that indicate importance
        String lowerPhrase = phrase.toLowerCase();
        if (lowerPhrase.contains("war") || lowerPhrase.contains("battle") || 
            lowerPhrase.contains("treaty") || lowerPhrase.contains("revolution")) {
            score += 8;
        }
        
        return score;
    }
    
    private String findBestContextForPhrase(String phrase, List<String> questions) {
        String bestContext = "";
        int bestScore = 0;
        
        for (String question : questions) {
            String context = extractContextSentence(question, phrase);
            if (context != null && !context.isEmpty()) {
                // Score context based on relevance and completeness
                int score = context.length() + (context.toLowerCase().contains(phrase.toLowerCase()) ? 50 : 0);
                if (score > bestScore) {
                    bestScore = score;
                    bestContext = context;
                }
            }
        }
        
        return bestContext.isEmpty() ? "Context not found." : bestContext;
    }
    
    private List<String> extractKeyPhrases(String text) {
        // Remove HTML tags and clean text
        String cleanText = text.replaceAll("<[^>]*>", "");
        
        // Split into sentences and extract proper nouns and multi-word phrases
        String[] words = cleanText.split("\\s+");
        List<String> phrases = new ArrayList<>();
        
        // Look for capitalized multi-word phrases (proper nouns)
        Pattern capitalizedPattern = Pattern.compile("^[A-Z][a-z]+");
        
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i].replaceAll("[^a-zA-Z]", "");
            String word2 = words[i + 1].replaceAll("[^a-zA-Z]", "");
            
            if (capitalizedPattern.matcher(word1).matches() && 
                capitalizedPattern.matcher(word2).matches() &&
                word1.length() > 2 && word2.length() > 2) {
                
                String phrase = word1 + " " + word2;
                
                // Check for 3-word phrases too
                if (i < words.length - 2) {
                    String word3 = words[i + 2].replaceAll("[^a-zA-Z]", "");
                    if (capitalizedPattern.matcher(word3).matches() && word3.length() > 2) {
                        phrase = word1 + " " + word2 + " " + word3;
                    }
                }
                
                if (!phrases.contains(phrase)) {
                    phrases.add(phrase);
                }
            }
        }
        
        // Return top phrases (limit to prevent too many API calls)
        return phrases.stream().limit(3).collect(Collectors.toList());
    }
    
    /**
     * Calls Wikipedia REST API and returns the first two sentences of the summary for a key phrase
     * @param phrase The key phrase to look up on Wikipedia
     * @return First two sentences of the Wikipedia summary, or null if not found
     */
    private String getWikipediaSummary(String phrase) {
        try {
            String encodedPhrase = phrase.replace(" ", "_");
            String url = "https://en.wikipedia.org/api/rest_v1/page/summary/" + encodedPhrase;
            
            System.out.println("Calling Wikipedia API for: " + phrase + " -> " + url);
            
            String response = restTemplate.getForObject(url, String.class);
            
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode extractNode = jsonNode.get("extract");
            
            if (extractNode != null) {
                String fullSummary = extractNode.asText();
                String firstTwoSentences = extractFirstTwoSentences(fullSummary);
                
                System.out.println("Wikipedia summary for '" + phrase + "': " + 
                    firstTwoSentences.substring(0, Math.min(100, firstTwoSentences.length())) + "...");
                
                return firstTwoSentences;
            }
            
        } catch (Exception e) {
            // If Wikipedia lookup fails, return null
            System.out.println("Failed to get Wikipedia summary for: " + phrase + " - " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Extracts the first two sentences from a text summary
     * @param summary The full Wikipedia summary text
     * @return First two sentences, or the full text if less than two sentences exist
     */
    private String extractFirstTwoSentences(String summary) {
        if (summary == null || summary.trim().isEmpty()) {
            return "";
        }
        
        // Split by sentence-ending punctuation, but be careful with abbreviations
        String[] sentences = summary.split("(?<=[.!?])\\s+");
        
        StringBuilder result = new StringBuilder();
        int sentenceCount = 0;
        
        for (String sentence : sentences) {
            if (sentenceCount >= 2) {
                break;
            }
            
            // Skip very short fragments (likely abbreviations)
            if (sentence.trim().length() > 10) {
                if (result.length() > 0) {
                    result.append(" ");
                }
                result.append(sentence.trim());
                sentenceCount++;
            }
        }
        
        // If we couldn't get two good sentences, return the original (up to 300 chars)
        if (sentenceCount == 0) {
            return summary.length() > 300 ? summary.substring(0, 300) + "..." : summary;
        }
        
        return result.toString();
    }
    
    /**
     * Batch method to get Wikipedia summaries for multiple key phrases
     * @param phrases List of key phrases to look up
     * @return Map of phrase -> first two sentences of Wikipedia summary
     */
    public Map<String, String> getWikipediaSummariesBatch(List<String> phrases) {
        Map<String, String> summaries = new HashMap<>();
        
        System.out.println("Getting Wikipedia summaries for " + phrases.size() + " phrases...");
        
        for (String phrase : phrases) {
            String summary = getWikipediaSummary(phrase);
            if (summary != null && !summary.trim().isEmpty()) {
                summaries.put(phrase, summary);
                System.out.println("✓ Successfully retrieved summary for: " + phrase);
            } else {
                System.out.println("✗ No summary found for: " + phrase);
            }
            
            // Small delay to be respectful to Wikipedia API
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("Batch completed: " + summaries.size() + "/" + phrases.size() + " summaries retrieved");
        return summaries;
    }
    
    private String extractContextSentence(String text, String phrase) {
        // Remove HTML tags first
        String cleanText = text.replaceAll("<[^>]*>", "");
        
        // Find the sentence containing the phrase
        String[] sentences = cleanText.split("\\.");
        
        for (String sentence : sentences) {
            if (sentence.toLowerCase().contains(phrase.toLowerCase())) {
                return sentence.trim() + ".";
            }
        }
        
        // If not found, return first sentence
        return sentences.length > 0 ? sentences[0].trim() + "." : cleanText;
    }
}
