package main;

import static binary.Binario.binaryStringToInt;
import static binary.Binario.intToBinaryString;
import hardware.HardDisk;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.tools.jlink.plugin.Plugin;
import operatingSystem.Kernel;

/**
 * Kernel desenvolvido pelo aluno. Outras classes criadas pelo aluno podem ser
 * utilizadas, como por exemplo: - Arvores; - Filas; - Pilhas; - etc...
 *
 * @author Vitor Vilela Moraes
 */
public final class MyKernel implements Kernel {

    Diretorio raiz = new Diretorio(null);
    Diretorio dirAtual = new Diretorio(raiz);
    HardDisk HD = new HardDisk(4);
    int positionHD, positionDirAtual;

    public MyKernel() {
        raiz.setNome("Vitor");
        raiz.setPai(raiz);
        dirAtual = raiz;

        positionHD = 0;
        positionDirAtual = 0;
        HD.inicializarMemoriaSecundaria();
        salvaDiretorioNoHD("Vitor", 0);
    }

    public int verificaOrigem(String Caminho[], boolean finalCaminho) {
        int dirTemp;

        if (Caminho == null) {
            return -1;
        } else {
            switch (Caminho[0]) {
                case "":
                    dirTemp = 0;
                    break;
                case "..":
                    String binario = retornaBinario(positionDirAtual + 880, (positionDirAtual + 880 + 16));
                    dirTemp = binaryStringToInt(binario);
                    break;
                default:
                    dirTemp = positionDirAtual;
                    break;
            }
        }
        if (dirTemp >= 0) {
            dirTemp = percorreCaminho(dirTemp, Caminho, finalCaminho);
        }
        return dirTemp;
    }

    public int percorreCaminho(int dirTemp, String[] Caminho, boolean finalCaminho) {
        int diretorio = dirTemp, limite, i = 0, positionFilhos, limiteDiretorio;
        boolean nomeEncontrado = false;
        String nome;

        if (finalCaminho) {
            limite = Caminho.length;
        } else {
            limite = Caminho.length - 1;
        }

        if (Caminho.length == 1) {
            return diretorio;
        }

        while (i < limite) {

            if (Caminho[i].equals("..")) {
                String binario = retornaBinario(positionDirAtual + 880, (positionDirAtual + 880 + 16));
                diretorio = binaryStringToInt(binario);
            } else if (Caminho[i].equals(".")) {
                diretorio = diretorio;
            } else if (Caminho[i].equals("")) {
                diretorio = 0;
            } else {
                limiteDiretorio = diretorio + 2496;
                diretorio += 896;
                while (diretorio < (limiteDiretorio) && !nomeEncontrado) {

                    String binario = retornaBinario(diretorio, (diretorio + 16));
                    positionFilhos = binaryStringToInt(binario);

                    nome = retornaString(positionFilhos, positionFilhos + (80 * 8));
                    if (Caminho[i].equals(nome)) {
                        diretorio = binaryStringToInt(binario);
                        nomeEncontrado = true;
                        dirTemp = diretorio;
                    } else {
                        diretorio += 16;
                        dirTemp = -1;
                    }
                }
                nomeEncontrado = false;
            }

            i++;
        }

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

    public boolean seExisteNome(String nomeProcurado, int posicao) {
        int diretorio = posicao + 896, limiteDiretorio, position;
        boolean encontrado = false;
        String nome;

        limiteDiretorio = diretorio + 2496;

        while (diretorio < (limiteDiretorio) && !encontrado) {

            String binario = retornaBinario(diretorio, (diretorio + 16));
            position = binaryStringToInt(binario);

            nome = retornaString(position, position + (80 * 8));
            if (nomeProcurado.equals(nome)) {
                return false;
            } else {
                diretorio += 16;
            }
        }

        return true;
    }

    public int retornaPosicaoPorNome(String nomeProcurado, int posicao) {
        int diretorio = posicao, limiteDiretorio, position, posicaoEncontrada = -1;;
        boolean encontrado = false;
        String nome;

        limiteDiretorio = diretorio + 2496;
        if (nomeProcurado.equals("..")) {
            String binario = retornaBinario(positionDirAtual + 880, (positionDirAtual + 880 + 16));
            return binaryStringToInt(binario);
        } else if (nomeProcurado.equals(".")) {
            return diretorio;
        } else if (nomeProcurado.equals("")) {
            return 0;
        } else {
            while (diretorio < (limiteDiretorio) && !encontrado) {

                String binario = retornaBinario(diretorio, (diretorio + 16));
                position = binaryStringToInt(binario);

                nome = retornaString(position, position + (80 * 8));
                if (nomeProcurado.equals(nome)) {
                    return position;
                } else {
                    diretorio += 16;
                }
            }
        }

        return posicaoEncontrada;
    }

    public String ls(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: ls");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        String[] instrucao = parameters.split(" ");
        String binario = "";
        int posicaoHDInicio = positionDirAtual + 896, posicao;
        boolean description;

        if (instrucao[0].equals("-l")) {
            description = true;
        } else {
            description = false;
        }
        if (instrucao.length == 1) {
            binario = retornaBinario(posicaoHDInicio, (posicaoHDInicio + 16));
            posicao = binaryStringToInt(binario);

            while (posicao > 0) {

                if (description) {
                    result += remontarDocs(description, posicao) + "\n";
                } else {
                    result += remontarDocs(description, posicao) + " ";
                }

                posicaoHDInicio += 16;
                binario = retornaBinario(posicaoHDInicio, (posicaoHDInicio + 16));
                posicao = binaryStringToInt(binario);
            }

        } else {
            String[] caminho = instrucao[1].split("/");
            int dirTemp = verificaOrigem(caminho, false);
            int posicaoPorNome = retornaPosicaoPorNome(caminho[caminho.length - 1], dirTemp + 896);

            if (dirTemp >= 0 && posicaoPorNome >= 0) {

                posicaoPorNome += 896;
                binario = retornaBinario(posicaoPorNome, (posicaoPorNome + 16));
                posicao = binaryStringToInt(binario);

                while (posicao > 0) {
                    if (description) {
                        result += remontarDocs(description, posicao) + "\n";
                    } else {
                        result += remontarDocs(description, posicao) + " ";
                    }

                    posicaoPorNome += 16;
                    binario = retornaBinario(posicaoPorNome, (posicaoPorNome + 16));
                    posicao = binaryStringToInt(binario);
                }
            } else {
                result = "nao foi possivel encontrar o diretorio";
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

        int dirTemporario = verificaOrigem(caminho, false);
        if (verificaNome(nome)) {
            if (dirTemporario >= 0) {
                if (seExisteNome(nome, dirTemporario)) {
                    salvaDiretorioNoHD(nome, dirTemporario);
                } else {
                    result = "nome informado já existe";
                }
            } else {
                result = "Erro no caminho informado!";
            }
        } else {
            result = "nome infomado invalido";
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
        int dirTemp = verificaOrigem(caminho, false);
        int posicaoPorNome = retornaPosicaoPorNome(caminho[caminho.length - 1], dirTemp + 896);
        if (dirTemp >= 0 && posicaoPorNome >= 0) {
            positionDirAtual = posicaoPorNome;

            if (positionDirAtual == 0) {
                currentDir = "/";
            } else {
                currentDir = remontarDocs(false, positionDirAtual);
            }
        } else {
            result = "Diretório nao encontrado!";
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
        Diretorio dirTemp = dirAtual;

        for (j = 0; j < dirTemp.getFilhos().size(); j++) {
            if (dirTemp.getFilhos().get(j).getNome().equals(nome)) {
                if (verificaHDVazio(1)) {
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

        if (comando.length == 2 && !comando[0].contains("-R")) {

            String[] caminho1 = comando[0].split("/");
            String[] caminho2 = comando[1].split("/");
            String nome = caminho1[caminho1.length - 1];
            String novoNome = "";
            Diretorio dirOrigem = dirAtual;
            Diretorio dirDestino;

            if (caminho2[caminho2.length - 1].contains(".txt")) {
                dirDestino = dirAtual;;
                mudarNome = true;
                novoNome = caminho2[caminho2.length - 1];
            } else {
                dirDestino = dirAtual;
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
                        result = "Diretório destino já possui esse arquivo";
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

            Diretorio dirOrigem = dirAtual;;
            Diretorio dirDestino = dirAtual;

            for (j = 0; j < dirOrigem.getFilhos().size(); j++) {

                if (dirOrigem.getFilhos().get(j).getNome().equals(nome)) {
                    if (dirDestino.verificaNomeFilhos(dirDestino, nome)) {
                        Diretorio novo = null;
                        try {
                            novo = (Diretorio) dirOrigem.getFilhos().get(i).clone();

                        } catch (CloneNotSupportedException ex) {
                            Logger.getLogger(MyKernel.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                        novo.setPai(dirDestino);
                        dirDestino.getFilhos().add(novo);
                        i = 0;
                        return result;
                    } else {
                        result = "Diretório destino ja possui esse diretório";
                    }

                } else {
                    i++;
                }
            }
            if (j == dirOrigem.getFilhos().size()) {
                result = "Diretório no encontrado";
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
        }
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
        }
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
            dirOrigem = dirAtual;

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
        }
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
        for (Arquivos arqAtual : dirOrigem.getArquivos()) {
            arqAtual.setPermissao(permissaoArq);
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

    public String desconverteCHMOD(String permissao) {
        String CHMOD = "", aux;
        int i = 1;
        while (i < 9) {
            aux = permissao.substring(i, i + 3);
            if (aux.equals("rwx")) {
                CHMOD += "7";
            } else if (aux.equals("rw-")) {
                CHMOD += "6";
            } else if (aux.equals("r-x")) {
                CHMOD += "5";
            } else if (aux.equals("r--")) {
                CHMOD += "4";
            } else if (aux.equals("-wx")) {
                CHMOD += "3";
            } else if (aux.equals("-w-")) {
                CHMOD += "2";
            } else if (aux.equals("--x")) {
                CHMOD += "1";
            } else if (aux.equals("---")) {
                CHMOD += "0";
            }
            i = i + 3;
        }
        //-d rwx rwx rwx

        return CHMOD;
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

            Diretorio dirTemp = dirAtual;
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
                        salvaArquivoNoHD(novo);
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
            Diretorio dirTemp = dirAtual;
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
                    result = "Arquivo não existe.";
                }

            } else {
                result = "Não foi possível encontrar esse diretório";
            }
        } else {
            result = "Comando incorreto";
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
        int i;
        String comando, parametros, caminho;
        caminho = parameters;

        List<String> config = FileManager.stringReader(caminho);
        for (i = 0; i < config.size(); i++) {
            comando = config.get(i).substring(0, config.get(i).indexOf(" "));
            parametros = config.get(i).substring(config.get(i).indexOf(" ") + 1);

            if (comando.equals("ls")) {
                ls(parametros);
                result = "Comandos Executados.";
            } else if (comando.equals("mkdir")) {
                mkdir(parametros);
                result = "Comandos Executados.";
            } else if (comando.equals("cd")) {
                cd(parametros);
                result = "Comandos Executados.";
            } else if (comando.equals("rmdir")) {
                rmdir(parametros);
                result = "Comandos Executados.";
            } else if (comando.equals("cp")) {
                cp(parametros);
                result = "Comandos Executados.";
            } else if (comando.equals("mv")) {
                mv(parametros);
                result = "Comandos Executados.";
            } else if (comando.equals("rm")) {
                rm(parametros);
                result = "Comandos Executados.";
            } else if (comando.equals("chmod")) {
                chmod(parametros);
                result = "Comandos Executados.";
            } else if (comando.equals("createfile")) {
                createfile(parametros);
                result = "Comandos Executados.";
            } else if (comando.equals("cat")) {
                cat(parametros);
                result = "Comandos Executados.";
            } else if (comando.equals("batch")) {
                batch(parametros);
                result = "Comandos Executados.";
            } else if (comando.equals("dump")) {
                dump(parametros);
                result = "Comandos Executados.";
            } else {
                result = "Arquivo não existe.";
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
        Diretorio dirTemp = raiz;
        String caminho = parameters;
        // caminho esperado = caminho
        caminho = "./testes/emLote3.txt";

        if (caminho != null) {
            FileManager.writer(caminho, "");
            visitaTodosOsFilhos(dirTemp, caminho);
        } else {
            result = "caminho inválido";
        }
        //fim da implementacao do aluno
        return result;
    }

    public void visitaTodosOsFilhos(Diretorio dirOrigem, String caminho) {
        String comando;
        String permissao;
        String conteudo = "";

        for (Diretorio atual : dirOrigem.getFilhos()) {
            if (atual != null) {

                comando = "mkdir " + atual.getNome();
                FileManager.writerAppend(caminho, comando + "\n");

                comando = "cd " + atual.getNome();
                FileManager.writerAppend(caminho, comando + "\n");

                visitaTodosOsFilhos(atual, caminho);

                if (!atual.getPermissao().equals("drwxrwxrwx")) {
                    permissao = desconverteCHMOD(atual.getPermissao());
                    comando = "chmod " + permissao + " " + atual.getNome() + "\n";
                    FileManager.writerAppend(caminho, comando);
                }

                for (Arquivos arqAtual : atual.getArquivos()) {
                    for (String linha : arqAtual.getConteudo()) {
                        conteudo = conteudo + linha + "\\n";
                    }

                    comando = "createfile " + arqAtual.getNome() + " " + conteudo + "\n";
                    conteudo = "";

                    FileManager.writerAppend(caminho, comando);

                    if (!arqAtual.getPermissao().equals("-rwxrwxrwx") && !arqAtual.getPermissao().equals(atual.getPermissao())) {
                        permissao = desconverteCHMOD(arqAtual.getPermissao());
                        comando = "chmod " + permissao + " " + arqAtual.getNome();
                        FileManager.writerAppend(caminho, comando + "\n");
                    }
                }

                comando = "cd ..";
                FileManager.writerAppend(caminho, comando + "\n");

            }

        }

        if (dirOrigem == raiz) {

            for (Arquivos arqAtual : dirOrigem.getArquivos()) {
                for (String linha : arqAtual.getConteudo()) {
                    conteudo = conteudo + linha + "\\n";
                }

                comando = "createfile " + arqAtual.getNome() + " " + conteudo;
                FileManager.writerAppend(caminho, comando + "\n");
                conteudo = "";

                if (!arqAtual.getPermissao().equals("-rwxrwxrwx")) {
                    permissao = desconverteCHMOD(arqAtual.getPermissao());
                    comando = "chmod " + permissao + " " + arqAtual.getNome();
                    FileManager.writerAppend(caminho, comando + "\n");
                }
            }
        }

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
        String version = "1.26";

        result += "Nome do Aluno:        " + name;
        result += "\nMatricula do Aluno:   " + registration;
        result += "\nVersao do Kernel:     " + version;

        return result;
    }

    public void salvaArquivoNoHD(Arquivos atual) {

        String binario = "";
        int positionAux, positionAuxMax;
        Boolean[] bitsBinario;

        atual.setPositionHD(positionHD);

        binario = retornaBinario(atual.getNome());
        bitsBinario = desconverteBinario(binario);
        armazenaNoHD(bitsBinario, positionHD);
        positionHD = positionHD + (80 * 8);

        binario = retornaBinario(atual.getPermissao());
        bitsBinario = desconverteBinario(binario);
        armazenaNoHD(bitsBinario, positionHD);
        positionHD = positionHD + (10 * 8);

        binario = retornaBinario(atual.getDataCriacao());
        bitsBinario = desconverteBinario(binario);
        armazenaNoHD(bitsBinario, positionHD);
        positionHD = positionHD + (20 * 8);

        binario = intToBinaryString(atual.getPai().getPositionHD(), 16);
        bitsBinario = desconverteBinario(binario);
        armazenaNoHD(bitsBinario, positionHD);
        positionHD = positionHD + (2 * 8);

        int j = 0;
        positionAux = positionHD;
        while (j < atual.getConteudo().size()) {
            binario = retornaBinario(atual.getConteudo().get(j));

            bitsBinario = desconverteBinario(binario);
            armazenaNoHD(bitsBinario, positionAux);
            positionAux += bitsBinario.length;
            j++;
        }

        binario = intToBinaryString(atual.getPositionHD(), 16);
        bitsBinario = desconverteBinario(binario);
        positionAux = atual.getPai().getPositionHD() + 2496;
        positionAuxMax = positionAux + 1600;
        while (!verificaHDVazio(positionAux) && (positionAux < positionAuxMax)) {
            positionAux += 16;
        }

        if ((positionAux < positionAuxMax)) {

            armazenaNoHD(bitsBinario, positionAux);
        }

        positionHD = positionHD + (400 * 8);
    }

    public void salvaDiretorioNoHD(String nome, int pai) {
        String binario = "";
        int positionAux, positionAuxMax, position = positionHD;
        Boolean[] bitsBinario;

        binario = retornaBinario(nome);
        bitsBinario = desconverteBinario(binario);
        armazenaNoHD(bitsBinario, positionHD);
        positionHD = positionHD + (80 * 8);

        binario = retornaBinario("drwxrwxrwx");
        bitsBinario = desconverteBinario(binario);
        armazenaNoHD(bitsBinario, positionHD);
        positionHD = positionHD + (10 * 8);

        SimpleDateFormat formato = new SimpleDateFormat("MMM dd yyyy HH:mm");
        String data = formato.format(new Date());
        binario = retornaBinario(data);
        bitsBinario = desconverteBinario(binario);
        armazenaNoHD(bitsBinario, positionHD);
        positionHD = positionHD + (20 * 8);

        binario = intToBinaryString(pai, 16);
        bitsBinario = desconverteBinario(binario);
        armazenaNoHD(bitsBinario, positionHD);
        positionHD = positionHD + (2 * 8);

        binario = intToBinaryString(position, 16);
        bitsBinario = desconverteBinario(binario);
        positionAux = pai + 896;
        positionAuxMax = pai + 2496;

        while (!verificaHDVazio(positionAux) && (positionAux < positionAuxMax)) {
            positionAux += 16;
        }

        if ((positionAux < positionAuxMax)) {
            armazenaNoHD(bitsBinario, positionAux);
        }

        positionHD = positionHD + (400 * 8);
    }

    public Boolean verificaHDVazio(int caminho) {
        String binario = "";
        int posicaoHDInicio = caminho, posicaoHDMax = caminho + 16, posicao = 0;
        for (int i = posicaoHDInicio; i < posicaoHDMax; i++) {
            if (HD.getBitDaPosicao(i)) {
                binario += "1";
            } else {
                binario += "0";
            }

            if (binario.length() == 16) {
                posicao = binaryStringToInt(binario);
                posicaoHDMax -= posicaoHDInicio;
            }
        }
        if (posicao > 0) {
            return false;
        } else {
            return true;
        }
    }

    public String retornaBinario(String parametro) {
        char teste;
        int j = 0, i;
        String binario = "";

        while (j < parametro.length()) {

            teste = parametro.charAt(j);
            i = teste;

            binario += intToBinaryString(i, 8);
            j++;
        }

        return binario;
    }

    public Boolean[] desconverteBinario(String Binario) {
        Boolean[] binario = new Boolean[Binario.length()];
        String[] aux = Binario.split("");
        int j;
        for (int i = 0; i < Binario.length(); i++) {
            j = Integer.parseInt(aux[i]);
            if (j > 0) {
                binario[i] = true;
            } else {
                binario[i] = false;
            }
        }
        return binario;
    }

    public void armazenaNoHD(Boolean[] bitsBinario, int inicio) {
        int i = 0, j = inicio;

        while (i < bitsBinario.length) {
            HD.setBitDaPosicao(bitsBinario[i], j);
            j++;
            i++;
        }
    }

    public String remontarDocs(Boolean description, int position) {
        String nome, permissao, data, retorno = "";
        int i = position, posicaoHDMax;

        posicaoHDMax = (position) + (80 * 8);
        nome = retornaString(i, posicaoHDMax);

        i = posicaoHDMax;
        posicaoHDMax += (10 * 8);
        permissao = retornaString(i, posicaoHDMax);

        i = posicaoHDMax;
        posicaoHDMax += (20 * 8);
        data = retornaString(i, posicaoHDMax);

        if (description) {
            retorno = permissao + " " + data + " " + nome;
        } else {
            retorno += nome;
        }

        return retorno;
    }

    public String retornaString(int posicao, int posicaoMax) {
        String string = "", binario = "";
        int posicaoHDMax, i, aux = 0;
        char[] numeroASC = new char[200];

        posicaoHDMax = posicaoMax;

        for (i = posicao; i < posicaoHDMax; i++) {
            if (HD.getBitDaPosicao(i)) {
                binario += "1";
            } else {
                binario += "0";
            }
            if (binario.length() == 8) {
                if (binaryStringToInt(binario) > 0) {
                    numeroASC[aux] = (char) binaryStringToInt(binario);
                    string += numeroASC[aux];
                    aux++;
                }
                binario = "";
            }
        }

        return string;
    }

    public String retornaBinario(int posicao, int posicaoMax) {
        String binario = "";
        int posicaoHDMax, i, aux = 0;

        posicaoHDMax = posicaoMax;

        for (i = posicao; i < posicaoHDMax; i++) {
            if (HD.getBitDaPosicao(i)) {
                binario += "1";
            } else {
                binario += "0";
            }
        }

        return binario;
    }

}
