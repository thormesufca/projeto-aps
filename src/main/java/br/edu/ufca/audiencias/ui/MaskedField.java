package br.edu.ufca.audiencias.ui;

import javax.swing.*;
import javax.swing.text.*;
import java.util.regex.Pattern;

/**
 * Utilitário de campos com máscara para a UI.
 *
 * Campos disponíveis:
 *  - cpfCnpj()        → máscara automática: CPF (###.###.###-##) ou CNPJ (##.###.###/####-##)
 *  - telefone()       → máscara automática: fixo (##) ####-#### ou celular (##) #####-####
 *  - numeroProcesso() → CNJ: #######-##.####.#.##.####
 *
 * Validações:
 *  - validarEmail(String)     → true se formato válido
 *  - valorSemMascara(JTextField) → apenas dígitos
 */
public final class MaskedField {

    private MaskedField() {}

    // ── Fábricas de campo ─────────────────────────────────────────────────────

    public static JTextField cpfCnpj(String valorInicial) {
        JTextField f = new JTextField(22);
        attachFilter(f, new CpfCnpjMask(f));
        if (valorInicial != null && !valorInicial.isBlank())
            setDigits(f, valorInicial);
        return f;
    }

    public static JTextField telefone(String valorInicial) {
        JTextField f = new JTextField(18);
        attachFilter(f, new TelefoneMask(f));
        if (valorInicial != null && !valorInicial.isBlank())
            setDigits(f, valorInicial);
        return f;
    }

    public static JTextField numeroProcesso(String valorInicial) {
        JTextField f = new JTextField(28);
        attachFilter(f, new NumeroProcessoMask(f));
        if (valorInicial != null && !valorInicial.isBlank())
            setDigits(f, valorInicial);
        return f;
    }

    // ── Utilitários ───────────────────────────────────────────────────────────

    /** Valida formato de e-mail (aceita vazio/nulo). */
    public static boolean validarEmail(String email) {
        if (email == null || email.isBlank()) return true;
        return Pattern.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$", email.trim());
    }

    /** Retorna o conteúdo do campo sem separadores (apenas dígitos). */
    public static String valorSemMascara(JTextField field) {
        return soDigitos(field.getText());
    }

    /** Aplica a máscara CNJ (NNNNNNN-DD.AAAA.J.TT.OOOO) a uma string de dígitos. */
    public static String formatarNumeroProcesso(String s) {
        if (s == null) return "";
        return new NumeroProcessoMask(null).format(soDigitos(s));
    }

    static String soDigitos(String s) {
        return s == null ? "" : s.replaceAll("\\D", "");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static void attachFilter(JTextField f, DigitMask mask) {
        ((AbstractDocument) f.getDocument()).setDocumentFilter(mask);
    }

    /** Insere apenas os dígitos de `value` no campo, acionando a máscara. */
    private static void setDigits(JTextField f, String value) {
        try {
            f.getDocument().insertString(0, soDigitos(value), null);
        } catch (BadLocationException ignored) {}
    }

    // ── Filtro base ──────────────────────────────────────────────────────────

    private static abstract class DigitMask extends DocumentFilter {

        private final JTextField owner;
        private boolean busy = false;

        DigitMask(JTextField owner) { this.owner = owner; }

        abstract String format(String digits);
        abstract int maxDigits();

        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet a)
                throws BadLocationException {
            replace(fb, offset, 0, text, a);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet a)
                throws BadLocationException {
            if (busy) { super.replace(fb, offset, length, text, a); return; }
            if (text == null) text = "";
            String doc  = fb.getDocument().getText(0, fb.getDocument().getLength());
            String before = doc.substring(0, offset);
            String after  = doc.substring(Math.min(offset + length, doc.length()));
            // Raw digits that should be before the caret after the operation
            String rawBefore = soDigitos(before) + soDigitos(text);
            String rawAll    = rawBefore + soDigitos(after);
            if (rawAll.length()   > maxDigits()) rawAll   = rawAll.substring(0, maxDigits());
            if (rawBefore.length() > maxDigits()) rawBefore = rawBefore.substring(0, maxDigits());
            final String formatted = format(rawAll);
            final int targetDigits = rawBefore.length();
            busy = true;
            try {
                fb.replace(0, doc.length(), formatted, a);
            } finally {
                busy = false;
            }
            SwingUtilities.invokeLater(() -> {
                if (owner != null) {
                    int pos = posAfterNthDigit(formatted, targetDigits);
                    owner.setCaretPosition(Math.min(pos, owner.getText().length()));
                }
            });
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            replace(fb, offset, length, "", null);
        }

        private int posAfterNthDigit(String s, int n) {
            if (n <= 0) return 0;
            int count = 0;
            for (int i = 0; i < s.length(); i++) {
                if (Character.isDigit(s.charAt(i))) {
                    count++;
                    if (count == n) return i + 1;
                }
            }
            return s.length();
        }
    }

    // ── CPF / CNPJ ────────────────────────────────────────────────────────────

    private static class CpfCnpjMask extends DigitMask {
        CpfCnpjMask(JTextField o) { super(o); }

        @Override public int maxDigits() { return 14; }

        @Override
        public String format(String d) {
            int len = d.length();
            if (len == 0) return "";
            StringBuilder sb = new StringBuilder();
            if (len <= 11) {           // CPF: ###.###.###-##
                for (int i = 0; i < len; i++) {
                    if (i == 3 || i == 6) sb.append('.');
                    else if (i == 9)       sb.append('-');
                    sb.append(d.charAt(i));
                }
            } else {                   // CNPJ: ##.###.###/####-##
                for (int i = 0; i < len; i++) {
                    if (i == 2 || i == 5) sb.append('.');
                    else if (i == 8)       sb.append('/');
                    else if (i == 12)      sb.append('-');
                    sb.append(d.charAt(i));
                }
            }
            return sb.toString();
        }
    }

    // ── Telefone ─────────────────────────────────────────────────────────────

    private static class TelefoneMask extends DigitMask {
        TelefoneMask(JTextField o) { super(o); }

        @Override public int maxDigits() { return 11; }

        @Override
        public String format(String d) {
            int len = d.length();
            if (len == 0) return "";
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < len; i++) {
                if (i == 2)                       sb.append(") ");
                else if (len <= 10 && i == 6)     sb.append('-');  // fixo: 4+4
                else if (len >  10 && i == 7)     sb.append('-');  // celular: 5+4
                sb.append(d.charAt(i));
            }
            return sb.toString();
        }
    }

    // ── Número de Processo CNJ ────────────────────────────────────────────────

    private static class NumeroProcessoMask extends DigitMask {
        NumeroProcessoMask(JTextField o) { super(o); }

        @Override public int maxDigits() { return 20; }

        // NNNNNNN-DD.AAAA.J.TT.OOOO
        // separadores inseridos ANTES do dígito de índice: 7→'-', 9,13,14,16→'.'
        @Override
        public String format(String d) {
            if (d.isEmpty()) return "";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < d.length(); i++) {
                if      (i == 7)  sb.append('-');
                else if (i == 9 || i == 13 || i == 14 || i == 16) sb.append('.');
                sb.append(d.charAt(i));
            }
            return sb.toString();
        }
    }
}
