package app.revanced.extension.shared.settings.preference;

import static android.text.Html.FROM_HTML_MODE_COMPACT;

import android.content.Context;
import android.os.Build;
import android.preference.Preference;
import android.text.Html;
import android.util.AttributeSet;

/**
 * Allows using basic html for the summary text.
 */
@SuppressWarnings({"unused", "deprecation"})
public class HtmlPreference extends Preference {
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setSummary(Html.fromHtml(getSummary().toString(), FROM_HTML_MODE_COMPACT));
        } else {
            setSummary(Html.fromHtml(getSummary().toString()));
        }
    }

    public HtmlPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public HtmlPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HtmlPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HtmlPreference(Context context) {
        super(context);
    }
}