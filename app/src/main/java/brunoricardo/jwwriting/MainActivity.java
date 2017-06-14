package brunoricardo.jwwriting;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private List<String> bibleBooks = new ArrayList<>();
    public static final List<String> texts = new ArrayList<>();
    public static int IndexInBibleBooks=0,ChapterNumber,VersicleNumber,FinalVersicleNumber=-1;
    public String BibleText;
    public String NameOfFileToWrite="";
    public TextView textView;
    static final int COSTUM_DIALOG_ID=0;
    TextView textFolder;
    ListView dialog_ListView;
    private List<String> VersicleDividedByComma = new ArrayList<>();
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


        final EditText editText = (EditText) findViewById(R.id.editText4);
        final TextView textView = (TextView) findViewById(R.id.textView5);

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
                AssetManager assetManager = getApplicationContext().getAssets();
                for ( int i = 0; i < words.length; i++) {
                    if (words[i].contains(":")) {
                        if ((i-1)>=0 && checkBook(words[i-1])){
                            if (checkChapterAndVersicle(words[i])){
                                try {
                                    //This lines treat the filename
                                    String Filename;
                                    if (IndexInBibleBooks<5){
                                        Filename="100106110"+(IndexInBibleBooks+5);
                                    }else {
                                        Filename="10010611"+(IndexInBibleBooks+5);
                                    }
                                    if (ChapterNumber>1){
                                        Filename+="-split"+ChapterNumber+".xhtml";
                                    }else {
                                        Filename+=".xhtml";
                                    }
                                    //new DoThreading().execute(Filename);
                                    //Open and read the file of the assets
                                    InputStream stream = assetManager.open(Filename);
                                    BufferedReader r = new BufferedReader(new InputStreamReader(stream));
                                    String content;
                                    String[] Text;

                                    BibleText="";
                                    //This code reads the xhtml and decompose the line where the versicle is, and put it in TESTE2
                                    while ((content=r.readLine())!=null){
                                        while (content.contains("<span id=\"chapter"+ChapterNumber+"_verse"+VersicleNumber+"\">")){

                                            //Splits the text in detail
                                            Text=content.split("<span id=\"chapter" + ChapterNumber + "_verse" + VersicleNumber + "\">");
                                            Text=Text[1].split("<span id=\"chapter" + ChapterNumber + "_verse" + (VersicleNumber+1) + "\">");

                                            //Gets the text without appearing  any HTML tag
                                            Document teste1= Jsoup.parse((Text[0]));

                                            //This puts the number of the text in bold
                                            String TempTeste=teste1.body().text();
                                            TempTeste=TempTeste.replace(VersicleNumber+""," <b>"+VersicleNumber+" </b> ");
                                            BibleText+=TempTeste;
                                            Log.d("Ok", "Final text is "+ BibleText);

                                            //This if , is here in case the user uses "-" than the while i'll keep looking until it reaches the final versicle
                                            if (VersicleNumber<FinalVersicleNumber){
                                                Log.d("Ok","VersicleNumber<FinalVersicleNumber, "+ VersicleNumber+" "+FinalVersicleNumber);
                                                VersicleNumber++;
                                            }else if (!VersicleDividedByComma.isEmpty() && VersicleDividedByComma.size()>1)
                                            {
                                                VersicleDividedByComma.remove(0);
                                                String teste="";
                                                for (String e:VersicleDividedByComma){
                                                    teste+=" "+e;
                                                }
                                                Log.d("Ok","In the arraylist the versicles are "+ teste);
                                                    try {
                                                        VersicleNumber=Integer.parseInt(VersicleDividedByComma.get(0));
                                                    }catch ( NumberFormatException e){

                                                    }
                                            }else {
                                                Log.d("Ok","Will break while");
                                                break;
                                            }

                                        }
                                    }
                                } catch (IOException e) {
                                    Log.d("Ok","ERROR "+ e.getMessage());
                                }

                                //Add the text and put first letter upper case
                                /*texts.add("<br><b><font  color=\"#2878BB\">");
                                if (!words[i-1].substring(0,1).matches("[a-zA-Z]")){
                                    texts.add("\r\n"+words[i-1].substring(0,1)+ words[i-1].substring(1,2).toUpperCase()+words[i-1].substring(2) + " " + words[i]);
                                }else {
                                    texts.add("\r\n"+ words[i-1].substring(0,1).toUpperCase()+words[i-1].substring(1) + " " + words[i]+"");
                                }
                                texts.add("</font></b>");
                                texts.add("\r\n"+BibleText+"<br>");*/
                            }


                        }
                    }
                }

                //The for that adds bible texts in the textview
                    if (texts.size() > 0) {
                    String finalText = "";
                    for (int i = 0; i < texts.size(); i++) {
                        finalText += texts.get(i);
                        if (i < texts.size() - 1) finalText += "\n";

                    }
                    textView.setText(Html.fromHtml(finalText));

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

private class DoThreading extends AsyncTask<String,Void,String> {
   protected String doInBackground(String...Filename){
       AssetManager assetManager = getApplicationContext().getAssets();
       InputStream stream = null;
       String teste="";
       try {
           stream = assetManager.open(Filename[0]);
            Log.d("Teste",Filename[0]);
       BufferedReader r = new BufferedReader(new InputStreamReader(stream));
       String content;
       String[] Text;
       BibleText="";
       //This code reads the xhtml and decompose the line where the versicle is, and put it in TESTE2
       while ((content=r.readLine())!=null){
           while (content.contains("<span id=\"chapter"+ChapterNumber+"_verse"+VersicleNumber+"\">")){

               //Splits the text in detail
               Text=content.split("<span id=\"chapter" + ChapterNumber + "_verse" + VersicleNumber + "\">");
               Text=Text[1].split("<span id=\"chapter" + ChapterNumber + "_verse" + (VersicleNumber+1) + "\">");

               //Gets the text without appearing  any HTML tag
               Document teste1= Jsoup.parse((Text[0]));

               //This puts the number of the text in bold
               String TempTeste=teste1.body().text();
               TempTeste=TempTeste.replace(VersicleNumber+""," <b>"+VersicleNumber+" </b> ");
               BibleText+=TempTeste;
               Log.d("Ok", "Final text is "+ BibleText);

               //This if , is here in case the user uses "-" than the while i'll keep looking until it reaches the final versicle
               if (VersicleNumber<FinalVersicleNumber){
                   Log.d("Ok","VersicleNumber<FinalVersicleNumber, "+ VersicleNumber+" "+FinalVersicleNumber);
                   VersicleNumber++;
               }else if (!VersicleDividedByComma.isEmpty() && VersicleDividedByComma.size()>1)
               {
                   VersicleDividedByComma.remove(0);
                    teste="";
                   for (String e:VersicleDividedByComma){
                       teste+=" "+e;
                   }
                   Log.d("Ok","In the arraylist the versicles are "+ teste);
                   try {
                       VersicleNumber=Integer.parseInt(VersicleDividedByComma.get(0));
                   }catch ( NumberFormatException e){

                   }
               }else {
                   Log.d("Ok","Will break while");
                   break;
               }

           }
       }
       } catch (IOException e) {
           Log.d("Teste"," Erro");
           e.printStackTrace();
       }
       teste=BibleText;
        Log.d("Teste","Final text is "+teste);
       return teste;
   }
   protected void onPostExecute(String result){

       Log.d("Teste",result);
       textView.append(result);
   }
}
    private boolean checkChapterAndVersicle(String c){
        //TODO correct algorithm that convert string in integers, because it is bugging the code
        Log.d("Ok", c);
        FinalVersicleNumber=-1;
        String[] Numbers =c.split(":");
        boolean IsItRight=true;
        Log.d("Ok","Splitted numbers "+Numbers.length);
        if (Numbers.length<=1){
            IsItRight=false;
        }else {
            try {
                if (Numbers[1].contains(",")){
                    if (!VersicleDividedByComma.isEmpty()){
                        Log.d("Ok","Im clearing arraylist");
                        VersicleDividedByComma= new ArrayList<>();
                    }
                    VersicleDividedByComma.addAll(Arrays.asList(Numbers[1].split(",")));
                    //VersicleDividedByComma=Arrays.asList(Numbers[1].split(","));
                    for (String e:VersicleDividedByComma){
                        if (e.contains(",")){
                            e.replace(",","");
                        }
                    }
                    ChapterNumber=Integer.parseInt(Numbers[0]);
                    VersicleNumber=Integer.parseInt(VersicleDividedByComma.get(0));
                }else if (Numbers[1].contains("-")){
                    int tempIndex=Numbers[1].indexOf("-");
                    FinalVersicleNumber=Integer.parseInt(Numbers[1].substring(tempIndex+1));
                    ChapterNumber=Integer.parseInt(Numbers[0]);
                    VersicleNumber=Integer.parseInt(Numbers[1].substring(0,tempIndex));
                }else {
                    ChapterNumber=Integer.parseInt(Numbers[0]);
                    VersicleNumber=Integer.parseInt(Numbers[1]);
                }
                Log.d("Ok",ChapterNumber+" "+VersicleNumber+" "+FinalVersicleNumber);
            } catch (NumberFormatException e){
                Log.d("Ok","Formato inválido");
                IsItRight=false;
            }
        }
        return IsItRight;
    }
    private boolean checkBook (String c) {
        //for (String c : texts) {
        boolean FoundValue=false;
        c = c.toLowerCase().replace("ê", "e");
        c =  c.toLowerCase().replace("í", "i");
        c =  c.toLowerCase().replace("ã", "a");
        c =  c.toLowerCase().replace("ú", "u");
        c =  c.toLowerCase().replace("é", "e");
        c =  c.toLowerCase().replace("ó", "o");
        c =  c.toLowerCase().replace("ô", "o");
        c =  c.toLowerCase().replace("á", "a");
        c =   c.toLowerCase().replace("ç", "c");
        for (String cap : bibleBooks) {
            if (c.contains(cap)) {
                FoundValue=true;
                IndexInBibleBooks=bibleBooks.indexOf(cap);
                Log.d("Ok", "" + cap);
            }
        }
        return FoundValue;
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
}
