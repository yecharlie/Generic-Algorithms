package tsp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import dataStructSupport.SimplerandomSampling;

/**   
* @Title: TSP.java 
* @Package  
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2016-11-19
* @version V1.0   
*/

/**
* <p> 用遗传算法求解旅行商问题。</p>
* <p>你可以通过继承本类来对本遗传算法进行改进或者使用自己的遗传算算法。为了实现这个目的，有以下几种手段：</br>1.提供自己的交叉算子 <code>OXOperator</code>。</br>2. 提供自己的变异算子 <code>MutationOperator</code></br>3.提供初始种群<br>4.重载{@link #evolution()} ，重新规划进化流程.</p>
*<p>你可以利用的资源如下：</br>1.一个单点交叉算子 <code>OnePointOXOperator</code></br>2.以随机方式产生初始种群的方法{@link #initiatePopulation(long)}</br>3.现成的进化框架{@link #evolution()}</p>
* @author FlyingFish
* @date 2016-11-19
*/
public class TSP {
	
	//城市信息
	protected CityPoint[] cities;
	
	/** 
	* @Fields start : TODO(起点城市序号，从1开始) 
	*/ 
	protected int start;
	
	/**城市距离矩阵*/
	protected double[][] distances;
	
	/**种群规模*/
	protected int populationScale;	

	/**父代种群，每次都由它产生新的种群*/
	protected Population oldPopulation ;
	
	/**子代种群*/
	protected Population newPopulation ;
	
	/**总代数*/
	protected int totalGenerations;	
	
	/**当前代数，初始为1*/
	protected int currentGen = 1;
	
	/**杂交算子*/
	protected OXOperator operOX;
	
	/**变异算子*/
	protected MutationOperator operMutate;
	
	//用于初始化种群
	private SimplerandomSampling<Integer> rsampling;
	
	//用于其他需要随机数的场合
	private Random random = new Random();
	

	public TSP(CityPoint[] cities,int start,int populScale,int totalGens,
			double rateOfOX,double pm1,double pm2){
		this.cities  = cities;
		this.distances = Util.Distance(cities);
		this.start = start ;
		
		this.populationScale = populScale;
		
		//提前给newPopulatio分配空间
		this.newPopulation = new Population(new Individual[populationScale]);		
		//一次性地声明新种群规模，并开辟好每个个体的空间
		for(int i = 0; i < populationScale;i ++)
			newPopulation.setIndividual(i, new Individual(new ArrayList<Integer>(populationScale)));

		
		this.totalGenerations = totalGens;
		this.currentGen = 1;
		
		operOX = new OnePointOXOperator(rateOfOX);

		//注意变异算子使用newPopulation作为信息源。
		//因为变异算子需要等到newPopulation分好空间后再定位到newPopulation上。
		//实际上本构造函数不能通过直接调用下面的构造函数来完成。
		operMutate = new ReverseMutationOperator(pm1,pm2,newPopulation);			
	}	
	
	protected TSP(CityPoint[] cities,int start,int populScale,int totalGens,
			OXOperator operOX,MutationOperator operMutate){
		this.cities  = cities;
		this.distances = Util.Distance(cities);
		this.start = start ;
		
		this.populationScale = populScale;					
		this.totalGenerations = totalGens;
		this.currentGen = 1;
		
		this.newPopulation = new Population(new Individual[populationScale]);		
		//一次性地声明新种群规模，并开辟好每个个体的空间
		for(int i = 0; i < populationScale;i ++)
			newPopulation.setIndividual(i, new Individual(new ArrayList<Integer>(populationScale)));

		
		this.operOX = operOX;
		this.operMutate = operMutate;
	}
	
	/** 
	* <p>用随机的方式产生初始种群</p> 
	* <p>Description: </p> 
	* @param seed 随机数种子
	*/
	protected void initiatePopulation(long seed){

		Integer[] tags = new Integer[cities.length - 1];
		int tagsIndex = 0;
		//城市从1开始编号，将除起点以外的所有城市
		//作为一个总体，进行简单随机抽样，每当将
		//总体中所有的个体都依次取出，就可得到一个完整的解
		for(int i = 1;i <= cities.length;i++)
			if(i!= start)//将除起点以外的所有城市编号依次放入tags
				tags[tagsIndex ++] = i;
		
		//准备建立第一代种群
		rsampling = new SimplerandomSampling<Integer>(tags,seed);		
		random = new Random(seed);
		
		this.oldPopulation= new Population(new Individual[populationScale]);
		int[] tempChrom = new int[cities.length];
		
		for(int i = 0;i < populationScale;i++){
			
			//得到一条染色体/路径
			tempChrom[0] = start;
			for(int j = 1;j < cities.length;j ++)
				//注意起点已经选定放在第一位的，所以不用经过抽样
				tempChrom[j] = rsampling.sample();
			
			//用路径生成新个体
			oldPopulation.setIndividual(i, new Individual(tempChrom)); 
			
			//重置
			rsampling.reset();			
		}
		
		//正式建立第一代种群
		this.populationSetup(oldPopulation);
		
		
		
		/*
		Integer padding = 0;ArrayList<Integer> receiver = new ArrayList<Integer>();
		for(int i = 0; i < populationScale;i ++){
			//因为交叉算法需要，强行给染色体上的基因占位
			//这样做可以修正chromosome.size()的值
			receiver = newPopulation.getIndividual(i).chromosome;
			for(int j = 0; j < cities.length;j ++)
				receiver.add(padding);
		}
		*/
	} 	
	
	/**作为终止之一，最优个体的适应度增长率小于等于门限值，终止进化*/
	public static double LIMIT_AUG_RATE = 0.0001;
	
	public void solveTSP(){
		this.initiatePopulation(320);
		double  record = oldPopulation.getIndividual(oldPopulation.getElite()).fitness;
		double  augRate = 1;
		do{
			try{
				evolution();
				augRate = (oldPopulation.getIndividual(oldPopulation.getElite()).fitness - record) / record;
				record = oldPopulation.getIndividual(oldPopulation.getElite()).fitness;
			}catch( IllegalArgumentException e){			
				System.out.println(e.getMessage()+"\n进化中断！");
				break;
			}
			//当最优个体的适应度增长率小于等于门限值时，或者当前世代已经达到总代数时会终止
			//在使用了精英选择的情况下为什么augRate会为负值？
			//因为这里使用的是经缩放的相对的适应度，而不是绝对的路径代价。
		}while(Math.abs(augRate) > LIMIT_AUG_RATE && currentGen <= totalGenerations);

		Individual optPath = oldPopulation.getIndividual(oldPopulation.getElite());
		System.out.println("\n\n*************结果*************\n最小路径代价是" + this.calculateCost(optPath.chromosome));
		System.out.println("最优路径为：\n" + optPath.chromosome);
		System.out.println("终止代数：" + currentGen );
		System.out.println("最终的种群：");
		oldPopulation.showInfo();	
	}
	
	
	/** 
	* <p>进化 </p> 
	* <p>策略，先通过比例选择、单点交叉产生同等规模的种群1，再经过变异得到种群2，随后利用精英选择用源种群的精英个体替换掉种群2的最弱个体。得到最终的新种群。 </p> 
	* @return 
	*/
	public void evolution(){

		System.out.println("代数："+currentGen);
		oldPopulation.showInfo();
	
		//1.比例选择、单点交叉
		Parent parent = 
				OXOperator.rouletteWheelSelect(oldPopulation.getPopulation(), oldPopulation.getSumOfFitness());
		Individual subInd1,subInd2;

		boolean successfullyOX = false;
		
		int deadlockIndex = 0;
		for(int subGenIndex = 0;subGenIndex < populationScale;deadlockIndex++){
			if(deadlockIndex >= 100)
				throw new IllegalArgumentException("无法产生新的种群");

			subInd1 = newPopulation.getIndividual(subGenIndex);

			subInd2 = newPopulation.getIndividual(subGenIndex+1);

			successfullyOX =operOX.operateOX(oldPopulation.getPopulation(), parent, subInd1, subInd2);	

			if(successfullyOX){
				subGenIndex+= 2;
			}
			parent = OXOperator.rouletteWheelSelect(oldPopulation.getPopulation(), oldPopulation.getSumOfFitness());
		}
			
		//精英选择,对于规模为奇数的种群，还可以用来凑子代
		OXOperator.eliteSelect(oldPopulation, newPopulation.getPopulation(), populationScale - 1);
			
		//变异之前的一些配置：适应度和种群信息。		
		populationSetup(newPopulation);

		//变异
		for(int i = 0; i < populationScale;i ++)
			operMutate.operateMutation(newPopulation.getIndividual(i), oldPopulation.getIndividual(i));
	
		//配置好变异后的种群
		populationSetup(oldPopulation);		


	
		//种群代数加1
		 ++currentGen;
	}
	
	
	public TSP(CityPoint[] cities,int start,int populScale,int totalGens){
		this(cities,start,populScale,totalGens,0.99,02,0.02);
	}

	private  void populationSetup(Population popul){
		int populationScale = popul.getPopulationScale();
		for(int i = 0; i < populationScale;i ++)
			popul.getIndividual(i).setFitness(
					calculateCost(
							popul.getIndividual(i).chromosome));
		gama(popul.getPopulation());
		popul.statisticsIndivInfo();
		popul.staticsticsWholeInfo();
	}
	
	/** 
	* <p>gama为适应度缩放函数 </p> 
	* <p> fitness(i,t) = Max(tempFitness(j,t)) - tempFitness(i,t), i,j在[1..scale]</br>
		Max(tempFitness(j,t))表示t时刻（代）该种群最大的未缩放的适应度，即旅行距离的最大值</p> 
	* @param popul 种群
	*/
	private static void gama(Individual[] popul){
		int populationScale = popul.length;
		
		double tempMaxFitness = 0;
		for(int i = 0;i < populationScale;i++)		
			if(popul[i].fitness > tempMaxFitness)
				tempMaxFitness = popul[i].fitness;
				
		for(int i = 0; i < populationScale;i ++)
			popul[i].setFitness(tempMaxFitness - popul[i].fitness  );
	}
	
	/** 
	* <p>计算路径花销</p> 
	* @param solution 
	*@return double 
	*/
	private  double calculateCost(ArrayList<Integer> solution){
		double cost = 0 ;
		for(int i = 0;i < solution.size() - 1; i++)
			cost += distances[solution.get(i)-1][solution.get(i + 1) - 1];
		
		//最后加上从终点站回到起点的长度
		cost += distances[solution.get(solution.size() - 1) - 1][start - 1];
		
		return cost;
	}
	
	/**
	* <p> 使用倒置变异法的变异算子。不建议继承TSP的子代使用，除非已经对这里的进化过程非常熟悉。</p>
	* <p>变异是有一定概率的。在一个种群里，我们让低适应度个体变异概率高，高适应度个体变异概率小，分别记为Pm1、Pm2。另一方面，我们让初始时的变异概率大一些，以便能搜索到更大的空间，随着演化过程接近尾声，变异概率Pm1、Pm2逐渐变为0，让结果收敛。 </p>
	* @author FlyingFish
	* @date 2016-11-20
	*/
	private class ReverseMutationOperator implements MutationOperator{
		private double lowerPm,upperPm;
		private Population srcPopul;
		
		/** 
		* <p>Title: </p> 
		* <p>Description: </p> 
		* @param upperPm
		* @param lowerPm
		* @param srcPopul 使用变异操作时，源个体一定要属于这个源种群
		*/
		public ReverseMutationOperator(double upperPm,double lowerPm,Population srcPopul){
			this.lowerPm = lowerPm;
			this.upperPm = upperPm;
			this.srcPopul= srcPopul;
		}
				
		/** 使用倒置变异法。
		 * <p>随机选取两个点（不能是起点），将两个点之间的包括两个点在内的路径倒置。类似于模拟退火中的二变换法。如果变异后的个体适应度更好，则替换原个体；否则，保持不变。</p>
		 * <p>变异有一定概率，如果没有发生变异，目标个体将直接是源个体的拷贝。</p>
		 * @see tsp.MutationOperator#operateMutation(tsp.Individual, tsp.Individual)
		 * @throws IllegalArgumentException 如果父体与子体是对同一个对象的引用
		 */
		@Override
		public void operateMutation(Individual srcIndividual, Individual dstIndividual) {
			// TODO Auto-generated method stub
			if(!checkAddress(srcIndividual,dstIndividual))
				throw new IllegalArgumentException("源个体与目标个体不能共用同一段空间");
			
			double pm = calcIndividualPm(srcIndividual);
			
			boolean mutated = false;
			ArrayList<Integer> newChrom = dstIndividual.chromosome;
			if(random.nextDouble() < pm){
					
				//newChrom容量是不变的
				newChrom.clear();
				
				//注意起点始终是不变的
				int l = (int)(random.nextDouble()* (srcIndividual.chromosome.size() - 1)) + 1;
				int r = (int)(random.nextDouble()* (srcIndividual.chromosome.size() - 1)) + 1;
				
				if(l > r){//交换l、r的内容
					int temp = l;
					l = r;
					r = temp;
				}
				//System.out.println("l , r:" + l +  "  " + r);
				
				for(int i = 0; i < l;i ++)
					newChrom.add(srcIndividual.chromosome.get(i));
				for(int i = r;i >= l;i --)
					newChrom.add(srcIndividual.chromosome.get(i));
				for(int i = r+1; i < srcIndividual.chromosome.size();i ++)
					newChrom.add(srcIndividual.chromosome.get(i));	
				
				//比较变异前、后个体的适应度。
				if(calculateCost(srcIndividual.chromosome) > calculateCost(newChrom))
					mutated = true;
			}
			if(!mutated){//如果变异没有成功,保留原来的个体
				for(int i = 0;i < srcIndividual.chromosome.size();i ++)
					newChrom.set(i,srcIndividual.chromosome.get(i));
			}				
		}
		
		private boolean checkAddress(Individual src,Individual dst){
			return src != dst;
		}
		
		/** 
		* <p>计算当前个体的变异概率</p> 
		* <p>查看{@link ReverseMutationOperator}的相关变异策略。使用公式 
		* </br> Pm(t)= Pm1(t) +(Pm2(t)-Pm1(t))*(F-Favg)/(Fmax - Favg) ,(F>Favg)</br>Pm(t) = Pm1(t),(F <= Favg) </br>来计算Pm。其中Pm1(t)、Pm2(t)表示t时刻的变异率上、下界，是初始值乘上衰减因子的结果。衰减因子factor计算公式为</br>factor =1.0 - 1.0 * currentGen / totalGenerations</p> 
		* @param srcPopulation
		* @return 
		*/
		private double calcIndividualPm(Individual srcIndiv){
			double factor = 1.0 - 1.0 * currentGen / totalGenerations;
			double pm1 = upperPm * factor,pm2 = lowerPm * factor;
			double pm ,deltaF = srcIndiv.fitness - srcPopul.getAverageOfFitness();
			if(deltaF > 0)
				pm = pm1 + (deltaF) 
					/ (srcPopul.getIndividual(srcPopul.getElite()).fitness 
							- srcIndiv.fitness)
					* (pm2-pm1);
			else
				pm = pm1;
			
			return pm;
		}	
	}

	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param args 
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TSP tspFormal = new TSP(Util.ReadFile("supplements/att48.tsp"),1,100,1000);
		tspFormal.solveTSP();		
	}
		
	
	/**
	* <p> 单点杂交算子</p>
	* <p>杂交过程。随机选择一个杂交点，将杂交点后面的等位基因两两互换。然后在两个染色体的首部至杂交点上分别找出重复的编码（城市）并交换。 </p>
	* <p>注意杂交点位置在[0...lenOfChromosome - 2]的范围上。</p>
	* @author FlyingFish
	* @date 2016-11-21
	*/
	protected class OnePointOXOperator extends OXOperator{
		
		private boolean[][] cityExist ;
			
		/** 
		* <p>Title: </p> 
		* <p>Description: </p> 
		* @param rate 
		*/
		protected OnePointOXOperator(double rate) {
			super(rate);
			// TODO Auto-generated constructor stub
			
			//cityExist[0]用于检查第一条源染色体前段与交换过来的
			//第二条源染色体后半段是否重复。（即校验第一个子体的染色体）
			//cityExist[1]类比
			cityExist = new boolean[2][cities.length];
		}

		private void init(){
			for(int i = 0; i < 2;i ++ )
				for(int j = 0;j < cities.length;j ++)
					cityExist[i][j] = false;
		}
		
		
		/** 经过单点交叉后,第一个子体得到第一个父体的前半段染色体（会做修改）和第二个父体的后半段染色体。第二个子体类比。
		 * @see tsp.OXOperator#operateOX(tsp.Individual[], tsp.Parent, tsp.Individual, tsp.Individual)
		 *@throws IllegalArgumentException 如果父体与子体是对同一个对象的引用,或者因为其它未知原因导致无法完成杂交
		 */
		@Override
		public boolean operateOX(Individual[] population, Parent parent, Individual dst1, Individual dst2) {
			// TODO Auto-generated method stub
			if(!this.checkAddress(population[parent.k1], population[parent.k2], dst1, dst2))
				throw new IllegalArgumentException("双亲与子代不能共用空间");
				
			if(random.nextDouble() > rateOfOX)//不能产生后代的概率 = 1 - rateOfOX
				return false;	
			
			//初始化cityExist
			init();
			
			//杂交前准备
			ArrayList<Integer> chrom1 ,chrom2;
			chrom1 = population[parent.k1].chromosome;
			chrom2 = population[parent.k2].chromosome;
			dst1.chromosome.clear();
			dst2.chromosome.clear();
			
			//选择杂交点
			int bound = chrom1.size() - 1,pointOfOX;
			pointOfOX  = (int)(Math.random() * bound);
			
			for(int i = 0; i < chrom1.size(); i ++){
				if(i > pointOfOX){
					//杂交点后的等位基因互换，并做好标记		
					dst1.chromosome.add(chrom2.get(i));
					cityExist[0][chrom2.get(i) - 1] = true;
					dst2.chromosome.add(chrom1.get(i));
					cityExist[1][chrom1.get(i) - 1] = true;
				}else{//杂交点及前面位置上的基因来自对应父本基因，
					//后面还会对这一段上的基因进行修改
					dst1.chromosome.add(chrom1.get(i));		
					dst2.chromosome.add(chrom2.get(i));	
				}
			}
			
			int deadlockIndex = 0;
			int src1 = 0,src2 = 0;
			while(src1 <= pointOfOX || src2 <= pointOfOX){
				if(deadlockIndex >= chrom1.size() + 1){//如果准备工作没做好，这里容易出现死锁
					System.out.println("源个体染色体\n" +chrom1+" \n" +chrom2);
					System.out.printf("pointOfOX,src1,src2%5d%5d%5d\n" ,pointOfOX,src1,src2 );
					throw new IllegalArgumentException("无法完成杂交");
				}
				while(src1 <= pointOfOX &&!cityExist[0][chrom1.get(src1) - 1] ) {src1++;}
				while(src2 <= pointOfOX && !cityExist[1][chrom2.get(src2) - 1] ) {src2++;}
				if(src1 <= pointOfOX && src2 <= pointOfOX){
					//显然在0到pointOfOX位置上dst1和dst2所遇到的重复元素一定相等
					//那么它们一定是同时完成对重复元素的处理（因为每次它们各自会
					//找一个重复点在进行交叉赋值），相反，如果一方已经完成了对所有
					//元素的处理（包括重复元素），则可以断定另一方都没有可处理的重复元素。
					dst1.chromosome.set(src1, chrom2.get(src2));
					dst2.chromosome.set(src2, chrom1.get(src1));
					src1++;
					src2++;
				}	
			}
			return true;
		}
		
		private boolean checkAddress(Individual p1,Individual p2,Individual s1,Individual s2){
			if(p1 == s1|| p1 == s2 || p2 == s1 || p2 == s2)
				return false;
			else
				return true;		
		}
		
	}

}
