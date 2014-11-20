package semanticweb.hws14.movapp.fragments;

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



    MovieCriteria that;
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

/*
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;



    public static MovieCriteria newInstance(String param1, String param2) {
        MovieCriteria fragment = new MovieCriteria();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MovieCriteria() {
        // Required empty public constructor
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
*/


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
            Intent intent = new Intent(getActivity(), MovieList.class);
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

        btnActor = (Button) view.findViewById(R.id.btnActor);
        btnYear = (Button) view.findViewById(R.id.btnYear);
        btnGenre = (Button) view.findViewById(R.id.btnGenre);
        btnDirector = (Button) view.findViewById(R.id.btnDirector);
        btnRegion = (Button) view.findViewById(R.id.btnRegion);

        swActor = (Switch) view.findViewById(R.id.swActor);
        swYear = (Switch) view.findViewById(R.id.swYear);
        swGenre = (Switch) view.findViewById(R.id.swGenre);
        swDirector = (Switch) view.findViewById(R.id.swDirector);
        swCity = (Switch) view.findViewById(R.id.swCity);
        swState = (Switch) view.findViewById(R.id.swState);

        regionKind = "set";

        final View panelActor = view.findViewById(R.id.panelActor);
        panelActor.setVisibility(View.VISIBLE);

        final View panelYear = view.findViewById(R.id.panelYear);
        panelYear.setVisibility(View.GONE);

        final View panelGenre = view.findViewById(R.id.panelGenre);
        panelGenre.setVisibility(View.GONE);

        final View panelDirector = view.findViewById(R.id.panelDirector);
        panelDirector.setVisibility(View.GONE);

        final View panelRegion = view.findViewById(R.id.panelRegion);
        panelRegion.setVisibility(View.GONE);

        btnActor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panelActor.setVisibility(View.VISIBLE);
                panelYear.setVisibility(View.GONE);
                panelGenre.setVisibility(View.GONE);
                panelDirector.setVisibility(View.GONE);
                panelRegion.setVisibility(View.GONE);
            }
        });

        btnYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panelActor.setVisibility(View.GONE);
                panelYear.setVisibility(View.VISIBLE);
                panelGenre.setVisibility(View.GONE);
                panelDirector.setVisibility(View.GONE);
                panelRegion.setVisibility(View.GONE);
            }
        });

        btnGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panelActor.setVisibility(View.GONE);
                panelYear.setVisibility(View.GONE);
                panelGenre.setVisibility(View.VISIBLE);
                panelDirector.setVisibility(View.GONE);
                panelRegion.setVisibility(View.GONE);
            }
        });

        btnDirector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panelActor.setVisibility(View.GONE);
                panelYear.setVisibility(View.GONE);
                panelGenre.setVisibility(View.GONE);
                panelDirector.setVisibility(View.VISIBLE);
                panelRegion.setVisibility(View.GONE);
            }
        });

        btnRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panelActor.setVisibility(View.GONE);
                panelYear.setVisibility(View.GONE);
                panelGenre.setVisibility(View.GONE);
                panelDirector.setVisibility(View.GONE);
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

}
