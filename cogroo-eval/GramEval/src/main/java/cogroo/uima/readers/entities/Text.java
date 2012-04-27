package cogroo.uima.readers.entities;

import java.util.Collections;
import java.util.List;

public class Text {

  private final List<Paragraph> paragraphs;

  private final String text;

  private final int id;

  public Text(List<Paragraph> paragraphs, int id) {
    this.paragraphs = Collections.unmodifiableList(paragraphs);
    StringBuilder sb = new StringBuilder();
    for (Paragraph paragraph : paragraphs) {
      paragraph.setStart(sb.length());
      sb.append(paragraph.getText());
      paragraph.setEnd(sb.length());
      sb.append("\n\n");
    }

    this.text = sb.toString().trim();

    this.id = id;
  }

  public List<Paragraph> getParagraphs() {
    return paragraphs;
  }

  public String getText() {
    return text;
  }

  public int getId() {
    return id;
  }
}
