import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Controller {

    @FXML
    TextArea AlgorithmArea;

    @FXML
    Label msg;


    String DoneText = "";
    String b = "";
    String PasFilePath = "";
    String AlgoFilePath = "";

    boolean SuccessfullyCompiled = false;

    public void initialize() {


        File directory = new File(System.getProperty("user.home") + "/ALGOS/");
        if (!directory.exists()) {
            directory.mkdir();

        }


        AlgorithmArea.setWrapText(true);


    }


    public void Save(String SavingPath) {


        PrintWriter writer = null;
        try {
            writer = new PrintWriter(SavingPath, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.print(AlgorithmArea.getText());

        writer.close();
        DoneText = AlgorithmArea.getText();


    }


    public void Execute() throws IOException {
        msg.setText("");
        b = "";


        String FileName = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
        AlgoFilePath = System.getProperty("user.home") + "/ALGOS/" + FileName + ".algo";

        Save(AlgoFilePath);


        Traiter(AlgoFilePath);
        try {


            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "fpc " + PasFilePath);
            builder.redirectErrorStream(true);
            Process p = null;

            p = builder.start();

            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = r.readLine()) != null) {

                System.out.println(line + "\n");

                if (line.contains("error")) {

                    msg.setStyle("-fx-text-fill:#e74c3c;");
                    msg.setText("Il y a une erreur quelque part, s'il vous plaît vérifier !");
                    SuccessfullyCompiled = false;
                    break;

                } else if (line.contains("compiled")) {

                    SuccessfullyCompiled = true;

                    msg.setStyle("-fx-text-fill:#2ecc71;");
                    msg.setText("L'algorithme a été compilé avec succès !");
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        if (SuccessfullyCompiled) {

            String path = PasFilePath.replace(".pas", "");
            File f = new File(path);
            while (!f.exists()) {
            }
            String[] cmd = {"/bin/bash", "-c", "open " + path};
            Runtime.getRuntime().exec(cmd);

        }
    }


    public void Traiter(String path) {

        String fileName = path;

        String line = "";

        try {

            FileReader fileReader = new FileReader(fileName);


            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {


                String c = line;


                c = c.replace("Algorithme", "program");
                c = c.replace("Variable", "var");

                c = c.replace("Entier", "Integer");
                c = c.replace("Booléen", "Boolean");
                c = c.replace("Caractère", "Char");
                c = c.replace("Réel", "Real");


                c = c.replace("Ecrire", "writeln");


                c = c.replace("Ecrire", "writeln");


                c = c.replace("Sinon", " end else begin");

                c = c.replace("Vrai", "true");
                c = c.replace("Faux", "false");


                c = c.replace("\"", "'");
                c = c.replace("Lire", "readln");
                c = c.replace("←", ":=");
                c = c.replace("/", " div ");
                c = c.replace("%", " mod ");

                c = c.replace("||", ") or (");
                c = c.replace("&&", ") and (");


                if (c.contains("TantQue") || c.contains("Si")) {

                    c = c.replace("!", "not");


                }


                if (c.contains("Pour")) {
                    c = c.replace("Jusqu'à", "to");
                } else {
                    c = c.replace("Jusqu'à", "until");
                }


                c = c.replace("Répéter", "repeat");

                if (c.contains("Pour")) {
                    c = c.replace("Faire", "do begin");
                } else {
                    c = c.replace("Faire", ") do begin");

                }


                if (!c.contains("Fin")) {
                    c = c.replace("Si", "if(");
                    c = c.replace("TantQue", "while (");
                    c = c.replace("Pour", "for");
                } else if (c.contains("Si")) {
                    c = c.replace("FinSi", "end;");
                } else if (c.contains("Tant")) {
                    c = c.replace("FinTantQue", "end;");
                } else if (c.contains("Pour")) {
                    c = c.replace("FinPour", "end;");
                }


                c = c.replace("Alors", ") then begin");

                c = c.replace("Début", "begin");


                if (c.equals("Fin")) {
                    c = "writeln('Appuyez sur une touche pour quitter');\nreadln();\nend.";
                }


                if (c.contains("begin") || c.contains("end.") || c.contains("end") || c.contains("repeat")) {
                    b = b + c + "\n";


                } else {

                    b = b + c + ";\n";
                }


            }


            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");

        }


        PasFilePath = AlgoFilePath.replace("algo", "pas");
        System.out.println(PasFilePath);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(PasFilePath, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.println(b);

        writer.close();


    }


    public void InsertDefault() {
        AlgorithmArea.setText("Algorithme Algo1\nVariable a : Entier\nDébut\nEcrire(\"Salut !\")\na ← 50\nEcrire(\"A = \",a)\nFin");

    }


    public void Exit() {
        System.exit(0);
    }

    public void Minimize() {
        Main.ps.setIconified(true);
    }

}