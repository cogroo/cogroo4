/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This code derived from Apache OpenNLP. Please keep the header.
 */

package br.ccsl.cogroo.cmdline;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import opennlp.tools.cmdline.AbstractCmdLineTool;
import opennlp.tools.cmdline.BasicCmdLineTool;
import opennlp.tools.cmdline.CmdLineTool;
import opennlp.tools.cmdline.StreamFactoryRegistry;
import opennlp.tools.cmdline.TerminateToolException;
import opennlp.tools.cmdline.TypedCmdLineTool;
import opennlp.tools.cmdline.chunker.ChunkerCrossValidatorTool;
import opennlp.tools.cmdline.chunker.ChunkerTrainerTool;
import opennlp.tools.cmdline.namefind.TokenNameFinderCrossValidatorTool;
import opennlp.tools.cmdline.namefind.TokenNameFinderEvaluatorTool;
import opennlp.tools.cmdline.namefind.TokenNameFinderTool;
import opennlp.tools.cmdline.namefind.TokenNameFinderTrainerTool;
import opennlp.tools.cmdline.postag.POSTaggerCrossValidatorTool;
import opennlp.tools.cmdline.postag.POSTaggerTrainerTool;
import opennlp.tools.util.Version;
import br.ccsl.cogroo.cmdline.dictionary.AbbreviationDictionaryBuilderTool;
import br.ccsl.cogroo.cmdline.dictionary.POSDictionaryBuilderTool;
import br.ccsl.cogroo.cmdline.dictionary.TabSeparatedPOSDictionaryBuilderTool;
import br.ccsl.cogroo.cmdline.featurizer.FeaturizerConverterTool;
import br.ccsl.cogroo.cmdline.featurizer.FeaturizerCrossValidatorTool;
import br.ccsl.cogroo.cmdline.featurizer.FeaturizerEvaluatorTool;
import br.ccsl.cogroo.cmdline.featurizer.FeaturizerMETool;
import br.ccsl.cogroo.cmdline.featurizer.FeaturizerTrainerTool;
import br.ccsl.cogroo.formats.FeatureSampleStreamFactory;
import br.ccsl.cogroo.formats.ad.ADChunkBasedHeadFinderSampleStreamFactory;
import br.ccsl.cogroo.formats.ad.ADChunkBasedShallowParserSampleStreamFactory;
import br.ccsl.cogroo.formats.ad.ADContractionNameSampleStreamFactory;
import br.ccsl.cogroo.formats.ad.ADExPOSSampleStreamFactory;
import br.ccsl.cogroo.formats.ad.ADExpNameSampleStreamFactory;
import br.ccsl.cogroo.formats.ad.ADFeatureSampleStreamFactory;

public final class CLI {

  public static final String CMD = "cogroo-common";

  private static Map<String, AbstractCmdLineTool> toolLookupMap;

  static {
    // Register other types
    FeatureSampleStreamFactory.registerFactory();
    ADFeatureSampleStreamFactory.registerFactory();
    ADContractionNameSampleStreamFactory.registerFactory();
    ADExpNameSampleStreamFactory.registerFactory();
    ADExPOSSampleStreamFactory.registerFactory();
    ADChunkBasedHeadFinderSampleStreamFactory.registerFactory();
    ADChunkBasedShallowParserSampleStreamFactory.registerFactory();
    
    toolLookupMap = new LinkedHashMap<String, AbstractCmdLineTool>();

    List<AbstractCmdLineTool> tools = new LinkedList<AbstractCmdLineTool>();

    // Dictionary Builder
    tools.add(new POSDictionaryBuilderTool());
    tools.add(new TabSeparatedPOSDictionaryBuilderTool());

    // Featurizer
    tools.add(new FeaturizerMETool());
    tools.add(new FeaturizerTrainerTool());
    tools.add(new FeaturizerEvaluatorTool());
    tools.add(new FeaturizerCrossValidatorTool());
    tools.add(new FeaturizerConverterTool());

    // Contraction
    tools.add(new AbbreviationDictionaryBuilderTool());
    tools.add(new TokenNameFinderTool());
    tools.add(new TokenNameFinderTrainerTool());
    tools.add(new TokenNameFinderEvaluatorTool());
    tools.add(new TokenNameFinderCrossValidatorTool());

    // tagger
    tools.add(new POSTaggerTrainerTool());
    tools.add(new POSTaggerCrossValidatorTool());

    // Chunker
    tools.add(new ChunkerTrainerTool());
    tools.add(new ChunkerCrossValidatorTool());
    
    for (AbstractCmdLineTool tool : tools) {
      toolLookupMap.put(tool.getName(), tool);
    }

    toolLookupMap = Collections.unmodifiableMap(toolLookupMap);
  }

  /**
   * @return a set which contains all tool names
   */
  public static Set<String> getToolNames() {
    return toolLookupMap.keySet();
  }

  private static void usage() {
    System.out.print("CoGrOO Common " + Version.currentVersion().toString()
        + ". ");
    System.out.println("Usage: " + CMD + " TOOL");
    System.out.println("where TOOL is one of:");

    // distance of tool name from line start
    int numberOfSpaces = -1;
    for (String toolName : toolLookupMap.keySet()) {
      if (toolName.length() > numberOfSpaces) {
        numberOfSpaces = toolName.length();
      }
    }
    numberOfSpaces = numberOfSpaces + 4;

    for (CmdLineTool tool : toolLookupMap.values()) {

      System.out.print("  " + tool.getName());

      for (int i = 0; i < Math.abs(tool.getName().length() - numberOfSpaces); i++) {
        System.out.print(" ");
      }

      System.out.println(tool.getShortDescription());
    }

    System.out.println("All tools print help when invoked with help parameter");
    System.out.println("Example: opennlp SimpleTokenizer help");
  }

  public static void main(String[] args) {

    if (args.length == 0) {
      usage();
      System.exit(0);
    }

    String toolArguments[] = new String[args.length - 1];
    System.arraycopy(args, 1, toolArguments, 0, toolArguments.length);

    String toolName = args[0];

    // check for format
    String formatName = StreamFactoryRegistry.DEFAULT_FORMAT;
    int idx = toolName.indexOf(".");
    if (-1 < idx) {
      formatName = toolName.substring(idx + 1);
      toolName = toolName.substring(0, idx);
    }
    CmdLineTool tool = toolLookupMap.get(toolName);

    try {
      if (null == tool) {
        throw new TerminateToolException(1, "Tool " + toolName
            + " is not found.");
      }

      if (0 == toolArguments.length || 0 < toolArguments.length
          && "help".equals(toolArguments[0])) {
        if (tool instanceof TypedCmdLineTool) {
          System.out.println(((TypedCmdLineTool) tool).getHelp(formatName));
        } else if (tool instanceof BasicCmdLineTool) {
          System.out.println(tool.getHelp());
        }

        System.exit(0);
      }

      if (tool instanceof TypedCmdLineTool) {
        ((TypedCmdLineTool) tool).run(formatName, toolArguments);
      } else if (tool instanceof BasicCmdLineTool) {
        if (-1 == idx) {
          ((BasicCmdLineTool) tool).run(toolArguments);
        } else {
          throw new TerminateToolException(1, "Tool " + toolName
              + " does not support formats.");
        }
      } else {
        throw new TerminateToolException(1, "Tool " + toolName
            + " is not supported.");
      }
    } catch (TerminateToolException e) {

      if (e.getMessage() != null)
        System.err.println(e.getMessage());

      System.exit(e.getCode());
    }
  }
}
