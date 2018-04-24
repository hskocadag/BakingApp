package com.example.android.bakingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.utilities.ImageUtils;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>{

    private final GridItemClickListener mGridItemClickListener;
    private List<Recipe> mRecipes;
    private final Context mContext;

    public RecipeAdapter(Context context, GridItemClickListener gridItemClickListener)
    {
        mGridItemClickListener = gridItemClickListener;
        mContext = context;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grid_item_recipe_card, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        if(position >= getItemCount())
            return;

        String imageUrl = mRecipes.get(position).getImage();
        //ImageUtils.insertImageIntoView(holder.mRecipeImage, mContext, imageUrl);
        //holder.mRecipeImage.setContentDescription(mRecipes.get(position).getName());
        holder.mRecipeName.setText(mRecipes.get(position).getName());
        holder.mRecipeFor.setText("For " + Integer.toString(mRecipes.get(position).getServings()));
    }

    @Override
    public int getItemCount() {
        if(mRecipes == null)
            return 0;
        return mRecipes.size();
    }

    public interface GridItemClickListener {
        void onGridItemClick(int clickedItemId);
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        //public final ImageView mRecipeImage;
        public final TextView mRecipeName;
        public final TextView mRecipeFor;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            //mRecipeImage = itemView.findViewById(R.id.image_recipe_card);
            mRecipeName = itemView.findViewById(R.id.tv_recipe_name);
            mRecipeFor = itemView.findViewById(R.id.tv_for_people);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mGridItemClickListener.onGridItemClick(adapterPosition);
        }

    }

    public void updateRecipesArray(List<Recipe> recipes)
    {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

}
