package algorithm.datastruct;

import java.util.Comparator;

/**
 * @author jinzhimin
 * @description: AVLtree
 */
public class AVLtree<E> {
    private static class Node<E> {
        int h;
        E element;
        Node<E> left;
        Node<E> right;
        /**
         * 由于java中不像C语言那样有二级指针的概念，所以添加一个父类的引用，方便程序编写。
         */
        Node<E> parent;

        public Node(E element, int h, Node<E> left, Node<E> right, Node<E> parent) {
            this.element = element;
            this.h = h;
            this.left = left;
            this.right = right;
            this.parent = parent;
        }
    }

    /**
     * 指向伪根节点的引用
     */
    private Node<E> root;
    /**
     * 节点个数
     */
    private int size = 0;
    /**
     * 节点大小的比较器
     */
    Comparator<? super E> cmp;

    /**
     * 如果调用了不带参数的构造函数，则使用该内部类作为比较器，
     * 但此时泛型E需要继承Comparable接口,否则运行时会抛出异常
     *
     * @param <T>
     */
    private static class Cmp<T> implements Comparator<T> {
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public int compare(T e1, T e2) {
            return ((Comparable) e1).compareTo(e2);
        }
    }

    /**
     * 带比较器的构造函数。
     *
     * @param cmp
     */
    public AVLtree(Comparator<? super E> cmp) {
        if (cmp == null) {
            throw new IllegalArgumentException();
        }
        this.cmp = cmp;
        // 创建一个伪根节点，该节点的右子支才是真正的AVL树的根
        // 使用伪根节点节点的目的是，对插入和删除操作递归的形式能够统一
        root = new Node<E>(null, -1, null, null, null);
    }

    /**
     * 不带比较器的构造函数。
     */
    public AVLtree() {
        this.cmp = new Cmp<E>();
        root = new Node<E>(null, -1, null, null, null);
    }

    /**
     * 如果树中节点有变动，从底向上逐级调用该函数，可以更新节点的高度
     *
     * @param x
     * @return
     */
    private int getHeight(Node<E> x) {
        if (x == null) {
            return 0;
        } else {
            return x.h;
        }
    }

    /**
     * 求某个节点作为根时，该子树的最小值。
     *
     * @param x
     * @return
     */
    private E treeMin(Node<E> x) {
        while (x.left != null) {
            x = x.left;
        }
        return x.element;
    }

    public int size() {
        return size;
    }

    /**
     * 先根遍历，调试时使用。
     */
    public void preorderTraverse() {
        if (root != null) {
            preorderTraverse0(root.right);
        }
    }

    private void preorderTraverse0(Node<E> x) {
        if (x != null) {
            System.out.print(x.element + " ");
            if (x.left != null) {
                System.out.print(x.left.element + " ");
            } else {
                System.out.print("null  ");
            }

            if (x.right != null) {
                System.out.print(x.right.element + " ");
            } else {
                System.out.print("null  ");
            }
            System.out.println();
            preorderTraverse0(x.left);
            preorderTraverse0(x.right);
        }
    }

    /**
     * 逆时针旋转（左旋），参数表示轴节点。
     *
     * @param X
     */
    private void antiClockwiseRotate(Node<E> X) {
        Node<E> P = X.parent;
        Node<E> XR = X.right;
        if (P.left == X) {
            P.left = XR;
        } else {
            P.right = XR;
        }
        XR.parent = P;

        X.right = XR.left;
        if (XR.left != null) {
            XR.left.parent = X;
        }

        XR.left = X;
        X.parent = XR;

        // 旋转后要更新这两个节点的高度
        X.h = Math.max(getHeight(X.left), getHeight(X.right)) + 1;
        XR.h = Math.max(getHeight(XR.left), getHeight(XR.right)) + 1;
    }

    /**
     * 顺时针旋转（右旋）,参数表示轴节点。
     *
     * @param X
     */
    private void clockwistRotate(Node<E> X) {
        Node<E> P = X.parent;
        Node<E> XL = X.left;
        if (P.left == X) {
            P.left = XL;
        } else {
            P.right = XL;
        }
        XL.parent = P;

        X.left = XL.right;
        if (XL.right != null) {
            XL.right.parent = X;
        }

        XL.right = X;
        X.parent = XL;

        // 旋转后要更新这两个节点的高度
        X.h = Math.max(getHeight(X.left), getHeight(X.right)) + 1;
        XL.h = Math.max(getHeight(XL.left), getHeight(XL.right)) + 1;
    }

    //
    public void insert(E e) {
        insert0(root.right, e);
    }

    private void insert0(Node<E> x, E e) {
        if (x == null) {
            // 根节点
            root.right = new Node<E>(e, 1, null, null, root);
            size++;
            return;
        }

        if (cmp.compare(e, x.element) > 0) {
            if (x.right != null) {
                insert0(x.right, e);
                int lh = getHeight(x.left);
                int rh = getHeight(x.right);
                if (rh - lh == 2) {
                    if (cmp.compare(e, x.right.element) > 0) {
                        antiClockwiseRotate(x);
                    } else {
                        clockwistRotate(x.right);
                        antiClockwiseRotate(x);
                    }
                }
            } else {
                size++;
                x.right = new Node<E>(e, 1, null, null, x);
            }
        } else if (cmp.compare(e, x.element) < 0) {
            if (x.left != null) {
                insert0(x.left, e);
                int lh = getHeight(x.left);
                int rh = getHeight(x.right);
                if (lh - rh == 2) {
                    if (cmp.compare(e, x.left.element) < 0) {
                        clockwistRotate(x);
                    } else {
                        antiClockwiseRotate(x.left);
                        clockwistRotate(x);
                    }
                }
            } else {
                size++;
                x.left = new Node<E>(e, 1, null, null, x);
            }
        } else {
            // 元素已存在，我们用新的元素更新旧，
            // compare返回值等于0,并不表示两个对象完全相等
            x.element = e;
        }
        x.h = Math.max(getHeight(x.left), getHeight(x.right)) + 1;
    }

    public boolean delete(E e) {
        return delete0(root.right, e);
    }

    /**
     * 返回值表示是否删除成功
     *
     * @param x
     * @param e
     * @return
     */
    private boolean delete0(Node<E> x, E e) {
        // 没有找到待删除的元素
        if (x == null) {
            return false;
        }

        if (cmp.compare(e, x.element) > 0) {
            boolean reval = delete0(x.right, e);
            if (reval == false) {
                return false;
            }

            int lh = getHeight(x.left);
            int rh = getHeight(x.right);
            if (lh - rh == 2) {
                if (getHeight(x.left.left) > getHeight(x.left.right)) {
                    clockwistRotate(x);
                } else {
                    antiClockwiseRotate(x.left);
                    clockwistRotate(x);
                }
            }
        } else if (cmp.compare(e, x.element) < 0) {
            boolean reval = delete0(x.left, e);
            if (reval == false) {
                return false;
            }

            int lh = getHeight(x.left);
            int rh = getHeight(x.right);
            if (rh - lh == 2) {
                if (getHeight(x.right.right) > getHeight(x.right.left)) {
                    antiClockwiseRotate(x);
                } else {
                    clockwistRotate(x.right);
                    antiClockwiseRotate(x);
                }
            }
        } else {
            // 找到待删除的元素
            Node<E> P = x.parent;

            if (x.left == null) {
                // 左子支为空，可直接删除，在这一层一定不需要旋转
                size--;
                if (P.left == x) {
                    P.left = x.right;
                    if (P.left != null) {
                        P.left.parent = P;
                    }
                } else {
                    P.right = x.right;
                    if (P.right != null) {
                        P.right.parent = P;
                    }
                }
            } else if (x.right == null) {
                // 右子支为空，可直接删除，在这一层一定不需要旋转
                size--;
                if (P.left == x) {
                    P.left = x.left;
                    if (P.left != null) {
                        P.left.parent = P;
                    }
                } else {
                    P.right = x.left;
                    if (P.right != null) {
                        P.right.parent = P;
                    }
                }
            } else {
                // 找到待删除的节点,用后继节点代替，然后删除后继节点
                E nextVal = treeMin(x.right);
                x.element = nextVal;
                delete0(x.right, nextVal);
                int lh = getHeight(x.left);
                int rh = getHeight(x.right);
                if (lh - rh == 2) {
                    if (getHeight(x.left.left) > getHeight(x.left.right)) {
                        clockwistRotate(x);
                    } else {
                        antiClockwiseRotate(x.left);
                        clockwistRotate(x);
                    }
                }
            }
        }
        x.h = Math.max(getHeight(x.left), getHeight(x.right)) + 1;
        return true;
    }

    public static void main(String[] args) {
        AVLtree<Integer> avl = new AVLtree<Integer>();
        /* 可自行添加插入，删除操作进行测试 */
        avl.insert(3);
        avl.insert(5);
        avl.insert(6);
        avl.insert(7);
        avl.insert(8);
        avl.insert(9);
        avl.preorderTraverse();
        System.out.println();
        System.out.println(avl.size());

        avl.delete(7);
        avl.delete(8);
        avl.preorderTraverse();
        System.out.println();
        System.out.println(avl.size());
    }
}