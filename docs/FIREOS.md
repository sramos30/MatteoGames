# MatteoGames no Fire Phone (FireOS 4.x / Android 4.4, API 19)

Branch `fireos-api19`. O app agora roda em Android 4.4+ (minSdk 19), em retrato, offline.

## Instalar

```
gradle :app:assembleDebug        (precisa de ~/.literki/assinatura.properties; sem ela o build falha)
adb -s B0BE070252130091 install -r app\build\outputs\apk\debug\app-debug.apk
adb -s B0BE070252130091 shell monkey -p com.literki.matteogames 1
```

Antes do build, baixe os emojis uma vez: `python scripts/fetch_twemoji.py`.

## O que mudou

- **Compose → Views clássicas + Canvas** (sem AndroidX, sem dependências). `framework/KidUi.kt` tem os botões grandes, cartões e o `EmojiView`.
- **DataStore → SharedPreferences** (`core/SettingsRepository.kt`, mesmas chaves).
- **Vibração**: `vibrate(ms)` antigo abaixo da API 26, protegido por try/catch.
- **TTS**: `speak(String, int, HashMap)` da API 19; se o motor não responder, a UI destrava após 10 s.
- **Emojis**: todos desenhados com PNGs do Twemoji em `assets/twemoji` (o Android 4.4 não tem emoji colorido). Twemoji © Twitter/X, licença CC-BY 4.0.
- **Ícone**: PNGs em `mipmap-*` (o ícone adaptativo não existe na API 19).
- **Pacote**: `com.literki.matteogames`, assinado com a chave do Literki (igual ao `literki-app/app/build.gradle`).
- **Permissão** `com.literki.app.permission.LER_PACOTES` declarada, para ler depois os pacotes de língua do Literki (provedor `com.literki.app.pacotes`; contrato em `literki-app/docs/PROVEDOR-PACOTES.md`). Ainda não é usada; a voz continua sendo o TTS do aparelho.
- **Quebra-cabeça**: importar foto pelo seletor do sistema, com portão dos pais.
