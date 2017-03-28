package cz.brmlab.yodaqa.analysis.ansevid;

import cz.brmlab.yodaqa.analysis.ansscore.AF;
import cz.brmlab.yodaqa.analysis.ansscore.AnswerFV;
import cz.brmlab.yodaqa.model.CandidateAnswer.AnswerFeature;
import cz.brmlab.yodaqa.model.CandidateAnswer.AnswerInfo;
import cz.brmlab.yodaqa.model.Question.Clue;
import cz.brmlab.yodaqa.model.Question.ClueLAT;
import cz.brmlab.yodaqa.model.TyCor.LAT;
import cz.brmlab.yodaqa.provider.Word2VecHandler;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by nagygergo on 2017.03.10..
 */
public class Word2vecCloseness extends JCasAnnotator_ImplBase {
    private static Word2VecHandler word2Vec = null;
    final Logger logger = LoggerFactory.getLogger(SolrHitsCounter.class);


    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        word2Vec = Word2VecHandler.getInstance();
        super.initialize(context);
    }

    @Override
    public void process(JCas jcas) throws AnalysisEngineProcessException {
        JCas questionView, answerView;
        AnswerInfo ai;
        String answerText;
        Double bestScore = 0.0;
        AnswerFV fv;
        try {
            questionView = jcas.getView("Question");
            answerView = jcas.getView("Answer");
            ai = JCasUtil.selectSingle(answerView, AnswerInfo.class);
        } catch (Exception e) {
            return; // AnswerHitlistCAS
        }

        answerText = answerView.getDocumentText();

        if(!answerText.equals("")) {

            Collection<LAT> clues = new ArrayList<>();
            for (LAT lat : JCasUtil.select(questionView, LAT.class)) {
                // do not include non-required clues
                clues.add(lat);
            }

            for (LAT lat : JCasUtil.select(answerView, LAT.class)) {
                // do not include non-required clues
                clues.add(lat);
            }

            for (LAT lat : clues) {
                Double score = word2Vec.similarity(answerText, lat.getText());
                bestScore = score > bestScore ? score : bestScore;
            }
            fv = new AnswerFV(ai);
            fv.setFeature(AF.Word2Vec_Closeness, bestScore);

            for (FeatureStructure af : ai.getFeatures().toArray())
                ((AnswerFeature) af).removeFromIndexes();
            ai.removeFromIndexes();

            ai.setFeatures(fv.toFSArray(answerView));
            ai.addToIndexes();
        }
    }
}
