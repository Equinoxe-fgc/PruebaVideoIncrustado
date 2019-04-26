package com.equinoxe.pruebavideoincrustado;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


public class MainActivity extends AppCompatActivity {
    final static int iMIN_READ_TIME = 15000;
    final static int iMIN_RANDOM_TIME = 5000;
    final static int iMAX_RANDOM_TIME = 30000;

    protected WebView webView;
    private TextView textURL;
    protected Vector links;
    protected Vector linksInicial;
    protected String sLinks = "";
    protected boolean bTerminado;
    Handler handler;
    Random r;
    int random;
    //int iNumLinks;

    Timer timerComprobarCarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linksInicial = new Vector();
        linksInicial.addElement("http://www.wikipedia.com");
        linksInicial.addElement("https://www.diariosur.es/");
        linksInicial.addElement("https://elpais.com/");
        linksInicial.addElement("https://www.abc.es/");
        linksInicial.addElement("https://www.elmundo.es/");
        linksInicial.addElement("https://www.uma.es/");
        linksInicial.addElement("https://www.instructables.com/");
        linksInicial.addElement("https://www.amazon.es/");
        linksInicial.addElement("https://www.agenciatributaria.es/");
        linksInicial.addElement("http://www.malaga.eu/");

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        links = new Vector();
        r = new Random();

        webView = findViewById(R.id.webView);
        textURL = findViewById(R.id.textURL);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(this), "HtmlViewer");

        random = r.nextInt(linksInicial.size());
        String sURL = (String) linksInicial.elementAt(random);
        webView.loadUrl(sURL);
        bTerminado = false;

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:window.HtmlViewer.showHTML" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
        });


        handler = new Handler();
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                if (bTerminado) {
                    bTerminado = false;
                    if (links.isEmpty()) {
                        webView.goBack();
                    } else {
                        String sURL;
                        random = r.nextInt(100);
                        if (random < 20) {
                            random = r.nextInt(linksInicial.size());
                            sURL = (String) linksInicial.elementAt(random);
                        } else {
                            random = r.nextInt(links.size());
                            sURL = (String) links.elementAt(random);

                        }
                        webView.loadUrl(sURL);
                    }
                }

                random = r.nextInt(iMAX_RANDOM_TIME - iMIN_RANDOM_TIME) + iMIN_RANDOM_TIME;
                handler.postDelayed(this, iMIN_READ_TIME + random);
            }
        });
    }


    public void btnIr(View v) {
        String sURL = textURL.getText().toString();
        if (sURL.indexOf("http://") != 0 && sURL.indexOf("https://") != 0)
            sURL = "https://" + sURL;
        webView.loadUrl(sURL);
    }

    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @android.webkit.JavascriptInterface
        public void showHTML(String html) {
            int index1, index2;
            String sURL;
            sLinks = "";
            links.clear();
            //iNumLinks = 0;

            int indexA = html.indexOf("<a");
            while (indexA >= 0){
                index1 = html.indexOf("href=\"", indexA);
                index2 = html.indexOf('\"', index1 + 7);

                String sSub = html.substring(index1+6, index1+10);
                if (sSub.compareToIgnoreCase("http") == 0 || sSub.compareToIgnoreCase("https") == 0) {
                    sURL = html.substring(index1 + 6, index2);
                    sLinks += "\n" + sURL + "\n";
                    links.addElement(sURL);
                    //iNumLinks++;
                }

                indexA = html.indexOf("<a", index2 + 1);
            }

            bTerminado = true;
            //new AlertDialog.Builder(ctx).setTitle("HTML").setMessage(sLinks)
            //        .setPositiveButton(android.R.string.ok, null).setCancelable(false).create().show();
        }

    }
}
