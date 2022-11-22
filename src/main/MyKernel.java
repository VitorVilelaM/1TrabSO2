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
        salvaDiretorioNoHD("Vitor", "drwxrwxrwx", 0);
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
        int diretorio = posicao, limiteDiretorio, position, posicaoEncontrada = -1;
        boolean encontrado = false;
        String nome;

        limiteDiretorio = diretorio + 3200;
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

    public int retornaPosicaoFilho(int filho, int pai) {
        int diretorio = pai + 896, limiteDiretorio, position, posicaoEncontrada = -1;
        boolean encontrado = false;

        limiteDiretorio = diretorio + 2496;

        while (diretorio < (limiteDiretorio) && !encontrado) {

            String binario = retornaBinario(diretorio, (diretorio + 16));
            position = binaryStringToInt(binario);

            if (position == filho) {
                return diretorio;
            } else {
                diretorio += 16;
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
        description = false;

        description = instrucao[0].equals("-l");

        if (instrucao.length == 1) {
            binario = retornaBinario(posicaoHDInicio, (posicaoHDInicio + 16));
            posicao = binaryStringToInt(binario);

            do {
                if (posicao > 0) {
                    if (!remontarDocs(description, posicao).equals("  ")) {
                        if (description) {
                            result += remontarDocs(description, posicao) + "\n";
                        } else {
                            result += remontarDocs(description, posicao) + " ";
                        }
                    }
                }

                posicaoHDInicio += 16;
                binario = retornaBinario(posicaoHDInicio, (posicaoHDInicio + 16));
                posicao = binaryStringToInt(binario);
            } while (posicao > 0);

            posicaoHDInicio = positionDirAtual + 2496;
            binario = retornaBinario(posicaoHDInicio, (posicaoHDInicio + 16));
            posicao = binaryStringToInt(binario);

            do {
                if (posicao > 0) {
                    if (!remontarDocs(description, posicao).equals("  ")) {
                        if (description) {
                            result += remontarDocs(description, posicao) + "\n";
                        } else {
                            result += remontarDocs(description, posicao) + " ";
                        }
                    }
                }

                posicaoHDInicio += 16;
                binario = retornaBinario(posicaoHDInicio, (posicaoHDInicio + 16));
                posicao = binaryStringToInt(binario);

            } while (posicao > 0);

        } else {
            String[] caminho = instrucao[1].split("/");
            int dirTemp = verificaOrigem(caminho, false);
            int posicaoPorNome = retornaPosicaoPorNome(caminho[caminho.length - 1], dirTemp + 896);

            if (dirTemp >= 0 && posicaoPorNome >= 0) {

                posicaoPorNome += 896;
                binario = retornaBinario(posicaoPorNome, (posicaoPorNome + 16));
                posicao = binaryStringToInt(binario);

                do {
                    if (!remontarDocs(description, posicao).equals("  ")) {
                        if (description) {
                            result += remontarDocs(description, posicao) + "\n";
                        } else {
                            result += remontarDocs(description, posicao) + " ";
                        }
                    }

                    posicaoPorNome += 16;
                    binario = retornaBinario(posicaoPorNome, (posicaoPorNome + 16));
                    posicao = binaryStringToInt(binario);
                } while (posicao > 0);
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
                    salvaDiretorioNoHD(nome, "drwxrwxrwx", dirTemporario);
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
            if (remontarDocs(false, posicaoPorNome).contains(".txt")) {
                result = "voce nao pode entrar em um arquivo";
                currentDir = "/";
            } else {
                positionDirAtual = posicaoPorNome;
                if (positionDirAtual == 0) {
                    currentDir = "/";
                } else {
                    currentDir = remontarDocs(false, positionDirAtual);
                }
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

        int dirTemp = verificaOrigem(caminho, false);
        int posicaoPorNome = retornaPosicaoPorNome(caminho[caminho.length - 1], dirTemp + 896);
        if (dirTemp >= 0 && posicaoPorNome >= 0) {
            if (verificaHDVazio(posicaoPorNome + 896, 3200)) {
                limpaHD(posicaoPorNome, posicaoPorNome + 4095);
            } else {
                result = "diretorio possui conteudo";
            }
        } else {
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
        int copia;
        boolean mudarNome = false;
        String[] comando = parameters.split(" ");

        if (comando.length == 2) {
            String[] caminho1 = comando[0].split("/");
            String[] caminho2 = comando[1].split("/");
            String nome = caminho1[caminho1.length - 1];
            String novoNome = caminho2[caminho2.length - 1];
            String binario;

            int dirOrigem = verificaOrigem(caminho1, false);
            int origemPorNome = retornaPosicaoPorNome(caminho1[caminho1.length - 1], dirOrigem + 896);

            int dirDestino = verificaOrigem(caminho2, false);
            int destinoPorNome = retornaPosicaoPorNome(caminho2[caminho2.length - 1], dirDestino + 896);
            int positionInicio = destinoPorNome + 896, positionFinal = positionInicio + 1600;

            Boolean bitsBinario[];

            if (nome.contains(".txt")) {

                if (seExisteNome(nome, destinoPorNome)) {
                    if (verificaHDVazio(positionInicio, positionFinal)) {
                        if (novoNome.contains(".txt") && nome.contains(".txt")) {
                            if (!novoNome.equals(nome)) {
                                copia = copiaBloco(origemPorNome);
                                positionHD += 4096;
                                addFilho(copia, destinoPorNome, true);

                                binario = retornaBinario(novoNome);
                                bitsBinario = desconverteBinario(binario);
                                armazenaNoHD(bitsBinario, copia);
                            }
                        } else {
                            copia = copiaBloco(origemPorNome);
                            positionHD += 4096;
                            addFilho(copia, destinoPorNome, true);
                        }

                    } else {
                        result = "impossivel criar pasta (Armazenamento Cheio)";
                    }

                }
            } else {
                if (seExisteNome(nome, destinoPorNome)) {
                    if (verificaHDVazio(positionInicio, positionFinal)) {
                        addFilho(origemPorNome, destinoPorNome, true);
                        int posicaoLimpa = retornaPosicaoFilho(origemPorNome, dirOrigem);
                        int max = posicaoLimpa + 16;
                        limpaHD(posicaoLimpa, max);
                    } else {
                        result = "impossivel criar pasta (Armazenamento Cheio)";
                    }
                }
            }

        } else if (comando.length == 3 && comando[0].contains("-R")) {
            String[] caminho1 = comando[0].split("/");
            String[] caminho2 = comando[1].split("/");
            String nome = caminho1[caminho1.length - 1];

            int dirOrigem = verificaOrigem(caminho1, false);
            int origemPorNome = retornaPosicaoPorNome(caminho1[caminho1.length - 1], dirOrigem + 896);

            int dirDestino = verificaOrigem(caminho2, false);
            int destinoPorNome = retornaPosicaoPorNome(caminho2[caminho2.length - 1], dirDestino + 896);

            int positionInicio = destinoPorNome + 896, positionFinal = positionInicio + 1600;

            if (seExisteNome(nome, destinoPorNome)) {
                if (destinoPorNome >= 0 && origemPorNome >= 0) {
                    if (verificaHDVazio(positionInicio, positionFinal)) {
                        copia = copiaBloco(origemPorNome);
                        positionHD += 4096;
                        addFilho(copia, destinoPorNome, true);
                    } else {
                        result = "impossivel copiar pasta (Armazenamento Cheio)";
                    }
                } else {
                    result = "diretorio informado nao encontrado";
                }
            } else {
                result = "nao foi possivel encontrar o objeto";
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
        String[] comando = parameters.split(" ");
        String binario;
        Boolean[] bitsBinario;

        if (comando.length == 2) {
            String[] caminho1 = comando[0].split("/");
            String[] caminho2 = comando[1].split("/");
            String nome = caminho1[caminho1.length - 1];
            String novoNome = caminho2[caminho2.length - 1];

            int dirOrigem = verificaOrigem(caminho1, false);
            int origemPorNome = retornaPosicaoPorNome(caminho1[caminho1.length - 1], dirOrigem + 896);

            int dirDestino = verificaOrigem(caminho2, false);
            int destinoPorNome = retornaPosicaoPorNome(caminho2[caminho2.length - 1], dirDestino + 896);
            int positionInicio = destinoPorNome + 896, positionFinal = positionInicio + 1600;

            if (nome.contains(".txt")) {

                if (seExisteNome(nome, destinoPorNome)) {
                    if (verificaHDVazio(positionInicio, positionFinal)) {
                        if (novoNome.contains(".txt") && nome.contains(".txt")) {
                            if (!novoNome.equals(nome)) {
                                binario = retornaBinario(novoNome);
                                bitsBinario = desconverteBinario(binario);
                                armazenaNoHD(bitsBinario, origemPorNome);
                            }
                        } else {
                            addFilho(origemPorNome, destinoPorNome, true);
                            int posicaoLimpa = retornaPosicaoFilho(origemPorNome, dirOrigem);
                            int max = posicaoLimpa + 16;
                            limpaHD(posicaoLimpa, max);
                        }

                    } else {
                        result = "impossivel criar pasta (Armazenamento Cheio)";
                    }

                }
            } else {
                if (seExisteNome(nome, destinoPorNome)) {
                    if (verificaHDVazio(positionInicio, positionFinal)) {
                        addFilho(origemPorNome, destinoPorNome, true);
                        int posicaoLimpa = retornaPosicaoFilho(origemPorNome, dirOrigem);
                        int max = posicaoLimpa + 16;
                        limpaHD(posicaoLimpa, max);
                    } else {
                        result = "impossivel criar pasta (Armazenamento Cheio)";
                    }
                }
            }

        } else {
            result = "comando incorreto";
        }

        return result;
    }

    public String rm(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: rm");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        String[] comando = parameters.split(" ");
        if (comando.length == 1) {

            String[] caminho1 = comando[0].split("/");
            String nome = caminho1[caminho1.length - 1];

            int dirTemp = verificaOrigem(caminho1, false);
            int posicaoPorNome = retornaPosicaoPorNome(caminho1[caminho1.length - 1], dirTemp + 896);
            if (dirTemp >= 0 && posicaoPorNome >= 0) {
                limpaHD(posicaoPorNome, posicaoPorNome + 4095);
            } else {
                result = "objeto para exclusao nao encontrado";
            }
        } else if (comando.length == 2 && comando[0].contains("-R")) {

            String[] caminho2 = comando[1].split("/");
            String nome = caminho2[caminho2.length - 1];

            int dirTemp = verificaOrigem(caminho2, false);
            int posicaoPorNome = retornaPosicaoPorNome(caminho2[caminho2.length - 1], dirTemp + 896);
            if (!nome.contains(".txt")) {
                if (dirTemp >= 0 && posicaoPorNome >= 0) {
                    limpaHD(posicaoPorNome, posicaoPorNome + 4095);
                } else {
                    result = "diretorio nao encontrado";
                }
            } else {
                result = "impossivel excluir arquivo com esse comando";
            }
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
        String newPermission, nome, binario;
        Boolean[] bitsBinario;
        int posicaoPermissao;

        if (comando.length == 3 && comando[0].contains("-R")) {

            newPermission = converteCHMOD(comando[1].split(""));
            caminho = comando[2].split("/");

            int dirTemp = verificaOrigem(caminho, false);
            int posicaoPorNome = retornaPosicaoPorNome(caminho[caminho.length - 1], dirTemp + 896);
            if (posicaoPorNome >= 0) {

                binario = retornaBinario(newPermission);
                bitsBinario = desconverteBinario(binario);
                alteraPermissaoFilhos(posicaoPorNome, bitsBinario);
            }

        } else if (comando.length == 2) {
            caminho = comando[1].split("/");
            int dirTemp = verificaOrigem(caminho, false);
            int posicaoPorNome = retornaPosicaoPorNome(caminho[caminho.length - 1], dirTemp + 896);
            if (posicaoPorNome >= 0) {
                newPermission = converteCHMOD(comando[0].split(""));
                posicaoPermissao = posicaoPorNome + (81 * 8);

                binario = retornaBinario(newPermission);
                bitsBinario = desconverteBinario(binario);
                armazenaNoHD(bitsBinario, posicaoPermissao);
            } else {
                result = "objeto nao encontrado";
            }

        } else {
            result = "comando incorreto";
        }
        return result;
    }

    public void alteraPermissaoFilhos(int origem, Boolean[] bitsBinario) {
        int posicaoFilho, posicaoPermissao, i = 1;
        String nome, binario;
        armazenaNoHD(bitsBinario, origem + 81 * 8);

        int posicaoInicio = origem + 896, posicaoMax = origem + 896 + 16;

        String posicao = retornaBinario(posicaoInicio, posicaoMax);
        posicaoFilho = binaryStringToInt(posicao);

        while (posicaoFilho != 0) {
            posicaoPermissao = posicaoFilho + 81 * 8;
            nome = retornaString(posicaoFilho, posicaoFilho + 81 * 8);

            if (nome.contains(".txt")) {
                armazenaNoHD(bitsBinario, posicaoPermissao);
            } else {
                alteraPermissaoFilhos(posicaoFilho, bitsBinario);
            }

            armazenaNoHD(bitsBinario, posicaoPermissao);

            posicaoInicio += 16;
            posicaoMax += 16;
            posicaoFilho = binaryStringToInt(retornaBinario(posicaoInicio, posicaoMax));
        }
        posicaoInicio = origem + 896 + 1600;
        posicaoMax = posicaoInicio + 16;
        posicao = retornaBinario(posicaoInicio, posicaoMax);
        posicaoFilho = binaryStringToInt(posicao);

        while (posicaoFilho != 0) {
            posicaoPermissao = posicaoFilho + 81 * 8;
            nome = retornaString(posicaoFilho, posicaoFilho + 81 * 8);

            if (nome.contains(".txt")) {
                armazenaNoHD(bitsBinario, posicaoPermissao);
            } else {
                alteraPermissaoFilhos(posicaoFilho, bitsBinario);
            }

            armazenaNoHD(bitsBinario, posicaoPermissao);

            posicaoInicio += 16;
            posicaoMax += 16;
            posicaoFilho = binaryStringToInt(retornaBinario(posicaoInicio, posicaoMax));
        }
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
            String conteudo = comando[1];
            //createfile ./disciplina.txt Sistemas Operacionais\nTrabalho Pratico 1
            String nome = caminho[caminho.length - 1];

            int dirTemporario = verificaOrigem(caminho, false);

            if (verificaNome(nome)) {
                nome = nome + ".txt";
                if (dirTemporario >= 0) {
                    if (seExisteNome(nome, dirTemporario)) {
                        salvaArquivoNoHD(nome, dirTemporario, conteudo);
                    } else {
                        result = "nome informado já existe";
                    }
                } else {
                    result = "Erro no caminho informado!";
                }
            } else {
                result = "nome infomado invalido";
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
        String nome = caminho[caminho.length - 1];

        int dirOrigem = verificaOrigem(caminho, false);
        int position, positionConteudo;
        String conteudo;
        if (dirOrigem >= 0) {
            if (!seExisteNome(nome, dirOrigem)) {
                position = retornaPosicaoPorNome(nome, dirOrigem);
                positionConteudo = position + 880;
                conteudo = retornaString(positionConteudo, positionConteudo + 3216);
                result = conteudo;
            } else {
                result = "arquivo nao encontrado";
            }
        } else {
            result = "nome infomado invalido";
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
        int dirTemp = 0;
        String caminho = parameters;
        // caminho esperado = caminho

        if (caminho != null) {
            FileManager.writer(caminho, "");
            visitaTodosOsFilhos(dirTemp, caminho);
        } else {
            result = "caminho inválido";
        }
        //fim da implementacao do aluno
        return result;
    }

    public void visitaTodosOsFilhos(int dirOrigem, String caminho) {
        String comando, permissao, conteudo = "", nome = "";
        int filho, irmao = 0, i = 1;
        String binario;

        binario = retornaBinario(dirOrigem + 896, (dirOrigem + 896 + 16));
        filho = binaryStringToInt(binario);

        if (filho > 0) {
            nome = retornaString(filho, filho + 80 * 8);

            if (!nome.contains(".txt")) {
                comando = "mkdir " + nome;
                FileManager.writerAppend(caminho, comando + "\n");

                comando = "cd " + nome;
                FileManager.writerAppend(caminho, comando + "\n");
                visitaTodosOsFilhos(filho, caminho);
            } else {
                conteudo = retornaString(filho + 880, filho + 4096);
                comando = "createfile " + nome + " " + conteudo;
                FileManager.writerAppend(caminho, comando + "\n");
            }

            permissao = retornaString(filho + 80 * 8, filho + 80 * 8 + 10 * 8);

            if (nome.contains(".txt")) {
                if (!permissao.equals("-rwxrwxrwx")) {
                    permissao = desconverteCHMOD(permissao);
                    comando = "chmod " + permissao + " " + nome;
                    FileManager.writerAppend(caminho, comando + "\n");
                }
            } else {
                if (!permissao.equals("drwxrwxrwx")) {
                    permissao = desconverteCHMOD(permissao);
                    comando = "chmod " + permissao + " " + nome;
                    FileManager.writerAppend(caminho, comando + "\n");
                }
            }
            if (!nome.contains(".txt")) {
                binario = retornaBinario(dirOrigem + 896 + 16, (dirOrigem + 896 + 16 + 16 * i));
                irmao = binaryStringToInt(binario);
            }

            if (irmao > 0) {
                while (irmao > 0) {
                    i++;
                    comando = "mkdir " + retornaString(irmao, irmao + 80 * 8);
                    FileManager.writerAppend(caminho, comando + "\n");
                    visitaTodosOsFilhos(irmao, caminho);
                    binario = "";
                    binario = retornaBinario(dirOrigem + 896 + 16 * (i), (dirOrigem + 896 + 16 + 16 * i));
                    irmao = binaryStringToInt(binario);
                }

            }
            if (!nome.contains(".txt")) {
                visitaTodosOsFilhos(dirOrigem + 1600, caminho);

            }
        } else {
            if (!nome.contains(".txt")) {
                comando = "cd ..";
                FileManager.writerAppend(caminho, comando + "\n");
            }
        }

        /*
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

         */
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
        String version = "1.30";

        result += "Nome do Aluno:        " + name;
        result += "\nMatricula do Aluno:   " + registration;
        result += "\nVersao do Kernel:     " + version;

        return result;
    }

    public void salvaArquivoNoHD(String nome, int pai, String conteudo) {

        String binario = "";
        int positionAux, positionAuxMax, position = positionHD;
        Boolean[] bitsBinario;

        binario = retornaBinario(nome);
        bitsBinario = desconverteBinario(binario);
        armazenaNoHD(bitsBinario, positionHD);
        positionHD = positionHD + (80 * 8);

        binario = retornaBinario("-rwxrwxrwx");
        bitsBinario = desconverteBinario(binario);
        armazenaNoHD(bitsBinario, positionHD);
        positionHD = positionHD + (10 * 8);

        SimpleDateFormat formato = new SimpleDateFormat("MMM dd yyyy HH:mm");
        String data = formato.format(new Date());
        binario = retornaBinario(data);
        bitsBinario = desconverteBinario(binario);
        armazenaNoHD(bitsBinario, positionHD);
        positionHD = positionHD + (20 * 8);

        if (position > 65536) {

            binario = intToBinaryString(position, 24);
            bitsBinario = desconverteBinario(binario);
            positionAux = pai + 2504;
            positionAuxMax = positionAux + 1600;
            while (!verificaHDVazio(positionAux, 16) && (positionAux < positionAuxMax)) {
                positionAux += 16;
            }

            if ((positionAux < positionAuxMax)) {
                armazenaNoHD(bitsBinario, positionAux);
            }

            int posicaoAux2 = positionHD;

            binario = retornaBinario(conteudo);
            bitsBinario = desconverteBinario(binario);
            armazenaNoHD(bitsBinario, posicaoAux2);

            positionHD = positionHD + (402 * 8);

        } else {
            binario = intToBinaryString(position, 16);
            bitsBinario = desconverteBinario(binario);
            positionAux = pai + 2496;
            positionAuxMax = positionAux + 1600;
            while (!verificaHDVazio(positionAux, 16) && (positionAux < positionAuxMax)) {
                positionAux += 16;
            }

            if ((positionAux < positionAuxMax)) {
                armazenaNoHD(bitsBinario, positionAux);
            }

            int posicaoAux2 = positionHD;

            binario = retornaBinario(conteudo);
            bitsBinario = desconverteBinario(binario);
            armazenaNoHD(bitsBinario, posicaoAux2);

            positionHD = positionHD + (402 * 8);
        }

    }

    public void salvaDiretorioNoHD(String nome, String permisao, int pai) {
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

        while (!verificaHDVazio(positionAux, 16) && (positionAux < positionAuxMax)) {
            positionAux += 16;
        }

        if ((positionAux < positionAuxMax)) {
            armazenaNoHD(bitsBinario, positionAux);
        }

        positionHD = positionHD + (400 * 8);
    }

    public Boolean verificaHDVazio(int caminho, int posicaoMax) {
        String binario = "";
        int posicaoHDInicio = caminho, posicaoHDMax = caminho + posicaoMax, posicao = 0;
        for (int i = posicaoHDInicio; i < posicaoHDMax; i++) {
            if (HD.getBitDaPosicao(i)) {
                binario += "1";
            } else {
                binario += "0";
            }

            if (binario.length() == 16) {
                posicao = binaryStringToInt(binario);
                binario = "";

                if (posicao > 0) {
                    return false;
                }
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

    public void limpaHD(int posicaoInicio, int posicaoFinal) {
        int i = posicaoInicio, j = posicaoInicio;
        String bin = "";

        while (i < posicaoFinal) {
            HD.setBitDaPosicao(false, j);
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

    public void addFilho(int filho, int pai, boolean mudaPai) {
        String binario;
        Boolean bitsBinario[];
        int positionAux, positionAuxMax;

        binario = intToBinaryString(filho, 16);
        bitsBinario = desconverteBinario(binario);
        positionAux = pai + 896;
        positionAuxMax = pai + 2496;

        while (!verificaHDVazio(positionAux, 16) && (positionAux < positionAuxMax)) {
            positionAux += 16;
        }

        if ((positionAux < positionAuxMax)) {
            armazenaNoHD(bitsBinario, positionAux);
        }
        if (mudaPai) {
            binario = intToBinaryString(pai, 16);
            bitsBinario = desconverteBinario(binario);
            armazenaNoHD(bitsBinario, filho + 880);
        }
    }

    public int copiaBloco(int origem) {
        int j = origem, i, count = 0;
        int pos = positionHD;
        int posMax = positionHD + 4096;
        for (i = positionHD; i < posMax; i++) {
            count++;
            HD.setBitDaPosicao(HD.getBitDaPosicao(j), i);
            j++;
        }
        return positionHD;
    }
}
