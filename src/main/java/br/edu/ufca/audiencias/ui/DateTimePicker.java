package br.edu.ufca.audiencias.ui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * Componente DateTimePicker usando JSpinner nativo do Swing.
 * Combina dois spinners: data (dd/MM/yyyy) e hora (HH:mm).
 */
public class DateTimePicker extends JPanel {

    private final JSpinner dateSpinner;
    private final JSpinner timeSpinner;

    public DateTimePicker() {
        this(LocalDateTime.now());
    }

    public DateTimePicker(LocalDateTime initial) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
        setOpaque(false);

        Date initialDate = Date.from(initial.atZone(ZoneId.systemDefault()).toInstant());

        SpinnerDateModel dateModel = new SpinnerDateModel(initialDate, null, null, Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));

        SpinnerDateModel timeModel = new SpinnerDateModel(initialDate, null, null, Calendar.MINUTE);
        timeSpinner = new JSpinner(timeModel);
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));

        add(new JLabel("Data:"));
        add(dateSpinner);
        add(Box.createHorizontalStrut(8));
        add(new JLabel("Hora:"));
        add(timeSpinner);
    }

    /** Retorna o LocalDateTime combinando os valores dos dois spinners. */
    public LocalDateTime getLocalDateTime() {
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime((Date) dateSpinner.getValue());

        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime((Date) timeSpinner.getValue());

        dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        dateCal.set(Calendar.SECOND, 0);
        dateCal.set(Calendar.MILLISECOND, 0);

        return dateCal.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
