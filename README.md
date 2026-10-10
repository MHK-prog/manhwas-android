# manhwas-app
A minimalist, offline-first PWA to track your manhwa reading progress. Simple, fast, and easy to use.

# Manhwas

A lightweight, minimalist web application to keep track of your manhwa reading progress.

Key Features:

- Offline-First: Works without an internet connection using PWA technology.
- Simple & Fast: No unnecessary clutter, just your progress.
- Backup: Export your data as a JSON file.
- Installable: Add it to your home screen and use it like a native app.

Built with HTML, CSS, and Vanilla JavaScript. KISS (Keep It Simple, Stupid) principle applied!
# مانهواهام برای اندروید

این مخزن نسخهٔ اندرویدیِ برنامهٔ وب است. رابط کاربری اصلی در `index.html` باقی مانده و اپ Android آن را داخل WebView امن و آفلاین نمایش می‌دهد. شناسهٔ بسته برای نصب نسخهٔ جدید روی نسخهٔ قبلی حفظ شده است.

## ذخیره‌سازی

از منوی برنامه گزینهٔ «انتخاب پوشهٔ ذخیره‌سازی» را بزنید. Android فقط به همان پوشه‌ای که انتخاب می‌کنید دسترسی می‌دهد و فایل `manhwas.json` را آنجا نگه می‌دارد. جلدها در زیرپوشهٔ مخفی `.manhwas-covers` کنار آن ذخیره می‌شوند؛ فایل `.nomedia` کمک می‌کند گالری اندروید آن‌ها را فهرست نکند. این تنظیم پنهان‌سازی از گالری است، نه رمزگذاری فایل‌ها. اگر پوشه فایل داده داشته باشد، هنگام انتخاب همان داده‌ها بارگذاری می‌شوند. تا وقتی پوشه انتخاب نشده، برنامه روی حافظهٔ خصوصی خود اپ ذخیره می‌کند. گزینهٔ پشتیبان‌گیری هم یک فایل JSON با انتخاب‌گر Android می‌سازد.

فهرست مانهواها جلد و نام را در دو ستون نشان می‌دهد. با دکمهٔ `!` اطلاعات و پیشرفت را می‌بینید؛ از آنجا وارد فهرست چپترها می‌شوید و برنامه آخرین چپتر تیک‌خورده را وسط صفحه می‌آورد. چیدمان صفحه هم inset نوار وضعیت و دکمه‌های پایین اندروید را رعایت می‌کند.

## ساخت APK از GitHub

با push کردن تغییرات به شاخهٔ `main`، یا اجرای دستی workflow از زبانهٔ **Actions**، GitHub یک APK نسخهٔ release با کلید امضای ذخیره‌شده در Secretهای مخزن می‌سازد:

1. مخزن را در GitHub باز کنید و وارد **Actions** شوید.
2. workflow با نام **Build signed Android APK** را اجرا کنید.
3. پس از پایان موفق، از صفحهٔ اجرای workflow بخش **Artifacts** فایل `manhwas-release-apk` را دریافت و از ZIP استخراج کنید.

این workflow از Secretهای امضای release همین مخزن استفاده می‌کند و امضای APK را پیش از انتشار Artifact بررسی می‌کند.

## ساخت محلی

با Android SDK نصب‌شده و JDK 17 یا جدیدتر:

```powershell
.\gradlew.bat --no-daemon :app:assembleDebug
```

خروجی debug محلی در `app/build/outputs/apk/debug/app-debug.apk` قرار می‌گیرد. برای ساخت release امضاشده، Secretهای `ANDROID_KEYSTORE_BASE64`، `ANDROID_KEYSTORE_PASSWORD`، `ANDROID_KEY_ALIAS` و `ANDROID_KEY_PASSWORD` لازم‌اند.
