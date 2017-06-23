package brunoricardo.jwwriting;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Color;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private List<String> bibleBooks = new ArrayList<>();
    public static final List<String> texts = new ArrayList<>();
    public String BibleText;
    public String NameOfFileToWrite="";
    public static TextView textView1;
    static final int COSTUM_DIALOG_ID=0;
    TextView textFolder;
    ListView dialog_ListView;
    Thread t=new Thread();
    MainActivity main;
    private List<String> fileList=new ArrayList<>();
    File curFolder,root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bibleBooks.add("gen");
        bibleBooks.add("exo");
        bibleBooks.add("lev");
        bibleBooks.add("num");
        bibleBooks.add("deut");
        bibleBooks.add("jos");
        bibleBooks.add("jui");
        bibleBooks.add("ru");
        bibleBooks.add("1sam");
        bibleBooks.add("2sam");
        bibleBooks.add("1rei");
        bibleBooks.add("2rei");
        bibleBooks.add("1cro");
        bibleBooks.add("2cro");
        bibleBooks.add("esdr");
        bibleBooks.add("nee");
        bibleBooks.add("ester");
        bibleBooks.add("jo");
        bibleBooks.add("salm");
        bibleBooks.add("prov");
        bibleBooks.add("ecle");
        bibleBooks.add("cantico");
        bibleBooks.add("isaias");
        bibleBooks.add("jeremi");
        bibleBooks.add("lament");
        bibleBooks.add("ezeq");
        bibleBooks.add("dani");
        bibleBooks.add("osei");
        bibleBooks.add("joel");
        bibleBooks.add("amos");
        bibleBooks.add("obad");
        bibleBooks.add("jonas");
        bibleBooks.add("miquei");
        bibleBooks.add("naum");
        bibleBooks.add("haba");
        bibleBooks.add("sofo");
        bibleBooks.add("ag");
        bibleBooks.add("zaca");
        bibleBooks.add("mala");
        bibleBooks.add("mateus");
        bibleBooks.add("marcos");
        bibleBooks.add("lucas");
        bibleBooks.add("joao");
        bibleBooks.add("atos");
        bibleBooks.add("roman");
        bibleBooks.add("1corin");
        bibleBooks.add("2corin");
        bibleBooks.add("gala");
        bibleBooks.add("efes");
        bibleBooks.add("filip");
        bibleBooks.add("colos");
        bibleBooks.add("1tes");
        bibleBooks.add("2tes");
        bibleBooks.add("1ti");
        bibleBooks.add("2ti");
        bibleBooks.add("tito");
        bibleBooks.add("filem");
        bibleBooks.add("hebre");
        bibleBooks.add("tia");
        bibleBooks.add("1ped");
        bibleBooks.add("2ped");
        bibleBooks.add("1joao");
        bibleBooks.add("2joao");
        bibleBooks.add("3joao");
        bibleBooks.add("jud");
        bibleBooks.add("apo");
        main=this;

        final EditText editText = (EditText) findViewById(R.id.editText4);
        final TextView textView = (TextView) findViewById(R.id.textView5);
        textView1=textView;
        editText.setText("");
        textView.setText("");
        textView.setMovementMethod(new ScrollingMovementMethod());
        //TODO put thread to not push too much of the main thread

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final String[] words = editText.getText().toString().replace("\n", " ").replace(".", " ").split(" ");
                texts.clear();
                textView.setText("");
                int NumberOfThread=-1;
                AssetManager assetManager = getApplicationContext().getAssets();
                for ( int i = 0; i < words.length; i++) {
                    Log.d("Texto","Words[i]="+words[i]);
                    if (words[i].contains(":") && (words[i].indexOf(":")!=(words[i].length()-1)) &&
                            (TryParseInt(words[i].substring(0,words[i].indexOf(":")))!=null) && (TryParseInt(words[i].substring(words[i].indexOf(":")+1)))!=null)  {
                        Log.d("Texto","Encontrei um");
                        NumberOfThread++;
                        String texto=words[i-1]+" "+words[i];
                        Log.d("Texto", "Texto é "+texto);
                        TextAnalizer text=new TextAnalizer(texto,getApplicationContext(),bibleBooks,main );
                        //TODO: adicionar texto na Main thread na textview
                        t=new Thread(text);
                        t.start(); //=new Thread(text);

                    }


                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            File folder = new File(Environment.getExternalStorageDirectory().getPath()+"/jwwriting");
            Log.d("Ok","Folder in "+(Environment.getExternalStorageDirectory().getPath()+"/jwwriting") );
            if (!folder.exists()){
                if (folder.mkdir()){
                    Log.d("Ok","Folder created ");
                }
            }
            root=new File(Environment.getExternalStorageDirectory().getPath()+"/jwwriting");
            curFolder=root;
            showDialog(COSTUM_DIALOG_ID);

        } else if (id == R.id.nav_gallery) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Nome do ficheiro a guardar");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            input.setTextColor(Color.WHITE);
            builder.setIcon(R.drawable.ic_save_icon_5);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   NameOfFileToWrite = input.getText().toString();
                    File folder = new File(Environment.getExternalStorageDirectory().getPath()+"/jwwriting");
                    Log.d("Ok","Folder in "+(Environment.getExternalStorageDirectory().getPath()+"/jwwriting") );
                    if (!folder.exists()){
                        if (folder.mkdir()){
                            Log.d("Ok","Folder created ");
                        }
                    }
                    writeToFile();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog=null;
        switch (id){
            case COSTUM_DIALOG_ID:
                dialog=new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialoglayout);
                dialog.setTitle("Choose file to open");
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                textFolder=(TextView) dialog.findViewById(R.id.folder);
                dialog_ListView=(ListView)  dialog.findViewById(R.id.dialoglist);
                dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        File selected=new File(fileList.get(position));
                        Log.d("Ok","Ficheiro clicado é "+ selected.getPath());
                        readFromFile(Environment.getExternalStorageDirectory().getPath()+"/jwwriting/"+selected.getPath());
                            dismissDialog(COSTUM_DIALOG_ID);
                    }
                });
                break;
        }
        return  dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id){
            case COSTUM_DIALOG_ID:
                ListDir(curFolder);
                break;
        }
    }
    void ListDir(File f){
    curFolder=f;
    textFolder.setText(f.getPath());
    File[] files=f.listFiles();
    fileList.clear();
    for (File file: files){
        //String
       fileList.add(file.getName());
    }
    ArrayAdapter<String> directoryList= new ArrayAdapter<String>(this,R.layout.list_view,fileList);
    dialog_ListView.setAdapter(directoryList);


}


    private void writeToFile() {
        try {
            String filepath =Environment.getExternalStorageDirectory().getPath()+"/jwwriting/"+NameOfFileToWrite.trim()+".docx";
            File file = new File(filepath);
            if (!file.exists()){
                if (file.createNewFile()){
                    Log.d("Ok","Empty file created");
                }
                final EditText editText = (EditText) findViewById(R.id.editText4);
                String teste=editText.getText().toString();
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(teste);
                myOutWriter.close();
                fOut.flush();
                fOut.close();
                Toast.makeText(getApplicationContext(),"Ficheiro guardado",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getApplicationContext(),"Ficheiro já existe",Toast.LENGTH_SHORT).show();
            }
        }
        catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Erro a escrever por favor tente mais tarde",Toast.LENGTH_SHORT).show();
        }
    }
    private void readFromFile(String filename) {
        Log.d("Ok","Starting void, with the file "+ filename);
        try {
            //InputStream inputStream = context.openFileInput(filename);
            BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
            String teste;
            StringBuilder stringBuilder = new StringBuilder();
            Log.d("Ok","Ficheiro aberto");
            while ((teste=in.readLine())!=null){
                    stringBuilder.append(teste);
            }
            final EditText editText= (EditText) findViewById(R.id.editText4);
            editText.setText("");
            editText.setText(stringBuilder.toString());
            final TextView textView= (TextView) findViewById(R.id.textView5);
            textView.setText("");
            texts.clear();
            Toast.makeText(getApplicationContext(),"Ficheiro aberto",Toast.LENGTH_LONG).show();
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
            Toast.makeText(getApplicationContext(),"Erro ao abrir ficheiro",Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
            Toast.makeText(getApplicationContext(),"Erro ao abrir ficheiro",Toast.LENGTH_LONG).show();
        }
    }

    public static Integer TryParseInt(String someText) {
        try {
            return Integer.parseInt(someText);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
    public void Test(String text){
        textView1.setText(text);
    }
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message text) {
            String message=(String)text.obj;
            textView1.append(message);
        }
    };
}