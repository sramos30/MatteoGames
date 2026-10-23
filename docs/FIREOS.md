# MatteoGames no Fire Phone (FireOS 4.x / Android 4.4, API 19)

Branch `fireos-api19`. O app agora roda em Android 4.4+ (minSdk 19), em retrato, offline.

## Instalar

```
gradle :app:assembleDebug        (precisa de ~/.literki/assinatura.properties; sem ela o build falha)
adb -s B0BE070252130091 install -r app\build\outputs\apk\debug\app-debug.apk
adb -s B0BE070252130091 shell monkey -p com.literki.matteogames 1
```

Antes do build, baixe os emojis uma vez: `python scripts/fetch_noto.py` (PNGs Noto 128 px em `assets/noto`, fora do git).

O Literki precisa estar instalado ANTES do MatteoGames (a permissão só é concedida se o app que a define já existe). Se não estiver, o app mostra uma tela amigável com botão de tentar de novo.

## O que mudou

- **Compose → Views clássicas + Canvas** (sem AndroidX, sem dependências). `framework/KidUi.kt` tem os botões grandes, cartões e o `EmojiView`.
- **DataStore → SharedPreferences** (`core/SettingsRepository.kt`, mesmas chaves).
- **Vibração**: `vibrate(ms)` antigo abaixo da API 26, protegido por try/catch.
- **Voz** (`core/Voice.kt`): mp3 dos pacotes Literki (`rec` tem prioridade sobre `tts`), tocado com MediaPlayer via `openFileDescriptor` em `/arquivo`. O TTS do aparelho só entra como último recurso, sem bloquear; a UI destrava após 10 s.
- **Ícones**: mesma estratégia do Literki: PNGs do Noto Emoji em `assets/noto` (Apache 2.0) e, para as palavras, os SVGs dos pacotes `*.images` renderizados com AndroidSVG. Nada depende de glifo de fonte.
- **Layout**: tudo é medido em `onSizeChanged` numa prancha de 360x640 unidades (`Ui.u`), texto com auto-ajuste (`fitLabel`), sem rolagem nem corte.
- **Ícone**: PNGs em `mipmap-*` (o ícone adaptativo não existe na API 19).
- **Pacote**: `com.literki.matteogames`, assinado com a chave do Literki (igual ao `literki-app/app/build.gradle`).
- **Conteúdo do Literki** (`core/Literki.kt`): lê `/lista`, `/linguas` e `/arquivo` do provedor `com.literki.app.pacotes` com a permissão `com.literki.app.permission.LER_PACOTES` (contrato em `literki-app/docs/PROVEDOR-PACOTES.md`). Botão de língua igual ao `cycleLang`: pula para a próxima língua que tem o item, ou treme e apita; a escolha fica salva. O que falta está em [CONTEUDO-FALTANTE.md](CONTEUDO-FALTANTE.md).
- **Quebra-cabeça**: importar foto pelo seletor do sistema, com portão dos pais.
