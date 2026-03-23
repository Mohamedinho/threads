package com.example.threads;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity demonstrating Thread and AsyncTask usage.
 * Personalized version for the lab.
 */
public class MainActivity extends AppCompatActivity {

    private TextView statusTextView;
    private ProgressBar taskProgressBar;
    private ImageView displayImageView;
    private Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialization of UI components
        statusTextView = findViewById(R.id.txtStatus);
        taskProgressBar = findViewById(R.id.progressBar);
        displayImageView = findViewById(R.id.img);

        Button btnLoad = findViewById(R.id.btnLoadThread);
        Button btnCompute = findViewById(R.id.btnCalcAsync);
        Button btnShowToast = findViewById(R.id.btnToast);

        // Handler to communicate with the UI thread from worker threads
        uiHandler = new Handler(Looper.getMainLooper());

        // Button to test UI responsiveness
        btnShowToast.setOnClickListener(v -> 
            Toast.makeText(this, "L'interface est toujours réactive !", Toast.LENGTH_SHORT).show()
        );

        // Load image using a simple Thread
        btnLoad.setOnClickListener(v -> startImageLoadingThread());

        // Perform heavy calculation using AsyncTask
        btnCompute.setOnClickListener(v -> new BackgroundComputeTask().execute());
    }

    /**
     * Demonstrates manual Thread management.
     */
    private void startImageLoadingThread() {
        taskProgressBar.setVisibility(View.VISIBLE);
        taskProgressBar.setProgress(0);
        statusTextView.setText("Statut : Chargement de l'image via Thread...");

        new Thread(() -> {
            try {
                // Simulate network or disk latency
                Thread.sleep(1500); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Decode image from resources
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

            // Back to UI thread to update views
            uiHandler.post(() -> {
                displayImageView.setImageBitmap(bmp);
                taskProgressBar.setVisibility(View.INVISIBLE);
                statusTextView.setText("Statut : Image chargée avec succès !");
            });
        }).start();
    }

    /**
     * Demonstrates AsyncTask for background operations with progress updates.
     */
    private class BackgroundComputeTask extends AsyncTask<Void, Integer, Long> {

        @Override
        protected void onPreExecute() {
            taskProgressBar.setVisibility(View.VISIBLE);
            taskProgressBar.setProgress(0);
            statusTextView.setText("Statut : Calcul intensif en cours...");
        }

        @Override
        protected Long doInBackground(Void... voids) {
            long total = 0;
            for (int i = 1; i <= 100; i++) {
                // Intensive dummy calculation
                for (int j = 0; j < 500000; j++) {
                    total += (i + j) % 13;
                }
                
                // Publish progress to UI
                publishProgress(i);
                
                // Slow down slightly to see the progress bar move
                try { Thread.sleep(20); } catch (InterruptedException ignored) {}
            }
            return total;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            taskProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Long result) {
            taskProgressBar.setVisibility(View.INVISIBLE);
            statusTextView.setText("Statut : Calcul terminé. Résultat = " + result);
        }
    }
}
