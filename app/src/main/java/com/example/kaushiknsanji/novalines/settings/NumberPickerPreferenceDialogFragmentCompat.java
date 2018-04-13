package com.example.kaushiknsanji.novalines.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.NumberPicker;

import com.example.kaushiknsanji.novalines.R;

/**
 * {@link PreferenceDialogFragmentCompat} class that displays and manages the NumberPicker dialog
 * shown for the custom {@link NumberPickerPreference} class
 *
 * @author Kaushik N Sanji
 */
public class NumberPickerPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    //Bundle Key constants
    private static final String NUMBER_PICKER_MAX_INT_KEY = "numberPicker.maxValue";
    private static final String NUMBER_PICKER_MIN_INT_KEY = "numberPicker.minValue";
    private static final String NUMBER_PICKER_VALUE_INT_KEY = "numberPicker.value";

    //Stores the reference to the NumberPicker in the Dialog
    private NumberPicker mNumberPicker;

    //Saves the value selected by the User, for restoring the state
    private int mNumberPickerValue;

    /**
     * Static Constructor of the PreferenceDialogFragmentCompat {@link NumberPickerPreferenceDialogFragmentCompat}
     *
     * @param key      is the Preference Key for the {@link NumberPickerPreference}
     * @param minValue is the Minimum Integer value of the NumberPicker used in the Preference
     * @param maxValue is the Maximum Integer value of the NumberPicker used in the Preference
     * @return instance of {@link NumberPickerPreferenceDialogFragmentCompat}
     */
    public static NumberPickerPreferenceDialogFragmentCompat newInstance(String key, int minValue, int maxValue) {
        final NumberPickerPreferenceDialogFragmentCompat dialogFragmentCompat
                = new NumberPickerPreferenceDialogFragmentCompat();

        //Saving the arguments passed, in a Bundle: START
        final Bundle bundle = new Bundle(3);
        bundle.putString(ARG_KEY, key);
        bundle.putInt(NUMBER_PICKER_MIN_INT_KEY, minValue);
        bundle.putInt(NUMBER_PICKER_MAX_INT_KEY, maxValue);
        dialogFragmentCompat.setArguments(bundle);
        //Saving the arguments passed, in a Bundle: END

        //Returning the instance
        return dialogFragmentCompat;
    }

    //Used to restore the Preference's state from the Bundle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            //Restoring the previous value of NumberPicker
            mNumberPickerValue = savedInstanceState.getInt(NUMBER_PICKER_VALUE_INT_KEY);
        }
    }

    //Saves the Preference's state in the bundle
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //Saving the value of NumberPicker
        outState.putInt(NUMBER_PICKER_VALUE_INT_KEY, mNumberPicker.getValue());
        super.onSaveInstanceState(outState);
    }

    /**
     * Binds views in the content View of the dialog to data.
     *
     * @param view The content View of the dialog, if it is custom.
     */
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        //Retrieving the NumberPicker from the view
        mNumberPicker = view.findViewById(R.id.pref_number_picker_id);

        //Throwing an exception when no NumberPicker is found
        if (mNumberPicker == null) {
            throw new IllegalStateException("Dialog view must contain a NumberPicker" +
                    " with the id as 'pref_number_picker_id'");
        }

        //Setting the Min and Max values for the NumberPicker
        Bundle bundleArgs = getArguments();
        mNumberPicker.setMinValue(bundleArgs.getInt(NUMBER_PICKER_MIN_INT_KEY));
        mNumberPicker.setMaxValue(bundleArgs.getInt(NUMBER_PICKER_MAX_INT_KEY));

        //Setting the value of NumberPicker if previously set
        mNumberPicker.setValue(mNumberPickerValue > 0 ? mNumberPickerValue : getNumberPickerPreference().getValue());

        //Wrapping the selector wheel on NumberPicker
        mNumberPicker.setWrapSelectorWheel(true);
    }

    /**
     * Invoked when the user submits a response through the dialog
     *
     * @param positiveResult boolean value indicated as True when the user clicks the positive button
     */
    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            //Saving the selected value on click of positive button

            //Retrieving the value selected to save
            int selectedValue = mNumberPicker.getValue();

            //Retrieving the reference to NumberPickerPreference
            NumberPickerPreference numberPickerPreference = getNumberPickerPreference();
            //Notifying the OnPreferenceChangeListeners and persisting the value if true
            if (numberPickerPreference.callChangeListener(selectedValue)) {
                //Saving the value
                numberPickerPreference.setValue(selectedValue);
            }
        }
    }

    /**
     * Method that returns the reference to {@link NumberPickerPreference}
     *
     * @return Reference to {@link NumberPickerPreference}
     */
    private NumberPickerPreference getNumberPickerPreference() {
        //Retrieving and returning the NumberPickerPreference
        return (NumberPickerPreference) getPreference();
    }
}
