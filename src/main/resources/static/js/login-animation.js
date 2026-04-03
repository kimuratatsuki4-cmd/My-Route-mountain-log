/**
 * 初回ログイン時の足跡アニメーションを制御するスクリプト。
 * sessionStorageを使用して、セッション内で初めてホームページにアクセスした際のみアニメーションを表示します。
 */
document.addEventListener('DOMContentLoaded', () => {
    const overlay = document.getElementById('login-animation-overlay');

    if (!overlay) return;

    // 常にアニメーションを実行する
    
    // 足跡（.footprint）の要素ごとに、少しずつ遅延させて表示させるためのクラスを付与
    const footprints = overlay.querySelectorAll('.footprint');
    footprints.forEach((fp, index) => {
        // アニメーションの遅延を設定
        fp.style.animationDelay = `${(index * 0.4) + 0.2}s`;
        // アニメーション用クラスを付与して開始
        fp.classList.add('animate');
    });

    // 全てのアニメーションが終わる頃合いでオーバーレイを消す
    const totalDuration = (footprints.length * 1.0) + 1.0; 
    
    setTimeout(() => {
        overlay.classList.add('fade-out');
        
        // フェードアウトアニメーション完了後にDOMから非表示にする
        setTimeout(() => {
            overlay.style.display = 'none';
        }, 1500);

    }, totalDuration * 1000);
});
