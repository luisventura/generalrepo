package korinver.com.ecomanda;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Luis on 2/1/2015.
 */
public class personaUI{

    final private LinearLayout ll;
    private FrameLayout fl;
    private LinearLayout ll2;
    //private TableLayout tl;
    private HashMap<Integer, TableLayout> tl;
    private HashMap<Integer, TextView> tvmap;
    private HashMap<Integer, Integer> platocatcounter;
    private TextView tv;
    private RelativeLayout rl;
    private int numeroCliente;
    final private TextSwitcher txtswtgenero;
    final private TextSwitcher txtswtedad;
    private ArrayList<platilloUI> rowsPlatillos;
    private persona personapertenece;

    public TextSwitcher getTxtswtedad() {
        return txtswtedad;
    }

    public TextSwitcher getTxtswtgenero() {
        return txtswtgenero;
    }

    public personaUI(LinearLayout layoutPadre, final Context context, int numeroCliente, final persona personapertenece){
        rowsPlatillos = new ArrayList<platilloUI>();
        this.ll = layoutPadre;
        this.fl = new FrameLayout(context);
        this.rl = new RelativeLayout(context);
        this.ll2 = new LinearLayout(context);
        //this.tl = new TableLayout(context);
        this.tl = new HashMap<Integer, TableLayout>();
        this.tvmap = new HashMap<Integer, TextView>();
        this.platocatcounter = new HashMap<Integer, Integer>();
        this.tv = new TextView(context);
        this.numeroCliente = numeroCliente;
        this.txtswtedad = new TextSwitcher(context);


        LayoutTransition LT = new LayoutTransition();
        LT.setStartDelay(LayoutTransition.APPEARING,0);
        LT.setStartDelay(LayoutTransition.CHANGE_APPEARING,0);
        LT.setStartDelay(LayoutTransition.DISAPPEARING,0);
        LT.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING,0);
        LT.setStartDelay(LayoutTransition.CHANGING, 0);
        LT.setAnimateParentHierarchy(true);
        this.ll2.setLayoutTransition(LT);
        this.fl.setLayoutTransition(LT);
        this.rl.setLayoutTransition(LT);


        this.txtswtedad.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView text = new TextView(context);

                text.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                text.setTextSize(18);
                text.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
                text.setTextColor(0xFF111111);

                return text;
            }
        });
        this.txtswtgenero = new TextSwitcher(context);
        this.txtswtgenero.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView text = new TextView(context);

                text.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                text.setTextSize(18);
                text.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
                text.setTextColor(0xFF111111);

                return text;
            }
        });

        tv.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));

        txtswtedad.setInAnimation(AnimationUtils.loadAnimation(context,android.R.anim.slide_in_left));
        txtswtedad.setOutAnimation(AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right));


        txtswtgenero.setInAnimation(AnimationUtils.loadAnimation(context,android.R.anim.slide_in_left));
        txtswtgenero.setOutAnimation(AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right));


        LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        FrameLayout.LayoutParams fllp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams lllp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //tv.setText("Cliente" + " N° " + (numeroCliente+1));
        tv.setText(context.getString(R.string.cliente) + " " + (numeroCliente+1));
        tv.setTextSize(20);
        tv.setTextColor(0xFF000000);

        fl.setClickable(true);
        final boolean genderFlag=false;

        lllp.setMargins(0,0,0,1);
        fllp.setMargins(10,10,10,10);

        this.ll2.setOrientation(LinearLayout.VERTICAL);
        this.rl.addView(tv);
        this.ll2.addView(rl);
        //this.ll2.addView(tl);
        this.fl.addView(ll2, fllp);

        this.fl.setBackgroundColor(0xFFEDEDED);

        this.ll.addView(this.fl, lllp);


        txtswtgenero.setClickable(true);
        txtswtgenero.setMinimumHeight(20);
        txtswtgenero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(personapertenece.getGenero()=="F"){
                    txtswtgenero.setText(context.getString(R.string.masculino));
                    personapertenece.setGenero("M");
                }else{
                    txtswtgenero.setText(context.getString(R.string.femenino));
                    personapertenece.setGenero("F");
                }
            }
        });

        txtswtedad.setClickable(true);
        txtswtedad.setMinimumHeight(20);
        txtswtedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(personapertenece.getRangoEdad()){
                    case 1: txtswtedad.setText(context.getString(R.string.adolescente));
                            personapertenece.setRangoEdad(2);
                        break;
                    case 2: txtswtedad.setText(context.getString(R.string.adulto));
                        personapertenece.setRangoEdad(3);
                        break;
                    case 3: txtswtedad.setText(context.getString(R.string.elder));
                        personapertenece.setRangoEdad(4);
                        break;
                    case 4: txtswtedad.setText(context.getString(R.string.child));
                        personapertenece.setRangoEdad(1);
                        break;
                }
            }
        });


        switch(personapertenece.getRangoEdad()){
            case 1: txtswtedad.setText(context.getString(R.string.child));
                break;
            case 2: txtswtedad.setText(context.getString(R.string.adolescente));
                break;
            case 3: txtswtedad.setText(context.getString(R.string.adulto));
                break;
            case 4: txtswtedad.setText(context.getString(R.string.elder));
                break;
        }

        if(personapertenece.getGenero().equals("M")){
            txtswtgenero.setText(context.getString(R.string.masculino));
        }else{
            txtswtgenero.setText(context.getString(R.string.femenino));
        }


        //this.ll2.addView(txtswt);
        //this.ll2.addView(txtswt2);

        txtswtedad.setMinimumWidth(150);
        txtswtgenero.setMinimumWidth(150);

        RelativeLayout.LayoutParams rllp1 = new RelativeLayout.LayoutParams(150,RelativeLayout.LayoutParams.WRAP_CONTENT);
        rllp1.addRule(RelativeLayout.CENTER_HORIZONTAL);


        RelativeLayout.LayoutParams rllp2 = new RelativeLayout.LayoutParams(150,RelativeLayout.LayoutParams.WRAP_CONTENT);
        txtswtedad.setId(35);
        rllp2.addRule(RelativeLayout.RIGHT_OF, txtswtedad.getId());

        this.rl.addView(txtswtedad, rllp1);
        this.rl.addView(txtswtgenero, rllp2);

    }

    public void eliminarPersonaUI(){
        ll.removeView(fl);
    }

    public FrameLayout getFl() {
        return fl;
    }

    public void seleccionarPersonaUI(){
        this.fl.setBackgroundColor(0xFF92bdb2);
    }

    public void deseleccionarPersonaUI(){
        this.fl.setBackgroundColor(0xFFEDEDED);
    }

    public int getNumeroCliente() {
        return numeroCliente;
    }

    public void setNumeroCliente(int numeroCliente) {
        this.numeroCliente = numeroCliente;
    }

   /* public TableLayout getTl() {
        return tl;
    }*/

    public ArrayList<platilloUI> getRowsPlatillos() {
        return rowsPlatillos;
    }

    public void anadirPlatilloUI(int recnoCat, String tituloCategoria, String plato, Context context, int personaDueno){
        TableLayout temptl=null;

        if(!this.tl.containsKey(recnoCat)){
            TextView tituloCat = new TextView(context);
            tituloCat.setText(tituloCategoria);
            this.tl.put(recnoCat, new TableLayout(context));
            this.tvmap.put(recnoCat, tituloCat);
            this.platocatcounter.put(recnoCat, 1);
            temptl = tl.get(recnoCat);
            this.ll2.addView(tituloCat);
            this.ll2.addView(temptl);
            tl.get(recnoCat).setLayoutTransition(new LayoutTransition());


        }else{
            temptl = tl.get(recnoCat);
            int temp = platocatcounter.get(recnoCat);
            platocatcounter.remove(recnoCat);
            platocatcounter.put(recnoCat, temp+1);
        }
        this.rowsPlatillos.add(new platilloUI(temptl, plato, this.rowsPlatillos.size(), context, personaDueno,recnoCat));
    }

    public void removerPlatilloUI(int posicion){
        int temp = this.rowsPlatillos.get(posicion).getCategoria();
        if(platocatcounter.get(temp)==1){
            ll2.removeView(tvmap.get(temp));
            ll2.removeView(tl.get(temp));
            tl.remove(temp);
            platocatcounter.remove(temp);
        }
        else{
            int temp2 = platocatcounter.get(temp);
            platocatcounter.remove(temp);
            platocatcounter.put(temp,temp2-1);
        }
        this.rowsPlatillos.get(posicion).eliminarPlatilloUI();
        this.rowsPlatillos.remove(posicion);
        for(int a=posicion; a<this.rowsPlatillos.size();a++){
            rowsPlatillos.get(a).posicion--;
        }
    }

}