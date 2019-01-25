package at.hometracker.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import at.hometracker.R;

public class GridViewOneSingleItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridview_singleitem);

        TextView txtInfo = (TextView)findViewById(R.id.txtInfo);
        if(getIntent() != null)
        {
            String info = getIntent().getStringExtra("info");
            txtInfo.setText(info);
        }
    }
}
