# امضای ثابت APK

برای به‌روزرسانی نصب موجود، APKها باید با شناسهٔ `com.mhkprog.manhwareader` و همان کلید امضا ساخته شوند. کلید JKS را امن نگه دار و آن را داخل GitHub یا مخزن پروژه قرار نده.

## ساخت کلید در ویندوز

در PowerShell این فرمان را اجرا کن:

```powershell
$keytool = (Get-Command keytool.exe -ErrorAction Stop).Source
$keyDir = Join-Path $env:USERPROFILE '.manhwa-reader-signing'
$keyPath = Join-Path $keyDir 'manhwa-reader-release.jks'
New-Item -ItemType Directory -Path $keyDir -Force | Out-Null
& $keytool -genkeypair -v -keystore $keyPath -alias manhwas -keyalg RSA -keysize 4096 -validity 10000 -dname 'CN=Manhwa Reader'
if ($LASTEXITCODE -ne 0) { throw 'ساخت کلید ناموفق بود.' }
[Convert]::ToBase64String([IO.File]::ReadAllBytes($keyPath)) | Set-Clipboard
Write-Host 'مقدار Base64 در Clipboard کپی شد. فایل JKS و رمزهایش را امن نگه دار.'
```

رمز مخزن را انتخاب و نگه‌داری کن. برای رمز کلید هم همان رمز را وارد کن. Alias این کلید `manhwas` است. اگر کلید را قبلاً ساخته‌ای و Secretهایش را ثبت کرده‌ای، همان را نگه دار.

## ثبت Secretها در GitHub

در مخزن برو به **Settings → Secrets and variables → Actions** و این Secretها را بساز:

| Secret | مقدار |
| --- | --- |
| `ANDROID_KEYSTORE_BASE64` | مقدار Base64 کپی‌شده |
| `ANDROID_KEYSTORE_PASSWORD` | رمز JKS |
| `ANDROID_KEY_ALIAS` | `manhwas` |
| `ANDROID_KEY_PASSWORD` | رمز کلید |

GitHub Actions از همین کلید برای ساخت نسخه‌های بعدی استفاده می‌کند. اگر APK قبلی با کلید دیگری امضا شده باشد، اندروید به‌روزرسانی را نمی‌پذیرد.
