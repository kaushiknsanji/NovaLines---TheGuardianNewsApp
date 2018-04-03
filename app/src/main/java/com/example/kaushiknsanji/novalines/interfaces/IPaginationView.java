package com.example.kaushiknsanji.novalines.interfaces;

import android.view.View;
import android.widget.ImageButton;

/**
 * A View Interface for the Pagination panel 'R.id.pagination_panel_id'
 * hosted by fragments that have paginated results
 *
 * @author Kaushik N Sanji
 */
public interface IPaginationView extends IGenericView {
    /**
     * Method that returns the {@link View}
     * of the Pagination Panel
     *
     * @return {@link View} of the Pagination Panel
     */
    View getPaginationPanel();

    /**
     * Method that returns the {@link ImageButton}
     * which is a Pagination Button that takes the user
     * to the very First Page
     *
     * @return The Pagination {@link ImageButton} for the First Page
     */
    ImageButton getPageFirstButton();

    /**
     * Method that returns the {@link ImageButton}
     * which is a Pagination Button that takes the user
     * to the Last page
     *
     * @return The Pagination {@link ImageButton} for the Last Page
     */
    ImageButton getPageLastButton();

    /**
     * Method that returns the {@link ImageButton}
     * which is a Pagination Button that takes the user
     * to the Next page
     *
     * @return The Pagination {@link ImageButton} for the Next Page
     */
    ImageButton getPageNextButton();

    /**
     * Method that returns the {@link ImageButton}
     * which is a Pagination Button that takes the user
     * to the Previous Page
     *
     * @return The Pagination {@link ImageButton} for the Previous Page
     */
    ImageButton getPagePreviousButton();

    /**
     * Method that returns the {@link ImageButton}
     * which is a Pagination Button that takes the user
     * to the Page selected by the user in the corresponding dialog
     *
     * @return The Pagination {@link ImageButton} for the User Selected Page
     */
    ImageButton getPageMoreButton();

}
