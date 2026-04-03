# API Specification (My Route & Mountain Log)

この仕様書は、`src/main/java/com/example/mountainlog/controller` 内で実装されているすべてのREST API（JSONレスポンスを返すもの）についてまとめたものです。

---

## 1. 地図データ取得API

山の緯度経度などのマップ表示用データを全件取得します。

- **URL:** `/api/map/mountains`
- **メソッド:** `GET`
- **リクエストパラメータ:**
  - なし（認証ヘッダーに基づくユーザー情報より、踏破済のフラグ等が設定される）
- **成功レスポンス (200 OK):**

```json
[
  {
    "mountainId": 1,
    "name": "富士山",
    "latitude": 35.3606,
    "longitude": 138.7274,
    "difficulty": "HARD",
    "elevation": 3776
  },
  ...
]
```

- **エラーコード:**
  - `401 Unauthorized` (アクセス権限がない場合)

---

## 2. 山 検索API

キーワードから山を検索し、一覧を返却します（非同期検索サジェスト用）。

- **URL:** `/api/mountains/search`
- **メソッド:** `GET`
- **リクエストパラメータ:**
  - `keyword` (クエリ / 必須 / String): 検索したいキーワード（山名・読みがな・都道府県など）
- **成功レスポンス (200 OK):**

```json
[
  {
    "id": 1,
    "name": "富士山",
    "elevation": 3776,
    "prefecture": "静岡県",
    "description": "日本一高い山。"
  }
]
```

- **エラーコード:**
  - `400 Bad Request` (キーワードが未指定の場合など)

---

## 3. 山 詳細情報取得API

IDに基づいて山の詳細情報を単体で取得します。

- **URL:** `/api/mountains/{id}`
- **メソッド:** `GET`
- **リクエストパラメータ:**
  - `id` (パス / 必須 / Integer): 山のID
- **成功レスポンス (200 OK):**

```json
{
  "id": 1,
  "name": "富士山",
  "elevation": 3776,
  "prefecture": "静岡県",
  "description": "日本一高い山。"
}
```

- **エラーコード:**
  - `404 Not Found` (指定されたIDの山が存在しない場合)

---

## 4. 有料会員向け：現在の天気取得API

OpenWeatherMapと連携し、特定山の現在の天候状態を取得します。

- **URL:** `/api/premium/weather/{mountainId}`
- **メソッド:** `GET`
- **リクエストパラメータ:**
  - `mountainId` (パス / 必須 / Integer): 天気を取得したい山のID
  - `locale` (自動解決 / 任意): ユーザーの言語設定（ja, enなど）
- **アクセス権限:** `ROLE_PREMIUM` 必須
- **成功レスポンス (200 OK):**

```json
{
  "weatherMain": "Clear",
  "weatherDescription": "晴れ",
  "temperature": 15.5,
  "humidity": 45,
  "windSpeed": 3.2,
  "iconCode": "01d"
}
```

- **エラーコード:**
  - `401 Unauthorized` / `403 Forbidden` (ログインしていない、または一般無料ユーザーからのアクセス)
  - `404 Not Found` (指定されたIDの山が見つからない場合)
  - `503 Service Unavailable` (外部天気予報APIとの通信失敗、または山に緯度経度が設定されていない場合)
