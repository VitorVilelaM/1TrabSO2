package main;

import java.util.ArrayList;

/**
 *
 * @author vitor
 */
public class Diretorio {

    private String nome;
    private Diretorio pai;
    private ArrayList<Diretorio> Filhos = new ArrayList();

    public Diretorio(Diretorio dir) {
        this.pai = dir;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Diretorio getPai() {
        return pai;
    }

    public void setPai(Diretorio pai) {
        this.pai = pai;
    }

    public ArrayList<Diretorio> getFilhos() {
        return Filhos;
    }

    public void setFilhos(ArrayList<Diretorio> Filhos) {
        this.Filhos = Filhos;
    }

    public Diretorio buscaCaminho(Diretorio inicio, String caminho[]) {
        Diretorio dirTemp = inicio;
        int i = 1;
        boolean achou = false;

        for (; i < caminho.length - 1; i++) {
            if (caminho[i].equals(".")) {
                dirTemp = dirTemp;
            } else if (caminho[i].equals("..")) {
                dirTemp = dirTemp.getPai();
            } else {
                for (Diretorio atual : dirTemp.getFilhos()) {
                    if (atual.getNome().equals(caminho[i])) {
                        dirTemp = atual;
                        achou = true;
                    }
                }
                if (!achou) {
                    return null;
                }

            }
        }
        return dirTemp;
    }

    public boolean verificaNomeFilhos(Diretorio dirTemp, String nome) {
        for (Diretorio atual : dirTemp.getFilhos()) {
            if (atual.getNome().equals(nome)) {
                return false;
            }
        }
        return true;
    }
}
