package com.jixiang52002.photomaster.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jixiang52002.photomaster.R;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 色光三原色调整
     * @param view
     */
    public void btnPrimaryColor(View view){
        Intent intent=new Intent(this,PrimaryColor.class);
        startActivity(intent);
    }

    //矩阵变换实现图像处理
    public void btnColorMatrix(View view){
        Intent intent=new Intent(this,ColorMatrix.class);
        startActivity(intent);
    }

    //像素点阵实现图像处理
    public void btnPiexlsEffect(View view){
        Intent intent=new Intent(this,PixelsEffect.class);
        startActivity(intent);
    }

    //通过基本矩阵实现四种效果
    public void btnMatrix(View view){
        Intent intent=new Intent(this,ImageMatrixTest.class);
        startActivity(intent);
    }

    //Paint 的xformodule方式实现圆角图片
    public void btnXfermodule(View view){
        Intent intent=new Intent(this,XfermoduleTest.class);
        startActivity(intent);
    }

    //Paint 的shader方式实现圆角图片
    public void btnShader(View view){
        Intent intent=new Intent(this,BitmapShaderTest.class);
        startActivity(intent);
    }

    //实现倒影效果和背景效果
    public void btnReflect(View view){
        Intent intent=new Intent(this,ReflectViewTest.class);
        startActivity(intent);
    }

    //以数学函数的方式实现一个动态的效果图
    public void btnMeshView(View view){
        Intent intent=new Intent(this,MeshViewTest.class);
        startActivity(intent);
    }

    //sharedSdk效果
    public void btnShareSdk(View view){
         showShare();
    }


    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
//关闭sso授权
        oks.disableSSOWhenAuthorize();

// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("标题");
// titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://sharesdk.cn");
// text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
// url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
// comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
// site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
// siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }

}
