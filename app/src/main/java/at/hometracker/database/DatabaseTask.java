package at.hometracker.database;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import at.hometracker.R;
import at.hometracker.shared.Constants;
import at.hometracker.utils.BiConsumer;
import at.hometracker.utils.Utils;

//AsyncTask<Params, Progress, Result>
public class DatabaseTask extends AsyncTask<Object, Void, String> {

    private final DatabaseMethod methodToExecute;

    private final AlertDialog progressDialog;
    private final BiConsumer<DatabaseTask, String> onFinishAction;

    public DatabaseMethod getMethodToExecute() {
        return methodToExecute;
    }

    public DatabaseTask(AppCompatActivity executingActivity, DatabaseMethod methodToExecute, BiConsumer<DatabaseTask, String> onFinishAction) {
        this.methodToExecute = methodToExecute;
        this.onFinishAction = onFinishAction;

        progressDialog = Utils.buildAlertDialog(executingActivity, R.layout.dialog_loading);
        progressDialog.setCancelable(false);
    }

    //executed on UI Thread
    @Override
    protected void onPreExecute() {
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Object... additionalParams) {
        MultipartPost post = new MultipartPost();
        try {
            post.addFormField("authkey", Constants.PHP_AUTHKEY);
            post.addFormField("method", methodToExecute.getPhpMethodName());

            for (int i = 0; i < additionalParams.length; i++) {
                Object param = additionalParams[i];

                if (param instanceof Integer || param instanceof String) {
                    post.addFormField("param" + i, param.toString());
                }
                else if (param instanceof InputStream) {
                    post.addFilePart("param" + i, (InputStream) param);
                }
                else {
                    throw new IllegalArgumentException("Invalid (additional) param type for DatabaseTask!");
                }
            }

            try {   //TODO: remove simulated waiting time
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
        Log.i("db", methodToExecute.name() + " result = {\"" + result + "\"}");
        if (onFinishAction != null)
            onFinishAction.accept(this, result);
        progressDialog.dismiss();
    }
}
