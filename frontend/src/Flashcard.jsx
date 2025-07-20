import { useState } from 'react'
import './Flashcard.css'

function Flashcard({ front, back }) {
  const [isFlipped, setIsFlipped] = useState(false)

  const handleCardClick = () => {
    setIsFlipped(!isFlipped)
  }

  return (
    <div className={`flashcard ${isFlipped ? 'flipped' : ''}`} onClick={handleCardClick}>
      <div className={`flashcard-inner ${isFlipped ? 'flipped' : ''}`}>
        <div className="flashcard-front">
          <h3>{front}</h3>
          <p className="flip-hint">Click to flip</p>
        </div>
        <div className="flashcard-back">
          <div className="flashcard-content">
            {back && back.split('\n\n').map((section, index) => (
              <div key={index} className="content-section">
                {section.startsWith('Context:') ? (
                  <div className="context-section">
                    <strong>Context:</strong>
                    <p>{section.replace('Context:', '').trim()}</p>
                  </div>
                ) : section.startsWith('Wikipedia:') ? (
                  <div className="wikipedia-section">
                    <strong>Wikipedia:</strong>
                    <p>{section.replace('Wikipedia:', '').trim()}</p>
                  </div>
                ) : (
                  <p>{section}</p>
                )}
              </div>
            ))}
          </div>
          <p className="flip-hint">Click to flip back</p>
        </div>
      </div>
    </div>
  )
}

export default Flashcard 