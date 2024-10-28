package com.example.tictactoegame;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class PlayerViewHolder extends RecyclerView.ViewHolder{
    public TextView playerName;
    public TextView totalGames;
    public TextView totalWins;
//    public TextView winPercent;

    public PlayerViewHolder(View itemView)
    {
        super(itemView);
        playerName = itemView.findViewById(R.id.playerName);
        totalGames = itemView.findViewById(R.id.totalGames);
        totalWins = itemView.findViewById(R.id.totalWins);
//        winPercent = itemView.findViewById(R.id.winPercent);
    }
}
