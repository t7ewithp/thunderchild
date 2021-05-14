package ml.withp.gui;

import ml.withp.utility.Convert;
import ml.withp.utility.Loader;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class STMGeneratorPanel extends JPanel {
    private final LabeledTextField inputFile =
            new LabeledTextField("Input File:","data/");

    private final LabeledTextField messageSpeed =
            new LabeledTextField("Message Speed:", "1.0");


    private final LabeledTextField mergeOne =
            new LabeledTextField("Merge CSV 1:","data/");
    private final LabeledTextField mergeTwo =
            new LabeledTextField("Merge CSV 2:","data/");
    private final LabeledTextField mergeTarget =
            new LabeledTextField("Output CSV Name:","data/");

    private static void setCST(int x, int y, GridBagConstraints cst) {
        cst.gridx = x;
        cst.gridy = y;
    }

    private void cleanTextbox(LabeledTextField fld, String ext) {
        String candidate = fld.getFieldText();
        if(candidate.length() > ext.length() && candidate.endsWith(ext))
            candidate = candidate.substring(0,candidate.length() - ext.length());
        fld.setFieldText(candidate);
    }

    private boolean validateMergeFields() {
        cleanTextbox(mergeOne, ".csv");
        cleanTextbox(mergeTwo, ".csv");
        cleanTextbox(mergeTarget, ".csv");
        File mergeOneF = new File(mergeOne.getFieldText() + ".csv");
        File mergeTwoF = new File(mergeTwo.getFieldText() + ".csv");
        if(mergeOneF.isFile()) {
            if(mergeTwoF.isFile()) {
                return true;
            } else {
                Helpers.PopupText("Error! " + mergeTwoF + " does not exist!");
                return false;
            }
        }
        Helpers.PopupText("Error! " + mergeOneF + " does not exist!");
        return false;
    }

    private boolean validateSTMGeneratingFields() {
        cleanTextbox(inputFile, ".csv");
        cleanTextbox(messageSpeed, "");

        File target = new File(inputFile.getFieldText() + ".csv");
        if(target.isFile()) {
            File outTarget = new File(inputFile.getFieldText() + ".stm");
            if(outTarget.exists()) {
                Helpers.PopupText("Error! "
                        + outTarget + " already exists! (If you already knew that, move/delete it...)");
                return false;
            }
            try {
                double d = Double.parseDouble(messageSpeed.getFieldText());
                if(d <= 0) {
                    Helpers.PopupText("Error! Speed must be > 0");
                    return false;
                }
            } catch(NumberFormatException e) {
                Helpers.PopupText("Error! Couldn't parse speed.");
                return false;
            }

          return true;
        }
            Helpers.PopupText("Error! " + target + " does not exist!");
        return false;
    }

    public STMGeneratorPanel() {
        super(new GridBagLayout());
        int xPos = 0;
        int yPos = 0;
        GridBagConstraints cst = new GridBagConstraints();
        JButton generate = new JButton("Generate STM!");
        JLabel splash = new JLabel("Just put the path to the .csv file into the input box, click generate, then wait for the success popup. :3");
        JLabel mergeSplash = new JLabel("Make sure both merge halves have the same column setup, I don't check that :)");
        generate.addActionListener(e -> {
            if(validateSTMGeneratingFields()){
                try {
                    Convert.fileCSVToSTM(inputFile.getFieldText(), Double.parseDouble(messageSpeed.getFieldText()));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    Helpers.PopupText("It didn't work (either bad csv or cannot write output file, check console output for stack trace.)");
                }
                Helpers.PopupText("It worked! (probably?)");
            }
        });

        JButton mergeButton = new JButton("Merge!");
        mergeButton.addActionListener(e -> {
            if(validateMergeFields()) {
                if(Loader.mergeCSV(
                        new File(mergeOne.getFieldText() + ".csv"),
                        new File(mergeTwo.getFieldText() + ".csv"),
                        mergeTarget.getFieldText() + ".csv"))
                    Helpers.PopupText("It worked! (probably?)");
                else
                    Helpers.PopupText("Couldn't merge (invalid csv or couldn't open the target. check stdout.)");
            }
        });

        setCST(xPos, yPos++, cst);
        add(splash, cst);

        setCST(xPos++, yPos, cst);
        add(inputFile, cst);

        setCST(xPos++,yPos, cst);
        add(messageSpeed, cst);

        setCST(xPos--, yPos++, cst);
        add(generate,cst);
        xPos--;

        setCST(xPos, yPos++, cst);
        add(Box.createVerticalStrut(10),cst);

        setCST(xPos, yPos++, cst);
        add(mergeSplash,cst);

        setCST(xPos++, yPos, cst);
        add(mergeOne, cst);

        setCST(xPos--, yPos++, cst);
        add(mergeTwo,cst);

        setCST(xPos++, yPos, cst);
        add(mergeTarget,cst);

        setCST(xPos, yPos, cst);
        add(mergeButton, cst);
    }
}
