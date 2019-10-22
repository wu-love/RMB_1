package com.example.rmb;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements Runnable {
    private static final String TAG = "Rate";
    EditText rmb;
    TextView show;
    Handler handler;
    private float dollarRate = 0.1f;
    private float euroRate = 0.2f;
    private float wonRate = 0.3f;
    private String updateDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rmb = findViewById(R.id.rmb);
        show = findViewById(R.id.showOut);

        //获取SP里保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences( "myrate", Activity.MODE_PRIVATE );
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( this );
        dollarRate = sharedPreferences.getFloat( "dollar_rate",0.0f );
        euroRate = sharedPreferences.getFloat( "euro_rate",0.0f );
        wonRate = sharedPreferences.getFloat( "won_rate",0.0f );
        updateDate = sharedPreferences.getString( "update_date","" );

        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
        final String todayStr = sdf.format( today );

        Log.i( TAG,"onCreate: sp dollarRate=" + dollarRate );
        Log.i( TAG,"onCreate: sp euroRate=" + euroRate );
        Log.i( TAG,"onCreate: sp wonRate=" + wonRate );
        Log.i( TAG,"onCreate: todayStr=" + todayStr );

        //判断时间
        if(!todayStr.equals( updateDate )){
            Log.i( TAG,"onCreate: 需要更新" );
            //开启子线程
            Thread t = new Thread( this );
            t.start();
        }else {
            Log.i( TAG,"onCreate: 不需要更新");
        }

        /*
        //开启子线程
        Thread t = new Thread( this );
        t.start();*/

        handler = new Handler(  ) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==5){
                    /*String str  = (String) msg.obj;
                    Log.i(TAG,"handleMessage: getMessage = " + str);
                    show.setText( str );*/
                    Bundle bdl = (Bundle) msg.obj;
                    dollarRate = bdl.getFloat( "dollar-rate" );
                    euroRate = bdl.getFloat( "euro-rate" );
                    wonRate = bdl.getFloat( "won-rate" );

                    Log.i( TAG,"handleMessage: dollarRate:" + dollarRate );
                    Log.i( TAG,"handleMessage: euroRate:" + euroRate );
                    Log.i( TAG,"handleMessage: wonRate:" + wonRate );

                    //保存更新的日期
                    SharedPreferences sp = getSharedPreferences( "myrate", Activity.MODE_PRIVATE );
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putFloat( "dollar_rate",dollarRate );
                    editor.putFloat( "euro_rate",euroRate );
                    editor.putFloat( "won_rate",wonRate );
                    editor.putString( "update_date",todayStr );
                    editor.apply();

                    Toast.makeText( MainActivity.this,"汇率已更新",Toast.LENGTH_SHORT ).show();
                }
                super.handleMessage( msg );
            }
        };
}

    public void onClick(View btn){
        //获取用户输入内容
        Log.i(TAG,"onClick: ");
        String str = rmb.getText().toString();
        Log.i(TAG,"onClick: get str="+str);

        float r = 0;
        if(str.length()>0){
            r = Float.parseFloat(str);
        }else{
            //提示用户输入内容
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
        }
        Log.i(TAG,"onClick: r="+ r);

        //计算
        if(btn.getId()==R.id.btn_dollar){
            show.setText(String.format("%.2f",r*dollarRate));
        }else if(btn.getId()==R.id.btn_euro){
            show.setText(String.format("%.2f",r*euroRate));
        }else{
            show.setText(String.format("%.2f",r*wonRate));
        }
    }

    public  void openOne(View btn){
        openConfig();

    }

    private void openConfig() {
        Intent config = new Intent( this, ConfigActivity.class );
        config.putExtra( "dollar_rate_key", dollarRate );
        config.putExtra( "euro_rate_key", euroRate );
        config.putExtra( "won_rate_key", wonRate );

        Log.i( TAG, "openOne: dollar_rate_key=" + dollarRate );
        Log.i( TAG, "openOne: euro_rate_key=" + euroRate );
        Log.i( TAG, "openOne: won_rate_key=" + wonRate );
        startActivityForResult( config, 1 );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_set){

            openConfig();
        }else if(item.getItemId()==R.id.open_list){
            //打开列表窗口
            //Intent list = new Intent( this, MyList2Activity.class );
            //startActivity( list );
            //测试数据库
            /*RateItem item1 = new RateItem( "aaaa","123" );
            RateManager manager = new RateManager( this );
            manager.add( item1 );
            manager.add( new RateItem( "bbbb","111.1" ) );
            Log.i( TAG,"onOptionsItemSelected: 写入数据库完毕" );


            //查询所有数据
            List<RateItem> testList = manager.listAll();
            for(RateItem i : testList){
                Log.i( TAG,"onOptionsItemSelected: 取出数据[id=" + i.getId() + "]Mame=" + i.getCurName() + "Rate=" + i.getCurRate() );
            }*/
        }
        return super.onOptionsItemSelected( item );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if(requestCode==1 && resultCode==2){
            Bundle bundle = data.getExtras();
            dollarRate = bundle.getFloat( "key_dollar",0.1f );
            euroRate = bundle.getFloat( "key_euro",0.1f );
            wonRate = bundle.getFloat( "key_won",0.1f );
            Log.i( TAG,"onActivityResult: dollarRate ="+ dollarRate );
            Log.i( TAG,"onActivityResult: euroRate="+ euroRate );
            Log.i( TAG,"onActivityResult: wonRate="+ wonRate );

            //将新设置的汇率写到SP里
            SharedPreferences sp = getSharedPreferences( "myrate", Activity.MODE_PRIVATE );
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat( "dollar_rate",dollarRate );
            editor.putFloat( "euro_rate",euroRate );
            editor.putFloat( "won_rate",wonRate );
            editor.apply();
            //editor.commit();
            Log.i( TAG,"onActivityResult:数据已保存到SharedPreferences" );
        }

        super.onActivityResult( requestCode,resultCode,data );
    }

    @Override
    public void run() {
        Log.i( TAG,"run: run()......" );
        //Log.i( TAG,"run: i=" + i );
        for(int i=1;i<6;i++){
           Log.i( TAG,"run: i=" + i );
            try {
                Thread.sleep( 3000 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //用于保存获取的汇率
        //Bundle bundle = new Bundle(  );
        Bundle bundle;

        //获取Msg对象，用于返回主线程
        /*Message msg = handler.obtainMessage();
        //msg.what = 5;
        msg.obj = "Hello from run()";
        handler.sendMessage( msg );*/

        //获取网络数据
        /*URL url = null;
        try {
            url = new URL( "www.usd-cny.com/bankofchina.htm" );
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream in = http.getInputStream();

            String html = inputString2String( in );
            Log.i( TAG,"run: html=" + html );
            Document doc = Jsoup.parse( html );

        } catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        bundle = getFromBOC();

        //bundle中保存所获取的汇率

        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage(5);
        //msg.what = 5;
        //msg.obj = "Hello from run()";
        msg.obj = bundle;
        handler.sendMessage( msg );
    }

    /**
     * 从bankofchina获取数据
     * @return
     */
    private Bundle getFromBOC() {
        Bundle bundle = new Bundle(  );
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj/").get();
            //doc = Jsoup.parse( html );
            Log.i(TAG,"run:" + doc.title());
            /*Elements newsHeadlines = doc.select("#mp-itn b a");
            for (Element headline : newsHeadlines) {
                Log.i(TAG,"%s\n\t%s" + headline.attr("title") + headline.absUrl("href"));
            }*/
            Elements tables = doc.getElementsByTag( "table" );
            //int i = 1;
            /*for (Element table : tables){
                Log.i( TAG,"run: tables["+i+"]=" + table );
                i++;
            }*/

            Element table2 = tables.get( 1 );
            //Element table6 = tables.get( 5 );
            //Log.i( TAG,"run: table6=" + table6 );
            //获取TD中的数据
            //Elements tds = table6.getElementsByTag( "td" );
            Elements tds = table2.getElementsByTag( "td" );
            for(int i=0;i<tds.size();i+=8){
                Element td1 = tds.get( i );
                Element td2 = tds.get( i+5 );
                Log.i( TAG,"run: "+td1.text() + "==>" +td2.text());
                String str1 = td1.text();
                String val = td2.text();

                if("美元".equals(str1 )){
                    bundle.putFloat( "dollar-rate",100f/Float.parseFloat( val ) );
                }else if("欧元".equals( str1 )){
                    bundle.putFloat( "euro-rate",100f/Float.parseFloat( val ) );
                }else if("韩国元".equals( str1 )){
                    bundle.putFloat( "won-rate",100f/Float.parseFloat( val ) );
                }
            }
            /*for(Element td :tds){
                Log.i( TAG,"run: td=" + td );
                Log.i( TAG,"run: text=" + td.text() );
                Log.i( TAG,"run: html=" + td.html() );
            }*/

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private Bundle getFromUsdCny() {
        Bundle bundle = new Bundle(  );
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            //doc = Jsoup.parse( html );
            Log.i(TAG,"run:" + doc.title());
            /*Elements newsHeadlines = doc.select("#mp-itn b a");
            for (Element headline : newsHeadlines) {
                Log.i(TAG,"%s\n\t%s" + headline.attr("title") + headline.absUrl("href"));
            }*/
            Elements tables = doc.getElementsByTag( "table" );
            //int i = 1;
            /*for (Element table : tables){
                Log.i( TAG,"run: tables["+i+"]=" + table );
                i++;
            }*/

            Element table6 = tables.get( 5 );
            Log.i( TAG,"run: table6=" + table6 );
            //获取TD中的数据
            Elements tds = table6.getElementsByTag( "td" );
            for(int i=0;i<tds.size();i+=8){
                Element td1 = tds.get( i );
                Element td2 = tds.get( i+5 );
                Log.i( TAG,"run: "+td1.text() + "==>" +td2.text());
                String str1 = td1.text();
                String val = td2.text();

                if("美元".equals(str1 )){
                    bundle.putFloat( "dollar-rate",100f/Float.parseFloat( val ) );
                }else if("欧元".equals( str1 )){
                    bundle.putFloat( "euro-rate",100f/Float.parseFloat( val ) );
                }else if("韩国元".equals( str1 )){
                    bundle.putFloat( "won-rate",100f/Float.parseFloat( val ) );
                }
            }
            /*for(Element td :tds){
                Log.i( TAG,"run: td=" + td );
                Log.i( TAG,"run: text=" + td.text() );
                Log.i( TAG,"run: html=" + td.html() );
            }*/

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private  String inputString2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "gb2312");
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}
