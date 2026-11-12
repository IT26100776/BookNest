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

  const FORMATS = {
    hb: 'Hardback',
    pb: 'Book',
    eb: 'Ebook'
};

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

  let BOOKS = [];
	
	
  // ==========================================
  // BOOKNEST BACKEND INTEGRATION
  // ==========================================

  const API_URL = '/api/books';

  // Generate consistent cover colours for each book
  function generateBookCover(book) {

    const palettes = [
      { bg: '#1f3a4d', fg: '#f4ead5', ac: '#e3aa3f' },
      { bg: '#7b1e2c', fg: '#f4ead5', ac: '#f3d98b' },
      { bg: '#1f4e5a', fg: '#f6efe1', ac: '#cfe3d4' },
      { bg: '#243b6b', fg: '#f6efe1', ac: '#e8b4a6' },
      { bg: '#eadcc2', fg: '#2a211c', ac: '#c4553a' },
      { bg: '#2f4a3a', fg: '#f1e6cf', ac: '#9cc3d5' }
    ];

    const layouts = [
      'arch', 'bigtype', 'orbit', 'sun', 'grid', 'blob'
    ];

    const index = Math.abs(Number(book.id) || 0) % palettes.length;

    return {
      l: layouts[index],
      ...palettes[index],
      f: 'serif'
    };
  }


  // Convert Spring Boot book data into Folio format
  function convertBook(book) {

    const genreText = (book.genre || '').toLowerCase();

    let genre = 'fiction';

    if (genreText.includes('crime') || genreText.includes('mystery')) {
      genre = 'crime';
    } else if (
      genreText.includes('science') ||
      genreText.includes('fantasy') ||
      genreText.includes('programming') ||
      genreText.includes('technology') ||
      genreText.includes('computer') ||
      genreText.includes('it')
    ) {
      genre = 'sff';
    } else if (
      genreText.includes('history') ||
      genreText.includes('biography')
    ) {
      genre = 'history';
    } else if (genreText.includes('poetry')) {
      genre = 'poetry';
    } else if (
      genreText.includes('nature') ||
      genreText.includes('travel')
    ) {
      genre = 'nature';
    } else if (
      genreText.includes('food') ||
      genreText.includes('cook')
    ) {
      genre = 'food';
    } else if (
      genreText.includes('children') ||
      genreText.includes('kids')
    ) {
      genre = 'kids';
    }

    return {

      id: String(book.id),

      title: book.title,
      author: book.author,
      genre: genre,

      // One price per book, using Folio's paperback slot
      price: {
        pb: Number(book.price)
      },

      stock: Number(book.stockQuantity || 0) > 0,

      stockQuantity: Number(book.stockQuantity || 0),

      rating: 0,
      reviews: 0,

      pages: book.pages || 0,
      publisher: book.publisher || 'BookNest',
      published: '2000-01-01',
      isbn: book.isbn || '',

      blurb: book.genre || 'Discover this book at BookNest.',

      synopsis: book.title + ' by ' + book.author +
        '. Available at BookNest.',

      cover: generateBookCover(book)

    };
  }


  // Retrieve actual books from Spring Boot
  async function loadCustomerBooks() {

    try {

      const response = await fetch(API_URL);

      if (!response.ok) {
        throw new Error('Unable to load books from BookNest.');
      }

      const backendBooks = await response.json();

      BOOKS = backendBooks.map(convertBook);

      console.log('BookNest catalogue loaded:', BOOKS);

    } catch (error) {

      console.error('BookNest catalogue error:', error);

      BOOKS = [];

      throw error;

    }
  }
	
	async function loadBookRatings() {
    try {
        const response = await fetch('/api/reviews');
    if (!response.ok) {
        throw new Error('Could not load book ratings.');
    }

    const reviews = await response.json();

    BOOKS.forEach(book => {
        const bookReviews = reviews.filter(
            review =>
                String(review.bookId).toLowerCase() ===
                String(book.id).toLowerCase()
        );

        book.reviews = bookReviews.length;

        book.rating = bookReviews.length
            ? bookReviews.reduce(
                (sum, review) => sum + Number(review.rating), 0
            ) / bookReviews.length
            : 0;
    });

} catch (error) {
    console.error('Book rating loading error:', error);
}
}


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

    const book = bookById(id);

    // Reject unavailable books
    if (!book || !book.stock || book.stockQuantity <= 0) {
        return false;
    }

    const line = this.find(id, format);
    const currentQty = line ? line.qty : 0;
    const newQty = currentQty + qty;

    // Never exceed available inventory
    if (newQty > book.stockQuantity) {
        return false;
    }

    if (line) {
        line.qty = newQty;
    } else {
        this.items.push({
            id,
            format,
            qty
        });
    }

    this.save();
    return true;
},

set(id, format, qty) {

    const line = this.find(id, format);
    const book = bookById(id);

    if (!line || !book) return;

    if (qty <= 0) {
        this.items = this.items.filter(i => i !== line);
    } else {

        // Prevent quantity exceeding available stock
        if (!book.stock || qty > book.stockQuantity) {
            return false;
        }

        line.qty = qty;
    }

    this.save();
    return true;
},

    count() { return this.items.reduce((n, i) => n + i.qty, 0); },
    subtotal() { return this.items.reduce((n, i) => n + bookById(i.id).price[i.format] * i.qty, 0); }
  };
	
	// ==========
// BOOKNEST CHECKOUT INTERFACE
// Allows checkout.html to access the cart
// ==========
window.BookNestCart = {
getItems() {

    return Cart.items.map(item => {

        const book = bookById(item.id);

        if (!book) return null;

        const unitPrice = Number(book.price[item.format]);

        return {
            bookId: Number(book.id),
            title: book.title,
            author: book.author,
            quantity: item.qty,
            unitPrice: unitPrice,
            subtotal: unitPrice * item.qty,
            stockQuantity: book.stockQuantity
        };

    }).filter(Boolean);

},

getSubtotal() {
    return Cart.subtotal();
},

getCount() {
    return Cart.count();
},

clear() {
    Cart.items = [];
    Cart.save();
}
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
      const added = Cart.add(book.id, format, 1);

if (!added) {
    announce(
        book.stockQuantity <= 0
            ? `${book.title} is out of stock.`
            : `Only ${book.stockQuantity} copies of ${book.title} are available.`
    );

    alert(
        book.stockQuantity <= 0
            ? "Sorry, this book is currently out of stock."
            : "Only " + book.stockQuantity + " copies are available."
    );

    return;
}

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
if (checkout) {
    checkout.addEventListener('click', () => {
    if (Cart.count() === 0) {
        if (note) {
            note.hidden = false;
        }
        return;
    }

    window.location.href = 'checkout.html';
});
}
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
  async function loadAndRenderReviews(root, book) {
    const reviewList = $('[data-review-list]', root);
    const reviewBars = $('[data-review-bars]', root);
try {
    const response = await fetch(
        `/api/reviews/book/${encodeURIComponent(book.id)}`
    );

    if (!response.ok) {
        throw new Error('Could not load reviews.');
    }

    const reviews = await response.json();

    const count = reviews.length;
    const average = count
        ? reviews.reduce((sum, review) => sum + Number(review.rating), 0) / count
        : 0;
	// Update the book's actual rating data
book.rating = average;
book.reviews (http://book.reviews/) = count;

    // Update review summary
    const ratingEl = $('[data-b="rating"]', root);
    const countEl = $('[data-b="reviews"]', root);
    const starsEl = $('[data-b="stars"]', root);

    if (ratingEl) ratingEl.textContent = average.toFixed(1);
    if (countEl) countEl.textContent = `${count} reviews`;
    if (starsEl) starsEl.innerHTML = starsHTML(average);

    // Update rating distribution
    if (reviewBars) {
        reviewBars.innerHTML = [5, 4, 3, 2, 1].map(star => {
            const number = reviews.filter(
                review => Number(review.rating) === star
            ).length;

            const percentage = count
                ? (number / count) * 100
                : 0;

            return `
                \<li>
                    \<span class="bar-label">${star} star\</span>
                    \<span class="bar">
                        \<span style="width:${percentage}%">\</span>
                    \</span>
                    \<span class="bar-count">${number}\</span>
                \</li>
            `;
        }).join('');
    }

    // Display reviews belonging to this book
    if (reviewList) {
        if (count === 0) {
            reviewList.innerHTML =
                '\<li class="review">No reviews yet. Be the first to review this book!\</li>';
        } else {
            reviewList.innerHTML = reviews.map(review => `
                \<li class="review">
                    \<div class="review-head">
                        \<span class="avatar" aria-hidden="true">
                            ${esc(initials(review.reviewerName))}
                        \</span>
                        \<div>
                            \<p class="review-name">${esc(review.reviewerName)}\</p>
                            \<p class="review-date">${esc(review.reviewType || 'Public Review')}\</p>
                        \</div>
                        ${starsHTML(Number(review.rating))}
                    \</div>
                    \<p class="review-text">${esc(review.comment)}\</p>
                \</li>
            `).join('');
        }
    }

} catch (error) {
    console.error('Review loading error:', error);

    if (reviewList) {
        reviewList.innerHTML =
            '\<li class="review">Unable to load reviews at the moment.\</li>';
    }
}
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
    async function loadAndRenderReviews(root, book) {
    const reviewList = $('[data-review-list]', root);
    const reviewBars = $('[data-review-bars]', root);
try {
    const response = await fetch(
        `/api/reviews/book/${encodeURIComponent(book.id)}`
    );

    if (!response.ok) {
        throw new Error('Could not load reviews.');
    }

    const reviews = await response.json();

    const count = reviews.length;
    const average = count
        ? reviews.reduce((sum, review) => sum + Number(review.rating), 0) / count
        : 0;

    // Update review summary
    const ratingEl = $('[data-b="rating"]', root);
    const countEl = $('[data-b="reviews"]', root);
    const starsEl = $('[data-b="stars"]', root);

    if (ratingEl) ratingEl.textContent = average.toFixed(1);
    if (countEl) countEl.textContent = `${count} reviews`;
    if (starsEl) starsEl.innerHTML = starsHTML(average);

    // Update rating distribution
    if (reviewBars) {
        reviewBars.innerHTML = [5, 4, 3, 2, 1].map(star => {
            const number = reviews.filter(
                review => Number(review.rating) === star
            ).length;

            const percentage = count
                ? (number / count) * 100
                : 0;

            return `
                \<li>
                    \<span class="bar-label">${star} star\</span>
                    \<span class="bar">
                        \<span style="width:${percentage}%">\</span>
                    \</span>
                    \<span class="bar-count">${number}\</span>
                \</li>
            `;
        }).join('');
    }

    // Display reviews belonging to this book
    if (reviewList) {
        if (count === 0) {
            reviewList.innerHTML =
                '\<li class="review">No reviews yet. Be the first to review this book!\</li>';
        } else {
            reviewList.innerHTML = reviews.map(review => `
                \<li class="review">
                    \<div class="review-head">
                        \<span class="avatar" aria-hidden="true">
                            ${esc(initials(review.reviewerName))}
                        \</span>
                        \<div>
                            \<p class="review-name">${esc(review.reviewerName)}\</p>
                            \<p class="review-date">${esc(review.reviewType || 'Public Review')}\</p>
                        \</div>
                        ${starsHTML(Number(review.rating))}
                    \</div>
                    \<p class="review-text">${esc(review.comment)}\</p>
                \</li>
            `).join('');
        }
    }

} catch (error) {
    console.error('Review loading error:', error);

    if (reviewList) {
        reviewList.innerHTML =
            '\<li class="review">Unable to load reviews at the moment.\</li>';
    }
}
}

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
	  loadAndRenderReviews(root, book);
	  
	  // ==========
// BOOKNEST REVIEW SUBMISSION
// ==========
const reviewForm = document.getElementById('reviewForm');
const reviewMessage = document.getElementById('reviewMessage');
function getReviewCsrfToken() {
    const cookie = document.cookie
        .split('; ')
        .find(row => row.startsWith('XSRF-TOKEN='));
return cookie
    ? decodeURIComponent(cookie.split('=').slice(1).join('='))
    : null;
}
if (reviewForm) {
    reviewForm.addEventListener('submit', async function(event) {
        event.preventDefault();
    const reviewerName =
        document.getElementById('reviewerName').value.trim();

    const rating =
        Number(document.getElementById('reviewRating').value);

    const comment =
        document.getElementById('reviewComment').value.trim();

    if (!reviewerName || !rating || !comment) {
        reviewMessage.textContent = 'Please complete all fields.';
        reviewMessage.className = 'text-danger mb-3';
        return;
    }

    const submitButton = reviewForm.querySelector(
        'button[type="submit"]'
    );

    submitButton.disabled = true;
    submitButton.textContent = 'Submitting...';

    reviewMessage.textContent = '';

    try {
        const csrfToken = getReviewCsrfToken();

        const headers = {
            'Content-Type': 'application/json'
        };

        if (csrfToken) {
            headers['X-XSRF-TOKEN'] = csrfToken;
        }

        const response = await fetch('/api/reviews', {
            method: 'POST',
            headers: headers,
            body: JSON.stringify({
                bookId: String(book.id),
                reviewerName: reviewerName,
                rating: rating,
                comment: comment
            })
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(
                errorText || 'Unable to submit your review.'
            );
        }

        reviewMessage.textContent =
            'Your review has been submitted successfully!';

        reviewMessage.className = 'text-success mb-3';

        reviewForm.reset();

        // Reload reviews and update the rating summary
        await loadAndRenderReviews(root, book);

    } catch (error) {
        console.error('Review submission error:', error);

        reviewMessage.textContent =
            'Failed to submit review. ' + error.message;

        reviewMessage.className = 'text-danger mb-3';

    } finally {
        submitButton.disabled = false;
        submitButton.textContent = 'Submit Review';
    }
});
}

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
      const added = Cart.add(book.id, f, n);

if (!added) {

    alert(
        book.stockQuantity <= 0
            ? "Sorry, this book is currently out of stock."
            : "Only " + book.stockQuantity + " copies are available."
    );

    return;
}

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

  async function startBookNest() {

    try {

        await loadCustomerBooks();
		await loadBookRatings();

        init();

    } catch (error) {

        console.error('BookNest startup failed:', error);

        document.querySelectorAll('[data-book-grid]').forEach(grid => {
            grid.innerHTML =
                '<p class="text-center">Unable to load books. Please try again later.</p>';
        });

    }

}

if (document.readyState === 'loading') {
document.addEventListener('DOMContentLoaded', () => {
    window.BookNestReady = startBookNest();
});
} else {
window.BookNestReady = startBookNest();
}
})();
