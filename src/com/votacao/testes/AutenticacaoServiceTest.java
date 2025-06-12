package com.votacao.testes;

import com.votacao.model.Administrador;
import com.votacao.model.Eleitor;
import com.votacao.model.Usuario;
import com.votacao.service.AutenticacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para AutenticacaoService")
class AutenticacaoServiceTest {

    private AutenticacaoService autenticacaoService;

    @BeforeEach
    void setUp() {
        autenticacaoService = new AutenticacaoService();
    }

    @Nested
    @DisplayName("Testes para isAdministrador()")
    class IsAdministradorTests {

        @Test
        @DisplayName("Deve retornar verdadeiro para usuario administrador")
        void shouldReturnTrueForAdministrador() {
            Usuario admin = new Administrador("Admin", "admin", "hashedPassword");

            assertTrue(autenticacaoService.isAdministrador(admin),
                    "Deveria retornar true para usuario administrador");
        }

        @Test
        @DisplayName("Deve retornar falso para usuario eleitor")
        void shouldReturnFalseForEleitor() {
            Usuario eleitor = new Eleitor("Eleitor", "eleitor", "hashedPassword", "E123");

            assertFalse(autenticacaoService.isAdministrador(eleitor),
                    "Deveria retornar false para usuario eleitor");
        }

        @Test
        @DisplayName("Deve retornar falso para usuario nulo")
        void shouldReturnFalseForNullUser() {
            assertFalse(autenticacaoService.isAdministrador(null),
                    "Deveria retornar false para usuario nulo");
        }
    }

    @Nested
    @DisplayName("Testes para isEleitor()")
    class IsEleitorTests {

        @Test
        @DisplayName("Deve retornar verdadeiro para usuario eleitor")
        void shouldReturnTrueForEleitor() {
            Usuario eleitor = new Eleitor("Eleitor", "eleitor", "hashedPassword", "E123");

            assertTrue(autenticacaoService.isEleitor(eleitor),
                    "Deveria retornar true para usuario eleitor");
        }

        @Test
        @DisplayName("Deve retornar falso para usuario administrador")
        void shouldReturnFalseForAdministrador() {
            Usuario admin = new Administrador("Admin", "admin", "hashedPassword");

            assertFalse(autenticacaoService.isEleitor(admin),
                    "Deveria retornar false para usuario administrador");
        }

        @Test
        @DisplayName("Deve retornar falso para usuario nulo")
        void shouldReturnFalseForNullUser() {
            assertFalse(autenticacaoService.isEleitor(null),
                    "Deveria retornar false para usuario nulo");
        }
    }

    @Nested
    @DisplayName("Testes para consistência de tipos")
    class TypeConsistencyTests {

        @Test
        @DisplayName("Um usuario não pode ser simultaneamente administrador e eleitor")
        void userCannotBeBothAdminAndEleitor() {
            Usuario admin = new Administrador("Admin", "admin", "hashedPassword");
            Usuario eleitor = new Eleitor("Eleitor", "eleitor", "hashedPassword", "E123");

            // Admin não é eleitor
            assertTrue(autenticacaoService.isAdministrador(admin));
            assertFalse(autenticacaoService.isEleitor(admin));

            // Eleitor não é admin
            assertTrue(autenticacaoService.isEleitor(eleitor));
            assertFalse(autenticacaoService.isAdministrador(eleitor));
        }

        @Test
        @DisplayName("Deve distinguir corretamente diferentes tipos de usuario")
        void shouldDistinguishUserTypesCorrectly() {
            Usuario admin1 = new Administrador("Admin1", "admin1", "hash1");
            Usuario admin2 = new Administrador("Admin2", "admin2", "hash2");
            Usuario eleitor1 = new Eleitor("Eleitor1", "eleitor1", "hash1", "E123");
            Usuario eleitor2 = new Eleitor("Eleitor2", "eleitor2", "hash2", "E456");

            // Todos os admins são reconhecidos como admins
            assertTrue(autenticacaoService.isAdministrador(admin1));
            assertTrue(autenticacaoService.isAdministrador(admin2));

            // Todos os eleitores são reconhecidos como eleitores
            assertTrue(autenticacaoService.isEleitor(eleitor1));
            assertTrue(autenticacaoService.isEleitor(eleitor2));

            // Nenhum admin é reconhecido como eleitor
            assertFalse(autenticacaoService.isEleitor(admin1));
            assertFalse(autenticacaoService.isEleitor(admin2));

            // Nenhum eleitor é reconhecido como admin
            assertFalse(autenticacaoService.isAdministrador(eleitor1));
            assertFalse(autenticacaoService.isAdministrador(eleitor2));
        }
    }
}