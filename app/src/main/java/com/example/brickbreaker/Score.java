package com.example.brickbreaker;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Score extends AppCompatActivity {
    private ListView listView;
    private DatabaseReference scoresRef;
    private List<GameOver.Score> scoreList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        scoresRef = database.getReference("scores");

        listView = findViewById(R.id.listView);
        scoreList = new ArrayList<>();

        // Fetch scores from Firebase and display them
        fetchScoresAndDisplay();
    }

    private void fetchScoresAndDisplay() {
        scoresRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                scoreList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GameOver.Score score = snapshot.getValue(GameOver.Score.class);
                    scoreList.add(score);
                }

                // Sort the scores in descending order (highest to lowest)
                Collections.sort(scoreList, new Comparator<GameOver.Score>() {
                    @Override
                    public int compare(GameOver.Score score1, GameOver.Score score2) {
                        return Integer.compare(score2.points, score1.points);
                    }
                });

                // Display the sorted scores in the ListView
                displayScores();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void displayScores() {
        ArrayAdapter<GameOver.Score> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scoreList);
        listView.setAdapter(adapter);
    }
}
