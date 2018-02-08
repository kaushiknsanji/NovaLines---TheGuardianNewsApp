package com.example.kaushiknsanji.novalines.adapters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.models.NewsArticleInfo;
import com.example.kaushiknsanji.novalines.workers.ImageDownloaderFragment;
import com.example.kaushiknsanji.novalines.workers.NewsArticlesDiffLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kaushik N Sanji on 05-Jan-18.
 */
public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder>
        implements LoaderManager.LoaderCallbacks<DiffUtil.DiffResult> {

    //Constant used for Logs
    private final static String LOG_TAG = ArticlesAdapter.class.getSimpleName();

    //Constants used for the Diff Loader's Bundle arguments
    private static final String OLD_LIST_STR_KEY = "NewsArticleInfo.Old";
    private static final String NEW_LIST_STR_KEY = "NewsArticleInfo.New";

    //Stores the layout resource of the list item that needs to be inflated manually
    private int mLayoutRes;

    //Stores the reference to the Context
    private Context mContext;

    //Loader IDs generated for use by the Adapter's Fragment
    private int[] mLoaderIds;

    //Rotate Animators for TextView Expand/Collapse ImageButton anchor
    private Animator mRotateTo180Anim;
    private Animator mRotateTo0Anim;

    //Stores a list of NewsArticleInfo objects which is the Dataset of the Adapter
    private List<NewsArticleInfo> mNewsArticleInfoList;

    //Stores the reference to the Listener OnAdapterItemClickListener
    private OnAdapterItemClickListener mItemClickListener;

    //Stores the reference to the Listener OnAdapterItemDataSwapListener
    private OnAdapterItemDataSwapListener mItemDataSwapListener;

    //Stores the reference to the Listener OnAdapterItemPopupMenuClickListener
    private OnAdapterItemPopupMenuClickListener mItemPopupMenuClickListener;

    /**
     * Constructor of the Adapter {@link ArticlesAdapter}
     *
     * @param context          is the Context of the Fragment
     * @param resource         is the layout resource ID of the item view ('R.layout.news_article_item')
     * @param newsArticleInfos is the list of {@link NewsArticleInfo} objects which is the Dataset of the Adapter
     * @param loaderIds        is the Integer Array of unique Loader IDs generated by the Adapter's Fragment
     */
    public ArticlesAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<NewsArticleInfo> newsArticleInfos, int[] loaderIds) {
        mContext = context;
        mLayoutRes = resource;
        mNewsArticleInfoList = newsArticleInfos;
        mLoaderIds = loaderIds;

        //Loading the Rotation Animators for TextView Expand/Collapse ImageButton anchors
        mRotateTo0Anim = AnimatorInflater.loadAnimator(mContext, R.animator.rotate_180_0);
        mRotateTo180Anim = AnimatorInflater.loadAnimator(mContext, R.animator.rotate_0_180);
    }

    /**
     * Method that registers the {@link OnAdapterItemDataSwapListener}
     * for the {@link com.example.kaushiknsanji.novalines.adapterviews.ArticlesFragment}
     * to receive event callbacks
     *
     * @param listener is the instance of the Activity/Fragment implementing the {@link OnAdapterItemDataSwapListener}
     */
    public void setOnAdapterItemDataSwapListener(OnAdapterItemDataSwapListener listener) {
        mItemDataSwapListener = listener;
    }

    /**
     * Method that registers the {@link OnAdapterItemClickListener}
     * for the {@link com.example.kaushiknsanji.novalines.adapterviews.ArticlesFragment}
     * to receive item click events
     *
     * @param listener is the instance of the Activity/Fragment implementing the {@link OnAdapterItemClickListener}
     */
    public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
        mItemClickListener = listener;
    }

    /**
     * Method that registers the {@link OnAdapterItemPopupMenuClickListener}
     * for the {@link com.example.kaushiknsanji.novalines.adapterviews.ArticlesFragment}
     * to receive Popup Menu Item click events
     *
     * @param listener is the instance of the Activity/Fragment implementing the {@link OnAdapterItemPopupMenuClickListener}
     */
    public void setOnAdapterItemPopupMenuClickListener(OnAdapterItemPopupMenuClickListener listener) {
        mItemPopupMenuClickListener = listener;
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflating the item Layout view
        //Passing False as we are attaching the View ourselves
        View itemView = LayoutInflater.from(mContext).inflate(mLayoutRes, parent, false);

        //Instantiating the ViewHolder to initialize the reference to the view components in the item layout
        //and returning the same
        return new ViewHolder(itemView);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Retrieving the NewsArticleInfo object at the current item position
        NewsArticleInfo newsArticleInfo = mNewsArticleInfoList.get(position);

        //Populating the data onto the Template View using the NewsArticleInfo object: START

        //Updating the News Thumbnail Image if link is present
        String imageURLStr = newsArticleInfo.getThumbImageUrl();
        if (!TextUtils.isEmpty(imageURLStr)) {
            //Loading the Image when the link is present
            holder.articleThumbImageView.setVisibility(View.VISIBLE); //Ensuring the ImageView is Visible
            ImageDownloaderFragment
                    .newInstance(((FragmentActivity) mContext).getSupportFragmentManager(), position)
                    .executeAndUpdate(holder.articleThumbImageView, imageURLStr, position);
        } else {
            //Hiding the ImageView when the Thumbnail is not available
            holder.articleThumbImageView.setVisibility(View.GONE);
        }

        //Updating the Section of the News Article
        holder.articleSectionTextView.setText(newsArticleInfo.getSectionName());

        //Updating the Title of the News Article
        holder.articleTitleTextView.setText(newsArticleInfo.getNewsTitle());

        //Updating the Trailing Text of the News Article
        String trailTextStr = newsArticleInfo.getTrailText();
        if (TextUtils.isEmpty(trailTextStr)) {
            //Updating with the Empty string when not available
            holder.articleTrailTextView.setText(trailTextStr);
        } else {
            //Updating with the embedded Html formatting when the content is present
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.articleTrailTextView.setText(Html.fromHtml(trailTextStr, Html.FROM_HTML_MODE_LEGACY));
            } else {
                holder.articleTrailTextView.setText(Html.fromHtml(trailTextStr));
            }
        }

        //Hiding the TextView of Trailing Text
        holder.articleTrailTextView.setVisibility(View.GONE);

        //Updating the Author of the News Article
        holder.articlePublisherTextView.setText(newsArticleInfo.getAuthor(mContext.getString(R.string.no_authors_found_default_text)));

        //Updating the Published Date of the News Article
        holder.articleDateTextView.setText(newsArticleInfo.getPublishedDate(mContext.getString(R.string.no_published_date_default_text)));

        //Retrieving the Ellipse count for the Text in Author TextView
        int publisherTextEllipseCount = getEllipseCountFromTextView(holder.articlePublisherTextView, holder.articlePublisherTextView.getLineCount());

        if (publisherTextEllipseCount > 0 || !TextUtils.isEmpty(trailTextStr)) {
            //Making the Content Expand Button visible when the Author Text is ellipsized
            //or the Title Trail Text is present
            holder.contentExpandButton.setVisibility(View.VISIBLE);

            //Looking for the Tag object to see if the image was rotated
            Object contentExpandButtonTagObj = holder.contentExpandButton.getTag();
            if (contentExpandButtonTagObj != null && (Boolean) contentExpandButtonTagObj) {
                //Rotating the Image Anchor from 180 to 0 when the image was previously rotated
                Animator rotateTo0AnimImmediate = mRotateTo0Anim.clone();
                rotateTo0AnimImmediate.setDuration(0);
                rotateTo0AnimImmediate.setTarget(holder.contentExpandButton);
                rotateTo0AnimImmediate.start();
                holder.contentExpandButton.setTag(Boolean.FALSE); //Resetting the Tag to FALSE
            }

        } else {
            //Hiding the Content Expand Button when the Author Text is NOT ellipsized
            //and the Title Trail Text is NOT present
            holder.contentExpandButton.setVisibility(View.GONE);
        }

        //Populating the data onto the Template View using the NewsArticleInfo object: END
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter which is the
     * total number of {@link NewsArticleInfo} objects in the adapter
     */
    @Override
    public int getItemCount() {
        return mNewsArticleInfoList.size();
    }

    /**
     * Method that searches for the Ellipsis in the Text of a TextView and returns its total count
     *
     * @param textView       is the TextView containing a Text which needs to be scanned for Ellipsis
     * @param totalLineCount is the Total Line count of the Text in Integer
     * @return Integer containing the total number of Ellipsis found in the Text of a TextView
     */
    private int getEllipseCountFromTextView(TextView textView, int totalLineCount) {
        int totalEllipseCount = 0; //Defaulting the Total Ellipse count to 0, initially

        //Retrieving the layout attached to TextView
        Layout textViewLayout = textView.getLayout();

        //Iterating over the Lines of the Text and counting the Ellipsis found
        for (int index = 0; index < totalLineCount; index++) {
            if (textViewLayout.getEllipsisCount(index) > 0) {
                totalEllipseCount++;
            }
        }

        //Returning the total Ellipsis found
        return totalEllipseCount;
    }

    /**
     * Method that computes the difference between the current and the new list of
     * {@link NewsArticleInfo} objects and sends the result to the adapter to notify the changes
     * on the item data and reload accordingly
     *
     * @param newArticleInfos is the new list of {@link NewsArticleInfo} objects which is the Dataset of the Adapter
     */
    public void swapItemData(@NonNull List<NewsArticleInfo> newArticleInfos) {
        //Loading the List of NewsArticleInfo objects as Bundle arguments to be passed to a Loader
        final Bundle args = new Bundle(2);
        args.putParcelableArrayList(OLD_LIST_STR_KEY, (ArrayList<? extends Parcelable>) mNewsArticleInfoList);
        args.putParcelableArrayList(NEW_LIST_STR_KEY, (ArrayList<? extends Parcelable>) newArticleInfos);
        //Initiating a loader to execute the difference computation in a background thread
        ((FragmentActivity) mContext).getSupportLoaderManager().restartLoader(mLoaderIds[1], args, this);
    }

    /**
     * Internal Method called by the Loader {@link NewsArticlesDiffLoader}
     * after the difference computation between the current and the new list of
     * {@link NewsArticleInfo} objects to notify the adapter of the changes required
     * with respect to the data
     *
     * @param diffResult         the result obtained after the difference computation between
     *                           two lists of {@link NewsArticleInfo} objects
     * @param newArticleInfoList is the new list of {@link NewsArticleInfo} objects which is the Dataset of the Adapter
     */
    private void doSwapItemData(DiffUtil.DiffResult diffResult, List<NewsArticleInfo> newArticleInfoList) {
        Log.d(LOG_TAG, "doSwapItemData: Started");
        //Clearing the Adapter's data to load the new list of NewsArticleInfo objects
        mNewsArticleInfoList.clear();
        mNewsArticleInfoList.addAll(newArticleInfoList);

        //Informing the adapter about the changes required, so that it triggers the notify accordingly
        diffResult.dispatchUpdatesTo(this);

        //Dispatching the Item Data Swap event to the listener
        if (mItemDataSwapListener != null) {
            mItemDataSwapListener.onItemDataSwapped();
        }
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<DiffUtil.DiffResult> onCreateLoader(int id, Bundle args) {
        if (id == mLoaderIds[1]) {
            //Preparing the Diff Loader and returning the instance
            List<NewsArticleInfo> oldArticleInfoList = args.getParcelableArrayList(OLD_LIST_STR_KEY);
            List<NewsArticleInfo> newArticleInfoList = args.getParcelableArrayList(NEW_LIST_STR_KEY);
            return new NewsArticlesDiffLoader(mContext, oldArticleInfoList, newArticleInfoList);
        }

        return null;
    }

    /**
     * Called when a previously created loader has finished its load.
     * This is where we notify the Adapter with the result of the difference computation.
     *
     * @param loader     The Loader that has finished.
     * @param diffResult The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<DiffUtil.DiffResult> loader, DiffUtil.DiffResult diffResult) {
        if (diffResult != null) {
            //When there is a result of the difference computation
            if (loader.getId() == mLoaderIds[1]) {
                //Update the New Data to the Adapter and notify the changes in the data
                doSwapItemData(diffResult, ((NewsArticlesDiffLoader) loader).getNewArticleInfoList());
            }
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<DiffUtil.DiffResult> loader) {
        //No-op, just invalidating the loader
        loader = null;
    }

    /**
     * Method that expands/collapses the content of Title's Trailing Text and the Publisher Text of the Article
     * on click of the expand/collapse button 'R.id.news_item_expand_btn_id'
     *
     * @param anchorButton             is the ImageButton on the article card that expands/collapses the content
     * @param articleTrailTextView     is the TextView for the Title's Trailing Text
     * @param articlePublisherTextView is the TextView for the Publisher Text
     */
    private void toggleContentExpansion(ImageButton anchorButton, TextView articleTrailTextView, TextView articlePublisherTextView) {
        //Boolean to store whether the Trailing Text is expanded or not. Defaulted to FALSE (Collapsed)
        boolean trailTextExpandState = false;
        if (!TextUtils.isEmpty(articleTrailTextView.getText())) {
            //Updating the Trailing Text state only when there is Trailing Text Content
            trailTextExpandState = (articleTrailTextView.getVisibility() == View.VISIBLE);
        }

        //Retrieving the Max Lines count setting of the Publisher TextView in Collapsed state
        int publisherTextCollapsedStateLineCountSetting = mContext.getResources().getInteger(R.integer.article_publisher_text_max_lines_collapsed);
        //Evaluating whether the Publisher Text is expanded (Line count is greater than the collapsed setting)
        boolean publisherTextExpandState = (articlePublisherTextView.getLineCount() > publisherTextCollapsedStateLineCountSetting);

        if (trailTextExpandState || publisherTextExpandState) {
            //Collapse/Hide the content as they are in expanded state

            //Hiding the content of Trailing Text
            articleTrailTextView.setVisibility(View.GONE);

            //Resetting the Max Lines on Publisher Text
            articlePublisherTextView.setMaxLines(publisherTextCollapsedStateLineCountSetting);

            //Rotating the Image Anchor from 180 to 0
            mRotateTo0Anim.setTarget(anchorButton);
            mRotateTo0Anim.start();
            anchorButton.setTag(Boolean.FALSE); //Resetting the Tag to FALSE

        } else {
            //Expand the content as they are in collapsed state

            //Revealing the content of Trailing Text if any
            if (!TextUtils.isEmpty(articleTrailTextView.getText())) {
                articleTrailTextView.setVisibility(View.VISIBLE);
            }

            //Setting the Max Lines on Publisher Text to a higher number for expanding content
            articlePublisherTextView.setMaxLines(mContext.getResources().getInteger(R.integer.article_publisher_text_max_lines_expanded));

            //Rotating the Image Anchor from 0 to 180
            mRotateTo180Anim.setTarget(anchorButton);
            mRotateTo180Anim.start();
            //Setting an Expanded State (TRUE) Identifier on the ImageButton's Tag
            anchorButton.setTag(Boolean.TRUE);
        }
    }

    /**
     * Interface that declares methods to be implemented by
     * the Fragment {@link com.example.kaushiknsanji.novalines.adapterviews.ArticlesFragment}
     * to receive event callbacks related to RecyclerView's Adapter data change
     */
    public interface OnAdapterItemDataSwapListener {
        /**
         * Method invoked when the data on the RecyclerView's Adapter has been swapped successfully
         */
        void onItemDataSwapped();
    }

    /**
     * Interface that declares methods to be implemented by
     * the Fragment {@link com.example.kaushiknsanji.novalines.adapterviews.ArticlesFragment}
     * to receive event callbacks related to the click action
     * on the item views displayed by the RecyclerView's Adapter
     */
    public interface OnAdapterItemClickListener {
        /**
         * Method invoked when an Item in the Adapter is clicked
         *
         * @param newsArticleInfo is the corresponding {@link NewsArticleInfo} object of the item view
         *                        clicked in the Adapter
         */
        void onItemClick(NewsArticleInfo newsArticleInfo);
    }

    /**
     * Interface that declares methods to be implemented by
     * the Fragment {@link com.example.kaushiknsanji.novalines.adapterviews.ArticlesFragment}
     * to receive event callbacks related to the click action
     * on the Popup Menu options of the item views displayed by the RecyclerView's Adapter
     */
    public interface OnAdapterItemPopupMenuClickListener {
        /**
         * Method invoked when "Read Later" option is clicked from the Popup Menu
         *
         * @param newsArticleInfo is the corresponding {@link NewsArticleInfo} object of the item view
         *                        in the Adapter
         */
        void onMarkForRead(NewsArticleInfo newsArticleInfo);

        /**
         * Method invoked when "Favorite this" option is clicked from the Popup Menu
         *
         * @param newsArticleInfo is the corresponding {@link NewsArticleInfo} object of the item view
         *                        in the Adapter
         */
        void onMarkAsFav(NewsArticleInfo newsArticleInfo);

        /**
         * Method invoked when "Open News Section" option is clicked from the Popup Menu
         *
         * @param newsArticleInfo is the corresponding {@link NewsArticleInfo} object of the item view
         *                        in the Adapter
         */
        void onOpenNewsSectionRequest(NewsArticleInfo newsArticleInfo);
    }

    /**
     * ViewHolder class for caching View components of the template item view
     */
    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        //Declaring the View components of the template item view
        private ImageView articleThumbImageView;
        private TextView articleSectionTextView;
        private TextView articleTitleTextView;
        private TextView articlePublisherTextView;
        private TextView articleDateTextView;
        private TextView articleTrailTextView;
        private ImageButton contentExpandButton;
        private ImageButton popupMenuButton;
        private PopupMenu popupMenu;

        /**
         * Constructor of the ViewHolder
         *
         * @param itemView is the inflated item layout View passed
         *                 for caching its View components
         */
        public ViewHolder(View itemView) {
            super(itemView);

            //Doing the view lookup for each of the item layout view's components
            articleThumbImageView = itemView.findViewById(R.id.article_thumb_id);
            articleSectionTextView = itemView.findViewById(R.id.article_section_text_id);
            articleTitleTextView = itemView.findViewById(R.id.article_title_text_id);
            articlePublisherTextView = itemView.findViewById(R.id.article_publisher_text_id);
            articleDateTextView = itemView.findViewById(R.id.article_published_date_text_id);
            articleTrailTextView = itemView.findViewById(R.id.article_trail_text_id);
            contentExpandButton = itemView.findViewById(R.id.news_item_expand_btn_id);
            popupMenuButton = itemView.findViewById(R.id.news_item_popup_btn_id);

            //Inflating the Popup Menu for the card
            popupMenu = new PopupMenu(mContext, popupMenuButton);
            MenuInflater menuInflater = popupMenu.getMenuInflater();
            menuInflater.inflate(R.menu.article_card_item_menu, popupMenu.getMenu());

            //Setting the Click Listener on the entire Item View
            itemView.setOnClickListener(this);
            //Setting the Click Listener on the Content Expand Button
            contentExpandButton.setOnClickListener(this);
            //Setting the Click Listener on the Popup Menu Button
            popupMenuButton.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {
            //Retrieving the position of the item view clicked
            int adapterPosition = getAdapterPosition();
            if (adapterPosition > RecyclerView.NO_POSITION) {
                //Verifying the validity of the position before proceeding

                //Retrieving the data at the position
                NewsArticleInfo newsArticleInfo = mNewsArticleInfoList.get(adapterPosition);

                //Executing action based on the view being clicked
                switch (view.getId()) {
                    case R.id.news_item_expand_btn_id:
                        //For the Content Expand Button
                        //Expanding/Collapsing the content of Title's Trailing Text and the Publisher Text of the Article
                        toggleContentExpansion(contentExpandButton, articleTrailTextView, articlePublisherTextView);
                        break;
                    case R.id.news_item_popup_btn_id:
                        //For the Popup Menu Button
                        popupMenu.show(); //Displaying the Popup Menu
                        break;
                    default:
                        //For the entire Item View
                        Log.d(LOG_TAG, "onClick: mItemClickListener " + mItemClickListener);
                        //Propagating the call to the listener with the selected item's data
                        if (mItemClickListener != null) {
                            mItemClickListener.onItemClick(newsArticleInfo);
                        }
                        break;
                }

            }
        }
    }

}