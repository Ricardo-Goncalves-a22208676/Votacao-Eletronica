package com.votacao.testes;

import com.votacao.model.Voto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para Voto")
class VotoTest {

    @Nested
    @DisplayName("Testes para construtores")
    class ConstructorTests {

        @Test
        @DisplayName("Construtor vazio deve criar voto sem dados")
        void emptyConstructorShouldCreateEmptyVote() {
            Voto voto = new Voto();

            assertEquals(0, voto.getId(), "ID deveria ser 0 por defeito");
            assertEquals(0, voto.getEleitorId(), "EleitorId deveria ser 0 por defeito");
            assertEquals(0, voto.getIdCandidato(), "IdCandidato deveria ser 0 por defeito");
            assertNull(voto.getTimestamp(), "Timestamp deveria ser nulo por defeito");
        }

        @Test
        @DisplayName("Construtor com parâmetros deve definir timestamp automaticamente")
        void parameterizedConstructorShouldSetTimestamp() {
            LocalDateTime antes = LocalDateTime.now();

            Voto voto = new Voto(123, 456);

            LocalDateTime depois = LocalDateTime.now();

            assertEquals(123, voto.getEleitorId(), "EleitorId deveria ser definido corretamente");
            assertEquals(456, voto.getIdCandidato(), "IdCandidato deveria ser definido corretamente");
            assertNotNull(voto.getTimestamp(), "Timestamp não deveria ser nulo");

            // Verifica se o timestamp foi definido aproximadamente agora
            assertTrue(voto.getTimestamp().isAfter(antes) || voto.getTimestamp().isEqual(antes),
                    "Timestamp deveria ser após ou igual ao momento antes da criação");
            assertTrue(voto.getTimestamp().isBefore(depois) || voto.getTimestamp().isEqual(depois),
                    "Timestamp deveria ser antes ou igual ao momento depois da criação");
        }

        @Test
        @DisplayName("Construtor deve definir timestamp muito próximo do momento atual")
        void constructorShouldSetTimestampCloseToNow() {
            LocalDateTime agora = LocalDateTime.now();
            Voto voto = new Voto(1, 2);

            // Verifica se a diferença é menor que 1 segundo
            long diferenca = ChronoUnit.MILLIS.between(agora, voto.getTimestamp());
            assertTrue(Math.abs(diferenca) < 1000,
                    "Timestamp deveria estar a menos de 1 segundo do momento atual, diferença: " + diferenca + "ms");
        }

        @Test
        @DisplayName("Múltiplos votos criados sequencialmente devem ter timestamps diferentes")
        void multipleVotesShoudHaveDifferentTimestamps() throws InterruptedException {
            Voto voto1 = new Voto(1, 2);
            Thread.sleep(1); // Pausa mínima para garantir timestamps diferentes
            Voto voto2 = new Voto(3, 4);

            assertNotEquals(voto1.getTimestamp(), voto2.getTimestamp(),
                    "Votos criados em momentos diferentes devem ter timestamps diferentes");
            assertTrue(voto2.getTimestamp().isAfter(voto1.getTimestamp()),
                    "Segundo voto deveria ter timestamp posterior ao primeiro");
        }
    }

    @Nested
    @DisplayName("Testes para getters e setters")
    class GettersAndSettersTests {

        @Test
        @DisplayName("Deve permitir definir e obter ID")
        void shouldAllowSettingAndGettingId() {
            Voto voto = new Voto();
            voto.setId(999);

            assertEquals(999, voto.getId(), "ID deveria ser definido e obtido corretamente");
        }

        @Test
        @DisplayName("Deve permitir definir e obter EleitorId")
        void shouldAllowSettingAndGettingEleitorId() {
            Voto voto = new Voto();
            voto.setEleitorId(123);

            assertEquals(123, voto.getEleitorId(), "EleitorId deveria ser definido e obtido corretamente");
        }

        @Test
        @DisplayName("Deve permitir definir e obter IdCandidato")
        void shouldAllowSettingAndGettingIdCandidato() {
            Voto voto = new Voto();
            voto.setIdCandidato(456);

            assertEquals(456, voto.getIdCandidato(), "IdCandidato deveria ser definido e obtido corretamente");
        }

        @Test
        @DisplayName("Deve permitir definir e obter Timestamp personalizado")
        void shouldAllowSettingAndGettingCustomTimestamp() {
            Voto voto = new Voto();
            LocalDateTime timestampPersonalizado = LocalDateTime.of(2023, 12, 25, 10, 30, 0);
            voto.setTimestamp(timestampPersonalizado);

            assertEquals(timestampPersonalizado, voto.getTimestamp(),
                    "Timestamp personalizado deveria ser definido e obtido corretamente");
        }

        @Test
        @DisplayName("Deve permitir redefinir valores após construção")
        void shouldAllowRedefinitionAfterConstruction() {
            Voto voto = new Voto(123, 456);
            LocalDateTime timestampOriginal = voto.getTimestamp();

            // Redefine valores
            voto.setEleitorId(789);
            voto.setIdCandidato(101112);
            LocalDateTime novoTimestamp = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
            voto.setTimestamp(novoTimestamp);

            assertEquals(789, voto.getEleitorId(), "EleitorId deveria ser redefinido");
            assertEquals(101112, voto.getIdCandidato(), "IdCandidato deveria ser redefinido");
            assertEquals(novoTimestamp, voto.getTimestamp(), "Timestamp deveria ser redefinido");
            assertNotEquals(timestampOriginal, voto.getTimestamp(),
                    "Novo timestamp deveria ser diferente do original");
        }
    }

    @Nested
    @DisplayName("Testes para validação de domínio")
    class DomainValidationTests {

        @Test
        @DisplayName("Deve aceitar IDs positivos")
        void shouldAcceptPositiveIds() {
            Voto voto = new Voto(1, 1);

            assertDoesNotThrow(() -> {
                voto.setId(1);
                voto.setEleitorId(999);
                voto.setIdCandidato(888);
            }, "IDs positivos deveriam ser aceites sem exceções");
        }

        @Test
        @DisplayName("Deve aceitar ID zero")
        void shouldAcceptZeroId() {
            Voto voto = new Voto();

            assertDoesNotThrow(() -> {
                voto.setId(0);
                voto.setEleitorId(0);
                voto.setIdCandidato(0);
            }, "ID zero deveria ser aceite sem exceções");
        }

        @Test
        @DisplayName("Deve aceitar IDs negativos (para testes ou casos especiais)")
        void shouldAcceptNegativeIds() {
            Voto voto = new Voto();

            assertDoesNotThrow(() -> {
                voto.setId(-1);
                voto.setEleitorId(-999);
                voto.setIdCandidato(-888);
            }, "IDs negativos deveriam ser aceites (para casos especiais de teste)");
        }
    }
}