package app.revanced.extension.youtube.settings.preference;

import static app.revanced.extension.shared.patches.PatchStatus.PatchVersion;
import static app.revanced.extension.shared.settings.preference.AbstractPreferenceFragment.showRestartDialog;
import static app.revanced.extension.shared.settings.preference.AbstractPreferenceFragment.updateListPreferenceSummary;
import static app.revanced.extension.shared.utils.ResourceUtils.getDrawableIdentifier;
import static app.revanced.extension.shared.utils.ResourceUtils.getLayoutIdentifier;
import static app.revanced.extension.shared.utils.ResourceUtils.getXmlIdentifier;
import static app.revanced.extension.shared.utils.StringRef.str;
import static app.revanced.extension.shared.utils.Utils.getChildView;
import static app.revanced.extension.shared.utils.Utils.isSDKAbove;
import static app.revanced.extension.shared.utils.Utils.showToastShort;
import static app.revanced.extension.youtube.settings.ReVancedSettingsHostActivity.setToolbarLayoutParams;
import static app.revanced.extension.youtube.settings.Settings.HIDE_PREVIEW_COMMENT;
import static app.revanced.extension.youtube.settings.Settings.HIDE_PREVIEW_COMMENT_TYPE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Insets;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.TypedValue;
import android.util.Pair;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.revanced.extension.shared.settings.BaseSettings;
import app.revanced.extension.shared.settings.BooleanSetting;
import app.revanced.extension.shared.settings.EnumSetting;
import app.revanced.extension.shared.settings.Setting;
import app.revanced.extension.shared.settings.preference.LogBufferManager;
import app.revanced.extension.shared.settings.preference.NoTitlePreferenceCategory;
import app.revanced.extension.shared.utils.Logger;
import app.revanced.extension.shared.utils.StringRef;
import app.revanced.extension.shared.utils.Utils;
import app.revanced.extension.youtube.sponsorblock.ui.SponsorBlockPreferenceGroup;
import app.revanced.extension.youtube.utils.ExtendedUtils;
import app.revanced.extension.youtube.utils.ThemeUtils;

@SuppressWarnings("deprecation")
public class ReVancedPreferenceFragment extends PreferenceFragment {
    private static final int READ_REQUEST_CODE = 42;
    private static final int WRITE_REQUEST_CODE = 43;
    boolean settingExportInProgress = false;
    static boolean settingImportInProgress = false;
    static boolean showingUserDialogMessage;
    static PreferenceManager mPreferenceManager;
    private SharedPreferences mSharedPreferences;

    /**
     * The main PreferenceScreen used to display the current set of preferences.
     * This screen is manipulated during initialization and filtering to show or hide preferences.
     */
    private PreferenceScreen mPreferenceScreen;

    /**
     * A copy of the original PreferenceScreen created during initialization.
     * Used to restore the preference structure to its initial state after filtering or other modifications.
     */
    private PreferenceScreen originalPreferenceScreen;

    /**
     * Used for searching preferences. A Collection of all preferences including nested preferences.
     * Root preferences are excluded (no need to search what's on the root screen),
     * but their sub preferences are included.
     */
    private final List<AbstractPreferenceSearchData<?>> allPreferences = new ArrayList<>();

    @SuppressLint("SuspiciousIndentation")
    private final SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences, str) -> {
        try {
            if (str == null) {
                return;
            }

            Setting<?> setting = Setting.getSettingFromPath(str);
            if (setting == null) {
                return;
            }

            Preference mPreference = findPreference(str);
            if (mPreference == null) {
                return;
            }

            if (mPreference instanceof SwitchPreference switchPreference) {
                BooleanSetting boolSetting = (BooleanSetting) setting;
                if (settingImportInProgress) {
                    switchPreference.setChecked(boolSetting.get());
                } else {
                    BooleanSetting.privateSetValue(boolSetting, switchPreference.isChecked());
                }

                if (ExtendedUtils.anyMatchSetting(setting)) {
                    ExtendedUtils.setPlayerFlyoutMenuAdditionalSettings();
                } else if (setting.equals(HIDE_PREVIEW_COMMENT) || setting.equals(HIDE_PREVIEW_COMMENT_TYPE)) {
                    ExtendedUtils.setCommentPreviewSettings();
                }
            } else if (mPreference instanceof EditTextPreference editTextPreference) {
                if (settingImportInProgress) {
                    editTextPreference.setText(setting.get().toString());
                } else {
                    Setting.privateSetValueFromString(setting, editTextPreference.getText());
                }
            } else if (mPreference instanceof ListPreference listPreference) {
                if (settingImportInProgress) {
                    listPreference.setValue(setting.get().toString());
                } else {
                    Setting.privateSetValueFromString(setting, listPreference.getValue());
                }
                updateListPreferenceSummary(listPreference, setting);
            } else {
                Logger.printException(() -> "Setting cannot be handled: " + mPreference.getClass() + " " + mPreference);
                return;
            }

            ReVancedSettingsPreference.initializeReVancedSettings();

            if (!settingImportInProgress && !showingUserDialogMessage) {
                final Context context = getActivity();

                if (setting.userDialogMessage != null && !prefIsSetToDefault(mPreference, setting)) {
                    showSettingUserDialogConfirmation(context, mPreference, setting);
                } else if (setting.rebootApp) {
                    showRestartDialog(context);
                }
            }
        } catch (Exception ex) {
            Logger.printException(() -> "OnSharedPreferenceChangeListener failure", ex);
        }
    };

    /**
     * @return If the preference is currently set to the default value of the Setting.
     */
    private boolean prefIsSetToDefault(Preference pref, Setting<?> setting) {
        Object defaultValue = setting.defaultValue;
        if (pref instanceof SwitchPreference switchPref) {
            return switchPref.isChecked() == (Boolean) defaultValue;
        }
        String defaultValueString = defaultValue.toString();
        if (pref instanceof EditTextPreference editPreference) {
            return editPreference.getText().equals(defaultValueString);
        }
        if (pref instanceof ListPreference listPref) {
            return listPref.getValue().equals(defaultValueString);
        }

        throw new IllegalStateException("Must override method to handle "
                + "preference type: " + pref.getClass());
    }

    private void showSettingUserDialogConfirmation(Context context, Preference pref, Setting<?> setting) {
        Utils.verifyOnMainThread();

        final StringRef userDialogMessage = setting.userDialogMessage;
        if (context != null && userDialogMessage != null) {
            showingUserDialogMessage = true;

            Pair<Dialog, LinearLayout> dialogPair = Utils.createCustomDialog(
                    context,
                    str("revanced_extended_confirm_user_dialog_title"), // Title.
                    userDialogMessage.toString(), // No message.
                    null, // No EditText.
                    null, // OK button text.
                    () -> {
                        if (setting.rebootApp) {
                            showRestartDialog(context);
                        }
                    },
                    () -> {
                        // Cancel button action. Restore whatever the setting was before the change.
                        // Restore whatever the setting was before the change.
                        if (setting instanceof BooleanSetting booleanSetting &&
                                pref instanceof SwitchPreference switchPreference) {
                            switchPreference.setChecked(booleanSetting.defaultValue);
                        } else if (setting instanceof EnumSetting<?> enumSetting &&
                                pref instanceof ListPreference listPreference) {
                            listPreference.setValue(enumSetting.defaultValue.toString());
                            updateListPreferenceSummary(listPreference, setting);
                        }
                    },
                    null, // No Neutral button.
                    null, // No Neutral button action.
                    true  // Dismiss dialog when onNeutralClick.
            );

            Dialog dialog = dialogPair.first;
            dialog.setOnShowListener(d -> showingUserDialogMessage = false);
            dialog.show();
        }
    }

    public ReVancedPreferenceFragment() {
        // Required empty public constructor
    }

    private void putPreferenceScreenMap(SortedMap<String, PreferenceScreen> preferenceScreenMap, PreferenceGroup preferenceGroup) {
        if (preferenceGroup instanceof PreferenceScreen mPreferenceScreen) {
            preferenceScreenMap.put(mPreferenceScreen.getKey(), mPreferenceScreen);
        }
    }

    private void setPreferenceScreenToolbar() {
        SortedMap<String, PreferenceScreen> preferenceScreenMap = new TreeMap<>();

        PreferenceScreen rootPreferenceScreen = getPreferenceScreen();
        for (Preference preference : getAllPreferencesBy(rootPreferenceScreen)) {
            if (!(preference instanceof PreferenceGroup preferenceGroup)) continue;
            putPreferenceScreenMap(preferenceScreenMap, preferenceGroup);
            for (Preference childPreference : getAllPreferencesBy(preferenceGroup)) {
                if (!(childPreference instanceof PreferenceGroup nestedPreferenceGroup)) continue;
                putPreferenceScreenMap(preferenceScreenMap, nestedPreferenceGroup);
                for (Preference nestedPreference : getAllPreferencesBy(nestedPreferenceGroup)) {
                    if (!(nestedPreference instanceof PreferenceGroup childPreferenceGroup))
                        continue;
                    putPreferenceScreenMap(preferenceScreenMap, childPreferenceGroup);
                }
            }
        }

        Integer targetSDKVersion = ExtendedUtils.getTargetSDKVersion(getContext().getPackageName());
        boolean isEdgeToEdgeSupported = isSDKAbove(35) && targetSDKVersion != null && targetSDKVersion >= 35;

        for (PreferenceScreen mPreferenceScreen : preferenceScreenMap.values()) {
            mPreferenceScreen.setOnPreferenceClickListener(
                    preferenceScreen -> {
                        Dialog preferenceScreenDialog = mPreferenceScreen.getDialog();
                        ViewGroup rootView = (ViewGroup) preferenceScreenDialog
                                .findViewById(android.R.id.content)
                                .getParent();

                        // Fix the system navigation bar color for submenus.
                        ThemeUtils.setNavigationBarColor(preferenceScreenDialog.getWindow());

                        // Edge-to-edge is enforced if the following conditions are met:
                        // 1. targetSDK is 35 or greater (YouTube 19.44.39 or greater).
                        // 2. user is using Android 15 or greater.
                        //
                        // Related Issues:
                        // https://github.com/ReVanced/revanced-patches/issues/3976
                        // https://github.com/ReVanced/revanced-patches/issues/4606
                        //
                        // Docs:
                        // https://developer.android.com/develop/ui/views/layout/edge-to-edge#system-bars-insets
                        //
                        // Since ReVanced Settings Activity do not use AndroidX libraries,
                        // You will need to manually fix the layout breakage caused by edge-to-edge.
                        if (isEdgeToEdgeSupported) {
                            rootView.setOnApplyWindowInsetsListener((v, insets) -> {
                                Insets statusInsets = insets.getInsets(WindowInsets.Type.statusBars());
                                Insets navInsets = insets.getInsets(WindowInsets.Type.navigationBars());
                                v.setPadding(0, statusInsets.top, 0, navInsets.bottom);
                                return insets;
                            });
                        }

                        Toolbar toolbar = new Toolbar(preferenceScreen.getContext());

                        toolbar.setTitle(preferenceScreen.getTitle());
                        toolbar.setNavigationIcon(ThemeUtils.getBackButtonDrawable());
                        toolbar.setNavigationOnClickListener(view -> preferenceScreenDialog.dismiss());

                        int margin = (int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()
                        );

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            toolbar.setTitleMargin(margin, 0, margin, 0);
                        }

                        TextView toolbarTextView = getChildView(toolbar, TextView.class::isInstance);
                        if (toolbarTextView != null) {
                            toolbarTextView.setTextColor(ThemeUtils.getAppForegroundColor());
                        }

                        setToolbarLayoutParams(toolbar);

                        rootView.addView(toolbar, 0);
                        return false;
                    }
            );
        }
    }

    // Map to store dependencies: key is the preference key, value is a list of dependent preferences
    private final Map<String, List<Preference>> dependencyMap = new HashMap<>();
    // Set to track already added preferences to avoid duplicates
    private final Set<String> addedPreferences = new HashSet<>();
    // Map to store preferences grouped by their parent PreferenceGroup
    private final Map<PreferenceGroup, List<Preference>> groupedPreferences = new LinkedHashMap<>();

    @SuppressLint("ResourceType")
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            mPreferenceManager = getPreferenceManager();
            mPreferenceManager.setSharedPreferencesName(Setting.preferences.name);
            mSharedPreferences = mPreferenceManager.getSharedPreferences();
            addPreferencesFromResource(getXmlIdentifier("revanced_prefs"));

            mPreferenceScreen = getPreferenceScreen();
            Utils.sortPreferenceGroups(mPreferenceScreen);
            Utils.setPreferenceTitlesToMultiLineIfNeeded(mPreferenceScreen);

            // Store the original structure for restoration after filtering.
            originalPreferenceScreen = getPreferenceManager().createPreferenceScreen(getContext());
            for (int i = 0, count = mPreferenceScreen.getPreferenceCount(); i < count; i++) {
                originalPreferenceScreen.addPreference(mPreferenceScreen.getPreference(i));
            }

            setPreferenceScreenToolbar(mPreferenceScreen);

            // Initialize ReVanced settings
            ReVancedSettingsPreference.initializeReVancedSettings();

            // Import/export
            setBackupRestorePreference();

            // Debug log
            setDebugLogPreference();

            // Load and set initial preferences states
            for (Setting<?> setting : Setting.allLoadedSettings()) {
                final Preference preference = mPreferenceManager.findPreference(setting.key);
                if (preference != null && isSDKAbove(26)) {
                    preference.setSingleLineTitle(false);
                }

                if (preference instanceof SwitchPreference switchPreference) {
                    BooleanSetting boolSetting = (BooleanSetting) setting;
                    switchPreference.setChecked(boolSetting.get());
                } else if (preference instanceof EditTextPreference editTextPreference) {
                    editTextPreference.setText(setting.get().toString());
                } else if (preference instanceof ListPreference listPreference) {
                    updateListPreferenceSummary(listPreference, setting);
                }
            }

            // Register preference change listener
            mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        } catch (Exception th) {
            Logger.printException(() -> "Error during onCreate()", th);
        }
    }

    /**
     * Called when the fragment starts, ensuring all preferences are collected after initialization.
     */
    @Override
    public void onStart() {
        super.onStart();
        try {
            if (allPreferences.isEmpty()) {
                // Must collect preferences on start and not in initialize since
                // legacy SB settings are not loaded yet.
                Logger.printDebug(() -> "Collecting preferences to search");

                // Do not show root menu preferences in search results.
                // Instead search for everything that's not shown when search is not active.
                collectPreferences(mPreferenceScreen, 1, 0);
            }
        } catch (Exception ex) {
            Logger.printException(() -> "onStart failure", ex);
        }
    }

    /**
     * Recursively collects all preferences from the screen or group.
     *
     * @param includeDepth Menu depth to start including preferences.
     *                     A value of 0 adds all preferences.
     */
    private void collectPreferences(PreferenceGroup group, int includeDepth, int currentDepth) {
        for (int i = 0, count = group.getPreferenceCount(); i < count; i++) {
            Preference preference = group.getPreference(i);
            if (includeDepth <= currentDepth && !(preference instanceof PreferenceCategory)
                    && !(preference instanceof SponsorBlockPreferenceGroup)) {

                AbstractPreferenceSearchData<?> data;
                if (preference instanceof SwitchPreference switchPref) {
                    data = new SwitchPreferenceSearchData(switchPref);
                } else if (preference instanceof ListPreference listPref) {
                    data = new ListPreferenceSearchData(listPref);
                } else {
                    data = new PreferenceSearchData(preference);
                }

                allPreferences.add(data);
            }

            if (preference instanceof PreferenceGroup subGroup) {
                collectPreferences(subGroup, includeDepth, currentDepth + 1);
            }
        }
    }

    /**
     * Filters the preferences using the given query string and applies highlighting.
     */
    public void filterPreferences(String query) {
        mPreferenceScreen.removeAll();

        if (TextUtils.isEmpty(query)) {
            // Restore original preferences and their titles/summaries/entries.
            for (int i = 0, count = originalPreferenceScreen.getPreferenceCount(); i < count; i++) {
                mPreferenceScreen.addPreference(originalPreferenceScreen.getPreference(i));
            }

            for (AbstractPreferenceSearchData<?> data : allPreferences) {
                data.clearHighlighting();
            }

            return;
        }

        // Navigation path -> Category
        Map<String, PreferenceCategory> categoryMap = new HashMap<>();
        String queryLower = Utils.removePunctuationToLowercase(query);

        Pattern queryPattern = Pattern.compile(Pattern.quote(Utils.removePunctuationToLowercase(query)),
                Pattern.CASE_INSENSITIVE);

        for (AbstractPreferenceSearchData<?> data : allPreferences) {
            if (data.matchesSearchQuery(queryLower)) {
                data.applyHighlighting(queryLower, queryPattern);

                String navigationPath = data.navigationPath;
                PreferenceCategory group = categoryMap.computeIfAbsent(navigationPath, key -> {
                    PreferenceCategory newGroup = new PreferenceCategory(mPreferenceScreen.getContext());
                    newGroup.setTitle(navigationPath);
                    mPreferenceScreen.addPreference(newGroup);
                    return newGroup;
                });
                group.addPreference(data.preference);
            }
        }

        // Show 'No results found' if search results are empty.
        if (categoryMap.isEmpty()) {
            Preference noResultsPreference = new Preference(mPreferenceScreen.getContext());
            noResultsPreference.setTitle(str("revanced_extended_settings_search_no_results_title", query));
            noResultsPreference.setSummary(str("revanced_extended_settings_search_no_results_summary"));
            noResultsPreference.setSelectable(false);
            // Set icon for the placeholder preference.
            noResultsPreference.setLayoutResource(getLayoutIdentifier(
                    "revanced_preference_with_icon_no_search_result"));
            noResultsPreference.setIcon(getDrawableIdentifier("revanced_settings_search_icon"));
            mPreferenceScreen.addPreference(noResultsPreference);
        }
    }

    /**
     * Sets toolbar for all nested preference screens.
     */
    private void setPreferenceScreenToolbar(PreferenceScreen parentScreen) {
        Integer targetSDKVersion = ExtendedUtils.getTargetSDKVersion(getContext().getPackageName());
        boolean isEdgeToEdgeSupported = isSDKAbove(35) && targetSDKVersion != null && targetSDKVersion >= 35;

        for (int i = 0, count = parentScreen.getPreferenceCount(); i < count; i++) {
            Preference childPreference = parentScreen.getPreference(i);
            if (childPreference instanceof PreferenceScreen) {
                // Recursively set sub preferences.
                setPreferenceScreenToolbar((PreferenceScreen) childPreference);

                childPreference.setOnPreferenceClickListener(
                        childScreen -> {
                            Dialog preferenceScreenDialog = ((PreferenceScreen) childScreen).getDialog();
                            ViewGroup rootView = (ViewGroup) preferenceScreenDialog
                                    .findViewById(android.R.id.content)
                                    .getParent();

                            // Fix the system navigation bar color for submenus.
                            ThemeUtils.setNavigationBarColor(preferenceScreenDialog.getWindow());

                            // Edge-to-edge is enforced if the following conditions are met:
                            // 1. targetSDK is 35 or greater (YouTube 19.44.39 or greater).
                            // 2. user is using Android 15 or greater.
                            //
                            // Related Issues:
                            // https://github.com/ReVanced/revanced-patches/issues/3976
                            // https://github.com/ReVanced/revanced-patches/issues/4606
                            //
                            // Docs:
                            // https://developer.android.com/develop/ui/views/layout/edge-to-edge#system-bars-insets
                            //
                            // Since ReVanced Settings Activity do not use AndroidX libraries,
                            // You will need to manually fix the layout breakage caused by edge-to-edge.
                            if (isEdgeToEdgeSupported) {
                                rootView.setOnApplyWindowInsetsListener((v, insets) -> {
                                    Insets statusInsets = insets.getInsets(WindowInsets.Type.statusBars());
                                    Insets navInsets = insets.getInsets(WindowInsets.Type.navigationBars());
                                    v.setPadding(0, statusInsets.top, 0, navInsets.bottom);
                                    return insets;
                                });
                            }

                            Toolbar toolbar = new Toolbar(childScreen.getContext());
                            toolbar.setTitle(childScreen.getTitle());
                            toolbar.setNavigationIcon(ThemeUtils.getBackButtonDrawable());
                            toolbar.setNavigationOnClickListener(view -> preferenceScreenDialog.dismiss());

                            final int margin = Utils.dipToPixels(16);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                toolbar.setTitleMargin(margin, 0, margin, 0);
                            }

                            TextView toolbarTextView = Utils.getChildView(toolbar,
                                    true, TextView.class::isInstance);
                            if (toolbarTextView != null) {
                                toolbarTextView.setTextColor(ThemeUtils.getAppForegroundColor());
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) toolbarTextView.getLayoutParams();
                                    lp.setMarginStart(margin);
                                    lp.setMarginEnd(margin);
                                    toolbarTextView.setLayoutParams(lp);
                                }
                            }

                            setToolbarLayoutParams(toolbar);

                            rootView.addView(toolbar, 0);
                            return false;
                        }
                );
            }
        }
    }

    @Override
    public void onDestroy() {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
        Utils.resetLocalizedContext();
        super.onDestroy();
    }

    /**
     * Add Preference to Import/Export settings submenu
     */
    private void setBackupRestorePreference() {
        findPreference("revanced_extended_settings_import").setOnPreferenceClickListener(pref -> {
            importActivity();
            return false;
        });

        findPreference("revanced_extended_settings_export").setOnPreferenceClickListener(pref -> {
            settingExportInProgress = true;
            exportActivity();
            return false;
        });
    }

    /**
     * Invoke the SAF(Storage Access Framework) to export settings
     */
    private void exportActivity() {
        if (!settingExportInProgress && !BaseSettings.DEBUG.get()) {
            Utils.showToastShort(str("revanced_debug_logs_disabled"));
            return;
        }

        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        final String appName = ExtendedUtils.getAppLabel();
        final String versionName = ExtendedUtils.getAppVersionName();
        final String formatDate = dateFormat.format(new Date(System.currentTimeMillis()));
        final StringBuilder sb = new StringBuilder();
        sb.append(appName);
        sb.append("_v");
        sb.append(versionName);
        String patchVersion = PatchVersion();
        if (!"Unknown".equals(patchVersion)) {
            sb.append("_rvp_v");
            sb.append(patchVersion);
        }
        sb.append("_");
        if (settingExportInProgress) {
            sb.append("settings");
        } else {
            sb.append("log");
        }
        sb.append("_");
        sb.append(formatDate);

        final Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, sb.toString());
        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    /**
     * Invoke the SAF(Storage Access Framework) to import settings
     */
    private void importActivity() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(isSDKAbove(29) ? "text/plain" : "*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    /**
     * Activity should be done within the lifecycle of PreferenceFragment
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            exportText(data.getData());
        } else if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            importText(data.getData());
        }
    }

    private void exportText(Uri uri) {
        final Context context = this.getActivity();
        try {
            @SuppressLint("Recycle")
            FileWriter jsonFileWriter =
                    new FileWriter(
                            Objects.requireNonNull(context.getApplicationContext()
                                            .getContentResolver()
                                            .openFileDescriptor(uri, "w"))
                                    .getFileDescriptor()
                    );
            PrintWriter printWriter = new PrintWriter(jsonFileWriter);
            if (settingExportInProgress) {
                printWriter.write(Setting.exportToJson(context));
            } else {
                String message = LogBufferManager.exportToString();
                if (message != null) {
                    printWriter.write(message);
                }
            }
            printWriter.close();
            jsonFileWriter.close();

            if (settingExportInProgress) {
                showToastShort(str("revanced_extended_settings_export_success"));
            } else {
                showToastShort(str("revanced_debug_logs_export_success"));
            }
        } catch (IOException e) {
            if (settingExportInProgress) {
                showToastShort(str("revanced_extended_settings_export_failed"));
            } else {
                showToastShort(String.format(str("revanced_debug_logs_failed_to_export"), e.getMessage()));
            }
        } finally {
            settingExportInProgress = false;
        }
    }

    private void importText(Uri uri) {
        final Context context = this.getActivity();
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            settingImportInProgress = true;

            @SuppressLint("Recycle")
            FileReader fileReader =
                    new FileReader(
                            Objects.requireNonNull(context.getApplicationContext()
                                            .getContentResolver()
                                            .openFileDescriptor(uri, "r"))
                                    .getFileDescriptor()
                    );
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            bufferedReader.close();
            fileReader.close();

            final boolean restartNeeded = Setting.importFromJSON(context, sb.toString());
            if (restartNeeded) {
                showRestartDialog(getActivity());
            }
        } catch (IOException e) {
            showToastShort(str("revanced_extended_settings_import_failed"));
            throw new RuntimeException(e);
        } finally {
            settingImportInProgress = false;
        }
    }

    /**
     * Set Preference to Debug settings submenu
     */
    private void setDebugLogPreference() {
        Preference clearLog = findPreference("revanced_debug_logs_clear_buffer");
        if (clearLog == null) {
            return;
        }
        clearLog.setOnPreferenceClickListener(pref -> {
            LogBufferManager.clearLogBuffer();
            return false;
        });

        Preference exportLogToClipboard = findPreference("revanced_debug_export_logs_to_clipboard");
        if (exportLogToClipboard == null) {
            return;
        }
        exportLogToClipboard.setOnPreferenceClickListener(pref -> {
            LogBufferManager.exportToClipboard();
            return false;
        });

        Preference exportLogToFile = findPreference("revanced_debug_export_logs_to_file");
        if (exportLogToFile == null) {
            return;
        }
        exportLogToFile.setOnPreferenceClickListener(pref -> {
            exportActivity();
            return false;
        });
    }
    
    private List<Preference> getAllPreferencesBy(PreferenceGroup preferenceGroup) {
        List<Preference> preferences = new ArrayList<>();
        for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
            preferences.add(preferenceGroup.getPreference(i));
        }
        return preferences;
    }

}

@SuppressWarnings("deprecation")
class AbstractPreferenceSearchData<T extends Preference> {
    /**
     * @return The navigation path for the given preference, such as "Player > Action buttons".
     */
    private static String getPreferenceNavigationString(Preference preference) {
        // On older Android versions, traversing parent preferences is not
        // reliably supported and can cause infinite loops, leading to OutOfMemoryError.
        // This check prevents the search feature from crashing the app on those versions
        // by returning an empty navigation path.
        if (!isSDKAbove(26)) {
            return "";
        }

        Deque<CharSequence> pathElements = new ArrayDeque<>();
        Preference current = preference.getParent(); // Start from the direct parent

        while (current != null) {
            // Exclude titles from the root screen and certain category types
            if (!(current instanceof PreferenceScreen)) {
                 if (!(current instanceof NoTitlePreferenceCategory) && !(current instanceof SponsorBlockPreferenceGroup)) {
                    CharSequence title = current.getTitle();
                    if (title != null && title.length() > 0) {
                        pathElements.addFirst(title);
                    }
                }
            }

            Preference parent = current.getParent();
            if (parent == current) { // Cycle detected
                break;
            }
            current = parent;
        }

        if (pathElements.isEmpty()) {
            return "";
        }

        Locale locale = BaseSettings.REVANCED_LANGUAGE.get().getLocale();
        return Utils.getTextDirectionString(locale) + String.join(" > ", pathElements);
    }

    /**
     * Highlights the search query in the given text by applying color span.
     *
     * @param text         The original text to process.
     * @param queryPattern The search query to highlight.
     * @return The text with highlighted query matches as a SpannableStringBuilder.
     */
    static CharSequence highlightSearchQuery(CharSequence text, Pattern queryPattern) {
        if (TextUtils.isEmpty(text)) {
            return text;
        }

        final int baseColor = ThemeUtils.getAppBackgroundColor();
        final int adjustedColor = ThemeUtils.isDarkModeEnabled()
                ? ThemeUtils.adjustColorBrightness(baseColor, 1.20f)  // Lighten for dark theme.
                : ThemeUtils.adjustColorBrightness(baseColor, 0.95f); // Darken for light theme.
        BackgroundColorSpan highlightSpan = new BackgroundColorSpan(adjustedColor);

        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        Matcher matcher = queryPattern.matcher(text);

        while (matcher.find()) {
            spannable.setSpan(
                    highlightSpan,
                    matcher.start(),
                    matcher.end(),
                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        return spannable;
    }

    final T preference;
    final String key;
    final String navigationPath;
    boolean highlightingApplied;

    @Nullable
    CharSequence originalTitle;
    @Nullable
    String searchTitle;

    AbstractPreferenceSearchData(T pref) {
        preference = pref;
        key = Utils.removePunctuationToLowercase(pref.getKey());
        navigationPath = getPreferenceNavigationString(pref);
    }

    @CallSuper
    void updateSearchDataIfNeeded() {
        if (highlightingApplied) {
            // Must clear, otherwise old highlighting is still applied.
            clearHighlighting();
        }

        CharSequence title = preference.getTitle();
        if (originalTitle != title) { // Check using reference equality.
            originalTitle = title;
            searchTitle = Utils.removePunctuationToLowercase(title);
        }
    }

    @CallSuper
    boolean matchesSearchQuery(String query) {
        updateSearchDataIfNeeded();

        return key.contains(query)
                || searchTitle != null && searchTitle.contains(query);
    }

    @CallSuper
    void applyHighlighting(String query, Pattern queryPattern) {
        preference.setTitle(highlightSearchQuery(originalTitle, queryPattern));
        highlightingApplied = true;
    }

    @CallSuper
    void clearHighlighting() {
        if (highlightingApplied) {
            preference.setTitle(originalTitle);
            highlightingApplied = false;
        }
    }
}

/**
 * Regular preference type that only uses the base preference summary.
 * Should only be used if a more specific data class does not exist.
 */
@SuppressWarnings("deprecation")
class PreferenceSearchData extends AbstractPreferenceSearchData<Preference> {
    @Nullable
    CharSequence originalSummary;
    @Nullable
    String searchSummary;

    PreferenceSearchData(Preference pref) {
        super(pref);
    }

    void updateSearchDataIfNeeded() {
        super.updateSearchDataIfNeeded();

        CharSequence summary = preference.getSummary();
        if (originalSummary != summary) {
            originalSummary = summary;
            searchSummary = Utils.removePunctuationToLowercase(summary);
        }
    }

    boolean matchesSearchQuery(String query) {
        return super.matchesSearchQuery(query)
                || searchSummary != null && searchSummary.contains(query);
    }

    @Override
    void applyHighlighting(String query, Pattern queryPattern) {
        super.applyHighlighting(query, queryPattern);

        preference.setSummary(highlightSearchQuery(originalSummary, queryPattern));
    }

    @CallSuper
    void clearHighlighting() {
        if (highlightingApplied) {
            preference.setSummary(originalSummary);
        }

        super.clearHighlighting();
    }
}

/**
 * Switch preference type that uses summaryOn and summaryOff.
 */
@SuppressWarnings("deprecation")
class SwitchPreferenceSearchData extends AbstractPreferenceSearchData<SwitchPreference> {
    @Nullable
    CharSequence originalSummaryOn, originalSummaryOff;
    @Nullable
    String searchSummaryOn, searchSummaryOff;

    SwitchPreferenceSearchData(SwitchPreference pref) {
        super(pref);
    }

    void updateSearchDataIfNeeded() {
        super.updateSearchDataIfNeeded();

        CharSequence summaryOn = preference.getSummaryOn();
        if (originalSummaryOn != summaryOn) {
            originalSummaryOn = summaryOn;
            searchSummaryOn = Utils.removePunctuationToLowercase(summaryOn);
        }

        CharSequence summaryOff = preference.getSummaryOff();
        if (originalSummaryOff != summaryOff) {
            originalSummaryOff = summaryOff;
            searchSummaryOff = Utils.removePunctuationToLowercase(summaryOff);
        }
    }

    boolean matchesSearchQuery(String query) {
        return super.matchesSearchQuery(query)
                || searchSummaryOn != null && searchSummaryOn.contains(query)
                || searchSummaryOff != null && searchSummaryOff.contains(query);
    }

    @Override
    void applyHighlighting(String query, Pattern queryPattern) {
        super.applyHighlighting(query, queryPattern);

        preference.setSummaryOn(highlightSearchQuery(originalSummaryOn, queryPattern));
        preference.setSummaryOff(highlightSearchQuery(originalSummaryOff, queryPattern));
    }

    @CallSuper
    void clearHighlighting() {
        if (highlightingApplied) {
            preference.setSummaryOn(originalSummaryOn);
            preference.setSummaryOff(originalSummaryOff);
        }

        super.clearHighlighting();
    }
}

/**
 * List preference type that uses entries.
 */
@SuppressWarnings("deprecation")
class ListPreferenceSearchData extends AbstractPreferenceSearchData<ListPreference> {
    @Nullable
    CharSequence[] originalEntries;
    @Nullable
    String searchEntries;

    ListPreferenceSearchData(ListPreference pref) {
        super(pref);
    }

    void updateSearchDataIfNeeded() {
        super.updateSearchDataIfNeeded();

        CharSequence[] entries = preference.getEntries();
        if (originalEntries != entries) {
            originalEntries = entries;
            searchEntries = Utils.removePunctuationToLowercase(String.join(" ", entries));
        }
    }

    boolean matchesSearchQuery(String query) {
        return super.matchesSearchQuery(query)
                || searchEntries != null && searchEntries.contains(query);
    }

    @Override
    void applyHighlighting(String query, Pattern queryPattern) {
        super.applyHighlighting(query, queryPattern);

        if (originalEntries != null) {
            final int length = originalEntries.length;
            CharSequence[] highlightedEntries = new CharSequence[length];

            for (int i = 0; i < length; i++) {
                highlightedEntries[i] = highlightSearchQuery(originalEntries[i], queryPattern);

                // Cannot highlight the summary text, because ListPreference uses
                // the toString() of the summary CharSequence which strips away all formatting.
            }

            preference.setEntries(highlightedEntries);
        }
    }

    @CallSuper
    void clearHighlighting() {
        if (highlightingApplied) {
            preference.setEntries(originalEntries);
        }

        super.clearHighlighting();
    }
}

