package basic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinzhimin
 * @description: 正则表达式测试。
 */
public class RegExpDemo {
  private static final Logger logger = LoggerFactory.getLogger(RegExpDemo.class);

  /**
   * ReDoS 正则表达式DoS，一种正则表达式拒绝服务（Regular expression Denial of Service-ReDoS) 是一种拒绝服务攻击。
   * 正则表达式引擎分成两类，一类称为DFA(确定性有限状态自动机)，另一类称为NFA(非确定性有限状态自动机)。
   * 两类引擎要顺利工作，都必须有一个正则式和一个文本串。DFA根据文本串去比较正则式，看到一个子正则式，
   * 就把可能的匹配串全标注出来，然后再看正则式的下一个部分，根据新的匹配结果更新标注。而NFA是根据正则
   * 式去比较文本串，取到一个字符，就将该字符跟正则式比较，然后接着往下找。一旦不匹配，就把刚取的字符
   * 吐出来，一个一个吐，直到回到上一次匹配的地方。也就是说对于同一个字符串中的每一个字符，DFA都只会去
   * 匹配一次，比较快，但特性较少，而NFA则会去匹配多次，速度相比会慢很多，但是特性多，所以用NFA作为正
   * 则表达式引擎的会多些。现在再来看^(a+)+$，当用它去匹配“aaaax”这个字符串时，需要尝试2^4=16次才会
   * 失败，当这个字符串再长一点时，需要尝试的次数会成指数增长，aaaaaaaaaaX就要经历2^10=1024次尝试。
   */
  public static void testRegExpCrash() {
    String regularExp = "(x+x+)+y";
    String testStr = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

    final Pattern pattern = Pattern.compile(regularExp);
    final Matcher matcher = pattern.matcher(testStr);
    logger.info(matcher.matches() + "");
  }

  /** 测试长字符串。 */
  public static void testLongReg() {
    String regex = "([a-z]|//d)*";
    String inputStr = "";

    int length = 500;
    for (int i = 0; i < length; i++) {
      // 循环的拼接输入字符串
      inputStr = inputStr.concat(String.valueOf(i));
    }
    logger.info("字符串长度为：" + inputStr.length());

    boolean flag = checkSpecialChars(inputStr, regex);
    logger.info("匹配结果为: " + flag);
  }

  /**
   * @param inputStr
   * @param regex
   * @return
   */
  public static boolean checkSpecialChars(String inputStr, String regex) {
    if (inputStr == null || "".equals(inputStr)) {
      return false;
    }

    // 注意是此处matches()方法抛的异常
    return Pattern.compile(regex).matcher(inputStr).matches();
  }

  public static void testTimeoutMatch() {
    String regularExp = "(x+x+)+y";
    String testStr = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

    Matcher matcher = createMatcherWithTimeout(testStr, regularExp, 5000);
    logger.info(matcher.matches() + "");
  }

  public static Matcher createMatcherWithTimeout(
      String stringToMatch, String regularExpression, int timeoutMillis) {
    Pattern pattern = Pattern.compile(regularExpression);
    return createMatcherWithTimeout(stringToMatch, pattern, timeoutMillis);
  }

  public static Matcher createMatcherWithTimeout(
      String stringToMatch, Pattern regularExpressionPattern, int timeoutMillis) {
    CharSequence charSequence =
        new TimeoutRegexCharSequence(
            stringToMatch, timeoutMillis, stringToMatch, regularExpressionPattern.pattern());
    return regularExpressionPattern.matcher(charSequence);
  }

  private static class TimeoutRegexCharSequence implements CharSequence {

    private final CharSequence inner;

    private final int timeoutMillis;

    private final long timeoutTime;

    private final String stringToMatch;

    private final String regularExpression;

    public TimeoutRegexCharSequence(
        CharSequence inner, int timeoutMillis, String stringToMatch, String regularExpression) {
      super();
      this.inner = inner;
      this.timeoutMillis = timeoutMillis;
      this.stringToMatch = stringToMatch;
      this.regularExpression = regularExpression;
      timeoutTime = System.currentTimeMillis() + timeoutMillis;
    }

    @Override
    public char charAt(int index) {
      if (System.currentTimeMillis() > timeoutTime) {
        throw new RuntimeException(
            "Timeout occurred after "
                + timeoutMillis
                + "ms while processing regular expression '"
                + regularExpression
                + "' on input '"
                + stringToMatch
                + "'!");
      }
      return inner.charAt(index);
    }

    @Override
    public int length() {
      return inner.length();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
      return new TimeoutRegexCharSequence(
          inner.subSequence(start, end), timeoutMillis, stringToMatch, regularExpression);
    }

    @Override
    public String toString() {
      return inner.toString();
    }
  }

  public static void testIP() {
    String regularExp = "[0-9]*\\.[0-9]*\\.[0-9]*\\.104";
    String testStr = "172.16.2.107";

    Pattern pattern = Pattern.compile(regularExp);
    Matcher matcher = pattern.matcher(testStr);

    logger.info(matcher.group() + "");
  }

  public static void main(String[] args) {
//    testLongReg();
  testIP();
  }
}
