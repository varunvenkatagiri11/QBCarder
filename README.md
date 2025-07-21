# 📚 QBCarder

**Generate educational flashcards from Quiz Bowl questions and Wikipedia content**

QBCarder is a full-stack web application that automatically creates educational flashcards by analyzing Quiz Bowl tossup questions and enriching them with Wikipedia summaries. With user authentication and personal flashcard collections, it's perfect for students, educators, and Quiz Bowl enthusiasts who want to study key concepts in an interactive way.

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

### 🔐 Authentication & Personal Collections
- **User Registration & Login**: Secure JWT-based authentication system
- **Personal Flashcard Library**: Save your favorite flashcards to build a personal collection
- **Tab Navigation**: Switch between "Generate Flashcards" and "Saved Cards" views
- **Persistent Storage**: Your saved flashcards are stored in a database and persist across sessions
- **Collection Management**: Delete saved flashcards you no longer need

### 🧠 Smart Content Generation
- **Advanced Phrase Extraction**: Uses TF-IDF and frequency analysis to identify key concepts
- **Context Preservation**: Includes original Quiz Bowl question context for better understanding
- **Multi-source Learning**: Combines Quiz Bowl expertise with Wikipedia knowledge
- **Quality Filtering**: Automatically filters out low-quality phrases and content

### 💻 Technical Features
- **Full-Stack Architecture**: React frontend with Spring Boot backend
- **JWT Authentication**: Secure token-based authentication with Spring Security
- **Database Integration**: H2 in-memory database with JPA/Hibernate for development
- **RESTful API**: Clean API design with proper error handling and authentication
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
   git clone https://github.com/yourusername/QBCarder.git
   cd QBCarder
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

### Getting Started
1. **Register/Login**: Click "Login / Register" in the top right to create an account or sign in
2. **Enter a Topic**: Type any educational topic (e.g., "Cold War", "DNA", "Renaissance")
3. **Select Category** (Optional): Choose from Literature, History, Science, Fine Arts, etc.
4. **Generate**: Click "Generate Flashcards" and wait for the magic to happen
5. **Study**: Click any flashcard to flip it and see the back with context and Wikipedia info
6. **Save Favorites**: Click the 💾 button on any flashcard to save it to your personal collection

### Managing Your Collection
- **View Saved Cards**: Click the "Saved Cards" tab to see your personal flashcard library
- **Organized by Date**: Your saved flashcards are organized by when you saved them
- **Delete Cards**: Click the 🗑️ button to remove flashcards you no longer need
- **Topic Labels**: Each saved flashcard shows which topic it came from

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

#### `POST /auth/register`
Register a new user account.

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securepassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john_doe"
}
```

#### `POST /auth/login`
Login with existing credentials.

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "securepassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john_doe"
}
```

#### `POST /saved-flashcards`
Save a flashcard to user's personal collection. **Requires authentication.**

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Request Body:**
```json
{
  "front": "Berlin Wall",
  "back": "Context and Wikipedia summary...",
  "topic": "Cold War"
}
```

#### `GET /saved-flashcards`
Get user's saved flashcards. **Requires authentication.**

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response:**
```json
[
  {
    "id": 1,
    "front": "Berlin Wall",
    "back": "Context and Wikipedia summary...",
    "topic": "Cold War",
    "savedAt": "2025-01-21T04:15:30"
  }
]
```

#### `DELETE /saved-flashcards/{id}`
Delete a saved flashcard. **Requires authentication.**

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

## 🛠️ Development

### Project Structure
```
QBCarder/
├── backend/                    # Spring Boot backend
│   ├── src/main/java/
│   │   └── com/tossupflash/backend/
│   │       ├── BackendApplication.java
│   │       ├── Flashcard.java
│   │       ├── FlashcardController.java
│   │       ├── FlashcardService.java
│   │       ├── User.java                    # User entity
│   │       ├── SavedFlashcard.java          # Saved flashcard entity
│   │       ├── UserRepository.java          # User data access
│   │       ├── SavedFlashcardRepository.java # Saved flashcard data access
│   │       ├── AuthController.java          # Authentication endpoints
│   │       ├── SavedFlashcardController.java # Saved flashcard endpoints
│   │       ├── AuthService.java             # Authentication logic
│   │       ├── JwtUtil.java                 # JWT token utilities
│   │       ├── JwtAuthFilter.java          # JWT authentication filter
│   │       └── SecurityConfig.java          # Spring Security configuration
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
├── frontend/                   # React frontend
│   ├── src/
│   │   ├── App.jsx              # Main app with auth & tabs
│   │   ├── App.css              # Enhanced styles with auth modal
│   │   ├── Flashcard.jsx        # Flashcard component with save/delete
│   │   ├── Flashcard.css        # Enhanced flashcard styles
│   │   └── main.jsx
│   ├── package.json
│   └── vite.config.js           # Updated with auth proxy
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
- `spring-boot-starter-data-jpa`: Database integration with Hibernate
- `spring-boot-starter-security`: Authentication and authorization
- `h2`: In-memory database for development
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson`: JWT token handling
- `spring-boot-devtools`: Development tools

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

# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:qbcarder
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# H2 Console (for development)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
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
- **Study Aid**: Create and save flashcards for any subject you're learning
- **Quiz Bowl Prep**: Practice with real Quiz Bowl content and build your question bank
- **Concept Review**: Get Wikipedia summaries for deeper understanding
- **Personal Library**: Build a curated collection of flashcards across multiple topics

### For Educators
- **Classroom Tool**: Generate flashcards for lesson topics and save class sets
- **Assessment Prep**: Help students review key concepts with saved flashcard collections
- **Curriculum Support**: Supplement existing materials with Quiz Bowl-quality content
- **Student Progress**: Track what topics students are studying through their saved cards

### For Quiz Bowl Teams
- **Training Tool**: Practice with actual tournament questions and save difficult concepts
- **Topic Exploration**: Discover related concepts and facts, building comprehensive study sets
- **Strategy Development**: Understand question patterns and clues through saved examples
- **Team Collaboration**: Share and discuss saved flashcards among team members

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

### Completed Features ✅
- [x] **User Authentication**: Save and manage personal flashcard collections
- [x] **Personal Library**: View, organize, and delete saved flashcards
- [x] **Tab Navigation**: Switch between generating and viewing saved cards
- [x] **Persistent Storage**: Database-backed flashcard storage
- [x] **JWT Security**: Secure token-based authentication

### Planned Features 🚧
- [ ] **Export Options**: PDF, Anki, and other formats
- [ ] **Difficulty Levels**: Beginner, intermediate, and advanced content
- [ ] **Spaced Repetition**: Smart review scheduling
- [ ] **Collaborative Features**: Share flashcard sets with others
- [ ] **Analytics**: Track study progress and performance
- [ ] **Mobile App**: Native iOS and Android applications
- [ ] **Offline Mode**: Study without internet connection
- [ ] **Flashcard Categories**: Organize saved cards by subject
- [ ] **Search Functionality**: Find specific flashcards in your collection

### Technical Improvements Completed ✅
- [x] **Database Integration**: H2 database with JPA/Hibernate
- [x] **Authentication System**: JWT-based security with Spring Security
- [x] **RESTful API**: Full CRUD operations for flashcards and users

### Technical Improvements Planned 🚧
- [ ] **Production Database**: PostgreSQL or MySQL for production deployment
- [ ] **Caching**: Improve performance with Redis
- [ ] **API Rate Limiting**: Prevent abuse
- [ ] **Enhanced NLP**: Better phrase extraction algorithms
- [ ] **Search Functionality**: Full-text search across saved flashcards
- [ ] **Batch Processing**: Generate multiple topics at once
- [ ] **Email Verification**: Verify user email addresses during registration
- [ ] **Password Reset**: Allow users to reset forgotten passwords

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
