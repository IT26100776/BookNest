/*!
 * Folio — Free Bookstore Website Template by uiCookies
 * https://uicookies.com/
 * Vanilla JavaScript, no jQuery. Bootstrap 5 bundle is used only for the
 * navbar collapse, the basket offcanvas, the filter offcanvas and the modal.
 */
(function () {
  'use strict';

  /* ==========================================================================
     1. SHOP DATA
     Edit the catalogue here. Every book cover on the site is generated from
     the `cover` settings below, so a new book needs no image file:
       l   layout   arch · sun · line · dots · waves · bigtype · grid · frame ·
                    rays · split · orbit · tile · stripes · signal · label ·
                    band · gingham · graph · blob · peaks · awning · rainbow
       bg  background colour     fg  text colour     ac / a2 / a3  accents
       f   title type            serif · black · italic · sans · wide
     ========================================================================== */
  /* @data-start */
  const SHOP = {
    currency: 'Rs.',
    freeDelivery: 5000,
    storageKey: 'folio-basket'
  };

  const FORMATS = { hb: 'Hardback', pb: 'Paperback', eb: 'Ebook' };

  const GENRES = [
    { id: 'fiction', name: 'Literary Fiction', kicker: 'A Novel' },
    { id: 'crime', name: 'Crime & Mystery', kicker: 'A Mystery' },
    { id: 'sff', name: 'Sci-Fi & Fantasy', kicker: 'A Novel' },
    { id: 'history', name: 'History & Biography', kicker: '' },
    { id: 'poetry', name: 'Poetry', kicker: 'Poems' },
    { id: 'nature', name: 'Nature & Travel', kicker: '' },
    { id: 'food', name: 'Food & Drink', kicker: 'Recipes' },
    { id: 'kids', name: 'Children’s', kicker: '' }
  ];

  const STAFF = {
    Hattie: { name: 'Hattie Brennan', role: 'Shop manager, fiction' },
    Jonah: { name: 'Jonah Reyes', role: 'Crime & sci-fi shelves' },
    Mags: { name: 'Mags Doyle', role: 'Children’s buyer' },
    Theo: { name: 'Theo Adeyemi', role: 'Poetry & nature' },
    Ines: { name: 'Ines Kowalczyk', role: 'History & food' }
  };

  const BOOKS = [
    {
      id: 'the-lantern-keepers', title: 'The Lantern Keepers', author: 'Ada Merriweather', genre: 'fiction',
      price: { hb: 27, pb: 17, eb: 11.99 }, stock: true, isNew: true, signed: true,
      rating: 4.7, reviews: 64, pages: 384, publisher: 'Lamplight Press', published: '2026-09-04', isbn: '978-1-555-10000-1',
      blurb: 'Three sisters inherit a failing lighthouse and forty years of their father’s logbooks.',
      synopsis: 'When their estranged father dies, Nell, Agnes and Roo Calloway inherit the Skerry Point light, a debt they cannot pay and forty years of handwritten logbooks. As a winter of storms closes in, the sisters read the entries aloud each night and begin to piece together the one rescue their father never wrote down. Warm, sharp and full of weather, it is a novel about the things families keep burning for each other.',
      cover: { l: 'arch', bg: '#1f3a4d', fg: '#f4ead5', ac: '#e3aa3f', f: 'serif' }
    },
    {
      id: 'nine-days-in-ferrow', title: 'Nine Days in Ferrow', author: 'Sadie Quill', genre: 'crime',
      price: { hb: 24, pb: 14.99, eb: 8.99 }, stock: true,
      rating: 4.8, reviews: 112, pages: 352, publisher: 'Harrow & Finch', published: '2026-03-12', isbn: '978-1-555-11393-3',
      pick: { by: 'Jonah', note: 'Do <u>not</u> read the last page first. Day seven made me put it down and walk a full lap of the shop.' },
      blurb: 'The island ferry stops running, the guest house fills up and someone starts counting down.',
      synopsis: 'When a storm cuts Ferrow off from the mainland, nine guests are stranded at the Gull’s Rest guest house, and on the first morning each finds a card under their door with a number on it. Detective Sergeant Maggie Rourke is on holiday, off duty and the only police officer on the island. A twisting locked-island mystery with a final day you will not see coming.',
      cover: { l: 'bigtype', bg: '#1c1a19', fg: '#f3ead8', ac: '#d7372f', f: 'black' }
    },
    {
      id: 'orbit-of-small-things', title: 'Orbit of Small Things', author: 'Kenji Harlowe', genre: 'sff',
      price: { hb: 27, pb: 17.99, eb: 11.99 }, stock: true, isNew: true,
      rating: 4.6, reviews: 48, pages: 336, publisher: 'Northgate Books', published: '2026-09-11', isbn: '978-1-555-12651-3',
      pick: { by: 'Jonah', note: 'Science fiction for people who swear they don’t like science fiction. I have converted four customers this month.' },
      blurb: 'The last engineer on a failing space station finds a garden growing where nothing should.',
      synopsis: 'Hana Ito is meant to spend her final six months aboard Meridian Station shutting it down, module by module, before it falls into the sea. Instead, in a sealed laboratory, she finds tomatoes growing, and a note in her own handwriting that she does not remember writing. Quiet, strange and full of heart: science fiction about loneliness, repair and the things worth keeping alive.',
      cover: { l: 'orbit', bg: '#141a33', fg: '#efe8ff', ac: '#f2c14e', a2: '#9aa7d9', f: 'wide' }
    },
    {
      id: 'the-salt-orchard', title: 'The Salt Orchard', author: 'Iris Vantongeren', genre: 'fiction',
      price: { hb: 26, pb: 16.99, eb: 10.99 }, stock: true, isNew: true,
      rating: 4.6, reviews: 41, pages: 312, publisher: 'Harrow & Finch', published: '2026-08-21', isbn: '978-1-555-10270-8',
      pick: { by: 'Hattie', note: 'I missed my bus stop twice reading this. The orchard chapters smell of rain. For anyone who loves slow, sad, beautiful things.' },
      blurb: 'A drought uncovers a drowned village, and an orchard that is somehow still bearing fruit.',
      synopsis: 'Forty years after the valley was flooded for a reservoir, a long drought draws the water back and the old village of Wenlow rises out of the mud, orchard and all. Marta Deane returns to the place her mother never spoke of and finds the trees still heavy with apples. A patient, luminous novel about memory, inheritance and what the land keeps when people forget.',
      cover: { l: 'sun', bg: '#eadcc2', fg: '#2a211c', ac: '#c4553a', f: 'italic' }
    },
    {
      id: 'pip-and-the-paper-moon', title: 'Pip and the Paper Moon', author: 'Hazel Ottaway', genre: 'kids',
      price: { hb: 14.99, pb: 7.99 }, stock: true, isNew: true, signed: true,
      rating: 4.9, reviews: 37, pages: 32, publisher: 'Bramble Books', published: '2026-09-18', isbn: '978-1-555-16914-5',
      blurb: 'Pip folds a moon out of paper to light the way home for a lost little owl.',
      synopsis: 'When a little owl loses its way on a cloudy night, Pip gets out her paper, her scissors and her very best folding to make a moon of her own. A glowing bedtime picture book about kindness, making things and finding your way home. Ages 3 to 6.',
      cover: { l: 'blob', bg: '#9cc3d5', fg: '#1d2d4f', ac: '#f3d98b', a2: '#e8b4a6', a3: '#fff6e0', f: 'black' }
    },
    {
      id: 'the-hedgerow-year', title: 'The Hedgerow Year', author: 'Tobias Greenhalgh', genre: 'nature',
      price: { hb: 24, pb: 15.99, eb: 9.99 }, stock: true,
      rating: 4.7, reviews: 29, pages: 288, publisher: 'Thistle House', published: '2026-04-02', isbn: '978-1-555-15344-1',
      pick: { by: 'Theo', note: 'One hedge, twelve months, and now I stop at every bramble on my walk to work. Quietly life-changing.' },
      blurb: 'Twelve months watching a single hedge, and the thousand lives that pass through it.',
      synopsis: 'For one year, naturalist Tobias Greenhalgh visited the same forty yards of hawthorn hedge every morning. He found wrens and dormice, forty kinds of beetle, a family of stoats and a history older than the lane beside it. A modest, wonderful book that will change how you walk down any country road.',
      cover: { l: 'blob', bg: '#e7dfc6', fg: '#2f3a28', ac: '#5d6e52', a2: '#8fa183', a3: '#c4553a', f: 'serif' }
    },
    {
      id: 'the-glasshouse-murders', title: 'The Glasshouse Murders', author: 'Rupert Lomax', genre: 'crime',
      price: { hb: 25, pb: 15.99, eb: 9.99 }, stock: true, isNew: true,
      rating: 4.4, reviews: 33, pages: 368, publisher: 'Quire Editions', published: '2026-09-02', isbn: '978-1-555-11636-1',
      blurb: 'A botanist is found dead in a locked Victorian glasshouse, and every orchid is a witness.',
      synopsis: 'Dr Elsie Tamworth is found dead among her orchids in the great glasshouse at Pellingham Hall, the doors locked from the inside and the heating turned up to tropical. Inspector Cyril Obi knows nothing about plants and even less about the family who fund the gardens. A clever, witty country-house mystery with a fiendish botanical solution.',
      cover: { l: 'grid', bg: '#1f4e5a', fg: '#f6efe1', ac: '#cfe3d4', f: 'sans' }
    },
    {
      id: 'weather-for-beginners', title: 'Weather for Beginners', author: 'Saoirse Blunt', genre: 'poetry',
      price: { pb: 12.99, eb: 7.99 }, stock: true, isNew: true,
      rating: 4.5, reviews: 18, pages: 96, publisher: 'Low Tide Press', published: '2026-08-28', isbn: '978-1-555-14554-5',
      blurb: 'Poems about forecasts, grandmothers and learning to read a sky.',
      synopsis: 'In her second collection, Saoirse Blunt writes about the shipping forecast on a kitchen radio, a grandmother who could smell rain two days off, and the slow business of learning what the sky is trying to say. Warm, precise and often very funny, these are poems to read aloud on a wet afternoon.',
      cover: { l: 'line', bg: '#9cc3d5', fg: '#1d2d4f', ac: '#1d2d4f', f: 'italic' }
    },
    {
      id: 'the-printers-apprentice', title: 'The Printer’s Apprentice', author: 'Beatrix Hollin', genre: 'history',
      price: { hb: 30, pb: 18.99, eb: 12.99 }, stock: true,
      rating: 4.5, reviews: 22, pages: 448, publisher: 'Quire Editions', published: '2026-02-19', isbn: '978-1-555-13696-3', kicker: 'A History',
      blurb: 'The ledgers of a small-town print shop reveal the apprentices, gossips and radicals who kept it running.',
      synopsis: 'When historian Beatrix Hollin found a box of ledgers under the floor of a former print shop, she found the working lives of everyone who passed through its doors: apprentices, pamphleteers, debtors and one very determined widow. Built from receipts, gossip and ink-stained accounts, this is a vivid history of how ordinary people made, and printed, the news.',
      cover: { l: 'label', bg: '#7b1e2c', fg: '#3a1017', ac: '#efe3c8', f: 'serif' }
    },
    {
      id: 'pickled-potted-preserved', title: 'Pickled, Potted, Preserved', author: 'Agnes Mulroney', genre: 'food',
      price: { hb: 26, eb: 13.99 }, stock: true,
      rating: 4.8, reviews: 26, pages: 272, publisher: 'Thistle House', published: '2026-06-11', isbn: '978-1-555-16367-9',
      pick: { by: 'Ines', note: 'The damson cheese on page 88 is worth the price alone. My kitchen has smelled of vinegar since August.' },
      blurb: 'Jams, pickles, ferments and cordials for every glut, from a cook who has never wasted a plum.',
      synopsis: 'Agnes Mulroney has been bottling, pickling and fermenting for fifty years, and this is everything she knows: ninety recipes for jams, chutneys, cordials, krauts and fruit cheeses, with clear advice on jars, sugar and safe storage. Practical, opinionated and very good company.',
      cover: { l: 'awning', bg: '#f4ead5', fg: '#3b1d14', ac: '#d9604a', f: 'black' }
    },
    {
      id: 'small-hours-at-the-blue-cafe', title: 'Small Hours at the Blue Café', author: 'Noor Castellane', genre: 'fiction',
      price: { hb: 25, pb: 16, eb: 10.99 }, stock: true, isNew: true,
      rating: 4.3, reviews: 27, pages: 320, publisher: 'Small Hours Press', published: '2026-09-15', isbn: '978-1-555-10852-6',
      blurb: 'Six strangers, one all-night café and the long winter they spend keeping it open.',
      synopsis: 'The Blue Café has served night-shift nurses, taxi drivers and insomniacs for thirty years, and on the first of December its owner hands the keys to whoever happens to be sitting at the counter. Six strangers decide to keep the lights on until spring. Told in overlapping voices across one winter of three-in-the-morning coffee, it is a love letter to the people who keep a city running while it sleeps.',
      cover: { l: 'dots', bg: '#243b6b', fg: '#f6efe1', ac: '#e8b4a6', f: 'sans' }
    },
    {
      id: 'moths-of-the-copper-moon', title: 'Moths of the Copper Moon', author: 'Priya Ellingsworth', genre: 'sff',
      price: { hb: 28, pb: 17.99, eb: 12.99 }, stock: true,
      rating: 4.6, reviews: 58, pages: 464, publisher: 'Northgate Books', published: '2026-01-22', isbn: '978-1-555-12964-4',
      blurb: 'In a city lit only by moths, a lamplighter’s apprentice notices the moon is going out.',
      synopsis: 'In the canal city of Lumen, light comes from moths kept in copper lanterns, and the moths come from the moon. When the moon begins to dim, a lamplighter’s apprentice named Tamsin Reed is the only one who seems to notice, and the only one small enough to climb the old moon ladder. A lush, inventive fantasy that reads like a fairy tale for grown-ups.',
      cover: { l: 'tile', bg: '#b8741a', fg: '#2a1d14', ac: '#8f5512', a2: '#f4ead5', f: 'serif' }
    },
    {
      id: 'the-badger-who-borrowed-books', title: 'The Badger Who Borrowed Books', author: 'Milo Pennington', genre: 'kids',
      price: { hb: 13.99, pb: 7.99 }, stock: true,
      rating: 4.9, reviews: 44, pages: 32, publisher: 'Bramble Books', published: '2026-05-07', isbn: '978-1-555-17160-5',
      pick: { by: 'Mags', note: 'Read it at Saturday storytime and forty small people went completely silent. An instant bedtime classic.' },
      blurb: 'Badger borrows every book in the woodland library, and then has to give them all back.',
      synopsis: 'Badger loves the woodland library so much that he borrows every single book, and soon nobody else in the wood has anything to read. A funny, warm picture book about sharing, stories and late fees, with a very patient hedgehog librarian. Ages 3 to 7.',
      cover: { l: 'arch', bg: '#f3d98b', fg: '#2f4a3a', ac: '#2f4a3a', f: 'black' }
    },
    {
      id: 'the-last-train-to-calder', title: 'The Last Train to Calder', author: 'Victor Almsgrove', genre: 'crime',
      price: { hb: 26, pb: 16.99, eb: 10.99 }, stock: true,
      rating: 4.2, reviews: 39, pages: 304, publisher: 'Harrow & Finch', published: '2025-11-13', isbn: '978-1-555-12164-8',
      blurb: 'The night train from the capital arrives in Calder one passenger short.',
      synopsis: 'Eleven passengers boarded the 23:40 sleeper to Calder and ten stepped off at dawn, but the conductor swears no one left the train. Retired railway detective Ivo Marchetti is travelling in the next carriage, and he has until the return journey to find out who is lying. A classic, clockwork mystery told to the rattle of the rails.',
      cover: { l: 'rays', bg: '#c1623f', fg: '#231a14', ac: '#f3d98b', f: 'sans' }
    },
    {
      id: 'moss-stone-and-starlight', title: 'Moss, Stone & Starlight', author: 'Leif Andersen-Hart', genre: 'nature',
      price: { hb: 26, pb: 16.99, eb: 10.99 }, stock: true, isNew: true,
      rating: 4.7, reviews: 21, pages: 256, publisher: 'Thistle House', published: '2026-09-09', isbn: '978-1-555-15876-7',
      blurb: 'A year of nights spent outdoors in the high forests, learning the dark by heart.',
      synopsis: 'Leif Andersen-Hart spent a year sleeping out one night a week in the high forests above his village, in every season and every weather. He writes about owls and frost, the slow life of moss and the vanishing gift of a truly dark sky. A contemplative book for anyone who has ever looked up and felt small in a good way.',
      cover: { l: 'peaks', bg: '#141a33', fg: '#f2e9d0', ac: '#3d5a45', a2: '#5d7a5a', f: 'serif' }
    },
    {
      id: 'tidal-hymns', title: 'Tidal Hymns', author: 'Amadou Kessler', genre: 'poetry',
      price: { hb: 20, pb: 13.99 }, stock: true, isNew: true, signed: true,
      rating: 4.6, reviews: 15, pages: 112, publisher: 'Low Tide Press', published: '2026-09-25', isbn: '978-1-555-14771-6',
      blurb: 'A debut collection of sea psalms, harbour songs and the long quiet between tides.',
      synopsis: 'Written over six years in a harbour town, Amadou Kessler’s debut moves between hymn and work song: fishermen’s prayers, lists of the names of boats, the sound of shingle at night. A collection full of salt and music that announces a distinctive new voice.',
      cover: { l: 'waves', bg: '#e9dcc3', fg: '#1f4e5a', ac: '#1f4e5a', f: 'serif' }
    },
    {
      id: 'what-the-river-kept', title: 'What the River Kept', author: 'Elspeth Maro', genre: 'fiction',
      price: { hb: 24, pb: 16.99, eb: 10.99 }, stock: true,
      rating: 4.5, reviews: 52, pages: 344, publisher: 'Lamplight Press', published: '2026-05-21', isbn: '978-1-555-11067-3',
      blurb: 'A flood, a missing boat and two brothers who remember that night very differently.',
      synopsis: 'In the spring of the great flood, the Hale brothers took their father’s boat out to rescue a neighbour, and only one of them came home with a story he could tell. Thirty years later the river gives the boat back. A tense, tender family novel about guilt, loyalty and the versions of the past we agree to live with.',
      cover: { l: 'waves', bg: '#2f4a3a', fg: '#f1e6cf', ac: '#9cc3d5', f: 'italic' }
    },
    {
      id: 'salt-sail-and-empire', title: 'Salt, Sail and Empire', author: 'Callum Rowe', genre: 'history',
      price: { hb: 32, pb: 19.99, eb: 13.99 }, stock: true,
      rating: 4.3, reviews: 17, pages: 512, publisher: 'Northgate Books', published: '2025-10-30', isbn: '978-1-555-13968-1',
      blurb: 'How salt, cod and small wooden boats built fortunes and ruined towns along a thousand miles of coast.',
      synopsis: 'Long before steam, the coastal trade in salt and fish made harbour towns rich, then left many of them stranded. Callum Rowe follows the boats, the merchants and the families who worked them across three centuries, from the salt pans to the counting houses. Sweeping, humane and full of the smell of tar and brine.',
      cover: { l: 'band', bg: '#1d2d4f', fg: '#e9dcc3', ac: '#e9dcc3', f: 'wide' }
    },
    {
      id: 'signal-from-lark-station', title: 'Signal from Lark Station', author: 'Mina Solberg', genre: 'sff',
      price: { hb: 25, pb: 15.99, eb: 9.99 }, stock: true, isNew: true,
      rating: 4.4, reviews: 31, pages: 296, publisher: 'Quire Editions', published: '2026-08-14', isbn: '978-1-555-13438-9',
      blurb: 'A radio operator at a polar station hears a voice she will record ten years from now.',
      synopsis: 'During the last month of the polar night, radio operator Dagny Holt picks up a distress call on a dead frequency. The voice is hers, older and frightened, describing a disaster that has not happened yet. A taut, atmospheric thriller of ice, static and time that plays fair right up to its final transmission.',
      cover: { l: 'signal', bg: '#e8e1d0', fg: '#1e1916', ac: '#d7372f', a2: '#243b6b', f: 'sans' }
    },
    {
      id: 'twelve-soups-for-winter', title: 'Twelve Soups for Winter', author: 'Bastien Leroux', genre: 'food',
      price: { hb: 22, eb: 11.99 }, stock: false,
      rating: 4.4, reviews: 14, pages: 176, publisher: 'Small Hours Press', published: '2025-10-16', isbn: '978-1-555-16585-7',
      blurb: 'Twelve soups, twelve breads and the case for eating the same good thing all week.',
      synopsis: 'Chef Bastien Leroux believes winter is best faced with a big pot on the stove. Here are twelve soups, each paired with a bread and a way to turn the leftovers into something new, from a smoky bean soup to a golden onion broth. A small, handsome cookbook that earns its place by the stove.',
      cover: { l: 'bigtype', bg: '#7b1e2c', fg: '#f4ead5', ac: '#f3d98b', f: 'sans' }
    },
    {
      id: 'a-knife-for-mr-pemberton', title: 'A Knife for Mr Pemberton', author: 'Harriet Oduya', genre: 'crime',
      price: { pb: 13.99, eb: 7.99 }, stock: false,
      rating: 4.1, reviews: 46, pages: 288, publisher: 'Quire Editions', published: '2025-06-05', isbn: '978-1-555-11893-8',
      blurb: 'A retired school cook, a village fête and the most polite poisoning in the county.',
      synopsis: 'Winnie Adebayo-Clarke retired after thirty years of school dinners to grow marrows and mind her own business. Then the chair of the fête committee collapses into the jam-judging table, and Winnie is the only one who noticed which knife cut the sponge. A sharp, funny village mystery that is kinder to its characters than they are to each other.',
      cover: { l: 'frame', bg: '#5a2e4a', fg: '#f4ead5', ac: '#d9a441', f: 'italic' }
    },
    {
      id: 'the-owl-who-ran-a-bookshop', title: 'The Owl Who Ran a Bookshop', author: 'Poppy Linwood', genre: 'kids',
      price: { hb: 12.99, pb: 6.99 }, stock: true,
      rating: 4.8, reviews: 35, pages: 32, publisher: 'Bramble Books', published: '2026-02-26', isbn: '978-1-555-17694-5',
      blurb: 'Every night after closing, the owl opens the shop for creatures who read by moonlight.',
      synopsis: 'When the bookshop on Hollow Lane closes for the night, Olive the owl turns the sign to Open for the creatures of the wood: mice who want adventure, a shy fox who loves poetry and a hedgehog who only reads recipes. A cosy picture book for anyone who has ever wanted to live in a bookshop. Ages 4 to 8.',
      cover: { l: 'sun', bg: '#243b6b', fg: '#f6efe1', ac: '#f3d98b', f: 'italic' }
    },
    {
      id: 'walking-the-salt-road', title: 'Walking the Salt Road', author: 'Imogen Farrah', genre: 'nature',
      price: { hb: 25, pb: 16.99, eb: 10.99 }, stock: true,
      rating: 4.5, reviews: 24, pages: 320, publisher: 'Northgate Books', published: '2026-03-26', isbn: '978-1-555-15603-9',
      blurb: 'Four hundred miles on foot along an old trade path, from the salt pans to the mountains.',
      synopsis: 'The salt road once carried the most precious cargo in the region from the coast to the high valleys. Imogen Farrah walks its whole length with a pack, a notebook and a borrowed dog, meeting shepherds, innkeepers and salt makers along the way. Travel writing at its most generous: curious, funny and alive to every mile.',
      cover: { l: 'rays', bg: '#d9a441', fg: '#2a1d14', ac: '#c1623f', f: 'wide' }
    },
    {
      id: 'maud-arden-a-life-in-thread', title: 'Maud Arden: A Life in Thread', author: 'Juniper Ashdown', genre: 'history',
      price: { hb: 28, pb: 17.99, eb: 11.99 }, stock: true,
      rating: 4.4, reviews: 19, pages: 400, publisher: 'Lamplight Press', published: '2026-04-16', isbn: '978-1-555-14254-4', kicker: 'A Biography',
      blurb: 'The first full biography of the dressmaker who dressed three queens and kept every pattern secret.',
      synopsis: 'Born above a draper’s shop, Maud Arden became the most sought-after dressmaker of her age, then burned her pattern books and vanished from society. Drawing on newly found letters, Juniper Ashdown reconstructs a life of extraordinary skill, stubborn independence and one great unexplained silence.',
      cover: { l: 'gingham', bg: '#f4ead5', fg: '#3b1d24', ac: '#c9707d', f: 'italic' }
    },
    {
      id: 'a-quiet-year-in-harrow', title: 'A Quiet Year in Harrow', author: 'Tomasz Bell', genre: 'fiction',
      price: { pb: 15.99, eb: 9.99 }, stock: true,
      rating: 4.3, reviews: 36, pages: 256, publisher: 'Small Hours Press', published: '2025-09-18', isbn: '978-1-555-10554-9',
      blurb: 'A widowed postman walks the same route for a year and slowly lets the town back in.',
      synopsis: 'Since his wife died, Aurel Penn has delivered the Harrow post in silence: forty-one streets, six hundred letterboxes and not one conversation. Then a girl on Mill Lane starts leaving him questions in the letterbox, and a year of small answers follows. Gentle, funny and quietly devastating in all the right places.',
      cover: { l: 'line', bg: '#d9d3c3', fg: '#1e1916', ac: '#7b1e2c', f: 'italic' }
    },
    {
      id: 'the-weaver-of-tallow-street', title: 'The Weaver of Tallow Street', author: 'Oren Blackwood', genre: 'sff',
      price: { pb: 16.99, eb: 9.99 }, stock: false,
      rating: 4.2, reviews: 40, pages: 416, publisher: 'Northgate Books', published: '2025-08-21', isbn: '978-1-555-13194-4',
      blurb: 'A weaver who can stitch memories into cloth is hired to unmake a king’s worst day.',
      synopsis: 'Ysolde Marr weaves memories into cloth for anyone who can pay: a wedding day for a widow, a first snowfall for a dying man. Then the king’s steward arrives on Tallow Street with a tapestry of a day the king wants gone, and a threat if she refuses. A dark, beautifully told fantasy about memory, power and what we owe to the past.',
      cover: { l: 'stripes', bg: '#3b2a4f', fg: '#f4ead5', ac: '#c9707d', f: 'italic' }
    },
    {
      id: 'field-notes-in-blue-ink', title: 'Field Notes in Blue Ink', author: 'Clover Anand', genre: 'poetry',
      price: { pb: 12, eb: 6.99 }, stock: false,
      rating: 4.7, reviews: 12, pages: 80, publisher: 'Low Tide Press', published: '2026-06-25', isbn: '978-1-555-15002-0',
      blurb: 'Short poems written in the margins of a botanist’s field notebook, one summer long.',
      synopsis: 'One summer, Clover Anand borrowed her late grandfather’s field notebook and began writing poems in its margins, beside his sketches of sedges and thistles. The result is a small, generous book of observation and grief, where every plant is also a memory.',
      cover: { l: 'graph', bg: '#f3d98b', fg: '#243b6b', ac: '#243b6b', f: 'italic' }
    },
    {
      id: 'the-slow-kitchen-almanac', title: 'The Slow Kitchen Almanac', author: 'Rosa Petrakis', genre: 'food',
      price: { hb: 32, eb: 14.99 }, stock: true,
      rating: 4.6, reviews: 30, pages: 368, publisher: 'Harrow & Finch', published: '2025-11-06', isbn: '978-1-555-16066-1',
      blurb: 'A year of seasonal cooking from a farmhouse kitchen, one unhurried recipe at a time.',
      synopsis: 'Organised month by month, Rosa Petrakis’s almanac collects 120 recipes from her farmhouse kitchen: braises that look after themselves, breads that rise overnight and preserves for every glut. Between the recipes are notes on the garden, the weather and the neighbours who drop in at dinner time.',
      cover: { l: 'label', bg: '#66652b', fg: '#3b3417', ac: '#f1e6cf', f: 'serif' }
    },
    {
      id: 'death-at-the-bell-and-anchor', title: 'Death at the Bell & Anchor', author: 'Morwenna Kite', genre: 'crime',
      price: { pb: 12.99, eb: 6.99 }, stock: true,
      rating: 4.3, reviews: 57, pages: 320, publisher: 'Harrow & Finch', published: '2025-07-10', isbn: '978-1-555-12449-6',
      blurb: 'Quiz night at the harbour pub ends with the landlord at the bottom of the cellar steps.',
      synopsis: 'Every Thursday the Bell & Anchor hosts the fiercest pub quiz on the coast, and this Thursday the landlord never reads out the final round. Quizmaster Tess Morrow suspects the answers are hidden somewhere in the questions. The first in a harbour-town series with a pub full of suspects and a very good fish pie.',
      cover: { l: 'split', bg: '#0f3b36', fg: '#f4ead5', ac: '#e2725b', f: 'serif' }
    },
    {
      id: 'captain-sprockets-cloud-machine', title: 'Captain Sprocket’s Cloud Machine', author: 'Fatima Yusuf', genre: 'kids',
      price: { hb: 15.99, pb: 8.99, eb: 5.99 }, stock: true,
      rating: 4.7, reviews: 28, pages: 176, publisher: 'Bramble Books', published: '2026-03-05', isbn: '978-1-555-17420-0',
      blurb: 'An inventor, a very stubborn goat and a machine that makes rain on demand.',
      synopsis: 'The village of Dustwell has not seen rain in a year, so Captain Sprocket and her goat Nutmeg build a machine to catch clouds. What could possibly go wrong? A fizzing, fully illustrated adventure for young readers, packed with diagrams, jokes and one enormous thunderstorm. Ages 7 to 10.',
      cover: { l: 'rainbow', bg: '#1d2d4f', fg: '#fff6e0', ac: '#f2c14e', a2: '#e2725b', a3: '#9cc3d5', f: 'sans' }
    }
  ];

  const REVIEWS = [
    { name: 'Ruth A.', rating: 5, date: '2026-09-12', title: 'Read it in two sittings', text: 'Started it on the bus home and finished it at two in the morning. The kind of book you immediately want to press into someone else’s hands.' },
    { name: 'Declan M.', rating: 5, date: '2026-09-03', title: 'The shelf note was right', text: 'I picked it up because the handwritten note on the shelf made me laugh. If anything, the note undersold it.' },
    { name: 'Sunita P.', rating: 4, date: '2026-08-27', title: 'Slow start, glorious finish', text: 'The first fifty pages ask for a little patience. Give it to them. By the halfway mark I was rationing chapters.' },
    { name: 'Owen T.', rating: 5, date: '2026-08-19', title: 'Bought three more as gifts', text: 'Ordered one for me, then came back for my sister, my dad and a friend at work. Wrapped beautifully at the counter, too.', all: true },
    { name: 'Marisol G.', rating: 4, date: '2026-08-11', title: 'Beautifully made', text: 'Lovely paper, a cover that looks good face-out on the shelf and writing to match. One star off only because I wanted more of it.', all: true },
    { name: 'Felix W.', rating: 5, date: '2026-07-30', title: 'Our book club argued for two hours', text: 'Everyone had a different favourite character and nobody agreed about the ending, which is exactly what you want from a club pick.', genres: ['fiction', 'crime', 'sff', 'history'] },
    { name: 'Grace O.', rating: 3, date: '2026-07-22', title: 'Not quite for me', text: 'Well written and I can see why people love it, but it did not grab me the way I hoped. I would still try the next one.' },
    { name: 'Benedict K.', rating: 5, date: '2026-07-14', title: 'Ordered Monday, collected Wednesday', text: 'Asked at the counter, collected two days later with a bookmark tucked inside. The book itself is a joy.', all: true },
    { name: 'Yara S.', rating: 4, date: '2026-07-02', title: 'A quiet, generous book', text: 'Nothing explodes and nobody shouts, and I loved it for that. Perfect for a wet weekend with the heating on.', genres: ['fiction', 'poetry', 'nature', 'history', 'food'] },
    { name: 'Hugh B.', rating: 5, date: '2026-06-21', title: 'Kept me guessing', text: 'I was sure I had it worked out by the middle. I had not. Very satisfying, and fair to the reader all the way through.', genres: ['crime', 'sff'] },
    { name: 'Nadia R.', rating: 5, date: '2026-06-12', title: 'Asked for every single night', text: 'We have read it at bedtime every night for a fortnight. I can now recite it, and I still like it, which says a lot.', genres: ['kids'] },
    { name: 'Tomás V.', rating: 4, date: '2026-06-03', title: 'So much to look at', text: 'The pictures reward a second and third look. Our four-year-old keeps finding new details on every page.', genres: ['kids'] },
    { name: 'Aoife C.', rating: 5, date: '2026-05-25', title: 'Already cooking from it', text: 'Three recipes in and every one has worked first time. The notes between the recipes are as good as the food.', genres: ['food'] },
    { name: 'Priyanka D.', rating: 4, date: '2026-05-16', title: 'Lines I keep coming back to', text: 'I have folded down the corners of half the pages. A few poems did not land for me; the ones that did will stay a long time.', genres: ['poetry'] }
  ];
  /* @data-end */

  /* ==========================================================================
     2. HELPERS
     ========================================================================== */
  const $ = (sel, root = document) => root.querySelector(sel);
  const $$ = (sel, root = document) => Array.from(root.querySelectorAll(sel));
  const money = (n) => SHOP.currency + Number(n).toFixed(2);
  const bookById = (id) => BOOKS.find((b) => b.id === id);
  const genreById = (id) => GENRES.find((g) => g.id === id);
  const esc = (s) => String(s).replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
  const formatOrder = ['pb', 'hb', 'eb'];          // preferred default format
  const displayOrder = ['hb', 'pb', 'eb'];         // order formats are listed in
  const defaultFormat = (b, allowed) => formatOrder.find((f) => b.price[f] != null && (!allowed || !allowed.size || allowed.has(f))) || formatOrder.find((f) => b.price[f] != null);
  const hash = (s) => Array.from(s).reduce((h, c) => Math.imul(h ^ c.charCodeAt(0), 16777619) >>> 0, 2166136261);
  const longDate = (iso) => new Date(iso + 'T12:00:00').toLocaleDateString('en-US', { day: 'numeric', month: 'long', year: 'numeric' });
  const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  const ICON_BAG = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M5 8h14l-1 12H6L5 8Z"/><path d="M9 8V6a3 3 0 0 1 6 0v2"/><path d="M12 12v4M10 14h4"/></svg>';
  const ICON_CHECK = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m5 12 5 5 9-10"/></svg>';

  /* ==========================================================================
     3. GENERATED BOOK COVERS
     Title size is fitted to the longest word so nothing overflows the cover.
     ========================================================================== */
  const FONT_WIDTH = { serif: 0.62, black: 0.76, italic: 0.5, sans: 0.5, wide: 0.86 };
  const LAYOUT_SIZE = { bigtype: 23, label: 9, tile: 10, gingham: 10, frame: 11, line: 10.5, rays: 11.5, band: 11, graph: 11.5, split: 12, stripes: 11.5, orbit: 11 };
  const LAYOUT_WIDTH = { label: 50, tile: 56, gingham: 56, frame: 58, rays: 64, band: 72, bigtype: 74, arch: 60, graph: 56 };

  function coverParts(book) {
    const c = book.cover;
    const words = book.title.split(' ');
    const longest = Math.max(...words.map((w) => w.length));
    const fit = (LAYOUT_WIDTH[c.l] || 74) / (longest * (FONT_WIDTH[c.f] || 0.6));
    const size = Math.min(LAYOUT_SIZE[c.l] || 13, fit);
    const kicker = book.kicker != null ? book.kicker : (genreById(book.genre) || {}).kicker;
    let style = `--bg:${c.bg};--fg:${c.fg};--ac:${c.ac};--ts:${size.toFixed(2)}cqi;`;
    if (c.a2) style += `--a2:${c.a2};`;
    if (c.a3) style += `--a3:${c.a3};`;
    const html = '<span class="cover-art"></span><span class="cover-text">'
      + (kicker ? `<span class="cover-kicker">${esc(kicker)}</span>` : '')
      + `<span class="cover-title">${words.map((w) => `<span>${esc(w)}</span>`).join(' ')}</span>`
      + `<span class="cover-author">${esc(book.author)}</span></span>`;
    return { layout: c.l, font: c.f, style, html };
  }

  function coverHTML(book, extraClass = '') {
    const p = coverParts(book);
    return `<div class="cover cover--${p.layout} ${extraClass}" data-f="${p.font}" style="${p.style}" aria-hidden="true">${p.html}</div>`;
  }

  function paintCover(el, book) {
    if (!book) return;
    const p = coverParts(book);
    Array.from(el.classList).filter((c) => /^cover--(?!lg$|sm$)/.test(c)).forEach((c) => el.classList.remove(c));
    el.classList.add('cover', 'cover--' + p.layout, 'is-painted');
    el.dataset.f = p.font;
    el.setAttribute('style', p.style);
    el.setAttribute('aria-hidden', 'true');
    el.innerHTML = p.html;
  }

  function initCovers() {
    $$('[data-cover]').forEach((el) => paintCover(el, bookById(el.dataset.cover)));
  }

  function starsHTML(rating, label = true) {
    const pct = Math.max(0, Math.min(100, (rating / 5) * 100));
    const aria = label ? `role="img" aria-label="Rated ${rating} out of 5"` : 'aria-hidden="true"';
    return `<span class="stars" ${aria}><span class="stars-fill" style="width:${pct}%"></span></span>`;
  }

  function badgesHTML(b) {
    return [
      b.isNew ? '<span class="tag tag--new">New</span>' : '',
      b.pick ? '<span class="tag tag--pick">Staff pick</span>' : '',
      b.signed ? '<span class="tag tag--signed">Signed</span>' : ''
    ].join('');
  }

  function bookCardHTML(b, allowedFormats) {
    const f = defaultFormat(b, allowedFormats);
    const badges = badgesHTML(b);
    const fmtList = displayOrder.filter((x) => b.price[x] != null).map((x) => `<span>${FORMATS[x]}</span>`).join('');
    return `<article class="book-card">
      <a class="book-card-cover" href="book.html?id=${b.id}" tabindex="-1" aria-hidden="true">${coverHTML(b)}</a>
      ${badges ? `<div class="book-card-tags">${badges}</div>` : ''}
      <div class="book-card-body">
        <p class="book-card-genre">${esc(genreById(b.genre).name)}</p>
        <h3 class="book-card-title"><a href="book.html?id=${b.id}">${esc(b.title)}</a></h3>
        <p class="book-card-author">${esc(b.author)}</p>
        <div class="book-card-rating">${starsHTML(b.rating)}<span>${b.rating.toFixed(1)} <span class="text-soft">(${b.reviews})</span></span></div>
        <p class="book-card-formats"><span class="visually-hidden">Formats: </span>${fmtList}</p>
        <div class="book-card-foot">
          <p class="book-card-price"><strong>${money(b.price[f])}</strong><span>${FORMATS[f]}</span></p>
          <button type="button" class="btn-add" data-add="${b.id}" data-format="${f}" aria-label="Add ${esc(b.title)}, ${FORMATS[f].toLowerCase()}, to basket">${ICON_BAG}<span class="btn-add-label">Add</span></button>
        </div>
        <p class="book-card-stock ${b.stock ? 'is-in' : 'is-out'}">${b.stock ? 'In stock at Wren Street' : 'Special order, 5–7 days'}</p>
      </div>
    </article>`;
  }

  const shelfScale = (id) => (0.92 + (hash(id) % 13) / 100).toFixed(2);

  function shelfItemHTML(b) {
    const f = defaultFormat(b);
    return `<li class="shelf-item" style="--s:${shelfScale(b.id)}">
      <a class="shelf-cover" href="book.html?id=${b.id}" tabindex="-1" aria-hidden="true">${coverHTML(b)}</a>
      <h3 class="shelf-title"><a href="book.html?id=${b.id}">${esc(b.title)}</a></h3>
      <p class="shelf-author">${esc(b.author)}</p>
      <div class="shelf-foot">
        <span class="shelf-price">${money(b.price[f])}</span>
        <button type="button" class="btn-add btn-add--sm" data-add="${b.id}" data-format="${f}" aria-label="Add ${esc(b.title)}, ${FORMATS[f].toLowerCase()}, to basket">${ICON_BAG}</button>
      </div>
    </li>`;
  }

  /* ==========================================================================
     4. HEADER, SHELVES, REVEAL
     ========================================================================== */
  function initHeader() {
    const header = $('[data-header]');
    if (!header) return;
    const onScroll = () => header.classList.toggle('is-scrolled', window.scrollY > 8);
    onScroll();
    window.addEventListener('scroll', onScroll, { passive: true });

    // Close the mobile menu after following an in-page link.
    const menu = $('#main-nav');
    if (menu && window.bootstrap) {
      $$('a.nav-link', menu).forEach((a) => a.addEventListener('click', () => {
        if (menu.classList.contains('show')) window.bootstrap.Collapse.getOrCreateInstance(menu).hide();
      }));
    }
  }

  /* In-page links scroll smoothly (CSS smooth scrolling is off so scripted
     scrolling stays instant) and move focus to the target section. */
  function initAnchors() {
    document.addEventListener('click', (e) => {
      const a = e.target.closest('a[href*="#"]');
      if (!a || e.defaultPrevented || e.button !== 0 || e.metaKey || e.ctrlKey || e.shiftKey) return;
      const url = new URL(a.href, location.href);
      if (url.pathname !== location.pathname || url.hash.length < 2) return;
      const target = document.getElementById(decodeURIComponent(url.hash.slice(1)));
      if (!target) return;
      e.preventDefault();
      target.scrollIntoView({ behavior: reduceMotion ? 'auto' : 'smooth', block: 'start' });
      if (!target.hasAttribute('tabindex')) target.setAttribute('tabindex', '-1');
      target.focus({ preventScroll: true });
      try { history.pushState(null, '', url.hash); } catch (err) { /* file:// */ }
    });
  }

  function initShelves() {
    // Arrow buttons are linked to their track through aria-controls.
    $$('[data-shelf-track]').forEach((track) => {
      const prev = track.id ? $(`[data-shelf-prev][aria-controls="${track.id}"]`) : null;
      const next = track.id ? $(`[data-shelf-next][aria-controls="${track.id}"]`) : null;
      const step = () => Math.max(track.clientWidth * 0.8, 200);
      // aria-disabled (not disabled) keeps keyboard focus on the button at either end.
      const update = () => {
        const max = track.scrollWidth - track.clientWidth - 2;
        if (prev) prev.setAttribute('aria-disabled', String(track.scrollLeft <= 2));
        if (next) next.setAttribute('aria-disabled', String(track.scrollLeft >= max));
      };
      const go = (btn, dir) => btn && btn.addEventListener('click', () => {
        if (btn.getAttribute('aria-disabled') === 'true') return;
        track.scrollBy({ left: dir * step(), behavior: reduceMotion ? 'auto' : 'smooth' });
      });
      go(prev, -1);
      go(next, 1);
      track.addEventListener('scroll', update, { passive: true });
      window.addEventListener('resize', update);
      update();
    });
  }

  function initReveal() {
    const items = $$('.reveal');
    if (!items.length) return;
    if (reduceMotion || !('IntersectionObserver' in window)) {
      items.forEach((el) => el.classList.add('is-visible'));
      return;
    }
    const io = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return;
        entry.target.classList.add('is-visible');
        io.unobserve(entry.target);
      });
    }, { rootMargin: '0px 0px -8% 0px', threshold: 0.08 });
    items.forEach((el) => io.observe(el));
  }

  /* Colour helpers so spine titles always stay readable on their cloth colour. */
  const rgb = (hex) => { const n = parseInt(hex.slice(1), 16); return [(n >> 16) & 255, (n >> 8) & 255, n & 255]; };
  const toHex = (c) => '#' + c.map((v) => Math.round(v).toString(16).padStart(2, '0')).join('');
  const luminance = (hex) => {
    const [r, g, b] = rgb(hex).map((v) => { v /= 255; return v <= 0.03928 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4); });
    return 0.2126 * r + 0.7152 * g + 0.0722 * b;
  };
  const contrast = (a, b) => { const [x, y] = [luminance(a), luminance(b)].sort((m, n) => n - m); return (x + 0.05) / (y + 0.05); };
  function spineColours(bg) {
    const light = '#f7f1e4';
    const dark = '#1f1a17';
    let cloth = bg;
    for (let i = 0; i < 6; i += 1) {
      const ink = contrast(cloth, light) >= contrast(cloth, dark) ? light : dark;
      if (contrast(cloth, ink) >= 4.6) return { cloth, ink };
      // Nudge the cloth away from the ink colour until the title reads clearly.
      const target = ink === light ? [0, 0, 0] : [255, 255, 255];
      cloth = toHex(rgb(cloth).map((v, k) => v + (target[k] - v) * 0.14));
    }
    return { cloth, ink: contrast(cloth, light) >= contrast(cloth, dark) ? light : dark };
  }

  /* Decorative row of spines that sits on the footer, built from the catalogue. */
  function initSpines() {
    const row = $('[data-spines]');
    if (!row) return;
    const books = BOOKS.concat(BOOKS.slice().reverse());
    row.innerHTML = books.map((b, i) => {
      const h = hash(b.id + i);
      const width = Math.round(22 + Math.min(b.pages, 520) / 22);
      const height = 72 + (h % 22);
      const lean = i % 17 === 11 ? ' is-leaning' : '';
      const { cloth, ink } = spineColours(b.cover.bg);
      return `<span class="spine${lean}" style="--bg:${cloth};--fg:${ink};--ac:${b.cover.ac};--w:${width}px;--h:${height}%"><span>${esc(b.title)}</span></span>`;
    }).join('');
  }

  /* ==========================================================================
     5. BASKET (offcanvas drawer + localStorage)
     ========================================================================== */
  const Cart = {
    items: [],
    load() {
      try {
        const raw = JSON.parse(localStorage.getItem(SHOP.storageKey) || '[]');
        this.items = Array.isArray(raw) ? raw.filter((i) => {
          const b = bookById(i.id);
          return b && b.price[i.format] != null && i.qty > 0;
        }) : [];
      } catch (e) {
        this.items = [];
      }
    },
    save() {
      try { localStorage.setItem(SHOP.storageKey, JSON.stringify(this.items)); } catch (e) { /* storage unavailable: basket lasts for this page view */ }
      document.dispatchEvent(new CustomEvent('basket:change'));
    },
    find(id, format) { return this.items.find((i) => i.id === id && i.format === format); },
    add(id, format, qty = 1) {
      const line = this.find(id, format);
      if (line) line.qty = Math.min(line.qty + qty, 20);
      else this.items.push({ id, format, qty: Math.min(qty, 20) });
      this.save();
    },
    set(id, format, qty) {
      const line = this.find(id, format);
      if (!line) return;
      if (qty <= 0) this.items = this.items.filter((i) => i !== line);
      else line.qty = Math.min(qty, 20);
      this.save();
    },
    count() { return this.items.reduce((n, i) => n + i.qty, 0); },
    subtotal() { return this.items.reduce((n, i) => n + bookById(i.id).price[i.format] * i.qty, 0); }
  };

  let announce = () => {};
  let openBasket = () => {};

  function initCart() {
    const drawer = $('#cart');
    Cart.load();

    const live = $('[data-live]');
    announce = (msg) => {
      if (!live) return;
      live.textContent = '';
      window.setTimeout(() => { live.textContent = msg; }, 60);
    };

    const toast = $('[data-toast]');
    let toastTimer;
    const showToast = (book, format) => {
      if (!toast) return;
      $('[data-toast-cover]', toast).innerHTML = coverHTML(book);
      $('[data-toast-title]', toast).textContent = book.title;
      $('[data-toast-format]', toast).textContent = FORMATS[format] + ' · ' + money(book.price[format]);
      toast.classList.add('is-shown');
      window.clearTimeout(toastTimer);
      toastTimer = window.setTimeout(() => toast.classList.remove('is-shown'), 3200);
    };

    // The toast is only a shortcut to the drawer; hide it once the drawer opens.
    if (drawer && toast) drawer.addEventListener('show.bs.offcanvas', () => toast.classList.remove('is-shown'));

    openBasket = () => {
      if (drawer && window.bootstrap) window.bootstrap.Offcanvas.getOrCreateInstance(drawer).show();
    };

    const render = () => {
      const count = Cart.count();
      $$('[data-cart-count]').forEach((el) => {
        el.textContent = count;
        el.classList.toggle('is-empty', count === 0);
      });
      $$('[data-cart-button]').forEach((btn) => btn.setAttribute('aria-label', `Open basket, ${count} ${count === 1 ? 'item' : 'items'}`));
      if (!drawer) return;

      const list = $('[data-cart-items]', drawer);
      const empty = $('[data-cart-empty]', drawer);
      const footer = $('[data-cart-footer]', drawer);
      const sub = Cart.subtotal();
      $('[data-cart-heading-count]', drawer).textContent = count ? `(${count})` : '';
      empty.hidden = count > 0;
      footer.hidden = count === 0;
      list.innerHTML = Cart.items.map((i) => {
        const b = bookById(i.id);
        return `<li class="cart-line">
          <a class="cart-line-cover" href="book.html?id=${b.id}" tabindex="-1" aria-hidden="true">${coverHTML(b)}</a>
          <div class="cart-line-body">
            <p class="cart-line-title"><a href="book.html?id=${b.id}">${esc(b.title)}</a></p>
            <p class="cart-line-meta">${esc(b.author)} · ${FORMATS[i.format]}</p>
            <div class="cart-line-row">
              <div class="qty qty--sm" role="group" aria-label="Quantity for ${esc(b.title)}">
                <button type="button" data-line-step="-1" data-id="${b.id}" data-format="${i.format}" aria-label="Remove one copy of ${esc(b.title)}">−</button>
                <span class="qty-value" aria-live="polite">${i.qty}</span>
                <button type="button" data-line-step="1" data-id="${b.id}" data-format="${i.format}" aria-label="Add one more copy of ${esc(b.title)}">+</button>
              </div>
              <span class="cart-line-price">${money(b.price[i.format] * i.qty)}</span>
            </div>
            <button type="button" class="cart-line-remove" data-line-remove data-id="${b.id}" data-format="${i.format}">Remove<span class="visually-hidden"> ${esc(b.title)} from basket</span></button>
          </div>
        </li>`;
      }).join('');

      $('[data-cart-subtotal]', drawer).textContent = money(sub);
      const left = SHOP.freeDelivery - sub;
      const bar = $('[data-ship-bar]', drawer);
      const msg = $('[data-ship-msg]', drawer);
      bar.style.width = Math.min(100, (sub / SHOP.freeDelivery) * 100) + '%';
      msg.innerHTML = left > 0
        ? `You are <strong>${money(left)}</strong> away from free delivery.`
        : '<strong>Free delivery unlocked.</strong> Or collect in store for free.';
      const note = $('[data-checkout-note]', drawer);
      if (note && count === 0) note.hidden = true;
    };

    // Add-to-basket buttons anywhere on the page (cards, shelves, hero, book club).
    document.addEventListener('click', (e) => {
      const btn = e.target.closest('[data-add]');
      if (!btn) return;
      const book = bookById(btn.dataset.add);
      if (!book) return;
      const format = btn.dataset.format && book.price[btn.dataset.format] != null ? btn.dataset.format : defaultFormat(book);
      Cart.add(book.id, format, 1);
      showToast(book, format);
      announce(`${book.title}, ${FORMATS[format].toLowerCase()}, added to your basket.`);
      btn.classList.add('is-added');
      window.setTimeout(() => btn.classList.remove('is-added'), 1400);
    });

    if (drawer) {
      drawer.addEventListener('click', (e) => {
        const step = e.target.closest('[data-line-step]');
        const remove = e.target.closest('[data-line-remove]');
        if (step) {
          const line = Cart.find(step.dataset.id, step.dataset.format);
          if (!line) return;
          const next = line.qty + Number(step.dataset.lineStep);
          Cart.set(step.dataset.id, step.dataset.format, next);
          if (next <= 0) {
            announce(`${bookById(step.dataset.id).title} removed from your basket.`);
            $('#cart-title').focus();
          } else {
            // Keep keyboard focus on the same control after the list re-renders.
            const sel = `[data-line-step="${step.dataset.lineStep}"][data-id="${step.dataset.id}"][data-format="${step.dataset.format}"]`;
            const again = $(sel, drawer);
            if (again) again.focus();
          }
        }
        if (remove) {
          const title = bookById(remove.dataset.id).title;
          Cart.set(remove.dataset.id, remove.dataset.format, 0);
          announce(`${title} removed from your basket.`);
          $('#cart-title').focus();
        }
      });

      const checkout = $('[data-checkout]', drawer);
      const note = $('[data-checkout-note]', drawer);
      if (checkout && note) checkout.addEventListener('click', () => { note.hidden = false; });
    }

    // Toast "View basket" button
    const toastOpen = $('[data-toast-open]');
    if (toastOpen) toastOpen.addEventListener('click', () => { toast.classList.remove('is-shown'); openBasket(); });

    // Keep several open tabs in sync.
    window.addEventListener('storage', (e) => {
      if (e.key !== SHOP.storageKey) return;
      Cart.load();
      render();
    });

    document.addEventListener('basket:change', render);
    render();
  }

  /* ==========================================================================
     6. SHOP: filters, price range, sort, search, result count
     ========================================================================== */
  function initShop() {
    const grid = $('[data-shop-grid]');
    const form = $('[data-filters]');
    if (!grid || !form) return;

    const prices = BOOKS.flatMap((b) => Object.values(b.price));
    const floor = Math.floor(Math.min(...prices));
    const ceil = Math.ceil(Math.max(...prices));
    const search = $('[data-shop-search]');
    const sort = $('[data-shop-sort]');
    const minRange = $('[data-price-min]', form);
    const maxRange = $('[data-price-max]', form);
    const priceOut = $('[data-price-output]', form);
    const rangeWrap = $('[data-range]', form);
    const countEl = $('[data-result-count]');
    const chips = $('[data-chips]');
    const empty = $('[data-empty]');
    const badge = $('[data-filter-badge]');

    [minRange, maxRange].forEach((r) => { r.min = floor; r.max = ceil; r.step = 1; });

    // Counts next to each genre checkbox
    GENRES.forEach((g) => {
      const el = $(`[data-genre-count="${g.id}"]`, form);
      if (el) el.textContent = BOOKS.filter((b) => b.genre === g.id).length;
    });
    Object.keys(FORMATS).forEach((f) => {
      const el = $(`[data-format-count="${f}"]`, form);
      if (el) el.textContent = BOOKS.filter((b) => b.price[f] != null).length;
    });

    // Read the starting state from the URL (links from the home page use ?genre=, the header search uses ?q=).
    const params = new URLSearchParams(location.search);
    const listParam = (k) => (params.get(k) || '').split(',').filter(Boolean);
    listParam('genre').forEach((g) => { const cb = $(`input[name="genre"][value="${CSS.escape(g)}"]`, form); if (cb) cb.checked = true; });
    listParam('format').forEach((f) => { const cb = $(`input[name="format"][value="${CSS.escape(f)}"]`, form); if (cb) cb.checked = true; });
    minRange.value = Math.max(floor, Number(params.get('min')) || floor);
    maxRange.value = Math.min(ceil, Number(params.get('max')) || ceil);
    if (params.get('stock') === '1') $('[name="stock"]', form).checked = true;
    if (params.get('picks') === '1') $('[name="picks"]', form).checked = true;
    if (search) search.value = params.get('q') || '';
    if (sort && params.get('sort') && $(`option[value="${CSS.escape(params.get('sort'))}"]`, sort)) sort.value = params.get('sort');

    const state = () => ({
      q: (search ? search.value : '').trim().toLowerCase(),
      genres: new Set($$('input[name="genre"]:checked', form).map((i) => i.value)),
      formats: new Set($$('input[name="format"]:checked', form).map((i) => i.value)),
      min: Number(minRange.value),
      max: Number(maxRange.value),
      stock: $('[name="stock"]', form).checked,
      picks: $('[name="picks"]', form).checked,
      sort: sort ? sort.value : 'featured'
    });

    const paintRange = () => {
      let lo = Number(minRange.value);
      let hi = Number(maxRange.value);
      if (lo > hi) { [lo, hi] = [hi, lo]; }
      const span = ceil - floor || 1;
      rangeWrap.style.setProperty('--from', ((lo - floor) / span) * 100 + '%');
      rangeWrap.style.setProperty('--to', ((hi - floor) / span) * 100 + '%');
      priceOut.textContent = `${SHOP.currency}${lo} – ${SHOP.currency}${hi}`;
      minRange.setAttribute('aria-valuetext', SHOP.currency + lo);
      maxRange.setAttribute('aria-valuetext', SHOP.currency + hi);
    };

    const matches = (b, s) => {
      if (s.genres.size && !s.genres.has(b.genre)) return false;
      const fmts = Object.keys(b.price).filter((f) => !s.formats.size || s.formats.has(f));
      if (!fmts.length) return false;
      const lo = Math.min(s.min, s.max);
      const hi = Math.max(s.min, s.max);
      if (!fmts.some((f) => b.price[f] >= lo && b.price[f] <= hi)) return false;
      if (s.stock && !b.stock) return false;
      if (s.picks && !b.pick) return false;
      if (s.q) {
        const hay = `${b.title} ${b.author} ${genreById(b.genre).name} ${b.publisher}`.toLowerCase();
        if (!s.q.split(/\s+/).every((word) => hay.includes(word))) return false;
      }
      return true;
    };

    const sorters = {
      featured: () => 0,
      newest: (a, b) => b.published.localeCompare(a.published),
      'price-asc': (a, b, s) => a.price[defaultFormat(a, s.formats)] - b.price[defaultFormat(b, s.formats)],
      'price-desc': (a, b, s) => b.price[defaultFormat(b, s.formats)] - a.price[defaultFormat(a, s.formats)],
      title: (a, b) => a.title.replace(/^(The|A) /, '').localeCompare(b.title.replace(/^(The|A) /, '')),
      rating: (a, b) => b.rating - a.rating || b.reviews - a.reviews
    };

    const chipHTML = (label, key, value) => `<button type="button" class="chip" data-chip="${key}" data-value="${esc(value)}">${esc(label)}<span class="visually-hidden"> (remove filter)</span><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M6 6l12 12M18 6 6 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg></button>`;

    const apply = () => {
      const s = state();
      paintRange();
      const results = BOOKS.filter((b) => matches(b, s)).sort((a, b) => (sorters[s.sort] || sorters.featured)(a, b, s));
      grid.innerHTML = results.map((b) => `<div class="col">${bookCardHTML(b, s.formats)}</div>`).join('');
      empty.hidden = results.length > 0;
      countEl.textContent = `Showing ${results.length} of ${BOOKS.length} books`;
      $$('[data-show-results]').forEach((btn) => { btn.textContent = results.length ? `Show ${results.length} ${results.length === 1 ? 'book' : 'books'}` : 'No matches, adjust filters'; });

      // Active-filter chips
      const list = [];
      if (s.q) list.push(chipHTML(`“${s.q}”`, 'q', ''));
      s.genres.forEach((g) => list.push(chipHTML(genreById(g).name, 'genre', g)));
      s.formats.forEach((f) => list.push(chipHTML(FORMATS[f], 'format', f)));
      if (s.min > floor || s.max < ceil) list.push(chipHTML(`${SHOP.currency}${Math.min(s.min, s.max)}–${SHOP.currency}${Math.max(s.min, s.max)}`, 'price', ''));
      if (s.stock) list.push(chipHTML('In stock', 'stock', ''));
      if (s.picks) list.push(chipHTML('Staff picks', 'picks', ''));
      chips.innerHTML = list.join('') + (list.length > 1 ? '<button type="button" class="chip chip--clear" data-clear-filters>Clear all</button>' : '');
      chips.hidden = list.length === 0;
      if (badge) {
        const n = s.genres.size + s.formats.size + (s.min > floor || s.max < ceil ? 1 : 0) + (s.stock ? 1 : 0) + (s.picks ? 1 : 0);
        badge.textContent = n;
        badge.hidden = n === 0;
      }

      // Keep the URL shareable
      const p = new URLSearchParams();
      if (s.q) p.set('q', s.q);
      if (s.genres.size) p.set('genre', Array.from(s.genres).join(','));
      if (s.formats.size) p.set('format', Array.from(s.formats).join(','));
      if (s.min > floor) p.set('min', s.min);
      if (s.max < ceil) p.set('max', s.max);
      if (s.stock) p.set('stock', '1');
      if (s.picks) p.set('picks', '1');
      if (s.sort !== 'featured') p.set('sort', s.sort);
      const qs = p.toString();
      try { history.replaceState(null, '', location.pathname + (qs ? '?' + qs : '')); } catch (e) { /* file:// */ }
    };

    const clearAll = () => {
      form.reset();
      minRange.value = floor;
      maxRange.value = ceil;
      if (search) search.value = '';
      apply();
    };

    let t;
    form.addEventListener('input', (e) => {
      if (e.target === minRange && Number(minRange.value) > Number(maxRange.value)) minRange.value = maxRange.value;
      if (e.target === maxRange && Number(maxRange.value) < Number(minRange.value)) maxRange.value = minRange.value;
      apply();
    });
    form.addEventListener('submit', (e) => e.preventDefault());
    if (search) {
      search.addEventListener('input', () => { window.clearTimeout(t); t = window.setTimeout(apply, 140); });
      const searchForm = search.closest('form');
      if (searchForm) searchForm.addEventListener('submit', (e) => { e.preventDefault(); apply(); });
    }
    if (sort) sort.addEventListener('change', apply);

    document.addEventListener('click', (e) => {
      if (e.target.closest('[data-clear-filters]')) { clearAll(); return; }
      const chip = e.target.closest('[data-chip]');
      if (!chip) return;
      const { chip: key, value } = chip.dataset;
      if (key === 'q' && search) search.value = '';
      if (key === 'genre' || key === 'format') { const cb = $(`input[name="${key}"][value="${CSS.escape(value)}"]`, form); if (cb) cb.checked = false; }
      if (key === 'price') { minRange.value = floor; maxRange.value = ceil; }
      if (key === 'stock' || key === 'picks') $(`[name="${key}"]`, form).checked = false;
      apply();
      if (countEl) countEl.focus();
    });

    if (location.hash === '#shop-search' && search) search.focus();
    apply();
  }

  /* ==========================================================================
     7. BOOK DETAIL PAGE (book.html?id=…)
     ========================================================================== */
  function reviewsFor(book) {
    const fits = (r) => (r.genres ? r.genres.includes(book.genre) : (r.all || book.genre !== 'kids'));
    return REVIEWS.filter(fits)
      .sort((a, b) => hash(book.id + a.name) - hash(book.id + b.name))
      .slice(0, 4)
      .sort((a, b) => b.date.localeCompare(a.date));
  }

  function ratingSpread(book) {
    const n = book.reviews;
    const five = Math.round(n * Math.min(0.9, Math.max(0.25, (book.rating - 3.4) / 1.6)));
    const four = Math.round((n - five) * 0.66);
    const three = Math.round((n - five - four) * 0.6);
    const two = Math.round((n - five - four - three) * 0.6);
    return [five, four, three, two, n - five - four - three - two];
  }

  function relatedFor(book) {
    const same = BOOKS.filter((b) => b.id !== book.id && b.genre === book.genre);
    const rest = BOOKS.filter((b) => b.id !== book.id && b.genre !== book.genre)
      .sort((a, b) => hash(book.id + a.id) - hash(book.id + b.id));
    return same.concat(rest).slice(0, 8);
  }

  function initials(name) {
    return name.split(/\s+/).map((p) => p[0]).join('').slice(0, 2).toUpperCase();
  }

  function renderBook(root, book) {
    const g = genreById(book.genre);
    const set = (key, value, html = false) => $$(`[data-b="${key}"]`, root).forEach((el) => { if (html) el.innerHTML = value; else el.textContent = value; });

    document.title = `${book.title} by ${book.author} — Folio & Fern Booksellers`;
    set('title', book.title);
    set('author', book.author);
    set('genre', g.name);
    set('blurb', book.blurb);
    set('synopsis', book.synopsis);
    set('rating', book.rating.toFixed(1));
    set('reviews', `${book.reviews} reviews`);
    set('stars', starsHTML(book.rating), true);
    set('tags', badgesHTML(book), true);
    $$('[data-b-genre-link]', root).forEach((a) => { a.href = `shop.html?genre=${book.genre}`; });
    $$('.book-object [data-cover]', root).forEach((el) => { el.dataset.cover = book.id; paintCover(el, book); });

    // Formats
    const picker = $('[data-format-options]', root);
    const def = defaultFormat(book);
    picker.innerHTML = displayOrder.filter((f) => book.price[f] != null).map((f) => `
      <label class="format-option">
        <input type="radio" name="format" value="${f}"${f === def ? ' checked' : ''}>
        <span class="format-name">${FORMATS[f]}</span>
        <span class="format-price">${money(book.price[f])}</span>
        <span class="format-meta">${f === 'eb' ? 'ePub, instant download' : (book.stock ? 'In stock' : 'Special order')}</span>
      </label>`).join('');

    // Details table
    const rows = [
      ['Formats', displayOrder.filter((f) => book.price[f] != null).map((f) => FORMATS[f]).join(', ')],
      ['ISBN-13', book.isbn],
      ['Pages', book.pages],
      ['Publisher', book.publisher],
      ['Published', longDate(book.published)],
      ['Genre', g.name],
      ['Language', 'English']
    ];
    $('[data-details]', root).innerHTML = rows.map(([k, v]) => `<tr><th scope="row">${k}</th><td>${esc(v)}</td></tr>`).join('');

    // Bookseller's note
    const note = $('[data-pick]', root);
    if (note) {
      note.hidden = !book.pick;
      if (book.pick) {
        const who = STAFF[book.pick.by];
        $('[data-pick-note]', note).innerHTML = `\u201c${book.pick.note}\u201d`;
        $('[data-pick-by]', note).textContent = `— ${who.name.split(' ')[0]}, ${who.role.toLowerCase()}`;
      }
    }

    // Reviews
    const spread = ratingSpread(book);
    $('[data-review-bars]', root).innerHTML = spread.map((n, i) => `
      <li><span class="bar-label">${5 - i} star</span><span class="bar"><span style="width:${book.reviews ? (n / book.reviews) * 100 : 0}%"></span></span><span class="bar-count">${n}</span></li>`).join('');
    $('[data-review-list]', root).innerHTML = reviewsFor(book).map((r) => `
      <li class="review">
        <div class="review-head">
          <span class="avatar" aria-hidden="true">${initials(r.name)}</span>
          <div><p class="review-name">${esc(r.name)}</p><p class="review-date">${longDate(r.date)}</p></div>
          ${starsHTML(r.rating)}
        </div>
        <h3 class="review-title">${esc(r.title)}</h3>
        <p class="review-text">${esc(r.text)}</p>
      </li>`).join('');

    // Related shelf
    const related = $('[data-related]', root);
    if (related) related.innerHTML = relatedFor(book).map(shelfItemHTML).join('');

    // Breadcrumb
    const crumb = $('[data-b="crumb"]', root);
    if (crumb) crumb.textContent = book.title;
  }

  function initBookPage() {
    const root = $('[data-book-page]');
    if (!root) return;
    const params = new URLSearchParams(location.search);
    const requested = bookById(params.get('id'));
    const book = requested || bookById(root.dataset.bookPage);
    if (!book) return;
    if (book.id !== root.dataset.bookPage) renderBook(root, book);

    const priceEl = $('[data-b="price"]', root);
    const stockEl = $('[data-b="stock"]', root);
    const qty = $('[data-qty-input]', root);
    const selected = () => { const r = $('input[name="format"]:checked', root); return r ? r.value : defaultFormat(book); };
    const update = () => {
      const f = selected();
      if (priceEl) priceEl.textContent = money(book.price[f]);
      if (stockEl) {
        stockEl.className = 'stock-line ' + (f === 'eb' || book.stock ? 'is-in' : 'is-out');
        stockEl.textContent = f === 'eb'
          ? 'Ebook: download link emailed straight after checkout.'
          : (book.stock ? 'In stock at Wren Street. Collect today, or delivered in 2–3 days.' : 'Special order: with you in 5–7 days.');
      }
    };
    root.addEventListener('change', (e) => { if (e.target.name === 'format') update(); });
    update();

    $$('[data-qty-step]', root).forEach((btn) => btn.addEventListener('click', () => {
      const n = Math.min(10, Math.max(1, (Number(qty.value) || 1) + Number(btn.dataset.qtyStep)));
      qty.value = n;
    }));
    if (qty) qty.addEventListener('change', () => { qty.value = Math.min(10, Math.max(1, Math.round(Number(qty.value) || 1))); });

    const add = $('[data-add-detail]', root);
    if (add) add.addEventListener('click', () => {
      const f = selected();
      const n = Math.min(10, Math.max(1, Number(qty.value) || 1));
      Cart.add(book.id, f, n);
      announce(`${n} ${n === 1 ? 'copy' : 'copies'} of ${book.title}, ${FORMATS[f].toLowerCase()}, added to your basket.`);
      add.classList.add('is-added');
      window.setTimeout(() => add.classList.remove('is-added'), 1400);
      openBasket();
    });
  }

  /* ==========================================================================
     8. FORMS (newsletter, seat reservations): validate, then show success
     ========================================================================== */
  function initForms() {
    $$('form[data-validate]').forEach((form) => {
      form.setAttribute('novalidate', '');
      const success = $('[data-success]', form);
      const fields = $$('input, select, textarea', form).filter((f) => f.required || f.type === 'email');

      const check = (field) => {
        const err = $(`[data-error-for="${field.id}"]`, form);
        let msg = '';
        if (field.validity.valueMissing) msg = field.dataset.msgRequired || 'Please fill in this field.';
        else if (field.validity.typeMismatch) msg = 'Please enter a valid email address.';
        field.classList.toggle('is-invalid', !!msg);
        field.setAttribute('aria-invalid', msg ? 'true' : 'false');
        if (err) err.textContent = msg;
        return !msg;
      };

      fields.forEach((f) => {
        f.addEventListener('blur', () => { if (f.value) check(f); });
        f.addEventListener('input', () => { if (f.classList.contains('is-invalid')) check(f); });
      });

      form.addEventListener('submit', (e) => {
        e.preventDefault();
        const bad = fields.filter((f) => !check(f));
        if (bad.length) { bad[0].focus(); return; }
        if (!success) return;
        const nameField = $('[data-name-field]', form);
        const first = nameField && nameField.value.trim() ? nameField.value.trim().split(' ')[0] : '';
        $$('[data-success-name]', success).forEach((el) => { el.textContent = first ? `, ${first}` : ''; });
        form.classList.add('is-sent');
        success.hidden = false;
        success.focus();
      });
    });

    // The reservation form lives in a modal: reset it each time it opens.
    const modal = $('#reserve');
    if (!modal) return;
    modal.addEventListener('show.bs.modal', (e) => {
      const trigger = e.relatedTarget;
      const form = $('form', modal);
      form.reset();
      form.classList.remove('is-sent');
      $$('.is-invalid', form).forEach((f) => f.classList.remove('is-invalid'));
      $$('[data-error-for]', form).forEach((f) => { f.textContent = ''; });
      $('[data-success]', form).hidden = true;
      if (trigger) {
        $$('[data-reserve-event]', modal).forEach((el) => { el.textContent = trigger.dataset.event || ''; });
        $$('[data-reserve-when]', modal).forEach((el) => { el.textContent = trigger.dataset.when || ''; });
        const hidden = $('input[name="event"]', form);
        if (hidden) hidden.value = trigger.dataset.event || '';
      }
    });
  }

  /* ==========================================================================
     9. OPENING HOURS: highlight today and show open/closed
     ========================================================================== */
  function initHours() {
    const table = $('[data-hours]');
    if (!table) return;
    const now = new Date();
    const day = now.getDay();
    const hour = now.getHours() + now.getMinutes() / 60;
    const fmt = (h) => { const hh = Math.floor(h); const suffix = hh >= 12 ? 'pm' : 'am'; return `${((hh + 11) % 12) + 1}${suffix}`; };
    const rows = $$('tr[data-day]', table);
    const rowFor = (d) => rows.find((r) => r.dataset.day.split(',').includes(String(d)));
    const today = rowFor(day);
    if (today) {
      today.classList.add('is-today');
      today.setAttribute('aria-current', 'date');
    }
    const status = $('[data-open-status]');
    if (!status || !today) return;
    const open = Number(today.dataset.open);
    const close = Number(today.dataset.close);
    let text;
    let isOpen = false;
    if (hour >= open && hour < close) { isOpen = true; text = `Open now, until ${fmt(close)}`; }
    else if (hour < open) text = `Closed now, opens at ${fmt(open)} today`;
    else { const tmr = rowFor((day + 1) % 7); text = `Closed now, opens at ${fmt(Number(tmr.dataset.open))} tomorrow`; }
    $$('[data-open-status]').forEach((el) => {
      el.textContent = text;
      el.classList.toggle('is-open', isOpen);
      el.classList.toggle('is-closed', !isOpen);
    });
  }

  function initYear() {
    $$('[data-year]').forEach((el) => { el.textContent = new Date().getFullYear(); });
  }

  /* ==========================================================================
     10. BOOT
     ========================================================================== */
  function init() {
    initCovers();
    initHeader();
    initAnchors();
    initCart();
    initShop();
    initBookPage();
    initShelves();
    initForms();
    initHours();
    initSpines();
    initYear();
    initReveal();
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', init);
  else init();
})();
