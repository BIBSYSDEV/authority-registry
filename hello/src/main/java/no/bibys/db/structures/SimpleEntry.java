package no.bibys.db.structures;

import java.util.List;

public class SimpleEntry implements Entry {


  private  String id;
  private  List<LanguageString> preferredLabels;


  public SimpleEntry(){}

  public SimpleEntry(String id, List<LanguageString> preferredLabels) {
    this.id = id;
    this.preferredLabels = preferredLabels;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public void setId(String id) {
      this.id=id;
  }


  public List<LanguageString> getPreferredLabels() {
    return this.preferredLabels;
  }


  public void setPreferredLabels(List<LanguageString> labels) {
      this.preferredLabels=labels;
  }


}
