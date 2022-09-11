package main;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author vitor
 */
public class Diretorio {

    private String name;
    private Diretorio dirPai;
    private ArrayList<Diretorio> filhos;
    private ArrayList<Arquivo> arquivos;
    private Date dataCriacao;

    public Diretorio(Diretorio pai, String nomeDiretorio) {
        this.dirPai = pai;
        this.filhos = new ArrayList();
        this.arquivos = new ArrayList();
        this.name = nomeDiretorio;
        //this.dataCricao = new Date(System.currentTimeMillis);
        //SimpleDateFormat sdf = new SimpleDateFormat("MM dd HH:mm:ss");
        //sdf.format(dataCriacao);s
    }

    public void addFilhos(Diretorio novoFilho) {
        filhos.add(novoFilho);
    }
    
    public Diretorio novoDirAtual(int i){
        Diretorio novoDiretorioAtual = filhos.get(i);
       
        return novoDiretorioAtual;
    }
    
    public void getFilhos() {
        int i = 0;
        while (i < filhos.size()) {
            System.out.println(filhos.get(i).getNome());
            i++;
        }
    }

    public int procuraFilhos(String nomeDir) {
        int i = 0, position = -1;
        boolean result = false;
        
        while (i <= filhos.size()-1 && !result) {
            if (filhos.get(i).getNome().equals(nomeDir)) {
                result = true;
                position = i;
            } else {
                i++;
            }

        }

        return position;
    }

    public String getNome() {
        return name;
    }

}
