package br.edu.ufca.audiencias.ui;

import raven.datetime.DatePicker;
import raven.datetime.TimePicker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Componente DateTimePicker usando swing-datetime-picker (DJ-Raven).
 * Combina DatePicker + TimePicker (24h) com pop-up visual.
 */
public class DateTimePicker extends JPanel {

    private final DatePicker datePicker;
    private final TimePicker timePicker;

    public DateTimePicker() {
        this(LocalDateTime.now());
    }

    public DateTimePicker(LocalDateTime initial) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
        setOpaque(false);

        datePicker = new DatePicker();
        datePicker.setDateFormat("dd/MM/yyyy");
        datePicker.setCloseAfterSelected(true);

        JFormattedTextField dateEditor = new JFormattedTextField();
        dateEditor.setColumns(10);
        datePicker.setEditor(dateEditor);
        datePicker.setSelectedDate(initial.toLocalDate());

        timePicker = new TimePicker();
        timePicker.set24HourView(true);

        JFormattedTextField timeEditor = new JFormattedTextField();
        timeEditor.setColumns(6);
        timePicker.setEditor(timeEditor);
        timePicker.setSelectedTime(initial.toLocalTime().withSecond(0).withNano(0));

        add(dateEditor);
        add(datePicker);
        add(Box.createHorizontalStrut(6));
        add(timeEditor);
        add(timePicker);
    }

    /** Retorna o LocalDateTime selecionado no picker. */
    public LocalDateTime getLocalDateTime() {
        LocalDate date = datePicker.isDateSelected()
                ? datePicker.getSelectedDate()
                : LocalDate.now();
        LocalTime time = timePicker.isTimeSelected()
                ? timePicker.getSelectedTime()
                : LocalTime.now().withSecond(0).withNano(0);
        return LocalDateTime.of(date, time);
    }
}
