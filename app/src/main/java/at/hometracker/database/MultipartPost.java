package at.hometracker.database;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import at.hometracker.shared.Constants;

import static at.hometracker.shared.Constants.crlf;

public class MultipartPost {

    private static final String boundary =  "*****";
    private static final String twoHyphens = "--";

    private final HttpURLConnection httpConn;
    private final DataOutputStream request;

    /**
     * initialize new HTTP POST request with content type set to "multipart/form-data"
     */
    public MultipartPost() {
        try {
            URL url = new URL(Constants.PHP_URL);

            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);

            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            request = new DataOutputStream(httpConn.getOutputStream());
            //request = new DataOutputStream(System.out);               //for debugging posts
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("MultipartPost constructor failed!");
        }
    }

    /**
     * Adds a form field to the request.
     *
     * @param name  field name
     * @param value field value
     */
    public void addFormField(String name, String value) throws IOException {
        request.writeBytes(twoHyphens + boundary + crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"" + name + "\""+ crlf);
        request.writeBytes("Content-Type: text/plain; charset=UTF-8" + crlf);
        request.writeBytes(crlf);
        request.writeBytes(value + crlf);
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName name attribute
     * @param file the file to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, InputStream file) throws IOException {
        request.writeBytes(twoHyphens + boundary + crlf);

        request.writeBytes(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"picture\"%s", fieldName, crlf));
        request.writeBytes(crlf);

        int _byte;
        while ((_byte = file.read()) != -1) {
            request.write(_byte);
        }
        request.writeBytes(crlf);
    }

    /**
     * Completes the request and receives response from the server.<br>
     * Do not use this MultipartPost instance anymore after calling this method.
     *
     * @return a (multi-line) String representing the servers response.
     * @throws IOException
     */
    public String finish() throws IOException {
        request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
        request.flush();
        request.close();

        // checks server's status code first
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));

            String line;
            StringBuilder responseBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                responseBuilder.append(line).append(crlf);
            }

            String ret = responseBuilder.toString();
            if (ret.endsWith(crlf))
                ret = ret.substring(0, ret.length() - crlf.length());   //delete last "crlf"

            responseStreamReader.close();
            httpConn.disconnect();
            return ret;
        } else {
            httpConn.disconnect();
            throw new IOException("Server returned non-OK status: " + status);
        }
    }

    public void cancel() {
        try {
            request.close();
            httpConn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}