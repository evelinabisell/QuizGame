package com.example.quizgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

// Main activity for Quiz Game
public class MainActivity extends AppCompatActivity {

    static int NUM_OF_QUESTIONS_PER_GAME = 10;

    List<Question> questions;
    List<Button> answerButtons;
    List<Button> wrongButtons;

    TextView questionText;
    TextView counterText;
    TextView endingText;
    TextView commentText;

    Button answerButton1;
    Button answerButton2;
    Button answerButton3;
    Button answerButton4;

    Button startButton;
    Button restartButton;

    Integer score = 0;

    int questionCounter = 0;

    // My custom colors
    String red  = "#FB7C7C";
    String green  = "#A3FFB2";
    String buttonColor = "#C3E1D8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Hide top menu bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Initialize lists as empty ArrayLists
        questions = new ArrayList<>();
        answerButtons = new ArrayList<>();
        wrongButtons = new ArrayList<>();

        // Find views in XML and add them to class variables
        questionText = findViewById(R.id.questionText);
        counterText = findViewById(R.id.counterText);
        endingText = findViewById(R.id.endingText);
        commentText = findViewById(R.id.commentText);
        answerButton1 = findViewById(R.id.answerButton1);
        answerButton2 = findViewById(R.id.answerButton2);
        answerButton3 = findViewById(R.id.answerButton3);
        answerButton4 = findViewById(R.id.answerButton4);
        startButton = findViewById(R.id.startButton);
        restartButton = findViewById(R.id.restartButton);

        // Add all answer buttons to the list answerButtons
        Collections.addAll(answerButtons, answerButton1, answerButton2, answerButton3, answerButton4);

        // Clicking the start button calls the startGame function
        startButton.setOnClickListener(View -> {
            startGame();
            startButton.setVisibility(android.view.View.INVISIBLE);
        });
    }

    // Starts the game
    private void startGame() {
        makeQuestions();
        Collections.shuffle(questions);

        displayQuestion(questions.get(questionCounter));
    }

    // Restarts the game
    private void restartGame() {
        restartButton.setVisibility(View.INVISIBLE);
        endingText.setVisibility(View.INVISIBLE);
        commentText.setVisibility(View.INVISIBLE);

        score = 0;
        questionCounter = 0;
        Collections.shuffle(questions);

        displayQuestion(questions.get(questionCounter));
    }

    // Displays the question and it's answers on the four buttons
    private void displayQuestion(Question question) {
        // Clear list with wrong answer buttons from previous question
        wrongButtons.clear();

        // Set the question text
        questionText.setText(question.question);
        questionText.setVisibility(View.VISIBLE);

        // Take a random number from the the size of answer buttons (1 to 4)
        int randNum = new Random().nextInt(answerButtons.size());
        int i = 0;
        for (Button button : answerButtons) {
            button.setVisibility(View.VISIBLE);
            // If it's the random number make it the correct answer button
            if (i == randNum) {
                button.setText(question.correctAnswer);
                // Correct button becomes green when pressed and increases score
                setButtonFeatures(button, green, 1, true);
            } else {
                // Not correct buttons become red and doesn't increase score
                setButtonFeatures(button, red, 0, false);
                wrongButtons.add(button);
            }
            i++;
        }
        // Set the text of the buttons in wrongButtons to the wrong answers
        wrongButtons.get(0).setText(question.wrongAnswer1);
        wrongButtons.get(1).setText(question.wrongAnswer2);
        wrongButtons.get(2).setText(question.wrongAnswer3);
    }

    // Sets effects on buttons to change color and score, and then disappear when pressed
    private void setButtonFeatures(Button button, String color, Integer points, boolean correct) {
        // Makes the button red or green while pressed
        button.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                button.setBackgroundColor(Color.parseColor(color));
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL)
            {
                button.setBackgroundColor(Color.parseColor(buttonColor));
            }
            return false;
        });

        button.setOnClickListener(view -> {
            score += (points);
            displayScore();

            if (!correct) sendVibration();

            // Display the next question, or if enough have been displayed, end the game
            questionCounter += 1;
            if (questionCounter < NUM_OF_QUESTIONS_PER_GAME) {
                displayQuestion(questions.get(questionCounter));
            } else {
                endGame();
            }
        });
    }

    // Show end game info, hide other buttons and text
    private void endGame() {
        for (Button button : answerButtons) {
            button.setVisibility(View.INVISIBLE);
        }
        counterText.setVisibility(View.INVISIBLE);
        questionText.setVisibility(View.INVISIBLE);

        String endInfo = "Quiz finished! \nYou got " + score + "/" + NUM_OF_QUESTIONS_PER_GAME +
                "\ncorrect answers.";
        endingText.setText(endInfo);
        endingText.setVisibility(View.VISIBLE);

        if (score > 7) {
            commentText.setText("Amazingly done!");
        } else if (score > 3){
            commentText.setText("Try again!");
        } else {
            commentText.setText("Oops! That didn't go very well... Maybe try again?");
        }
        commentText.setVisibility(View.VISIBLE);

        // Make restartButton visible and make it restart the game when pressed
        restartButton.setVisibility(View.VISIBLE);
        restartButton.setOnClickListener(view -> {
            restartGame();
        });
    }

    // Shows current score out of current number of questions
    private void displayScore() {
        String scoreKeeping = score.toString() + "/" + Integer.toString(questionCounter + 1);
        counterText.setText(scoreKeeping);
        counterText.setVisibility(View.VISIBLE);
    }

    // Makes the phone vibrate
    public void sendVibration() {
        Vibrator vibrator = null;
        // If build version 26 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(200);
        }
    }

    // Function to create unique Question objects and add them to a list called questions
    private void makeQuestions() {
        Question q1 = new Question("Which cat breed is often called 'Sacred' and is known for " +
                "it's deep blue eyes and white paws?",
                "Birman", "Maine Coon", "Abyssinian", "Russian Blue");
        questions.add(q1);

        Question q2 = new Question("What is the name of the greek god of war?",
                "Ares", "Hades", "Zeus", "Kratos");
        questions.add(q2);

        Question q3 = new Question("In which museum can you find Leonardo Da Vinci’s Mona Lisa?",
                "Le Louvre", "Uffizi Gallery", "Rijksmuseum", "The Metropolitan Museum of Art");
        questions.add(q3);

        Question q4 = new Question("Which actor plays Geralt in the TV-series 'The Witcher'?",
                "Henry Cavill", "Liam Hemsworth", "Matt Bomer", "Ben Affleck");
        questions.add(q4);

        Question q5 = new Question("Which of the following countries consume the most coffee per person?",
                "Finland", "The United States of America", "Netherlands", "Italy");
        questions.add(q5);

        Question q6 = new Question("Which of the following books is NOT written by Stephen King?",
                "Let the Right One In", "The Green Mile", "The Dark Tower", "The Shining");
        questions.add(q6);

        Question q7 = new Question("Which of these people have received the Nobel Peace Prize?",
                "Barack Obama", "Mahatma Gandhi", "Eleanor Roosevelt", "Pope John Paul II");
        questions.add(q7);

        Question q8 = new Question("Who directed 'E.T. the Extra-Terrestrial'?",
                "Steven Spielberg", "James Cameron", "Peter Jackson", "Robert Zemeckis");
        questions.add(q8);

        Question q9 = new Question("Who discovered Penicillin?",
                "Alexander Fleming", "Louis Pasteur", "Marie Curie", "Dmitri Mendeleev");
        questions.add(q9);

        Question q10 = new Question("What is the capital of Brazil?",
                "Brasília", "Rio de Janeiro", "São Paulo", "Fortaleza");
        questions.add(q10);

        Question q11 = new Question("What number was the Apollo mission that put a man on the " +
                "moon for the first time?",
                "Apollo 11", "Apollo 9", "Apollo 14", "Apollo 13");
        questions.add(q11);

        Question q12 = new Question("What spirit is used in making a Tom Collins?",
                "Gin", "Rom", "Vodka", "Bourbon");
        questions.add(q12);

        Question q13 = new Question("What country has the most World Cup soccer titles?",
                "Brazil", "Germany", "Italy", "Argentina");
        questions.add(q13);

        Question q14 = new Question("Which blood type is known as the “universal recipient” and " +
                "can receive blood from all other blood types?",
                "AB+", "O+", "O-", "A+");
        questions.add(q14);

        Question q15 = new Question("Which of these phobias mean a fear of spiders?",
                "Arachnophobia", "Agoraphobia", "Xenophobia", "Scopophobia");
        questions.add(q15);

        Question q16 = new Question("'Murder on the Orient Express' was written by Agatha " +
                "Christie, and features which famous detective?",
                "Hercule Poirot", "Sherlock Holmes", "Miss Marple", "Richard Poole");
        questions.add(q16);

        Question q17 = new Question("Hyundai Motors is an automotive manufacturer, from which country?",
                "South Korea", "Japan", "Germany", "Czechia");
        questions.add(q17);

        Question q18 = new Question("Which dog breed is considered the fastest in the world, " +
                "with a top speed of around 70 kilometers per hour (43 mph)?",
                "Greyhound", "Saluki", "Whippet", "Jack Russell Terrier");
        questions.add(q18);

        Question q19 = new Question("On which island state is it tradition to hang a garland " +
                "called 'Lei' around the necks of newly arrived guests?",
                "Hawaii", "Bahamas", "Easter Island", "New Zealand");
        questions.add(q19);

        Question q20 = new Question("What year did the Berlin Wall fall?",
                "1989", "1986", "1984", "1992");
        questions.add(q20);

        Question q21 = new Question("On which city was the first ever nuclear weapon used in " +
                "armed conflict dropped?",
                "Hiroshima", "Nagasaki", "Hibakusha", "Osaka");
        questions.add(q21);

        Question q22 = new Question("LeBron James plays basketball for which team?",
                "Los Angeles Lakers", "New York Knicks", "Chicago Bulls", "Philadelphia 76ers");
        questions.add(q22);

        Question q23 = new Question("Which of these companies were NOT created in Sweden?",
                "Siemens", "IKEA", "H&M", "Spotify");
        questions.add(q23);

        Question q24 = new Question("Which of these Zodiac signs comes latest in the year?",
                "Sagittarius", "Aquarius", "Aries", "Scorpio");
        questions.add(q24);

        Question q25 = new Question("What city has hosted both the Summer and Winter Olympic Games?",
                "Beijing", "Tokyo", "Montreal", "Turin");
        questions.add(q25);

        Question q26 = new Question("What festival is traditionally celebrated every year in " +
                "München, Germany?",
                "Oktoberfest", "Karneval", "Sundance Film Festival", "Weihnachtsmarkt");
        questions.add(q26);

        Question q27 = new Question("Which of the following was one of the Seven Ancient " +
                "Wonders of the World?",
                "Great Pyramid of Giza", "Great Wall of China", "Colosseum", "Machu Picchu");
        questions.add(q27);

        Question q28 = new Question("This show officially opened on January 26, 1988 and is " +
                "the longest-running Broadway show ever?",
                "The Phantom of the Opera", "The Lion King", "Les Misérables", "Cats");
        questions.add(q28);

        Question q29 = new Question("What is the name of the protagonist in the Nintendo game" +
                " series 'The Legend of Zelda'?",
                "Link", "Chrom", "Ash", "Mario");
        questions.add(q29);

        Question q30 = new Question("Which of these famous inventors is credited with " +
                "inventing the telephone?",
                "Alexander Graham Bell", "Nikola Tesla", "Benjamin Franklin", "Thomas Edison");
        questions.add(q30);

        Question q31 = new Question("Who sang the songs 'Respect', 'Think' and '(You Make Me " +
                "Feel Like) A Natural Woman' and is referred to as the 'Queen of Soul'?",
                "Aretha Franklin", "Etta James", "Tina Turner", "Diana Ross");
        questions.add(q31);

        Question q32 = new Question("Which classic band recorded songs like 'Yesterday', " +
                "'Let It Be' and 'All You Need Is Love'?",
                "The Beatles", "The Beach Boys", "The Rolling Stones", "The Monkees");
        questions.add(q32);
    }
}