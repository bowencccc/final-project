package com.example.pharzie.calculatorv2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private static final String MODE_MATH = "Go Calculation.";
    private static final String MODE_CALC = "Do Calculus.";
    private static final String MODE_SIMP = "Simplify Expressions.";
    /**
     * The tag of the activity.
     */
    private static final String TAG = "Calculator";
    /**
     * Request queue for our network requests.
     */
    private static RequestQueue requestQueue;
    private String PicUrlGoogle = "http://chart.apis.google.com/chart?cht=tx&chl=";
    private String PicUrlCogs = "http://latex.codecogs.com/gif.latex?";
    private Bitmap pngBM;
    private URL Url;
    private boolean finishFlag = false;
    /**
     * The status of the calculator.
     * Innitially set Go Calculation.
     */
    private static String currentMode = MODE_MATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setFocusable(false);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setIcon(R.mipmap.ic_launcher);
                dialog.setTitle(R.string.DialogTitle);
                dialog.setMessage(R.string.DialogContent);
                dialog.setPositiveButton(R.string.PosReplytoDlg ,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                dialog.show();
                return true;
            }
        });
        */
        /**
         * The input field where we get a specific arithmetic expression.
         */
        final EditText inputField = findViewById(R.id.expression);
        inputField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputField.setText("");
            }
        });
        final ImageView mathExpr = findViewById(R.id.mathExpr);
        final Button showMathExpr = findViewById(R.id.show);
        showMathExpr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Url = new URL(PicUrlGoogle + inputField.getText().toString()); // 转换字符串
                    new MyDownloadTask().execute();
                    while(!finishFlag) {}
                    mathExpr.setImageBitmap(pngBM);
                    finishFlag = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        /**
         * The fairy.
         */
        final Fairy pipi = new Fairy(inputField, (TextView) findViewById(R.id.board));
        /**
         * The radio group, which shows up when the current mode is Go Calc.
         * Contains two radio buttons: derive and integrate.
         * Innitially set invisible.
         */
        final RadioGroup radioGroup = findViewById(R.id.radio_group);
        radioGroup.setVisibility(View.INVISIBLE);
        /**
         * The button to calculate the value or get the integral/derivative/simplified form of the expression.
         */
        final Button go = findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button clicked.");
                Log.d(TAG, "Current mode: " + currentMode);
                String finExpr = "";
                String iniExpr = inputField.getText().toString();
                try {
                    Log.d(TAG, "Mode " + currentMode);
                    if (currentMode.equals(MODE_MATH)) {
                        finExpr = Operator.go(new Expression(iniExpr).getpostExpr());
                    } else if (currentMode.equals(MODE_CALC)) {
                        requestOperation(((RadioButton)
                                findViewById(((RadioGroup) findViewById(R.id.radio_group))
                                        .getCheckedRadioButtonId())).getText().toString()
                                .toLowerCase());
                        return;
                    } else if (currentMode.equals(MODE_SIMP)) {
                        requestOperation("simplify");
                        return;
                    }
                    pipi.update();
                    final TextView result = findViewById(R.id.result);
                    result.setText(finExpr);
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        });
        /**
         * The board where the fairy talks to us... Well it's not an AI.
         */
        final TextView board = findViewById(R.id.board);
        board.setTextSize(20.0f);
        /**
         * The result.
         */
        final TextView result = findViewById(R.id.result);
        result.setTextSize(32.0f);
        /**
         * Set up the sensor detector.(Don't know whether it works.)
         */
        new SensorDetector().setOnShakeListener(new ShakeListener());
        /**
         * Set up the mode.
         * There are three modes available: common calculation; integral/derivative;
         */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.more);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.menu_modes, null);
                mBuilder.setTitle("Choose Mode");
                final Spinner mSpinner = (Spinner) mView.findViewById(R.id.modes_spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_list_item_1,
                        getResources().getStringArray(R.array.modes));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(adapter);
                //Positive button.
                mBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mSpinner.getSelectedItem().toString().equals(MODE_MATH)) {
                            currentMode = MODE_MATH;
                            findViewById(R.id.radio_group).setVisibility(View.INVISIBLE);
                            mSpinner.setSelection(0);
                        } else if (mSpinner.getSelectedItem().toString().equals(MODE_CALC)) {
                            currentMode = MODE_CALC;
                            findViewById(R.id.radio_group).setVisibility(View.VISIBLE);
                            mSpinner.setSelection(1);
                        } else if (mSpinner.getSelectedItem().toString().equals(MODE_SIMP)) {
                            currentMode = MODE_SIMP;
                            findViewById(R.id.radio_group).setVisibility(View.INVISIBLE);
                            mSpinner.setSelection(2);
                        }
                        //Show the mode you've chosen.
                        Snackbar.make(view, "Now you've set it to the \""
                                + currentMode + "\" mode.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        dialog.dismiss();
                    }
                });
                mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });
    }

    /**
     * Request an operation by a given operator. Done by the web API.
     *
     * @param oper The given operator.
     * @throws Exception In case something is wrong.
     */
    private void requestOperation(final String oper) throws Exception {
        final EditText inputField = findViewById(R.id.expression);
        String iniExpr = new Polynomial(inputField.getText().toString()).toString();
        Log.d(TAG, "Expression get: " + iniExpr);
        String url = "https://newton.now.sh/" + oper + "/" + iniExpr;
        JsonObjectRequest requestGet = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String r = response.get("result").toString();
                            Log.d(TAG, "Case: " + oper + "; Result:" + r);
                            final TextView result = findViewById(R.id.result);
                            result.setText(r);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(requestGet);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
    public class Fairy {
        private TextView input;
        private TextView output;
        private String lastInput = "";
        public Fairy(TextView setInput, TextView setOutput) {
            input = setInput;
            output = setOutput;
        }
        public void update() {
            String inputString = input.getText().toString();
            if (inputString.length() < 6) {
                if (lastInput.length() == 0 || lastInput.length() > 5) {
                    output.setText(getResources().getStringArray
                            (R.array.response_to_easy_questions)[new Random().nextInt(3)]);
                } else {
                    output.setText(getResources().getStringArray
                            (R.array.response_again_to_easy_questions)[new Random().nextInt(3)]);
                }
            } else if (inputString.length() > 12) {
                output.setText(getResources().getStringArray
                        (R.array.response_to_hard_questions)[new Random().nextInt(3)]);
            } else {
                output.setText(getResources().getStringArray
                        (R.array.response_common)[new Random().nextInt(3)]);
            }
            lastInput = inputString;
        }
    }
    class MyDownloadTask extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            //display progress dialog.
        }
        protected Void doInBackground(Void... params) {
            try {
                URL picUrl = Url;
                HttpURLConnection conn = (HttpURLConnection) picUrl.openConnection();
                conn.setConnectTimeout(1000);
                conn.setReadTimeout(1000);
                conn.connect();
                if (conn.getResponseCode() == 200) {
                    InputStream ins = conn.getInputStream();
                    pngBM = BitmapFactory.decodeStream(picUrl.openStream());
                    finishFlag = true;
                    ins.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            // dismiss progress dialog and update ui
        }
    }

    public static class SensorDetector implements SensorEventListener {
        private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
        private static final int SHAKE_SLOP_TIME_MS = 500;
        private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

        private OnShakeListener mListener;
        private long mShakeTimestamp;
        private int mShakeCount;

        public void setOnShakeListener(OnShakeListener listener) {
            this.mListener = listener;
        }

        public interface OnShakeListener {
            public void onShake(int count);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // ignore
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (mListener != null) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float gX = x / SensorManager.GRAVITY_EARTH;
                float gY = y / SensorManager.GRAVITY_EARTH;
                float gZ = z / SensorManager.GRAVITY_EARTH;

                // gForce will be close to 1 when there is no movement.
                float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

                if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                    final long now = System.currentTimeMillis();
                    // ignore shake events too close to each other (500ms)
                    if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                        return;
                    }

                    // reset the shake count after 3 seconds of no shakes
                    if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                        mShakeCount = 0;
                    }

                    mShakeTimestamp = now;
                    mShakeCount++;

                    mListener.onShake(mShakeCount);
                }
            }
        }
    }

    public class ShakeListener implements SensorDetector.OnShakeListener {
        public void onShake(int count) {
            if (count == 2) {
                String finExpr;
                final TextView inputField = findViewById(R.id.expression);
                String iniExpr = inputField.getText().toString();
                try {
                    finExpr = Operator.go(new Expression(iniExpr).getpostExpr());
                    final TextView result = findViewById(R.id.result);
                    result.setText(finExpr);
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        }
    }
}

