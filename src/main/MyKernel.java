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

            dirTemp = dirAtual;

            if (instrucao[0].equals("-l")) {
                for (Diretorio dirTempo : dirTemp.getFilhos()) {
                    result += dirTempo.getPermissao() + " " + dirTempo.getDataCriacao() + " " + dirTempo.getNome() + "\n";
                }

            } else if (instrucao[0].equals("")) {
                for (Diretorio dirTempo : dirTemp.getFilhos()) {
                    result += dirTempo.getNome() + " ";
                }
                for (Arquivos arqTempo : dirTemp.getArquivos()) {
                    result += arqTempo.getNome() + " ";
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
        String[] caminho = parameters.split("/");
        String nome = caminho[caminho.length - 1];
        int j, i = 0;
        Diretorio dirTemp = verificaCaminho(caminho, false);

        for (j = 0; j < dirTemp.getFilhos().size(); j++) {
            if (dirTemp.getFilhos().get(j).getNome().equals(nome)) {
                if (dirTemp.getFilhos().get(i).getFilhos().size() == 0) {
                    dirTemp.getFilhos().remove(i);
                    i = 0;
                    return result;
                } else {
                    result = "possui arquivos e/ou diretorios. (Nada foi removido)";
                }
            } else {
                i++;
            }
        }
        if (j == dirTemp.getFilhos().size()) {
            result = "diretorio nao encontrado";
        }
//fim da implementacao do aluno
        return result;
    }

    public String cp(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: cp");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        int i = 0, j;
        boolean mudarNome = false;
        String[] comando = parameters.split(" ");

        if (comando.length == 2 && !comando[0].contains("R")) {

            String[] caminho1 = comando[0].split("/");
            String[] caminho2 = comando[1].split("/");
            String nome = caminho1[caminho1.length - 1];
            String novoNome = "";
            Diretorio dirOrigem = verificaCaminho(caminho1, false);
            Diretorio dirDestino;

            if (caminho2[caminho2.length - 1].contains(".txt")) {
                dirDestino = verificaCaminho(caminho2, false);
                mudarNome = true;
                novoNome = caminho2[caminho2.length - 1];
            } else {
                dirDestino = verificaCaminho(caminho2, true);
            }

            for (j = 0; j < dirOrigem.getArquivos().size(); j++) {
                if (dirOrigem.getArquivos().get(j).getNome().equals(nome)) {
                    if (dirDestino.verificaNomeArquivos(dirDestino, nome)) {
                        if (mudarNome) {
                            Arquivos arqAux = dirOrigem.getArquivos().get(i);
                            arqAux.setNome(novoNome);
                            dirDestino.getArquivos().add(arqAux);
                            i = 0;

                        } else {
                            dirDestino.getArquivos().add(dirOrigem.getArquivos().get(i));
                            i = 0;
                        }

                        return result;
                    } else {
                        result = "diretorio destino ja possui esse arquivo";
                    }
                } else {
                    i++;
                }
            }
            if (j == dirOrigem.getArquivos().size()) {
                result = "Arquivo nao encontrado";
            }

        } else if (comando.length == 3 && comando[0].contains("R")) {

            String[] caminho1 = comando[1].split("/");
            String[] caminho2 = comando[2].split("/");
            String nome = caminho1[caminho1.length - 1];

            Diretorio dirOrigem = verificaCaminho(caminho1, false);
            Diretorio dirDestino = verificaCaminho(caminho2, true);

            for (j = 0; j < dirOrigem.getFilhos().size(); j++) {
                System.out.println(nome);
                if (dirOrigem.getFilhos().get(j).getNome().equals(nome)) {
                    if (dirDestino.verificaNomeFilhos(dirDestino, nome)) {
                        dirDestino.getFilhos().add(dirOrigem.getFilhos().get(i));
                        i = 0;
                        return result;
                    } else {
                        result = "diretorio destino ja possui esse diretorio";
                    }
                } else {
                    i++;
                }
            }
            if (j == dirOrigem.getFilhos().size()) {
                result = "Diretorio nao encontrado";
            }

        } else {
            result = "comando incorreto";
        }

        //fim da implementacao do aluno
        return result;
    }

    public String mv(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: mv");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        int i = 0, j;
        boolean mudarNome = false;
        String[] comando = parameters.split(" ");

        if (comando.length == 2) {
            String[] caminho1 = comando[0].split("/");
            String[] caminho2 = comando[1].split("/");
            String nome = caminho1[caminho1.length - 1];
            String novoNome = "";
            Diretorio dirOrigem = verificaCaminho(caminho1, false);
            Diretorio dirDestino = verificaCaminho(caminho2, true);

            if (nome.contains(".txt")) {
                for (j = 0; j < dirOrigem.getArquivos().size(); j++) {
                    if (dirOrigem.getArquivos().get(j).getNome().equals(nome)) {
                        if (dirDestino.verificaNomeArquivos(dirDestino, nome)) {
                            if (mudarNome) {
                                Arquivos arqAux = dirOrigem.getArquivos().remove(i);
                                arqAux.setNome(novoNome);
                                dirDestino.getArquivos().add(arqAux);
                                i = 0;

                            } else {
                                dirDestino.getArquivos().add(dirOrigem.getArquivos().remove(i));
                                i = 0;
                            }

                            return result;
                        } else {
                            result = "diretorio destino ja possui esse arquivo";
                        }
                    } else {
                        i++;
                    }
                    if (j == dirOrigem.getArquivos().size()) {
                        result = "Arquivo nao encontrado";
                    }
                }
            } else {
                j = 0;
                for (j = 0; j < dirOrigem.getFilhos().size(); j++) {
                    System.out.println(dirOrigem.getNome() + " para " + dirDestino.getNome());
                    if (dirOrigem.getFilhos().get(j).getNome().equals(nome)) {
                        if (dirDestino.verificaNomeFilhos(dirDestino, nome)) {
                            dirDestino.getFilhos().add(dirOrigem.getFilhos().remove(i));
                            i = 0;
                            return result;
                        } else {
                            result = "diretorio destino ja possui esse diretorio";
                        }
                    } else {
                        i++;
                    }
                }
                if (j == dirOrigem.getFilhos().size()) {
                    result = "Diretorio nao encontrado";
                }
            }
        } else {
            result = "comando incorreto";
        }

        //fim da implementacao do aluno
        return result;
    }

    public String rm(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: rm");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        int i = 0, j;
        String[] comando = parameters.split(" ");
        if (comando.length == 1 && !comando[0].contains("R")) {

            String[] caminho1 = comando[0].split("/");
            String nome = caminho1[caminho1.length - 1];
            Diretorio dirOrigem = verificaCaminho(caminho1, false);

            for (j = 0; j < dirOrigem.getArquivos().size(); j++) {
                if (dirOrigem.getArquivos().get(j).getNome().equals(nome)) {
                    dirOrigem.getArquivos().remove(i);
                    i = 0;
                    return result;
                } else {
                    i++;
                }
            }
            if (j == dirOrigem.getArquivos().size()) {
                result = "Arquivo nao existente!";
            }

        } else if (comando.length == 2 && comando[0].contains("R")) {

            String[] caminho1 = comando[1].split("/");
            String nome = caminho1[caminho1.length - 1];
            Diretorio dirOrigem = verificaCaminho(caminho1, false);

            for (j = 0; j < dirOrigem.getFilhos().size(); j++) {
                if (dirOrigem.getFilhos().get(j).getNome().equals(nome)) {
                    dirOrigem.getFilhos().remove(i);
                    i = 0;
                    return result;
                } else {
                    i++;
                }
            }
            if (j == dirOrigem.getFilhos().size()) {
                result = "Arquivo nao existente!";
            }

        } else {
            result = "comando incorreto";
        }

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
        String[] comando = parameters.split(".txt");
        String[] caminho = comando[0].split("/");

        if (comando.length > 1) {
            String ajuste = comando[1].replace("\\", "/");
            String[] conteudo = ajuste.split("/n");
            //createfile ./disciplina.txt SistemasOperacionais\nTrabalho Pratico 1
            String nome = caminho[caminho.length - 1];
            System.out.println(conteudo[1]);
            
            Diretorio dirTemp = verificaCaminho(caminho, false);
            if (dirTemp != null) {
                if (verificaNome(nome)) {
                    if (dirTemp.verificaNomeArquivos(dirTemp, nome)) {
                        Arquivos novo = new Arquivos(dirTemp);
                        nome = nome + ".txt";
                        novo.setNome(nome);
                        for(int i = 0; i < conteudo.length; i++) novo.getConteudo().add(conteudo[i]);
                        dirTemp.getArquivos().add(novo);
                    } else {
                        result = "Ja existe um arquivo com esse nome!";
                    }
                } else {
                    result = "Nome informado é invalido!";
                }
            } else {
                result = "Erro no caminho informado!";
            }
        } else {
            result = "Erro no comando inserido";
        }

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
        String name = "Vitor Vilela";
        //numero de matricula
        String registration = "2020.110.200.22";
        //versao do sistema de arquivos
        String version = "1.0";

        result += "Nome do Aluno:        " + name;
        result += "\nMatricula do Aluno:   " + registration;
        result += "\nVersao do Kernel:     " + version;

        return result;
    }

}
