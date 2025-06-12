package com.votacao.testes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.votacao.utils.PasswordUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para PasswordUtils")
class PasswordUtilsTest {

    @Nested
    @DisplayName("Testes para hashPassword()")
    class HashPasswordTests {

        @Test
        @DisplayName("Deve gerar hash para password válida")
        void shouldGenerateHashForValidPassword() {
            String password = "minhaPasswordSegura123";
            String hash = hashPassword(password);

            assertNotNull(hash, "Hash não deveria ser nulo");
            assertFalse(hash.isEmpty(), "Hash não deveria estar vazio");
            assertEquals(64, hash.length(), "Hash SHA-256 deveria ter 64 caracteres");
            assertTrue(hash.matches("^[a-f0-9]+$"), "Hash deveria conter apenas caracteres hexadecimais");
        }

        @Test
        @DisplayName("Deve gerar hashes diferentes para passwords diferentes")
        void shouldGenerateDifferentHashesForDifferentPasswords() {
            String password1 = "password123";
            String password2 = "password456";

            String hash1 = hashPassword(password1);
            String hash2 = hashPassword(password2);

            assertNotEquals(hash1, hash2, "Passwords diferentes devem gerar hashes diferentes");
        }

        @Test
        @DisplayName("Deve gerar o mesmo hash para a mesma password")
        void shouldGenerateSameHashForSamePassword() {
            String password = "mesmaPassword";

            String hash1 = hashPassword(password);
            String hash2 = hashPassword(password);

            assertEquals(hash1, hash2, "A mesma password deve gerar o mesmo hash");
        }

        @Test
        @DisplayName("Deve gerar hash para password vazia")
        void shouldGenerateHashForEmptyPassword() {
            String emptyPassword = "";
            String hash = hashPassword(emptyPassword);

            assertNotNull(hash, "Hash para password vazia não deveria ser nulo");
            assertEquals(64, hash.length(), "Hash SHA-256 deveria ter 64 caracteres mesmo para password vazia");
        }

        @Test
        @DisplayName("Deve ser case-sensitive")
        void shouldBeCaseSensitive() {
            String password1 = "Password";
            String password2 = "password";

            String hash1 = hashPassword(password1);
            String hash2 = hashPassword(password2);

            assertNotEquals(hash1, hash2, "Passwords com cases diferentes devem gerar hashes diferentes");
        }

        @Test
        @DisplayName("Deve processar caracteres especiais")
        void shouldProcessSpecialCharacters() {
            String password = "minha@Password#2023!";
            String hash = hashPassword(password);

            assertNotNull(hash, "Hash com caracteres especiais não deveria ser nulo");
            assertEquals(64, hash.length(), "Hash SHA-256 deveria ter 64 caracteres");
        }

        @Test
        @DisplayName("Deve processar caracteres acentuados")
        void shouldProcessAccentedCharacters() {
            String password = "minhaPasswordComAçãoEÇedilha";
            String hash = hashPassword(password);

            assertNotNull(hash, "Hash com caracteres acentuados não deveria ser nulo");
            assertEquals(64, hash.length(), "Hash SHA-256 deveria ter 64 caracteres");
        }
    }

    @Nested
    @DisplayName("Testes para verifyPassword()")
    class VerifyPasswordTests {

        @Test
        @DisplayName("Deve retornar verdadeiro para password correta")
        void shouldReturnTrueForCorrectPassword() {
            String password = "minhaPasswordSegura";
            String hash = hashPassword(password);

            assertTrue(verifyPassword(password, hash), "Verificação deveria retornar true para password correta");
        }

        @Test
        @DisplayName("Deve retornar falso para password incorreta")
        void shouldReturnFalseForIncorrectPassword() {
            String correctPassword = "passwordCorreta";
            String incorrectPassword = "passwordIncorreta";
            String hash = hashPassword(correctPassword);

            assertFalse(verifyPassword(incorrectPassword, hash),
                    "Verificação deveria retornar false para password incorreta");
        }

        @Test
        @DisplayName("Deve retornar falso para hash inválido")
        void shouldReturnFalseForInvalidHash() {
            String password = "minhaPassword";
            String invalidHash = "hashInvalido";

            assertFalse(verifyPassword(password, invalidHash), "Verificação deveria retornar false para hash inválido");
        }

        @Test
        @DisplayName("Deve ser case-sensitive na verificação")
        void shouldBeCaseSensitiveInVerification() {
            String originalPassword = "Password";
            String wrongCasePassword = "password";
            String hash = hashPassword(originalPassword);

            assertTrue(verifyPassword(originalPassword, hash), "Password original deveria ser verificada como correta");
            assertFalse(verifyPassword(wrongCasePassword, hash),
                    "Password com case diferente deveria ser verificada como incorreta");
        }

        @Test
        @DisplayName("Deve verificar password vazia corretamente")
        void shouldVerifyEmptyPasswordCorrectly() {
            String emptyPassword = "";
            String hash = hashPassword(emptyPassword);

            assertTrue(verifyPassword(emptyPassword, hash), "Password vazia deveria ser verificada corretamente");
            assertFalse(verifyPassword("naoVazia", hash),
                    "Password não vazia não deveria passar na verificação de hash vazio");
        }

        @Test
        @DisplayName("Deve verificar passwords com caracteres especiais")
        void shouldVerifyPasswordsWithSpecialCharacters() {
            String password = "minha@Password#2023!";
            String hash = hashPassword(password);

            assertTrue(verifyPassword(password, hash),
                    "Password com caracteres especiais deveria ser verificada corretamente");
        }

        @Test
        @DisplayName("Deve verificar passwords com caracteres acentuados")
        void shouldVerifyPasswordsWithAccentedCharacters() {
            String password = "minhaPasswordComAção";
            String hash = hashPassword(password);

            assertTrue(verifyPassword(password, hash),
                    "Password com caracteres acentuados deveria ser verificada corretamente");
        }

        @Test
        @DisplayName("Deve retornar falso para hash nulo")
        void shouldReturnFalseForNullHash() {
            String password = "qualquerPassword";

            assertFalse(verifyPassword(password, null), "Verificação com hash nulo deveria retornar false");
        }
    }
}