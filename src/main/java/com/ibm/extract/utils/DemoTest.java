package com.ibm.extract.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.google.common.collect.Lists;

public class DemoTest {
	public static final String[] AccessFor= {"APP Name","CNUM","Employee LN ID","Employee LN ID(GET)","Employee LN ID(BP)","JobResponsibilities:","HrOrganizationDisplay:","HrUnit ID:"};
	public static final String[] UplineFor= {"APP Name","CNUM","Employee LN ID","Employee LN ID(GET)","Employee LN ID(BP)","Chain","JobResponsibilities:","HrOrganizationDisplay:","HrUnit ID:"};
	
	public static <T> List<List<T>> splitList(List<T> source, int n) {
		List<List<T>> result = new ArrayList<List<T>>();
		int remaider = source.size() % n; // (先计算出余数)
		int number = source.size() / n; // 然后是商
		int offset = 0;// 偏移量
		for (int i = 0; i < n; i++) {
			List<T> value = null;
			if (remaider > 0) {
				value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
				remaider--;
				offset++;
			} else {
				value = source.subList(i * number + offset, (i + 1) * number + offset);
			}
			result.add(value);
		}
		return result;
	}
    @Test
    public void test(){
        List<Integer> numList = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
 
        List<List<Integer>> lists=Lists.partition(numList,3);
        System.out.println(lists);//[[1, 2, 3], [4, 5, 6], [7, 8]]
 
    }

	public static void main(String[] args)  {
		String org="1 5BP1 ";
		String re=org.replaceAll("1\\s$", "");
		System.out.println(re);
		System.out.println(org);
		
//		Set<String> list=new HashSet<>();
//		list.addAll(Arrays.asList("Chain","1 ","1 2BP","1 3BP","1 4BP","1 5BP","1 6BP","1 7BP","1 8BP","1 9BP","1 10BP","1 11BP","1 12BP","1 ","1 2GET","1 3GET","1 4GET","1 5GET","1 6GET","2 ","2 2BP","2 3BP","2 4BP","2 5BP","2 6BP","2 7BP","2 ","2 2GET","2 3GET","2 4GET","2 5GET"));
//		String str=list.toString();
//		
//		System.out.println(str);
//		StringBuffer s1=new StringBuffer();
//		s1.append("aa");
//		StringBuffer s2=new StringBuffer();
//		s2.append("aa");
//		System.out.println(s1.toString().equals(s2.toString()));
//		List<String> list=Arrays.asList("a","b","c","d","e");
//		List<List<String>> lists=DemoTest.splitList(list, 2);
//		System.out.println(lists);
//		List<List<String>> lists=new ArrayList<>();
//		List<String> list1=new ArrayList<>();
//		list1.add("1");
//		list1.add("1");
//		System.out.println(list1.toString());
//		List<String> list3=new ArrayList<>();
//		list1.add("1");
//		list1.add("1");
//		System.out.println((list1.toString().equals(list3.toString())));
//		
//		
//		List<String> list2=new ArrayList<>();
//		list1.add("a");
//		list1.add("a");
//		list1.add("c");
//		list1.add("b");
//		list1.add(null);
//		lists.add(list1);
//		lists.add(list2);
////		System.out.println(list1);
//		for(List<String> l:lists) {
////			List<String> newList=l;
//			System.out.println(l);
//		}
		
    }

	
}
