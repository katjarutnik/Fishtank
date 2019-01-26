package com.xd.akvarij;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FishAdapter extends RecyclerView.Adapter<FishAdapter.MyViewHolder> {

    private List<Fish> fishList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView id, alive, age;

        public MyViewHolder(View view) {
            super(view);
            id = view.findViewById(R.id.id);
            alive = view.findViewById(R.id.alive);
            age = view.findViewById(R.id.age);
        }
    }

    public FishAdapter(List<Fish> fishList) {
        this.fishList = fishList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fish_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Fish fish = fishList.get(position);
        holder.id.setText("Fish " + String.valueOf(fish.getId()));
        holder.alive.setText(fish.getAlive() ? "alive" : "dead");
        holder.age.setText(String.valueOf(fish.getAge()) + " days old");
    }

    @Override
    public int getItemCount() {
        return fishList.size();
    }

}
