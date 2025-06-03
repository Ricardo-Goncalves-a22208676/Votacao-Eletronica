package com.votacao.utils;

public class NIFUtils {

    /**
     * Valida um NIF português.
     * @param nif string de 9 dígitos
     * @return true se passar em todas as regras de formação e dígito de controlo
     */
    public static boolean isValidNIF(String nif) {
        // 1. Não pode ser nulo, vazio, e deve ter exatamente 9 dígitos
        if (nif == null || !nif.matches("\\d{9}")) {
            return false;
        }

        int first = Character.getNumericValue(nif.charAt(0));
        int second = Character.getNumericValue(nif.charAt(1));

        // 2. Validação do primeiro (e segundo) dígito conforme regras oficiais
        if (first >= 1 && first <= 3) {
            // Pessoa singular comum
        } else if (first == 4) {
            // Não residente: deve começar por 45
            if (second != 5) return false;
        } else if (first == 5 || first == 6 || first == 8) {
            // Coletivas (5), entidades públicas (6), usos específicos/antigos (8)
        } else if (first == 7) {
            // Diversos regimes: 70,71,72,74,75,77,79
            if (second != 0 && second != 1 && second != 2 &&
                    second != 4 && second != 5 && second != 7 &&
                    second != 9) {
                return false;
            }
        } else if (first == 9) {
            // Regimes especiais: 90,91,98,99
            if (second != 0 && second != 1 &&
                    second != 8 && second != 9) {
                return false;
            }
        } else {
            // Qualquer outro primeiro dígito (ex. 0) é inválido
            return false;
        }

        // 3. Cálculo do dígito de controlo
        int sum = 0;
        // peso para o dígito i é (9 - i), i variando de 0 a 7
        for (int i = 0; i < 8; i++) {
            int d = Character.getNumericValue(nif.charAt(i));
            sum += d * (9 - i);
        }
        int remainder = sum % 11;
        int expectedCheck = (remainder <= 1) ? 0 : 11 - remainder;
        int actualCheck = Character.getNumericValue(nif.charAt(8));

        return expectedCheck == actualCheck;
    }

    /**
     * Mensagem de erro padrão para NIF inválido.
     */
    public static String getNIFErrorMessage() {
        return "NIF inválido! Verifique se o número está correto.";
    }


}