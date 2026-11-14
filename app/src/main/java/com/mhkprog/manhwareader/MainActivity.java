package com.mhkprog.manhwareader;

import android.app.Activity;
import android.app.AlertDialog;
import android.animation.ObjectAnimator;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineBackgroundSpan;
import android.text.Layout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowInsets;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.math.BigDecimal;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.util.LruCache;

/** Native Android checklist app. It intentionally uses platform Views only. */
public final class MainActivity extends Activity {
    static final int REQUEST_FOLDER = 41;
    private static final int REQUEST_COVER = 42;
    private static final int BG_DARK = Color.rgb(9, 9, 9);
    private static final int SURFACE_DARK = Color.rgb(20, 20, 20);
    private static final int SURFACE_ALT_DARK = Color.rgb(17, 17, 17);
    private static final int BLUE = Color.rgb(90, 169, 255);
    private static final int GREEN = Color.rgb(86, 201, 135);
    private static final int RED = Color.rgb(255, 76, 76);
    private static final int[] FLAT_ACCENT_COLORS = {
            Color.rgb(187, 134, 252), Color.rgb(79, 195, 247), Color.rgb(77, 182, 172),
            Color.rgb(129, 199, 132), Color.rgb(255, 183, 77), Color.rgb(255, 138, 101),
            Color.rgb(240, 98, 146)
    };
    private static final String[] FLAT_ACCENT_NAMES = {"بنفش", "آبی", "فیروزه‌ای", "سبز", "کهربایی", "مرجانی", "صورتی"};
    private static final int[][] GRADIENT_COLORS = {
            {0xFFFAD95C, 0xFFF9C251, 0xFFFFA270, 0xFFFD80A4, 0xFFFE66CD, 0xFFFE59D2},
            {0xFF030712, 0xFF040917, 0xFF050D1D, 0xFF071225, 0xFF0A172D, 0xFF0D1D36, 0xFF10233F, 0xFF122946, 0xFF142F4E, 0xFF163556, 0xFF173C5E, 0xFF184366, 0xFF194B6D, 0xFF185274, 0xFF175A7A, 0xFF15617F, 0xFF146984, 0xFF147189, 0xFF167A8E, 0xFF1A8395, 0xFF1B8D9D, 0xFF1A98A6, 0xFF1CA4AF, 0xFF23B0B8, 0xFF32BBC1, 0xFF47C6CA, 0xFF62D1D3, 0xFF7FDCDD, 0xFF9BE7E8, 0xFFB5F1F1, 0xFFCAF9F9, 0xFFD9FFFF},
            {0xFFA8CBFF, 0xFFB8CBFE, 0xFFD2CBFF, 0xFFF2C9F9, 0xFFFEC6F2, 0xFFFFC1EC, 0xFFFDBAEC},
            {0xFF0FE6DC, 0xFF45DDE1, 0xFF53D1E2, 0xFF33C2E3, 0xFF05B2E6, 0xFF09A0EA, 0xFF008EEC, 0xFF007CEB, 0xFF3068F1, 0xFF4B58F2},
            {0xFF02380C, 0xFF004D02, 0xFF007103, 0xFF00A236, 0xFF00D975},
            {0xFF13BFDC, 0xFF12C0DB, 0xFF14C3D9, 0xFF18C5D6, 0xFF1FC8D3, 0xFF27CAD0, 0xFF2ECDCC, 0xFF35D0C9, 0xFF3AD2C7, 0xFF3DD5C4, 0xFF3FD8C2, 0xFF41DBC0, 0xFF43DEBE, 0xFF48E0BB, 0xFF4EE3B8, 0xFF57E5B4, 0xFF62E6AF, 0xFF70E7AA, 0xFF7DE8A5, 0xFF8BE8A0, 0xFF97E79C, 0xFFA3E798, 0xFFADE794, 0xFFB7E790, 0xFFC0E78B, 0xFFC9E785, 0xFFD3E77E, 0xFFDDE677, 0xFFE7E66F, 0xFFF0E568, 0xFFF7E463, 0xFFFDE35F},
            {0xFFFEFFD2, 0xFFFFF7AA, 0xFFFEBF90, 0xFFFF8271, 0xFFF84354, 0xFFE10C3A, 0xFFCB0522, 0xFFB2010B},
            {0xFF92D5B7, 0xFF8FD6BC, 0xFF8CD7C3, 0xFF8AD8CB, 0xFF89DAD3, 0xFF89DADC, 0xFF8ADBE5, 0xFF8EDBED, 0xFF92DBF4, 0xFF99DAFA, 0xFFA1D9FF, 0xFFAED7FF, 0xFFB8D6FF, 0xFFC1D5FF, 0xFFC9D4FF, 0xFFD0D3FF, 0xFFD7D3FF, 0xFFDDD2FF, 0xFFE3D1FF, 0xFFE9D0FF, 0xFFF0CFFF, 0xFFF7CDFF, 0xFFFDCDFC, 0xFFFFD0F5, 0xFFFFD3EE, 0xFFFFD7EA, 0xFFFFDBE6, 0xFFFFDEE4, 0xFFFFE1E4, 0xFFFFE4E3, 0xFFFFE7E4, 0xFFFFE8E4},
            {0xFF030C44, 0xFF034C6B, 0xFF008695, 0xFF00B6C3, 0xFF05D8EC},
            {0xFF3D6FF6, 0xFF7277FC, 0xFFAA63DA, 0xFFD73394, 0xFFF70026, 0xFFFD2100},
            {0xFF2C0090, 0xFF4D0BAA, 0xFF7927EC, 0xFFAA46FF, 0xFFD55DF0, 0xFFF677B0, 0xFFFF8F92, 0xFFFDA6B5},
            {0xFFFAD95C, 0xFFFAD65A, 0xFFF9D358, 0xFFF8D056, 0xFFF8CC53, 0xFFF8C751, 0xFFF8C351, 0xFFFABE52, 0xFFFBB956, 0xFFFCB45B, 0xFFFDAF61, 0xFFFEAA68, 0xFFFEA46E, 0xFFFF9F74, 0xFFFF9A7C, 0xFFFF9484, 0xFFFF8E8D, 0xFFFF8896, 0xFFFE839F, 0xFFFD7FA7, 0xFFFD7AAF, 0xFFFD76B6, 0xFFFE71BD, 0xFFFE6CC4, 0xFFFE68C9, 0xFFFE65CE, 0xFFFD62D1, 0xFFFD60D2, 0xFFFD5ED3, 0xFFFD5CD2, 0xFFFE5AD2, 0xFFFE59D2},
            {0xFF02ADED, 0xFF03AFED, 0xFF08B2ED, 0xFF10B5ED, 0xFF16B8EE, 0xFF1BBCEE, 0xFF1DC1EE, 0xFF1EC5EE, 0xFF1BCAEE, 0xFF15CFED, 0xFF0ED5EC, 0xFF09DCEB, 0xFF0DE2E8, 0xFF16E8E5, 0xFF21EDE2, 0xFF2AF2DE, 0xFF30F5DA, 0xFF33F8D7, 0xFF35FAD3, 0xFF34FCCF, 0xFF33FECB, 0xFF34FFC7, 0xFF3FFFC3, 0xFF45FFBF, 0xFF47FFBA, 0xFF40FFB5, 0xFF33FFAE, 0xFF32FDA9, 0xFF30FCA4, 0xFF2EFBA0, 0xFF2DF99C, 0xFF2DF999},
            {0xFF414FD7, 0xFF4349DC, 0xFF453FE1, 0xFF4931E7, 0xFF5122EC, 0xFF5C17EE, 0xFF690FED, 0xFF7711EB, 0xFF8619E6, 0xFF9725DF, 0xFFA834D6, 0xFFB945CD, 0xFFC955C2, 0xFFDA63B6, 0xFFEA72A7, 0xFFF78495, 0xFFFE9888, 0xFFFFAC82, 0xFFFFBA81, 0xFFFFC681, 0xFFFFCF7F, 0xFFFFD479, 0xFFFFD670, 0xFFFED267, 0xFFF7CB75, 0xFFEEC282, 0xFFE4B689, 0xFFDDA78A, 0xFFD4918D, 0xFFC27C99, 0xFFA66EA9, 0xFF8C68B1},
            {0xFFFAD95C, 0xFFFAD65A, 0xFFF9D358, 0xFFF8D056, 0xFFF8CC53, 0xFFF8C751, 0xFFF8C351, 0xFFFABE52, 0xFFFBB956, 0xFFFCB45B, 0xFFFDAF61, 0xFFFEAA68, 0xFFFEA46E, 0xFFFF9F74, 0xFFFF9A7C, 0xFFFF9484, 0xFFFF8E8D, 0xFFFF8896, 0xFFFE839F, 0xFFFD7FA7, 0xFFFD7AAF, 0xFFFD76B6, 0xFFFE71BD, 0xFFFE6CC4, 0xFFFE68C9, 0xFFFE65CE, 0xFFFD62D1, 0xFFFD60D2, 0xFFFD5ED3, 0xFFFD5CD2, 0xFFFE5AD2, 0xFFFE59D2},
            {0xFF51C5FD, 0xFF50BEFF, 0xFF54B5FF, 0xFF5BA9FF, 0xFF639CFF, 0xFF6D8FFF, 0xFF7883FF, 0xFF8278FE, 0xFF8E6DFC, 0xFF9963F9, 0xFFA459F5, 0xFFAF50EF, 0xFFBA47E8, 0xFFC43FDF, 0xFFD038D6, 0xFFDB33CD, 0xFFE52FC4, 0xFFEF2BBA, 0xFFF629B0, 0xFFFC27A5, 0xFFFE269B, 0xFFFF2791, 0xFFFF2988, 0xFFFE2C80, 0xFFFE3078, 0xFFFE356F, 0xFFFF3C66, 0xFFFF455D, 0xFFFF4F55, 0xFFFF574D, 0xFFFF5D47, 0xFFFF6142},
            {0xFF0FE6DC, 0xFF20E4DD, 0xFF31E2DF, 0xFF40DEE0, 0xFF4BDBE1, 0xFF53D7E2, 0xFF55D4E2, 0xFF53D0E2, 0xFF4DCCE2, 0xFF43C8E2, 0xFF38C3E3, 0xFF2ABFE4, 0xFF1BBAE5, 0xFF0EB5E5, 0xFF04B0E7, 0xFF01ABE8, 0xFF03A6E9, 0xFF08A1EA, 0xFF0C9CEB, 0xFF0C97EB, 0xFF0492EC, 0xFF008CEB, 0xFF0087EA, 0xFF0082EA, 0xFF007DEB, 0xFF0177EF, 0xFF1872F0, 0xFF286CF1, 0xFF3566F2, 0xFF3F60F2, 0xFF465BF2, 0xFF4B58F2},
            {0xFFEED01A, 0xFFF1CB16, 0xFFF5C517, 0xFFF8BD1D, 0xFFFBB425, 0xFFFDAC2D, 0xFFFEA331, 0xFFFF9B33, 0xFFFF9238, 0xFFFF883C, 0xFFFF7D40, 0xFFFF7344, 0xFFFF6848, 0xFFFF5D4B, 0xFFFE5450, 0xFFFD4A53, 0xFFFC4056, 0xFFFA3659, 0xFFF72C5D, 0xFFF32262, 0xFFEE1A66, 0xFFE81469, 0xFFE2106C, 0xFFDB0C6F, 0xFFD40972, 0xFFCC0576, 0xFFC3017A, 0xFFB9007E, 0xFFAF0082, 0xFFA50085, 0xFF9D0087, 0xFF970088},
            {0xFF0042F8, 0xFF0043FC, 0xFF0147FF, 0xFF064DFF, 0xFF0D54FF, 0xFF155BFF, 0xFF1D61FF, 0xFF2668FE, 0xFF2E6EFE, 0xFF3774FE, 0xFF3F7AFE, 0xFF487FFE, 0xFF5285FE, 0xFF5B8AFE, 0xFF6590FE, 0xFF6F96FF, 0xFF799BFF, 0xFF82A1FF, 0xFF8AA6FE, 0xFF92ABFE, 0xFF99B0FE, 0xFF9FB5FE, 0xFFA5B9FE, 0xFFAABEFE, 0xFFAFC2FE, 0xFFB4C6FE, 0xFFB8CAFE, 0xFFBCCDFE, 0xFFC0D0FF, 0xFFC3D3FF, 0xFFC6D5FF, 0xFFC9D7FF},
            {0xFF000A53, 0xFF000C56, 0xFF010E5A, 0xFF01105E, 0xFF011262, 0xFF011567, 0xFF01176C, 0xFF011A72, 0xFF021D78, 0xFF02207E, 0xFF022384, 0xFF03268A, 0xFF032991, 0xFF042C97, 0xFF042F9D, 0xFF0432A3, 0xFF0535A9, 0xFF0537AF, 0xFF053AB5, 0xFF063DBC, 0xFF0640C3, 0xFF0643CA, 0xFF0646D0, 0xFF0549D7, 0xFF054CDE, 0xFF054FE4, 0xFF0451EB, 0xFF0454F0, 0xFF0356F6, 0xFF0358FB, 0xFF025AFF, 0xFF085DFF}
    };
    private static final String[] GRADIENT_NAMES = {
            "غروب", "شب و فیروزه", "یخی", "اقیانوس آبی", "جنگل", "بهار", "آتش", "نعنا", "دریای عمیق", "طلوع", "یاسی",
            "غروب مورب", "آب و سبز", "نیلی", "کهربا و صورتی", "ساحل", "موج آبی", "کهربا", "آسمان", "آبی شب"
    };
    private static final GradientDrawable.Orientation[] GRADIENT_ORIENTATIONS = {
            GradientDrawable.Orientation.BL_TR, GradientDrawable.Orientation.TOP_BOTTOM,
            GradientDrawable.Orientation.TOP_BOTTOM, GradientDrawable.Orientation.BL_TR,
            GradientDrawable.Orientation.TR_BL, GradientDrawable.Orientation.BL_TR,
            GradientDrawable.Orientation.TL_BR, GradientDrawable.Orientation.TL_BR,
            GradientDrawable.Orientation.TOP_BOTTOM, GradientDrawable.Orientation.TOP_BOTTOM,
            GradientDrawable.Orientation.BOTTOM_TOP, GradientDrawable.Orientation.TL_BR,
            GradientDrawable.Orientation.LEFT_RIGHT, GradientDrawable.Orientation.BL_TR,
            GradientDrawable.Orientation.LEFT_RIGHT, GradientDrawable.Orientation.LEFT_RIGHT,
            GradientDrawable.Orientation.BL_TR, GradientDrawable.Orientation.TOP_BOTTOM,
            GradientDrawable.Orientation.TL_BR, GradientDrawable.Orientation.TL_BR
    };
    private static final int BUILTIN_THEME_COUNT = FLAT_ACCENT_COLORS.length + GRADIENT_COLORS.length;
    private static final int MAX_GENRE_WEIGHT = 50;
    private static final String VERSION = "1.4.32";
    private static final Pattern CHAPTER_NUMBER_PATTERN = Pattern.compile(
            "(?i)^(?:chapter|ch)?\\s*([0-9]+(?:\\.[0-9]+)?)(?:\\b.*)?$");
    private static final Pattern ANIME_SEASON_RANGE_PATTERN = Pattern.compile(
            "(?i)^(?:S(?:EASON)?\\s*)?([0-9]+)\\s*[:：,]\\s*([0-9]+)\\s*(?:-|TO)\\s*([0-9]+)$");

    private int BG = BG_DARK;
    private int SURFACE = SURFACE_DARK;
    private int SURFACE_ALT = SURFACE_ALT_DARK;
    private int PURPLE = FLAT_ACCENT_COLORS[0];
    private int TEXT = Color.rgb(241, 241, 241);
    private int MUTED = Color.rgb(165, 165, 165);
    private boolean lightTheme;
    private int accentIndex;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Set<String> activeGenreFilter = new HashSet<>();
    private final Set<String> selectedManhwaIds = new LinkedHashSet<>();
    private final Set<String> selectedChapterKeys = new LinkedHashSet<>();
    private final Set<Long> seenRecommendationIds = new HashSet<>();
    private final Set<String> favoriteGenrePreferences = new LinkedHashSet<>();
    private final Set<String> favoriteManhwaIds = new LinkedHashSet<>();
    private final Set<String> dislikedManhwaIds = new LinkedHashSet<>();
    private final Set<Long> seenAnimeRecommendationIds = new HashSet<>();
    private final Set<String> favoriteAnimeGenrePreferences = new LinkedHashSet<>();
    private final Set<String> favoriteAnimeIds = new LinkedHashSet<>();
    private final Set<String> dislikedAnimeIds = new LinkedHashSet<>();
    private final Set<String> dislikedGenrePreferences = new LinkedHashSet<>();
    private final Set<String> dislikedAnimeGenrePreferences = new LinkedHashSet<>();
    private final Set<String> selectedTagNames = new LinkedHashSet<>();
    private final ManhwaRecommendationEngine.UserPreferences recommendationPreferences =
            new ManhwaRecommendationEngine.UserPreferences();
    private final ManhwaRecommendationEngine recommendationEngine =
            new ManhwaRecommendationEngine(recommendationPreferences);
    private final ManhwaRecommendationEngine.UserPreferences animeRecommendationPreferences =
            new ManhwaRecommendationEngine.UserPreferences();
    private final ManhwaRecommendationEngine animeRecommendationEngine =
            new ManhwaRecommendationEngine(animeRecommendationPreferences);
    private final ExecutorService imageExecutor = Executors.newSingleThreadExecutor();
    private final LruCache<String, Bitmap> coverCache = new LruCache<String, Bitmap>(coverCacheBudgetKb()) {
        @Override protected int sizeOf(String key, Bitmap value) { return Math.max(1, value.getByteCount() / 1024); }
    };
    private ManhwaStorage storage;
    private Typeface vazir;
    private JSONObject library = new JSONObject();
    private String page = "dashboard";
    private String manhwaInfoReturnPage = "allManhwas";
    private String chaptersReturnPage = "allManhwas";
    private String activeManhwaId;
    private boolean animeMode;
    private String manhwaInfoScrollItemId;
    private int manhwaInfoScrollY;
    private String chapterScrollItemId;
    private int chapterScrollY;
    private boolean drawerOpen;
    private boolean returnToSettingsAfterPicker;
    private String activeReorderType;
    private String activeReorderSourceId;
    private String lastReorderTargetId;
    private String reorderOriginalLibrary;
    private boolean reorderChanged;
    private View coverRemoveButton;
    private ManhwaJsonImportTarget activeManhwaJsonImportTarget;
    private FrameLayout systemRoot;
    private LinearLayout appColumn;
    private FrameLayout drawerLayer;
    private View drawerScrim;
    private LinearLayout drawerPanel;
    private LinearLayout drawerSettingsOptions;
    private NativeIconView drawerSettingsChevron;
    private ValueAnimator drawerSettingsAnimator;
    private boolean drawerSettingsExpanded;
    private TextView titleView;
    private TextView subtitleView;
    private EditText searchField;
    private FrameLayout genreFilterButton;
    private LinearLayout managedTagRows;
    private EditText managedTagSearch;
    private GridView manhwasGrid;
    private GridView chaptersGrid;
    private ManhwaAdapter manhwaAdapter;
    private ChapterAdapter chapterAdapter;
    private ScrollView chapterSeasonScroll;
    private ScrollView manhwaInfoScroll;
    private ScrollView settingsScroll;
    private TextView toastView;
    private FrameLayout splashOverlay;
    private ProgressBar splashProgress;
    private ObjectAnimator splashArtAnimator;
    private ImageView manhwaCoverPreview;
    private String pendingCoverName;
    private boolean restoringChapterUndo;
    private boolean lastUndoWasTyping;
    private long lastUndoAt;
    private int lastUndoEnd = -1;
    private SharedPreferences recommendationState;
    private SharedPreferences animeRecommendationState;
    private OnBackInvokedCallback systemBackCallback;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 33) {
            systemBackCallback = this::handleBack;
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT, systemBackCallback);
        }
        vazir = Typeface.createFromAsset(getAssets(), "fonts/Vazirmatn-Regular.ttf");
        storage = new ManhwaStorage(this);
        animeMode = getSharedPreferences("app_settings", MODE_PRIVATE).getBoolean("anime_mode", false);
        recommendationState = getSharedPreferences("recommendations", MODE_PRIVATE);
        animeRecommendationState = getSharedPreferences("anime_recommendations", MODE_PRIVATE);
        loadRecommendationState(false);
        loadRecommendationState(true);
        android.content.SharedPreferences appearance = getSharedPreferences("appearance", MODE_PRIVATE);
        lightTheme = appearance.getBoolean("light", false);
        accentIndex = Math.max(0, Math.min(themeCount() - 1, appearance.getInt("accent", 0)));
        if (appearance.getBoolean("random_each_launch", false)) {
            String defaultPool = appearance.getBoolean("random_each_launch", false) ? "favorites" : "all";
            List<Integer> choices = "favorites".equals(appearance.getString("random_theme_pool", defaultPool))
                    ? favoriteThemeIndices() : allThemeIndices();
            if (!choices.isEmpty()) {
                if (choices.size() > 1) choices.remove(Integer.valueOf(accentIndex));
                accentIndex = choices.get(new java.util.Random().nextInt(choices.size()));
                appearance.edit().putInt("accent", accentIndex).apply();
            }
        }
        applyThemeColors();
        configureSystemBars();
        showStorageGate();
        showStartupSplash();
        handler.postDelayed(() -> {
            if (storage.isReady()) {
                migrateCoversThen(() -> {
                    loadLibraryAndDashboard();
                    finishStartupSplash();
                });
            } else {
                showStorageGate();
                finishStartupSplash();
            }
        }, 900);
        getWindow().setSoftInputMode(WindowManagerFlags.ADJUST_RESIZE);
    }

    private void applyThemeColors() {
        PURPLE = themeAccentColor(accentIndex);
        BG = lightTheme ? Color.rgb(246, 244, 249) : BG_DARK;
        SURFACE = lightTheme ? Color.WHITE : SURFACE_DARK;
        SURFACE_ALT = lightTheme ? Color.rgb(239, 236, 244) : SURFACE_ALT_DARK;
        TEXT = lightTheme ? Color.rgb(28, 24, 33) : Color.rgb(241, 241, 241);
        MUTED = lightTheme ? Color.rgb(104, 97, 112) : Color.rgb(165, 165, 165);
        if (getWindow() != null) {
            getWindow().setStatusBarColor(BG);
            getWindow().setNavigationBarColor(BG);
            if (Build.VERSION.SDK_INT >= 23) {
                int flags = getWindow().getDecorView().getSystemUiVisibility();
                if (lightTheme) flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                else flags &= ~(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                getWindow().getDecorView().setSystemUiVisibility(flags);
            }
        }
        if (systemRoot != null) systemRoot.setBackgroundColor(BG);
        if (drawerPanel != null) {
            drawerPanel.setBackgroundColor(SURFACE);
            buildDrawer();
        }
    }

    private int themeAccentColor(int index) {
        if (index < FLAT_ACCENT_COLORS.length) return FLAT_ACCENT_COLORS[index];
        if (index < BUILTIN_THEME_COUNT) {
            int[] colors = GRADIENT_COLORS[index - FLAT_ACCENT_COLORS.length];
            return colors[colors.length / 2];
        }
        JSONArray colors = customTheme(index).optJSONArray("colors");
        return colors == null || colors.length() == 0 ? FLAT_ACCENT_COLORS[0]
                : colors.optInt(colors.length() / 2, FLAT_ACCENT_COLORS[0]);
    }

    private int themeSecondaryColor() {
        int count = Math.max(1, themeCount());
        return themeAccentColor((accentIndex + 2) % count);
    }

    private String themeName(int index) {
        if (index < FLAT_ACCENT_NAMES.length) return FLAT_ACCENT_NAMES[index];
        if (index < BUILTIN_THEME_COUNT) return GRADIENT_NAMES[index - FLAT_ACCENT_COLORS.length];
        return customTheme(index).optString("name", "تم سفارشی");
    }

    private int themeCount() {
        return BUILTIN_THEME_COUNT + customThemes().length();
    }

    private JSONArray customThemes() {
        try {
            return new JSONArray(getSharedPreferences("appearance", MODE_PRIVATE)
                    .getString("custom_themes", "[]"));
        } catch (JSONException ignored) {
            return new JSONArray();
        }
    }

    private JSONObject customTheme(int index) {
        JSONArray themes = customThemes();
        JSONObject theme = themes.optJSONObject(index - BUILTIN_THEME_COUNT);
        return theme == null ? new JSONObject() : theme;
    }

    private boolean isGradientTheme(int index) {
        if (index >= FLAT_ACCENT_COLORS.length && index < BUILTIN_THEME_COUNT) return true;
        if (index < BUILTIN_THEME_COUNT) return false;
        JSONArray colors = customTheme(index).optJSONArray("colors");
        return colors != null && colors.length() > 1;
    }

    private boolean isThemeFavorite(int index) {
        if (index >= BUILTIN_THEME_COUNT) return customTheme(index).optBoolean("favorite", false);
        Set<Integer> favorites = new HashSet<>();
        String saved = getSharedPreferences("appearance", MODE_PRIVATE).getString("favorite_builtin_themes", "");
        for (String item : saved.split(",")) {
            try { if (!item.isEmpty()) favorites.add(Integer.parseInt(item)); }
            catch (NumberFormatException ignored) { }
        }
        return favorites.contains(index);
    }

    private List<Integer> favoriteThemeIndices() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < themeCount(); i++) if (isThemeFavorite(i)) result.add(i);
        return result;
    }

    private List<Integer> allThemeIndices() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < themeCount(); i++) result.add(i);
        return result;
    }

    private void setThemeFavorite(int index, boolean favorite) {
        SharedPreferences preferences = getSharedPreferences("appearance", MODE_PRIVATE);
        if (index >= BUILTIN_THEME_COUNT) {
            JSONArray themes = customThemes();
            JSONObject theme = themes.optJSONObject(index - BUILTIN_THEME_COUNT);
            if (theme == null) return;
            try { theme.put("favorite", favorite); }
            catch (JSONException ignored) { return; }
            preferences.edit().putString("custom_themes", themes.toString()).apply();
            return;
        }
        Set<Integer> favorites = new java.util.TreeSet<>();
        String saved = preferences.getString("favorite_builtin_themes", "");
        for (String item : saved.split(",")) {
            try { if (!item.isEmpty()) favorites.add(Integer.parseInt(item)); }
            catch (NumberFormatException ignored) { }
        }
        if (favorite) favorites.add(index); else favorites.remove(index);
        StringBuilder value = new StringBuilder();
        for (Integer item : favorites) {
            if (value.length() > 0) value.append(',');
            value.append(item);
        }
        preferences.edit().putString("favorite_builtin_themes", value.toString()).apply();
    }

    private void applyGradientTheme(GradientDrawable drawable, int index) {
        applyGradientTheme(drawable, index, 260);
    }

    private void applyGradientTheme(GradientDrawable drawable, int index, int radialRadiusDp) {
        if (index >= BUILTIN_THEME_COUNT) {
            JSONArray storedColors = customTheme(index).optJSONArray("colors");
            if (storedColors == null || storedColors.length() < 2) return;
            int[] colors = new int[storedColors.length()];
            for (int i = 0; i < storedColors.length(); i++) colors[i] = storedColors.optInt(i, PURPLE);
            drawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
            drawable.setColors(colors);
            return;
        }
        int gradientIndex = index - FLAT_ACCENT_COLORS.length;
        drawable.setOrientation(GRADIENT_ORIENTATIONS[gradientIndex]);
        drawable.setColors(GRADIENT_COLORS[gradientIndex]);
        if (gradientIndex == GRADIENT_COLORS.length - 1) {
            drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
            drawable.setGradientRadius(dp(radialRadiusDp));
        }
    }

    private int contrastOnAccent() {
        return contrastOnColor(PURPLE);
    }

    private int contrastOnColor(int color) {
        int red = Color.red(color), green = Color.green(color), blue = Color.blue(color);
        double luminance = (0.2126 * red + 0.7152 * green + 0.0722 * blue) / 255.0;
        return luminance > 0.62 ? Color.BLACK : Color.WHITE;
    }

    private void configureSystemBars() {
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= 29) window.setNavigationBarContrastEnforced(false);
        if (Build.VERSION.SDK_INT >= 30) window.setDecorFitsSystemWindows(false);
        else window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        systemRoot = new SwipeRoot(this);
        systemRoot.setBackgroundColor(BG);
        systemRoot.setOnApplyWindowInsetsListener((view, insets) -> {
            int left, top, right, bottom;
            if (Build.VERSION.SDK_INT >= 30) {
                android.graphics.Insets safe = insets.getInsets(WindowInsets.Type.navigationBars()
                        | WindowInsets.Type.displayCutout());
                android.graphics.Insets cutout = insets.getInsets(WindowInsets.Type.displayCutout());
                left = Math.max(safe.left, cutout.left); top = cutout.top;
                right = Math.max(safe.right, cutout.right);
                bottom = Math.max(safe.bottom, insets.getInsets(WindowInsets.Type.ime()).bottom);
            } else {
                left = insets.getSystemWindowInsetLeft();
                top = 0;
                right = insets.getSystemWindowInsetRight(); bottom = insets.getSystemWindowInsetBottom();
                if (Build.VERSION.SDK_INT >= 28 && insets.getDisplayCutout() != null) {
                    left = Math.max(left, insets.getDisplayCutout().getSafeInsetLeft());
                    top = insets.getDisplayCutout().getSafeInsetTop();
                    right = Math.max(right, insets.getDisplayCutout().getSafeInsetRight());
                    bottom = Math.max(bottom, insets.getDisplayCutout().getSafeInsetBottom());
                }
            }
            view.setPadding(left, top, right, bottom);
            return insets;
        });
        appColumn = new LinearLayout(this);
        appColumn.setOrientation(LinearLayout.VERTICAL);
        systemRoot.addView(appColumn, new FrameLayout.LayoutParams(-1, -1));
        drawerLayer = new FrameLayout(this);
        drawerLayer.setVisibility(View.GONE);
        drawerLayer.setBackgroundColor(Color.TRANSPARENT);
        drawerScrim = new View(this);
        drawerScrim.setBackgroundColor(0x99000000);
        drawerScrim.setAlpha(0f);
        drawerScrim.setOnClickListener(v -> closeDrawer());
        drawerLayer.addView(drawerScrim, new FrameLayout.LayoutParams(-1, -1));
        drawerPanel = new LinearLayout(this);
        drawerPanel.setOrientation(LinearLayout.VERTICAL);
        drawerPanel.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        drawerPanel.setPadding(dp(18), dp(22), dp(18), dp(18));
        drawerPanel.setBackgroundColor(SURFACE);
        FrameLayout.LayoutParams panelParams = new FrameLayout.LayoutParams(-1, -1, Gravity.RIGHT);
        panelParams.width = (int) (getResources().getDisplayMetrics().widthPixels * (2f / 3f));
        drawerPanel.setTranslationX(panelParams.width);
        drawerLayer.addView(drawerPanel, panelParams);
        drawerPanel.setOnClickListener(v -> { });
        buildDrawer();
        systemRoot.addView(drawerLayer, new FrameLayout.LayoutParams(-1, -1));
        setContentView(systemRoot);
        systemRoot.requestApplyInsets();
        hideAndroidStatusBar();
    }

    private void hideAndroidStatusBar() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= 30) {
            android.view.WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                controller.setSystemBarsBehavior(
                        android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                controller.hide(WindowInsets.Type.statusBars());
            }
            return;
        }
        View decor = window.getDecorView();
        int flags = decor.getSystemUiVisibility()
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decor.setSystemUiVisibility(flags);
    }

    private void showStartupSplash() {
        splashOverlay = new FrameLayout(this);
        splashOverlay.setBackgroundColor(BG);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER);
        content.setPadding(dp(42), dp(30), dp(42), dp(30));
        ManhwaSplashArt art = new ManhwaSplashArt(this);
        content.addView(art, new LinearLayout.LayoutParams(dp(156), dp(142)));
        splashArtAnimator = ObjectAnimator.ofFloat(art, "pulse", 0.97f, 1.03f);
        splashArtAnimator.setDuration(1150);
        splashArtAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        splashArtAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        splashArtAnimator.start();
        TextView title = text("مانهواخوان", 23, TEXT);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(-1, dp(48));
        titleParams.topMargin = dp(15);
        content.addView(title, titleParams);
        splashProgress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        splashProgress.setMax(100); splashProgress.setProgress(0);
        splashProgress.setProgressDrawable(createSplashProgressDrawable());
        splashProgress.setPadding(0, 0, 0, 0);
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(-1, dp(9));
        progressParams.topMargin = dp(13);
        content.addView(splashProgress, progressParams);
        splashOverlay.addView(content, new FrameLayout.LayoutParams(-1, -1));
        systemRoot.addView(splashOverlay, new FrameLayout.LayoutParams(-1, -1));
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(splashProgress, "progress", 0, 100);
        progressAnimator.setDuration(850);
        progressAnimator.start();
    }

    private Drawable createSplashProgressDrawable() {
        GradientDrawable track = new GradientDrawable();
        track.setShape(GradientDrawable.RECTANGLE);
        track.setColor(lightTheme ? 0xFFDCD6E5 : 0xFF302A38);
        track.setCornerRadius(dp(8));
        int bright = Color.rgb(Math.min(255, Color.red(PURPLE) + 55),
                Math.min(255, Color.green(PURPLE) + 45), Math.min(255, Color.blue(PURPLE) + 35));
        GradientDrawable fill = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{PURPLE, bright});
        fill.setCornerRadius(dp(8));
        ClipDrawable clippedFill = new ClipDrawable(fill, Gravity.LEFT, ClipDrawable.HORIZONTAL);
        LayerDrawable layers = new LayerDrawable(new Drawable[]{track, clippedFill});
        layers.setId(0, android.R.id.background);
        layers.setId(1, android.R.id.progress);
        return layers;
    }

    private void finishStartupSplash() {
        if (splashOverlay == null) return;
        if (splashArtAnimator != null) {
            splashArtAnimator.cancel();
            splashArtAnimator = null;
        }
        if (splashProgress != null) splashProgress.setProgress(100);
        systemRoot.removeView(splashOverlay);
        splashOverlay = null;
        splashProgress = null;
    }

    private void showStorageGate() {
        TextView message = text("برای نگهداری فهرست و فصل‌ها، پوشه Documents لازم است.", 17, TEXT);
        message.setGravity(Gravity.CENTER);
        message.setPadding(dp(26), dp(24), dp(26), dp(24));
        LinearLayout gate = new LinearLayout(this);
        gate.setGravity(Gravity.CENTER);
        gate.setOrientation(LinearLayout.VERTICAL);
        gate.addView(message, new LinearLayout.LayoutParams(-1, -2));
        gate.addView(textButton("انتخاب پوشه", "folder", PURPLE, this::chooseFolder),
                new LinearLayout.LayoutParams(-2, dp(54)));
        appColumn.removeAllViews();
        animatePageTransition();
        appColumn.addView(gate, new LinearLayout.LayoutParams(-1, -1));
    }

    private void chooseFolder() {
        try { storage.openPicker(); }
        catch (Exception error) { toast("انتخاب‌گر پوشه در دسترس نیست."); }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_COVER) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) loadImageForCover(data.getData());
            return;
        }
        if (requestCode != REQUEST_FOLDER) return;
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                storage.acceptFolder(data.getData(), data.getFlags());
                migrateCoversThen(() -> {
                    loadLibraryAndDashboard();
                    if (returnToSettingsAfterPicker) showSettings();
                    returnToSettingsAfterPicker = false;
                });
                return;
            } catch (Exception error) {
                returnToSettingsAfterPicker = false;
                toast("ساخت پوشهٔ ManhwaReader ناموفق بود.");
                showStorageGate();
            }
        } else {
            returnToSettingsAfterPicker = false;
            if (!storage.isReady()) showStorageGate();
        }
    }

    private void migrateCoversThen(Runnable completion) {
        try {
            imageExecutor.execute(() -> {
                storage.migrateLegacyImages();
                runOnUiThread(() -> { if (!isFinishing()) completion.run(); });
            });
        } catch (java.util.concurrent.RejectedExecutionException error) {
            completion.run();
        }
    }

    private void loadLibraryAndDashboard() {
        String saved;
        try {
            saved = storage.read("library.json");
        } catch (Exception error) {
            toast("خواندن library.json ناموفق بود: " + storage.lastError());
            showStorageGate();
            return;
        }
        try {
            library = saved == null ? new JSONObject() : new JSONObject(saved);
            normalizeLibrary();
        } catch (Exception error) {
            android.util.Log.e("ManhwaReader", "library.json is invalid; preserving the file", error);
            toast("فهرست مانهواها خوانده نشد؛ فایل قبلی دست‌نخورده باقی ماند.");
            showStorageGate();
            return;
        }
        saveLibrary();
        canonicalizeAllGenrePreferences();
        rebuildAllRecommendationStates();
        showDashboard();
    }

    private void canonicalizeAllGenrePreferences() {
        boolean previousMode = animeMode;
        Set<String> previousFilters = new HashSet<>(activeGenreFilter);
        activeGenreFilter.clear();
        animeMode = false;
        canonicalizeGenrePreferences();
        animeMode = true;
        canonicalizeGenrePreferences();
        activeGenreFilter.clear();
        activeGenreFilter.addAll(previousFilters);
        animeMode = previousMode;
    }

    private void rebuildAllRecommendationStates() {
        boolean previousMode = animeMode;
        animeMode = false;
        rebuildRecommendationWeights();
        persistRecommendationState();
        animeMode = true;
        rebuildRecommendationWeights();
        persistRecommendationState();
        animeMode = previousMode;
    }

    private void setAnimeMode(boolean anime) {
        animeMode = anime;
        getSharedPreferences("app_settings", MODE_PRIVATE).edit().putBoolean("anime_mode", anime).apply();
        activeGenreFilter.clear();
        selectedManhwaIds.clear();
        selectedTagNames.clear();
        if (drawerPanel != null) buildDrawer();
    }

    private String contentArrayKey() { return animeMode ? "animes" : "manhwas"; }
    private String contentGenresKey() { return animeMode ? "animeGenres" : "genres"; }
    private String contentGenreEquivalentsKey() { return animeMode ? "animeGenreEquivalents" : "genreEquivalents"; }
    private String contentUnitLabel() { return animeMode ? "قسمت" : "فصل"; }
    private String contentUnitPlural() { return animeMode ? "قسمت‌ها" : "فصل‌ها"; }
    private String contentTitleLabel() { return animeMode ? "انیمه" : "مانهوا"; }
    private JSONArray contentItems() { return library.optJSONArray(contentArrayKey()); }
    private JSONArray contentGenres() { return library.optJSONArray(contentGenresKey()); }
    private JSONArray checklistArray(JSONObject item) {
        return item == null ? null : item.optJSONArray(animeMode ? "episodes" : "chapters");
    }
    private Set<String> currentFavoriteIds() { return animeMode ? favoriteAnimeIds : favoriteManhwaIds; }
    private Set<String> currentFavoriteGenres() {
        return animeMode ? favoriteAnimeGenrePreferences : favoriteGenrePreferences;
    }
    private Set<String> currentDislikedGenres() {
        return animeMode ? dislikedAnimeGenrePreferences : dislikedGenrePreferences;
    }
    private Set<String> currentDislikedIds() {
        return animeMode ? dislikedAnimeIds : dislikedManhwaIds;
    }
    private Set<Long> currentSeenRecommendationIds() {
        return animeMode ? seenAnimeRecommendationIds : seenRecommendationIds;
    }
    private ManhwaRecommendationEngine.UserPreferences currentRecommendationPreferences() {
        return animeMode ? animeRecommendationPreferences : recommendationPreferences;
    }
    private ManhwaRecommendationEngine currentRecommendationEngine() {
        return animeMode ? animeRecommendationEngine : recommendationEngine;
    }
    private SharedPreferences currentRecommendationState() {
        return animeMode ? animeRecommendationState : recommendationState;
    }

    private static int coverCacheBudgetKb() {
        long heapKb = Runtime.getRuntime().maxMemory() / 1024L;
        return (int) Math.max(8 * 1024L, Math.min(32 * 1024L, heapKb / 8L));
    }

    private void normalizeLibrary() throws JSONException {
        boolean originalAnimeMode = animeMode;
        animeMode = false;
        JSONArray manhwas = library.optJSONArray("manhwas");
        if (manhwas == null) {
            manhwas = new JSONArray();
            JSONArray works = library.optJSONArray("works");
            if (works != null) for (int w = 0; w < works.length(); w++) {
                JSONObject work = works.optJSONObject(w);
                JSONArray children = work == null ? null : work.optJSONArray("manhwas");
                if (children == null) continue;
                for (int b = 0; b < children.length(); b++) {
                    JSONObject manhwa = children.optJSONObject(b);
                    if (manhwa != null) {
                        if (manhwa.optJSONArray("genres") == null && manhwa.optJSONArray("tags") == null) {
                            manhwa.put("genres", new JSONArray());
                        }
                        manhwas.put(manhwa);
                    }
                }
            }
        }
        JSONArray genres = library.optJSONArray("genres");
        if (genres == null) genres = new JSONArray();
        JSONArray legacyTags = library.optJSONArray("tags");
        if (legacyTags != null) {
            for (int i = 0; i < legacyTags.length(); i++) appendUniqueGenre(genres, legacyTags.optString(i));
        }
        library.put("manhwas", manhwas).put("genres", genres);
        library.remove("tags");
        library.remove("works");
        JSONArray animes = library.optJSONArray("animes");
        if (animes == null) animes = new JSONArray();
        JSONArray animeGenres = library.optJSONArray("animeGenres");
        if (animeGenres == null) animeGenres = new JSONArray();
        library.put("animes", animes).put("animeGenres", animeGenres);
        Set<String> all = new HashSet<>();
        Set<Long> recommendationIds = new HashSet<>();
        long nextRecommendationId = 1;
        for (int i = 0; i < genres.length(); i++) {
            String genre = canonicalGenre(genres.optString(i));
            if (!genre.isEmpty() && !"Other".equalsIgnoreCase(genre)) all.add(genre);
        }
        for (int i = 0; i < manhwas.length(); i++) {
            JSONObject manhwa = manhwas.optJSONObject(i);
            if (manhwa == null) continue;
            inflateCompactChecklist(manhwa, false);
            if (manhwa.optString("id").isEmpty()) manhwa.put("id", UUID.randomUUID().toString());
            if (manhwa.optString("title").isEmpty()) manhwa.put("title", "بدون عنوان");
            manhwa.put("title", englishDigits(manhwa.optString("title")));
            manhwa.remove("author");
            manhwa.remove("creator");
            manhwa.remove("studio");
            manhwa.remove("interestRating");
            long recommendationId = manhwa.optLong("recommendationId", -1);
            if (recommendationId <= 0 || !recommendationIds.add(recommendationId)) {
                while (recommendationIds.contains(nextRecommendationId)) nextRecommendationId++;
                recommendationId = nextRecommendationId++;
                manhwa.put("recommendationId", recommendationId);
                recommendationIds.add(recommendationId);
            } else if (recommendationId >= nextRecommendationId && recommendationId < Long.MAX_VALUE) {
                nextRecommendationId = recommendationId + 1;
            }
            // Hidden-library data is no longer used; keep old manhwas visible in all shelves.
            manhwa.remove("hidden");
            JSONArray manhwaGenres = manhwa.optJSONArray("genres");
            if (manhwaGenres == null) manhwaGenres = manhwa.optJSONArray("tags");
            if (manhwaGenres == null) manhwaGenres = new JSONArray();
            manhwa.remove("tags");
            String oldGenre = manhwa.optString("genre", "").trim();
            if (!oldGenre.isEmpty() && !"Other".equalsIgnoreCase(oldGenre)) appendUniqueGenre(manhwaGenres, oldGenre);
            manhwa.remove("genre");
            JSONArray canonicalManhwaGenres = new JSONArray();
            for (int j = 0; j < manhwaGenres.length(); j++) {
                String genre = canonicalGenre(manhwaGenres.optString(j));
                if (genre.isEmpty() || "Other".equalsIgnoreCase(genre)) continue;
                appendUniqueGenre(canonicalManhwaGenres, genre);
                all.add(genre);
            }
            manhwa.put("genres", canonicalManhwaGenres);
            JSONArray chapters = manhwa.optJSONArray("chapters");
            if (chapters == null) { chapters = new JSONArray(); manhwa.put("chapters", chapters); }
            for (int j = 0; j < chapters.length(); j++) {
                JSONObject chapter = chapters.optJSONObject(j);
                if (chapter == null) { chapter = new JSONObject(); chapters.put(j, chapter); }
                if (chapter.optString("title").trim().isEmpty()) chapter.put("title", "فصل " + (j + 1));
                chapter.put("title", englishDigits(chapter.optString("title")));
                chapter.remove("hasText");
                chapter.remove("wordsRead");
                chapter.remove("totalWords");
                chapter.put("percent", Math.max(0, Math.min(100, chapter.optInt("percent", 0))));
                int state = chapter.has("state") ? chapter.optInt("state", 0)
                        : chapter.optBoolean("done", false) ? 2 : 0;
                chapter.put("state", Math.max(0, Math.min(2, state)));
                chapter.remove("done");
                chapter.put("scroll", Math.max(0, chapter.optInt("scroll", 0)));
            }
            ensureChapterStorageIndexes(chapters);
            ensureOrderIndexes(chapters, true);
        }
        ensureOrderIndexes(manhwas, false);
        JSONArray cleanGenres = new JSONArray();
        List<String> sorted = new ArrayList<>(all);
        Collections.sort(sorted, String.CASE_INSENSITIVE_ORDER);
        for (String genre : sorted) cleanGenres.put(genre);
        library.put("genres", cleanGenres);
        animeMode = true;
        normalizeAnimeLibrary(animes, animeGenres);
        animeMode = originalAnimeMode;
    }

    private void inflateCompactChecklist(JSONObject item, boolean anime) throws JSONException {
        String listKey = anime ? "episodes" : "chapters";
        String countKey = anime ? "episodeCount" : "chapterCount";
        String completedKey = anime ? "watchedEpisodeIds" : "readChapterIds";
        String readyKey = anime ? "readyEpisodeIds" : "readyChapterIds";
        String overridesKey = anime ? "episodeOverrides" : "chapterOverrides";
        if (item.optJSONArray(listKey) != null
                || !item.has(completedKey) && !item.has(readyKey) && !item.has(overridesKey)) return;

        JSONArray completed = item.optJSONArray(completedKey);
        Set<Integer> completedIds = new HashSet<>();
        if (completed != null) for (int i = 0; i < completed.length(); i++) {
            int value = completed.optInt(i, -1);
            if (value > 0) completedIds.add(value);
        }
        JSONArray ready = item.optJSONArray(readyKey);
        Set<Integer> readyIds = new HashSet<>();
        if (ready != null) for (int i = 0; i < ready.length(); i++) {
            int value = ready.optInt(i, -1);
            if (value > 0) readyIds.add(value);
        }
        JSONArray overrides = item.optJSONArray(overridesKey);
        Map<Integer, JSONObject> overrideByIndex = new HashMap<>();
        int count = Math.max(0, Math.min(5000, item.optInt(countKey, 0)));
        if (overrides != null) for (int i = 0; i < overrides.length(); i++) {
            JSONObject override = overrides.optJSONObject(i);
            if (override == null) continue;
            int index = override.optInt("i", -1);
            if (index < 0 || index >= 5000) continue;
            overrideByIndex.put(index, override);
            count = Math.max(count, index + 1);
        }

        JSONArray restored = new JSONArray();
        String unit = anime ? "قسمت " : "فصل ";
        for (int i = 0; i < count; i++) {
            JSONObject entry = new JSONObject();
            entry.put("state", completedIds.contains(i + 1) ? 2 : readyIds.contains(i + 1) ? 1 : 0);
            entry.put("title", unit + (i + 1));
            entry.put("storageIndex", i + 1);
            entry.put("orderIndex", i);
            JSONObject override = overrideByIndex.get(i);
            if (override != null) {
                if (override.has("t")) entry.put("title", override.get("t"));
                if (override.has("x")) entry.put("storageIndex", override.get("x"));
                if (override.has("o")) entry.put("orderIndex", override.get("o"));
                if (override.has("p")) entry.put("percent", override.get("p"));
                if (override.has("y")) entry.put("scroll", override.get("y"));
                if (override.has("s")) entry.put("state", override.get("s"));
                JSONObject extra = override.optJSONObject("e");
                if (extra != null) {
                    Iterator<String> names = extra.keys();
                    while (names.hasNext()) {
                        String name = names.next();
                        entry.put(name, extra.get(name));
                    }
                }
            }
            restored.put(entry);
        }
        item.put(listKey, restored);
        item.remove(countKey);
        item.remove(completedKey);
        item.remove(readyKey);
        item.remove(overridesKey);
    }

    private void normalizeAnimeLibrary(JSONArray animes, JSONArray genres) throws JSONException {
        Set<String> allGenres = new HashSet<>();
        for (int i = 0; i < genres.length(); i++) {
            String genre = canonicalGenre(genres.optString(i));
            if (!genre.isEmpty() && !"Other".equalsIgnoreCase(genre)) allGenres.add(genre);
        }
        Set<Long> recommendationIds = new HashSet<>();
        long nextRecommendationId = 1;
        for (int i = 0; i < animes.length(); i++) {
            JSONObject anime = animes.optJSONObject(i);
            if (anime == null) continue;
            inflateCompactChecklist(anime, true);
            if (anime.optString("id").isEmpty()) anime.put("id", UUID.randomUUID().toString());
            if (anime.optString("title").isEmpty()) anime.put("title", "بدون عنوان");
            anime.put("title", englishDigits(anime.optString("title")));
            anime.remove("author");
            anime.remove("creator");
            anime.remove("studio");
            anime.remove("interestRating");
            long recommendationId = anime.optLong("recommendationId", -1);
            if (recommendationId <= 0 || !recommendationIds.add(recommendationId)) {
                while (recommendationIds.contains(nextRecommendationId)) nextRecommendationId++;
                recommendationId = nextRecommendationId++;
                anime.put("recommendationId", recommendationId);
                recommendationIds.add(recommendationId);
            } else if (recommendationId >= nextRecommendationId && recommendationId < Long.MAX_VALUE) {
                nextRecommendationId = recommendationId + 1;
            }
            JSONArray oldGenres = anime.optJSONArray("genres");
            if (oldGenres == null) oldGenres = anime.optJSONArray("tags");
            if (oldGenres == null) oldGenres = new JSONArray();
            JSONArray clean = new JSONArray();
            for (int j = 0; j < oldGenres.length(); j++) {
                String genre = canonicalGenre(oldGenres.optString(j));
                if (genre.isEmpty() || "Other".equalsIgnoreCase(genre)) continue;
                appendUniqueGenre(clean, genre);
                allGenres.add(genre);
            }
            anime.put("genres", clean);
            anime.remove("tags");
            anime.remove("genre");
            JSONArray episodes = anime.optJSONArray("episodes");
            if (episodes == null) { episodes = new JSONArray(); anime.put("episodes", episodes); }
            for (int j = 0; j < episodes.length(); j++) {
                JSONObject episode = episodes.optJSONObject(j);
                if (episode == null) { episode = new JSONObject(); episodes.put(j, episode); }
                int defaultEpisodeNumber = Math.max(1, episode.optInt("storageIndex", j + 1));
                int seasonNumber = Math.max(1, episode.optInt("seasonNumber", 1));
                int episodeNumber = Math.max(1, episode.optInt("episodeNumber", defaultEpisodeNumber));
                episode.put("seasonNumber", seasonNumber);
                episode.put("episodeNumber", episodeNumber);
                if (episode.optString("title").trim().isEmpty()) episode.put("title", "قسمت " + episodeNumber);
                episode.put("title", englishDigits(episode.optString("title")));
                int state = episode.has("state") ? episode.optInt("state", 0)
                        : episode.optBoolean("done", false) ? 2 : 0;
                episode.put("state", Math.max(0, Math.min(2, state)));
                episode.remove("done");
                episode.put("percent", Math.max(0, Math.min(100, episode.optInt("percent", 0))));
                episode.put("scroll", Math.max(0, episode.optInt("scroll", 0)));
            }
            ensureChapterStorageIndexes(episodes);
            ensureOrderIndexes(episodes, true);
        }
        ensureOrderIndexes(animes, false);
        JSONArray sorted = new JSONArray();
        List<String> names = new ArrayList<>(allGenres);
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        for (String name : names) sorted.put(name);
        library.put("animeGenres", sorted);
    }

    /** Initializes each list alphabetically once, then follows its saved orderIndex. */
    private void ensureOrderIndexes(JSONArray items, boolean chapterNames) {
        if (items == null) return;
        List<JSONObject> ordered = new ArrayList<>();
        Set<Integer> used = new HashSet<>();
        boolean hasValidIndexes = true;
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.optJSONObject(i);
            if (item == null) continue;
            ordered.add(item);
            int index = item.optInt("orderIndex", Integer.MIN_VALUE);
            if (!item.has("orderIndex") || index < 0 || !used.add(index)) hasValidIndexes = false;
        }
        final boolean preserveSavedOrder = hasValidIndexes;
        Collections.sort(ordered, (first, second) -> {
            if (preserveSavedOrder) {
                int order = Integer.compare(first.optInt("orderIndex"), second.optInt("orderIndex"));
                if (order != 0) return order;
            }
            String firstTitle = first.optString("title", "");
            String secondTitle = second.optString("title", "");
            if (chapterNames) {
                BigDecimal firstNumber = chapterNumber(firstTitle);
                BigDecimal secondNumber = chapterNumber(secondTitle);
                if (firstNumber != null && secondNumber != null) {
                    int numberOrder = firstNumber.compareTo(secondNumber);
                    if (numberOrder != 0) return numberOrder;
                }
            }
            return String.CASE_INSENSITIVE_ORDER.compare(firstTitle, secondTitle);
        });
        for (int i = 0; i < ordered.size(); i++) {
            putJson(ordered.get(i), "orderIndex", i);
            try { items.put(i, ordered.get(i)); } catch (JSONException ignored) { }
        }
    }

    private void appendUniqueGenre(JSONArray genres, String value) {
        String candidate = canonicalGenre(value);
        if (candidate.isEmpty() || "Other".equalsIgnoreCase(candidate)) return;
        for (int i = 0; i < genres.length(); i++) {
            if (candidate.equalsIgnoreCase(genres.optString(i).trim())) return;
        }
        genres.put(candidate);
    }

    private void appendUniqueRawGenre(JSONArray genres, String value) {
        String candidate = value == null ? "" : englishDigits(value.trim());
        if (candidate.isEmpty() || "Other".equalsIgnoreCase(candidate)) return;
        for (int i = 0; i < genres.length(); i++) {
            if (candidate.equalsIgnoreCase(genres.optString(i).trim())) return;
        }
        genres.put(candidate);
    }

    private boolean saveLibrary() {
        String contents;
        try { contents = compactLibraryJson().toString(); }
        catch (JSONException error) {
            toast("آماده‌سازی فایل اطلاعات ناموفق بود.");
            return false;
        }
        if (!storage.write("library.json", contents)) {
            toast("ذخیرهٔ اطلاعات ناموفق بود: " + storage.lastError());
            return false;
        }
        return true;
    }

    private JSONObject compactLibraryJson() throws JSONException {
        JSONObject compact = new JSONObject(library.toString());
        compactChecklistList(compact.optJSONArray("manhwas"), false);
        compactChecklistList(compact.optJSONArray("animes"), true);
        compact.put("schemaVersion", 3);
        return compact;
    }

    private void compactChecklistList(JSONArray items, boolean anime) throws JSONException {
        if (items == null) return;
        String listKey = anime ? "episodes" : "chapters";
        String countKey = anime ? "episodeCount" : "chapterCount";
        String completedKey = anime ? "watchedEpisodeIds" : "readChapterIds";
        String readyKey = anime ? "readyEpisodeIds" : "readyChapterIds";
        String overridesKey = anime ? "episodeOverrides" : "chapterOverrides";
        String unit = anime ? "قسمت " : "فصل ";
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.optJSONObject(i);
            JSONArray list = item == null ? null : item.optJSONArray(listKey);
            if (item == null || list == null) continue;
            JSONArray completed = new JSONArray();
            JSONArray ready = new JSONArray();
            JSONArray overrides = new JSONArray();
            for (int j = 0; j < list.length(); j++) {
                JSONObject entry = list.optJSONObject(j);
                if (entry == null) continue;
                int storageIndex = Math.max(1, entry.optInt("storageIndex", j + 1));
                int state = chapterState(entry);
                if (state == 2) completed.put(storageIndex);
                else if (state == 1) ready.put(storageIndex);
                JSONObject override = new JSONObject();
                override.put("i", j);
                String defaultTitle = unit + storageIndex;
                String title = entry.optString("title", defaultTitle);
                if (!title.equals(defaultTitle)) override.put("t", title);
                if (storageIndex != j + 1) override.put("x", storageIndex);
                if (entry.optInt("orderIndex", j) != j) override.put("o", entry.optInt("orderIndex", j));
                if (entry.optInt("percent", 0) != 0) override.put("p", entry.optInt("percent", 0));
                if (entry.optInt("scroll", 0) != 0) override.put("y", entry.optInt("scroll", 0));
                if (state != 0) override.put("s", state);
                JSONObject extra = new JSONObject();
                Iterator<String> names = entry.keys();
                while (names.hasNext()) {
                    String name = names.next();
                    if ("done".equals(name) || "state".equals(name) || "title".equals(name) || "storageIndex".equals(name)
                            || "orderIndex".equals(name) || "percent".equals(name) || "scroll".equals(name)
                            || "hasText".equals(name) || "wordsRead".equals(name) || "totalWords".equals(name)) continue;
                    extra.put(name, entry.get(name));
                }
                if (extra.length() > 0) override.put("e", extra);
                if (override.length() > 1) overrides.put(override);
            }
            item.remove(listKey);
            item.put(countKey, list.length());
            item.put(completedKey, completed);
            item.put(readyKey, ready);
            item.put(overridesKey, overrides);
        }
    }

    private void restoreLibrary(String snapshot) {
        try { library = new JSONObject(snapshot); }
        catch (JSONException ignored) { }
    }

    private void buildDrawer() {
        if (drawerSettingsAnimator != null) drawerSettingsAnimator.cancel();
        drawerPanel.removeAllViews();
        TextView heading = text("فهرست", 20, TEXT);
        LinearLayout.LayoutParams headingParams = new LinearLayout.LayoutParams(-1, dp(56));
        headingParams.bottomMargin = dp(10);
        drawerPanel.addView(heading, headingParams);
        addDrawerAction(drawerPanel, animeMode ? "پیشنهاد انیمه" : "پیشنهاد مانهوا",
                animeMode ? "anime" : "manhwa", 0xFFFFB45E, () -> {
            closeDrawer();
            showRecommendation();
        }, false);
        addDrawerAction(drawerPanel, "پیشرفت", "chart", 0xFF69D2A0, () -> {
            closeDrawer();
            showStatistics();
        }, false);

        LinearLayout settingsToggle = drawerSettingsButton();
        LinearLayout.LayoutParams settingsParams = drawerActionParams(false);
        drawerPanel.addView(settingsToggle, settingsParams);

        drawerSettingsOptions = new LinearLayout(this);
        drawerSettingsOptions.setOrientation(LinearLayout.VERTICAL);
        drawerSettingsOptions.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        drawerSettingsOptions.setVisibility(drawerSettingsExpanded ? View.VISIBLE : View.GONE);
        drawerPanel.addView(drawerSettingsOptions, new LinearLayout.LayoutParams(-1,
                drawerSettingsExpanded ? ViewGroup.LayoutParams.WRAP_CONTENT : 0));
        addDrawerAction(drawerSettingsOptions, "عمومی", "settings", 0xFFB69AFF, () -> {
            closeDrawer();
            showSettings();
        }, true);
        addDrawerAction(drawerSettingsOptions, "تگ‌ها", "tag", 0xFF82CF8B, () -> {
            closeDrawer();
            showTagSettings();
        }, true);
        addDrawerAction(drawerSettingsOptions, "تم‌ها", "settings", 0xFFE6A7FF, () -> {
            closeDrawer();
            showThemes();
        }, true);

        View spacer = new View(this);
        drawerPanel.addView(spacer, new LinearLayout.LayoutParams(1, 0, 1));
        drawerPanel.addView(text("نسخهٔ " + VERSION, 12, MUTED), new LinearLayout.LayoutParams(-1, dp(36)));
    }

    private LinearLayout drawerSettingsButton() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        row.setPadding(dp(14), 0, dp(14), 0);
        int primaryText = contrastOnColor(PURPLE);
        row.setBackground(drawerPrimaryBackground());
        NativeIconView icon = new NativeIconView(this, "settings", primaryText);
        row.addView(icon, new LinearLayout.LayoutParams(dp(22), dp(22)));
        TextView label = text("تنظیمات", 15, primaryText);
        label.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(0, -1, 1);
        labelParams.leftMargin = dp(12);
        row.addView(label, labelParams);
        drawerSettingsChevron = new NativeIconView(this, "dropdown", primaryText);
        row.addView(drawerSettingsChevron, new LinearLayout.LayoutParams(dp(18), dp(18)));
        drawerSettingsChevron.setRotation(drawerSettingsExpanded ? 180f : 0f);
        row.setClickable(true);
        row.setFocusable(true);
        row.setContentDescription("تنظیمات، باز و بسته کردن زیرمنو");
        row.setOnClickListener(v -> toggleDrawerSettings());
        applyPressScale(row);
        return row;
    }

    private void addDrawerAction(LinearLayout parent, String label, String icon, int accent,
                                 Runnable action, boolean nested) {
        int iconColor = nested ? themeSecondaryColor() : contrastOnColor(PURPLE);
        LinearLayout button = (LinearLayout) textButton(label, icon, iconColor, action);
        if (!nested && button.getChildCount() > 1 && button.getChildAt(1) instanceof TextView) {
            ((TextView) button.getChildAt(1)).setTextColor(iconColor);
        }
        button.setBackground(nested ? drawerActionBackground(accent) : drawerPrimaryBackground());
        parent.addView(button, drawerActionParams(nested));
    }

    private LinearLayout.LayoutParams drawerActionParams(boolean nested) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1,
                dp(nested ? 48 : 52));
        params.bottomMargin = dp(nested ? 7 : 10);
        if (nested) params.rightMargin = dp(12);
        return params;
    }

    private GradientDrawable drawerActionBackground(int accent) {
        return roundDrawable(SURFACE, themedStrokeColor(accent), 14);
    }

    private GradientDrawable drawerPrimaryBackground() {
        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(dp(14));
        if (isGradientTheme(accentIndex)) {
            applyGradientTheme(background, accentIndex);
        } else {
            background.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
            background.setColors(new int[]{PURPLE, themeSecondaryColor()});
        }
        return background;
    }

    private void toggleDrawerSettings() {
        if (drawerSettingsOptions == null) return;
        if (drawerSettingsAnimator != null) drawerSettingsAnimator.cancel();
        boolean expand = !drawerSettingsExpanded;
        drawerSettingsExpanded = expand;
        if (drawerSettingsChevron != null) {
            drawerSettingsChevron.animate().rotation(expand ? 180f : 0f).setDuration(200)
                    .setInterpolator(new DecelerateInterpolator()).start();
        }

        LinearLayout options = drawerSettingsOptions;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) options.getLayoutParams();
        int startHeight;
        int endHeight;
        if (expand) {
            options.setVisibility(View.VISIBLE);
            options.setAlpha(0f);
            options.setTranslationY(-dp(5));
            int width = Math.max(1, drawerWidth() - drawerPanel.getPaddingLeft() - drawerPanel.getPaddingRight());
            options.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            endHeight = options.getMeasuredHeight();
            startHeight = 0;
            params.height = 0;
            options.setLayoutParams(params);
            options.animate().alpha(1f).translationY(0f).setDuration(210)
                    .setInterpolator(new DecelerateInterpolator()).start();
        } else {
            startHeight = options.getHeight();
            if (startHeight <= 0) startHeight = options.getMeasuredHeight();
            endHeight = 0;
            options.animate().alpha(0f).translationY(-dp(5)).setDuration(190)
                    .setInterpolator(new DecelerateInterpolator()).start();
        }
        drawerSettingsAnimator = ValueAnimator.ofInt(startHeight, endHeight);
        drawerSettingsAnimator.setDuration(expand ? 210 : 190);
        drawerSettingsAnimator.setInterpolator(new DecelerateInterpolator());
        drawerSettingsAnimator.addUpdateListener(animation -> {
            LinearLayout.LayoutParams current = (LinearLayout.LayoutParams) options.getLayoutParams();
            current.height = (int) animation.getAnimatedValue();
            options.setLayoutParams(current);
        });
        drawerSettingsAnimator.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                if (!expand) {
                    options.setVisibility(View.GONE);
                    options.setAlpha(1f);
                    options.setTranslationY(0f);
                }
                LinearLayout.LayoutParams finalParams = (LinearLayout.LayoutParams) options.getLayoutParams();
                finalParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                options.setLayoutParams(finalParams);
            }
        });
        drawerSettingsAnimator.start();
    }

    private void showSettings() {
        page = "settings";
        appColumn.removeAllViews();
        animatePageTransition();
        appColumn.addView(makeHeader("تنظیمات", "", "menu", null, this::openDrawer, null));
        ScrollView scroll = new ScrollView(this);
        settingsScroll = scroll;
        scroll.setVerticalScrollBarEnabled(false); scroll.setHorizontalScrollBarEnabled(false);
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(18), dp(14), dp(18), dp(24));

        body.addView(text("ذخیره‌سازی", 15, MUTED), fieldParams());
        addPreferenceSwitch(body, "هنگام افزودن، به ابزار هوش مصنوعی دسترسی ندارم",
                "app_settings", "ai_access_unavailable", false);
        body.addView(textButton("انتخاب پوشهٔ ذخیره‌سازی", "folder", PURPLE, () -> {
            returnToSettingsAfterPicker = true;
            chooseFolder();
        }), fieldParams());

        TextView promptsTitle = text("پرامپت‌های تولید اطلاعات", 15, MUTED);
        promptsTitle.setPadding(0, dp(16), 0, dp(7));
        body.addView(promptsTitle);
        body.addView(textButton("ویرایش پرامپت مانهوا", "edit", themeSecondaryColor(),
                () -> showJsonPromptEditor(false)), fieldParams());
        body.addView(textButton("ویرایش پرامپت انیمه", "edit", themeSecondaryColor(),
                () -> showJsonPromptEditor(true)), fieldParams());

        scroll.addView(body);
        appColumn.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
    }

    private void showThemes() {
        page = "themes";
        appColumn.removeAllViews();
        animatePageTransition();
        appColumn.addView(makeHeader("تم‌ها", "", "menu", null, this::openDrawer, null));
        ScrollView scroll = new ScrollView(this);
        settingsScroll = scroll;
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setHorizontalScrollBarEnabled(false);
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(18), dp(14), dp(18), dp(24));

        TextView modeTitle = text("حالت نمایش", 15, MUTED);
        modeTitle.setPadding(0, 0, 0, dp(7));
        body.addView(modeTitle);
        LinearLayout modes = new LinearLayout(this);
        modes.setOrientation(LinearLayout.HORIZONTAL);
        modes.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        modes.addView(themeChoice("سیاه", !lightTheme, false), new LinearLayout.LayoutParams(0, dp(48), 1));
        modes.addView(new View(this), new LinearLayout.LayoutParams(dp(10), 1));
        modes.addView(themeChoice("روشن", lightTheme, true), new LinearLayout.LayoutParams(0, dp(48), 1));
        body.addView(modes, fieldParams());
        addPreferenceSwitch(body, "انتخاب تصادفی تم هنگام ورود", "appearance",
                "random_each_launch", false);
        SharedPreferences appearance = getSharedPreferences("appearance", MODE_PRIVATE);
        String defaultPool = appearance.getBoolean("random_each_launch", false) ? "favorites" : "all";
        String randomPool = appearance.getString("random_theme_pool", defaultPool);
        TextView randomPoolTitle = text("انتخاب از", 13, MUTED);
        randomPoolTitle.setPadding(0, dp(2), 0, dp(6));
        body.addView(randomPoolTitle);
        LinearLayout randomPools = new LinearLayout(this);
        randomPools.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        boolean hasFavorites = !favoriteThemeIndices().isEmpty();
        randomPools.addView(themeRandomPoolChoice("همهٔ تم‌ها", "all".equals(randomPool), "all", true),
                new LinearLayout.LayoutParams(0, dp(46), 1));
        View poolSpacer = new View(this);
        randomPools.addView(poolSpacer, new LinearLayout.LayoutParams(dp(8), 1));
        randomPools.addView(themeRandomPoolChoice("تم‌های پسندیده", "favorites".equals(randomPool),
                "favorites", hasFavorites), new LinearLayout.LayoutParams(0, dp(46), 1));
        body.addView(randomPools, fieldParams());
        if (!hasFavorites) {
            TextView favoritesHint = text("برای این حالت، دست‌کم یک تم را پسندیده کنید.", 12, MUTED);
            favoritesHint.setPadding(0, 0, 0, dp(7));
            body.addView(favoritesHint);
        }

        TextView themesTitle = text("همهٔ تم‌ها", 15, MUTED);
        themesTitle.setPadding(0, dp(9), 0, dp(7));
        body.addView(themesTitle);
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(2);
        grid.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        grid.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        for (int i = 0; i < themeCount(); i++) {
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            int screenWidthDp = (int) (getResources().getDisplayMetrics().widthPixels
                    / getResources().getDisplayMetrics().density);
            int widthDp = Math.max(132, (screenWidthDp - 48) / 2);
            params.width = dp(widthDp);
            params.height = dp(56);
            params.setMargins(dp(3), dp(3), dp(3), dp(3));
            grid.addView(themeCard(i), params);
        }
        body.addView(grid, new LinearLayout.LayoutParams(-1, -2));
        LinearLayout.LayoutParams addParams = new LinearLayout.LayoutParams(-1, dp(50));
        addParams.topMargin = dp(11);
        body.addView(textButton("افزودن تم جدید", "add", PURPLE, this::showAddThemeDialog), addParams);
        scroll.addView(body);
        appColumn.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
    }

    private TextView themeRandomPoolChoice(String label, boolean selected, String pool, boolean enabled) {
        TextView choice = text(label, 14, selected ? contrastOnAccent() : TEXT);
        choice.setGravity(Gravity.CENTER);
        choice.setSingleLine(true);
        choice.setEllipsize(android.text.TextUtils.TruncateAt.END);
        choice.setEnabled(enabled);
        choice.setAlpha(enabled ? 1f : .5f);
        choice.setBackground(roundDrawable(selected ? PURPLE : SURFACE,
                selected ? PURPLE : 0xFF40384A, 12));
        choice.setOnClickListener(v -> {
            if (!enabled) return;
            getSharedPreferences("appearance", MODE_PRIVATE).edit()
                    .putString("random_theme_pool", pool).apply();
            showThemes();
        });
        return choice;
    }

    private View themeCard(int index) {
        LinearLayout card = new LinearLayout(this);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        card.setPadding(dp(8), dp(5), dp(8), dp(5));
        card.setBackground(roundDrawable(SURFACE, index == accentIndex ? PURPLE : 0xFF40384A, 12));
        card.setClickable(true);
        card.setFocusable(true);

        FrameLayout favorite = new FrameLayout(this);
        NativeIconView heart = new NativeIconView(this, isThemeFavorite(index) ? "heart_filled" : "heart_outline",
                isThemeFavorite(index) ? RED : MUTED);
        favorite.addView(heart, new FrameLayout.LayoutParams(dp(20), dp(20), Gravity.CENTER));
        favorite.setContentDescription("پسندیدن تم");
        favorite.setClickable(true);
        favorite.setFocusable(true);
        favorite.setOnClickListener(v -> {
            setThemeFavorite(index, !isThemeFavorite(index));
            rerenderCurrentPage();
        });
        card.addView(favorite, new LinearLayout.LayoutParams(dp(30), dp(40)));

        GradientDrawable preview = new GradientDrawable();
        preview.setCornerRadius(dp(9));
        if (isGradientTheme(index)) applyGradientTheme(preview, index, 19);
        else preview.setColor(themeAccentColor(index));
        FrameLayout swatch = new FrameLayout(this);
        swatch.setBackground(preview);
        card.addView(swatch, new LinearLayout.LayoutParams(dp(30), dp(30)));

        TextView label = text(themeName(index), 12, TEXT);
        label.setMaxLines(2);
        label.setEllipsize(android.text.TextUtils.TruncateAt.END);
        label.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(0, -1, 1);
        labelParams.rightMargin = dp(4);
        card.addView(label, labelParams);
        card.setOnClickListener(v -> {
            accentIndex = index;
            getSharedPreferences("appearance", MODE_PRIVATE).edit().putInt("accent", accentIndex).apply();
            applyThemeColors();
            rerenderCurrentPage();
        });
        applyPressScale(card, .97f);
        return card;
    }

    private void showAddThemeDialog() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(4), dp(4), dp(4), dp(4));
        EditText name = edit("نام تم", false);
        EditText colors = edit("#B69AFF یا #B69AFF, #7046C8", false);
        colors.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        form.addView(name, fieldParams());
        form.addView(colors, fieldParams());
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("تم جدید")
                .setView(dialogPadding(form)).setPositiveButton("افزودن", null)
                .setNegativeButton("لغو", null).create();
        dialog.setOnShowListener(ignored -> {
            styleDialogWindow(dialog);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String themeName = name.getText().toString().trim();
                String[] rawColors = colors.getText().toString().split(",");
                if (themeName.isEmpty() || rawColors.length < 1 || rawColors.length > 2) {
                    toast("نام تم و یک یا دو رنگ وارد کن.");
                    return;
                }
                JSONArray parsedColors = new JSONArray();
                try {
                    for (String raw : rawColors) {
                        String value = raw.trim();
                        if (!value.startsWith("#")) value = "#" + value;
                        parsedColors.put(Color.parseColor(value));
                    }
                    JSONArray themes = customThemes();
                    JSONObject theme = new JSONObject();
                    theme.put("name", themeName);
                    theme.put("colors", parsedColors);
                    theme.put("favorite", false);
                    themes.put(theme);
                    getSharedPreferences("appearance", MODE_PRIVATE).edit()
                            .putString("custom_themes", themes.toString()).apply();
                    accentIndex = BUILTIN_THEME_COUNT + themes.length() - 1;
                    getSharedPreferences("appearance", MODE_PRIVATE).edit().putInt("accent", accentIndex).apply();
                    dialog.dismiss();
                    applyThemeColors();
                    rerenderCurrentPage();
                } catch (IllegalArgumentException | JSONException error) {
                    toast("رنگ را با قالب #RRGGBB وارد کن.");
                }
            });
        });
        dialog.show();
        styleDialogWindow(dialog);
    }

    private void addPreferenceSwitch(LinearLayout body, String label, String preferenceFile,
                                     String key, boolean defaultValue) {
        SharedPreferences preferences = getSharedPreferences(preferenceFile, MODE_PRIVATE);
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        row.setPadding(dp(14), 0, dp(12), 0);
        row.setBackground(roundDrawable(SURFACE, 0xFF302A38, 12));
        TextView labelView = text(label, 14, TEXT);
        row.addView(labelView, new LinearLayout.LayoutParams(0, -1, 1));
        Switch toggle = new Switch(this);
        toggle.setChecked(preferences.getBoolean(key, defaultValue));
        if (Build.VERSION.SDK_INT >= 21) {
            int[][] states = new int[][]{{android.R.attr.state_checked}, new int[0]};
            toggle.setThumbTintList(new android.content.res.ColorStateList(states, new int[]{PURPLE, MUTED}));
            toggle.setTrackTintList(new android.content.res.ColorStateList(states,
                    new int[]{Color.argb(100, Color.red(PURPLE), Color.green(PURPLE), Color.blue(PURPLE)), 0xFF625D66}));
        }
        toggle.setOnCheckedChangeListener((button, checked) -> {
            preferences.edit().putBoolean(key, checked).apply();
        });
        row.addView(toggle, new LinearLayout.LayoutParams(-2, -2));
        row.setOnClickListener(v -> toggle.setChecked(!toggle.isChecked()));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp(52));
        params.bottomMargin = dp(8);
        body.addView(row, params);
    }

    private TextView themeChoice(String label, boolean selected, boolean light) {
        TextView choice = text(label, 15, selected ? contrastOnAccent() : TEXT);
        choice.setGravity(Gravity.CENTER);
        choice.setBackground(roundDrawable(selected ? PURPLE : SURFACE, selected ? PURPLE : 0xFF40384A, 12));
        choice.setOnClickListener(v -> {
            lightTheme = light;
            getSharedPreferences("appearance", MODE_PRIVATE).edit().putBoolean("light", lightTheme).apply();
            applyThemeColors();
            rerenderCurrentPage();
        });
        return choice;
    }

    private void rerenderCurrentPage() {
        boolean settingsPage = "settings".equals(page) || "themes".equals(page);
        int settingsScrollY = settingsPage && settingsScroll != null
                ? settingsScroll.getScrollY() : 0;
        if (systemRoot != null) systemRoot.setBackgroundColor(BG);
        if ("chapters".equals(page)) showChapters();
        else if ("manhwaInfo".equals(page) || "animeInfo".equals(page)) showManhwaInfo(activeManhwa());
        else if ("settings".equals(page)) showSettings();
        else if ("themes".equals(page)) showThemes();
        else if ("recommendations".equals(page) || "animeRecommendations".equals(page)) showRecommendation();
        else if ("statistics".equals(page)) showStatistics();
        else if ("tagSettings".equals(page)) showTagSettings();
        else if ("allManhwas".equals(page) || "allAnimes".equals(page)) showManhwaShelfPage();
        else showDashboard();
        if (settingsPage && settingsScroll != null) {
            settingsScroll.post(() -> settingsScroll.scrollTo(0, settingsScrollY));
        }
    }

    private void openDrawer() {
        if (drawerOpen && drawerLayer.getVisibility() == View.VISIBLE) return;
        settleDrawer(true);
    }

    private void closeDrawer() {
        if (!drawerOpen && drawerLayer.getVisibility() != View.VISIBLE) return;
        settleDrawer(false);
    }

    private int drawerWidth() {
        if (drawerPanel != null && drawerPanel.getWidth() > 0) return drawerPanel.getWidth();
        return Math.round(getResources().getDisplayMetrics().widthPixels * (2f / 3f));
    }

    private void setDrawerProgress(float progress) {
        float bounded = Math.max(0f, Math.min(1f, progress));
        drawerLayer.setVisibility(View.VISIBLE);
        drawerPanel.setTranslationX(drawerWidth() * (1f - bounded));
        drawerScrim.setAlpha(bounded);
    }

    private void settleDrawer(boolean open) {
        drawerOpen = open;
        drawerLayer.setVisibility(View.VISIBLE);
        drawerScrim.animate().cancel();
        drawerPanel.animate().cancel();
        float targetX = open ? 0f : drawerWidth();
        float distance = Math.abs(drawerPanel.getTranslationX() - targetX);
        long duration = Math.max(90, Math.min(210, Math.round(210f * distance / Math.max(1, drawerWidth()))));
        DecelerateInterpolator easing = new DecelerateInterpolator();
        drawerScrim.animate().alpha(open ? 1f : 0f).setDuration(duration).setInterpolator(easing).start();
        drawerPanel.animate().translationX(targetX).setDuration(duration).setInterpolator(easing)
                .withEndAction(() -> { if (!drawerOpen) drawerLayer.setVisibility(View.GONE); }).start();
    }

    private boolean drawerSwipeAvailable() {
        return "allManhwas".equals(page) || "allAnimes".equals(page) || "settings".equals(page)
                || "recommendations".equals(page) || "animeRecommendations".equals(page)
                || "tagSettings".equals(page);
    }

    private void beginDrawerGesture(float progress) {
        drawerScrim.animate().cancel();
        drawerPanel.animate().cancel();
        setDrawerProgress(progress);
    }

    private void updateDrawerGesture(float progress) {
        drawerPanel.setTranslationX(drawerWidth() * (1f - Math.max(0f, Math.min(1f, progress))));
        drawerScrim.setAlpha(Math.max(0f, Math.min(1f, progress)));
    }

    private void settleDrawerGesture(boolean open) {
        settleDrawer(open);
    }

    private void showDashboard() {
        selectedManhwaIds.clear();
        activeGenreFilter.clear();
        showManhwaShelfPage();
    }

    private void animatePageTransition() {
        if (appColumn == null) return;
        appColumn.animate().cancel();
        appColumn.setAlpha(0.78f);
        appColumn.setTranslationY(dp(7));
        appColumn.animate().alpha(1f).translationY(0f)
                .setDuration(190)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private boolean showingAllManhwas() { return "allManhwas".equals(page) || "allAnimes".equals(page); }

    private void showAllManhwas() {
        setAnimeMode(false);
        showManhwaShelfPage();
    }

    private void showAllAnimes() {
        setAnimeMode(true);
        showManhwaShelfPage();
    }

    private void showRecommendation() {
        page = animeMode ? "animeRecommendations" : "recommendations";
        appColumn.removeAllViews();
        animatePageTransition();
        appColumn.addView(makeHeader("پیشنهاد " + (animeMode ? "انیمه" : "مانهوا"), "", "menu", null, this::openDrawer, null));
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setGravity(Gravity.CENTER);
        body.setPadding(dp(24), dp(20), dp(24), dp(20));

        List<ManhwaRecommendationEngine.Manhwa> availableManhwas = recommendationManhwas();
        ManhwaRecommendationEngine.Manhwa manhwa = currentRecommendationEngine()
                .recommendManhwa(availableManhwas, currentSeenRecommendationIds());
        if (manhwa == null) {
            String emptyMessage = (animeMode ? "انیمه" : "مانهوا") + " ناتمام و قابل‌پیشنهادی وجود ندارد";
            body.addView(emptyLabel(emptyMessage), new LinearLayout.LayoutParams(-1, 0, 1));
            appColumn.addView(body, new LinearLayout.LayoutParams(-1, 0, 1));
            return;
        }

        currentSeenRecommendationIds().add(manhwa.id);
        persistRecommendationState();
        JSONObject recommendedRecord = findManhwaByRecommendationId(manhwa.id);
        FrameLayout coverFrame = new FrameLayout(this);
        coverFrame.setBackground(roundDrawable(SURFACE_ALT, Color.TRANSPARENT, 14));
        coverFrame.setClipToOutline(true);
        ImageView cover = new ImageView(this);
        cover.setScaleType(ImageView.ScaleType.FIT_CENTER);
        cover.setBackground(roundDrawable(SURFACE_ALT, Color.TRANSPARENT, 14));
        coverFrame.addView(cover, new FrameLayout.LayoutParams(-1, -1));
        NativeIconView manhwaIcon = new NativeIconView(this, animeMode ? "anime" : "manhwa", PURPLE);
        coverFrame.addView(manhwaIcon, new FrameLayout.LayoutParams(dp(48), dp(48), Gravity.CENTER));
        if (recommendedRecord != null) bindCover(cover, manhwaIcon, recommendedRecord.optString("cover", ""));
        coverFrame.setClickable(true);
        coverFrame.setOnClickListener(v -> {
            if (recommendedRecord == null) return;
            manhwaInfoReturnPage = page;
            showManhwaInfo(recommendedRecord);
        });
        body.addView(coverFrame, new LinearLayout.LayoutParams(dp(184), dp(238)));
        TextView title = text(recommendedRecord == null ? manhwa.title : displayManhwaTitle(recommendedRecord), 22, TEXT);
        title.setGravity(Gravity.CENTER);
        title.setMaxLines(3);
        title.setPadding(0, dp(22), 0, dp(10));
        body.addView(title, new LinearLayout.LayoutParams(-1, -2));
        String descriptionText = recommendedRecord == null ? "" : recommendedRecord.optString("description", "").trim();
        TextView description;
        if (descriptionText.isEmpty()) {
            description = text("توضیحی برای این " + (animeMode ? "انیمه" : "مانهوا") + " ثبت نشده است.", 14, MUTED);
        } else {
            boolean more = descriptionText.length() > 90;
            String preview = more ? descriptionText.substring(0, 90).trim() + "… بیشتر" : descriptionText;
            preview = englishDigits(preview);
            android.text.SpannableString styled = new android.text.SpannableString(preview);
            if (more) styled.setSpan(new ForegroundColorSpan(RED), preview.length() - 5,
                    preview.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            description = text(preview, 14, MUTED);
            description.setText(styled);
            description.setOnClickListener(v -> showRecommendationDescription(descriptionText));
            description.setClickable(true);
            description.setFocusable(true);
        }
        description.setGravity(Gravity.CENTER);
        description.setMaxLines(4);
        description.setEllipsize(android.text.TextUtils.TruncateAt.END);
        description.setPadding(dp(8), 0, dp(8), 0);
        LinearLayout.LayoutParams descriptionParams = new LinearLayout.LayoutParams(-1, -2);
        descriptionParams.topMargin = dp(12);
        body.addView(description, descriptionParams);

        View spacer = new View(this);
        body.addView(spacer, new LinearLayout.LayoutParams(1, 0, 1));
        Runnable openRecommendedChapters = () -> {
            if (recommendedRecord == null) return;
            activeManhwaId = recommendedRecord.optString("id");
            manhwaInfoReturnPage = page;
            chaptersReturnPage = page;
            showChapters();
        };
        Runnable openRecommendedDetails = () -> {
            if (recommendedRecord == null) return;
            manhwaInfoReturnPage = page;
            showManhwaInfo(recommendedRecord);
        };
        body.addView(textButton("جزئیات", "manhwa", PURPLE, openRecommendedDetails),
                new LinearLayout.LayoutParams(-1, dp(46)));
        LinearLayout.LayoutParams chaptersButtonParams = new LinearLayout.LayoutParams(-1, dp(48));
        chaptersButtonParams.topMargin = dp(6);
        body.addView(textButton("چک‌لیست " + contentUnitPlural(), "folder", PURPLE, openRecommendedChapters),
                chaptersButtonParams);
        LinearLayout.LayoutParams nextButtonParams = new LinearLayout.LayoutParams(-1, dp(48));
        nextButtonParams.topMargin = dp(7);
        body.addView(textButton("پیشنهاد بعدی", "next", themeSecondaryColor(), this::showRecommendation), nextButtonParams);
        appColumn.addView(body, new LinearLayout.LayoutParams(-1, 0, 1));
    }

    private void showRecommendationDescription(String value) {
        TextView description = text(value, 15, TEXT);
        description.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        description.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG_RTL);
        description.setPadding(dp(14), dp(12), dp(14), dp(12));
        ScrollView scroll = new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.addView(description, new ScrollView.LayoutParams(-1, -2));
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("توضیحات " + contentTitleLabel())
                .setView(dialogPadding(scroll)).setPositiveButton("بستن", null).create();
        dialog.show();
        styleDialogWindow(dialog);
    }

    private void showStatistics() {
        page = "statistics";
        appColumn.removeAllViews();
        animatePageTransition();
        appColumn.addView(makeHeader("پیشرفت " + contentUnitPlural(), "", "menu", null, this::openDrawer, null));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(12), dp(16), dp(20));
        ScrollView scroll = new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.addView(content);

        JSONArray manhwas = contentItems();
        int total = 0;
        int read = 0;
        int ready = 0;
        if (manhwas != null) for (int i = 0; i < manhwas.length(); i++) {
            JSONObject manhwa = manhwas.optJSONObject(i);
            JSONArray chapters = checklistArray(manhwa);
            if (chapters == null) continue;
            total += chapters.length();
            for (int j = 0; j < chapters.length(); j++) {
                int state = chapterState(chapters.optJSONObject(j));
                if (state == 2) read++;
                else if (state == 1) ready++;
            }
        }

        LinearLayout summary = new LinearLayout(this);
        summary.setOrientation(LinearLayout.VERTICAL);
        summary.setGravity(Gravity.CENTER);
        summary.setPadding(dp(14), dp(16), dp(14), dp(16));
        summary.setBackground(roundDrawable(SURFACE, 0xFF302A38, 16));
        TextView count = text(formatCount(read) + " / " + formatCount(total), 25, PURPLE);
        count.setGravity(Gravity.CENTER);
        TextView caption = text(formatCount(ready) + " آمادهٔ خواندن  ·  " + contentUnitLabel()
                + " خوانده‌شده از کل " + contentUnitPlural(), 13, MUTED);
        caption.setGravity(Gravity.CENTER);
        summary.addView(count);
        summary.addView(caption);
        content.addView(summary, new LinearLayout.LayoutParams(-1, dp(100)));

        TextView sectionTitle = text("پیشرفت هر عنوان", 15, MUTED);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(-1, -2);
        titleParams.topMargin = dp(18);
        titleParams.bottomMargin = dp(8);
        content.addView(sectionTitle, titleParams);
        if (manhwas != null) for (int i = 0; i < manhwas.length(); i++) {
            JSONObject manhwa = manhwas.optJSONObject(i);
            if (manhwa == null) continue;
            JSONArray chapters = checklistArray(manhwa);
            int chapterCount = chapters == null ? 0 : chapters.length();
            int readCount = 0;
            int readyCount = 0;
            if (chapters != null) for (int j = 0; j < chapters.length(); j++) {
                JSONObject chapter = chapters.optJSONObject(j);
                int state = chapterState(chapter);
                if (state == 2) readCount++;
                else if (state == 1) readyCount++;
            }
            LinearLayout row = new LinearLayout(this);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(dp(14), dp(10), dp(14), dp(10));
            row.setBackground(roundDrawable(SURFACE, 0xFF302A38, 14));
            TextView title = text(displayManhwaTitle(manhwa), 14, TEXT);
            title.setMaxLines(1);
            title.setEllipsize(android.text.TextUtils.TruncateAt.END);
            TextView progress = text(formatCount(readCount) + " خوانده‌شده  ·  "
                    + formatCount(readyCount) + " آمادهٔ خواندن  ·  " + formatCount(chapterCount)
                    + " " + contentUnitLabel(), 12, MUTED);
            progress.setGravity(Gravity.RIGHT);
            row.addView(title, new LinearLayout.LayoutParams(-1, -2));
            LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(-1, -2);
            progressParams.topMargin = dp(4);
            row.addView(progress, progressParams);
            TextView genres = text(genreSummary(manhwa.optJSONArray("genres")), 11, MUTED);
            genres.setMaxLines(1);
            genres.setEllipsize(android.text.TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams genresParams = new LinearLayout.LayoutParams(-1, -2);
            genresParams.topMargin = dp(2);
            row.addView(genres, genresParams);
            addStatisticsProgressTube(row, readCount, chapterCount, readyCount);
            row.setOnClickListener(v -> {
                manhwaInfoReturnPage = "statistics";
                showManhwaInfo(manhwa);
            });
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, -2);
            rowParams.bottomMargin = dp(8);
            content.addView(row, rowParams);
        }
        appColumn.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
    }

    private void addStatisticsProgressTube(LinearLayout parent, int read, int total, int ready) {
        FrameLayout tube = new FrameLayout(this);
        tube.setBackground(roundDrawable(SURFACE_ALT, Color.TRANSPARENT, 8));
        int height = dp(8);
        View readyFill = new View(this);
        readyFill.setBackground(roundDrawable(themeSecondaryColor(), Color.TRANSPARENT, 8));
        tube.addView(readyFill, new FrameLayout.LayoutParams(0, height, Gravity.LEFT));
        View readFill = new View(this);
        readFill.setBackground(roundDrawable(PURPLE, Color.TRANSPARENT, 8));
        tube.addView(readFill, new FrameLayout.LayoutParams(0, height, Gravity.LEFT));
        tube.post(() -> {
            int width = tube.getWidth();
            float readFraction = total <= 0 ? 0f : Math.min(1f, read / (float) total);
            float readyFraction = total <= 0 ? 0f : Math.min(1f, (read + ready) / (float) total);
            FrameLayout.LayoutParams readyParams = (FrameLayout.LayoutParams) readyFill.getLayoutParams();
            readyParams.width = Math.max(0, Math.round(width * readyFraction));
            readyFill.setLayoutParams(readyParams);
            FrameLayout.LayoutParams readParams = (FrameLayout.LayoutParams) readFill.getLayoutParams();
            readParams.width = Math.max(0, Math.round(width * readFraction));
            readFill.setLayoutParams(readParams);
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, height);
        params.topMargin = dp(9);
        parent.addView(tube, params);
    }

    private String formatCount(long value) {
        return java.text.NumberFormat.getIntegerInstance(Locale.US).format(Math.max(0, value));
    }

    private int chapterState(JSONObject chapter) {
        if (chapter == null) return 0;
        int state = chapter.has("state") ? chapter.optInt("state", 0)
                : chapter.optBoolean("done", false) ? 2 : 0;
        return Math.max(0, Math.min(2, state));
    }

    private void showTagSettings() {
        page = "tagSettings";
        ensureFavoriteTagsDefined();
        appColumn.removeAllViews();
        animatePageTransition();
        appColumn.addView(makeTagSettingsHeader(), new LinearLayout.LayoutParams(-1, dp(62)));
        ScrollView scroll = new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setHorizontalScrollBarEnabled(false);
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(18), dp(14), dp(18), dp(24));
        EditText tagSearch = edit("جستجو یا افزودن تگ", false);
        tagSearch.setSingleLine(true);
        managedTagSearch = tagSearch;
        LinearLayout tagRows = new LinearLayout(this);
        tagRows.setOrientation(LinearLayout.VERTICAL);
        managedTagRows = tagRows;
        LinearLayout searchRow = new LinearLayout(this);
        searchRow.setGravity(Gravity.CENTER_VERTICAL);
        searchRow.addView(clearableInput(tagSearch), new LinearLayout.LayoutParams(0, dp(48), 1));
        FrameLayout addTag = iconButton("add", PURPLE, false, () -> addManagedTag(tagSearch, tagRows));
        LinearLayout.LayoutParams addTagParams = new LinearLayout.LayoutParams(dp(48), dp(48));
        addTagParams.leftMargin = dp(7);
        searchRow.addView(addTag, addTagParams);
        body.addView(searchRow, fieldParams());
        body.addView(tagRows, new LinearLayout.LayoutParams(-1, -2));
        tagSearch.addTextChangedListener(watcher(() -> renderManagedTags(tagRows, tagSearch)));
        renderManagedTags(tagRows, tagSearch);

        LinearLayout.LayoutParams equivalentParams = new LinearLayout.LayoutParams(-1, dp(48));
        equivalentParams.topMargin = dp(8);
        body.addView(textButton("تگ‌های معادل", "tag", themeSecondaryColor(), this::showGenreEquivalents),
                equivalentParams);

        scroll.addView(body);
        appColumn.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
    }

    private void showTagSettingsHelp() {
        TextView help = text("پسندیدن یا نپسندیدن تگ‌ها روی پیشنهادها اثر می‌گذارد. نگه‌داشتن انگشت روی تگ، آن را برای ویرایش یا حذف انتخاب می‌کند.", 15, TEXT);
        help.setGravity(Gravity.CENTER);
        help.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        help.setPadding(dp(10), dp(12), dp(10), dp(12));
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("راهنمای تنظیمات تگ‌ها")
                .setView(dialogPadding(help))
                .setPositiveButton("بستن", null)
                .create();
        dialog.show();
        styleDialogWindow(dialog);
    }

    private void ensureFavoriteTagsDefined() {
        JSONArray tags = contentGenres();
        if (tags == null) tags = new JSONArray();
        int originalCount = tags.length();
        for (String favorite : currentFavoriteGenres()) appendUniqueGenre(tags, favorite);
        for (String disliked : currentDislikedGenres()) appendUniqueGenre(tags, disliked);
        if (tags.length() == originalCount) return;
        putJson(library, contentGenresKey(), tags);
        if (saveLibrary()) {
            rebuildRecommendationWeights();
            persistRecommendationState();
        }
    }

    private View makeTagSettingsHeader() {
        if (selectedTagNames.isEmpty()) {
            return makeHeader("تنظیمات تگ‌ها", "", "menu", "help",
                    this::openDrawer, this::showTagSettingsHelp, 0, 68, true);
        }
        View header = makeHeader(formatCount(selectedTagNames.size()) + " انتخاب‌شده", "",
                "delete", "edit", this::confirmDeleteSelectedTags, this::editSelectedTag, 0, 68, true);
        if (selectedTagNames.size() > 1) {
            View editButton = ((ViewGroup) header).findViewWithTag("header_right_action");
            if (editButton != null) {
                editButton.setAlpha(.35f);
                editButton.setEnabled(false);
                editButton.setClickable(false);
            }
        }
        return header;
    }

    private void refreshTagSelection() {
        if (!"tagSettings".equals(page) || appColumn == null) return;
        if (appColumn.getChildCount() > 0) appColumn.removeViewAt(0);
        appColumn.addView(makeTagSettingsHeader(), 0, new LinearLayout.LayoutParams(-1, dp(62)));
        if (managedTagRows != null && managedTagSearch != null) {
            renderManagedTags(managedTagRows, managedTagSearch);
        }
    }

    private void toggleManagedTagSelection(String tag) {
        if (!selectedTagNames.add(tag)) selectedTagNames.remove(tag);
        refreshTagSelection();
    }

    private void editSelectedTag() {
        if (selectedTagNames.size() != 1 || managedTagRows == null || managedTagSearch == null) return;
        String tag = selectedTagNames.iterator().next();
        showEditTagDialog(tag, managedTagRows, managedTagSearch);
    }

    private void confirmDeleteSelectedTags() {
        if (selectedTagNames.isEmpty()) return;
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("" + formatCount(selectedTagNames.size()) + " تگ و موارد مرتبط حذف شوند؟")
                .setNegativeButton("لغو", null)
                .setPositiveButton("حذف", (ignored, which) -> {
                    for (String tag : new ArrayList<>(selectedTagNames)) {
                        deleteTag(tag, managedTagRows, managedTagSearch);
                    }
                    selectedTagNames.clear();
                    refreshTagSelection();
                }).create();
        dialog.show();
        styleDialogWindow(dialog);
    }

    private void toggleGenreReaction(String tag, boolean liked) {
        Set<String> favorites = currentFavoriteGenres();
        Set<String> dislikes = currentDislikedGenres();
        if (liked) {
            if (!favorites.add(tag)) favorites.remove(tag);
            if (favorites.contains(tag)) removeIgnoreCase(dislikes, tag);
        } else {
            if (!dislikes.add(tag)) dislikes.remove(tag);
            if (dislikes.contains(tag)) removeIgnoreCase(favorites, tag);
        }
        rebuildRecommendationWeights();
        persistRecommendationState();
        if (managedTagRows != null && managedTagSearch != null) {
            renderManagedTags(managedTagRows, managedTagSearch);
        }
    }

    private void renderManagedTags(LinearLayout rows, EditText search) {
        rows.removeAllViews();
        String query = englishDigits(search.getText().toString().trim()).toLowerCase(Locale.ROOT);
        JSONArray tags = allGenres();
        for (int i = 0; i < tags.length(); i++) {
            String tag = tags.optString(i);
            if (!tag.toLowerCase(Locale.ROOT).contains(query)) continue;
            boolean favorite = containsIgnoreCase(currentFavoriteGenres(), tag);
            boolean disliked = containsIgnoreCase(currentDislikedGenres(), tag);
            boolean selected = selectedTagNames.contains(tag);
            LinearLayout row = new LinearLayout(this);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            row.setPadding(dp(8), dp(3), dp(8), dp(3));
            row.setBackground(roundDrawable(selected ? (lightTheme ? 0xFFEDE3FA : 0xFF241B2C) : SURFACE,
                    selected ? PURPLE : themedStrokeColor(0xFF302A38), 12));
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, dp(54));
            rowParams.bottomMargin = dp(7);
            TextView label = text(tag, 15, TEXT);
            label.setSingleLine(true);
            label.setEllipsize(android.text.TextUtils.TruncateAt.END);
            FrameLayout like = iconButton("thumb_up", favorite ? PURPLE : MUTED, favorite,
                    () -> toggleGenreReaction(tag, true));
            like.setContentDescription(favorite ? "حذف پسند تگ" : "پسندیدن تگ");
            FrameLayout dislike = iconButton("thumb_down", disliked ? themeSecondaryColor() : MUTED, disliked,
                    () -> toggleGenreReaction(tag, false));
            dislike.setContentDescription(disliked ? "حذف نپسندیدن تگ" : "نپسندیدن تگ");
            row.addView(like, new LinearLayout.LayoutParams(dp(40), dp(42)));
            row.addView(dislike, new LinearLayout.LayoutParams(dp(40), dp(42)));
            row.addView(label, new LinearLayout.LayoutParams(0, -1, 1));
            View.OnLongClickListener select = v -> { toggleManagedTagSelection(tag); return true; };
            row.setOnLongClickListener(select);
            label.setOnLongClickListener(select);
        like.setOnLongClickListener(select);
        dislike.setOnLongClickListener(select);
            rows.addView(row, rowParams);
        }
        if (rows.getChildCount() == 0) {
            TextView empty = emptyLabel(query.isEmpty() ? "هنوز تگی ساخته نشده" : "تگی پیدا نشد");
            rows.addView(empty, new LinearLayout.LayoutParams(-1, dp(80)));
        }
    }

    private void removeIgnoreCase(Set<String> values, String value) {
        for (String current : new ArrayList<>(values)) if (current.equalsIgnoreCase(value)) values.remove(current);
    }

    private void addManagedTag(EditText search, LinearLayout rows) {
        String tag = canonicalGenre(titleCaseWords(englishDigits(search.getText().toString())));
        if (tag.isEmpty()) { search.setError("نام تگ را وارد کن"); return; }
        JSONArray tags = contentGenres();
        if (tags == null) tags = new JSONArray();
        if (findGenreName(tags, tag) != null) {
            search.setText("");
            toast("این نام به تگ «" + tag + "» تبدیل می‌شود.");
            renderManagedTags(rows, search);
            return;
        }
        appendUniqueGenre(tags, tag);
        putJson(library, contentGenresKey(), tags);
        if (!saveLibrary()) return;
        rebuildRecommendationWeights();
        persistRecommendationState();
        search.setText("");
        renderManagedTags(rows, search);
    }

    private void showEditTagDialog(String oldTag, LinearLayout rows, EditText search) {
        EditText name = edit("نام تگ", false);
        name.setText(englishDigits(oldTag));
        name.setSelection(name.length());
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setGravity(Gravity.CENTER_HORIZONTAL);
        form.addView(clearableInput(name), fieldParams());
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("ویرایش تگ")
                .setView(dialogPadding(form)).setNegativeButton("لغو", null)
                .setPositiveButton("ذخیره", null).create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String newTag = titleCaseWords(name.getText().toString());
            if (newTag.isEmpty()) { name.setError("نام تگ را وارد کن"); return; }
            String existing = findGenreName(contentGenres(), newTag);
            if (existing != null && !existing.equalsIgnoreCase(oldTag)) {
                AlertDialog merge = new AlertDialog.Builder(this)
                        .setMessage("همهٔ موارد دارای «" + oldTag + "» به «" + existing + "» تغییر کنند؟")
                        .setNegativeButton("لغو", null)
                        .setPositiveButton("ادغام", (ignoredDialog, which) -> {
                            if (renameTag(oldTag, existing)) {
                                dialog.dismiss();
                                renderManagedTags(rows, search);
                            }
                        }).create();
                merge.show();
                styleDialogWindow(merge);
                return;
            }
            if (renameTag(oldTag, newTag)) {
                dialog.dismiss();
                renderManagedTags(rows, search);
            }
        }));
        dialog.show();
        styleDialogWindow(dialog);
        dialog.getWindow().setSoftInputMode(WindowManagerFlags.ADJUST_RESIZE);
    }

    private boolean renameTag(String oldTag, String newTag) {
        JSONArray tags = contentGenres();
        String existing = tags == null ? null : findGenreName(tags, newTag);
        if (existing != null && !existing.equalsIgnoreCase(oldTag)) newTag = existing;
        if (oldTag.equals(newTag)) return true;
        String before = library.toString();
        Set<String> oldFavorites = new LinkedHashSet<>(currentFavoriteGenres());
        Set<String> oldDislikes = new LinkedHashSet<>(currentDislikedGenres());
        Set<String> oldFilters = new HashSet<>(activeGenreFilter);
        try {
            library.put(contentGenresKey(), renamedTags(tags, oldTag, newTag));
            JSONArray manhwas = contentItems();
            if (manhwas != null) for (int i = 0; i < manhwas.length(); i++) {
                JSONObject manhwa = manhwas.optJSONObject(i);
                if (manhwa != null) manhwa.put("genres", renamedTags(manhwa.optJSONArray("genres"), oldTag, newTag));
            }
            JSONArray groups = genreEquivalentGroups();
            JSONArray renamedGroups = new JSONArray();
            for (int i = 0; i < groups.length(); i++) {
                JSONObject group = groups.optJSONObject(i);
                if (group == null) continue;
                String primary = group.optString("primary", "");
                JSONArray aliases = group.optJSONArray("aliases");
                JSONArray renamedAliases = new JSONArray();
                if (aliases != null) for (int j = 0; j < aliases.length(); j++) {
                    String alias = aliases.optString(j);
                    String replacement = alias.equalsIgnoreCase(oldTag) ? newTag : alias;
                    if (!replacement.equalsIgnoreCase(primary) && !replacement.equalsIgnoreCase(newTag)) {
                        appendUniqueRawGenre(renamedAliases, replacement);
                    }
                }
                if (primary.equalsIgnoreCase(oldTag)) primary = newTag;
                if (renamedAliases.length() > 0) {
                    JSONObject renamedGroup = new JSONObject();
                    putJson(renamedGroup, "primary", primary);
                    putJson(renamedGroup, "aliases", renamedAliases);
                    renamedGroups.put(renamedGroup);
                }
            }
            library.put(contentGenreEquivalentsKey(), renamedGroups);
            canonicalizeLibraryGenres();
        } catch (JSONException ignored) { return false; }
        replaceSetValue(currentFavoriteGenres(), oldTag, newTag);
        replaceSetValue(currentDislikedGenres(), oldTag, newTag);
        replaceSetValue(selectedTagNames, oldTag, newTag);
        replaceSetValue(activeGenreFilter, oldTag, newTag);
        if (!saveLibrary()) {
            restoreLibrary(before);
            currentFavoriteGenres().clear(); currentFavoriteGenres().addAll(oldFavorites);
            currentDislikedGenres().clear(); currentDislikedGenres().addAll(oldDislikes);
            activeGenreFilter.clear(); activeGenreFilter.addAll(oldFilters);
            return false;
        }
        rebuildRecommendationWeights();
        persistRecommendationState();
        toast("تگ و موارد مرتبط به‌روزرسانی شدند");
        return true;
    }

    private void confirmDeleteTag(String tag, LinearLayout rows, EditText search) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("ژانر «" + tag + "» از فهرست و موارد مرتبط حذف شود؟")
                .setNegativeButton("لغو", null)
                .setPositiveButton("حذف", (ignored, which) -> deleteTag(tag, rows, search))
                .create();
        dialog.show();
        styleDialogWindow(dialog);
    }

    private void deleteTag(String tag, LinearLayout rows, EditText search) {
        String before = library.toString();
        Set<String> oldFavorites = new LinkedHashSet<>(currentFavoriteGenres());
        Set<String> oldDislikes = new LinkedHashSet<>(currentDislikedGenres());
        Set<String> oldFilters = new HashSet<>(activeGenreFilter);
        JSONArray tags = contentGenres();
        JSONArray remaining = new JSONArray();
        if (tags != null) for (int i = 0; i < tags.length(); i++) {
            String current = tags.optString(i);
            if (!current.equalsIgnoreCase(tag)) remaining.put(current);
        }
        try {
            library.put(contentGenresKey(), remaining);
            JSONArray manhwas = contentItems();
            if (manhwas != null) for (int i = 0; i < manhwas.length(); i++) {
                JSONObject manhwa = manhwas.optJSONObject(i);
                if (manhwa == null) continue;
                JSONArray genres = manhwa.optJSONArray("genres");
                JSONArray kept = new JSONArray();
                if (genres != null) for (int j = 0; j < genres.length(); j++) {
                    String value = genres.optString(j);
                    if (!value.equalsIgnoreCase(tag)) kept.put(value);
                }
                manhwa.put("genres", kept);
            }
            JSONArray groups = genreEquivalentGroups();
            JSONArray keptGroups = new JSONArray();
            if (groups != null) for (int i = 0; i < groups.length(); i++) {
                JSONObject group = groups.optJSONObject(i);
                if (group == null) continue;
                if (group.optString("primary").equalsIgnoreCase(tag)) continue;
                JSONArray aliases = group.optJSONArray("aliases");
                JSONArray keptAliases = new JSONArray();
                if (aliases != null) for (int j = 0; j < aliases.length(); j++) {
                    if (!aliases.optString(j).equalsIgnoreCase(tag)) {
                        appendUniqueRawGenre(keptAliases, aliases.optString(j));
                    }
                }
                if (keptAliases.length() > 0) {
                    JSONObject copy = new JSONObject();
                    putJson(copy, "primary", group.optString("primary"));
                    putJson(copy, "aliases", keptAliases);
                    keptGroups.put(copy);
                }
            }
            library.put(contentGenreEquivalentsKey(), keptGroups);
        } catch (JSONException error) { return; }
        removeIgnoreCase(currentFavoriteGenres(), tag);
        removeIgnoreCase(currentDislikedGenres(), tag);
        removeIgnoreCase(selectedTagNames, tag);
        removeIgnoreCase(activeGenreFilter, tag);
        if (!saveLibrary()) {
            restoreLibrary(before);
            currentFavoriteGenres().clear(); currentFavoriteGenres().addAll(oldFavorites);
            currentDislikedGenres().clear(); currentDislikedGenres().addAll(oldDislikes);
            activeGenreFilter.clear(); activeGenreFilter.addAll(oldFilters);
            return;
        }
        rebuildRecommendationWeights();
        persistRecommendationState();
        renderManagedTags(rows, search);
    }

    private JSONArray genreEquivalentGroups() {
        JSONArray groups = library.optJSONArray(contentGenreEquivalentsKey());
        return groups == null ? new JSONArray() : groups;
    }

    private String canonicalGenre(String value) {
        String candidate = value == null ? "" : englishDigits(value.trim());
        if (candidate.isEmpty()) return "";
        JSONArray groups = genreEquivalentGroups();
        Set<String> visited = new HashSet<>();
        for (int pass = 0; pass <= groups.length(); pass++) {
            if (!visited.add(candidate.toLowerCase(Locale.ROOT))) break;
            String primary = null;
            for (int i = 0; i < groups.length() && primary == null; i++) {
                JSONObject group = groups.optJSONObject(i);
                if (group == null) continue;
                JSONArray aliases = group.optJSONArray("aliases");
                if (aliases == null) continue;
                for (int j = 0; j < aliases.length(); j++) {
                    if (candidate.equalsIgnoreCase(aliases.optString(j).trim())) {
                        primary = group.optString("primary", "").trim();
                        break;
                    }
                }
            }
            if (primary == null || primary.isEmpty()) break;
            candidate = primary;
        }
        String known = findGenreName(contentGenres(), candidate);
        return known == null ? titleCaseWords(candidate) : known;
    }

    private void canonicalizeGenrePreferences() {
        Set<String> favoriteGenres = new LinkedHashSet<>();
        Set<String> currentFavorites = currentFavoriteGenres();
        for (String value : currentFavorites) {
            String canonical = canonicalGenre(value);
            if (!canonical.isEmpty()) favoriteGenres.add(canonical);
        }
        currentFavorites.clear();
        currentFavorites.addAll(favoriteGenres);

        Set<String> filters = new HashSet<>();
        for (String value : activeGenreFilter) {
            String canonical = canonicalGenre(value);
            if (!canonical.isEmpty()) filters.add(canonical);
        }
        activeGenreFilter.clear();
        activeGenreFilter.addAll(filters);
    }

    private void canonicalizeLibraryGenres() {
        JSONArray known = contentGenres();
        JSONArray canonical = new JSONArray();
        if (known != null) for (int i = 0; i < known.length(); i++) {
            appendUniqueGenre(canonical, known.optString(i));
        }
        JSONArray manhwas = contentItems();
        if (manhwas != null) for (int i = 0; i < manhwas.length(); i++) {
            JSONObject manhwa = manhwas.optJSONObject(i);
            if (manhwa == null) continue;
            JSONArray source = manhwa.optJSONArray("genres");
            JSONArray manhwaGenres = new JSONArray();
            if (source != null) for (int j = 0; j < source.length(); j++) {
                appendUniqueGenre(manhwaGenres, source.optString(j));
            }
            putJson(manhwa, "genres", manhwaGenres);
            for (int j = 0; j < manhwaGenres.length(); j++) appendUniqueGenre(canonical, manhwaGenres.optString(j));
        }
        putJson(library, contentGenresKey(), canonical);
        canonicalizeGenrePreferences();
    }

    private List<String> allGenreCandidates() {
        LinkedHashSet<String> values = new LinkedHashSet<>();
        JSONArray genres = allGenres();
        for (int i = 0; i < genres.length(); i++) values.add(genres.optString(i));
        JSONArray groups = genreEquivalentGroups();
        for (int i = 0; i < groups.length(); i++) {
            JSONObject group = groups.optJSONObject(i);
            JSONArray aliases = group == null ? null : group.optJSONArray("aliases");
            if (aliases != null) for (int j = 0; j < aliases.length(); j++) values.add(aliases.optString(j));
        }
        return new ArrayList<>(values);
    }

    private void showGenreEquivalents() {
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(8), dp(4), dp(8), dp(4));
        LinearLayout rows = new LinearLayout(this);
        rows.setOrientation(LinearLayout.VERTICAL);
        ScrollView scroll = new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.addView(rows);
        content.addView(textButton("افزودن گروه معادل", "add", PURPLE,
                () -> showGenreEquivalentEditor(null, rows)), fieldParams());
        content.addView(scroll, new LinearLayout.LayoutParams(-1, dp(330)));
        renderGenreEquivalentRows(rows);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("تگ‌های معادل")
                .setView(dialogPadding(content)).setPositiveButton("بستن", null).create();
        dialog.setOnDismissListener(ignored -> {
            if ("tagSettings".equals(page)) showTagSettings();
        });
        dialog.show();
        styleDialogWindow(dialog);
    }

    private void renderGenreEquivalentRows(LinearLayout rows) {
        rows.removeAllViews();
        JSONArray groups = genreEquivalentGroups();
        for (int i = 0; i < groups.length(); i++) {
            JSONObject group = groups.optJSONObject(i);
            if (group == null) continue;
            String primary = group.optString("primary", "").trim();
            JSONArray aliases = group.optJSONArray("aliases");
            if (primary.isEmpty() || aliases == null || aliases.length() == 0) continue;
            LinearLayout row = new LinearLayout(this);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(10), dp(5), dp(8), dp(5));
            row.setBackground(roundDrawable(SURFACE, 0xFF302A38, 12));
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, dp(62));
            rowParams.topMargin = dp(6);
            LinearLayout labels = new LinearLayout(this);
            labels.setOrientation(LinearLayout.VERTICAL);
            labels.setGravity(Gravity.CENTER_VERTICAL);
            TextView main = text(primary, 15, TEXT);
            TextView equivalents = text(android.text.TextUtils.join(", ", jsonArrayStrings(aliases)), 12, MUTED);
            equivalents.setSingleLine(true);
            equivalents.setEllipsize(android.text.TextUtils.TruncateAt.END);
            labels.addView(main);
            labels.addView(equivalents);
            row.addView(labels, new LinearLayout.LayoutParams(0, -1, 1));
            row.addView(iconButton("edit", MUTED, false,
                    () -> showGenreEquivalentEditor(group, rows)), new LinearLayout.LayoutParams(dp(42), dp(42)));
            row.addView(iconButton("delete", RED, false,
                    () -> confirmDeleteGenreEquivalent(primary, rows)), new LinearLayout.LayoutParams(dp(42), dp(42)));
            rows.addView(row, rowParams);
        }
        if (rows.getChildCount() == 0) rows.addView(emptyLabel("هنوز تگ معادلی تعریف نشده است."),
                new LinearLayout.LayoutParams(-1, dp(74)));
    }

    private List<String> jsonArrayStrings(JSONArray array) {
        List<String> result = new ArrayList<>();
        if (array != null) for (int i = 0; i < array.length(); i++) result.add(array.optString(i));
        return result;
    }

    private void showGenreEquivalentEditor(JSONObject existing, LinearLayout rows) {
        String oldPrimary = existing == null ? "" : existing.optString("primary", "");
        final String[] primary = {oldPrimary};
        LinkedHashSet<String> aliases = new LinkedHashSet<>();
        JSONArray oldAliases = existing == null ? null : existing.optJSONArray("aliases");
        if (oldAliases != null) for (int i = 0; i < oldAliases.length(); i++) aliases.add(oldAliases.optString(i));

        TextView primaryChoice = text("تگ اصلی: " + (primary[0].isEmpty() ? "انتخاب نشده" : primary[0]), 15, TEXT);
        primaryChoice.setGravity(Gravity.CENTER);
        primaryChoice.setBackground(roundDrawable(SURFACE_ALT, 0xFF40384A, 12));
        TextView aliasChoice = text(equivalentAliasSummary(aliases), 14, TEXT);
        aliasChoice.setGravity(Gravity.CENTER);
        aliasChoice.setBackground(roundDrawable(SURFACE_ALT, 0xFF40384A, 12));
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setGravity(Gravity.CENTER_HORIZONTAL);
        form.setPadding(dp(4), dp(6), dp(4), dp(6));
        form.addView(primaryChoice, fieldParams());
        form.addView(aliasChoice, fieldParams());
        primaryChoice.setOnClickListener(v -> showGenrePrimaryPicker(primary, primaryChoice));
        aliasChoice.setOnClickListener(v -> showManhwaGenreSelectionDialog(allGenreCandidates(), aliases,
                (selected, available) -> {
                    aliases.clear();
                    aliases.addAll(selected);
                    aliasChoice.setText(equivalentAliasSummary(aliases));
                }));

        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("گروه تگ‌های معادل")
                .setView(dialogPadding(form)).setNegativeButton("لغو", null)
                .setPositiveButton("ذخیره", null).create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String main = titleCaseWords(primary[0]);
            if (main.isEmpty()) { toast("یک تگ اصلی انتخاب کن."); return; }
            LinkedHashSet<String> cleanAliases = new LinkedHashSet<>();
            for (String alias : aliases) {
                String clean = titleCaseWords(alias);
                if (!clean.isEmpty() && !clean.equalsIgnoreCase(main)) cleanAliases.add(clean);
            }
            if (cleanAliases.isEmpty()) { toast("حداقل یک تگ معادل انتخاب کن."); return; }
            if (saveGenreEquivalentGroup(oldPrimary, main, cleanAliases)) {
                dialog.dismiss();
                renderGenreEquivalentRows(rows);
            }
        }));
        dialog.show();
        styleDialogWindow(dialog);
    }

    private String equivalentAliasSummary(Collection<String> aliases) {
        return aliases.isEmpty() ? "انتخاب تگ‌های معادل" : android.text.TextUtils.join("، ", aliases);
    }

    private void showGenrePrimaryPicker(String[] selected, TextView target) {
        JSONArray tags = allGenres();
        String[] items = new String[tags.length() + 1];
        for (int i = 0; i < tags.length(); i++) items[i] = tags.optString(i);
        items[items.length - 1] = "＋ تگ تازه";
        new AlertDialog.Builder(this).setTitle("انتخاب تگ اصلی").setItems(items, (dialog, which) -> {
            if (which == items.length - 1) {
                showManhwaTextEntryDialog("تگ اصلی تازه", "نام تگ", "", value -> {
                    selected[0] = titleCaseWords(value);
                    target.setText("تگ اصلی: " + selected[0]);
                });
            } else {
                selected[0] = canonicalGenre(items[which]);
                target.setText("تگ اصلی: " + selected[0]);
            }
        }).show();
    }

    private boolean saveGenreEquivalentGroup(String oldPrimary, String primary, Set<String> selectedAliases) {
        String before = library.toString();
        Set<String> oldFavorites = new LinkedHashSet<>(currentFavoriteGenres());
        Set<String> oldFilters = new HashSet<>(activeGenreFilter);
        JSONArray current = genreEquivalentGroups();
        JSONArray rebuilt = new JSONArray();
        LinkedHashSet<String> mergedAliases = new LinkedHashSet<>();
        for (String alias : selectedAliases) if (!alias.equalsIgnoreCase(primary)) addGenreName(mergedAliases, alias);

        for (int i = 0; i < current.length(); i++) {
            JSONObject group = current.optJSONObject(i);
            if (group == null) continue;
            String groupPrimary = group.optString("primary", "").trim();
            JSONArray aliases = group.optJSONArray("aliases");
            boolean replacing = groupPrimary.equalsIgnoreCase(oldPrimary) && !oldPrimary.isEmpty();
            boolean samePrimary = groupPrimary.equalsIgnoreCase(primary);
            boolean merged = replacing || samePrimary;
            if (!merged && aliases != null) {
                if (containsIgnoreCase(selectedAliases, groupPrimary)) merged = true;
                for (int j = 0; aliases != null && j < aliases.length() && !merged; j++) {
                    if (aliases.optString(j).equalsIgnoreCase(primary)) merged = true;
                }
            }
            if (merged) {
                if (!replacing && aliases != null) for (int j = 0; j < aliases.length(); j++) {
                    String alias = titleCaseWords(aliases.optString(j));
                    if (!alias.isEmpty() && !alias.equalsIgnoreCase(primary)) addGenreName(mergedAliases, alias);
                }
                continue;
            }
            JSONArray keptAliases = new JSONArray();
            if (aliases != null) for (int j = 0; j < aliases.length(); j++) {
                String alias = aliases.optString(j).trim();
                if (!alias.equalsIgnoreCase(primary) && !containsIgnoreCase(selectedAliases, alias)) {
                    appendUniqueRawGenre(keptAliases, alias);
                }
            }
            if (keptAliases.length() > 0) {
                JSONObject keptGroup = new JSONObject();
                putJson(keptGroup, "primary", groupPrimary);
                putJson(keptGroup, "aliases", keptAliases);
                rebuilt.put(keptGroup);
            }
        }

        JSONArray finalAliases = new JSONArray();
        for (String alias : mergedAliases) appendUniqueRawGenre(finalAliases, alias);
        JSONObject finalGroup = new JSONObject();
        putJson(finalGroup, "primary", primary);
        putJson(finalGroup, "aliases", finalAliases);
        rebuilt.put(finalGroup);
        putJson(library, contentGenreEquivalentsKey(), rebuilt);
        JSONArray genres = contentGenres();
        if (genres == null) genres = new JSONArray();
        appendUniqueGenre(genres, primary);
        for (String alias : mergedAliases) appendUniqueGenre(genres, alias);
        putJson(library, contentGenresKey(), genres);
        canonicalizeLibraryGenres();
        if (!saveLibrary()) {
            restoreLibrary(before);
            currentFavoriteGenres().clear(); currentFavoriteGenres().addAll(oldFavorites);
            activeGenreFilter.clear(); activeGenreFilter.addAll(oldFilters);
            return false;
        }
        rebuildRecommendationWeights();
        persistRecommendationState();
        toast("تگ‌های معادل ذخیره شدند و موارد یکدست شدند.");
        return true;
    }

    private void confirmDeleteGenreEquivalent(String primary, LinearLayout rows) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("گروه معادل «" + primary + "» حذف شود؟")
                .setNegativeButton("لغو", null)
                .setPositiveButton("حذف", (ignored, which) -> {
                    JSONArray current = genreEquivalentGroups();
                    JSONArray remaining = new JSONArray();
                    for (int i = 0; i < current.length(); i++) {
                        JSONObject group = current.optJSONObject(i);
                        if (group != null && !group.optString("primary").equalsIgnoreCase(primary)) remaining.put(group);
                    }
                    putJson(library, "genreEquivalents", remaining);
                    if (saveLibrary()) renderGenreEquivalentRows(rows);
                }).create();
        dialog.show();
        styleDialogWindow(dialog);
    }

    private JSONArray renamedTags(JSONArray source, String oldTag, String newTag) {
        JSONArray result = new JSONArray();
        if (source == null) return result;
        for (int i = 0; i < source.length(); i++) {
            String value = source.optString(i);
            appendUniqueGenre(result, value.equalsIgnoreCase(oldTag) ? newTag : value);
        }
        return result;
    }

    private void replaceSetValue(Set<String> values, String oldValue, String newValue) {
        boolean found = false;
        for (String value : new ArrayList<>(values)) {
            if (value.equalsIgnoreCase(oldValue)) { values.remove(value); found = true; }
        }
        if (found) values.add(newValue);
    }

    private void addGenreName(Collection<String> genres, String value) {
        String candidate = value == null ? "" : value.trim();
        if (candidate.isEmpty()) return;
        for (String genre : genres) if (genre.equalsIgnoreCase(candidate)) return;
        genres.add(candidate);
    }

    private String findGenreName(Collection<String> genres, String value) {
        for (String genre : genres) if (genre.equalsIgnoreCase(value)) return genre;
        return null;
    }

    private String findGenreName(JSONArray genres, String value) {
        for (int i = 0; i < genres.length(); i++) {
            String genre = genres.optString(i);
            if (genre.equalsIgnoreCase(value)) return genre;
        }
        return null;
    }

    private String rememberRecommendationGenre(Map<String, String> genres, String value) {
        String name = value == null ? "" : value.trim();
        if (name.isEmpty() || "Other".equalsIgnoreCase(name)) return null;
        String key = name.toLowerCase(Locale.ROOT);
        if (!genres.containsKey(key)) genres.put(key, name);
        return key;
    }

    private void rebuildRecommendationWeights() {
        ManhwaRecommendationEngine.UserPreferences preferences = currentRecommendationPreferences();
        Set<String> preferredGenres = currentFavoriteGenres();
        Set<String> favoriteIds = currentFavoriteIds();
        Set<String> dislikedIds = currentDislikedIds();
        Set<String> dislikedGenres = currentDislikedGenres();
        Map<String, String> genreNames = new HashMap<>();
        JSONArray knownGenres = allGenres();
        for (int i = 0; i < knownGenres.length(); i++) {
            rememberRecommendationGenre(genreNames, knownGenres.optString(i));
        }

        Set<String> preferredGenreKeys = new HashSet<>();
        Set<String> dislikedGenreKeys = new HashSet<>();
        for (String genre : preferredGenres) {
            String key = rememberRecommendationGenre(genreNames, genre);
            if (key != null) preferredGenreKeys.add(key);
        }
        for (String genre : dislikedGenres) {
            String key = rememberRecommendationGenre(genreNames, genre);
            if (key != null) dislikedGenreKeys.add(key);
        }

        JSONArray items = contentItems();
        if (items != null) for (int i = 0; i < items.length(); i++) {
            JSONObject manhwa = items.optJSONObject(i);
            if (manhwa == null) continue;
            boolean liked = favoriteIds.contains(manhwa.optString("id"));
            boolean disliked = dislikedIds.contains(manhwa.optString("id"));
            if (!liked && !disliked) continue;
            JSONArray genres = manhwa.optJSONArray("genres");
            if (genres == null) continue;
            Set<String> unique = new HashSet<>();
            for (int j = 0; j < genres.length(); j++) {
                String key = rememberRecommendationGenre(genreNames, genres.optString(j));
                if (key != null) unique.add(key);
            }
            if (liked) preferredGenreKeys.addAll(unique);
            if (disliked) dislikedGenreKeys.addAll(unique);
        }

        preferences.genreWeights.clear();
        for (Map.Entry<String, String> entry : genreNames.entrySet()) {
            preferences.genreWeights.put(entry.getKey(), 10);
        }
        for (String key : preferredGenreKeys) {
            if (genreNames.containsKey(key)) preferences.genreWeights.put(key, 30);
        }
        for (String key : dislikedGenreKeys) {
            if (genreNames.containsKey(key) && !preferredGenreKeys.contains(key)) {
                preferences.genreWeights.put(key, 2);
            }
        }

    }

    private List<ManhwaRecommendationEngine.Manhwa> recommendationManhwas() {
        List<ManhwaRecommendationEngine.Manhwa> result = new ArrayList<>();
        JSONArray items = contentItems();
        if (items == null) return result;
        for (int i = 0; i < items.length(); i++) {
            JSONObject manhwa = items.optJSONObject(i);
            if (manhwa == null || manhwaReadPercent(manhwa) >= 100) continue;
            long id = manhwa.optLong("recommendationId", -1);
            JSONArray genres = manhwa.optJSONArray("genres");
            if (id <= 0) continue;
            List<String> genreNames = new ArrayList<>();
            Set<String> addedGenres = new HashSet<>();
            if (genres != null) for (int j = 0; j < genres.length(); j++) {
                String genre = genres.optString(j).trim();
                if (!genre.isEmpty() && !"Other".equalsIgnoreCase(genre)
                        && addedGenres.add(genre.toLowerCase(Locale.ROOT))) genreNames.add(genre);
            }
            String title = displayManhwaTitle(manhwa);
            result.add(new ManhwaRecommendationEngine.Manhwa(id, title, genreNames,
                    currentFavoriteIds().contains(manhwa.optString("id")),
                    currentDislikedIds().contains(manhwa.optString("id"))));
        }
        return result;
    }

    private boolean containsIgnoreCase(Collection<String> values, String candidate) {
        if (candidate == null || candidate.trim().isEmpty()) return false;
        for (String value : values) if (value.equalsIgnoreCase(candidate.trim())) return true;
        return false;
    }

    private String displayManhwaTitle(JSONObject manhwa) {
        return manhwa == null ? "" : englishDigits(manhwa.optString("title", "بدون عنوان"));
    }

    private JSONObject findManhwaByRecommendationId(long recommendationId) {
        JSONArray items = contentItems();
        if (items == null) return null;
        for (int i = 0; i < items.length(); i++) {
            JSONObject manhwa = items.optJSONObject(i);
            if (manhwa != null && manhwa.optLong("recommendationId", -1) == recommendationId) return manhwa;
        }
        return null;
    }

    private void loadRecommendationState(boolean anime) {
        SharedPreferences state = anime ? animeRecommendationState : recommendationState;
        ManhwaRecommendationEngine.UserPreferences preferences = anime
                ? animeRecommendationPreferences : recommendationPreferences;
        Set<Long> seenIds = anime ? seenAnimeRecommendationIds : seenRecommendationIds;
        Set<String> favoriteGenres = anime ? favoriteAnimeGenrePreferences : favoriteGenrePreferences;
        Set<String> favoriteIds = anime ? favoriteAnimeIds : favoriteManhwaIds;
        Set<String> dislikedGenres = anime ? dislikedAnimeGenrePreferences : dislikedGenrePreferences;
        Set<String> dislikedIds = anime ? dislikedAnimeIds : dislikedManhwaIds;
        if (state == null) return;
        try {
            JSONObject savedWeights = new JSONObject(state.getString("genre_weights", "{}"));
            Iterator<String> names = savedWeights.keys();
            while (names.hasNext()) {
                String genre = names.next();
                preferences.genreWeights.put(genre.trim().toLowerCase(Locale.ROOT),
                        Math.max(1, savedWeights.optInt(genre, 10)));
            }
        } catch (Exception ignored) { }
        Set<String> savedIds = state.getStringSet(anime ? "seen_animes" : "seen_manhwas", Collections.emptySet());
        if (savedIds != null) {
            for (String id : savedIds) {
                try { seenIds.add(Long.parseLong(id)); }
                catch (NumberFormatException ignored) { }
            }
        }
        Set<String> savedGenres = state.getStringSet("favorite_genres", Collections.emptySet());
        if (savedGenres != null) for (String genre : savedGenres) {
            String normalized = titleCaseWords(genre);
            if (!normalized.isEmpty()) favoriteGenres.add(normalized);
        }
        Set<String> savedDislikedGenres = state.getStringSet("disliked_genres", Collections.emptySet());
        if (savedDislikedGenres != null) for (String genre : savedDislikedGenres) {
            String normalized = titleCaseWords(genre);
            if (!normalized.isEmpty()) dislikedGenres.add(normalized);
        }
        Set<String> savedContentIds = state.getStringSet(anime ? "favorite_anime_ids" : "favorite_manhwa_ids", Collections.emptySet());
        if (savedContentIds != null) favoriteIds.addAll(savedContentIds);
        Set<String> savedDislikedIds = state.getStringSet(anime ? "disliked_anime_ids" : "disliked_manhwa_ids", Collections.emptySet());
        if (savedDislikedIds != null) dislikedIds.addAll(savedDislikedIds);
    }

    private void persistRecommendationState() {
        SharedPreferences state = currentRecommendationState();
        if (state == null) return;
        ManhwaRecommendationEngine.UserPreferences preferences = currentRecommendationPreferences();
        JSONObject weights = new JSONObject();
        for (Map.Entry<String, Integer> entry : preferences.genreWeights.entrySet()) {
            try { weights.put(entry.getKey(), Math.max(1, entry.getValue())); }
            catch (JSONException ignored) { }
        }
        Set<String> ids = new HashSet<>();
        for (Long id : currentSeenRecommendationIds()) ids.add(Long.toString(id));
        state.edit().putString("genre_weights", weights.toString())
                .putStringSet(animeMode ? "seen_animes" : "seen_manhwas", ids)
                .putStringSet("favorite_genres", new HashSet<>(currentFavoriteGenres()))
                .putStringSet("disliked_genres", new HashSet<>(currentDislikedGenres()))
                .putStringSet(animeMode ? "favorite_anime_ids" : "favorite_manhwa_ids", new HashSet<>(currentFavoriteIds()))
                .putStringSet(animeMode ? "disliked_anime_ids" : "disliked_manhwa_ids", new HashSet<>(currentDislikedIds()))
                .remove("favorite_authors").remove("favorite_studios").apply();
    }


    private int nextOrderIndex(JSONArray items) {
        int next = 0;
        if (items != null) for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.optJSONObject(i);
            if (item != null) next = Math.max(next, item.optInt("orderIndex", -1) + 1);
        }
        return next;
    }

    private boolean startReorderDrag(View source, String listType, String itemId) {
        ClipData data = ClipData.newPlainText(listType, itemId);
        View.DragShadowBuilder shadow = new View.DragShadowBuilder(source);
        activeReorderType = listType;
        activeReorderSourceId = itemId;
        lastReorderTargetId = itemId;
        reorderOriginalLibrary = library.toString();
        reorderChanged = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (source.startDragAndDrop(data, shadow, null, 0)) return true;
        } else if (source.startDrag(data, shadow, null, 0)) {
            return true;
        }
        clearReorderSession();
        return false;
    }

    private void setReorderDropTarget(View target, String listType, String targetId) {
        target.setOnDragListener((view, event) -> {
            if (event.getAction() == android.view.DragEvent.ACTION_DRAG_STARTED) {
                return event.getClipDescription() != null
                        && listType.contentEquals(event.getClipDescription().getLabel());
            }
            if (event.getAction() == android.view.DragEvent.ACTION_DRAG_ENTERED
                    || event.getAction() == android.view.DragEvent.ACTION_DRAG_LOCATION) {
                if (listType.equals(activeReorderType) && activeReorderSourceId != null
                        && !targetId.equals(lastReorderTargetId)
                        && reorderItems(listType, activeReorderSourceId, targetId)) {
                    lastReorderTargetId = targetId;
                    reorderChanged = true;
                }
                return true;
            }
            if (event.getAction() == android.view.DragEvent.ACTION_DROP) {
                if (listType.equals(activeReorderType) && activeReorderSourceId != null) {
                    if (!targetId.equals(lastReorderTargetId)
                            && reorderItems(listType, activeReorderSourceId, targetId)) {
                        lastReorderTargetId = targetId;
                        reorderChanged = true;
                    }
                    finishReorderSession();
                    return true;
                }
                return false;
            }
            if (event.getAction() == android.view.DragEvent.ACTION_DRAG_ENDED) {
                if (listType.equals(activeReorderType)) finishReorderSession();
                return true;
            }
            return event.getAction() == android.view.DragEvent.ACTION_DRAG_EXITED;
        });
    }

    private boolean reorderItems(String listType, String sourceId, String targetId) {
        if (sourceId == null || targetId == null || sourceId.equals(targetId)) return false;
        Map<String, int[]> beforePositions = captureReorderPositions(listType);
        boolean changed = false;
        if ("manhwa".equals(listType) || "anime".equals(listType)) {
            changed = reorderVisibleManhwas(sourceId, targetId);
        } else if ("chapter".equals(listType)) {
            JSONObject manhwa = activeManhwa();
            JSONArray items = manhwa == null ? null : checklistArray(manhwa);
            if (moveJsonItem(items, "storageIndex", sourceId, targetId)) {
                ensureOrderIndexes(items, true);
                refreshChapterSelection();
                changed = true;
            }
        }
        if (changed) animateReorderPositions(listType, beforePositions);
        return changed;
    }

    private ViewGroup reorderContainer(String listType) {
        if ("manhwa".equals(listType) || "anime".equals(listType)) return manhwasGrid;
        if ("chapter".equals(listType)) return chaptersGrid;
        return null;
    }

    private Map<String, int[]> captureReorderPositions(String listType) {
        Map<String, int[]> positions = new HashMap<>();
        ViewGroup container = reorderContainer(listType);
        if (container == null) return positions;
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            Object id = child.getTag();
            if (id instanceof String) {
                int[] location = new int[2];
                child.getLocationOnScreen(location);
                positions.put((String) id, location);
            }
        }
        return positions;
    }

    private void animateReorderPositions(String listType, Map<String, int[]> before) {
        ViewGroup container = reorderContainer(listType);
        if (container == null || before == null || before.isEmpty()) return;
        android.view.ViewTreeObserver observer = container.getViewTreeObserver();
        observer.addOnPreDrawListener(new android.view.ViewTreeObserver.OnPreDrawListener() {
            @Override public boolean onPreDraw() {
                if (observer.isAlive()) observer.removeOnPreDrawListener(this);
                if (!container.isAttachedToWindow()) return true;
            for (int i = 0; i < container.getChildCount(); i++) {
                View child = container.getChildAt(i);
                Object tag = child.getTag();
                int[] old = tag instanceof String ? before.get((String) tag) : null;
                if (old == null) continue;
                int[] now = new int[2];
                child.getLocationOnScreen(now);
                float dx = old[0] - now[0];
                float dy = old[1] - now[1];
                if (Math.abs(dx) < 1f && Math.abs(dy) < 1f) continue;
                child.animate().cancel();
                child.setTranslationX(dx);
                child.setTranslationY(dy);
                child.animate().translationX(0f).translationY(0f)
                        .setDuration(180).setInterpolator(new android.view.animation.DecelerateInterpolator(1.5f)).start();
            }
                return true;
            }
        });
    }

    private void finishReorderSession() {
        if (reorderChanged && !saveLibrary()) {
            restoreLibrary(reorderOriginalLibrary);
            if ("manhwa".equals(activeReorderType) || "anime".equals(activeReorderType)) refreshDashboardSelection();
            else if ("chapter".equals(activeReorderType)) refreshChapterSelection();
            toast("ذخیرهٔ ترتیب جدید انجام نشد.");
        }
        clearReorderSession();
    }

    private void clearReorderSession() {
        activeReorderType = null;
        activeReorderSourceId = null;
        lastReorderTargetId = null;
        reorderOriginalLibrary = null;
        reorderChanged = false;
    }

    private boolean moveJsonItem(JSONArray items, String key, String sourceId, String targetId) {
        if (items == null) return false;
        List<JSONObject> ordered = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.optJSONObject(i);
            if (item != null) ordered.add(item);
        }
        int from = -1, to = -1;
        for (int i = 0; i < ordered.size(); i++) {
            JSONObject item = ordered.get(i);
            String id = "storageIndex".equals(key)
                    ? Integer.toString(chapterStorageIndex(item, i)) : item.optString(key);
            if (sourceId.equals(id)) from = i;
            if (targetId.equals(id)) to = i;
        }
        if (from < 0 || to < 0 || from == to) return false;
        JSONObject moving = ordered.remove(from);
        ordered.add(to, moving);
        for (int i = 0; i < ordered.size(); i++) {
            putJson(ordered.get(i), "orderIndex", i);
            try { items.put(i, ordered.get(i)); } catch (JSONException ignored) { }
        }
        return true;
    }

    private boolean reorderVisibleManhwas(String sourceId, String targetId) {
        if (manhwaAdapter == null) return false;
        List<JSONObject> visible = new ArrayList<>(manhwaAdapter.visible());
        int from = -1, to = -1;
        for (int i = 0; i < visible.size(); i++) {
            String id = visible.get(i).optString("id");
            if (sourceId.equals(id)) from = i;
            if (targetId.equals(id)) to = i;
        }
        if (from < 0 || to < 0 || from == to) return false;
        JSONObject moving = visible.remove(from);
        visible.add(to, moving);
        Set<String> visibleIds = new HashSet<>();
        for (JSONObject item : visible) visibleIds.add(item.optString("id"));
        JSONArray allManhwas = contentItems();
        if (allManhwas == null) return false;
        int nextVisible = 0;
        for (int i = 0; i < allManhwas.length(); i++) {
            JSONObject item = allManhwas.optJSONObject(i);
            if (item != null && visibleIds.contains(item.optString("id"))) {
                try { allManhwas.put(i, visible.get(nextVisible++)); } catch (JSONException ignored) { }
            }
        }
        for (int i = 0; i < allManhwas.length(); i++) {
            JSONObject item = allManhwas.optJSONObject(i);
            if (item != null) putJson(item, "orderIndex", i);
        }
        refreshDashboardSelection();
        return true;
    }











    private void showManhwaShelf() {
        showManhwaShelfPage();
    }

    private void showManhwaShelfPage() {
        page = animeMode ? "allAnimes" : "allManhwas";
        appColumn.removeAllViews();
        animatePageTransition();
        addDashboardHeader();
        LinearLayout filters = new LinearLayout(this);
        filters.setGravity(Gravity.CENTER_VERTICAL);
        filters.setPadding(dp(14), dp(7), dp(14), dp(7));
        searchField = edit("جستجو", false);
        searchField.setSingleLine(true);
        searchField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        filters.addView(clearableInput(searchField), new LinearLayout.LayoutParams(0, dp(48), 1));
        genreFilterButton = iconButton("filter", PURPLE, !activeGenreFilter.isEmpty(), this::showGenreFilter);
        LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(dp(48), dp(48)); fp.leftMargin = dp(8);
        filters.addView(genreFilterButton, fp);
        appColumn.addView(filters);
        manhwasGrid = new GridView(this);
        manhwasGrid.setNumColumns(2); manhwasGrid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        manhwasGrid.setVerticalScrollBarEnabled(false); manhwasGrid.setHorizontalScrollBarEnabled(false);
        manhwasGrid.setHorizontalSpacing(dp(10)); manhwasGrid.setVerticalSpacing(dp(10)); manhwasGrid.setColumnWidth(dp(150));
        manhwasGrid.setSelector(android.R.color.transparent); manhwasGrid.setCacheColorHint(Color.TRANSPARENT); manhwasGrid.setBackgroundColor(BG);
        manhwasGrid.setPadding(dp(12), dp(4), dp(12), dp(8)); manhwasGrid.setClipToPadding(false);
        manhwaAdapter = new ManhwaAdapter(); manhwasGrid.setAdapter(manhwaAdapter);
        FrameLayout listFrame = new FrameLayout(this);
        listFrame.addView(manhwasGrid, new FrameLayout.LayoutParams(-1, -1));
        TextView empty = emptyLabel("هنوز موردی به فهرست اضافه نشده است");
        listFrame.addView(empty, new FrameLayout.LayoutParams(-1, -1)); manhwasGrid.setEmptyView(empty);
        appColumn.addView(listFrame, new LinearLayout.LayoutParams(-1, 0, 1));
        TextView version = text(VERSION, 10, MUTED); version.setGravity(Gravity.CENTER);
        appColumn.addView(version, new LinearLayout.LayoutParams(-1, dp(22)));
        searchField.addTextChangedListener(watcher(() -> { if (manhwaAdapter != null) manhwaAdapter.notifyDataSetChanged(); }));
    }

    private void onManhwaCardTap(JSONObject manhwa) {
        if (!selectedManhwaIds.isEmpty()) toggleManhwaSelection(manhwa);
        else {
            manhwaInfoReturnPage = page;
            showManhwaInfo(manhwa);
        }
    }

    private void showManhwaInfo(JSONObject manhwa) {
        if (manhwa == null) { showDashboard(); return; }
        String scrollItemKey = (animeMode ? "anime:" : "manhwa:") + manhwa.optString("id");
        int restoreScrollY = scrollItemKey.equals(manhwaInfoScrollItemId)
                ? (manhwaInfoScroll == null ? manhwaInfoScrollY : manhwaInfoScroll.getScrollY()) : 0;
        manhwaInfoScrollItemId = scrollItemKey;
        manhwaInfoScrollY = restoreScrollY;
        page = animeMode ? "animeInfo" : "manhwaInfo";
        activeManhwaId = manhwa.optString("id");
        appColumn.removeAllViews();
        animatePageTransition();
        appColumn.addView(makeHeader(displayManhwaTitle(manhwa), "",
                null, "edit", null,
                () -> showManhwaDialog(manhwa), 10),
                new LinearLayout.LayoutParams(-1, dp(62)));
        if (titleView != null) {
            titleView.setContentDescription(displayManhwaTitle(manhwa));
        }

        ScrollView scroll = new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setHorizontalScrollBarEnabled(false);
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(18), dp(14), dp(18), dp(22));

        FrameLayout coverFrame = new FrameLayout(this);
        coverFrame.setBackground(roundDrawable(SURFACE_ALT, Color.TRANSPARENT, 12));
        coverFrame.setClipToOutline(true);
        ImageView cover = new ImageView(this);
        cover.setScaleType(ImageView.ScaleType.FIT_CENTER);
        cover.setBackground(roundDrawable(SURFACE_ALT, Color.TRANSPARENT, 12));
        coverFrame.addView(cover, new FrameLayout.LayoutParams(-1, -1));
        NativeIconView coverPlaceholder = new NativeIconView(this, animeMode ? "anime" : "manhwa", PURPLE);
        coverFrame.addView(coverPlaceholder,
                new FrameLayout.LayoutParams(dp(48), dp(48), Gravity.CENTER));
        bindCover(cover, coverPlaceholder, manhwa.optString("cover", ""));
        LinearLayout.LayoutParams coverParams = new LinearLayout.LayoutParams(dp(166), dp(230));
        coverParams.gravity = Gravity.CENTER_HORIZONTAL;
        coverParams.bottomMargin = dp(18);
        body.addView(coverFrame, coverParams);

        JSONArray chapters = checklistArray(manhwa);
        int chapterCount = chapters == null ? 0 : chapters.length();
        int readPercent = manhwaReadPercent(manhwa);
        LinearLayout genresRow = addManhwaInfoLine(body, "ژانرها", genreSummary(manhwa.optJSONArray("genres")));
        genresRow.setContentDescription("کپی همهٔ ژانرها");
        genresRow.setOnClickListener(v -> copyManhwaGenres(manhwa));
        addManhwaInfoLine(body, "تعداد " + contentUnitPlural(), String.valueOf(chapterCount));
        addManhwaInfoLine(body, contentUnitPlural() + " دیده‌شده", readPercent + "٪");
        ProgressBar progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100);
        progress.setProgress(readPercent);
        if (Build.VERSION.SDK_INT >= 21) progress.setProgressTintList(
                android.content.res.ColorStateList.valueOf(PURPLE));
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(-1, dp(5));
        progressParams.bottomMargin = dp(16);
        body.addView(progress, progressParams);

        TextView descriptionLabel = text("توضیحات", 14, MUTED);
        descriptionLabel.setGravity(Gravity.RIGHT);
        body.addView(descriptionLabel);
        TextView description = text(manhwa.optString("description", "").trim().isEmpty()
                ? "توضیحی ثبت نشده" : manhwa.optString("description"), 15,
                manhwa.optString("description", "").trim().isEmpty() ? MUTED : TEXT);
        description.setGravity(Gravity.RIGHT);
        description.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        description.setPadding(0, dp(6), 0, dp(18));
        body.addView(description, new LinearLayout.LayoutParams(-1, -2));

        addManhwaInterestRating(body, manhwa);
        View chaptersButton = textButton("رفتن به چک‌لیست " + contentUnitPlural(), "manhwa", PURPLE,
                () -> openChapters(manhwa));
        LinearLayout.LayoutParams chaptersButtonParams = new LinearLayout.LayoutParams(-1, dp(50));
        chaptersButtonParams.topMargin = dp(18);
        body.addView(chaptersButton, chaptersButtonParams);

        scroll.addView(body);
        manhwaInfoScroll = scroll;
        scroll.setOnScrollChangeListener((view, x, y, oldX, oldY) -> manhwaInfoScrollY = y);
        appColumn.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        scroll.post(() -> scroll.scrollTo(0, Math.max(0, restoreScrollY)));
    }

    private LinearLayout addManhwaInfoLine(LinearLayout body, String label, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        row.setPadding(dp(12), dp(10), dp(12), dp(10));

        TextView heading = text(label, 13, MUTED);
        heading.setSingleLine(true);
        heading.setMaxLines(1);
        heading.setEllipsize(android.text.TextUtils.TruncateAt.END);
        heading.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        row.addView(heading, new LinearLayout.LayoutParams(-2, -2));

        TextView content = text(value == null || value.isEmpty() ? "—" : value, 14, TEXT);
        content.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        content.setMaxLines(2);
        content.setEllipsize(android.text.TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(0, -2, 1);
        contentParams.rightMargin = dp(10);
        row.addView(content, contentParams);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.bottomMargin = dp(5);
        row.setBackground(roundDrawable(SURFACE, 0xFF29242F, 11));
        body.addView(row, params);
        return row;
    }
    private String genreSummary(JSONArray genres) {
        if (genres == null || genres.length() == 0) return "—";
        List<String> values = new ArrayList<>();
        for (int i = 0; i < genres.length(); i++) {
            String value = genres.optString(i).trim();
            if (!value.isEmpty()) values.add(value);
        }
        return values.isEmpty() ? "—" : android.text.TextUtils.join(" · ", values);
    }

    private int manhwaReadPercent(JSONObject manhwa) {
        JSONArray chapters = checklistArray(manhwa);
        if (chapters == null || chapters.length() == 0) return 0;
        int total = 0;
        for (int i = 0; i < chapters.length(); i++) {
            JSONObject chapter = chapters.optJSONObject(i);
            if (chapter == null) continue;
            if (chapterState(chapter) == 2) total += 100;
        }
        return Math.round(total / (float) chapters.length());
    }

    private int estimatedInterestRating(JSONObject manhwa) {
        JSONArray genres = manhwa == null ? null : manhwa.optJSONArray("genres");
        int totalWeight = 0;
        int genreCount = 0;
        if (genres != null) for (int i = 0; i < genres.length(); i++) {
            String genre = genres.optString(i).trim();
            if (genre.isEmpty() || "Other".equalsIgnoreCase(genre)) continue;
            totalWeight += currentRecommendationPreferences().weightFor(genre);
            genreCount++;
        }
        int averageWeight = genreCount == 0 ? 10 : totalWeight / genreCount;
        int rating = averageWeight >= 28 ? 5 : averageWeight >= 18 ? 4
                : averageWeight >= 9 ? 3 : averageWeight >= 6 ? 2 : 1;
        if (manhwa != null && currentFavoriteIds().contains(manhwa.optString("id"))) rating++;
        return Math.max(1, Math.min(5, rating));
    }

    private void addManhwaInterestRating(LinearLayout body, JSONObject manhwa) {
        int estimate = estimatedInterestRating(manhwa);
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(dp(12), dp(12), dp(12), dp(12));
        card.setBackground(roundDrawable(SURFACE, 0xFF29242F, 11));
        TextView title = text("امتیاز برآوردشدهٔ سامانه", 14, TEXT);
        title.setGravity(Gravity.CENTER);
        card.addView(title);
        TextView estimateText = text("برآورد بر اساس تگ‌ها: " + estimate + " از 5", 12, MUTED);
        estimateText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams estimateParams = new LinearLayout.LayoutParams(-1, -2);
        estimateParams.topMargin = dp(3);
        card.addView(estimateText, estimateParams);
        LinearLayout stars = new LinearLayout(this);
        stars.setGravity(Gravity.CENTER);
        stars.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        for (int i = 1; i <= 5; i++) {
            View star = ratingStar(i <= estimate, i);
            stars.addView(star, new LinearLayout.LayoutParams(dp(42), dp(46)));
        }
        LinearLayout.LayoutParams starsParams = new LinearLayout.LayoutParams(-1, -2);
        starsParams.topMargin = dp(4);
        card.addView(stars, starsParams);
        LinearLayout reactions = new LinearLayout(this);
        reactions.setGravity(Gravity.CENTER);
        boolean liked = currentFavoriteIds().contains(manhwa.optString("id"));
        boolean disliked = currentDislikedIds().contains(manhwa.optString("id"));
        View likeButton = makeReactionButton("لایک", "thumb_up", PURPLE, liked,
                () -> toggleContentReaction(manhwa, true));
        View dislikeButton = makeReactionButton("دیس لایک", "thumb_down", themeSecondaryColor(), disliked,
                () -> toggleContentReaction(manhwa, false));
        reactions.addView(likeButton, new LinearLayout.LayoutParams(0, dp(46), 1));
        LinearLayout.LayoutParams dislikeParams = new LinearLayout.LayoutParams(0, dp(46), 1);
        dislikeParams.leftMargin = dp(8);
        reactions.addView(dislikeButton, dislikeParams);
        LinearLayout.LayoutParams reactionsParams = new LinearLayout.LayoutParams(-1, dp(46));
        reactionsParams.topMargin = dp(10);
        card.addView(reactions, reactionsParams);
        body.addView(card, new LinearLayout.LayoutParams(-1, -2));
    }

    private View makeReactionButton(String label, String icon, int accent, boolean selected, Runnable action) {
        LinearLayout button = new LinearLayout(this);
        button.setGravity(Gravity.CENTER);
        button.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        button.setBackground(roundDrawable(selected ? accent : SURFACE,
                selected ? accent : themedStrokeColor(0xFF40384A), 12));
        NativeIconView glyph = new NativeIconView(this, icon, selected ? contrastOnColor(accent) : accent);
        button.addView(glyph, new LinearLayout.LayoutParams(dp(20), dp(20)));
        TextView title = text(label, 13, selected ? contrastOnColor(accent) : TEXT);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(-2, -2);
        titleParams.rightMargin = dp(7);
        button.addView(title, titleParams);
        button.setOnClickListener(v -> action.run());
        button.setContentDescription(label);
        button.setClickable(true);
        button.setFocusable(true);
        applyPressScale(button);
        return button;
    }

    private void toggleContentReaction(JSONObject item, boolean liked) {
        if (item == null) return;
        String id = item.optString("id");
        if (id.isEmpty()) return;
        if (liked) {
            if (!currentFavoriteIds().add(id)) currentFavoriteIds().remove(id);
            if (currentFavoriteIds().contains(id)) currentDislikedIds().remove(id);
        } else {
            if (!currentDislikedIds().add(id)) currentDislikedIds().remove(id);
            if (currentDislikedIds().contains(id)) currentFavoriteIds().remove(id);
        }
        rebuildRecommendationWeights();
        persistRecommendationState();
        showManhwaInfo(item);
    }

    private View ratingStar(boolean selected, int rating) {
        View star = new View(this) {
            private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            @Override protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                float outer = Math.min(getWidth(), getHeight()) * .39f;
                float inner = outer * .48f;
                float centerX = getWidth() / 2f;
                float centerY = getHeight() / 2f;
                float[] xs = new float[10], ys = new float[10];
                for (int i = 0; i < 10; i++) {
                    double angle = -Math.PI / 2 + i * Math.PI / 5;
                    float radius = (i % 2 == 0) ? outer : inner;
                    xs[i] = centerX + (float) Math.cos(angle) * radius;
                    ys[i] = centerY + (float) Math.sin(angle) * radius;
                }
                Path path = new Path();
                float startX = xs[0] + (xs[9] - xs[0]) * .18f;
                float startY = ys[0] + (ys[9] - ys[0]) * .18f;
                path.moveTo(startX, startY);
                for (int i = 0; i < 10; i++) {
                    int next = (i + 1) % 10;
                    float beforeX = xs[i] + (xs[(i + 9) % 10] - xs[i]) * .18f;
                    float beforeY = ys[i] + (ys[(i + 9) % 10] - ys[i]) * .18f;
                    float afterX = xs[i] + (xs[next] - xs[i]) * .18f;
                    float afterY = ys[i] + (ys[next] - ys[i]) * .18f;
                    path.lineTo(beforeX, beforeY);
                    path.quadTo(xs[i], ys[i], afterX, afterY);
                }
                path.close();
                paint.setColor(selected ? Color.rgb(255, 193, 7) : MUTED);
                paint.setStyle(selected ? Paint.Style.FILL : Paint.Style.STROKE);
                paint.setStrokeWidth(dp(1.8f));
                paint.setStrokeJoin(Paint.Join.ROUND);
                canvas.drawPath(path, paint);
            }
        };
        star.setContentDescription("امتیاز سامانه " + rating + " از 5");
        star.setClickable(false);
        star.setFocusable(false);
        return star;
    }

    private void addDashboardHeader() {
        View header;
        if (selectedManhwaIds.isEmpty()) {
            header = makeHeader("", "", "menu", "add", this::openDrawer,
                    () -> showManhwaDialog(null), 10);
            if (header instanceof FrameLayout) {
                FrameLayout pill = new FrameLayout(this);
                pill.setPadding(dp(16), 0, dp(16), 0);
                pill.setBackground(roundDrawable(PURPLE, PURPLE, 24));
                LinearLayout capsuleContents = new LinearLayout(this);
                capsuleContents.setGravity(Gravity.CENTER);
                capsuleContents.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                TextView mode = text(animeMode ? "فهرست انیمه" : "فهرست مانهوا", 14, contrastOnAccent());
                mode.setSingleLine(true);
                mode.setGravity(Gravity.CENTER);
                capsuleContents.addView(mode, new LinearLayout.LayoutParams(-2, -2));
                pill.addView(capsuleContents, new FrameLayout.LayoutParams(-2, -1, Gravity.CENTER));
                pill.setContentDescription("تغییر فهرست بین مانهوا و انیمه");
                pill.setClickable(true);
                pill.setFocusable(true);
                applyPressScale(pill);
                pill.setOnClickListener(v -> {
                    if (animeMode) showAllManhwas();
                    else showAllAnimes();
                });
                ((FrameLayout) header).addView(pill,
                        new FrameLayout.LayoutParams(-2, dp(42), Gravity.CENTER));
            }
        } else if (selectedManhwaIds.size() == 1) {
            JSONObject selected = findManhwaById(selectedManhwaIds.iterator().next());
            header = makeHeader(formatCount(1) + " انتخاب‌شده", "", "delete", "edit", this::confirmDeleteSelected,
                    () -> { if (selected != null) showManhwaDialog(selected); }, 10, 118);
        } else {
            header = makeHeader(formatCount(selectedManhwaIds.size()) + " انتخاب‌شده", "", "delete", null,
                    this::confirmDeleteSelected, null);
        }
        appColumn.addView(header, 0, new LinearLayout.LayoutParams(-1, dp(62)));
    }
    private void refreshDashboardSelection() {
        if (!showingAllManhwas() || appColumn == null) return;
        if (appColumn.getChildCount() > 0) appColumn.removeViewAt(0);
        addDashboardHeader();
        if (manhwaAdapter != null) manhwaAdapter.notifyDataSetChanged();
    }

    private JSONObject findManhwaById(String id) {
        JSONArray manhwas = contentItems();
        if (manhwas == null) return null;
        for (int i = 0; i < manhwas.length(); i++) {
            JSONObject manhwa = manhwas.optJSONObject(i);
            if (manhwa != null && id.equals(manhwa.optString("id"))) return manhwa;
        }
        return null;
    }

    private void toggleManhwaSelection(JSONObject manhwa) {
        if (manhwa == null) return;
        String id = manhwa.optString("id");
        if (!selectedManhwaIds.add(id)) selectedManhwaIds.remove(id);
        refreshDashboardSelection();
    }

    private void confirmDeleteSelected() {
        if (selectedManhwaIds.isEmpty()) return;
        int count = selectedManhwaIds.size();
        String itemLabel = animeMode ? "انیمه" : "مانهوا";
        String message = count == 1 ? "این " + itemLabel + " حذف شود؟"
                : formatCount(count) + " " + itemLabel + " حذف شوند؟";
        AlertDialog dialog = new AlertDialog.Builder(this).setMessage(message).setNegativeButton("لغو", null)
                .setPositiveButton("حذف", (clickedDialog, which) -> {
                    JSONArray manhwas = contentItems();
                    if (manhwas == null) return;
                    String beforeDelete = library.toString();
                    List<String> removedCovers = new ArrayList<>();
                    JSONArray kept = new JSONArray();
                    for (int i = 0; i < manhwas.length(); i++) {
                        JSONObject manhwa = manhwas.optJSONObject(i);
                        if (manhwa == null || !selectedManhwaIds.contains(manhwa.optString("id"))) kept.put(manhwa);
                        else {
                            if (!manhwa.optString("cover", "").isEmpty()) removedCovers.add(manhwa.optString("cover"));
                        }
                    }
                    try { library.put(contentArrayKey(), kept); } catch (JSONException ignored) { }
                    if (saveLibrary()) {
                        selectedManhwaIds.clear();
                        for (String cover : removedCovers) deleteCoverFile(cover);
                    } else {
                        restoreLibrary(beforeDelete);
                    }
                    refreshDashboardSelection();
                }).create();
        dialog.show();
        styleDialogWindow(dialog);
    }

    private void showChapters() {
        JSONObject manhwa = activeManhwa();
        if (manhwa == null) { showDashboard(); return; }
        String scrollItemKey = (animeMode ? "anime:" : "manhwa:") + manhwa.optString("id");
        boolean continueAnimePosition = animeMode && "chapters".equals(page)
                && scrollItemKey.equals(chapterScrollItemId);
        int restoreAnimeScrollY = continueAnimePosition
                ? (chapterSeasonScroll == null ? chapterScrollY : chapterSeasonScroll.getScrollY()) : -1;
        page = "chapters";
        chapterScrollItemId = scrollItemKey;
        appColumn.removeAllViews();
        if (!continueAnimePosition) animatePageTransition();
        addChaptersHeader(manhwa);
        JSONArray manhwaGenres = manhwa.optJSONArray("genres");
        if (manhwaGenres != null && manhwaGenres.length() > 0) {
            StringBuilder labels = new StringBuilder();
            for (int i = 0; i < manhwaGenres.length(); i++) {
                if (i > 0) labels.append("  ·  ");
                labels.append(manhwaGenres.optString(i));
            }
            HorizontalScrollView genreScroller = new HorizontalScrollView(this);
            genreScroller.setVerticalScrollBarEnabled(false); genreScroller.setHorizontalScrollBarEnabled(false);
            TextView genreLine = text(labels.toString(), 12, PURPLE);
            genreLine.setSingleLine(true);
            genreLine.setPadding(dp(16), 0, dp(16), 0);
            genreScroller.addView(genreLine, new HorizontalScrollView.LayoutParams(-2, dp(36)));
            appColumn.addView(genreScroller, new LinearLayout.LayoutParams(-1, dp(38)));
        }
        appColumn.addView(chapterStateLegend(), new LinearLayout.LayoutParams(-1, dp(30)));
        chaptersGrid = null;
        chapterAdapter = null;
        chapterSeasonScroll = null;
        if (animeMode) {
            ScrollView seasonScroll = new ScrollView(this);
            chapterSeasonScroll = seasonScroll;
            seasonScroll.setVerticalScrollBarEnabled(false);
            seasonScroll.setHorizontalScrollBarEnabled(false);
            LinearLayout seasonContent = new LinearLayout(this);
            seasonContent.setOrientation(LinearLayout.VERTICAL);
            seasonContent.setPadding(dp(12), dp(8), dp(12), dp(12));
            seasonScroll.addView(seasonContent);
            renderAnimeSeasonChecklist(manhwa, seasonContent, seasonScroll, restoreAnimeScrollY);
            appColumn.addView(seasonScroll, new LinearLayout.LayoutParams(-1, 0, 1));
            seasonScroll.setOnScrollChangeListener((view, x, y, oldX, oldY) -> chapterScrollY = y);
        } else {
            chapterSeasonScroll = null;
        chaptersGrid = new GridView(this);
        chaptersGrid.setNumColumns(GridView.AUTO_FIT);
        chaptersGrid.setColumnWidth(dp(60));
        chaptersGrid.setHorizontalSpacing(dp(8));
        chaptersGrid.setVerticalSpacing(dp(8));
        chaptersGrid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        chaptersGrid.setGravity(Gravity.CENTER);
        chaptersGrid.setCacheColorHint(Color.TRANSPARENT);
        chaptersGrid.setBackgroundColor(BG);
        chaptersGrid.setVerticalScrollBarEnabled(false);
        chaptersGrid.setHorizontalScrollBarEnabled(false);
        chaptersGrid.setPadding(dp(12), dp(8), dp(12), dp(10));
        chaptersGrid.setClipToPadding(false);
        chapterAdapter = new ChapterAdapter();
        chaptersGrid.setAdapter(chapterAdapter);
        appColumn.addView(chaptersGrid, new LinearLayout.LayoutParams(-1, 0, 1));
        int lastSeen = lastSeenChapter(manhwa);
        chaptersGrid.post(() -> chaptersGrid.setSelectionFromTop(lastSeen, Math.max(dp(48), chaptersGrid.getHeight() / 3)));
        }
    }

    private void renderAnimeSeasonChecklist(JSONObject anime, LinearLayout content,
                                             ScrollView seasonScroll, int restoreScrollY) {
        JSONArray episodes = checklistArray(anime);
        if (episodes == null || episodes.length() == 0) {
            TextView empty = emptyLabel("هنوز قسمتی ثبت نشده است");
            content.addView(empty, new LinearLayout.LayoutParams(-1, dp(160)));
            return;
        }
        java.util.TreeMap<Integer, ArrayList<Integer>> bySeason = new java.util.TreeMap<>();
        for (int i = 0; i < episodes.length(); i++) {
            JSONObject episode = episodes.optJSONObject(i);
            int season = animeSeasonNumber(episode);
            ArrayList<Integer> positions = bySeason.get(season);
            if (positions == null) { positions = new ArrayList<>(); bySeason.put(season, positions); }
            positions.add(i);
        }
        Comparator<Integer> episodeOrder = (first, second) -> {
            JSONObject firstEpisode = episodes.optJSONObject(first);
            JSONObject secondEpisode = episodes.optJSONObject(second);
            int numberOrder = Integer.compare(animeEpisodeNumber(firstEpisode, first),
                    animeEpisodeNumber(secondEpisode, second));
            return numberOrder != 0 ? numberOrder : Integer.compare(chapterStorageIndex(firstEpisode, first),
                    chapterStorageIndex(secondEpisode, second));
        };
        int availableWidthDp = (int) (getResources().getDisplayMetrics().widthPixels
                / getResources().getDisplayMetrics().density) - 24;
        int columns = Math.max(3, Math.min(8, availableWidthDp / 68));
        int lastSeenIndex = lastProgressedChapterIndex(anime);
        View[] lastSeenCell = {null};
        for (Map.Entry<Integer, ArrayList<Integer>> entry : bySeason.entrySet()) {
            int seasonNumber = entry.getKey();
            ArrayList<Integer> positions = entry.getValue();
            Collections.sort(positions, episodeOrder);
            LinearLayout section = new LinearLayout(this);
            section.setOrientation(LinearLayout.VERTICAL);
            section.setPadding(dp(10), dp(8), dp(10), dp(10));
            section.setBackground(roundDrawable(SURFACE, themedStrokeColor(0xFF302A38), 12));
            LinearLayout.LayoutParams sectionParams = new LinearLayout.LayoutParams(-1, -2);
            sectionParams.bottomMargin = dp(10);
            content.addView(section, sectionParams);

            LinearLayout heading = new LinearLayout(this);
            heading.setGravity(Gravity.CENTER_VERTICAL);
            heading.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            TextView name = text("فصل S" + formatCount(seasonNumber), 14, TEXT);
            heading.addView(name, new LinearLayout.LayoutParams(0, dp(34), 1));
            TextView count = text(formatCount(positions.size()) + " قسمت", 12, MUTED);
            heading.addView(count, new LinearLayout.LayoutParams(-2, dp(34)));
            section.addView(heading);

            for (int start = 0; start < positions.size(); start += columns) {
                LinearLayout row = new LinearLayout(this);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, dp(46));
                rowParams.topMargin = dp(5);
                section.addView(row, rowParams);
                for (int column = 0; column < columns; column++) {
                    int localIndex = start + column;
                    View cell;
                    if (localIndex < positions.size()) {
                        int episodeIndex = positions.get(localIndex);
                        JSONObject episode = episodes.optJSONObject(episodeIndex);
                        cell = createChapterCell(episode, episodeIndex);
                        if (episodeIndex == lastSeenIndex) lastSeenCell[0] = cell;
                    } else {
                        cell = new View(this);
                        cell.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
                    }
                    LinearLayout.LayoutParams cellParams = new LinearLayout.LayoutParams(0, dp(46), 1f);
                    cellParams.leftMargin = dp(3);
                    cellParams.rightMargin = dp(3);
                    row.addView(cell, cellParams);
                }
            }
        }
        seasonScroll.post(() -> {
            if (chapterSeasonScroll != seasonScroll || !"chapters".equals(page)) return;
            if (restoreScrollY >= 0) {
                seasonScroll.scrollTo(0, restoreScrollY);
            } else if (lastSeenCell[0] != null) {
                int[] cellLocation = new int[2];
                int[] scrollLocation = new int[2];
                lastSeenCell[0].getLocationOnScreen(cellLocation);
                seasonScroll.getLocationOnScreen(scrollLocation);
                int top = seasonScroll.getScrollY() + cellLocation[1] - scrollLocation[1];
                seasonScroll.scrollTo(0, Math.max(0,
                        top - (seasonScroll.getHeight() - lastSeenCell[0].getHeight()) / 2));
            }
        });
    }

    private void copyManhwaGenres(JSONObject manhwa) {
        JSONArray genres = manhwa == null ? null : manhwa.optJSONArray("genres");
        if (genres == null || genres.length() == 0) return;
        List<String> values = new ArrayList<>();
        for (int i = 0; i < genres.length(); i++) {
            String genre = genres.optString(i).trim();
            if (!genre.isEmpty()) values.add(genre);
        }
        if (!values.isEmpty()) copyToClipboard("genres", android.text.TextUtils.join(", ", values));
    }

    private void copyToClipboard(String label, String value) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) clipboard.setPrimaryClip(ClipData.newPlainText(label, value));
    }

    private void addChaptersHeader(JSONObject manhwa) {
        View header;
        if (selectedChapterKeys.isEmpty()) {
            header = makeHeader(displayManhwaTitle(manhwa), "", "add", "range",
                    this::addChecklistChapter, this::showQuickChapterRangeDialog);
        } else if (selectedChapterKeys.size() == 1) {
            header = makeHeader(formatCount(selectedChapterKeys.size()) + " " + contentUnitLabel() + " انتخاب‌شده", "", "edit", "close",
                    this::renameSelectedChapter, this::clearChapterSelection, 0, 118);
        } else {
            header = makeHeader(formatCount(selectedChapterKeys.size()) + " " + contentUnitLabel() + " انتخاب‌شده", "", "close", null,
                    this::clearChapterSelection, null);
        }
        appColumn.addView(header, 0, new LinearLayout.LayoutParams(-1, dp(62)));
    }

    private void addChecklistChapter() {
        JSONObject manhwa = activeManhwa();
        JSONArray chapters = checklistArray(manhwa);
        if (manhwa == null) return;
        if (chapters == null) chapters = new JSONArray();
        if (chapters.length() >= 5000) {
            toast("امکان افزودن " + contentUnitPlural() + " بیشتر وجود ندارد.");
            return;
        }
        final EditText title;
        final EditText seasonField;
        LinearLayout fields = new LinearLayout(this);
        fields.setOrientation(LinearLayout.VERTICAL);
        if (animeMode) {
            int defaultSeason = 1;
            int defaultEpisode = 1;
            for (int i = 0; i < chapters.length(); i++) {
                JSONObject episode = chapters.optJSONObject(i);
                defaultSeason = Math.max(defaultSeason, animeSeasonNumber(episode));
            }
            for (int i = 0; i < chapters.length(); i++) {
                JSONObject episode = chapters.optJSONObject(i);
                if (animeSeasonNumber(episode) == defaultSeason) {
                    defaultEpisode = Math.max(defaultEpisode, animeEpisodeNumber(episode, i) + 1);
                }
            }
            seasonField = edit("شمارهٔ فصل", false);
            seasonField.setInputType(InputType.TYPE_CLASS_NUMBER);
            seasonField.setText(Integer.toString(defaultSeason));
            title = edit("شمارهٔ قسمت", false);
            title.setInputType(InputType.TYPE_CLASS_NUMBER);
            title.setText(Integer.toString(defaultEpisode));
            fields.addView(clearableInput(seasonField), fieldParams());
            fields.addView(clearableInput(title), fieldParams());
        } else {
            seasonField = null;
            title = edit("نام " + contentUnitLabel(), false);
            title.setText(contentUnitLabel() + " " + formatCount(chapters.length() + 1));
            fields.addView(clearableInput(title), fieldParams());
        }
        title.setSelection(title.length());
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("افزودن " + contentUnitLabel())
                .setView(dialogPadding(fields)).setNegativeButton("لغو", null)
                .setPositiveButton("افزودن", null).create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String before = library.toString();
            JSONArray current = checklistArray(manhwa);
            if (current == null) current = new JSONArray();
            ensureChapterStorageIndexes(current);
            JSONObject chapter = newChapter();
            String label = title.getText().toString().trim();
            int insertAt;
            if (animeMode) {
                int seasonNumber = parseRangeNumber(seasonField.getText().toString());
                int episodeNumber = parseRangeNumber(label);
                if (seasonNumber < 1 || seasonNumber > 500) { seasonField.setError("شمارهٔ فصل معتبر نیست"); return; }
                if (episodeNumber < 1 || episodeNumber > 5000) { title.setError("شمارهٔ قسمت معتبر نیست"); return; }
                for (int i = 0; i < current.length(); i++) {
                    JSONObject existingEpisode = current.optJSONObject(i);
                    if (animeSeasonNumber(existingEpisode) == seasonNumber
                            && animeEpisodeNumber(existingEpisode, i) == episodeNumber) {
                        title.setError("این قسمت از فصل قبلاً ثبت شده است");
                        return;
                    }
                }
                putJson(chapter, "seasonNumber", seasonNumber);
                putJson(chapter, "episodeNumber", episodeNumber);
                putJson(chapter, "title", "قسمت " + episodeNumber);
                insertAt = current.length();
            } else {
                if (label.isEmpty()) {
                    title.setError("نام " + contentUnitLabel() + " را وارد کنید");
                    return;
                }
                putJson(chapter, "title", label);
                insertAt = chapterInsertionIndex(current, label);
            }
            putJson(chapter, "storageIndex", current.length() + 1);
            JSONArray ordered = new JSONArray();
            for (int i = 0; i <= current.length(); i++) {
                if (i == insertAt) ordered.put(chapter);
                if (i < current.length()) ordered.put(current.optJSONObject(i));
            }
            for (int i = 0; i < ordered.length(); i++) putJson(ordered.optJSONObject(i), "orderIndex", i);
            putJson(manhwa, animeMode ? "episodes" : "chapters", ordered);
            if (!saveLibrary()) {
                restoreLibrary(before);
                return;
            }
            dialog.dismiss();
            selectedChapterKeys.clear();
            showChapters();
        }));
        dialog.show();
        styleDialogWindow(dialog);
        dialog.getWindow().setSoftInputMode(WindowManagerFlags.ADJUST_RESIZE);
    }

    private void showQuickChapterRangeDialog() {
        JSONObject item = activeManhwa();
        JSONArray chapters = checklistArray(item);
        if (item == null || chapters == null || chapters.length() == 0) return;
        ensureChapterStorageIndexes(chapters);
        int firstUnread = -1;
        int firstNumber = Integer.MAX_VALUE;
        int lastNumber = 0;
        int defaultSeason = Integer.MAX_VALUE;
        int firstUnreadEpisode = -1;
        int lastEpisodeInDefaultSeason = 0;
        for (int i = 0; i < chapters.length(); i++) {
            JSONObject chapter = chapters.optJSONObject(i);
            if (animeMode) {
                int season = animeSeasonNumber(chapter);
                int number = animeEpisodeNumber(chapter, i);
                if (season < defaultSeason && chapterState(chapter) == 0) {
                    defaultSeason = season;
                    firstUnreadEpisode = number;
                    lastEpisodeInDefaultSeason = number;
                } else if (season == defaultSeason) {
                    if (chapterState(chapter) == 0 && (firstUnreadEpisode < 0 || number < firstUnreadEpisode)) {
                        firstUnreadEpisode = number;
                    }
                    lastEpisodeInDefaultSeason = Math.max(lastEpisodeInDefaultSeason, number);
                }
            } else {
                int number = chapterStorageIndex(chapter, i);
                firstNumber = Math.min(firstNumber, number);
                lastNumber = Math.max(lastNumber, number);
                if (firstUnread < 0 && chapterState(chapter) == 0) firstUnread = number;
            }
        }
        if (animeMode && defaultSeason == Integer.MAX_VALUE) {
            for (int i = 0; i < chapters.length(); i++) {
                defaultSeason = Math.min(defaultSeason, animeSeasonNumber(chapters.optJSONObject(i)));
            }
            firstUnreadEpisode = Integer.MAX_VALUE;
            lastEpisodeInDefaultSeason = 0;
            for (int i = 0; i < chapters.length(); i++) {
                JSONObject episode = chapters.optJSONObject(i);
                if (animeSeasonNumber(episode) != defaultSeason) continue;
                int number = animeEpisodeNumber(episode, i);
                firstUnreadEpisode = Math.min(firstUnreadEpisode, number);
                lastEpisodeInDefaultSeason = Math.max(lastEpisodeInDefaultSeason, number);
            }
        }

        LinearLayout fields = new LinearLayout(this);
        fields.setOrientation(LinearLayout.VERTICAL);
        fields.setPadding(dp(8), dp(6), dp(8), dp(6));
        EditText seasonField = animeMode ? edit("شمارهٔ فصل", false) : null;
        if (seasonField != null) {
            seasonField.setInputType(InputType.TYPE_CLASS_NUMBER);
            seasonField.setText(Integer.toString(defaultSeason));
            fields.addView(clearableInput(seasonField), fieldParams());
        }
        EditText from = edit("از شماره", false);
        from.setInputType(InputType.TYPE_CLASS_NUMBER);
        int fromDefault = animeMode
                ? (firstUnreadEpisode > 0 && firstUnreadEpisode != Integer.MAX_VALUE ? firstUnreadEpisode : 1)
                : firstUnread > 0 ? firstUnread : Math.max(1, firstNumber);
        int toDefault = animeMode ? Math.max(fromDefault, lastEpisodeInDefaultSeason)
                : Math.max(fromDefault, lastNumber);
        from.setText(Integer.toString(fromDefault));
        from.setSelection(from.length());
        EditText to = edit("تا شماره", false);
        to.setInputType(InputType.TYPE_CLASS_NUMBER);
        to.setText(Integer.toString(toDefault));
        to.setSelection(to.length());
        fields.addView(clearableInput(from), fieldParams());
        fields.addView(clearableInput(to), fieldParams());
        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.VERTICAL);
        View readyButton = textButton("آمادهٔ خواندن", "check", themeSecondaryColor(), () -> { });
        View readButton = textButton("خوانده‌شده", "check", PURPLE, () -> { });
        View unreadButton = textButton("نخوانده", "close", MUTED, () -> { });
        for (View button : new View[]{readyButton, readButton, unreadButton}) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp(46));
            params.topMargin = dp(6);
            actions.addView(button, params);
        }
        fields.addView(actions);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("تغییر وضعیت بازهٔ " + contentUnitPlural())
                .setView(dialogPadding(fields)).create();
        dialog.show();
        styleDialogWindow(dialog);
        dialog.getWindow().setSoftInputMode(WindowManagerFlags.ADJUST_RESIZE);

        readyButton.setOnClickListener(v -> applyQuickChapterRange(from, to, seasonField, chapters, 1, dialog));
        readButton.setOnClickListener(v -> applyQuickChapterRange(from, to, seasonField, chapters, 2, dialog));
        unreadButton.setOnClickListener(v -> applyQuickChapterRange(from, to, seasonField, chapters, 0, dialog));
    }

    private void applyQuickChapterRange(EditText from, EditText to, EditText seasonField,
                                        JSONArray chapters, int state, AlertDialog dialog) {
        int start = parseRangeNumber(from.getText().toString());
        int end = parseRangeNumber(to.getText().toString());
        if (start < 1) { from.setError("شمارهٔ شروع را وارد کنید"); return; }
        if (end < start) { to.setError("پایان بازه باید برابر یا بزرگ‌تر باشد"); return; }
        int season = 0;
        if (animeMode) {
            season = parseRangeNumber(seasonField == null ? "" : seasonField.getText().toString());
            if (season < 1) { if (seasonField != null) seasonField.setError("شمارهٔ فصل را وارد کنید"); return; }
        }
        String before = library.toString();
        int changed = 0;
        for (int i = 0; i < chapters.length(); i++) {
            JSONObject chapter = chapters.optJSONObject(i);
            int number;
            if (animeMode) {
                if (animeSeasonNumber(chapter) != season) continue;
                number = animeEpisodeNumber(chapter, i);
            } else {
                number = chapterStorageIndex(chapter, i);
            }
            if (number < start || number > end || chapterState(chapter) == state) continue;
            putJson(chapter, "state", state);
            if (chapter != null) chapter.remove("done");
            changed++;
        }
        if (changed > 0 && !saveLibrary()) {
            restoreLibrary(before);
            return;
        }
        dialog.dismiss();
        showChapters();
    }

    private int parseRangeNumber(String value) {
        try { return Integer.parseInt(englishDigits(value == null ? "" : value.trim())); }
        catch (NumberFormatException ignored) { return -1; }
    }

    private View chapterStateLegend() {
        LinearLayout legend = new LinearLayout(this);
        legend.setGravity(Gravity.CENTER);
        legend.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        addChapterStateLegendItem(legend, "نخوانده", SURFACE);
        addChapterStateLegendItem(legend, "آمادهٔ خواندن", themeSecondaryColor());
        addChapterStateLegendItem(legend, "خوانده‌شده", PURPLE);
        return legend;
    }

    private void addChapterStateLegendItem(LinearLayout parent, String label, int color) {
        LinearLayout item = new LinearLayout(this);
        item.setGravity(Gravity.CENTER);
        View dot = new View(this);
        dot.setBackground(roundDrawable(color, color, 5));
        item.addView(dot, new LinearLayout.LayoutParams(dp(8), dp(8)));
        TextView text = text(label, 10, MUTED);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(-2, -2);
        textParams.rightMargin = dp(4);
        item.addView(text, textParams);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -1, 1);
        parent.addView(item, params);
    }

    private String chapterSelectionKey(JSONObject chapter, int position) {
        return Integer.toString(chapterStorageIndex(chapter, position));
    }

    private void toggleChapterSelection(JSONObject chapter, int position) {
        if (chapter == null) return;
        String key = chapterSelectionKey(chapter, position);
        if (!selectedChapterKeys.add(key)) selectedChapterKeys.remove(key);
        refreshChapterSelection();
    }

    private void clearChapterSelection() {
        selectedChapterKeys.clear();
        refreshChapterSelection();
    }

    private void refreshChapterSelection() {
        if (!"chapters".equals(page) || appColumn == null) return;
        JSONObject manhwa = activeManhwa();
        if (manhwa == null) return;
        if (animeMode) { showChapters(); return; }
        if (appColumn.getChildCount() > 0) appColumn.removeViewAt(0);
        addChaptersHeader(manhwa);
        if (chapterAdapter != null) chapterAdapter.notifyDataSetChanged();
    }

    private void renameSelectedChapter() {
        if (selectedChapterKeys.size() != 1) return;
        JSONObject manhwa = activeManhwa();
        JSONArray chapters = checklistArray(manhwa);
        if (chapters == null) return;
        String selectedKey = selectedChapterKeys.iterator().next();
        JSONObject selected = null;
        int selectedPosition = -1;
        for (int i = 0; i < chapters.length(); i++) {
            JSONObject chapter = chapters.optJSONObject(i);
            if (chapter != null && selectedKey.equals(chapterSelectionKey(chapter, i))) {
                selected = chapter;
                selectedPosition = i;
                break;
            }
        }
        if (selected == null) return;
        String before = library.toString();
        EditText title = edit("نام " + contentUnitLabel(), false);
        title.setText(chapterLabel(selected, selectedPosition));
        title.setSelection(title.length());
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("ویرایش " + contentUnitLabel())
                .setView(dialogPadding(title)).setNegativeButton("لغو", null)
                .setPositiveButton("ذخیره", null).create();
        JSONObject chapterToEdit = selected;
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String newTitle = title.getText().toString().trim();
            if (newTitle.isEmpty()) { title.setError("نام " + contentUnitLabel() + " را وارد کنید"); return; }
            putJson(chapterToEdit, "title", newTitle);
            if (!saveLibrary()) {
                restoreLibrary(before);
            }
            dialog.dismiss();
            refreshChapterSelection();
        }));
        dialog.show();
        styleDialogWindow(dialog);
        dialog.getWindow().setSoftInputMode(WindowManagerFlags.ADJUST_RESIZE);
    }

    private void ensureChapterStorageIndexes(JSONArray chapters) {
        Set<Integer> used = new HashSet<>();
        int next = 1;
        for (int i = 0; i < chapters.length(); i++) {
            JSONObject chapter = chapters.optJSONObject(i);
            if (chapter != null) next = Math.max(next, chapter.optInt("storageIndex", i + 1) + 1);
        }
        for (int i = 0; i < chapters.length(); i++) {
            JSONObject chapter = chapters.optJSONObject(i);
            if (chapter == null) continue;
            int saved = chapter.optInt("storageIndex", 0);
            if (saved < 1 || !used.add(saved)) {
                while (used.contains(next)) next++;
                putJson(chapter, "storageIndex", next);
                used.add(next++);
            }
        }
    }

    private int chapterInsertionIndex(JSONArray chapters, String newLabel) {
        BigDecimal number = chapterNumber(newLabel);
        if (number == null) return chapters.length();
        for (int i = 0; i < chapters.length(); i++) {
            JSONObject chapter = chapters.optJSONObject(i);
            BigDecimal existing = chapterNumber(chapter == null ? "" : chapter.optString("title"));
            if (existing != null && existing.compareTo(number) > 0) return i;
        }
        return chapters.length();
    }

    private BigDecimal chapterNumber(String label) {
        if (label == null) return null;
        Matcher matcher = CHAPTER_NUMBER_PATTERN.matcher(label.trim());
        if (!matcher.matches()) return null;
        try { return new BigDecimal(matcher.group(1)); }
        catch (NumberFormatException ignored) { return null; }
    }

    private int chapterStorageIndex(JSONObject chapter, int position) {
        int saved = chapter == null ? 0 : chapter.optInt("storageIndex", 0);
        return saved > 0 ? saved : position + 1;
    }

    private int animeSeasonNumber(JSONObject episode) {
        return Math.max(1, episode == null ? 1 : episode.optInt("seasonNumber", 1));
    }

    private int animeEpisodeNumber(JSONObject episode, int position) {
        int saved = episode == null ? 0 : episode.optInt("episodeNumber", 0);
        return saved > 0 ? saved : chapterStorageIndex(episode, position);
    }

    private String chapterLabel(JSONObject chapter, int index) {
        String savedTitle = chapter == null ? "" : englishDigits(chapter.optString("title").trim());
        return savedTitle.isEmpty() ? contentUnitLabel() + " " + (index + 1) : savedTitle;
    }

    private String chapterCellNumber(JSONObject chapter, int index) {
        return formatCount(animeMode ? animeEpisodeNumber(chapter, index) : chapterStorageIndex(chapter, index));
    }

    private int lastSeenChapter(JSONObject manhwa) {
        return Math.max(0, lastProgressedChapterIndex(manhwa));
    }

    private int lastProgressedChapterIndex(JSONObject manhwa) {
        JSONArray chapters = checklistArray(manhwa);
        if (chapters == null || chapters.length() == 0) return -1;
        int lastSeen = -1;
        for (int i = 0; i < chapters.length(); i++) {
            JSONObject chapter = chapters.optJSONObject(i);
            if (chapterState(chapter) > 0) lastSeen = i;
        }
        return lastSeen;
    }

    private void openChapters(JSONObject manhwa) {
        selectedChapterKeys.clear();
        activeManhwaId = manhwa.optString("id");
        chaptersReturnPage = manhwaInfoReturnPage;
        showChapters();
    }

    private long nextRecommendationId() {
        Set<Long> used = new HashSet<>();
        JSONArray manhwas = contentItems();
        if (manhwas != null) for (int i = 0; i < manhwas.length(); i++) {
            JSONObject manhwa = manhwas.optJSONObject(i);
            if (manhwa != null) used.add(manhwa.optLong("recommendationId", -1));
        }
        long candidate = 1;
        while (used.contains(candidate) && candidate < Long.MAX_VALUE) candidate++;
        return candidate;
    }

    private interface ManhwaTextEntryAction { void save(String value); }

    private interface ManhwaGenreSelectionAction {
        void save(LinkedHashSet<String> selected, ArrayList<String> available);
    }

    private static final class ManhwaJsonData {
        String title = "";
        String description = "";
        String coverUrl = "";
        int unitCount;
        boolean hasTitle;
        boolean hasDescription;
        boolean hasGenres;
        boolean hasUnitCount;
        boolean hasCoverUrl;
        boolean hasSeasons;
        boolean validSeasons = true;
        final ArrayList<String> genres = new ArrayList<>();
        final ArrayList<AnimeSeasonRange> seasons = new ArrayList<>();
    }

    private static final class AnimeSeasonRange {
        final int seasonNumber;
        final int episodeStart;
        final int episodeEnd;

        AnimeSeasonRange(int seasonNumber, int episodeStart, int episodeEnd) {
            this.seasonNumber = seasonNumber;
            this.episodeStart = episodeStart;
            this.episodeEnd = episodeEnd;
        }

        int episodeCount() { return episodeEnd - episodeStart + 1; }
    }

    private static final class ManhwaJsonImportTarget {
        final EditText title;
        final EditText count;
        final String[] description;
        final LinkedHashSet<String> selectedGenres;
        final ArrayList<String> availableGenres;
        final View descriptionEntry;
        final View genreEntry;
        final EditText seasonRanges;
        final EditText coverUrl;
        final boolean isNew;
        final boolean anime;
        final String originalCoverName;
        boolean coverDownloadInProgress;

        ManhwaJsonImportTarget(EditText title, EditText count,
                             String[] description,
                             LinkedHashSet<String> selectedGenres, ArrayList<String> availableGenres,
                              View descriptionEntry, View genreEntry, EditText seasonRanges, EditText coverUrl,
                              boolean isNew, boolean anime,
                             String originalCoverName) {
            this.title = title;
            this.count = count;
            this.description = description;
            this.selectedGenres = selectedGenres;
            this.availableGenres = availableGenres;
            this.descriptionEntry = descriptionEntry;
            this.genreEntry = genreEntry;
            this.seasonRanges = seasonRanges;
            this.coverUrl = coverUrl;
            this.isNew = isNew;
            this.anime = anime;
            this.originalCoverName = originalCoverName == null ? "" : originalCoverName;
        }
    }

    private void chooseManhwaJson() {
        ManhwaJsonImportTarget target = activeManhwaJsonImportTarget;
        if (target == null) {
            toast("فرم فعال پیدا نشد.");
            return;
        }
        EditText source = edit("متن JSON را اینجا بچسبانید", true);
        source.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        source.setMinLines(8);
        source.setMaxLines(14);
        source.setGravity(Gravity.TOP | Gravity.RIGHT);
        ScrollView scroll = new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.addView(source);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("ورود متن JSON")
                .setView(dialogPadding(scroll))
                .setNegativeButton("لغو", null)
                .setPositiveButton("بررسی", null)
                .create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (activeManhwaJsonImportTarget != target) {
                toast("فرم بسته شده است.");
                dialog.dismiss();
                return;
            }
            try {
                ManhwaJsonData imported = parseManhwaJsonText(source.getText().toString(), target.anime);
                dialog.dismiss();
                showManhwaJsonImportPreview(imported, target);
            } catch (JSONException error) {
                source.setError("ساختار JSON را بررسی کنید");
            }
        }));
        dialog.show();
        styleDialogWindow(dialog);
        dialog.getWindow().setSoftInputMode(WindowManagerFlags.ADJUST_RESIZE);
    }

    private ManhwaJsonData parseManhwaJsonText(String source, boolean anime) throws JSONException {
        String normalized = source == null ? "" : source.trim();
        if (normalized.startsWith("```") && normalized.endsWith("```")) {
            int firstLine = normalized.indexOf('\n');
            if (firstLine >= 0) normalized = normalized.substring(firstLine + 1, normalized.length() - 3).trim();
        }
        JSONObject root = new JSONObject(normalized);
        JSONObject nested = root.optJSONObject(anime ? "anime" : "manhwa");
        if (nested == null) nested = root.optJSONObject(anime ? "manhwa" : "anime");
        return parseManhwaJson(nested == null ? root : nested, anime);
    }

    private String readUtf8(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = input.read(buffer)) != -1) output.write(buffer, 0, read);
        return new String(output.toByteArray(), StandardCharsets.UTF_8);
    }

    private ManhwaJsonData parseManhwaJson(JSONObject json, boolean anime) {
        ManhwaJsonData result = new ManhwaJsonData();
        result.hasTitle = json.has("title") || json.has("name");
        result.title = json.optString("title", json.optString("name", "")).trim();
        result.hasDescription = json.has("description");
        result.description = json.optString("description", "").trim();
        result.hasCoverUrl = json.has("coverUrl") || json.has("cover_url");
        result.coverUrl = json.optString("coverUrl", json.optString("cover_url", "")).trim();
        String countKey = anime ? "episodeCount" : "chapterCount";
        Object rawCount = json.has(countKey) ? json.opt(countKey)
                : json.has(anime ? "episodesCount" : "chaptersCount")
                ? json.opt(anime ? "episodesCount" : "chaptersCount") : json.opt("count");
        result.hasUnitCount = json.has(countKey) || json.has(anime ? "episodesCount" : "chaptersCount") || json.has("count");
        if (rawCount instanceof Number) result.unitCount = ((Number) rawCount).intValue();
        else if (rawCount instanceof String) {
            try { result.unitCount = Integer.parseInt(englishDigits(((String) rawCount).trim())); }
            catch (NumberFormatException ignored) { result.unitCount = 0; }
        }

        result.hasSeasons = json.has("seasons");
        JSONArray rawSeasons = json.optJSONArray("seasons");
        if (result.hasSeasons && rawSeasons == null) result.validSeasons = false;
        if (rawSeasons != null) {
            Set<Integer> seasonNumbers = new HashSet<>();
            for (int i = 0; i < rawSeasons.length(); i++) {
                JSONObject rawSeason = rawSeasons.optJSONObject(i);
                if (rawSeason == null) { result.validSeasons = false; continue; }
                int seasonNumber = rawSeason.optInt("seasonNumber", rawSeason.optInt("season", i + 1));
                int episodeStart = rawSeason.optInt("episodeStart", rawSeason.optInt("startEpisode", 1));
                int episodeEnd = rawSeason.optInt("episodeEnd", rawSeason.optInt("endEpisode", 0));
                if (episodeEnd < episodeStart && rawSeason.has("episodeCount")) {
                    episodeEnd = episodeStart + Math.max(0, rawSeason.optInt("episodeCount", 0)) - 1;
                }
                if (seasonNumber < 1 || seasonNumber > 500 || episodeStart < 1
                        || episodeEnd < episodeStart || episodeEnd > 5000
                        || !seasonNumbers.add(seasonNumber)) {
                    result.validSeasons = false;
                    continue;
                }
                result.seasons.add(new AnimeSeasonRange(seasonNumber, episodeStart, episodeEnd));
            }
        }
        if (totalEpisodes(result.seasons) > 5000) result.validSeasons = false;
        if (!result.hasUnitCount && result.hasSeasons && result.validSeasons) {
            result.unitCount = totalEpisodes(result.seasons);
            result.hasUnitCount = true;
        }

        String genreKey = json.has("genres") ? "genres" : "tags";
        result.hasGenres = json.has(genreKey);
        Object rawGenres = json.opt(genreKey);
        if (rawGenres instanceof JSONArray) {
            JSONArray genres = (JSONArray) rawGenres;
            for (int i = 0; i < genres.length(); i++) {
                String value = genres.optString(i, "").trim();
                if (!value.isEmpty()) result.genres.add(value);
            }
        } else if (rawGenres instanceof String) {
            for (String value : ((String) rawGenres).split("[,،]")) {
                if (!value.trim().isEmpty()) result.genres.add(value.trim());
            }
        }
        return result;
    }

    private int totalEpisodes(List<AnimeSeasonRange> seasons) {
        int total = 0;
        if (seasons != null) for (AnimeSeasonRange season : seasons) total += season.episodeCount();
        return total;
    }

    private String formatAnimeSeasonRanges(List<AnimeSeasonRange> seasons) {
        ArrayList<AnimeSeasonRange> ordered = new ArrayList<>();
        if (seasons != null) ordered.addAll(seasons);
        Collections.sort(ordered, (first, second) -> Integer.compare(first.seasonNumber, second.seasonNumber));
        StringBuilder text = new StringBuilder();
        for (AnimeSeasonRange season : ordered) {
            if (text.length() > 0) text.append('\n');
            text.append('S').append(season.seasonNumber).append(": ")
                    .append(season.episodeStart).append('-').append(season.episodeEnd);
        }
        return text.toString();
    }

    private ArrayList<AnimeSeasonRange> parseAnimeSeasonRanges(String source, int defaultCount) {
        ArrayList<AnimeSeasonRange> ranges = new ArrayList<>();
        String normalized = englishDigits(source == null ? "" : source.trim());
        if (normalized.isEmpty()) {
            if (defaultCount < 1 || defaultCount > 5000) throw new IllegalArgumentException("تعداد قسمت‌ها معتبر نیست.");
            ranges.add(new AnimeSeasonRange(1, 1, defaultCount));
            return ranges;
        }
        Set<Integer> usedSeasons = new HashSet<>();
        int total = 0;
        for (String rawLine : normalized.split("\\r?\\n")) {
            String line = rawLine.trim();
            if (line.isEmpty()) continue;
            Matcher matcher = ANIME_SEASON_RANGE_PATTERN.matcher(line);
            if (!matcher.matches()) throw new IllegalArgumentException("قالب هر فصل باید مانند S1: 1-12 باشد.");
            int seasonNumber = Integer.parseInt(matcher.group(1));
            int episodeStart = Integer.parseInt(matcher.group(2));
            int episodeEnd = Integer.parseInt(matcher.group(3));
            if (seasonNumber < 1 || seasonNumber > 500 || episodeStart < 1 || episodeEnd < episodeStart
                    || episodeEnd > 5000 || !usedSeasons.add(seasonNumber)) {
                throw new IllegalArgumentException("شماره یا بازهٔ فصل‌ها معتبر نیست.");
            }
            AnimeSeasonRange range = new AnimeSeasonRange(seasonNumber, episodeStart, episodeEnd);
            ranges.add(range);
            total += range.episodeCount();
            if (total > 5000) throw new IllegalArgumentException("مجموع قسمت‌ها نباید بیشتر از 5000 باشد.");
        }
        if (ranges.isEmpty()) throw new IllegalArgumentException("بازهٔ فصل‌ها را وارد کنید.");
        Collections.sort(ranges, (first, second) -> Integer.compare(first.seasonNumber, second.seasonNumber));
        return ranges;
    }

    private void showManhwaJsonImportPreview(ManhwaJsonData data, ManhwaJsonImportTarget target) {
        StringBuilder preview = new StringBuilder();
        if (data.hasTitle) appendPreviewLine(preview, contentTitleLabel(), data.title.isEmpty() ? "خالی" : data.title);
        if (data.hasDescription) appendPreviewLine(preview, "توضیحات", shortManhwaPreview(data.description));
        if (data.hasCoverUrl) appendPreviewLine(preview, "نشانی جلد", data.coverUrl.isEmpty() ? "خالی" : shortManhwaPreview(data.coverUrl));
        if (target.anime && data.hasSeasons) appendPreviewLine(preview, "فصل‌ها", data.validSeasons
                ? shortManhwaPreview(formatAnimeSeasonRanges(data.seasons)) : "ساختار نامعتبر");
        if (data.hasGenres) appendPreviewLine(preview, "ژانرها", data.genres.isEmpty()
                ? "خالی" : android.text.TextUtils.join("، ", data.genres));
        if (data.hasUnitCount) appendPreviewLine(preview, "تعداد " + contentUnitPlural(), target.isNew
                ? (data.unitCount > 0 ? Integer.toString(data.unitCount) : "نامعتبر؛ بدون تغییر")
                : "در ویرایش اعمال نمی‌شود");
        if (preview.length() == 0) preview.append("فیلد قابل استفاده‌ای در فایل پیدا نشد.");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("بازبینی اطلاعات JSON")
                .setMessage(preview.toString())
                .setNegativeButton("لغو", null)
                .setPositiveButton("اعمال به فرم", (ignored, which) -> applyManhwaJson(data, target))
                .create();
        dialog.show();
        styleDialogWindow(dialog);
    }

    private void appendPreviewLine(StringBuilder text, String label, String value) {
        if (text.length() > 0) text.append('\n');
        text.append(englishDigits(label)).append(": ").append(englishDigits(value));
    }

    private String shortManhwaPreview(String value) {
        String preview = value == null ? "" : value.replace('\n', ' ').trim();
        if (preview.isEmpty()) return "خالی";
        return preview.length() <= 150 ? preview : preview.substring(0, 147) + "…";
    }

    private void applyManhwaJson(ManhwaJsonData data, ManhwaJsonImportTarget target) {
        if (activeManhwaJsonImportTarget != target) {
            toast("فرم بسته شده است.");
            return;
        }
        if (target.anime && data.hasSeasons && !data.validSeasons) {
            toast("ساختار فصل‌ها در JSON معتبر نیست.");
            return;
        }
        if (data.hasTitle) target.title.setText(englishDigits(data.title));
        if (data.hasDescription) {
            target.description[0] = data.description;
            updateManhwaDialogEntryButton(target.descriptionEntry, descriptionButtonSummary(data.description));
        }
        if (target.isNew && data.hasUnitCount && data.unitCount > 0 && data.unitCount <= 5000) {
            target.count.setText(Integer.toString(data.unitCount));
        }
        if (target.anime && target.seasonRanges != null && data.hasSeasons) {
            target.seasonRanges.setText(formatAnimeSeasonRanges(data.seasons));
            int total = totalEpisodes(data.seasons);
            if (total > 0 && total <= 5000) target.count.setText(Integer.toString(total));
        }
        if (data.hasCoverUrl && target.coverUrl != null) target.coverUrl.setText(data.coverUrl);
        if (data.hasGenres) {
            target.selectedGenres.clear();
            for (String raw : data.genres) {
                String genre = canonicalGenre(titleCaseWords(raw));
                if (genre.isEmpty()) continue;
                String matching = null;
                for (String available : target.availableGenres) {
                    if (available.equalsIgnoreCase(genre)) { matching = available; break; }
                }
                if (matching == null) {
                    target.availableGenres.add(genre);
                    matching = genre;
                }
                target.selectedGenres.add(matching);
            }
            updateManhwaDialogEntryButton(target.genreEntry, genreButtonSummary(target.selectedGenres));
        }
        toast("اطلاعات به فرم اضافه شد؛ برای ذخیره، ثبت اطلاعات را بزن.");
        if (data.hasCoverUrl && !data.coverUrl.isEmpty()) downloadCoverFromUrl(data.coverUrl, target);
    }

    private void downloadCoverFromUrl(String address, ManhwaJsonImportTarget target) {
        final String requestedUrl = address == null ? "" : address.trim();
        if (target == null || requestedUrl.isEmpty()) return;
        if (!requestedUrl.regionMatches(true, 0, "https://", 0, 8)) {
            toast("برای دانلود خودکار جلد، نشانی امن HTTPS لازم است.");
            return;
        }
        target.coverDownloadInProgress = true;
        toast("در حال دریافت جلد...");
        imageExecutor.execute(() -> {
            Bitmap bitmap = null;
            String savedName = null;
            String failure = null;
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(requestedUrl).openConnection();
                connection.setConnectTimeout(12000);
                connection.setReadTimeout(15000);
                connection.setInstanceFollowRedirects(true);
                connection.setRequestProperty("Accept", "image/*");
                connection.connect();
                if (!"https".equalsIgnoreCase(connection.getURL().getProtocol())) {
                    throw new IOException("Cover URL redirected away from HTTPS");
                }
                int status = connection.getResponseCode();
                int length = connection.getContentLength();
                if (status < 200 || status >= 300) throw new IOException("Cover URL request failed");
                final int maxBytes = 15 * 1024 * 1024;
                if (length > maxBytes) throw new IOException("Cover image is too large");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream(length > 0 ? (int) length : 32768);
                byte[] chunk = new byte[8192];
                int read;
                try (InputStream input = connection.getInputStream()) {
                    int total = 0;
                    while ((read = input.read(chunk)) != -1) {
                        total += read;
                        if (total > maxBytes) throw new IOException("Cover image is too large");
                        bytes.write(chunk, 0, read);
                    }
                }
                byte[] image = bytes.toByteArray();
                BitmapFactory.Options bounds = new BitmapFactory.Options();
                bounds.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(image, 0, image.length, bounds);
                if (bounds.outWidth <= 0 || bounds.outHeight <= 0) throw new IOException("Invalid cover image");
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                while (Math.max(bounds.outWidth, bounds.outHeight) / options.inSampleSize > 1800) {
                    options.inSampleSize *= 2;
                }
                bitmap = BitmapFactory.decodeByteArray(image, 0, image.length, options);
                if (bitmap == null) throw new IOException("Could not decode cover image");
                savedName = saveCoverBitmap(bitmap);
                bitmap = null;
                if (savedName == null) throw new IOException("Could not store cover image");
            } catch (Exception error) {
                android.util.Log.w("ManhwaReader", "Automatic cover download failed", error);
                failure = "دریافت یا ذخیرهٔ جلد ناموفق بود؛ می‌توانی جلد را دستی انتخاب کنی.";
            } finally {
                if (connection != null) connection.disconnect();
                if (bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
            }
            final String fileName = savedName;
            final String errorMessage = failure;
            runOnUiThread(() -> {
                target.coverDownloadInProgress = false;
                if (activeManhwaJsonImportTarget != target || isFinishing()) {
                    if (fileName != null) deleteCoverFile(fileName);
                    return;
                }
                if (fileName == null) {
                    if (errorMessage != null) toast(errorMessage);
                    return;
                }
                String previous = pendingCoverName;
                pendingCoverName = fileName;
                if (manhwaCoverPreview != null) renderCoverPreview(manhwaCoverPreview, fileName);
                if (coverRemoveButton != null) coverRemoveButton.setVisibility(View.VISIBLE);
                if (previous != null && !previous.isEmpty() && !previous.equals(fileName)
                        && !previous.equals(target.originalCoverName)) deleteCoverFile(previous);
                toast("جلد در پوشهٔ اختصاصی ذخیره شد.");
            });
        });
    }

    private void showManhwaJsonPrompt(EditText titleField) {
        boolean anime = animeMode;
        try {
            String prompt = loadJsonPrompt(anime)
                    .replace("__TITLE_JSON__", JSONObject.quote(titleField.getText().toString().trim()));
            TextView content = text(prompt, 14, TEXT);
            content.setTextIsSelectable(true);
            content.setGravity(Gravity.RIGHT | Gravity.TOP);
            content.setPadding(dp(14), dp(12), dp(14), dp(12));
            setRtlTextAlignment(content);
            ScrollView scroll = new ScrollView(this);
            scroll.setVerticalScrollBarEnabled(false);
            scroll.addView(content);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("پرامپت دریافت اطلاعات")
                    .setView(dialogPadding(scroll))
                    .setNegativeButton("بستن", null)
                    .setNeutralButton("اشتراک‌گذاری", (ignored, which) -> shareManhwaPrompt(prompt))
                    .setPositiveButton("کپی متن", (ignored, which) -> {
                        copyToClipboard("manhwa-json-prompt", prompt);
                        toast("پرامپت کپی شد.");
                    })
                    .create();
            dialog.show();
            styleDialogWindow(dialog);
        } catch (IOException error) {
            toast("خواندن متن راهنما ناموفق بود.");
        }
    }

    private String loadJsonPrompt(boolean anime) throws IOException {
        String key = anime ? "anime" : "manhwa";
        SharedPreferences preferences = getSharedPreferences("ai_prompts", MODE_PRIVATE);
        String saved = preferences.getString(key, null);
        if (saved != null) return saved;
        String assetName = anime ? "anime_info_prompt.txt" : "manhwa_info_prompt.txt";
        try (InputStream input = getAssets().open(assetName)) {
            return readUtf8(input);
        }
    }

    private void showJsonPromptEditor(boolean anime) {
        String key = anime ? "anime" : "manhwa";
        String title = anime ? "پرامپت انیمه" : "پرامپت مانهوا";
        String defaultPrompt;
        try {
            String assetName = anime ? "anime_info_prompt.txt" : "manhwa_info_prompt.txt";
            try (InputStream input = getAssets().open(assetName)) {
                defaultPrompt = readUtf8(input);
            }
        } catch (IOException error) {
            toast("خواندن پرامپت پیش‌فرض ناموفق بود.");
            return;
        }
        EditText editor = edit("متن پرامپت", true);
        editor.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editor.setGravity(Gravity.TOP | Gravity.RIGHT);
        editor.setMinLines(12);
        editor.setText(getSharedPreferences("ai_prompts", MODE_PRIVATE).getString(key, defaultPrompt));
        ScrollView promptScroll = new ScrollView(this);
        promptScroll.setVerticalScrollBarEnabled(false);
        promptScroll.addView(editor);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("ویرایش " + title)
                .setView(dialogPadding(promptScroll))
                .setNegativeButton("لغو", null)
                .setNeutralButton("پیش‌فرض", null)
                .setPositiveButton("ذخیره", null)
                .create();
        dialog.setOnShowListener(ignored -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                getSharedPreferences("ai_prompts", MODE_PRIVATE).edit()
                        .putString(key, editor.getText().toString()).apply();
                dialog.dismiss();
                toast("پرامپت ذخیره شد.");
            });
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
                getSharedPreferences("ai_prompts", MODE_PRIVATE).edit().remove(key).apply();
                dialog.dismiss();
                toast("پرامپت پیش‌فرض بازیابی شد.");
            });
        });
        dialog.show();
        styleDialogWindow(dialog);
        dialog.getWindow().setSoftInputMode(WindowManagerFlags.ADJUST_RESIZE);
    }

    private void shareManhwaPrompt(String prompt) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "پرامپت اطلاعات " + contentTitleLabel());
        share.putExtra(Intent.EXTRA_TEXT, prompt);
        try { startActivity(Intent.createChooser(share, "اشتراک‌گذاری نمونه")); }
        catch (Exception error) { toast("اشتراک‌گذاری در دسترس نیست."); }
    }

    private View manhwaDialogEntryButton(String title, String summary, String icon, Runnable action) {
        LinearLayout button = new LinearLayout(this);
        button.setGravity(Gravity.CENTER_VERTICAL);
        button.setOrientation(LinearLayout.HORIZONTAL);
        button.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        button.setPadding(dp(13), dp(7), dp(13), dp(7));
        button.setBackground(roundDrawable(SURFACE_ALT, 0xFF40384A, 12));

        NativeIconView glyph = new NativeIconView(this, icon, PURPLE);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(23), dp(23));
        iconParams.leftMargin = dp(11);
        button.addView(glyph, iconParams);

        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.VERTICAL);
        labels.setGravity(Gravity.CENTER_VERTICAL);
        labels.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        TextView titleView = text(title, 12, MUTED);
        setRtlTextAlignment(titleView);
        labels.addView(titleView, new LinearLayout.LayoutParams(-1, -2));
        TextView summaryView = text(summary, 14, TEXT);
        summaryView.setSingleLine(true);
        summaryView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        setRtlTextAlignment(summaryView);
        labels.addView(summaryView, new LinearLayout.LayoutParams(-1, -2));
        button.addView(labels, new LinearLayout.LayoutParams(0, -2, 1));
        button.setTag(summaryView);
        button.setContentDescription(title);
        button.setMinimumHeight(dp(58));
        button.setClickable(true);
        button.setFocusable(true);
        button.setOnClickListener(v -> action.run());
        applyPressScale(button);
        return button;
    }

    private void updateManhwaDialogEntryButton(View button, String summary) {
        if (button != null && button.getTag() instanceof TextView) {
            ((TextView) button.getTag()).setText(englishDigits(summary));
        }
    }

    private String genreButtonSummary(Set<String> selected) {
        if (selected.isEmpty()) return "هیچ ژانری انتخاب نشده";
        return android.text.TextUtils.join(" · ", selected);
    }

    private String descriptionButtonSummary(String value) {
        String summary = value == null ? "" : value.replace('\n', ' ').trim();
        return summary.isEmpty() ? "برای نوشتن توضیحات بزنید" : summary;
    }

    private void showManhwaTextEntryDialog(String title, String hint, String initialValue,
                                         ManhwaTextEntryAction action) {
        EditText input = edit(hint, false);
        input.setSingleLine(false);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setMinLines(6);
        input.setMaxLines(9);
        input.setHorizontallyScrolling(false);
        input.setVerticalScrollBarEnabled(false);
        input.setGravity(Gravity.TOP | Gravity.RIGHT);
        input.setPadding(dp(14), dp(12), dp(14), dp(12));
        input.setBackground(roundDrawable(SURFACE_ALT, PURPLE, 15));
        setRtlTextAlignment(input);
        input.setText(englishDigits(initialValue == null ? "" : initialValue));
        input.setSelection(input.length());

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        form.addView(input, new LinearLayout.LayoutParams(-1, dp(210)));
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(dialogPadding(form))
                .setNegativeButton("لغو", null)
                .setPositiveButton("ذخیره", null)
                .create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    action.save(input.getText().toString().trim());
                    dialog.dismiss();
                }));
        dialog.show();
        styleDialogWindow(dialog);
        dialog.getWindow().setSoftInputMode(WindowManagerFlags.ADJUST_RESIZE);
    }

    private void showManhwaGenreSelectionDialog(List<String> availableGenres, Set<String> selectedGenres,
                                              ManhwaGenreSelectionAction action) {
        ArrayList<String> draftAvailable = new ArrayList<>(availableGenres);
        LinkedHashSet<String> draftSelected = new LinkedHashSet<>(selectedGenres);
        EditText search = edit("جستجو یا افزودن ژانرها با ،", false);
        setRtlTextAlignment(search);
        LinearLayout searchRow = new LinearLayout(this);
        searchRow.setGravity(Gravity.CENTER_VERTICAL);
        searchRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        searchRow.addView(clearableInput(search), new LinearLayout.LayoutParams(0, dp(48), 1));
        FrameLayout addGenre = iconButton("add", PURPLE, false, null);
        LinearLayout.LayoutParams addParams = new LinearLayout.LayoutParams(dp(48), dp(48));
        addParams.leftMargin = dp(7);
        searchRow.addView(addGenre, addParams);

        GenreCloud cloud = new GenreCloud(this);
        Runnable render = () -> {
            String query = search.getText().toString();
            int comma = Math.max(query.lastIndexOf(','), query.lastIndexOf('،'));
            if (comma >= 0) query = query.substring(comma + 1);
            cloud.showGenres(genreArray(draftAvailable), draftSelected, query.trim());
        };
        render.run();
        search.addTextChangedListener(watcher(render));
        addGenre.setOnClickListener(v -> {
            boolean any = false;
            for (String part : search.getText().toString().split("[,،]")) {
                String value = canonicalGenre(titleCaseWords(part));
                if (value.isEmpty()) continue;
                any = true;
                String matching = null;
                for (String available : draftAvailable) {
                    if (available.equalsIgnoreCase(value)) { matching = available; break; }
                }
                if (matching == null) { draftAvailable.add(value); matching = value; }
                draftSelected.add(matching);
            }
            if (!any) { search.setError("نام ژانر را بنویس"); return; }
            search.setText("");
            render.run();
        });

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        content.addView(searchRow, fieldParams());
        ScrollView scroll = new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setFillViewport(false);
        scroll.addView(cloud);
        content.addView(scroll, new LinearLayout.LayoutParams(-1, dp(270)));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("ژانرهای " + contentTitleLabel())
                .setView(dialogPadding(content))
                .setNegativeButton("لغو", null)
                .setPositiveButton("ذخیره", null)
                .create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    action.save(new LinkedHashSet<>(draftSelected), new ArrayList<>(draftAvailable));
                    dialog.dismiss();
                }));
        dialog.show();
        styleDialogWindow(dialog);
        dialog.getWindow().setSoftInputMode(WindowManagerFlags.ADJUST_RESIZE);
    }

    private void showManhwaDialog(JSONObject existing) {
        boolean isNew = existing == null;
        boolean anime = animeMode;
        String itemLabel = anime ? "انیمه" : "مانهوا";
        String unitLabel = anime ? "قسمت" : "فصل";
        String originalCoverName = isNew ? "" : existing.optString("cover", "");
        String sourcePage = page;
        String libraryBeforeSave = library.toString();
        final boolean[] coverCommitted = {false};
        pendingCoverName = originalCoverName;

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(8), dp(4), dp(8), dp(4));

        LinearLayout coverRow = new LinearLayout(this);
        coverRow.setGravity(Gravity.CENTER);
        FrameLayout previewFrame = new FrameLayout(this);
        previewFrame.setBackground(roundDrawable(SURFACE_ALT, 0xFF40384A, 10));
        previewFrame.setClipToOutline(true);
        manhwaCoverPreview = new ImageView(this);
        manhwaCoverPreview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        previewFrame.addView(manhwaCoverPreview, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout placeholder = new LinearLayout(this);
        placeholder.setGravity(Gravity.CENTER);
        placeholder.setOrientation(LinearLayout.VERTICAL);
        placeholder.addView(new NativeIconView(this, "image", PURPLE), new LinearLayout.LayoutParams(dp(30), dp(30)));
        TextView chooseCoverLabel = text("انتخاب جلد", 13, PURPLE);
        chooseCoverLabel.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams chooseLabelParams = new LinearLayout.LayoutParams(-2, -2);
        chooseLabelParams.topMargin = dp(8);
        placeholder.addView(chooseCoverLabel, chooseLabelParams);
        previewFrame.addView(placeholder, new FrameLayout.LayoutParams(-1, -1));
        previewFrame.setTag(placeholder);
        previewFrame.setOnClickListener(v -> chooseManhwaCover());
        previewFrame.setContentDescription("انتخاب جلد " + itemLabel);
        coverRow.addView(previewFrame, new FrameLayout.LayoutParams(dp(128), dp(182), Gravity.CENTER));
        coverRemoveButton = iconButton("close", TEXT, true, () -> {
            pendingCoverName = "";
            renderCoverPreview(manhwaCoverPreview, pendingCoverName);
            if (coverRemoveButton != null) coverRemoveButton.setVisibility(View.GONE);
        });
        FrameLayout.LayoutParams removeCoverParams = new FrameLayout.LayoutParams(dp(36), dp(36),
                Gravity.TOP | Gravity.RIGHT);
        removeCoverParams.topMargin = dp(5);
        removeCoverParams.rightMargin = dp(5);
        previewFrame.addView(coverRemoveButton, removeCoverParams);
        coverRemoveButton.setVisibility(originalCoverName.isEmpty() ? View.GONE : View.VISIBLE);
        LinearLayout.LayoutParams coverRowParams = new LinearLayout.LayoutParams(-1, dp(194));
        coverRowParams.bottomMargin = dp(9);
        renderCoverPreview(manhwaCoverPreview, pendingCoverName);

        EditText title = edit("نام " + itemLabel, false);
        title.setText(isNew ? "" : englishDigits(existing.optString("title")));
        final String[] descriptionValue = {isNew ? "" : existing.optString("description", "")};
        final View[] descriptionEntry = new View[1];
        descriptionEntry[0] = manhwaDialogEntryButton("توضیحات کوتاه",
                descriptionButtonSummary(descriptionValue[0]), "edit", () ->
                        showManhwaTextEntryDialog("توضیحات کوتاه " + itemLabel, "توضیحات کوتاه " + itemLabel,
                                descriptionValue[0], value -> {
                                    descriptionValue[0] = value;
                                    updateManhwaDialogEntryButton(descriptionEntry[0], descriptionButtonSummary(value));
                                }));
        EditText count = edit("تعداد " + (anime ? "قسمت" : "فصل"), false);
        count.setInputType(InputType.TYPE_CLASS_NUMBER);
        form.addView(clearableInput(title), fieldParams());
        View promptButton = textButton("ساخت پرامپت", "help", themeSecondaryColor(),
                () -> showManhwaJsonPrompt(title));
        LinearLayout.LayoutParams promptParams = new LinearLayout.LayoutParams(-1, dp(50));
        promptParams.bottomMargin = dp(8);
        form.addView(promptButton, promptParams);
        View importJsonButton = textButton("ورود اطلاعات از JSON", "scan_text", themeSecondaryColor(), this::chooseManhwaJson);
        LinearLayout.LayoutParams importJsonParams = new LinearLayout.LayoutParams(-1, dp(50));
        importJsonParams.bottomMargin = dp(8);
        form.addView(importJsonButton, importJsonParams);

        LinearLayout detailsPanel = new LinearLayout(this);
        detailsPanel.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams descriptionEntryParams = new LinearLayout.LayoutParams(-1, dp(58));
        descriptionEntryParams.bottomMargin = dp(9);
        detailsPanel.addView(coverRow, coverRowParams);
        EditText coverUrl = edit("لینک مستقیم دانلود جلد", false);
        coverUrl.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI
                | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        detailsPanel.addView(clearableInput(coverUrl), fieldParams());
        View downloadCoverButton = textButton("دریافت جلد از لینک", "download", themeSecondaryColor(), () -> {
            ManhwaJsonImportTarget target = activeManhwaJsonImportTarget;
            String address = coverUrl.getText().toString().trim();
            if (address.isEmpty()) { coverUrl.setError("لینک مستقیم تصویر را وارد کنید"); return; }
            if (target == null) { toast("فرم فعال پیدا نشد."); return; }
            if (target.coverDownloadInProgress) { toast("دریافت جلد در حال انجام است."); return; }
            downloadCoverFromUrl(address, target);
        });
        LinearLayout.LayoutParams downloadCoverParams = new LinearLayout.LayoutParams(-1, dp(48));
        downloadCoverParams.bottomMargin = dp(9);
        detailsPanel.addView(downloadCoverButton, downloadCoverParams);
        detailsPanel.addView(descriptionEntry[0], descriptionEntryParams);
        EditText seasonRanges = isNew && anime ? edit("S1: 1-12\nS2: 13-25", true) : null;
        if (seasonRanges != null) {
            TextView seasonHint = text("هر فصل در یک خط؛ شمارهٔ قسمت می‌تواند بین فصل‌ها ادامه پیدا کند یا دوباره از 1 شروع شود.", 12, MUTED);
            seasonHint.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams seasonHintParams = new LinearLayout.LayoutParams(-1, -2);
            seasonHintParams.topMargin = dp(4);
            seasonHintParams.bottomMargin = dp(4);
            detailsPanel.addView(seasonHint, seasonHintParams);
            seasonRanges.setMinLines(2);
            seasonRanges.setMaxLines(6);
            seasonRanges.setGravity(Gravity.TOP | Gravity.RIGHT);
            detailsPanel.addView(seasonRanges, fieldParams());
        }
        if (isNew) detailsPanel.addView(clearableInput(count), fieldParams());

        final LinkedHashSet<String> selected = new LinkedHashSet<>();
        JSONArray oldGenres = isNew ? null : existing.optJSONArray("genres");
        if (oldGenres != null) for (int i = 0; i < oldGenres.length(); i++) selected.add(oldGenres.optString(i));
        final ArrayList<String> availableGenres = new ArrayList<>();
        JSONArray existingGenres = allGenres();
        for (int i = 0; i < existingGenres.length(); i++) availableGenres.add(existingGenres.optString(i));
        final View[] genreEntry = new View[1];
        genreEntry[0] = manhwaDialogEntryButton("ژانرها", genreButtonSummary(selected), "tag", () ->
                showManhwaGenreSelectionDialog(availableGenres, selected, (newSelected, newAvailable) -> {
                    selected.clear();
                    selected.addAll(newSelected);
                    availableGenres.clear();
                    availableGenres.addAll(newAvailable);
                    updateManhwaDialogEntryButton(genreEntry[0], genreButtonSummary(selected));
                }));
        LinearLayout.LayoutParams genreEntryParams = new LinearLayout.LayoutParams(-1, dp(58));
        genreEntryParams.bottomMargin = dp(9);
        detailsPanel.addView(genreEntry[0], genreEntryParams);

        if (isNew) {
            TextView detailsToggle = text("نمایش جزئیات", 14, PURPLE);
            detailsToggle.setGravity(Gravity.CENTER);
            detailsToggle.setBackground(roundDrawable(SURFACE, 0xFF40384A, 12));
            boolean showFullForm = getSharedPreferences("app_settings", MODE_PRIVATE)
                    .getBoolean("ai_access_unavailable", false);
            detailsPanel.setVisibility(showFullForm ? View.VISIBLE : View.GONE);
            detailsToggle.setText(showFullForm ? "پنهان کردن جزئیات" : "نمایش جزئیات");
            detailsToggle.setOnClickListener(v -> {
                boolean expand = detailsPanel.getVisibility() != View.VISIBLE;
                detailsPanel.setVisibility(expand ? View.VISIBLE : View.GONE);
                detailsToggle.setText(expand ? "پنهان کردن جزئیات" : "نمایش جزئیات");
            });
            LinearLayout.LayoutParams detailButtonParams = new LinearLayout.LayoutParams(-1, dp(44));
            detailButtonParams.bottomMargin = dp(8);
            form.addView(detailsToggle, detailButtonParams);
        }
        form.addView(detailsPanel, new LinearLayout.LayoutParams(-1, -2));

        activeManhwaJsonImportTarget = new ManhwaJsonImportTarget(title, count,
                descriptionValue, selected, availableGenres, descriptionEntry[0], genreEntry[0],
                seasonRanges, coverUrl, isNew, anime, originalCoverName);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(false);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.addView(form);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(isNew ? "افزودن " + itemLabel : "ویرایش " + itemLabel)
                .setView(dialogPadding(scroll))
                .setNegativeButton("لغو", null)
                .setPositiveButton("ثبت اطلاعات", null)
                .create();
        dialog.setOnDismissListener(ignored -> {
            if (!coverCommitted[0] && pendingCoverName != null && !pendingCoverName.isEmpty()
                    && !pendingCoverName.equals(originalCoverName)) deleteCoverFile(pendingCoverName);
            if (activeManhwaJsonImportTarget != null && activeManhwaJsonImportTarget.title == title) {
                activeManhwaJsonImportTarget = null;
            }
            manhwaCoverPreview = null;
            coverRemoveButton = null;
        });
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String manhwaTitle = titleCaseWords(englishDigits(title.getText().toString()));
            if (manhwaTitle.isEmpty()) { title.setError("نام " + itemLabel + " را وارد کن"); return; }
            if (activeManhwaJsonImportTarget != null && activeManhwaJsonImportTarget.title == title
                    && activeManhwaJsonImportTarget.coverDownloadInProgress) {
                toast("صبر کن تا دریافت جلد تمام شود.");
                return;
            }
            int unitTotal = 0;
            ArrayList<AnimeSeasonRange> animeSeasons = null;
            if (isNew) {
                String value = englishDigits(count.getText().toString().trim());
                String ranges = seasonRanges == null ? "" : seasonRanges.getText().toString().trim();
                if (anime && !ranges.isEmpty()) {
                    try {
                        animeSeasons = parseAnimeSeasonRanges(ranges, 0);
                        unitTotal = totalEpisodes(animeSeasons);
                        count.setText(Integer.toString(unitTotal));
                    } catch (IllegalArgumentException error) {
                        seasonRanges.setError(error.getMessage());
                        return;
                    }
                } else {
                    if (value.isEmpty()) { count.setError("تعداد " + unitLabel + " را وارد کن"); return; }
                    try { unitTotal = Integer.parseInt(value); }
                    catch (NumberFormatException error) { count.setError("تعداد " + unitLabel + " معتبر نیست"); return; }
                    if (anime) animeSeasons = parseAnimeSeasonRanges("", unitTotal);
                }
                if (unitTotal < 1 || unitTotal > 5000) {
                    count.setError("تعداد باید بین 1 تا 5000 باشد"); return;
                }
            }
            LinkedHashSet<String> normalizedSelected = new LinkedHashSet<>();
            for (String value : selected) {
                String normalized = canonicalGenre(titleCaseWords(value));
                if (!normalized.isEmpty()) normalizedSelected.add(normalized);
            }
            JSONArray savedGenres = new JSONArray();
            for (String value : normalizedSelected) savedGenres.put(value);
            JSONArray globalGenres = contentGenres();
            if (globalGenres == null) globalGenres = new JSONArray();
            for (String value : normalizedSelected) appendUniqueGenre(globalGenres, value);
            try { library.put(contentGenresKey(), globalGenres); } catch (JSONException ignoredError) { }

            if (isNew) {
                JSONArray chapters = new JSONArray();
                String checklistKey = anime ? "episodes" : "chapters";
                String countKey = anime ? "episodeCount" : "chapterCount";
                if (anime) {
                    int index = 0;
                    for (AnimeSeasonRange season : animeSeasons) {
                        for (int episodeNumber = season.episodeStart; episodeNumber <= season.episodeEnd; episodeNumber++) {
                            JSONObject episode = newChapter();
                            putJson(episode, "title", "قسمت " + episodeNumber);
                            putJson(episode, "seasonNumber", season.seasonNumber);
                            putJson(episode, "episodeNumber", episodeNumber);
                            putJson(episode, "storageIndex", index + 1);
                            putJson(episode, "orderIndex", index);
                            chapters.put(episode);
                            index++;
                        }
                    }
                } else {
                    for (int i = 0; i < unitTotal; i++) {
                        JSONObject chapter = newChapter();
                        putJson(chapter, "title", unitLabel + " " + (i + 1));
                        putJson(chapter, "storageIndex", i + 1);
                        putJson(chapter, "orderIndex", i);
                        chapters.put(chapter);
                    }
                }
                JSONObject manhwa = new JSONObject();
                try {
                    manhwa.put("id", UUID.randomUUID().toString());
                    manhwa.put("recommendationId", nextRecommendationId());
                    manhwa.put("title", manhwaTitle);
                    manhwa.put("description", descriptionValue[0]);
                    manhwa.put("genres", savedGenres);
                    manhwa.put("cover", pendingCoverName == null ? "" : pendingCoverName);
                    manhwa.put("orderIndex", nextOrderIndex(contentItems()));
                    manhwa.put(checklistKey, chapters);
                    manhwa.put(countKey, unitTotal);
                    JSONArray items = contentItems();
                    if (items == null) { items = new JSONArray(); library.put(contentArrayKey(), items); }
                    items.put(manhwa);
                } catch (JSONException ignoredError) { }
                if (!saveLibrary()) {
                    restoreLibrary(libraryBeforeSave);
                    dialog.dismiss();
                    showManhwaShelf();
                    return;
                }
                coverCommitted[0] = true;
                rebuildRecommendationWeights();
                persistRecommendationState();
                activeManhwaId = manhwa.optString("id");
                manhwaInfoReturnPage = anime ? "allAnimes" : "allManhwas";
                chaptersReturnPage = manhwaInfoReturnPage;
                dialog.dismiss();
                showChapters();
            } else {
                putJson(existing, "title", manhwaTitle);
                putJson(existing, "description", descriptionValue[0]);
                putJson(existing, "genres", savedGenres);
                putJson(existing, "cover", pendingCoverName == null ? "" : pendingCoverName);
                if (!saveLibrary()) {
                    restoreLibrary(libraryBeforeSave);
                    dialog.dismiss();
                    if ("chapters".equals(sourcePage)) showChapters();
                    else if ("manhwaInfo".equals(sourcePage) || "animeInfo".equals(sourcePage)) showManhwaInfo(activeManhwa());
                    else showDashboard();
                    return;
                }
                coverCommitted[0] = true;
                rebuildRecommendationWeights();
                persistRecommendationState();
                if (!originalCoverName.isEmpty() && !originalCoverName.equals(pendingCoverName)) {
                    deleteCoverFile(originalCoverName);
                }
                dialog.dismiss();
                if ("chapters".equals(sourcePage)) showChapters();
                else if ("manhwaInfo".equals(sourcePage) || "animeInfo".equals(sourcePage)) showManhwaInfo(existing);
                else if (manhwaAdapter != null) manhwaAdapter.notifyDataSetChanged();
            }
        }));
        dialog.show();
        styleDialogWindow(dialog);
        dialog.getWindow().setSoftInputMode(WindowManagerFlags.ADJUST_RESIZE);
    }


    private JSONArray genreArray(List<String> values) {
        JSONArray result = new JSONArray();
        ArrayList<String> sorted = new ArrayList<>(values);
        Collections.sort(sorted, String.CASE_INSENSITIVE_ORDER);
        for (String value : sorted) result.put(value);
        return result;
    }

    private String titleCaseWords(String value) {
        String[] words = value == null ? new String[0] : value.trim().toLowerCase(Locale.ROOT).split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            if (result.length() > 0) result.append(' ');
            int index = 0;
            while (index < word.length()) {
                int codePoint = word.codePointAt(index);
                if (Character.isLetter(codePoint)) {
                    result.append(word.substring(0, index));
                    result.append(new String(Character.toChars(codePoint)).toUpperCase(Locale.ROOT));
                    result.append(word.substring(index + Character.charCount(codePoint)));
                    break;
                }
                index += Character.charCount(codePoint);
            }
            if (index >= word.length()) result.append(word);
        }
        return result.toString();
    }

    private void chooseManhwaCover() {
        try {
            if (Build.VERSION.SDK_INT >= 33) {
                Intent photoPicker = new Intent(MediaStore.ACTION_PICK_IMAGES);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, REQUEST_COVER);
            } else {
                openGalleryIntent();
            }
        } catch (android.content.ActivityNotFoundException error) {
            try { openGalleryIntent(); }
            catch (Exception fallbackError) { toast("گالری تصویر در دسترس نیست."); }
        } catch (Exception error) { toast("گالری تصویر در دسترس نیست."); }
    }

    private void openGalleryIntent() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        startActivityForResult(gallery, REQUEST_COVER);
    }

    private void loadImageForCover(Uri uri) {
        imageExecutor.execute(() -> {
            Bitmap bitmap = null;
            try {
                BitmapFactory.Options bounds = new BitmapFactory.Options(); bounds.inJustDecodeBounds = true;
                try (java.io.InputStream input = getContentResolver().openInputStream(uri)) { BitmapFactory.decodeStream(input, null, bounds); }
                int sample = 1;
                while (Math.max(bounds.outWidth, bounds.outHeight) / sample > 1800) sample *= 2;
                BitmapFactory.Options options = new BitmapFactory.Options(); options.inSampleSize = sample;
                try (java.io.InputStream input = getContentResolver().openInputStream(uri)) { bitmap = BitmapFactory.decodeStream(input, null, options); }
            } catch (Exception error) { android.util.Log.e("ManhwaReader", "Could not open cover image", error); }
            Bitmap loaded = bitmap;
            if (loaded == null) {
                runOnUiThread(() -> toast("خواندن تصویر ناموفق بود."));
                return;
            }
            String fileName = saveCoverBitmap(loaded);
            runOnUiThread(() -> {
                if (fileName == null) toast("ذخیرهٔ جلد ناموفق بود.");
                else {
                    pendingCoverName = fileName;
                    if (manhwaCoverPreview != null) renderCoverPreview(manhwaCoverPreview, pendingCoverName);
                    if (coverRemoveButton != null) coverRemoveButton.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private String saveCoverBitmap(Bitmap bitmap) {
        String name = "cover-" + UUID.randomUUID().toString() + ".jpg";
        try (java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream()) {
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 88, output)) return null;
            output.flush();
            return storage.writeImage(name, output.toByteArray()) ? name : null;
        } catch (Exception error) { android.util.Log.e("ManhwaReader", "Could not save cover", error); return null; }
        finally { if (!bitmap.isRecycled()) bitmap.recycle(); }
    }

    private Bitmap loadCoverBitmap(String name) {
        if (name == null || name.isEmpty()) return null;
        Bitmap cached = coverCache.get(name); if (cached != null && !cached.isRecycled()) return cached;
        try {
            byte[] bytes = storage.readImage(name);
            if (bytes == null) return null;
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bounds);
            int targetEdge = Math.max(720, Math.min(1200, getResources().getDisplayMetrics().widthPixels));
            int sample = 1;
            while (bounds.outWidth > 0 && bounds.outHeight > 0
                    && Math.max(bounds.outWidth, bounds.outHeight) / (sample * 2) >= targetEdge) sample *= 2;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sample;
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            if (bitmap != null) coverCache.put(name, bitmap);
            return bitmap;
        } catch (Exception error) {
            android.util.Log.w("ManhwaReader", "Could not load cover " + name, error);
            return null;
        }
    }

    private void bindCover(ImageView image, View placeholder, String name) {
        image.setTag(name);
        image.setImageDrawable(null);
        Bitmap cached = name == null ? null : coverCache.get(name);
        if (cached != null && !cached.isRecycled()) {
            image.setImageBitmap(cached);
            placeholder.setVisibility(View.GONE);
            return;
        }
        placeholder.setVisibility(View.VISIBLE);
        if (name == null || name.isEmpty()) return;
        imageExecutor.execute(() -> {
            Bitmap loaded = loadCoverBitmap(name);
            runOnUiThread(() -> {
                if (isFinishing() || image.getTag() == null || !name.equals(image.getTag())) return;
                image.setImageBitmap(loaded);
                placeholder.setVisibility(loaded == null ? View.VISIBLE : View.GONE);
            });
        });
    }

    private void renderCoverPreview(ImageView image, String name) {
        if (image == null) return;
        if (image.getParent() instanceof FrameLayout) {
            View placeholder = ((FrameLayout) image.getParent()).getTag() instanceof View
                    ? (View) ((FrameLayout) image.getParent()).getTag() : null;
            if (placeholder != null) bindCover(image, placeholder, name);
        }
    }

    private void deleteCoverFile(String name) {
        if (name == null || name.isEmpty()) return;
        coverCache.remove(name);
        try { imageExecutor.execute(() -> storage.deleteImage(name)); }
        catch (java.util.concurrent.RejectedExecutionException ignored) { }
    }

    private void showGenreFilter() {
        LinearLayout box = new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(dp(8), dp(4), dp(8), dp(4));
        EditText search = edit("جستجوی ژانرها", false); search.setSingleLine(true); box.addView(clearableInput(search), fieldParams());
        GenreCloud cloud = new GenreCloud(this); Set<String> selected = new HashSet<>(activeGenreFilter);
        Runnable render = () -> cloud.showGenres(allGenres(), selected, search.getText().toString()); render.run();
        search.addTextChangedListener(watcher(render)); box.addView(cloud);
        ScrollView scroll = new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false); scroll.setHorizontalScrollBarEnabled(false); scroll.addView(box);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("فیلتر ژانرها").setView(dialogPadding(scroll))
                .setNegativeButton("لغو", null).setPositiveButton("اعمال", null).create();
        dialog.setOnShowListener(v -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(b -> {
            activeGenreFilter.clear(); activeGenreFilter.addAll(selected); dialog.dismiss();
            styleIconButton(genreFilterButton, !activeGenreFilter.isEmpty()); manhwaAdapter.notifyDataSetChanged();
        }));
        dialog.show();
        styleDialogWindow(dialog);
    }

    private JSONObject newChapter() {
        JSONObject chapter = new JSONObject();
        try { chapter.put("state", 0); }
        catch (JSONException ignored) { }
        return chapter;
    }

    private JSONArray allGenres() {
        JSONArray genres = contentGenres(); JSONArray result = new JSONArray();
        List<String> values = new ArrayList<>(); if (genres != null) for (int i = 0; i < genres.length(); i++) values.add(genres.optString(i));
        Collections.sort(values, String.CASE_INSENSITIVE_ORDER); for (String value : values) result.put(value); return result;
    }

    private JSONObject activeManhwa() {
        JSONArray manhwas = contentItems(); if (manhwas == null) return null;
        for (int i = 0; i < manhwas.length(); i++) { JSONObject manhwa = manhwas.optJSONObject(i); if (manhwa != null && activeManhwaId != null && activeManhwaId.equals(manhwa.optString("id"))) return manhwa; }
        return null;
    }

    private void handleBack() {
        if (drawerOpen) { closeDrawer(); return; }
        if (!selectedChapterKeys.isEmpty() && "chapters".equals(page)) {
            selectedChapterKeys.clear();
            refreshChapterSelection();
            return;
        }
        if (!selectedManhwaIds.isEmpty() && showingAllManhwas()) {
            selectedManhwaIds.clear();
            refreshDashboardSelection();
            return;
        }
        if ("chapters".equals(page)) {
            manhwaInfoReturnPage = chaptersReturnPage;
            showManhwaInfo(activeManhwa());
        }
        else if ("manhwaInfo".equals(page) || "animeInfo".equals(page)) showManhwaShelfForPage(manhwaInfoReturnPage);
        else if ("allManhwas".equals(page) || "allAnimes".equals(page)) { selectedManhwaIds.clear(); showDashboard(); }
        else if ("settings".equals(page) || "themes".equals(page) || "recommendations".equals(page)
                || "animeRecommendations".equals(page)
                || "statistics".equals(page)
                || "tagSettings".equals(page)) showDashboard();
        else super.onBackPressed();
    }

    private void showManhwaShelfForPage(String target) {
        if ("allAnimes".equals(target) || "animeRecommendations".equals(target)) setAnimeMode(true);
        else setAnimeMode(false);
        if ("recommendations".equals(target) || "animeRecommendations".equals(target)) showRecommendation();
        else showDashboard();
    }

    @Override public void onBackPressed() { handleBack(); }

    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideAndroidStatusBar();
    }

    @Override protected void onDestroy() {
        if (Build.VERSION.SDK_INT >= 33 && systemBackCallback != null) {
            getOnBackInvokedDispatcher().unregisterOnBackInvokedCallback(systemBackCallback);
            systemBackCallback = null;
        }
        imageExecutor.shutdown();
        super.onDestroy();
    }

    private View makeHeader(String title, String subtitle, String leftIcon, String rightIcon, Runnable left, Runnable right) {
        return makeHeader(title, subtitle, leftIcon, rightIcon, left, right, 0);
    }

    private View makeHeader(String title, String subtitle, String leftIcon, String rightIcon,
                            Runnable left, Runnable right, int sideInsetDp) {
        return makeHeader(title, subtitle, leftIcon, rightIcon, left, right, sideInsetDp, 68);
    }

    private View makeHeader(String title, String subtitle, String leftIcon, String rightIcon,
                            Runnable left, Runnable right, int sideInsetDp, int titleInsetDp) {
        return makeHeader(title, subtitle, leftIcon, rightIcon, left, right, sideInsetDp, titleInsetDp, false);
    }

    private View makeHeader(String title, String subtitle, String leftIcon, String rightIcon,
                            Runnable left, Runnable right, int sideInsetDp, int titleInsetDp, boolean plainIcons) {
        FrameLayout header = new FrameLayout(this); header.setBackgroundColor(BG);
        LinearLayout textBlock = new LinearLayout(this); textBlock.setGravity(Gravity.CENTER); textBlock.setOrientation(LinearLayout.VERTICAL);
        titleView = text(title, 18, TEXT); titleView.setGravity(Gravity.CENTER); titleView.setMaxLines(1); titleView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        textBlock.addView(titleView, new LinearLayout.LayoutParams(-1, -2));
        subtitleView = text(subtitle, 12, MUTED); subtitleView.setGravity(Gravity.CENTER); subtitleView.setMaxLines(1);
        if (!subtitle.isEmpty()) textBlock.addView(subtitleView, new LinearLayout.LayoutParams(-1, -2));
        FrameLayout.LayoutParams titleParams = new FrameLayout.LayoutParams(-1, -1);
        titleParams.leftMargin = dp(titleInsetDp); titleParams.rightMargin = dp(titleInsetDp); header.addView(textBlock, titleParams);
        if (leftIcon != null) {
            FrameLayout.LayoutParams button = new FrameLayout.LayoutParams(dp(50), dp(50), Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            button.rightMargin = dp(sideInsetDp);
            View actionButton = plainIcons ? plainIconButton(leftIcon, PURPLE, left)
                    : iconButton(leftIcon, PURPLE, false, left);
            actionButton.setTag("header_left_action");
            if ("favorite".equals(leftIcon)) actionButton.setTag("content_favorite");
            header.addView(actionButton, button);
        }
        if (rightIcon != null) {
            FrameLayout.LayoutParams button = new FrameLayout.LayoutParams(dp(50), dp(50), Gravity.LEFT | Gravity.CENTER_VERTICAL);
            button.leftMargin = dp(sideInsetDp);
            View actionButton = plainIcons ? plainIconButton(rightIcon, PURPLE, right)
                    : iconButton(rightIcon, PURPLE, false, right);
            actionButton.setTag("header_right_action");
            if ("favorite".equals(rightIcon)) actionButton.setTag("content_favorite");
            header.addView(actionButton, button);
        }
        return header;
    }

    private FrameLayout iconButton(String icon, int color, boolean selected, Runnable action) {
        return makeIconButton(icon, color, selected, action, true);
    }

    private FrameLayout plainIconButton(String icon, int color, Runnable action) {
        return makeIconButton(icon, color, false, action, false);
    }

    private FrameLayout makeIconButton(String icon, int color, boolean selected, Runnable action, boolean circular) {
        FrameLayout button = new FrameLayout(this);
        if (circular) styleIconButton(button, selected);
        else button.setBackgroundColor(Color.TRANSPARENT);
        NativeIconView glyph = new NativeIconView(this, icon, color);
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(dp(24), dp(24), Gravity.CENTER); button.addView(glyph, p);
        button.setContentDescription(iconDescription(icon)); button.setClickable(true); button.setFocusable(true);
        applyPressScale(button);
        if (action != null) button.setOnClickListener(v -> action.run());
        return button;
    }

    private FrameLayout weightedIconButton(String icon, Runnable action) {
        FrameLayout holder = new FrameLayout(this);
        holder.addView(iconButton(icon, PURPLE, false, action),
                new FrameLayout.LayoutParams(dp(52), dp(52), Gravity.CENTER));
        return holder;
    }

    private void styleIconButton(FrameLayout button, boolean selected) {
        GradientDrawable bg = new GradientDrawable(); bg.setShape(GradientDrawable.OVAL);
        bg.setColor(selected ? 0xFF30203D : SURFACE);
        bg.setStroke(dp(1), selected ? PURPLE : themedStrokeColor(0xFF302A38)); button.setBackground(bg);
    }

    private View textButton(String label, String icon, int color, Runnable action) {
        LinearLayout row = new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL); row.setOrientation(LinearLayout.HORIZONTAL);
        NativeIconView glyph = new NativeIconView(this, icon, color);
        row.addView(glyph, new LinearLayout.LayoutParams(dp(22), dp(22)));
        TextView text = text(label, 15, TEXT); LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(0, -2, 1); tp.leftMargin = dp(12); text.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL); row.addView(text, tp);
        row.setPadding(dp(14), 0, dp(14), 0);
        row.setBackground(roundDrawable(SURFACE, themedStrokeColor(color), 14));
        row.setOnClickListener(v -> action.run());
        row.setClickable(true); row.setFocusable(true); applyPressScale(row); return row;
    }

    private void applyPressScale(View view) {
        applyPressScale(view, .92f);
    }

    private void applyPressScale(View view, float pressedScale) {
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().cancel();
                v.animate().scaleX(pressedScale).scaleY(pressedScale)
                        .setDuration(80).setInterpolator(new DecelerateInterpolator()).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.animate().cancel();
                v.animate().scaleX(1f).scaleY(1f).setDuration(150)
                        .setInterpolator(new android.view.animation.OvershootInterpolator(1.15f)).start();
            }
            return false;
        });
    }

    private String iconDescription(String icon) {
        switch (icon) {
            case "menu": return "منو"; case "add": return "افزودن"; case "filter": return "فیلتر";
            case "back": return "بازگشت"; case "next": return "بعدی";
            case "help": return "راهنمای توالی‌های ویژه";
            case "undo": return "بازگردانی ویرایش";
            case "redo": return "بازانجام ویرایش";
            case "edit": return "ویرایش";
            case "hide": return "مخفی کردن مانهوا"; case "show": return "نمایش مانهوا";
            case "delete": return "حذف"; case "image": return "تصویر جلد"; case "settings": return "تنظیمات";
            case "favorite": return "افزودن به علاقه‌مندی‌ها";
            case "select": return "انتخاب مانهوا";
            case "range": return "آماده‌کردن بازهٔ چپترها";
            case "swap": return "تغییر فهرست";
            case "thumb_up": return "لایک";
            case "thumb_down": return "دیس لایک";
            default: return icon;
        }
    }

    private TextView emptyLabel(String value) {
        TextView empty = text(value, 15, MUTED); empty.setGravity(Gravity.CENTER); return empty;
    }

    private TextView text(String value, int sp, int color) {
        TextView view = new TextView(this); view.setText(englishDigits(value)); view.setTextSize(sp); view.setTextColor(color);
        view.setTypeface(vazir == null ? Typeface.DEFAULT : vazir); view.setGravity(Gravity.CENTER_VERTICAL);
        view.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG); view.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE); return view;
    }

    private void setRtlTextAlignment(TextView view) {
        view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        int vertical = view.getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
        view.setGravity(Gravity.RIGHT | (vertical == 0 ? Gravity.CENTER_VERTICAL : vertical));
        view.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        view.setTextDirection(View.TEXT_DIRECTION_RTL);
    }

    private EditText edit(String hint, boolean multiline) {
        EditText view = new EditText(this);
        view.setTextColor(TEXT); view.setHintTextColor(0xFF77717D);
        view.setTypeface(vazir == null ? Typeface.DEFAULT : vazir); view.setTextSize(16); view.setHint(englishDigits(hint));
        view.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE); view.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        view.setPadding(dp(13), dp(9), dp(13), dp(9)); view.setBackground(roundDrawable(SURFACE_ALT, 0xFF352D3F, 12));
        view.setSingleLine(!multiline); view.setGravity(multiline ? Gravity.TOP | Gravity.RIGHT : Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        view.setHighlightColor(selectionHighlightColor());
        if (Build.VERSION.SDK_INT >= 29) {
            view.setTextSelectHandle(new SelectionHandleDrawable(PURPLE, 0));
            view.setTextSelectHandleLeft(new SelectionHandleDrawable(PURPLE, 1));
            view.setTextSelectHandleRight(new SelectionHandleDrawable(PURPLE, -1));
        }
        return view;
    }

    private String englishDigits(String value) {
        if (value == null || value.isEmpty()) return value == null ? "" : value;
        StringBuilder out = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c >= '\u06F0' && c <= '\u06F9') out.append((char) ('0' + c - '\u06F0'));
            else if (c >= '\u0660' && c <= '\u0669') out.append((char) ('0' + c - '\u0660'));
            else out.append(c);
        }
        return out.toString();
    }

    private int selectionHighlightColor() {
        return Color.argb(0x88, Color.red(PURPLE), Color.green(PURPLE), Color.blue(PURPLE));
    }

    private View clearableInput(EditText input) {
        FrameLayout field = new FrameLayout(this);
        field.setLayoutDirection(input.getLayoutDirection());
        input.setPadding(dp(46), input.getPaddingTop(), input.getPaddingRight(), input.getPaddingBottom());
        field.addView(input, new FrameLayout.LayoutParams(-1, -1));

        FrameLayout clear = new FrameLayout(this);
        clear.setContentDescription("پاک کردن متن");
        clear.setClickable(true);
        clear.setFocusable(true);
        NativeIconView close = new NativeIconView(this, "close", MUTED);
        clear.addView(close, new FrameLayout.LayoutParams(dp(18), dp(18), Gravity.CENTER));
        clear.setOnClickListener(v -> { input.setText(""); input.requestFocus(); });
        applyPressScale(clear);
        field.addView(clear, new FrameLayout.LayoutParams(dp(42), -1, Gravity.LEFT | Gravity.CENTER_VERTICAL));
        return field;
    }

    private GradientDrawable roundDrawable(int color, int stroke, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        if (color == PURPLE && isGradientTheme(accentIndex)) applyGradientTheme(drawable, accentIndex);
        else drawable.setColor(color);
        drawable.setCornerRadius(dp(radiusDp));
        if (stroke != Color.TRANSPARENT) drawable.setStroke(dp(1), themedStrokeColor(stroke));
        return drawable;
    }

    private int themedStrokeColor(int color) {
        if (!lightTheme || color == PURPLE) return color;
        int red = Color.red(color), green = Color.green(color), blue = Color.blue(color);
        int maximum = Math.max(red, Math.max(green, blue));
        int minimum = Math.min(red, Math.min(green, blue));
        if (maximum < 115 && maximum - minimum < 75) return Color.rgb(216, 213, 221);
        return color;
    }

    private LinearLayout.LayoutParams fieldParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp(48)); params.bottomMargin = dp(9); return params;
    }

    private View dialogPadding(View child) {
        LinearLayout wrap = new LinearLayout(this);
        wrap.setGravity(Gravity.CENTER_HORIZONTAL);
        wrap.setPadding(dp(20), dp(8), dp(20), dp(8));
        wrap.addView(child, new LinearLayout.LayoutParams(-1, -2));
        return wrap;
    }

    private void styleDialogWindow(AlertDialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(roundDrawable(SURFACE,
                    lightTheme ? 0xFFD8D2E0 : 0xFF352D3F, 20));
            window.setGravity(Gravity.CENTER);
            int width = Math.max(dp(240), getResources().getDisplayMetrics().widthPixels - dp(36));
            window.setLayout(Math.min(width, dp(460)), android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        }
        int titleId = dialog.getContext().getResources().getIdentifier("alertTitle", "id", "android");
        TextView title = titleId == 0 ? null : dialog.findViewById(titleId);
        if (title != null) {
            title.setTextColor(TEXT);
            title.setTypeface(vazir == null ? Typeface.DEFAULT : vazir);
            title.setGravity(Gravity.CENTER);
        }
        TextView message = dialog.findViewById(android.R.id.message);
        if (message != null) {
            message.setTextColor(TEXT);
            message.setTypeface(vazir == null ? Typeface.DEFAULT : vazir);
            message.setGravity(Gravity.CENTER);
        }
        for (int which : new int[]{AlertDialog.BUTTON_POSITIVE, AlertDialog.BUTTON_NEGATIVE, AlertDialog.BUTTON_NEUTRAL}) {
            android.widget.Button button = dialog.getButton(which);
            if (button != null) {
                button.setTextColor(PURPLE);
                button.setTypeface(vazir == null ? Typeface.DEFAULT : vazir);
            }
        }
        int buttonPanelId = dialog.getContext().getResources().getIdentifier("buttonPanel", "id", "android");
        View buttonPanel = buttonPanelId == 0 ? null : dialog.findViewById(buttonPanelId);
        if (buttonPanel instanceof LinearLayout) ((LinearLayout) buttonPanel).setGravity(Gravity.CENTER_HORIZONTAL);
        if (window != null) applyDialogTypeface(window.getDecorView());
    }

    private void applyDialogTypeface(View view) {
        if (view instanceof TextView) ((TextView) view).setTypeface(vazir == null ? Typeface.DEFAULT : vazir);
        if (!(view instanceof ViewGroup)) return;
        ViewGroup group = (ViewGroup) view;
        for (int i = 0; i < group.getChildCount(); i++) applyDialogTypeface(group.getChildAt(i));
    }

    private TextWatcher watcher(Runnable run) {
        return new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { run.run(); }
            @Override public void afterTextChanged(Editable s) { }
        };
    }

    private int dp(float value) { return Math.round(value * getResources().getDisplayMetrics().density); }

    private final class SelectionHandleDrawable extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final int pointDirection;

        SelectionHandleDrawable(int color, int pointDirection) {
            paint.setColor(color);
            this.pointDirection = pointDirection;
        }

        @Override public void draw(Canvas canvas) {
            android.graphics.Rect bounds = getBounds();
            if (bounds.isEmpty()) return;
            canvas.save();
            canvas.translate(bounds.left, bounds.top);
            canvas.scale(bounds.width() / 48f, bounds.height() / 24f);
            float pathOffsetX = pointDirection > 0 ? 24f : pointDirection < 0 ? 0f : 12f;
            canvas.translate(pathOffsetX, -2f);
            Path shape = new Path();
            shape.moveTo(12f, 22f);
            shape.cubicTo(16.4183f, 22f, 20f, 18.4183f, 20f, 14f);
            shape.cubicTo(20f, 9.58172f, 12f, 2f, 12f, 2f);
            shape.cubicTo(12f, 2f, 4f, 9.58172f, 4f, 14f);
            shape.cubicTo(4f, 18.4183f, 7.58172f, 22f, 12f, 22f);
            shape.close();
            canvas.drawPath(shape, paint);
            canvas.restore();
        }

        @Override public int getIntrinsicWidth() { return dp(48); }
        @Override public int getIntrinsicHeight() { return dp(24); }
        @Override public void setAlpha(int alpha) { paint.setAlpha(alpha); invalidateSelf(); }
        @Override public void setColorFilter(ColorFilter colorFilter) { paint.setColorFilter(colorFilter); invalidateSelf(); }
        @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
    }
    private void toast(String value) { Toast.makeText(this, value, Toast.LENGTH_SHORT).show(); }

    private void putJson(JSONObject object, String key, Object value) {
        try { object.put(key, value); }
        catch (JSONException error) { android.util.Log.e("ManhwaReader", "Could not save " + key, error); }
    }

    private static final class WindowManagerFlags { static final int ADJUST_RESIZE = android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE; }

    private final class SwipeRoot extends FrameLayout {
        private float startX, startY, startProgress, lastProgress;
        private boolean gestureCandidate, trackingDrawer, emptyAnimeShelf;

        SwipeRoot(Context context) { super(context); }

        @Override public boolean dispatchTouchEvent(MotionEvent event) {
            int action = event.getActionMasked();
            if (action == MotionEvent.ACTION_DOWN) {
                startX = event.getX();
                startY = event.getY();
                float width = Math.max(1f, drawerWidth());
                boolean drawerVisible = drawerLayer.getVisibility() == View.VISIBLE;
                startProgress = drawerVisible
                        ? Math.max(0f, Math.min(1f, 1f - drawerPanel.getTranslationX() / width))
                        : drawerOpen ? 1f : 0f;
                int drawerEdgeWidth = Math.max(dp(72), getWidth() / 4);
                emptyAnimeShelf = "allAnimes".equals(page)
                        && (manhwaAdapter == null || manhwaAdapter.getCount() == 0);
                gestureCandidate = drawerVisible && startProgress > 0f
                        || drawerSwipeAvailable() && (startX >= getWidth() - drawerEdgeWidth || emptyAnimeShelf);
                trackingDrawer = false;
                lastProgress = startProgress;
                return super.dispatchTouchEvent(event);
            }

            if (!trackingDrawer && action == MotionEvent.ACTION_MOVE && gestureCandidate) {
                float dx = event.getX() - startX;
                float dy = event.getY() - startY;
                boolean opening = dx < 0f && startProgress < 1f;
                boolean closing = dx > 0f && startProgress > 0f;
                float horizontalBias = emptyAnimeShelf ? 0.9f : 1.15f;
                int horizontalSlop = emptyAnimeShelf ? dp(5) : dp(8);
                if (Math.abs(dx) > horizontalSlop && Math.abs(dx) > Math.abs(dy) * horizontalBias
                        && (opening || closing)) {
                    trackingDrawer = true;
                    beginDrawerGesture(startProgress);
                    MotionEvent cancel = MotionEvent.obtain(event);
                    cancel.setAction(MotionEvent.ACTION_CANCEL);
                    super.dispatchTouchEvent(cancel);
                    cancel.recycle();
                    lastProgress = startProgress - dx / Math.max(1f, drawerWidth());
                    updateDrawerGesture(lastProgress);
                    return true;
                }
            }

            if (trackingDrawer) {
                if (action == MotionEvent.ACTION_MOVE) {
                    lastProgress = startProgress - (event.getX() - startX) / Math.max(1f, drawerWidth());
                    updateDrawerGesture(lastProgress);
                    return true;
                }
                if (action == MotionEvent.ACTION_UP) {
                    boolean open = lastProgress >= 0.5f;
                    trackingDrawer = false;
                    gestureCandidate = false;
                    settleDrawerGesture(open);
                    return true;
                }
                if (action == MotionEvent.ACTION_CANCEL) {
                    trackingDrawer = false;
                    gestureCandidate = false;
                    settleDrawerGesture(startProgress >= 0.5f);
                    return true;
                }
                return true;
            }

            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                gestureCandidate = false;
            }
            return super.dispatchTouchEvent(event);
        }
    }
    private final class ManhwaAdapter extends BaseAdapter {
        private String query = "";
        private List<JSONObject> cachedManhwas;

        @Override public void notifyDataSetChanged() {
            cachedManhwas = null;
            super.notifyDataSetChanged();
        }

        private List<JSONObject> visible() {
            if (cachedManhwas != null) return cachedManhwas;
            List<JSONObject> result = new ArrayList<>(); JSONArray manhwas = contentItems(); if (manhwas == null) return cachedManhwas = result;
            query = searchField == null ? "" : englishDigits(searchField.getText().toString().trim()).toLowerCase(Locale.ROOT);
            for (int i = 0; i < manhwas.length(); i++) {
                JSONObject manhwa = manhwas.optJSONObject(i); if (manhwa == null) continue;
                if (!manhwa.optString("title").toLowerCase(Locale.ROOT).contains(query)) continue;
                JSONArray genres = manhwa.optJSONArray("genres"); boolean match = true;
                for (String selectedGenre : activeGenreFilter) { boolean found = false; if (genres != null) for (int j = 0; j < genres.length(); j++) if (selectedGenre.equals(genres.optString(j))) found = true; if (!found) { match = false; break; } }
                if (match) result.add(manhwa);
            }
            cachedManhwas = result;
            return cachedManhwas;
        }
        JSONObject getManhwa(int position) { return visible().get(position); }
        @Override public int getCount() { return visible().size(); }
        @Override public Object getItem(int position) { return getManhwa(position); }
        @Override public long getItemId(int position) { return position; }
        @Override public View getView(int position, View convert, ViewGroup parent) {
            JSONObject manhwa = getManhwa(position);
            boolean selected = selectedManhwaIds.contains(manhwa.optString("id"));
            LinearLayout card = new LinearLayout(MainActivity.this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setTag(manhwa.optString("id"));
            card.setPadding(dp(7), dp(7), dp(7), dp(4));
            card.setBackground(roundDrawable(selected ? (lightTheme ? 0xFFEDE3FA : 0xFF241B2C) : SURFACE,
                    selected ? PURPLE : 0xFF29242F, 13));
            card.setClickable(true); card.setFocusable(true);
            applyPressScale(card, .96f);
            card.setOnClickListener(v -> onManhwaCardTap(manhwa));
            card.setOnLongClickListener(v -> {
                String id = manhwa.optString("id");
                if (selectedManhwaIds.contains(id)) return startReorderDrag(card, animeMode ? "anime" : "manhwa", id);
                toggleManhwaSelection(manhwa);
                return true;
            });
            setReorderDropTarget(card, animeMode ? "anime" : "manhwa", manhwa.optString("id"));
            int gridWidth = parent.getWidth() > 0 ? parent.getWidth() : getResources().getDisplayMetrics().widthPixels;
            int cellWidth = Math.max(dp(120), (gridWidth - dp(24 + 10)) / 2);
            int artworkHeight = Math.max(dp(160), Math.round((cellWidth - dp(14)) * 1.42f));
            card.setLayoutParams(new AbsListView.LayoutParams(-1, artworkHeight + dp(42 + 11)));

            FrameLayout artwork = new FrameLayout(MainActivity.this);
            artwork.setBackground(roundDrawable(SURFACE_ALT, Color.TRANSPARENT, 9));
            artwork.setClipToOutline(true);
            ImageView cover = new ImageView(MainActivity.this);
            cover.setScaleType(ImageView.ScaleType.FIT_CENTER);
            cover.setBackground(roundDrawable(SURFACE_ALT, Color.TRANSPARENT, 9));
            artwork.addView(cover, new FrameLayout.LayoutParams(-1, -1));
            NativeIconView placeholder = new NativeIconView(MainActivity.this, animeMode ? "anime" : "manhwa", PURPLE);
            artwork.addView(placeholder, new FrameLayout.LayoutParams(dp(42), dp(42), Gravity.CENTER));
            artwork.setTag(placeholder);
            bindCover(cover, placeholder, manhwa.optString("cover", ""));
            FrameLayout selectionBadge = new FrameLayout(MainActivity.this);
            selectionBadge.setBackground(roundDrawable(selected ? PURPLE : 0x99000000,
                    selected ? PURPLE : Color.TRANSPARENT, 20));
            NativeIconView check = new NativeIconView(MainActivity.this, selected ? "reorder" : "select", Color.WHITE);
            selectionBadge.addView(check, new FrameLayout.LayoutParams(dp(22), dp(22), Gravity.CENTER));
            FrameLayout.LayoutParams badgeParams = new FrameLayout.LayoutParams(dp(32), dp(32), Gravity.TOP | Gravity.RIGHT);
            badgeParams.topMargin = dp(6); badgeParams.rightMargin = dp(6);
            artwork.addView(selectionBadge, badgeParams);
            selectionBadge.setVisibility(selected || !selectedManhwaIds.isEmpty() ? View.VISIBLE : View.GONE);
            card.addView(artwork, new LinearLayout.LayoutParams(-1, artworkHeight));

            TextView title = text(displayManhwaTitle(manhwa), 14, TEXT);
            title.setMaxLines(2); title.setEllipsize(android.text.TextUtils.TruncateAt.END);
            title.setGravity(Gravity.CENTER);
            card.addView(title, new LinearLayout.LayoutParams(-1, dp(42)));

            return card;
        }
    }

    private final class ChapterAdapter extends BaseAdapter {
        private JSONArray chapters() { JSONObject b = activeManhwa(); return b == null ? new JSONArray() : checklistArray(b); }
        @Override public int getCount() { return chapters() == null ? 0 : chapters().length(); }
        @Override public Object getItem(int position) { return chapters().optJSONObject(position); }
        @Override public long getItemId(int position) { return position; }
        @Override public View getView(int position, View convert, ViewGroup parent) {
            JSONObject chapter = chapters().optJSONObject(position);
            View cell = createChapterCell(chapter, position);
            cell.setLayoutParams(new AbsListView.LayoutParams(-1, dp(46)));
            return cell;
        }
    }

    private View createChapterCell(JSONObject chapter, int position) {
        if (chapter == null) chapter = newChapter();
        final JSONObject chapterItem = chapter;
        String key = chapterSelectionKey(chapterItem, position);
        boolean selected = selectedChapterKeys.contains(key);
        int state = chapterState(chapterItem);
        String number = chapterCellNumber(chapterItem, position);
        TextView chip = text(number, 13, TEXT);
        chip.setGravity(Gravity.CENTER);
        chip.setSingleLine(true);
        chip.setEllipsize(android.text.TextUtils.TruncateAt.END);
        chip.setPadding(dp(4), 0, dp(4), 0);
        chip.setTag(key);
        int stateColor = state == 2 ? PURPLE : state == 1 ? themeSecondaryColor() : SURFACE;
        int fillColor = selected ? (lightTheme ? 0xFFEDE3FA : 0xFF241B2C) : stateColor;
        int strokeColor = selected ? PURPLE : state > 0 ? stateColor : themedStrokeColor(0xFF29242F);
        chip.setBackground(roundDrawable(fillColor, strokeColor, 8));
        int numberColor = state == 0 || selected ? TEXT : contrastOnColor(stateColor);
        chip.setTextColor(numberColor);
        String stateLabel = state == 2 ? "خوانده‌شده" : state == 1 ? "آمادهٔ خواندن" : "خوانده‌نشده";
        String itemDescription = animeMode
                ? "فصل S" + formatCount(animeSeasonNumber(chapterItem)) + "، قسمت " + number
                : contentUnitLabel() + " " + number;
        chip.setContentDescription(itemDescription + "، " + stateLabel);
        chip.setOnClickListener(v -> {
            if (!selectedChapterKeys.isEmpty()) toggleChapterSelection(chapterItem, position);
            else toggleChapterDone(position);
        });
        chip.setOnLongClickListener(v -> {
            if (!animeMode && selectedChapterKeys.contains(key)) return startReorderDrag(chip, "chapter", key);
            toggleChapterSelection(chapterItem, position);
            return true;
        });
        if (!animeMode) setReorderDropTarget(chip, "chapter", key);
        return chip;
    }

    private void toggleChapterDone(int index) {
        JSONObject manhwa = activeManhwa(); JSONArray chapters = checklistArray(manhwa);
        if (chapters == null || index < 0 || index >= chapters.length()) return;
        JSONObject chapter = chapters.optJSONObject(index);
        putJson(chapter, "state", (chapterState(chapter) + 1) % 3);
        if (chapter != null) chapter.remove("done");
        saveLibrary();
        if (animeMode) showChapters();
        else if (chapterAdapter != null) chapterAdapter.notifyDataSetChanged();
    }

    private final class GenreCloud extends ViewGroup {
        GenreCloud(Context context) { super(context); setLayoutDirection(View.LAYOUT_DIRECTION_RTL); }
        void showGenres(JSONArray genres, Set<String> selected, String query) {
            removeAllViews(); String q = englishDigits(query.trim()).toLowerCase(Locale.ROOT);
            for (int i = 0; i < genres.length(); i++) {
                String genre = genres.optString(i); if (!genre.toLowerCase(Locale.ROOT).contains(q)) continue;
                TextView chip = text(genre, 13, selected.contains(genre) ? contrastOnAccent() : TEXT); chip.setGravity(Gravity.CENTER); chip.setPadding(dp(14), 0, dp(14), 0);
                chip.setBackground(roundDrawable(selected.contains(genre) ? PURPLE : SURFACE_ALT, selected.contains(genre) ? PURPLE : 0xFF40384A, 18));
                chip.setOnClickListener(v -> { if (!selected.add(genre)) selected.remove(genre); showGenres(genres, selected, query); });
                addView(chip, new MarginLayoutParams(-2, dp(38)));
            }
            requestLayout();
        }
        @Override protected ViewGroup.LayoutParams generateDefaultLayoutParams() { return new MarginLayoutParams(-2, dp(38)); }
        @Override protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) { return new MarginLayoutParams(p); }
        @Override protected boolean checkLayoutParams(ViewGroup.LayoutParams p) { return p instanceof MarginLayoutParams; }
        @Override protected void onMeasure(int widthSpec, int heightSpec) {
            int maxWidth = MeasureSpec.getSize(widthSpec), x = 0, y = 0, rowHeight = 0;
            for (int i = 0; i < getChildCount(); i++) { View child = getChildAt(i); measureChild(child, widthSpec, heightSpec); MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams(); int w = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin; int h = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin; if (x + w > maxWidth && x > 0) { x = 0; y += rowHeight + dp(6); rowHeight = 0; } x += w + dp(6); rowHeight = Math.max(rowHeight, h); }
            setMeasuredDimension(resolveSize(maxWidth, widthSpec), resolveSize(y + rowHeight + dp(2), heightSpec));
        }
        @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int x = 0, y = 0, rowHeight = 0, width = r - l;
            for (int i = 0; i < getChildCount(); i++) { View child = getChildAt(i); MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams(); int cw = child.getMeasuredWidth(), ch = child.getMeasuredHeight(); int total = cw + lp.leftMargin + lp.rightMargin; if (x + total > width && x > 0) { x = 0; y += rowHeight + dp(6); rowHeight = 0; } child.layout(x + lp.leftMargin, y + lp.topMargin, x + lp.leftMargin + cw, y + lp.topMargin + ch); x += total + dp(6); rowHeight = Math.max(rowHeight, ch + lp.topMargin + lp.bottomMargin); }
        }
    }

    private final class ManhwaSplashArt extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float pulse = 1f;

        ManhwaSplashArt(Context context) {
            super(context);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        public void setPulse(float value) {
            pulse = value;
            invalidate();
        }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float cx = getWidth() / 2f;
            float cy = getHeight() / 2f;
            canvas.save();
            canvas.scale(pulse, pulse, cx, cy);

            float glowRadius = dp(50);
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(new RadialGradient(cx, cy, glowRadius,
                    new int[]{Color.argb(90, Color.red(PURPLE), Color.green(PURPLE), Color.blue(PURPLE)),
                            Color.TRANSPARENT}, null, android.graphics.Shader.TileMode.CLAMP));
            canvas.drawCircle(cx, cy, glowRadius, paint);
            paint.setShader(null);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1.2f));
            paint.setColor(Color.argb(85, Color.red(PURPLE), Color.green(PURPLE), Color.blue(PURPLE)));
            canvas.drawCircle(cx, cy, dp(42), paint);
            android.graphics.RectF orbit = new android.graphics.RectF(cx - dp(52), cy - dp(25),
                    cx + dp(52), cy + dp(25));
            paint.setStrokeWidth(dp(1));
            canvas.drawArc(orbit, 205, 220, false, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(PURPLE);
            canvas.drawCircle(cx - dp(44), cy - dp(12), dp(2.2f), paint);
            canvas.drawCircle(cx + dp(47), cy + dp(7), dp(1.7f), paint);
            paint.setColor(0xFFDCC6FF);
            canvas.drawCircle(cx + dp(27), cy - dp(37), dp(1.5f), paint);

            Path leftPage = new Path();
            leftPage.moveTo(cx, cy - dp(19));
            leftPage.cubicTo(cx - dp(11), cy - dp(27), cx - dp(24), cy - dp(26), cx - dp(31), cy - dp(23));
            leftPage.lineTo(cx - dp(31), cy + dp(19));
            leftPage.cubicTo(cx - dp(19), cy + dp(14), cx - dp(8), cy + dp(17), cx, cy + dp(25));
            leftPage.close();
            Path rightPage = new Path();
            rightPage.moveTo(cx, cy - dp(19));
            rightPage.cubicTo(cx + dp(11), cy - dp(27), cx + dp(24), cy - dp(26), cx + dp(31), cy - dp(23));
            rightPage.lineTo(cx + dp(31), cy + dp(19));
            rightPage.cubicTo(cx + dp(19), cy + dp(14), cx + dp(8), cy + dp(17), cx, cy + dp(25));
            rightPage.close();
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(new LinearGradient(cx - dp(31), cy, cx + dp(31), cy,
                    new int[]{0xFF7B35EB, PURPLE, 0xFFD9BCFF}, null,
                    android.graphics.Shader.TileMode.CLAMP));
            canvas.drawPath(leftPage, paint);
            canvas.drawPath(rightPage, paint);
            paint.setShader(null);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1.4f));
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setColor(0xFFEDE1FF);
            canvas.drawPath(leftPage, paint);
            canvas.drawPath(rightPage, paint);
            canvas.drawLine(cx, cy - dp(18), cx, cy + dp(23), paint);
            canvas.drawLine(cx - dp(24), cy - dp(13), cx - dp(10), cy - dp(10), paint);
            canvas.drawLine(cx - dp(23), cy - dp(5), cx - dp(10), cy - dp(2), paint);
            canvas.drawLine(cx + dp(10), cy - dp(10), cx + dp(24), cy - dp(13), paint);
            canvas.drawLine(cx + dp(10), cy - dp(2), cx + dp(23), cy - dp(5), paint);
            canvas.restore();
        }
    }

}
