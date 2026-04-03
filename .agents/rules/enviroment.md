---
trigger: always_on
---

【作業環境・その他の気になる点の説明】

1. 基本 OS・ハードウェア情報
   OS: Windows

2. 言語・ランタイム
   Java: Java 17 (Amazon Corretto)

Java Gold SE 17 の試験範囲に基づいた最新の構文（Record, Switch式, テキストブロック等）を優先的に使用する

JavaScript: ES6+ 標準（フロントエンドの動的制御に使用）

Node.js / npm: フロントエンドライブラリの管理に使用

3. フレームワーク・ライブラリ
   Backend: Spring Boot
   認証: Spring Security (Roleベース: ROLE_GENERAL / ROLE_PREMIUM/ ROLE_ADMIN)
   DB操作: Spring Data JPA
   API通信: RestTemplate (Bean定義済み)

Frontend: Thymeleaf, HTML5, CSS3 (Bootstrap 5)
地図: Leaflet.js (z-index: 1000前後の制御が必要)

外部API:
決済: Stripe Java SDK (WebhookテストにStripe CLIを使用)
天気: OpenWeatherMap API (units=metric 指定必須)

4. 開発ツール・IDE
   メインエディタ: Antigravity (AI-native Editor)
   ビルドツール: Maven (pom.xml による依存関係管理)
   テスト: JUnit 5, Mockito, AssertJ

5. データベース構成 (H2 / MySQL)
   構成例:
   users: 会員情報とロール（1:一般, 2:有料, 3:管理者）を管理

6. AI への動作・生成ルール
   返答言語: 日本語を基本とする

## 7. AI エディタの自律動作設定

- **コマンド実行の承認**:
  - Mavenビルド（mvnw）およびテスト実行（JUnit）のコマンドは、確認なしで実行を許可する
  - 環境変数の設定（JAVA_HOMEの指定等）を含むコマンドも常時許可対象とする

コード品質:
@JsonIgnoreProperties(ignoreUnknown = true) を付与し、API仕様変更に強いDTOを生成する
ResponseEntity.ok() 等を用い、適切なHTTPステータスコードを返す
双方向リレーション（1:N）の保存時は、親子両方のリンクを更新するヘルパーメソッドを含める。

セキュリティ:
プロパティファイルを本番環境と開発環境でわけること。
ローカル環境で開発する際は、開発環境用のプロパティファイルを利用する。
デプロイ時には、本番環境用のプロパティファイルを利用する。

プロパティファイル：
本番環境におけるプロパティファイルに関して、APIキーやシークレットは環境変数 ${...} 形式で扱うコードを生成すること。
開発環境については、ハードコーディングを許可する。

開発環境：application.property
本番環境：application_prod.property
