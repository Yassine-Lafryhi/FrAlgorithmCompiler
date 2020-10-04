package com.fralgo.controllers;

import com.fralgo.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class Controller {
    @FXML
    TextArea codeArea;
    @FXML
    Label message;
    String doneText = "";
    String pasFileContent = "";
    String pasFilePath = "";
    String algoFilePath = "";
    boolean successfullyCompiled = false;

    public void initialize() {
        File directory = new File(System.getProperty("user.home") + "/ALGOS/");
        if (!directory.exists()) {
            directory.mkdir();
        } else {
            for (File file : Objects.requireNonNull(directory.listFiles()))
                file.delete();
        }
        codeArea.setWrapText(true);
    }

    public void save(String savingPath) {
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(savingPath, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (printWriter != null) {
            printWriter.print(codeArea.getText());
            printWriter.close();
        }
        doneText = codeArea.getText();
    }

    public void execute() throws IOException {
        message.setText("");
        pasFileContent = "";
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
        algoFilePath = System.getProperty("user.home") + "/ALGOS/" + fileName + ".algo";
        save(algoFilePath);
        process(algoFilePath);
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", "fpc " + pasFilePath);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line + "\n");
                if (line.contains("error")) {
                    message.setStyle("-fx-text-fill:#e74c3c;");
                    message.setText("Il y a une erreur quelque part, veuillez vérifier !");
                    successfullyCompiled = false;
                    break;
                } else if (line.contains("compiled")) {
                    successfullyCompiled = true;
                    message.setStyle("-fx-text-fill:#2ecc71;");
                    message.setText("L'algorithme a été compilé avec succès !");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (successfullyCompiled) {
            String path = pasFilePath.replace(".pas", "");
            File file = new File(path);
            while (!file.exists()) ;
            String[] command = {"/bin/bash", "-c", "open " + path};
            Runtime.getRuntime().exec(command);
        }
    }

    public void process(String path) {
        String line;
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                String replacedLine = line;
                replacedLine = replacedLine.replace("Algorithme", "program");
                replacedLine = replacedLine.replace("Variable", "var");
                replacedLine = replacedLine.replace("Entier", "Integer");
                replacedLine = replacedLine.replace("Booléen", "Boolean");
                replacedLine = replacedLine.replace("Caractère", "Char");
                replacedLine = replacedLine.replace("Réel", "Real");
                replacedLine = replacedLine.replace("Ecrire", "writeln");
                replacedLine = replacedLine.replace("Sinon", " end else begin");
                replacedLine = replacedLine.replace("Vrai", "true");
                replacedLine = replacedLine.replace("Faux", "false");
                replacedLine = replacedLine.replace("\"", "'");
                replacedLine = replacedLine.replace("Lire", "readln");
                replacedLine = replacedLine.replace("←", ":=");
                replacedLine = replacedLine.replace("/", " div ");
                replacedLine = replacedLine.replace("%", " mod ");
                replacedLine = replacedLine.replace("||", ") or (");
                replacedLine = replacedLine.replace("&&", ") and (");
                if (replacedLine.contains("TantQue") || replacedLine.contains("Si")) {
                    replacedLine = replacedLine.replace("!", "not");
                }
                if (replacedLine.contains("Pour")) {
                    replacedLine = replacedLine.replace("Jusqu'à", "to");
                } else {
                    replacedLine = replacedLine.replace("Jusqu'à", "until");
                }
                replacedLine = replacedLine.replace("Répéter", "repeat");
                if (replacedLine.contains("Pour")) {
                    replacedLine = replacedLine.replace("Faire", "do begin");
                } else {
                    replacedLine = replacedLine.replace("Faire", ") do begin");
                }
                if (!replacedLine.contains("Fin")) {
                    replacedLine = replacedLine.replace("Si", "if(");
                    replacedLine = replacedLine.replace("TantQue", "while (");
                    replacedLine = replacedLine.replace("Pour", "for");
                } else if (replacedLine.contains("Si")) {
                    replacedLine = replacedLine.replace("FinSi", "end;");
                } else if (replacedLine.contains("Tant")) {
                    replacedLine = replacedLine.replace("FinTantQue", "end;");
                } else if (replacedLine.contains("Pour")) {
                    replacedLine = replacedLine.replace("FinPour", "end;");
                }
                replacedLine = replacedLine.replace("Alors", ") then begin");
                replacedLine = replacedLine.replace("Début", "begin");
                if (replacedLine.equals("Fin")) {
                    replacedLine = "writeln('Appuyez sur une touche pour quitter');\nreadln();\nend.";
                }
                if (replacedLine.contains("begin") || replacedLine.contains("end.") || replacedLine.contains("end") || replacedLine.contains("repeat")) {
                    pasFileContent = pasFileContent.concat(replacedLine + "\n");
                } else {
                    pasFileContent = pasFileContent.concat(replacedLine + ";\n");
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pasFilePath = algoFilePath.replace("algo", "pas");
        System.out.println(pasFilePath);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(pasFilePath, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (writer != null) {
            writer.println(pasFileContent);
            writer.close();
        }
    }

    public void insertDefault() {
        codeArea.setText("Algorithme Algo1\nVariable a : Entier\nDébut\nEcrire(\"Salut !\")\na ← 50\nEcrire(\"A = \",a)\nFin");
    }

    public void exit() {
        System.exit(0);
    }

    public void minimize() {
        Main.stage.setIconified(true);
    }
}