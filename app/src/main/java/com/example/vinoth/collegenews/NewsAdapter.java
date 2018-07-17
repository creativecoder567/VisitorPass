package com.example.vinoth.collegenews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vinoth.collegenews.model.News;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by vinoth on 10/11/17.
 */
public class NewsAdapter extends
        RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private static final String TAG = NewsAdapter.class.getSimpleName();

    private Context context;
    private List<News> list;
    private OnItemClickListener onItemClickListener;

    public NewsAdapter(Context context, List<News> list,
                       OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.new_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        News news = list.get(position);
        holder.bind(news);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView news_img;
        private final TextView titleTv;
        private final TextView descTv;
        private final TextView create_time;

        public ViewHolder(View itemView) {
            super(itemView);
            news_img = (ImageView) itemView.findViewById(R.id.news_img);
            titleTv = (TextView) itemView.findViewById(R.id.titleTv);
            descTv = (TextView) itemView.findViewById(R.id.descTv);
            create_time = (TextView) itemView.findViewById(R.id.create_time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getLayoutPosition());

                }
            });

        }

        public void bind(final News news) {


            if (news != null) {

                String bannerUrl = news.getBannerUrl();
                String body = news.getBody();
                String title = news.getTitle();
                String createAt = news.getCreateAt();

                if (!TextUtils.isEmpty(body)) {
                    descTv.setText(body);
                }
                if (!TextUtils.isEmpty(bannerUrl)) {
                    Picasso.with(context)
                            .load(bannerUrl)
                            .resize(150, 150)
                            .centerCrop()
                            .into(news_img);
                }
                if (!TextUtils.isEmpty(title)) {
                    titleTv.setText(title);
                }
                if (!TextUtils.isEmpty(createAt)) {
                    final SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                    try {
                        String updated_at = "Created: " + DateUtils.getRelativeDateTimeString(context, serverFormat.parse(createAt).getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
                        create_time.setText(updated_at);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }


            }
        }
    }

}