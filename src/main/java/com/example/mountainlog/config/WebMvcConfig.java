package com.example.mountainlog.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * Spring MVCの国際化（i18n）設定クラス
 * 
 * 【仕組みの概要】
 * 1. LocaleResolver: ユーザーの現在のロケール（言語設定）をどこに保存・取得するかを決定する。
 * ここでは CookieLocaleResolver を使用し、ブラウザの Cookie に保存する。
 * → ブラウザを閉じてもロケールの選択が保持される。
 * 
 * 2. LocaleChangeInterceptor: URLパラメータ（例: ?lang=ja）でロケールを変更できるようにする。
 * 全リクエストをインターセプト（横取り）して、"lang" パラメータがあればロケールを切り替える。
 * 
 * 3. MessageSource: application.properties 側で設定済み。
 * messages.properties (英語) と messages_ja.properties (日本語) を読み込む。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * ロケールリゾルバーの定義
     * CookieLocaleResolver はユーザーのロケール設定を Cookie に保存する。
     * - setDefaultLocale: Cookieが未設定の場合のデフォルト言語（ここでは英語）
     * - setCookieName: Cookie の名前
     * - setCookieMaxAge: Cookie の有効期限（秒）。ここでは30日に設定。
     */
    @SuppressWarnings("deprecation")
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver("lang");
        resolver.setDefaultLocale(Locale.ENGLISH);
        resolver.setCookieMaxAge(60 * 60 * 24 * 30); // 30日間保持
        return resolver;
    }

    /**
     * ロケール変更インターセプターの定義
     * URLに "lang" パラメータがついていたら、その値でロケールを変更する。
     * 例: http://localhost:8080/activities?lang=ja → 日本語に切り替え
     * http://localhost:8080/activities?lang=en → 英語に切り替え
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang"); // URLパラメータ名を "lang" に設定
        return interceptor;
    }

    /**
     * 上で作成した LocaleChangeInterceptor を Spring MVC のインターセプターチェーンに登録する。
     * これにより、全てのリクエストで "lang" パラメータが監視される。
     */
    @SuppressWarnings("null")
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
