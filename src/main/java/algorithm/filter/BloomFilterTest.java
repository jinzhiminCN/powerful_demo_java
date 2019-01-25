package algorithm.filter;

public class BloomFilterTest {
	
	public static void main(String[] args) {
		String url3 = "";
		String url1 = "123";
		String url2 = "1234";
		FilterUtil.add(url3);
		if(!FilterUtil.contains(url1)){
			FilterUtil.add(url1);
			FilterUtil.add(url2);
		}
		System.out.println(FilterUtil.contains(url3));
		System.out.println(FilterUtil.contains(url1));
		System.out.println(FilterUtil.contains(url2));
		
	}

}
