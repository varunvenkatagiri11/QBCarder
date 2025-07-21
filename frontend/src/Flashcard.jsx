import { useState } from 'react'
import './Flashcard.css'

function Flashcard({ 
  front, 
  back, 
  topic, 
  savedAt, 
  onSave, 
  onDelete, 
  showSaveButton = false, 
  showDeleteButton = false 
}) {
  const [isFlipped, setIsFlipped] = useState(false)

  const handleFlip = () => {
    setIsFlipped(!isFlipped)
  }

  const handleSave = (e) => {
    e.stopPropagation() // Prevent flip when clicking save
    onSave()
  }

  const handleDelete = (e) => {
    e.stopPropagation() // Prevent flip when clicking delete
    if (window.confirm('Are you sure you want to delete this flashcard?')) {
      onDelete()
    }
  }

  const formatDate = (dateString) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    })
  }

  return (
    <div className={`flashcard ${isFlipped ? 'flipped' : ''}`} onClick={handleFlip}>
      <div className="flashcard-inner">
        {/* Front of the card */}
        <div className="flashcard-front">
          <div className="flashcard-content">
            <h3 className="flashcard-term">{front}</h3>
          </div>
          
          {/* Action buttons */}
          <div className="flashcard-actions">
            {showSaveButton && (
              <button 
                onClick={handleSave}
                className="save-button"
                title="Save this flashcard"
              >
                💾
              </button>
            )}
            {showDeleteButton && (
              <button 
                onClick={handleDelete}
                className="delete-button"
                title="Delete this flashcard"
              >
                🗑️
              </button>
            )}
          </div>
          
          {/* Metadata for saved cards */}
          {topic && (
            <div className="flashcard-metadata">
              <span className="topic-tag">{topic}</span>
              {savedAt && (
                <span className="saved-date">Saved {formatDate(savedAt)}</span>
              )}
            </div>
          )}
        </div>

        {/* Back of the card */}
        <div className="flashcard-back">
          <div className="flashcard-content">
            <div className="flashcard-answer">
              {back}
            </div>
          </div>
          
          {/* Action buttons (duplicate for back side) */}
          <div className="flashcard-actions">
            {showSaveButton && (
              <button 
                onClick={handleSave}
                className="save-button"
                title="Save this flashcard"
              >
                💾
              </button>
            )}
            {showDeleteButton && (
              <button 
                onClick={handleDelete}
                className="delete-button"
                title="Delete this flashcard"
              >
                🗑️
              </button>
            )}
          </div>
          
          {/* Metadata for saved cards (duplicate for back side) */}
          {topic && (
            <div className="flashcard-metadata">
              <span className="topic-tag">{topic}</span>
              {savedAt && (
                <span className="saved-date">Saved {formatDate(savedAt)}</span>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default Flashcard 