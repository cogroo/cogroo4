package cogroo.uima.eval;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RuleGroups {
  private static final Map<String, String> CATEGORIES;
  static {
    Map<String, String> elems = new HashMap<String, String>();
    elems.put("xml:1", "\u00E0 + substantivo masculino");// à
    elems.put("xml:2", "\u00E0 + substativo masculino");
    elems.put("xml:3", "\u00E0 + substativo feminino plural");
    elems.put("xml:4", "\u00E0 + verbo");
    elems.put("xml:5", "a + indicador de horas");
    elems.put("xml:6", "a + indicador de horas");
    elems.put("xml:7", "a + lado de");
    elems.put("xml:8", "a + lado de");
    elems.put("xml:9", "em rela\u00E7\u00E3o a + substantivo feminino");// ç\u00E3
                                                                        // /
    elems.put("xml:10", "em rela\u00E7\u00E3o a + substantivo feminino");
    elems.put("xml:11", "com rela\u00E7\u00E3o a");
    elems.put("xml:12", "com rela\u00E7\u00E3o a");
    elems.put("xml:13", "devido a");
    elems.put("xml:14", "devido a");
    elems.put("xml:15", "\u00E0 + pronomes de tratamento");
    elems.put("xml:16", "\u00E0 + pronomes de tratamento");
    elems.put("xml:17", "substantivo + adjetivo");
    elems.put("xml:18", "substantivo + adjetivo");
    elems.put("xml:19", "substantivo + adjetivo");
    elems.put("xml:20", "substantivo + adjetivo");
    elems.put("xml:21", "adjetivo + substantivo");
    elems.put("xml:22", "adjetivo + substantivo");
    elems.put("xml:23", "adjetivo + substantivo");
    elems.put("xml:24", "adjetivo + substantivo");
    elems.put("xml:25", "em anexo");
    elems.put("xml:26", "em anexo");
    elems.put("xml:27", "em anexo");
    elems.put("xml:28", "anexo como adjetivo");
    elems.put("xml:29", "anexo como adjetivo");
    elems.put("xml:30", "anexo como adjetivo");
    elems.put("xml:31", "anexo como adjetivo");
    elems.put("xml:32", "anexo como adjetivo");
    elems.put("xml:33", "anexo como adjetivo");
    elems.put("xml:34", "anexo como adjetivo");
    elems.put("xml:35", "anexo como adjetivo");
    elems.put("xml:36", "anexo como adjetivo");
    elems.put("xml:37", "anexo como adjetivo");
    elems.put("xml:38", "meio no sentido de 'um pouco'");
    elems.put("xml:39", "meio no sentido de 'um pouco'");
    elems.put("xml:40", "meio como adjetivo");
    elems.put("xml:41", "meio como adjetivo");
    elems.put("xml:42", "fazer indicando tempo");
    elems.put("xml:43", "fazer indicando tempo");
    elems.put("xml:44", "verbo auxiliar + fazer indicando tempo");
    elems.put("xml:45", "verbo auxiliar + fazer indicando tempo");
    elems.put("xml:46", "haver + denota\u00E7\u00E3o de tempo");
    elems.put("xml:47", "haver + denota\u00E7\u00E3o de tempo");
    elems.put("xml:48", "haver + denota\u00E7\u00E3o de tempo");
    elems.put("xml:49", "haver + denota\u00E7\u00E3o de tempo");
    elems.put("xml:50", "haver no sentido de existir");
    elems.put("xml:119", "haver no sentido de existir");
    elems.put("xml:51", "verbo auxiliar + haver no sentido de existir");
    elems.put("xml:120", "verbo auxiliar + haver no sentido de existir");
    elems.put("xml:52", "mim + verbo no infinitivo");
    elems.put("xml:53", "eu regido por preposi\u00E7\u00E3o");
    elems.put("xml:54", "eu regido por preposi\u00E7\u00E3o");
    elems.put("xml:55", "eu regido por preposi\u00E7\u00E3o");
    elems.put("xml:56", "eu regido por preposi\u00E7\u00E3o");
    elems.put("xml:57", "uso de mau");
    elems.put("xml:58", "uso de mal");
    elems.put("xml:59", "preferir + redund\u00E2ncia");// \u00E2
    elems.put("xml:60", "reg\u00EAncia do verbo preferir");// \u00EA
    elems.put("xml:61",
        "palavras de sentido negativo + verbo + pronome obl\u00EDquo");// \u00ED
    elems.put("xml:62",
        "palavras de sentido negativo + verbo + pronome obl\u00EDquo");
    elems
        .put("xml:63",
            "palavras de sentido negativo + substantivo + verbo + pronome obl\u00EDquo");
    elems
        .put("xml:64",
            "palavras de sentido negativo + substantivo + verbo + pronome obl\u00EDquo");
    elems
        .put(
            "xml:65",
            "pronome relativo ou conjun\u00E7\u00E3o subordinativa + verbo + pronome obl\u00EDquo");
    elems
        .put(
            "xml:66",
            "pronome relativo ou conjun\u00E7\u00E3o subordinativa + verbo + pronome obl\u00EDquo");
    elems.put("xml:67", "adv\u00E9rbio + verbo + pronome obl\u00EDquo");
    elems.put("xml:68", "adv\u00E9rbio + verbo + pronome obl\u00EDquo");
    elems.put("xml:69", "adv\u00E9rbio + verbo + pronome obl\u00EDquo");
    elems.put("xml:70", "adv\u00E9rbio + verbo + pronome obl\u00EDquo");
    elems.put("xml:71", "pronome indefinido + verbo + pronome obl\u00EDquo");
    elems.put("xml:72", "pronome indefinido + verbo + pronome obl\u00EDquo");
    elems.put("xml:73", "s\u00F3, ou, ora ou quer + pronome obl\u00EDquo");
    elems.put("xml:74", "s\u00F3, ou, ora ou quer + pronome obl\u00EDquo");
    elems.put("xml:75",
        "conjuga\u00E7\u00E3o de um verbo irregular no futuro do subjuntivo");
    elems.put("xml:76",
        "conjuga\u00E7\u00E3o de um verbo irregular no futuro do subjuntivo");
    elems.put("xml:77",
        "conjuga\u00E7\u00E3o de um verbo irregular no futuro do subjuntivo");
    elems.put("xml:78", "reg\u00EAncia verbal");
    elems.put("xml:79", "reg\u00EAncia verbal");
    elems.put("xml:80", "reg\u00EAncia verbal");
    elems.put("xml:81", "reg\u00EAncia verbal");
    elems.put("xml:82", "reg\u00EAncia verbal");
    elems.put("xml:83",
        "pronome + verbo de liga\u00E7\u00E3o + adjetivo predicativo");
    elems.put("xml:84", "Crase - reg\u00EAncia de alguns nomes");
    elems.put("xml:85", "reg\u00EAncia verbal - crase");
    elems.put("xml:86", "reg\u00EAncia do verbo obedecer/desobedecer");
    elems.put("xml:87", "\u00E0 + pronomes pessoais");
    elems.put("xml:89", "\u00E0 + pronomes pessoais");
    elems.put("xml:88", "a + eu");
    elems.put("xml:90", "reg\u00EAncia do verbo namorar");
    elems
        .put("xml:91", "crase - reg\u00EAncia de alguns nomes - compl. plural");
    elems.put("xml:92", "meio-dia e meia");
    elems.put("xml:93", "reg\u00EAncia verbal - crase");
    elems.put("xml:94", "reg\u00EAncia verbal - crase");
    elems.put("xml:95", "artigo plural + substantivo singular");
    elems.put("xml:96", "reg\u00EAncia do verbo evitar, usufruir.");
    elems.put("xml:97", "reg\u00EAncia de demorar, torcer, votar");
    elems.put("xml:98", "reg\u00EAncia do verbo arrasar");
    elems.put("xml:99", "reg\u00EAncia verbo habituar-se");
    elems.put("xml:100", "reg\u00EAncia habituar com pr\u00F3clise.");
    elems.put("xml:101", "reg\u00EAncia verbo habituar-se");
    elems.put("xml:102", "reg\u00EAncia verbo habituar-se");
    elems.put("xml:103", "artigo singular + substantivo plural");
    elems.put("xml:104", "artigo feminino + substantivo masculino");
    elems.put("xml:105", "artigo masculino + substantivo feminino");
    elems.put("xml:106", "v\u00EDcios de express\u00E3o");
    elems.put("xml:107", "verbo acarretar");
    elems.put("xml:108", "segunda a sexta");
    elems.put("xml:109", "assistir com o sentido de presenciar.");
    elems.put("xml:110", "valoriza\u00E7\u00E3o de");
    elems.put("xml:111", "express\u00F5es entre v\u00EDrgulas");
    elems.put("xml:112", "express\u00F5es entre v\u00EDrgulas");
    elems.put("xml:113", "express\u00F5es entre v\u00EDrgulas");
    elems.put("xml:114", "determinante singular + substantivo plural");
    elems.put("xml:115", "Concord\u00E2ncia Numeral-Substantivo");
    elems.put("xml:117", "sujeito plural + verbo singular");
    elems.put("xml:118", "sujeito singular + verbo plural");
    elems.put("xml:121", "Gerundismo");
    elems.put("xml:122", "Redund\u00E2ncia sem\u00E2ntica");
    elems.put("xml:123", "Redund\u00E2ncia sem\u00E2ntica");
    elems.put("xml:124",
        "pronome + verbo de liga\u00E7\u00E3o + adjetivo predicativo");

    CATEGORIES = Collections.unmodifiableMap(elems);
  }

  public static String getGroup(String rule) {
    return CATEGORIES.get(rule);
  }
}
