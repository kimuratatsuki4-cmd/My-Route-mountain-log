/**
 * 山の詳細ページ用の「現在の山の天気」を取得・表示するJavaScript
 * 
 * 【解説】
 * プレミアム会員向けに、OpenWeatherMap等のAPIを通じて現在の天気を表示します。
 * ページ読み込み完了時(DOMContentLoaded)に非同期通信(Fetch API)を実行します。
 */
document.addEventListener("DOMContentLoaded", function() {
    // 天気情報を表示するコンテナ（プレミアム会員のみHTMLに存在する）
    const container = document.getElementById("premiumWeatherContainer");
    
    // コンテナが存在する場合のみ処理を実行
    if (container) {
        /**
         * 【解説: データ属性(dataset)の利用】
         * Thymeleafから動的な値(mountainId)を外部JSに渡すため、
         * 今回はHTML側に data-mountain-id 属性を付与し、そこから値を取得する設計にします。
         */
        const mountainId = container.dataset.mountainId;
        
        if (!mountainId) {
            console.error("Mountain ID is missing for weather fetch.");
            return;
        }

        // 独自の天気取得APIを呼び出し（非同期通信）
        fetch(`/api/premium/weather/${mountainId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error("API Network response was not ok: " + response.statusText);
                }
                return response.json(); // レスポンスをJSONとしてパース
            })
            .then(data => {
                // ローディング表示を隠し、コンテンツ領域を表示
                const loadingEl = document.getElementById("weatherLoading");
                const contentEl = document.getElementById("weatherContent");
                if (loadingEl) loadingEl.classList.add("d-none");
                if (contentEl) contentEl.classList.remove("d-none");
                
                // 天気情報が配列で返ってきた場合（OpenWeatherMapの仕様）
                if (data.weather && data.weather.length > 0) {
                    const w = data.weather[0];
                    const wDescEl = document.getElementById("wDesc");
                    const wIconEl = document.getElementById("wIcon");
                    if (wDescEl) wDescEl.textContent = w.description;
                    if (wIconEl) wIconEl.src = `https://openweathermap.org/img/wn/${w.icon}@2x.png`; // HTTPをHTTPSに変更推奨
                }
                
                // 気温や湿度などのメイン情報
                if (data.main) {
                    const tempEl = document.getElementById("wTemp");
                    const feelsLikeEl = document.getElementById("wFeelsLike");
                    const humidityEl = document.getElementById("wHumidity");
                    
                    // 【解説: 四捨五入のテクニック】
                    // Math.round(value * 10) / 10 は、小数点第一位までに丸める一般的な方法です。
                    if (tempEl) tempEl.textContent = Math.round(data.main.temp * 10) / 10;
                    if (feelsLikeEl) feelsLikeEl.textContent = Math.round(data.main.feels_like * 10) / 10;
                    if (humidityEl) humidityEl.textContent = data.main.humidity;
                }
                
                // 風速情報
                if (data.wind) {
                    const windSpeedEl = document.getElementById("wWindSpeed");
                    if (windSpeedEl) windSpeedEl.textContent = data.wind.speed;
                }
            })
            .catch(error => {
                // エラー発生時の処理
                console.error("Error fetching weather:", error);
                const loadingEl = document.getElementById("weatherLoading");
                const errorEl = document.getElementById("weatherError");
                
                if (loadingEl) loadingEl.classList.add("d-none");
                if (errorEl) errorEl.classList.remove("d-none");
            });
    }
});
