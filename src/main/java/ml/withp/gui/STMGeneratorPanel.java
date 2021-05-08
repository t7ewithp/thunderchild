package ml.withp.gui;

import ml.withp.utility.Convert;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class STMGeneratorPanel extends JPanel {
    private final LabeledTextField inputFile =
            new LabeledTextField("Input File:","data/");

    private static void setCST(int x, int y, GridBagConstraints cst) {
        cst.gridx = x;
        cst.gridy = y;
    }

    private void cleanTextbox() {
        String candidate = inputFile.getFieldText();
        if(candidate.length() > 4 && candidate.endsWith(".csv"))
            candidate = candidate.substring(0,candidate.length() - 4);
        inputFile.setFieldText(candidate);
    }

    private boolean validateFields() {
        cleanTextbox();
        File target = new File(inputFile.getFieldText() + ".csv");
        if(target.isFile()) {
            File outTarget = new File(inputFile.getFieldText() + ".stm");
            if(outTarget.exists()) {
                Helpers.PopupText("Error! "
                        + outTarget + " already exists! (If you already knew that, move/delete it...)");
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
        JButton generate = new JButton("generate");
        JLabel splash = new JLabel("Just put the path to the .csv file into the input box, click generate, then wait for the success popup. :3");

        generate.addActionListener(e -> {
            if(validateFields()){
                try {
                    Convert.fileCSVToSTM(inputFile.getFieldText());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    Helpers.PopupText("It didn't work (either bad csv or cannot write output file, check console output for stack trace.)");
                }
                Helpers.PopupText("It worked! (probably?)");
            }
        });


        setCST(xPos, yPos++, cst);
        add(splash, cst);

        setCST(xPos, yPos++, cst);
        add(inputFile, cst);

        setCST(xPos, yPos++, cst);
        add(generate,cst);

    }
}
