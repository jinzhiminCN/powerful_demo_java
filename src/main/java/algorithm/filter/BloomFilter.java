package algorithm.filter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.Collection;

/**
 * @author jinzhimin
 * @description: 布隆过滤器
*/
public class BloomFilter {
	private BitSet bitset;
	private int bitSetSize;

	/**
	 * 期望的最大可添加的数量
	 */
	private int expectedNumberOfFilterElements;
	/**
	 * 实际添加的数量
	 */
	private int numberOfAddedElements;
	/**
	 * number of hash functions
	 */
	private int hashNumber;

	/**
	 * 获取Hash值的算法
	 */
	static final String HASHNAME = "SHA-1";
	static final MessageDigest DIGESTFUNCTION;

	static { // 加密方法可重复使用
		MessageDigest tmp;
		try {
			tmp = MessageDigest.getInstance(HASHNAME);
		} catch (NoSuchAlgorithmException e) {
			tmp = null;
		}
		DIGESTFUNCTION = tmp;
	}

	/**
	 * Constructs an empty Bloom filter. The total length of the Bloom filter will be c*n.
	 * 
	 * @param c
	 *            is the number of bits used per element.
	 * @param n
	 *            is the expected number of elements the filter will contain.
	 * @param k
	 *            is the number of hash functions used.
	 */
	public BloomFilter(double c, int n, int k) {
		this.expectedNumberOfFilterElements = n;
		this.hashNumber = k;
		this.bitSetSize = (int) Math.ceil(c * n);
		numberOfAddedElements = 0;
		this.bitset = new BitSet(bitSetSize);
	}

	/**
	 * Constructs an empty Bloom filter. The optimal number of hash functions (k) is estimated from the total size of the Bloom and the
	 * number of expected elements.
	 * 
	 * @param bitSetSize
	 *            defines how many bits should be used in total for the filter.
	 * @param expectedNumberOElements
	 *            defines the maximum number of elements the filter is expected to contain.
	 * @param hashNumber
	 *            hash运算的次数
	 */

	public BloomFilter(int bitSetSize, int expectedNumberOElements, int hashNumber) {
		this(bitSetSize / (double) expectedNumberOElements, expectedNumberOElements, hashNumber);
	}

	public BloomFilter(int bitSetSize, int expectedNumberOElements) {
		this(bitSetSize / (double) expectedNumberOElements, expectedNumberOElements, 15);
	}

	/**
	 * Generates digests based on the contents of an array of bytes and splits the result into 4-byte int's and store them in an array. The
	 * digest function is called until the required number of int's are produced. For each call to digest a salt is prepended to the data.
	 * The salt is increased by 1 for each call.
	 * 
	 * @param data
	 *            specifies input data.
	 * @param hashes
	 *            number of hashes/int's to produce.
	 * @return array of int-sized hashes
	 */
	public static int[] createHashes(byte[] data, int hashes) {
		int[] result = new int[hashes];
		int k = 0;
		byte salt = 0;
		while (k < hashes) {
			byte[] digest;
			synchronized (DIGESTFUNCTION) {
				DIGESTFUNCTION.update(salt);
				salt++;
				digest = DIGESTFUNCTION.digest(data);
			}

			for (int i = 0; i < digest.length / 4 && k < hashes; i++) {
				int h = 0;
				for (int j = (i * 4); j < (i * 4) + 4; j++) {
					h <<= 8;
					h |= ((int) digest[j]) & 0xFF;
				}
				result[k] = h;
				k++;
			}
		}
		return result;
	}

	/**
	 * Calculates the expected probability of false positives based on the number of expected filter elements and the size of the Bloom
	 * filter. <br />
	 * <br />
	 * The value returned by this method is the <i>expected</i> rate of false positives, assuming the number of inserted elements equals the
	 * number of expected elements. If the number of elements in the Bloom filter is less than the expected value, the true probability of
	 * false positives will be lower.
	 * 
	 * @return expected probability of false positives.
	 */
	public double expectedFalsePositiveProbability() {
		return getFalsePositiveProbability(expectedNumberOfFilterElements);
	}

	/**
	 * Calculate the probability of a false positive given the specified number of inserted elements.
	 * 
	 * @param numberOfElements
	 *            number of inserted elements.
	 * @return probability of a false positive.
	 */
	public double getFalsePositiveProbability(double numberOfElements) {
		// (1 - e^(-k * n / m)) ^ k
		return Math.pow((1 - Math.exp(-hashNumber * (double) numberOfElements / (double) bitSetSize)), hashNumber);
	}

	/**
	 * Get the current probability of a false positive. The probability is calculated from the size of the Bloom filter and the current
	 * number of elements added to it.
	 * 
	 * @return probability of false positives.
	 */
	public double getFalsePositiveProbability() {
		return getFalsePositiveProbability(numberOfAddedElements);
	}

	/**
	 * Returns the value chosen for K.<br />
	 * <br />
	 * K is the optimal number of hash functions based on the size of the Bloom filter and the expected number of inserted elements.
	 * 
	 * @return optimal k.
	 */
	public int getHashNumber() {
		return hashNumber;
	}

	public byte[] toBytes(long val) {
		byte[] b = new byte[8];
		for (int i = 7; i > 0; i--) {
			b[i] = (byte) val;
			val >>>= 8;
		}
		b[0] = (byte) val;
		return b;
	}

	/**
	 * Sets all bits to false in the Bloom filter.
	 */
	public void clear() {
		bitset.clear();
		numberOfAddedElements = 0;
	}

	public void add(Long value) {
		add(toBytes(value));
	}

	/**
	 * Adds an array of bytes to the Bloom filter.添加数据
	 * 
	 * @param bytes
	 *            array of bytes to add to the Bloom filter.
	 */
	public void add(byte[] bytes) {
		int[] hashes = createHashes(bytes, hashNumber);
		for (int hash : hashes) {
			bitset.set(Math.abs(hash % bitSetSize), true);
		}
		numberOfAddedElements++;
	}

	/**
	 * Adds all elements from a Collection to the Bloom filter.
	 * 
	 * @param c
	 *            Collection of elements.
	 */
	public void addAll(Collection<Long> c) {
		for (Long element : c) {
			add(element);
		}
	}

	/**
	 * Returns true if the element could have been inserted into the Bloom filter. Use getFalsePositiveProbability() to calculate the
	 * probability of this being correct.
	 * 
	 * @param element
	 *            element to check.
	 * @return true if the element could have been inserted into the Bloom filter.
	 */
	public boolean contains(Long element) {
		return contains(toBytes(element));
	}

	/**
	 * 判断数据是否存在。 Returns true if the array of bytes could have been inserted into the Bloom filter. Use getFalsePositiveProbability() to
	 * calculate the probability of this being correct.
	 * 
	 * @param bytes
	 *            array of bytes to check.
	 * @return true if the array could have been inserted into the Bloom filter.
	 */
	public boolean contains(byte[] bytes) {
		int[] hashes = createHashes(bytes, hashNumber);
		for (int hash : hashes) {
			if (!bitset.get(Math.abs(hash % bitSetSize))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the number of bits in the Bloom filter. Use count() to retrieve the number of inserted elements.
	 * 
	 * @return the size of the bitset used by the Bloom filter.
	 */
	public int size() {
		return this.bitSetSize;
	}

	/**
	 * Returns the number of elements added to the Bloom filter after it was constructed or after clear() was called.
	 * 
	 * @return number of elements added to the Bloom filter.
	 */
	public int count() {
		return this.numberOfAddedElements;
	}

}
