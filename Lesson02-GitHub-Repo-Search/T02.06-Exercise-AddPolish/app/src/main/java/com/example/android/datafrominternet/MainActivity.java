/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.datafrominternet;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.datafrominternet.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity {

    final  static String LOG_TAG = MainActivity.class.getSimpleName();

    private EditText mSearchBoxEditText;

    private TextView mUrlDisplayTextView;

    private TextView mSearchResultsTextView;

    // (12) Create a variable to store a reference to the error message TextView
    private TextView mErrorTextView;
    // (24) Create a ProgressBar variable to store a reference to the ProgressBar
    private ProgressBar mProgressBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);

        mUrlDisplayTextView = (TextView) findViewById(R.id.tv_url_display);
        mSearchResultsTextView = (TextView) findViewById(R.id.tv_github_search_results_json);

        // (13) Get a reference to the error TextView using findViewById
        mErrorTextView = (TextView) findViewById(R.id.tv_error_message_display);
        // (25) Get a reference to the ProgressBar using findViewById
        mProgressBarView = (ProgressBar) findViewById(R.id.pb_loading_indicator);
    }

    /**
     * This method retrieves the search text from the EditText, constructs the
     * URL (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally fires off an AsyncTask to perform the GET request using
     * our {@link GithubQueryTask}
     */
    private void makeGithubSearchQuery() {
        String githubQuery = mSearchBoxEditText.getText().toString();
        URL githubSearchUrl = NetworkUtils.buildUrl(githubQuery);
        mUrlDisplayTextView.setText(githubSearchUrl.toString());
        new GithubQueryTask().execute(githubSearchUrl);
    }

    // (14) Create a method called showJsonDataView to show the data and hide the error
    private void showJsonDataView(){
        mSearchResultsTextView.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
    }

    // (15) Create a method called showErrorMessage to show the error and hide the data
    private void showErrorMessage(){
        mSearchResultsTextView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }
    public class GithubQueryTask extends AsyncTask<URL, Void, String> {

        // (26) Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBarView.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL searchURL = urls[0];
            String githubSearchResults = null;
            try {
                githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchURL);
            } catch (IOException e) {
                Log.e(LOG_TAG, "IOException"+e);
            }
            return githubSearchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            // (27) As soon as the loading is complete, hide the loading indicator
            mProgressBarView.setVisibility(View.INVISIBLE);
            if (s != null && !s.equals("")) {
                // (17) Call showJsonDataView if we have valid, non-null results
                showJsonDataView();
                mSearchResultsTextView.setText(s);
            } else {
                // (16) Call showErrorMessage if the result is null in onPostExecute
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Search" menu option
            case R.id.action_search:
                Context context = getApplicationContext();
                CharSequence text = "Search item selected" ;
                Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                toast.show();
                makeGithubSearchQuery();
                return true;
            default:
                /* an unknown menu item Id was passed */
                return super.onOptionsItemSelected(item);
        }
    }
}
