[CoGrOO: Apache Open|LibreOffice Grammar Checker](http://cogroo.org)
====================================================================

Here you will find the source code of CoGrOO version 4.x.

Source organization
------------

The code is organized as maven subprojects:

* cogroo4
  * cogroo-nlp: low level customizations of Apache OpenNLP and tools, like the Featurizer tool and FSA dictionaries
  * cogroo-ann: a framework composed of Annotators and Pipes, this allow easy abstraction of Apache OpenNLP
  * cogroo-gc: the grammar checker core, based on the framework from cogroo-ann
  * cogroo-dict: dictionaries and scripst to deal with it (should be removed soon because now it is partially covered by Jspell-ptBR project)
  * cogroo-addon: core of the LO|OO add on (not present in the moment, we need to refactor the pt_Br addon)
  * eval: evaluation related subprojects (used by wcolen to write his master's dissertation)
  * lang: language specific code
      * [lang]: a language (for example pt_br)
          * cogroo-res-[lang]: resources (language models and dictionaries) for language
          * cogroo-ann-[lang]: customization of the framework for language
          * cogroo-gc-[lang]: language specific code and resources for language
          * cogroo-addon-[lang]: LO|OO addon for language

Building
--------

Execute `mvn clean install` inside the root folder.

Usage
-----
[Using the API](http://ccsl.ime.usp.br/redmine/projects/cogroo/wiki/API_CoGrOO_4x)

License
-------
[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)