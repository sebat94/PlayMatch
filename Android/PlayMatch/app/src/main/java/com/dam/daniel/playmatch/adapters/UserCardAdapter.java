package com.dam.daniel.playmatch.adapters;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.dam.daniel.playmatch.R;
import com.dam.daniel.playmatch.MatchCardUserDetailsFragment;
import com.dam.daniel.playmatch.models.UserCard;

import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

public class UserCardAdapter extends Adapter<UserCardAdapter.ViewHolder>  {

    private FragmentManager fragmentManager;
    private List users;

    public UserCardAdapter(FragmentManager fragmentManager, List<UserCard> users) {
        this.fragmentManager = fragmentManager;
        this.users = users;
    }

    @NonNull
    public UserCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cardPresentation = inflater.inflate(R.layout.card_presentation, parent, false);
        return new UserCardAdapter.ViewHolder(cardPresentation);
    }

    public void onBindViewHolder(@NonNull UserCardAdapter.ViewHolder holder, int position) {
        final UserCard userCard = (UserCard)this.users.get(position);
        holder.getName().setText((CharSequence)(userCard.id + ". " + userCard.name));
        holder.getCity().setText((CharSequence)userCard.city);
        Glide.with((View)holder.getImage()).load(userCard.url).into(holder.getImage());
        holder.itemView.setOnClickListener((OnClickListener)(new OnClickListener() {
            public final void onClick(View v) {
                //Toast.makeText(v.getContext(), (CharSequence)userCard.name, Toast.LENGTH_LONG).show();
                // Show User Details On Fragment
                Bundle bundle = new Bundle();
                bundle.putInt("userId", userCard.id);
                // Set Fragmentclass Arguments
                MatchCardUserDetailsFragment matchCardUserDetailsFragment = new MatchCardUserDetailsFragment();
                matchCardUserDetailsFragment.setArguments(bundle);
                // Create Fragment Transaction With Animation
                FragmentTransaction ft = fragmentManager.beginTransaction();
                //ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);   // TODO: definir en drawable las animaciones
                //ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                ft.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
                ft.add(R.id.fragment_container_ActivityMatch, matchCardUserDetailsFragment, "USER_DETAILS_FRAGMENT").commit();
            }
        }));
    }

    public int getItemCount() {
        return this.users.size();
    }

    public static final class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        @NonNull
        private final TextView name;
        @NonNull
        private TextView city;
        @NonNull
        private ImageView image;

        @NonNull
        public final TextView getName() {
            return this.name;
        }

        @NonNull
        public final TextView getCity() {
            return this.city;
        }

        public final void setCity(@NonNull TextView var1) {
            this.city = var1;
        }

        @NonNull
        public final ImageView getImage() {
            return this.image;
        }

        public final void setImage(@NonNull ImageView var1) {
            this.image = var1;
        }

        public ViewHolder(@NonNull View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.item_name);
            this.city = (TextView) view.findViewById(R.id.item_city);
            this.image = (ImageView) view.findViewById(R.id.item_image);
        }
    }

}
