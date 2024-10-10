package com.example.calculatorapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScientificFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScientificFragment extends Fragment {
    private NumPad_Fragment.OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String buttonValue);
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScientificFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Ensure the activity implements the listener interface
            mListener = (NumPad_Fragment.OnFragmentInteractionListener) context;
            Log.d("onAttachSci", "Attached scientific to main activity!");
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null; // Avoid memory leaks by setting listener to null when detached
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScientificFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScientificFragment newInstance(String param1, String param2) {
        ScientificFragment fragment = new ScientificFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.d("Scientific", "Fragment Created");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_num_pad_, container, false);

        // Create the main layout view
        LinearLayout mainLayout = new LinearLayout(getActivity());
        mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);

        // create and add the GridLayout for calculator buttons
        GridLayout gridLayout = new GridLayout(getActivity());
        gridLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        gridLayout.setColumnCount(6);
        gridLayout.setRowCount(4);

        addScientificButtons(gridLayout);
        mainLayout.addView(gridLayout);

        Log.d("onCreateViewSci", "Created the science view");

        // return the root view
        return mainLayout;
    }

    private void addScientificButtons(GridLayout layout) {
        // define button ids
        String[][] buttonIdsAndText = {
                {"leftParen", "rightParen", "squared", "cubed", "eRaisedX", "tenRaisedX" },
                {"raisedToY", "enterExponent", "inverse", "sin", "cos", "tan",},
                {"squareRoot", "cubeRoot", "yRoot", "sinh", "cosh", "tanh"},
                {"factorial", "logBase10", "ln", "e", "pi", "rand"},
        };

        for(int row = 0; row < buttonIdsAndText.length;row++)
        {
            for(int col = 0; col < buttonIdsAndText[row].length; col++)
            {
                String buttonId = buttonIdsAndText[row][col];
//                Button button = new Button(getActivity());
                Button button = new Button(requireActivity());

                // set button text and id
                button.setText(getString(getResources().getIdentifier(buttonId, "string", requireActivity().getPackageName())));
                button.setId(getResources().getIdentifier(buttonId, "id", requireActivity().getPackageName()));

                // set layout parameters for the button
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(row, 1f);
                params.columnSpec = GridLayout.spec(col, 1f);
                params.width = 0;
                params.height = 0;
                params.setMargins(10, 10, 10, 10);

                // Apply layout params to button
                button.setLayoutParams(params);

                // apply the complete style from styles.xml depending on the button
                button.setTextAppearance(getActivity(), R.style.SymbolButton);
                button.setTransformationMethod(null);
                button.setBackgroundResource(R.drawable.rounded_button_background_symbols_text);

                button.setVisibility(View.VISIBLE);


                // handle button click events
                button.setOnClickListener(v ->
                {
                    if(mListener != null) {
                        mListener.onFragmentInteraction(buttonId);
                    }
                });

                // add the button to the GridLayout
                layout.addView(button);
                Log.d("ScientificFragment", "Adding button: " + buttonId);
            }
        }
        Log.d("ScientificFragment", "Total buttons added: " + layout.getChildCount());
    }

//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        // Ensure all buttons are visible
//        for (int i = 0; i < layout.getChildCount(); i++) {
//            layout.getChildAt(i).setVisibility(View.VISIBLE);
//        }
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
}