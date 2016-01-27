package cn.qqtheme.switcherimageviewsample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.boycy815.pinchimageview.SwitcherImageView;
import com.squareup.picasso.Picasso;

public class MainActivity extends Activity implements SwitcherImageView.OnSwitchListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SwitcherImageView imageView = (SwitcherImageView) findViewById(R.id.imageview_content);
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            String[] urls = (String[]) bundle.getSerializable("urls");
//            int position = bundle.getInt("position");
        String[] urls = new String[]{
                "http://g.hiphotos.baidu.com/zhidao/pic/item/09fa513d269759eee314015bb3fb43166c22dfde.jpg"
                , "http://ww2.sinaimg.cn/mw1024/6df127bfjw1esojfinxmxj20xc18gqfm.jpg"
                , "http://ww2.sinaimg.cn/mw1024/6df127bfjw1esiveg31hwj20u00gvn18.jpg"
                , "http://ww1.sinaimg.cn/mw1024/6df127bfjw1esivelw317j20u00gvq77.jpg"
                , "http://ww4.sinaimg.cn/mw1024/6df127bfjw1esbuy81ovzj20ku04taah.jpg"
                , "http://ww4.sinaimg.cn/mw1024/6df127bfjw1esaen9u5k8j20hs0nq75k.jpg"
                , "http://ww3.sinaimg.cn/mw1024/6df127bfjw1es1ixs8uctj20hs0vkgpc.jpg"
                , "http://ww2.sinaimg.cn/mw1024/6df127bfjw1erveujphhxj20xc18gwsy.jpg"
                , "http://ww1.sinaimg.cn/mw1024/6df127bfjw1eroxgfbkopj216o0m543e.jpg"
                , "http://ww4.sinaimg.cn/mw1024/6df127bfjw1erox2ywpn6j218g0xcwjp.jpg"
                , "http://ww2.sinaimg.cn/mw1024/6df127bfjw1erovfvilebj20hs0vkacd.jpg"
                , "http://ww3.sinaimg.cn/mw1024/6df127bfjw1erj3jcayb1j20qo0zkgrv.jpg"
                , "http://ww3.sinaimg.cn/mw1024/6df127bfgw1erc5yiqciaj20ke0b0abg.jpg"
                , "http://ww2.sinaimg.cn/mw1024/6df127bfjw1erbuxu8qa7j20ds0ct3zy.jpg"
                , "http://ww2.sinaimg.cn/mw1024/6df127bfjw1er0erdh1jaj20qo0zkq9j.jpg"
                , "http://ww3.sinaimg.cn/mw1024/6df127bfjw1er0enq1o3lj218g18gnbx.jpg"
                , "http://ww2.sinaimg.cn/mw1024/6df127bfjw1er0gnhidtdj20hs0nogoi.jpg"
                , "http://ww3.sinaimg.cn/mw1024/6df127bfjw1er0gqq4ff9j20hs0non07.jpg"
                , "http://ww3.sinaimg.cn/mw1024/6df127bfjw1eqmfsasl7fj218g0p07a2.jpg"
                , "http://ww2.sinaimg.cn/mw1024/6df127bfjw1eqmfuizagpj218g0r9dms.jpg"
        };
        int position = 5;
        imageView.setData(urls, position, this);
//        }
    }

    private void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBegin() {
        showToast("已经是第一张");
    }

    @Override
    public void onSwitched(ImageView view, String url) {
        Picasso.with(this).load(url).placeholder(android.R.drawable.ic_menu_report_image).into(view);
    }

    @Override
    public void onEnd() {
        showToast("已经是最后一张");
    }

}
