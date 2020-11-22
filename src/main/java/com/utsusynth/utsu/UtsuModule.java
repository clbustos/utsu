package com.utsusynth.utsu;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.*;
import com.utsusynth.utsu.common.StatusBar;
import com.utsusynth.utsu.common.i18n.Localizer;
import com.utsusynth.utsu.common.i18n.NativeLocale;
import com.utsusynth.utsu.common.quantize.Quantizer;
import com.utsusynth.utsu.common.quantize.Scaler;
import com.utsusynth.utsu.controller.common.IconManager;
import com.utsusynth.utsu.engine.*;
import com.utsusynth.utsu.files.*;
import com.utsusynth.utsu.files.voicebank.VoicebankReader;
import javafx.fxml.FXMLLoader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

public class UtsuModule extends AbstractModule {
    @BindingAnnotation
    @Target({PARAMETER, METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Version {
    }

    @BindingAnnotation
    @Target({PARAMETER, METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SettingsPath {
    }

    @Override
    protected void configure() {
        bind(StatusBar.class).asEagerSingleton();
        bind(AssetManager.class).asEagerSingleton();
        bind(CacheManager.class).asEagerSingleton();
        bind(FileNameFixer.class).asEagerSingleton();
        bind(IconManager.class).asEagerSingleton();
        bind(VoicebankReader.class).asEagerSingleton();
    }

    @Provides
    private FXMLLoader provideFXMLLoader(final Injector injector) {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(p -> injector.getInstance(p));
        return loader;
    }

    @Provides
    @Version
    private String provideVersion() {
        return ("0.4.2");
    }

    @Provides
    @SettingsPath
    private File provideSettingsPath(@Version String curVersion) {
        File homePath = new File(System.getProperty("user.home"));
        return new File(homePath, ".utsu/" + curVersion);
    }

    @Provides
    @Singleton
    private ThemeManager provideThemeManager(@SettingsPath File settingsPath) {
        return new ThemeManager(
                settingsPath,
                "/css/css_template.txt",
                "/css/themes/");
    }

    @Provides
    @Singleton
    private PreferencesManager providePreferencesManager(
            @SettingsPath File settingsPath, AssetManager assetManager) {
        ImmutableMap.Builder<String, String> defaultBuilder = ImmutableMap.builder();
        defaultBuilder.put("theme", ThemeManager.DEFAULT_LIGHT_THEME);
        defaultBuilder.put("autoscroll", PreferencesManager.AutoscrollMode.ENABLED_END.name());
        defaultBuilder.put(
                "autoscrollCancel", PreferencesManager.AutoscrollCancelMode.ENABLED.name());
        defaultBuilder.put("locale", "en");
        defaultBuilder.put("cache", PreferencesManager.CacheMode.ENABLED.name());
        defaultBuilder.put("resampler", assetManager.getResamplerFile().getAbsolutePath());
        defaultBuilder.put("wavtool", assetManager.getWavtoolFile().getAbsolutePath());
        defaultBuilder.put("voicebank", assetManager.getVoicePath().getAbsolutePath());
        return new PreferencesManager(
                settingsPath,
                DocumentBuilderFactory.newDefaultInstance(),
                TransformerFactory.newDefaultInstance(),
                defaultBuilder.build());
    }

    @Provides
    @Singleton
    private Localizer provideLocalizer() {
        NativeLocale defaultLocale = new NativeLocale(new Locale("en"));
        ImmutableList<NativeLocale> allLocales = ImmutableList.of(
                defaultLocale,
                new NativeLocale(new Locale("ja")),
                new NativeLocale(new Locale("es")),
                new NativeLocale(new Locale("it")),
                new NativeLocale(new Locale("in")),
                new NativeLocale(new Locale("fr", "FR")),
                new NativeLocale(new Locale("zh", "CN")),
                new NativeLocale(new Locale("zh", "TW")),
                new NativeLocale(new Locale("pt", "BR")));
        return new Localizer(defaultLocale, allLocales);
    }

    @Provides
    private Engine provideEngine(
            Resampler resampler,
            Wavtool wavtool,
            StatusBar statusBar,
            CacheManager cacheManager,
            PreferencesManager preferencesManager) {
        return new Engine(
                resampler,
                wavtool,
                statusBar,
                /* threadPoolSize= */ 10,
                cacheManager,
                preferencesManager);
    }

    @Provides
    @Singleton
    private FrqGenerator provideFrqGenerator(
            ExternalProcessRunner runner, FileNameFixer fileNameFixer, AssetManager assetManager) {
        return new FrqGenerator(runner, fileNameFixer, assetManager, 256);
    }

    @Provides
    @Singleton
    private Quantizer provideQuantizer() {
        return new Quantizer(4);
    }

    @Provides
    @Singleton
    private Scaler provideScaler() {
        return new Scaler(2, 0);
    }
}
