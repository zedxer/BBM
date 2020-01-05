package com.example.naqi.bebettermuslim.adapter;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.naqi.bebettermuslim.R;
import com.example.naqi.bebettermuslim.models.SurahDataHolder;

import java.util.List;

public class SurahNameAdapter extends
        RecyclerView.Adapter<SurahNameAdapter.ViewHolder> {

    Context context;
    SurahNameAdapter adapter;
    private Typeface ayaFont;
    private Typeface ayaSeparatorFont;
    private String ayaSeparator;

    SurahOnClickListener onClickListener;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        adapter = this;
        LayoutInflater inflater = LayoutInflater.from(context);

//        ayaFont = Typeface.createFromAsset(context.getAssets(), "fonts/PDMS_Saleem_QuranFont-signed.ttf");
//        ayaSeparatorFont = Typeface.createFromAsset(context.getAssets(), "fonts/Scheherazade-Regular.ttf");
//        ayaSeparator = Character.toString((char) 1757);

        // Inflate the custom layout
        View ayaView = inflater.inflate(R.layout.surah_search_row, parent, false);
        // Return a new holder INSTANCE

        return new ViewHolder(ayaView);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final SurahDataHolder surahDataHolder = surahDataHolders.get(position);

        // Set item views based on the data model
        TextView indexTextView = holder.indexTextView;
        TextView englishNameTextView = holder.englishNameTextView;
        TextView arabicNameTextView = holder.arabicNameTextView;
        View rowView = holder.row;

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(v, surahDataHolder);
            }
        });

        indexTextView.setText(String.valueOf(surahDataHolder.getIndex()) + ".");
        englishNameTextView.setText(surahDataHolder.getEnglishSurahName());
        arabicNameTextView.setText(surahDataHolder.getArabicSurahName());

    }


    @Override
    public int getItemCount() {
        return surahDataHolders.size();
    }

    private List<SurahDataHolder> surahDataHolders;

    public SurahNameAdapter(List<SurahDataHolder> surahDataHolders, SurahOnClickListener onClickListener) {
        this.surahDataHolders = surahDataHolders;
        this.onClickListener = onClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView indexTextView;
        public TextView englishNameTextView;
        public TextView arabicNameTextView;
        public View row;

        public ViewHolder(View itemView) {
            super(itemView);
            row = itemView;
            indexTextView = itemView.findViewById(R.id.index_textview);
            englishNameTextView = itemView.findViewById(R.id.surah_name_english);
            arabicNameTextView = itemView.findViewById(R.id.surah_name_arabic);
        }

    }
    public interface SurahOnClickListener {
        public void onItemClick(View v, SurahDataHolder surahDataHolder);
    }

}