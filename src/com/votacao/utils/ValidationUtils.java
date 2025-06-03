package com.votacao.utils;

public class ValidationUtils {

    // Verifica se uma string não está vazia nem é só espaços
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // Verifica se o username é válido (apenas letras e números, 3-20 caracteres)
    public static boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9]{3,20}$");
    }

    // Verifica se o número de eleitor é válido (exemplo: começa por E e tem dígitos)
    public static boolean isValidNumeroEleitor(String numero) {
        return numero != null && numero.matches("^E\\d+$");
    }

    // Verifica se um número é positivo
    public static boolean isPositive(int number) {
        return number > 0;
    }

    // Verifica se o nome é válido (apenas letras e espaços, pelo menos 2 caracteres)
    public static boolean isValidNome(String nome) {
        return nome != null && nome.matches("^[a-zA-ZÀ-ÿ ]{2,}$");
    }
}