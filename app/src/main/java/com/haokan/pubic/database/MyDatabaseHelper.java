package com.haokan.pubic.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.haokan.pubic.bean.BeanConvertUtil;
import com.haokan.pubic.bean.BigImageBean;
import com.haokan.pubic.bean.MainImageBean;
import com.haokan.pubic.cachesys.ACache;
import com.haokan.pubic.logsys.LogHelper;
import com.haokan.pubic.util.Values;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDatabaseHelper extends OrmLiteSqliteOpenHelper {
    /**
     * 数据库名
     */
    private static final String DB_NAME = "hklockscreen.db";

    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 4;

    /**
     * DAO对象的缓存
     */
    private Map<String, Dao> mDaos = new HashMap<String, Dao>();

    /**
     * DBHelper的单利
     */
    private static MyDatabaseHelper sInstance = null;

    private Context mContext;

    /**
     * 单例获取该Helper
     */
    public static MyDatabaseHelper getInstance(Context context) {
        //不适用传入进来的context，防止传入的是activity的话导致activity无法被释放
        context = context.getApplicationContext();
        if (sInstance == null) {
            synchronized (MyDatabaseHelper.class) {
                if (sInstance == null) {
                    sInstance = new MyDatabaseHelper(context);
                }
            }
        }
        return sInstance;
    }

    private MyDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    public synchronized Dao getDaoQuickly(Class clazz) throws Exception {
        Dao dao = null;
        String className = clazz.getSimpleName();
        if (mDaos.containsKey(className)) {
            dao = mDaos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            mDaos.put(className, dao);
        }
        return dao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        mDaos.clear();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        LogHelper.d("wangzixu", "database onCreate is called");
        try {
            TableUtils.createTable(connectionSource, BeanCollection.class);
            //版本4时, 去掉本地表, 增加了锁屏表, 集合本地数据和网络数据, 数据库迭代的历史信息注释, 不能删除
            TableUtils.createTable(connectionSource, BeanLocalImage.class);
            TableUtils.createTable(connectionSource, BeanLsImage.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion,
                          int newVersion) {
        LogHelper.d("wangzixu", "database onUpgrade is called, oldV, newV = " + oldVersion + ", " + newVersion);
        int version = oldVersion;
        if (version < 1) { //BeanCollection
            try {
                TableUtils.createTable(connectionSource, BeanCollection.class);
                version = 1;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (version < 2) { //版本2时加了BeanLocalImage表
            try {
                TableUtils.createTable(connectionSource, BeanLocalImage.class);
                version = 2;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try{
            if(version < 3){ //版本3时, 改动了bigImageBean, 增加了collect_num, share_num, 所以BeanCollection表也要增加这两个字段
                //并且增加收藏推荐详情页的需求, 所以需要区分收藏的单图和详情页, 添加了collectType字段
                getDaoQuickly(BeanCollection.class).executeRawNoArgs("ALTER TABLE table_collect ADD COLUMN collect_num INTEGER DEFAULT 0");
                getDaoQuickly(BeanCollection.class).executeRawNoArgs("ALTER TABLE table_collect ADD COLUMN share_num INTEGER DEFAULT 0");
                getDaoQuickly(BeanCollection.class).executeRawNoArgs("ALTER TABLE table_collect ADD COLUMN collectType INTEGER DEFAULT 0");
                version = 3;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if (version < 4) { //4增加了BeanLsImage表, 包含了本地的和网络的图片信息, 不在用序列化形式存储数据
            try {
                TableUtils.createTable(connectionSource, BeanLsImage.class);

                Dao daoLs = getDaoQuickly(BeanLsImage.class);
                //本地图片表中的数据信息导入
                Dao daoLocalImage = getDaoQuickly(BeanLocalImage.class);
                List<BeanLocalImage> listLocal = daoLocalImage.queryForAll();
                if (listLocal != null && listLocal.size() > 0) {
                    for (int i = 0; i < listLocal.size(); i++) {
                        BeanLocalImage beanLocalImage = listLocal.get(i);
                        BeanLsImage lsImage = new BeanLsImage();
                        lsImage.imgId = beanLocalImage.imgId;
                        lsImage.myType = 1;
                        lsImage.imgSmallUrl = beanLocalImage.imgUrl;
                        lsImage.imgBigUrl = beanLocalImage.imgUrl;
                        lsImage.imgTitle = beanLocalImage.imgTitle;
                        lsImage.imgDesc = beanLocalImage.imgDesc;
                        lsImage.create_time = beanLocalImage.create_time;

                        daoLs.create(lsImage);
                    }
                }
                TableUtils.dropTable(daoLocalImage, true);
                mDaos.remove(BeanLocalImage.class.getSimpleName());

                //导入网络图片数据, 以前是序列化存的对象
                ArrayList<BigImageBean> listNet = new ArrayList<>();
                ACache aCache = ACache.get(mContext);
                Object asObject = aCache.getAsObject(Values.AcacheKey.KEY_ACACHE_OFFLINE_JSONNAME);
                LogHelper.d("wangzixu", "getLsData asObject = " + asObject);
                if (asObject != null && asObject instanceof ArrayList) {
                    try {
                        ArrayList<BigImageBean> tempList = (ArrayList<BigImageBean>) asObject;
                        BigImageBean bigImageBean = tempList.get(0); //验证是否会强转失败, 因为4.0.1之前老版本的数据存储的是mainImageBean
                        listNet.addAll(tempList);
                    } catch (Exception e) {
                        LogHelper.d("wangzixu", "getLsData 强转失败, 老数据强转成mainimageBean");
                        ArrayList<MainImageBean> oldList = (ArrayList<MainImageBean>) asObject;
                        for (int i = 0; i < oldList.size(); i++) {
                            MainImageBean imageBean = oldList.get(i);
                            BigImageBean bigImageBean = BeanConvertUtil.mainImageBean2BigImageBean(imageBean);
                            listNet.add(bigImageBean);
                        }
                    }
                    LogHelper.d("wangzixu", "getLsData list = " + listNet.size());

                    if (listNet.size() > 0) {
                        for (int i = 0; i < listNet.size(); i++) {
                            BigImageBean bigImageBean = listNet.get(i);
                            BeanLsImage lsImage = BeanConvertUtil.BigImg2LsImageBean(bigImageBean);
                            daoLs.create(lsImage);
                        }
                    }
                }

                version = 4;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        删除字段
//        getUserDao().executeRawNoArgs("ALTER TABLE user_db DROP COLUMN age")
//        二、修改字段名：
//        alter table 表名 rename column A to B
//        三、修改字段类型：
//        alter table 表名 alter column UnitPrice decimal(18, 4) not null
//        三、修改增加字段：
//        alter table 表名 ADD 字段 类型 NOT NULL Default 0
    }
}
