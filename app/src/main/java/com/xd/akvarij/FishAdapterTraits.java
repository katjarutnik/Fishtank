package com.xd.akvarij;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

// TODO ON TOUCH EVENT KILL FISH YES NO
public class FishAdapterTraits extends RecyclerView.Adapter<FishAdapterTraits.MyViewHolder> {

    private List<Fish> fishList;
    private MyCallback myCallback;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView age, gender, stage, pregnant, alive;
        public ProgressBar speed, vision, fertility;

        public MyViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.list_img);
            age = view.findViewById(R.id.list_age);
            gender = view.findViewById(R.id.list_gender);
            stage = view.findViewById(R.id.list_stage);
            pregnant = view.findViewById(R.id.list_pregnant);
            speed = view.findViewById(R.id.list_speed);
            vision = view.findViewById(R.id.list_vision);
            fertility = view.findViewById(R.id.list_fertility);
            alive = view.findViewById(R.id.list_alive);

            speed.setMax(Constants.MAX_HORIZONTAL_SPEED);
            vision.setMax(Constants.MAX_VISION);
            fertility.setMax(100);
        }
    }

    public FishAdapterTraits(List<Fish> fishList, MyCallback myCallback) {
        this.fishList = fishList;
        this.myCallback = myCallback;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fish_list_row_traits, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Fish fish = fishList.get(position);
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
        holder.speed.setProgress(fish.getSpeed());
        holder.vision.setProgress(fish.getVision());
        holder.fertility.setProgress(fish.getFertility());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCallback.removeFish(fish, position);
                Toast toast = Toast.makeText(v.getContext(), "Bye bye fish!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 30, 24);
                toast.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return fishList.size();
    }

}
