package com.hss.hss_backend.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtils {

  private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
  private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
  private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");

  public static String makeSlug(String input) {
    if (input == null) {
      throw new IllegalArgumentException("Input cannot be null");
    }

    // Turkish character replacements
    String noTurkish = input
        .replace("ı", "i")
        .replace("İ", "i")
        .replace("ğ", "g")
        .replace("Ğ", "g")
        .replace("ü", "u")
        .replace("Ü", "u")
        .replace("ş", "s")
        .replace("Ş", "s")
        .replace("ö", "o")
        .replace("Ö", "o")
        .replace("ç", "c")
        .replace("Ç", "c");

    String nowhitespace = WHITESPACE.matcher(noTurkish).replaceAll("-");
    String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
    String slug = NONLATIN.matcher(normalized).replaceAll("");
    slug = EDGESDHASHES.matcher(slug).replaceAll("");
    return slug.toLowerCase(Locale.ENGLISH);
  }
}
