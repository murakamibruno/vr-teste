package com.teste.vr.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MyUtils {

    public String encodeSenha(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    public boolean checaSeSenhasMatches(String senha1, String senha2) {
        return BCrypt.checkpw(senha1, senha2);
    }
}
