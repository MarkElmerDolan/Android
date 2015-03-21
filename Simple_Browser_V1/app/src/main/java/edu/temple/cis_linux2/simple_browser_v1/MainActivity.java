package edu.temple.cis_linux2.simple_browser_v1;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.http.HttpConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Mark Dolan
 */
public class MainActivity extends Activity implements View.OnClickListener {

    ImageButton imageButton;
    WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS); // call before adding content
        setContentView(R.layout.activity_main);

        imageButton = (ImageButton)findViewById(R.id.imagebutton);
        imageButton.setOnClickListener(this);

        final Activity activity = this;

        wv = (WebView)findViewById(R.id.webview);
        wv.getSettings().setJavaScriptEnabled(true);

        wv.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress){
                activity.setProgress(progress * 1000);
            }
        });

        wv.setWebViewClient(new WebViewClient(){
            public void onReceivedError(WebView view, int errorCode, String desc, String failURL){
                Toast.makeText(activity, "oh no!" + desc, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onClick(View v){
        switch(v.getId()) { // in case I want to implement more than one button
            case R.id.imagebutton:
                EditText et = (EditText)findViewById(R.id.edittext);
                String link = et.getText().toString();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                String safeLink = parseForHTTP(link);
                new GetHTML().execute(safeLink);
                break;
        }
    }

    private class GetHTML extends AsyncTask<String, Integer, String> {

        // perform work in worker thread, pass parameters of execute
        protected String doInBackground(String... urls){

            Log.d("DoInBackground", "On do in background");
            HttpGet httpGet = new HttpGet(urls[0]);
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
            HttpConnectionParams.setSoTimeout(httpParams, 4000);
            HttpClient httpClient = new DefaultHttpClient(httpParams);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while((line = reader.readLine()) != null){
                    sb.append(line + "\n");
                }
                String resString = sb.toString();
                is.close();

                return resString;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Error Getting HTML";
        }

        // perform work in UI thread and deliver result from doInBackground
        protected void onPostExecute(String result){
            if(result.equals("Error Getting HTML")){
                EditText et = (EditText)findViewById(R.id.edittext);
                et.setText(result);
            }else {
                WebView wv = (WebView) findViewById(R.id.webview);
                wv.loadData(result, "text/html", null);
            }
        }

        @Override
        protected void onPreExecute() {
            Log.d("PreExecute", "On pre execute ..");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("progressUpdate","Progess update" + values[0]);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected String parseForHTTP(String s){
        String[] tokens = s.split(" ");
        String result = "";
        for(int i = 0; i < tokens.length; i++){
            if(i == 0 && (tokens[0].indexOf("http://") == -1 || tokens[0].indexOf("https://") == -1)){
                result = "http://";
                result = result.concat(tokens[i]);
            }else if(i > 0){
                result = result.concat(tokens[i]);
            }else{
                result = result.concat(tokens[i]);
            }
        }
        return result;
    }

}
