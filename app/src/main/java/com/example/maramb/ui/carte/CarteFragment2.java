package com.example.maramb.ui.carte;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.maramb.R;
import com.example.maramb.ui.ra.RaViewModel;
import com.example.maramb.utils.AmbianceMarker;

import java.util.HashMap;

public class CarteFragment2 extends Fragment {
    private CarteViewModel carteViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        carteViewModel =
                new ViewModelProvider(this).get(CarteViewModel.class);
        View root = inflater.inflate(R.layout.fragment_carte2, container, false);
        final TextView textView = root.findViewById(R.id.text_carte2);
        carteViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        HashMap<Integer, AmbianceMarker> allAmbiancesMarkers = ((CarteActivity)getActivity()).getAmbianceMarkers();

        return root;
    }
}

//package com.example.maramb.ui.carte;
//
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.example.maramb.R;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link CarteFragment2#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class CarteFragment2 extends Fragment {
//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public CarteFragment2() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment CarteFragment2.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static CarteFragment2 newInstance(String param1, String param2) {
//        CarteFragment2 fragment = new CarteFragment2();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_carte2, container, false);
//    }
//}