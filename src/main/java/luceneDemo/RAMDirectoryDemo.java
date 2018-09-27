package luceneDemo;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RAMDirectoryDemo {
    private static final Logger logger = LoggerFactory.getLogger(RAMDirectoryDemo.class);

    public static void ramDirectoryIndex(){
        long startTime = System.currentTimeMillis();

        // 创建一个内存目录对象，所以这里生成的索引不会放在磁盘中，而是在内存中。
        RAMDirectory directory = new RAMDirectory();

        IndexWriterConfig writerConfig = new IndexWriterConfig(new StandardAnalyzer());
        try {
            IndexWriter writer = new IndexWriter(directory, writerConfig);
            Document doc = new Document();

            doc.add(new Field("name", "zhangshan", TextField.TYPE_STORED));
            doc.add(new Field("address", "中国上海", TextField.TYPE_STORED));
            doc.add(new Field("dosomething", "I am learning lucene", TextField.TYPE_STORED));
            writer.addDocument(doc);
            writer.close();

            DirectoryReader iReader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(iReader);
            // 根据搜索关键字 封装一个term组合对象，然后封装成Query查询对象
            // TermQuery 是 lucene查询中最基本的一种原子查询，从它的名字Term我们可以看出，它只能针对一个字段进行查询。
            Query query = new TermQuery(new Term("dosomething", "lucene"));
            TopDocs rs = searcher.search(query, 100);
            long endTime = System.currentTimeMillis();
            logger.info("总共花费" + (endTime - startTime) + "毫秒，检索到" + rs.totalHits + "条记录。");
            for (int i = 0; i < rs.scoreDocs.length; i++) {
                // rs.scoreDocs[i].doc 是获取索引中的标志位id, 从0开始记录
                Document firstHit = searcher.doc(rs.scoreDocs[i].doc);
                logger.info("name:" + firstHit.getField("name").stringValue());
                logger.info("address:" + firstHit.getField("address").stringValue());
                logger.info("dosomething:" + firstHit.getField("dosomething").stringValue());
            }

            writer.close();
            directory.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ramDirectoryIndex();
    }
}
