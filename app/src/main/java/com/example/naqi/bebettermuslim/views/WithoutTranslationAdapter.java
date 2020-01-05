package com.example.naqi.bebettermuslim.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.naqi.bebettermuslim.R;

import java.util.List;

/**
 * Created by naqi on 22,January,2019
 */

public class WithoutTranslationAdapter extends
        RecyclerView.Adapter<WithoutTranslationAdapter.ViewHolder> {

    Context context;
    WithoutTranslationAdapter adapter;
    private Typeface ayaFont;

    private float textSize;

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        adapter = this;
        LayoutInflater inflater = LayoutInflater.from(context);

        ayaFont = Typeface.createFromAsset(context.getAssets(), "fonts/pdms-saleem-quranfont-signed.ttf");

        View ayaView = inflater.inflate(R.layout.without_trans_row_item, parent, false);

        return new ViewHolder(ayaView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WithoutTranslationHolder withoutTranslationHolder = withoutTranslationHolders.get(position);

        TextView textView = holder.textView;
        textView.setText(withoutTranslationHolder.getAya());
        textView.setTypeface(ayaFont);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }


    @Override
    public int getItemCount() {
        return withoutTranslationHolders.size();
    }

    private List<WithoutTranslationHolder> withoutTranslationHolders;

    // Pass in the contact array into the constructor
    public WithoutTranslationAdapter(List<WithoutTranslationHolder> withoutTranslationHolders) {
        this.withoutTranslationHolders = withoutTranslationHolders;
        setTextSize(60);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.textView);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setHighlightColor(Color.TRANSPARENT);
        }
    }
}