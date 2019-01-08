package at.hometracker.database;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import at.hometracker.app.Constants;

//AsyncTask<Params, Progress, Result>
public class DatabaseTask extends AsyncTask<Object, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private final TextView textView;

    private final DatabaseMethod methodToExecute;

    public DatabaseTask(DatabaseMethod methodToExecute, TextView textViewToUpdate) {
        this.methodToExecute = methodToExecute;
        this.textView = textViewToUpdate;
    }

    @Override
    protected String doInBackground(Object... additionalParams) {
        MultipartPost post = new MultipartPost();
        try {
            post.addFormField("authkey", Constants.PHP_AUTHKEY);
            post.addFormField("method", methodToExecute.getPhpMethodName());

            for (int i = 0; i < additionalParams.length; i++) {
                Object param = additionalParams[i];

                if (param instanceof String) {
                    post.addFormField("param" + i, param.toString());
                }
                else if (param instanceof InputStream) {
                    post.addFilePart("param" + i, (InputStream) param);
                }
                else {
                    throw new IllegalArgumentException("Invalid (additional) param type for DatabaseTask!");
                }
            }

            return post.finish();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //executed on UI Thread
    @Override
    protected void onPostExecute(String result) {
        Log.i("db", methodToExecute.name() + " result = " + result);
        if (textView != null) {
            textView.setText(result.trim().replace('|', '\n'));
        }
    }
}
