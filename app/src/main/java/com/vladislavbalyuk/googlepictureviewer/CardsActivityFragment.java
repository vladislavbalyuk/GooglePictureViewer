package com.vladislavbalyuk.googlepictureviewer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A placeholder fragment containing a simple view.
 */
public class CardsActivityFragment extends Fragment {

    private View view;

    private boolean isDone;

    public CardsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.fragment_cards, container, false);
            isDone = false;
        }

        return view;
    }

    public void loadPictures(String word){

        if(!isDone){

            Observable<String> observable = getObservableWords(word);
            observable.map(x -> getHtmlCode(x))
                    .flatMapIterable(x -> getListRef(x))
                    .collect(ArrayList<String>::new, List::add)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(x -> {

                        isDone = true;
                        Collections.sort(x);
                        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv);
                        StaggeredGridLayoutManager llm = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                        rv.setLayoutManager(llm);

                        CardsAdapter adapter = new CardsAdapter(x);
                        rv.setAdapter(adapter);

                    });
        }
    }

    private String getHtmlCode(String word) {

        String result = "";
        BufferedReader reader=null;
        try {
            URL url=new URL(getResources().getString(R.string.url_google).toString().replace("%word%", word));
            HttpURLConnection c =(HttpURLConnection)url.openConnection();
            c.setRequestMethod("GET");
            c.setReadTimeout(10000);

            c.connect();
            reader= new BufferedReader(new InputStreamReader(c.getInputStream(), "windows-1251"));
            StringBuilder buf=new StringBuilder();
            String line=null;
            while ((line=reader.readLine()) != null) {
                buf.append(line + "\n");
            }
            result = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

    }

    private List<String> getListRef(String s) {
        List<String> list = new ArrayList<String>();
        int indexEnd;
        int indexBegin = s.indexOf("https://encrypted");
        while(indexBegin >= 0){
            s = s.substring(indexBegin + 1);
            indexEnd = s.indexOf("\"");
            list.add("h" + s.substring(0,indexEnd));
            indexBegin = s.indexOf("https://encrypted");
        }

        return list;
    }

    private Observable<String> getObservableWords(String word){

        ArrayList<String> listWords = new ArrayList<String>();

        int index = word.indexOf(",");
        while(index >= 0){
            listWords.add(word.substring(0,index));
            word = word.substring(index + 1).trim().replaceAll(" ","+");
            index = word.indexOf(",");
        }

        listWords.add(word.trim().replaceAll(" ","+"));

        return  Observable.from(listWords);
    }

}
