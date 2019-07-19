package algorithm.datastruct;

/**
 * @author jinzhimin
 * @description: 快速排序
 * https://www.cnblogs.com/nullzx/p/5880191.html
 */
public class QuickSortForInt {

  public static void swap(int[] A, int i, int j) {
    int tmp;
    tmp = A[i];
    A[i] = A[j];
    A[j] = tmp;
  }

  /**
   * 从两端扫描交换的方式
   *  @param arr
   * @param left
   * @param right
   */
  public static void quickSortByTwoEndSwap(int[] arr, int left, int right) {
    if (left < right) {
      // 递归的边界条件，当 left == right 时数组的元素个数为1
      // 以最左边的元素作为中轴
      int pivot = arr[left];
      int i = left + 1, j = right;
      // 当i == j时，i和j同时指向的元素还没有与中轴元素判断，
      // 小于等于中轴元素，i++,大于中轴元素j--,
      // 当循环结束时，一定有i = j+1, 且i指向的元素大于中轴，j指向的元素小于等于中轴
      while (i <= j) {
        while (i <= j && arr[i] <= pivot) {
          i++;
        }
        while (i <= j && arr[j] > pivot) {
          j--;
        }
        // 当 i > j 时整个切分过程就应该停止了，不能进行交换操作
        // 这个可以改成 i < j， 这里 i 永远不会等于 j， 因为有上述两个循环的作用
        if (i <= j) {
          swap(arr, i, j);
          i++;
          j--;
        }
      }
      // 当循环结束时，j指向的元素是最后一个（从左边算起）小于等于中轴的元素
      // 将中轴元素和j所指的元素互换
      swap(arr, left, j);
      // 递归左半部分
      quickSortByTwoEndSwap(arr, left, j - 1);
      // 递归右半部分
      quickSortByTwoEndSwap(arr, j + 1, right);
    }
  }

  /**
   * 两端扫描，一端挖坑，另一端填补
   * @param arr
   * @param left
   * @param right
   */
  public static void quickSortByTwoEndInsert(int[] arr, int left, int right) {
    if (left < right) {
      // 最左边的元素作为中轴复制到pivot，这时最左边的元素可以看做一个坑
      int pivot = arr[left];
      // 注意这里 i = left,而不是 i = left+1, 因为i代表坑的位置,当前坑的位置位于最左边
      int i = left, j = right;
      while (i < j) {
        // 下面面两个循环的位置不能颠倒，因为第一次坑的位置在最左边
        while (i < j && arr[j] > pivot) {
          j--;
        }
        // 填A[i]这个坑,填完后A[j]是个坑
        // 注意不能是A[i++] = A[j],当因i==j时跳出上面的循环时
        // 坑为i和j共同指向的位置,执行A[i++] = A[j],会导致i比j大1，
        // 但此时i并不能表示坑的位置
        arr[i] = arr[j];

        while (i < j && arr[i] <= pivot) {
          i++;
        }
        // 填A[j]这个坑，填完后A[i]是个坑，
        // 同理不能是A[j--] = A[i]
        arr[j] = arr[i];
      }
      // 循环结束后i和j相等，都指向坑的位置，将中轴填入到这个位置
      arr[i] = pivot;
      // 递归左边的数组
      quickSortByTwoEndInsert(arr, left, i - 1);
      // 递归右边的数组
      quickSortByTwoEndInsert(arr, i + 1, right);
    }
  }

  /**
   * 单端扫描
   *  @param arr
   * @param left
   * @param right
   */
  public static void quickSortByOneEndSwap(int[] arr, int left, int right) {
    if (left < right) {
      // 最左边的元素作为中轴元素
      int pivot = arr[left];
      // 初始化时小于等于pivot的部分，元素个数为0
      // 大于pivot的部分，元素个数也为0
      int i = left, j = left + 1;
      while (j <= right) {
        if (arr[j] <= pivot) {
          i++;
          swap(arr, i, j);
          // j继续向前，扫描下一个
          j++;
        } else {
          // 大于pivot的元素增加一个
          j++;
        }
      }
      // A[i]及A[i]以前的都小于等于pivot
      // 循环结束后A[i+1]及它以后的都大于pivot
      // 所以交换A[L]和A[i],这样我们就将中轴元素放到了适当的位置
      swap(arr, left, i);
      quickSortByOneEndSwap(arr, left, i - 1);
      quickSortByOneEndSwap(arr, i + 1, right);
    }
  }

  /**
   * 三向切分的快速排序
   *  @param arr
   * @param left
   * @param right
   */
  public static void quickSort3Way(int[] arr, int left, int right) {
    if (left >= right) {
      // 递归终止条件，少于等于一个元素的数组已有序
      return;
    }

    int i, j, k, pivot;
    // 首元素作为中轴
    pivot = arr[left];
    i = left;
    k = left + 1;
    j = right;

    OUT_LOOP:
    while (k <= j) {
      if (arr[k] < pivot) {
        swap(arr, i, k);
        i++;
        k++;
      } else if (arr[k] == pivot) {
        k++;
      } else {
        // 遇到A[k]>pivot的情况，j从右向左扫描
        while (arr[j] > pivot) {
          // A[j]>pivot的情况,j继续向左扫描
          j--;
          if (j < k) {
            break OUT_LOOP;
          }
        }
        if (arr[j] == pivot) {
          // A[j]==pivot的情况
          swap(arr, k, j);
          k++;
          j--;
        } else {
          // A[j]<pivot的情况
          swap(arr, i, j);
          swap(arr, j, k);
          i++;
          k++;
          j--;
        }
      }
    }
    // A[i, j] 等于 pivot 且位置固定，不需要参与排序
    // 对小于pivot的部分进行递归
    quickSort3Way(arr, left, i - 1);
    // 对大于pivot的部分进行递归
    quickSort3Way(arr, j + 1, right);
  }

  /**
   * 双轴快速排序
   *  @param arr
   * @param left
   * @param right
   */
  public static void quickSortDualPivot1(int[] arr, int left, int right) {
    if (left >= right) {
      return;
    }

    if (arr[left] > arr[right]) {
      // 保证pivot1 <= pivot2
      swap(arr, left, right);
    }

    int pivot1 = arr[left];
    int pivot2 = arr[right];

    // 如果这样初始化 i = L+1, k = L+1, j = R-1,也可以
    // 但代码中边界条件, i,j先增减，循环截止条件，递归区间的边界都要发生相应的改变
    int i = left;
    int k = left + 1;
    int j = right;

    OUT_LOOP:
    while (k < j) {
      if (arr[k] < pivot1) {
        // i先增加，首次运行pivot1就不会发生改变
        i++;
        swap(arr, i, k);
        k++;
      } else if (arr[k] >= pivot1 && arr[k] <= pivot2) {
        k++;
      } else {
        while (arr[--j] > pivot2) {
          // j先增减，首次运行pivot2就不会发生改变
          if (j <= k) {
            // 当k和j相遇
            break OUT_LOOP;
          }
        }
        if (arr[j] >= pivot1 && arr[j] <= pivot2) {
          swap(arr, k, j);
          k++;
        } else {
          i++;
          swap(arr, j, k);
          swap(arr, i, k);
          k++;
        }
      }
    }
    // 将pivot1交换到适当位置
    swap(arr, left, i);
    // 将pivot2交换到适当位置
    swap(arr, right, j);

    // 一次双轴切分至少确定两个元素的位置，这两个元素将整个数组区间分成三份
    quickSortDualPivot1(arr, left, i - 1);
    quickSortDualPivot1(arr, i + 1, j - 1);
    quickSortDualPivot1(arr, j + 1, right);
  }

  /**
   * 双轴快速排序
   *  @param arr
   * @param left
   * @param right
   */
  public static void quickSortDualPivot2(int[] arr, int left, int right) {

    if (left >= right) {
      return;
    }

    if (arr[left] > arr[right]) {
      // 保证pivot1 <= pivot2
      swap(arr, left, right);
    }

    int pivot1 = arr[left];
    int pivot2 = arr[right];

    int i = left + 1;
    int k = left + 1;
    int j = right - 1;

    OUT_LOOP:
    while (k <= j) {
      if (arr[k] < pivot1) {
        swap(arr, i, k);
        k++;
        i++;
      } else if (arr[k] >= pivot1 && arr[k] <= pivot2) {
        k++;
      } else {
        while (arr[j] > pivot2) {
          j--;
          if (j < k) {
            // 当k和j错过
            break OUT_LOOP;
          }
        }
        if (arr[j] >= pivot1 && arr[j] <= pivot2) {
          swap(arr, k, j);
          k++;
          j--;
        } else {
          // A[j] < pivot1
          // 注意k不动
          swap(arr, j, k);
          j--;
        }
      }
    }
    i--;
    j++;
    // 将pivot1交换到适当位置
    swap(arr, left, i);
    // 将pivot2交换到适当位置
    swap(arr, right, j);

    // 一次双轴切分至少确定两个元素的位置，这两个元素将整个数组区间分成三份
    quickSortDualPivot2(arr, left, i - 1);
    quickSortDualPivot2(arr, i + 1, j - 1);
    quickSortDualPivot2(arr, j + 1, right);
  }

  public static void main(String[] args) {
    int[] arr = new int[]{5, 1, 2, 3, 4, 6, 7, 8};
    quickSortByTwoEndInsert(arr, 0, 7);

  }
}
