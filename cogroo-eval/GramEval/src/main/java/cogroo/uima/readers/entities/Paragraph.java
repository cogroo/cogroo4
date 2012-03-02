package cogroo.uima.readers.entities;

import java.util.Collections;
import java.util.List;


public class Paragraph {
	
	private final List<SentenceEx> sentences;
	
	private final int id;
	private int start;
	private int end;


	private final String text;
	
	public Paragraph(List<SentenceEx> sentences, int id) {
		this.sentences = Collections.unmodifiableList(sentences);
		
		this.start = sentences.get(0).getStart();
		
		StringBuilder sb = new StringBuilder();
		for (SentenceEx sentence : sentences) {
			sentence.setStart(sb.length());
			sb.append(sentence.getSentence().getText() + " ");
		}
		text = sb.substring(0, sb.length() - 1);
		
		end = this.start + text.length();
		
		this.id = id;
	}

	public List<SentenceEx> getSentences() {
		return sentences;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public String getText() {
		return text;
	}

	public int getId() {
		return id;
	}

	public void setStart(int start) {
		this.start = start;
		
		for (SentenceEx sentence : sentences) {
			sentence.setStart(sentence.getStart() + start);
		}
	}

	public void setEnd(int end) {
		this.end = end;
	}
}
