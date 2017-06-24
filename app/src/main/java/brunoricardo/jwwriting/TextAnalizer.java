package brunoricardo.jwwriting;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Message;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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

    //Variavel textview para acrescentar texto
    Element textoFinal;
    MainActivity livrosAcrescentar;

    public TextAnalizer(String texto, Context ctx, List<String> books, MainActivity livros){
        this.textoRecebido=texto;
        this.context=ctx;
        this.livrosAcrescentar=livros;
        //Receber a arraylist com todos os livros para não estar sempre a reescrever quando a thread é lida
        this.bibleBooks=books;
    }

    @Override
    public void run() {

        //TODO: usar try???     verificar caso haja erros na thread, ver este link https://www.tutorialspoint.com/javaexamples/exception_thread.htm
        try {
            //Vai buscar o livro
            livroRecebido=textoRecebido.substring(0,(textoRecebido.indexOf(" "))).toLowerCase();

            //Vais buscar capitulo e versiculo
             Capitulo=textoRecebido.substring(textoRecebido.indexOf(" "),textoRecebido.indexOf(":")).trim();
             Versiculo=textoRecebido.substring(textoRecebido.indexOf(":")+1);
            
            Log.d("Texto",livroRecebido+" "+Capitulo+" "+Versiculo);
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
                Log.d("Texto","Versiculo é "+ Versiculo);
                int VersiculoParaAcabar=Integer.parseInt(Versiculo)+1;
                boolean PararCiclo=false;
                boolean ContinuarALer=false;
                //// TODO: É preciso corrigir o ciclo para aceitar mais de um versiculo 
                //// TODO: Este ciclo esta a ler o ficheiro e adicionar numa string, mas depois é preciso trabalhar a string
                int LinhasLida=0;
                while ((Conteudo=leitorTexto.readLine())!=null && !PararCiclo){
                    LinhasLida++;

                    //Começar a ler o ficheiro
                    if (Conteudo.contains("<span id=\"chapter"+Capitulo+"_verse"+Versiculo+"\">")){
                        Log.d("Texto","Encontrei o 1º vers");
                        Log.d("Texto", "Conteudo da linha="+ Conteudo);
                        if (Conteudo.contains("<span id=\"chapter"+Capitulo+"_verse"+VersiculoParaAcabar+"\">")){
                            textoDoFicheiro+=Conteudo.substring(Conteudo.indexOf("<span id=\"chapter"+Capitulo+"_verse"+Versiculo+"\">"),Conteudo.indexOf("<span id=\"chapter"+Capitulo+"_verse"+VersiculoParaAcabar+"\">"));
                            PararCiclo=true;
                            Log.d("Texto","Encontrei o último vers na mesma linha, devo parar");
                        }else{
                            ContinuarALer=true;
                            Log.d("Texto","O último vers não está mesma linha, devo continuar");
                            textoDoFicheiro+=Conteudo.substring(Conteudo.indexOf("<span id=\"chapter"+Capitulo+"_verse"+Versiculo+"\">"));
                        }
                    }

                    //À procura do fim do texto

                    if (!PararCiclo && Conteudo.contains("<span id=\"chapter"+Capitulo+"_verse"+VersiculoParaAcabar+"\">")){
                        Log.d("Texto","O ciclo ainda não encontrou nada e Encontrei agora a linha do ultimo versiculo");
                        textoDoFicheiro+=Conteudo.substring(0,Conteudo.indexOf("<span id=\"chapter"+Capitulo+"_verse"+VersiculoParaAcabar+"\">"));
                        ContinuarALer=false;
                        PararCiclo=true;

                        //Continuar a ler enquanto não encontrou o fim do documento;
                    }else if(ContinuarALer && !Conteudo.contains("<span id=\"chapter"+Capitulo+"_verse"+Versiculo+"\">")){
                        Log.d("Texto","Os versiculos estão em linhas diferentes");
                        //Log.d("Texto","Meio das linhas Linhalida="+LinhasLida+" Texto é "+Conteudo);
                        textoDoFicheiro+=Conteudo;
                    }

                }
                //Caso o versiculo seja o ultimo remover a parte que contem as notas
                if (textoDoFicheiro.contains("^")){
                    textoDoFicheiro=textoDoFicheiro.substring(0,textoDoFicheiro.indexOf("^"));
                }
                Document doc = Jsoup.parseBodyFragment(textoDoFicheiro);
                 textoFinal = doc.body();
                Log.d("Texto",textoFinal.text());
                Message msg=Message.obtain();
                msg.obj="<br><b><span style='color:#2878BB;'>"+livroRecebido+" "+ Capitulo+":"+Versiculo+"</span></b><br>"+textoFinal.text();
                livrosAcrescentar.handler.sendMessage(msg);
                //livrosAcrescentar.Test(textoFinal.text());


            }else {
                Log.d("Texto","Livro não existe");
            }
            /*TextView teste = (TextView) livrosAcrescentar.findViewById(R.id.textView5);
            teste.setText(textoFinal.text());*/
            
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Erro",e.getMessage());
        }

    }

}
