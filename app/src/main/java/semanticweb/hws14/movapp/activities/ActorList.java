package semanticweb.hws14.movapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


import semanticweb.hws14.activities.R;

public class ActorList extends Activity {

    protected static String staticCity;
    protected ArrayAdapter<String> alAdapter;
    private Activity that = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_actor_list);


        Intent intent = getIntent();
        ListView listView = (ListView) findViewById(R.id.actorList);
        ArrayList<String> actorList = new ArrayList<String>();
        String city = "";
        if(intent.hasExtra("actorList")) {
            actorList = intent.getStringArrayListExtra("actorList");
        } else {
            city = intent.getStringExtra("city");
        }




        if(intent.hasExtra("actorList")) {
            this.alAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, actorList);
            listView.setAdapter(alAdapter);
        } else if(city.equals(staticCity)){
            this.alAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, actorList);
            listView.setAdapter(alAdapter);
        } else {
            staticCity = city;
            // queryForMovies q = new queryForMovies();
            //  q.execute(criteria);
            this.alAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, actorList);
            listView.setAdapter(alAdapter);
        }

        final ArrayList<String> finalActorList = actorList;

        AdapterView.OnItemClickListener clickListen = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(that, ActorDetail.class);
                String actorName = finalActorList.get(position);
                intent.putExtra("actorName", actorName);
                startActivity(intent);
            }
        };
        listView.setOnItemClickListener(clickListen);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actor_list, menu);
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
}
