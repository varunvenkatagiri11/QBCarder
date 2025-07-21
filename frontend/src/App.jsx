import { useState, useEffect } from 'react'
import axios from 'axios'
import Flashcard from './Flashcard'
import './App.css'

function App() {
  // Authentication state
  const [user, setUser] = useState(null)
  const [showAuthModal, setShowAuthModal] = useState(false)
  const [authMode, setAuthMode] = useState('login') // 'login' or 'register'
  
  // App state
  const [activeTab, setActiveTab] = useState('generate')
  const [topic, setTopic] = useState('')
  const [category, setCategory] = useState('')
  const [flashcards, setFlashcards] = useState([])
  const [savedFlashcards, setSavedFlashcards] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  // Check for existing auth token on app load
  useEffect(() => {
    const token = localStorage.getItem('authToken')
    const username = localStorage.getItem('username')
    if (token && username) {
      setUser({ token, username })
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    }
  }, [])

  // Load saved flashcards when user logs in or tab switches to saved
  useEffect(() => {
    if (user && activeTab === 'saved') {
      loadSavedFlashcards()
    }
  }, [user, activeTab])

  const loadSavedFlashcards = async () => {
    try {
      const response = await axios.get('/saved-flashcards')
      setSavedFlashcards(response.data)
    } catch (err) {
      console.error('Error loading saved flashcards:', err)
    }
  }

  // Authentication functions
  const handleAuth = async (authData) => {
    try {
      const endpoint = authMode === 'login' ? '/auth/login' : '/auth/register'
      const response = await axios.post(endpoint, authData)
      
      if (response.data.token) {
        const { token, username } = response.data
        setUser({ token, username })
        localStorage.setItem('authToken', token)
        localStorage.setItem('username', username)
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
        setShowAuthModal(false)
        setError('')
      }
    } catch (err) {
      console.error('Auth error:', err)
      if (err.response?.data?.error) {
        setError(err.response.data.error)
      } else {
        setError('Authentication failed. Please try again.')
      }
    }
  }

  const handleLogout = () => {
    setUser(null)
    localStorage.removeItem('authToken')
    localStorage.removeItem('username')
    delete axios.defaults.headers.common['Authorization']
    setSavedFlashcards([])
    setActiveTab('generate')
  }

  // Generate flashcards function (unchanged)
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

  // Save flashcard function
  const saveFlashcard = async (flashcard) => {
    if (!user) {
      setShowAuthModal(true)
      return
    }

    try {
      await axios.post('/saved-flashcards', {
        front: flashcard.front,
        back: flashcard.back,
        topic: topic
      })
      
      // Show success (you could add a toast notification here)
      console.log('Flashcard saved successfully')
      
      // Reload saved flashcards if we're on that tab
      if (activeTab === 'saved') {
        loadSavedFlashcards()
      }
    } catch (err) {
      console.error('Error saving flashcard:', err)
      setError('Failed to save flashcard')
    }
  }

  // Delete saved flashcard
  const deleteSavedFlashcard = async (id) => {
    try {
      await axios.delete(`/saved-flashcards/${id}`)
      setSavedFlashcards(savedFlashcards.filter(card => card.id !== id))
    } catch (err) {
      console.error('Error deleting flashcard:', err)
      setError('Failed to delete flashcard')
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
        <div className="header-content">
          <div className="header-left">
            <h1>QBCarder</h1>
            <p>Generate educational flashcards from Quiz Bowl questions and Wikipedia</p>
          </div>
          <div className="header-right">
            {user ? (
              <div className="user-menu">
                <span className="username">Hello, {user.username}!</span>
                <button onClick={handleLogout} className="logout-button">Logout</button>
              </div>
            ) : (
              <button 
                onClick={() => setShowAuthModal(true)} 
                className="login-button"
              >
                Login / Register
              </button>
            )}
          </div>
        </div>
        
        {/* Tab Navigation */}
        <div className="tab-navigation">
          <button 
            className={`tab ${activeTab === 'generate' ? 'active' : ''}`}
            onClick={() => setActiveTab('generate')}
          >
            Generate Flashcards
          </button>
          <button 
            className={`tab ${activeTab === 'saved' ? 'active' : ''}`}
            onClick={() => {
              if (!user) {
                setShowAuthModal(true)
                return
              }
              setActiveTab('saved')
            }}
          >
            Saved Cards ({user ? savedFlashcards.length : '0'})
          </button>
        </div>
      </header>

      <main className="app-main">
        {/* Generate Tab */}
        {activeTab === 'generate' && (
          <>
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

            {/* Generated Flashcards Display */}
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
                        onSave={() => saveFlashcard(flashcard)}
                        showSaveButton={true}
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
                </div>
              </div>
            )}
          </>
        )}

        {/* Saved Cards Tab */}
        {activeTab === 'saved' && (
          <div className="saved-cards-section">
            {savedFlashcards.length > 0 ? (
              <>
                <div className="flashcards-header">
                  <h2>💾 Your Saved Flashcards ({savedFlashcards.length})</h2>
                  <p className="flashcards-subtitle">Your personal collection of saved flashcards</p>
                </div>
                <div className="flashcards-container">
                  <div className="flashcards-grid">
                    {savedFlashcards.map((flashcard) => (
                      <Flashcard 
                        key={flashcard.id} 
                        front={flashcard.front} 
                        back={flashcard.back}
                        topic={flashcard.topic}
                        savedAt={flashcard.savedAt}
                        onDelete={() => deleteSavedFlashcard(flashcard.id)}
                        showDeleteButton={true}
                      />
                    ))}
                  </div>
                </div>
              </>
            ) : (
              <div className="placeholder-section">
                <div className="placeholder-content">
                  <div className="placeholder-icon">💾</div>
                  <h2>No Saved Flashcards Yet</h2>
                  <p className="placeholder-description">
                    Save flashcards from the Generate tab to build your personal collection.
                  </p>
                  <button 
                    onClick={() => setActiveTab('generate')}
                    className="generate-button"
                  >
                    Generate Some Flashcards
                  </button>
                </div>
              </div>
            )}
          </div>
        )}
      </main>

      {/* Authentication Modal */}
      {showAuthModal && (
        <AuthModal 
          mode={authMode}
          onClose={() => {
            setShowAuthModal(false)
            setError('')
          }}
          onAuth={handleAuth}
          onModeSwitch={() => setAuthMode(authMode === 'login' ? 'register' : 'login')}
          error={error}
        />
      )}
      
      <footer className="app-footer">
        <p>Created by Varun Venkatagiri</p>
      </footer>
    </div>
  )
}

// Authentication Modal Component
function AuthModal({ mode, onClose, onAuth, onModeSwitch, error }) {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: ''
  })

  const handleSubmit = (e) => {
    e.preventDefault()
    onAuth(formData)
  }

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    })
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>{mode === 'login' ? 'Login' : 'Register'}</h2>
          <button onClick={onClose} className="close-button">×</button>
        </div>
        
        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label>Username</label>
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              required
            />
          </div>
          
          {mode === 'register' && (
            <div className="form-group">
              <label>Email</label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </div>
          )}
          
          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
            />
          </div>
          
          {error && (
            <div className="error-message">
              ⚠️ {error}
            </div>
          )}
          
          <button type="submit" className="auth-submit-button">
            {mode === 'login' ? 'Login' : 'Register'}
          </button>
        </form>
        
        <div className="auth-switch">
          <p>
            {mode === 'login' ? "Don't have an account? " : "Already have an account? "}
            <button onClick={onModeSwitch} className="switch-mode-button">
              {mode === 'login' ? 'Register' : 'Login'}
            </button>
          </p>
        </div>
      </div>
    </div>
  )
}

export default App
