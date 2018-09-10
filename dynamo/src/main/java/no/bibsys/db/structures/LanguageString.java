package no.bibsys.db.structures;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class LanguageString {

  @JsonProperty("@value")
  private String value;

  @JsonProperty("@language")
  private String language;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof LanguageString)) {
      return false;
    }

    final LanguageString label1 = (LanguageString) o;

    if (!getValue().equals(label1.getValue())) {
      return false;
    }
    return getLanguage().equals(label1.getLanguage());
  }

  @Override
  public int hashCode() {
    int result = getValue().hashCode();
    result = 31 * result + getLanguage().hashCode();
    return result;
  }

  public LanguageString(final String text, final String language) {
    Preconditions.checkNotNull(text);
    Preconditions.checkNotNull(language);
    this.value = text;
    this.language = language;
  }


  public String getValue() {
    return value;
  }

  public void setValue(final String value) {
    Preconditions.checkNotNull(value);
    this.value = value;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(final String language) {
    Preconditions.checkNotNull(language);
    this.language = language;
  }
}
