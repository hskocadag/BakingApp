package com.example.android.bakingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.model.Step;

import java.util.List;

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.RecipeStepViewHolder>{
    private List<Step> mRecipeSteps;
    private final Context mContext;
    private int mRecipeId;
    private OnRecipeStepClickListener mOnRecipeStepClickListener;

    public interface OnRecipeStepClickListener {
        void onRecipeStepSelected(int position);
    }

    public RecipeStepAdapter(Context context)
    {
        mContext = context;
        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mOnRecipeStepClickListener = (OnRecipeStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnRecipeStepClickListener");
        }
    }

    @Override
    public RecipeStepAdapter.RecipeStepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grid_item_recipe_step, parent, false);
        return new RecipeStepAdapter.RecipeStepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeStepAdapter.RecipeStepViewHolder holder, int position) {
        if(position >= getItemCount())
            return;

        String recipeShortDesc = mRecipeSteps.get(position).getShortDescription();
        if (position >= 1) {
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(position - 1))
                    .append(". ")
                    .append(recipeShortDesc);
            holder.mRecipeStepShortDesc.setText(sb.toString());
        }
        else {
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toString(0))
                    .append(". ")
                    .append(recipeShortDesc);
            holder.mRecipeStepShortDesc.setText(sb.toString());
        }
    }

    @Override
    public int getItemCount() {
        if(mRecipeSteps == null)
            return 0;
        return mRecipeSteps.size();
    }

    public class RecipeStepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public final TextView mRecipeStepShortDesc;

        public RecipeStepViewHolder(View itemView) {
            super(itemView);
            mRecipeStepShortDesc = itemView.findViewById(R.id.tv_recipe_step_short_desc);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mOnRecipeStepClickListener.onRecipeStepSelected(adapterPosition);
        }

    }

    public void updateRecipeStepsArray(List<Step> recipeSteps, int recipeId)
    {
        mRecipeSteps = recipeSteps;
        mRecipeId = recipeId;
        notifyDataSetChanged();
    }
}
