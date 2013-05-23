Required Environment Variables
==============================

CORPUS_ROOT:	where to find corpus

MODEL_ROOT:		where to get or put models

UIMA_HOME:		UIMA installation
REPO_ROOT:		a place that would be the UIMA pear repository

COGROO_3:		the root of cogroo3 source (to be used by the comparators)

example:



export MODEL_ROOT=~/workspace/Models

export UIMA_HOME=~/Programas/apache-uima

export CORPUS_ROOT=~/workspace/corpus

export REPO_ROOT=~/workspace/Repo

export COGROO_3=~/workspace/Cogroo3


HOW TO
======

Evaluate a grammar checker configuration
----------------------------------------

To evaluate the grammar checker changing some configuration do:

perl evaluate_gc.pl 

