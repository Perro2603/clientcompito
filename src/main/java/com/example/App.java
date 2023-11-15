package com.example;
 import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class App 
{

public static void main(String[] args) {
    String[] PAROLE = {"pizza", "lasagna", "spaghetti", "cannoli"};
        try {
            ServerSocket serverSocket = new ServerSocket(3000);
            System.out.println("Server in ascolto ");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuova connessione da: " + clientSocket.getInetAddress());

              
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket, String[] PAROLE) {
        try {
            // Inizializzazione della parola segreta
            String parolaSegreta = PAROLE[new Random().nextInt(PAROLE.length)];

            // Inizializzazione della parola oscurata
            StringBuilder parolaOscurata = new StringBuilder("*".repeat(parolaSegreta.length()));
            int tentativiRimasti = 4; // Imposta il numero massimo di tentativi desiderato

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("Connessione effettuata. Digita ESCI per uscire.");
            out.println(parolaOscurata.toString());

            String input;
            while ((input = in.readLine()) != null) {
                if (input.equalsIgnoreCase("ESCI")) {
                    break;
                } else if (input.length() == 1) {
                    handleLetterGuess(input.charAt(0), parolaSegreta, parolaOscurata, out, tentativiRimasti);
                } else {
                    handleWordGuess(input, parolaSegreta, out, tentativiRimasti);
                }

                if (isGameOver(tentativiRimasti, parolaOscurata)) {
                    sendGameOverMessage(out, tentativiRimasti, parolaSegreta);
                    break;
                }
            }

            clientSocket.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleLetterGuess(char lettera, String parolaSegreta, StringBuilder parolaOscurata, PrintWriter out, int tentativiRimasti) {
        boolean indovinato = false;
        for (int i = 0; i < parolaSegreta.length(); i++) {
            if (parolaSegreta.charAt(i) == lettera) {
                parolaOscurata.setCharAt(i, lettera);
                indovinato = true;
            }
        }
        out.println(parolaOscurata.toString());
        if (!indovinato) {
            out.println("Tentativi rimasti: " + (tentativiRimasti - 1));
        }
    }

    private static void handleWordGuess(String tentativo, String parolaSegreta, PrintWriter out, int tentativiRimasti) {
        if (parolaSegreta.equalsIgnoreCase(tentativo)) {
            out.println("Bravo! Hai indovinato la parola.");
        } else {
            out.println("Tentativi rimasti: " +  (tentativiRimasti - 1));
        }
    }

    private static boolean isGameOver(int tentativiRimasti, StringBuilder parolaOscurata) {
        return tentativiRimasti <= 0 || parolaOscurata.indexOf("*") == -1;
    }

    private static void sendGameOverMessage(PrintWriter out, int tentativiRimasti, String parolaSegreta) {
        if (tentativiRimasti > 0) {
            out.println("Bravo! Hai indovinato in " + (7 - tentativiRimasti) + " tentativi");
        } else {
            out.println("Mi dispiace, hai esaurito i tentativi. La parola era: " + parolaSegreta);
        }
    }
}

