/**
 * 山マスタ検索用の共通JavaScriptファイル
 *
 * 【解説】
 * このスクリプトは、ユーザーが入力したキーワードに基づいて山を検索し、
 * その結果をフォームに自動入力するための処理をまとめています。
 * DOMContentLoadedイベントを使用することで、HTMLの読み込みが完了した後に
 * スクリプトが実行されることを保証しています。
 */
document.addEventListener("DOMContentLoaded", function () {
  // 各要素の取得
  const searchInput = document.getElementById("mountainSearchInput");
  const searchResults = document.getElementById("mountainSearchResults");
  const mountainIdField = document.getElementById("mountainId");
  const selectedBadge = document.getElementById("selectedMountainBadge");
  const selectedName = document.getElementById("selectedMountainName");
  const selectedDetail = document.getElementById("selectedMountainDetail");
  const clearBtn = document.getElementById("clearMountainBtn");

  // 自動入力対象のフォームフィールド
  const distanceField = document.getElementById("distanceKm");
  const durationField = document.getElementById("durationMinutes");
  const elevGainField = document.getElementById("elevationGain");
  const maxElevField = document.getElementById("maxElevation");
  const locationField = document.getElementById("location");
  const titleField = document.getElementById("title");

  /**
   * 【解説: 定義済み変数の利用】
   * Thymeleafから渡された設定情報(mountainSearchConfig)を参照します。
   * これにより、多言語対応(i18n)のメッセージを外部のJavaScriptでも利用できます。
   */
  const notFoundMessage =
    typeof mountainSearchConfig !== "undefined" &&
    mountainSearchConfig.notFoundMessage
      ? mountainSearchConfig.notFoundMessage
      : "No mountains found";

  /**
   * 【解説: 編集画面表示時の初期データ取得 (fetch API)】
   * すでにmountainIdが設定されている場合(編集時など)は、APIから山情報を取得して表示します。
   * fetch()は非同期（裏側で通信を行う）でデータを取得するためのモダンなJavaScript手法です。
   * .then() で通信成功時の処理をつなげ、.catch() でエラー時の処理を記述します。
   */
  const existingMountainId = mountainIdField ? mountainIdField.value : "";
  if (existingMountainId) {
    fetch("/api/mountains/" + existingMountainId)
      .then((response) => response.json()) // レスポンスをJSONとして解釈
      .then((mountain) => {
        selectedName.textContent =
          mountain.name + " (" + mountain.elevation + "m)";
        selectedDetail.textContent =
          (mountain.prefecture || "") +
          " | " +
          (mountain.typicalDistanceKm || "-") +
          "km | " +
          (mountain.difficulty || "-");
        selectedBadge.style.display = "flex";
      })
      .catch((err) => console.error("Failed to load mountain info:", err));
  }

  /**
   * 【解説: デバウンス (Debounce) について】
   * ユーザーが文字を入力するたびにAPIを呼び出すと、サーバーに負荷がかかります。
   * そこで、入力が一定時間(ここでは300ミリ秒)停止した時のみリクエストを送るように制御します。
   * これによってパフォーマンスとサーバー負荷を最適化します。
   */
  let debounceTimer;

  // === キー入力イベント ===
  if (searchInput) {
    searchInput.addEventListener("input", function () {
      const keyword = this.value.trim();

      // タイマーをリセット（前の入力のリクエストをキャンセル）
      clearTimeout(debounceTimer);

      // 2文字未満なら検索結果を非表示
      if (keyword.length < 2) {
        searchResults.classList.remove("show");
        searchResults.innerHTML = "";
        return;
      }

      // 300msのデバウンス: 入力が止まってからリクエスト
      debounceTimer = setTimeout(() => {
        searchMountains(keyword);
      }, 300);
    });
  }

  /**
   * APIに山を検索し、結果を表示する関数
   */
  function searchMountains(keyword) {
    // ローディング表示
    searchResults.innerHTML =
      '<div class="mountain-search-loading"><i class="bi bi-hourglass-split me-1"></i>Searching...</div>';
    searchResults.classList.add("show");

    // GETリクエストで検索APIを呼び出す
    fetch("/api/mountains/search?keyword=" + encodeURIComponent(keyword))
      .then((response) => response.json())
      .then((data) => {
        if (data.length === 0) {
          searchResults.innerHTML =
            '<div class="mountain-search-loading">' +
            notFoundMessage +
            "</div>";
          return;
        }

        // 検索結果のHTML生成
        let html = "";
        data.forEach((mountain) => {
          const hours = Math.floor(mountain.typicalDurationMinutes / 60);
          const mins = mountain.typicalDurationMinutes % 60;
          const timeStr = hours > 0 ? hours + "h " + mins + "m" : mins + "m";

          html +=
            '<div class="mountain-search-item" data-mountain-id="' +
            mountain.mountainId +
            '"' +
            ' data-name="' +
            mountain.name +
            '"' +
            ' data-elevation="' +
            mountain.elevation +
            '"' +
            ' data-prefecture="' +
            (mountain.prefecture || "") +
            '"' +
            ' data-distance="' +
            (mountain.typicalDistanceKm || "") +
            '"' +
            ' data-duration="' +
            (mountain.typicalDurationMinutes || "") +
            '"' +
            ' data-elev-gain="' +
            (mountain.typicalElevationGain || "") +
            '"' +
            ' data-difficulty="' +
            (mountain.difficulty || "") +
            '">' +
            '<div class="mountain-name">' +
            mountain.name +
            ' <span style="font-size: 0.75rem; color: var(--color-text-muted);">(' +
            mountain.elevation +
            "m)</span></div>" +
            '<div class="mountain-info">' +
            '<i class="bi bi-geo-alt me-1"></i>' +
            (mountain.prefecture || "-") +
            ' &nbsp;|&nbsp; <i class="bi bi-rulers me-1"></i>' +
            (mountain.typicalDistanceKm || "-") +
            "km" +
            ' &nbsp;|&nbsp; <i class="bi bi-clock me-1"></i>' +
            timeStr +
            " &nbsp;|&nbsp; " +
            (mountain.difficulty || "-") +
            "</div></div>";
        });
        searchResults.innerHTML = html;

        // 各結果にクリックイベントを設定
        document.querySelectorAll(".mountain-search-item").forEach((item) => {
          item.addEventListener("click", function () {
            selectMountain(this);
          });
        });
      })
      .catch((error) => {
        console.error("Mountain search error:", error);
        searchResults.innerHTML =
          '<div class="mountain-search-loading">Error occurred</div>';
      });
  }

  /**
   * 山を選択した時の処理
   */
  function selectMountain(element) {
    // datasetプロパティを使って、data-* 属性で設定された値を取得
    const id = element.dataset.mountainId;
    const name = element.dataset.name;
    const elevation = element.dataset.elevation;
    const prefecture = element.dataset.prefecture;
    const distance = element.dataset.distance;
    const duration = element.dataset.duration;
    const elevGain = element.dataset.elevGain;
    const difficulty = element.dataset.difficulty;

    // hiddenフィールドにID設定
    if (mountainIdField) mountainIdField.value = id;

    // 【解説: DOM操作とクラスの付与】
    // classList.add()を利用して、入力された項目に一次的なハイライト(CSS)を付与します。
    if (distance && distance !== "null" && distanceField) {
      distanceField.value = distance;
      distanceField.classList.add("auto-filled");
    }
    if (duration && duration !== "null" && durationField) {
      durationField.value = duration;
      durationField.classList.add("auto-filled");
    }
    if (elevGain && elevGain !== "null" && elevGainField) {
      elevGainField.value = elevGain;
      elevGainField.classList.add("auto-filled");
    }
    if (elevation && elevation !== "null" && maxElevField) {
      maxElevField.value = elevation;
      maxElevField.classList.add("auto-filled");
    }
    if (prefecture && locationField) {
      locationField.value = prefecture;
      locationField.classList.add("auto-filled");
    }

    // タイトルが空なら山名をセット
    if (titleField && !titleField.value.trim()) {
      titleField.value = name;
      titleField.classList.add("auto-filled");
    }

    // 選択バッジを表示
    if (selectedName && selectedDetail && selectedBadge) {
      selectedName.textContent = name + " (" + elevation + "m)";
      selectedDetail.textContent =
        prefecture + " | " + (distance || "-") + "km | " + difficulty;
      selectedBadge.style.display = "flex";
    }

    // 検索欄をクリア
    if (searchInput) searchInput.value = "";
    if (searchResults) {
      searchResults.classList.remove("show");
      searchResults.innerHTML = "";
    }

    /**
     * 【解説: setTimeoutによる遅延処理】
     * 2000ミリ秒(2秒)後に無名関数(アロー関数)を実行し、
     * ハイライト用に追加した 'auto-filled' クラスを削除します。
     */
    setTimeout(() => {
      document.querySelectorAll(".auto-filled").forEach((el) => {
        el.classList.remove("auto-filled");
      });
    }, 2000);
  }

  /*
   * 「×」ボタンで選択解除
   */
  if (clearBtn) {
    clearBtn.addEventListener("click", function () {
      if (mountainIdField) mountainIdField.value = "";
      if (distanceField) distanceField.value = "";
      if (durationField) durationField.value = "";
      if (elevGainField) elevGainField.value = "";
      if (maxElevField) maxElevField.value = "";
      if (locationField) locationField.value = "";
      if (selectedBadge) selectedBadge.style.display = "none";
    });
  }

  /**
   * 【解説: イベントのバブリングを利用した外側クリック検知】
   * 画面全体のどこがクリックされたかを監視(document全体へイベントリスナーを登録)し、
   * クリックされた場所(e.target)が検索入力枠や結果一覧の中でなければ、結果ドロップダウンを閉じます。
   */
  document.addEventListener("click", function (e) {
    if (
      searchInput &&
      searchResults &&
      !searchInput.contains(e.target) &&
      !searchResults.contains(e.target)
    ) {
      searchResults.classList.remove("show");
    }
  });
});
