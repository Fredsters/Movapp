package semanticweb.hws14.movapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.helper.InputCleaner;
import semanticweb.hws14.movapp.model.TimePeriod;


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
        initCriteriaView();
    }

    public void submitSearch(View view) {
        Intent intent = new Intent(this, List.class);

        EditText actorNameInput = (EditText) findViewById(R.id.tfActorName);
        String actorName = InputCleaner.cleanActorName(actorNameInput.getText().toString());

       // EditText directorNameInput = (EditText) findViewById(R.id.tfDirectorName);
     //   String directorName = InputCleaner.cleanActorName(directorNameInput.getText().toString());

        HashMap<String, Object> criteria = new HashMap<String, Object>();

        criteria.put("actorName", actorName);
        criteria.put("isActor", true);

   //     criteria.put("directorName", directorName);
        criteria.put("isDirector", false);

        criteria.put("timePeriod", new TimePeriod(1995, 2005));
        criteria.put("isTime", false);


        criteria.put("isGenre", false);
        intent.putExtra("criteria", criteria);

        startActivity(intent);
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

    private void initCriteriaView(){
        Button btnActor = (Button) findViewById(R.id.btnActor);
        Button btnYear = (Button) findViewById(R.id.btnYear);
        Button btnGenre = (Button) findViewById(R.id.btnGenre);
        Button btnDirector = (Button) findViewById(R.id.btnDirector);

        View panelActor = findViewById(R.id.panelActor);
        panelActor.setVisibility(View.GONE);

        View panelYear = findViewById(R.id.panelYear);
        panelYear.setVisibility(View.GONE);

        View panelGenre = findViewById(R.id.panelGenre);
        panelGenre.setVisibility(View.GONE);

        View panelDirector = findViewById(R.id.panelDirector);
        panelDirector.setVisibility(View.GONE);

        btnActor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO STUFF
                View panelActor = findViewById(R.id.panelActor);
                panelActor.setVisibility(View.VISIBLE);

                View panelYear = findViewById(R.id.panelYear);
                panelYear.setVisibility(View.GONE);

                View panelGenre = findViewById(R.id.panelGenre);
                panelGenre.setVisibility(View.GONE);

                View panelDirector = findViewById(R.id.panelDirector);
                panelDirector.setVisibility(View.GONE);

            }
        });

        btnYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO STUFF
                View panelActor = findViewById(R.id.panelActor);
                panelActor.setVisibility(View.GONE);

                View panelYear = findViewById(R.id.panelYear);
                panelYear.setVisibility(View.VISIBLE);

                View panelGenre = findViewById(R.id.panelGenre);
                panelGenre.setVisibility(View.GONE);

                View panelDirector = findViewById(R.id.panelDirector);
                panelDirector.setVisibility(View.GONE);

            }
        });

        btnGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO STUFF
                View panelActor = findViewById(R.id.panelActor);
                panelActor.setVisibility(View.GONE);

                View panelYear = findViewById(R.id.panelYear);
                panelYear.setVisibility(View.GONE);

                View panelGenre = findViewById(R.id.panelGenre);
                panelGenre.setVisibility(View.VISIBLE);

                View panelDirector = findViewById(R.id.panelDirector);
                panelDirector.setVisibility(View.GONE);

            }
        });

        btnDirector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO STUFF
                View panelActor = findViewById(R.id.panelActor);
                panelActor.setVisibility(View.GONE);

                View panelYear = findViewById(R.id.panelYear);
                panelYear.setVisibility(View.GONE);

                View panelGenre = findViewById(R.id.panelGenre);
                panelGenre.setVisibility(View.GONE);

                View panelDirector = findViewById(R.id.panelDirector);
                panelDirector.setVisibility(View.VISIBLE);

            }
        });

        setupSpinnerYearFrom();
        setupSpinnerYearTo();
        setupSpinnerGenre();
    }

    private void setupSpinnerYearFrom(){
        yearFrom = (Spinner) findViewById(R.id.spYearFrom);
        ArrayAdapter<CharSequence> adapterFrom = ArrayAdapter.createFromResource(this, R.array.year_from, android.R.layout.simple_spinner_item);
        adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearFrom.setAdapter(adapterFrom);
        yearFrom.setOnItemSelectedListener(this);
    }

    private void setupSpinnerYearTo(){
        yearTo = (Spinner) findViewById(R.id.spYearTo);
        ArrayAdapter<CharSequence> adapterTo = ArrayAdapter.createFromResource(this, R.array.year_to,android.R.layout.simple_spinner_item);
        adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        yearTo.setAdapter(adapterTo);
        yearTo.setOnItemSelectedListener(this);
    }

    private void setupSpinnerGenre(){
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
