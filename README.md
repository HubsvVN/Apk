# DFX – Delta Force floating menu (no-root)

Android overlay menu + native stub + basic anti-detect.

## Build

1. Open folder in **Android Studio** (Hedgehog / Iguana+).
2. Let Gradle sync.
3. Build → Build Bundle(s) / APK(s) → Build APK(s).
4. Or CLI:
   ```bash
   ./gradlew assembleRelease
   ```
   APK: `app/build/outputs/apk/release/app-release-unsigned.apk`

## First run

1. Install APK.
2. Open app → grant **Display over other apps**.
3. Press **Enable Overlay** → floating menu appears.
4. Open Delta Force, use switches.

## Anti-detect included

- App label: "System Service" (not obvious)
- `excludeFromRecents`
- ProGuard minify + repackage
- Basic debugger / emulator check
- Native `ptrace` self-check
- Hidden visibility + no RTTI on native
- Foreground service with minimal notification

## Reality check (no-root)

| Feature            | Status                          |
|--------------------|---------------------------------|
| Floating menu      | Works                           |
| Memory read/write  | Needs same-UID (Virtual Space)  |
| Real ESP draw      | Needs memory + surface          |
| ACE / Tencent AC   | External overlay only = lower risk than inject |

For actual ESP you must run this APK **inside a Virtual Space / Dual App** that shares UID with the game, then implement `process_vm_readv` on the game pid.

## Customize package

Change `applicationId` and `namespace` in `app/build.gradle.kts` + package folders if you want a different signature surface.
