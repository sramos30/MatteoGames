# Conteúdo faltante no Literki para o MatteoGames

O MatteoGames usa **só** o conteúdo dos pacotes do Literki (provedor `com.literki.app.pacotes`: `/lista`, `/linguas`, `/arquivo`).
Nada de texto ou voz é inventado no app. Onde falta conteúdo, o jogo se adapta (abaixo). Esta lista é o que precisaria entrar
nos pacotes (`*.base`, `*.words`, `*.tts`/`*.rec`, `*.images`) para cada jogo ficar completo.

Medido no Fire Phone em 23/10/2026 com 7 línguas instaladas (pl, br, en, fr, es, it, de; 35 pacotes: base, images, tts, ui, words).
Nenhum pacote `*.rec` nem pacote de fonte estava instalado.

## 1. Áudio (o que mais falta)

| Língua | Palavras com mp3 | Letras | Dígitos 0–9 | Frases (`p.*`) | rec |
|---|---|---|---|---|---|
| pl | 16/133 | 32/32 | 10/10 | 12/13 | 0 |
| br | 16/133 | 27/27 | 10/10 | 12/13 | 0 |
| en | 16/133 | 26/26 | 10/10 | 12/13 | 0 |
| fr | 16/133 | 26/26 | 10/10 | 12/13 | 0 |
| es | 16/133 | 27/27 | 10/10 | 12/13 | 0 |
| it | 16/133 | 26/26 | 10/10 | 12/13 | 0 |
| de | 16/133 | 29/29 | 10/10 | 12/13 | 0 |

- **w017–w133 não têm mp3 em nenhuma língua.** Hoje caem no TTS do aparelho (último recurso, sem bloquear). No Fire Phone o TTS
  nem sempre tem voz para pl/br, e aí a palavra fica muda.
- Uma frase `p.*` por língua está sem mp3 (veja a linha `som:` do logcat, tag `MatteoLiterki`).
- Nenhuma gravação aprovada (`*.rec`) instalada. Quando houver, ela já ganha do `tts` automaticamente.

## 2. Frases (prompts) que os jogos gostariam de ter

Hoje o Literki tem 13 frases (`p.lang`, `p.missing`, `p.ok`, `p.try`, `p.night`, `p.mode.*`, `p.type.*`). O app usa `p.ok` (acerto),
`p.try` (erro) e `p.lang` (botão de língua). Faltam, com texto e áudio em todas as línguas:

| Chave sugerida | Exemplo (br) | Usada em |
|---|---|---|
| `p.howmany` | Quantos são? | Contar |
| `p.find` | Onde está…? / Ache… | Ache! (antes da palavra) |
| `p.same` | Ache o igual | Cores e formas |
| `p.pick` | Escolha uma figura | Quebra-cabeça (galeria) |
| `p.win` | Você conseguiu! | Quebra-cabeça, Memória |
| `p.pop` | Estoure o balão! | Balões |
| `p.parent` | Peça ajuda a um adulto | Portão dos pais |
| `p.game.*` | Quebra-cabeça, Números, Letras, Contar, Cores e formas, Memória, Ache!, Balões | Títulos da tela inicial |
| `p.loading`, `p.nolit`, `p.noperm` | Carregando… / Instale o Literki / Reinstale | Telas de erro (hoje fixas em PT) |

Os títulos da tela inicial e o portão dos pais continuam em inglês fixo (strings do app) até isso existir.

## 3. Cores e formas

Não há nomes de cor (vermelho, azul, …) nem de forma (círculo, quadrado, triângulo, estrela, coração, …) nos pacotes.
**Adaptação:** o jogo virou "ache o igual" sem nomes: mesma forma com cores diferentes ou mesma cor com formas diferentes;
acerto diz `p.ok`, erro `p.try`. Para voltar ao jogo original precisa de `c.red`… e `s.circle`… (texto + mp3) por língua.

## 4. Números

`base.json` tem `digitos` 0–9. Falta **10** (e 11–20 se quiser contar mais).
**Adaptação:** Números mostra 0–9; Contar vai de 1 a 9.

## 5. Palavras

O Literki tem 133 palavras (todas com desenho SVG em `*.images`). O vocabulário antigo do MatteoGames tinha 494; **371 não existem
no Literki** e foram removidas (Ache!, Memória, Balões, Quebra-cabeça e exemplos de Letras usam só as 133).
Tabela completa abaixo (emoji | en | pl | pt), para importar no literki-estudio se quiser.

Também: algumas letras não têm `exemplo` (no br, por exemplo, o Y); nesses casos o jogo de Letras mostra só a letra.

## 6. Pacote `*.ui`

Os pacotes `br.ui` etc. (`ui.json`, chaves `ui.*`) são textos da interface do próprio Literki. O MatteoGames não os usa;
se o Literki quiser oferecer títulos genéricos (Voltar, Fechar, Galeria), dá para ler daí no futuro.

### Palavras do MatteoGames antigo que faltam no Literki (371)

| emoji | en | pl | pt |
|---|---|---|---|
| 🐹 | hamster | chomik | hamster |
| 🐮 | cow | krowa | vaca |
| 🐵 | monkey | małpa | macaco |
| 🐔 | chicken | kurczak | galinha |
| 🦅 | eagle | orzeł | águia |
| 🐜 | ant | mrówka | formiga |
| 🕷️ | spider | pająk | aranha |
| 🦎 | lizard | jaszczurka | lagarto |
| 🦀 | crab | krab | caranguejo |
| 🐬 | dolphin | delfin | golfinho |
| 🦈 | shark | rekin | tubarão |
| 🦏 | rhino | nosorożec | rinoceronte |
| 🐪 | camel | wielbłąd | camelo |
| 🐿️ | squirrel | wiewiórka | esquilo |
| 🦜 | parrot | papuga | papagaio |
| 🦢 | swan | łabędź | cisne |
| 🐣 | chick | pisklę | pintinho |
| 👄 | mouth | usta | boca |
| 👅 | tongue | język | língua |
| 🖐️ | hand | ręka | mão |
| 🦵 | leg | noga | perna |
| 💪 | arm | ramię | braço |
| 🧠 | brain | mózg | cérebro |
| ❤️ | heart | serce | coração |
| 🍑 | peach | brzoskwinia | pêssego |
| 🥭 | mango | mango | manga |
| 🥥 | coconut | kokos | coco |
| 🌽 | corn | kukurydza | milho |
| 🥔 | potato | ziemniak | batata |
| 🍔 | hamburger | hamburger | hambúrguer |
| 🍟 | fries | frytki | batata frita |
| 🥪 | sandwich | kanapka | sanduíche |
| 🍩 | donut | pączek | rosquinha |
| 🍪 | cookie | ciastko | biscoito |
| 🎂 | cake | tort | bolo |
| 🍬 | candy | cukierek | bala |
| 🍭 | lollipop | lizak | pirulito |
| 🍿 | popcorn | popcorn | pipoca |
| 🧸 | teddy bear | miś | ursinho |
| 📚 | book | książka | livro |
| 🕰️ | clock | zegar | relógio |
| 📱 | phone | telefon | telefone |
| 📷 | camera | aparat | câmera |
| 🪁 | kite | latawiec | pipa |
| 🧢 | cap | czapka | boné |
| 👕 | shirt | koszulka | camiseta |
| 👗 | dress | sukienka | vestido |
| 🧤 | gloves | rękawiczki | luvas |
| 🧣 | scarf | szalik | cachecol |
| 🌂 | umbrella | parasol | guarda-chuva |
| 🧼 | soap | mydło | sabonete |
| 🧹 | broom | miotła | vassoura |
| 🚑 | ambulance | karetka | ambulância |
| 🚒 | fire truck | wóz strażacki | caminhão de bombeiros |
| 🚓 | police car | radiowóz | carro de polícia |
| 🚜 | tractor | traktor | trator |
| 🏍️ | motorcycle | motocykl | moto |
| 🚁 | helicopter | helikopter | helicóptero |
| 🚢 | ship | statek | navio |
| 🚚 | truck | ciężarówka | caminhão |
| ☁️ | cloud | chmura | nuvem |
| 🌧️ | rain | deszcz | chuva |
| ⛄ | snowman | bałwan | boneco de neve |
| 🌊 | wave | fala | onda |
| 🌸 | flower | kwiat | flor |
| 🌻 | sunflower | słonecznik | girassol |
| 🌵 | cactus | kaktus | cacto |
| 🍁 | leaf | liść | folha |
| 🌍 | earth | ziemia | terra |
| 🏔️ | mountain | góra | montanha |
| 🦍 | gorilla | goryl | gorila |
| 🦥 | sloth | leniwiec | preguiça |
| 🦦 | otter | wydra | lontra |
| 🦝 | raccoon | szop | guaxinim |
| 🦙 | llama | lama | lhama |
| 🦩 | flamingo | flaming | flamingo |
| 🕊️ | dove | gołąb | pomba |
| 🦚 | peacock | paw | pavão |
| 🦇 | bat | nietoperz | morcego |
| 🐛 | caterpillar | gąsienica | lagarta |
| 🦂 | scorpion | skorpion | escorpião |
| 🦕 | dinosaur | dinozaur | dinossauro |
| 🥦 | broccoli | brokuł | brócolis |
| 🥑 | avocado | awokado | abacate |
| 🥒 | cucumber | ogórek | pepino |
| 🧅 | onion | cebula | cebola |
| 🧄 | garlic | czosnek | alho |
| 🥐 | croissant | rogalik | croissant |
| 🥞 | pancakes | naleśniki | panquecas |
| 🍣 | sushi | sushi | sushi |
| 🍚 | rice | ryż | arroz |
| 🍝 | spaghetti | spaghetti | espaguete |
| 🥗 | salad | sałatka | salada |
| 🧁 | cupcake | babeczka | cupcake |
| 🌮 | taco | taco | taco |
| 🥜 | peanut | orzeszek | amendoim |
| ✂️ | scissors | nożyczki | tesoura |
| 🔨 | hammer | młotek | martelo |
| 📏 | ruler | linijka | régua |
| 🖍️ | crayon | kredka | giz de cera |
| 🎒 | backpack | plecak | mochila |
| 🔔 | bell | dzwonek | sino |
| 🕯️ | candle | świeca | vela |
| 🪣 | bucket | wiadro | balde |
| 🪜 | ladder | drabina | escada |
| 🪥 | toothbrush | szczoteczka | escova de dentes |
| 🪞 | mirror | lustro | espelho |
| 🧲 | magnet | magnes | ímã |
| ⏰ | alarm clock | budzik | despertador |
| 🎲 | dice | kostka | dado |
| 🏆 | trophy | puchar | troféu |
| 🏀 | basketball | koszykówka | basquete |
| ⚾ | baseball | baseball | beisebol |
| 🎷 | saxophone | saksofon | saxofone |
| 🎺 | trumpet | trąbka | trompete |
| 🛴 | scooter | hulajnoga | patinete |
| ⚓ | anchor | kotwica | âncora |
| 🌴 | palm tree | palma | palmeira |
| 🌲 | pine tree | sosna | pinheiro |
| 🌹 | rose | róża | rosa |
| 🍀 | clover | koniczyna | trevo |
| 🐚 | shell | muszla | concha |
| 🌋 | volcano | wulkan | vulcão |
| 🪨 | rock | kamień | pedra |
| 🪿 | goose | gęś | ganso |
| 🦫 | beaver | bóbr | castor |
| 🦡 | badger | borsuk | texugo |
| 🦨 | skunk | skunks | gambá |
| 🐂 | ox | wół | boi |
| 🐏 | ram | baran | carneiro |
| 🦣 | mammoth | mamut | mamute |
| 🫎 | moose | łoś | alce |
| 🫏 | donkey | osioł | burro |
| 🦬 | bison | żubr | bisão |
| 🦗 | cricket | świerszcz | grilo |
| 🪰 | fly | mucha | mosca |
| 🪲 | beetle | żuk | besouro |
| 🦟 | mosquito | komar | mosquito |
| 🐠 | tropical fish | rybka | peixinho |
| 🐡 | pufferfish | rozdymka | baiacu |
| 🦞 | lobster | homar | lagosta |
| 🦐 | shrimp | krewetka | camarão |
| 🦑 | squid | kałamarnica | lula |
| 👦 | boy | chłopiec | menino |
| 👮 | police | policjant | policial |
| 🤴 | prince | książę | príncipe |
| 🧙 | wizard | czarodziej | mago |
| 🧚 | fairy | wróżka | fada |
| 🧜 | mermaid | syrena | sereia |
| 🤖 | robot | robot | robô |
| 👻 | ghost | duch | fantasma |
| 👽 | alien | kosmita | alienígena |
| 🎅 | Santa | Mikołaj | Papai Noel |
| 👣 | footprints | ślady | pegadas |
| 🍆 | eggplant | bakłażan | berinjela |
| 🫑 | bell pepper | papryka | pimentão |
| 🌶️ | chili | chili | pimenta |
| 🥬 | lettuce | sałata | alface |
| 🍠 | sweet potato | batat | batata-doce |
| 🌰 | chestnut | kasztan | castanha |
| 🥖 | baguette | bagietka | baguete |
| 🥯 | bagel | bajgiel | bagel |
| 🥓 | bacon | boczek | bacon |
| 🍖 | meat | mięso | carne |
| 🍗 | drumstick | udko | coxa |
| 🌭 | hot dog | hot dog | cachorro-quente |
| 🌯 | burrito | burrito | burrito |
| 🍳 | fried egg | jajko sadzone | ovo frito |
| 🧇 | waffle | gofr | waffle |
| 🧈 | butter | masło | manteiga |
| 🧂 | salt | sól | sal |
| 🍮 | pudding | budyń | pudim |
| 🥧 | pie | placek | torta |
| 🥤 | soda | napój | refrigerante |
| 🫐 | blueberry | jagoda | mirtilo |
| 🍈 | melon | melon | melão |
| 🧊 | ice | lód | gelo |
| 🍽️ | plate | talerz | prato |
| 🍴 | fork | widelec | garfo |
| 🔪 | knife | nóż | faca |
| 🥣 | bowl | miska | tigela |
| 🪐 | planet | planeta | planeta |
| 🌠 | shooting star | spadająca gwiazda | estrela cadente |
| ☄️ | comet | kometa | cometa |
| ⚡ | lightning | błyskawica | relâmpago |
| 🌪️ | tornado | tornado | tornado |
| ⛈️ | storm | burza | tempestade |
| 🌫️ | fog | mgła | neblina |
| 🌡️ | thermometer | termometr | termômetro |
| 💨 | wind | wiatr | vento |
| 🫧 | bubbles | bąbelki | bolhas |
| 🌱 | seedling | sadzonka | muda |
| 🌾 | wheat | pszenica | trigo |
| 🌼 | daisy | stokrotka | margarida |
| 💐 | bouquet | bukiet | buquê |
| 🏖️ | beach | plaża | praia |
| 🏜️ | desert | pustynia | deserto |
| 🌉 | bridge | most | ponte |
| 🛰️ | satellite | satelita | satélite |
| 💻 | laptop | laptop | notebook |
| 🖥️ | computer | komputer | computador |
| ⌚ | watch | zegarek | relógio |
| 🔒 | lock | kłódka | cadeado |
| 💡 | light bulb | żarówka | lâmpada |
| 🔦 | flashlight | latarka | lanterna |
| 🧯 | fire extinguisher | gaśnica | extintor |
| 🪓 | axe | siekiera | machado |
| 🪚 | saw | piła | serra |
| 🪛 | screwdriver | śrubokręt | chave de fenda |
| 🔩 | bolt | śruba | parafuso |
| ⚙️ | gear | koło zębate | engrenagem |
| 🧪 | test tube | probówka | tubo de ensaio |
| 🔬 | microscope | mikroskop | microscópio |
| 🔭 | telescope | teleskop | telescópio |
| 💊 | pill | tabletka | comprimido |
| 🩹 | bandage | plaster | curativo |
| 🚽 | toilet | toaleta | vaso sanitário |
| 🚿 | shower | prysznic | chuveiro |
| 🛁 | bathtub | wanna | banheira |
| 🧽 | sponge | gąbka | esponja |
| 🛋️ | couch | kanapa | sofá |
| 🖼️ | picture | obraz | quadro |
| ⏳ | hourglass | klepsydra | ampulheta |
| 📝 | note | notatka | nota |
| 📎 | paperclip | spinacz | clipe |
| 📌 | pin | pinezka | alfinete |
| 🧵 | thread | nić | linha |
| 🧶 | yarn | włóczka | lã |
| 🎀 | bow | kokarda | laço |
| 👜 | bag | torebka | bolsa |
| 💍 | ring | pierścionek | anel |
| 👖 | jeans | dżinsy | calça |
| 🩳 | shorts | szorty | shorts |
| 👔 | tie | krawat | gravata |
| 👢 | boot | kozak | bota |
| 🕶️ | sunglasses | okulary słoneczne | óculos de sol |
| 🎾 | tennis | tenis | tênis |
| 🏐 | volleyball | siatkówka | vôlei |
| 🏓 | ping pong | ping-pong | pingue-pongue |
| 🏸 | badminton | badminton | badminton |
| 🥇 | medal | medal | medalha |
| 🎯 | target | tarcza | alvo |
| 🧩 | puzzle | puzzle | quebra-cabeça |
| 🎮 | controller | kontroler | controle |
| 🎤 | microphone | mikrofon | microfone |
| 🎧 | headphones | słuchawki | fones |
| 🎵 | music note | nuta | nota musical |
| 🪗 | accordion | akordeon | acordeão |
| 🎪 | circus | cyrk | circo |
| 🎠 | carousel | karuzela | carrossel |
| 🎡 | ferris wheel | diabelski młyn | roda-gigante |
| 🎢 | roller coaster | kolejka górska | montanha-russa |
| ⛺ | tent | namiot | barraca |
| 🏎️ | race car | wyścigówka | carro de corrida |
| 🛹 | skateboard | deskorolka | skate |
| 🛼 | roller skate | wrotki | patins |
| 🚇 | metro | metro | metrô |
| 🚊 | tram | tramwaj | bonde |
| 🛸 | UFO | ufo | óvni |
| 🛶 | canoe | kajak | canoa |
| 🚤 | speedboat | motorówka | lancha |
| 🧭 | compass | kompas | bússola |
| 🗺️ | map | mapa | mapa |
| 🚦 | traffic light | sygnalizacja | semáforo |
| 🏥 | hospital | szpital | hospital |
| 🏫 | school | szkoła | escola |
| 🐩 | poodle | pudel | poodle |
| 🐆 | leopard | lampart | leopardo |
| 🐀 | rat | szczur | ratazana |
| 🦧 | orangutan | orangutan | orangotango |
| 🪱 | worm | robak | minhoca |
| 🦪 | oyster | ostryga | ostra |
| 🪺 | nest | gniazdo | ninho |
| 🐾 | paw prints | łapki | patas |
| 🪶 | feather | pióro | pena |
| 🕸️ | spider web | pajęczyna | teia |
| 🦠 | germ | zarazek | micróbio |
| 🍏 | green apple | zielone jabłko | maçã verde |
| 🫒 | olive | oliwka | azeitona |
| 🫘 | beans | fasola | feijão |
| 🫛 | peas | groszek | ervilha |
| 🫚 | ginger | imbir | gengibre |
| 🥨 | pretzel | precel | pretzel |
| 🥩 | steak | stek | bife |
| 🍜 | noodles | makaron | macarrão |
| 🍲 | stew | gulasz | ensopado |
| 🥟 | dumpling | pierogi | pastel |
| 🍼 | baby bottle | butelka | mamadeira |
| 🫖 | teapot | czajniczek | bule |
| 🫙 | jar | słoik | pote |
| 👚 | blouse | bluzka | blusa |
| 👠 | high heels | szpilki | salto |
| 👡 | sandal | sandał | sandália |
| 🩴 | flip-flops | klapki | chinelo |
| 🩱 | swimsuit | kostium kąpielowy | maiô |
| 👛 | purse | portmonetka | carteira |
| 🧳 | suitcase | walizka | mala |
| 🧺 | basket | kosz | cesto |
| 💄 | lipstick | szminka | batom |
| ⛑️ | helmet | kask | capacete |
| 🦺 | safety vest | kamizelka | colete |
| 🪆 | nesting doll | matrioszka | matrioska |
| 💂 | guard | strażnik | guarda |
| 🕵️ | detective | detektyw | detetive |
| 👷 | builder | budowlaniec | construtor |
| 👰 | bride | panna młoda | noiva |
| 🦸 | superhero | superbohater | super-herói |
| 🥷 | ninja | ninja | ninja |
| 🧛 | vampire | wampir | vampiro |
| 🧟 | zombie | zombie | zumbi |
| 🧝 | elf | elf | elfo |
| 🧞 | genie | dżin | gênio |
| 👼 | angel | aniołek | anjo |
| 🤡 | clown | klaun | palhaço |
| 👾 | monster | potwór | monstro |
| 🪕 | banjo | banjo | banjo |
| 🪈 | flute | flet | flauta |
| 🪇 | maracas | marakasy | maracas |
| 🏉 | rugby | rugby | rúgbi |
| 🎱 | pool | bilard | bilhar |
| ⛳ | golf | golf | golfe |
| 🏒 | hockey | hokej | hóquei |
| ⛸️ | ice skate | łyżwy | patins de gelo |
| 🎿 | skis | narty | esqui |
| 🛷 | sled | sanki | trenó |
| 🏹 | bow and arrow | łuk | arco |
| 🎣 | fishing rod | wędka | vara de pescar |
| 🛝 | slide | zjeżdżalnia | escorregador |
| 🎳 | bowling | kręgle | boliche |
| 🛒 | shopping cart | wózek | carrinho |
| 🧻 | toilet paper | papier toaletowy | papel higiênico |
| 📻 | radio | radio | rádio |
| 📺 | TV | telewizor | televisão |
| 🔌 | plug | wtyczka | tomada |
| 🔋 | battery | bateria | bateria |
| 🧱 | brick | cegła | tijolo |
| 🖊️ | pen | długopis | caneta |
| 🖌️ | paintbrush | pędzel | pincel |
| 📰 | newspaper | gazeta | jornal |
| ✉️ | envelope | koperta | envelope |
| 📦 | box | pudełko | caixa |
| 🎟️ | ticket | bilet | ingresso |
| 💵 | money | pieniądze | dinheiro |
| 🪙 | coin | moneta | moeda |
| 💎 | diamond | diament | diamante |
| 🪄 | magic wand | różdżka | varinha |
| 🕹️ | joystick | joystick | joystick |
| ♟️ | chess | szachy | xadrez |
| 🗑️ | trash can | kosz na śmieci | lixeira |
| 🪝 | hook | hak | gancho |
| 🌿 | herb | zioło | erva |
| 🪴 | potted plant | roślina | planta |
| 🌺 | hibiscus | hibiskus | hibisco |
| 🪸 | coral | koral | coral |
| 🏕️ | camping | kemping | acampamento |
| 🏞️ | park | park | parque |
| 🏙️ | city | miasto | cidade |
| ⛲ | fountain | fontanna | fonte |
| 🗼 | tower | wieża | torre |
| 🏟️ | stadium | stadion | estádio |
| 🏢 | office | biuro | escritório |
| 🏭 | factory | fabryka | fábrica |
| 🏨 | hotel | hotel | hotel |
| 🏦 | bank | bank | banco |
| 🏪 | shop | sklep | loja |
| 🛻 | pickup truck | pikap | caminhonete |
| 🛵 | moped | motorower | lambreta |
| 🚄 | bullet train | szybki pociąg | trem-bala |
| ⛴️ | ferry | prom | balsa |
| 🚠 | cable car | kolejka linowa | teleférico |
| 🛞 | wheel | koło | roda |