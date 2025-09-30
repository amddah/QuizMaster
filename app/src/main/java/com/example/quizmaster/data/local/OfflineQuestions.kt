package com.example.quizmaster.data.local

import com.example.quizmaster.data.Question
import com.example.quizmaster.data.Quiz
import com.example.quizmaster.data.QuizCategory
import com.example.quizmaster.data.QuizDifficulty

object OfflineQuestions {
    
    fun getQuizForCategory(category: QuizCategory, difficulty: QuizDifficulty): Quiz {
        val questions = when (category) {
            QuizCategory.GENERAL -> getGeneralKnowledgeQuestions(difficulty)
            QuizCategory.SCIENCE -> getScienceQuestions(difficulty)
            QuizCategory.HISTORY -> getHistoryQuestions(difficulty)
            QuizCategory.TECHNOLOGY -> getTechnologyQuestions(difficulty)
            QuizCategory.SPORTS -> getSportsQuestions(difficulty)
            QuizCategory.ENTERTAINMENT -> getEntertainmentQuestions(difficulty)
            QuizCategory.GEOGRAPHY -> getGeographyQuestions(difficulty)
            QuizCategory.ANIMALS -> getAnimalQuestions(difficulty)
        }
        
        return Quiz(
            questions = questions.shuffled().take(10), // Take 10 random questions
            category = category,
            difficulty = difficulty
        )
    }
    
    private fun getGeneralKnowledgeQuestions(difficulty: QuizDifficulty): List<Question> {
        return when (difficulty) {
            QuizDifficulty.EASY -> listOf(
                Question(
                    question = "What is the capital of France?",
                    correctAnswer = "Paris",
                    incorrectAnswers = listOf("London", "Berlin", "Madrid"),
                    category = "General Knowledge",
                    difficulty = "easy"
                ),
                Question(
                    question = "How many continents are there?",
                    correctAnswer = "7",
                    incorrectAnswers = listOf("5", "6", "8"),
                    category = "General Knowledge",
                    difficulty = "easy"
                ),
                Question(
                    question = "What is the largest mammal in the world?",
                    correctAnswer = "Blue Whale",
                    incorrectAnswers = listOf("Elephant", "Giraffe", "Hippopotamus"),
                    category = "General Knowledge",
                    difficulty = "easy"
                ),
                Question(
                    question = "What color do you get when you mix red and white?",
                    correctAnswer = "Pink",
                    incorrectAnswers = listOf("Purple", "Orange", "Yellow"),
                    category = "General Knowledge",
                    difficulty = "easy"
                ),
                Question(
                    question = "How many days are there in a leap year?",
                    correctAnswer = "366",
                    incorrectAnswers = listOf("365", "364", "367"),
                    category = "General Knowledge",
                    difficulty = "easy"
                )
            )
            QuizDifficulty.MEDIUM -> listOf(
                Question(
                    question = "What is the smallest country in the world?",
                    correctAnswer = "Vatican City",
                    incorrectAnswers = listOf("Monaco", "San Marino", "Liechtenstein"),
                    category = "General Knowledge",
                    difficulty = "medium"
                ),
                Question(
                    question = "What is the hardest natural substance on Earth?",
                    correctAnswer = "Diamond",
                    incorrectAnswers = listOf("Gold", "Iron", "Platinum"),
                    category = "General Knowledge",
                    difficulty = "medium"
                ),
                Question(
                    question = "Which planet is known as the Red Planet?",
                    correctAnswer = "Mars",
                    incorrectAnswers = listOf("Venus", "Jupiter", "Saturn"),
                    category = "General Knowledge",
                    difficulty = "medium"
                ),
                Question(
                    question = "What is the currency of Japan?",
                    correctAnswer = "Yen",
                    incorrectAnswers = listOf("Won", "Yuan", "Rupee"),
                    category = "General Knowledge",
                    difficulty = "medium"
                ),
                Question(
                    question = "How many bones are in the adult human body?",
                    correctAnswer = "206",
                    incorrectAnswers = listOf("205", "207", "208"),
                    category = "General Knowledge",
                    difficulty = "medium"
                )
            )
            QuizDifficulty.HARD -> listOf(
                Question(
                    question = "What is the most abundant gas in Earth's atmosphere?",
                    correctAnswer = "Nitrogen",
                    incorrectAnswers = listOf("Oxygen", "Carbon Dioxide", "Argon"),
                    category = "General Knowledge",
                    difficulty = "hard"
                ),
                Question(
                    question = "What is the chemical symbol for tungsten?",
                    correctAnswer = "W",
                    incorrectAnswers = listOf("Tu", "Tn", "T"),
                    category = "General Knowledge",
                    difficulty = "hard"
                ),
                Question(
                    question = "Which ancient wonder of the world was located in Alexandria?",
                    correctAnswer = "Lighthouse of Alexandria",
                    incorrectAnswers = listOf("Hanging Gardens", "Colossus of Rhodes", "Temple of Artemis"),
                    category = "General Knowledge",
                    difficulty = "hard"
                ),
                Question(
                    question = "What is the longest river in Asia?",
                    correctAnswer = "Yangtze",
                    incorrectAnswers = listOf("Ganges", "Mekong", "Yellow River"),
                    category = "General Knowledge",
                    difficulty = "hard"
                ),
                Question(
                    question = "What does 'www' stand for?",
                    correctAnswer = "World Wide Web",
                    incorrectAnswers = listOf("World Wide Wire", "Web Wide World", "Wide World Web"),
                    category = "General Knowledge",
                    difficulty = "hard"
                )
            )
        }
    }
    
    private fun getScienceQuestions(difficulty: QuizDifficulty): List<Question> {
        return when (difficulty) {
            QuizDifficulty.EASY -> listOf(
                Question(
                    question = "What is H2O commonly known as?",
                    correctAnswer = "Water",
                    incorrectAnswers = listOf("Hydrogen", "Oxygen", "Helium"),
                    category = "Science",
                    difficulty = "easy"
                ),
                Question(
                    question = "How many hearts does an octopus have?",
                    correctAnswer = "3",
                    incorrectAnswers = listOf("1", "2", "4"),
                    category = "Science",
                    difficulty = "easy"
                ),
                Question(
                    question = "What is the center of an atom called?",
                    correctAnswer = "Nucleus",
                    incorrectAnswers = listOf("Electron", "Proton", "Neutron"),
                    category = "Science",
                    difficulty = "easy"
                )
            )
            QuizDifficulty.MEDIUM -> listOf(
                Question(
                    question = "What is the speed of light in a vacuum?",
                    correctAnswer = "299,792,458 m/s",
                    incorrectAnswers = listOf("300,000,000 m/s", "299,000,000 m/s", "301,000,000 m/s"),
                    category = "Science",
                    difficulty = "medium"
                ),
                Question(
                    question = "What type of bond involves the sharing of electrons?",
                    correctAnswer = "Covalent",
                    incorrectAnswers = listOf("Ionic", "Metallic", "Hydrogen"),
                    category = "Science",
                    difficulty = "medium"
                )
            )
            QuizDifficulty.HARD -> listOf(
                Question(
                    question = "What is the Heisenberg Uncertainty Principle?",
                    correctAnswer = "You cannot simultaneously determine position and momentum of a particle",
                    incorrectAnswers = listOf("Energy cannot be created or destroyed", "Every action has an equal reaction", "Matter cannot be created or destroyed"),
                    category = "Science",
                    difficulty = "hard"
                )
            )
        }
    }
    
    private fun getHistoryQuestions(difficulty: QuizDifficulty): List<Question> {
        return when (difficulty) {
            QuizDifficulty.EASY -> listOf(
                Question(
                    question = "In which year did World War II end?",
                    correctAnswer = "1945",
                    incorrectAnswers = listOf("1944", "1946", "1943"),
                    category = "History",
                    difficulty = "easy"
                ),
                Question(
                    question = "Who was the first person to walk on the moon?",
                    correctAnswer = "Neil Armstrong",
                    incorrectAnswers = listOf("Buzz Aldrin", "John Glenn", "Yuri Gagarin"),
                    category = "History",
                    difficulty = "easy"
                )
            )
            QuizDifficulty.MEDIUM -> listOf(
                Question(
                    question = "Which empire was ruled by Julius Caesar?",
                    correctAnswer = "Roman Empire",
                    incorrectAnswers = listOf("Byzantine Empire", "Ottoman Empire", "Persian Empire"),
                    category = "History",
                    difficulty = "medium"
                )
            )
            QuizDifficulty.HARD -> listOf(
                Question(
                    question = "What year did the Berlin Wall fall?",
                    correctAnswer = "1989",
                    incorrectAnswers = listOf("1987", "1991", "1988"),
                    category = "History",
                    difficulty = "hard"
                )
            )
        }
    }
    
    private fun getTechnologyQuestions(difficulty: QuizDifficulty): List<Question> {
        return when (difficulty) {
            QuizDifficulty.EASY -> listOf(
                Question(
                    question = "What does 'CPU' stand for?",
                    correctAnswer = "Central Processing Unit",
                    incorrectAnswers = listOf("Computer Processing Unit", "Central Program Unit", "Computer Program Unit"),
                    category = "Technology",
                    difficulty = "easy"
                )
            )
            QuizDifficulty.MEDIUM -> listOf(
                Question(
                    question = "Which programming language is known for its use in web development?",
                    correctAnswer = "JavaScript",
                    incorrectAnswers = listOf("Python", "C++", "Java"),
                    category = "Technology",
                    difficulty = "medium"
                )
            )
            QuizDifficulty.HARD -> listOf(
                Question(
                    question = "What does 'SQL' stand for?",
                    correctAnswer = "Structured Query Language",
                    incorrectAnswers = listOf("Simple Query Language", "Standard Query Language", "System Query Language"),
                    category = "Technology",
                    difficulty = "hard"
                )
            )
        }
    }
    
    private fun getSportsQuestions(difficulty: QuizDifficulty): List<Question> {
        return when (difficulty) {
            QuizDifficulty.EASY -> listOf(
                Question(
                    question = "How many players are on a basketball team on the court at once?",
                    correctAnswer = "5",
                    incorrectAnswers = listOf("6", "4", "7"),
                    category = "Sports",
                    difficulty = "easy"
                )
            )
            QuizDifficulty.MEDIUM -> listOf(
                Question(
                    question = "In which sport would you perform a slam dunk?",
                    correctAnswer = "Basketball",
                    incorrectAnswers = listOf("Volleyball", "Tennis", "Baseball"),
                    category = "Sports",
                    difficulty = "medium"
                )
            )
            QuizDifficulty.HARD -> listOf(
                Question(
                    question = "Which country has won the most FIFA World Cups?",
                    correctAnswer = "Brazil",
                    incorrectAnswers = listOf("Germany", "Argentina", "Italy"),
                    category = "Sports",
                    difficulty = "hard"
                )
            )
        }
    }
    
    private fun getEntertainmentQuestions(difficulty: QuizDifficulty): List<Question> {
        return when (difficulty) {
            QuizDifficulty.EASY -> listOf(
                Question(
                    question = "Which movie features the song 'Let It Go'?",
                    correctAnswer = "Frozen",
                    incorrectAnswers = listOf("Moana", "Tangled", "The Little Mermaid"),
                    category = "Entertainment",
                    difficulty = "easy"
                )
            )
            QuizDifficulty.MEDIUM -> listOf(
                Question(
                    question = "Who directed the movie 'Jaws'?",
                    correctAnswer = "Steven Spielberg",
                    incorrectAnswers = listOf("George Lucas", "Martin Scorsese", "Francis Ford Coppola"),
                    category = "Entertainment",
                    difficulty = "medium"
                )
            )
            QuizDifficulty.HARD -> listOf(
                Question(
                    question = "Which film won the first Academy Award for Best Picture?",
                    correctAnswer = "Wings",
                    incorrectAnswers = listOf("Sunrise", "The Jazz Singer", "7th Heaven"),
                    category = "Entertainment",
                    difficulty = "hard"
                )
            )
        }
    }
    
    private fun getGeographyQuestions(difficulty: QuizDifficulty): List<Question> {
        return when (difficulty) {
            QuizDifficulty.EASY -> listOf(
                Question(
                    question = "What is the largest ocean on Earth?",
                    correctAnswer = "Pacific Ocean",
                    incorrectAnswers = listOf("Atlantic Ocean", "Indian Ocean", "Arctic Ocean"),
                    category = "Geography",
                    difficulty = "easy"
                )
            )
            QuizDifficulty.MEDIUM -> listOf(
                Question(
                    question = "Which mountain range contains Mount Everest?",
                    correctAnswer = "Himalayas",
                    incorrectAnswers = listOf("Andes", "Rocky Mountains", "Alps"),
                    category = "Geography",
                    difficulty = "medium"
                )
            )
            QuizDifficulty.HARD -> listOf(
                Question(
                    question = "What is the deepest point in the Earth's oceans?",
                    correctAnswer = "Mariana Trench",
                    incorrectAnswers = listOf("Puerto Rico Trench", "Java Trench", "Tonga Trench"),
                    category = "Geography",
                    difficulty = "hard"
                )
            )
        }
    }
    
    private fun getAnimalQuestions(difficulty: QuizDifficulty): List<Question> {
        return when (difficulty) {
            QuizDifficulty.EASY -> listOf(
                Question(
                    question = "What is the largest land animal?",
                    correctAnswer = "Elephant",
                    incorrectAnswers = listOf("Giraffe", "Rhinoceros", "Hippopotamus"),
                    category = "Animals",
                    difficulty = "easy"
                )
            )
            QuizDifficulty.MEDIUM -> listOf(
                Question(
                    question = "Which animal is known to have the most powerful bite?",
                    correctAnswer = "Saltwater Crocodile",
                    incorrectAnswers = listOf("Great White Shark", "Lion", "Hyena"),
                    category = "Animals",
                    difficulty = "medium"
                )
            )
            QuizDifficulty.HARD -> listOf(
                Question(
                    question = "What is the only mammal capable of true flight?",
                    correctAnswer = "Bat",
                    incorrectAnswers = listOf("Flying Squirrel", "Sugar Glider", "Flying Lemur"),
                    category = "Animals",
                    difficulty = "hard"
                )
            )
        }
    }
}