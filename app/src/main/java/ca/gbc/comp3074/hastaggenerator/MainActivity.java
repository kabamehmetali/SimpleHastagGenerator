package ca.gbc.comp3074.hastaggenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {

    private EditText promptEditText;
    private EditText numberEditText;
    private Button generateButton;
    private TextView hashtagsTextView;
    private Button copyButton;
    private Dialog loadingDialog;

    private static final String OPENAI_API_KEY = "your_api_key";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        promptEditText = findViewById(R.id.promptEditText);
        numberEditText = findViewById(R.id.numberEditText);
        generateButton = findViewById(R.id.generateButton);
        hashtagsTextView = findViewById(R.id.hashtagsTextView);
        copyButton = findViewById(R.id.copyButton);

        // Set up the loading dialog with a progress bar
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(new ProgressBar(this));
        loadingDialog.setCancelable(false);

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String prompt = promptEditText.getText().toString().trim();
                String numberStr = numberEditText.getText().toString().trim();

                if (prompt.isEmpty() || numberStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a prompt and the number of hashtags.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int numberOfHashtags;
                try {
                    numberOfHashtags = Integer.parseInt(numberStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Please enter a valid number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Disable the button and show the loading dialog
                generateButton.setEnabled(false);
                loadingDialog.show();

                // Generate hashtags in a background task
                new GenerateHashtagsTask().execute(prompt, String.valueOf(numberOfHashtags));
            }
        });

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hashtags = hashtagsTextView.getText().toString();
                if (!hashtags.isEmpty()) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Hashtags", hashtags);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(MainActivity.this, "Hashtags copied to clipboard.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No hashtags to copy.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // AsyncTask to generate hashtags in the background
    private class GenerateHashtagsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String prompt = params[0];
            int numberOfHashtags = Integer.parseInt(params[1]);
            return generateHashtags(prompt, numberOfHashtags);
        }

        @Override
        protected void onPostExecute(String hashtags) {
            // Update the UI with the hashtags and hide the loading dialog
            hashtagsTextView.setText(hashtags);
            loadingDialog.dismiss();
            generateButton.setEnabled(true);
        }
    }

    private String generateHashtags(String prompt, int numberOfHashtags) {
        OkHttpClient client = new OkHttpClient();

        String fullPrompt = "Generate " + numberOfHashtags + " relevant hashtags for: \"" + prompt + "\" in turkish ";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo");

            // Messages array
            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", fullPrompt);
            messages.put(message);

            jsonBody.put("messages", messages);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error creating request.";
        }

        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .post(body)
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);
                String result = jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
                return result.trim();
            } else {
                return "Error: " + response.message();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return "Error: Unable to connect to the API.";
        }
    }
}
