# derdimET mobil (Kotlin)

Expo / React Native katmani kaldirildi. Uygulama **Kotlin** ile calisir:

- **Android:** `composeApp` modulu (`:composeApp`)
- **Ortak UI + is mantigi:** `shared` (Compose Multiplatform)
- **Web (Wasm):** `shared` wasmJs hedefi

## Gereksinimler

- JDK 17+ (Gradle daemini calistirmak icin; **Java 25**, Gradle 8.x ile uyumsuz)
- Android Studio + Android SDK (Android icin)
- `local.properties` icinde `sdk.dir` (ornek: `local.properties.example`)

**Java 25 uyari:** Gradle 8.14, JVM olarak Java 25 kullanamaz (`Unsupported class file major version 69`).
Android Studio → **Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK**: **17** veya **21** (embedded **jbr-17** / **jbr-21**) sec.  
Projede `.idea/gradle.xml` Gradle JVM icin `jbr-17` onculugunde ayarlandi; sende listede yoksa manuel sec.

## Android calistirma

```bash
./gradlew :composeApp:installDebug
```

Debug API tabani: `http://10.0.2.2:8080` (emulatorden makinedeki Spring).  
Release: `https://api.derdimet.com` (`composeApp/build.gradle.kts` icinde `buildConfigField`).

`derdimET` backend varsayilanlari:
- `server.port=8080`
- `server.address=0.0.0.0`

Gerekirse `local.properties` ile mobil URL override edebilirsin:

```properties
MOBILE_DEBUG_API_BASE_URL=http://10.0.2.2:8080
MOBILE_RELEASE_API_BASE_URL=https://api.derdimet.com
```

Gercek cihazda debug icin `10.0.2.2` yerine bilgisayarinin LAN IP'sini kullan.

## Web (Wasm) calistirma

```bash
./gradlew :shared:wasmJsBrowserRun
```

Varsayilan adres: `http://localhost:8080`

## Proje yapisi

| Modul | Aciklama |
|-------|----------|
| `shared` | `AppRoot`, login/register, admin/seller/buyer ekranlari, API |
| `composeApp` | Android `MainActivity`, `DerdimAndroidApp` girisi |

## Not

Eski Expo dosyalari (`app/`, `screens/`, `contexts/`, `services/`, vb.) silindi. Geri donus yoksa git gecmisinden alin.

## Android Studio: `composeApp` modulu gorunmuyorsa

Proje bazen tek bir `JAVA_MODULE` olarak acilir; Gradle alt modulleri (`composeApp`) Run Configuration listesine dusmez.

1. `local.properties` icinde `sdk.dir` ayarli olsun.
2. Android Studio’da **File → Close Project**, sonra klasoru tekrar **Open** et (kokte `settings.gradle.kts` olan dizin).
3. **File → Sync Project with Gradle Files** (veya Gradle fil ikonu).
4. **Run → Edit Configurations → + → Android App** → **Module** alaninda `composeApp` veya `derdimET-mobil.composeApp` benzeri secenek gelmeli.

Hata devam ederse `.idea` klasorunu yedekleyip silip projeyi yeniden ac (IDE Gradle modullerini yeniden uretir).
