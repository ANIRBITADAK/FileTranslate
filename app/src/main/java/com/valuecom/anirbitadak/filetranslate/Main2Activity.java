package com.valuecom.anirbitadak.filetranslate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

public class Main2Activity extends AppCompatActivity {

    public static final int READ_REQ_CODE = 11;
    Button open, translate,reset;
    TextView show, Selectfile,targetlanguage;
    Spinner language;
    String type, s,translated,inpt,selectedlanguage=null;
    Uri uri = null;
    StringBuilder sb;
    Translate  translates;

    public static String getMimeType(String path, Context context) {
        String extention = path.substring(path.lastIndexOf("."));
        String mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extention);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap);
        return mimeType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        open = (Button) findViewById(R.id.open);
        translate = (Button) findViewById(R.id.translate);
        reset=(Button)findViewById(R.id.reset);
        show = (TextView) findViewById(R.id.show);
        Selectfile = (TextView) findViewById(R.id.Selectfile);
        targetlanguage = (TextView) findViewById(R.id.targetlanguage);
        language = (Spinner) findViewById(R.id.language);
        ArrayList<String> Data = new ArrayList<>();
        Data.add("Select");
        Data.add("Hindi");
        Data.add("Bengali");
        Data.add("Gujarati");
        Data.add("Marathi");
        Data.add("Kannada");
        Data.add("Malayalam");
        Data.add("Punajbi");
        Data.add("Tamil");
        Data.add("Telegu");
        Data.add("urdu");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.simple_spinner_item, Data);
        language.setAdapter(adapter);

        translate.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                if (inpt==null||inpt=="") {
                    Toast.makeText(getApplicationContext(), "Please Select a File First", Toast.LENGTH_LONG).show();
                    readFile(view);
                }
                else if(selectedlanguage!="Select"){
                    getLanguage();
                    Log.i("inside translate", "working");
                    final ProgressDialog dialog = new ProgressDialog(Main2Activity.this);
                    dialog.setMessage("Translating");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("inside run", "inside run");

                            translates = TranslateOptions.newBuilder().setApiKey("AIzaSyC8iF4BLdEuA6-eA9gc79MrsGgwtmh33Ho").build().getService();
                            if (translates != null) {
                                dialog.dismiss();

                                String text = inpt;

                                Translation translation =
                                        translates.translate(
                                                text,
                                                TranslateOption.sourceLanguage("en"),
                                                TranslateOption.targetLanguage(selectedlanguage));

                                translated = translation.getTranslatedText().toString();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        show.setText(translated);
                                    }
                                });
                            }
                        }
                    }).start();
                }
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.setText("");
                language.setSelection(0);
                selectedlanguage=null;
                inpt="";

            }
        });
    }
    public void readFile(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQ_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {


        if (requestCode == READ_REQ_CODE && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                uri = resultData.getData();
                s = uri.toString();
                Log.i("TAG", "Uri: " + uri.toString());
                Toast.makeText(getApplicationContext(), "FILE COPIED\n" + s, Toast.LENGTH_LONG).show();
                Log.i("MSG", uri.toString());
                readTextFile(uri);

            }
        }
    }
    private void readTextFile(Uri uri2) {
        type = getMimeType(s, getApplicationContext());
        if (type == "application/pdf") {
            Toast.makeText(getApplicationContext(),"ERROR OCCURED", Toast.LENGTH_LONG).show();
        }
        else {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri2);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                Log.i("", "open text file - content" + "\n");
                sb = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    Log.i("msg", line);
                    sb.append(line + "\n");
                    line = reader.readLine();

                }
                inpt=sb.toString();
                reader.close();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    public void getLanguage(){
        selectedlanguage=language.getSelectedItem().toString();
        switch (selectedlanguage){
            case "Select": Toast.makeText(getApplicationContext(),"Please select a language",Toast.LENGTH_SHORT).show();
                break;
            case "Hindi" :  selectedlanguage="hi";
                break;
            case "Bengali": selectedlanguage="bn";
                break;
            case "Gujarati": selectedlanguage="gu";
                break;
            case "Marathi": selectedlanguage="mr";
                break;
            case "Kannada": selectedlanguage="kn";
                break;
            case "Malayalam": selectedlanguage="ml";
                break;
            case "Punjabi": selectedlanguage="pa";
                break;
            case "Tamil": selectedlanguage="ta";
                break;
            case "Telegu": selectedlanguage="te";
                break;
            case "urdu": selectedlanguage="ur";
                break;

            default:Toast.makeText(getApplicationContext(),"Please select an option",Toast.LENGTH_LONG).show();
        }
    }

}
