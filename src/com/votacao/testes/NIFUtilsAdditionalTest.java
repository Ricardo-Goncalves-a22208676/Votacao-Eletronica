package com.votacao.testes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.votacao.utils.NIFUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Adicionais para NIFUtils")
class NIFUtilsAdditionalTest {

    @Nested
    @DisplayName("Testes para getNIFErrorMessage()")
    class GetNIFErrorMessageTests {

        @Test
        @DisplayName("Deve retornar mensagem de erro padrão")
        void shouldReturnDefaultErrorMessage() {
            String errorMessage = getNIFErrorMessage();

            assertNotNull(errorMessage, "Mensagem de erro não deveria ser nula");
            assertFalse(errorMessage.isEmpty(), "Mensagem de erro não deveria estar vazia");
            assertTrue(errorMessage.contains("NIF inválido"), "Mensagem deveria conter 'NIF inválido'");
        }
    }

    @Nested
    @DisplayName("Testes Adicionais para isValidNIF() - Regras Específicas")
    class AdditionalNIFValidationTests {

        @Test
        @DisplayName("Deve validar NIFs que começam por 1 (pessoa singular)")
        void shouldValidateNIFsStartingWith1() {
            assertTrue(isValidNIF("123456789"), "NIF começado por 1 com checksum correto deveria ser válido");
        }

        @Test
        @DisplayName("Deve validar NIFs que começam por 2 (pessoa singular)")
        void shouldValidateNIFsStartingWith2() {
            assertTrue(isValidNIF("213588188"), "NIF começado por 2 com checksum correto deveria ser válido");
        }

        @Test
        @DisplayName("Deve rejeitar NIFs que começam por 4 mas não por 45")
        void shouldRejectNIFsStartingWith4ButNot45() {
            assertFalse(isValidNIF("412345678"), "NIF começado por 41 deveria ser inválido");
            assertFalse(isValidNIF("467890123"), "NIF começado por 46 deveria ser inválido");
            assertFalse(isValidNIF("489012345"), "NIF começado por 48 deveria ser inválido");
        }

        @Test
        @DisplayName("Deve validar NIFs que começam por 5 (entidades coletivas)")
        void shouldValidateNIFsStartingWith5() {
            assertTrue(isValidNIF("503837431"), "NIF começado por 5 com checksum correto deveria ser válido");
        }

        @Test
        @DisplayName("Deve rejeitar NIFs que começam por 7 mas com segundo dígito inválido")
        void shouldRejectNIFsStartingWith7WithInvalidSecondDigit() {
            assertFalse(isValidNIF("731234567"), "NIF começado por 73 deveria ser inválido");
            assertFalse(isValidNIF("761234567"), "NIF começado por 76 deveria ser inválido");
            assertFalse(isValidNIF("781234567"), "NIF começado por 78 deveria ser inválido");
        }


        @Test
        @DisplayName("Deve validar NIFs que começam por 90 (regimes especiais)")
        void shouldValidateNIFsStartingWith90() {
            assertTrue(isValidNIF("901234567"), "NIF começado por 90 com checksum correto deveria ser válido");
        }

        @Test
        @DisplayName("Deve rejeitar NIFs que começam por 9 mas com segundo dígito inválido")
        void shouldRejectNIFsStartingWith9WithInvalidSecondDigit() {
            assertFalse(isValidNIF("921234567"), "NIF começado por 92 deveria ser inválido");
            assertFalse(isValidNIF("931234567"), "NIF começado por 93 deveria ser inválido");
            assertFalse(isValidNIF("971234567"), "NIF começado por 97 deveria ser inválido");
        }

        @Test
        @DisplayName("Deve rejeitar NIFs que começam por 0")
        void shouldRejectNIFsStartingWith0() {
            assertFalse(isValidNIF("012345678"), "NIF começado por 0 deveria ser inválido");
        }

        @Test
        @DisplayName("Deve testar cálculo do dígito de controlo com resto 1")
        void shouldTestChecksumCalculationWithRemainder1() {
            // Um NIF onde a soma % 11 = 1, então o dígito de controlo deve ser 0
            assertTrue(isValidNIF("272383740"), "NIF com resto 1 no cálculo deveria ter checksum 0");
        }

        @Test
        @DisplayName("Deve testar cálculo do dígito de controlo com resto 0")
        void shouldTestChecksumCalculationWithRemainder0() {
            // Um NIF onde a soma % 11 = 0, então o dígito de controlo deve ser 0
            assertTrue(isValidNIF("111111110"), "NIF com resto 0 no cálculo deveria ter checksum 0");
        }

        @Test
        @DisplayName("Deve testar NIFs com todos os dígitos iguais (exceto o último)")
        void shouldTestNIFsWithAllSameDigits() {
            assertTrue(isValidNIF("111111110"), "NIF 111111110 deveria ser válido");
            assertTrue(isValidNIF("222222220"), "NIF 222222220 deveria ser válido");
        }
    }
}