package com.example.brickbreaker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GameOver extends AppCompatActivity {
    TextView tvPoints,score;
    EditText nameEditText;
    Button okButton;
    ImageView ivNewHighest;
    DatabaseReference scoresRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        scoresRef = database.getReference("scores");

        ivNewHighest = findViewById(R.id.ivNewHighest);
        tvPoints = findViewById(R.id.tvPoints);
        okButton = findViewById(R.id.ok);
        nameEditText = findViewById(R.id.name);
        score = findViewById(R.id.score);


        int points = getIntent().getExtras().getInt("points");

        if (points == 240) {
            ivNewHighest.setVisibility(View.VISIBLE);
        }

        tvPoints.setText(String.valueOf(points));

        score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOver.this, Score.class);
                startActivity(intent);

            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (submit()) {
                    saveScoreToFirebase(nameEditText.getText().toString(), points);
                }
            }
        });
    }

    private boolean submit() {
        String val = nameEditText.getText().toString().trim();
        if (val.isEmpty()) {
            nameEditText.setError("Username cannot be empty");
            return false;
        } else {
            nameEditText.setError(null);
            return true;
        }
    }

    private void saveScoreToFirebase(String username, int points) {
        // Create a unique key for each score entry
        String key = scoresRef.push().getKey();

        // Create a Score object
        Score score = new Score(username, points);

        // Save the score to the database
        scoresRef.child(key).setValue(score);

        // Restart the game or perform other actions if needed
        restart(null);
    }

    // Restart the game
    public void restart(View view) {
        Intent intent = new Intent(GameOver.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Exit the game
    public void exit(View view) {
        finish();
    }

    // Score class to represent the data structure
    public static class Score {
        public String username;
        public int points;

        public Score() {
            // Default constructor required for calls to DataSnapshot.getValue(Score.class)
        }

        public Score(String username, int points) {
            this.username = username;
            this.points = points;
        }
        public int compareTo(Score otherScore) {
            // Compare scores in descending order
            return Integer.compare(otherScore.points, this.points);
        }
    }
}
