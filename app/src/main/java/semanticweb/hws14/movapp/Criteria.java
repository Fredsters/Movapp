package semanticweb.hws14.movapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;


public class Criteria extends Activity implements AdapterView.OnItemSelectedListener {

    String selectedActorName;
    int selectedFromDate;
    int selctedToDate;
    String selectedGenre;

    Spinner yearFrom;
    Spinner yearTo;
    Spinner genre;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criteria);

        Button btnActor = (Button) findViewById(R.id.btnActor);
        Button btnYear = (Button) findViewById(R.id.btnYear);
        Button btnGenre = (Button) findViewById(R.id.btnGenre);

        View panelProfile = findViewById(R.id.panelActor);
        panelProfile.setVisibility(View.GONE);

        View panelSettings = findViewById(R.id.panelYear);
        panelSettings.setVisibility(View.GONE);

        View panelPrivacy = findViewById(R.id.panelGenre);
        panelPrivacy.setVisibility(View.GONE);

        btnActor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO STUFF
                View panelProfile = findViewById(R.id.panelActor);
                panelProfile.setVisibility(View.VISIBLE);

                View panelSettings = findViewById(R.id.panelYear);
                panelSettings.setVisibility(View.GONE);

                View panelPrivacy = findViewById(R.id.panelGenre);
                panelPrivacy.setVisibility(View.GONE);

            }
        });

        btnYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO STUFF
                View panelProfile = findViewById(R.id.panelActor);
                panelProfile.setVisibility(View.GONE);

                View panelSettings = findViewById(R.id.panelYear);
                panelSettings.setVisibility(View.VISIBLE);

                View panelPrivacy = findViewById(R.id.panelGenre);
                panelPrivacy.setVisibility(View.GONE);

            }
        });

        btnGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO STUFF
                View panelProfile = findViewById(R.id.panelActor);
                panelProfile.setVisibility(View.GONE);

                View panelSettings = findViewById(R.id.panelYear);
                panelSettings.setVisibility(View.GONE);

                View panelPrivacy = findViewById(R.id.panelGenre);
                panelPrivacy.setVisibility(View.VISIBLE);

            }
        });

        setupSpinnerYearFrom();
        setupSpinnerYearTo();
        setupSpinnerGenre();
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
        EditText editText = (EditText) findViewById(R.id.tfActorName);

        ArrayList<HashMap<String,String>> result = QueryEngine.runListQuery(editText.getText().toString());

        intent.putExtra("result", result);
        startActivity(intent);
    }

    public void setupSpinnerYearFrom(){
        yearFrom = (Spinner) findViewById(R.id.spYearFrom);
        ArrayAdapter<CharSequence> adapterFrom = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_item);
        adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearFrom.setAdapter(adapterFrom);
        yearFrom.setOnItemSelectedListener(this);
    }

    public void setupSpinnerYearTo(){
        yearTo = (Spinner) findViewById(R.id.spYearTo);
        ArrayAdapter<CharSequence> adapterTo = ArrayAdapter.createFromResource(this,R.array.year_array,android.R.layout.simple_spinner_item);
        adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearTo.setAdapter(adapterTo);
        yearTo.setOnItemSelectedListener(this);
    }

    public void setupSpinnerGenre(){
        genre = (Spinner) findViewById(R.id.spGenre);
        ArrayAdapter<CharSequence> adapterGenre = ArrayAdapter.createFromResource(this,R.array.genre_array,android.R.layout.simple_spinner_item);
        adapterGenre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genre.setAdapter(adapterGenre);
        genre.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item =  adapterView.getItemAtPosition(i).toString();

        if(adapterView.getId() == genre.getId()){
            selectedGenre=item;
        }
        else if(adapterView.getId() == yearFrom.getId()){
            selectedFromDate=Integer.parseInt(item);
        }
        else if (adapterView.getId() == yearTo.getId()){
            selctedToDate=Integer.parseInt(item);
        }

        System.out.println();
        System.out.println("selectedActorName: " + selectedActorName);
        System.out.println("selectedFromDate: " +selectedFromDate);
        System.out.println("selctedToDate: " + selctedToDate);
        System.out.println("selectedGenre: " +selectedGenre);
        System.out.println();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
