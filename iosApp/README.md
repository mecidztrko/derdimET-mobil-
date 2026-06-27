# iosApp — derdimET iOS

Kotlin Multiplatform `shared` modülünü iOS'ta çalıştırmak için Xcode projesi iskeleti.

## Önkoşullar

- Xcode 15+
- CocoaPods veya doğrudan framework bağlantısı

## Framework üretimi

```bash
# Simulator + gerçek cihaz (Xcode öncesi)
./gradlew :shared:embedAndSignAppleFrameworkForXcode

# Yalnızca simulator
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

Çıktı: `shared/build/bin/iosSimulatorArm64/debugFramework/shared.framework`

## Xcode kurulumu

1. Xcode → **File → New → Project → App** (SwiftUI, `iosApp` klasörüne kaydet)
2. **General → Frameworks** altına `shared.framework` ekle (Embed & Sign)
3. `ContentView.swift` ve `derdimETApp.swift` bu repodaki dosyalarla değiştir
4. Simulator'da çalıştır

## API adresi

Simulator için backend: `http://localhost:8081` (`ContentView.swift` içinde `apiBaseUrl`).

Gerçek cihazda Mac IP adresinizi kullanın (ör. `http://192.168.1.10:8081`).
