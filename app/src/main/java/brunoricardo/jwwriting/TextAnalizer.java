package brunoricardo.jwwriting;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Paint;
import android.support.v4.app.NavUtils;
import android.util.Log;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bruno on 15/06/2017.
 */

public class TextAnalizer extends Thread {

    //Variaveis contem texto recebido e livro,cap. e vers.
    String textoRecebido,livroRecebido,Capitulo,Versiculo;

    private List<String> TodosOsVersiculos=new ArrayList<>();
    //Variavel contem todos os livros
    private List<String> bibleBooks = new ArrayList<>();


    //Variaveis para ler o ficheiro xhtml
    private Context context;
    int NumeroLivro;


    public TextAnalizer(String texto, Context ctx,List<String> books){
        this.textoRecebido=texto;
        this.context=ctx;

        //Receber a arraylist com todos os livros para não estar sempre a reescrever quando a thread é lida
        this.bibleBooks=books;
    }

    @Override
    public void run() {

        //TODO: usar try???     verificar caso haja erros na thread, ver este link https://www.tutorialspoint.com/javaexamples/exception_thread.htm
        try {


            //Vai buscar o livro
            livroRecebido=textoRecebido.substring(0,(textoRecebido.indexOf(" ")));

            //Vais buscar capitulo e versiculo
             Capitulo=textoRecebido.substring(textoRecebido.indexOf(" "),textoRecebido.indexOf(":"));
             Versiculo=textoRecebido.substring(textoRecebido.indexOf(":"));
            
            
            //TODO:Verificações se existe mais de um versiculo poderão não estar 100% corretas
            if (Versiculo.contains(",")){
                //Faz split da string por , e adiciona isso na arraylist e de seguida remove as , da arraylist
                TodosOsVersiculos.addAll(Arrays.asList(Versiculo.split(",")));
                for (String versiculo : TodosOsVersiculos){
                    if (versiculo.contains(",")){
                        versiculo.replace(",","");
                    }
                }
            }
            if (Versiculo.contains("-")){
                
                int PrimeiroVers,UltimoVers;
                PrimeiroVers=Integer.parseInt(Versiculo.substring(0,Versiculo.indexOf("-")));
                UltimoVers=Integer.parseInt(Versiculo.substring(Versiculo.indexOf("-")));
                //Adiciona o numero de todos os versiculos do inicio ao fim
                for (int i=PrimeiroVers;i<=UltimoVers;i++){
                    TodosOsVersiculos.add(i+"");
                }
            }

             boolean livroExiste=false;
            for (String livro : bibleBooks) {
                if (livroRecebido.contains(livro)) {
                    livroExiste=true;
                    NumeroLivro=bibleBooks.indexOf(livro);
                    Log.d("Ok", "Livro existe é " + livro);
                 }
            }

            if (livroExiste) {


                AssetManager assetManager = context.getAssets();


            //Ficar com o nome correto do arquivo
                String nomeArquivo;
                if (NumeroLivro < 5) {
                    nomeArquivo = "100106110" + (NumeroLivro + 5);
                } else {
                    nomeArquivo = "10010611" + (NumeroLivro + 5);
                }
                if ((Integer.parseInt(Capitulo)) > 1) {
                    nomeArquivo += "-split" + Capitulo + ".xhtml";
                } else {
                    nomeArquivo += ".xhtml";
                }



                //Este ciclo vai se encarregar de entregar o texto correto para depois se decompor
                InputStream streamComFicheiro  = assetManager.open(nomeArquivo);

                BufferedReader leitorTexto = new BufferedReader(new InputStreamReader(streamComFicheiro));
                String Conteudo="";
                String textoDoFicheiro="";
                //// TODO: É preciso corrigir o ciclo para aceitar mais de um versiculo 
                //// TODO: Este ciclo esta a ler o ficheiro e adicionar numa string, mas depois é preciso trabalhar a string 
                while ((Conteudo=leitorTexto.readLine())!=null){

                    //Vai ver se o conteudo
                    if (Conteudo.contains("<span id=\"chapter"+Capitulo+"_verse"+Versiculo+"\">")){
                        textoDoFicheiro+=Conteudo;
                    }
                }
                

            }



        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
