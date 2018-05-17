package com.manish.chushylearn.helper;

/**
 * Created by rbaisak on 1/2/17.
 */

public class Constants {

    public static final int REQUEST_CODE_CAPTURE = 2000;

    public static final int FETCH_STARTED = 2001;
    public static final int FETCH_COMPLETED = 2002;
    public static final int ERROR = 2003;

    public static final int MAX_LIMIT = 999;

    public static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 23;
    public static final int PERMISSION_REQUEST_CAMERA = 24;

    public static final String PREF_WRITE_EXTERNAL_STORAGE_REQUESTED = "writeExternalRequested";
    public static final String PREF_CAMERA_REQUESTED = "cameraRequested";
    public static final String INTENT_EXTRA_IMAGES = "images";

    public static final String CHATUSER = "chatuser";

    public static final int REQUEST_READ_EXTERNAL_STORAGE = 4001;
    public static final int PLACE_PICKER_REQUEST = 11;

    /**
     * Folder names in FireBase database
     */
    public static final String OFFER_FOLDER_NAME = "offers";
    public static final String LOCATION_FOLDER_NAME = "locations";

    public static final double nitlat = 20.5937;  //23.840878;
    public static final double nitlng = 78.9629;  //91.423792;

}
