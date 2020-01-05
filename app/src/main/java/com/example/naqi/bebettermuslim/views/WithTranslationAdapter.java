package com.example.naqi.bebettermuslim.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
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

public class WithTranslationAdapter extends
        RecyclerView.Adapter<WithTranslationAdapter.ViewHolder> {

    Context context;
    WithTranslationAdapter adapter;
    private Typeface ayaFont;
    private Typeface ayaSeparatorFont;
    private String ayaSeparator;

    private float textSize;

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    CustomOnClickListener onClickListener;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        adapter = this;
        LayoutInflater inflater = LayoutInflater.from(context);

        ayaFont = Typeface.createFromAsset(context.getAssets(), "fonts/pdms-saleem-quranfont-signed.ttf");
        ayaSeparatorFont = Typeface.createFromAsset(context.getAssets(), "fonts/Scheherazade-Regular.ttf");
        ayaSeparator = Character.toString((char) 1757);

        // Inflate the custom layout
        View ayaView = inflater.inflate(R.layout.with_trans_row_item, parent, false);
        // Return a new holder INSTANCE

        return new ViewHolder(ayaView);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final WithTranslationHolder withTranslationHolder = withTranslationHolders.get(position);

        // Set item views based on the data model
        TextView arabicTextView = holder.arabicTextView;
        TextView translationTextView = holder.translationTextView;
        View rowView = holder.row;

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(v, position, withTranslationHolder);
            }
        });

        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(withTranslationHolder.getArabicAya(), new CustomTypefaceSpan("", ayaFont), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(" " + ayaSeparator + HelperFunctions.getCodeFromNumber(String.valueOf(withTranslationHolder.getAyaNumber()))
                + " ", new CustomTypefaceSpan("", ayaSeparatorFont), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        arabicTextView.setText(new SpannableString(sb));
        translationTextView.setText(withTranslationHolder.getTranslationAya());

        arabicTextView.setTypeface(ayaFont);
        translationTextView.setTypeface(ayaFont);

        arabicTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        translationTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

    }


    @Override
    public int getItemCount() {
        return withTranslationHolders.size();
    }

    private List<WithTranslationHolder> withTranslationHolders;

    public WithTranslationAdapter(List<WithTranslationHolder> withTranslationHolders, CustomOnClickListener onClickListener) {
        this.withTranslationHolders = withTranslationHolders;
        this.onClickListener = onClickListener;
        setTextSize(60);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView arabicTextView;
        public TextView translationTextView;
        public View row;

        public ViewHolder(View itemView) {
            super(itemView);
            row = itemView;
            arabicTextView = (TextView) itemView.findViewById(R.id.arabic_verse_textview);
            translationTextView = (TextView) itemView.findViewById(R.id.translation_verse_textview);
        }

    }
}