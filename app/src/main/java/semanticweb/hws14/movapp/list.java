package semanticweb.hws14.movapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class list extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();


        ArrayList<HashMap<String,String>> result = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("result");


        ListView listView = (ListView) findViewById(R.id.resultList);

        // Convert ArrayList to array
       // HashMap<String, String>[] lv_arr = (HashMap<String,String>[]) result.toArray();
        ArrayAdapter adapter = new ArrayAdapter<HashMap<String,String>>(list.this,android.R.layout.simple_list_item_1, result);
        listView.setAdapter(adapter);
        //TODO: Cach the data or just query if the list does not have items
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);
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
