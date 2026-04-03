/**
 * トップページ（index.html）用のマップ初期化・ピン表示用JavaScript
 * 
 * 【解説】
 * Leaflet.jsを利用して地図を描画し、APIから取得した山の情報（登頂済み/未登頂）を
 * 地図上に赤い/青いピンとして表示します。
 */
document.addEventListener('DOMContentLoaded', function() {
    // 【解説: 変数の初期化】
    // index.html側で定義した mapConfig オブジェクトから翻訳メッセージを受け取ります。
    // 定義されていない場合のフォールバック（予備）テキストも用意しておきます。
    const msgClimbed = (typeof mapConfig !== 'undefined' && mapConfig.msgClimbed) ? mapConfig.msgClimbed : 'Climbed';
    const msgUnclimbed = (typeof mapConfig !== 'undefined' && mapConfig.msgUnclimbed) ? mapConfig.msgUnclimbed : 'Unclimbed';
    const msgClimbedPopup = (typeof mapConfig !== 'undefined' && mapConfig.msgClimbedPopup) ? mapConfig.msgClimbedPopup : 'Climbed:';
    const msgDetail = (typeof mapConfig !== 'undefined' && mapConfig.msgDetail) ? mapConfig.msgDetail : 'View Details';

    // マップを表示するHTML要素が存在するかチェック
    if (!document.getElementById('map')) {
        return; // 要素がなければこのスクリプトは終了
    }

    /**
     * 【解説: 初期マップの表示設定】
     * 日本の概ね中心([緯度, 経度])を指定し、ズームレベル5で表示します。
     * タイル（地図画像のデータ元）はOpenStreetMapを利用します。
     */
    const map = L.map('map').setView([36.2048, 138.2529], 5);

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);

    /**
     * 【解説: 凡例（Legend）の作成】
     * 地図の右下に「赤＝登頂済み、青＝未登頂」を示すボックスを追加します。
     */
    const legend = L.control({ position: 'bottomright' });

    legend.onAdd = function(map) {
        // div要素を新規作成し、クラスやスタイルを適用
        const div = L.DomUtil.create('div', 'info legend');
        div.style.backgroundColor = 'white';
        div.style.padding = '10px';
        div.style.borderRadius = '5px';
        div.style.boxShadow = '0 0 15px rgba(0,0,0,0.2)';
        div.style.fontSize = '14px';

        // 凡例の中身(HTML)を定義。Thymeleaf用の変数を埋め込んでいました。
        div.innerHTML = `
            <div style="display: flex; align-items: center; margin-bottom: 5px;">
                <div style="background-color: #d9534f; width: 16px; height: 16px; border-radius: 50%; border: 2px solid white; box-shadow: 0 1px 3px rgba(0,0,0,0.3); margin-right: 8px;"></div>
                <span>${msgClimbed}</span>
            </div>
            <div style="display: flex; align-items: center;">
                <div style="background-color: #428bca; width: 12px; height: 12px; border-radius: 50%; border: 2px solid white; box-shadow: 0 1px 3px rgba(0,0,0,0.3); margin-right: 10px; margin-left: 2px;"></div>
                <span>${msgUnclimbed}</span>
            </div>
        `;
        return div;
    };

    legend.addTo(map);

    /**
     * 【解説: API通信とピンの描画】
     * 非同期で山データを取得・解析し、地図上にマーカーアイコンを立てます。
     */
    fetch('/api/map/mountains')
        .then(response => response.json())
        .then(data => {
            // 取得した山ごとのループ処理
            data.forEach(mountain => {
                if (mountain.lat && mountain.lng) {
                    
                    let markerIcon;

                    // 登頂済み(isClimbed)か否かでアイコンの色とサイズを変える
                    if (mountain.isClimbed) {
                        // 登頂済み： 赤色、やや大きめの円形ピン
                        markerIcon = L.divIcon({
                            className: 'custom-div-icon',
                            html: '<div style="background-color: #d9534f; width: 24px; height: 24px; border-radius: 50%; border: 2px solid white; box-shadow: 0 2px 4px rgba(0,0,0,0.3);"></div>',
                            iconSize: [24, 24],
                            iconAnchor: [12, 12]
                        });
                    } else {
                        // 未登頂： 青色、標準サイズの円形ピン
                        markerIcon = L.divIcon({
                            className: 'custom-div-icon',
                            html: '<div style="background-color: #428bca; width: 16px; height: 16px; border-radius: 50%; border: 2px solid white; box-shadow: 0 2px 4px rgba(0,0,0,0.3);"></div>',
                            iconSize: [16, 16],
                            iconAnchor: [8, 8]
                        });
                    }

                    // 算出したアイコンを使って地図上にマーカーを設置
                    const marker = L.marker([mountain.lat, mountain.lng], { icon: markerIcon }).addTo(map);
                    
                    /**
                     * ポップアップの表示処理
                     * マーカーをクリックした時に表示される吹き出しの内容を作成
                     */
                    let popupContent = `
                        <div style="text-align: center;">
                            <h6 style="margin-bottom: 4px; font-weight: bold;">${mountain.name}</h6>
                            <p style="margin-bottom: 8px; font-size: 0.9rem;">${mountain.elevation} m</p>
                    `;
                    
                    if (mountain.isClimbed && mountain.lastClimbDate) {
                        popupContent += `<span class="badge bg-success mb-2">${msgClimbedPopup} ${mountain.lastClimbDate}</span><br>`;
                    }

                    popupContent += `
                            <a href="/mountains/${mountain.id}" class="btn btn-sm btn-outline-primary mt-1" style="font-size: 0.8rem;">${msgDetail}</a>
                        </div>
                    `;

                    marker.bindPopup(popupContent);
                }
            });
        })
        .catch(error => console.error('Error fetching mountain data:', error));
});
