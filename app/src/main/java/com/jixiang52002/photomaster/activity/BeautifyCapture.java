package com.jixiang52002.photomaster.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Toast;

import com.jixiang52002.photomaster.R;
import com.jixiang52002.photomaster.config.Constacts;
import com.jixiang52002.photomaster.utils.BaseUtils;
import com.jixiang52002.photomaster.utils.ImageUtils;

import java.io.File;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.PermissionUtils;
import permissions.dispatcher.RuntimePermissions;
@RuntimePermissions
public class BeautifyCapture extends Activity implements View.OnFocusChangeListener{

    ImageView imagerView;//待处理图片
    LinearLayout watch;//观看效果
    LinearLayout increase;//三原色效果增强
    LinearLayout effect;//特效
    LinearLayout frame;//相框
    LinearLayout person;//个人中心

    private boolean isChanged=false;//图片是否有更改

    private String filePath;

    Bitmap bitmap;//正在处理的图像

    //弹出按钮
    private PopupWindow popupWindow1;
    private PopupWindow popupWindow2;
    private PopupWindow popupWindow3;
    private PopupWindow popupWindow4;
    private PopupWindow popupWindow5;

    //图标
    private ImageView imageWatch;
    private ImageView imageIncrease;
    private ImageView imageEffect;
    private ImageView imageFrame;
    private ImageView imagePerson;

    //触屏标志变量 1-缩放图片 2-画图
    private int flagOnTouch = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         /*
         * 防止键盘挡住输入框
         * 不希望遮挡设置activity属性 android:windowSoftInputMode="adjustPan"
         * 希望动态调整高度 android:windowSoftInputMode="adjustResize"
         */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.
                SOFT_INPUT_ADJUST_PAN);
        //锁定屏幕
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_beautify_capture);
        initView();
        BeautifyCapturePermissionsDispatcher.initImageWithCheck(this);
        //自定义函数 设置监听事件
        SetClickTouchListener();

    }

    //初始化界面
    private void initView() {
        imagerView= (ImageView) findViewById(R.id.imageView1);
        watch= (LinearLayout) findViewById(R.id.layout_watch);
        increase= (LinearLayout) findViewById(R.id.layout_increase);
        effect= (LinearLayout) findViewById(R.id.layout_effect);
        frame= (LinearLayout) findViewById(R.id.layout_frame);
        person= (LinearLayout) findViewById(R.id.layout_person);

        //图标
        imageWatch = (ImageView) findViewById(R.id.image_watch);
        imageIncrease = (ImageView) findViewById(R.id.image_increase);
        imageEffect = (ImageView) findViewById(R.id.image_effect);
        imageFrame = (ImageView) findViewById(R.id.image_frame);
        imagePerson = (ImageView) findViewById(R.id.image_person);
        //为底部菜单添加相应的focuse（焦点）改变检测
        watch.setOnFocusChangeListener(this);
        increase.setOnFocusChangeListener(this);
        effect.setOnFocusChangeListener(this);
        frame.setOnFocusChangeListener(this);
        person.setOnFocusChangeListener(this);
        //为顶部菜单添加点击事件
        findViewById(R.id.view_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检测是否有更改
                if(isChanged){

                }else{
                    finish();
                }
            }
        });

        findViewById(R.id.view_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    /*
	 * 函数功能 设置监听事件
	 * 触摸监听事件 点击监听事件
	 */
    private void SetClickTouchListener()
    {
		/*
		 * 按钮一 监听事件 查看图片
		 */
        watch.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ProcessActivity.this, "点击按钮1", Toast.LENGTH_SHORT).show();
                //载入PopupWindow
                if (popupWindow1 != null&&popupWindow1.isShowing()) {
                    popupWindow1.dismiss();
                    return;
                } else {
                    initmPopupWindowView(1);   //当number=1时查看图片
                    int[] location = new int[2];
                    v.getLocationOnScreen(location);
                    popupWindow1.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow1.getHeight());
                }
            }
        });
        watch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //按下背景图片
                    watch.setBackgroundResource(R.drawable.image_home_layout_bg);
                    increase.setBackgroundResource(R.drawable.image_home_layout_no);
                    effect.setBackgroundResource(R.drawable.image_home_layout_no);
                    frame.setBackgroundResource(R.drawable.image_home_layout_no);
                    person.setBackgroundResource(R.drawable.image_home_layout_no);
                    //设置按钮图片
                    imageWatch.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_watch_sel));
                    imageIncrease.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_increase_nor));
                    imageEffect.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_effect_nor));
                    imageFrame.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_frame_nor));
                    imagePerson.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_person_nor));
                }
                return false;
            }
        });
		/*
		 * 按钮二 监听事件增强图片
		 */
        increase.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //载入PopupWindow
                if (popupWindow2 != null&&popupWindow2.isShowing()) {
                    popupWindow2.dismiss();
                    return;
                } else {
                    initmPopupWindowView(2);   //number=2
                    int[] location = new int[2];
                    v.getLocationOnScreen(location);
                    popupWindow2.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow2.getHeight());
                }
            }
        });
        increase.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //按下背景图片
                    watch.setBackgroundResource(R.drawable.image_home_layout_no);
                    increase.setBackgroundResource(R.drawable.image_home_layout_bg);
                    effect.setBackgroundResource(R.drawable.image_home_layout_no);
                    frame.setBackgroundResource(R.drawable.image_home_layout_no);
                    person.setBackgroundResource(R.drawable.image_home_layout_no);
                    //设置按钮图片
                    imageWatch.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_watch_nor));
                    imageIncrease.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_increase_sel));
                    imageEffect.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_effect_nor));
                    imageFrame.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_frame_nor));
                    imagePerson.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_person_nor));
                }
                return false;
            }
        });
        /*
         * 按钮三 监听事件图片特效
         */
        effect.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //载入PopupWindow
                if (popupWindow3 != null&&popupWindow3.isShowing()) {
                    popupWindow3.dismiss();
                    return;
                } else {
                    initmPopupWindowView(3);   //number=3
                    int[] location = new int[2];
                    v.getLocationOnScreen(location);
                    popupWindow3.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow3.getHeight());
                }
            }
        });
        effect.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //按下背景图片
                    watch.setBackgroundResource(R.drawable.image_home_layout_no);
                    increase.setBackgroundResource(R.drawable.image_home_layout_no);
                    effect.setBackgroundResource(R.drawable.image_home_layout_bg);
                    frame.setBackgroundResource(R.drawable.image_home_layout_no);
                    person.setBackgroundResource(R.drawable.image_home_layout_no);
                    //图标
                    imageWatch.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_watch_nor));
                    imageIncrease.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_increase_nor));
                    imageEffect.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_effect_sel));
                    imageFrame.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_frame_nor));
                    imagePerson.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_person_nor));
                }
                return false;
            }
        });
		/*
		 * 按钮四 监听事件图片相框
		 */
        frame.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //载入PopupWindow
                if (popupWindow4 != null&&popupWindow4.isShowing()) {
                    popupWindow4.dismiss();
                    return;
                } else {
                    initmPopupWindowView(4);   //number=4
                    int[] location = new int[2];
                    v.getLocationOnScreen(location);
                    popupWindow4.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow4.getHeight());
                }
            }
        });
        frame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //按下背景图片
                    watch.setBackgroundResource(R.drawable.image_home_layout_no);
                   increase.setBackgroundResource(R.drawable.image_home_layout_no);
                    effect.setBackgroundResource(R.drawable.image_home_layout_no);
                    frame.setBackgroundResource(R.drawable.image_home_layout_bg);
                    person.setBackgroundResource(R.drawable.image_home_layout_no);
                    //图标
                    imageWatch.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_watch_nor));
                    imageIncrease.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_increase_nor));
                    imageEffect.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_effect_nor));
                    imageFrame.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_frame_sel));
                    imagePerson.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_person_nor));
                }
                return false;
            }
        });
        /*
         * 按钮五 监听事件图片美白
         */
        person.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //载入PopupWindow
                if (popupWindow5 != null&&popupWindow5.isShowing()) {
                    popupWindow5.dismiss();
                    return;
                } else {
                    initmPopupWindowView(5);   //number=5
                    int[] location = new int[2];
                    v.getLocationOnScreen(location);
                    popupWindow5.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow5.getHeight());
                }
            }
        });
        person.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //按下背景图片
                    watch.setBackgroundResource(R.drawable.image_home_layout_no);
                    increase.setBackgroundResource(R.drawable.image_home_layout_no);
                    effect.setBackgroundResource(R.drawable.image_home_layout_no);
                    frame.setBackgroundResource(R.drawable.image_home_layout_no);
                    person.setBackgroundResource(R.drawable.image_home_layout_bg);
                    //图标
                    imageWatch.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_watch_nor));
                    imageIncrease.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_increase_nor));
                    imageEffect.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_effect_nor));
                    imageFrame.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_frame_nor));
                    imagePerson.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_person_sel));
                }
                return false;
            }
        });//结束监听5个事件
    }

    /*
	 * 函数功能 PopupWindow窗体动画
	 * 获取自定义布局文件
	 */
    public void initmPopupWindowView(int number) {
        View customView = null;
        //触屏标记默认为0 否则点一次"缩放"总能移动
        flagOnTouch  = 0;
      	/*
    	 * number=1 查看
    	 */
        if(number==1) {
            customView = getLayoutInflater().inflate(R.layout.popup_watch, null, false);
            // 创建PopupWindow实例  (250,180)分别是宽度和高度
            popupWindow1 = new PopupWindow(customView, 450, 150);
            // 使其聚集 要想监听菜单里控件的事件就必须要调用此方法
            popupWindow1.setFocusable(true);
            // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
            popupWindow1.setOutsideTouchable(true);
            popupWindow1.setAnimationStyle(R.style.AnimationPreview);
            // 自定义view添加触摸事件
            customView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (popupWindow1 != null && popupWindow1.isShowing()) {
                        popupWindow1.dismiss();
                        popupWindow1 = null;
                    }
                    return false;
                }
            });
            //判断点击子菜单不同按钮实现不同功能
            //自定义引用类
            watchProcess = new WatchProcessImage(bmp);
            LinearLayout layoutWatch2 = (LinearLayout) customView.findViewById(R.id.layout_watch2);
            layoutWatch2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    textShow.setText("图像处理--水平翻转");
                    popupWindow1.dismiss();
                    //调用WatchProcessImage中函数实现水平翻转
                    mbmp = watchProcess.FlipHorizontalImage(bmp,flagWatch2);
                    imageShow.setImageBitmap(mbmp);
                    //标记变量 0翻转 1变回原图
                    if(flagWatch2 == 0) {
                        flagWatch2 = 1;
                    } else if(flagWatch2 == 1) {
                        flagWatch2 =0;
                    }
                }
            });
            LinearLayout layoutWatch3 = (LinearLayout) customView.findViewById(R.id.layout_watch3);
            layoutWatch3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textShow.setText("图像处理--垂直翻转");
                    popupWindow1.dismiss();
                    mbmp = watchProcess.FlipVerticalImage(bmp,flagWatch3);
                    imageShow.setImageBitmap(mbmp);
                    //标记变量 0翻转 1变回原图
                    if(flagWatch3 == 0) {
                        flagWatch3 = 1;
                    } else if(flagWatch3 == 1) {
                        flagWatch3 =0;
                    }
                }
            });
            LinearLayout layoutWatch1 = (LinearLayout) customView.findViewById(R.id.layout_watch1);
            layoutWatch1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    title.setText("图像处理--旋转图片");
                    popupWindow1.dismiss();
                    //旋转一次表示增加45度 模8表示360度=0度
                    flagWatch1 = (flagWatch1+1) % 8;
                    //设置背景颜色黑色
                    //imageShow.setBackgroundColor(Color.parseColor("#000000"));
                    mbmp = watchProcess.TurnImage(bmp, flagWatch1);
                    imageShow.setImageBitmap(mbmp);
                }
            });
            LinearLayout layoutWatch4 = (LinearLayout) customView.findViewById(R.id.layout_watch4);
            layoutWatch4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textShow.setText("图像处理--移动缩放");
                    popupWindow1.dismiss();
                    flagOnTouch = 1; //标志变量
                    //动态设置android:scaleType="matrix"
                    imageShow.setScaleType(ImageView.ScaleType.MATRIX);
                    imageShow.setImageBitmap(bmp);
                }
            });
            LinearLayout layoutWatch5 = (LinearLayout) customView.findViewById(R.id.layout_watch5);
            layoutWatch5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textShow.setText("图像处理--绘制图片");
                    popupWindow1.dismiss();
                    flagOnTouch = 2; //标志变量
                    //动态设置android:scaleType="matrix"
                    imageShow.setScaleType(ImageView.ScaleType.MATRIX);
                    //画图 图片移动至(0,0) 否则绘图线与手指存在误差
                    matrix = new Matrix();
                    matrix.postTranslate(0, 0);
                    imageShow.setImageMatrix(matrix);
                    canvas.drawBitmap(bmp, matrix, paint);
                    imageShow.setImageBitmap(alteredBitmap); //备份图片
                }
            });
        }
    	/*
    	 * number=2 增强
    	 */
        if(number==2) {
            customView = getLayoutInflater().inflate(R.layout.popup_increase, null, false);
            //设置子窗体PopupWindow高度500 饱和度 色相 亮度
            popupWindow2 = new PopupWindow(customView, 600, 500);
            // 使其聚集 要想监听菜单里控件的事件就必须要调用此方法
            popupWindow2.setFocusable(true);
            // 设置允许在外点击消失
            popupWindow2.setOutsideTouchable(true);
            popupWindow2.setAnimationStyle(R.style.AnimationPreview);
            // 自定义view添加触摸事件
            customView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (popupWindow2 != null && popupWindow2.isShowing()) {
                        popupWindow2.dismiss();
                        popupWindow2 = null;
                    }
                    return false;
                }
            });
            //SeekBar
            seekBar1 = (SeekBar) customView.findViewById(R.id.seekBarSaturation);  //饱和度
            seekBar2 = (SeekBar) customView.findViewById(R.id.seekBarHue);            //色相
            seekBar3 = (SeekBar) customView.findViewById(R.id.seekBarLum);            //亮度
             /*
    	      * 设置Seekbar变化监听事件
    	      * 注意:此时修改活动接口
    	      * ProcessActivity extends Activity implements OnSeekBarChangeListener
    	      */
            seekBar1.setOnSeekBarChangeListener(this);
            seekBar2.setOnSeekBarChangeListener(this);
            seekBar3.setOnSeekBarChangeListener(this);
            //自定义引用类
            increaseProcess = new IncreaseProcessImage(bmp);
        }
    	/*
    	 * number=3 效果
    	 */
        if(number==3) {
            customView = getLayoutInflater().inflate(R.layout.popup_effect, null, false);
            popupWindow3 = new PopupWindow(customView, 450, 150);
            popupWindow3.setFocusable(true);
            popupWindow3.setOutsideTouchable(true);
            popupWindow3.setAnimationStyle(R.style.AnimationPreview);
            // 自定义view添加触摸事件
            customView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (popupWindow3 != null && popupWindow3.isShowing()) {
                        popupWindow3.dismiss();
                        popupWindow3 = null;
                    }
                    return false;
                }
            });
            //判断点击子菜单不同按钮实现不同功能
            //自定义引用类
            effectProcess = new EffectProcessImage(bmp);
            LinearLayout layoutEffect1 = (LinearLayout) customView.findViewById(R.id.layout_effect_hj);
            layoutEffect1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textShow.setText("图像处理--怀旧效果");
                    popupWindow3.dismiss();
                    //调用EffectProcessImage.java中函数
                    mbmp = effectProcess.OldRemeberImage(bmp);
                    imageShow.setImageBitmap(mbmp);
                }
            });
            LinearLayout layoutEffect2 = (LinearLayout) customView.findViewById(R.id.layout_effect_fd);
            layoutEffect2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textShow.setText("图像处理--浮雕效果");
                    popupWindow3.dismiss();
                    mbmp = effectProcess.ReliefImage(bmp);
                    imageShow.setImageBitmap(mbmp);
                }
            });
            LinearLayout layoutEffect3 = (LinearLayout) customView.findViewById(R.id.layout_effect_gz);
            layoutEffect3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textShow.setText("图像处理--光照效果");
                    popupWindow3.dismiss();
                    mbmp = effectProcess.SunshineImage(bmp);
                    imageShow.setImageBitmap(mbmp);
                }
            });
            LinearLayout layoutEffect4 = (LinearLayout) customView.findViewById(R.id.layout_effect_sm);
            layoutEffect4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textShow.setText("图像处理--素描效果");
                    popupWindow3.dismiss();
                    mbmp = effectProcess.SuMiaoImage(bmp);
                    imageShow.setImageBitmap(mbmp);
                }
            });
            LinearLayout layoutEffect5 = (LinearLayout) customView.findViewById(R.id.layout_effect_rh);
            layoutEffect5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textShow.setText("图像处理--锐化效果");
                    popupWindow3.dismiss();
                    mbmp = effectProcess.SharpenImage(bmp);
                    imageShow.setImageBitmap(mbmp);
                }
            });

        }
		/*
		 * number=4 边框
		 */
        if(number==4) {
            customView = getLayoutInflater().inflate(R.layout.popup_frame, null, false);
            popupWindow4 = new PopupWindow(customView, 450, 150);
            popupWindow4.setFocusable(true);
            popupWindow4.setAnimationStyle(R.style.AnimationPreview);
            // 自定义view添加触摸事件
            customView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (popupWindow4 != null && popupWindow4.isShowing()) {
                        popupWindow4.dismiss();
                        popupWindow4 = null;
                    }
                    return false;
                }
            });
            //判断点击子菜单不同按钮实现不同功能
            //自定义引用类
            frameProcess = new FrameProcessImage(bmp);
            LinearLayout layoutFrame3 = (LinearLayout) customView.findViewById(R.id.layout_frame3);
            layoutFrame3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textShow.setText("图像处理--框架模式三");
                    popupWindow4.dismiss();
                    //获取相框 自定义函数getImageFromAssets 获取assets中资源
                    Bitmap frameBitmap = getImageFromAssets("image_frame_big_3.png");
                    //显示图像并增加相框
                    mbmp = frameProcess.addFrameToImage(bmp,frameBitmap);
                    imageShow.setImageBitmap(mbmp);
                }
            });
            LinearLayout layoutFrame2 = (LinearLayout) customView.findViewById(R.id.layout_frame2);
            layoutFrame2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textShow.setText("图像处理--框架模式二");
                    popupWindow4.dismiss();
                    Bitmap frameBitmap = getImageFromAssets("image_frame_big_2.png");
                    mbmp = frameProcess.addFrameToImage(bmp,frameBitmap);
                    imageShow.setImageBitmap(mbmp);
                }
            });
            LinearLayout layoutFrame1 = (LinearLayout) customView.findViewById(R.id.layout_frame1);
            layoutFrame1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textShow.setText("图像处理--框架模式一");
                    popupWindow4.dismiss();
                    Bitmap frameBitmap = getImageFromAssets("image_frame_big_1.png");
                    mbmp = frameProcess.addFrameToImage(bmp,frameBitmap);
                    imageShow.setImageBitmap(mbmp);
                }
            });
            LinearLayout layoutFrame4 = (LinearLayout) customView.findViewById(R.id.layout_frame4);
            layoutFrame4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textShow.setText("图像处理--圆角矩形");
                    popupWindow4.dismiss();
                    mbmp = frameProcess.RoundedCornerBitmap(bmp);
                    imageShow.setImageBitmap(mbmp);
                }
            });
            LinearLayout layoutFrame5 = (LinearLayout) customView.findViewById(R.id.layout_frame5);
            layoutFrame5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textShow.setText("图像处理--圆形相框");
                    popupWindow4.dismiss();
                    mbmp = frameProcess.RoundedBitmap(bmp);
                    imageShow.setImageBitmap(mbmp);
                }
            });
        }
        /*
         * number=5 美白 -> 交互
         */
        if(number==5) {
            customView = getLayoutInflater().inflate(R.layout.popup_person, null, false);
            popupWindow5 = new PopupWindow(customView, 300, 150);
            popupWindow5.setFocusable(true);
            popupWindow5.setAnimationStyle(R.style.AnimationPreview);
            // 自定义view添加触摸事件
            customView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (popupWindow5 != null && popupWindow5.isShowing()) {
                        popupWindow5.dismiss();
                        popupWindow5 = null;
                    }
                    return false;
                }
            });
            //判断点击子菜单不同按钮实现不同功能
            //自定义引用类
            personProcess = new PersonProcessImage(bmp);
            LinearLayout layoutPerson1 = (LinearLayout) customView.findViewById(R.id.layout_person1);
            layoutPerson1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textShow.setText("保存图像至SD卡");
                    popupWindow5.dismiss();
                    try {
            			/*
            			 * 注意：由于手机重启才能显示图片 所以定义广播刷新相册 其中saveBitmapToSD保存图片
            			 */
                        if(mbmp == null) { //防止出现mbmp空
                            mbmp = bmp;
                        }
                        Uri uri = personProcess.saveBitmapToSD(mbmp);
                        Intent intent  = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.setData(uri);
                        sendBroadcast(intent);
                        Toast.makeText(ProcessActivity.this, "图像保存成功", Toast.LENGTH_SHORT).show();
                    } catch(Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ProcessActivity.this, "图像保存失败", Toast.LENGTH_SHORT).show();
                    }
                    imageShow.setImageBitmap(mbmp);
                }
            });
            LinearLayout layoutPerson2 = (LinearLayout) customView.findViewById(R.id.layout_person2);
            layoutPerson2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textShow.setText("取消处理操作--恢复原图");
                    popupWindow5.dismiss();
                    mbmp = bmp;
                    imageShow.setImageBitmap(mbmp);
                }
            });
            LinearLayout layoutPerson3 = (LinearLayout) customView.findViewById(R.id.layout_person3);
            layoutPerson3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textShow.setText("上传图片至发布界面");
                    popupWindow5.dismiss();
                    try {
                        if(mbmp == null) { //防止出现mbmp空
                            mbmp = bmp;
                        }
                        //图像上传 先保存 后传递图片路径
                        Uri uri = personProcess.loadBitmap(mbmp);
                        Intent intent  = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.setData(uri);
                        sendBroadcast(intent);
                        //上传图片*
                        Intent intentPut = new Intent(ProcessActivity.this, MainActivity.class);
                        String pathImage = null;
                        intentPut.putExtra("pathProcess", personProcess.pathPicture );
	    				/*
	    				 * 返回活动使用setResult 使用startActivity总是显示一张图片并RunTime
	    				 * startActivity(intentPut);
	    				 * 在onActivityResult中获取数据
	    				 */
                        setResult(RESULT_OK, intentPut);
                        //返回上一界面
                        Toast.makeText(ProcessActivity.this, "图片上传成功" , Toast.LENGTH_SHORT).show();
                        ProcessActivity.this.finish();
                    } catch(Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ProcessActivity.this, "图像上传失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } //end if
    }

    /**
     * 初始化图像
     */
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void initImage(){
        //获取从其他intent中传过来的数据
        Intent intent=getIntent();
        String filePath=intent.getStringExtra(Constacts.CAPTURE_FILES);
        bitmap=ImageUtils.getBitmapFromFile(new File(filePath),480,320);
        imagerView.setImageBitmap(bitmap);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BeautifyCapturePermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void shyWhy(final PermissionRequest request) {
        BaseUtils.Toast(request.toString());
    }


    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void cancel() {
        BaseUtils.Toast("无法读取外部内存卡数据，请设置通过权限");
    }

    @OnNeverAskAgain({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void never() {
        BaseUtils.Toast("永远无法读取外部内存卡数据，请设置通过权限");
    }
}
