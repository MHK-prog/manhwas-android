# مانهواها برای اندروید

نسخهٔ بومی اندرویدِ برنامهٔ ثبت پیشرفت مانهوا. رابط با Kotlin و Viewهای خود اندروید نوشته شده و برای اجرا به WebView یا اتصال اینترنت نیاز ندارد.

## امکانات

- ثبت عنوان با تعداد فصل مشخص یا نامشخص
- علامت‌گذاری جداگانهٔ فصل‌ها و حفظ دقیق فصل‌های خوانده‌شده هنگام ویرایش
- وضعیت‌های «در حال خواندن»، «متوقف» و «تمام‌شده»
- جست‌وجو، فیلتر وضعیت و مرتب‌سازی بر اساس آخرین تغییر یا عنوان
- رفتن به فصل بعدی و نمایش مرحله‌ای فهرست فصل‌های عنوان‌های ناتمام
- نمایش درصد پیشرفت وقتی تعداد کل فصل‌ها مشخص باشد
- ویرایش و حذف عنوان‌ها
- ذخیرهٔ خودکار در فایل `manhwas.json` داخل پوشه‌ای که کاربر با انتخاب‌گر خود اندروید برمی‌گزیند
- خروجی‌گرفتن از بکاپ JSON

در اجرای اول، برنامه انتخاب پوشه را باز می‌کند. مجوز پوشه برای استفاده‌های بعدی نگه داشته می‌شود. اگر پوشه جابه‌جا یا حذف شود، باید پوشه را دوباره انتخاب کرد. فایل داده بیرون از فضای خصوصی برنامه می‌ماند و با حذف برنامه پاک نمی‌شود.

## ساخت APK محلی

Android Studio یا Android SDK، JDK 17 و بسته‌های Android SDK Platform 36 و Build Tools 36.0.0 لازم‌اند. در PowerShell از همین پوشه اجرا کن:

    .\gradlew.bat assembleDebug

فایل آزمایشی اینجا ساخته می‌شود:

    app\build\outputs\apk\debug\app-debug.apk

اگر اجرای wrapper در مسیری با نویسه‌های غیرلاتین خطای پیدا نکردن `gradle-wrapper.jar` داد، پروژه را به مسیری با حروف لاتین منتقل کن و دوباره اجرا کن.

نسخهٔ debug با کلید آزمایشی Android امضا می‌شود و برای نصب شخصی مناسب است. برای انتشار یا به‌روزرسانی پایدار، از کلید release خودت استفاده کن.

## ساخت APK امضاشده با GitHub Actions

Workflow فایل `.github/workflows/build-apk.yml` فقط با اجرای دستی شروع می‌شود و نسخهٔ release را با کلید ذخیره‌شده در GitHub Secrets امضا می‌کند.

1. محتوای این پوشه را در ریشهٔ مخزن GitHub خودت قرار بده.
2. یک کلید release بساز و نسخهٔ امن آن را نگه دار. در PowerShell:

       keytool -genkeypair -v -keystore manhwas-release.jks -storetype JKS -alias manhwas -keyalg RSA -keysize 4096 -validity 10000

3. مقدار Base64 فایل کلید را بساز و به‌عنوان secret مخزن با نام `ANDROID_KEYSTORE_BASE64` ثبت کن:

       [Convert]::ToBase64String([IO.File]::ReadAllBytes(".\manhwas-release.jks")) | Set-Clipboard

4. این سه secret را هم در **Settings → Secrets and variables → Actions** اضافه کن:
   - `ANDROID_KEYSTORE_PASSWORD`: گذرواژهٔ مخزن کلید
   - `ANDROID_KEY_ALIAS`: مقدار `manhwas`
   - `ANDROID_KEY_PASSWORD`: گذرواژهٔ کلید
5. از **Actions → Build signed Android APK → Run workflow** اجرا کن. پس از پایان، artifact با نام `manhwas-release-apk` را دانلود کن.

کلید release برای به‌روزرسانی‌های بعدی باید همان کلید بماند. فایل `.jks` را در مخزن یا داخل پروژه قرار نده. اگر کلید گم شود، نسخهٔ امضاشدهٔ جدید به‌عنوان به‌روزرسانی همان برنامه نصب نمی‌شود.

## ساختار

    app/src/main/java/com/manhwatracker/app/
      MainActivity.kt       رابط و گردش کار برنامه
      Manhwa.kt             مدل داده و تبدیل JSON
      ManhwaStorage.kt      ذخیره در پوشهٔ انتخابی و پشتیبان‌گیری
    app/src/main/res/        نام برنامه، تم و آیکن
    .github/workflows/       ساخت APK امضاشده در GitHub Actions

شناسهٔ برنامه `com.manhwatracker.app` است. اگر بعداً آن را تغییر بدهی، اندروید آن را برنامه‌ای جداگانه می‌شناسد.
