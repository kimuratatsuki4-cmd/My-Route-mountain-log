---
trigger: always_on
---

【テストについて】
・各実装完了後、AI自身が内部ブラウザを利用してテストを実施すること
・javascriptの埋め込みは常時許可する。

ガイドラインとして以下の内容を厳守する

1. テスト環境の前提条件
   テストライブラリ: JUnitを標準として使用する。
   ビルドツール: Maven を使用し、mvn test で全テストが正常に通過する状態を維持する。
   カバレッジ目標: Service層のロジックについては、正常系・異常系ともに網羅率80%以上を目指す。

2. Mock（モック）化のルール
   外部API: OpenWeatherMap や Stripe などの外部通信が発生するコンポーネントは、必ず Mockito または MockRestServiceServer を使用して擬似応答を定義する。
   データベース: Repository層のテストには H2 Database（インメモリ）を使用し、実環境のDBには接続しない。
   RestTemplate: RestTemplateConfig で定義された Bean をモック化し、特定のJSONレスポンス（WeatherResponse 等）を返すように指示する。
