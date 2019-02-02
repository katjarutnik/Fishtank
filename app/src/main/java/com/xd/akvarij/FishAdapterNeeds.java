package com.xd.akvarij;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class FishAdapterNeeds extends RecyclerView.Adapter<FishAdapterNeeds.MyViewHolder> {

    private List<Fish> fishList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView age, gender, stage, pregnant, alive, feeling;
        public ProgressBar hunger, bladder, environment;

        public MyViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.list_img);
            age = view.findViewById(R.id.list_age);
            gender = view.findViewById(R.id.list_gender);
            stage = view.findViewById(R.id.list_stage);
            pregnant = view.findViewById(R.id.list_pregnant);
            hunger = view.findViewById(R.id.list_hunger);
            bladder = view.findViewById(R.id.list_bladder);
            environment = view.findViewById(R.id.list_environment);
            alive = view.findViewById(R.id.list_alive);
            feeling = view.findViewById(R.id.list_feeling);

            hunger.setMax(Constants.MAX_HUNGER);
            bladder.setMax(Constants.MAX_BLADDER);
            environment.setMax(Constants.MAX_ENVIRONMENT);
        }
    }

    public FishAdapterNeeds(List<Fish> fishList) {
        this.fishList = fishList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fish_list_row_needs, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Fish fish = fishList.get(position);
        holder.img.setImageBitmap(fish.image);
        holder.img.setAdjustViewBounds(true);
        holder.age.setText(String.valueOf(fish.getAge()) + " DAYS OLD");
        holder.gender.setText(fish.getGender() ? "♂" : "♀");
        holder.stage.setText(
                (fish.getLifeStage() == 0) ? "INFANT" :
                        (fish.getLifeStage() == 1) ? "TEEN" :
                                (fish.getLifeStage() == 2) ? "ADULT" :
                                        "ELDER");
        holder.pregnant.setText(fish.getPregnant() ?
                (fish.getEggs() > 1 ? "\uD83D\uDC95\uD83D\uDC95" : "\uD83D\uDC95") : "");
        holder.alive.setText(fish.getAlive() ? "" : "\uD83D\uDC80");
        holder.hunger.setProgress(fish.getHunger());
        holder.bladder.setProgress(fish.getBladder());
        holder.environment.setProgress(fish.getEnvironment());
        holder.feeling.setText(fish.isHappy() ? "\uD83D\uDE03" : "\uD83D\uDE2D");
    }

    @Override
    public int getItemCount() {
        return fishList.size();
    }

}
