package algorithm.datastruct;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

/**
 * @author jinzhimin
 * @description: 简易 TimSort 算法
 */
public class SimpleTimSort<T extends Comparable<? super T>> {
  // 最小归并长度
  private static final int MIN_MERGE = 16;
  // 待排序数组
  private final T[] arr;
  // 辅助数组
  private T[] aux;
  // 用两个数组表示栈
  private int[] runsBase = new int[40];
  private int[] runsLen = new int[40];
  // 表示栈顶指针
  private int stackTop = 0;

  @SuppressWarnings("unchecked")
  public SimpleTimSort(T[] arr) {
    this.arr = arr;
    aux = (T[]) Array.newInstance(arr[0].getClass(), arr.length);
  }

  // T[from, to]已有序，T[to]以后的n元素插入到有序的序列中
  private void insertSort(T[] a, int from, int to, int n) {
    int i = to + 1;
    while (n > 0) {
      T tmp = a[i];
      int j;
      for (j = i - 1; j >= from && tmp.compareTo(a[j]) < 0; j--) {
        a[j + 1] = a[j];
      }
      a[++j] = tmp;
      i++;
      n--;
    }
  }

  // 返回从a[from]开始，的最长有序片段的个数
  private int maxAscendingLen(T[] a, int from) {
    int n = 1;
    int i = from;

    // 超出范围
    if (i >= a.length) {
      return 0;
    }

    // 只有一个元素
    if (i == a.length - 1) {
      return 1;
    }

    // 至少两个元素
    if (a[i].compareTo(a[i + 1]) < 0) {
      // 升序片段
      while (i + 1 <= a.length - 1 && a[i].compareTo(a[i + 1]) <= 0) {
        i++;
        n++;
      }
      return n;
    } else {
      // 降序片段，这里是严格的降序，不能有>=的情况，否则不能保证稳定性
      while (i + 1 <= a.length - 1 && a[i].compareTo(a[i + 1]) > 0) {
        i++;
        n++;
      }
      // 对降序片段逆序
      int j = from;
      while (j < i) {
        T tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
        j++;
        i--;
      }
      return n;
    }
  }

  // 对有序片段的起始索引位置和长度入栈
  private void pushRun(int base, int len) {
    runsBase[stackTop] = base;
    runsLen[stackTop] = len;
    stackTop++;
  }

  // 返回-1表示不需要归并栈中的有序片段
  public int needMerge() {
    // 至少两个run序列
    if (stackTop > 1) {
      int x = stackTop - 2;
      // x > 0 表示至少三个run序列
      if (x > 0 && runsLen[x - 1] <= runsLen[x] + runsLen[x + 1]) {
        if (runsLen[x - 1] < runsLen[x + 1]) {
          // 说明 runsLen[x+1]是runsLen[x]和runsLen[x-1]中最大的值
          // 应该先合并runsLen[x]和runsLen[x-1]这两段run
          return --x;
        } else {
          return x;
        }
      } else if (runsLen[x] <= runsLen[x + 1]) {
        return x;
      } else {
        return -1;
      }
    }
    return -1;
  }

  // 返回后一个片段的首元素在前一个片段应该位于的位置
  private int gallopLeft(T[] a, int base, int len, T key) {
    int i = base;
    while (i <= base + len - 1) {
      if (key.compareTo(a[i]) >= 0) {
        i++;
      } else {
        break;
      }
    }
    return i;
  }

  // 返回前一个片段的末元素在后一个片段应该位于的位置
  private int gallopRight(T[] a, int base, int len, T key) {
    int i = base + len - 1;
    while (i >= base) {
      if (key.compareTo(a[i]) <= 0) {
        i--;
      } else {
        break;
      }
    }
    return i;
  }

  public void mergeAt(int x) {
    int base1 = runsBase[x];
    int len1 = runsLen[x];

    int base2 = runsBase[x + 1];
    int len2 = runsLen[x + 1];

    // 合并run[x]和run[x+1],合并后base不用变,长度需要发生变化
    runsLen[x] = len1 + len2;
    if (stackTop == x + 3) {
      // 栈顶元素下移，省去了合并后的先出栈，再入栈
      runsBase[x + 1] = runsBase[x + 2];
      runsLen[x + 1] = runsLen[x + 2];
    }
    stackTop--;

    // 飞奔模式，减小归并的长度
    int from = gallopLeft(arr, base1, len1, arr[base2]);
    if (from == base1 + len1) {
      return;
    }
    int to = gallopRight(arr, base2, len2, arr[base1 + len1 - 1]);

    // 对两个需要归并的片段长度进行归并
    System.arraycopy(arr, from, aux, from, to - from + 1);
    int i = from;
    int iEnd = base1 + len1 - 1;

    int j = base2;
    int jEnd = to;

    int k = from;
    int kEnd = to;

    while (k <= kEnd) {
      if (i > iEnd) {
        arr[k] = aux[j++];
      } else if (j > jEnd) {
        arr[k] = aux[i++];
      } else if (aux[i].compareTo(aux[j]) <= 0) {
        // 等号保证排序的稳定性
        arr[k] = aux[i++];
      } else {
        arr[k] = aux[j++];
      }
      k++;
    }
  }

  // 强制归并已入栈的序列
  private void forceMerge() {
    while (stackTop > 1) {
      mergeAt(stackTop - 2);
    }
  }

  // timSort的主方法
  public void timSort() {
    // n表示剩余长度
    int n = arr.length;

    if (n < 2) {
      return;
    }

    // 待排序的长度小于MIN_MERGE,直接采用插入排序完成
    if (n < MIN_MERGE) {
      insertSort(arr, 0, 0, arr.length - 1);
      return;
    }

    int base = 0;
    while (n > 0) {
      int len = maxAscendingLen(arr, base);
      if (len < MIN_MERGE) {
        int abscent = n > MIN_MERGE ? MIN_MERGE - len : n - len;
        insertSort(arr, base, base + len - 1, abscent);
        len = len + abscent;
      }
      pushRun(base, len);
      n = n - len;
      base = base + len;

      int x;
      while ((x = needMerge()) >= 0) {
        mergeAt(x);
      }
    }
    forceMerge();
  }

  public static void main(String[] args) {

    // 随机产生测试用例
    Random rnd = new Random(System.currentTimeMillis());
    boolean flag = true;
    while (flag) {

      // 首先产生一个全部有序的数组
      Integer[] arr1 = new Integer[1000];
      for (int i = 0; i < arr1.length; i++) {
        arr1[i] = i;
      }

      // 有序的基础上随机交换一些值
      for (int i = 0; i < (int) (0.1 * arr1.length); i++) {
        int x, y, tmp;
        x = rnd.nextInt(arr1.length);
        y = rnd.nextInt(arr1.length);
        tmp = arr1[x];
        arr1[x] = arr1[y];
        arr1[y] = tmp;
      }

      // 逆序部分数据
      for (int i = 0; i < (int) (0.05 * arr1.length); i++) {
        int x = rnd.nextInt(arr1.length);
        int y = rnd.nextInt((int) (arr1.length * 0.01) + x);
        if (y >= arr1.length) {
          continue;
        }
        while (x < y) {
          int tmp;
          tmp = arr1[x];
          arr1[x] = arr1[y];
          arr1[y] = tmp;
          x++;
          y--;
        }
      }

      Integer[] arr2 = arr1.clone();
      Integer[] arr3 = arr1.clone();
      Arrays.sort(arr2);

      SimpleTimSort<Integer> sts = new SimpleTimSort<Integer>(arr1);
      sts.timSort();

      // 比较SimpleTimSort排序和库函数提供的排序结果比较是否一致
      // 如果没有打印任何结果，说明排序结果正确
      if (!Arrays.deepEquals(arr1, arr2)) {
        for (int i = 0; i < arr1.length; i++) {
          if (!arr1[i].equals(arr2[i])) {
            System.out.printf("%d: arr1 %d  arr2 %d\n", i, arr1[i], arr2[i]);
          }
        }
        System.out.println(Arrays.deepToString(arr3));
        flag = false;
      }
    }
  }
}
