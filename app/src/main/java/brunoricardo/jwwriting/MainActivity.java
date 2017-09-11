package brunoricardo.jwwriting;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private List<String> bibleBooks = new ArrayList<>();
    private int autosaveCount = 0,numeroDeTextosEncontrados=0;
    private String NameOfFileToWrite="",lastPathOfFileSaved = "";
    public static TextView textView1;
    public EditText editText;
    public LinearLayout opcoes;
    static final int COSTUM_DIALOG_ID=0;
    public int DragPrimeiroY,DragUltimoY,DragPrimeiroX,DragUltimoX,TextViewX;
    long TimeStart,TimeTake;
    Date date=new Date();
    TextView textFolder;
    ListView dialog_ListView;
    MainActivity main;
    private List<String> fileList=new ArrayList<>();
    private String[] textosRecebidos=new String[1];
    private List<Thread> t =new ArrayList<>();
    File curFolder,root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main=this;
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Error"+Thread.currentThread().getStackTrace()[2],paramThrowable.getLocalizedMessage());
                Log.d("Texto","Entrei aqui antes de fechar");
                if (isStoragePermissionGranted()){
                    try {
                        Calendar teste=Calendar.getInstance();
                        String fileName="copiaSeguranca_"+teste.get(Calendar.DATE)+"_"+teste.get(Calendar.HOUR_OF_DAY)+"_"+teste.get(Calendar.MINUTE)+"_"+teste.get(Calendar.MILLISECOND)+"";
                        Log.d("Texto","NomeFicheiro="+fileName);
                        String tmpPath=Environment.getExternalStorageDirectory().getPath()+"/jwwriting/"+fileName.trim()+".docx";
                        File file = new File(tmpPath);
                        if (!file.exists()) {
                            if (file.createNewFile()) {
                                Log.d("Ok", "Empty file created");
                            }
                        }
                        String textoParaGravar = ((EditText) findViewById(R.id.editText4)).getText().toString();
                        FileOutputStream fOut = new FileOutputStream(tmpPath, false);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                        myOutWriter.append(textoParaGravar);
                        myOutWriter.close();
                        fOut.flush();
                        fOut.close();
                        Toast.makeText(getApplicationContext(),"Erro no programa - cópia de segurança criada",Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"Erro no programa - não foi possível criar cópia de segurança", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        //TODO: fazer menu de opcoes, já está a funcionar o metodo de abrir e fechar menu

        //TODO: poder movimentar a textView

        //TODO : corrigir livros com apenas 1 capitulo, neste caso jud,123joa
        //TODO : ao dar erro/sair dar backup do texto com data e milis, ver seguintes links:
        //https://stackoverflow.com/questions/7370981/how-to-catch-my-applications-crash-report
        //https://github.com/Ereza/CustomActivityOnCrash
        //https://stackoverflow.com/questions/27829955/android-handle-application-crash-and-start-a-particular-activity
        //https://stackoverflow.com/questions/22978245/how-to-prevent-android-app-from-crashing-due-to-exception-in-background-thread
        //https://stackoverflow.com/questions/19897628/need-to-handle-uncaught-exception-and-send-log-file
        
        //TODO : pedir permissão para escrever e ler os arquivos do cartão, caso não seja aceite não poderá se escrever no cartão
        //https://developer.android.com/training/permissions/requesting.html
        //https://stackoverflow.com/questions/33162152/storage-permission-error-in-marshmallow
        //Tenho uma function que vi no stackoverflow não verifiquei se funciona
        
        loadBibleBooks();
        setUpInterface(); // mais clean (mas é opcional, podes meter como estava)

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        opcoes=(LinearLayout) findViewById(R.id.linearLayout);
        TextView textView= (TextView)findViewById(R.id.textView5);
        editText= (EditText) findViewById(R.id.editText4);
        ImageButton minimize= (ImageButton) findViewById(R.id.minimizeButton);
        minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout barraLateral= (LinearLayout) findViewById(R.id.barraLateral);
                barraLateral.setVisibility(View.GONE);
                ImageButton maximize= (ImageButton) findViewById(R.id.maxButton);
                maximize.setVisibility(View.VISIBLE);
            }
        });

        final ImageButton maximize= (ImageButton) findViewById(R.id.maxButton);
        maximize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maximize.setVisibility(View.GONE);
                LinearLayout barraLateral= (LinearLayout) findViewById(R.id.barraLateral);
                barraLateral.setVisibility(View.VISIBLE);
            }
        });
        //Estes dois objetos vao estar encarregues de lidar com o menu de opcoes
        textView.setOnTouchListener(new DragHandler());
        editText.setOnTouchListener(new DragHandler());


        final Spinner tamanhoFonte = (Spinner) findViewById(R.id.tamanhoFonte);
        ArrayList<String> tamanhoDeFontePossiveis = new ArrayList<String>();
        for (int i=2;i<=20;i++){
            tamanhoDeFontePossiveis.add(i+"");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, tamanhoDeFontePossiveis);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        //tamanhoFonte.setPrompt("Escolhe o tamanho da fonte");
        tamanhoFonte.setAdapter(adapter);
        tamanhoFonte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String teste=parent.getItemAtPosition(position).toString();
                Log.d("Spinner","Texto="+teste);
                //tamanhoFonte.setSelection();
                float t=Float.parseFloat(teste);
                editText.setTextSize(t);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void loadBibleBooks() {
        // assim podes esconder o código :)
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
        bibleBooks.add("esd");
        bibleBooks.add("nee");
        bibleBooks.add("est");
        bibleBooks.add("jo");
        bibleBooks.add("salm");
        bibleBooks.add("prov");
        bibleBooks.add("ecle");
        bibleBooks.add("cant");
        bibleBooks.add("isa");
        bibleBooks.add("jer");
        bibleBooks.add("lam");
        bibleBooks.add("eze");
        bibleBooks.add("dan");
        bibleBooks.add("ose");
        bibleBooks.add("joe");
        bibleBooks.add("amo");
        bibleBooks.add("obad");
        bibleBooks.add("jon");
        bibleBooks.add("miq");
        bibleBooks.add("naum");
        bibleBooks.add("haba");
        bibleBooks.add("sofo");
        bibleBooks.add("ag");
        bibleBooks.add("zaca");
        bibleBooks.add("mal");
        bibleBooks.add("mat");
        bibleBooks.add("mar");
        bibleBooks.add("luc");
        bibleBooks.add("joa");
        bibleBooks.add("ato");
        bibleBooks.add("rom");
        bibleBooks.add("1cor");
        bibleBooks.add("2cor");
        bibleBooks.add("gal");
        bibleBooks.add("efes");
        bibleBooks.add("fili");
        bibleBooks.add("col");
        bibleBooks.add("1tes");
        bibleBooks.add("2tes");
        bibleBooks.add("1ti");
        bibleBooks.add("2ti");
        bibleBooks.add("tit");
        bibleBooks.add("file");
        bibleBooks.add("heb");
        bibleBooks.add("tia");
        bibleBooks.add("1pe");
        bibleBooks.add("2pe");
        bibleBooks.add("1joa");
        bibleBooks.add("2joa");
        bibleBooks.add("3joa");
        bibleBooks.add("jud");
        bibleBooks.add("apo");
    }

    private void setUpInterface() {
        final EditText editText = (EditText) findViewById(R.id.editText4);
        final TextView textView = (TextView) findViewById(R.id.textView5);
        textView1=textView;
        editText.setText("");
        textView.setText("");
        textView.setMovementMethod(new ScrollingMovementMethod());

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (lastPathOfFileSaved != "") {
                    autosaveCount++;
                    if (autosaveCount >= 500) {
                        try {
                            String textoParaGravar = ((EditText) findViewById(R.id.editText4)).getText().toString();
                            FileOutputStream fOut = new FileOutputStream(lastPathOfFileSaved, false);
                            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                            myOutWriter.append(textoParaGravar);
                            myOutWriter.close();
                            fOut.flush();
                            fOut.close();
                            autosaveCount = 0;
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), "Erro ao gravar automaticamente! Por favor tente gravar manualmente.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                final String[] words = editText.getText().toString().replace("\n", " ").replace(".", " ").split(" ");
                textView.setText("");
                t.clear();
                textosRecebidos=new String[1];
                AssetManager assetManager = getApplicationContext().getAssets();
                numeroDeTextosEncontrados=-1;
                TimeStart = date.getTime();
                for ( int i = 0; i < words.length; i++) {

                  //Esta condição, testa se existe : ,se existe algo mais a seguir aos :
                  //Testa se o texto antes dos : correspondente ao capitulo é mesmo int, e se existe algum versiculo
                    if (words[i].contains(":") && (words[i].indexOf(":")!=(words[i].length()-1)) &&
                            (TryParseInt(words[i].substring(0,words[i].indexOf(":")))!=null))  {
                        Log.d("Texto","Encontrei um");
                        String texto=words[i-1]+" "+words[i];
                        Log.d("Texto", "Texto é " + texto);
                        numeroDeTextosEncontrados++;
                        TextAnalizer text = new TextAnalizer(texto, getApplicationContext(), bibleBooks, main,numeroDeTextosEncontrados);

                        t.add(new Thread(text));
                        t.get(t.size()-1).start();
                        textosRecebidos = Arrays.copyOf(textosRecebidos, numeroDeTextosEncontrados+1);
                        //t.start(); //=new Thread(text);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
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

        if (id == R.id.new_file) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Vai perder TODO O TEXTO que não foi gravado. Continuar mesmo assim?");
            builder.setIcon(R.drawable.warning);
            builder.setPositiveButton("sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
        else if (id == R.id.nav_camera) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Vai perder TODO O TEXTO que não foi gravado. Continuar mesmo assim?");
            builder.setIcon(R.drawable.warning);
            builder.setPositiveButton("sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
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
                }
            });
            builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();

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
                    if (isStoragePermissionGranted()){
                        writeToFile();
                    }

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
        else if (id == R.id.quick_save) {
            if (lastPathOfFileSaved != "") {
                try {
                    String textoParaGravar = ((EditText) findViewById(R.id.editText4)).getText().toString();
                    FileOutputStream fOut = new FileOutputStream(lastPathOfFileSaved, false);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(textoParaGravar);
                    myOutWriter.close();
                    fOut.flush();
                    fOut.close();
                    Toast.makeText(getApplicationContext(), "Ficheiro guardado", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Erro a gravar! Por favor tente novamente.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Por favor, grave primeiro o ficheiro.", Toast.LENGTH_SHORT).show();
            }
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

                lastPathOfFileSaved = filepath;
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.getMenu().findItem(R.id.quick_save).setEnabled(true);
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
            BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
            String teste;
            StringBuilder stringBuilder = new StringBuilder();
            Log.d("Ok","Ficheiro aberto");
            while ((teste=in.readLine())!=null){
                    stringBuilder.append(teste + "\n");
            }
            final EditText editText= (EditText) findViewById(R.id.editText4);
            editText.setText("");
            editText.append(stringBuilder.toString()); // usar append em vez de setText para mover o cursor para o final do texto depois de ler o ficheiro
            final TextView textView= (TextView) findViewById(R.id.textView5);
            textView.setText("");
            Toast.makeText(getApplicationContext(),"Ficheiro aberto",Toast.LENGTH_LONG).show();

            lastPathOfFileSaved = filename;
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.getMenu().findItem(R.id.quick_save).setEnabled(true);
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
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message text) {
            String message=(String)text.obj;
            int numeroDoTexto=text.arg1;
            textosRecebidos[numeroDoTexto]=message;
            CheckThreads();
        }
    };
    void CheckThreads(){
        boolean Acabaram=true;
        for (int i=0;i<=t.size()-1;i++){
            if (t.get(i).isAlive() || textosRecebidos[i]==null){
                Acabaram=false;
                Log.d("Texto","Eu estou viva");
            }
        }
        if (Acabaram){
            Log.d("Texto","Tenho "+textosRecebidos.length+ " elementos");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                for (String texto:textosRecebidos){
                    Log.d("Texto","Neste momento tenho o texto "+texto);
                    textView1.append(Html.fromHtml(texto,Html.FROM_HTML_MODE_LEGACY));
                }
            } else {
                for (String texto:textosRecebidos){
                    Log.d("Texto","Neste momento tenho o texto "+texto);
                  textView1.append(Html.fromHtml(texto));
                }
            }
            date=new Date();
            TimeTake=date.getTime()-TimeStart;            
            Log.d("Texto","Demorei "+TimeTake+ " Atualmente "+ date.getTime());
        }
    }
    
    //TODO :verificar se metodo está a funcionar corretamente
    public  boolean isStoragePermissionGranted() {
    if (Build.VERSION.SDK_INT >= 23) {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("Permission","Permission is granted");
            return true;
        } else {

            Log.v("Permission","Permission is revoked");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
    }
    else { //permission is automatically granted on sdk<23 upon installation
        Log.v("Permission","Permission is granted");
        return true;
    }
}


    //Serve para lidar com os eventos de Drag
    private class DragHandler implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d("Touch",event+"");
            switch (event.getActionMasked()){
                case MotionEvent.ACTION_DOWN:
                    TextViewX=editText.getWidth();
                    DragPrimeiroY=(int)event.getY();
                    DragPrimeiroX=(int)event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    DragUltimoX=(int)event.getX();
                    int Range=20;
                    Log.d("Ola","PrimeiroX="+DragPrimeiroX+" "+TextViewX);
                    if (TextViewX-DragPrimeiroX<=Range && TextViewX-DragPrimeiroX>=0|| DragPrimeiroX-TextViewX<=Range && DragPrimeiroX-TextViewX>=0){


                        LinearLayout test=(LinearLayout) findViewById(R.id.linearLayout);
                        int FullWidth=test.getWidth();
                        float percentagem=(DragUltimoX*100)/FullWidth;
                        percentagem/=100;

                        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        p.weight = percentagem;
                        editText.setLayoutParams(p);
                        LinearLayout test1=(LinearLayout) findViewById(R.id.barraLateral);
                        p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        p.weight=1-percentagem;
                        test1.setBackgroundResource(R.drawable.onselect);
                        test1.setLayoutParams(p);


                    }
                    break;
                case MotionEvent.ACTION_UP:

                    DragUltimoY=(int)event.getY();
                    DragUltimoX=(int)event.getX();
                    LinearLayout test1=(LinearLayout) findViewById(R.id.barraLateral);
                    test1.setBackgroundResource(R.drawable.border_textview);
                    //Se isto acontecer significa que foi feito drag para baixo
                    if (DragUltimoY>DragPrimeiroY && DragPrimeiroY<=50){
                        opcoes.setVisibility(View.VISIBLE);

                    //Se isto acontecer foi feito drag para cima
                    }else if (DragUltimoY<DragPrimeiroY && DragPrimeiroY<=100){
                        opcoes.setVisibility(View.GONE);

                    }

                    break;
            }
            return false;
        }
    };

    private class EditTextScroll implements View.OnScrollChangeListener{
        @Override
        public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

        }
    }

}
