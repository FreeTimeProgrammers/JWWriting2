package brunoricardo.jwwriting;

import java.util.Collections;
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

public class TextAnalizer extends Thread {

    //Variaveis contem texto recebido e livro,cap. e vers.
    String textoRecebido,livroRecebido,Capitulo,Versiculo,textoOriginalVersiculo;
    private List<Integer> TodosOsVersiculos=new ArrayList<>();
    private List<String> VersiculosAEncontrar = new ArrayList<>();

    //Variavel contem todos os livros
    private List<String> bibleBooks = new ArrayList<>();

    //Variaveis para ler o ficheiro xhtml
    private Context context;
    int NumeroLivro;
    int TextoNumero;

    //Variavel textview para acrescentar texto
    Element textoFinal;
    MainActivity livrosAcrescentar;
    //TODO: ANTES DE CORRER ESTE CODIGO, TENHO QUE PERMITIR NA MainActivity QUE O TEXTO POSSA SER STRING E NAO APENAS INT
    public TextAnalizer(String texto, Context ctx, List<String> books, MainActivity livros,int NumTextosEncontrados){//,List<String> versicles){
        this.textoRecebido=texto;
        this.context=ctx;
        this.livrosAcrescentar=livros;
        //Receber a arraylist com todos os livros para não estar sempre a reescrever quando a thread é lida
        this.bibleBooks=books;
        this.TextoNumero=NumTextosEncontrados;
        //this.textVersicles=versicles;
    }

    @Override
    public void run() {
        try {
           boolean apenasUmVersiculo=false,algumErro=false;
            //Vai buscar o livro
            livroRecebido=textoRecebido.substring(0,(textoRecebido.indexOf(" "))).toLowerCase();

            //Vais buscar capitulo e versiculo
             Capitulo=textoRecebido.substring(textoRecebido.indexOf(" "),textoRecebido.indexOf(":")).trim();

            textoOriginalVersiculo=textoRecebido.substring(textoRecebido.indexOf(":")+1);
            Versiculo=textoOriginalVersiculo;
            Log.d("Texto",livroRecebido+" "+Capitulo+" "+Versiculo);


            //TODO:Verificações se existe mais de um versiculo poderão não estar 100% corretas
            try {
            if (Versiculo.contains(",")){
                //Faz split da string por , e adiciona isso na arraylist e de seguida remove as , da arraylist
                VersiculosAEncontrar.addAll(Arrays.asList(Versiculo.split(",")));
                for (String versiculo : VersiculosAEncontrar){
                    if (versiculo.contains(",")){
                        versiculo.replace(",","");
                    }
                }
                Log.d("Texto","Contain , ");
            }else {
              //Se não tiver virgulas é provavel que seja apenas um versiculo
              apenasUmVersiculo=true;
            }
            }catch (Exception e) {
                Log.d("Texto","Erro ao ,");
              algumErro=true;
            }

            //Adiciona textos que vão desde X-Y
            try {
            if (Versiculo.contains("-")){
              apenasUmVersiculo=false;
                int PrimeiroVers,UltimoVers;
                PrimeiroVers=Integer.parseInt(Versiculo.substring(0,Versiculo.indexOf("-")));
                UltimoVers=Integer.parseInt(Versiculo.substring(Versiculo.indexOf("-")));
                //Adiciona o numero de todos os versiculos do inicio ao fim
                for (int i=PrimeiroVers;i<=UltimoVers;i++){
                    VersiculosAEncontrar.add(i+"");
                }
                Log.d("Texto","Contain -");
              }
            }catch (Exception e) {
                Log.d("Texto","Erro ao -");
                algumErro=true;
           }
              //Faço as validações anteriores e caso se confirme que apenas tem um versiculo, faço validação
            try {
                Log.d("Texto","Apenas 1 vers");
                if (apenasUmVersiculo){
                    TodosOsVersiculos.add(Integer.parseInt(textoRecebido.substring(textoRecebido.indexOf(":")+1)));
                }
            }catch (Exception e){
                algumErro=true;
            }



              //Receber os versiculos em String por para Int e ordená-los
              try {
                  Log.d("Texto","String to Int");
                for (int l=0;l<=VersiculosAEncontrar.size()-1;l++){
                  TodosOsVersiculos.add(Integer.parseInt(VersiculosAEncontrar.get(l)));
                }
                Collections.sort(TodosOsVersiculos);
              }catch (Exception e){
                  Log.d("Texto","Erro ao ordenar");
                algumErro=true;
              }

            //Verificar se o livro existe, senão o próximo passo é ignorado
             boolean livroExiste=false;
            for (String livro : bibleBooks) {
                if (livroRecebido.contains(livro)) {
                    livroExiste=true;
                    NumeroLivro=bibleBooks.indexOf(livro);
                    Log.d("Ok", "Livro existe é " + livro);
                 }
            }
            if (livroExiste && !algumErro) {
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
                Log.d("Texto","Arraylist contem "+TodosOsVersiculos);
                Log.d("Texto","Versiculo é "+ Versiculo);
                int VersiculoParaAcabar;
                boolean PararCiclo=false;
                boolean ContinuarALer=false;
                //// TODO: É preciso corrigir o ciclo para aceitar mais de um versiculo
                //// TODO: Este ciclo esta a ler o ficheiro e adicionar numa string, mas depois é preciso trabalhar a string
                while ((Conteudo=leitorTexto.readLine())!=null && !PararCiclo){

                    //Buscar o versiculo atual que deve procurar
                      Versiculo=TodosOsVersiculos.get(0)+"";
                      VersiculoParaAcabar=Integer.parseInt(Versiculo)+1;

                    //Começar a ler o ficheiro
                    if (Conteudo.contains("<span id=\"chapter"+Capitulo+"_verse"+Versiculo+"\">")){
                        Log.d("Texto","Encontrei o 1º vers");
                        Log.d("Texto", "Conteudo da linha="+ Conteudo);

                        //Verificar se o texto do versiculo acaba na mesma linha
                        if (Conteudo.contains("<span id=\"chapter"+Capitulo+"_verse"+VersiculoParaAcabar+"\">")){
                            textoDoFicheiro+=Conteudo.substring(Conteudo.indexOf("<span id=\"chapter"+Capitulo+"_verse"+Versiculo+"\">"),Conteudo.indexOf("<span id=\"chapter"+Capitulo+"_verse"+VersiculoParaAcabar+"\">"));
                            Log.d("Texto","Encontrei o último vers na mesma linha, devo parar");
                            TodosOsVersiculos.remove(0);
                            Versiculo="";

                            //Verificar se já encontrou todos os versiculos ou ainda falta algum
                            if (TodosOsVersiculos.size()==0){
                              PararCiclo=true;
                              ContinuarALer=false;
                            }
                        }else{
                            ContinuarALer=true;
                            Log.d("Texto","O último vers não está mesma linha, devo continuar");
                            textoDoFicheiro+=Conteudo.substring(Conteudo.indexOf("<span id=\"chapter"+Capitulo+"_verse"+Versiculo+"\">"));
                        }
                    }

                    //À procura do fim do texto
                    if (!PararCiclo && Conteudo.contains("<span id=\"chapter"+Capitulo+"_verse"+VersiculoParaAcabar+"\">") && Versiculo!=""){
                        Log.d("Texto","O ciclo ainda não encontrou nada e Encontrei agora a linha do ultimo versiculo");
                        textoDoFicheiro+=Conteudo.substring(0,Conteudo.indexOf("<span id=\"chapter"+Capitulo+"_verse"+VersiculoParaAcabar+"\">"));
                        TodosOsVersiculos.remove(0);
                        Versiculo="";

                        //Verificar se já encontrou todos os versiculos ou ainda falta algum
                        if (TodosOsVersiculos.size()==0){
                          PararCiclo=true;
                          ContinuarALer=false;
                        }

                        //Continuar a ler enquanto não encontrou o fim do documento;
                    }else if(ContinuarALer && !Conteudo.contains("<span id=\"chapter"+Capitulo+"_verse"+Versiculo+"\">")){
                      //Adicionar o texto do ficheiro
                        Log.d("Texto","Os versiculos estão em linhas diferentes");
                        textoDoFicheiro+=Conteudo;
                    }
                }

                //Caso os versiculos sejam os ultimos, remover a parte que contem as notas
                if (textoDoFicheiro.contains("^")){
                    textoDoFicheiro=textoDoFicheiro.substring(0,textoDoFicheiro.indexOf("^"));
                }

                //Isto retira todos os vestigios de HTML no texto
                Document doc = Jsoup.parseBodyFragment(textoDoFicheiro);
                 textoFinal = doc.body();
                Log.d("Texto",textoFinal.text());

                //Gera a mensagem para enviar para a outra class com todos os dados
                Message msg=Message.obtain();

                //Gera o texto final que deve aparecer- LIVRO CAPITULO VERSICULO a azul e o resto do texto
                //TODO: corrigir o codigo de cor
                msg.obj="<br><b><font color=\"#2878BB\">"+livroRecebido+" "+ Capitulo+":"+textoOriginalVersiculo+"</font></b><br>"+textoFinal.text();

                msg.arg1=TextoNumero;
                Log.d("Handle","Esta é a thread "+ TextoNumero);

                //TODO: corrigir se encontrar o livro,mas não existir o versiculo ou capitulo, não adicionar no handler
                livrosAcrescentar.handler.sendMessage(msg);
            }else {
                Log.d("Texto","Livro não existe");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Erro",e.getMessage());
        }
    }
}
