package edu.usc.UscAR.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class UscARConstant {

    public static class UscARField implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://uscar/ar");

        public static final String USC_AR_TABLE = "usc_ar";

        public static final String AR_ID = "ar_id";

        public static final String CODE = "code";

        public static final String NAME = "name";

        public static final String SHORT = "short";

        public static final String LATITUDE = "latitude";

        public static final String LONGITUDE = "longitude";

        public static final String DATA = "_data";

        public static final String URL = "url";

        public static final String ADDRESS = "address";
    }
}
