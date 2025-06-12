package com.votacao.testes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.votacao.utils.ValidationUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para ValidationUtils")
class ValidationUtilsTest {

    @Nested
    @DisplayName("Testes para isNotEmpty()")
    class IsNotEmptyTests {

        @Test
        @DisplayName("Deve retornar verdadeiro para string válida")
        void shouldReturnTrueForValidString() {
            assertTrue(isNotEmpty("teste"), "String válida deveria retornar true");
        }

        @Test
        @DisplayName("Deve retornar verdadeiro para string com espaços no meio")
        void shouldReturnTrueForStringWithSpaces() {
            assertTrue(isNotEmpty("teste com espaços"), "String com espaços deveria retornar true");
        }

        @Test
        @DisplayName("Deve retornar falso para string nula")
        void shouldReturnFalseForNullString() {
            assertFalse(isNotEmpty(null), "String nula deveria retornar false");
        }

        @Test
        @DisplayName("Deve retornar falso para string vazia")
        void shouldReturnFalseForEmptyString() {
            assertFalse(isNotEmpty(""), "String vazia deveria retornar false");
        }

        @Test
        @DisplayName("Deve retornar falso para string com apenas espaços")
        void shouldReturnFalseForWhitespaceString() {
            assertFalse(isNotEmpty("   "), "String com apenas espaços deveria retornar false");
        }

        @Test
        @DisplayName("Deve retornar falso para string com tabs e espaços")
        void shouldReturnFalseForTabsAndSpaces() {
            assertFalse(isNotEmpty("\t\n "), "String com tabs e espaços deveria retornar false");
        }
    }

    @Nested
    @DisplayName("Testes para isValidUsername()")
    class IsValidUsernameTests {

        @ParameterizedTest
        @ValueSource(strings = { "abc", "user123", "Admin", "test1234", "a1b2c3d4e5f6g7h8i9j0" })
        @DisplayName("Deve retornar verdadeiro para usernames válidos")
        void shouldReturnTrueForValidUsernames(String username) {
            assertTrue(isValidUsername(username), "Username '" + username + "' deveria ser válido");
        }

        @ParameterizedTest
        @ValueSource(strings = { "ab", "user_123", "user@123", "user 123", "user-123", "user.123" })
        @DisplayName("Deve retornar falso para usernames inválidos")
        void shouldReturnFalseForInvalidUsernames(String username) {
            assertFalse(isValidUsername(username), "Username '" + username + "' deveria ser inválido");
        }

        @Test
        @DisplayName("Deve retornar falso para username muito longo")
        void shouldReturnFalseForTooLongUsername() {
            String longUsername = "a".repeat(21);
            assertFalse(isValidUsername(longUsername), "Username com mais de 20 caracteres deveria ser inválido");
        }

        @Test
        @DisplayName("Deve retornar falso para username nulo")
        void shouldReturnFalseForNullUsername() {
            assertFalse(isValidUsername(null), "Username nulo deveria ser inválido");
        }

        @Test
        @DisplayName("Deve retornar falso para username vazio")
        void shouldReturnFalseForEmptyUsername() {
            assertFalse(isValidUsername(""), "Username vazio deveria ser inválido");
        }
    }

    @Nested
    @DisplayName("Testes para isValidNumeroEleitor()")
    class IsValidNumeroEleitorTests {

        @ParameterizedTest
        @ValueSource(strings = { "E1", "E123", "E999999", "E0", "E12345678901234567890" })
        @DisplayName("Deve retornar verdadeiro para números de eleitor válidos")
        void shouldReturnTrueForValidNumeroEleitor(String numero) {
            assertTrue(isValidNumeroEleitor(numero), "Número de eleitor '" + numero + "' deveria ser válido");
        }

        @ParameterizedTest
        @ValueSource(strings = { "123", "e123", "E", "E123A", "E12.3", "E-123", "F123", "E 123" })
        @DisplayName("Deve retornar falso para números de eleitor inválidos")
        void shouldReturnFalseForInvalidNumeroEleitor(String numero) {
            assertFalse(isValidNumeroEleitor(numero), "Número de eleitor '" + numero + "' deveria ser inválido");
        }

        @Test
        @DisplayName("Deve retornar falso para número de eleitor nulo")
        void shouldReturnFalseForNullNumeroEleitor() {
            assertFalse(isValidNumeroEleitor(null), "Número de eleitor nulo deveria ser inválido");
        }

        @Test
        @DisplayName("Deve retornar falso para número de eleitor vazio")
        void shouldReturnFalseForEmptyNumeroEleitor() {
            assertFalse(isValidNumeroEleitor(""), "Número de eleitor vazio deveria ser inválido");
        }
    }

    @Nested
    @DisplayName("Testes para isPositive()")
    class IsPositiveTests {

        @ParameterizedTest
        @ValueSource(ints = { 1, 2, 10, 100, 1000, Integer.MAX_VALUE })
        @DisplayName("Deve retornar verdadeiro para números positivos")
        void shouldReturnTrueForPositiveNumbers(int number) {
            assertTrue(isPositive(number), "Número " + number + " deveria ser considerado positivo");
        }

        @ParameterizedTest
        @ValueSource(ints = { 0, -1, -10, -100, Integer.MIN_VALUE })
        @DisplayName("Deve retornar falso para números não positivos")
        void shouldReturnFalseForNonPositiveNumbers(int number) {
            assertFalse(isPositive(number), "Número " + number + " não deveria ser considerado positivo");
        }
    }

    @Nested
    @DisplayName("Testes para isValidNome()")
    class IsValidNomeTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "João", "Maria Silva", "José António", "Ana Catarina Pereira",
                "António José da Silva", "Àlex Ângelo", "Fernão Mendes Pinto",
                "José María García", "Françoise Müller"
        })
        @DisplayName("Deve retornar verdadeiro para nomes válidos")
        void shouldReturnTrueForValidNomes(String nome) {
            assertTrue(isValidNome(nome), "Nome '" + nome + "' deveria ser válido");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "A", "João123", "Maria@Silva", "José_António",
                "Ana.Catarina", "António-José", "José1Silva"
        })
        @DisplayName("Deve retornar falso para nomes inválidos")
        void shouldReturnFalseForInvalidNomes(String nome) {
            assertFalse(isValidNome(nome), "Nome '" + nome + "' deveria ser inválido");
        }

        @Test
        @DisplayName("Deve retornar falso para nome nulo")
        void shouldReturnFalseForNullNome() {
            assertFalse(isValidNome(null), "Nome nulo deveria ser inválido");
        }

        @Test
        @DisplayName("Deve retornar falso para nome vazio")
        void shouldReturnFalseForEmptyNome() {
            assertFalse(isValidNome(""), "Nome vazio deveria ser inválido");
        }

        @Test
        @DisplayName("Deve aceitar nomes com acentos portugueses")
        void shouldAcceptPortugueseAccents() {
            assertTrue(isValidNome("João Ação"), "Nome com acentos portugueses deveria ser válido");
            assertTrue(isValidNome("Conceição"), "Nome com ç deveria ser válido");
            assertTrue(isValidNome("António Ângelo"), "Nome com acentos deveria ser válido");
        }
    }
}