package tsp;

import java.util.ArrayList;
import java.util.Random;

/**   
* @Title: OXOperator.java 
* @Package  
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2016-11-19
* @version V1.0   
*/

/**
* <p> OXOperator</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2016-11-19
*/
public abstract class OXOperator {

	protected double rateOfOX;
	
	//debug
	private static Random random = new Random(120);
	
	protected OXOperator(double rate){
		rateOfOX = rate;
	}
	/** 
	* <p>在种群中选定两个进行杂交。 </p> 
	* <p>杂交受杂交概率的影响，如果按概率没有发生杂交，将返回false。完成了杂交将返回true。 </p> 
	* @param population 种群中所有个体
	* @param parent 选定的双亲
	* @param dst1 子代个体1，需事先完成初始化
	* @param dst2 子代个体2，需事先完成初始化
	* @return 只有完成杂交才返回true，否则为false
	*/
	public abstract boolean operateOX(Individual[] population,Parent parent,Individual dst1,Individual dst2);
	
	
	
	public static void eliteSelect(Population srcPopul,Individual[] dstPopul,int dstIndex){
		ArrayList<Integer> eliteChrom =  srcPopul.getIndividual(srcPopul.getElite()).chromosome;
		double eliteFitness = srcPopul.getIndividual(srcPopul.getElite()).fitness;
		dstPopul[dstIndex].chromosome.clear();
		for(int i = 0; i < eliteChrom.size();i ++)
			dstPopul[dstIndex].chromosome.add(eliteChrom.get(i));	
		dstPopul[dstIndex].fitness = eliteFitness;
	}
	
	/** 
	* <p>比例选择之赌轮选择 </p> 
	* <p>会根据个体适应度在种群中选择两个不同个体，以便后续进行杂交变异。注意到这里用到的必须是缩放后的适应度。 </p> 
	* @param population 种群
	* @param sumFitness 种群适应度之和
	* @return 两个被选中的个体在种群中的编号，从0开始。
	* @throws IllegalArgumentException 种群已经不存在两个不相同的个体
	*/
	public  static Parent rouletteWheelSelect(Individual[] population,double sumFitness){
		if(sumFitness == 0.0)
			throw new IllegalArgumentException("种群所有个体都是一样的！");
			
		int i = 0;
		double sumP = population[i].fitness / sumFitness; 
		//double r = Math.random();
		double r = random.nextDouble();
		while(sumP < r){
			i++;
			//System.out.println(i + " " + population.length);
			sumP = sumP + population[i].fitness / sumFitness;
		}
		
		//找另一个不同的个体
		//注意概率公式的变化
		int j = i;
		int deadlockTag = 0;
		while(j == i){
			if(deadlockTag >= 100)
				throw new IllegalArgumentException("种群已不存在两个不同的个体（注意根据比例选择，适应度为0的个体不在考虑范围内）");				
			j = 0;
			 sumP = population[j].fitness / sumFitness; 
			 r = Math.random();
			 r = random.nextDouble();
			while(sumP < r){				
				j++;
				sumP = sumP + population[j].fitness / sumFitness;
			}
			deadlockTag ++;	
		}
		return new Parent(i,j);
	}
	

	
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param args 
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//测试精英选择
		Individual i1 = new Individual(1.0);
		Individual i2 = new Individual(4.5);
		Individual i3 = new Individual(4.5);
		Individual[] p1 = {i1,i2,i3};
		System.out.println("P1:{ " + p1[0].fitness + "," + p1[1].fitness + "," + p1[2].fitness+ " }");		
		/*
		Individual i4 = new Individual(5.5);		
		Individual i5 = new Individual(0.5);
		Individual[] p2 = {i4,i5};
		
		System.out.println("前：P2:{ " + p2[0].fitness + "," + p2[1].fitness + " }");
		eliteSelect(p1,p2);
		System.out.println("后P2:{ " + p2[0].fitness + "," + p2[1].fitness + " }");
		*/
		//测试比例选择
		double sumOfFitness = 10;
		Parent parent;
		int c1 = 0,c2 = 0,c3 = 0;
		for(int i = 0; i < 100 ;i ++){
			parent = OXOperator.rouletteWheelSelect(p1, sumOfFitness);
			int k = parent.k1;
			switch(k){
			case 0:c1++;	break;
			case 1: c2++;break;
			case 2: c3++;break;			
			}
			k = parent.k2;
			switch(k){
			case 0:c1++;	break;
			case 1: c2++;break;
			case 2: c3++;break;			
			}			
		}//三个个体被选中的概率依次是：0.18、0.495、0.495
		System.out.println("100 * 2 = " +c1+" + " +c2+" + " +c3);;
	}

	
}
