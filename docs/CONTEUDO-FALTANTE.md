# Conteúdo faltante no Literki para o MatteoGames

O MatteoGames usa **só** o conteúdo dos pacotes do Literki (provedor `com.literki.app.pacotes`: `/lista`, `/linguas`, `/arquivo`).
Nada de texto ou voz é inventado no app.

Atualizado em 23/10/2026 (v1.0.4-fireos) com o conteúdo novo do literki-estudio (`docs/MATTEO-IDS.json`),
medido no Fire Phone com 7 línguas (pl, br, en, fr, es, it, de): **143 palavras, 28 frases e 10 dígitos, todos com som**.

## Já resolvido (o app usa)

| Conteúdo | Ids | Onde |
|---|---|---|
| Cores `c.red` `c.blue` `c.green` `c.yellow` `c.pink` `c.orange` | w134–w139 | Cores e formas (nomeia a cor) |
| Formas `s.circle` `s.square` `s.triangle` `s.star` | w140, w141, w142, w104 | Cores e formas (nomeia a forma) |
| `n.10` | w143 | Contar vai de 1 a 10 |
| `p.howmany` | | Contar (início de cada rodada) |
| `p.find` | | Ache! e Cores e formas (antes da palavra) |
| `p.win` | | Quebra-cabeça e Memória (fim) |
| `p.pop` | | Balões (início) |
| `p.game.*` | | Títulos da tela inicial (texto e voz) |

Cores/formas/10 ficam fora dos jogos de figura (Ache!, Memória, Balões, Quebra-cabeça); a estrela w104 continua também como figura.

## Ainda faltando / pendente

- **`p.same`, `p.pick`, `p.parent`** existem mas ainda não estão ligados (galeria do quebra-cabeça e portão dos pais usam texto fixo).
- **`p.loading`, `p.nolit`, `p.noperm`**: telas de erro continuam fixas em PT (sem Literki não há de onde ler).
- **Números** mostra 0–9 (não há dígito `d10`; 10 só como palavra).
- Algumas letras não têm `exemplo` (no br, o Y); o jogo de Letras mostra só a letra.
- **DECISÃO PENDENTE — 371 palavras antigas:** o vocabulário antigo do MatteoGames tinha 494 palavras; 371 não existem no Literki
  e **não dá para acrescentar**: o limite de fx do Literki é 1–254 e já estão em uso até w143. Opções: ampliar o limite no
  Literki, escolher só algumas, ou deixar como está. Até decidir, os jogos usam só as palavras do Literki. Tabela abaixo.

## Pacote `*.ui`

Os pacotes `br.ui` etc. (`ui.json`, chaves `ui.*`) são textos da interface do próprio Literki. O MatteoGames não os usa.

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