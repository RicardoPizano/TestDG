package com.claresti.testdg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText user_id;
    private EditText answer_id;
    private EditText number_of_question;
    private TextView result;
    private Button send;
    private String url = "http://148.220.211.42:8000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_id = (EditText)findViewById(R.id.user_id);
        answer_id = (EditText)findViewById(R.id.answer_id);
        number_of_question = (EditText)findViewById(R.id.number_of_question);
        result = (TextView)findViewById(R.id.txt_result);
        send = (Button)findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_id.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Falta ingresar el id del usuario", Toast.LENGTH_SHORT).show();
                }else if(answer_id.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Falta ingresarel id de la respuesta", Toast.LENGTH_SHORT).show();
                }else if(number_of_question.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Falta ingresar el numero de la seccion de la pregunta", Toast.LENGTH_SHORT).show();
                }else {
                    setAnswer();
                }

            }
        });

    }


    private void setAnswer() {
        result.setText("Cargando...");
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id.getText().toString());
        params.put("answer_id", answer_id.getText().toString());
        params.put("section_id", number_of_question.getText().toString());
        final Gson gson = new Gson();
        JsonObjectRequest request;
        VolleySingleton.getInstance(MainActivity.this).
                addToRequestQueue(
                        request = new JsonObjectRequest(
                                Request.Method.POST,
                                this.url + "/onboard/api/set_answer/",
                                new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            String res = response.getString("res");
                                            switch(res){
                                                case "1":
                                                    result.setText("La operacion fue correcta");
                                                    break;
                                                case "0":
                                                    result.setText("La conexion fue correcto pero la respuesta fue 0\n" + response.toString());
                                                    break;
                                                default:
                                                    result.setText(response.toString());
                                                    break;
                                            }
                                        }catch(JSONException json){
                                            Log.e("JSON", json.toString());
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }
                        )
                );
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }
}
