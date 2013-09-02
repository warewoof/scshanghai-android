package com.warewoof.shanghai;

import android.graphics.Color;
import android.os.Environment;



public class GlVar {
	
	/* RELEASE CHECKLIST
	 * 1. Check the catalog variables in this section
	 * 2. Set Android Manifest version info
	 * 3. Check mapView.layout API key
	 * 4. Update version.xml file
	 * 5. Double check updated fixes & feature, remove debug and hard code
	 * 6. Check Catalog.XML last change date */
	public static final boolean RELEASE_MODE = true;
	public static final boolean CATALOG_USE_INTERNAL_MEMORY = true;
	public static final String CATALOG_DOWNLOAD_URL = "http://www.warewoof.com/scs/release/";
	//public static final String CATALOG_DOWNLOAD_URL = "http://www.warewoof.com/scs/dev/";
	public static final String CATALOG_LAST_UPDATE = "2013-09-10 00:00:00";  // set this to the date the APK is posted to Play
	/* Finish checking section */
	
	
	/** Catalog file local and host settings */
	public static final String CATALOG_DOWNLOAD_FILE_NAME = "scs.zip";  //must match server package
	public static final String CATALOG_LOCAL_FILE_NAME = "catalog.xml"; //must match server packaged xml file name 
	public static final String CATALOG_LOCAL_DATA_DIRECTORY = Environment.getExternalStorageDirectory()+"/Android/data/com.warewoof.shanghai/files/data/";
	public static final String CATALOG_LOCAL_DIRECTORY = Environment.getExternalStorageDirectory()+ "/Android/data/com.warewoof.shanghai/files/";

	/** XML Directory Data Resource used in ParseXml Class */ 	
	public static final String CATALOG_PARENT_NAME = "Simple City Shanghai";  // matches catalog <directory name="________">
	public static final String CATALOG_TOC_NAME = "Shanghai";  // matches catalog <toc name="________">	   
	public static final String CHILDREN_SECTION = "children";
	public static final String CHILD_NODE_TAG = "child";
	public static final String CHILD_META_TAG = "(meta)";
	public static final String CHILD_LINK_TAG = "(link)";
	public static final String CHILD_IMAGE_TAG = "(image)";
	public static final int XML_FILE = R.xml.catalog;

	/** splash page attributes section */	
	public static final int SPLASH_BACKGROUND_COLOR = 0xff222222;
	public static final int SPLASH_SCREEN_DELAY = 700; // in milliseconds
	public static final int SPLASH_SCREEN_TIMEOUT = 2000; // in milliseconds
	
	/** Map view attributes section */
	public static final String MAP_SOURCE_PREFIX = "http://ditu.google.cn/maps?q=";	//alternate option  "http://maps.google.com/maps?q=";
	public static final int MAP_INITIAL_ZOOM_LEVEL = 14;
	public static final int MAP_MAX_DISTANCE_SPAN = 500000; //in meters, set 0 to turn off
	public static final int MAP_MAXOVERLAYITEMS = 1000; 
	
	/** Framebar section */
	public static boolean SPACER_VISIBLE = true;
	public static final int SPACER_COLOR = Color.GREEN;
	public static final int SPACER_COLOR_HIDE_CODE = 0x00000001;

	/** child page attributes section	 */
	public static final int CHILD_TEXT_COLOR = 0xff333333;
	public static float CHILD_SUBSECTION_TITLE_TEXT_SIZE;
	public static final int CHILD_SUBSECTION_TITLE_TEXT_DEFAULT_SIZE = 12;
	public static final int CHILD_SUBSECTION_TITLE_TEXT_COLOR = 0xff000000;
	public static final boolean CHILD_SUBSECTION_TITLE_TEXT_ALLCAPS = true;
	public static final int CHILD_SUBSECTION_VALUE_TEXT_DEFAULT_SIZE = 18;
	public static final int CHILD_SUBSECTION_VALUE_TEXT_COLOR = 0xff333333;
	public static float CHILD_SUBSECTION_IMAGE_TEXT_DEFAULT_SIZE = 13;
	public static final int CHILD_SUBSECTION_ORPHAN_TEXT_COLOR = 0xff999999;
	public static final int CHILD_SUBSECTION_HIGHLIGHT_AOI_COLOR = 0xffffff77;
	public static final int FRAME_DIVIDER_WIDTH = 1;  // in px, need to convert to dp
	public static final int CHILD_LINK_COLOR = 0xff4949cc;
	public static final int CHILD_FRAME_MAP_ICON = R.drawable.ic_menu_mapmode;
	public static final int CHILD_FRAME_SHARE_ICON = R.drawable.ic_menu_share;
	public static final int CHILD_FRAME_BG = R.drawable.frame_header_gradient_background; //R.drawable.list_header_bg;

	/** child text options, case will be ignored */
	public static final String OPTION_IMAGEVIEWER = "linkimage";
	public static final String OPTION_FONT = "font:";

	/** taxi card options */
	public static final int TAXI_CARD_TEXT_DEFAULT_SIZE = 40;
	public static final int TAXI_CARD_TEXT_COLOR = 0xff000000;
		
	/** list page attributes section	 */
	public static final int LIST_TEXT_COLOR = 0xff222222;
	public static final int LIST_BACKGROUND_COLOR = 0xffECECEC;
	public static final int LIST_NEXT_ARROW_COLOR = 0xffb8b8b8;
	public static final float LIST_TEXT_SIZE = 20;
	public static final float LIST_NEXT_ARROW_SIZE = 20;
	public static final int LIST_FRAME_TEXT_COLOR = 0xfff6f6f6; //0xff111111;
	public static final int LIST_FRAME_BG = R.drawable.frame_header_gradient_background;; //R.drawable.list_header_bg;




	/** Intent parameter names */ 
	public static final String INTENT_EXTRA_PARENT_NAME_VAR = "IveseenthingsyoupeoplewoudntBelieve";
	public static final String INTENT_EXTRA_SEARCH_NAME_VAR = "attackshipsonfire";
	public static final String INTENT_EXTRA_SECTION_NAME_VAR = "offtheshoulderoforion";
	public static final String INTENT_EXTRA_IS_CHILD_BOOL = "iwatchedcbeamsglitter";
	public static final String INTENT_EXTRA_CHILD_TAB_COLOR = "inthedark";
	public static final String INTENT_EXTRA_TITLE_VAR = "nearthetannhausergate";
	public static final String INTENT_EXTRA_OBJECT_VAR = "allthosemoments";
	public static final String INTENT_EXTRA_OBJECT_KEY = "willbelostintime";
	public static final String INTENT_EXTRA_NO_BACK_BOOL = "liketearsintherain";
	public static final String INTENT_EXTRA_SEARCH_SECTION_PARENT_VAR = "timetodie";
	public static final String INTENT_EXTRA_SEARCH_SECTION_VAR = "thefactsoflife";
	public static final String INTENT_EXTRA_SEARCH_STRING_VAR = "tomakeanalteration";
	public static final String INTENT_EXTRA_FRAME_HEIGHT_STRING_VAR = "intheinvolvementofanorganic";
	public static final String INTENT_EXTRA_GEO_COORD_STRING_VAR = "lifesystemisfatal";
	public static final String INTENT_EXTRA_GEO_ADDY_STRING_VAR = "acodingsequence";
	public static final String INTENT_EXTRA_GEO_NAME_STRING_VAR = "cannotberevised";
	public static final String INTENT_EXTRA_SHARE_DISABLE_BOOL = "onceitsbeenestablished";
	public static final String INTENT_EXTRA_SEARCH_DISABLE_BOOL = "beacasebytheseconddayofincubation";
	public static final String INTENT_EXTRA_MAP_DISABLE_BOOL = "anycellsthathaveundergone";
	public static final String INTENT_EXTRA_MAP_REQUEST_FROM_CHILD = "revisionmutationgiverise";
	public static final String INTENT_TYPE_CALENDAR = "torevertamtcolanies";
	public static final String INTENT_EXTRA_CALENDAR_LOCAL_FILE = "likeratsleavingasinkingship";
	public static final String INTENT_EXTRA_CALENDAR_REMOTE_FILE = "thentheship";
	public static final String INTENT_EXTRA_CALENDAR_EVENTS_UPDATED = "sinksallthewaydown";
	public static final String INTENT_EXTRA_TAXI_CARD_STRING = "itiswhatitis";
	public static final String INTENT_EXTRA_HIGHLIGHT_AOI = "highlightaoidangit";
	




}