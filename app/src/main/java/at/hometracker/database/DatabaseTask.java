package at.hometracker.database;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import at.hometracker.app.Constants;

//AsyncTask<Params, Progress, Result>
public class DatabaseTask extends AsyncTask<String, Void, String> {

    private final DatabaseMethod methodToExecute;
    private URLConnection conn;

    public DatabaseTask(DatabaseMethod methodToExecute) {
        this.methodToExecute = methodToExecute;
    }

    @Override
    protected String doInBackground(String... additionalParams) {
        try {
            openConnection();
            sendPost(additionalParams);
            return readResult();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Opens a connection. ({@link URLConnection}).
     * @throws IOException
     */
    private void openConnection() throws IOException {
        //Address of PHP interface that connects to MySQL database
        URL url = new URL(Constants.PHP_URL);
        conn = url.openConnection();
        conn.setDoOutput(true);
    }

    private void sendPost(String... params) throws IOException {
        //postBuilder: authkey = <authkey> & method = <phpMethodName>
        StringBuilder postBuilder = new StringBuilder();

        postBuilder.append(URLEncoder.encode("authkey", "UTF-8"));
        postBuilder.append(Constants.HTTPPOST_KEYVALUE_SEPARATOR);
        postBuilder.append(URLEncoder.encode(Constants.PHP_AUTHKEY, "UTF-8"));

        postBuilder.append(Constants.HTTPPOST_PARAM_SEPARATOR);

        postBuilder.append(URLEncoder.encode("method", "UTF-8"));
        postBuilder.append(Constants.HTTPPOST_KEYVALUE_SEPARATOR);
        postBuilder.append(URLEncoder.encode(methodToExecute.getPhpMethodName(), "UTF-8"));

        //extract params
        for (int i = 0; i < params.length; i++) {
            postBuilder.append(Constants.HTTPPOST_PARAM_SEPARATOR);
            postBuilder.append(URLEncoder.encode("param" + i, "UTF-8"));
            postBuilder.append(Constants.HTTPPOST_KEYVALUE_SEPARATOR);
            postBuilder.append(URLEncoder.encode(params[i], "UTF-8"));
        }

        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(postBuilder.toString());
        writer.flush();
        writer.close();
    }

    /**
     * Reads result from opened connection.
     * @return A String with the results.
     * @throws IOException
     */
    private String readResult()throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder resultBuilder = new StringBuilder();

        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            resultBuilder.append(line);
        }

        reader.close();
        return resultBuilder.toString();
    }

}
