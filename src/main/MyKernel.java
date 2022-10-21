package main;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

                for (Arquivos arqAtual : dirTemp.getArquivos()) {
                    result += arqAtual.getPermissao() + " " + arqAtual.getDataCriacao() + " " + arqAtual.getNome() + "\n";
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

                if (dirOrigem.getFilhos().get(j).getNome().equals(nome)) {
                    if (dirDestino.verificaNomeFilhos(dirDestino, nome)) {
                        Diretorio novo = null;
                        try {
                            novo = (Diretorio) dirOrigem.getFilhos().get(i).clone();
                        } catch (CloneNotSupportedException ex) {
                            Logger.getLogger(MyKernel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        novo.setPai(dirDestino);
                        dirDestino.getFilhos().add(novo);
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
        if (comando.length == 1 && !comando[0].contains("-R")) {

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
        String[] comando = parameters.split(" "), caminho;
        String nome;
        Arquivos arqModificado;
        Diretorio dirOrigem;
        int i, j;
        String newPermission;

        if (comando.length == 3 && comando[0].contains("-R")) {

            newPermission = converteCHMOD(comando[1].split(""));
            caminho = comando[2].split("/");
            dirOrigem = verificaCaminho(caminho, true);

            if (dirOrigem != null) {
                newPermission = newPermission;
                alteraPermissaoFilhos(dirOrigem, newPermission);

            } else {
                result = "diretorio nao encontrado";
            }

        } else if (comando.length == 2) {

            caminho = comando[1].split("/");
            nome = caminho[caminho.length - 1];
            newPermission = converteCHMOD(comando[0].split(""));

            if (nome.contains(".txt")) {
                newPermission = "-" + newPermission;
                dirOrigem = verificaCaminho(caminho, false);

                Arquivos arqDestino = dirOrigem.buscaArquivoPorNome(dirOrigem, nome);
                arqDestino.setPermissao(newPermission);

            } else {
                newPermission = "d" + newPermission;
                dirOrigem = verificaCaminho(caminho, true);
                dirOrigem.setPermissao(newPermission);
            }
        } else {
            result = "comando incorreto";
        }
        //fim da implementacao do aluno
        return result;
    }

    public void alteraPermissaoFilhos(Diretorio dirOrigem, String permissao) {
        String permissaoDir = "d" + permissao;
        String permissaoArq = "-" + permissao;

        for (Diretorio atual : dirOrigem.getFilhos()) {
            if (atual != null) {
                alteraPermissaoFilhos(atual, permissao);
                atual.setPermissao(permissaoDir);

                for (Arquivos arqAtual : atual.getArquivos()) {
                    arqAtual.setPermissao(permissaoArq);
                }
            }
        }

        dirOrigem.setPermissao(permissaoDir);

    }

    public String converteCHMOD(String[] chmod) {
        String permissao = "";
        if (chmod.length == 3) {
            for (String position : chmod) {
                if (position.equals("0")) {
                    permissao = permissao + "---";
                } else if (position.equals("1")) {
                    permissao = permissao + "--x";
                } else if (position.equals("2")) {
                    permissao = permissao + "-w-";
                } else if (position.equals("3")) {
                    permissao = permissao + "-wx";
                } else if (position.equals("4")) {
                    permissao = permissao + "r--";
                } else if (position.equals("5")) {
                    permissao = permissao + "r-x";
                } else if (position.equals("6")) {
                    permissao = permissao + "rw-";
                } else if (position.equals("7")) {
                    permissao = permissao + "rwx";
                }
            }

        }

        return permissao;
    }

    public String createfile(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: createfile");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        String[] comando = parameters.split(".txt ");
        String[] caminho = comando[0].split("/");

        if (comando.length > 1) {
            String ajuste = comando[1].replace("\\", "/");
            String[] conteudo = ajuste.split("/n");
            //createfile ./disciplina.txt Sistemas Operacionais\nTrabalho Pratico 1
            String nome = caminho[caminho.length - 1];

            Diretorio dirTemp = verificaCaminho(caminho, false);
            if (dirTemp != null) {
                if (verificaNome(nome)) {
                    if (dirTemp.verificaNomeArquivos(dirTemp, nome)) {
                        Arquivos novo = new Arquivos(dirTemp);
                        nome = nome + ".txt";
                        novo.setNome(nome);
                        dirTemp.getArquivos().add(novo);

                        for (int i = 0; i < conteudo.length; i++) {
                            novo.getConteudo().add(i, conteudo[i]);
                        }
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
        String[] caminho = parameters.split("/");
        int i, j = 0;
        if (caminho != null) {
            Diretorio dirTemp = verificaCaminho(caminho, false);
            String nome = caminho[caminho.length - 1];

            if (dirTemp != null) {
                for (Arquivos atual : dirTemp.getArquivos()) {
                    if (atual.getNome().equals(nome)) {
                        for (i = 0; i < atual.getConteudo().size(); i++) {
                            result += atual.getConteudo().get(i) + "\n";
                        }
                    } else {
                        j++;
                    }
                }
                if (j == dirTemp.getArquivos().size()) {
                    result = "arquivo não encontrado";
                }

            } else {
                result = "nao foi possivel encontrar esse diretorio";
            }
        } else {
            result = "comando incorreto";
        }
        //fim da implementacao do aluno
        return result;
    }

    public String batch(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: batch");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        //./testes/emLote.txt
        int i;
        String comando, parametros;
        List<String> config = FileManager.stringReader("./testes/emLote.txt");
        for (i = 0; i < config.size(); i++) {
            comando = config.get(i).substring(0, config.get(i).indexOf(" "));
            parametros = config.get(i).substring(config.get(i).indexOf(" "));

            if (comando.equals("ls")) {
            } else if (comando.equals("mkdir")) {
                mkdir(parametros);
            } else if (comando.equals("cd")) {
                cd(parametros);
            } else if (comando.equals("rmdir")) {
                rmdir(parametros);
            } else if (comando.equals("cp")) {
                cp(parametros);
            } else if (comando.equals("mv")) {
                mv(parametros);
            } else if (comando.equals("rm")) {
                rm(parametros);
            } else if (comando.equals("chmod")) {
                chmod(parametros);
            } else if (comando.equals("createfile")) {
                createfile(parametros);
            } else if (comando.equals("cat")) {
                cat(parametros);
            } else if (comando.equals("batch")) {
                batch(parametros);
            } else if (comando.equals("dump")) {
                dump(parametros);
            } else {
                result = "comando incorreto";
            }
        }
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
        String version = "1.21";

        result += "Nome do Aluno:        " + name;
        result += "\nMatricula do Aluno:   " + registration;
        result += "\nVersao do Kernel:     " + version;

        return result;
    }

}
