package com.example.slotr.booksapiapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText searchbar;
    ListView listView;
    TextView emptyView;
    ArrayList<Book> books;
    BookAdapter adapter;
    final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    Parcelable state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //asignment for books arraylist
        books = new ArrayList<>();
        if (savedInstanceState != null) {
            books = savedInstanceState.getParcelableArrayList("Data");
        }

        searchbar = (EditText) findViewById(R.id.search_bar);

        //list view
        emptyView = (TextView) findViewById(R.id.empty_state);
        adapter = new BookAdapter(this, 0, books);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        //search bar listener
        searchbar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    startSearch();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("Data", books);
        state = listView.onSaveInstanceState();
        outState.putParcelable("LIST_STATE", state);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        state = savedInstanceState.getParcelable("LIST_STATE");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (state != null)
            listView.onRestoreInstanceState(state);
        state = null;
    }

    public void startSearch() {
        String searchQuery = GOOGLE_BOOKS_API_URL + searchbar.getText().toString();
        searchQuery = searchQuery.replace(" ", "+");
        if (searchQuery.equals(GOOGLE_BOOKS_API_URL)) {
            Toast.makeText(this, "please enter something in text box", Toast.LENGTH_LONG).show();
        } else {
            new BooksAsyncTask().execute(searchQuery);
            Toast.makeText(getBaseContext(), "Searching...", Toast.LENGTH_LONG).show();
        }
    }

    public class BooksAsyncTask extends AsyncTask<String, Nullable, Book> {
        @Override
        protected Book doInBackground(String... urls) {
            books.clear();
            URL url = createUrl(urls[0]);
            String jsonResponse = null;
            try {
                jsonResponse = makeHttpRequest(url);
                convertJson(jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Book book) {
            emptyView.setText("No Content");
            listView.setEmptyView(emptyView);
            adapter.notifyDataSetChanged();
            super.onPostExecute(book);
        }

        //crete a URL from String
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return url;
        }

        //make Http Request To Retrieve Json Response
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = null;
            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readStream(inputStream);
            } else {

            }
            return jsonResponse;
        }

        //convert input strream to jsonResponse
        private String readStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }

            return output.toString();
        }

        private void convertJson(String jsonResponse) {
            String title;
            try {
                JSONObject root = new JSONObject(jsonResponse);
                JSONArray items = root.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject volume = items.getJSONObject(i);
                    JSONObject volumeInfo = volume.getJSONObject("volumeInfo");
                    title = volumeInfo.getString("title");
                    JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                    String authors = authorsArray.getString(0);
                    JSONObject imagelinks = volumeInfo.getJSONObject("imageLinks");
                    String imageresource = imagelinks.getString("thumbnail");
                    Bitmap bmp = null;
                    String infolink = volumeInfo.getString("infoLink");
                    try {
                        URL imageUrl = new URL(imageresource);
                        bmp = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    books.add(new Book(title, authors, bmp, infolink));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}