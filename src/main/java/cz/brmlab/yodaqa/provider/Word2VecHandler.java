package cz.brmlab.yodaqa.provider;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;


import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;


public class Word2VecHandler {
    private static Word2Vec instance = null;
    private static final String CORPUS_FILE_ROUTE = "data/word2vec/GoogleNews-vectors-negative300.bin.gz";
    private static final String CORPUS_URL = "https://github.com/mmihaltz/word2vec-GoogleNews-vectors/blob/master/GoogleNews-vectors-negative300.bin.gz";

    public static Word2Vec getInstance() {
        if(instance == null) {
            File file = new File(CORPUS_FILE_ROUTE);
            if(file.exists() && !file.isDirectory()){
                instance = WordVectorSerializer.readWord2VecModel(file);
                return instance;
            } else {
                downloadCorpus();
                return getInstance();
            }
        } else {
            return instance;
        }
    }

    private static void downloadCorpus() {
        URL url;
        URLConnection con;
        DataInputStream dis;
        FileOutputStream fos;
        byte[] fileData;
        try {
            url = new URL(CORPUS_URL); //File Location goes here
            con = url.openConnection(); // open the url connection.
            dis = new DataInputStream(con.getInputStream());
            fileData = new byte[con.getContentLength()];
            for (int q = 0; q < fileData.length; q++) {
                fileData[q] = dis.readByte();
            }
            dis.close(); // close the data input stream
            fos = new FileOutputStream(new File(CORPUS_FILE_ROUTE)); //FILE Save Location goes here
            fos.write(fileData);  // write out the file we want to save.
            fos.close(); // close the output stream writer
        }
        catch(Exception m) {
            System.out.println(m);
        }


    }

    private Word2VecHandler() {
    }
}
