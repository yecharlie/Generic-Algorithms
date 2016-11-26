/**   
* @Title: TCap.java 
* @Package tsp 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2016-11-21
* @version V1.0   
*/
package tsp;

import java.util.ArrayList;

/**
* <p> TCap</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2016-11-21
*/
public class TCap {

	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param args 
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		ArrayList<Integer> l1 = new ArrayList<Integer>();
		ArrayList<Integer> l2 = new ArrayList<Integer>();
		Integer n = new Integer(10);
		l1.add(n);
		l2.add(n);
		l1.clear();
		System.out.println(l2.get(0) == null ? "null":l2.get(0));
		*/
		ArrayList<Integer> l1 = new ArrayList<Integer>();
		ArrayList<Integer> l2 = l1;
		l2.add(1);
		l2.add(10);
		System.out.println(l1.size());//2
		
		
	}

}
