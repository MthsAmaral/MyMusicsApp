package com.example.alodrawermenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.alodrawermenu.db.bean.Musica;

import java.util.List;

public class MusicaAdapter extends ArrayAdapter<Musica> {

    private Context context;
    private List<Musica> musicas;

    public MusicaAdapter(@NonNull Context context, @NonNull List<Musica> musicas) {
        super(context, 0, musicas);
        this.context = context;
        this.musicas = musicas;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_layout_musica, parent, false);
        }

        Musica musica = musicas.get(position);

        TextView tvId = convertView.findViewById(R.id.tvId);
        TextView tvAno = convertView.findViewById(R.id.tvAno);
        TextView tvDuracao = convertView.findViewById(R.id.tvDuracao);
        TextView tvTitulo = convertView.findViewById(R.id.tvTitulo);
        TextView tvInterprete = convertView.findViewById(R.id.tvInterprete);
        TextView tvGenero = convertView.findViewById(R.id.tvGenero);

        tvId.setText(String.valueOf(musica.getId()));
        tvAno.setText(String.valueOf(musica.getAno()));

        double duracao = musica.getDuracao(); // Ex.: 3.33 (minutos)
        int minutos = (int) duracao; // pega a parte inteira (3)
        int segundos = (int) ((duracao - minutos) * 60); // converte a parte decimal em segundos
        tvDuracao.setText(minutos + "min" + segundos + "seg");

        tvTitulo.setText(musica.getTitulo());
        tvInterprete.setText(musica.getInterprete());
        tvGenero.setText(musica.getGenero().getNome());

        return convertView;
    }
}
