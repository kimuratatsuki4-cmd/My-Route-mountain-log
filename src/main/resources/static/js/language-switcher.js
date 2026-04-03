/**
 * アプリケーション全体の言語切り替え用JavaScript
 * 
 * 【解説】
 * thymeleafのfragments/common.htmlから読み込まれ、全ページで動作します。
 * 現在のURLのクエリパラメータ(?lang=xx)を書き換えてリロードすることで、
 * Spring Boot側のLocaleResolverが言語設定を変更します。
 */

/**
 * 言語切替関数
 * @param {string} lang - 切り替え先の言語コード (例: 'en', 'ja')
 */
function changeLang(lang) {
    // 現在のURLを解析するための URL オブジェクトを作成
    const url = new URL(window.location.href);
    
    // URLSearchParams: URLのクエリパラメータ（?key=value）を操作するためのAPI
    // .set() は既に "lang" が存在すれば上書き、なければ追加する
    url.searchParams.set('lang', lang);
    
    // 書き換えたURLに遷移 (ページリロード)
    // hrefを変更することで、ブラウザが新しいURLに遷移（この場合は同じページでパラメータ違い）します。
    window.location.href = url.toString();
}

/**
 * 【解説: グローバル関数の登録】
 * HTMLのインラインイベントハンドラ (例: onclick="changeLang('en')") から
 * 呼び出せるようにするため、関数を window オブジェクト（グローバルスコープ）に
 * 明示的に登録しておきます。（通常はトップレベルの定義で問題ないですが、モジュール化等を見越して）
 */
window.changeLang = changeLang;
