package LeitorCpfs;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class LeitorCPFSCom10Threads {

    public static void main(String[] args) {
        String pastaCPFS = "cpfs"; 
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");

        long startMS = System.currentTimeMillis();
        Date dataStart = new Date(startMS);
        System.out.println("Início: " + formatoHora.format(dataStart));

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            int inicio = i * 3 + 1;
            int fim = inicio + 2;
            Thread t = new Thread(() -> processarArquivos(inicio, fim, pastaCPFS));
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.out.println("Erro ao aguardar thread: " + e.getMessage());
            }
        }

        long fimMS = System.currentTimeMillis();
        Date dataEnd = new Date(fimMS);
        long tempoExecucao = fimMS - startMS;

        System.out.println("Término: " + formatoHora.format(dataEnd));
        System.out.println("Tempo de Execução: " + tempoExecucao + " milissegundos");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("versao10threads.txt"))) {
            writer.write("Início: " + formatoHora.format(dataStart) + "\n");
            writer.write("Término: " + formatoHora.format(dataEnd) + "\n");
            writer.write("Tempo de Execução: " + tempoExecucao + " milissegundos\n");
        } catch (IOException e) {
            System.out.println("Erro ao criar o arquivo de log: " + e.getMessage());
        }
    }

    private static void processarArquivos(int inicio, int fim, String pasta) {
        for (int i = inicio; i <= fim; i++) {
            String nomeArquivo = Paths.get(pasta, "f" + i + "-4-25.txt").toString();
            int validos = 0, invalidos = 0;

            try {
                List<String> lista = Files.readAllLines(Paths.get(nomeArquivo));

                for (String linha : lista) {
                    if (validaCPF(linha)) {
                        validos++;
                    } else {
                        invalidos++;
                    }
                }

                System.out.println("Arquivo f" + i + "-4-25.txt: " + validos + " válidos | " + invalidos + " inválidos");

            } catch (IOException e) {
                System.out.println("Erro ao ler o arquivo " + nomeArquivo + ": " + e.getMessage());
            }
        }
    }

    public static boolean validaCPF(String cpf) {
        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) {
            return false;
        }

        for (int i = 9; i <= 10; i++) {
            int soma = 0;
            for (int j = 0; j < i; j++) {
                soma += (cpf.charAt(j) - '0') * ((i + 1) - j);
            }
            int digito = (soma * 10) % 11;
            if (digito == 10) digito = 0;
            if (digito != (cpf.charAt(i) - '0')) {
                return false;
            }
        }

        return true;
    }
}