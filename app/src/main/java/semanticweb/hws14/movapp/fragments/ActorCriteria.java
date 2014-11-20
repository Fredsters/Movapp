package semanticweb.hws14.movapp.fragments;

import android.content.Intent;
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


public class ActorCriteria extends Fragment implements AdapterView.OnItemSelectedListener {

    ActorCriteria that;
    int selectedFromDate;
    int selectedToDate;
    String selectedCity;
    String selectedState;

    EditText tfMovieName;
    Spinner spYearFrom;
    Spinner spYearTo;
    Spinner spCity;
    Spinner spState;

    Button btnMovie;
    Button btnYear ;
    Button btnRegion;

    Switch swMovie ;
    Switch swYear ;
    Switch swCity;
    Switch swState;

    boolean activeMovie = false;
    boolean activeYear = false;
    boolean activeState = false;
    boolean activeCity = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View movieCriteriaView = inflater.inflate(R.layout.fragment_actor_criteria, container, false);
        initCriteriaView(movieCriteriaView);
        return movieCriteriaView;
    }

    private void initCriteriaView(View view) {

        tfMovieName = (EditText) view.findViewById(R.id.tfActorName);

        btnMovie = (Button) view.findViewById(R.id.btnMovie);
        btnYear = (Button) view.findViewById(R.id.btnActorYear);
        btnRegion = (Button) view.findViewById(R.id.btnActorRegion);

        swMovie = (Switch) view.findViewById(R.id.swMovie);
        swYear = (Switch) view.findViewById(R.id.swActorYear);
        swCity = (Switch) view.findViewById(R.id.swActorCity);
        swState = (Switch) view.findViewById(R.id.swActorState);

        final View panelMovie = view.findViewById(R.id.panelMovie);
        panelMovie.setVisibility(View.VISIBLE);

        final View panelYear = view.findViewById(R.id.panelActorYear);
        panelYear.setVisibility(View.GONE);

        final View panelRegion = view.findViewById(R.id.panelActorRegion);
        panelRegion.setVisibility(View.GONE);

        btnMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panelMovie.setVisibility(View.VISIBLE);
                panelYear.setVisibility(View.GONE);
                panelRegion.setVisibility(View.GONE);
            }
        });

        btnYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panelMovie.setVisibility(View.GONE);
                panelYear.setVisibility(View.VISIBLE);
                panelRegion.setVisibility(View.GONE);
            }
        });

        btnRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panelMovie.setVisibility(View.GONE);
                panelYear.setVisibility(View.GONE);
                panelRegion.setVisibility(View.VISIBLE);
            }
        });

        setupSpinnerYearFrom(view);
        setupSpinnerYearTo(view);
        setupSpinnerCity(view);
        setupSpinnerState(view);

        swMovie.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activeMovie=b;
            }
        });

        swYear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activeYear = b;
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

        Button submitButton = (Button) view.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                that.submitSearch(v);
            }
        });

    }

    private void setupSpinnerYearFrom(View view){
        spYearFrom = (Spinner) view.findViewById(R.id.spActorYearFrom);
        ArrayAdapter<CharSequence> adapterFrom = ArrayAdapter.createFromResource(getActivity(), R.array.year_array, android.R.layout.simple_spinner_item);
        adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYearFrom.setAdapter(adapterFrom);
        spYearFrom.setSelection(6);
        spYearFrom.setOnItemSelectedListener(this);
    }

    private void setupSpinnerYearTo(View view){
        spYearTo = (Spinner) view.findViewById(R.id.spActorYearTo);
        ArrayAdapter<CharSequence> adapterTo = ArrayAdapter.createFromResource(getActivity(), R.array.year_array,android.R.layout.simple_spinner_item);
        adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYearTo.setAdapter(adapterTo);
        spYearTo.setOnItemSelectedListener(this);
    }

    private void setupSpinnerCity(View view){
        spCity = (Spinner) view.findViewById(R.id.spActorCity);
        ArrayAdapter<CharSequence> adapterCity = ArrayAdapter.createFromResource(getActivity(),R.array.city_array,android.R.layout.simple_spinner_item);
        adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCity.setAdapter(adapterCity);
        spCity.setOnItemSelectedListener(this);
    }

    private void setupSpinnerState(View view){
        spState = (Spinner) view.findViewById(R.id.spActorState);
        ArrayAdapter<CharSequence> adapterState = ArrayAdapter.createFromResource(getActivity(),R.array.state_array,android.R.layout.simple_spinner_item);
        adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spState.setAdapter(adapterState);
        spState.setOnItemSelectedListener(this);
    }


    public void submitSearch(View view) {
        HashMap<String, Object> criteria = new HashMap<String, Object>();

        String actorName = tfMovieName.getText().toString();

        if(activeMovie && !actorName.equals("")){
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

        if(activeCity) {
            criteria.put("city", cleanCityStateInput(selectedCity));
            criteria.put("isCity", true);
        } else {
            criteria.put("isCity", false);
        }
        if(activeState) {
            cleanCityStateInput(selectedState);
            criteria.put("state", cleanCityStateInput(selectedState));
            criteria.put("isState", true);
        } else {
            criteria.put("isState", false);
        }
        if( (activeMovie && !actorName.equals("")) || activeYear || activeCity || activeState){
           // Intent intent = new Intent(getActivity(), MovieList.class);
          //  intent.putExtra("criteria", criteria);
          //  startActivity(intent);
        }
        else{
            Toast.makeText(getActivity(), "Please choose at least one valid criteria!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item =  adapterView.getItemAtPosition(i).toString();

        if(adapterView.getId() == spYearFrom.getId()){
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


}
