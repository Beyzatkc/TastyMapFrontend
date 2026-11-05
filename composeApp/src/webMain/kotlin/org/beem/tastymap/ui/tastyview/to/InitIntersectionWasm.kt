package org.beem.tastymap.ui.tastyview.to

fun initIntersectionObserverWasm(triggerId: String, topBarId: String) {
    js(
        """
    var triggerElement = document.getElementById(triggerId);
    var topBar = document.getElementById(topBarId);

    if (triggerElement && topBar) {
        var options = { root: null, rootMargin: '0px', threshold: 0.0 };
        
        var observer = new IntersectionObserver(function(entries) {
            var entry = entries[0];
            
            // 🎯 ESNEK NEŞTER: Çizgi ekrandan çıktığı an direkt üst barı patlat!
            // Artık ekranın en tepesini (< 0) kontrol etmeye kasıp bottom sheet'e takılmıyoruz.
            if (!entry.isIntersecting) {
                topBar.style.opacity = "1";
                topBar.style.visibility = "visible";
                topBar.style.transform = "translateY(0)";
                topBar.style.pointerEvents = "auto";
            } else {
                topBar.style.opacity = "0";
                topBar.style.transform = "translateY(-10px)";
                topBar.style.visibility = "hidden";
                topBar.style.pointerEvents = "none";
            }
        }, options);
        
        observer.observe(triggerElement);
    }
""")
}