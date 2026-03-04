package br.edu.ufca.audiencias;

import br.edu.ufca.audiencias.ui.MainWindow;
import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatDarculaLaf.setup();
            new MainWindow().setVisible(true);
        });
    }
}
