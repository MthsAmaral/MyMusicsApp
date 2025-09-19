package com.example.mymusics;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.mymusics.db.bean.Musica;
import com.example.mymusics.db.dal.MusicaDAL;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MusicasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicasFragment extends Fragment {

    private ListView lvMusicas;
    private MainActivity mainActivity;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MusicasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MusicasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicasFragment newInstance(String param1, String param2) {
        MusicasFragment fragment = new MusicasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_list,container,false);
        lvMusicas = view.findViewById(R.id.listView);
        //segurar no item dda lista apaga
        lvMusicas.setOnItemLongClickListener((adapterView, view1, i, l) -> {
            MusicaDAL dal = new MusicaDAL(view.getContext());
            Musica musica =(Musica)adapterView.getItemAtPosition(i);
            dal.apagar(musica.getId());
            //atualiza o listView
            //((ArrayAdapter)lvGeneros.getAdapter()).notifyDataSetChanged();
            carregarMusicas(view);
            return true;
        });
        //clicar irá editar
        lvMusicas.setOnItemClickListener((adapterView, view2, i, l) -> {
            mainActivity.cadastrarMusicas((Musica)adapterView.getItemAtPosition(i));
        });
        carregarMusicas(view);
        return view;
    }
    //chama adapter com item_layout personalizado
    private void carregarMusicas(View view) {
        MusicaDAL dal = new MusicaDAL(view.getContext());
        List<Musica> musicaList = dal.get("");
        lvMusicas.setAdapter(new MusicaAdapter(view.getContext(), musicaList));
    }
}