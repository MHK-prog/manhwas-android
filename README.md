# مانهواخوان

اپلیکیشن بومی اندروید برای نگهداری فهرست، علامت‌زدن فصل‌های دیده‌شده و دریافت پیشنهاد بر اساس ژانرها و علایق.

## فهرست فصل‌ها

برای دیدن یا برداشتن تیک، روی چیپ فصل بزن. تعداد فصل‌های دیده‌شده بالای فهرست نمایش داده می‌شود و می‌توان فصل‌ها را از دکمهٔ افزودن به فهرست اضافه کرد.

در اجرای نخست، یک پوشه در انتخاب‌گر Android برگزین تا برنامه پوشهٔ `ManhwaReader` و داده‌هایش را بسازد. جلدها در پوشهٔ مخفی `.covers` نگهداری می‌شوند و فایل `.nomedia` مانع نمایش آن‌ها در گالری می‌شود.

## پیشنهادها و اطلاعات

پیشنهادها با توجه به ژانرها، سازندگان، امتیازها و علاقه‌مندی‌ها ساخته می‌شوند. اطلاعات را می‌توان با چسباندن متن JSON وارد کرد؛ فایل نمونهٔ دستور برای آماده‌کردن این اطلاعات در `app/src/main/assets/manhwa_info_prompt.txt` قرار دارد.

## ساخت APK

برای ساخت در PowerShell، JDK 17 و Android SDK Platform 36 لازم است:

```powershell
.\gradlew.bat :app:copyNamedReleaseApk
```

فایل خروجی با نامی مانند `Manhwa-Reader-Android-v1.4.28.apk` در `app/build/outputs/apk/manhwa-reader/` ساخته می‌شود. در مخزن `MHK-prog/manhwas-android`، workflow ساخت نسخهٔ امضاشده را با همین نام به‌عنوان Artifact منتشر می‌کند. برای امضا، secretهای `ANDROID_KEYSTORE_BASE64`، `ANDROID_KEYSTORE_PASSWORD`، `ANDROID_KEY_ALIAS` و `ANDROID_KEY_PASSWORD` باید در تنظیمات مخزن موجود باشند. راهنمای کلید در [ANDROID_UPDATE_SIGNING.md](ANDROID_UPDATE_SIGNING.md) است.

شناسهٔ بسته `com.mhkprog.manhwareader` و کمینهٔ نسخهٔ اندروید 6.0 (API 23) است.
