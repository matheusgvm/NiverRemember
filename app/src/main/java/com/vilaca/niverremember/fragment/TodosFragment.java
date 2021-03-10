package com.vilaca.niverremember.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.vilaca.niverremember.R;
import com.vilaca.niverremember.activity.AddAniversarioActivity;
import com.vilaca.niverremember.activity.MainActivity;
import com.vilaca.niverremember.adapter.AniversariosAdapter;
import com.vilaca.niverremember.config.ConfiguracaoFirebase;
import com.vilaca.niverremember.helper.UsuarioFirebase;
import com.vilaca.niverremember.model.Aniversario;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodosFragment extends Fragment {

    private RecyclerView recyclerViewAniversarios;
    private AniversariosAdapter adapter;
    private ArrayList<Aniversario> listaAniversarios = new ArrayList<>();
    private DatabaseReference usuariosRef;
    private ValueEventListener valueEventListenerAniversarios;

    public TodosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_todos, container, false);

        usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("aniversarios").child(UsuarioFirebase.getIdentifacadorUsuario());

        FloatingActionButton fab = view.findViewById(R.id.fab);
        recyclerViewAniversarios= view.findViewById(R.id.RecyclerViewAniversarios);

        adapter = new AniversariosAdapter(listaAniversarios, getActivity());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewAniversarios.setLayoutManager(layoutManager);
        recyclerViewAniversarios.setHasFixedSize(true);
        recyclerViewAniversarios.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                abrirTelaAddAniversario();

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        recuperarAniversarios();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListenerAniversarios);
    }

    public void recuperarAniversarios(){
        listaAniversarios.clear();
        valueEventListenerAniversarios = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot dados: dataSnapshot.getChildren()){

                    Aniversario aniversario = dados.getValue(Aniversario.class);
                    listaAniversarios.add(aniversario);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void abrirTelaAddAniversario(){
        Intent intent = new Intent(getActivity(), AddAniversarioActivity.class);
        startActivity(intent);
    }

}
