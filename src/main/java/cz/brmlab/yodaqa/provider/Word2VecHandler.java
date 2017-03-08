package cz.brmlab.yodaqa.provider;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;


import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;


public class Word2VecHandler {
    private static final String CORPUS_FILE_ROUTE = "data/word2vec/GoogleNews-vectors-negative300.bin.gz";
    private static Word2VecHandler instance = null;
    private Word2Vec vec;

    public static Word2VecHandler getInstance() {
        if(instance == null) {
             instance = new Word2VecHandler();
        }
        return instance;
    }

    private Word2VecHandler() {
        File file = new File(CORPUS_FILE_ROUTE);
        if(file.exists() && !file.isDirectory()) {
            vec = WordVectorSerializer.readWord2VecModel(file);
        }
    }

    public Collection<String> wordsNearest(String word, int nearestN) {
        Collection <String> wordList;
        if(vec == null) {
            wordList = new ArrayList<>();
        } else {
            wordList = vec.wordsNearest(word, nearestN);
        }

        return wordList;
    }
}
