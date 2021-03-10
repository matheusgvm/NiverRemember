package com.vilaca.niverremember.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.vilaca.niverremember.config.ConfiguracaoFirebase;
import com.vilaca.niverremember.helper.UsuarioFirebase;

public class Aniversario {

    public static final int IDADE_NULA = -1;

    private String nome;
    private String data;
    private int idade;
    private String foto;

    public Aniversario() {
    }

    public void salvar(){
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();

        firebase.child("aniversarios")
                .child(UsuarioFirebase.getIdentifacadorUsuario())
                .push()
                .setValue(this);

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
