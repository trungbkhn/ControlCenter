package com.tapbi.spark.controlcenter.common


object Constant {

    const val IS_SHOW_DIALOG_USER_MANUAL: String="is_show_dialog_user_manual"
    const val KEY_ID_CATEGORY: String = "KEY_ID_CATEGORY"
    const val KEY_ID_CURRENT_APPLY_THEME = "KEY_ID_CURRENT_APPLY_THEME"
    const val KEY_ID_CURRENT_APPLY_THEME_DEFAULT = 2001L
    const val KEY_ID_2002 = 2002L
    const val TIME_DELAYED_DISPATCH_TOUCH_EVENT: Int = 200
    const val TIME_DELAYED_DISPATCH_LONG_TOUCH_EVENT: Int = 2000
    const val MAX_SELECT_FAVORITE = 3
    const val ID_THEME_CONTROL = "id"
    const val ID_CATEGORY = "idCategory"
    const val PREVIEW = "preview"
    const val KEY_EDIT_THEME = "KEY_EDIT_THEME"



    const val LANGUAGE_EN: String = "en"
    const val LANGUAGE_VN: String = "vi"
    const val DB_NAME = "CONTROL_CENTER_DATA_BASE"
    const val DB_VERSION = 1
    const val TABLE_THEME_CONTROL = "TABLE_THEME_CONTROL"
    const val TABLE_ITEM_CONTROL = "TABLE_ITEM_CONTROL"

    const val DIALOG_REQUEST_PERMISSION_WRITE_SETTING: String =
        "DIALOG_REQUEST_PERMISSION_WRITE_SETTING"


    const val INSERT_DEFAUT: String = "INSERT_DEFAUT"

    const val UPDATE_COLOR_FOCUS: String = "UPDATE_COLOR_FOCUS"


    const val JUST_NOW: String = " just now"
    const val A_MINUTE_AGO: String = " a minute ago"
    const val MINUTES_AGO: String = " minutes ago"
    const val AN_HOUR_AGO: String = " an hour ago"
    const val HOURS_AGO: String = " hours ago"
    const val YESTERDAY: String = " yesterday"
    const val DAYS_AGO: String = " days ago"
    const val K_6: Int = 60000
    const val K_12: Int = 120000
    const val K_300: Int = 3000000
    const val K_540: Int = 5400000
    const val K_86400: Int = 86400000
    const val K_17280: Int = 172800000
    const val K_360: Int = 3600000

    const val MUSIC_PLAYER_SELECTED_PACKAGENAME: String = "music_player_selected_packagename"
    const val MUSIC_PLAYER_SELECTED_RECEIVERNAME: String = "music_player_selected_receivername"

    const val BACKGROUND_SELECTED: String = "background_selected"
    const val BACKGROUND_SELECTED_CUSTOM: String = "background_selected_custom"
    const val DEFAULT: String = "DEFAULT"
    const val MAX_ITEM_LOADING: Int = 15
    const val TRANSPARENT: String = "TRANSPARENT"
    const val CURRENT_BACKGROUND: String = "CURRENT_BACKGROUND"
    const val REAL_TIME: String = "REAL_TIME"
    const val STORE_WALLPAPER: String = "STORE_WALLPAPER"
    const val ID_STORE_WALLPAPER_SELECTED: String = "ID_STORE_WALLPAPER_SELECTED"
    const val ID_STORE_WALLPAPER_CUSTOM_SELECTED: String = "ID_STORE_WALLPAPER_CUSTOM_SELECTED"
    const val FOLDER_BACKGROUND_ASSETS: String = "background"
    const val FOLDER_FONT_CONTROL_ASSETS: String = "fontControls/"
    const val FOLDER_APPEARANCE_ASSETS: String = "appearance"
    const val FOLDER_THEMES_ASSETS: String = "themes"
    const val FILE_NAME_BACKGROUND_ASSETS: String = "store_background.json"
    const val FILE_NAME_THEME_ASSETS: String = "theme_control.json"
    const val FILE_NAME_THEME_ASSETS_FAVORITE: String = "theme_control_favorite.json"
    const val FILE_NAME_BACKGROUND_LIGHT: String = "light1.webp"
    const val FILE_NAME_BACKGROUND_DARK: String = "dark1.webp"
    const val DEFAULT_BACKGROUND_MI: String = "themes/2000/2001/background.webp"
    const val FOLDER_THUMB_CONTROL: String = "THUMB CONTROL"
    const val ID_STORE_WALLPAPER_SELECTED_DEFAULT: Int = 1
    const val ID_STORE_WALLPAPER_SELECTED_CUSTOM_DEFAULT: Int = -1
    const val LAST_TIME_EDIT_THEME ="LAST_TIME_EDIT_THEME"

    const val CONTROL_CUSTOMIZE_SAVE: String = "control_customize_save"
    const val NOTES_CONTROL_ADDED: String = "NOTES_CONTROL_ADDED"
    const val CLOCK: String = "Clock"
    const val CALCULATOR: String = "Calculator"
    const val CAMERA: String = "Camera"
    const val RECORD: String = "Screen Record"
    const val DARK_MODE: String = "Dark Mode"
    const val NOTE: String = "Notes"

    const val BROADCAST_CHANGE_CONTROL_CUSTOM: String = "broadcast_change_control_custom"
    const val ACTION_CHANGE_ITEM_CONTROL: String = "action_change_item_control"
    const val ACTION_CHANGE_LAYOUT_CONTROL: String = "action_change_layout_control"

    const val INTENT_NOTY_GROUP: String = "INTENT_NOTY_GROUP"
    const val CHANGE_TYPE: String = "change_type"
    const val CHANGE_POSITION_EDGE: String = "CHANGE_POSITION"
    const val POSITION: String = "position_touch"
    const val CHANGE_LENGTH_EDGE: String = "change_length"
    const val CHANGE_ENABLED_EDGE: String = "change_enabled"
    const val CHANGE_SIZE_EDGE: String = "CHANGE_SIZE"
    const val LENGTH: String = "length"
    const val TYPE_EDGE: String = "TYPE_EDGE"
    const val VALUE_INT: String = "VALUE_INT"
    const val VALUE_BOOLEAN: String = "value_boolean"
    const val CHANGE_COLOR_CONTROL: String = "CHANGE_COLOR_CONTROL"
    const val CHANGE_COLOR_NOTY: String = "CHANGE_COLOR_NOTY"
    const val CHANGE_STATUS_EDIT_EDGE: String = "CHANGE_STATUS_EDIT_EDGE"
    const val EVENT_DATA_SAVER_NOT_SUPPORT = "event_data_saver_not_support"
    const val CHANGE_DARK_MODE: Int = 5
    const val TYPE_AUTO_START: Int = 7
    const val CHANGE_LOW_POWER: Int = 6
    const val COLOR: String = "color_touch"
    const val SAVE_MARGIN: Int = 4
    const val VIEW_TYPE_1: Int = 1
    const val VIEW_TYPE_2: Int = 2
    const val VIEW_TYPE_3: Int = 3
    const val VIEW_TYPE_4: Int = 4
    const val VIEW_TYPE_5: Int = 5
    const val FONT_FOLDER: String = "fontControls"
    const val ICON_SHADE_FOLDER = "iconShade"
    const val FONT_ROBOTO_REGULAR = "roboto_regular.ttf"

    const val LENGTH_TOUCH_EDGE_LEFT_PERCENT: String = "length_touch_edge_left_percent"
    const val LENGTH_TOUCH_EDGE_RIGHT_PERCENT: String = "length_touch_edge_right_percent"
    const val LENGTH_TOUCH_EDGE_BOTTOM_PERCENT: String = "length_touch_edge_bottom_percent"
    const val LENGTH_TOUCH_EDGE_PERCENT_DEFAULT: Int = 50
    const val LENGTH_TOUCH_EDGE_PERCENT_MIN: Int = 10
    const val SIZE_TOUCH_EDGE_TOP_PERCENT: String = "size_touch_edge_top_percent"
    const val SIZE_TOUCH_EDGE_LEFT_PERCENT: String = "size_touch_edge_left_percent"
    const val SIZE_TOUCH_EDGE_RIGHT_PERCENT: String = "size_touch_edge_right_percent"
    const val SIZE_TOUCH_EDGE_BOTTOM_PERCENT: String = "size_touch_edge_bottom_percent"
    const val SIZE_TOUCH_EDGE_PERCENT_DEFAULT: Int = 50
    const val POSITION_TOUCH_EDGE_LEFT_PERCENT: String = "position_touch_edge_left_percent"
    const val POSITION_TOUCH_EDGE_RIGHT_PERCENT: String = "position_touch_edge_right_percent"
    const val POSITION_TOUCH_EDGE_BOTTOM_PERCENT: String = "position_touch_edge_bottom_percent"
    const val POSITION_TOUCH_EDGE_PERCENT_DEFAULT: Int = 50

    const val COLOR_CONTROL_SELECTED: String = "color_control_selected"
    const val ALPHA_CONTROL_SELECTED: String = "alpha_control_selected"
    const val COLOR_EDGE_CONTROL_TOP: String = "color_with_alpha_control_selected"
    const val COLOR_EDGE_CONTROL_LEFT: String = "color_edge_control_left"
    const val COLOR_EDGE_CONTROL_RIGHT: String = "color_edge_control_right"
    const val COLOR_EDGE_CONTROL_BOTTOM: String = "color_edge_control_bottom"

    const val EVENT_CHANGE_CONNER: String = "EVENT_CHANGE_CONNER"
    const val EVENT_CHANGE_BACKGROUND: String = "EVENT_CHANGE_BACKGROUND"
    const val EVENT_CHANGE_COLOR: String = "EVENT_CHANGE_COLOR"
    const val EVENT_CHANGE_GROUP_COLOR: String = "EVENT_CHANGE_GROUP_COLOR"
    const val EVENT_CHANGE_ICON_SHADE: String = "EVENT_CHANGE_ICON_SHADE"
    const val EVENT_CHANGE_FONT: String = "EVENT_CHANGE_FONT"
    const val EVENT_CHANGE_STATE_SEEK_BAR: String = "EVENT_CHANGE_STATE_SEEK_BAR"
    const val EVENT_CHANGE_GALLERY: String = "EVENT_CHANGE_GALLERY"
    const val EVENT_SET_SELECT: String = "EVENT_SET_SELECT"

    const val COLOR_NOTY_SELECTED: String = "color_noty_selected"
    const val ALPHA_NOTY_SELECTED: String = "alpha_noty_selected"
    const val COLOR_EDGE_NOTY_TOP: String = "color_edge_noty_top"
    const val COLOR_EDGE_NOTY_LEFT: String = "color_edge_noty_left"
    const val COLOR_EDGE_NOTY_RIGHT: String = "color_edge_noty_right"
    const val COLOR_EDGE_NOTY_BOTTOM: String = "color_edge_noty_bottom"

    const val VIBRATOR_EDGE_TOP: String = "vibrator"
    const val VIBRATOR_EDGE_LEFT: String = "vibrator_edge_left"
    const val VIBRATOR_EDGE_RIGHT: String = "vibrator_edge_right"
    const val VIBRATOR_EDGE_BOTTOM: String = "vibrator_edge_bottom"
    const val VIBRATOR_CONTROL_LONG_CLICK: String = "vibrator_control_long_click"
    const val VALUE_DEFAULT_VIBRATOR: Boolean = false


    const val REMOVE_GROUP: String = "remove_group"
    const val POSITION_GROUP_REMOVE: String = "position_group_remove"
    const val POSITION_MODEL_IN_GROUP_REMOVE: String = "POSITION_MODEL_IN_GROUP_REMOVE"
    const val IS_REMOVE_GROUP_NOTY: String = "IS_REMOVE_GROUP_NOTY"

    const val ACTION_OPEN_APP: String = "action_open_app_lockscreen"
    const val PACKAGE_NAME_APP_OPEN: String = "package_name_app_open"

    const val REQUEST_PERMISSION_APP_RECENT: String = "request_permission_app_recent_lockscreen"
    const val REQUEST_PERMISSION_CALENDAR: String = "request_permission_calendar_lockscreen"
    const val OPEN_EVENT_NEXT_UP: String = "open_event_next_up"
    const val ID_EVENT_NEXT_UP: String = "id_event_next_up "

    const val ACTION_NOTY_CHANGE: String = "action_noty_change"
    const val ACTION_NOTY_SNOOZED: String = "ACTION_NOTY_SNOOZED"
    const val OPEN_APP_NOTY: String = "open_app_noty_lockscree"
    const val REQUEST_PERMISSION_CAMERA: String = "request_permission_camera"
    const val STYLE_CONTROL: String = "style_control"
    const val STYLE_CONTROL_TOP: Int = 0
    const val STYLE_CONTROL_BOTTOM: Int = 1
    const val OPEN_CAMERA: String = "open_camera_lockscreen"
    const val DIALOG_PREVIEW_CONTROL: String = "DIALOG_PREVIEW_CONTROL"
    const val DIALOG_CHOOSE_STYLE: String = "DIALOG_CHOOSE_STYLE"

    const val NOTY_X: String = "noty_x"
    const val NOTY_Y: String = "noty_y"
    const val CONTROL_X: String = "control_x"
    const val CONTROL_Y: String = "control_y"

    const val ENABLE_CONTROL: String = "enable_control"
    const val ENABLE_NOTY: String = "enable_noty"
    const val DEFAULT_ENABLE_NOTY: Boolean = true
    const val DEFAULT_ENABLE_CONTROL: Boolean = true
    const val TYPE_NOTY: String = "type_noty"
    const val KEY_ENABLED_EDGE_LEFT: String = "key_enabled_left"
    const val KEY_ENABLED_EDGE_RIGHT: String = "key_enabled_right"
    const val KEY_ENABLED_EDGE_BOTTOM: String = "key_enabled_bottom"
    const val DEFAULT_ENABLED_EDGE_LEFT_RIGHT_BOTTOM: Boolean = false
    const val CHECK_CLEAR_APP: String = "check_clear_app"
    const val KEY_AUTO_HIDE_NOTI_PANEL_SYSTEM_SHADE: String =
        "KEY_AUTO_HIDE_NOTI_PANEL_SYSTEM_SHADE"
    const val VALUE_DEFAULT_AUTO_HIDE_NOTI_PANEL_SYSTEM_SHADE: Boolean = true


    //    const val VALUE_CONTROL_CENTER: Int = 6000
    const val VALUE_MI_NOTI: Int = 2000
    const val VALUE_SHADE: Int = 1000
    const val VALUE_CONTROL_CENTER_OS: Int = 6000
    const val VALUE_CONTROL_CENTER: Int = 2000
    const val VALUE_PIXEL: Int = 3000
    const val VALUE_SAMSUNG: Int = 5000


    const val PAUSE: Int = 1
    const val PLAY: Int = 2
    const val NEXT: Int = 3
    const val PREVIOUS: Int = 4


    const val KEY_COLLAPSE: Int = 4
    const val KEY_EXPAND: Int = 4


    //  public static final int KEY_BG_DEFAULT = 0;
    //  public static final int KEY_BG_TRANSPARENT = 1;
    //  public static final int KEY_BG_CURRENT = 2;
    //  public static final int KEY_BG_SCREEN_BLUR = 3;
    const val STYLE_SELECTED: String = "style_selected"

    const val LIGHT: Int = 0
    const val DARK: Int = 1

    const val ICON_ACTION_SELECT: String = "value_icon_select"
    const val ICON_ACTION_DEFAULT: String = "ic_ellipse"

    const val SHOW: String = "SHOW"
    const val HIDE: String = "HIDE"

    const val ACTION_Mi_SELECT: String = "action_mi_select"
    const val ACTION_SHADE_SELECT: String = "action_shade_select"
    const val ACTION_OPPO_SELECT: String = "action_oppo_select"
    const val SHOW_DATE_TIME: String = "show_date_time"
    const val TEXT_SHOW: String = "value_text"
    const val IS_ENABLE: String = "is_enable"

    const val UPDATE_ACTION_V6_1_3: String = "UPDATE_ACTION_V6_1_3"

    const val VERSION_CODE: String = "VERSION_CODE"

    const val FORMAT_SIMPLE_DATE: String = "EEE, MMM d_HH:mm"

    const val DEFAULT_IS_ENABLE: Int = 0
    const val IS_DISABLE: Int = 1
    const val TYPE_ADD: Int = 0
    const val TYPE_REMOVE: Int = 1

    const val PORTRAIT: Int = 1
    const val LANDSCAPE: Int = 2
    const val REVERTLANDSCAPE: Int = 3

    const val KEY_PUT_NOTY_GROUP: String = "KEY_PUT_NOTY_GROUP"
    const val KEY_SAVE_FOCUS: String = "key_save_focus"


    const val STRING_ACTION_DATA_MOBILE: String = "Data mobile"
    const val STRING_ACTION_WIFI: String = "Wifi"
    const val STRING_ACTION_BLUETOOTH: String = "Bluetooth"
    const val STRING_ACTION_FLASH_LIGHT: String = "Flash light"
    const val STRING_ACTION_SOUND: String = "Sound"
    const val STRING_ACTION_AIRPLANE_MODE: String = "Airplane mode"
    const val STRING_ACTION_DO_NOT_DISTURB: String = "Do not disturb"
    const val STRING_ACTION_LOCATION: String = "Location"
    const val STRING_ACTION_AUTO_ROTATE: String = "Auto-rotate"
    const val STRING_ACTION_HOST_POST: String = "Host post"
    const val STRING_ACTION_SCREEN_CAST: String = "Screen Cast"
    const val STRING_ACTION_OPEN_SYSTEM: String = "Open System"
    const val STRING_ACTION_SYNC: String = "Sync"
    const val STRING_ACTION_CLOCK: String = "Clock"
    const val STRING_ACTION_CAMERA: String = "Camera"
    const val STRING_ACTION_KEYBOARD_PICKER: String = "KeyBroad Picker"
    const val STRING_ACTION_BATTERY: String = "Battery"
    const val STRING_ACTION_VIBRATE: String = "Vibrate"
    const val STRING_ACTION_SILENT: String = "Silent"
    const val STRING_ACTION_NIGHT_LIGHT: String = "Night Light"
    const val STRING_ACTION_SCREEN_LOCK: String = "Screen lock"
    const val STRING_ACTION_SCREEN_RECODING: String = "Screen Recoding"
    const val STRING_ACTION_SCREEN_SHOT: String = "Screen Shot"


    const val RECORD_SCREEN: String = "Record Screen"


    const val INSTANCE_FRAG_CURRENT: String = "Instance Frag Current"
    const val AUTO_OPEN_NOTY_SYSTEM: String = "AUTO_OPEN_NOTY_SYSTEM"
    const val EVENT_HIDE_LOADING_ADS: String = "EVENT_HIDE_LOADING_ADS"


    const val CHANGE_SIM: String = "ACTION_CHANGE_SIM"


    //focus default
    const val CUSTOM: String = "Custom"
    const val SLEEP: String = "Sleep"
    const val WORK: String = "Work"
    const val GAMING: String = "Gaming"
    const val DO_NOT_DISTURB: String = "Do Not Disturb"
    const val DRIVING: String = "Driving"
    const val MINDFULNESS: String = "Mindfulness"
    const val PERSONAL: String = "Personal"
    const val READING: String = "Reading"

    const val LIST_FOCUS_ADDED: String = "list_focus_added"
    const val ACTION_CHECK_PERMISSION: String = "action_check_permission"
    const val ACTION_CHECK_PERMISSION_CONTACT: String = "action_check_permission_contact"


    const val TABLE: String = "FocusIOS"

    //muti adapter people
    const val TYPE_TOP: Int = 0
    const val TYPE_MIDDLE: Int = 1
    const val TYPE_BOTOM: Int = 2

    //type automation
    const val TIME: String = "TIME"
    const val LOCATION: String = "LOCATION"
    const val APPS: String = "APPS"


    //also allow people
    const val EVERY_ONE: Int = 1
    const val NO_ONE: Int = 2
    const val FAVOURITE: Int = 3
    const val ALL_CONTACT: Int = 4

    //path assest
    const val PATH: String = "file:///android_asset/icon/"
    const val PATH_FOLDER_FONT: String = "fontControls/"
    const val PATH_APPEARANCE: String = "file:///android_asset/appearance/"
    const val PATH_ASSET_THEME: String = "file:///android_asset/themes/"


    //serive
    const val INCOMING_CALL: String = "INCOMING_CALL"

    const val PACKAGE_REMOVE: String = "PACKAGE_REMOVE"
    const val TIME_CHANGE: String = "TIME_CHANGE"

    //eventBus
    const val UPDATE_TIME_CHANGE: Int = 1

    //    public static final int UPDATE_LOCATION_CHANGE = 2;
    const val UPDATE_APP_CHANGE: Int = 3
    const val UPDATE_VIEW_FROM_CONTROL: Int = 4
    const val PACKAGE_APP_ADD: Int = 5
    const val CONTACT_CHANGE: Int = 6
    const val CONTACT_DELETE: Int = 7
    const val PACKAGE_APP_REMOVE: Int = 8

    const val TYPE_EVENT_UPDATE_ITEM_BLUR: Int = 9
    const val TYPE_EVENT_UPDATE_ITEM_BLUR_WHEN_SCROLL_VIEWPAGER: Int = 10
    const val TYPE_CHANGE_BITMAP_ITEM_NOTY: Int = 11
    const val EVENT_UPDATE_STATE_VIEW_CONTROL: String = "EVENT_UPDATE_STATE_VIEW_CONTROL"


    const val ADD_FOCUS: String = "add_focus"
    const val SETTING_FOCUS: String = "setting_focus"
    const val ID_FOCUS_SETTING: String = "id_focus_setting"
    const val UPDATE_FOCUS: String = "update_focus"
    const val REQUEST_PERMISSION_LOCATION: String = "request_permission_location"

    //    public static final String REQUEST_PERMISSION_PHONE = "request_permission_phone";
    const val KEY_IS_RATE: String = "key_is_rate"

    //adpter mi shade
    const val TYPE_VIEW_HEADER: Int = 0
    const val TYPE_VIEW_ACTION_SAVE: Int = 1
    const val TYPE_VIEW_ACTION: Int = 3

    //tinyDB
    //    public static final String AUTO_TIME_ON = "AUTO_TIME_ON";
    //    public static final String AUTO_TIME_OFF = "AUTO_TIME_OFF";
    const val FOCUS_START_OLD: String = "FOCUS_START_OLD"

    const val IS_PAUSE_FOCUS: String = "IS_PAUSE_FOCUS"


    //KILL_APP
    //1.FOCUS_DETAIL
    const val ITEM_FOCUS_DETAIL: String = "ITEM_FOCUS_DETAIL"

    //2.EDIT_FOCUS
    const val ITEM_EDIT_FOCUS: String = "ITEM_EDIT_FOCUS"

    //3.CUSTOM_ALLOWED_PEOPLE
    const val ITEM_CUSTOM_ALLOWED_PEOPLE: String = "ITEM_CUSTOM_ALLOWED_PEOPLE"
    const val ITEM_CUSTOM_ALLOWED_PEOPLE_START: String = "ITEM_CUSTOM_ALLOWED_PEOPLE_START"
    const val TEXT_QUERY_CUSTOM_ALLOWED_PEOPLE: String = "TEXT_QUERY_CUSTOM_ALLOWED_PEOPLE"

    //4.ALLOWED_PEOPLE
    const val ITEM_FOCUS_PEOPLE: String = "ITEM_FOCUS_PEOPLE"
    const val TEXT_QUERY_ALLOWED_PEOPLE: String = "TEXT_QUERY_ALLOWED_PEOPLE"

    //5.ALLOWED_APP
    const val ITEM_FOCUS_APP: String = "ITEM_FOCUS_APP"
    const val TEXT_QUERY_ALLOWED_APP: String = "TEXT_QUERY_ALLOWED_APP"

    //6.CUSTOM_ALLOWED_APP
    const val ITEM_CUSTOM_ALLOWED_APP: String = "ITEM_CUSTOM_ALLOWED_APP"
    const val ITEM_CUSTOM_ALLOWED_PEOPLE_INSERT: String = "ITEM_CUSTOM_ALLOWED_PEOPLE_INSERT"
    const val ITEM_CUSTOM_ALLOWED_APP_START: String = "ITEM_CUSTOM_ALLOWED_APP_START"
    const val TEXT_QUERY_CUSTOM_ALLOWED_APP: String = "TEXT_QUERY_CUSTOM_ALLOWED_APP"

    //7.EDIT_LOCATION
    const val ITEM_EDIT_LOCATION_FOCUS: String = "ITEM_EDIT_LOCATION_FOCUS"
    const val ITEM_EDIT_AUTO_LOCATION_FOCUS: String = "ITEM_EDIT_AUTO_LOCATION_FOCUS"

    //8.NEW_AUTOMATION
    const val ITEM_NEW_AUTOMATION: String = "ITEM_NEW_AUTOMATION"

    //9.
    const val ITEM_FOCUS_TIME: String = "ITEM_FOCUS_TIME"
    const val LIST_TIME_REPEAT: String = "LIST_TIME_REPEAT"

    //10.
    const val ITEM_FOCUS_EDIT_TIME: String = "ITEM_FOCUS_EDIT_TIME"
    const val ITEM_EDIT_TIME: String = "ITEM_EDIT_TIME"

    //11
    const val ITEM_FOCUS_NEW_AUTOMATION_APP: String = "ITEM_FOCUS_NEW_AUTOMATION_APP"
    const val ITEM_APP_START: String = "ITEM_APP_START"
    const val FIRST_PERMISSION_USAGE: String = "FIRST_PERMISSION_USAGE"
    const val NEW_AUTO_APP: String = "NEW_AUTO_APP"
    const val EDIT_AUTO_APP: String = "EDIT_AUTO_APP"

    //muti adapter focus_ios
    const val TYPE_TOP_TITLE: Int = 0
    const val TYPE_MID_TITLE: Int = 2
    const val TYPE_APP_PEOPLE: Int = 1
    const val TYPE_ITEM_TURN_ON: Int = 3
    const val SCALE_BITMAP_BLUR: Int = 8


    const val ACTION_GET_LAST_NOTY: String = "ACTION_GET_LAST_NOTY"


    const val VOLUME_CHANGED_ACTION: String = "android.media.VOLUME_CHANGED_ACTION"
    const val EXTRA_VOLUME_STREAM_TYPE: String = "android.media.EXTRA_VOLUME_STREAM_TYPE"
    const val EXTRA_VOLUME_STREAM_VALUE: String = "android.media.EXTRA_VOLUME_STREAM_VALUE"

    const val ITEM_FOCUS_DETAIL_DIALOG: String = "ITEM_FOCUS_DETAIL_DIALOG"
    const val FRAG_CURRENT: String = "FRAG_CURRENT"

    const val LONG_PRESS_TIME_OUT: Int = 400


    const val TYPE_ACCEESSIBILITY: Int = 10
    const val REQUEST_CODE_READ_PHONE_STATE= 1

    const val EDGE_TOP: Int = 0
    const val EDGE_LEFT: Int = 1
    const val EDGE_BOT: Int = 2
    const val EDGE_RIGHT: Int = 3

    const val ACTION_CUSTOMIZE_CONTROL: String = "action_customize_control"
    const val ACTION_START_SPLASH: String = "action_start_splash"
    const val ACTION_COLOR: String = "action_color"
    const val ACTION_EDGE_TRIGGERS: String = "action_edge_triggers"
    const val ACTION_LAYOUT: String = "action_layout"
    const val ACTION_HOME: String = "action_home"

    const val KEY_SHOW_APPEARANCE: String = "key_show_appearance"
    const val KEY_SHOW_APPEARANCE_NEW: String = "key_show_appearance_new"
    const val KEY_HIDE_APPEARANCE_CONTROL_CENTER: String = "key_hide_appearance_control_center"
    const val DEFAULT_VALUE_SHOW_APPEARANCE: Boolean = true
    const val IS_FIRST_TIME_SHOW_LANGUAGE_ACTIVITY = "IS_FIRST_TIME_SHOW_LANGUAGE_ACTIVITY"
    const val IS_ONBOARD_STARED = "IS_ONBOARD_STARED"
    const val IS_FAVORITE_SELECTED = "IS_FAVORITE_SELECTED"

    val UPDATE_KEY_CONTROL_PHASE_9_POLICY: String = "UPDATE_KEY_CONTROL_PHASE_9_POLICY"
    const val TYPE_CONTROL = "TYPE_CONTROL"
    const val POS_MI_CONTROL = 0
    const val POS_IOS = 1
    const val POS_MI_SHADE = 2
    const val TYPE_SINGLE_COLOR = 0
    const val TYPE_GROUP_COLOR = 1
    const val SHAPE_DEFAULT = "shape_default"


    const val ICON_DEFAULT = "ICON_DEFAULT"
    const val NO_POSITION = -1


    //key control center ios
    const val KEY_CONTROL_SETTINGS_4_2 : String = "KEY_CONTROL_SETTINGS_4_2"
    const val KEY_CONTROL_SETTINGS_2_2 : String = "KEY_CONTROL_SETTINGS_2_2"
    const val KEY_CONTROL_IOS_TOP_2_2 : String = "KEY_CONTROL_IOS_TOP_2_2"
    const val KEY_CONTROL_MUSIC_IOS_TOP_2_2 : String = "KEY_CONTROL_MUSIC_IOS_TOP_2_2"
    const val KEY_CONTROL_SETTINGS_2_2_TEXT : String = "KEY_CONTROL_SETTINGS_2_2_TEXT"
    const val KEY_CONTROL_SETTINGS_4_1 : String = "KEY_CONTROL_SETTINGS_4_1"
    const val KEY_CONTROL_SETTINGS_2_2_TEXT_2 : String = "KEY_CONTROL_SETTINGS_2_2_TEXT_2"
    const val KEY_CONTROL_ROTATE : String = "KEY_CONTROL_ROTATE"
    const val KEY_CONTROL_ROTATE_RECTANGLE : String = "KEY_CONTROL_ROTATE_RECTANGLE"
    const val KEY_CONTROL_SILENT : String = "KEY_CONTROL_SILENT"
    const val KEY_CONTROL_SILENT_21 : String = "KEY_CONTROL_SILENT_21"
    const val KEY_CONTROL_SCREEN_TIME_OUT : String = "KEY_CONTROL_SCREEN_TIME_OUT"
    const val KEY_CONTROL_SCREEN_TIME_OUT_SQUARE : String = "KEY_CONTROL_SCREEN_TIME_OUT_SQUARE"
    const val KEY_CONTROL_VOLUME_PORTRAIT : String = "KEY_CONTROL_VOLUME_PORTRAIT"
    const val KEY_CONTROL_VOLUME_LANDSCAPE : String = "KEY_CONTROL_VOLUME_LANDSCAPE"
    const val KEY_CONTROL_VOLUME_LANDSCAPE_2 : String = "KEY_CONTROL_VOLUME_LANDSCAPE_2"
    const val KEY_CONTROL_VOLUME_LANDSCAPE_THUMB : String = "KEY_CONTROL_VOLUME_LANDSCAPE_THUMB"
    const val KEY_CONTROL_BRIGHTNESS_PORTRAIT : String = "KEY_CONTROL_BRIGHTNESS_PORTRAIT"
    const val KEY_CONTROL_BRIGHTNESS_LANDSCAPE : String = "KEY_CONTROL_BRIGHTNESS_LANDSCAPE"
    const val KEY_CONTROL_BRIGHTNESS_LANDSCAPE_2 : String = "KEY_CONTROL_BRIGHTNESS_LANDSCAPE_2"
    const val KEY_CONTROL_BRIGHTNESS_LANDSCAPE_THUMB : String = "KEY_CONTROL_BRIGHTNESS_LANDSCAPE_THUMB"
    const val KEY_CONTROL_MUSIC_4_1 : String = "KEY_CONTROL_MUSIC_4_1"
    const val KEY_CONTROL_MUSIC_4_2 : String = "KEY_CONTROL_MUSIC_4_2"
    const val KEY_CONTROL_MUSIC_2_2 : String = "KEY_CONTROL_MUSIC_2_2"
    const val KEY_CONTROL_ROTATE_RECORD_FLASHT_DARKMODE : String = "KEY_CONTROL_ROTATE_RECORD_FLASHT_DARKMODE"
    const val KEY_CONTROL_AIRPLANE_RECORD_SYNDATA : String = "KEY_CONTROL_AIRPLANE_RECORD_SYNDATA"
    const val KEY_CONTROL_SYNDATA_TEXT : String = "KEY_CONTROL_SYNDATA_TEXT"
    const val KEY_CONTROL_AIRPLANE_TEXT : String = "KEY_CONTROL_AIRPLANE_TEXT"
    const val KEY_CONTROL_FOCUS : String = "KEY_CONTROL_FOCUS"
    const val KEY_CONTROL_ALARM : String = "KEY_CONTROL_ALARM"
    const val KEY_CONTROL_ALARM_TEXT : String = "KEY_CONTROL_ALARM_TEXT"
    const val KEY_CONTROL_CALCULATOR : String = "KEY_CONTROL_CALCULATOR"
    const val KEY_CONTROL_CALCULATOR_TEXT : String = "KEY_CONTROL_CALCULATOR_TEXT"
    const val KEY_CONTROL_CAMERA : String = "KEY_CONTROL_CAMERA"
    const val KEY_CONTROL_CAMERA_TEXT : String = "KEY_CONTROL_CAMERA_TEXT"
    const val KEY_CONTROL_RECORD : String = "KEY_CONTROL_RECORD"
    const val KEY_CONTROL_RECORD_TEXT : String = "KEY_CONTROL_RECORD_TEXT"
    const val KEY_CONTROL_DARKMODE : String = "KEY_CONTROL_DARKMODE"
    const val KEY_CONTROL_DARKMODE_TEXT : String = "KEY_CONTROL_DARKMODE_TEXT"
    const val KEY_CONTROL_PIN : String = "KEY_CONTROL_PIN"
    const val KEY_CONTROL_PIN_TEXT : String = "KEY_CONTROL_PIN_TEXT"
    const val KEY_CONTROL_FLASH : String = "KEY_CONTROL_FLASH"
    const val KEY_CONTROL_ADD : String = "KEY_CONTROL_ADD"
    const val KEY_CONTROL_FLASH_TEXT : String = "KEY_CONTROL_FLASH_TEXT"
    const val KEY_CONTROL_NOTE : String = "KEY_CONTROL_NOTE"
    const val KEY_CONTROL_NOTE_TEXT : String = "KEY_CONTROL_NOTE_TEXT"
    const val KEY_CONTROL_OPEN_APP : String = "KEY_CONTROL_OPEN_APP"
    const val STRING_ICON_SHADE_1 : String = "icon_shade_1.webp"

     const val KEY_IS_DELETE_FOCUS = "KEY_IS_DELETE_FOCUS"
     const val KEY_IS_DELETE_THEME_CONTROL = "KEY_IS_DELETE_THEME_CONTROL"

    //SharedPreferences
    const val PREF_SETTING_LANGUAGE: String = "pref_setting_language"
    const val ENGLISH_LANGUAGE_CODE = "en"
    const val ARABIC_LANGUAGE_CODE = "ar"
    const val GERMAN_LANGUAGE_CODE = "de"
    const val SPANISH_LANGUAGE_CODE = "es"
    const val PHILIPPINE_LANGUAGE_CODE = "fil"
    const val FRENCH_LANGUAGE_CODE = "fr"
    const val HINDI_LANGUAGE_CODE = "hi"
    const val INDONESIA_LANGUAGE_CODE = "in"
    const val JAPAN_LANGUAGE_CODE = "ja"
    const val KOREA_LANGUAGE_CODE = "ko"
    const val PORTUGAL_LANGUAGE_CODE = "pt"
    const val RUSSIAN_LANGUAGE_CODE = "ru"
    const val TURKEY_LANGUAGE_CODE = "tr"
    const val VIETNAM_LANGUAGE_CODE = "vi"
    const val ACTION_GO_TO_HOME_FRAGMENT = "ACTION_GO_TO_HOME_FRAGMENT"
    const val CHANGE_LANGUAGE = "CHANGE_LANGUAGE"

    //Type ads
    const val TYPE_ADS_1 = 1L
    const val TYPE_ADS_2 = 2L
    const val TYPE_ADS_3 = 3L
    const val TYPE_PREVIEW_THEMECONTROL_1 = 1L
    const val TYPE_CHOOSE_STYLE_1 = 1L
    const val TYPE_ADS_REQUEST_PERMISSION = "type_ads_request_permission"
    const val TYPE_PREVIEW_THEME_CONTROL = "type_preview_theme_control"
    const val TYPE_CHOOSE_STYLE = "type_choose_style"
    const val TYPE_ADS_STORE_WALLPAPER : String = "type_ads_store_wallpaper"
    const val ACCESSIBILITY_PERMISSION = "ACCESSIBILITY_PERMISSION"
    const val OVERDRAW_PERMISSION = "OVERDRAW_PERMISSION"
    const val NOTIFICATION_PERMISSION = "NOTIFICATION_PERMISSION"
    const val IS_ALLOW_SHOW_BOTTOM_SHEET = "IS_ALLOW_SHOW_BOTTOM_SHEET"
    const val FINISH = "FINISH"

    // favorite theme
    const val BASIC = "Basic"
    const val ELEGANT = "Elegant"
    const val CUTE = "Cute"
    const val ANIME = "Anime"
    const val PET = "Pet"
    const val MINIMAL = "Minimal"
    const val FAVORITE_THEMES = "favorite_themes"

    const val UPDATE_THEME_6000_VER_1_2= "UPDATE_THEME_6000_VER_1_2"
}
