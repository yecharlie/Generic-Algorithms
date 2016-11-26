package dataStructSupport;

import java.util.ArrayList;
import java.util.Random;

/**   
* @Title: SimplerandomSampling.java 

* @Package  
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2016-11-19
* @version V1.0   
*/

/**
* <p> SimplerandomSampling</p>
* <p>实现简单随机抽样 </p>
* @author FlyingFish
* @date 2016-11-19
*/
public class SimplerandomSampling<E> {

	private MyLinkedList<E> population;
	
	
	/** 
	* @Fields tagIndex : TODO(标识总体现有个体的存在范围</br>population[0...tagIndex]) 
	*/ 
	private int tagIndex = -1;
	
	private Random random;
	
	public SimplerandomSampling(E[] population){
		this(population,0);
	}
	
	public SimplerandomSampling(E[] population,long seed){
		this.population = new MyLinkedList<E>(population);
		tagIndex = this.population.size() - 1;
		random = new Random(seed);		
	}
	
	
	/** 
	* <p>从现有总体中进行一次简单随机抽样。</p> 
	* <p>抽样前应保证总体中仍有个体。可以利用{@link #isEmpty()}来判断。</p> 
	* @return 样本
	*/
	public E sample(){
		//index 在0到tagIndex之间
		int index = random.nextInt(tagIndex + 1);
		E elemTookOut = population.remove(index);
		population.addLast(elemTookOut);
		tagIndex --;
		return elemTookOut;
	}
	
	/** 
	* <p>从总体中连续、随机地抽取m个样本。 </p> 
	* <p>注意，如果总体个体数不足m，将以随机的方式将剩余所有个体取出。可以通过{@link #amountOfIndividual()} 确认当前总体中的个体数。</p> 
	* @param m 要抽取的样本数目
	* @return 按序抽取到的样本
	*/
	public ArrayList<E> sample(int m){
		ArrayList<E> samples = new ArrayList<E>();
		
		while(m-- != 0 && !isEmpty()){
			samples.add(sample());
		}
		
		return samples;
	}
	
	public int amountOfIndividual(){
		if(tagIndex < 0)
			return 0;
		else
			return tagIndex + 1;
	}
	
	/**总体中是否还有元素可以进行抽样*/
	public boolean isEmpty(){
		return tagIndex < 0;
	}
	
	/** 
	* <p>重置，可以将总体恢复到抽样前的初始状态 </p> 
	* <p>Description: </p>  
	*/
	public void reset(){
		tagIndex = population.size - 1;
	}
	
	/** 
	* <p>设置新的伪随机数种子 </p> 
	* <p>Description: </p> 
	* @param seed 
	*/
	public void setSeed(long seed){
		random = new Random(seed);
	}
	
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param args 
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//测试1
		Integer[] intSet = new Integer[10];
		for(int i = 0; i < intSet.length;i++)
			intSet[i] = i + 1;
		System.out.println("从1到10组成的总体中进行简单随机抽样\n第一轮：");
		SimplerandomSampling<Integer> srSampling = new SimplerandomSampling<Integer>(intSet); 
		for(int i = 0; i < 4;i ++)
			System.out.print(srSampling.sample() + " " );

		System.out.println("剩余个体：" + srSampling.amountOfIndividual());
		ArrayList<Integer> samp = srSampling.sample(6);
		while(!samp.isEmpty())
			System.out.print(samp.remove(0) + " " );		
		System.out.println("\n还有元素？ " + !srSampling.isEmpty() );
		
		//测试2
		System.out.println("第二轮：");
		srSampling.reset();
		samp = srSampling.sample(12);
		while(!samp.isEmpty())
			System.out.print(samp.remove(0) + " " );				
		System.out.println("\n剩余个体 " + srSampling.amountOfIndividual() );
	}

}
