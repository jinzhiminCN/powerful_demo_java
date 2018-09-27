package luceneDemo;

import config.LuceneConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FtpClientUtil;

import java.nio.file.Paths;

public class SearcherDemo {
    private static final Logger logger = LoggerFactory.getLogger(SearcherDemo.class);

    public static void search(String indexDir, String q) throws Exception {
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher is = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer(); // 标准分词器
        QueryParser parser = new QueryParser("contents", analyzer);
        Query query = parser.parse(q);
        long start = System.currentTimeMillis();
        TopDocs hits = is.search(query, 10);
        long end = System.currentTimeMillis();
        logger.info("匹配：" + q + "，总共花费" + (end - start) + "毫秒" + "查询到" + hits.totalHits + "个记录");
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc);
            logger.info(doc.get("fullPath"));
        }
        reader.close();
    }

    public static void main(String[] args) {
        String indexDir = LuceneConfig.indexDir;
        String q = "大学";
        try {
            search(indexDir, q);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
