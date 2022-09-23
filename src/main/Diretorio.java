package main;

import java.util.ArrayList;

/**
 *
 * @author vitor
 */
public class Diretorio {
    private Diretorio dirPai;
    private ArrayList<Diretorio> filhos;
    private ArrayList<Arquivo> arquivos;
    
    
    public Diretorio ( Diretorio pai){
        this.dirPai = pai;
    }
}
