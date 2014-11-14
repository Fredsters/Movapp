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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.HashMap;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.helper.InputCleaner;
import semanticweb.hws14.movapp.model.TimePeriod;


public class Criteria extends Activity implements AdapterView.OnItemSelectedListener {

    String selectedActorName;
    int selectedFromDate;
    int selectedToDate;
    String selectedGenre;

    int actorCount = 1;

    EditText tfActorName;
    EditText tfDirectorName;

    Spinner yearFrom;
    Spinner yearTo;
    Spinner genre;

    Button btnActor;
    Button btnYear ;
    Button btnGenre ;
    Button btnDirector ;

    Switch swActor ;
    Switch swYear ;
    Switch swGenre ;
    Switch swDirector ;

    boolean activeActor = false;
    boolean activeYear = false;
    boolean activeGenre = false;
    boolean activeDirector = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criteria);
        initCriteriaView();
    }

    public void submitSearch(View view) {



        HashMap<String, Object> criteria = new HashMap<String, Object>();

        String actorName = tfActorName.getText().toString();
        String directorName = tfDirectorName.getText().toString();






        if(activeActor && !actorName.equals("")){
            actorName = InputCleaner.cleanName(actorName);
            criteria.put("actorName", actorName);
            criteria.put("isActor", true);
            System.out.println("!!!!!!!ACTOR ENABLED on Submit!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        else{
            criteria.put("isActor", false);
            System.out.println("!!!!!!!ACTOR DISABLED on Submit!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        if(activeYear){
            criteria.put("timePeriod", new TimePeriod(selectedFromDate, selectedToDate));
            criteria.put("isTime", true);
            System.out.println("!!!!!!!TIME ENABLED on Submit!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        else{
            criteria.put("isTime", false);
            System.out.println("!!!!!!!TIME DISABLED on Submit!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        if(activeGenre){
            criteria.put("genreName", selectedGenre);
            criteria.put("isGenre", true);
            System.out.println("!!!!!!!GENRE ENABLED on Submit!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        else{
            criteria.put("isGenre", false);
            System.out.println("!!!!!!!GENRE DISABLED on Submit!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        if(activeDirector && !directorName.equals("")){
            directorName = InputCleaner.cleanName(directorName);
            criteria.put("directorName", tfDirectorName.getText().toString());
            criteria.put("isDirector", true);
            System.out.println("!!!!!!!DIRECTOR ENABLED on Submit!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        else{
            criteria.put("isDirector", false);
            System.out.println("!!!!!!!DIRECTOR DISABLED on Submit!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

        if( (activeActor && !actorName.equals("")) || activeYear || activeGenre || (activeDirector && !directorName.equals(""))){
            Intent intent = new Intent(this, MovieList.class);
            intent.putExtra("criteria", criteria);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "Please choose at least one valid criteria!", Toast.LENGTH_SHORT).show();
        }


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

        tfActorName = (EditText) findViewById(R.id.tfActorName);
        tfDirectorName = (EditText) findViewById(R.id.tfDirectorName);

         btnActor = (Button) findViewById(R.id.btnActor);
         btnYear = (Button) findViewById(R.id.btnYear);
         btnGenre = (Button) findViewById(R.id.btnGenre);
         btnDirector = (Button) findViewById(R.id.btnDirector);

         swActor = (Switch) findViewById(R.id.swActor);
         swYear = (Switch) findViewById(R.id.swYear);
         swGenre = (Switch) findViewById(R.id.swGenre);
         swDirector = (Switch) findViewById(R.id.swDirector);


        final View panelActor = findViewById(R.id.panelActor);
        panelActor.setVisibility(View.VISIBLE);

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

        swActor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
             activeActor=b;
            }
        });

        swYear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activeYear = b;
            }
        });

        swGenre.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activeGenre = b;
            }
        });

        swDirector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activeDirector = b;
            }
        });
    }

    private void setupSpinnerYearFrom(){
        yearFrom = (Spinner) findViewById(R.id.spYearFrom);
        ArrayAdapter<CharSequence> adapterFrom = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_item);
        adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearFrom.setAdapter(adapterFrom);
        yearFrom.setSelection(6);
        yearFrom.offsetTopAndBottom(0);
        yearFrom.offsetLeftAndRight(0);
        yearFrom.setOnItemSelectedListener(this);
    }

    private void setupSpinnerYearTo(){
        yearTo = (Spinner) findViewById(R.id.spYearTo);
        ArrayAdapter<CharSequence> adapterTo = ArrayAdapter.createFromResource(this, R.array.year_array,android.R.layout.simple_spinner_item);
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
            selectedToDate =Integer.parseInt(item);
        }

        System.out.println();
        System.out.println("selectedActorName: " + selectedActorName);
        System.out.println("selectedFromDate: " +selectedFromDate);
        System.out.println("selectedToDate: " + selectedToDate);
        System.out.println("selectedGenre: " +selectedGenre);
        System.out.println();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public void addActor (View view){


        if(actorCount==1){
            View panelActor2 = findViewById(R.id.panelActor2);
            panelActor2.setVisibility(View.VISIBLE);
            actorCount++;
        }
        else if(actorCount==2){
            View panelActor3 = findViewById(R.id.panelActor3);
            panelActor3.setVisibility(View.VISIBLE);
            actorCount++;
        }
        else{
        }

    }

    public void delActor (View view){


        if(actorCount==2){
            View panelActor2 = findViewById(R.id.panelActor2);
            panelActor2.setVisibility(View.GONE);
            actorCount--;
        }
        else if(actorCount==3){
            View panelActor3 = findViewById(R.id.panelActor3);
            panelActor3.setVisibility(View.GONE);
            actorCount--;
        }
        else{
        }
    }
}

