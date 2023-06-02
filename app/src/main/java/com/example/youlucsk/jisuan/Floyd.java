package com.example.youlucsk.jisuan;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/*
 *                0    1    2    3
 *             0 {0,   5,   MAX, 7},
               1 {MAX, 0,   4,   2},
               2 {3,   3,   0,   2},
               3 {MAX, MAX, 1,   0}
 *
 * */

public class Floyd {

	// 表示无穷大 即不可达
//    public static double MAX = Integer.MAX_VALUE;
	// 距离矩阵
	public static int[][] dist;
	// 路径Path矩阵
	public static int[][] path;

	// 所有的结果
	static List<fanx> lcts = new ArrayList<>();



	public Floyd(int sj[][]) {
		int size = sj.length;
		this.path = new int[size][size];
		this.dist = new int[size][size];

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				this.path[i][j] = sj[i][j] <= 0 ? -1 : 1;
				this.dist[i][j] = sj[i][j];
			}
		}

	}

	public void pint() {

		for (int i = 0; i < lcts.size(); i++) {
			System.out.println("----------------------------第" + i + "条路线------------------------------");
			List<Integer> ljcs = lcts.get(i).getLjs();
			for (int j = 0; j < ljcs.size(); j++) {
				System.out.print(ljcs.get(j));
				if (!(j >= (ljcs.size() - 1))) {
					System.out.print("->");
				}
			}
			System.out.println("长度为" + lcts.get(i).getCd());

			System.out.println();
			System.out.println();
			System.out.println();

		}
	}

	public fanx Sf(int qis) {

		System.out.println("开始");
		// 路径顺序
		List<Integer> ljs = new ArrayList<>();
//		ljs.add(new Integer(qis));
		int[] srsz = new int[path.length];
		for(int i = 0;i<srsz.length;i++) {
			srsz[i] = i;
		}
		boolean[] cdl = new boolean[path.length];

		System.out.println("进入");
		sx(cdl, srsz, ljs, qis);

		pint();

		return lcts.get(panduan());

	}


	public int panduan() {
		int sj = 0;
		for(int i = 1; i <lcts.size();i++) {
			if(lcts.get(sj).getCd()>lcts.get(i).getCd()) {
				sj = i;
			}
		}
		return sj;
	}


	//获取每一条路径
	public static void sx(boolean[] flags, int[] number, List<Integer> ljs, int qis) {
		//如果flags全为true 则输出结果
//		System.out.println("进入了");
		boolean print = true;
		for (int i=0; i<number.length;i++) {
//			System.out.println("第一个循环");
			print = print && flags[i];
		}
		if (print) {
//			System.out.println("添加完了");
//			System.out.println(nowStr.substring(0, nowStr.length() - 2) + "]");
			jslc(ljs,qis);
			return;
		}
		for (int i=0; i<number.length;i++) {
//			System.out.println("进入了第二个循环");
			//该数字置还没使用
			if (!flags[i]) {
				//将该数字置为已用
				flags[i] = true;
				ljs.add(new Integer(number[i]));
				sx(flags, number, ljs, qis);
//				sx(flags, number, nowStr + number[i] + ", ");
				//方法结束后将该数字置为未用
				ljs.remove(ljs.size()-1);
				flags[i] = false;
			}
		}
	}





	public static void jslc(List<Integer> ljs, int qis) {
		ljs.add(new Integer(0));
		int sum = 0;
		int shs = 0;
		for(int i = 0;i < ljs.size()-1;i++) {
			sum += dist[ljs.get(i)][ljs.get(i+1)];
			shs += path[ljs.get(i)][ljs.get(i+1)];

		}
		if(shs == ljs.size()-1) {
			if(ljs.get(0) == qis) {
				lcts.add(new fanx(depCopy(ljs),sum));
				System.out.println();
				for(Integer lx:ljs) {
					System.out.print(lx+"->");
				}
				System.out.println("添加路径成功");

			}else {
				System.out.println("开头不正确");
			}

		}else {
			System.out.println("添加路径失败");
		}
	      ljs.remove(ljs.size()-1);
//	      ljs.remove(ljs.size()-1);
//		ljs.remove(ljs.size()-1);


	}


/*	public static void main(String[] args) {
		// 表示无穷大 即不可达

		double[][] matrix = { { 0, 5, 3, 6 },
				{ 5, 0, 4, 2 },
				{ 3, 4, 0, 1 },
				{ 6, 2, 1, 0 }
		};
		Floyd flyd = new Floyd(matrix);

		List<Integer> ljss = flyd.Sf(0).getLjs();
		System.out.println("最终结果为");
		System.out.println("距离为："+flyd.Sf(0).getCd());
		for(Integer lx:ljss) {
			System.out.print(lx+"->");
		}
		*//* 1 2 3 4
		 * 1 2 4 3
		 * 1 3 2 4
		 * 1 3 4 2
		 * 1 4 2 3
		 * 1 4 3 2
		 *
		 *
		 * *//*

	}*/


	//List深度拷贝
	/***
	 * 方法一对集合进行深拷贝 注意需要对泛型类进行序列化(实现Serializable)
	 *
	 * @param srcList
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> depCopy(List<T> srcList) {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		try {
			ObjectOutputStream out = new ObjectOutputStream(byteOut);
			out.writeObject(srcList);

			ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
			ObjectInputStream inStream = new ObjectInputStream(byteIn);
			List<T> destList = (List<T>) inStream.readObject();
			return destList;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}