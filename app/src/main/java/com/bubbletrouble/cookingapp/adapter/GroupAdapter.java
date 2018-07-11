package com.bubbletrouble.cookingapp.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bubbletrouble.cookingapp.R;
import com.bubbletrouble.cookingapp.RecyclerViewClickListener;
import com.bubbletrouble.cookingapp.model.Group;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.PersonViewHolder>{

    private List<Group> group;
    private static RecyclerViewClickListener itemListener;

    public static class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CardView cv;
        TextView personName;
        TextView personAge;
        ImageView personPhoto;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            personName = itemView.findViewById(R.id.person_name);
            personAge = itemView.findViewById(R.id.person_age);
            personPhoto = itemView.findViewById(R.id.person_photo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());
        }
    }

    public GroupAdapter(List<Group> group, RecyclerViewClickListener itemListener){
        this.group = group;
        this.itemListener = itemListener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_cardview_main, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.personName.setText(group.get(i).getMembers().get(i).getUsername());
        personViewHolder.personAge.setText(group.get(i).getMembers().get(i).getEmail());
      //  personViewHolder.personPhoto.setImageResource(group.get(i).photoId);
    }

    @Override
    public int getItemCount() {
        return group.size();
    }
}
