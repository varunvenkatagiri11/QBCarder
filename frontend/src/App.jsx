import { useState } from 'react'
import axios from 'axios'
import Flashcard from './Flashcard'
import './App.css'

function App() {
  const [topic, setTopic] = useState('')
  const [category, setCategory] = useState('')
  const [flashcards, setFlashcards] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  // Function to send GET request to /flashcards?topic=<topic>
  const generateFlashcards = async () => {
    if (!topic.trim()) {
      setError('Please enter a topic')
      return
    }

    setLoading(true)
    setError('')
    setFlashcards([])

    try {
      console.log('Generating flashcards for topic:', topic)
      
      // Make GET request to backend API (via Vite proxy)
      const categoryParam = category ? `&categories=${encodeURIComponent(category)}` : ''
      const response = await axios.get(`/flashcards?topic=${encodeURIComponent(topic)}${categoryParam}`)
      
      console.log('API Response:', response.data)
      setFlashcards(response.data)
      
      if (response.data.length === 0) {
        setError('No flashcards could be generated for this topic. Try a different topic.')
      }
    } catch (err) {
      console.error('Error generating flashcards:', err)
      setError('Failed to generate flashcards. Make sure the backend server is running.')
    } finally {
      setLoading(false)
    }
  }

  // Handle Enter key press in input field
  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      generateFlashcards()
    }
  }

  return (
    <div className="app">
      <header className="app-header">
                      <h1>QBCarder</h1>
        <p>Generate educational flashcards from Quiz Bowl questions and Wikipedia</p>
      </header>

      <main className="app-main">
        {/* Topic Input Section */}
        <div className="input-section">
          <div className="input-group">
            <input
              type="text"
              value={topic}
              onChange={(e) => setTopic(e.target.value)}
              onKeyPress={handleKeyPress}
              placeholder="Enter a topic (e.g., Cold War, Renaissance, DNA)"
              className="topic-input"
              disabled={loading}
            />
            
            <select
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              className="category-dropdown"
              disabled={loading}
            >
              <option value="">All Categories</option>
              <option value="Literature" className="category-primary">Literature</option>
              <option value="History" className="category-primary">History</option>
              <option value="Science" className="category-primary">Science</option>
              <option value="Fine Arts" className="category-primary">Fine Arts</option>
              <option value="Religion">Religion</option>
              <option value="Mythology">Mythology</option>
              <option value="Philosophy">Philosophy</option>
              <option value="Social Science">Social Science</option>
              <option value="Current Events">Current Events</option>
              <option value="Geography">Geography</option>
              <option value="Other Academic">Other Academic</option>
              <option value="Pop Culture">Pop Culture</option>
            </select>
            
            <button 
              onClick={generateFlashcards}
              disabled={loading || !topic.trim()}
              className="generate-button"
            >
              {loading ? 'Generating...' : 'Generate Flashcards'}
            </button>
          </div>
          
          {error && (
            <div className="error-message">
              ⚠️ {error}
            </div>
          )}
        </div>

        {/* Loading State */}
        {loading && (
          <div className="loading-section">
            <div className="loading-spinner"></div>
            <p>Fetching tossup questions and Wikipedia summaries...</p>
          </div>
        )}

        {/* Flashcards Display */}
        {flashcards.length > 0 && (
          <div className="flashcards-section">
            <div className="flashcards-header">
              <h2>📚 Generated Flashcards ({flashcards.length})</h2>
              <p className="flashcards-subtitle">Click any card to flip and see the back</p>
            </div>
            <div className="flashcards-container">
              <div className="flashcards-grid">
                {flashcards.map((flashcard, index) => (
                  <Flashcard 
                    key={index} 
                    front={flashcard.front} 
                    back={flashcard.back} 
                  />
                ))}
              </div>
            </div>
          </div>
        )}

        {/* Placeholder Message */}
        {!loading && flashcards.length === 0 && !error && (
          <div className="placeholder-section">
            <div className="placeholder-content">
              <div className="placeholder-icon">📚</div>
              <h2>Ready to Create Flashcards?</h2>
              <p className="placeholder-description">
                Enter a topic above to generate educational flashcards from Quiz Bowl questions and Wikipedia summaries.
              </p>
              <div className="example-topics">
                <h3>Try these topics:</h3>
                <div className="topic-suggestions">
                  <span className="topic-tag">Cold War</span>
                  <span className="topic-tag">Renaissance</span>
                  <span className="topic-tag">DNA</span>
                  <span className="topic-tag">World War II</span>
                  <span className="topic-tag">Ancient Greece</span>
                </div>
              </div>
              <div className="how-it-works">
                <h3>How it works:</h3>
                <div className="steps-grid">
                  <div className="step">
                    <div className="step-number">1</div>
                    <p>Fetch Quiz Bowl questions</p>
                  </div>
                  <div className="step">
                    <div className="step-number">2</div>
                    <p>Extract key phrases</p>
                  </div>
                  <div className="step">
                    <div className="step-number">3</div>
                    <p>Get Wikipedia summaries</p>
                  </div>
                  <div className="step">
                    <div className="step-number">4</div>
                    <p>Create flashcards</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </main>
      
      <footer className="app-footer">
        <p>Created by Varun Venkatagiri</p>
      </footer>
    </div>
  )
}

export default App
