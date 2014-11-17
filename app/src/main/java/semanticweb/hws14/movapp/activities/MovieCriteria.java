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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.HashMap;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.helper.InputCleaner;
import semanticweb.hws14.movapp.model.TimePeriod;

import static semanticweb.hws14.movapp.helper.InputCleaner.cleanCityStateInput;


public class MovieCriteria extends Activity implements AdapterView.OnItemSelectedListener {

    int selectedFromDate;
    int selectedToDate;
    String selectedGenre;
    String selectedCity;
    String selectedState;

    String regionKind;

    EditText tfActorName;
    EditText tfDirectorName;
    Spinner spYearFrom;
    Spinner spYearTo;
    Spinner spGenre;
    Spinner spCity;
    Spinner spState;

    Button btnActor;
    Button btnYear ;
    Button btnGenre ;
    Button btnDirector;
    Button btnRegion;

    Switch swActor ;
    Switch swYear ;
    Switch swGenre ;
    Switch swDirector ;
    Switch swCity;
    Switch swState;

    boolean activeActor = false;
    boolean activeYear = false;
    boolean activeGenre = false;
    boolean activeDirector = false;
    boolean activeState = false;
    boolean activeCity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_criteria);
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
        } else{
            criteria.put("isActor", false);
        }

        if(activeYear){
            criteria.put("timePeriod", new TimePeriod(selectedFromDate, selectedToDate));
            criteria.put("isTime", true);
        } else{
            criteria.put("isTime", false);
        }

        if(activeGenre){
            criteria.put("genreName", selectedGenre);
            criteria.put("isGenre", true);
        } else{
            criteria.put("isGenre", false);
        }

        if(activeDirector && !directorName.equals("")){
            directorName = InputCleaner.cleanName(directorName);
            criteria.put("directorName", tfDirectorName.getText().toString());
            criteria.put("isDirector", true);
        } else {
            criteria.put("isDirector", false);
        }

        if(activeCity) {
            criteria.put("city", cleanCityStateInput(selectedCity));
            criteria.put("isCity", true);
            criteria.put("regionKind", regionKind);
        } else {
            criteria.put("isCity", false);
        }
        if(activeState) {
            cleanCityStateInput(selectedState);
            criteria.put("state", cleanCityStateInput(selectedState));
            criteria.put("isState", true);
            criteria.put("regionKind", regionKind);
        } else {
            criteria.put("isState", false);
        }

        if( (activeActor && !actorName.equals("")) || activeYear || activeGenre || (activeDirector && !directorName.equals("")) || activeCity || activeState){
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
        getMenuInflater().inflate(R.menu.movie_criteria, menu);
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
         btnRegion = (Button) findViewById(R.id.btnRegion);

         swActor = (Switch) findViewById(R.id.swActor);
         swYear = (Switch) findViewById(R.id.swYear);
         swGenre = (Switch) findViewById(R.id.swGenre);
         swDirector = (Switch) findViewById(R.id.swDirector);
         swCity = (Switch) findViewById(R.id.swCity);
         swState = (Switch) findViewById(R.id.swState);

        regionKind = "set";

        final View panelActor = findViewById(R.id.panelActor);
        panelActor.setVisibility(View.VISIBLE);

        View panelYear = findViewById(R.id.panelYear);
        panelYear.setVisibility(View.GONE);

        View panelGenre = findViewById(R.id.panelGenre);
        panelGenre.setVisibility(View.GONE);

        View panelDirector = findViewById(R.id.panelDirector);
        panelDirector.setVisibility(View.GONE);

        View panelRegion = findViewById(R.id.panelRegion);
        panelRegion.setVisibility(View.GONE);

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

                View panelRegion = findViewById(R.id.panelRegion);
                panelRegion.setVisibility(View.GONE);

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

                View panelRegion = findViewById(R.id.panelRegion);
                panelRegion.setVisibility(View.GONE);

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

                View panelRegion = findViewById(R.id.panelRegion);
                panelRegion.setVisibility(View.GONE);

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

                View panelRegion = findViewById(R.id.panelRegion);
                panelRegion.setVisibility(View.GONE);

            }
        });

        btnRegion.setOnClickListener(new View.OnClickListener() {
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
                panelDirector.setVisibility(View.GONE);

                View panelRegion = findViewById(R.id.panelRegion);
                panelRegion.setVisibility(View.VISIBLE);
            }
        });

        setupSpinnerYearFrom();
        setupSpinnerYearTo();
        setupSpinnerGenre();
        setupSpinnerCity();
        setupSpinnerState();

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

        swCity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activeCity = b;
                if(b) {
                    swState.setChecked(false);
                }
            }
        });

        swState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activeState = b;
                if(b) {
                    swCity.setChecked(false);
                }
            }
        });
    }

    private void setupSpinnerYearFrom(){
        spYearFrom = (Spinner) findViewById(R.id.spYearFrom);
        ArrayAdapter<CharSequence> adapterFrom = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_item);
        adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYearFrom.setAdapter(adapterFrom);
        spYearFrom.setSelection(6);
        spYearFrom.offsetTopAndBottom(0);
        spYearFrom.offsetLeftAndRight(0);
        spYearFrom.setOnItemSelectedListener(this);
    }

    private void setupSpinnerYearTo(){
        spYearTo = (Spinner) findViewById(R.id.spYearTo);
        ArrayAdapter<CharSequence> adapterTo = ArrayAdapter.createFromResource(this, R.array.year_array,android.R.layout.simple_spinner_item);
        adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYearTo.setAdapter(adapterTo);
        spYearTo.setOnItemSelectedListener(this);
    }

    private void setupSpinnerGenre(){
        spGenre = (Spinner) findViewById(R.id.spGenre);
        ArrayAdapter<CharSequence> adapterGenre = ArrayAdapter.createFromResource(this,R.array.genre_array,android.R.layout.simple_spinner_item);
        adapterGenre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGenre.setAdapter(adapterGenre);
        spGenre.setOnItemSelectedListener(this);
    }

    private void setupSpinnerCity(){
        spCity = (Spinner) findViewById(R.id.spCity);
        ArrayAdapter<CharSequence> adapterCity = ArrayAdapter.createFromResource(this,R.array.city_array,android.R.layout.simple_spinner_item);
        adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCity.setAdapter(adapterCity);
        spCity.setOnItemSelectedListener(this);
    }

    private void setupSpinnerState(){
        spState = (Spinner) findViewById(R.id.spState);
        ArrayAdapter<CharSequence> adapterState = ArrayAdapter.createFromResource(this,R.array.state_array,android.R.layout.simple_spinner_item);
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spState.setAdapter(adapterState);
        spState.setOnItemSelectedListener(this);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item =  adapterView.getItemAtPosition(i).toString();

        if(adapterView.getId() == spGenre.getId()){
            selectedGenre=item;
        }
        else if(adapterView.getId() == spYearFrom.getId()){
            selectedFromDate=Integer.parseInt(item);
        }
        else if (adapterView.getId() == spYearTo.getId()){
            selectedToDate =Integer.parseInt(item);
        }
        else if(adapterView.getId() == spCity.getId()){
            selectedCity=item;
        }
        else if(adapterView.getId() == spState.getId()){
            selectedState=item;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radio_set:
                if (checked)
                    regionKind = "set";
                    break;
            case R.id.radio_shot:
                if (checked)
                    regionKind = "shot";
                    break;
        }
    }

    public void onGPSLocationClicked(View view) {
    }
}

