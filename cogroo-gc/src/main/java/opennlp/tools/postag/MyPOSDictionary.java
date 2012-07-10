package opennlp.tools.postag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.cogroo.entities.impl.MorphologicalTag;
import org.cogroo.interpreters.TagInterpreterI;


public class MyPOSDictionary extends POSDictionary {

  private Set<String> knwonTags = new HashSet<String>();

  public static ExtendedPOSDictionary parseOneEntryPerLine(Reader in,
      TagInterpreterI tago, TagInterpreterI tagd, Set<String> knownFeats,
      Set<String> knownPostags, boolean allowInvalidFeats) throws IOException {

    knownFeats = new TreeSet<String>(knownFeats);

    BufferedReader lineReader = new BufferedReader(in);

    ExtendedPOSDictionary dictionary = new ExtendedPOSDictionary();

    String line;

    Set<String> unknownTags = new TreeSet<String>();

    while ((line = lineReader.readLine()) != null) {
      StringTokenizer whiteSpaceTokenizer = new StringTokenizer(line, " ");

      String word = whiteSpaceTokenizer.nextToken();

      while (whiteSpaceTokenizer.hasMoreTokens()) {
        String data = whiteSpaceTokenizer.nextToken();
        String[] lemmaTag = data.split(">");

        if (lemmaTag.length != 2) {
          // System.err.println("** Invalid lemmatag. " + word + " -> " + data);
        } else {

          // convert the jspell tag to floresta tag
          MorphologicalTag completeTag = tago
              .parseMorphologicalTag(lemmaTag[1]);

          if (completeTag == null || completeTag.getClazzE() == null) {
            // System.err.println("-- Missing class tag. " + word + " -> " +
            // data);
          } else {
            MorphologicalTag classMT = new MorphologicalTag();
            classMT.setClazz(completeTag.getClazzE());
            String classString = tagd.serialize(classMT);

            MorphologicalTag featsMT = completeTag.clone();
            featsMT.setClazz(null);
            String featsString = tagd.serialize(featsMT);

            if (featsString == null || featsString.length() == 0) {
              featsString = "-";
            }

            if ("pron".equals(classString)) {
              // change to pron-det
              if (knownFeats.contains(featsString) || allowInvalidFeats) {
                dictionary.addTriple(word, new Triple("pron-det", lemmaTag[0],
                    featsString));
              }
              if (knownFeats.contains(featsString) || allowInvalidFeats) {
                dictionary.addTriple(word, new Triple("pron-indp", lemmaTag[0],
                    featsString));
              }
            } else if (classString != null
                && knownPostags.contains(classString)
                && (knownFeats.contains(featsString) || allowInvalidFeats)) {
              dictionary.addTriple(word, new Triple(classString, lemmaTag[0],
                  featsString));
            } else {
              if (!classString.startsWith("v-"))
                System.err.println("unknown - "
                    + word
                    + " -> "
                    + new Triple(classString, lemmaTag[0], classString + "_"
                        + featsString));
              unknownTags.add(classString + "_" + featsString);
            }
          }
        }
      }

    }

    if (knownFeats.size() > 0) {
      System.err.print("Known tags:");
      for (String tag : knownFeats) {
        System.err.print(" " + tag);
      }
      System.err.println();
    }

    if (unknownTags.size() > 0) {
      System.err.print("Found unknown tags:");
      for (String tag : unknownTags) {
        System.err.print(" " + tag);
      }
      System.err.println();
    }

    return dictionary;
  }

  // private static MorphologicalTag preprocess(MorphologicalTag tag) {
  // if(Class.PROPER_NOUN.equals(tag.getClazzE())) {
  // if(tag.getNumberE() == null) {
  // tag.setNumber(Number.SINGULAR);
  // }
  // if(tag.getGenderE() == null) {
  // tag.setGender(Gender.MALE);
  // }
  // }
  // return tag;
  // }

  void addTags(String word, String... tags) {
    super.addTags(word, tags);
    for (String t : tags) {
      knwonTags.add(t);
    }
  }

  public void addTag(String word, String tag) {
    String[] tags = getTags(word);
    if (tag.startsWith("B-") || tag.startsWith("I-")) {
      tag = tag.substring(2);
    }
    if (tags != null) {
      if (!arrayContains(tag, tags) && knwonTags.contains(tag)) {
        System.err.println("-- tag not found " + word + ":" + tag);
        String[] newTags = Arrays.copyOf(tags, tags.length + 1);
        newTags[tags.length] = tag;
        addTags(word, newTags);
      }
    }
  }

  private boolean arrayContains(String tag, String[] tags) {
    for (String t : tags) {
      if (tag.equals(t)) {
        return true;
      }
    }
    return false;
  }

  public static MyPOSDictionary createCopy(POSDictionary original) {
    MyPOSDictionary newDict = new MyPOSDictionary();
    for (String word : original) {
      newDict.addTags(word, original.getTags(word));
    }

    return newDict;
  }

}
