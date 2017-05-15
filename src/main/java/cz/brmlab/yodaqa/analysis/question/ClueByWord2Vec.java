package cz.brmlab.yodaqa.analysis.question;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.deeplearning4j.models.word2vec.Word2Vec;
import cz.brmlab.yodaqa.provider.Word2VecHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import cz.brmlab.yodaqa.model.Question.Clue;
import cz.brmlab.yodaqa.model.Question.ClueSV;
import cz.brmlab.yodaqa.model.Question.SV;

import java.io.File;
import java.util.Collection;

/**
 * Generate Clue annotations in a QuestionCAS. These represent key information
 * stored in the question that is then used in primary search.  E.g. "What was
 * the first book written by Terry Pratchett?" should generate clues "first",
 * "book", "first book", "write" and "Terry Pratchett".
 *
 * This just generates clues from SVs (selecting verbs). So it generates
 * "write" for the above. */

public class ClueByWord2Vec extends JCasAnnotator_ImplBase {
    final Logger logger = LoggerFactory.getLogger(ClueByWord2Vec.class);
    final static private int CLOSEST_WORD_COUNT = 10;
    private Word2VecHandler vec;


    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        vec = Word2VecHandler.getInstance();
        super.initialize(aContext);
    }

    public void process(JCas jcas) throws AnalysisEngineProcessException {
        for (Clue originClue  : JCasUtil.select(jcas, Clue.class)) {
            Collection<String> closeStrings = vec.wordsNearest(originClue.getLabel(), CLOSEST_WORD_COUNT);
            for(String string : closeStrings) {
                addClue(jcas, originClue, string, originClue);
            }
        }
    }

    protected void addClue(JCas jcas, Annotation base, String label, Clue originClue) {
        Clue clue = new Clue(jcas);
        clue.setBegin(originClue.getBegin());
        clue.setEnd(originClue.getEnd());
        clue.setBase(base);
        clue.setWeight(originClue.getWeight());
        clue.setLabel(label);
        clue.setIsReliable(false);
        clue.addToIndexes();
        logger.debug("new by {}: {}", base.getType().getShortName(), clue.getLabel());
    }
}
