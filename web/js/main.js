// =============================================
// LANDING PAGE — Smooth scroll + Nav highlight + Tim carousel
// =============================================

// Smooth scroll untuk nav-scroll links
document.querySelectorAll('.nav-scroll').forEach(link => {
    link.addEventListener('click', function (e) {
        e.preventDefault();
        const targetId = this.getAttribute('href');
        const target = document.querySelector(targetId);
        if (!target) return;

        const navHeight = document.getElementById('mainNavbar')?.offsetHeight || 68;
        const top = target.getBoundingClientRect().top + window.scrollY - navHeight;

        window.scrollTo({ top, behavior: 'smooth' });

        // Tutup mobile menu jika terbuka
        const navbarCollapse = document.getElementById('navbarMenu');
        if (navbarCollapse && navbarCollapse.classList.contains('show')) {
            const bsCollapse = bootstrap.Collapse.getInstance(navbarCollapse);
            if (bsCollapse) bsCollapse.hide();
        }
    });
});

// Highlight nav link saat scroll (active state)
const sections = document.querySelectorAll('section[id]');
const navLinks = document.querySelectorAll('.nav-scroll');

window.addEventListener('scroll', () => {
    const navHeight = document.getElementById('mainNavbar')?.offsetHeight || 68;
    let current = '';

    sections.forEach(section => {
        const sectionTop = section.offsetTop - navHeight - 20;
        if (window.scrollY >= sectionTop) {
            current = section.getAttribute('id');
        }
    });

    navLinks.forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('href') === `#${current}`) {
            link.classList.add('active');
        }
    });
});

// =============================================
// TIM CAROUSEL — 3-card overlap (5-1-2, 1-2-3, …)
// =============================================
(function () {
    const track = document.getElementById('timTrack');
    const dotsWrap = document.getElementById('timDots');
    const btnPrev = document.getElementById('timPrev');
    const btnNext = document.getElementById('timNext');

    if (!track) return;

    const slides = Array.from(track.querySelectorAll('.tim-slide'));
    const total = slides.length;
    if (total === 0) return;

    let current = 0;

    // Buat dots
    slides.forEach((_, i) => {
        const dot = document.createElement('div');
        dot.classList.add('tim-dot');
        dot.addEventListener('click', () => goTo(i));
        dotsWrap.appendChild(dot);
    });

    function positionSlides() {
        const wrapW = track.offsetWidth;
        const cardW = slides[0].offsetWidth;
        const gap = cardW * 0.74;

        const centerX = wrapW / 2 - cardW / 2;
        const leftX = centerX - gap;
        const rightX = centerX + gap;

        const leftIdx = (current - 1 + total) % total;
        const rightIdx = (current + 1) % total;

        slides.forEach((slide, i) => {
            slide.className = 'tim-slide pos-hidden';

            if (i === current) {
                slide.classList.replace('pos-hidden', 'pos-center');
                slide.style.left = centerX + 'px';
            } else if (i === leftIdx) {
                slide.classList.replace('pos-hidden', 'pos-left');
                slide.style.left = leftX + 'px';
            } else if (i === rightIdx) {
                slide.classList.replace('pos-hidden', 'pos-right');
                slide.style.left = rightX + 'px';
            } else {
                if (i === (leftIdx - 1 + total) % total) {
                    slide.style.left = (leftX - gap) + 'px';
                } else {
                    slide.style.left = (rightX + gap) + 'px';
                }
            }
        });

        // Update dots
        document.querySelectorAll('.tim-dot').forEach((dot, i) => {
            dot.classList.toggle('active', i === current);
        });
    }

    function goTo(index) {
        current = (index + total) % total;
        positionSlides();
    }

    btnPrev.addEventListener('click', () => goTo(current - 1));
    btnNext.addEventListener('click', () => goTo(current + 1));

    // Swipe (mobile)
    let startX = 0;
    track.addEventListener('touchstart', e => { startX = e.touches[0].clientX; }, { passive: true });
    track.addEventListener('touchend', e => {
        const diff = startX - e.changedTouches[0].clientX;
        if (Math.abs(diff) > 40) goTo(current + (diff > 0 ? 1 : -1));
    });

    // Auto-play
    let autoplay = setInterval(() => goTo(current + 1), 4000);
    track.parentElement.addEventListener('mouseenter', () => clearInterval(autoplay));
    track.parentElement.addEventListener('mouseleave', () => {
        autoplay = setInterval(() => goTo(current + 1), 4000);
    });

    // Init & resize
    positionSlides();
    window.addEventListener('resize', positionSlides);
})();
