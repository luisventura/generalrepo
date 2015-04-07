package korinver.com.ecomanda;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by Luis on 2/1/2015.
 */
public class platilloUI extends Activity {

    private TableLayout tl = null;
    private TableRow tr;
    private Button button;
    private TextView tv;
    private CheckBox cb;
    int posicion=0;
    int categoria=0;
    int dueno=0;

    public int getCategoria() {
        return categoria;
    }

    public platilloUI(TableLayout tl, String plato, int posicion, Context context, int dueno, int categoria){
        this.posicion = posicion;
        this.categoria=categoria;
        this.tl = tl;
        this.tr = new TableRow(context);
        this.button = new Button(context);
        this.tv= new TextView(context);
        this.dueno = dueno;
        this.cb = new CheckBox(context);


        TableRow.LayoutParams trlp = new TableRow.LayoutParams(40,40);

        TableLayout.LayoutParams tllp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT);
        tllp.setMargins(0,3,0,0);


        button.setText("");
        button.setBackgroundResource(R.drawable.deleteplatobutton);
        button.setPadding(0, 0, 0, 0);
        cb.setPadding(0, 0, 0, 0);
        cb.setButtonDrawable(R.drawable.custom_checkbox_design);
        cb.setHeight(40);
        cb.setWidth(40);
        tv.setText(plato);
        tv.setPadding(10,0,10,0);
        tv.setSingleLine(true);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setTextSize(18);
        tv.setGravity(Gravity.CENTER_VERTICAL);


        tv.setTextColor(0xFF222222);
        tv.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));

        tr.addView(button,trlp);

        TableRow.LayoutParams trlp2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT, 1f);

        tr.addView(tv,trlp2);
        tr.addView(cb);
        tl.addView(tr,tllp);
    }

    public Button getButton() {
        return button;
    }

    public void eliminarPlatilloUI(){
        this.tl.removeView(this.tr);
    }

    public CheckBox getCb() {
        return cb;
    }
}
