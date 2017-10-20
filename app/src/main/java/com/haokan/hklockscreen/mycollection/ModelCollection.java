package com.haokan.hklockscreen.mycollection;

/**
 * Created by wangzixu on 2016/11/28.
 * 所有收藏相关的接口
 */
public class ModelCollection {
//    public static void getCollectedDantuData(final Context mContext, int page, int pageSize, final onDataResponseListener<List<MainImageBean>> listener) {
//        if (listener == null) {
//            return;
//        }
//
//        if (!HttpStatusManager.checkNetWorkConnect(mContext)) {
//            listener.onNetError();
//            return;
//        }
//
//        String url = UrlsUtil.getCollection(mContext, "", page, pageSize, 6, App.sBigImgSize, Values.ImageSize.SIZE_360x640);
//        Observable<DataResponse<List<MainImageBean>>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getCollectionImages(url);
//        observable.map(new Func1<DataResponse<List<MainImageBean>>, DataResponse<List<MainImageBean>>>() {
//            @Override
//            public DataResponse<List<MainImageBean>> call(DataResponse<List<MainImageBean>> listDataResponse) {
//                DataResponse<List<MainImageBean>> response = HttpStatusManager.checkResponseSuccess(listDataResponse);
//                if (response.getCode() == 200 && response.getData() != null
//                        && response.getData() != null && response.getData().size() > 0) {
//                    List<MainImageBean> list = response.getData();
//                    ImgAndTagWallManager imgAndTagWallManager = ImgAndTagWallManager.getInstance(mContext);
//                    imgAndTagWallManager.initTagsPosition(list);
//                }
//                return response;
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<DataResponse<List<MainImageBean>>>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        listener.onDataFailed(e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(DataResponse<List<MainImageBean>> listDataResponse) {
//                        if (listDataResponse.getCode() == 200) {
//                            if (listDataResponse.getData() == null || listDataResponse.getData().size() == 0) {
//                                listener.onDataEmpty();
//                            } else {
//                                listener.onDataSucess(listDataResponse.getData());
//                            }
//                        } else {
//                            listener.onDataFailed(listDataResponse.getMessage());
//                        }
//                    }
//
//                    @Override
//                    public void onStart() {
//                        listener.onStart();
//                    }
//                });
//    }
//
//    public static void getCollectedZutuData(final Context mContext, int page, int pageSize, final onDataResponseListener listener) {
//        if (listener == null) {
//            return;
//        }
//
//        if (!HttpStatusManager.checkNetWorkConnect(mContext)) {
//            listener.onNetError();
//            return;
//        }
//
//        String url = UrlsUtil.getCollection(mContext, "", page, pageSize, 5, App.sBigImgSize, Values.ImageSize.SIZE_360x640);
//        Observable<DataResponse<List<MainImageBean>>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getCollectionImages(url);
//        observable.map(new Func1<DataResponse<List<MainImageBean>>, DataResponse<List<MainImageBean>>>() {
//            @Override
//            public DataResponse<List<MainImageBean>> call(DataResponse<List<MainImageBean>> listDataResponse) {
//                DataResponse<List<MainImageBean>> response = HttpStatusManager.checkResponseSuccess(listDataResponse);
//                return response;
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<DataResponse<List<MainImageBean>>>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        listener.onDataFailed(e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(DataResponse<List<MainImageBean>> listDataResponse) {
//                        if (listDataResponse.getCode() == 200) {
//                            if (listDataResponse.getData() == null || listDataResponse.getData().size() == 0) {
//                                listener.onDataEmpty();
//                            } else {
//                                listener.onDataSucess(listDataResponse.getData());
//                            }
//                        } else {
//                            listener.onDataFailed(listDataResponse.getMessage());
//                        }
//                    }
//
//                    @Override
//                    public void onStart() {
//                        listener.onStart();
//                    }
//                });
//    }
//
//    public static void delCollection_Dantu_Zutu(final Context mContext, @NonNull List<MainImageBean> selectItems, final onDataResponseListener listener) {
//        if (listener == null) {
//            return;
//        }
//
//        if (!HttpStatusManager.checkNetWorkConnect(mContext)) {
//            listener.onNetError();
//            return;
//        }
//        List<RequestBeanDelCollection> requestList = new ArrayList<>();
//        for (int i = 0; i < selectItems.size(); i++) {
//            MainImageBean imageBean = selectItems.get(i);
//            RequestBeanDelCollection requestBean = new RequestBeanDelCollection();
//            requestBean.setType(imageBean.getType() == 2 ? "5" : "1");
//            requestBean.setCid(imageBean.getId());
//            requestList.add(requestBean);
//        }
//        String url = UrlsUtil.delCollection(mContext, JsonUtil.toJson(requestList));
//        ModelBase.sendBaseRequest(mContext, url, listener);
//    }
//
//
//    public static void addCollection_Dantu_Zutu(Context context, @NonNull MainImageBean bean, @NonNull final onDataResponseListener listener) {
//        String url = UrlsUtil.addCollection(context, bean.getType() == 2 ? 5 : 1, bean.getImage_id());
//        ModelBase.sendBaseRequest(context, url, listener);
//    }
//
//    public static void addCollectionCp_Datebase(final Context context, final String cpId) {
//        App.sWorker.post(new Runnable() {
//            @Override
//            public void run() {
//                LogHelper.d("wangzixu", "addCollectionCp_Datebase called");
//                MyDatabaseHelper instance = MyDatabaseHelper.getInstance(context);
//                try {
//                    Dao dao = instance.getDaoQuickly(LockScreenFollowCp.class);
//                    int num = dao.updateRaw("UPDATE tablelockfollowcp SET isFollow=? where cpId=?", "1", cpId);
//                    LogHelper.d("wangzixu", "addCollectionCp_Datebase success num = " + num);
//                } catch (Exception e) {
//                    LogHelper.d("wangzixu", "addCollectionCp_Datebase failed");
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    public static void delCollectionCp_Datebase(final Context context, final String cpId) {
//        App.sWorker.post(new Runnable() {
//            @Override
//            public void run() {
//                LogHelper.d("wangzixu", "delCollectionCp_Datebase called");
//                MyDatabaseHelper instance = MyDatabaseHelper.getInstance(context);
//                try {
//                    Dao dao = instance.getDaoQuickly(LockScreenFollowCp.class);
//                    int num = dao.updateRaw("UPDATE tablelockfollowcp SET isFollow=? where cpId=?", "0", cpId);
//                    LogHelper.d("wangzixu", "delCollectionCp_Datebase success num = " + num);
//                } catch (Exception e) {
//                    LogHelper.d("wangzixu", "delCollectionCp_Datebase failed");
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    public static void addCollectionCp(Context context, @NonNull String cpId, @NonNull final onDataResponseListener listener) {
////        String url = UrlsUtil.addCollection(context, 3, cpId);
////        ModelBase.sendBaseRequest(context, url, listener);
//        if (listener == null) {
//            return;
//        }
//
//        if (!HttpStatusManager.checkNetWorkConnect(context)) {
//            listener.onNetError();
//            return;
//        }
//
//        final RequestBody_8015 body = new RequestBody_8015();
//        body.setdId(App.DID);
//        body.setcIds(cpId);
//        body.setOp(1);
//        body.setType(3);
//
//        final RequestEntity<RequestBody_8015> requestEntity = new RequestEntity<>();
//        RequestHeader<RequestBody_8015> header = new RequestHeader(UrlsUtil_Java.TransactionType.TYPE_8015, body);
//        requestEntity.setBody(body);
//        requestEntity.setHeader(header);
//
//        listener.onStart();
//        Observable<ResponseEntity> observable = HttpRetrofitManager.getInstance().getRetrofitService().changeCollection(UrlsUtil_Java.HostMethod.getJavaUrl_Switch_Followed(), requestEntity);
//        observable
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<ResponseEntity>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        listener.onDataFailed(e.getMessage());
//                        e.printStackTrace();
//                        LogHelper.d("add_del_CollectionImageToServer", "onError ");
//                    }
//
//                    @Override
//                    public void onNext(ResponseEntity res) {
//                        LogHelper.d("add_del_CollectionImageToServer", "成功了 ");
//                        listener.onDataSucess(res);
//                    }
//
//                    @Override
//                    public void onStart() {
//                    }
//                });
//    }
//
//    public static void delCollectionCp(Context context, @NonNull ArrayList<CpBean> cps, @NonNull final onDataResponseListener listener) {
//        List<RequestBeanDelCollection> requestList = new ArrayList<>();
//        for (int i = 0; i < cps.size(); i++) {
//            RequestBeanDelCollection bean1 = new RequestBeanDelCollection();
//            bean1.setType("3");
//            bean1.setCid(cps.get(i).getCp_id());
//            requestList.add(bean1);
//        }
//        String url = UrlsUtil.delCollection(context, JsonUtil.toJson(requestList));
//        ModelBase.sendBaseRequest(context, url, listener);
//    }
//
//    public static void delCollectionCp(Context context, @NonNull String cpId, @NonNull final onDataResponseListener listener) {
////        List<RequestBeanDelCollection> requestList = new ArrayList<>();
////        RequestBeanDelCollection bean1 = new RequestBeanDelCollection();
////        bean1.setType("3");
////        bean1.setCid(cpId);
////        requestList.add(bean1);
////        String url = UrlsUtil.delCollection(context, JsonUtil.toJson(requestList));
////        ModelBase.sendBaseRequest(context, url, listener);
//
//        if (listener == null) {
//            return;
//        }
//
//        if (!HttpStatusManager.checkNetWorkConnect(context)) {
//            listener.onNetError();
//            return;
//        }
//
//        final RequestBody_8015 body = new RequestBody_8015();
//        body.setdId(App.DID);
//        body.setcIds(cpId);
//        body.setOp(0);
//        body.setType(3);
//
//        final RequestEntity<RequestBody_8015> requestEntity = new RequestEntity<>();
//        RequestHeader<RequestBody_8015> header = new RequestHeader(UrlsUtil_Java.TransactionType.TYPE_8015, body);
//        requestEntity.setBody(body);
//        requestEntity.setHeader(header);
//
//        listener.onStart();
//        Observable<ResponseEntity> observable = HttpRetrofitManager.getInstance().getRetrofitService().changeCollection(UrlsUtil_Java.HostMethod.getJavaUrl_Switch_Followed(), requestEntity);
//        observable
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<ResponseEntity>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        listener.onDataFailed(e.getMessage());
//                        e.printStackTrace();
//                        LogHelper.d("add_del_CollectionImageToServer", "onError ");
//                    }
//
//                    @Override
//                    public void onNext(ResponseEntity res) {
//                        LogHelper.d("add_del_CollectionImageToServer", "成功了 ");
//                        listener.onDataSucess(res);
//                    }
//
//                    @Override
//                    public void onStart() {
//                    }
//                });
//    }
//
//    public static void getAllCollectCp(Context mContext, @NonNull final onDataResponseListener<List<CpBean>> listener) {
//        if (listener == null) {
//            return;
//        }
//        if (!HttpStatusManager.checkNetWorkConnect(mContext)) {
//            listener.onNetError();
//            return;
//        }
//        listener.onStart();
//        //分页的2个参数是无效的，随便传
//        String url = UrlsUtil.getCollection(mContext, "", 1, 3000, 3, App.sBigImgSize, App.sLoadingImgSize);
//        Observable<DataResponse<List<CpBean>>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getCollectionCp(url);
//        observable
//                .map(new Func1<DataResponse<List<CpBean>>, DataResponse<List<CpBean>>>() {
//                    @Override
//                    public DataResponse<List<CpBean>> call(DataResponse<List<CpBean>> listDataResponse) {
//                        DataResponse<List<CpBean>> response = HttpStatusManager.checkResponseSuccess(listDataResponse);
//                        return response;
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<DataResponse<List<CpBean>>>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        listener.onDataFailed(e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(DataResponse<List<CpBean>> listDataResponse) {
//                        if (listDataResponse.getCode() == 200) {
//                            if (listDataResponse.getData() == null
//                                    || listDataResponse.getData() == null
//                                    || listDataResponse.getData().size() == 0) {
//                                listener.onDataEmpty();
//                            } else {
//                                listener.onDataSucess(listDataResponse.getData());
//                            }
//                        } else {
//                            listener.onDataFailed(listDataResponse.getMessage());
//                        }
//                    }
//                });
//
//    }
//
//
//    public static void addCollectionTag(Context mContext, @NonNull String tagId, @NonNull final onDataResponseListener listener) {
//        String url = UrlsUtil.addCollection(mContext, 2, tagId);
//        ModelBase.sendBaseRequest(mContext, url, listener);
//    }
//
//    public static void delCollectionTag(Context mContext, @NonNull String tagId, @NonNull final onDataResponseListener listener) {
//        RequestBeanDelCollection bean = new RequestBeanDelCollection();
//        List<RequestBeanDelCollection> list = new ArrayList<>();
//        bean.setType("2");
//        bean.setCid(tagId);
//        list.add(bean);
//        String url = UrlsUtil.delCollection(mContext, JsonUtil.toJson(list));
//        ModelBase.sendBaseRequest(mContext, url, listener);
//    }
//
//    public static void getAllCollectTags(Context mContext, @NonNull final onDataResponseListener<List<TagBean>> listener) {
//        if (listener == null) {
//            return;
//        }
//        if (!HttpStatusManager.checkNetWorkConnect(mContext)) {
//            listener.onNetError();
//            return;
//        }
//        listener.onStart();
//        //分页的2个参数是无效的，随便传
//        String url = UrlsUtil.getCollection(mContext, "", 1, 18, 2, App.sBigImgSize, App.sLoadingImgSize);
//        Observable<DataResponse<List<TagBean>>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getCollectionTags1(url);
//        observable
//                .map(new Func1<DataResponse<List<TagBean>>, DataResponse<List<TagBean>>>() {
//                    @Override
//                    public DataResponse<List<TagBean>> call(DataResponse<List<TagBean>> listDataResponse) {
//                        DataResponse<List<TagBean>> response = HttpStatusManager.checkResponseSuccess(listDataResponse);
//                        return response;
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<DataResponse<List<TagBean>>>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        listener.onDataFailed(e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(DataResponse<List<TagBean>> listDataResponse) {
//                        if (listDataResponse.getCode() == 200) {
//                            if (listDataResponse.getData() == null
//                                    || listDataResponse.getData() == null
//                                    || listDataResponse.getData().size() == 0) {
//                                listener.onDataEmpty();
//                            } else {
//                                listener.onDataSucess(listDataResponse.getData());
//                            }
//                        } else {
//                            listener.onDataFailed(listDataResponse.getMessage());
//                        }
//                    }
//                });
//    }
//
//    /**
//     * private String cIds;//逗号分隔,,,
//     * private int op;//1表示收藏 0表示取消,,,
//     * private int type;// 1单图，2标签，3CP，4分类，5组图
//     */
//    public static void addCollectionImage(final Context context, final MainImageBean bean, final onDataResponseListener listener) {
//        if (listener == null) {
//            return;
//        }
//
//        //存本地
//        Observable.create(new Observable.OnSubscribe<Object>() {
//            @Override
//            public void call(Subscriber<? super Object> subscriber) {
//                synchronized (ModelCollection.class) {
//                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                        subscriber.onError(new Throwable("sd卡不可用"));
//                        return;
//                    }
//                    String path = Environment.getExternalStorageDirectory().toString() + Values.Path.PATH_COLLECT_DIR;
//                    File dir = new File(path);
//                    if (!dir.mkdirs() && !dir.isDirectory()) {
//                        subscriber.onError(new Throwable("addCollectionImage create dir failed dir"));
//                        return;
//                    }
//                    String name = "img_" + System.currentTimeMillis() + ".jpg";
//                    File file = new File(dir, name);
//                    try {
//                        Bitmap bitmap;
//                        if (bean.image_url.startsWith("hk_def_imgs")) {
//                            bitmap = AssetsImageLoader.loadAssetsImageBitmap(context, bean.image_url);
//                        } else {
//                            bitmap = Glide.with(context).load(bean.image_url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
//                        }
//                        if (bitmap != null) {
//                            boolean sucess = FileUtil.saveBitmapToFile(context, bitmap, file, false);
//                            if (sucess) {
//                                final ContentValues values = new ContentValues();
//                                values.put("imageId", bean.image_id);
//                                values.put("albumId", bean.id);
//                                values.put("image_url", file.getAbsolutePath());
//                                values.put("title", bean.getTitle());
//                                values.put("content", bean.getContent());
//                                values.put("cp_name", bean.getCp_name());
//                                values.put("url_title", bean.getUrl_title());
//                                values.put("url_click", bean.getUrl_click());
//                                values.put("type", bean.getType());
//                                values.put("is_like", bean.getIs_like());
//                                values.put("create_time", System.currentTimeMillis() + "");
//                                context.getContentResolver().insert(HaokanProvider.URI_PROVIDER_LOCK_COLLECT, values);
//                                subscriber.onNext(null);
//                                subscriber.onCompleted();
//                            } else {
//                                subscriber.onError(new Throwable("addCollectionImage saveBitmapToFile failed"));
//                            }
//                        } else {
//                            subscriber.onError(new Throwable("addCollectionImage bitmap is null"));
//                        }
//                    } catch (Exception e) {
//                        subscriber.onError(e);
//                    }
//                }
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Object>() {
//
//                    @Override
//                    public void onStart() {
//                        listener.onStart();
//                    }
//
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        listener.onDataFailed(throwable.getMessage());
//
//                    }
//
//                    @Override
//                    public void onNext(Object o) {
//                        listener.onDataSucess(o);
//                    }
//                });
//
//
//        final RequestBody_8015 body = new RequestBody_8015();
//        body.setdId(App.DID);
//        body.setcIds(bean.image_id);
//        body.setOp(1);
//        body.setType(1);
//
//        if (!HttpStatusManager.isWifi(context)) { //非wifi,存储网络请求,待有wifi时发送
//            Observable.create(new Observable.OnSubscribe<Object>() {
//                @Override
//                public void call(Subscriber<? super Object> subscriber) {
//                    try {
//                        LogHelper.d("addCollectionImage", "存网络请求");
//                        ContentValues values = new ContentValues();
//                        values.put("type", 8015);
//                        values.put("url", UrlsUtil_Java.URL_HOST);
//                        values.put("reqBody", JsonUtil.toJson(body));
//                        values.put("create_time", System.currentTimeMillis());
//
//                        context.getContentResolver().insert(HaokanProvider.URI_PROVIDER_WIFI_REQUEST, values);
//                        subscriber.onNext(null);
//                        subscriber.onCompleted();
//                        LogHelper.d("addCollectionImage", "存网络请求 成功");
//                    } catch (Exception e) {
//                        subscriber.onNext(null);
//                        subscriber.onCompleted();
//                    }
//                }
//            })
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe();
//            return;
//        }
//
//        add_del_CollectionImageToServer(context, body);
//    }
//
//    public static void add_del_CollectionImageToServer(final Context context, final RequestBody_8015 body) {
//        if (true)   //屏蔽
//            return;
//        if (body == null || context == null) {
//            return;
//        }
//
//        RequestEntity<RequestBody_8015> requestEntity = new RequestEntity<>();
//        RequestHeader<RequestBody_8015> header = new RequestHeader(UrlsUtil_Java.TransactionType.TYPE_8015, body);
//        requestEntity.setBody(body);
//        requestEntity.setHeader(header);
//        Observable<ResponseEntity> observable = HttpRetrofitManager.getInstance().getRetrofitService().changeCollection(UrlsUtil_Java.URL_HOST, requestEntity);
//        observable
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<ResponseEntity>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                        LogHelper.d("add_del_CollectionImageToServer", "onError ");
//                    }
//
//                    @Override
//                    public void onNext(ResponseEntity res) {
//                        LogHelper.d("add_del_CollectionImageToServer", "成功了 ");
//                    }
//
//                    @Override
//                    public void onStart() {
//                    }
//                });
//    }
//
//    /**
//     * private String cIds;//逗号分隔,,,
//     * private int op;//1表示收藏 0表示取消,,,
//     * private int type;// 1单图，2标签，3CP，4分类，5组图
//     */
//    public static void delCollectionImage(final Context context, final MainImageBean bean, final onDataResponseListener listener) {
//        if (listener == null) {
//            return;
//        }
//        //本地
//        Observable.create(new Observable.OnSubscribe<Object>() {
//            @Override
//            public void call(Subscriber<? super Object> subscriber) {
//                synchronized (ModelCollection.class) {
//                    try {
//                        context.getContentResolver().delete(HaokanProvider.URI_PROVIDER_LOCK_COLLECT, "imageId=?", new String[]{bean.image_id});
//                        subscriber.onNext(null);
//                        subscriber.onCompleted();
//                    } catch (Exception e) {
//                        //                    subscriber.onNext(null);
//                        //                    subscriber.onCompleted();
//                        subscriber.onError(e);
//                    }
//                }
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Object>() {
//
//                    @Override
//                    public void onStart() {
//                        listener.onStart();
//                    }
//
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        listener.onDataFailed(throwable.getMessage());
//
//                    }
//
//                    @Override
//                    public void onNext(Object o) {
//                        listener.onDataSucess(o);
//                    }
//                });
//
//
//        final RequestBody_8015 body = new RequestBody_8015();
//        body.setdId(App.DID);
//        body.setcIds(bean.image_id);
//        body.setOp(0);
//        body.setType(1);
//
//        if (!HttpStatusManager.isWifi(context)) { //非wifi,存储网络请求,待有wifi时发送
//            Observable.create(new Observable.OnSubscribe<Object>() {
//                @Override
//                public void call(Subscriber<? super Object> subscriber) {
//                    try {
//                        LogHelper.d("delCollectionImage", "存网络请求");
//
//                        ContentValues values = new ContentValues();
//                        values.put("type", 8015);
//                        values.put("url", UrlsUtil_Java.URL_HOST);
//                        values.put("reqBody", JsonUtil.toJson(body));
//                        values.put("create_time", System.currentTimeMillis());
//
//                        context.getContentResolver().insert(HaokanProvider.URI_PROVIDER_WIFI_REQUEST, values);
//                        subscriber.onNext(null);
//                        subscriber.onCompleted();
//                        LogHelper.d("delCollectionImage", "存网络请求 成功");
//                    } catch (Exception e) {
//                        subscriber.onNext(null);
//                        subscriber.onCompleted();
//                    }
//                }
//            })
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe();
//            return;
//        }
//
//        add_del_CollectionImageToServer(context, body);
//    }
//
//    /**
//     * 批量删除
//     */
//    public static void delCollectionImageBatch(final Context context, final List<NewImageBean> list, final onDataResponseListener listener) {
//        if (list == null || listener == null) {
//            return;
//        }
//        //本地
//        //存本地
//        Observable.create(new Observable.OnSubscribe<Object>() {
//            @Override
//            public void call(Subscriber<? super Object> subscriber) {
//                try {
//                    for (int i = 0; i < list.size(); i++) {
//                        context.getContentResolver().delete(HaokanProvider.URI_PROVIDER_LOCK_COLLECT, "imageId=?", new String[]{list.get(i).imgId});
//                    }
//                    subscriber.onNext(null);
//                    subscriber.onCompleted();
//                } catch (Exception e) {
//                    subscriber.onNext(null);
//                    subscriber.onCompleted();
////                    subscriber.onError(e);
//                }
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Object>() {
//
//                    @Override
//                    public void onStart() {
//                        listener.onStart();
//                    }
//
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        listener.onDataFailed(throwable.getMessage());
//
//                    }
//
//                    @Override
//                    public void onNext(Object o) {
//                        listener.onDataSucess(o);
//                    }
//                });
//
//
//        final RequestBody_8015 body = new RequestBody_8015();
//        body.setdId(App.DID);
//        String cid = "";
//        for (int i = 0; i < list.size(); i++) {
//            cid = cid + list.get(i).imgId + ",";
//        }
//        cid = cid.substring(0, cid.lastIndexOf(","));
//        body.setcIds(cid);
//        body.setOp(0);
//        body.setType(1);
//
//        if (!HttpStatusManager.isWifi(context)) { //非wifi,存储网络请求,待有wifi时发送
//            Observable.create(new Observable.OnSubscribe<Object>() {
//                @Override
//                public void call(Subscriber<? super Object> subscriber) {
//                    try {
//                        LogHelper.d("delCollectionImage", "存网络请求");
//                        ContentValues values = new ContentValues();
//                        values.put("type", 8015);
//                        values.put("url", UrlsUtil_Java.URL_HOST);
//                        values.put("reqBody", JsonUtil.toJson(body));
//                        values.put("create_time", System.currentTimeMillis());
//
//                        context.getContentResolver().insert(HaokanProvider.URI_PROVIDER_WIFI_REQUEST, values);
//                        subscriber.onNext(null);
//                        subscriber.onCompleted();
//                        LogHelper.d("delCollectionImage", "存网络请求 成功");
//                    } catch (Exception e) {
//                        subscriber.onNext(null);
//                        subscriber.onCompleted();
//                    }
//                }
//            })
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe();
//            return;
//        }
//
//        add_del_CollectionImageToServer(context, body);
//    }
//
//    public static void getCollectionImages(final Context context, final onDataResponseListener<List<NewImageBean>> listener) {
//        if (listener == null) {
//            return;
//        }
//
//        Observable.create(new Observable.OnSubscribe<List<NewImageBean>>() {
//            @Override
//            public void call(Subscriber<? super List<NewImageBean>> subscriber) {
//                ArrayList<NewImageBean> newImageBeens = new ArrayList<>();
//                Cursor cursor = null;
//                try {
//                    cursor = context.getContentResolver().query(HaokanProvider.URI_PROVIDER_LOCK_COLLECT, null, null, null, "create_time DESC");
//                    if (cursor != null) {
//                        int iImageId = cursor.getColumnIndex("imageId");
//                        int iAlbumId = cursor.getColumnIndex("albumId");
//                        int iImageUrl = cursor.getColumnIndex("image_url");
//                        int iTitle = cursor.getColumnIndex("title");
//                        int iContent = cursor.getColumnIndex("content");
//                        int iCpName = cursor.getColumnIndex("cp_name");
//                        int iLinkTitle = cursor.getColumnIndex("url_title");
//                        int iLinkUrl = cursor.getColumnIndex("url_click");
//                        int iType = cursor.getColumnIndex("type");
//                        int iLike = cursor.getColumnIndex("is_like");
//
//                        while (cursor.moveToNext()) {
//                            NewImageBean bean = new NewImageBean();
//                            bean.imgId = cursor.getString(iImageId);
//                            bean.albumId = cursor.getColumnName(iAlbumId);
//                            bean.imgTitle = cursor.getString(iTitle);
//                            bean.imgDesc = cursor.getString(iContent);
//                            bean.linkTitle = cursor.getString(iLinkTitle);
//                            bean.linkUrl = cursor.getString(iLinkUrl);
//                            bean.cpName = cursor.getString(iCpName);
//                            bean.type = cursor.getInt(iType);
//                            bean.isLike = cursor.getInt(iLike);
//                            bean.imgBigUrl = cursor.getString(iImageUrl);
//                            bean.imgSmallUrl = bean.imgBigUrl;
//                            bean.isCollect = 1;
//                            newImageBeens.add(bean);
//                        }
//
//                        ArrayList<NewImageBean> tempList = new ArrayList<NewImageBean>();
//                        for (int i = 0; i < newImageBeens.size(); i++) {
//                            NewImageBean bean = newImageBeens.get(i);
//                            File file = new File(bean.imgBigUrl);
//                            if (!file.exists() || file.isDirectory()) { //这条数据不是图片或者不存在,删除
//                                tempList.add(bean);
//                                context.getContentResolver().delete(HaokanProvider.URI_PROVIDER_LOCK_COLLECT, "imageId=?", new String[]{bean.imgId});
//                            }
//                        }
//                        if (tempList.size() > 0) {
//                            newImageBeens.removeAll(tempList);
//                        }
//                        tempList.clear();
//                        tempList = null;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (cursor != null) {
//                        cursor.close();
//                    }
//                }
//                subscriber.onNext(newImageBeens);
//                subscriber.onCompleted();
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<List<NewImageBean>>() {
//
//                    @Override
//                    public void onStart() {
//                        listener.onStart();
//                    }
//
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        listener.onDataFailed(throwable.getMessage());
//
//                    }
//
//                    @Override
//                    public void onNext(List<NewImageBean> list) {
//                        if (list == null || list.isEmpty()) {
//                            listener.onDataEmpty();
//                        } else {
//                            listener.onDataSucess(list);
//                        }
//                    }
//                });
//
//    }
//
//    /**
//     * //1:单图，5：组图，6：单图和组图
//     */
//    public static void getCollections(final Context mContext, int type, int pageIndex, int pageSize, final onDataResponseListener<List<NewImageBean>> listener) {
//        if (listener == null) {
//            return;
//        }
//
//        if (!HttpStatusManager.checkNetWorkConnect(mContext)) {
//            listener.onNetError();
//            return;
//        }
//
//        final RequestEntity<RequestBody_8018> requestEntity = new RequestEntity<>();
//        RequestBody_8018 body = new RequestBody_8018();
//        body.dId = App.DID;
//        body.eId = Integer.valueOf(App.eid);
//        body.type = type;//1:单图，5：组图，6：单图和组图
//        body.pageIndex = pageIndex;
//        body.pageSize = pageSize;
//        body.childSize = App.sZutuThumbnailSize;
//        body.imageSize = App.sBigImgSize;
//        body.loadingSize = App.sLoadingImgSize;
//        body.language = App.sLanguage_code;
//        RequestHeader<RequestBody_8018> header = new RequestHeader(UrlsUtil_Java.TransactionType.TYPE_8018, body);
//        requestEntity.setHeader(header);
//        requestEntity.setBody(body);
//
//        Observable<ResponseEntity<ResponseBody_8018>> observable = HttpRetrofitManager.getInstance().getRetrofitService().getCollection(UrlsUtil_Java.URL_HOST, requestEntity);
//        observable
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<ResponseEntity<ResponseBody_8018>>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        listener.onDataFailed(e.getMessage());
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onNext(ResponseEntity<ResponseBody_8018> res) {
//                        if (res.getHeader().getResCode() == 0) {
//                            ArrayList<NewImageBean> list = res.getBody().list;
//                            if (list == null || list.size() == 0) {
//                                listener.onDataEmpty();
//                            } else {
//                                listener.onDataSucess(list);
//                            }
//                        } else {
//                            listener.onDataFailed(res.getHeader().getResMsg());
//                        }
//                    }
//
//                    @Override
//                    public void onStart() {
//                        listener.onStart();
//                    }
//                });
//    }
}
