package com.example.youlucsk.XuLie;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.search.route.DrivingRouteResult;

import java.util.ArrayList;
import java.util.List;

public class xLDRR implements Parcelable {
    public List<DrivingRouteResult> drrList = new ArrayList<DrivingRouteResult>();
    public xLDRR(List<DrivingRouteResult> drrList) {
        this.drrList = drrList;
    }

    public xLDRR() {
    }


    /**
     * 该方法负责序列化
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {

//        dest.writeInt(rouList);
//        dest.writeTypedList(rouList);
        dest.writeList(drrList);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    /**
     * 负责反序列化
     */
    public static final Creator<xLDRR> CREATOR = new Creator<xLDRR>() {
        /**
         * 从序列化后的对象中创建原始对象
         */
        @Override
        public xLDRR createFromParcel(Parcel in) {
            return new xLDRR(in);
        }
        /**
         * 创建指定长度的原始对象数组
         */
        @Override
        public xLDRR[] newArray(int size) {
            return new xLDRR[size];
        }
    };


    protected xLDRR(Parcel in) {
        // 读取对象需要提供一个类加载器去读取,因为写入的时候写入了类的相关信息
//        author = in.readParcelable(Author.class.getClassLoader());

        //读取集合也分为两类,对应写入的两类
        //这一类需要用相应的类加载器去获取
        in.readList(drrList, DrivingRouteResult.class.getClassLoader()); // 对应writeList

        //这一类需要使用类的CREATOR去获取
//        in.readTypedList(rouList, RouteLine.CREATOR); //对应writeTypeList
        //authors = in.createTypedArrayList(Author.CREATOR);//对应writeTypeList

        //这里获取类加载器主要有几种方式
/*        getClass().getClassLoader();
        Thread.currentThread().getContextClassLoader();
        RouteLine.class.getClassLoader();*/
    }

}
