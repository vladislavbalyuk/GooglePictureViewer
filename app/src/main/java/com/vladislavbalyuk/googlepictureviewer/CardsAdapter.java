package com.vladislavbalyuk.googlepictureviewer;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardViewHolder>{

        List<String> list;

        private Picasso picasso;

        public CardsAdapter(List<String> list) {
            this.list = list;

            try {
                Picasso.setSingletonInstance(picasso);
            } catch (Exception e) {
            };
        }

        @Override
        public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card, viewGroup, false);
            CardViewHolder pvh = new CardViewHolder(v);
            return pvh;    }

        @Override
        public void onBindViewHolder(CardViewHolder holder, int position) {
            picasso.with(holder.image.getContext())
                    .load(list.get(position))
                    .into(holder.image);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
            CardView cv;
            ImageView image;

            Date dateLastTap;
            int countTap;

            CardViewHolder(View itemView) {
                super(itemView);
                cv = (CardView)itemView.findViewById(R.id.cv);
                image = (ImageView)itemView.findViewById(R.id.image);
                image.setOnTouchListener(this);
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int actionMask = (event.getAction() & MotionEvent.ACTION_MASK);
                int pointerCount = event.getPointerCount();

                switch (actionMask) {
                    case MotionEvent.ACTION_DOWN:

                        if(dateLastTap == null || new Date().getTime() - dateLastTap.getTime() > 300){
                            countTap = 0;
                        }
                        else {
                            countTap++;
                        }
                        dateLastTap = new Date();

                        if(countTap == 4){
                            ((CardsActivity)v.getContext()).finish();
                        }

                        break;
                }
                return true;
            }
        }

    }