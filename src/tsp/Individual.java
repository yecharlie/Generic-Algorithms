package tsp;

import java.util.ArrayList;

/**   
* @Title: Individual.java 
* @Package  
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2016-11-19
* @version V1.0   
*/

/**
* <p> Individual</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2016-11-19
*/
public class Individual {

	ArrayList<Integer> chromosome;
	double fitness;
	
	//测试用
	Individual(double fitness){
		this.fitness = fitness;
	}
		
	public Individual(int[] chrom){
		//注意这里有一个优化：设置线性表初始容量为染色体长度
		this.chromosome = new ArrayList<Integer>(chrom.length);
		for(int i = 0;i < chrom.length;i ++)
			chromosome.add(chrom[i]);
	}
	
	public Individual(ArrayList<Integer> chrom){
		this.chromosome = chrom;
	}
	
	public Individual(int[] chrom,double fitness){
		this.chromosome = new ArrayList<Integer>();
		for(int i = 0;i < chrom.length;i ++)
			chromosome.add(chrom[i]);
		this.fitness = fitness;
	}
	
	public void setGene(int index,int value){
		chromosome.set(index, value);
	}
	
	public int getGene(int index){
		return chromosome.get(index);
	}
	
	/**
	 * @param fitness the fitness to set
	 */
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
}
