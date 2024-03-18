package com.example.brickbreaker;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class HighScoresActivity extends AppCompatActivity {

    private DatabaseReference scoresRef;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_high_scores);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        scoresRef = database.getReference("scores");

        listView = findViewById(R.id.listView);

        // Retrieve scores from Firebase
        scoresRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<GameOver.Score> scores = new ArrayList<>();

                for (DataSnapshot scoreSnapshot : dataSnapshot.getChildren()) {
                    GameOver.Score score = scoreSnapshot.getValue(GameOver.Score.class);
                    scores.add(score);
                }

                // Sort scores
                // Sort scores
                Collections.sort(scores, new Comparator<GameOver.Score>() {
                    @Override
                    public int compare(GameOver.Score score1, GameOver.Score score2) {
                        // Compare scores in descending order
                        return Integer.compare(score2.points, score1.points);
                    }
                });


                // Display scores in a ListView
                displayScores(scores);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors if needed
            }
        });
    }

    private void displayScores(List<GameOver.Score> scores) {
        List<String> scoreStrings = new ArrayList<>();
        for (GameOver.Score score : scores) {
            scoreStrings.add(score.username + ": " + score.points);
        }

        // Use an ArrayAdapter to display scores in a ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, scoreStrings);
        listView.setAdapter(adapter);
    }
    }
