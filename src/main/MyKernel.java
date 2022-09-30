package main;

import jdk.tools.jlink.plugin.Plugin;
import operatingSystem.Kernel;

/**
 * Kernel desenvolvido pelo aluno. Outras classes criadas pelo aluno podem ser
 * utilizadas, como por exemplo: - Arvores; - Filas; - Pilhas; - etc...
 *
 * @author nome do aluno...
 */
public class MyKernel implements Kernel {

    Diretorio raiz = new Diretorio(null);
    Diretorio dirAtual = new Diretorio(raiz);

    public MyKernel() {
        raiz.setPai(raiz);
        dirAtual = raiz;
    }

    private Diretorio verificaCaminho(String Caminho[], boolean finalCaminho) {
        Diretorio dirTemp;

        if (Caminho == null) {
            return null;
        } else {
            if (Caminho[0].equals("")) {
                dirTemp = raiz;
            } else {
                dirTemp = dirAtual;
            }
        }

        dirTemp = dirTemp.buscaCaminho(dirTemp, Caminho, finalCaminho);

        return dirTemp;
    }

    public boolean verificaNome(String nome) {

        if (nome.contains(".")) {
            return false;
        } else if (nome.trim().equals("")) {
            return false;
        } else if ((Character) nome.charAt(0) == '-') {
            return false;
        }

        return true;
    }

    public String ls(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: ls");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        Diretorio dirTemp;
        String[] instrucao = parameters.split(" ");

        if (instrucao.length == 1) {

            System.out.println(instrucao[0].equals(""));

            dirTemp = dirAtual;

            if (instrucao[0].equals("-l")) {
                for (Diretorio dirTempo : dirTemp.getFilhos()) {
                    result += dirTempo.getPermissao() + " " + dirTempo.getDataCriacao() + " " + dirTempo.getNome() + "\n";
                }

            } else if (instrucao[0].equals("")) {
                for (Diretorio dirTempo : dirTemp.getFilhos()) {
                    result += dirTempo.getNome() + " ";
                }
            } else {
                String[] caminho = instrucao[0].split("/");
                dirTemp = verificaCaminho(caminho, true);

                for (Diretorio dirTempo : dirTemp.getFilhos()) {
                    result += dirTempo.getNome() + " ";
                }
            }

        } else {
            String[] caminho = instrucao[1].split("/");
            dirTemp = verificaCaminho(caminho, true);

            for (Diretorio dirTempo : dirTemp.getFilhos()) {
                result += dirTempo.getPermissao() + " " + dirTempo.getDataCriacao() + " " + dirTempo.getNome() + "\n";
            }
        }
        //fim da implementacao do aluno
        return result;
    }

    public String mkdir(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: mkdir");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        String[] caminho = parameters.split("/");
        String nome = caminho[caminho.length - 1];

        Diretorio dirTemp = verificaCaminho(caminho, false);

        if (dirTemp != null) {
            if (verificaNome(nome)) {
                if (dirTemp.verificaNomeFilhos(dirTemp, nome)) {
                    Diretorio novo = new Diretorio(dirTemp);
                    novo.setNome(nome);
                    dirTemp.getFilhos().add(novo);
                } else {
                    result = "Ja existe uma pasta com esse nome!";
                }
            } else {
                result = "Nome informado é invalido!";
            }
        } else {
            result = "Erro no caminho informado!";
        }
        //fim da implementacao do aluno
        return result;
    }

    public String cd(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        String currentDir = "";
        System.out.println("Chamada de Sistema: cd");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        System.out.println(dirAtual.getNome());
        String[] caminho = parameters.split("/");
        Diretorio dirTemp = verificaCaminho(caminho, true);

        if (dirTemp != null && dirTemp.verificaNomeFilhos(dirTemp, caminho[caminho.length - 1])) {
            dirAtual = dirTemp;

            if (dirAtual == raiz) {
                currentDir = "/";
            } else {
                currentDir = dirAtual.getNome();
            }
        } else {
            result = "Diretorio nao encontrado!";
        }
        //indique o diretório atual. Por exemplo... /

        //setando parte gráfica do diretorio atual
        operatingSystem.fileSystem.FileSytemSimulator.currentDir = currentDir;

        //fim da implementacao do aluno
        return result;
    }

    public String rmdir(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: rmdir");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        //fim da implementacao do aluno
        return result;
    }

    public String cp(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: cp");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        //fim da implementacao do aluno
        return result;
    }

    public String mv(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: mv");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        //fim da implementacao do aluno
        return result;
    }

    public String rm(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: rm");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        //fim da implementacao do aluno
        return result;
    }

    public String chmod(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: chmod");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        //fim da implementacao do aluno
        return result;
    }

    public String createfile(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: createfile");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        //fim da implementacao do aluno
        return result;
    }

    public String cat(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: cat");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        //fim da implementacao do aluno
        return result;
    }

    public String batch(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: batch");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        //fim da implementacao do aluno
        return result;
    }

    public String dump(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: dump");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        //fim da implementacao do aluno
        return result;
    }

    public String info() {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: info");
        System.out.println("\tParametros: sem parametros");

        //nome do aluno
        String name = "Fulano da Silva";
        //numero de matricula
        String registration = "2001.xx.yy.00.11";
        //versao do sistema de arquivos
        String version = "0.1";

        result += "Nome do Aluno:        " + name;
        result += "\nMatricula do Aluno:   " + registration;
        result += "\nVersao do Kernel:     " + version;

        return result;
    }

}
