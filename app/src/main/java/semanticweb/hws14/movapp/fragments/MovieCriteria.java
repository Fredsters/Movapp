package semanticweb.hws14.movapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import semanticweb.hws14.activities.R;
import semanticweb.hws14.movapp.activities.MovieList;
import semanticweb.hws14.movapp.helper.InputCleaner;
import semanticweb.hws14.movapp.model.TimePeriod;

import static semanticweb.hws14.movapp.helper.InputCleaner.cleanCityStateInput;

//This is a fragment
//This is the movie tab on the criteria activity
public class MovieCriteria extends Fragment implements AdapterView.OnItemSelectedListener{

    //Some class variables
    private MovieCriteria that;
    private int selectedFromDate;
    private int selectedToDate;
    private String selectedGenre;
    private String selectedCity;
    private String selectedState;

    private String regionKind;

    private EditText tfActorName;
    private EditText tfDirectorName;
    private EditText tfPartName;
    private Spinner spYearFrom;
    private Spinner spYearTo;
    private Spinner spGenre;
    private Spinner spCity;
    private Spinner spState;

    private int spYearFromCount;
    private int spYearToCount;
    private int spGenreCount;
    private int spCityCount;
    private int spStateCount;

    private Button btnActor;
    private Button btnYear ;
    private Button btnGenre ;
    private Button btnPartName;
    private Button btnRegion;

    private Switch swActor ;
    private Switch swYear ;
    private Switch swGenre ;
    private Switch swDirector ;
    private Switch swCity;
    private Switch swState;
    private Switch swPartName;

    private boolean activeActor = false;
    private boolean activeYear = false;
    private boolean activeGenre = false;
    private boolean activeDirector = false;
    private boolean activeState = false;
    private boolean activeCity = false;
    private boolean activePartName = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        that = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View movieCriteriaView = inflater.inflate(R.layout.fragment_movie_criteria, container, false);
        initCriteriaView(movieCriteriaView);
        return movieCriteriaView;
    }

    private void initCriteriaView(View view){

        //init all the components and add the listeners

        tfActorName = (EditText) view.findViewById(R.id.tfActorName);
        tfDirectorName = (EditText) view.findViewById(R.id.tfDirectorName);
        tfPartName = (EditText) view.findViewById(R.id.tfTitleName);

        btnActor = (Button) view.findViewById(R.id.btnActor);
        btnYear = (Button) view.findViewById(R.id.btnYear);
        btnGenre = (Button) view.findViewById(R.id.btnGenre);
        btnPartName = (Button) view.findViewById(R.id.btnTitle);
        btnRegion = (Button) view.findViewById(R.id.btnRegion);

        swActor = (Switch) view.findViewById(R.id.swActor);
        swYear = (Switch) view.findViewById(R.id.swYear);
        swGenre = (Switch) view.findViewById(R.id.swGenre);
        swDirector = (Switch) view.findViewById(R.id.swDirector);
        swCity = (Switch) view.findViewById(R.id.swCity);
        swState = (Switch) view.findViewById(R.id.swState);
        swPartName = (Switch) view.findViewById(R.id.swTitle);

        regionKind = "set";

        final View panelActor = view.findViewById(R.id.panelActor);
        panelActor.setVisibility(View.VISIBLE);

        final View panelYear = view.findViewById(R.id.panelYear);
        panelYear.setVisibility(View.GONE);

        final View panelGenre = view.findViewById(R.id.panelGenre);
        panelGenre.setVisibility(View.GONE);

        final View panelPartName = view.findViewById(R.id.panelTitle);
        panelPartName.setVisibility(View.GONE);

        final View panelRegion = view.findViewById(R.id.panelRegion);
        panelRegion.setVisibility(View.GONE);

        btnActor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panelActor.setVisibility(View.VISIBLE);
                panelYear.setVisibility(View.GONE);
                panelGenre.setVisibility(View.GONE);
                panelPartName.setVisibility(View.GONE);
                panelRegion.setVisibility(View.GONE);
            }
        });

        btnYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panelActor.setVisibility(View.GONE);
                panelYear.setVisibility(View.VISIBLE);
                panelGenre.setVisibility(View.GONE);
                panelPartName.setVisibility(View.GONE);
                panelRegion.setVisibility(View.GONE);
            }
        });

        btnGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panelActor.setVisibility(View.GONE);
                panelYear.setVisibility(View.GONE);
                panelGenre.setVisibility(View.VISIBLE);
                panelPartName.setVisibility(View.GONE);
                panelRegion.setVisibility(View.GONE);
            }
        });

        btnPartName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panelActor.setVisibility(View.GONE);
                panelYear.setVisibility(View.GONE);
                panelGenre.setVisibility(View.GONE);
                panelPartName.setVisibility(View.VISIBLE);
                panelRegion.setVisibility(View.GONE);
            }
        });

        btnRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panelActor.setVisibility(View.GONE);
                panelYear.setVisibility(View.GONE);
                panelGenre.setVisibility(View.GONE);
                panelPartName.setVisibility(View.GONE);
                panelRegion.setVisibility(View.VISIBLE);
            }
        });

        setTfKeyListener(tfActorName, swActor);
        setTfKeyListener(tfDirectorName, swDirector);
        setTfKeyListener(tfPartName, swPartName);

        setTfFocusChangeListener(tfActorName, swActor, btnActor);
        setTfFocusChangeListener(tfDirectorName, swDirector, btnActor);
        setTfFocusChangeListener(tfPartName, swPartName, btnPartName);

        setupSpinnerYearFrom(view);
        setupSpinnerYearTo(view);
        setupSpinnerGenre(view);
        setupSpinnerCity(view);
        setupSpinnerState(view);


        swActor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activeActor=b;
                if(b && tfActorName.getText().toString().equals("")) {
                    swActor.setChecked(false);
                } else {
                    setButtonColorText(swActor, btnActor, b || activeDirector);
                    if (activeDirector && !b) {
                        btnActor.setText("Director: " + tfDirectorName.getText().toString());
                    }
                }

            }
        });
        swDirector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activeDirector = b;
                if(b && tfDirectorName.getText().toString().equals("")) {
                    swDirector.setChecked(false);
                } else {
                    setButtonColorText(swDirector, btnActor, b || activeActor);
                    if (activeActor && !b) {
                        btnActor.setText("Actor: " + tfActorName.getText().toString());
                    }
                }

            }
        });

        swYear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activeYear = b;
                setButtonColorText(swYear, btnYear, b);
            }
        });

        swGenre.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activeGenre = b;
                setButtonColorText(swGenre, btnGenre, b);
            }
        });

        swPartName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activePartName = b;
                if(b && tfPartName.getText().toString().equals("")) {
                    swPartName.setChecked(false);
                } else {
                    setButtonColorText(swPartName, btnPartName, b);
                }
            }
        });

        swCity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activeCity = b;
                if(b) {
                    swState.setChecked(false);
                }
                setButtonColorText(swCity, btnRegion, b || activeState);
            }
        });

        swState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activeState = b;
                if(b) {
                    swCity.setChecked(false);
                }
                setButtonColorText(swState, btnRegion, b || activeCity);
            }
        });

        RadioButton radioButtonSet = (RadioButton) view.findViewById(R.id.radio_set);
        radioButtonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                that.onRadioButtonClicked(v);
            }
        });
        RadioButton radioButtonShot = (RadioButton) view.findViewById(R.id.radio_shot);
        radioButtonShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                that.onRadioButtonClicked(v);
            }
        });

        Button submitButton = (Button) view.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                that.submitSearch(v);
            }
        });
    }

    private void setupSpinnerYearFrom(View view){
        spYearFrom = (Spinner) view.findViewById(R.id.spYearFrom);
        ArrayAdapter<CharSequence> adapterFrom = ArrayAdapter.createFromResource(getActivity(), R.array.year_array, android.R.layout.simple_spinner_item);
        adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYearFrom.setAdapter(adapterFrom);
        spYearFrom.setSelection(20);
        spYearFrom.setOnItemSelectedListener(this);
    }

    private void setupSpinnerYearTo(View view){
        spYearTo = (Spinner) view.findViewById(R.id.spYearTo);
        ArrayAdapter<CharSequence> adapterTo = ArrayAdapter.createFromResource(getActivity(), R.array.year_array,android.R.layout.simple_spinner_item);
        adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYearTo.setAdapter(adapterTo);
        spYearTo.setOnItemSelectedListener(this);
    }

    private void setupSpinnerGenre(View view){
        spGenre = (Spinner) view.findViewById(R.id.spGenre);
        ArrayAdapter<CharSequence> adapterGenre = ArrayAdapter.createFromResource(getActivity(),R.array.genre_array,android.R.layout.simple_spinner_item);
        adapterGenre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGenre.setAdapter(adapterGenre);
        spGenre.setOnItemSelectedListener(this);
    }

    private void setupSpinnerCity(View view){
        spCity = (Spinner) view.findViewById(R.id.spCity);
        ArrayAdapter<CharSequence> adapterCity = ArrayAdapter.createFromResource(getActivity(),R.array.city_array,android.R.layout.simple_spinner_item);
        adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCity.setAdapter(adapterCity);
        spCity.setOnItemSelectedListener(this);
    }

    private void setupSpinnerState(View view){
        spState = (Spinner) view.findViewById(R.id.spState);
        ArrayAdapter<CharSequence> adapterState = ArrayAdapter.createFromResource(getActivity(),R.array.state_array,android.R.layout.simple_spinner_item);
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spState.setAdapter(adapterState);
        spState.setOnItemSelectedListener(this);
    }



    public void submitSearch(View view) {
        HashMap<String, Object> criteria = new HashMap<String, Object>();

        String actorName = tfActorName.getText().toString();
        String directorName = tfDirectorName.getText().toString();
        String partName = tfPartName.getText().toString();

        if(activeActor && !actorName.equals("")){
            actorName = InputCleaner.cleanName(actorName);
            criteria.put("actorName", actorName);
            criteria.put("isActor", true);
        } else{
            criteria.put("isActor", false);
        }

        if(activePartName && !partName.equals("")){
            partName = InputCleaner.cleanName(partName);
            criteria.put("partName", partName);
            criteria.put("isPartName", true);
        } else{
            criteria.put("isPartName", false);
        }

        if(activeYear){
            int dateFrom = selectedFromDate;
            int dateTo = selectedToDate;
            criteria.put("timePeriod", new TimePeriod(dateFrom, dateTo));
            criteria.put("isTime", true);
        } else{
            criteria.put("isTime", false);
        }

        if(activeGenre){
            String genre = selectedGenre;
            criteria.put("genreName", genre);
            criteria.put("isGenre", true);
        } else{
            criteria.put("isGenre", false);
        }

        if(activeDirector && !directorName.equals("")){
            directorName = InputCleaner.cleanName(directorName);
            criteria.put("directorName", directorName);
            criteria.put("isDirector", true);
        } else {
            criteria.put("isDirector", false);
        }

        if(activeCity) {
            String city = cleanCityStateInput(selectedCity);
            String setShot = regionKind;
            criteria.put("city", city);
            criteria.put("isCity", true);
            criteria.put("regionKind", setShot);
        } else {
            criteria.put("isCity", false);
        }
        if(activeState) {
            String state = cleanCityStateInput(selectedState);
            String setShot = regionKind;
            criteria.put("state", state);
            criteria.put("isState", true);
            criteria.put("regionKind", setShot);
        } else {
            criteria.put("isState", false);
        }
        //Sends the criteria hashmap via intent to movieList activity
        if( (activeActor && !actorName.equals("")) || activeYear || activeGenre || (activeDirector && !directorName.equals("")) || activeCity || activeState || (activePartName && !partName.equals(""))){
            Activity criteriaActivity = getActivity();
            Intent intent = new Intent(criteriaActivity, MovieList.class);
            intent.putExtra("criteria", criteria);
            startActivity(intent);
        }
        else{
            Toast.makeText(getActivity(), "Please choose at least one valid criteria!", Toast.LENGTH_SHORT).show();
        }
    }

    //Spinner select event
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item =  adapterView.getItemAtPosition(i).toString();

        if(adapterView.getId() == spGenre.getId()){
            selectedGenre=item;
            if(spGenreCount>0){
                swGenre.setChecked(true);
                setButtonColorText(swGenre, btnGenre, true);
            }
            spGenreCount++;
        }
        else if(adapterView.getId() == spYearFrom.getId()){
            selectedFromDate=Integer.parseInt(item);
            if(spYearFromCount>0) {
                swYear.setChecked(true);
                setButtonColorText(swYear, btnYear, true);
            }
            spYearFromCount++;
        }
        else if (adapterView.getId() == spYearTo.getId()){
            selectedToDate =Integer.parseInt(item);
            if(spYearToCount>0) {
                swYear.setChecked(true);
                setButtonColorText(swYear, btnYear, true);
            }
            spYearToCount++;
        }
        else if(adapterView.getId() == spCity.getId()){
            selectedCity=item;

            if(spCityCount>0) {
                swCity.setChecked(true);
                setButtonColorText(swCity, btnRegion, true);
            }
            spCityCount++;
        }
        else if(adapterView.getId() == spState.getId()){
            selectedState=item;
            if(spStateCount>0) {
                swState.setChecked(true);
                setButtonColorText(swState, btnRegion, true);
            }
            spStateCount++;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //choose between movie shot and movie set
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

    //use location data
    public void setGPSLocation(String city) {

        String[] cityArray = getResources().getStringArray(R.array.city_array);
        int position = -1;
        for(int i = 0 ; i < cityArray.length; i++) {
            if(city.equals(cityArray[i])) {
                position = i;
                break;
            }
        }
        if(position == -1) {
            Toast.makeText(getActivity(), "This city is not usable", Toast.LENGTH_SHORT).show();
        } else {
            spCity.setSelection(position);
            selectedCity = spCity.getSelectedItem().toString();
        }
        swCity.setChecked(true);
    }

    //EditText listener

    private void setTfKeyListener(final EditText tf, final Switch sw ) {
        tf.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (tf.isDirty())
                        sw.setChecked(true);
                }
                return false;
            }
        });
    }
    //EditText listener
    private void setTfFocusChangeListener(final EditText tf, final Switch sw, final Button btn ) {
        tf.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && sw.isChecked() && "".equals(tf.getText().toString())) {
                    setButtonColorText(sw, btn, false);
                    sw.setChecked(false);
                } else if (!hasFocus && sw.isChecked()) {
                    setButtonColorText(sw, btn, true);
                }

            }
        });
    }

    //coloring and changing of texts ob the buttons
    private void setButtonColorText(Switch sw, Button btn, boolean sub) {
        if(sub) {
            btn.setBackground(that.getResources().getDrawable(R.drawable.button_background_submit));
            if(sw.equals(swActor) && activeDirector || sw.equals(swDirector) && activeActor) {
                btn.setText("A: "+ tfActorName.getText().toString() + "  D: "+ tfDirectorName.getText().toString());
            } else if(sw.equals(swActor)  && !activeDirector) {
                btn.setText("Actor: "+ tfActorName.getText().toString());
            } else if(sw.equals(swDirector)  && !activeActor) {
                btn.setText("Director: "+ tfDirectorName.getText().toString());
            } else if (sw.equals(swGenre)) {
                btn.setText("Genre: "+ selectedGenre);
            } else if(sw.equals(swYear)) {
                btn.setText("Release year: "+ selectedFromDate +" - "+ selectedToDate);
            } else if(sw.equals(swPartName)) {
                btn.setText("Part of Title: "+ tfPartName.getText().toString());
            } else if(sw.equals(swCity)) {
                btn.setText("City: "+ selectedCity);
            }else if(sw.equals(swState)) {
                btn.setText("Country: "+ selectedState);
            }
        } else {
            btn.setBackground(that.getResources().getDrawable(R.drawable.button_background_accordion));
            if(btn.equals(btnActor)) {
                btn.setText("Actor/Director");
            } else if (btn.equals(btnGenre)) {
                btn.setText("Genre");
            } else if(btn.equals(btnPartName)) {
                btn.setText("Part of Title");
            } else if(btn.equals(btnYear)) {
                btn.setText("Release year");
            } else if(btn.equals(btnRegion)) {
                btn.setText("Region");
            }
        }
    }

}
