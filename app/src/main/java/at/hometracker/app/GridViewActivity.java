package at.hometracker.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import at.hometracker.shared.ImageAdapter;
import at.hometracker.R;

public class GridViewActivity  extends AppCompatActivity {
    GridView grid;
    String text[] = {"testimg 1","testimg 2","testimg 3","testimg 4","testimg 5"};
    int image[] = {R.drawable.ic_testimg1, R.drawable.ic_testimg2, R.drawable.ic_testimg3, R.drawable.ic_testimg4, R.drawable.ic_testimg5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridview);

        grid = (GridView)findViewById(R.id.simpleGrid);
        grid.setAdapter(new ImageAdapter(this,image,text));

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),text[position],Toast.LENGTH_LONG).show();
            }
        });
    }
}
