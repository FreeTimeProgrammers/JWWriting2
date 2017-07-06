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

            //try {
                //Vai buscar o livro
                livroRecebido=textoRecebido.substring(0,(textoRecebido.indexOf(" "))).toLowerCase();
                //Vais buscar capitulo e versiculo
                Capitulo=textoRecebido.substring(textoRecebido.indexOf(" "),textoRecebido.indexOf(":")).trim();

                textoOriginalVersiculo=textoRecebido.substring(textoRecebido.indexOf(":")+1);
                Versiculo=textoOriginalVersiculo;
                Log.d("Texto",livroRecebido+" "+Capitulo+" "+Versiculo);
            //}catch (Exception e){
              //  algumErro=true;
            //}


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
            }else {
              //Se não tiver virgulas é provavel que seja apenas um versiculo
              apenasUmVersiculo=true;
            }
            }catch (Exception e) {
                Log.d("Texto","Erro ao ,");
              algumErro=true;
            }
            Log.d("Texto","Arraylist contem "+VersiculosAEncontrar);
            //Adiciona textos que vão desde X-Y
            try {
            if (Versiculo.contains("-")){
              apenasUmVersiculo=false;
                int PrimeiroVers,UltimoVers;
                PrimeiroVers=Integer.parseInt(Versiculo.substring(0,Versiculo.indexOf("-")));
                if (Versiculo.substring(Versiculo.indexOf("-")+1).contains(",")){
                    String test1=Versiculo.substring(Versiculo.indexOf("-")+1);
                    int Indice=test1.indexOf(",");
                    Log.d("Texto","Tem , "+ Versiculo.substring(Versiculo.indexOf("-")+1,Versiculo.indexOf("-")+1+Indice));
                    UltimoVers=Integer.parseInt( Versiculo.substring(Versiculo.indexOf("-")+1,Versiculo.indexOf("-")+1+Indice));

                }else {
                    UltimoVers=Integer.parseInt(Versiculo.substring(Versiculo.indexOf("-")+1));
                }

                Log.d("Texto","1ºvers-"+PrimeiroVers+ " Lastº vers-"+UltimoVers);
                //Adiciona o numero de todos os versiculos do inicio ao fim
                if (PrimeiroVers<UltimoVers){
                    for (int i=PrimeiroVers;i<=UltimoVers;i++){
                        VersiculosAEncontrar.add(i+"");
                    }
                }else {
                    algumErro=true;
                }
              }
            }catch (Exception e) {
                Log.d("Texto","Erro ao - "+ e.getMessage());
                algumErro=true;
           }
              //Faço as validações anteriores e caso se confirme que apenas tem um versiculo, faço validação

                if (apenasUmVersiculo){
                    Log.d("Texto","Apenas 1 vers");
                    try {
                        TodosOsVersiculos.add(Integer.parseInt(textoRecebido.substring(textoRecebido.indexOf(":") + 1)));
                    }catch (Exception e){
                        algumErro=true;
                    }
                }


              //Receber os versiculos em String por para Int e ordená-los
              try {
                  Log.d("Texto","Arraylist string contem="+VersiculosAEncontrar);
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
                boolean irProximoTexto=false;
                //// TODO: É preciso corrigir o ciclo para aceitar mais de um versiculo
                //// TODO: Este ciclo esta a ler o ficheiro e adicionar numa string, mas depois é preciso trabalhar a string
                Versiculo=TodosOsVersiculos.get(0)+"";
                VersiculoParaAcabar=TodosOsVersiculos.get(0)+1;
                 int MainWhile=0,SecondWhile=0;
                while ((Conteudo=leitorTexto.readLine())!=null && !PararCiclo){

                    //TODO: depois alterar estes killers xD
                        MainWhile++;
                        if (MainWhile>=1000){
                            break;
                        }
                        while ((Conteudo.contains("<span id=\"chapter"+Capitulo+"_verse"+Versiculo+"\">") || ContinuarALer )) {

                            //TODO: depois alterar estes killers xD
                            SecondWhile++;
                            if (SecondWhile>=1000){
                                break;
                            }
                            if (ContinuarALer) { //Se esta condição for verdadeira significa que estou à procura do versiculo final/ ou mais versiculos na mesma linha

                                if (Conteudo.contains("<span id=\"chapter"+Capitulo+"_verse"+VersiculoParaAcabar+"\">")) {
                                    textoDoFicheiro += Conteudo.substring(0, Conteudo.indexOf("<span id=\"chapter" + Capitulo + "_verse" + VersiculoParaAcabar + "\">"));
                                    TodosOsVersiculos.remove(0);
                                    if (TodosOsVersiculos.size()==0){
                                        Log.d("Texto","Vou dar break");
                                        PararCiclo=true;
                                        break;
                                    }else {
                                        Versiculo=TodosOsVersiculos.get(0)+"";
                                        VersiculoParaAcabar=TodosOsVersiculos.get(0)+1;
                                        Log.d("Texto","Vou procurar o próximo vers "+Versiculo);
                                    }
                                    ContinuarALer=false;
                                }else {
                                    ContinuarALer=true;
                                    break;
                                }
                            }else { //Se a condição for falsa significa que estou à 1º procura do versiculo

                                if (Conteudo.contains("<span id=\"chapter"+Capitulo+"_verse"+VersiculoParaAcabar+"\">")){ //Se encontrar significa que o versiculo esta todo na mesma linha
                                    textoDoFicheiro+=Conteudo.substring(Conteudo.indexOf("<span id=\"chapter"+Capitulo+"_verse"+Versiculo+"\">"),Conteudo.indexOf("<span id=\"chapter"+Capitulo+"_verse"+VersiculoParaAcabar+"\">"));
                                    Log.d("Texto","Encontrei o último vers na mesma linha, devo parar");
                                    TodosOsVersiculos.remove(0);
                                    if (TodosOsVersiculos.size()==0){
                                        Log.d("Texto","Vou dar break");
                                        PararCiclo=true;
                                        break;
                                    }else{
                                        Versiculo=TodosOsVersiculos.get(0)+"";
                                        VersiculoParaAcabar=TodosOsVersiculos.get(0)+1;
                                        Log.d("Texto","Vou procurar o próximo vers "+Versiculo);
                                    }
                                    ContinuarALer=false;

                                }else {  //Se for ao else, significa que o versiculo continua noutra linha
                                    textoDoFicheiro+=Conteudo.substring(Conteudo.indexOf("<span id=\"chapter"+Capitulo+"_verse"+Versiculo+"\">"));
                                    ContinuarALer=true;
                                    break;
                                }
                        }

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
                livroRecebido=livroRecebido.substring(0,1).toUpperCase()+livroRecebido.substring(1);
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




//Buscar o versiculo atual que deve procurar
                      /*Versiculo=TodosOsVersiculos.get(0)+"";
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
                              }*/