package com.votacao.testes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.votacao.utils.NIFUtils.isValidNIF;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes para NIFUtils.isValidNIF()")
class TestarNifUtils {

    @Nested
    @DisplayName("Testes com NIFs Válidos")
    class ValidNifTests {

        @ParameterizedTest
        @ValueSource(
                strings = {
                        "213588188",
                        "503837431",
                        "272383740"
                }
        )
        @DisplayName("Deve retornar verdadeiro para NIFs válidos conhecidos")
        void shouldReturnTrueForValidNifs(String nif) {
            assertTrue(isValidNIF(nif), "O NIF " + nif + " deveria ser válido");
        }
    }

    @Nested
    @DisplayName("Testes com NIFs Inválidos")
    class InvalidNifTests {

        @Test
        @DisplayName("Deve retornar falso para NIF com menos de 9 dígitos")
        void shouldReturnFalseForNifTooShort() {
            assertFalse(isValidNIF("12345678"), "NIF com 8 dígitos deveria ser inválido");
        }

        @Test
        @DisplayName("Deve retornar falso para NIF com mais de 9 dígitos")
        void shouldReturnFalseForNifTooLong() {
            assertFalse(isValidNIF("1234567890"), "NIF com 10 dígitos deveria ser inválido");
        }

        @Test
        @DisplayName("Deve retornar falso para NIF com letras")
        void shouldReturnFalseForNifWithLetters() {
            assertFalse(isValidNIF("12345678A"), "NIF com letras deveria ser inválido");
        }

        @Test
        @DisplayName("Deve retornar falso para NIF com caracteres especiais")
        void shouldReturnFalseForNifWithSpecialChars() {
            assertFalse(isValidNIF("123-45678"), "NIF com caracteres especiais deveria ser inválido");
        }

        @Test
        @DisplayName("Deve retornar falso para NIF com dígito de controlo incorreto")
        void shouldReturnFalseForIncorrectChecksum() {
            assertFalse(isValidNIF("213588189"), "NIF com checksum incorreto deveria ser inválido");
        }

        @Test
        @DisplayName("Deve retornar falso para uma string nula")
        void shouldReturnFalseForNullInput() {
            assertFalse(isValidNIF(null), "Input nulo deveria ser inválido");
        }

        @Test
        @DisplayName("Deve retornar falso para uma string vazia")
        void shouldReturnFalseForEmptyString() {
            assertFalse(isValidNIF(""), "String vazia deveria ser inválida");
        }

        @Test
        @DisplayName("Deve retornar falso para uma string com apenas espaços")
        void shouldReturnFalseForWhitespaceString() {
            assertFalse(isValidNIF("         "), "String com espaços deveria ser inválida");
        }
    }
}