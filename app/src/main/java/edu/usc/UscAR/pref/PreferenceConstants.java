package edu.usc.UscAR.pref;

import android.net.Uri;
import android.provider.BaseColumns;

public class PreferenceConstants {

    public static class PreferenceField implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://uscar_pref/pref");

        public static final String PREFERENCE_TABLE = "pref";

        public static final String DEFAULT_INIT = "default_init";
    }
}
