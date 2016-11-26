package tsp;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
* <p> Util 工具类</p>
* <p>Description: </p>
* @author GJS,FlyingFish
* @date 2016-10-29
* @see<a href ="http://www.cnblogs.com/GuoJiaSheng/p/4192301.html">GJS主页</a>
*/
public class Util {

	/**
	 * <p>读取文件。</p>
	 * 文件格式为TSP，这是由tsplib提供的数据源。
	 * @param path tsp文件路径
	 * @return
	 */
	public static CityPoint[] ReadFile(String path) {
		CityPoint[] point = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(path));
			String tempString;
			for (int i = 0; i < 5; i++) {
				reader.readLine();
				if (i == 2) {//保存第四行内容
					tempString = reader.readLine();
					
					int index=tempString.indexOf(":");
					
					String DIMENSION = tempString.substring(
							index+2, tempString.length());
					point = new CityPoint[Integer.valueOf(DIMENSION)]; // 城市个数
				}
			}

			int index = 0;
			// 读取坐标
			while ((tempString = reader.readLine()) != null) {
				if (index == point.length)
					break;
				//每个城市的坐标格式为 :序号 X坐标 Y坐标
				String ar[] = tempString.split(" ");
				int x = 0, tempx = 0, tempy = 0;
				for (int i = 0; i < ar.length; i++) {
					if (!ar[i].equals("")) {
						if (x == 1) {//城市序号不保存，所以X从1开始
							tempx = Integer.valueOf(ar[i]);
						} else if (x == 2) {
							tempy = Integer.valueOf(ar[i]);
						}
						x++;
					}
				}
				CityPoint pointTemp = new CityPoint(tempx, tempy);
				point[index] = pointTemp;
				index++;
				// pointTemp.x=

			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return point;
	}

	/**针对给定的数据源，从文件中读取保存的最优旅行方案，文件格式为opt.tour，它由tsplib提供。
	 * @parameter file opt.tour文件路径*/
	public ArrayList<Integer>GetBest(String file)
	{
		ArrayList<Integer>best=new ArrayList<Integer>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String	tempString= "";
			//跳过前5行的说明
			 reader.readLine();
			 reader.readLine();
			 reader.readLine();
			 reader.readLine();
			 reader.readLine();
	
			// 读取坐标
			while ((tempString = reader.readLine()) != null) {
				if (tempString.equals("-1"))//“-1”是城市遍历完的标志
					break;
				//让城市的编号从0开始
				best.add(Integer.valueOf(tempString)-1);
			}
			reader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return best;
	}
	
	/**
	 * 计算两个城市的距离
	 * @param begin
	 * @param end
	 * @return
	 */
	public static double Distance(CityPoint begin,CityPoint end)
	{
		double y=Math.abs(begin.y-end.y);
		double x=Math.abs(begin.x-end.x);
		return Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
	}
	
	
	public static double[][] Distance(CityPoint[] cities){
		double[][] distanceMatrix = new double[cities.length][cities.length];
		for(int i = 0;i < cities.length;i ++)
			for(int j = 0;j <cities.length; j++)
				distanceMatrix[i][j] = Distance(cities[i],cities[j]);
		return distanceMatrix;
	}
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		/*
		String FilePath = "E://a280.tsp";
		Util.ReadFile(FilePath);
        Util test=new Util();
        test.GetBest("E://att48.opt.tour");
        SA SA=new SA(1000,0.9);
     System.out.print(SA.DistanceCost(test.GetBest("E://att48.opt.tour")));
       */ 
        //测试Distance
        CityPoint[] cities = new CityPoint[3];
        cities[0] = new CityPoint(0,0);
        cities[1] = new CityPoint(3,0);
        cities[2] = new CityPoint(0,4);
        double[][] dis = Distance(cities);
        for(int i = 0;i < dis.length;i ++){
        	for(int j = 0; j < dis[i].length;j ++)
        		System.out.print(dis[i][j] + " ");
        	System.out.println();
    	}
	}

}
