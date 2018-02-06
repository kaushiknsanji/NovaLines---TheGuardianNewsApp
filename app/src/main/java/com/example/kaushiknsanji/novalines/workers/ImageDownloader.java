package com.example.kaushiknsanji.novalines.workers;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.AsyncTaskLoader;

import com.example.kaushiknsanji.novalines.utils.ImageUtility;
import com.example.kaushiknsanji.novalines.utils.NetworkUtility;

/**
 * {@link AsyncTaskLoader} for downloading the Bitmap Image from a given Image URL,
 * in a worker thread
 *
 * @author Kaushik N Sanji
 */
public class ImageDownloader extends AsyncTaskLoader<Bitmap> {

    //Integer Constant of the Loader
    public final static int IMAGE_LOADER = 5;

    //Stores the Image URL String from which the Image needs to be downloaded
    private String mImageURLStr;

    //Stores the Bitmap Downloaded
    private Bitmap mDownloadedBitmap;

    /**
     * Constructor of the Loader
     *
     * @param context     used to retrieve the application context.
     * @param imageURLStr String containing the Image URL from
     *                    which the image needs to be downloaded
     */
    public ImageDownloader(Context context, String imageURLStr) {
        super(context);
        mImageURLStr = imageURLStr;
    }

    /**
     * Called on a worker thread to perform the actual load and to return
     * the result of the load operation.
     *
     * @return The result of the load operation which is a Bitmap downloaded from the URL
     * @throws android.support.v4.os.OperationCanceledException if the load is canceled during execution.
     */
    @Override
    public Bitmap loadInBackground() {
        //Proceeding to download when the Internet Connectivity is established
        if (NetworkUtility.isNetworkConnected(getContext())) {
            //Downloading the Image from URL and returning the Bitmap
            Bitmap downloadedBitmap = ImageUtility.downloadFromURL(mImageURLStr);
            if (downloadedBitmap != null) {
                //Uploading the Bitmap to GPU for caching in background thread (for faster loads)
                downloadedBitmap.prepareToDraw();
            }
            return downloadedBitmap; //Returning the Bitmap downloaded
        }

        //For all else, returning null
        return null;
    }

    /**
     * Sends the result of the load to the registered listener. Should only be called by subclasses.
     *
     * @param newBitmap the result of the load which is a new Bitmap downloaded
     */
    @Override
    public void deliverResult(Bitmap newBitmap) {
        if (isReset()) {
            //Ignoring the result if the loader is already reset
            newBitmap = null;
            //Returning when the loader is already reset
            return;
        }

        //Storing a reference to the old Bitmap as we are about to deliver the result
        Bitmap oldBitmap = mDownloadedBitmap;
        mDownloadedBitmap = newBitmap;

        if (isStarted()) {
            //Delivering the result when the loader is started
            super.deliverResult(newBitmap);
        }

        //invalidating the old bitmap as it is not required anymore
        if (oldBitmap != null && oldBitmap != newBitmap) {
            oldBitmap = null;
        }

    }

    /**
     * Subclasses must implement this to take care of loading their data,
     * as per {@link #startLoading()}.  This is not called by clients directly,
     * but as a result of a call to {@link #startLoading()}.
     */
    @Override
    protected void onStartLoading() {
        if (mDownloadedBitmap != null) {
            //Deliver the result immediately if the Bitmap is already downloaded
            deliverResult(mDownloadedBitmap);
        }

        if (takeContentChanged() || mDownloadedBitmap == null) {
            //Force a new Load when the Bitmap Image is not yet downloaded
            //or the content has changed
            forceLoad();
        }

    }

    /**
     * Subclasses must implement this to take care of stopping their loader,
     * as per {@link #stopLoading()}.  This is not called by clients directly,
     * but as a result of a call to {@link #stopLoading()}.
     * This will always be called from the process's main thread.
     */
    @Override
    protected void onStopLoading() {
        //Canceling the load if any as the loader has entered Stopped state
        cancelLoad();
    }

    /**
     * Subclasses must implement this to take care of resetting their loader,
     * as per {@link #reset()}.  This is not called by clients directly,
     * but as a result of a call to {@link #reset()}.
     * This will always be called from the process's main thread.
     */
    @Override
    protected void onReset() {
        //Ensuring the loader has stopped
        onStopLoading();

        //Releasing the resources associated with the Loader
        releaseResources();
    }

    /**
     * Called if the task was canceled before it was completed.  Gives the class a chance
     * to clean up post-cancellation and to properly dispose of the result.
     *
     * @param data The value that was returned by {@link #loadInBackground}, or null
     *             if the task threw {@link android.support.v4.os.OperationCanceledException}.
     */
    @Override
    public void onCanceled(Bitmap data) {
        //Canceling any asynchronous load
        super.onCanceled(data);

        //Releasing the resources associated with the Loader, as the Loader is canceled
        releaseResources();
    }

    /**
     * Method to release the resources associated with the loader
     */
    private void releaseResources() {
        //Invalidating the Loader data
        if (mDownloadedBitmap != null) {
            mDownloadedBitmap = null;
        }
    }

    /**
     * Method that returns the Image URL String from which this loader downloads the image
     *
     * @return The Image URL String from which this loader downloads the image
     */
    public String getImageURLStr() {
        return mImageURLStr;
    }

}
