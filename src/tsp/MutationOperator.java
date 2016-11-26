package tsp;
/**   
* @Title: MutationOperator.java 
* @Package  
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2016-11-19
* @version V1.0   
*/

/**
* <p> MutationOperator</p>
* <p>提供从一个个体变异到另一个个体的方法的接口 </p>
* @author FlyingFish
* @date 2016-11-19
*/
public interface MutationOperator {
	
	/** 
	* <p>变异操作 </p> 
	* <p>注意源个体和目标个体应占据两段不同的内存空间。</p> 
	* @param srcIndividual 源个体（变异前）
	* @param dstIndividual 目标个体（变异后）
	*/
	public void operateMutation(Individual srcIndividual,Individual dstIndividual);
	
}
