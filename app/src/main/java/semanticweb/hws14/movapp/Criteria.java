package semanticweb.hws14.movapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Criteria extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criteria);

        Button btnProfile = (Button) findViewById(R.id.btnProfile);
        Button btnSettings = (Button) findViewById(R.id.btnSettings);
        Button btnPrivacy = (Button) findViewById(R.id.btnPrivacy);

        View panelProfile = findViewById(R.id.panelProfile);
        panelProfile.setVisibility(View.GONE);

        View panelSettings = findViewById(R.id.panelSettings);
        panelSettings.setVisibility(View.GONE);

        View panelPrivacy = findViewById(R.id.panelPrivacy);
        panelPrivacy.setVisibility(View.GONE);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO STUFF
                View panelProfile = findViewById(R.id.panelProfile);
                panelProfile.setVisibility(View.VISIBLE);

                View panelSettings = findViewById(R.id.panelSettings);
                panelSettings.setVisibility(View.GONE);

                View panelPrivacy = findViewById(R.id.panelPrivacy);
                panelPrivacy.setVisibility(View.GONE);

            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO STUFF
                View panelProfile = findViewById(R.id.panelProfile);
                panelProfile.setVisibility(View.GONE);

                View panelSettings = findViewById(R.id.panelSettings);
                panelSettings.setVisibility(View.VISIBLE);

                View panelPrivacy = findViewById(R.id.panelPrivacy);
                panelPrivacy.setVisibility(View.GONE);

            }
        });

        btnPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO STUFF
                View panelProfile = findViewById(R.id.panelProfile);
                panelProfile.setVisibility(View.GONE);

                View panelSettings = findViewById(R.id.panelSettings);
                panelSettings.setVisibility(View.GONE);

                View panelPrivacy = findViewById(R.id.panelPrivacy);
                panelPrivacy.setVisibility(View.VISIBLE);

            }
        });

        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.criteria, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void submitSearch(View view) {
        Intent intent = new Intent(this, list.class);
    //    EditText editText = (EditText) findViewById(R.id.actor_name);
    //    String message = editText.getText().toString();


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        EditText editText = (EditText) findViewById(R.id.actor_name);

        ArrayList<HashMap<String,String>> result = QueryEngine.runListQuery(editText.getText().toString());

        intent.putExtra("result", result);
        startActivity(intent);
    }
}
