import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
                    message.setText("Il y a une erreur quelque part, s'il vous plaît vérifier !");
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
            while (!file.exists()) {
            }
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
                String _line = line;
                _line = _line.replace("Algorithme", "program");
                _line = _line.replace("Variable", "var");
                _line = _line.replace("Entier", "Integer");
                _line = _line.replace("Booléen", "Boolean");
                _line = _line.replace("Caractère", "Char");
                _line = _line.replace("Réel", "Real");
                _line = _line.replace("Ecrire", "writeln");
                _line = _line.replace("Ecrire", "writeln");
                _line = _line.replace("Sinon", " end else begin");
                _line = _line.replace("Vrai", "true");
                _line = _line.replace("Faux", "false");
                _line = _line.replace("\"", "'");
                _line = _line.replace("Lire", "readln");
                _line = _line.replace("←", ":=");
                _line = _line.replace("/", " div ");
                _line = _line.replace("%", " mod ");
                _line = _line.replace("||", ") or (");
                _line = _line.replace("&&", ") and (");
                if (_line.contains("TantQue") || _line.contains("Si")) {
                    _line = _line.replace("!", "not");
                }
                if (_line.contains("Pour")) {
                    _line = _line.replace("Jusqu'à", "to");
                } else {
                    _line = _line.replace("Jusqu'à", "until");
                }
                _line = _line.replace("Répéter", "repeat");
                if (_line.contains("Pour")) {
                    _line = _line.replace("Faire", "do begin");
                } else {
                    _line = _line.replace("Faire", ") do begin");
                }
                if (!_line.contains("Fin")) {
                    _line = _line.replace("Si", "if(");
                    _line = _line.replace("TantQue", "while (");
                    _line = _line.replace("Pour", "for");
                } else if (_line.contains("Si")) {
                    _line = _line.replace("FinSi", "end;");
                } else if (_line.contains("Tant")) {
                    _line = _line.replace("FinTantQue", "end;");
                } else if (_line.contains("Pour")) {
                    _line = _line.replace("FinPour", "end;");
                }
                _line = _line.replace("Alors", ") then begin");

                _line = _line.replace("Début", "begin");
                if (_line.equals("Fin")) {
                    _line = "writeln('Appuyez sur une touche pour quitter');\nreadln();\nend.";
                }
                if (_line.contains("begin") || _line.contains("end.") || _line.contains("end") || _line.contains("repeat")) {
                    pasFileContent = pasFileContent + _line + "\n";
                } else {
                    pasFileContent = pasFileContent + _line + ";\n";
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