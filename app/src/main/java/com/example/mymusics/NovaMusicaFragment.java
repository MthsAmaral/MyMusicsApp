package com.example.mymusics;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.mymusics.db.bean.Genero;
import com.example.mymusics.db.bean.Musica;
import com.example.mymusics.db.dal.GeneroDAL;
import com.example.mymusics.db.dal.MusicaDAL;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NovaMusicaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NovaMusicaFragment extends Fragment {

    private TextInputEditText etNomeMusica, etCantor, etAnoLancamento, etDuracao;
    private AutoCompleteTextView etGenero;
    private Button btnSalvar;
    private ArrayList<Genero> listaGeneros;
    private Musica musicaAtual = null;
    private int musicaId = -1; //nova musica



    public NovaMusicaFragment() {
    }

    public static NovaMusicaFragment newInstance(int musicaId) {
        NovaMusicaFragment fragment = new NovaMusicaFragment();
        Bundle args = new Bundle();
        args.putInt("musica_id", musicaId); // Musica deve implementar Serializable ou Parcelable
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            musicaId = getArguments().getInt("musica_id",-1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nova_musica, container, false);

        etNomeMusica = view.findViewById(R.id.etNomeMusica);
        etCantor = view.findViewById(R.id.etCantor);
        etGenero = view.findViewById(R.id.etGenero);
        etAnoLancamento = view.findViewById(R.id.etAnoLancamento);
        etDuracao = view.findViewById(R.id.etDuracao);
        btnSalvar = view.findViewById(R.id.btnSalvarMusica);

        popularGeneros();

        if (musicaId != -1) {
            MusicaDAL dal = new MusicaDAL(requireContext());
            musicaAtual = dal.get(musicaId);
            if(musicaAtual != null)
                preencherCamposEdicao();
            else
                musicaAtual = null;
        }

        btnSalvar.setOnClickListener(v -> salvarMusica());

        return view;
    }

    private void preencherCamposEdicao() {
        etNomeMusica.setText(musicaAtual.getTitulo());
        etCantor.setText(musicaAtual.getInterprete());
        etAnoLancamento.setText(String.valueOf(musicaAtual.getAno()));

        // Converter double para MM:SS
        int minutos = (int) musicaAtual.getDuracao();
        int segundos = (int) ((musicaAtual.getDuracao() - minutos) * 60);
        etDuracao.setText(String.format("%02d:%02d", minutos, segundos));

        etGenero.setText(musicaAtual.getGenero().getNome(), false);
    }

    private void popularGeneros() {
        GeneroDAL generoDAL = new GeneroDAL(requireContext());
        listaGeneros = generoDAL.get(""); // Busca todos os gêneros do banco

        // Cria uma lista apenas com os NOMES dos gêneros para o adapter
        List<String> nomesGeneros = new ArrayList<>();

        for(int i=0;i<listaGeneros.size();i++){
            nomesGeneros.add(listaGeneros.get(i).getNome());
        }

        // Adapter para o AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                nomesGeneros
        );
        etGenero.setAdapter(adapter);
    }

    private void salvarMusica() {
        // 1️Validando se os campos
        if (etNomeMusica.getText().toString().isEmpty() || etCantor.getText().toString().isEmpty() ||
                etAnoLancamento.getText().toString().isEmpty() || etDuracao.getText().toString().isEmpty() ||
                etGenero.getText().toString().isEmpty()) {

            Toast.makeText(getContext(), "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //Verificar se duração está no formato MM:SS
            double duracao = parseDuracao(etDuracao.getText().toString());
            if (duracao == -1) {
                Toast.makeText(getContext(), "Formato de duração inválido! Use MM:SS", Toast.LENGTH_SHORT).show();
            }
            else
            {
                //Encontrar o objeto Genero
                String nomeGeneroSelecionado = etGenero.getText().toString();
                Genero generoSelecionado = null;
                int flag=0;
                for(int i=0;i<listaGeneros.size() && flag!=1;i++){
                    if(listaGeneros.get(i).getNome().equalsIgnoreCase(nomeGeneroSelecionado)){
                        generoSelecionado=listaGeneros.get(i);
                        flag=1;
                    }
                }
                if (generoSelecionado == null)
                    Toast.makeText(getContext(), "Gênero inválido!", Toast.LENGTH_SHORT).show();
                else{
                    //Criando o objeto Musica ou atualizando
                    Musica novaMusica;
                    if(musicaAtual != null)
                        novaMusica = musicaAtual;
                    else
                        novaMusica = new Musica();
                    try {
                        novaMusica.setTitulo(etNomeMusica.getText().toString());
                        novaMusica.setInterprete(etCantor.getText().toString());
                        novaMusica.setAno(Integer.parseInt(etAnoLancamento.getText().toString()));
                        novaMusica.setDuracao(duracao);
                        novaMusica.setGenero(generoSelecionado);

                        //Salvando no banco de dados
                        MusicaDAL musicaDAL = new MusicaDAL(getContext());
                        if (musicaAtual == null) {
                            // SALVAR NOVA
                            if (musicaDAL.salvar(novaMusica)) {
                                Toast.makeText(getContext(), "Música salva com sucesso!", Toast.LENGTH_LONG).show();
                                limparCampos();
                            } else {
                                Toast.makeText(getContext(), "Erro ao salvar a música.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // EDITAR EXISTENTE
                            novaMusica.setId(musicaAtual.getId()); // <-- AQUI!
                            if (musicaDAL.alterar(novaMusica)) {
                                Toast.makeText(getContext(), "Música atualizada com sucesso!", Toast.LENGTH_LONG).show();
                                limparCampos();
                            } else {
                                Toast.makeText(getContext(), "Erro ao atualizar a música.", Toast.LENGTH_LONG).show();
                            }
                        }

                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Ano inválido. Verifique os números.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

    }

    // Função auxiliar para validar duração no formato MM:SS e converter para double
    private double parseDuracao(String input) {
        if (input == null || !input.contains(":"))
            return -1;
        String[] partes = input.split(":");
        if (partes.length != 2)
            return -1;
        try {
            int minutos = Integer.parseInt(partes[0]);
            int segundos = Integer.parseInt(partes[1]);
            if (minutos < 0 || segundos < 0 || segundos >= 60)
                return -1;
            return minutos + segundos / 60.0;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void limparCampos() {
        etNomeMusica.setText("");
        etCantor.setText("");
        etAnoLancamento.setText("");
        etDuracao.setText("");
        etGenero.setText("");
        musicaAtual = null;
    }


}