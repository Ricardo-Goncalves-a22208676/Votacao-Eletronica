package com.votacao.service;

import com.votacao.model.Usuario;
import com.votacao.model.Eleitor;
import com.votacao.model.Administrador;
import com.votacao.dao.UsuarioDAO;
import com.votacao.utils.PasswordUtils;

public class AutenticacaoService {
    private UsuarioDAO usuarioDAO;

    public AutenticacaoService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public Usuario autenticar(String username, String password) {
        Usuario usuario = usuarioDAO.buscarPorUsername(username);
        if (usuario != null && PasswordUtils.verifyPassword(password, usuario.getPasswordHash())) {
            return usuario;
        }
        return null;
    }

    public boolean isAdministrador(Usuario usuario) {
        return usuario instanceof Administrador;
    }

    public boolean isEleitor(Usuario usuario) {
        return usuario instanceof Eleitor;
    }
}