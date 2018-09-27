package luceneDemo;

import config.LuceneConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.spel.ast.Indexer;
import utils.FtpClientUtil;
import utils.FtpUtil;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;

public class IndexerDemo {
    private static final Logger logger = LoggerFactory.getLogger(IndexerDemo.class);

    /**
     * 实例化IndexWriter
     *
     * @param indexDir
     * @throws Exception
     */
    public static IndexWriter createIndexWriter(String indexDir) throws Exception {
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        // 标准分词器
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(dir, iwConfig);
        return writer;
    }

    /**
     * 关闭写索引
     *
     * @throws Exception
     */
    public static void closeIndexWriter(IndexWriter writer) throws Exception {
        writer.close();
    }

    /**
     * 索引指定目录的所有文件
     *
     * @param dataDir
     * @throws Exception
     */
    public static int makeIndex(String dataDir, IndexWriter writer) throws Exception {
        File[] files = new File(dataDir).listFiles();
        for (File file : files) {
            indexFile(file, writer);
        }
        return writer.numDocs();
    }

    /**
     * 索引指定文件
     *
     * @param file
     */
    private static void indexFile(File file,  IndexWriter writer) throws Exception {
        logger.info("索引文件：" + file.getCanonicalPath());
        Document doc = getDocument(file);
        writer.addDocument(doc);
    }

    /**
     * 获取文档，文档里再设置每个字段
     *
     * @param file
     */
    private static Document getDocument(File file) throws Exception {
        Document doc = new Document();
        doc.add(new TextField("contents", new FileReader(file)));
        doc.add(new TextField("fileName", file.getName(), Field.Store.YES));
        doc.add(new TextField("fullPath", file.getCanonicalPath(), Field.Store.YES));
        return doc;
    }

    public static void main(String[] args) {
        String indexDir = LuceneConfig.indexDir;
        String dataDir = LuceneConfig.dataDir;
        IndexWriter indexWriter = null;
        int numIndexed = 0;
        long start = System.currentTimeMillis();
        try {
            indexWriter = createIndexWriter(indexDir);
            makeIndex(dataDir, indexWriter);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                closeIndexWriter(indexWriter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        logger.info("索引：" + numIndexed + " 个文件 花费了" + (end - start) + " 毫秒");
    }


}
