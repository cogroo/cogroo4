Required Environment Variables
==============================

CORPUS_ROOT:	where to find corpus
MODEL_ROOT:		where to get or put models
UIMA_HOME:		UIMA installation
REPO_ROOT:		a place that would be the UIMA pear repository
COGROO_3:		the root of cogroo3 source (to be used by the comparators)

example:

export MODEL_ROOT=~/Documents/wrks/___MODELS
export UIMA_HOME=~/programs/apache-uima
export CORPUS_ROOT=~/Documents/wrks/corpus
export REPO_ROOT=~/Documents/wrks/_REPO
export COGROO_3=~/Documents/wrks/cogroo4-old_svn/cogroo3


HOW TO
======

Evaluate a grammar checker configuration
----------------------------------------

To evaluate the grammar checker changing some configuration do:

perl evaluate_gc.pl 

