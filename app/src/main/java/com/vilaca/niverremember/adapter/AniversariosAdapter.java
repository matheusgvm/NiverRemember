package com.vilaca.niverremember.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vilaca.niverremember.R;
import com.vilaca.niverremember.model.Aniversario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AniversariosAdapter extends RecyclerView.Adapter<AniversariosAdapter.MyViewHolder> {

    private List<Aniversario> aniversarios;
    private Context context;

    public AniversariosAdapter(List<Aniversario> listaAniversarios, Context c) {
        this.aniversarios = listaAniversarios;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_aniversarios, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Aniversario aniversario = aniversarios.get(position);

        holder.nome.setText(aniversario.getNome());
        holder.data.setText(aniversario.getData());

        if(aniversario.getIdade() != -1)
        {
            holder.idade.setText(String.valueOf(aniversario.getIdade()));
        }else holder.idade.setText("");

        if(aniversario.getFoto() != null){
            Uri uri = Uri.parse(aniversario.getFoto());
            Glide.with(context).load(uri).into(holder.foto);
        }else holder.foto.setImageResource(R.drawable.padrao);
    }

    @Override
    public int getItemCount() {
        return aniversarios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView foto;
        TextView nome, data,idade;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageViewFotoAniversario);
            nome = itemView.findViewById(R.id.textNomeAniversario);
            data = itemView.findViewById(R.id.textDataAniversario);
            idade = itemView.findViewById(R.id.textIdadeAniversario);
        }
    }

}


