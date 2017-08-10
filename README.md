## 内容包括

这个一个封装好的 okhttp 的库，里面包含了如下：


 - get方法，可以添加参数
 - 封装好的Gson
 - 下载图片，再自定义图片大小进行压缩后存储
 - 下载文件，并显示进度
 - 多线程下载文件，并显示进度
 - post发送 key - value
 - post 发送字符串或者json
 - post 发送文件，并显示上传进度
 - post 发送表单，变显示上传进度

##2 、怎么使用


**Download**
	
Step 1、 Add the JitPack repository in your root build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependency
```
dependencies {
	        compile 'com.github.LillteZheng:okhttplib:v1.1'
	}
```

## 3、初始化
```
 OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(TIME_OUT+TIME_OUT,TimeUnit.SECONDS)
                    .cookieJar(new PersistentCookieJar(new SetCookieCache(),new SharedPrefsCookiePersistor(this)))
                    .build();



        mClient = OkHttpCommonClient.getInstance();
        mClient.setOkhttpClient(okHttpClient);
```

**基本使用：**
```
//使用get方法获取数据，
        mClient.getBuilder()
                .url(Weather_baseurl)
                .putParams("key","m9datavogh53ftie")
                .putParams("location","shenzhen")
                .builder()
                .enqueue(new StringResponse() { //使用不同的 response
                    @Override
                    public void onSuccess(String response) { //返回你需要的值，比如这里的String，或者json等等
                        Log.d(TAG, "builder: "+response);
                    }

                    @Override
                    public void onFailure(Object errorObj) {

                    }
                });
```

**Gson**

```
mClient.getBuilder()
                .url(gsonurl)
                .builder()
                //在这里把json的实体类写在这里即可，比如我的root.class
                .enqueue(new JsonResponse(Root.class) { //这里的颜色区域是 jdk1.5的警告，
                                                        // 因为用的是泛型,直接关掉即可。
                    @Override
                    public void onSuccess(Object response) {
                        Root root = (Root) response;
                        Log.d(TAG, "onSuccess: "+root.getContent());
                    }

                    @Override
                    public void onFailure(Object errorObj) {

                    }
                });

```
图片

```
 mClient.getBuilder()
                .url(imgurl)
                .builder()
                .enqueue(new BitmapResponse(230,150) { //这里可以设置图片的大小，不写则下载原始图片
                    @Override
                    public void onSuccess(Bitmap response) {
                        img.setImageBitmap(response);
                    }

                    @Override
                    public void onFailure(Object errorObj) {
                        Log.d(TAG, "onFailure: "+errorObj.toString());
                    }
                });
```

**文件下载，单线程：**

```
 String path = Environment.getExternalStorageDirectory().getPath();
        mClient.getBuilder()
                .url(fileurl)
                .builder()
                .enqueue(new FileResponse(fileurl,path) {
                    @Override
                    public void onProgress(int progress) {
                        Log.d(TAG, "onProgress: "+progress);
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.d(TAG, "onSuccess: "+response);
                    }

                    @Override
                    public void onFailure(Object errorObj) {
                        Log.d(TAG, "onFailure: "+errorObj.toString());
                    }
                });
```
**文件下载，多线程**

```
String path = Environment.getExternalStorageDirectory().getPath();

        mClient.getBuilder()
                .url(fileurl)
                .builder()
                .enqueue(new FileMultResponse(fileurl,path,3) {
                    @Override
                    public void onProgress(int progress) {
                        Log.d(TAG, "onProgress: "+progress);
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.d(TAG, "onSuccess: "+response);
                    }

                    @Override
                    public void onFailure(Object errorObj) {
                        Log.d(TAG, "onFailure: "+errorObj.toString());
                    }
                });
```

## 4、 post 方法

**基本使用**

```
 mClient.postBuilder()
                .url(BASEURL+"login")
                .putParams("username","zhengshaorui")
                .putParams("password","123456789")
                .builder()
                .enqueue(new StringResponse() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d(TAG, "onSuccess: "+response);
                    }

                    @Override
                    public void onFailure(Object errorObj) {
                        Log.d(TAG, "onFailure: "+errorObj.toString());
                    }
                });
```
**post String**

```
 mClient.postStringBuilder()
                .url(BASEURL+"getString")
                .addMedieType("text/plain;chaset-utf-8","{username:rachel,password:123}")
                .builder()
                .enqueue(new StringResponse() {
                    @Override
                    public void onSuccess(String response) {

                    }

                    @Override
                    public void onFailure(Object errorObj) {

                    }
                });
```

**Post File 显示上传进度**

```
File file = new File(Environment.getExternalStorageDirectory(),"TvHouseManager.apk");
        if (!file.exists()){
            Toast.makeText(this, "TvHouseManager.apk" + "文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        mClient.postFileBuilder()
                .url(BASEURL+"getFile")
                .addMedieType("application/vnd.android.package-archive",file)
                .builder()
                .enqueue(new StringResponse() {
                    @Override
                    public void onSuccess(String response) {

                    }

                    @Override
                    public void onFailure(Object errorObj) {

                    }

                    @Override
                    public void onUploadProgress(int progress) {
                        super.onUploadProgress(progress);
                        Log.d(TAG, "onUploadProgress: "+progress);
                    }
                });

```

**Post form 也显示上传进度**

```
File file = new File(Environment.getExternalStorageDirectory(),"tvlog.jpg");
        if (!file.exists()){
            Toast.makeText(this, "tvlog.jpg" + "文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        mClient.postUploadFile()
                .url(BASEURL+"UpdateInfo")
                .addFile("mPic","mTestPhone.jpg",file)
                .addPart("username","zhengshaorui")
                .addPart("password","10086")
                .builder()
                .enqueue(new StringResponse() {
                    @Override
                    public void onSuccess(String response) {

                    }

                    @Override
                    public void onFailure(Object errorObj) {

                    }

                    @Override
                    public void onUploadProgress(int progress) {
                        super.onUploadProgress(progress);
                        Log.d(TAG, "onUploadProgress: "+progress);
                    }
                });

```