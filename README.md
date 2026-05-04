# derdimET mobil

Repo Kotlin-first migration surecindedir.

## Calisma modlari

- **Kotlin/Wasm (aktif yeni akıs):**
  - `./gradlew :shared:wasmJsBrowserRun`
  - Varsayilan adres: `http://localhost:8080`
- **Expo/React Native (legacy, gecis tamamlanana kadar):**
  - `npm install`
  - `npx expo start -c`

## Mevcut Kotlin kapsamı

- Auth bootstrap (`SPLASH -> LOGIN -> REGISTER -> MAIN`)
- Login/Register API akislari
- Role-based home ekranlari (Admin/Seller/Buyer)
- Profil filtresi + seller list filter davranisi

## Sonraki hedef

- Expo `app/`, `screens/`, `contexts/`, `services/` katmanini tamamen devreden cikarmak
- Native Android/iOS girislerini dogrudan Compose/KMP akisina baglamak
