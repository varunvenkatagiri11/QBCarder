# 📚 TossupCreator (TossupFlash)

**Generate educational flashcards from Quiz Bowl questions and Wikipedia content**

TossupCreator is a full-stack web application that automatically creates educational flashcards by analyzing Quiz Bowl tossup questions and enriching them with Wikipedia summaries. Perfect for students, educators, and Quiz Bowl enthusiasts who want to study key concepts in an interactive way.

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![React](https://img.shields.io/badge/React-19.1.0-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-green.svg)

## 🌟 Features

### 🎯 Core Functionality
- **Intelligent Topic Processing**: Enter any educational topic and get relevant flashcards
- **Quiz Bowl Integration**: Leverages the QBReader API to fetch high-quality academic questions
- **Wikipedia Enhancement**: Automatically enriches flashcards with Wikipedia summaries
- **Category Filtering**: Filter by academic categories (Literature, History, Science, Fine Arts, etc.)
- **Interactive UI**: Flip-able flashcards with smooth animations

### 🧠 Smart Content Generation
- **Advanced Phrase Extraction**: Uses TF-IDF and frequency analysis to identify key concepts
- **Context Preservation**: Includes original Quiz Bowl question context for better understanding
- **Multi-source Learning**: Combines Quiz Bowl expertise with Wikipedia knowledge
- **Quality Filtering**: Automatically filters out low-quality phrases and content

### 💻 Technical Features
- **Full-Stack Architecture**: React frontend with Spring Boot backend
- **RESTful API**: Clean API design with proper error handling
- **Responsive Design**: Works on desktop, tablet, and mobile devices
- **Real-time Generation**: Fast flashcard creation with progress indicators
- **CORS Support**: Properly configured for cross-origin requests

## 🚀 Quick Start

### Prerequisites
- **Java 17** or higher
- **Node.js 16** or higher
- **Maven 3.6** or higher
- **Git**

### 🔧 Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/TossupCreator.git
   cd TossupCreator
   ```

2. **Start the Backend (Spring Boot)**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
   
   The backend will start on `http://localhost:8080`

3. **Start the Frontend (React + Vite)**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   
   The frontend will start on `http://localhost:5173`

4. **Open your browser**
   
   Navigate to `http://localhost:5173` and start generating flashcards!

## 📖 How to Use

### Basic Usage
1. **Enter a Topic**: Type any educational topic (e.g., "Cold War", "DNA", "Renaissance")
2. **Select Category** (Optional): Choose from Literature, History, Science, Fine Arts, etc.
3. **Generate**: Click "Generate Flashcards" and wait for the magic to happen
4. **Study**: Click any flashcard to flip it and see the back with context and Wikipedia info

### Example Topics That Work Well
- **History**: "World War II", "American Revolution", "Cold War", "Ancient Rome"
- **Science**: "DNA", "Photosynthesis", "Quantum Mechanics", "Evolution"
- **Literature**: "Shakespeare", "Homer", "Jane Austen", "Mark Twain"
- **Fine Arts**: "Renaissance", "Impressionism", "Baroque", "Pablo Picasso"

### Tips for Best Results
- Use specific but not overly narrow topics
- Historical events, scientific concepts, and literary works tend to work best
- The system works better with topics that appear frequently in Quiz Bowl questions

## 🏗️ Architecture

### System Overview
```
┌─────────────────┐    HTTP/REST    ┌─────────────────┐
│   React Frontend │◄───────────────►│ Spring Boot API │
│   (Port 5173)   │                 │   (Port 8080)   │
└─────────────────┘                 └─────────────────┘
                                            │
                                            ▼
                                    ┌─────────────────┐
                                    │  External APIs  │
                                    │                 │
                                    │ • QBReader API  │
                                    │ • Wikipedia API │
                                    └─────────────────┘
```

### Frontend Architecture
- **React 19**: Modern functional components with hooks
- **Vite**: Fast build tool with hot module replacement
- **Axios**: HTTP client for API communication
- **CSS3**: Custom styling with animations and responsive design
- **Component Structure**:
  - `App.jsx`: Main application component with state management
  - `Flashcard.jsx`: Individual flashcard component with flip animation

### Backend Architecture
- **Spring Boot 3.5.3**: RESTful web service framework
- **Java 17**: Modern Java features and performance
- **Maven**: Dependency management and build automation
- **Service Layer Architecture**:
  - `FlashcardController`: REST endpoints
  - `FlashcardService`: Core business logic
  - `Flashcard`: Data model

### Data Flow
1. **User Input**: Topic entered in React frontend
2. **API Call**: Frontend sends GET request to `/flashcards?topic={topic}`
3. **Question Fetching**: Backend calls QBReader API to get Quiz Bowl questions
4. **Phrase Extraction**: Advanced NLP techniques extract key phrases
5. **Wikipedia Lookup**: Batch API calls to Wikipedia for summaries
6. **Flashcard Creation**: Combine context + Wikipedia content
7. **Response**: JSON array of flashcards returned to frontend
8. **Rendering**: React displays interactive flashcards

## 🔌 API Reference

### Endpoints

#### `GET /flashcards`
Generate flashcards for a given topic.

**Parameters:**
- `topic` (required): The educational topic to generate flashcards for
- `categories` (optional): Filter by Quiz Bowl category

**Example Request:**
```bash
curl "http://localhost:8080/flashcards?topic=Cold%20War&categories=History"
```

**Example Response:**
```json
[
  {
    "front": "Berlin Wall",
    "back": "Context: This barrier was erected in 1961 to prevent East Germans from fleeing to West Berlin.\n\nWikipedia: The Berlin Wall was a guarded concrete barrier that physically and ideologically divided Berlin from 1961 to 1989. It was built by the German Democratic Republic to prevent its population from escaping Soviet-controlled East Berlin."
  },
  {
    "front": "Cuban Missile Crisis",
    "back": "Context: This 1962 confrontation brought the world closer to nuclear war than ever before.\n\nWikipedia: The Cuban Missile Crisis was a 13-day confrontation between the United States and the Soviet Union initiated by the American discovery of missile deployments in Cuba."
  }
]
```

## 🛠️ Development

### Project Structure
```
TossupCreator/
├── backend/                    # Spring Boot backend
│   ├── src/main/java/
│   │   └── com/tossupflash/backend/
│   │       ├── BackendApplication.java
│   │       ├── Flashcard.java
│   │       ├── FlashcardController.java
│   │       └── FlashcardService.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
├── frontend/                   # React frontend
│   ├── src/
│   │   ├── App.jsx
│   │   ├── App.css
│   │   ├── Flashcard.jsx
│   │   ├── Flashcard.css
│   │   └── main.jsx
│   ├── package.json
│   └── vite.config.js
└── README.md
```

### Development Commands

**Backend (Spring Boot)**
```bash
# Run in development mode
./mvnw spring-boot:run

# Run tests
./mvnw test

# Build JAR
./mvnw clean package
```

**Frontend (React + Vite)**
```bash
# Install dependencies
npm install

# Development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Lint code
npm run lint
```

### Key Dependencies

**Backend (Maven)**
- `spring-boot-starter-web`: REST API framework
- `spring-boot-starter-webflux`: Reactive web client
- `spring-boot-devtools`: Development tools
- `jackson-databind`: JSON processing

**Frontend (npm)**
- `react`: UI library
- `axios`: HTTP client
- `vite`: Build tool
- `eslint`: Code linting

## 🎨 Customization

### Adding New Categories
Edit the category dropdown in `frontend/src/App.jsx`:
```jsx
<option value="Your Category">Your Category</option>
```

### Modifying Flashcard Limits
Change the limit in `FlashcardService.java`:
```java
if (flashcards.size() >= 15) { // Increase from 10 to 15
    break;
}
```

### Styling Customization
- **Flashcard appearance**: Edit `frontend/src/Flashcard.css`
- **Main app styling**: Edit `frontend/src/App.css`
- **Global styles**: Edit `frontend/src/index.css`

### Backend Configuration
Modify `backend/src/main/resources/application.properties`:
```properties
server.port=8080
spring.application.name=backend
```

## 🌐 Deployment

### Production Build

**Backend**
```bash
cd backend
./mvnw clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

**Frontend**
```bash
cd frontend
npm run build
# Deploy the 'dist' folder to your web server
```

### Environment Considerations
- Update API URLs in production
- Configure CORS for your production domain
- Set up proper logging and monitoring
- Consider rate limiting for external API calls

## 🤝 Contributing

### How to Contribute
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow existing code style and conventions
- Add comments for complex logic
- Test your changes thoroughly
- Update documentation if needed

## 📚 Educational Use Cases

### For Students
- **Study Aid**: Create flashcards for any subject you're learning
- **Quiz Bowl Prep**: Practice with real Quiz Bowl content
- **Concept Review**: Get Wikipedia summaries for deeper understanding

### For Educators
- **Classroom Tool**: Generate flashcards for lesson topics
- **Assessment Prep**: Help students review key concepts
- **Curriculum Support**: Supplement existing materials

### For Quiz Bowl Teams
- **Training Tool**: Practice with actual tournament questions
- **Topic Exploration**: Discover related concepts and facts
- **Strategy Development**: Understand question patterns and clues

## 🔧 Troubleshooting

### Common Issues

**Backend won't start**
- Ensure Java 17 is installed: `java -version`
- Check port 8080 isn't in use: `lsof -i :8080`
- Verify Maven installation: `mvn -version`

**Frontend won't start**
- Ensure Node.js is installed: `node -version`
- Clear npm cache: `npm cache clean --force`
- Delete node_modules and reinstall: `rm -rf node_modules && npm install`

**No flashcards generated**
- Check if the topic is too specific or obscure
- Verify internet connection for API calls
- Check browser console for error messages
- Ensure backend is running and accessible

**API errors**
- Check CORS configuration in backend
- Verify proxy setup in `vite.config.js`
- Check network tab in browser dev tools

## 📋 Roadmap

### Planned Features
- [ ] **User Authentication**: Save and manage personal flashcard collections
- [ ] **Export Options**: PDF, Anki, and other formats
- [ ] **Difficulty Levels**: Beginner, intermediate, and advanced content
- [ ] **Spaced Repetition**: Smart review scheduling
- [ ] **Collaborative Features**: Share flashcard sets with others
- [ ] **Analytics**: Track study progress and performance
- [ ] **Mobile App**: Native iOS and Android applications
- [ ] **Offline Mode**: Study without internet connection

### Technical Improvements
- [ ] **Database Integration**: Persistent data storage
- [ ] **Caching**: Improve performance with Redis
- [ ] **API Rate Limiting**: Prevent abuse
- [ ] **Enhanced NLP**: Better phrase extraction algorithms
- [ ] **Search Functionality**: Find existing flashcards
- [ ] **Batch Processing**: Generate multiple topics at once

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Varun Venkatagiri**
- Passionate about educational technology and Quiz Bowl
- Built this tool to help students and educators learn more effectively

## 🙏 Acknowledgments

- **QBReader**: For providing the excellent Quiz Bowl question database
- **Wikipedia**: For the comprehensive educational content
- **Quiz Bowl Community**: For inspiration and feedback
- **Open Source Contributors**: For the amazing tools and libraries used

## 📞 Support

Having issues? Here's how to get help:

1. **Check the Documentation**: Read through this README thoroughly
2. **Search Issues**: Look through existing GitHub issues
3. **Create an Issue**: Describe your problem with reproduction steps
4. **Community**: Join Quiz Bowl and educational technology communities

---

**Made with ❤️ for the educational community**

*Happy studying! 📚✨*
