package com.example.tictactoegame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerViewHolder>{
    private List<Player> players;
    private Context context;

    public PlayerAdapter(Context context, List<Player> players)
    {
        this.context = context;
        this.players = players;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position)
    {
        Player player = players.get(position);
        holder.playerName.setText(player.getName());
        holder.totalGames.setText(String.valueOf(player.getGames()));
        holder.totalWins.setText(String.valueOf(player.getWins()));

        // display win percent, if toggled on in settings, otherwise display nothing
//        boolean showWinPercentage = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE).getBoolean("win_percentage", false);
//        if(showWinPercentage)
//        {
//            double percent = player.getGames() > 0 ? (double) player.getWins() / player.getGames() * 100 : 0;
//            holder.winPercent.setText(String.format("%.2f%%", percent));
//        } else {
//            holder.winPercent.setText("");
//        }
    }

    @Override
    public int getItemCount()
    {
        return players.size();
    }
}
