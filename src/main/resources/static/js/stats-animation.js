/**
 * リッチなカウントアップアニメーションを実行する関数
 * @param {HTMLElement} obj - 数字を表示するHTML要素
 * @param {number} target - 最終的な目標の数値
 * @param {number} duration - アニメーションにかける時間（ミリ秒）
 * @param {boolean} isFloat - 小数かどうか
 */
function animateValue(obj, target, duration, isFloat = false) {
    let startTime = null;

    // requestAnimationFrame：ブラウザの描画更新のタイミングに合わせて実行される
    // setIntervalを使うよりもパフォーマンスが高く、滑らかなアニメーションを実現します
    function step(timestamp) {
        if (!startTime) startTime = timestamp;
        // 進行度を0〜1の間で計算
        const progress = Math.min((timestamp - startTime) / duration, 1);
        
        // イージング（徐々にスピードが落ちる演出）を適用し、よりプロフェッショナルな動きに
        const easeOutQuad = progress * (2 - progress);
        
        // 値を計算
        const currentValue = easeOutQuad * target;
        
        // 桁区切りのカンマを追加して画面に描画
        if (isFloat) {
            obj.innerHTML = currentValue.toLocaleString(undefined, { minimumFractionDigits: 1, maximumFractionDigits: 1 });
        } else {
            obj.innerHTML = Math.floor(currentValue).toLocaleString(); 
        }
        
        if (progress < 1) {
            window.requestAnimationFrame(step); // まだ途中なら次の描画タイミングで再帰呼び出し
        } else {
            // 最後に正確な目標値をセット
            if (isFloat) {
                obj.innerHTML = target.toLocaleString(undefined, { minimumFractionDigits: 1, maximumFractionDigits: 1 });
            } else {
                obj.innerHTML = target.toLocaleString();
            }
        }
    }
    
    // アニメーションを開始
    window.requestAnimationFrame(step);
}

// DOMの読み込みが完了した直後にアニメーションを発火させる
document.addEventListener('DOMContentLoaded', () => {
    // 画面に表示された際にアニメーションを発火させるためのIntersectionObserver
    const observerOptions = {
        root: null,
        rootMargin: '0px',
        threshold: 0.1
    };

    const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const el = entry.target;
                const targetValue = parseFloat(el.getAttribute("data-target"));
                const isFloat = el.getAttribute("data-target").includes('.');
                
                if(!isNaN(targetValue)) {
                    animateValue(el, targetValue, 2000, isFloat); 
                }
                observer.unobserve(el); // 一度アニメーションしたら監視を解除
            }
        });
    }, observerOptions);

    document.querySelectorAll('.count-up').forEach(el => {
        observer.observe(el);
    });
});
