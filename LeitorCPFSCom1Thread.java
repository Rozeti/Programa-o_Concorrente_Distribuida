package LeitorCpfs;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class LeitorCPFSCom1Thread {

    public static void main(String[] args) {
    	SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
        String pastaCPFS = "cpfs"; 

        long startMS = System.currentTimeMillis(); 
        Date dataStart = new Date(startMS);
        System.out.println("Início: " + formatoHora.format(dataStart));

        for (int i = 1; i <= 30; i++) {
            String nomeDoArquivo = Paths.get(pastaCPFS, "f" + i + "-4-25.txt").toString();
            int validos = 0, invalidos = 0;

            try {
                List<String> lista = Files.readAllLines(Paths.get(nomeDoArquivo));

                for (String linha : lista) {
                    if (validaCPF(linha)) {
                        validos++;
                    } else {
                        invalidos++;
                    }
                }

                System.out.println("Arquivo f" + i + "-4-25.txt: " + validos + " válidos | " + invalidos + " inválidos");

            } catch (IOException e) {
                System.out.println("Erro ao ler o arquivo " + nomeDoArquivo + ": " + e.getMessage());
            }
        }

        long fimMS = System.currentTimeMillis();
        Date dataEnd = new Date(fimMS);
        long tempoExecucao = fimMS - startMS;

        System.out.println("Término: " + formatoHora.format(dataEnd));
        System.out.println("Tempo de Execução: " + tempoExecucao + " milissegundos");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("versao1thread.txt"))) {
            writer.write("Início: " + formatoHora.format(dataStart) + "\n");
            writer.write("Término: " + formatoHora.format(dataEnd) + "\n");
            writer.write("Tempo de Execução: " + tempoExecucao + " milissegundos\n");
        } catch (IOException e) {
            System.out.println("Erro ao criar o arquivo de log: " + e.getMessage());
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