package semanticweb.hws14.movapp.fragments;

import android.app.Activity;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import semanticweb.hws14.movapp.activities.MovieList;
import semanticweb.hws14.movapp.helper.InputCleaner;
import semanticweb.hws14.movapp.model.TimePeriod;

import static semanticweb.hws14.movapp.helper.InputCleaner.cleanCityStateInput;

public class MovieCriteria extends Fragment implements AdapterView.OnItemSelectedListener{



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

    private void initCriteriaView(View view){

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

        setupSpinnerYearFrom(view);
        setupSpinnerYearTo(view);
        setupSpinnerGenre(view);
        setupSpinnerCity(view);
        setupSpinnerState(view);

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

        swPartName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activePartName = b;
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
        spYearFrom.setSelection(6);
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

    public void setGPSLocation(String city) {
        swCity.setChecked(true);
        String[] cityArray = getResources().getStringArray(R.array.city_array);
        int position = 0;
        for(int i = 0 ; i < cityArray.length; i++) {
            if(city.equals(cityArray[i])) {
                position = i;
                break;
            }
        }
        spCity.setSelection(position);
    }

}
