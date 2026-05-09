package com.biblioteca;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Usar el look and feel del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        // Lanzar la interfaz en el hilo de eventos de Swing (EDT)
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
