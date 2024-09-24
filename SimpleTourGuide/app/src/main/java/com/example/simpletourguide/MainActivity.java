package com.example.simpletourguide;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements ListFragment.OnFragmentInteractionListener, TextFragment.OnFragmentInteractionListener{

    private int pos;
    private boolean wasWelcomed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null)
        {
            pos = savedInstanceState.getInt("position");
            wasWelcomed = savedInstanceState.getBoolean("wasWelcomed");
            Log.d("Saving", "Loaded the position: " + pos);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(wasWelcomed)
        {
            tellTextFrag();
        }
        else
        {
            wasWelcomed = true;
            pos = -1;
        }
    }

    public void onFragmentInteraction(int position)
    {
        pos = position;

        Log.d("TAG", "Main Activity. Value passed = " + pos);
        tellTextFrag();

//        FragmentManager fragMgr = getSupportFragmentManager();
//        TextFragment textFrag = (TextFragment) fragMgr.findFragmentById(R.id.textFragCont);
//        if(textFrag != null) {
//            textFrag.updateText(pos);
//        }

    }

    private void tellTextFrag()
    {
        FragmentManager fragMgr = getSupportFragmentManager();
        TextFragment textFrag = (TextFragment) fragMgr.findFragmentById(R.id.textFragCont);
        if(textFrag != null) {
            Log.d("TAG", "Updating textFrag");
            textFrag.updateText(pos);
        }
    }


    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d("Saving", "Saving the position: " + pos);
        outState.putInt("position", pos);
        outState.putBoolean("wasWelcomed", wasWelcomed);
    }

}