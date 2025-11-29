package app.revanced.extension.youtube.settings.preference;

import static app.revanced.extension.shared.utils.StringRef.str;
import static app.revanced.extension.shared.utils.Utils.dipToPixels;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.Preference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.Arrays;

import app.revanced.extension.shared.settings.StringSetting;
import app.revanced.extension.shared.utils.ResourceUtils;
import app.revanced.extension.shared.utils.Utils;
import app.revanced.extension.youtube.settings.Settings;
import app.revanced.extension.youtube.utils.ExtendedUtils;

@SuppressWarnings({"unused", "deprecation"})
public class ExternalDownloaderVideoPreference extends Preference implements Preference.OnPreferenceClickListener {

    private static final StringSetting settings = Settings.EXTERNAL_DOWNLOADER_PACKAGE_NAME_VIDEO;
    private static final String[] mEntries = ResourceUtils.getStringArray("revanced_external_downloader_video_label");
    private static final String[] mEntryValues = ResourceUtils.getStringArray("revanced_external_downloader_video_package_name");
    private static final String[] mWebsiteEntries = ResourceUtils.getStringArray("revanced_external_downloader_video_website");

    @SuppressLint("StaticFieldLeak")
    private static EditText mEditText;
    private static String packageName;
    private static int mClickedDialogEntryIndex;

    private final TextWatcher textWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            packageName = s.toString();
            mClickedDialogEntryIndex = Arrays.asList(mEntryValues).indexOf(packageName);
        }
    };

    private void init() {
        setSelectable(true);
        setOnPreferenceClickListener(this);
    }

    public ExternalDownloaderVideoPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public ExternalDownloaderVideoPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ExternalDownloaderVideoPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExternalDownloaderVideoPreference(Context context) {
        super(context);
        init();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        packageName = settings.get();
        mClickedDialogEntryIndex = Arrays.asList(mEntryValues).indexOf(packageName);

        final Context context = getContext();

        // Create the main layout for the dialog content.
        LinearLayout contentLayout = new LinearLayout(context);
        contentLayout.setOrientation(LinearLayout.VERTICAL);

        // Add behavior selection radio buttons.
        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        for (int i = 0; i < mEntries.length; i++) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText(mEntries[i]);
            radioButton.setId(i);
            radioButton.setChecked(i == mClickedDialogEntryIndex);
            radioGroup.addView(radioButton);
        }
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            mClickedDialogEntryIndex = checkedId;
            mEditText.setText(mEntryValues[checkedId]);
        });
        radioGroup.setPadding(dipToPixels(10), 0, 0, 0);
        contentLayout.addView(radioGroup);

        TableLayout table = new TableLayout(context);
        table.setOrientation(LinearLayout.HORIZONTAL);
        table.setPadding(15, 0, 15, 0);

        TableRow row = new TableRow(context);

        mEditText = new EditText(context);
        mEditText.setHint(settings.defaultValue);
        mEditText.setText(packageName);
        mEditText.addTextChangedListener(textWatcher);
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_PT, 9);
        mEditText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        row.addView(mEditText);

        table.addView(row);
        contentLayout.addView(table);

        // Create ScrollView to wrap the content layout.
        ScrollView contentScrollView = new ScrollView(context);
        contentScrollView.setVerticalScrollBarEnabled(false); // Disable vertical scrollbar.
        contentScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER); // Disable overscroll effect.
        LinearLayout.LayoutParams scrollViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1.0f
        );
        contentScrollView.setLayoutParams(scrollViewParams);
        contentScrollView.addView(contentLayout);

        // Create the custom dialog.
        Pair<Dialog, LinearLayout> dialogPair = Utils.createCustomDialog(
                context,
                str("revanced_external_downloader_dialog_title"), // Title.
                null, // No message (replaced by contentLayout).
                null, // No EditText.
                null, // OK button text.
                () -> {
                    // OK button action.
                    final String packageName = mEditText.getText().toString().trim();
                    settings.save(packageName);
                    checkPackageIsValid(context, packageName);
                },
                () -> {}, // Cancel button action (dismiss only).
                str("revanced_extended_settings_reset"), // Neutral button text.
                settings::resetToDefault,
                true  // Dismiss dialog when onNeutralClick.
        );

        // Add the ScrollView to the dialog's main layout.
        LinearLayout dialogMainLayout = dialogPair.second;
        dialogMainLayout.addView(contentScrollView, dialogMainLayout.getChildCount() - 1);
        // Show the dialog.
        dialogPair.first.show();

        return true;
    }

    private static boolean checkPackageIsValid(Context context, String packageName) {
        String appName = "";
        String website = "";

        if (mClickedDialogEntryIndex >= 0) {
            appName = mEntries[mClickedDialogEntryIndex];
            website = mWebsiteEntries[mClickedDialogEntryIndex];
        }

        return showToastOrOpenWebsites(context, appName, packageName, website);
    }

    private static boolean showToastOrOpenWebsites(Context context, String appName, String packageName, String website) {
        if (ExtendedUtils.isPackageEnabled(packageName))
            return true;

        if (website.isEmpty()) {
            Utils.showToastShort(str("revanced_external_downloader_not_installed_warning", packageName));
            return false;
        }

        Pair<Dialog, LinearLayout> dialogPair = Utils.createCustomDialog(
                context,
                // Title.
                str("revanced_external_downloader_not_installed_dialog_title"),
                // Message.
                str("revanced_external_downloader_not_installed_dialog_message", appName, appName),
                // No EditText.
                null,
                // OK button text.
                null,
                // OK button action.
                () -> {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                    context.startActivity(i);
                },
                // Cancel button action (dismiss only).
                () -> {},
                // Neutral button text.
                null,
                // Neutral button action.
                null,
                // Dismiss dialog when onNeutralClick.
                false
        );

        dialogPair.first.show();

        return false;
    }

    public static boolean checkPackageIsDisabled() {
        final Context context = Utils.getActivity();
        packageName = settings.get();
        mClickedDialogEntryIndex = Arrays.asList(mEntryValues).indexOf(packageName);
        return !checkPackageIsValid(context, packageName);
    }

    public static String getExternalDownloaderPackageName() {
        String downloaderPackageName = settings.get().trim();

        if (downloaderPackageName.isEmpty()) {
            settings.resetToDefault();
            downloaderPackageName = settings.defaultValue;
        }

        return downloaderPackageName;
    }
}