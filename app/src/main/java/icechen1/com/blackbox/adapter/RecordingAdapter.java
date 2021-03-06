package icechen1.com.blackbox.adapter;

import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.Date;

import icechen1.com.blackbox.R;
import icechen1.com.blackbox.common.CursorRecyclerViewAdapter;
import icechen1.com.blackbox.fragments.PlayerDialogFragment;
import icechen1.com.blackbox.provider.recording.RecordingCursor;
import icechen1.com.blackbox.provider.recording.RecordingSelection;

import static humanize.Humanize.duration;
import static humanize.Humanize.naturalTime;

public class RecordingAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder, RecordingCursor> implements Filterable {

    private final FragmentActivity mContext;
    private boolean mIsFavorite;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTitle;
        public final TextView mLength;
        private final TextView mDate;

        public ViewHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.title);
            mLength = (TextView) itemView.findViewById(R.id.length);
            mDate = (TextView) itemView.findViewById(R.id.date);
        }
    }


    public RecordingAdapter(FragmentActivity c, boolean isFavorite){
        this(c, isFavorite ?
                new RecordingSelection().favorite(true).reverseQueryByTimestamp(c.getContentResolver()) :
                new RecordingSelection().reverseQueryByTimestamp(c.getContentResolver()));
        mIsFavorite = isFavorite;
    }

    public RecordingAdapter(FragmentActivity context,RecordingCursor cursor){
        super(context, cursor);
        mContext = context;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final RecordingCursor cursor) {
        final ViewHolder vh = (ViewHolder) holder;
        final long id = cursor.getId();
        vh.mTitle.setText(cursor.getName());
        vh.mLength.setText(String.valueOf(duration(cursor.getDuration() / 1000)));
        vh.mDate.setText(String.valueOf(naturalTime(new Date(cursor.getTimestamp()))));
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerDialogFragment.show(mContext, id);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recording_item, parent, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ((RippleDrawable) view.getBackground()).setHotspot(event.getX(), event.getY());
                    return false;

                }
            });
        }
        return new ViewHolder(view);
    }


    public void refreshCursor() {
        if(mIsFavorite)
            changeCursor(new RecordingSelection().favorite(true).reverseQueryByTimestamp(mContext.getContentResolver()));
        else
            changeCursor(new RecordingSelection().reverseQueryByTimestamp(mContext.getContentResolver()));
    }


    @Override
    public Filter getFilter() {
        return new CursorFilter();
    }

    class CursorFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            //Here you have to implement filtering way
            final FilterResults results = new FilterResults();

            RecordingCursor cursor = new RecordingSelection().nameContains(constraint.toString()).reverseQueryByTimestamp(mContext.getContentResolver());
            //logic to filtering
            results.count = cursor.getCount();
            results.values = cursor;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // here you can use result - (f.e. set in in adapter list)
            RecordingAdapter.this.swapCursor((RecordingCursor) results.values);
        }
    }

}