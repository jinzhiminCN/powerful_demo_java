package hadoop;

/**
 * @author jinzhimin
 * @description: 假如一个文件，规范的格式是3个字段，“\t”作为分隔符，其中有2条异常数据，一条数据是只有2个字段，一条数据是有4个字段。
 * https://www.cnblogs.com/codeOfLife/p/5521356.html
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class SplitCounterDemo {

    // \t键
    private static String TAB_SEPARATOR = "\t";

    public static class CounterMap extends
            Mapper<LongWritable, Text, Text, Text> {
        // 定义枚举对象
        public static enum LOG_PROCESSOR_COUNTER {
            BAD_RECORDS_LONG, BAD_RECORDS_SHORT
        };

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String arr_value[] = value.toString().split(TAB_SEPARATOR);
            if (arr_value.length > 3) {
                /* 自定义计数器 */
                context.getCounter("ErrorCounter", "toolong").increment(1);
                /* 枚举计数器 */
                context.getCounter(LOG_PROCESSOR_COUNTER.BAD_RECORDS_LONG).increment(1);
            } else if (arr_value.length < 3) {
                // 自定义计数器
                context.getCounter("ErrorCounter", "tooshort").increment(1);
                // 枚举计数器
                context.getCounter(LOG_PROCESSOR_COUNTER.BAD_RECORDS_SHORT).increment(1);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        String[] args0 = {
                "hdfs://hadoop:9000/counter/counter.txt",
                "hdfs://hadoop:9000/counter/out/"
        };
        // 读取配置文件
        Configuration conf = new Configuration();

        // 如果输出目录存在，则删除
        Path mypath = new Path(args0[1]);
        FileSystem hdfs = mypath.getFileSystem(conf);
        if (hdfs.isDirectory(mypath)) {
            hdfs.delete(mypath, true);
        }

        // 新建一个任务
        Job job = Job.getInstance(conf, "SplitCounter");
        // 主类
        job.setJarByClass(SplitCounterDemo.class);
        // Mapper
        job.setMapperClass(CounterMap.class);

        // 输入目录
        FileInputFormat.addInputPath(job, new Path(args0[0]));
        // 输出目录
        FileOutputFormat.setOutputPath(job, new Path(args0[1]));

        // 提交任务，并退出
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

