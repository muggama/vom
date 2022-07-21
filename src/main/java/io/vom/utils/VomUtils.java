package io.vom.utils;

import com.github.pemistahl.lingua.api.IsoCode639_1;
import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VomUtils {
    public static Locale getLocale(List<String> list) {
        String[] arr = Properties.getInstance().getProperty("locale_languages","")
                .split(";");
        LanguageDetector detector;
        if (arr.length <= 1){
            detector = LanguageDetectorBuilder.fromAllLanguages().build();
        }else {
            var languages = Arrays.stream(arr)
                    .map(String::toUpperCase)
                    .map(Language::valueOf)
                    .toArray(Language[]::new);

            detector = LanguageDetectorBuilder.fromLanguages(languages).build();
        }

        var grouped = list.stream()
                .map(detector::detectLanguageOf)
                .map(Language::getIsoCode639_1)
                .map(IsoCode639_1::toString)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return grouped.entrySet().stream().max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .map(Locale::new)
                .orElse(Locale.ENGLISH);
    }
}
