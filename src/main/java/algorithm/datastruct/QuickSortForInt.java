package algorithm.datastruct;

/**
 * @author jinzhimin
 * @description: 快速排序
 */
public class QuickSortForInt {

    public static void Swap(int[] A, int i, int j) {
        int tmp;
        tmp = A[i];
        A[i] = A[j];
        A[j] = tmp;
    }

    /**
     * 从两端扫描交换的方式
     *
     * @param A
     * @param L
     * @param R
     */
    public static void QuickSort1(int[] A, int L, int R) {
        if (L < R) {
            //递归的边界条件，当 L == R时数组的元素个数为1个
            //最左边的元素作为中轴，L表示left, R表示right
            int pivot = A[L];
            int i = L + 1, j = R;
            //当i == j时，i和j同时指向的元素还没有与中轴元素判断，
            //小于等于中轴元素，i++,大于中轴元素j--,
            //当循环结束时，一定有i = j+1, 且i指向的元素大于中轴，j指向的元素小于等于中轴
            while (i <= j) {
                while (i <= j && A[i] <= pivot) {
                    i++;
                }
                while (i <= j && A[j] > pivot) {
                    j--;
                }
                //当 i > j 时整个切分过程就应该停止了，不能进行交换操作
                //这个可以改成 i < j， 这里 i 永远不会等于j， 因为有上述两个循环的作用
                if (i <= j) {
                    Swap(A, i, j);
                    i++;
                    j--;
                }
            }
            //当循环结束时，j指向的元素是最后一个（从左边算起）小于等于中轴的元素
            //将中轴元素和j所指的元素互换
            Swap(A, L, j);
            //递归左半部分
            QuickSort1(A, L, j - 1);
            //递归右半部分
            QuickSort1(A, j + 1, R);
        }
    }

    /**
     * 两端扫描，一端挖坑，另一端填补
     *
     * @param A
     * @param L
     * @param R
     */
    public static void QuickSort2(int[] A, int L, int R) {
        if (L < R) {
            //最左边的元素作为中轴复制到pivot，这时最左边的元素可以看做一个坑
            int pivot = A[L];
            //注意这里 i = L,而不是 i = L+1, 因为i代表坑的位置,当前坑的位置位于最左边
            int i = L, j = R;
            while (i < j) {
                //下面面两个循环的位置不能颠倒，因为第一次坑的位置在最左边
                while (i < j && A[j] > pivot) {
                    j--;
                }
                //填A[i]这个坑,填完后A[j]是个坑
                //注意不能是A[i++] = A[j],当因i==j时跳出上面的循环时
                //坑为i和j共同指向的位置,执行A[i++] = A[j],会导致i比j大1，
                //但此时i并不能表示坑的位置
                A[i] = A[j];

                while (i < j && A[i] <= pivot) {
                    i++;
                }
                //填A[j]这个坑，填完后A[i]是个坑，
                //同理不能是A[j--] = A[i]
                A[j] = A[i];
            }
            //循环结束后i和j相等，都指向坑的位置，将中轴填入到这个位置
            A[i] = pivot;
            //递归左边的数组
            QuickSort2(A, L, i - 1);
            //递归右边的数组
            QuickSort2(A, i + 1, R);
        }
    }

    /**
     * 单端扫描
     *
     * @param A
     * @param L
     * @param R
     */
    public static void QuickSort3(int[] A, int L, int R) {
        if (L < R) {
            //最左边的元素作为中轴元素
            int pivot = A[L];
            //初始化时小于等于pivot的部分，元素个数为0
            //大于pivot的部分，元素个数也为0
            int i = L, j = L + 1;
            while (j <= R) {
                if (A[j] <= pivot) {
                    i++;
                    Swap(A, i, j);
                    j++;//j继续向前，扫描下一个
                } else {
                    j++;//大于pivot的元素增加一个
                }
            }
            //A[i]及A[i]以前的都小于等于pivot
            //循环结束后A[i+1]及它以后的都大于pivot
            //所以交换A[L]和A[i],这样我们就将中轴元素放到了适当的位置
            Swap(A, L, i);
            QuickSort3(A, L, i - 1);
            QuickSort3(A, i + 1, R);
        }
    }

    /**
     * 三向切分的快速排序
     *
     * @param A
     * @param L
     * @param R
     */
    public static void QuickSort3Way(int[] A, int L, int R) {
        if (L >= R) {
            //递归终止条件，少于等于一个元素的数组已有序
            return;
        }

        int i, j, k, pivot;
        //首元素作为中轴
        pivot = A[L];
        i = L;
        k = L + 1;
        j = R;

        OUT_LOOP:
        while (k <= j) {
            if (A[k] < pivot) {
                Swap(A, i, k);
                i++;
                k++;
            } else if (A[k] == pivot) {
                k++;
            } else {
                // 遇到A[k]>pivot的情况，j从右向左扫描
                while (A[j] > pivot) {
                    //A[j]>pivot的情况,j继续向左扫描
                    j--;
                    if (j < k) {
                        break OUT_LOOP;
                    }
                }
                if (A[j] == pivot) {
                    //A[j]==pivot的情况
                    Swap(A, k, j);
                    k++;
                    j--;
                } else {
                    //A[j]<pivot的情况
                    Swap(A, i, j);
                    Swap(A, j, k);
                    i++;
                    k++;
                    j--;
                }
            }
        }
        //A[i, j] 等于 pivot 且位置固定，不需要参与排序
        // 对小于pivot的部分进行递归
        QuickSort3Way(A, L, i - 1);
        // 对大于pivot的部分进行递归
        QuickSort3Way(A, j + 1, R);
    }

    /**
     * 双轴快速排序
     *
     * @param A
     * @param L
     * @param R
     */
    public static void QuickSortDualPivot1(int[] A, int L, int R) {
        if (L >= R) {
            return;
        }

        if (A[L] > A[R]) {
            //保证pivot1 <= pivot2
            Swap(A, L, R);
        }

        int pivot1 = A[L];
        int pivot2 = A[R];

        //如果这样初始化 i = L+1, k = L+1, j = R-1,也可以
        //但代码中边界条件, i,j先增减，循环截止条件，递归区间的边界都要发生相应的改变
        int i = L;
        int k = L + 1;
        int j = R;

        OUT_LOOP:
        while (k < j) {
            if (A[k] < pivot1) {
                //i先增加，首次运行pivot1就不会发生改变
                i++;
                Swap(A, i, k);
                k++;
            } else if (A[k] >= pivot1 && A[k] <= pivot2) {
                k++;
            } else {
                while (A[--j] > pivot2) {
                    //j先增减，首次运行pivot2就不会发生改变
                    if (j <= k) {
                        //当k和j相遇
                        break OUT_LOOP;
                    }
                }
                if (A[j] >= pivot1 && A[j] <= pivot2) {
                    Swap(A, k, j);
                    k++;
                } else {
                    i++;
                    Swap(A, j, k);
                    Swap(A, i, k);
                    k++;
                }
            }
        }
        //将pivot1交换到适当位置
        Swap(A, L, i);
        //将pivot2交换到适当位置
        Swap(A, R, j);

        //一次双轴切分至少确定两个元素的位置，这两个元素将整个数组区间分成三份
        QuickSortDualPivot(A, L, i - 1);
        QuickSortDualPivot(A, i + 1, j - 1);
        QuickSortDualPivot(A, j + 1, R);
    }

    /**
     * 双轴快速排序
     *
     * @param A
     * @param L
     * @param R
     */
    public static void QuickSortDualPivot(int[] A, int L, int R) {

        if (L >= R) {
            return;
        }

        if (A[L] > A[R]) {
            //保证pivot1 <= pivot2
            Swap(A, L, R);
        }

        int pivot1 = A[L];
        int pivot2 = A[R];

        int i = L + 1;
        int k = L + 1;
        int j = R - 1;

        OUT_LOOP:
        while (k <= j) {
            if (A[k] < pivot1) {
                Swap(A, i, k);
                k++;
                i++;
            } else if (A[k] >= pivot1 && A[k] <= pivot2) {
                k++;
            } else {
                while (A[j] > pivot2) {
                    j--;
                    if (j < k) {
                        //当k和j错过
                        break OUT_LOOP;
                    }
                }
                if (A[j] >= pivot1 && A[j] <= pivot2) {
                    Swap(A, k, j);
                    k++;
                    j--;
                } else {
                    //A[j] < pivot1
                    //注意k不动
                    Swap(A, j, k);
                    j--;
                }
            }
        }
        i--;
        j++;
        //将pivot1交换到适当位置
        Swap(A, L, i);
        //将pivot2交换到适当位置
        Swap(A, R, j);

        //一次双轴切分至少确定两个元素的位置，这两个元素将整个数组区间分成三份
        QuickSortDualPivot(A, L, i - 1);
        QuickSortDualPivot(A, i + 1, j - 1);
        QuickSortDualPivot(A, j + 1, R);
    }

}
